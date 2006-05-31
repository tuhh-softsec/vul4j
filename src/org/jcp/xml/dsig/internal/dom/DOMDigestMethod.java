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
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;

import java.io.InputStream;
import java.io.IOException;
import java.security.DigestException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOM-based abstract implementation of DigestMethod.
 *
 * @author Sean Mullan
 */
public abstract class DOMDigestMethod extends DOMStructure 
    implements DigestMethod {

    private String algorithm;
    private DigestMethodParameterSpec params;

    /**
     * Creates a <code>DOMDigestMethod</code>.
     *
     * @param algorithm the URI identifying the digest algorithm
     * @param params the algorithm-specific params (may be <code>null</code>)
     * @throws InvalidAlgorithmParameterException if the parameters are not
     *    appropriate for this digest method
     */
    protected DOMDigestMethod(String algorithm, AlgorithmParameterSpec params)
	throws InvalidAlgorithmParameterException {
	if (algorithm == null) {
	    throw new NullPointerException("algorithm cannot be null");
	}
	if (params != null && !(params instanceof DigestMethodParameterSpec)) {
	    throw new InvalidAlgorithmParameterException
		("params must be of type DigestMethodParameterSpec");
	}
	checkParams((DigestMethodParameterSpec) params);
	this.algorithm = algorithm;
	this.params = (DigestMethodParameterSpec) params;
    }

    /**
     * Creates a <code>DOMDigestMethod</code> from an element. This constructor
     * invokes the abstract {@link #unmarshalParams unmarshalParams} method to
     * unmarshal any algorithm-specific input parameters.
     *
     * @param dmElem a DigestMethod element
     */
    protected DOMDigestMethod(Element dmElem) throws MarshalException {
        algorithm = DOMUtils.getAttributeValue(dmElem, "Algorithm");
	Element paramsElem = DOMUtils.getFirstChildElement(dmElem);
	if (paramsElem != null) {
	    params = unmarshalParams(paramsElem);
	}
	try {
	    checkParams(params);
	} catch (InvalidAlgorithmParameterException iape) {
	    throw new MarshalException(iape);
	}
    }

    static DigestMethod unmarshal(Element dmElem) throws MarshalException {
        String alg = DOMUtils.getAttributeValue(dmElem, "Algorithm");
        if (alg.equals(DigestMethod.SHA1)) {
            return new DOMSHA1DigestMethod(dmElem);
        } else {
            throw new MarshalException("unsupported digest algorithm: " + alg);
        }
    }

    /**
     * Checks if the specified parameters are valid for this algorithm.
     *
     * @param params the algorithm-specific params (may be <code>null</code>)
     * @throws InvalidAlgorithmParameterException if the parameters are not
     *    appropriate for this digest method
     */
    protected abstract void checkParams(DigestMethodParameterSpec params) 
	throws InvalidAlgorithmParameterException;

    public final AlgorithmParameterSpec getParameterSpec() {
	return params;
    }

    public final String getAlgorithm() {
	return algorithm;
    }

    /**
     * Unmarshals <code>DigestMethodParameterSpec</code> from the specified 
     * <code>Element</code>. Subclasses should implement this to unmarshal
     * the algorithm-specific parameters.
     *
     * @param paramsElem the <code>Element</code> holding the input params
     * @return the algorithm-specific <code>DigestMethodParameterSpec</code>
     * @throws MarshalException if the parameters cannot be unmarshalled
     */
    protected abstract DigestMethodParameterSpec 
	unmarshalParams(Element paramsElem) throws MarshalException;

    /**
     * This method invokes the abstract {@link #marshalParams marshalParams} 
     * method to marshal any algorithm-specific parameters.
     */
    public void marshal(Node parent, String prefix, DOMCryptoContext context) 
	throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);

        Element dmElem = DOMUtils.createElement
	    (ownerDoc, "DigestMethod", XMLSignature.XMLNS, prefix);
        DOMUtils.setAttribute(dmElem, "Algorithm", algorithm);

        if (params != null) {
	    marshalParams(dmElem, prefix);
        }

        parent.appendChild(dmElem);
    }

    /**
     * Digests the specified data using the underlying message digest algorithm.
     *
     * @param is the input stream containing the data to be digested
     * @return the resulting hash value
     * @throws IOException if an I/O error occurs while reading from the stream
     * @throws DigestException if an unexpected error occurs while digesting
     * @throws NullPointerException if <code>is</code> is <code>null</code>
     */
    public abstract byte[] digest(InputStream is) 
	throws IOException, DigestException;

    public boolean equals(Object o) {
	if (this == o) {
            return true;
	}

        if (!(o instanceof DigestMethod)) {
            return false;
	}
        DigestMethod odm = (DigestMethod) o;

	boolean paramsEqual = (params == null ? odm.getParameterSpec() == null :
	    params.equals(odm.getParameterSpec()));

	return (algorithm.equals(odm.getAlgorithm()) && paramsEqual);
    }

    public int hashCode() {
	// uncomment when JDK 1.4 is required
	// assert false : "hashCode not designed";
	return 51;
    }

    /**
     * Marshals the algorithm-specific parameters to an Element and
     * appends it to the specified parent element.
     *
     * @param parent the parent element to append the parameters to
     * @param the namespace prefix to use
     * @throws MarshalException if the parameters cannot be marshalled
     */
    protected abstract void marshalParams(Element parent, String prefix)
	throws MarshalException;
}
