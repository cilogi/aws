// Copyright (c) 2008 Tim Niblett All Rights Reserved.
//
// File:        SDB.java  (8-Feb-2008)
// Author:      Tim Niblett
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

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.*;
import com.cilogi.util.Secrets;
import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;

import java.util.List;


/**
 * Simple interface to SDB
 */
class SDB {
    static final Logger LOG = Logger.getLogger(SDB.class);

    private final AmazonSimpleDBClient client;

    SDB() {
        this(Secrets.VALUES.get("aws.access"), Secrets.VALUES.get("aws.accessSecret"));
    }

    SDB(String accessKey, String secretAccessKey) {
        AWSCredentials credentials = new BasicAWSCredentials( accessKey, secretAccessKey );
        client = new AmazonSimpleDBClient( credentials);
    }

    public void createDomain(String name) {
        Preconditions.checkNotNull(name);
        client.createDomain(new CreateDomainRequest().withDomainName(name));
    }

    public void deleteDomain(String name) {
        Preconditions.checkNotNull(name);
        client.deleteDomain(new DeleteDomainRequest().withDomainName(name));
    }

    public List<Attribute> get(String domainName, String itemName) {
        Preconditions.checkNotNull(itemName);
        GetAttributesResult result =  client.getAttributes(new GetAttributesRequest().withDomainName(domainName).withItemName(itemName));
        return result.getAttributes();
    }

    public void put(String domainName, String itemName, List<ReplaceableAttribute> atts) {
        Preconditions.checkNotNull(domainName);
        Preconditions.checkNotNull(itemName);
        client.putAttributes(new PutAttributesRequest().withDomainName(domainName).withItemName(itemName).withAttributes(atts));
    }

    public void deleteAttribute(String domainName, String itemName, String attributeName, String value) {
        Preconditions.checkNotNull(itemName, "item name can't be null when deleting items in SDB");
        Preconditions.checkNotNull(attributeName, "attribute name can't be null when deleting items in SDB");
        Preconditions.checkNotNull(value, "value can't be null when deleting items in SDB");

        Attribute attr = new Attribute()
                .withName(attributeName)
                .withValue(value);
        DeleteAttributesRequest deleteAttributeRequest = new DeleteAttributesRequest()
                .withDomainName(domainName)
                .withItemName(itemName)
                .withAttributes(attr);
        client.deleteAttributes(deleteAttributeRequest);
    }

    public void deleteAttribute(String domainName, String itemName, String attributeName) {
        Preconditions.checkNotNull(itemName, "item name can't be null when deleting items in SDB");
        Preconditions.checkNotNull(attributeName, "attribute name can't be null when deleting items in SDB");

        Attribute attr = new Attribute()
                .withName(attributeName);
        DeleteAttributesRequest deleteAttributeRequest = new DeleteAttributesRequest()
                .withDomainName(domainName)
                .withItemName(itemName)
                .withAttributes(attr);
        client.deleteAttributes(deleteAttributeRequest);
    }

    public void deleteItem(String domainName, String itemName) {
        Preconditions.checkNotNull(itemName, "item name can't be null when deleting items in SDB");

        DeleteAttributesRequest deleteAttributeRequest = new DeleteAttributesRequest()
                .withDomainName(domainName)
                .withItemName(itemName);
        client.deleteAttributes(deleteAttributeRequest);
    }
}
