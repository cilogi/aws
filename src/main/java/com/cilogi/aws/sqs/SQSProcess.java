// Copyright (c) 2013 Cilogi. All Rights Reserved.
//
// File:        SQSProcess.java  (26/03/13)
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
import com.amazonaws.util.json.JSONObject;
import com.cilogi.util.LimitedExecutor;
import com.google.common.base.Preconditions;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class SQSProcess {
    static final Logger LOG = Logger.getLogger(SQSProcess.class.getName());

    private final LimitedExecutor executor;
    private Thread runner;
    private final SQS sqs;
    private volatile boolean isRunning;
    private ISQSHandler handler;

    public SQSProcess(String queueName, ISQSHandler handler) {
        Preconditions.checkNotNull(queueName, "You can't have a null queue name");
        executor = new LimitedExecutor();
        sqs = new SQS(queueName);
        this.handler = handler;
        runner = null;
        isRunning = false;
    }

    public synchronized  void start() {
        runner = new Thread(new RunForever());
        isRunning = true;
        runner.start();
    }

    public synchronized void stop() {
        isRunning = false;
        executor.shutdown();
        try {
            executor.awaitTermination(1L, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    private class RunForever implements Runnable {
        public void run() {
            while (isRunning) {
                try {
                    List<Message> messages = sqs.getMessages();
                    for (Message message : messages) {
                        final Message messageFinal = message;
                        final JSONObject json = SQS.message2json(messageFinal);
                        executor.submit(new Runnable() {
                            public void run() {
                                handler.handle(json, new MyCallBack(messageFinal));
                            }
                        });
                    }
                } catch (Exception e) {
                    LOG.warning("Problem handling message: " + e.getMessage());
                    isRunning = false;
                }
            }
        }
    }

    private class MyCallBack implements ISQSHandler.Callback {
        private final Message message;

        MyCallBack(Message message) {
            this.message = message;
        }

        @Override
        public void callback() {
            sqs.deleteMessage(message);
        }
    }
}
