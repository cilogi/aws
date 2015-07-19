// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        CartCreateDemo.java  (18/07/15)
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


import java.util.logging.Logger;

public class CartCreateDemo {
    @SuppressWarnings("unused")
    static final Logger LOG = Logger.getLogger(CartCreateDemo.class.getName());


    private static final String BEGONIA_ASIN = "1847972314";
    private static final String PEVSNER_ASIN = "0300096747";

    public static void main(String[] args) {
        Cart cart = new Cart();
        cart.addItem(BEGONIA_ASIN, 1);
        cart.addItem(PEVSNER_ASIN, 2);
        CartCreate create = new CartCreate(cart);
        BasicOp.printDocument(create.getDoc(), System.out);
        String url = create.getUrl();
        if (url == null) {
            System.out.println("error message is " + create.errorMessage());
        } else {
            System.out.println("URL is " + url);
        }
    }
}
