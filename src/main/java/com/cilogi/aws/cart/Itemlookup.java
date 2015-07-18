// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        Itemlookup.java  (18/07/15)
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


import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ItemLookup {
    @SuppressWarnings("unused")
    static final Logger LOG = Logger.getLogger(ItemLookup.class.getName());

    private final Document doc;

    public ItemLookup(@NonNull String asin) {
        BasicOp base = new BasicOp();

        Map<String, String> params = new HashMap<>();
        params.put("Operation", "ItemLookup");
        params.put("ItemId", asin);
        params.put("ResponseGroup", "Medium");

        doc = base.fetch(params);
    }

    public String getTitle() {
        Node titleNode = doc.getElementsByTagName("Title").item(0);
        return (titleNode == null) ? null : titleNode.getTextContent();
    }

    public String getDescription() {
        Node contentsNode = doc.getElementsByTagName("Content").item(0);
        return (contentsNode == null) ? null : contentsNode.getTextContent();
    }

    public ItemImage getImage() {
        List<String> names = Lists.newArrayList("LargeImage", "MediumImage", "SmallImage");
        for (String name: names) {
            ItemImage image = getImage(name);
            if (image != null) {
                return image;
            }
        }
        return null;
    }

    private ItemImage getImage(String elementName) {
        Element element = (Element)doc.getElementsByTagName(elementName).item(0);
        if (element != null) {
            ItemImage image = new ItemImage();
            NodeList nodeList = element.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node child = nodeList.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    String name = child.getNodeName().toLowerCase();
                    if (name.equals("url")) {
                        image.setUrl(child.getTextContent());
                    } else if (name.equals("width")) {
                        image.setWidth(Integer.parseInt(child.getTextContent()));
                    } else if (name.equals("height")) {
                        image.setHeight(Integer.parseInt(child.getTextContent()));
                    }
                }
            }
            return image;
        }
        return null;
    }


    @Data
    public class ItemImage {
        private int width;
        private int height;
        private String url;
        ItemImage() {
            url = null;
        }
        public boolean isValid() {
            return url != null;
        }
    }
}
