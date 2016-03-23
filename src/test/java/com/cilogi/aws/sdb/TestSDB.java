// Copyright (c) 2013 Cilogi. All Rights Reserved.
//
// File:        TestSDB.java  (27/03/13)
// Author:      tim
//
// Copyright in the whole and every part of this source file belongs to
// Cilogi (the Author) and may not be used, sold, licenced, 
// transferred, copied or reproduced in whole or in part in 
// any manner or form or in or on any media to any person other than 
// in accordance with the terms of The Author's agreement
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
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class TestSDB {
    static final Logger LOG = Logger.getLogger(TestSDB.class.getName());

    private static final String DOMAIN = "testDomain";

    private SDB sdb;

    public TestSDB() {
    }

    @Before
    public void setUp() {
        sdb = new SDB();
        sdb.createDomain(DOMAIN);
    }

    @Test
    public void testDummy() {

    }

    public void testPut() {
        sdb.put(DOMAIN, "item", atts("one", "a", "one", "b", "one", "c"));
        sdb.deleteAttribute(DOMAIN, "item", "one", "c");
        sleep(3000L);
        List<Attribute> list = sdb.get(DOMAIN, "item");
        assertEquals(2, list.size());
        sdb.deleteAttribute(DOMAIN, "item", "one");
        sleep(3000L);
        List<Attribute> list2 = sdb.get(DOMAIN, "item");
        assertEquals(0, list2.size());
    }

    public void testDeleteItem() {
        sdb.put(DOMAIN, "item2", atts("one", "a", "one", "b", "one", "c"));
        sdb.deleteItem(DOMAIN, "item2");
        List<Attribute> list = sdb.get(DOMAIN, "item2");
        assertEquals(0, list.size());

    }

    private static List<ReplaceableAttribute> atts(String... vals) {
        List<ReplaceableAttribute> out = Lists.newArrayList();
        for (int i = 0; i < vals.length; i += 2) {
            out.add(new ReplaceableAttribute(vals[i], vals[i+1], true));
        }
        return out;
    }

    private static void sleep(long msec) {
        try {
            Thread.currentThread().sleep(msec);
        } catch (InterruptedException e)  {
            // ok
        }

    }
}