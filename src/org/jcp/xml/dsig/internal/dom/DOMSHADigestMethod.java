/*
 * Copyright 2006 The Apache Software Foundation.
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
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */
/*
 * $Id$
 */
package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.*;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;

/**
 * DOM-based implementation of DigestMethod for the SHA1, SHA256 and
 * SHA512 algorithms.
 *
 * @author Sean Mullan
 */
public abstract class DOMSHADigestMethod extends DOMDigestMethod {

    private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");

    /**
     * Creates a <code>DOMSHADigestMethod</code>.
     */
    protected DOMSHADigestMethod(AlgorithmParameterSpec params) 
	throws InvalidAlgorithmParameterException {
	super(params);
    }

    /**
     * Creates a <code>DOMSHADigestMethod</code> from an element.
     *
     * @param dmElem a DigestMethod element
     */
    protected DOMSHADigestMethod(Element dmElem) throws MarshalException {
	super(dmElem);
    }

    protected void checkParams(DigestMethodParameterSpec params) 
	throws InvalidAlgorithmParameterException {
	if (params != null) {
	    throw new InvalidAlgorithmParameterException("no parameters " +
		"should be specified for the " + getName() 
		 + " DigestMethod algorithm");
	}
    }

    protected DigestMethodParameterSpec unmarshalParams(Element paramsElem)
	throws MarshalException {
	throw new MarshalException("no parameters should " +
	    "be specified for the " + getName() + " DigestMethod algorithm");
    }

    protected void marshalParams(Element parent, String dsPrefix)
	throws MarshalException {
	// should never get invoked
	throw new MarshalException("no parameters should " +
	    "be specified for the " + getName() + " DigestMethod algorithm");
    }

    /**
     * Returns the name of the DigestMethod algorithm.
     */
    abstract String getName();

    /**
     * Returns a SHA1 DigestMethod.
     */
    static final DOMSHADigestMethod SHA1(AlgorithmParameterSpec params) 
	throws InvalidAlgorithmParameterException {
	return new DOMSHA1DigestMethod(params);
    }
    static final DOMSHADigestMethod SHA1(Element dmElem) 
	throws MarshalException { 
	return new DOMSHA1DigestMethod(dmElem);
    }

    /**
     * Returns a SHA256 DigestMethod.
     */
    static final DOMSHADigestMethod SHA256(AlgorithmParameterSpec params) 
	throws InvalidAlgorithmParameterException {
	return new DOMSHA256DigestMethod(params);
    }
    static final DOMSHADigestMethod SHA256(Element dmElem) 
	throws MarshalException { 
	return new DOMSHA256DigestMethod(dmElem);
    }

    /**
     * Returns a SHA512 DigestMethod.
     */
    static final DOMSHADigestMethod SHA512(AlgorithmParameterSpec params) 
	throws InvalidAlgorithmParameterException { 
	return new DOMSHA512DigestMethod(params);
    }
    static final DOMSHADigestMethod SHA512(Element dmElem) 
	throws MarshalException { 
	return new DOMSHA512DigestMethod(dmElem);
    }

    private static final class DOMSHA1DigestMethod extends DOMSHADigestMethod {
	DOMSHA1DigestMethod(AlgorithmParameterSpec params) 
	    throws InvalidAlgorithmParameterException {
	    super(params);
	}
	DOMSHA1DigestMethod(Element dmElem) throws MarshalException {
	    super(dmElem);
	}
        public String getAlgorithm() {
	    return DigestMethod.SHA1;
        }
        String getMessageDigestAlgorithm() {
            return "SHA";
        }
        String getName() {
            return "SHA1";
        }
    }

    private static final class DOMSHA256DigestMethod extends DOMSHADigestMethod{
	DOMSHA256DigestMethod(AlgorithmParameterSpec params) 
	    throws InvalidAlgorithmParameterException {
	    super(params);
	}
	DOMSHA256DigestMethod(Element dmElem) throws MarshalException {
	    super(dmElem);
	}
        public String getAlgorithm() {
	    return DigestMethod.SHA256;
        }
        String getMessageDigestAlgorithm() {
            return "SHA-256";
        }
        String getName() {
            return "SHA256";
        }
    }

    private static final class DOMSHA512DigestMethod extends DOMSHADigestMethod{
	DOMSHA512DigestMethod(AlgorithmParameterSpec params) 
	    throws InvalidAlgorithmParameterException {
	    super(params);
	}
	DOMSHA512DigestMethod(Element dmElem) throws MarshalException {
	    super(dmElem);
	}
        public String getAlgorithm() {
	    return DigestMethod.SHA512;
        }
        String getMessageDigestAlgorithm() {
            return "SHA-512";
        }
        String getName() {
            return "SHA512";
        }
    }
}
