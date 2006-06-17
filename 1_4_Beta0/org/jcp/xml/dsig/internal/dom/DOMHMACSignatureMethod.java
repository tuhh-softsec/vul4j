/*
 * Copyright 2005 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
/*
 * $Id$
 */
package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.*;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.spec.HMACParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import org.jcp.xml.dsig.internal.HmacSHA1;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.jcp.xml.dsig.internal.MacOutputStream;

/**
 * DOM-based implementation of HMAC SignatureMethod.
 *
 * @author Sean Mullan
 */
public final class DOMHMACSignatureMethod extends DOMSignatureMethod {

    private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
    private HmacSHA1 hmac = new HmacSHA1();
    private int outputLength;

    /**
     * Creates a <code>DOMHMACSignatureMethod</code> with the specified params 
     *
     * @param params algorithm-specific parameters (may be <code>null</code>)
     * @throws InvalidAlgorithmParameterException if params are inappropriate
     */
    public DOMHMACSignatureMethod(AlgorithmParameterSpec params) 
	throws InvalidAlgorithmParameterException {
	super(SignatureMethod.HMAC_SHA1, params);
    }

    /**
     * Creates a <code>DOMHMACSignatureMethod</code> from an element.
     *
     * @param smElem a SignatureMethod element
     */
    public DOMHMACSignatureMethod(Element smElem) throws MarshalException {
	super(smElem);
    }

    protected void checkParams(SignatureMethodParameterSpec params) 
	throws InvalidAlgorithmParameterException {
        if (params != null) {
            if (!(params instanceof HMACParameterSpec)) {
	        throw new InvalidAlgorithmParameterException
	            ("params must be of type HMACParameterSpec");
	    }
	    outputLength = ((HMACParameterSpec) params).getOutputLength();
	    if (log.isLoggable(Level.FINE)) {
	        log.log(Level.FINE, 
		    "Setting outputLength from HMACParameterSpec to: "
		    + outputLength);
	    }
        } else {
	    outputLength = -1;
        }
    }

    protected SignatureMethodParameterSpec unmarshalParams(Element paramsElem) 
	throws MarshalException {
        outputLength = new Integer
	    (paramsElem.getFirstChild().getNodeValue()).intValue();
        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "unmarshalled outputLength: " + outputLength);
	}
	return new HMACParameterSpec(outputLength);
    }

    protected void marshalParams(Element parent, String prefix)
	throws MarshalException {

	Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        Element hmacElem = DOMUtils.createElement(ownerDoc, "HMACOutputLength", 
	    XMLSignature.XMLNS, prefix);
        hmacElem.appendChild(ownerDoc.createTextNode
	   (String.valueOf(outputLength)));

        parent.appendChild(hmacElem);
    }

    public boolean verify(Key key, DOMSignedInfo si, byte[] sig,
	XMLValidateContext context) 
	throws InvalidKeyException, SignatureException, XMLSignatureException {
        if (key == null || si == null || sig == null) {
            throw new NullPointerException
		("key, signedinfo or signature data can't be null");
        }
        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "outputLength = " + outputLength);
	}
        hmac.init(key, outputLength);
	si.canonicalize(context, new MacOutputStream(hmac));
        return hmac.verify(sig);
    }

    public byte[] sign(Key key, DOMSignedInfo si, XMLSignContext context) 
	throws InvalidKeyException, XMLSignatureException {
        if (key == null || si == null) {
            throw new NullPointerException();
        }
        hmac.init(key, outputLength);
	si.canonicalize(context, new MacOutputStream(hmac));

        try {
            return hmac.sign();
        } catch (SignatureException se) {
            // should never occur!
            throw new RuntimeException(se.getMessage());
        }
    }

    public boolean paramsEqual(AlgorithmParameterSpec spec) {
	if (getParameterSpec() == spec) {
	    return true;
	}
        if (!(spec instanceof HMACParameterSpec)) {
	    return false;
	}
	HMACParameterSpec ospec = (HMACParameterSpec) spec;

	return (outputLength == ospec.getOutputLength());
    }
}
