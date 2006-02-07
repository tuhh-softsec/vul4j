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
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;

import java.io.InputStream;
import java.io.IOException;
import java.security.DigestException;
import java.security.DigestInputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;

/**
 * DOM-based implementation of DigestMethod for the SHA1 algorithm.
 *
 * @author Sean Mullan
 */
public final class DOMSHA1DigestMethod extends DOMDigestMethod {

    private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
    private MessageDigest md;

    /**
     * Creates a <code>DOMSHA1DigestMethod</code>.
     */
    public DOMSHA1DigestMethod(AlgorithmParameterSpec params) 
	throws InvalidAlgorithmParameterException {
	super(DigestMethod.SHA1, params);
    }

    /**
     * Creates a <code>DOMSHA1DigestMethod</code> from an element.
     *
     * @param dmElem a DigestMethod element
     */
    public DOMSHA1DigestMethod(Element dmElem) throws MarshalException {
	super(dmElem);
    }

    protected void checkParams(DigestMethodParameterSpec params) 
	throws InvalidAlgorithmParameterException {
	if (params != null) {
	    throw new InvalidAlgorithmParameterException("no parameters " +
		"should be specified for SHA1 message digest algorithm");
	}
    }

    protected DigestMethodParameterSpec unmarshalParams(Element paramsElem)
	throws MarshalException {
	throw new MarshalException("no parameters should " +
	    "be specified for SHA1 message digest algorithm");
    }

    protected void marshalParams(Element parent, String dsPrefix)
	throws MarshalException {
	// should never get invoked
	throw new MarshalException("no parameters should " +
	    "be specified for SHA1 message digest algorithm");
    }

    public byte[] digest(InputStream is) throws IOException, DigestException {
	if (is == null) {
	    throw new NullPointerException("pre-digested input stream is null");
	}
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException nsae) {
	    throw new DigestException("SHA1 MessageDigest not available");
        }
        DigestInputStream dis = new DigestInputStream(is, md);
	if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Predigested reference:\n");
            byte[] buf = new byte[1024];
	    int bytesRead = 0;
            while (true) {
	        int read = dis.read(buf);
	        if (read == -1) { // EOF
	            break;
	        }
	        bytesRead = bytesRead + read;
	        log.log(Level.FINE, new String(buf));
	    }
	    log.log(Level.FINE, "bytesRead=" + bytesRead);
	}
	return md.digest();
    }
}
