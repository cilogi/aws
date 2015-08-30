// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        CreateCart.java  (18/07/15)
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

import lombok.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class CartCreate {
    @SuppressWarnings("unused")
    static final Logger LOG = Logger.getLogger(CartCreate.class.getName());

    private final Document doc;

    public CartCreate(@NonNull Cart cart) {
        BasicOp base = new BasicOp();
        doc = doc(cart, base);
    }

    public Document getDoc() {
        return doc;
    }

    public String getUrl() {
        NodeList nodeList = doc.getElementsByTagName("PurchaseURL");
        return (nodeList.getLength() == 0) ? null : nodeList.item(0).getTextContent();
    }

    public String errorMessage() {
        NodeList nodeList = doc.getElementsByTagName("Message");
        return (nodeList.getLength() == 0) ? null : nodeList.item(0).getTextContent();
    }

    private Document doc(@NonNull Cart cart, BasicOp base) {
        List<CartItem> items = cart.getItems();
        if (items.size() == 0) {
            return null;
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("Operation", "CartCreate");
            for (int i = 0; i < items.size(); i++) {
                String index = Integer.toString(i+1); // the index starts at 1 for the cart
                CartItem item = items.get(i);
                params.put("Item." + index + ".ASIN", item.getId());
                params.put("Item." + index + ".Quantity", Integer.toString(item.getQt()));
            }
            return base.fetch(params);
        }
    }
}
