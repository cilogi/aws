// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        TestCart.java  (18/07/15)
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

import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

import static org.junit.Assert.*;

public class TestCart {
    @SuppressWarnings("unused")
    static final Logger LOG = Logger.getLogger(TestCart.class.getName());


    public TestCart() {
    }

    @Before
    public void setUp() {

    }

    @Test
    public void testCart() {
        Cart cart = new Cart();
        cart.addItem("109", 1);
        cart.addItem("123456", 2);
        String base64 = cart.toBase64String();
        Cart back = Cart.fromBase64(base64);
        assertEquals(cart, back);
    }
}