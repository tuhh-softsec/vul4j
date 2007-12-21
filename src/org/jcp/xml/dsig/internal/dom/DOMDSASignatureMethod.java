/*
 * Copyright 1999-2005 The Apache Software Foundation.
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
 * DOM-based implementation of SignatureMethod for DSA algorithm.
 * Use DOMHMACSignatureMethod for HMAC algorithms.
 *
 * @author Sean Mullan
 */
public final class DOMDSASignatureMethod extends DOMSignatureMethod { 

    private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
    private Signature signature;

    /**
     * Creates a <code>DOMDSASignatureMethod</code> with the specified 
     * input parameters.
     *
     * @param params algorithm-specific parameters (may be null)
     * @throws InvalidAlgorithmParameterException if the parameters are not
     *    appropriate for this signature method
     */
    public DOMDSASignatureMethod(AlgorithmParameterSpec params) 
	throws InvalidAlgorithmParameterException {
	super(SignatureMethod.DSA_SHA1, params);
    }

    /**
     * Creates a <code>DOMDSASignatureMethod</code> from an element.
     *
     * @param smElem a SignatureMethod element
     */
    public DOMDSASignatureMethod(Element smElem) throws MarshalException {
	super(smElem);
    }

    protected void checkParams(SignatureMethodParameterSpec params) 
	throws InvalidAlgorithmParameterException {
        if (params != null) {
            throw new InvalidAlgorithmParameterException("no parameters " +
                "should be specified for DSA signature algorithm");
        }
    }

    protected SignatureMethodParameterSpec unmarshalParams(Element paramsElem)
        throws MarshalException {
        throw new MarshalException("no parameters should " +
            "be specified for DSA signature algorithm");
    }

    protected void marshalParams(Element parent, String prefix)
        throws MarshalException {
        // should never get invoked
        throw new MarshalException("no parameters should " +
            "be specified for DSA signature algorithm");
    }

    protected boolean paramsEqual(AlgorithmParameterSpec spec) {
	// params should always be null
	return (getParameterSpec() == spec);
    }

