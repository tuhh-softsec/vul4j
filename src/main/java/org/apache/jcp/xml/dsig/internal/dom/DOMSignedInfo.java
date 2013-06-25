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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.security.Provider;
import java.util.*;

import org.w3c.dom.Element;

import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.UnsyncBufferedOutputStream;

/**
 * DOM-based implementation of SignedInfo.
 *
 * @author Sean Mullan
 */
public final class DOMSignedInfo extends DOMStructure implements SignedInfo {
    
    /**
     * The maximum number of references per Manifest, if secure validation is enabled.
     */
    public static final int MAXIMUM_REFERENCE_COUNT = 30;

    private static org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(DOMSignedInfo.class);
    
    /** Signature - NOT Recommended RSAwithMD5 */
    private static final String ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5 = 
        Constants.MoreAlgorithmsSpecNS + "rsa-md5";
    
    /** HMAC - NOT Recommended HMAC-MD5 */
    private static final String ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5 = 
        Constants.MoreAlgorithmsSpecNS + "hmac-md5";
    
    private List<Reference> references;
    private CanonicalizationMethod canonicalizationMethod;
    private SignatureMethod signatureMethod;
    private String id;
    private Element localSiElem;
    private InputStream canonData;

    /**
     * Creates a <code>DOMSignedInfo</code> from the specified parameters. Use
     * this constructor when the <code>Id</code> is not specified.
     *
     * @param cm the canonicalization method
     * @param sm the signature method
     * @param references the list of references. The list is copied.
     * @throws NullPointerException if
     *    <code>cm</code>, <code>sm</code>, or <code>references</code> is 
     *    <code>null</code>
     * @throws IllegalArgumentException if <code>references</code> is empty
     * @throws ClassCastException if any of the references are not of
     *    type <code>Reference</code>
     */
    public DOMSignedInfo(CanonicalizationMethod cm, SignatureMethod sm,
                         List<? extends Reference> references) {
        if (cm == null || sm == null || references == null) {
            throw new NullPointerException();
        }
        this.canonicalizationMethod = cm;
        this.signatureMethod = sm;
        this.references = Collections.unmodifiableList(
            new ArrayList<Reference>(references));
        if (this.references.isEmpty()) {
            throw new IllegalArgumentException("list of references must " +
                "contain at least one entry");
        }
        for (int i = 0, size = this.references.size(); i < size; i++) {
            Object obj = this.references.get(i);
            if (!(obj instanceof Reference)) {
                throw new ClassCastException("list of references contains " +
                    "an illegal type");
            }
        }
    }

    /**
     * Creates a <code>DOMSignedInfo</code> from the specified parameters.
     *
     * @param cm the canonicalization method
     * @param sm the signature method
     * @param references the list of references. The list is copied.
     * @param id an optional identifer that will allow this
     *    <code>SignedInfo</code> to be referenced by other signatures and
     *    objects
     * @throws NullPointerException if <code>cm</code>, <code>sm</code>,
     *    or <code>references</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>references</code> is empty
     * @throws ClassCastException if any of the references are not of
     *    type <code>Reference</code>
     */
    public DOMSignedInfo(CanonicalizationMethod cm, SignatureMethod sm, 
                         List<? extends Reference> references, String id) {
        this(cm, sm, references);
        this.id = id;
    }

    /**
     * Creates a <code>DOMSignedInfo</code> from an element.
     *
     * @param siElem a SignedInfo element
     */
    public DOMSignedInfo(Element siElem, XMLCryptoContext context, Provider provider)
        throws MarshalException {
        localSiElem = siElem;

        // get Id attribute, if specified
        id = DOMUtils.getAttributeValue(siElem, "Id");

        // unmarshal CanonicalizationMethod
        Element cmElem = DOMUtils.getFirstChildElement(siElem);
        canonicalizationMethod = new DOMCanonicalizationMethod(cmElem, context, provider);

        // unmarshal SignatureMethod
        Element smElem = DOMUtils.getNextSiblingElement(cmElem);
        signatureMethod = DOMSignatureMethod.unmarshal(smElem);
        
        boolean secVal = Utils.secureValidation(context);

        String signatureMethodAlgorithm = signatureMethod.getAlgorithm();
        if (secVal && ((ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5.equals(signatureMethodAlgorithm)
                || ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5.equals(signatureMethodAlgorithm)))) {
            throw new MarshalException(
                "It is forbidden to use algorithm " + signatureMethod + " when secure validation is enabled"
            );
        }
        
        // unmarshal References
        ArrayList<Reference> refList = new ArrayList<Reference>(5);
        Element refElem = DOMUtils.getNextSiblingElement(smElem);
        
        int refCount = 0;
        while (refElem != null) {
            refList.add(new DOMReference(refElem, context, provider));
            refElem = DOMUtils.getNextSiblingElement(refElem);
            
            refCount++;
            if (secVal && (refCount > MAXIMUM_REFERENCE_COUNT)) {
                String error = "A maxiumum of " + MAXIMUM_REFERENCE_COUNT + " " 
                    + "references per Manifest are allowed with secure validation";
                throw new MarshalException(error);
            }
        }
        references = Collections.unmodifiableList(refList);
    }

