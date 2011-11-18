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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.dsig.Transform;

/**
 * Parameters for the W3C Recommendation
 * <a href="http://www.w3.org/TR/xmldsig-filter2/">
 * XPath Filter 2.0 Transform Algorithm</a>.
 * The parameters include a list of one or more {@link XPathType} objects.
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 * @see Transform
 * @see XPathFilterParameterSpec
 */
public final class XPathFilter2ParameterSpec implements TransformParameterSpec {

    private final List xPathList;

    /**
     * Creates an <code>XPathFilter2ParameterSpec</code>.
     *
     * @param xPathList a list of one or more {@link XPathType} objects. The 
     *    list is defensively copied to protect against subsequent modification.
     * @throws ClassCastException if <code>xPathList</code> contains any
     *    entries that are not of type {@link XPathType}
     * @throws IllegalArgumentException if <code>xPathList</code> is empty
     * @throws NullPointerException if <code>xPathList</code> is 
     *    <code>null</code>
     */
    public XPathFilter2ParameterSpec(List xPathList) {
        if (xPathList == null) {
            throw new NullPointerException("xPathList cannot be null");
        }
        this.xPathList = unmodifiableCopyOfList(xPathList);
        if (this.xPathList.isEmpty()) {
            throw new IllegalArgumentException("xPathList cannot be empty");
        }
        int size = this.xPathList.size();
        for (int i = 0; i < size; i++) {
            if (!(this.xPathList.get(i) instanceof XPathType)) {
                throw new ClassCastException
                    ("xPathList["+i+"] is not a valid type");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static List unmodifiableCopyOfList(List list) {
        return Collections.unmodifiableList(new ArrayList(list));
    }

    /**
     * Returns a list of one or more {@link XPathType} objects. 
     * <p>
     * This implementation returns an {@link Collections#unmodifiableList
     * unmodifiable list}.
     *
     * @return a <code>List</code> of <code>XPathType</code> objects
     *    (never <code>null</code> or empty)
     */
    public List getXPathList() {
        return xPathList;
    }
}
