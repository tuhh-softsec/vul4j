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

import java.io.ByteArrayInputStream;
import java.security.cert.*;
import java.util.*;
import javax.xml.crypto.*;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.security.auth.x500.X500Principal;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.Base64;

/**
 * DOM-based implementation of X509Data.
 *
 * @author Sean Mullan
 */
//@@@ check for illegal combinations of data violating MUSTs in W3c spec
public final class DOMX509Data extends BaseStructure implements X509Data {

    private final List<Object> content;
    private CertificateFactory cf;

    /**
     * Creates a DOMX509Data.
     *
     * @param content a list of one or more X.509 data types. Valid types are
     *    {@link String} (subject names), <code>byte[]</code> (subject key ids),
     *    {@link java.security.cert.X509Certificate}, {@link X509CRL},
     *    or {@link javax.xml.dsig.XMLStructure} ({@link X509IssuerSerial}
     *    objects or elements from an external namespace). The list is 
     *    defensively copied to protect against subsequent modification.
     * @return a <code>X509Data</code>
     * @throws NullPointerException if <code>content</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>content</code> is empty
     * @throws ClassCastException if <code>content</code> contains any entries
     *    that are not of one of the valid types mentioned above
     */
    public DOMX509Data(List<?> content) {
        if (content == null) {
            throw new NullPointerException("content cannot be null");
        }
        List<Object> contentCopy = new ArrayList<Object>(content);
        if (contentCopy.isEmpty()) {
            throw new IllegalArgumentException("content cannot be empty");
        }
        for (int i = 0, size = contentCopy.size(); i < size; i++) {
            Object x509Type = contentCopy.get(i);
            if (x509Type instanceof String) {
                new X500Principal((String)x509Type);
            } else if (!(x509Type instanceof byte[]) &&
                !(x509Type instanceof X509Certificate) &&
                !(x509Type instanceof X509CRL) &&
                !(x509Type instanceof XMLStructure)) {
                throw new ClassCastException
                    ("content["+i+"] is not a valid X509Data type");
            }
        }
        this.content = Collections.unmodifiableList(contentCopy);
    }

    /**
     * Creates a <code>DOMX509Data</code> from an element.
     *
     * @param xdElem an X509Data element
     * @throws MarshalException if there is an error while unmarshalling
     */
    public DOMX509Data(Element xdElem) throws MarshalException {
        // get all children nodes
        NodeList nl = xdElem.getChildNodes();
        int length = nl.getLength();
        List<Object> content = new ArrayList<Object>(length);
        for (int i = 0; i < length; i++) {
            Node child = nl.item(i);
            // ignore all non-Element nodes
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element childElem = (Element)child;
            String localName = childElem.getLocalName();
            if (localName.equals("X509Certificate")) {
                content.add(unmarshalX509Certificate(childElem));
            } else if (localName.equals("X509IssuerSerial")) {
                content.add(new DOMX509IssuerSerial(childElem));
            } else if (localName.equals("X509SubjectName")) {
                content.add(childElem.getFirstChild().getNodeValue());
            } else if (localName.equals("X509SKI")) {
                try {
                    content.add(Base64.decode(childElem));
                } catch (Base64DecodingException bde) {
                    throw new MarshalException("cannot decode X509SKI", bde);
                }
            } else if (localName.equals("X509CRL")) {
                content.add(unmarshalX509CRL(childElem));
            } else {
                content.add(new javax.xml.crypto.dom.DOMStructure(childElem));
            }
        }
        this.content = Collections.unmodifiableList(content);
    }

    @Override
    public List<Object> getContent() {
        return content;
    }

