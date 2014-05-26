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
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;

import java.io.IOException;
import java.security.*;
import java.security.interfaces.DSAKey;
import java.security.spec.AlgorithmParameterSpec;
import org.w3c.dom.Element;

import org.apache.xml.security.algorithms.implementations.SignatureECDSA;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.jcp.xml.dsig.internal.SignerOutputStream;

/**
 * DOM-based abstract implementation of SignatureMethod.
 *
 * @author Sean Mullan
 */
public abstract class DOMSignatureMethod extends AbstractDOMSignatureMethod {

    private static org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(DOMSignatureMethod.class);

    private SignatureMethodParameterSpec params;
    private Signature signature;
    
    // see RFC 4051 for these algorithm definitions
    static final String RSA_SHA256 =
        "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    static final String RSA_SHA384 =
        "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
    static final String RSA_SHA512 =
        "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
    static final String RSA_RIPEMD160 =
        "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
    static final String ECDSA_SHA1 =
        "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
    static final String ECDSA_SHA224 =
        "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224";
    static final String ECDSA_SHA256 =
        "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
    static final String ECDSA_SHA384 =
        "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
    static final String ECDSA_SHA512 =
        "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
    static final String ECDSA_RIPEMD160 =
        "http://www.w3.org/2007/05/xmldsig-more#ecdsa-ripemd160";
    static final String DSA_SHA256 =
        "http://www.w3.org/2009/xmldsig11#dsa-sha256";

    /**
     * Creates a <code>DOMSignatureMethod</code>.
     *
     * @param params the algorithm-specific params (may be <code>null</code>)
     * @throws InvalidAlgorithmParameterException if the parameters are not
     *    appropriate for this signature method
     */
    DOMSignatureMethod(AlgorithmParameterSpec params) 
        throws InvalidAlgorithmParameterException
    {
        if (params != null && 
            !(params instanceof SignatureMethodParameterSpec)) {
            throw new InvalidAlgorithmParameterException
                ("params must be of type SignatureMethodParameterSpec");
        }
        checkParams((SignatureMethodParameterSpec)params);
        this.params = (SignatureMethodParameterSpec)params;
    }

    /**
     * Creates a <code>DOMSignatureMethod</code> from an element. This ctor
     * invokes the {@link #unmarshalParams unmarshalParams} method to
     * unmarshal any algorithm-specific input parameters.
     *
     * @param smElem a SignatureMethod element
     */
    DOMSignatureMethod(Element smElem) throws MarshalException {
        Element paramsElem = DOMUtils.getFirstChildElement(smElem);
        if (paramsElem != null) {
            params = unmarshalParams(paramsElem);
        }
        try {
            checkParams(params);
        } catch (InvalidAlgorithmParameterException iape) {
            throw new MarshalException(iape);
        }
    }

    static SignatureMethod unmarshal(Element smElem) throws MarshalException {
        String alg = DOMUtils.getAttributeValue(smElem, "Algorithm");
        if (alg.equals(SignatureMethod.RSA_SHA1)) {
            return new SHA1withRSA(smElem);
        } else if (alg.equals(RSA_SHA256)) {
            return new SHA256withRSA(smElem);
        } else if (alg.equals(RSA_SHA384)) {
            return new SHA384withRSA(smElem);
        } else if (alg.equals(RSA_SHA512)) {
            return new SHA512withRSA(smElem);
        } else if (alg.equals(RSA_RIPEMD160)) {
            return new RIPEMD160withRSA(smElem);
        } else if (alg.equals(SignatureMethod.DSA_SHA1)) {
            return new SHA1withDSA(smElem);
        } else if (alg.equals(DSA_SHA256)) {
            return new SHA256withDSA(smElem);
        } else if (alg.equals(ECDSA_SHA1)) {
            return new SHA1withECDSA(smElem);
        } else if (alg.equals(ECDSA_SHA224)) {
            return new SHA224withECDSA(smElem);
        } else if (alg.equals(ECDSA_SHA256)) {
            return new SHA256withECDSA(smElem);
        } else if (alg.equals(ECDSA_SHA384)) {
            return new SHA384withECDSA(smElem);
        } else if (alg.equals(ECDSA_SHA512)) {
            return new SHA512withECDSA(smElem);
        } else if (alg.equals(ECDSA_RIPEMD160)) {
            return new RIPEMD160withECDSA(smElem);
        } else if (alg.equals(SignatureMethod.HMAC_SHA1)) {
            return new DOMHMACSignatureMethod.SHA1(smElem);
        } else if (alg.equals(DOMHMACSignatureMethod.HMAC_SHA224)) {
            return new DOMHMACSignatureMethod.SHA224(smElem);
        } else if (alg.equals(DOMHMACSignatureMethod.HMAC_SHA256)) {
            return new DOMHMACSignatureMethod.SHA256(smElem);
        } else if (alg.equals(DOMHMACSignatureMethod.HMAC_SHA384)) {
            return new DOMHMACSignatureMethod.SHA384(smElem);
        } else if (alg.equals(DOMHMACSignatureMethod.HMAC_SHA512)) {
            return new DOMHMACSignatureMethod.SHA512(smElem);
        } else if (alg.equals(DOMHMACSignatureMethod.HMAC_RIPEMD160)) {
            return new DOMHMACSignatureMethod.RIPEMD160(smElem);
        } else {
            throw new MarshalException
                ("unsupported SignatureMethod algorithm: " + alg);
        }
    }

