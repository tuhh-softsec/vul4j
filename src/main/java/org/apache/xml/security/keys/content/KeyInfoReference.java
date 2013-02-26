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
package org.apache.xml.security.keys.content;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.Signature11ElementProxy;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Provides content model support for the <code>dsig11:KeyInfoReference</code> element.
 * 
 * @author Brent Putman (putmanb@georgetown.edu)
 */
public class KeyInfoReference extends Signature11ElementProxy implements KeyInfoContent {

    /**
     * Constructor RetrievalMethod
     *
     * @param element
     * @param BaseURI
     * @throws XMLSecurityException
     */
    public KeyInfoReference(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    /**
     * Constructor RetrievalMethod
     *
     * @param doc
     * @param URI
     */
    public KeyInfoReference(Document doc, String URI) {
        super(doc);

        setLocalAttribute(Constants._ATT_URI, URI);
    }

    /**
     * Method getURIAttr
     *
     * @return the URI attribute
     */
    public Attr getURIAttr() {
        return getElement().getAttributeNodeNS(null, Constants._ATT_URI);
    }

    /**
     * Method getURI
     *
     * @return URI string
     */
    public String getURI() {
        return this.getURIAttr().getNodeValue();
    }

    /**
     * Sets the <code>Id</code> attribute
     *
     * @param Id ID
     */
    public void setId(String id) {
        setLocalIdAttribute(Constants._ATT_ID, id);
    }

    /**
     * Returns the <code>Id</code> attribute
     *
     * @return the <code>Id</code> attribute
     */
    public String getId() {
        return getLocalAttribute(Constants._ATT_ID);
    }

    /** @inheritDoc */
    public String getBaseLocalName() {
        return Constants._TAG_KEYINFOREFERENCE;
    }
}
