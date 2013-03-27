// Copyright (c) 2013 Cilogi. All Rights Reserved.
//
// File:        SimpleMessage.java  (27/03/13)
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


package com.cilogi.aws.sqs;

import java.util.logging.Logger;


public class SimpleMessage implements IMessage {
    static final Logger LOG = Logger.getLogger(SimpleMessage.class.getName());

    private final String messageBody;
    private final String receiptHandle;

    public SimpleMessage(String messageBody, String receiptHandle) {
        this.messageBody = messageBody;
        this.receiptHandle = receiptHandle;
    }

    @Override
    public String getBody() {
        return messageBody;
    }

    @Override
    public String getReceiptHandle() {
        return receiptHandle;
    }
}
