// Copyright (c) 2007 Tim Niblett All Rights Reserved.
//
// File:        SDBInfo.java  (13-Feb-2008)
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

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Abstracts a little from the SimpleDB database.  We look at a single domain and treat
 * it as a set if name/multimap pairs.
 */
public class SDBTupleStore implements ITupleStore {
    static final Logger LOG = Logger.getLogger(SDBTupleStore.class);

    private static final int MAX_ITER = 100;

    private SDB db;
    private String domainName;

    public SDBTupleStore(String domainName) {
        Preconditions.checkNotNull(domainName);
        db = new SDB();
        this.domainName = domainName;
        db.createDomain(domainName);
    }

    @Override
    public String getName() {
        return domainName;
    }

    public void deleteStore()  {
        db.deleteDomain(domainName);
    }

    @Override
    public Multimap<String, String> get(String name) {
        Preconditions.checkNotNull(name);
        List<Attribute> attributes = db.get(domainName, name);
        Multimap<String,String> out = HashMultimap.create();
        for (Attribute att: attributes) {
            out.put(att.getName(), att.getValue());
        }
        return out;
    }


    public void put(String itemName, Multimap<String, String> map) throws TupleStoreException {
        put(itemName, true, map);
    }

    @Override
    public void put(String itemName, boolean replace, String... attributeValuePairs) {
        Preconditions.checkArgument(attributeValuePairs.length % 2 == 0);
        Multimap<String,String> map = HashMultimap.create();
        for (int i = 0; i < attributeValuePairs.length; i += 2) {
            map.put(attributeValuePairs[i], attributeValuePairs[i+1]);
        }
        put(itemName, replace, map);
    }

    @Override
    public void put(String itemName, boolean replace, Multimap<String, String> map) throws TupleStoreException {
        Preconditions.checkNotNull(itemName);
        Preconditions.checkNotNull(map);
        List<ReplaceableAttribute> atts = Lists.newArrayList();
        for (Map.Entry<String,String> entry : map.entries()) {
            atts.add(new ReplaceableAttribute(entry.getKey(), entry.getValue(), replace));
        }
        db.put(domainName, itemName, atts);
    }
}
