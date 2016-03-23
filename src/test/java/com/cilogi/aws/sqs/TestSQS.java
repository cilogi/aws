// Copyright (c) 2013 Cilogi. All Rights Reserved.
//
// File:        TestSQS.java  (26/03/13)
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

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import org.codehaus.jackson.map.util.JSONPObject;
import org.junit.Before;
import org.junit.Test;

import java.io.Console;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestSQS {
    static final Logger LOG = Logger.getLogger(TestSQS.class.getName());

    private SQS sqs;

    public TestSQS() {
    }

    @Before
    public void setUp() {
        sqs = new SQS("testQueue");
    }

    @Test
    public void testDummy() {

    }

    public void testAddRemove() {
        sqs.sendMessage(create());
        List<IMessage> messages = sqs.getMessages();
        assertEquals(1, messages.size());
        JSONObject back = SQS.message2json(messages.get(0));
        for (IMessage message : messages) {
            sqs.deleteMessage(message);
        }
        assertTrue(back.has("key"));
        try {
            assertEquals("value", back.get("key"));
        } catch (JSONException e) {
            fail("Can't get key from JSON object: " + back.toString() + ": " + e.getMessage());
        }
    }

    public void testHandle() {
        SQSProcess process = new SQSProcess("testQueue", new ISQSHandler() {
            public void handle(JSONObject obj, Callback callback) {
                try {
                    System.out.println("handle: " + obj.get("key"));
                } catch (Exception e) {}
                callback.callback();
            }
        });
        process.start();
        for (int i = 0; i < 10; i++) {
            sqs.sendMessage( create() );
            try { Thread.currentThread().sleep(3000); } catch (InterruptedException e) {}
        }
        try { Thread.currentThread().sleep(10000); } catch (InterruptedException e) {}
        System.out.println("done");
        process.stop();
    }

    private JSONObject create() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("key", "value");
        } catch (JSONException e) {
            fail("Can't create JSON object: " + e.getMessage());
        }
        return obj;
    }
}