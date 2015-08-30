// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        ItemLookup.java  (18/07/15)
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


import com.cilogi.ds.guide.shop.Sku;
import com.cilogi.ds.guide.shop.SkuImage;
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
    public Sku getInfo() {
        return new Sku(asin)
                .title(getTitle())
                .description(getDescription())
                .image(getImage())
                .thumb(getThumb())
                .unitPrice(getListPrice());
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

    public SkuImage getImage() {
        List<String> names = Lists.newArrayList("LargeImage", "MediumImage", "SmallImage");
        for (String name: names) {
            SkuImage image = getImage(name);
            if (image != null) {
                return image;
            }
        }
        return null;
    }

    public SkuImage getThumb() {
        SkuImage image = getImage("ThumbnailImage");
        return image;
    }

    public BigDecimal getListPrice() {
        List<String> prices = Lists.newArrayList("ListPrice", "LowestNewPrice");
        for (String price : prices) {
            BigDecimal dec = getPrice(price);
            if (dec != null) {
                return dec;
            }
        }
        return null;
    }

    private BigDecimal getPrice(@NonNull String tag) {
        NodeList priceNodes = doc.getElementsByTagName(tag);
        if (priceNodes == null || priceNodes.getLength() == 0) {
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

    private SkuImage getImage(String elementName) {
        Element element = (Element)doc.getElementsByTagName(elementName).item(0);
        if (element != null) {
            SkuImage image = new SkuImage();
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
