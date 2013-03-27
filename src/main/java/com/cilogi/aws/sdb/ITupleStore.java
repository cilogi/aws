// Copyright (c) 2008 Tim Niblett All Rights Reserved.
//
// File:        ITupleStore.java  (02-May-2008)
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

import com.google.common.collect.Multimap;

public interface ITupleStore {

    /**
     * The name of this store, used for persistence purposes
     * @return the name, will not be null
     */
    public String getName();


    /**
     * Get the map for an item
     * @param itemName  The name of the item
     * @return The multimap of attribute/value pairs for this item, may be empty, not null.
     */
    public Multimap<String, String> get(String itemName);

    /**
     * Store an item
     * @param itemName The item name.
     * @param map Must be a legal attribute/value map
     * @param replace  if true existing values for attributes will be replaced (ie not multimap)
     */
    public void put(String itemName, boolean replace, Multimap<String, String> map);

    /**
     * Store an attribute/value pair in an item
     * @param itemName  The name of the item
     * @param replace  if true then the current attribute for this item will be replaced.
     * @param attributeValuePairs  attribute and value pairs, must be even count
     */
    public void put(String itemName, boolean replace, String... attributeValuePairs);

    /*
    public Set<String> get(Query query);

    public TupleStoreResult list(Query query, String nextToken);

    public void delete(String itemName);

    public void delete(String itemName, String attribute, String value);
    */
}
