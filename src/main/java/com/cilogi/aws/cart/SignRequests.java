/**********************************************************************************************
 * Copyright 2009 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file 
 * except in compliance with the License. A copy of the License is located at
 *
 *       http://aws.amazon.com/apache2.0/
 *
 * or in the "LICENSE.txt" file accompanying this file. This file is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the License. 
 *
 * ********************************************************************************************
 *
 *  Amazon Product Advertising API
 *  Signed Requests Sample Code
 *
 *  API Version: 2009-03-31
 *
 */

package com.cilogi.aws.cart;

import com.google.common.base.Charsets;
import lombok.NonNull;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * This class contains all the logic for signing requests
 * for  the Amazon Product Advertising API.
 */
class SignRequests {
    @SuppressWarnings({"unused"})
    static final Logger LOG = Logger.getLogger(SignRequests.class.getName());

    private static final String UTF8 = Charsets.UTF_8.toString();
    
    /** The HMAC algorithm required by Amazon */
    private static final String HMAC_NAME = "HmacSHA256";
    
    /** The URI for the service, under the host which varies by country */
    private static final String REQUEST_URI = "/onca/xml";
    
    private static final String REQUEST_METHOD = "GET";

    private final EndPoint endpoint;
    private final String awsAccessKeyId;
    private final Mac mac;

    @SuppressWarnings({"unused"})
    SignRequests(@NonNull String awsAccessKeyId, @NonNull String awsSecretKey)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        this(EndPoint.UK, awsAccessKeyId, awsSecretKey);
    }

    SignRequests(@NonNull EndPoint endpoint, @NonNull String awsAccessKeyId, @NonNull String awsSecretKey)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        this.endpoint = endpoint;
        this.awsAccessKeyId = awsAccessKeyId;

        byte[] secretKeyBytes = awsSecretKey.getBytes(UTF8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, HMAC_NAME);
        this.mac = Mac.getInstance(HMAC_NAME);
        this.mac.init(secretKeySpec);
    }

    /**
     * This method signs requests in hash map form. It returns a URL that should
     * be used to fetch the response. The URL returned should not be modified in
     * any way, doing so will invalidate the signature and Amazon will reject
     * the request.
     */
    public String sign(Map<String, String> params) {
        // Let's add the AWSAccessKeyId and Timestamp parameters to the request.
        params.put("AWSAccessKeyId", this.awsAccessKeyId);
        params.put("Timestamp", this.timestamp());

        SortedMap<String, String> sortedParamMap = new TreeMap<>(params);
        
        String canonicalQS = this.makeCanonical(sortedParamMap);
        
        String toSign =
            REQUEST_METHOD + "\n" 
            + this.endpoint.getHost() + "\n"
            + REQUEST_URI + "\n"
            + canonicalQS;

        String hmac = this.hmac(toSign);
        String sig = this.percentEncodeRfc3986(hmac);

        return "http://" + this.endpoint.getHost() + REQUEST_URI + "?" + canonicalQS + "&Signature=" + sig;
    }

    /**
     * Compute the HMAC.
     *  
     * @param stringToSign  String to compute the HMAC over.
     * @return              base64-encoded hmac value.
     */
    private String hmac(String stringToSign) {
        try {
            byte[] data = stringToSign.getBytes(UTF8);
            byte[] rawHmac = mac.doFinal(data);
            Base64 encoder = new Base64();
            return new String(encoder.encode(rawHmac));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(UTF8 + " is unsupported!", e);
        }
    }

    /**
     * Generate a ISO-8601 format timestamp as required by Amazon.
     *  
     * @return  ISO-8601 format timestamp.
     */
    private String timestamp() {
        Calendar cal = Calendar.getInstance();
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dfm.format(cal.getTime());
    }

    /**
     * Make the query string canonical as required by Amazon.
     * 
     * @param sortedParamMap    Parameter name-value pairs in lexicographical order.
     * @return                  Canonical form of query string.
     */
    private String makeCanonical(@NonNull SortedMap<String, String> sortedParamMap) {
        StringBuilder buffer = new StringBuilder();
        Iterator<Map.Entry<String, String>> iter = sortedParamMap.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<String, String> keyValuePair = iter.next();
            buffer.append(percentEncodeRfc3986(keyValuePair.getKey()));
            buffer.append("=");
            buffer.append(percentEncodeRfc3986(keyValuePair.getValue()));
            if (iter.hasNext()) {
                buffer.append("&");
            }
        }
        return buffer.toString();
    }

    /**
     * Percent-encode values according the RFC 3986. The built-in Java
     * URLEncoder does not encode according to the RFC, so we make the
     * extra replacements.
     * 
     * @param s decoded string
     * @return  encoded string per RFC 3986
     */
    private String percentEncodeRfc3986(String s) {
        try {
            return URLEncoder.encode(s, UTF8)
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }
}
