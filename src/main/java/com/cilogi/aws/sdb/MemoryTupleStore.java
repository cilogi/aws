// Copyright (c) 2008 Tim Niblett All Rights Reserved.
//
// File:        MemoryTupleStore.java  (02-May-2008)
// Author:      tim
//
// Copyright in the whole and every part of this source file belongs to
// Tim Niblett (the Author) and may not be used,
// sold, licenced, transferred, copied or reproduced in whole or in
// part in any manner or form or in or on any media to any person
// other than in accordance with the terms of The Author's agreement
// or otherwise without the prior written consent of The Author.  All
// information contained in this source file is confidential information
// belonging to The Author and as such may not be disclosed other
// than in accordance with the terms of The Author's agreement, or
// otherwise, without the prior written consent of The Author.  As
// confidential information this source file must be kept fully and
// effectively secure at all times.
//

package com.cilogi.aws.sdb;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;

import java.util.*;

/**
 * For debugging and local running.  Is meant to be thread safe...
 */
public class MemoryTupleStore implements ITupleStore {

    private String name;
    private final Map<String, Multimap<String, String>> db;

    public MemoryTupleStore(String name) {
        Preconditions.checkNotNull(name);
        this.name = name;
        db = Collections.synchronizedMap(new LinkedHashMap<String, Multimap<String, String>>());
    }

    public int size() {
        return db.size();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public synchronized Multimap<String, String> get(String itemName) {
        Preconditions.checkNotNull(itemName);
        Multimap<String, String> map = db.get(itemName);
        if (map == null) {
            map = LinkedHashMultimap.create();
        }
        return map;
    }



    @Override
    public synchronized void put(String itemName, boolean replace, Multimap<String, String> map) {
        Preconditions.checkNotNull(itemName);
        Multimap<String, String> itemMap = getMap(itemName);
        List<Map.Entry<String, String>> entries = Lists.newLinkedList(map.entries());
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (replace) {
                itemMap.remove(key, value);
            }
            itemMap.put(key, value);
        }

    }

    @Override
    public synchronized void put(String itemName, boolean replace, String... attributeValuePairs) {
        Preconditions.checkNotNull(itemName);
        Preconditions.checkArgument(attributeValuePairs.length % 2 == 0);
        Multimap<String, String> itemMap = getMap(itemName);
        for (int i = 0; i < attributeValuePairs.length; i += 2)  {
            String attribute = attributeValuePairs[i];
            String value = attributeValuePairs[i+1];
            if (replace) {
                itemMap.removeAll(attribute);
            }
            itemMap.put(attribute, value);
        }
    }

    @Override
    public void delete(String itemName, String attribute, String value) {
        Multimap<String, String> itemMap = getMap(itemName);
        if (itemMap.containsKey(attribute)) {
            itemMap.remove(attribute, value);
        }
    }

    @Override
    public void delete(String itemName, String attribute) {
        Multimap<String, String> itemMap = getMap(itemName);
        itemMap.removeAll(attribute);
    }

    @Override
    public void delete(String itemName) {
        Multimap<String, String> itemMap = getMap(itemName);
        itemMap.clear();
    }

    private Multimap<String, String> getMap(String itemName) {
        Multimap<String, String> itemMap = db.get(itemName);
        if (itemMap == null) {
            itemMap = Multimaps.synchronizedSetMultimap(LinkedHashMultimap.<String, String>create());
            db.put(itemName, itemMap);
        }
        return itemMap;
    }
}
