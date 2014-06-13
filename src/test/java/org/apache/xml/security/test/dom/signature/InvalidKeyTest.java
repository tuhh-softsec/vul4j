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
package org.apache.xml.security.test.dom.signature;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PublicKey;

import org.apache.xml.security.Init;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Test case contributed by Matthias Germann for testing that bug 43239 is
 * fixed: "No installed provider supports this key" when checking a RSA 
 * signature against a DSA key before RSA key.
 */
public class InvalidKeyTest extends org.junit.Assert {

    private static final String BASEDIR = 
        System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
    private static final String SEP = System.getProperty("file.separator");
    
    static {
        Init.init();
    }

    @org.junit.Test
    public void test() throws Exception {
        FileInputStream input = new FileInputStream(BASEDIR + SEP + 
            "src/test/resources/org/apache/xml/security/samples/input/truststore.jks");
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
    
    private void validate(PublicKey pk) throws Exception {
        FileInputStream is = new FileInputStream(BASEDIR + SEP +
            "src/test/resources/org/apache/xml/security/samples/input/test-assertion.xml");
            
        Document e = XMLUtils.createDocumentBuilder(false).parse(is);
            
        Node assertion = e.getFirstChild();
        while (!(assertion instanceof Element)) {
            assertion = assertion.getNextSibling();
        }
        Attr attr = ((Element)assertion).getAttributeNodeNS(null, "AssertionID");
        if (attr != null) {
            ((Element)assertion).setIdAttributeNode(attr, true);
        }
        
        Element n = (Element)assertion.getLastChild();
            
        XMLSignature si = new XMLSignature(n, "");
        si.checkSignatureValue(pk);

        // System.out.println("VALIDATION OK" );
    }
    
}
