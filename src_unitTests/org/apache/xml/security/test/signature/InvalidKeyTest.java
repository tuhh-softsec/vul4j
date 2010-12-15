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
package org.apache.xml.security.test.signature;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PublicKey;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.Init;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test case contributed by Matthias Germann for testing that bug 43239 is
 * fixed: "No installed provider supports this key" when checking a RSA 
 * signature against a DSA key before RSA key.
 */
public class InvalidKeyTest extends TestCase {

    static {
	Init.init();
    }

    public static Test suite() {
       return new TestSuite(InvalidKeyTest.class);
    }

    private static final String BASEDIR = 
        System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
    private static final String SEP = System.getProperty("file.separator");

    public void test() throws Exception {
	FileInputStream input = new FileInputStream(BASEDIR + SEP + 
	    "data/org/apache/xml/security/samples/input/truststore.jks");
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(input, "testpw".toCharArray());
        
	try {
            validate(trustStore.getCertificate("bedag-test").getPublicKey());
            throw new Exception("Failure expected on a DSA key");
	} catch (Exception e) {
	    // e.printStackTrace();
	}
        validate(trustStore.getCertificate("a70-garaio-frontend-u").getPublicKey());
    }
    
    public void validate(PublicKey pk) throws Exception {
        FileInputStream is = new FileInputStream(BASEDIR + SEP +
	    "data/org/apache/xml/security/samples/input/test-assertion.xml");
            
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document e = dbf.newDocumentBuilder().parse(is);
            
        NodeList nl = e.getFirstChild().getChildNodes();
        Element n = (Element)nl.item(nl.getLength()-1);
            
        XMLSignature si = new XMLSignature((Element)n,"");
        si.checkSignatureValue(pk);

       	// System.out.println("VALIDATION OK" );
    }
}
