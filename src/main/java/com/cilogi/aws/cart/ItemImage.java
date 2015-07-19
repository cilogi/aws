// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        ItemImage.java  (19/07/15)
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;


@Data
public class ItemImage implements Serializable {
    private static final long serialVersionUID = 2273269846334191575L;
    private int width;
    private int height;
    private String url;
    ItemImage() {
        url = null;
    }
    @JsonIgnore
    public boolean isValid() {
        return url != null;
    }
}