    public static void marshal(XmlWriter xwriter, X509Data x509Data, String dsPrefix, XMLCryptoContext context)
        throws MarshalException
    {
        xwriter.writeStartElement(dsPrefix, "X509Data", XMLSignature.XMLNS);

        @SuppressWarnings("unchecked")
        List<Object> content = x509Data.getContent();
        // append children and preserve order
        for (int i = 0, size = content.size(); i < size; i++) {
            Object object = content.get(i);
            if (object instanceof X509Certificate) {
                marshalCert(xwriter, (X509Certificate) object,dsPrefix);
            } else if (object instanceof XMLStructure) {
                xwriter.marshalStructure((XMLStructure) object, dsPrefix, context);
            } else if (object instanceof byte[]) {
                marshalSKI(xwriter, (byte[]) object, dsPrefix);
            } else if (object instanceof String) {
                marshalSubjectName(xwriter, (String) object, dsPrefix);
            } else if (object instanceof X509CRL) {
                marshalCRL(xwriter, (X509CRL) object, dsPrefix);
            }
        }
        xwriter.writeEndElement(); // "X509Data"
    }

    private static void marshalSKI(XmlWriter xwriter, byte[] skid, String dsPrefix)
    {
        xwriter.writeTextElement(dsPrefix, "X509SKI", XMLSignature.XMLNS, Base64.encode(skid));
    }

    private static void marshalSubjectName(XmlWriter xwriter, String name, String dsPrefix)
    {
        xwriter.writeTextElement(dsPrefix, "X509SubjectName", XMLSignature.XMLNS, name);
    }

    private static void marshalCert(XmlWriter xwriter, X509Certificate cert, String dsPrefix)
        throws MarshalException
    {
        try {
            byte[] encoded = cert.getEncoded();
            xwriter.writeTextElement(dsPrefix, "X509Certificate", XMLSignature.XMLNS, Base64.encode(encoded));
        } catch (CertificateEncodingException e) {
            throw new MarshalException("Error encoding X509Certificate", e);
        }
    }

    private static void marshalCRL(XmlWriter xwriter, X509CRL crl, String dsPrefix)
        throws MarshalException
    {
        try {
            byte[] encoded = crl.getEncoded();
            xwriter.writeTextElement(dsPrefix, "X509CRL", XMLSignature.XMLNS, Base64.encode(encoded));
        } catch (CRLException e) {
            throw new MarshalException("Error encoding X509CRL", e);
        }
    }

    private X509Certificate unmarshalX509Certificate(Element elem) 
        throws MarshalException
    {
        try {
            ByteArrayInputStream bs = unmarshalBase64Binary(elem);
            return (X509Certificate)cf.generateCertificate(bs);
        } catch (CertificateException e) {
            throw new MarshalException("Cannot create X509Certificate", e);
        }
    }

    private X509CRL unmarshalX509CRL(Element elem) throws MarshalException {
        try {
            ByteArrayInputStream bs = unmarshalBase64Binary(elem);
            return (X509CRL)cf.generateCRL(bs);
        } catch (CRLException e) {
            throw new MarshalException("Cannot create X509CRL", e);
        }
    }

    private ByteArrayInputStream unmarshalBase64Binary(Element elem) 
        throws MarshalException {
        try {
            if (cf == null) {
                cf = CertificateFactory.getInstance("X.509");
            }
            return new ByteArrayInputStream(Base64.decode(elem));
        } catch (CertificateException e) {
            throw new MarshalException("Cannot create CertificateFactory", e);
        } catch (Base64DecodingException bde) {
            throw new MarshalException("Cannot decode Base64-encoded val", bde);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof X509Data)) {
            return false;
        }
        X509Data oxd = (X509Data)o;

        @SuppressWarnings("unchecked") List<Object> ocontent = oxd.getContent();
        int size = content.size();
        if (size != ocontent.size()) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            Object x = content.get(i);
            Object ox = ocontent.get(i);
            if (x instanceof byte[]) {
                if (!(ox instanceof byte[]) || 
                    !Arrays.equals((byte[])x, (byte[])ox)) {
                    return false;
                } 
            } else {
                if (!(x.equals(ox))) {
                    return false;
                }
            }
        }

        return true;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + content.hashCode();

        return result;
    }
}
