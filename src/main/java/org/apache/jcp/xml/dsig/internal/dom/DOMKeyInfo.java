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
package org.apache.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.*;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;

import java.security.Provider;
import java.util.*;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOM-based implementation of KeyInfo.
 *
 * @author Sean Mullan
 */
public final class DOMKeyInfo extends BaseStructure implements KeyInfo {

    private final String id;
    private final List<XMLStructure> keyInfoTypes;

    /**
     * A utility function to suppress casting warnings.
     * @param ki
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<XMLStructure> getContent(KeyInfo ki) {
        return ki.getContent();
    }
    
    /**
     * Creates a <code>DOMKeyInfo</code>.
     *
     * @param content a list of one or more {@link XMLStructure}s representing
     *    key information types. The list is defensively copied to protect
     *    against subsequent modification.
     * @param id an ID attribute
     * @throws NullPointerException if <code>content</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>content</code> is empty
     * @throws ClassCastException if <code>content</code> contains any entries
     *    that are not of type {@link XMLStructure}
     */
    public DOMKeyInfo(List<? extends XMLStructure> content, String id) {
        if (content == null) {
            throw new NullPointerException("content cannot be null");
        }
        this.keyInfoTypes =
            Collections.unmodifiableList(new ArrayList<XMLStructure>(content));
        if (this.keyInfoTypes.isEmpty()) {
            throw new IllegalArgumentException("content cannot be empty");
        }
        for (int i = 0, size = this.keyInfoTypes.size(); i < size; i++) {
            if (!(this.keyInfoTypes.get(i) instanceof XMLStructure)) {
                throw new ClassCastException
                    ("content["+i+"] is not a valid KeyInfo type");
            }
        }
        this.id = id;
    }

    /**
     * Creates a <code>DOMKeyInfo</code> from XML.
     *
     * @param kiElem KeyInfo element
     */
    public DOMKeyInfo(Element kiElem, XMLCryptoContext context,
                      Provider provider)
        throws MarshalException
    {
        id = DOMUtils.getIdAttributeValue(kiElem, "Id");

        // get all children nodes
        NodeList nl = kiElem.getChildNodes();
        int length = nl.getLength();
        if (length < 1) {
            throw new MarshalException
                ("KeyInfo must contain at least one type");
        }
        List<XMLStructure> content = new ArrayList<XMLStructure>(length);
        for (int i = 0; i < length; i++) {
            Node child = nl.item(i);
            // ignore all non-Element nodes
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element childElem = (Element)child;
            String localName = childElem.getLocalName();
            if (localName.equals("X509Data")) {
                content.add(new DOMX509Data(childElem));
            } else if (localName.equals("KeyName")) {
                content.add(new DOMKeyName(childElem));
            } else if (localName.equals("KeyValue")) {
                content.add(DOMKeyValue.unmarshal(childElem));
            } else if (localName.equals("RetrievalMethod")) {
                content.add(new DOMRetrievalMethod(childElem,
                                                   context, provider));
            } else if (localName.equals("PGPData")) {
                content.add(new DOMPGPData(childElem));
            } else { //may be MgmtData, SPKIData or element from other namespace
                content.add(new javax.xml.crypto.dom.DOMStructure(childElem));
            }
        }
        keyInfoTypes = Collections.unmodifiableList(content);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<XMLStructure> getContent() {
        return keyInfoTypes;
    }

    @Override
    public void marshal(XMLStructure parent, XMLCryptoContext context)
        throws MarshalException
    {
        if (parent == null) {
            throw new NullPointerException("parent is null");
        }
        if (!(parent instanceof javax.xml.crypto.dom.DOMStructure)) {
            throw new ClassCastException("parent must be of type DOMStructure");
        }

        internalMarshal( (javax.xml.crypto.dom.DOMStructure) parent, context);
    }

    private void internalMarshal(javax.xml.crypto.dom.DOMStructure parent, XMLCryptoContext context)
            throws MarshalException {
        Node pNode = parent.getNode();
        String dsPrefix = DOMUtils.getSignaturePrefix(context);

        XmlWriterToTree xwriter = new XmlWriterToTree(Marshaller.getMarshallers(), pNode);        
        marshalInternal(xwriter, this, dsPrefix, context, true);
    }

    public static void marshal(XmlWriter xwriter, KeyInfo ki, String dsPrefix,
    XMLCryptoContext context) throws MarshalException {
        marshalInternal(xwriter, ki, dsPrefix, context, false);
    }

    private static void marshalInternal(XmlWriter xwriter, KeyInfo ki,
        String dsPrefix, XMLCryptoContext context, boolean declareNamespace) throws MarshalException {

        xwriter.writeStartElement(dsPrefix, "KeyInfo", XMLSignature.XMLNS);
        if (declareNamespace) {
            xwriter.writeNamespace(dsPrefix, XMLSignature.XMLNS);
        }
        
        xwriter.writeIdAttribute("", "", "Id", ki.getId());
        // create and append KeyInfoType elements
        List<XMLStructure> keyInfoTypes = getContent(ki);
        for (XMLStructure kiType : keyInfoTypes) {
            xwriter.marshalStructure(kiType, dsPrefix, context);
        }

        xwriter.writeEndElement(); // "KeyInfo"
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof KeyInfo)) {
            return false;
        }
        KeyInfo oki = (KeyInfo)o;

        boolean idsEqual = id == null ? oki.getId() == null
                                       : id.equals(oki.getId());

        return keyInfoTypes.equals(oki.getContent()) && idsEqual;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        if (id != null) {
            result = 31 * result + id.hashCode();
        }
        result = 31 * result + keyInfoTypes.hashCode();
        
        return result;
    }
}
