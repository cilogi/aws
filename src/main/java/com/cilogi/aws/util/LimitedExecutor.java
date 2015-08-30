// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        LimitedExecutor.java  (04-Jun-2011)
// Author:      tim
// $Id$
//
// Copyright in the whole and every part of this source file belongs to
// Tim Niblett (the Author) and may not be used,
// sold, licenced, transferred, copied or reproduced in whole or in
// part in any manner or form or in or on any media to any person
// other than in accordance with the terms of The Author's agreement
// or otherwise without the prior written consent of The Author.  All
// information contained in this source file is confidential information
// belonging to The Author and as such may not be disclosed other
// than in accordance with the terms of The Author's agreement, or
// otherwise, without the prior written consent of The Author.  As
// confidential information this source file must be kept fully and
// effectively secure at all times.
//


package com.cilogi.aws.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class LimitedExecutor extends ThreadPoolExecutor {
    static final Logger LOG = LoggerFactory.getLogger(LimitedExecutor.class);

    private final int nThreads;

    public LimitedExecutor() {
        this(nThreads());
    }

    public LimitedExecutor(int nThreads) {
        super(nThreads, nThreads,
              0L, TimeUnit.MILLISECONDS,
              new LinkedBlockingQueue<Runnable>());
        this.nThreads = nThreads;
    }

    public boolean isAvailable() {
        int count = getActiveCount();
        return nThreads > count;
    }

    public void finish(long timeout, TimeUnit unit) {
        shutdown();
        try {
            awaitTermination(timeout, unit);    
        } catch (InterruptedException e) {
            // ok
        }
    }

    private static int nThreads() {
        int nProcessors = Runtime.getRuntime().availableProcessors();
        switch (nProcessors) {
            case 1: return 1;
            case 2: return 1;
            default: return nProcessors - 1;
        }
    }
}
