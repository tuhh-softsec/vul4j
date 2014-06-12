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
package org.apache.xml.security.test.stax.encryption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.stax.ext.OutboundXMLSec;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.XMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.test.stax.utils.XMLSecEventAllocator;
import org.apache.xml.security.test.stax.utils.XmlReaderToWriter;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A test to make sure that the various KeyWrap Encryption algorithms are working
 */
public class KeyWrapEncryptionCreationTest extends org.junit.Assert {

    private XMLInputFactory xmlInputFactory;
    private KeyPair rsaKeyPair;

    @Before
    public void setUp() throws Exception {
        org.apache.xml.security.Init.init();

        xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setEventAllocator(new XMLSecEventAllocator());
        
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

    @Test
    public void testAES128KW() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.ENCRYPT);
        properties.setActions(actions);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(128);
        SecretKey key = keygen.generateKey();
        properties.setEncryptionKey(key);
        String algorithm = "http://www.w3.org/2001/04/xmlenc#aes128-cbc";
        properties.setEncryptionSymAlgorithm(algorithm);
        
        // Set up the Key Wrapping Key
        keygen = KeyGenerator.getInstance("AES");
        keygen.init(128);
        SecretKey keyWrappingKey = keygen.generateKey();
        String wrappingAlgorithm = "http://www.w3.org/2001/04/xmlenc#kw-aes128";
        properties.setEncryptionKeyTransportAlgorithm(wrappingAlgorithm);
        properties.setEncryptionTransportKey(keyWrappingKey);
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Element);
        properties.addEncryptionPart(securePart);
        
        OutboundXMLSec outboundXMLSec = XMLSec.getOutboundXMLSec(properties);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLStreamWriter xmlStreamWriter = outboundXMLSec.processOutMessage(baos, "UTF-8");
        
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);
        
        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();
        
        // System.out.println("Got:\n" + new String(baos.toByteArray(), "UTF-8"));
        
        Document document = 
            XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));
        
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "PaymentInfo");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // Check the CreditCard encrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        nodeList = document.getElementsByTagNameNS(
                XMLSecurityConstants.TAG_xenc_EncryptedData.getNamespaceURI(),
                XMLSecurityConstants.TAG_xenc_EncryptedData.getLocalPart()
            );
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Decrypt using DOM API
        Document doc = decryptUsingDOM(document, keyWrappingKey);
        
        // Check the CreditCard decrypted ok
        nodeList = doc.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    @Test
    public void testAES192KW() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.ENCRYPT);
        properties.setActions(actions);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(192);
        SecretKey key = keygen.generateKey();
        properties.setEncryptionKey(key);
        String algorithm = "http://www.w3.org/2001/04/xmlenc#aes192-cbc";
        properties.setEncryptionSymAlgorithm(algorithm);
        
        // Set up the Key Wrapping Key
        keygen = KeyGenerator.getInstance("AES");
        keygen.init(192);
        SecretKey keyWrappingKey = keygen.generateKey();
        String wrappingAlgorithm = "http://www.w3.org/2001/04/xmlenc#kw-aes192";
        properties.setEncryptionKeyTransportAlgorithm(wrappingAlgorithm);
        properties.setEncryptionTransportKey(keyWrappingKey);
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Element);
        properties.addEncryptionPart(securePart);
        
        OutboundXMLSec outboundXMLSec = XMLSec.getOutboundXMLSec(properties);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLStreamWriter xmlStreamWriter = outboundXMLSec.processOutMessage(baos, "UTF-8");
        
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);
        
        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();
        
        // System.out.println("Got:\n" + new String(baos.toByteArray(), "UTF-8"));
        
        Document document = 
            XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));
        
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "PaymentInfo");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // Check the CreditCard encrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        nodeList = document.getElementsByTagNameNS(
                XMLSecurityConstants.TAG_xenc_EncryptedData.getNamespaceURI(),
                XMLSecurityConstants.TAG_xenc_EncryptedData.getLocalPart()
            );
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Decrypt using DOM API
        Document doc = decryptUsingDOM(document, keyWrappingKey);
        
        // Check the CreditCard decrypted ok
        nodeList = doc.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    @Test
    public void testAES256KW() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.ENCRYPT);
        properties.setActions(actions);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(256);
        SecretKey key = keygen.generateKey();
        properties.setEncryptionKey(key);
        String algorithm = "http://www.w3.org/2001/04/xmlenc#aes256-cbc";
        properties.setEncryptionSymAlgorithm(algorithm);
        
        // Set up the Key Wrapping Key
        keygen = KeyGenerator.getInstance("AES");
        keygen.init(256);
        SecretKey keyWrappingKey = keygen.generateKey();
        String wrappingAlgorithm = "http://www.w3.org/2001/04/xmlenc#kw-aes256";
        properties.setEncryptionKeyTransportAlgorithm(wrappingAlgorithm);
        properties.setEncryptionTransportKey(keyWrappingKey);
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Element);
        properties.addEncryptionPart(securePart);
        
        OutboundXMLSec outboundXMLSec = XMLSec.getOutboundXMLSec(properties);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLStreamWriter xmlStreamWriter = outboundXMLSec.processOutMessage(baos, "UTF-8");
        
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);
        
        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();
        
        // System.out.println("Got:\n" + new String(baos.toByteArray(), "UTF-8"));
        
        Document document = 
            XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));
        
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "PaymentInfo");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // Check the CreditCard encrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        nodeList = document.getElementsByTagNameNS(
                XMLSecurityConstants.TAG_xenc_EncryptedData.getNamespaceURI(),
                XMLSecurityConstants.TAG_xenc_EncryptedData.getLocalPart()
            );
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Decrypt using DOM API
        Document doc = decryptUsingDOM(document, keyWrappingKey);
        
        // Check the CreditCard decrypted ok
        nodeList = doc.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    @Test
    public void testTripleDESKW() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.ENCRYPT);
        properties.setActions(actions);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("DESede");
        keygen.init(192);
        SecretKey key = keygen.generateKey();
        properties.setEncryptionKey(key);
        String algorithm = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
        properties.setEncryptionSymAlgorithm(algorithm);
        
        // Set up the Key Wrapping Key
        keygen = KeyGenerator.getInstance("DESede");
        keygen.init(192);
        SecretKey keyWrappingKey = keygen.generateKey();
        String wrappingAlgorithm = "http://www.w3.org/2001/04/xmlenc#kw-tripledes";
        properties.setEncryptionKeyTransportAlgorithm(wrappingAlgorithm);
        properties.setEncryptionTransportKey(keyWrappingKey);
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Element);
        properties.addEncryptionPart(securePart);
        
        OutboundXMLSec outboundXMLSec = XMLSec.getOutboundXMLSec(properties);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLStreamWriter xmlStreamWriter = outboundXMLSec.processOutMessage(baos, "UTF-8");
        
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);
        
        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();
        
        // System.out.println("Got:\n" + new String(baos.toByteArray(), "UTF-8"));
        
        Document document = 
            XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));
        
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "PaymentInfo");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // Check the CreditCard encrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        nodeList = document.getElementsByTagNameNS(
                XMLSecurityConstants.TAG_xenc_EncryptedData.getNamespaceURI(),
                XMLSecurityConstants.TAG_xenc_EncryptedData.getLocalPart()
            );
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Decrypt using DOM API
        Document doc = decryptUsingDOM(document, keyWrappingKey);
        
        // Check the CreditCard decrypted ok
        nodeList = doc.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    @Test
    public void testRSAv15KW() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.ENCRYPT);
        properties.setActions(actions);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("DESede");
        keygen.init(192);
        SecretKey key = keygen.generateKey();
        properties.setEncryptionKey(key);
        String algorithm = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
        properties.setEncryptionSymAlgorithm(algorithm);
        
        // Set up the Key Wrapping Key
        Key keyWrappingKey = rsaKeyPair.getPublic();
        String wrappingAlgorithm = "http://www.w3.org/2001/04/xmlenc#rsa-1_5";
        properties.setEncryptionKeyTransportAlgorithm(wrappingAlgorithm);
        properties.setEncryptionTransportKey(keyWrappingKey);
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Element);
        properties.addEncryptionPart(securePart);
        
        OutboundXMLSec outboundXMLSec = XMLSec.getOutboundXMLSec(properties);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLStreamWriter xmlStreamWriter = outboundXMLSec.processOutMessage(baos, "UTF-8");
        
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);
        
        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();
        
        // System.out.println("Got:\n" + new String(baos.toByteArray(), "UTF-8"));
        
        Document document = 
            XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));
        
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "PaymentInfo");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // Check the CreditCard encrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        nodeList = document.getElementsByTagNameNS(
                XMLSecurityConstants.TAG_xenc_EncryptedData.getNamespaceURI(),
                XMLSecurityConstants.TAG_xenc_EncryptedData.getLocalPart()
            );
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Decrypt using DOM API
        Document doc = decryptUsingDOM(document, rsaKeyPair.getPrivate());
        
        // Check the CreditCard decrypted ok
        nodeList = doc.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    @Test
    public void testRSAOAEPKW() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.ENCRYPT);
        properties.setActions(actions);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("DESede");
        keygen.init(192);
        SecretKey key = keygen.generateKey();
        properties.setEncryptionKey(key);
        String algorithm = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
        properties.setEncryptionSymAlgorithm(algorithm);
        
        // Set up the Key Wrapping Key
        Key keyWrappingKey = rsaKeyPair.getPublic();
        String wrappingAlgorithm = "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p";
        properties.setEncryptionKeyTransportAlgorithm(wrappingAlgorithm);
        properties.setEncryptionTransportKey(keyWrappingKey);
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Element);
        properties.addEncryptionPart(securePart);
        
        OutboundXMLSec outboundXMLSec = XMLSec.getOutboundXMLSec(properties);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLStreamWriter xmlStreamWriter = outboundXMLSec.processOutMessage(baos, "UTF-8");
        
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);
        
        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();
        
        // System.out.println("Got:\n" + new String(baos.toByteArray(), "UTF-8"));
        
        Document document = 
            XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));
        
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "PaymentInfo");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // Check the CreditCard encrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        nodeList = document.getElementsByTagNameNS(
                XMLSecurityConstants.TAG_xenc_EncryptedData.getNamespaceURI(),
                XMLSecurityConstants.TAG_xenc_EncryptedData.getLocalPart()
            );
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Decrypt using DOM API
        Document doc = decryptUsingDOM(document, rsaKeyPair.getPrivate());
        
        // Check the CreditCard decrypted ok
        nodeList = doc.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    @Test
    public void testRSAOAEP11KW() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.ENCRYPT);
        properties.setActions(actions);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("DESede");
        keygen.init(192);
        SecretKey key = keygen.generateKey();
        properties.setEncryptionKey(key);
        String algorithm = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
        properties.setEncryptionSymAlgorithm(algorithm);
        
        // Set up the Key Wrapping Key
        Key keyWrappingKey = rsaKeyPair.getPublic();
        String wrappingAlgorithm = "http://www.w3.org/2009/xmlenc11#rsa-oaep";
        properties.setEncryptionKeyTransportAlgorithm(wrappingAlgorithm);
        properties.setEncryptionTransportKey(keyWrappingKey);
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Element);
        properties.addEncryptionPart(securePart);
        
        OutboundXMLSec outboundXMLSec = XMLSec.getOutboundXMLSec(properties);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLStreamWriter xmlStreamWriter = outboundXMLSec.processOutMessage(baos, "UTF-8");
        
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);
        
        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();
        
        // System.out.println("Got:\n" + new String(baos.toByteArray(), "UTF-8"));
        
        Document document = 
            XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));
        
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "PaymentInfo");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // Check the CreditCard encrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        nodeList = document.getElementsByTagNameNS(
                XMLSecurityConstants.TAG_xenc_EncryptedData.getNamespaceURI(),
                XMLSecurityConstants.TAG_xenc_EncryptedData.getLocalPart()
            );
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Decrypt using DOM API
        Document doc = decryptUsingDOM(document, rsaKeyPair.getPrivate());
        
        // Check the CreditCard decrypted ok
        nodeList = doc.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    @Test
    public void testCamellia128KW() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.ENCRYPT);
        properties.setActions(actions);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("Camellia");
        keygen.init(128);
        SecretKey key = keygen.generateKey();
        properties.setEncryptionKey(key);
        String algorithm = "http://www.w3.org/2001/04/xmldsig-more#camellia128-cbc";
        properties.setEncryptionSymAlgorithm(algorithm);
        
        // Set up the Key Wrapping Key
        keygen = KeyGenerator.getInstance("Camellia");
        keygen.init(128);
        SecretKey keyWrappingKey = keygen.generateKey();
        String wrappingAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#kw-camellia128";
        properties.setEncryptionKeyTransportAlgorithm(wrappingAlgorithm);
        properties.setEncryptionTransportKey(keyWrappingKey);
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Element);
        properties.addEncryptionPart(securePart);
        
        OutboundXMLSec outboundXMLSec = XMLSec.getOutboundXMLSec(properties);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLStreamWriter xmlStreamWriter = outboundXMLSec.processOutMessage(baos, "UTF-8");
        
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);
        
        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();
        
        // System.out.println("Got:\n" + new String(baos.toByteArray(), "UTF-8"));
        
        Document document = 
            XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));
        
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "PaymentInfo");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // Check the CreditCard encrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        nodeList = document.getElementsByTagNameNS(
                XMLSecurityConstants.TAG_xenc_EncryptedData.getNamespaceURI(),
                XMLSecurityConstants.TAG_xenc_EncryptedData.getLocalPart()
            );
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Decrypt using DOM API
        Document doc = decryptUsingDOM(document, keyWrappingKey);
        
        // Check the CreditCard decrypted ok
        nodeList = doc.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    @Test
    public void testCamellia192KW() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.ENCRYPT);
        properties.setActions(actions);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("Camellia");
        keygen.init(192);
        SecretKey key = keygen.generateKey();
        properties.setEncryptionKey(key);
        String algorithm = "http://www.w3.org/2001/04/xmldsig-more#camellia192-cbc";
        properties.setEncryptionSymAlgorithm(algorithm);
        
        // Set up the Key Wrapping Key
        keygen = KeyGenerator.getInstance("Camellia");
        keygen.init(192);
        SecretKey keyWrappingKey = keygen.generateKey();
        String wrappingAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#kw-camellia192";
        properties.setEncryptionKeyTransportAlgorithm(wrappingAlgorithm);
        properties.setEncryptionTransportKey(keyWrappingKey);
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Element);
        properties.addEncryptionPart(securePart);
        
        OutboundXMLSec outboundXMLSec = XMLSec.getOutboundXMLSec(properties);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLStreamWriter xmlStreamWriter = outboundXMLSec.processOutMessage(baos, "UTF-8");
        
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);
        
        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();
        
        // System.out.println("Got:\n" + new String(baos.toByteArray(), "UTF-8"));
        
        Document document = 
            XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));
        
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "PaymentInfo");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // Check the CreditCard encrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        nodeList = document.getElementsByTagNameNS(
                XMLSecurityConstants.TAG_xenc_EncryptedData.getNamespaceURI(),
                XMLSecurityConstants.TAG_xenc_EncryptedData.getLocalPart()
            );
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Decrypt using DOM API
        Document doc = decryptUsingDOM(document, keyWrappingKey);
        
        // Check the CreditCard decrypted ok
        nodeList = doc.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    @Test
    public void testCamellia256KW() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.ENCRYPT);
        properties.setActions(actions);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("Camellia");
        keygen.init(256);
        SecretKey key = keygen.generateKey();
        properties.setEncryptionKey(key);
        String algorithm = "http://www.w3.org/2001/04/xmldsig-more#camellia256-cbc";
        properties.setEncryptionSymAlgorithm(algorithm);
        
        // Set up the Key Wrapping Key
        keygen = KeyGenerator.getInstance("Camellia");
        keygen.init(256);
        SecretKey keyWrappingKey = keygen.generateKey();
        String wrappingAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#kw-camellia256";
        properties.setEncryptionKeyTransportAlgorithm(wrappingAlgorithm);
        properties.setEncryptionTransportKey(keyWrappingKey);
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Element);
        properties.addEncryptionPart(securePart);
        
        OutboundXMLSec outboundXMLSec = XMLSec.getOutboundXMLSec(properties);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLStreamWriter xmlStreamWriter = outboundXMLSec.processOutMessage(baos, "UTF-8");
        
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);
        
        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();
        
        // System.out.println("Got:\n" + new String(baos.toByteArray(), "UTF-8"));
        
        Document document = 
            XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));
        
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "PaymentInfo");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // Check the CreditCard encrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        nodeList = document.getElementsByTagNameNS(
                XMLSecurityConstants.TAG_xenc_EncryptedData.getNamespaceURI(),
                XMLSecurityConstants.TAG_xenc_EncryptedData.getLocalPart()
            );
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Decrypt using DOM API
        Document doc = decryptUsingDOM(document, keyWrappingKey);
        
        // Check the CreditCard decrypted ok
        nodeList = doc.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    @Test
    public void testSEED128KW() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.ENCRYPT);
        properties.setActions(actions);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("SEED");
        keygen.init(128);
        SecretKey key = keygen.generateKey();
        properties.setEncryptionKey(key);
        String algorithm = "http://www.w3.org/2007/05/xmldsig-more#seed128-cbc";
        properties.setEncryptionSymAlgorithm(algorithm);
        
        // Set up the Key Wrapping Key
        keygen = KeyGenerator.getInstance("SEED");
        keygen.init(128);
        SecretKey keyWrappingKey = keygen.generateKey();
        String wrappingAlgorithm = "http://www.w3.org/2007/05/xmldsig-more#kw-seed128";
        properties.setEncryptionKeyTransportAlgorithm(wrappingAlgorithm);
        properties.setEncryptionTransportKey(keyWrappingKey);
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Element);
        properties.addEncryptionPart(securePart);
        
        OutboundXMLSec outboundXMLSec = XMLSec.getOutboundXMLSec(properties);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLStreamWriter xmlStreamWriter = outboundXMLSec.processOutMessage(baos, "UTF-8");
        
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);
        
        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();
        
        // System.out.println("Got:\n" + new String(baos.toByteArray(), "UTF-8"));
        
        Document document = 
            XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));
        
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "PaymentInfo");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // Check the CreditCard encrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        nodeList = document.getElementsByTagNameNS(
                XMLSecurityConstants.TAG_xenc_EncryptedData.getNamespaceURI(),
                XMLSecurityConstants.TAG_xenc_EncryptedData.getLocalPart()
            );
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Decrypt using DOM API
        Document doc = decryptUsingDOM(document, keyWrappingKey);
        
        // Check the CreditCard decrypted ok
        nodeList = doc.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    
    private Document decryptUsingDOM(
        Document document,
        Key keyWrappingKey
    ) throws Exception {
        NodeList nodeList = 
            document.getElementsByTagNameNS(
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