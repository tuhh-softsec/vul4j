/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
/*
 * $Id$
 */
package javax.xml.crypto.dsig.spec;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parameters for the W3C Recommendation:
 * <a href="http://www.w3.org/TR/xml-exc-c14n/">
 * Exclusive XML Canonicalization (C14N) algorithm</a>. The
 * parameters include an optional inclusive namespace prefix list. The XML 
 * Schema Definition of the Exclusive XML Canonicalization parameters is
 * defined as:
 * <pre><code>
 * &lt;schema xmlns="http://www.w3.org/2001/XMLSchema"
 *         xmlns:ec="http://www.w3.org/2001/10/xml-exc-c14n#"
 *         targetNamespace="http://www.w3.org/2001/10/xml-exc-c14n#"
 *         version="0.1" elementFormDefault="qualified"&gt;
 *
 * &lt;element name="InclusiveNamespaces" type="ec:InclusiveNamespaces"/&gt;
 * &lt;complexType name="InclusiveNamespaces"&gt;
 *   &lt;attribute name="PrefixList" type="xsd:string"/&gt;
 * &lt;/complexType&gt;
 * &lt;/schema&gt;
 * </code></pre>
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 * @see CanonicalizationMethod
 */
public final class ExcC14NParameterSpec implements C14NMethodParameterSpec {

    private List preList;

    /**
     * Indicates the default namespace ("#default").
     */
    public static final String DEFAULT = "#default";

    /**
     * Creates a <code>ExcC14NParameterSpec</code> with an empty prefix 
     * list.
     */
    public ExcC14NParameterSpec() {
        preList = Collections.EMPTY_LIST;
    }

    /**
     * Creates a <code>ExcC14NParameterSpec</code> with the specified list
     * of prefixes. The list is copied to protect against subsequent 
     * modification.
     *
     * @param prefixList the inclusive namespace prefix list. Each entry in
     *    the list is a <code>String</code> that represents a namespace prefix.
     * @throws NullPointerException if <code>prefixList</code> is 
     *    <code>null</code>
     * @throws ClassCastException if any of the entries in the list are not
     *    of type <code>String</code>
     */
    public ExcC14NParameterSpec(List prefixList) {
        if (prefixList == null) {
            throw new NullPointerException("prefixList cannot be null");
        }
        this.preList = unmodifiableCopyOfList(prefixList);
        for (int i = 0, size = preList.size(); i < size; i++) {
            if (!(preList.get(i) instanceof String)) {
                throw new ClassCastException("not a String");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static List unmodifiableCopyOfList(List list) {
        return Collections.unmodifiableList(new ArrayList(list));
    }

    /**
     * Returns the inclusive namespace prefix list. Each entry in the list
     * is a <code>String</code> that represents a namespace prefix.
     *
     * <p>This implementation returns an {@link
     * java.util.Collections#unmodifiableList unmodifiable list}.
     *
     * @return the inclusive namespace prefix list (may be empty but never
     *    <code>null</code>)
     */
    public List getPrefixList() {
        return preList;
    }
}
