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

import javax.xml.crypto.dsig.Transform;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Parameters for the <a href="http://www.w3.org/TR/xmldsig-core/#sec-XPath">
 * XPath Filtering Transform Algorithm</a>.
 * The parameters include the XPath expression and an optional <code>Map</code> 
 * of additional namespace prefix mappings. The XML Schema Definition of
 * the XPath Filtering transform parameters is defined as:
 * <pre><code>
 * &lt;element name="XPath" type="string"/&gt;
 * </code></pre>
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 * @see Transform
 */
public final class XPathFilterParameterSpec implements TransformParameterSpec {

    private String xPath;
    private Map nsMap;

    /**
     * Creates an <code>XPathFilterParameterSpec</code> with the specified 
     * XPath expression.
     *
     * @param xPath the XPath expression to be evaluated
     * @throws NullPointerException if <code>xPath</code> is <code>null</code>
     */
    public XPathFilterParameterSpec(String xPath) {
        if (xPath == null) {
            throw new NullPointerException();
        }
        this.xPath = xPath;
        this.nsMap = Collections.EMPTY_MAP;
    }

    /**
     * Creates an <code>XPathFilterParameterSpec</code> with the specified 
     * XPath expression and namespace map. The map is copied to protect against
     * subsequent modification.
     *
     * @param xPath the XPath expression to be evaluated
     * @param namespaceMap the map of namespace prefixes. Each key is a
     *    namespace prefix <code>String</code> that maps to a corresponding
     *    namespace URI <code>String</code>.
     * @throws NullPointerException if <code>xPath</code> or
     *    <code>namespaceMap</code> are <code>null</code>
     * @throws ClassCastException if any of the map's keys or entries are not
     *    of type <code>String</code>
     */
    public XPathFilterParameterSpec(String xPath, Map namespaceMap) {
        if (xPath == null || namespaceMap == null) {
            throw new NullPointerException();
        }
        this.xPath = xPath;
        nsMap = unmodifiableCopyOfMap(namespaceMap);
        Iterator entries = nsMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry me = (Map.Entry) entries.next();
            if (!(me.getKey() instanceof String) || 
                !(me.getValue() instanceof String)) {
                throw new ClassCastException("not a String");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Map unmodifiableCopyOfMap(Map map) {
        return Collections.unmodifiableMap(new HashMap(map));
    }

    /**
     * Returns the XPath expression to be evaluated.
     *
     * @return the XPath expression to be evaluated
     */
    public String getXPath() {
        return xPath;
    }

    /**
     * Returns a map of namespace prefixes. Each key is a namespace prefix 
     * <code>String</code> that maps to a corresponding namespace URI 
     * <code>String</code>.
     * <p>
     * This implementation returns an {@link Collections#unmodifiableMap 
     * unmodifiable map}.
     *
     * @return a <code>Map</code> of namespace prefixes to namespace URIs (may 
     *    be empty, but never <code>null</code>)
     */
    public Map getNamespaceMap() {
        return nsMap;
    }
}
