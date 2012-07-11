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
package org.apache.xml.security.test.stax.signature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.KeyName;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.stax.config.Init;
import org.apache.xml.security.stax.ext.InboundXMLSec;
import org.apache.xml.security.stax.ext.XMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.securityEvent.AlgorithmSuiteSecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEventConstants;
import org.apache.xml.security.stax.securityEvent.SecurityEventConstants.Event;
import org.apache.xml.security.stax.securityEvent.SecurityEventListener;
import org.apache.xml.security.stax.securityEvent.SignatureValueSecurityEvent;
import org.apache.xml.security.stax.securityEvent.SignedElementSecurityEvent;
import org.apache.xml.security.stax.securityEvent.X509TokenSecurityEvent;
import org.apache.xml.security.test.dom.DSNamespaceContext;
import org.apache.xml.security.test.stax.utils.StAX2DOM;
import org.apache.xml.security.test.stax.utils.XMLSecEventAllocator;
import org.apache.xml.security.transforms.Transforms;
// import org.apache.xml.security.utils.XMLUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A set of test-cases for Signature verification.
 */
public class SignatureVerificationTest extends org.junit.Assert {

    private XMLInputFactory xmlInputFactory;
    private DocumentBuilderFactory documentBuilderFactory;
    private TransformerFactory transformerFactory = TransformerFactory.newInstance();
    
    @Before
    public void setUp() throws Exception {
        Init.init(SignatureVerificationTest.class.getClassLoader().getResource("security-config.xml").toURI());
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
    public void testSignatureVerification() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(
            this.getClass().getClassLoader().getResource("transmitter.jks").openStream(), 
            "default".toCharArray()
        );
        Key key = keyStore.getKey("transmitter", "default".toCharArray());
        X509Certificate cert = (X509Certificate)keyStore.getCertificate("transmitter");
        
        // Sign using DOM
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        signUsingDOM(
            "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, cert, key
        );
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
  
        // Verify signature
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setSignatureVerificationKey(cert.getPublicKey());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        document = StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
        
        // Check the SecurityEvents
        checkSecurityEvents(securityEventListener);
        checkSignedElementSecurityEvents(securityEventListener);
    }
    
    @Test
    public void testMultipleElements() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(
            this.getClass().getClassLoader().getResource("transmitter.jks").openStream(), 
            "default".toCharArray()
        );
        Key key = keyStore.getKey("transmitter", "default".toCharArray());
        X509Certificate cert = (X509Certificate)keyStore.getCertificate("transmitter");
        
        // Sign using DOM
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        localNames.add("ShippingAddress");
        signUsingDOM(
            "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, cert, key
        );
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
  
        // Verify signature
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setSignatureVerificationKey(cert.getPublicKey());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
            inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        document = StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
        
