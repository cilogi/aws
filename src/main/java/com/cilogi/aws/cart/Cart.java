// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        Cart.java  (18/07/15)
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


import com.cilogi.util.Base64Codec;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import lombok.Data;
import lombok.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A cart is just of bunch of item ids with quantities.
 * A cart can be turned in to Base64 for coding as a URL, so that carts
 * can be emailed around.  We  email this rep as we can't talk to the server
 * to create the URL for Amazon.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Cart {
    @SuppressWarnings("unused")
    static final Logger LOG = Logger.getLogger(Cart.class.getName());

    private List<CartItem> items;

    public static Cart fromBase64(String s) {
        String json = new String(Base64Codec.decode(s), Charsets.UTF_8);
        return fromJSONString(json);
    }

    public static Cart fromJSONString(String s) {
        try {
            return new ObjectMapper().readValue(s, Cart.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Cart() {
        items = new ArrayList<>();
    }


    public void addItem(@NonNull String id, int quantity) {
        items.add(new CartItem(id, quantity));
    }

    public String toJsonString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toBase64String() {
        return Base64Codec.encodeBytes(toJsonString().getBytes(Charsets.UTF_8));
    }
}
