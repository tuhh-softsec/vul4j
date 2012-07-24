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
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.stax.ext.InboundXMLSec;
import org.apache.xml.security.stax.ext.XMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.securityEvent.ContentEncryptedElementSecurityEvent;
import org.apache.xml.security.stax.securityEvent.DefaultTokenSecurityEvent;
import org.apache.xml.security.stax.securityEvent.EncryptedElementSecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEventConstants;
import org.apache.xml.security.test.dom.DSNamespaceContext;
import org.apache.xml.security.test.stax.signature.TestSecurityEventListener;
import org.apache.xml.security.test.stax.utils.StAX2DOM;
import org.apache.xml.security.test.stax.utils.XMLSecEventAllocator;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A set of test-cases for Decryption.
 * 
 * @author $Author: coheigea $
 * @version $Revision: 1236690 $ $Date: 2012-01-27 14:07:10 +0000 (Fri, 27 Jan 2012) $
 */
public class DecryptionTest extends org.junit.Assert {

    private XMLInputFactory xmlInputFactory;
    private DocumentBuilderFactory documentBuilderFactory;
    private TransformerFactory transformerFactory = TransformerFactory.newInstance();
    
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
    public void testDecryptElementValidation() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        SecretKey secretKey = generateSecretKey();
        
        // Encrypt using DOM
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        encryptUsingDOM(
            "http://www.w3.org/2001/04/xmlenc#tripledes-cbc", secretKey, null, null, document, 
            localNames, false
        );
        
