// Copyright (c) 2013 Cilogi. All Rights Reserved.
//
// File:        SQS.java  (26/03/13)
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

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.cilogi.util.Secrets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class SQS {
    static final Logger LOG = Logger.getLogger(SQS.class.getName());

    private static final int VisibilityTimeout = 180; // time in seconds, during which we must process a message
    private static final int MessageRetentionPeriod = 3600; // time in seconds messages are retained
    private static final int MaximNumberOfMessages = 5;
    private static final int WaitTimeSeconds = 20;

    private final String queueName;
    private final String queueUrl;
    private final AmazonSQS sqs;
    private int waitTime;

    public SQS(String queueName) {
        AWSCredentials credentials =
                new BasicAWSCredentials(Secrets.VALUES.get("aws.access"), Secrets.VALUES.get("aws.accessSecret"));
        this.sqs = new AmazonSQSClient(credentials);
        this.queueName = queueName;
        this.queueUrl = createQueue(queueName);
        this.waitTime = WaitTimeSeconds;
    }

    public int getWaitTimeSeconds() {
        return waitTime;
    }

    public void setWaitTimeSeconds(int waitTime) {
        Preconditions.checkArgument(0 <= waitTime && waitTime <= 20, "Wait time must be from 0 to 20 seconds");
        this.waitTime = waitTime;
    }

    public String getQueueName() {
        return queueName;
    }

    public void sendMessage(JSONObject message) {
        Preconditions.checkNotNull(message, "You must have a non-null message to insert into SQS");
        String jsonString = message.toString();
        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withMessageBody(jsonString)
                .withQueueUrl(queueUrl);
        sqs.sendMessage(sendMessageRequest);
    }

    public List<Message> getMessages() {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest()
                .withQueueUrl(queueUrl)
                .withWaitTimeSeconds(waitTime)
                .withMaxNumberOfMessages(MaximNumberOfMessages);
        ReceiveMessageResult result = sqs.receiveMessage(receiveMessageRequest);
        return result.getMessages();
    }

    public static JSONObject message2json(Message message) {
        try {
            return new JSONObject(message.getBody());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMessage(Message message) {
        Preconditions.checkNotNull(message, "You can't have a null message to delete from SQS");
        DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest()
                .withQueueUrl(queueUrl)
                .withReceiptHandle(message.getReceiptHandle());
        sqs.deleteMessage(deleteMessageRequest);
    }

    private String createQueue(String queueName) {
        CreateQueueRequest request = new CreateQueueRequest()
                .withQueueName(queueName)
                .withAttributes(queueAttributes());
        CreateQueueResult result =  sqs.createQueue(request);
        return result.getQueueUrl();
    }

    private static Map<String,String> queueAttributes() {
        Map<String,String> map = Maps.newHashMap();
        map.put("MessageRetentionPeriod", Integer.toString(MessageRetentionPeriod));
        map.put("VisibilityTimeout", Integer.toString(VisibilityTimeout));
        return map;
    }
}
