// Copyright (c) 2008 Tim Niblett All Rights Reserved.
//
// File:        TupleStoreException.java  (02-May-2008)
// Author:      tim
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

package com.cilogi.aws.sdb;

public class TupleStoreException extends RuntimeException {

        public TupleStoreException(String s) {
            super(s);
        }

        public TupleStoreException(Exception e) {
            super(e);
        }
}