    @Override
    public final AlgorithmParameterSpec getParameterSpec() {
        return params;
    }

    @Override
    boolean verify(Key key, DOMSignedInfo si, byte[] sig,
                   XMLValidateContext context)
        throws InvalidKeyException, SignatureException, XMLSignatureException
    {
        if (key == null || si == null || sig == null) {
            throw new NullPointerException();
        }

        if (!(key instanceof PublicKey)) {
            throw new InvalidKeyException("key must be PublicKey");
        }
        if (signature == null) {
            try {
                Provider p = (Provider)context.getProperty
                    ("org.jcp.xml.dsig.internal.dom.SignatureProvider");
                signature = (p == null)
                    ? Signature.getInstance(getJCAAlgorithm()) 
                    : Signature.getInstance(getJCAAlgorithm(), p);
            } catch (NoSuchAlgorithmException nsae) {
                throw new XMLSignatureException(nsae);
            }
        }
        signature.initVerify((PublicKey)key);
        if (log.isDebugEnabled()) {
            log.debug("Signature provider:" + signature.getProvider());
            log.debug("verifying with key: " + key);
        }
        si.canonicalize(context, new SignerOutputStream(signature));

        try {
            Type type = getAlgorithmType();
            if (type == Type.DSA) {
                int size = ((DSAKey)key).getParams().getQ().bitLength();
                return signature.verify(JavaUtils.convertDsaXMLDSIGtoASN1(sig,
                                                                       size/8));
            } else if (type == Type.ECDSA) {
                return signature.verify(SignatureECDSA.convertXMLDSIGtoASN1(sig));
            } else {
                return signature.verify(sig);
            }
        } catch (IOException ioe) {
            throw new XMLSignatureException(ioe);
        }
    }

    @Override
    byte[] sign(Key key, DOMSignedInfo si, XMLSignContext context)
        throws InvalidKeyException, XMLSignatureException
    {
        if (key == null || si == null) {
            throw new NullPointerException();
        }

        if (!(key instanceof PrivateKey)) {
            throw new InvalidKeyException("key must be PrivateKey");
        }
        if (signature == null) {
            try {
                Provider p = (Provider)context.getProperty
                    ("org.jcp.xml.dsig.internal.dom.SignatureProvider");
                signature = (p == null)
                    ? Signature.getInstance(getJCAAlgorithm()) 
                    : Signature.getInstance(getJCAAlgorithm(), p);
            } catch (NoSuchAlgorithmException nsae) {
                throw new XMLSignatureException(nsae);
            }
        }
        signature.initSign((PrivateKey)key);
        if (log.isDebugEnabled()) {
            log.debug("Signature provider:" + signature.getProvider());
            log.debug("Signing with key: " + key);
        }

        si.canonicalize(context, new SignerOutputStream(signature));

        try {
            Type type = getAlgorithmType();
            if (type == Type.DSA) {
                int size = ((DSAKey)key).getParams().getQ().bitLength();
                return JavaUtils.convertDsaASN1toXMLDSIG(signature.sign(),
                                                         size/8);
            } else if (type == Type.ECDSA) {
                return SignatureECDSA.convertASN1toXMLDSIG(signature.sign());
            } else {
                return signature.sign();
            }
        } catch (SignatureException se) {
            throw new XMLSignatureException(se);
        } catch (IOException ioe) {
            throw new XMLSignatureException(ioe);
        }
    }

