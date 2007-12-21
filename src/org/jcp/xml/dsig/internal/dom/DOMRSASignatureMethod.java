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
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;

import org.jcp.xml.dsig.internal.SignerOutputStream;

/**
 * DOM-based implementation of SignatureMethod for RSA algorithm.
 * Use DOMHMACSignatureMethod for HMAC algorithms.
 *
 * @author Sean Mullan
 */
public final class DOMRSASignatureMethod extends DOMSignatureMethod { 

    private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
    private Signature signature;

    /**
     * Creates a <code>DOMRSASignatureMethod</code> for the specified 
     * input parameters.
     *
     * @param params algorithm-specific parameters (may be null)
     * @throws InvalidAlgorithmParameterException if the parameters are not
     *    appropriate for this signature method
     */
    public DOMRSASignatureMethod(AlgorithmParameterSpec params) 
	throws InvalidAlgorithmParameterException {
	super(SignatureMethod.RSA_SHA1, params);
    }

    /**
     * Creates a <code>DOMRSASignatureMethod</code> from an element.
     *
     * @param smElem a SignatureMethod element
     */
    public DOMRSASignatureMethod(Element smElem) throws MarshalException {
	super(smElem);
    }

    protected void checkParams(SignatureMethodParameterSpec params) 
	throws InvalidAlgorithmParameterException {
        if (params != null) {
            throw new InvalidAlgorithmParameterException("no parameters " +
                "should be specified for RSA signature algorithm");
        }
    }

    protected SignatureMethodParameterSpec unmarshalParams(Element paramsElem)
        throws MarshalException {
        throw new MarshalException("no parameters should " +
            "be specified for RSA signature algorithm");
    }

    protected void marshalParams(Element parent, String dsPrefix)
        throws MarshalException {
        // should never get invoked
        throw new MarshalException("no parameters should " +
            "be specified for RSA signature algorithm");
    }

    protected boolean paramsEqual(AlgorithmParameterSpec spec) {
	// params should always be null
	return (getParameterSpec() == spec);
    }

    public boolean verify(Key key, DOMSignedInfo si, byte[] sig,
	XMLValidateContext context) 
	throws InvalidKeyException, SignatureException, XMLSignatureException {
    	if (key == null || si == null || sig == null) {
    	    throw new NullPointerException
		("key, signed info or signature cannot be null");
    	}

        if (!(key instanceof PublicKey)) {
	    throw new InvalidKeyException("key must be PublicKey");
        }
	if (signature == null) {
	    try {
                // FIXME: do other hashes besides sha-1
                Provider p = (Provider) context.getProperty
                    ("org.jcp.xml.dsig.internal.dom.SignatureProvider");
                signature = (p == null) ? Signature.getInstance("SHA1withRSA")
		    : Signature.getInstance("SHA1withRSA", p);
	    } catch (NoSuchAlgorithmException nsae) {
		throw new SignatureException("SHA1withRSA Signature not found");
	    }
	}
        signature.initVerify((PublicKey) key);
	if (log.isLoggable(Level.FINE)) {
	    log.log(Level.FINE, "Signature provider:"+ signature.getProvider());
            log.log(Level.FINE, "verifying with key: " + key);
	}
	si.canonicalize(context, new SignerOutputStream(signature));

	return signature.verify(sig);
    }

    public byte[] sign(Key key, DOMSignedInfo si, XMLSignContext context) 
	throws InvalidKeyException, XMLSignatureException {
    	if (key == null || si == null) {
    	    throw new NullPointerException();
    	}

        if (!(key instanceof PrivateKey)) {
            throw new InvalidKeyException("key must be PrivateKey");
        }
	if (signature == null) {
	    try {
                // FIXME: do other hashes besides sha-1
                Provider p = (Provider) context.getProperty
                    ("org.jcp.xml.dsig.internal.dom.SignatureProvider");
                signature = (p == null) ? Signature.getInstance("SHA1withRSA")
		    : Signature.getInstance("SHA1withRSA", p);
	    } catch (NoSuchAlgorithmException nsae) {
		throw new InvalidKeyException("SHA1withRSA Signature not found");
	    }
	}
        signature.initSign((PrivateKey) key);
	if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Signature provider:" +signature.getProvider());
            log.log(Level.FINE, "Signing with key: " + key);
        }

	si.canonicalize(context, new SignerOutputStream(signature));

        try {
	    return signature.sign();
        } catch (SignatureException se) {
	    // should never occur!
	    throw new RuntimeException(se.getMessage());
        }
    }
}
