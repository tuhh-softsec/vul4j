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
package org.apache.xml.security.test.dom.keys.keyresolver;

import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;

import javax.xml.parsers.DocumentBuilder;

import org.apache.xml.security.Init;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class KeyInfoReferenceResolverTest extends Assert {

    private static final String BASEDIR = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
    private static final String SEP = System.getProperty("file.separator");

    private DocumentBuilder documentBuilder;

    public KeyInfoReferenceResolverTest() throws Exception {
        documentBuilder = XMLUtils.createDocumentBuilder(false);

        if (!Init.isInitialized()) {
            Init.init();
        }
    }

    @org.junit.Test
    public void testRSAPublicKey() throws Exception {
        PublicKey rsaKeyControl = loadPublicKey("rsa-KeyInfoReference.key", "RSA");

        Document doc = loadXML("KeyInfoReference-RSA.xml");
        markKeyInfoIdAttrs(doc);

        Element referenceElement = doc.getElementById("theReference");
        assertNotNull(referenceElement);

        KeyInfo keyInfo = new KeyInfo(referenceElement, "");
        assertEquals(rsaKeyControl, keyInfo.getPublicKey());
    }

    @org.junit.Test
    public void testX509Certificate() throws Exception {
        X509Certificate certControl = loadCertificate("cert-KeyInfoReference.crt");

        Document doc = loadXML("KeyInfoReference-X509Certificate.xml");
        markKeyInfoIdAttrs(doc);

        Element referenceElement = doc.getElementById("theReference");
        assertNotNull(referenceElement);

        KeyInfo keyInfo = new KeyInfo(referenceElement, "");
        assertEquals(certControl, keyInfo.getX509Certificate());
        assertEquals(certControl.getPublicKey(), keyInfo.getPublicKey());
    }

    @org.junit.Test
    public void testWrongReferentType() throws Exception {
        Document doc = loadXML("KeyInfoReference-WrongReferentType.xml");
        markKeyInfoIdAttrs(doc);

        // Mark the ID-ness of the bogus element so can be resolved
        NodeList nl = doc.getElementsByTagNameNS("http://www.example.org/test", "KeyInfo");
        for (int i = 0; i < nl.getLength(); i++) {
            Element keyInfoElement = (Element) nl.item(i);
            keyInfoElement.setIdAttributeNS(null, Constants._ATT_ID, true);
        }

        Element referenceElement = doc.getElementById("theReference");
        assertNotNull(referenceElement);

        KeyInfo keyInfo = new KeyInfo(referenceElement, "");
        assertNull(keyInfo.getPublicKey());
    }

    @org.junit.Test
    public void testSameDocumentReferenceChain() throws Exception {
        Document doc = loadXML("KeyInfoReference-ReferenceChain.xml");
        markKeyInfoIdAttrs(doc);

        Element referenceElement = doc.getElementById("theReference");
        assertNotNull(referenceElement);

        KeyInfo keyInfo = new KeyInfo(referenceElement, "");
        // Chains of references are not supported at this time
        assertNull(keyInfo.getPublicKey());
    }

    @org.junit.Test
    public void testSameDocumentReferenceChainWithSecureValidation() throws Exception {
        Document doc = loadXML("KeyInfoReference-ReferenceChain.xml");
        markKeyInfoIdAttrs(doc);

        Element referenceElement = doc.getElementById("theReference");
        assertNotNull(referenceElement);

        KeyInfo keyInfo = new KeyInfo(referenceElement, "");
        keyInfo.setSecureValidation(true);
        // Chains of references are not supported at this time
        assertNull(keyInfo.getPublicKey());
    }

    // Utility methods

    private String getControlFilePath(String fileName) {
        return BASEDIR + SEP + "src" + SEP + "test" + SEP + "resources" + 
            SEP + "org" + SEP + "apache" + SEP + "xml" + SEP + "security" + 
            SEP + "keyresolver" +
            SEP + fileName;
    }

    private Document loadXML(String fileName) throws Exception {
        return documentBuilder.parse(new FileInputStream(getControlFilePath(fileName)));
    }

    private PublicKey loadPublicKey(String filePath, String algorithm) throws Exception {
        String fileData = new String(JavaUtils.getBytesFromFile(getControlFilePath(filePath)));
        byte[] keyBytes = Base64.decode(fileData);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        return kf.generatePublic(keySpec);
    }

    private X509Certificate loadCertificate(String fileName) throws Exception {
        FileInputStream fis = new FileInputStream(getControlFilePath(fileName));
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certFactory.generateCertificate(fis);
    }

    private void markKeyInfoIdAttrs(Document doc) {
        NodeList nl = doc.getElementsByTagNameNS(Constants.SignatureSpecNS, Constants._TAG_KEYINFO);
        for (int i = 0; i < nl.getLength(); i++) {
            Element keyInfoElement = (Element) nl.item(i);
            keyInfoElement.setIdAttributeNS(null, Constants._ATT_ID, true);
        }
    }

}
