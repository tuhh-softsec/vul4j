/*
 * Copyright 2009-2010 The Apache Software Foundation.
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
package org.apache.xml.security.test.algorithms;

import java.security.KeyPairGenerator;
import java.security.PrivateKey;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Document;

public class SignatureAlgorithmTest extends TestCase {
    
    /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(SignatureAlgorithmTest.class.getName());

	static {
	    org.apache.xml.security.Init.init();
	}

	public void testSameKeySeveralAlgorithmSigning() throws Exception {
		Document doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		SignatureAlgorithm signatureAlgorithm = new SignatureAlgorithm(doc,XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
		PrivateKey pk=KeyPairGenerator.getInstance("RSA").genKeyPair().getPrivate();
		signatureAlgorithm.initSign(pk);
		signatureAlgorithm.update((byte)2);
		signatureAlgorithm.sign();
		SignatureAlgorithm otherSignatureAlgorithm =
             new SignatureAlgorithm(doc, XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256);

		try {
		    otherSignatureAlgorithm.initSign(pk);
		} catch (XMLSecurityException ex) {
            log.warn("Test testSameKeySeveralAlgorithmSigning skipped as necessary algorithms not available");
            return;
        }
		    
		otherSignatureAlgorithm.update((byte)2);
		otherSignatureAlgorithm.sign();
	}
	public static Test suite() {
		return new TestSuite(SignatureAlgorithmTest.class);
	}
}
