// Copyright (c) 2008 Tim Niblett All Rights Reserved.
//
// File:        Query.java  (08-May-2008)
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

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Query {
    //static final Logger LOG = Logger.getLogger(Query.class);

    private static final int DEFAULT_ITEM_LIMIT = 100;

    private static final int TRIPLE = 3;

    public enum ORDER {
        asc,
        desc
    }

    public enum REL {
        LT("<"),
        GT(">"),
        LE("<="),
        GE(">="),
        EQ("="),
        LIKE("like");
        private String value;

        REL(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }

    private static REL relOf(String s) {
        Preconditions.checkNotNull(s);
        for (REL rel : REL.values()) {
            if (rel.toString().equals(s.toLowerCase())) {
                return rel;
            }
        }
        return null;
    }

    private static boolean eval(REL rel, String att, String val) {
        final int compare = att.compareTo(val);
        switch (rel) {
            case LT:
                return compare < 0;
            case GT:
                return compare > 0;
            case LE:
                return compare <= 0;
            case GE:
                return compare >= 0;
            case EQ:
                return compare == 0;
            case LIKE:
                return val.startsWith(att) || val.endsWith(att);
            default:
                throw new IllegalArgumentException("Unknown relation: " + rel);
        }
    }

    private String[] query;
    private ORDER sortOrder;
    private String sortAtt;
    private int limit = DEFAULT_ITEM_LIMIT;

    public Query(String... s) {
        Preconditions.checkNotNull(s);
        Preconditions.checkArgument(s.length % TRIPLE == 0);
        String err = relationsOK(s);
        if (err != null) {
            throw new UnexpectedRelationException(err);
        }
        this.query = s;
    }

    public String getSortAtt() {
        return sortAtt;
    }

    public ORDER getSortOrder() {
        return sortOrder;
    }

    public Query limit(int limit) {
        Preconditions.checkArgument(limit > 0);
        this.limit = limit;
        return this;
    }

    public Query sortBy(String sortAtt, ORDER order) {
        Preconditions.checkNotNull(sortAtt);
        Preconditions.checkNotNull(order);
        this.sortOrder = order;
        this.sortAtt = sortAtt;
        return this;
    }

    private static String relationsOK(String[] args) {
        for (int i = 1; i < args.length; i += TRIPLE) {
            String val = args[i];
            REL rel = relOf(val.toLowerCase());
            if (rel == null) {
                return val;
            }
        }
        return null;
    }


    public String sdbQueryString(String domainName) {
        return sdbQueryString(domainName, limit);
    }

    public String sdbQueryString(String domainName, int nItems) {
        Preconditions.checkNotNull(domainName);
        if (query.length == 0) {
            return "select itemName() from " + domainName;
        }
        String out = "select itemName() from " + domainName + " where ";
        for (int i = 0; i < query.length; i += 3) {
            if (i > 0) {
                out += " and ";
            }
            out += " " + query[i];
            out += " " + query[i + 1];
            out += " '" + query[i + 2] + "'";
        }
        if (sortAtt != null) {
            out += " and " + sortAtt + " is not null order by " + sortAtt + " " + sortOrder;
        }
        out += " limit " + nItems;
        return out;
    }

    public List<String> getQuery() {
        return Collections.unmodifiableList(Arrays.asList(query));
    }

    /**
     * Evaluate a query for a map (an item)
     *
     * @param map The map of attribute/value pairs to map
     * @return the value of the query
     */
    public boolean evaluate(Multimap<String, String> map) {
        for (int i = 0; i < query.length; i += TRIPLE) {
            if (!evaluate(query[i], query[i + 1], query[i + 2], map)) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluate(String att, String relString, String val, Multimap<String, String> map) {
        REL rel = relOf(relString);
        Collection<String> values = map.get(att);
        if (values.size() > 0) {
            for (String s : values) {
                boolean compVal = eval(rel, s, val);
                if (compVal) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public static class UnexpectedRelationException extends RuntimeException {
        UnexpectedRelationException(String name) {
            super(name + " is not a valid relation");
        }
    }
}
