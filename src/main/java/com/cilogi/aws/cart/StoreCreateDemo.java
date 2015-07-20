// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        StoreCreateDemo.java  (20/07/15)
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


package com.cilogi.aws.cart;

import com.cilogi.ds.guide.shop.Shop;
import com.cilogi.ds.guide.shop.Sku;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class StoreCreateDemo {
    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(StoreCreateDemo.class);

    List<String> ids = Lists.newArrayList(
            "1847972314", // begonias book
            "1903657067", // orchids
            "0300096747", // pevsner
            "1908931515"  // curtis story
    );

    public StoreCreateDemo() {

    }

    public String create(String name) {
        Shop shop = new Shop(name);
        for (String id: ids) {
            try {
                ItemLookup item = new ItemLookup(id);
                Sku sku = item.getInfo();
                shop.addSku(sku);
            } catch (Exception e) {
                LOG.warn("Can't load " + id + ": " + e.getMessage());
            }
        }
        return shop.toJSONString();
    }

    public static void main(String[] args) {
        StoreCreateDemo demo = new StoreCreateDemo();
        String s = demo.create("botanics");
        System.out.println(s);
    }
}