    public boolean verify(Key key, DOMSignedInfo si, byte[] sig,
	XMLValidateContext context) 
	throws InvalidKeyException, SignatureException, XMLSignatureException {
	if (key == null) {
	    throw new NullPointerException("key cannot be null");
	} else if (sig == null) {
	    throw new NullPointerException("signature cannot be null");
	} else if (si == null) {
	    throw new NullPointerException("signedInfo cannot be null");
	}
	if (signature == null) {
	    try {
		Provider p = (Provider) context.getProperty
		    ("org.jcp.xml.dsig.internal.dom.SignatureProvider");
                signature = (p == null) ? Signature.getInstance("SHA1withDSA") 
		    : Signature.getInstance("SHA1withDSA", p);
	    } catch (NoSuchAlgorithmException nsae) {
		throw new SignatureException("SHA1withDSA Signature not found");
	    }
	}
        try {
            if (!(key instanceof PublicKey)) {
	        throw new InvalidKeyException("key must be PublicKey");
            }
            signature.initVerify((PublicKey) key);
	    si.canonicalize(context, new SignerOutputStream(signature));

	    // avoid overhead of converting key to String unless necessary
            if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE, "verifying with key: " + key);
	    }
            return signature.verify(convertXMLDSIGtoASN1(sig));  
        } catch (IOException ioex) {
	    // should never occur!
	    throw new RuntimeException(ioex.getMessage());
	}
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
		Provider p = (Provider) context.getProperty
		    ("org.jcp.xml.dsig.internal.dom.SignatureProvider");
                signature = (p == null) ? Signature.getInstance("SHA1withDSA") 
		    : Signature.getInstance("SHA1withDSA", p);
	    } catch (NoSuchAlgorithmException nsae) {
		throw new InvalidKeyException("SHA1withDSA Signature not found");
	    }
	}

        // avoid overhead of converting key to String unless necessary
        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Signing with key: " + key);
	}
        signature.initSign((PrivateKey) key);
	si.canonicalize(context, new SignerOutputStream(signature));

        try {
	    return convertASN1toXMLDSIG(signature.sign());
        } catch (SignatureException se) {
	    // should never occur!
	    throw new RuntimeException(se.getMessage());
	} catch (IOException ioex) {
	    // should never occur!
	    throw new RuntimeException(ioex.getMessage());
	}
    }
    
    /**
     * Converts an ASN.1 DSA value to a XML Signature DSA Value.
     *
     * The JAVA JCE DSA Signature algorithm creates ASN.1 encoded (r,s) value
     * pairs; the XML Signature requires the core BigInteger values.
     *
     * @param asn1Bytes
     *
     * @throws IOException
     * @see <A HREF="http://www.w3.org/TR/xmldsig-core/#dsa-sha1">6.4.1 DSA</A>
     */
    private static byte[] convertASN1toXMLDSIG(byte asn1Bytes[])
        throws IOException {

        // THIS CODE IS COPIED FROM APACHE (see copyright at top of file)
        byte rLength = asn1Bytes[3];
        int i;

        for (i = rLength; (i > 0) && (asn1Bytes[(4 + rLength) - i] == 0); i--);

        byte sLength = asn1Bytes[5 + rLength];
        int j;

        for (j = sLength;
            (j > 0) && (asn1Bytes[(6 + rLength + sLength) - j] == 0); j--);

        if ((asn1Bytes[0] != 48) || (asn1Bytes[1] != asn1Bytes.length - 2)
            || (asn1Bytes[2] != 2) || (i > 20)
            || (asn1Bytes[4 + rLength] != 2) || (j > 20)) {
            throw new IOException("Invalid ASN.1 format of DSA signature");
        } else {
            byte xmldsigBytes[] = new byte[40];

            System.arraycopy(asn1Bytes, (4+rLength)-i, xmldsigBytes, 20-i, i);
            System.arraycopy(asn1Bytes, (6+rLength+sLength)-j, xmldsigBytes,
                          40 - j, j);

            return xmldsigBytes;
        }
    }

    /**
     * Converts a XML Signature DSA Value to an ASN.1 DSA value.
     *
     * The JAVA JCE DSA Signature algorithm creates ASN.1 encoded (r,s) value
     * pairs; the XML Signature requires the core BigInteger values.
     *
     * @param xmldsigBytes
     *
     * @throws IOException
     * @see <A HREF="http://www.w3.org/TR/xmldsig-core/#dsa-sha1">6.4.1 DSA</A>
     */
    private static byte[] convertXMLDSIGtoASN1(byte xmldsigBytes[])
        throws IOException {

        // THIS CODE IS COPIED FROM APACHE (see copyright at top of file)
        if (xmldsigBytes.length != 40) {
            throw new IOException("Invalid XMLDSIG format of DSA signature");
        }

        int i;

        for (i = 20; (i > 0) && (xmldsigBytes[20 - i] == 0); i--);

        int j = i;

        if (xmldsigBytes[20 - i] < 0) {
            j += 1;
        }

        int k;

        for (k = 20; (k > 0) && (xmldsigBytes[40 - k] == 0); k--);

        int l = k;

        if (xmldsigBytes[40 - k] < 0) {
            l += 1;
        }

        byte asn1Bytes[] = new byte[6 + j + l];

        asn1Bytes[0] = 48;
        asn1Bytes[1] = (byte) (4 + j + l);
        asn1Bytes[2] = 2;
        asn1Bytes[3] = (byte) j;

        System.arraycopy(xmldsigBytes, 20 - i, asn1Bytes, (4 + j) - i, i);

        asn1Bytes[4 + j] = 2;
        asn1Bytes[5 + j] = (byte) l;

        System.arraycopy(xmldsigBytes, 40 - k, asn1Bytes, (6 + j + l) - k, k);

        return asn1Bytes;
    }
}
