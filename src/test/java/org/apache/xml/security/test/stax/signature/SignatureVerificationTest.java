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

import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.KeyName;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.keys.content.x509.XMLX509IssuerSerial;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.stax.config.Init;
import org.apache.xml.security.stax.ext.InboundXMLSec;
import org.apache.xml.security.stax.ext.XMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.test.stax.utils.StAX2DOM;
import org.apache.xml.security.test.stax.utils.XMLSecEventAllocator;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * A set of test-cases for Signature verification.
 */
public class SignatureVerificationTest extends AbstractSignatureVerificationTest {

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
        XMLSignature sig = signUsingDOM(
            "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, key
        );
        
        // Add KeyInfo
        sig.addKeyInfo(cert);
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
  
        // Verify signature
        XMLSecurityProperties properties = new XMLSecurityProperties();
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        document = StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
        
        // Check the SecurityEvents
        checkSecurityEvents(securityEventListener);
        checkSignedElementSecurityEvents(securityEventListener);
        checkSignatureToken(securityEventListener, cert, null,
                            XMLSecurityConstants.XMLKeyIdentifierType.X509_CERTIFICATE);
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
        XMLSignature sig = signUsingDOM(
            "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, key
        );
        
        // Add KeyInfo
        sig.addKeyInfo(cert);
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
  
        // Verify signature
        XMLSecurityProperties properties = new XMLSecurityProperties();
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
            inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        document = StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
        
        // Check the SecurityEvents
        checkSecurityEvents(securityEventListener);
        checkSignedElementMultipleSecurityEvents(securityEventListener);
        checkSignatureToken(securityEventListener, cert, null,
                            XMLSecurityConstants.XMLKeyIdentifierType.X509_CERTIFICATE);
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
        XMLSignature sig = signUsingDOM(
            "http://www.w3.org/2000/09/xmldsig#hmac-sha1", document, localNames, key
        );
        
        // Add KeyInfo
        KeyInfo keyInfo = sig.getKeyInfo();
        KeyName keyName = new KeyName(document, "SecretKey");
        keyInfo.add(keyName);
        
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
        checkSecurityEvents(securityEventListener,
                            "http://www.w3.org/2001/10/xml-exc-c14n#",
                            "http://www.w3.org/2000/09/xmldsig#sha1",
                            "http://www.w3.org/2000/09/xmldsig#hmac-sha1");
        checkSignedElementSecurityEvents(securityEventListener);
        checkSignatureToken(securityEventListener, null, key,
                            XMLSecurityConstants.XMLKeyIdentifierType.KEY_NAME);
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
        XMLSignature sig = signUsingDOM(
            "http://www.w3.org/2000/09/xmldsig#hmac-sha1", document, localNames, key
        );
        
        // Add KeyInfo
        KeyInfo keyInfo = sig.getKeyInfo();
        KeyName keyName = new KeyName(document, "SecretKey");
        keyInfo.add(keyName);
        
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
        XMLSignature sig = signUsingDOM(
            "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1", document, localNames, key
        );
        
        // Add KeyInfo
        sig.addKeyInfo(cert);
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
  
        // Verify signature
        XMLSecurityProperties properties = new XMLSecurityProperties();
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
        checkSignatureToken(securityEventListener, cert, null,
                            XMLSecurityConstants.XMLKeyIdentifierType.X509_CERTIFICATE);
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
        XMLSignature sig = signUsingDOM(
            "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, key,
            "http://www.w3.org/TR/2001/REC-xml-c14n-20010315"
        );
        
        // Add KeyInfo
        sig.addKeyInfo(cert);
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
  
        // Verify signature
        XMLSecurityProperties properties = new XMLSecurityProperties();
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
        checkSignatureToken(securityEventListener, cert, null,
                            XMLSecurityConstants.XMLKeyIdentifierType.X509_CERTIFICATE);
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
        XMLSignature sig = signUsingDOM(
            "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, key,
            "http://www.w3.org/2006/12/xml-c14n11"
        );
        
        // Add KeyInfo
        sig.addKeyInfo(cert);
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
  
        // Verify signature
        XMLSecurityProperties properties = new XMLSecurityProperties();
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
        checkSignatureToken(securityEventListener, cert, null,
                            XMLSecurityConstants.XMLKeyIdentifierType.X509_CERTIFICATE);
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
        XMLSignature sig = signUsingDOM(
            "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", document, localNames, key,
            "http://www.w3.org/2001/10/xml-exc-c14n#", "http://www.w3.org/2001/04/xmlenc#sha256"
        );
        
        // Add KeyInfo
        sig.addKeyInfo(cert);
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
  
        // Verify signature
        XMLSecurityProperties properties = new XMLSecurityProperties();
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
        checkSignatureToken(securityEventListener, cert, null,
                            XMLSecurityConstants.XMLKeyIdentifierType.X509_CERTIFICATE);
    }
    
    @Test
    public void testIssuerSerial() throws Exception {
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
        XMLSignature sig = signUsingDOM(
            "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, key
        );
        
        // Add KeyInfo
        KeyInfo keyInfo = sig.getKeyInfo();
        XMLX509IssuerSerial issuerSerial = 
            new XMLX509IssuerSerial(sig.getDocument(), cert);
        X509Data x509Data = new X509Data(sig.getDocument());
        x509Data.add(issuerSerial);
        keyInfo.add(x509Data);
        
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
        checkSignatureToken(securityEventListener, cert, null,
                            XMLSecurityConstants.XMLKeyIdentifierType.X509_ISSUER_SERIAL);
    }
    
    @Test
    public void testSubjectName() throws Exception {
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
        XMLSignature sig = signUsingDOM(
            "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, key
        );
        
        // Add KeyInfo
        KeyInfo keyInfo = sig.getKeyInfo();
        X509Data x509Data = new X509Data(sig.getDocument());
        x509Data.addSubjectName(cert);
        keyInfo.add(x509Data);
        
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
        checkSignatureToken(securityEventListener, cert, null,
                            XMLSecurityConstants.XMLKeyIdentifierType.X509_SUBJECT_NAME);
    }
    
    @Test
    public void testSubjectSKI() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        keyStore.load(
            this.getClass().getClassLoader().getResource("test.jceks").openStream(), 
            "secret".toCharArray()
        );
        Key key = keyStore.getKey("rsakey", "secret".toCharArray());
        X509Certificate cert = (X509Certificate)keyStore.getCertificate("rsakey");
        
        // Sign using DOM
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        XMLSignature sig = signUsingDOM(
            "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, key
        );
        
        // Add KeyInfo
        KeyInfo keyInfo = sig.getKeyInfo();
        X509Data x509Data = new X509Data(sig.getDocument());
        x509Data.addSKI(cert);
        keyInfo.add(x509Data);
        
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
        checkSignatureToken(securityEventListener, cert, null,
                            XMLSecurityConstants.XMLKeyIdentifierType.X509_SKI);
    }
    
    @Test
    public void testKeyValue() throws Exception {
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
        XMLSignature sig = signUsingDOM(
            "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, key
        );
        
        // Add KeyInfo
        sig.addKeyInfo(cert.getPublicKey());
        
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
        checkSignatureToken(securityEventListener, null, cert.getPublicKey(),
                            XMLSecurityConstants.XMLKeyIdentifierType.KEY_VALUE);
    }

}
