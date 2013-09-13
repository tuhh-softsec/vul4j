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
package org.apache.xml.security.test.dom.keys;

import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.security.KeyFactory;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.X509EncodedKeySpec;

import javax.xml.parsers.DocumentBuilder;

import org.apache.xml.security.keys.content.DEREncodedKeyValue;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DEREncodedKeyValueTest extends Assert {

    private static final String BASEDIR = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
    private static final String SEP = System.getProperty("file.separator");

    private DocumentBuilder documentBuilder;

    private PublicKey rsaKeyControl;
    private PublicKey dsaKeyControl;
    private PublicKey ecKeyControl;

    private final String idControl = "abc123";

    public DEREncodedKeyValueTest() throws Exception {
        documentBuilder = XMLUtils.createDocumentBuilder(false);

        //
        // If the BouncyCastle provider is not installed, then try to load it 
        // via reflection. 
        //
        if (Security.getProvider("BC") == null) {
            Constructor<?> cons = null;
            try {
                Class<?> c = Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
                cons = c.getConstructor(new Class[] {});
            } catch (Exception e) {
                //ignore
            }
            if (cons != null) {
                Provider provider = (java.security.Provider)cons.newInstance();
                Security.insertProviderAt(provider, 1);
                ecKeyControl = loadPublicKey("ec.key", "EC");
            }
        }

        rsaKeyControl = loadPublicKey("rsa.key", "RSA");
        dsaKeyControl = loadPublicKey("dsa.key", "DSA");
    }

    @org.junit.Test
    public void testSchema() throws Exception {
        DEREncodedKeyValue derEncodedKeyValue = new DEREncodedKeyValue(documentBuilder.newDocument(), rsaKeyControl);
        Element element = derEncodedKeyValue.getElement();

        assertEquals("http://www.w3.org/2009/xmldsig11#", element.getNamespaceURI());
        assertEquals("DEREncodedKeyValue", element.getLocalName());
    }

    @org.junit.Test
    public void testRSAPublicKeyFromElement() throws Exception {
        Document doc = loadXML("DEREncodedKeyValue-RSA.xml");
        NodeList nl = doc.getElementsByTagNameNS(Constants.SignatureSpec11NS, Constants._TAG_DERENCODEDKEYVALUE);
        Element element = (Element) nl.item(0);

        DEREncodedKeyValue derEncodedKeyValue = new DEREncodedKeyValue(element, "");
        assertEquals(rsaKeyControl, derEncodedKeyValue.getPublicKey());
        assertArrayEquals(rsaKeyControl.getEncoded(), derEncodedKeyValue.getBytesFromTextChild());
        assertEquals(idControl, derEncodedKeyValue.getId());
    }

    @org.junit.Test
    public void testDSAPublicKeyFromElement() throws Exception {
        Document doc = loadXML("DEREncodedKeyValue-DSA.xml");
        NodeList nl = doc.getElementsByTagNameNS(Constants.SignatureSpec11NS, Constants._TAG_DERENCODEDKEYVALUE);
        Element element = (Element) nl.item(0);

        DEREncodedKeyValue derEncodedKeyValue = new DEREncodedKeyValue(element, "");
        assertEquals(dsaKeyControl, derEncodedKeyValue.getPublicKey());
        assertArrayEquals(dsaKeyControl.getEncoded(), derEncodedKeyValue.getBytesFromTextChild());
        assertEquals(idControl, derEncodedKeyValue.getId());
    }

    @org.junit.Test
    public void testECPublicKeyFromElement() throws Exception {
        if (ecKeyControl == null) {
            return;
        }

        Document doc = loadXML("DEREncodedKeyValue-EC.xml");
        NodeList nl = doc.getElementsByTagNameNS(Constants.SignatureSpec11NS, Constants._TAG_DERENCODEDKEYVALUE);
        Element element = (Element) nl.item(0);

        DEREncodedKeyValue derEncodedKeyValue = new DEREncodedKeyValue(element, "");
        assertEquals(ecKeyControl, derEncodedKeyValue.getPublicKey());
        assertArrayEquals(ecKeyControl.getEncoded(), derEncodedKeyValue.getBytesFromTextChild());
        assertEquals(idControl, derEncodedKeyValue.getId());
    }

    @org.junit.Test
    public void testRSAPublicKeyFromKey() throws Exception {
        DEREncodedKeyValue derEncodedKeyValue = new DEREncodedKeyValue(documentBuilder.newDocument(), rsaKeyControl);
        assertEquals(rsaKeyControl, derEncodedKeyValue.getPublicKey());
        assertArrayEquals(rsaKeyControl.getEncoded(), derEncodedKeyValue.getBytesFromTextChild());
    }

    @org.junit.Test
    public void testDSAPublicKeyFromKey() throws Exception {
        DEREncodedKeyValue derEncodedKeyValue = new DEREncodedKeyValue(documentBuilder.newDocument(), dsaKeyControl);
        assertEquals(dsaKeyControl, derEncodedKeyValue.getPublicKey());
        assertArrayEquals(dsaKeyControl.getEncoded(), derEncodedKeyValue.getBytesFromTextChild());
    }

    @org.junit.Test
    public void testECPublicKeyFromKey() throws Exception {
        if (ecKeyControl == null) {
            return;
        }

        DEREncodedKeyValue derEncodedKeyValue = new DEREncodedKeyValue(documentBuilder.newDocument(), ecKeyControl);
        assertEquals(ecKeyControl, derEncodedKeyValue.getPublicKey());
        assertArrayEquals(ecKeyControl.getEncoded(), derEncodedKeyValue.getBytesFromTextChild());
    }

    @org.junit.Test
    public void testId() throws Exception {
        DEREncodedKeyValue derEncodedKeyValue = new DEREncodedKeyValue(documentBuilder.newDocument(), rsaKeyControl);
        assertEquals("", derEncodedKeyValue.getId());
        assertNull(derEncodedKeyValue.getElement().getAttributeNodeNS(null, Constants._ATT_ID));

        derEncodedKeyValue.setId(idControl);
        assertEquals(idControl, derEncodedKeyValue.getId());
        assertTrue(derEncodedKeyValue.getElement().getAttributeNodeNS(null, Constants._ATT_ID).isId());

        derEncodedKeyValue.setId(null);
        assertEquals("", derEncodedKeyValue.getId());
        assertNull(derEncodedKeyValue.getElement().getAttributeNodeNS(null, Constants._ATT_ID));
    }

    // Utility methods

    private String getControlFilePath(String fileName) {
        return BASEDIR + SEP + "src" + SEP + "test" + SEP + "resources" + 
            SEP + "org" + SEP + "apache" + SEP + "xml" + SEP + "security" + 
            SEP + "keys" + SEP + "content" +
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

}
