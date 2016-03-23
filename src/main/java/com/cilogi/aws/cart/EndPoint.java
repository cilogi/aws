// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        EndPoint.java  (18/07/15)
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

import lombok.Getter;

public enum EndPoint {
    US("ecs.amazonaws.com"),
    CA("ecs.amazonaws.ca"),
    UK("ecs.amazonaws.co.uk"),
    DE("ecs.amazonaws.de"),
    FR("ecs.amazonaws.fr"),
    JP("ecs.amazonaws.jp");

    @Getter
    private final String host;

    EndPoint(String host) {
        this.host = host;
    }
}
