// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        ItemInfo.java  (19/07/15)
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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemInfo implements Serializable {
    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(ItemInfo.class);
    private static final long serialVersionUID = 5561759521568058667L;

    private String id;
    private String title;
    private String description;
    private ItemImage image;
    private BigDecimal unitPrice;

    public static ItemInfo fromJSONString(@NonNull String s) {
        try {
            return new Mapper().readValue(s, ItemInfo.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    ItemInfo(@NonNull String id, String title, String description, ItemImage image,
             BigDecimal unitPrice) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.unitPrice = unitPrice;
    }

    public String toJsonString() {
        try {
            return new Mapper().writeValueAsString(this);
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class Mapper extends ObjectMapper {
        public Mapper() {
             SimpleModule module = new SimpleModule();
             module.addSerializer(BigDecimal.class, new ToStringSerializer());
             registerModule(module);
             setVisibilityChecker(getVisibilityChecker().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
             configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
         }
    }
}
