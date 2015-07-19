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
import lombok.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ItemLookup {
    @SuppressWarnings("unused")
    static final Logger LOG = Logger.getLogger(ItemLookup.class.getName());

    private final String asin;
    private final Document doc;

    public ItemLookup(@NonNull String asin) {
        this.asin = asin;
        BasicOp base = new BasicOp();

        Map<String, String> params = new HashMap<>();
        params.put("Operation", "ItemLookup");
        params.put("ItemId", asin);
        params.put("ResponseGroup", "Medium");

        doc = base.fetch(params);
    }

    @SuppressWarnings({"unused"})
    public ItemInfo getInfo() {
        return new ItemInfo(asin, getTitle(), getDescription(), getImage(), getThumb(),
                     getListPrice());
    }

    public Document getDoc() {
        return doc;
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

    public ItemImage getThumb() {
        ItemImage image = getImage("ThumbnailImage");
        return image;
    }

    public BigDecimal getListPrice() {
        NodeList priceNodes = doc.getElementsByTagName("ListPrice");
        if (priceNodes == null) {
            return null;
        }
        Element priceElement = (Element)priceNodes.item(0);
        Node amountNode = priceElement.getElementsByTagName("Amount").item(0);
        String amount = amountNode.getTextContent();
        try {
            BigDecimal dec = new BigDecimal(amount);
            return dec.divide(new BigDecimal(100));
        } catch (NumberFormatException e) {
            LOG.warning("Can't parse <" + amount + "> as number");
            return null;
        }
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
                    switch (name) {
                        case "url":
                            image.setUrl(child.getTextContent());
                            break;
                        case "width":
                            image.setWidth(Integer.parseInt(child.getTextContent()));
                            break;
                        case "height":
                            image.setHeight(Integer.parseInt(child.getTextContent()));
                            break;
                    }
                }
            }
            return image;
        }
        return null;
    }


}
