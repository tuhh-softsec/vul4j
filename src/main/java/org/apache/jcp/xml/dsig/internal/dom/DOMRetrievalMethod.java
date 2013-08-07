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
 * Portions copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
/*
 * =========================================================================== 
 *
 * (C) Copyright IBM Corp. 2003 All Rights Reserved.
 *
 * ===========================================================================
 */
/*
 * $Id$
 */
package org.apache.jcp.xml.dsig.internal.dom;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Provider;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.crypto.*;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dom.DOMURIReference;
import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;
import javax.xml.parsers.*;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOM-based implementation of RetrievalMethod.
 *
 * @author Sean Mullan
 * @author Joyce Leung
 */
public final class DOMRetrievalMethod extends DOMStructure
    implements RetrievalMethod, DOMURIReference {

    private final List<Transform> transforms;
    private String uri;
    private String type;
    private Attr here;

    /**
     * Creates a <code>DOMRetrievalMethod</code> containing the specified 
     * URIReference and List of Transforms.
     *
     * @param uri the URI
     * @param type the type
     * @param transforms a list of {@link Transform}s. The list is defensively
     *    copied to prevent subsequent modification. May be <code>null</code>
     *    or empty.
     * @throws IllegalArgumentException if the format of <code>uri</code> is 
     *    invalid, as specified by Reference's URI attribute in the W3C
     *    specification for XML-Signature Syntax and Processing
     * @throws NullPointerException if <code>uriReference</code>
     *    is <code>null</code> 
     * @throws ClassCastException if <code>transforms</code> contains any
     *    entries that are not of type {@link Transform}
     */
    public DOMRetrievalMethod(String uri, String type,
                              List<? extends Transform> transforms)
    {
        if (uri == null) {
            throw new NullPointerException("uri cannot be null");
        }
        if (transforms == null || transforms.isEmpty()) {
            this.transforms = Collections.emptyList();
        } else {
            this.transforms = Collections.unmodifiableList(
                new ArrayList<Transform>(transforms));
            for (int i = 0, size = this.transforms.size(); i < size; i++) {
                if (!(this.transforms.get(i) instanceof Transform)) {
                    throw new ClassCastException
                        ("transforms["+i+"] is not a valid type");
                }
            }
        }
        this.uri = uri;
        if (!uri.equals("")) {
            try {
                new URI(uri);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        this.type = type;
    }
        
    /**
     * Creates a <code>DOMRetrievalMethod</code> from an element.
     *
     * @param rmElem a RetrievalMethod element
     */
    public DOMRetrievalMethod(Element rmElem, XMLCryptoContext context,
                              Provider provider)
        throws MarshalException
    {
        // get URI and Type attributes
        uri = DOMUtils.getAttributeValue(rmElem, "URI");
        type = DOMUtils.getAttributeValue(rmElem, "Type");

        // get here node
        here = rmElem.getAttributeNodeNS(null, "URI");
        
        boolean secVal = Utils.secureValidation(context);

        // get Transforms, if specified
        List<Transform> transforms = new ArrayList<Transform>();
        Element transformsElem = DOMUtils.getFirstChildElement(rmElem);
        
        int transformCount = 0;
        if (transformsElem != null) {
            Element transformElem =
                DOMUtils.getFirstChildElement(transformsElem);
            while (transformElem != null) {
                transforms.add
                    (new DOMTransform(transformElem, context, provider));
                transformElem = DOMUtils.getNextSiblingElement(transformElem);
                
                transformCount++;
                if (secVal && transformCount > DOMReference.MAXIMUM_TRANSFORM_COUNT) {
                    String error = "A maxiumum of " + DOMReference.MAXIMUM_TRANSFORM_COUNT + " " 
                        + "transforms per Reference are allowed with secure validation";
                    throw new MarshalException(error);
                }
            }
        }
        if (transforms.isEmpty()) {
            this.transforms = Collections.emptyList();
        } else {
            this.transforms = Collections.unmodifiableList(transforms);
        }
    }

    @Override
    public String getURI() {
        return uri;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public List<Transform> getTransforms() {
        return transforms;
    }

    @Override
    public void marshal(XmlWriter xwriter, String dsPrefix, XMLCryptoContext context)
        throws MarshalException
    {
        xwriter.writeStartElement(dsPrefix, "RetrievalMethod", XMLSignature.XMLNS);

        // TODO - see whether it is important to capture the "here" attribute as part of the
        // marshalling - do any of the tests fail?
        // add URI and Type attributes
        here = xwriter.writeAttribute("", "", "URI", uri);
        xwriter.writeAttribute("", "", "Type", type);

        // add Transforms elements
        if (!transforms.isEmpty()) {
            xwriter.writeStartElement(dsPrefix, "Transforms", XMLSignature.XMLNS);
            for (Transform transform : transforms) {
                ((DOMTransform)transform).marshal(xwriter, dsPrefix, context);
            }
            xwriter.writeEndElement(); // "Transforms"
        }
        xwriter.writeEndElement(); // "RetrievalMethod"
    }

    @Override
    public Node getHere() {
        return here;
    }

    @Override
    public Data dereference(XMLCryptoContext context)
        throws URIReferenceException
    {
        if (context == null) {
            throw new NullPointerException("context cannot be null");
        }

        /*
         * If URIDereferencer is specified in context; use it, otherwise use 
         * built-in.
         */
        URIDereferencer deref = context.getURIDereferencer();
        if (deref == null) {
            deref = DOMURIDereferencer.INSTANCE;
        }

        Data data = deref.dereference(this, context);

        // pass dereferenced data through Transforms
        try {
            for (Transform transform : transforms) {
                data = transform.transform(data, context);
            }
        } catch (Exception e) {
            throw new URIReferenceException(e);
        }
        return data;
    }

    public XMLStructure dereferenceAsXMLStructure(XMLCryptoContext context)
        throws URIReferenceException
    {
        try {
            ApacheData data = (ApacheData)dereference(context);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new ByteArrayInputStream
                (data.getXMLSignatureInput().getBytes()));
            Element kiElem = doc.getDocumentElement();
            if (kiElem.getLocalName().equals("X509Data")) {
                return new DOMX509Data(kiElem);
            } else {
                return null; // unsupported
            }
        } catch (Exception e) {
            throw new URIReferenceException(e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RetrievalMethod)) {
            return false;
        }
        RetrievalMethod orm = (RetrievalMethod)obj;

        boolean typesEqual = type == null ? orm.getType() == null
                                           : type.equals(orm.getType());

        return uri.equals(orm.getURI()) &&
            transforms.equals(orm.getTransforms()) && typesEqual;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        if (type != null) {
            result = 31 * result + type.hashCode();
        }
        result = 31 * result + uri.hashCode();
        result = 31 * result + transforms.hashCode();
        
        return result;
    }
}
