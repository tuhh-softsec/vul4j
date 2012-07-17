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
import java.security.Key;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.stax.ext.OutboundXMLSec;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.XMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.test.stax.utils.XMLSecEventAllocator;
import org.apache.xml.security.test.stax.utils.XmlReaderToWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A set of test-cases for Encryption.
 * 
 * @author $Author: coheigea $
 * @version $Revision: 1236690 $ $Date: 2012-01-27 14:07:10 +0000 (Fri, 27 Jan 2012) $
 */
public class EncryptionCreationTest extends org.junit.Assert {

    private XMLInputFactory xmlInputFactory;
    private DocumentBuilderFactory documentBuilderFactory;

    @Before
    public void setUp() throws Exception {
        org.apache.xml.security.Init.init();

        xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setEventAllocator(new XMLSecEventAllocator());

        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setIgnoringComments(false);
        documentBuilderFactory.setCoalescing(false);
        documentBuilderFactory.setIgnoringElementContentWhitespace(false);
    }

    @Test
    public void testEncryptionContentCreation() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions = 
            new XMLSecurityConstants.Action[]{XMLSecurityConstants.ENCRYPT};
        properties.setOutAction(actions);
        
        // Set the key up
        SecretKey key = generateDESSecretKey();
        properties.setEncryptionKey(key);
        properties.setEncryptionSymAlgorithm("http://www.w3.org/2001/04/xmlenc#tripledes-cbc");
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Content);
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
            documentBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(baos.toByteArray()));
        
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "PaymentInfo");
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Check the CreditCard encrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        nodeList = document.getElementsByTagNameNS(
                XMLSecurityConstants.TAG_xenc_EncryptedData.getNamespaceURI(),
                XMLSecurityConstants.TAG_xenc_EncryptedData.getLocalPart()
            );
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Decrypt using DOM API
        decryptUsingDOM("http://www.w3.org/2001/04/xmlenc#tripledes-cbc", key, null, document);
    }
    
    @Test
    public void testEncryptionElementCreation() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions = 
            new XMLSecurityConstants.Action[]{XMLSecurityConstants.ENCRYPT};
        properties.setOutAction(actions);
        
        // Set the key up
        SecretKey key = generateDESSecretKey();
        properties.setEncryptionKey(key);
        properties.setEncryptionSymAlgorithm("http://www.w3.org/2001/04/xmlenc#tripledes-cbc");
        
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
            documentBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(baos.toByteArray()));
        
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
        decryptUsingDOM("http://www.w3.org/2001/04/xmlenc#tripledes-cbc", key, null, document);
    }
    
    @Test
    public void testStrongEncryption() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions = 
            new XMLSecurityConstants.Action[]{XMLSecurityConstants.ENCRYPT};
        properties.setOutAction(actions);
        
        // Set the key up
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(256);
        SecretKey key = keygen.generateKey();
        properties.setEncryptionKey(key);
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Content);
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
            documentBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(baos.toByteArray()));
        
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "PaymentInfo");
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Check the CreditCard encrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        nodeList = document.getElementsByTagNameNS(
                XMLSecurityConstants.TAG_xenc_EncryptedData.getNamespaceURI(),
                XMLSecurityConstants.TAG_xenc_EncryptedData.getLocalPart()
            );
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Decrypt using DOM API
        decryptUsingDOM("http://www.w3.org/2001/04/xmlenc#aes256-cbc", key, null, document);
    }
    
    @Test
    public void testEncryptionMultipleElements() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions = 
            new XMLSecurityConstants.Action[]{XMLSecurityConstants.ENCRYPT};
        properties.setOutAction(actions);
        
        // Set the key up
        SecretKey key = generateDESSecretKey();
        properties.setEncryptionKey(key);
        properties.setEncryptionSymAlgorithm("http://www.w3.org/2001/04/xmlenc#tripledes-cbc");
        
        SecurePart securePart = 
            new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Content);
        properties.addEncryptionPart(securePart);
        securePart = 
            new SecurePart(new QName("urn:example:po", "ShippingAddress"), SecurePart.Modifier.Content);
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
            documentBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(baos.toByteArray()));
        
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "PaymentInfo");
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Check the CreditCard encrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        nodeList = document.getElementsByTagNameNS(
                XMLSecurityConstants.TAG_xenc_EncryptedData.getNamespaceURI(),
                XMLSecurityConstants.TAG_xenc_EncryptedData.getLocalPart()
            );
        Assert.assertEquals(nodeList.getLength(), 2);
    }
    
    /*
    
    /**
     * Test encryption using a generated AES 128 bit key that is
     * encrypted using a AES 192 bit key.  Then reverse using the KEK
    @Test
    public void testAES128ElementAES192KWCipherUsingKEKOutbound() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions = 
            new XMLSecurityConstants.Action[]{XMLSecurityConstants.ENCRYPT};
        properties.setOutAction(actions);
        properties.loadEncryptionKeystore(
            this.getClass().getClassLoader().getResource("transmitter.jks"), "default".toCharArray()
        );
        properties.setEncryptionUser("receiver");
        properties.setEncryptionSymAlgorithm("http://www.w3.org/2001/04/xmlenc#aes128-cbc");
        properties.setEncryptionKeyTransportAlgorithm("http://www.w3.org/2001/04/xmlenc#kw-aes192");
        
        SecurePart securePart = new SecurePart("PaymentInfo", "urn:example:po", SecurePart.Modifier.Element);
        properties.addEncryptionPart(securePart);
        
        SecurityContextImpl securityContextImpl = new SecurityContextImpl();
        // Set up a Key Encryption Key
        byte[] bits192 = "abcdefghijklmnopqrstuvwx".getBytes();
        SecretKey kek = new SecretKeySpec(bits192, "AES");

        // Generate a traffic key
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(128);
        SecretKey secretKey = keygen.generateKey();
        
        EncryptionSecurityToken securityToken = new EncryptionSecurityToken(secretKey);
        String id = UUID.randomUUID().toString();
        EncryptionSecurityTokenProvider securityTokenProvider = 
                new EncryptionSecurityTokenProvider(securityToken, id);
        securityContextImpl.registerSecurityTokenProvider(id, securityTokenProvider);
        securityContextImpl.put(XMLSecurityConstants.PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTION, id);

        final DocumentContextImpl documentContext = new DocumentContextImpl();
        documentContext.setEncoding("UTF-8");
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream("testdata/plaintext.xml");
        
        // Add processor to the chain
        OutputProcessorChainImpl processorChain = 
            new OutputProcessorChainImpl(securityContextImpl, documentContext);
        processorChain.addProcessor(
            new EKEncryptOutputProcessor(properties, XMLSecurityConstants.ENCRYPT, kek)
        );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        processorChain.addProcessor(new FinalOutputProcessor(baos, "UTF-8", properties, null));
        
        XMLStreamWriter xmlStreamWriter = new XMLSecurityStreamWriter(processorChain);
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);
        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();

        // System.out.println("Got:\n" + new String(baos.toByteArray(), "UTF-8"));
        
        Document document = 
            documentBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(baos.toByteArray()));
        
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
        
        // Decrypt using Apache Santuario
        decryptUsingSantuario("http://www.w3.org/2001/04/xmlenc#tripledes-cbc", null, kek, document);
    }
    
    /**
     * Test encryption using a generated AES 256 bit key that is
     * encrypted using an RSA key.  Reverse using KEK
    @Test
    public void testAES128ElementRSAKWCipherUsingKEK() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions = 
            new XMLSecurityConstants.Action[]{XMLSecurityConstants.ENCRYPT};
        properties.setOutAction(actions);
        properties.loadEncryptionKeystore(
            this.getClass().getClassLoader().getResource("transmitter.jks"), "default".toCharArray()
        );
        properties.setEncryptionUser("receiver");
        properties.setEncryptionSymAlgorithm("http://www.w3.org/2001/04/xmlenc#aes256-cbc");
        properties.setEncryptionKeyTransportAlgorithm("http://www.w3.org/2001/04/xmlenc#rsa-1_5");
        
        SecurePart securePart = new SecurePart("PaymentInfo", "urn:example:po", SecurePart.Modifier.Element);
        properties.addEncryptionPart(securePart);
        
        SecurityContextImpl securityContextImpl = new SecurityContextImpl();
        // Generate an RSA key
        KeyPairGenerator rsaKeygen = KeyPairGenerator.getInstance("RSA");
        KeyPair kp = rsaKeygen.generateKeyPair();
        PrivateKey priv = kp.getPrivate();
        PublicKey pub = kp.getPublic();

        // Generate a traffic key
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(256);
        SecretKey secretKey = keygen.generateKey();
        
        EncryptionSecurityToken securityToken = new EncryptionSecurityToken(secretKey);
        String id = UUID.randomUUID().toString();
        EncryptionSecurityTokenProvider securityTokenProvider = 
                new EncryptionSecurityTokenProvider(securityToken, id);
        securityContextImpl.registerSecurityTokenProvider(id, securityTokenProvider);
        securityContextImpl.put(XMLSecurityConstants.PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTION, id);

        final DocumentContextImpl documentContext = new DocumentContextImpl();
        documentContext.setEncoding("UTF-8");
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream("testdata/plaintext.xml");
        
        // Add processor to the chain
        OutputProcessorChainImpl processorChain = 
            new OutputProcessorChainImpl(securityContextImpl, documentContext);
        processorChain.addProcessor(
            new EKEncryptOutputProcessor(properties, XMLSecurityConstants.ENCRYPT, pub)
        );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        processorChain.addProcessor(new FinalOutputProcessor(baos, "UTF-8", properties, null));
        
        XMLStreamWriter xmlStreamWriter = new XMLSecurityStreamWriter(processorChain);
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);
        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();

        // System.out.println("Got:\n" + new String(baos.toByteArray(), "UTF-8"));
        
        Document document = 
            documentBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(baos.toByteArray()));
        
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
        
        // Decrypt using Apache Santuario
        decryptUsingSantuario("http://www.w3.org/2001/04/xmlenc#tripledes-cbc", null, priv, document);
    }
    
    /**
     * Test encryption using a generated AES 192 bit key that is
     * encrypted using a 3DES key.  Then reverse using the KEK
    @Test
    public void testAES192Element3DESKWCipher() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions = 
            new XMLSecurityConstants.Action[]{XMLSecurityConstants.ENCRYPT};
        properties.setOutAction(actions);
        properties.loadEncryptionKeystore(
            this.getClass().getClassLoader().getResource("transmitter.jks"), "default".toCharArray()
        );
        properties.setEncryptionUser("receiver");
        properties.setEncryptionSymAlgorithm("http://www.w3.org/2001/04/xmlenc#aes192-cbc");
        properties.setEncryptionKeyTransportAlgorithm("http://www.w3.org/2001/04/xmlenc#kw-tripledes");
        
        SecurePart securePart = new SecurePart("PaymentInfo", "urn:example:po", SecurePart.Modifier.Element);
        properties.addEncryptionPart(securePart);
        
        SecurityContextImpl securityContextImpl = new SecurityContextImpl();
        // Set up a Key Encryption Key
        SecretKey kek = generateSecretKey();

        // Generate a traffic key
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(192);
        SecretKey secretKey = keygen.generateKey();
        
        EncryptionSecurityToken securityToken = new EncryptionSecurityToken(secretKey);
        String id = UUID.randomUUID().toString();
        EncryptionSecurityTokenProvider securityTokenProvider = 
                new EncryptionSecurityTokenProvider(securityToken, id);
        securityContextImpl.registerSecurityTokenProvider(id, securityTokenProvider);
        securityContextImpl.put(XMLSecurityConstants.PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTION, id);

        final DocumentContextImpl documentContext = new DocumentContextImpl();
        documentContext.setEncoding("UTF-8");
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream("testdata/plaintext.xml");
        
        // Add processor to the chain
        OutputProcessorChainImpl processorChain = 
            new OutputProcessorChainImpl(securityContextImpl, documentContext);
        processorChain.addProcessor(
            new EKEncryptOutputProcessor(properties, XMLSecurityConstants.ENCRYPT, kek)
        );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        processorChain.addProcessor(new FinalOutputProcessor(baos, "UTF-8", properties, null));
        
        XMLStreamWriter xmlStreamWriter = new XMLSecurityStreamWriter(processorChain);
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);
        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();

        // System.out.println("Got:\n" + new String(baos.toByteArray(), "UTF-8"));
        
        Document document = 
            documentBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(baos.toByteArray()));
        
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
        
        // Decrypt using Apache Santuario
        decryptUsingSantuario("http://www.w3.org/2001/04/xmlenc#tripledes-cbc", null, kek, document);
    }
    */
    
    /**
     * Generate a secret key
     */
    private SecretKey generateDESSecretKey() throws Exception {
        byte[] bits192 = "abcdefghijklmnopqrstuvwx".getBytes();
        DESedeKeySpec keySpec = new DESedeKeySpec(bits192);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        return keyFactory.generateSecret(keySpec);
    }
    
    /**
     * Decrypt the document using DOM API and run some tests on the decrypted Document.
     */
    private void decryptUsingDOM(
        String algorithm, 
        SecretKey secretKey,
        Key wrappingKey,
        Document document
    ) throws Exception {
        XMLCipher cipher = XMLCipher.getInstance(algorithm);
        cipher.init(XMLCipher.DECRYPT_MODE, secretKey);
        if (wrappingKey != null) {
            cipher.setKEK(wrappingKey);
        }
        
        NodeList nodeList = document.getElementsByTagNameNS(
                XMLSecurityConstants.TAG_xenc_EncryptedData.getNamespaceURI(),
                XMLSecurityConstants.TAG_xenc_EncryptedData.getLocalPart()
            );
        Element ee = (Element)nodeList.item(0);
        document = cipher.doFinal(document, ee);
        
        // Check the CreditCard decrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }

}