    static final class SHA1withRSA extends DOMSignatureMethod {
        SHA1withRSA(AlgorithmParameterSpec params)
            throws InvalidAlgorithmParameterException {
            super(params);
        }
        SHA1withRSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }
        @Override
        public String getAlgorithm() {
            return SignatureMethod.RSA_SHA1;
        }
        @Override
        String getJCAAlgorithm() {
            return "SHA1withRSA";
        }
        @Override
        Type getAlgorithmType() {
            return Type.RSA;
        }
    }

    static final class SHA256withRSA extends DOMSignatureMethod {
        SHA256withRSA(AlgorithmParameterSpec params)
            throws InvalidAlgorithmParameterException {
            super(params);
        }
        SHA256withRSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }
        @Override
        public String getAlgorithm() {
            return RSA_SHA256;
        }
        @Override
        String getJCAAlgorithm() {
            return "SHA256withRSA";
        }
        @Override
        Type getAlgorithmType() {
            return Type.RSA;
        }
    }

    static final class SHA384withRSA extends DOMSignatureMethod {
        SHA384withRSA(AlgorithmParameterSpec params)
            throws InvalidAlgorithmParameterException {
            super(params);
        }
        SHA384withRSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }
        @Override
        public String getAlgorithm() {
            return RSA_SHA384;
        }
        @Override
        String getJCAAlgorithm() {
            return "SHA384withRSA";
        }
        @Override
        Type getAlgorithmType() {
            return Type.RSA;
        }
    }

    static final class SHA512withRSA extends DOMSignatureMethod {
        SHA512withRSA(AlgorithmParameterSpec params)
            throws InvalidAlgorithmParameterException {
            super(params);
        }
        SHA512withRSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }
        @Override
        public String getAlgorithm() {
            return RSA_SHA512;
        }
        @Override
        String getJCAAlgorithm() {
            return "SHA512withRSA";
        }
        @Override
        Type getAlgorithmType() {
            return Type.RSA;
        }
    }
    
    static final class RIPEMD160withRSA extends DOMSignatureMethod {
        RIPEMD160withRSA(AlgorithmParameterSpec params)
            throws InvalidAlgorithmParameterException {
            super(params);
        }
        RIPEMD160withRSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }
        @Override
        public String getAlgorithm() {
            return RSA_RIPEMD160;
        }
        @Override
        String getJCAAlgorithm() {
            return "RIPEMD160withRSA";
        }
        @Override
        Type getAlgorithmType() {
            return Type.RSA;
        }
    }

    static final class SHA1withDSA extends DOMSignatureMethod {
        SHA1withDSA(AlgorithmParameterSpec params)
            throws InvalidAlgorithmParameterException {
            super(params);
        }
        SHA1withDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }
        @Override
        public String getAlgorithm() {
            return SignatureMethod.DSA_SHA1;
        }
        @Override
        String getJCAAlgorithm() {
            return "SHA1withDSA";
        }
        @Override
        Type getAlgorithmType() {
            return Type.DSA;
        }
    }

    static final class SHA256withDSA extends DOMSignatureMethod {
        SHA256withDSA(AlgorithmParameterSpec params)
            throws InvalidAlgorithmParameterException {
            super(params);
        }
        SHA256withDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }
        public String getAlgorithm() {
            return DSA_SHA256;
        }
        String getJCAAlgorithm() {
            return "SHA256withDSA";
        }
        Type getAlgorithmType() {
            return Type.DSA;
        }
    }

    static final class SHA1withECDSA extends DOMSignatureMethod {
        SHA1withECDSA(AlgorithmParameterSpec params)
            throws InvalidAlgorithmParameterException {
            super(params);
        }
        SHA1withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }
        @Override
        public String getAlgorithm() {
            return ECDSA_SHA1;
        }
        @Override
        String getJCAAlgorithm() {
            return "SHA1withECDSA";
        }
        @Override
        Type getAlgorithmType() {
            return Type.ECDSA;
        }
    }
    
    static final class SHA224withECDSA extends DOMSignatureMethod {
        SHA224withECDSA(AlgorithmParameterSpec params)
            throws InvalidAlgorithmParameterException {
            super(params);
        }
        SHA224withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }
        @Override
        public String getAlgorithm() {
            return ECDSA_SHA224;
        }
        @Override
        String getJCAAlgorithm() {
            return "SHA224withECDSA";
        }
        @Override
        Type getAlgorithmType() {
            return Type.ECDSA;
        }
    }

    static final class SHA256withECDSA extends DOMSignatureMethod {
        SHA256withECDSA(AlgorithmParameterSpec params)
            throws InvalidAlgorithmParameterException {
            super(params);
        }
        SHA256withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }
        @Override
        public String getAlgorithm() {
            return ECDSA_SHA256;
        }
        @Override
        String getJCAAlgorithm() {
            return "SHA256withECDSA";
        }
        @Override
        Type getAlgorithmType() {
            return Type.ECDSA;
        }
    }

    static final class SHA384withECDSA extends DOMSignatureMethod {
        SHA384withECDSA(AlgorithmParameterSpec params)
            throws InvalidAlgorithmParameterException {
            super(params);
        }
        SHA384withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }
        @Override
        public String getAlgorithm() {
            return ECDSA_SHA384;
        }
        @Override
        String getJCAAlgorithm() {
            return "SHA384withECDSA";
        }
        @Override
        Type getAlgorithmType() {
            return Type.ECDSA;
        }
    }

    static final class SHA512withECDSA extends DOMSignatureMethod {
        SHA512withECDSA(AlgorithmParameterSpec params)
            throws InvalidAlgorithmParameterException {
            super(params);
        }
        SHA512withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }
        @Override
        public String getAlgorithm() {
            return ECDSA_SHA512;
        }
        @Override
        String getJCAAlgorithm() {
            return "SHA512withECDSA";
        }
        @Override
        Type getAlgorithmType() {
            return Type.ECDSA;
        }
    }
    
    static final class RIPEMD160withECDSA extends DOMSignatureMethod {
        RIPEMD160withECDSA(AlgorithmParameterSpec params)
            throws InvalidAlgorithmParameterException {
            super(params);
        }
        RIPEMD160withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }
        @Override
        public String getAlgorithm() {
            return ECDSA_RIPEMD160;
        }
        @Override
        String getJCAAlgorithm() {
            return "RIPEMD160withECDSA";
        }
        @Override
        Type getAlgorithmType() {
            return Type.ECDSA;
        }
    }
    
}
