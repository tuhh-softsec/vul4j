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
package org.apache.xml.security.test.dom.algorithms;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.test.dom.DSNamespaceContext;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A test to make sure that the various KeyWrap Encryption algorithms are working
 */
public class KeyWrapEncryptionAlgorithmTest extends org.junit.Assert {

    private KeyPair rsaKeyPair;
    
    static {
        org.apache.xml.security.Init.init();
    }
    
    public KeyWrapEncryptionAlgorithmTest() throws Exception {
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
            if (cons == null) {
                // BouncyCastle is not available so just return
                return;
            } else {
                Provider provider = (java.security.Provider)cons.newInstance();
                Security.insertProviderAt(provider, 2);
            }
        }
        
        rsaKeyPair = KeyPairGenerator.getInstance("RSA").genKeyPair();
    }
    
    @org.junit.Test
    public void testAES128KW() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(128);
        SecretKey key = keygen.generateKey();
        
        // Set up the Key Wrapping Key
        XMLCipher cipher = XMLCipher.getInstance(XMLCipher.AES_128_KeyWrap);
        keygen = KeyGenerator.getInstance("AES");
        keygen.init(128);
        SecretKey keyWrappingKey = keygen.generateKey();
        cipher.init(XMLCipher.WRAP_MODE, keyWrappingKey);
        EncryptedKey encryptedKey = cipher.encryptKey(document, key);
        
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        String encryptionAlgorithm = XMLCipher.AES_128;
        
        encrypt(encryptedKey, encryptionAlgorithm, document, localNames, key);
        
        // Check the CreditCard encrypted ok
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // XMLUtils.outputDOM(document, System.out);
        document = decrypt(document, keyWrappingKey);
        
        // Check the CreditCard decrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    @org.junit.Test
    public void testAES192KW() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(192);
        SecretKey key = keygen.generateKey();
        
        // Set up the Key Wrapping Key
        XMLCipher cipher = XMLCipher.getInstance(XMLCipher.AES_192_KeyWrap);
        keygen = KeyGenerator.getInstance("AES");
        keygen.init(192);
        SecretKey keyWrappingKey = keygen.generateKey();
        cipher.init(XMLCipher.WRAP_MODE, keyWrappingKey);
        EncryptedKey encryptedKey = cipher.encryptKey(document, key);
        
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        String encryptionAlgorithm = XMLCipher.AES_192;
        
        encrypt(encryptedKey, encryptionAlgorithm, document, localNames, key);
        
        // Check the CreditCard encrypted ok
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // XMLUtils.outputDOM(document, System.out);
        document = decrypt(document, keyWrappingKey);
        
        // Check the CreditCard decrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    @org.junit.Test
    public void testAES256KW() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(256);
        SecretKey key = keygen.generateKey();
        
        // Set up the Key Wrapping Key
        XMLCipher cipher = XMLCipher.getInstance(XMLCipher.AES_256_KeyWrap);
        keygen = KeyGenerator.getInstance("AES");
        keygen.init(256);
        SecretKey keyWrappingKey = keygen.generateKey();
        cipher.init(XMLCipher.WRAP_MODE, keyWrappingKey);
        EncryptedKey encryptedKey = cipher.encryptKey(document, key);
        
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        String encryptionAlgorithm = XMLCipher.AES_256;
        
        encrypt(encryptedKey, encryptionAlgorithm, document, localNames, key);
        
        // Check the CreditCard encrypted ok
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // XMLUtils.outputDOM(document, System.out);
        document = decrypt(document, keyWrappingKey);
        
        // Check the CreditCard decrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    @org.junit.Test
    public void testTripleDESKW() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("DESede");
        keygen.init(192);
        SecretKey key = keygen.generateKey();
        
        // Set up the Key Wrapping Key
        XMLCipher cipher = XMLCipher.getInstance(XMLCipher.TRIPLEDES_KeyWrap);
        keygen = KeyGenerator.getInstance("DESede");
        keygen.init(192);
        SecretKey keyWrappingKey = keygen.generateKey();
        cipher.init(XMLCipher.WRAP_MODE, keyWrappingKey);
        EncryptedKey encryptedKey = cipher.encryptKey(document, key);
        
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        String encryptionAlgorithm = XMLCipher.TRIPLEDES;
        
        encrypt(encryptedKey, encryptionAlgorithm, document, localNames, key);
        
        // Check the CreditCard encrypted ok
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // XMLUtils.outputDOM(document, System.out);
        document = decrypt(document, keyWrappingKey);
        
        // Check the CreditCard decrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    @org.junit.Test
    public void testRSAv15KW() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("DESede");
        keygen.init(192);
        SecretKey key = keygen.generateKey();
        
        // Set up the Key Wrapping Key
        XMLCipher cipher = XMLCipher.getInstance(XMLCipher.RSA_v1dot5);
        cipher.init(XMLCipher.WRAP_MODE, rsaKeyPair.getPublic());
        EncryptedKey encryptedKey = cipher.encryptKey(document, key);
        
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        String encryptionAlgorithm = XMLCipher.TRIPLEDES;
        
        encrypt(encryptedKey, encryptionAlgorithm, document, localNames, key);
        
        // Check the CreditCard encrypted ok
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // XMLUtils.outputDOM(document, System.out);
        document = decrypt(document, rsaKeyPair.getPrivate());
        
        // Check the CreditCard decrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    @org.junit.Test
    public void testRSAOAEPKW() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("DESede");
        keygen.init(192);
        SecretKey key = keygen.generateKey();
        
        // Set up the Key Wrapping Key
        XMLCipher cipher = XMLCipher.getInstance(XMLCipher.RSA_OAEP);
        cipher.init(XMLCipher.WRAP_MODE, rsaKeyPair.getPublic());
        EncryptedKey encryptedKey = cipher.encryptKey(document, key);
        
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        String encryptionAlgorithm = XMLCipher.TRIPLEDES;
        
        encrypt(encryptedKey, encryptionAlgorithm, document, localNames, key);
        
        // Check the CreditCard encrypted ok
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // XMLUtils.outputDOM(document, System.out);
        document = decrypt(document, rsaKeyPair.getPrivate());
        
        // Check the CreditCard decrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    @org.junit.Test
    public void testRSAOAEP11KW() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("DESede");
        keygen.init(192);
        SecretKey key = keygen.generateKey();
        
        // Set up the Key Wrapping Key
        XMLCipher cipher = XMLCipher.getInstance(XMLCipher.RSA_OAEP_11);
        cipher.init(XMLCipher.WRAP_MODE, rsaKeyPair.getPublic());
        EncryptedKey encryptedKey = cipher.encryptKey(document, key);
        
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        String encryptionAlgorithm = XMLCipher.TRIPLEDES;
        
        encrypt(encryptedKey, encryptionAlgorithm, document, localNames, key);
        
        // Check the CreditCard encrypted ok
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // XMLUtils.outputDOM(document, System.out);
        document = decrypt(document, rsaKeyPair.getPrivate());
        
        // Check the CreditCard decrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    @org.junit.Test
    public void testCamellia128KW() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("Camellia");
        keygen.init(128);
        SecretKey key = keygen.generateKey();
        
        // Set up the Key Wrapping Key
        XMLCipher cipher = XMLCipher.getInstance(XMLCipher.CAMELLIA_128_KeyWrap);
        keygen = KeyGenerator.getInstance("Camellia");
        keygen.init(128);
        SecretKey keyWrappingKey = keygen.generateKey();
        cipher.init(XMLCipher.WRAP_MODE, keyWrappingKey);
        EncryptedKey encryptedKey = cipher.encryptKey(document, key);
        
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        String encryptionAlgorithm = XMLCipher.CAMELLIA_128;
        
        encrypt(encryptedKey, encryptionAlgorithm, document, localNames, key);
        
        // Check the CreditCard encrypted ok
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // XMLUtils.outputDOM(document, System.out);
        document = decrypt(document, keyWrappingKey);
        
        // Check the CreditCard decrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    @org.junit.Test
    public void testCamellia192KW() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("Camellia");
        keygen.init(192);
        SecretKey key = keygen.generateKey();
        
        // Set up the Key Wrapping Key
        XMLCipher cipher = XMLCipher.getInstance(XMLCipher.CAMELLIA_192_KeyWrap);
        keygen = KeyGenerator.getInstance("Camellia");
        keygen.init(192);
        SecretKey keyWrappingKey = keygen.generateKey();
        cipher.init(XMLCipher.WRAP_MODE, keyWrappingKey);
        EncryptedKey encryptedKey = cipher.encryptKey(document, key);
        
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        String encryptionAlgorithm = XMLCipher.CAMELLIA_192;
        
        encrypt(encryptedKey, encryptionAlgorithm, document, localNames, key);
        
        // Check the CreditCard encrypted ok
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // XMLUtils.outputDOM(document, System.out);
        document = decrypt(document, keyWrappingKey);
        
        // Check the CreditCard decrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    @org.junit.Test
    public void testCamellia256KW() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("Camellia");
        keygen.init(256);
        SecretKey key = keygen.generateKey();
        
        // Set up the Key Wrapping Key
        XMLCipher cipher = XMLCipher.getInstance(XMLCipher.CAMELLIA_256_KeyWrap);
        keygen = KeyGenerator.getInstance("Camellia");
        keygen.init(256);
        SecretKey keyWrappingKey = keygen.generateKey();
        cipher.init(XMLCipher.WRAP_MODE, keyWrappingKey);
        EncryptedKey encryptedKey = cipher.encryptKey(document, key);
        
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        String encryptionAlgorithm = XMLCipher.CAMELLIA_256;
        
        encrypt(encryptedKey, encryptionAlgorithm, document, localNames, key);
        
        // Check the CreditCard encrypted ok
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // XMLUtils.outputDOM(document, System.out);
        document = decrypt(document, keyWrappingKey);
        
        // Check the CreditCard decrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    private void encrypt(
        EncryptedKey encryptedKey,
        String algorithm,
        Document document,
        List<String> localNames,
        Key encryptingKey
    ) throws Exception {
        XMLCipher cipher = XMLCipher.getInstance(algorithm);
        cipher.init(XMLCipher.ENCRYPT_MODE, encryptingKey);
        
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());

        EncryptedData builder = cipher.getEncryptedData();
        KeyInfo builderKeyInfo = builder.getKeyInfo();
        if (builderKeyInfo == null) {
            builderKeyInfo = new KeyInfo(document);
            builder.setKeyInfo(builderKeyInfo);
        }
        builderKeyInfo.add(encryptedKey);
        
        for (String localName : localNames) {
            String expression = "//*[local-name()='" + localName + "']";
            Element elementToEncrypt =
                    (Element) xpath.evaluate(expression, document, XPathConstants.NODE);
            Assert.assertNotNull(elementToEncrypt);

            document = cipher.doFinal(document, elementToEncrypt, false);
        }
        
        NodeList nodeList = document.getElementsByTagNameNS(
                XMLSecurityConstants.TAG_xenc_EncryptedData.getNamespaceURI(),
                XMLSecurityConstants.TAG_xenc_EncryptedData.getLocalPart()
            );
        Assert.assertTrue(nodeList.getLength() > 0);
    }
    
    private Document decrypt(
        Document document,
        Key keyWrappingKey
    ) throws Exception {
        NodeList nodeList = document.getElementsByTagNameNS(
                XMLSecurityConstants.TAG_xenc_EncryptedData.getNamespaceURI(),
                XMLSecurityConstants.TAG_xenc_EncryptedData.getLocalPart()
            );
        Element ee = (Element)nodeList.item(0);
        
        // Need to pre-load the Encrypted Data so we can get the key info
        XMLCipher cipher = XMLCipher.getInstance();
        cipher.init(XMLCipher.DECRYPT_MODE, null);
        EncryptedData encryptedData = cipher.loadEncryptedData(document, ee);

        XMLCipher kwCipher = XMLCipher.getInstance();
        kwCipher.init(XMLCipher.UNWRAP_MODE, keyWrappingKey);
        KeyInfo ki = encryptedData.getKeyInfo();
        EncryptedKey encryptedKey = ki.itemEncryptedKey(0);
        Key symmetricKey = 
            kwCipher.decryptKey(
                encryptedKey, encryptedData.getEncryptionMethod().getAlgorithm()
            );
        
        cipher.init(XMLCipher.DECRYPT_MODE, symmetricKey);
        return cipher.doFinal(document, ee);
    }
    

}