    @Override
    public CanonicalizationMethod getCanonicalizationMethod() {
        return canonicalizationMethod;
    }

    @Override
    public SignatureMethod getSignatureMethod() {
        return signatureMethod;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<Reference> getReferences() {
        return references;
    }

    @Override
    public InputStream getCanonicalizedData() {
        return canonData;
    }

    public void canonicalize(XMLCryptoContext context, ByteArrayOutputStream bos)
        throws XMLSignatureException {
        if (context == null) {
            throw new NullPointerException("context cannot be null");
        }

        OutputStream os = new UnsyncBufferedOutputStream(bos);
        try {
            os.close();
        } catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage(), e);
            }
            // Impossible
        }

        DOMSubTreeData subTree = new DOMSubTreeData(localSiElem, true);

        try {
            ((DOMCanonicalizationMethod) 
                canonicalizationMethod).canonicalize(subTree, context, bos);
        } catch (TransformException te) {
            throw new XMLSignatureException(te);
        }

        byte[] signedInfoBytes = bos.toByteArray();

        // this whole block should only be done if logging is enabled
        if (log.isDebugEnabled()) {
            log.debug("Canonicalized SignedInfo:"); 
            StringBuilder sb = new StringBuilder(signedInfoBytes.length);
            for (int i = 0; i < signedInfoBytes.length; i++) {
                sb.append((char)signedInfoBytes[i]);
            }
            log.debug(sb.toString());
            log.debug("Data to be signed/verified:" + Base64.encode(signedInfoBytes));
        }

        this.canonData = new ByteArrayInputStream(signedInfoBytes);
    }

    @Override
    public void marshal(XmlWriter xwriter, String dsPrefix, XMLCryptoContext context)
        throws MarshalException
    {
        xwriter.writeStartElement(dsPrefix, "SignedInfo", XMLSignature.XMLNS);
        XMLStructure siStruct = xwriter.getCurrentNodeAsStructure();
        localSiElem = (Element) ((javax.xml.crypto.dom.DOMStructure) siStruct).getNode();

        // append Id attribute
        xwriter.writeIdAttribute("", "", "Id", id);
            
        // create and append CanonicalizationMethod element
        DOMCanonicalizationMethod dcm =
            (DOMCanonicalizationMethod)canonicalizationMethod;
        dcm.marshal(xwriter, dsPrefix, context); 

        // create and append SignatureMethod element
        ((AbstractDOMSignatureMethod)signatureMethod).marshal(xwriter, dsPrefix);

        // create and append Reference elements
        for (Reference reference : references) {
            // TODO - either suppress warning here, or figure out how to get rid of the cast.
            DOMReference domRef = (DOMReference)reference;
            domRef.marshal(xwriter, dsPrefix, context);
        }

        xwriter.writeEndElement(); // "SignedInfo"
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof SignedInfo)) {
            return false;
        }
        SignedInfo osi = (SignedInfo)o;

        boolean idEqual = (id == null ? osi.getId() == null
                                      : id.equals(osi.getId()));

        return (canonicalizationMethod.equals(osi.getCanonicalizationMethod()) 
                && signatureMethod.equals(osi.getSignatureMethod()) && 
                references.equals(osi.getReferences()) && idEqual);
    }

    @SuppressWarnings("unchecked")
    public static List<Reference> getSignedInfoReferences(SignedInfo si) {
        return si.getReferences();
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        if (id != null) {
            result = 31 * result + id.hashCode();
        }
        result = 31 * result + canonicalizationMethod.hashCode();
        result = 31 * result + signatureMethod.hashCode();
        result = 31 * result + references.hashCode();
        
        return result;
    }
}
