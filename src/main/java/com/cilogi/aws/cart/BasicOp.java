// Copyright (c) 2015 Cilogi. All Rights Reserved.
//
// File:        BasicOp.java  (18/07/15)
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

import com.cilogi.aws.util.Secrets;
import lombok.Getter;
import lombok.NonNull;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


class BasicOp {
    @SuppressWarnings("unused")
    static final Logger LOG = Logger.getLogger(BasicOp.class.getName());

    private static final String AWS_ACCESS_KEY_NAME = "amazon.accessKey";
    private static final String AWS_SECRET_KEY_NAME = "amazon.secretKey";
    private static final String AWS_ASSOCIATE_TAG = "amazon.associateTag";

    @Getter
    private final SignRequests helper;
    private final String awsAccess;
    private final String associateTag;

    BasicOp() {
        this(Secrets.VALUES.get(AWS_ACCESS_KEY_NAME), Secrets.VALUES.get(AWS_SECRET_KEY_NAME), Secrets.VALUES.get(AWS_ASSOCIATE_TAG));
    }

    BasicOp(@NonNull String awsAccess, @NonNull String awsSecret, @NonNull String associateTag) {
        try {
            helper = new SignRequests(awsAccess, awsSecret);
            this.awsAccess = awsAccess;
            this.associateTag = associateTag;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Document fetch(@NonNull Map<String,String> params) {
        String requestUrl = getRequestUrl(params);
        return fetchDoc(requestUrl);
    }

    String getRequestUrl(@NonNull Map<String,String> params) {
        Map<String,String> baseParams = baseParams();
        baseParams.putAll(params);
        return helper.sign(baseParams);
    }

    Document fetchDoc(String requestUrl) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(requestUrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    Map<String,String> baseParams() {
        Map<String, String> params = new HashMap<>();
        params.put("Service", "AWSECommerceService");
        params.put("Version", "2013-08-01");
        params.put("AWSAccessKeyId", awsAccess);
        params.put("AssociateTag", associateTag);
        return params;
    }

    static void printDocument(Document doc, OutputStream out) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            transformer.transform(new DOMSource(doc),
                 new StreamResult(new OutputStreamWriter(out, "UTF-8")));
        } catch (Exception e) {
            LOG.warning("Error printing document: " + e.getMessage());
        }
    }
}
