// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        CartItem.java  (18/07/15)
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


import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Preconditions;
import lombok.Data;
import lombok.NonNull;

import java.util.logging.Logger;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class CartItem {
    @SuppressWarnings("unused")
    static final Logger LOG = Logger.getLogger(CartItem.class.getName());

    private String id;
    private int qt;

    private CartItem() {}

    public CartItem(@NonNull String id, int qt) {
        Preconditions.checkArgument(qt >= 0);
        this.id = id;
        this.qt = qt;
    }
}