        // Check the CreditCard encrypted ok
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        
        // Decrypt
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setDecryptionKey(secretKey);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);
         
        document = StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
         
        // Check the CreditCard decrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Check the SecurityEvents
        checkEncryptedElementSecurityEvents(securityEventListener);
        checkEncryptionToken(securityEventListener, null, secretKey, 
                XMLSecurityConstants.XMLKeyIdentifierType.NO_KEY_INFO);
    }
    

    @Test
    public void testDecryptContentValidation() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        SecretKey secretKey = generateSecretKey();
        
        // Encrypt using DOM
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        encryptUsingDOM(
            "http://www.w3.org/2001/04/xmlenc#tripledes-cbc", secretKey, null, null, document, 
            localNames, true
        );
        
        // Check the CreditCard encrypted ok
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        
        // Decrypt
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setDecryptionKey(secretKey);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);
         
        document = StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
         
        // Check the CreditCard decrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Check the SecurityEvents
        checkEncryptedContentSecurityEvents(securityEventListener);
        checkEncryptionToken(securityEventListener, null, secretKey, 
                XMLSecurityConstants.XMLKeyIdentifierType.NO_KEY_INFO);
    }
    
    @Test
    public void testStrongDecryption() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(256);
        SecretKey secretKey = keygen.generateKey();
        
        // Encrypt using DOM
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        encryptUsingDOM(
            "http://www.w3.org/2001/04/xmlenc#aes256-cbc", secretKey, null, null, document, 
            localNames, false
        );
        
        // Check the CreditCard encrypted ok
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        
        // Decrypt
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setDecryptionKey(secretKey);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);
         
        document = StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
         
        // Check the CreditCard decrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Check the SecurityEvents
        checkEncryptedElementSecurityEvents(securityEventListener);
        checkEncryptionToken(securityEventListener, null, secretKey, 
                XMLSecurityConstants.XMLKeyIdentifierType.NO_KEY_INFO);
    }
    
    @Test
    public void testDecryptMultipleElements() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        SecretKey secretKey = generateSecretKey();
        
        // Encrypt using DOM
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        localNames.add("ShippingAddress");
        encryptUsingDOM(
            "http://www.w3.org/2001/04/xmlenc#tripledes-cbc", secretKey, null, null, document, 
            localNames, false
        );
        
        // Check the CreditCard encrypted ok
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        
        // Decrypt
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setDecryptionKey(secretKey);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);
         
        document = StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
         
        // Check the CreditCard decrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Check the ShippingAddress decrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "ShippingAddress");
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Check the SecurityEvents
        checkMultipleEncryptedElementSecurityEvents(securityEventListener);
        checkEncryptionToken(securityEventListener, null, secretKey, 
                XMLSecurityConstants.XMLKeyIdentifierType.NO_KEY_INFO);
    }
    
    // TODO
    @Test
    @org.junit.Ignore
    public void testAES128ElementAES192KWCipherUsingKEKInbound() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        byte[] bits192 = "abcdefghijklmnopqrstuvwx".getBytes();
        SecretKey kek = new SecretKeySpec(bits192, "AES");

        // Generate a traffic key
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(128);
        SecretKey secretKey = keygen.generateKey();
        
        // Encrypt using DOM
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        encryptUsingDOM(
            "http://www.w3.org/2001/04/xmlenc#aes128-cbc", secretKey, 
            "http://www.w3.org/2001/04/xmlenc#kw-aes192", kek, document, localNames, true
        );
        
        // Check the CreditCard encrypted ok
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 0);
        
        XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        
        // Decrypt
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setDecryptionKey(kek);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);
         
        document = StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
         
        // Check the CreditCard decrypted ok
        nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // TODO Check the SecurityEvents
    }
    
    /*
    @Test
    /**
     * Test encryption using a generated AES 128 bit key that is
     * encrypted using a AES 192 bit key.  Then reverse using the KEK
    @Test
    public void testAES128ElementAES192KWCipherUsingKEKOutbound() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream("testdata/plaintext.xml");
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document document = builder.parse(sourceDocument);
        
        // Generate the SecretKey
        // Set up a Key Encryption Key
        byte[] bits192 = "abcdefghijklmnopqrstuvwx".getBytes();
        SecretKey kek = new SecretKeySpec(bits192, "AES");

        // Generate a traffic key
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(128);
        SecretKey secretKey = keygen.generateKey();
        
        // Encrypt using Apache Santuario
        encryptUsingSantuario(
            "http://www.w3.org/2001/04/xmlenc#aes128-cbc", secretKey, 
            "http://www.w3.org/2001/04/xmlenc#kw-aes192", kek, document, false
        );
        
        XMLUtils.outputDOM(document, System.out);
        
        // Set up key
        SecurityContextImpl securityContextImpl = new SecurityContextImpl();
        EncryptionSecurityToken securityToken = new EncryptionSecurityToken(secretKey);
        securityContextImpl.put(XMLSecurityConstants.XMLINPUTFACTORY, xmlInputFactory);

        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.loadDecryptionKeystore(
                this.getClass().getClassLoader().getResource("transmitter.jks"), "default".toCharArray()
        );
        final DocumentContextImpl documentContext = new DocumentContextImpl();
        documentContext.setEncoding("UTF-8");
        
        // Convert Document to an Event Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLEventReader xmlEventReader = 
                xmlInputFactory.createXMLEventReader(new ByteArrayInputStream(baos.toByteArray()));
        
        // Set up the processor chain
        InputProcessorChainImpl processorChain = 
            new InputProcessorChainImpl(securityContextImpl, documentContext);
        processorChain.addProcessor(new XMLEventReaderInputProcessor(properties, xmlEventReader));
        XMLDecryptInputProcessor decProcessor = new XMLDecryptInputProcessor(null, properties);
        decProcessor.setSecurityToken(securityToken);
        processorChain.addProcessor(decProcessor);
        
        XMLStreamReader newStreamReader = new XMLSecurityStreamReader(processorChain, properties);
        document = StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), newStreamReader);
        
        XMLUtils.outputDOM(document, System.out);

        // Check the CreditCard decrypted ok
        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
    }
    */
    
    /**
     * Generate a secret key
     */
    private SecretKey generateSecretKey() throws Exception {
        byte[] bits192 = "abcdefghijklmnopqrstuvwx".getBytes();
        DESedeKeySpec keySpec = new DESedeKeySpec(bits192);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        return keyFactory.generateSecret(keySpec);
    }
    
    /**
     * Encrypt the document using DOM APIs and run some tests on the encrypted Document.
     */
    private void encryptUsingDOM(
        String algorithm, 
        SecretKey secretKey,
        String keyTransportAlgorithm,
        SecretKey wrappingKey,
        Document document,
        List<String> localNames,
        boolean content
    ) throws Exception {
        XMLCipher cipher = XMLCipher.getInstance(algorithm);
        cipher.init(XMLCipher.ENCRYPT_MODE, secretKey);
        
        if (wrappingKey != null) {
            XMLCipher newCipher = XMLCipher.getInstance(keyTransportAlgorithm);
            newCipher.init(XMLCipher.WRAP_MODE, wrappingKey);
            EncryptedKey encryptedKey = newCipher.encryptKey(document, wrappingKey);
            
            EncryptedData builder = cipher.getEncryptedData();

            KeyInfo builderKeyInfo = builder.getKeyInfo();
            if (builderKeyInfo == null) {
                builderKeyInfo = new KeyInfo(document);
                builderKeyInfo.getElement().setAttributeNS(
                    "http://www.w3.org/2000/xmlns/", "xmlns:dsig", "http://www.w3.org/2000/09/xmldsig#"
                );
                builder.setKeyInfo(builderKeyInfo);
            }

            builderKeyInfo.add(encryptedKey);
        }
        
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());

        for (String localName : localNames) {
            String expression = "//*[local-name()='" + localName + "']";
            Element elementToEncrypt =
                    (Element) xpath.evaluate(expression, document, XPathConstants.NODE);
            Assert.assertNotNull(elementToEncrypt);
            String id = UUID.randomUUID().toString();
            elementToEncrypt.setAttributeNS(null, "Id", id);
            elementToEncrypt.setIdAttributeNS(null, "Id", true);

            document = cipher.doFinal(document, elementToEncrypt, content);
        }
        
        NodeList nodeList = document.getElementsByTagNameNS(
                XMLSecurityConstants.TAG_xenc_EncryptedData.getNamespaceURI(),
                XMLSecurityConstants.TAG_xenc_EncryptedData.getLocalPart()
            );
        Assert.assertTrue(nodeList.getLength() > 0);
    }
    
    protected void checkEncryptedElementSecurityEvents(TestSecurityEventListener securityEventListener) {
        EncryptedElementSecurityEvent encryptedElementEvent =
                (EncryptedElementSecurityEvent) securityEventListener.getTokenEvent(SecurityEventConstants.EncryptedElement);
        assertNotNull(encryptedElementEvent);
        assertEquals(encryptedElementEvent.getElementPath().size(), 2);
        assertEquals("{urn:example:po}PurchaseOrder", encryptedElementEvent.getElementPath().get(0).toString());
        assertEquals("{urn:example:po}PaymentInfo", encryptedElementEvent.getElementPath().get(1).toString());
        assertTrue(encryptedElementEvent.isEncrypted());
    }
    
    protected void checkMultipleEncryptedElementSecurityEvents(TestSecurityEventListener securityEventListener) {
        List<SecurityEvent> encryptedElements =
                securityEventListener.getTokenEvents(SecurityEventConstants.EncryptedElement);
        assertTrue(encryptedElements.size() == 2);
        
        EncryptedElementSecurityEvent encryptedElementEvent =
                (EncryptedElementSecurityEvent)encryptedElements.get(0);
        assertNotNull(encryptedElementEvent);
        assertEquals(encryptedElementEvent.getElementPath().size(), 2);
        assertEquals("{urn:example:po}PurchaseOrder", encryptedElementEvent.getElementPath().get(0).toString());
        assertEquals("{urn:example:po}ShippingAddress", encryptedElementEvent.getElementPath().get(1).toString());
        assertTrue(encryptedElementEvent.isEncrypted());
        
        encryptedElementEvent =
                (EncryptedElementSecurityEvent)encryptedElements.get(1);
        assertNotNull(encryptedElementEvent);
        assertEquals(encryptedElementEvent.getElementPath().size(), 2);
        assertEquals("{urn:example:po}PurchaseOrder", encryptedElementEvent.getElementPath().get(0).toString());
        assertEquals("{urn:example:po}PaymentInfo", encryptedElementEvent.getElementPath().get(1).toString());
        assertTrue(encryptedElementEvent.isEncrypted());
    }
    
    protected void checkEncryptedContentSecurityEvents(TestSecurityEventListener securityEventListener) {
        ContentEncryptedElementSecurityEvent encryptedElementEvent =
                (ContentEncryptedElementSecurityEvent) securityEventListener.getTokenEvent(SecurityEventConstants.ContentEncrypted);
        assertNotNull(encryptedElementEvent);
        assertEquals(encryptedElementEvent.getElementPath().size(), 2);
        assertEquals("{urn:example:po}PurchaseOrder", encryptedElementEvent.getElementPath().get(0).toString());
        assertEquals("{urn:example:po}PaymentInfo", encryptedElementEvent.getElementPath().get(1).toString());
        assertTrue(encryptedElementEvent.isEncrypted());
    }
    
    protected void checkEncryptionToken(
            TestSecurityEventListener securityEventListener,
            X509Certificate cert,
            Key key,
            XMLSecurityConstants.XMLKeyIdentifierType keyIdentifierType
    ) throws XMLSecurityException {
        if (keyIdentifierType == XMLSecurityConstants.XMLKeyIdentifierType.NO_KEY_INFO) {
            DefaultTokenSecurityEvent tokenEvent =
                    (DefaultTokenSecurityEvent) securityEventListener.getTokenEvent(SecurityEventConstants.DefaultToken);
            assertNotNull(tokenEvent);
            Key processedKey = tokenEvent.getSecurityToken().getSecretKey("", null);
            assertEquals(processedKey, key);
        } 
    }
}
