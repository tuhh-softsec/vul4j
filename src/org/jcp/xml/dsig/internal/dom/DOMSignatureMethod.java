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
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;

import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOM-based abstract implementation of SignatureMethod.
 *
 * @author Sean Mullan
 */
public abstract class DOMSignatureMethod extends DOMStructure 
    implements SignatureMethod {

    private String algorithm;
    private SignatureMethodParameterSpec params;

    /**
     * Creates a <code>DOMSignatureMethod</code>.
     *
     * @param algorithm the URI identifying the signature algorithm
     * @param params the algorithm-specific params (may be <code>null</code>)
     * @throws NullPointerException if <code>algorithm</code> is
     *    <code>null</code>
     * @throws InvalidAlgorithmParameterException if the parameters are not
     *    appropriate for this signature method
     */
    protected DOMSignatureMethod(String algorithm, 
	AlgorithmParameterSpec params) 
	throws InvalidAlgorithmParameterException {
	if (algorithm == null) {
	    throw new NullPointerException("algorithm cannot be null");
	}
	if (params != null && 
	    !(params instanceof SignatureMethodParameterSpec)) {
	    throw new InvalidAlgorithmParameterException
		("params must be of type SignatureMethodParameterSpec");
	}
        checkParams((SignatureMethodParameterSpec) params);
	this.algorithm = algorithm;
	this.params = (SignatureMethodParameterSpec) params;
    }

    /**
     * Creates a <code>DOMSignatureMethod</code> from an element. This ctor
     * invokes the abstract {@link #unmarshalParams unmarshalParams} method to
     * unmarshal any algorithm-specific input parameters.
     *
     * @param smElem a SignatureMethod element
     */
    protected DOMSignatureMethod(Element smElem) throws MarshalException {
        algorithm = DOMUtils.getAttributeValue(smElem, "Algorithm");
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
        if (alg.equals(SignatureMethod.HMAC_SHA1)) {
            return new DOMHMACSignatureMethod(smElem);
        } else if (alg.equals(SignatureMethod.RSA_SHA1)) {
            return new DOMRSASignatureMethod(smElem);
        } else if (alg.equals(SignatureMethod.DSA_SHA1)) {
            return new DOMDSASignatureMethod(smElem);
        } else {
            throw new MarshalException("unsupported signature algorithm: " 
		+ alg);
        }
    }

    /**
     * Checks if the specified parameters are valid for this algorithm.
     *
     * @param params the algorithm-specific params (may be <code>null</code>)
     * @throws InvalidAlgorithmParameterException if the parameters are not
     *    appropriate for this signature method
     */
    protected abstract void checkParams(SignatureMethodParameterSpec params) 
	throws InvalidAlgorithmParameterException;

    public final AlgorithmParameterSpec getParameterSpec() {
	return params;
    }

    public final String getAlgorithm() {
	return algorithm;
    }

    /**
     * Unmarshals <code>SignatureMethodParameterSpec</code> from the specified 
     * <code>Element</code>. Subclasses should implement this to unmarshal
     * the algorithm-specific parameters.
     *
     * @param paramsElem the <code>Element</code> holding the input params
     * @return the algorithm-specific <code>SignatureMethodParameterSpec</code>
     * @throws MarshalException if the parameters cannot be unmarshalled
     */
    protected abstract SignatureMethodParameterSpec 
	unmarshalParams(Element paramsElem) throws MarshalException;

    /**
     * This method invokes the abstract {@link #marshalParams marshalParams} 
     * method to marshal any algorithm-specific parameters.
     */
    public void marshal(Node parent, String dsPrefix, DOMCryptoContext context)
	throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);

        Element smElem = DOMUtils.createElement
	    (ownerDoc, "SignatureMethod", XMLSignature.XMLNS, dsPrefix);
        DOMUtils.setAttribute(smElem, "Algorithm", algorithm);

        if (params != null) {
	    marshalParams(smElem, dsPrefix);
        }

        parent.appendChild(smElem);
    }

    /**
     * Verifies the passed-in signature with the specified key, using the
     * underlying signature or MAC algorithm.
     *
     * @param key the verification key
     * @param si the DOMSignedInfo
     * @param signature the signature bytes to be verified
     * @param context the XMLValidateContext
     * @return <code>true</code> if the signature verified successfully,
     *    <code>false</code> if not
     * @throws NullPointerException if <code>key</code>, <code>si</code> or
     *    <code>signature</code> are <code>null</code>
     * @throws InvalidKeyException if the key is improperly encoded, of
     *    the wrong type, or parameters are missing, etc
     * @throws SignatureException if an unexpected error occurs, such
     *    as the passed in signature is improperly encoded
     * @throws XMLSignatureException if an unexpected error occurs
     */
    public abstract boolean verify(Key key, DOMSignedInfo si, byte[] signature,
	XMLValidateContext context) throws InvalidKeyException, SignatureException,
	XMLSignatureException;

    /**
     * Signs the bytes with the specified key, using the underlying
     * signature or MAC algorithm.
     *
     * @param key the signing key
     * @param si the DOMSignedInfo
     * @param context the XMLSignContext
     * @return the signature
     * @throws NullPointerException if <code>key</code> or
     *    <code>si</code> are <code>null</code>
     * @throws InvalidKeyException if the key is improperly encoded, of
     *    the wrong type, or parameters are missing, etc
     * @throws XMLSignatureException if an unexpected error occurs
     */
    public abstract byte[] sign(Key key, DOMSignedInfo si, XMLSignContext context) 
        throws InvalidKeyException, XMLSignatureException;

    /**
     * Marshals the algorithm-specific parameters to an Element and
     * appends it to the specified parent element.
     *
     * @param parent the parent element to append the parameters to
     * @param paramsPrefix the algorithm parameters prefix to use
     * @throws MarshalException if the parameters cannot be marshalled
     */
    protected abstract void marshalParams(Element parent, String paramsPrefix)
	throws MarshalException;

    /**
     * Returns true if parameters are equal; false otherwise.
     *
     * Subclasses should override this method to compare algorithm-specific
     * parameters.
     */
    protected abstract boolean paramsEqual(AlgorithmParameterSpec spec);

    public boolean equals(Object o) {
	if (this == o) {
            return true;
	}

        if (!(o instanceof SignatureMethod)) {
            return false;
	}
        SignatureMethod osm = (SignatureMethod) o;

	return (algorithm.equals(osm.getAlgorithm()) && 
	    paramsEqual(osm.getParameterSpec()));
    }

    public int hashCode() {
	// uncomment when JDK 1.4 is required
	// assert false : "hashCode not designed";
	return 57;
    }
}