        // Check the SecurityEvents
        checkSecurityEvents(securityEventListener);
        checkSignedElementMultipleSecurityEvents(securityEventListener);
    }
    
    @Test
    public void testHMACSignatureVerification() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        byte[] hmacKey = "secret".getBytes("ASCII");
        SecretKey key = new SecretKeySpec(hmacKey, "http://www.w3.org/2000/09/xmldsig#hmac-sha1");
        
        // Sign using DOM
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        signUsingDOM(
            "http://www.w3.org/2000/09/xmldsig#hmac-sha1", document, localNames, null, key
        );
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
  
        // Verify signature
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setSignatureVerificationKey(key);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
            inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        document = StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
        
        // Check the SecurityEvents
        SignatureValueSecurityEvent sigValueEvent = 
                (SignatureValueSecurityEvent)securityEventListener.getTokenEvent(SecurityEventConstants.SignatureValue);
        assertNotNull(sigValueEvent);
        assertNotNull(sigValueEvent.getSignatureValue());
        
        checkSignedElementSecurityEvents(securityEventListener);
    }
    
    @Test
    public void testHMACSignatureVerificationWrongKey() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        byte[] hmacKey = "secret".getBytes("ASCII");
        SecretKey key = new SecretKeySpec(hmacKey, "http://www.w3.org/2000/09/xmldsig#hmac-sha1");
        
        // Sign using DOM
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        signUsingDOM(
            "http://www.w3.org/2000/09/xmldsig#hmac-sha1", document, localNames, null, key
        );
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
  
        // Verify signature
        XMLSecurityProperties properties = new XMLSecurityProperties();
        
        byte[] badKey = "secret2".getBytes("ASCII");
        key = new SecretKeySpec(badKey, "http://www.w3.org/2000/09/xmldsig#hmac-sha1");
        properties.setSignatureVerificationKey(key);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        XMLStreamReader securityStreamReader = inboundXMLSec.processInMessage(xmlStreamReader);

        try {
            StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
            fail("Failure expected on a bad key");
        } catch (XMLStreamException ex) {
            // expected
        }
    }
    
    @Test
    public void testECDSASignatureVerification() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(
            this.getClass().getClassLoader().getResource(
                "org/apache/xml/security/samples/input/ecdsa.jks").openStream(), 
                "security".toCharArray()
        );
        Key key = keyStore.getKey("ECDSA", "security".toCharArray());
        X509Certificate cert = (X509Certificate)keyStore.getCertificate("ECDSA");
        
        // Sign using DOM
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        signUsingDOM(
            "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1", document, localNames, cert, key
        );
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
  
        // Verify signature
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setSignatureVerificationKey(cert.getPublicKey());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
            inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        document = StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
        
        // Check the SecurityEvents
        checkSecurityEvents(securityEventListener, 
                "http://www.w3.org/2001/10/xml-exc-c14n#",
                "http://www.w3.org/2000/09/xmldsig#sha1",
                "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1");
        checkSignedElementSecurityEvents(securityEventListener);
    }
    
    @Test
    public void testDifferentC14nMethod() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(
            this.getClass().getClassLoader().getResource("transmitter.jks").openStream(), 
            "default".toCharArray()
        );
        Key key = keyStore.getKey("transmitter", "default".toCharArray());
        X509Certificate cert = (X509Certificate)keyStore.getCertificate("transmitter");
        
        // Sign using DOM
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        signUsingDOM(
            "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, cert, key,
            "http://www.w3.org/TR/2001/REC-xml-c14n-20010315"
        );
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
  
        // Verify signature
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setSignatureVerificationKey(cert.getPublicKey());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
            inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        document = StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
        
        // Check the SecurityEvents
        checkSecurityEvents(securityEventListener, 
                            "http://www.w3.org/TR/2001/REC-xml-c14n-20010315",
                            "http://www.w3.org/2000/09/xmldsig#sha1",
                            "http://www.w3.org/2000/09/xmldsig#rsa-sha1");
        checkSignedElementSecurityEvents(securityEventListener);
    }
    
    @Test
    public void testC14n11Method() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(
            this.getClass().getClassLoader().getResource("transmitter.jks").openStream(), 
            "default".toCharArray()
        );
        Key key = keyStore.getKey("transmitter", "default".toCharArray());
        X509Certificate cert = (X509Certificate)keyStore.getCertificate("transmitter");
        
        // Sign using DOM
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        signUsingDOM(
            "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, cert, key,
            "http://www.w3.org/2006/12/xml-c14n11"
        );
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
  
        // Verify signature
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setSignatureVerificationKey(cert.getPublicKey());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
            inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        document = StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
        
        // Check the SecurityEvents
        checkSecurityEvents(securityEventListener, 
                            "http://www.w3.org/2006/12/xml-c14n11",
                            "http://www.w3.org/2000/09/xmldsig#sha1",
                            "http://www.w3.org/2000/09/xmldsig#rsa-sha1");
        checkSignedElementSecurityEvents(securityEventListener);
    }
    
    @Test
    public void testStrongSignatureVerification() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(
            this.getClass().getClassLoader().getResource("transmitter.jks").openStream(), 
            "default".toCharArray()
        );
        Key key = keyStore.getKey("transmitter", "default".toCharArray());
        X509Certificate cert = (X509Certificate)keyStore.getCertificate("transmitter");
        
        // Sign using DOM
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        signUsingDOM(
            "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", document, localNames, cert, key,
            "http://www.w3.org/2001/10/xml-exc-c14n#", "http://www.w3.org/2001/04/xmlenc#sha256"
        );
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
  
        // Verify signature
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setSignatureVerificationKey(cert.getPublicKey());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
            inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        document = StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
        
        // Check the SecurityEvents
        checkSecurityEvents(securityEventListener, 
                            "http://www.w3.org/2001/10/xml-exc-c14n#",
                            "http://www.w3.org/2001/04/xmlenc#sha256",
                            "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
        checkSignedElementSecurityEvents(securityEventListener);
    }
    
    /**
     * Sign the document using DOM
     */
    private void signUsingDOM(
        String algorithm,
        Document document,
        List<String> localNames,
        X509Certificate cert,
        Key signingKey
    ) throws Exception {
        String c14nMethod = "http://www.w3.org/2001/10/xml-exc-c14n#";
        signUsingDOM(algorithm, document, localNames, cert, signingKey, c14nMethod);
    }
    
    /**
     * Sign the document using DOM
     */
    private void signUsingDOM(
        String algorithm,
        Document document,
        List<String> localNames,
        X509Certificate cert,
        Key signingKey,
        String c14nMethod
    ) throws Exception {
        String digestMethod = "http://www.w3.org/2000/09/xmldsig#sha1";
        signUsingDOM(algorithm, document, localNames, cert, signingKey, c14nMethod, digestMethod);
    }
    
    /**
     * Sign the document using DOM
     */
    private void signUsingDOM(
        String algorithm,
        Document document,
        List<String> localNames,
        X509Certificate cert,
        Key signingKey,
        String c14nMethod,
        String digestMethod
    ) throws Exception {
        XMLSignature sig = new XMLSignature(document, "", algorithm, c14nMethod);
        Element root = document.getDocumentElement();
        root.appendChild(sig.getElement());

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());
        
        for (String localName : localNames) {
            String expression = "//*[local-name()='" + localName + "']";
            Element elementToSign = 
                (Element)xpath.evaluate(expression, document, XPathConstants.NODE);
            Assert.assertNotNull(elementToSign);
            String id = UUID.randomUUID().toString();
            elementToSign.setAttributeNS(null, "Id", id);
            elementToSign.setIdAttributeNS(null, "Id", true);
            
            Transforms transforms = new Transforms(document);
            transforms.addTransform(c14nMethod);
            sig.addDocument("#" + id, transforms, digestMethod);
        }
        
        if (cert != null) {
            sig.addKeyInfo(cert);
        } else {
            KeyInfo keyInfo = sig.getKeyInfo();
            KeyName keyName = new KeyName(document, "SecretKey");
            keyInfo.add(keyName);
        }
        sig.sign(signingKey);
        
        String expression = "//ds:Signature[1]";
        Element sigElement = 
            (Element) xpath.evaluate(expression, document, XPathConstants.NODE);
        Assert.assertNotNull(sigElement);
    }
    
    private static class TestSecurityEventListener implements SecurityEventListener {
        List<SecurityEvent> events = new ArrayList<SecurityEvent>();

        @Override
        public void registerSecurityEvent(SecurityEvent securityEvent)
                throws XMLSecurityException {
            events.add(securityEvent);
        }
        
        public SecurityEvent getTokenEvent(Event securityEvent) {
            for (SecurityEvent event : events) {
                if (event.getSecurityEventType() == securityEvent) {
                    return event;
                }
            }
            return null;
        }
        
        public List<SecurityEvent> getTokenEvents(Event securityEvent) {
            List<SecurityEvent> foundEvents = new ArrayList<SecurityEvent>();
            for (SecurityEvent event : events) {
                if (event.getSecurityEventType() == securityEvent) {
                    foundEvents.add(event);
                }
            }
            return foundEvents;
        }
    }
    
    private void checkSecurityEvents(TestSecurityEventListener securityEventListener) {
        String c14nAlgorithm = "http://www.w3.org/2001/10/xml-exc-c14n#";
        String digestAlgorithm = "http://www.w3.org/2000/09/xmldsig#sha1";
        String signatureMethod = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
        checkSecurityEvents(securityEventListener, c14nAlgorithm, digestAlgorithm, signatureMethod);
    }
    
    private void checkSecurityEvents(
        TestSecurityEventListener securityEventListener,
        String c14nAlgorithm,
        String digestAlgorithm,
        String signatureMethod
    ) {
        X509TokenSecurityEvent tokenEvent = 
            (X509TokenSecurityEvent)securityEventListener.getTokenEvent(SecurityEventConstants.X509Token);
        assertNotNull(tokenEvent);

        SignatureValueSecurityEvent sigValueEvent = 
            (SignatureValueSecurityEvent)securityEventListener.getTokenEvent(SecurityEventConstants.SignatureValue);
        assertNotNull(sigValueEvent);
        assertNotNull(sigValueEvent.getSignatureValue());
        
        List<SecurityEvent> algorithmEvents = 
            securityEventListener.getTokenEvents(SecurityEventConstants.AlgorithmSuite);
        assertFalse(algorithmEvents.isEmpty());
        
        // C14n algorithm
        for (SecurityEvent event : algorithmEvents) {
            AlgorithmSuiteSecurityEvent algorithmEvent = (AlgorithmSuiteSecurityEvent)event;
            if (algorithmEvent.getKeyUsage() == XMLSecurityConstants.C14n) {
                assertEquals(c14nAlgorithm, algorithmEvent.getAlgorithmURI());
            }
        }
        
        // Digest algorithm
        for (SecurityEvent event : algorithmEvents) {
            AlgorithmSuiteSecurityEvent algorithmEvent = (AlgorithmSuiteSecurityEvent)event;
            if (algorithmEvent.getKeyUsage() == XMLSecurityConstants.Dig) {
                assertEquals(digestAlgorithm, algorithmEvent.getAlgorithmURI());
            }
        }
        
        // Signature method
        for (SecurityEvent event : algorithmEvents) {
            AlgorithmSuiteSecurityEvent algorithmEvent = (AlgorithmSuiteSecurityEvent)event;
            if (algorithmEvent.getKeyUsage() == XMLSecurityConstants.Asym_Sig
                || algorithmEvent.getKeyUsage() == XMLSecurityConstants.Sym_Sig) {
                assertEquals(signatureMethod, algorithmEvent.getAlgorithmURI());
            }
        }
    }
    
    private void checkSignedElementSecurityEvents(TestSecurityEventListener securityEventListener) {
        SignedElementSecurityEvent signedElementEvent = 
            (SignedElementSecurityEvent)securityEventListener.getTokenEvent(SecurityEventConstants.SignedElement);
        assertNotNull(signedElementEvent);
        assertEquals(signedElementEvent.getElementPath().size(), 2);
        assertEquals("{urn:example:po}PurchaseOrder", signedElementEvent.getElementPath().get(0).toString());
        assertEquals("{urn:example:po}PaymentInfo", signedElementEvent.getElementPath().get(1).toString());
        assertTrue(signedElementEvent.isSigned());
    }
    
    private void checkSignedElementMultipleSecurityEvents(
        TestSecurityEventListener securityEventListener
    ) {
        List<SecurityEvent> signedElements = 
            securityEventListener.getTokenEvents(SecurityEventConstants.SignedElement);
        assertTrue(signedElements.size() == 2);
        SignedElementSecurityEvent signedElementEvent = 
                (SignedElementSecurityEvent)signedElements.get(0);
        assertNotNull(signedElementEvent);
        assertEquals(signedElementEvent.getElementPath().size(), 2);
        assertEquals("{urn:example:po}PurchaseOrder", signedElementEvent.getElementPath().get(0).toString());
        assertEquals("{urn:example:po}ShippingAddress", signedElementEvent.getElementPath().get(1).toString());
        
        assertTrue(signedElementEvent.isSigned());
        
        signedElementEvent = 
            (SignedElementSecurityEvent)signedElements.get(1);
        assertNotNull(signedElementEvent);
        assertEquals(signedElementEvent.getElementPath().size(), 2);
        assertEquals("{urn:example:po}PurchaseOrder", signedElementEvent.getElementPath().get(0).toString());
        assertEquals("{urn:example:po}PaymentInfo", signedElementEvent.getElementPath().get(1).toString());
        assertTrue(signedElementEvent.isSigned());
    }
    
    

}
