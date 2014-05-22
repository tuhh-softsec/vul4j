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
import java.lang.reflect.Constructor;
import java.security.Key;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.stax.config.Init;
import org.apache.xml.security.stax.ext.InboundXMLSec;
import org.apache.xml.security.stax.ext.XMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.test.stax.utils.StAX2DOM;
import org.apache.xml.security.test.stax.utils.XMLSecEventAllocator;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * A set of test-cases for Signature verification with various digest algorithms
 */
public class SignatureDigestVerificationTest extends AbstractSignatureVerificationTest {

    private XMLInputFactory xmlInputFactory;
    private TransformerFactory transformerFactory = TransformerFactory.newInstance();
    
    @Before
    public void setUp() throws Exception {
        Init.init(SignatureDigestVerificationTest.class.getClassLoader().getResource("security-config.xml").toURI(),
                this.getClass());
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
    }
    
    @Test
    public void testSHA1() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
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
        
        String digestAlgorithm = "http://www.w3.org/2000/09/xmldsig#sha1";
        
        XMLSignature sig = signUsingDOM(
                "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, key,
                "http://www.w3.org/2001/10/xml-exc-c14n#", digestAlgorithm
        );
        
        // Add KeyInfo
        sig.addKeyInfo(cert);

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

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }

    @Test
    public void testSHA224() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
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
        
        String digestAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#sha224";
        
        XMLSignature sig = signUsingDOM(
                "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, key,
                "http://www.w3.org/2001/10/xml-exc-c14n#", digestAlgorithm
        );
        
        // Add KeyInfo
        sig.addKeyInfo(cert);

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

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testSHA256() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
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
        
        String digestAlgorithm = "http://www.w3.org/2001/04/xmlenc#sha256";
        
        XMLSignature sig = signUsingDOM(
                "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, key,
                "http://www.w3.org/2001/10/xml-exc-c14n#", digestAlgorithm
        );
        
        // Add KeyInfo
        sig.addKeyInfo(cert);

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

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testSHA384() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
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
        
        String digestAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#sha384";
        
        XMLSignature sig = signUsingDOM(
                "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, key,
                "http://www.w3.org/2001/10/xml-exc-c14n#", digestAlgorithm
        );
        
        // Add KeyInfo
        sig.addKeyInfo(cert);

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

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testSHA512() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
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
        
        String digestAlgorithm = "http://www.w3.org/2001/04/xmlenc#sha512";
        
        XMLSignature sig = signUsingDOM(
                "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, key,
                "http://www.w3.org/2001/10/xml-exc-c14n#", digestAlgorithm
        );
        
        // Add KeyInfo
        sig.addKeyInfo(cert);

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

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testRIPEMD160() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
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
        
        String digestAlgorithm = "http://www.w3.org/2001/04/xmlenc#ripemd160";
        
        XMLSignature sig = signUsingDOM(
                "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, key,
                "http://www.w3.org/2001/10/xml-exc-c14n#", digestAlgorithm
        );
        
        // Add KeyInfo
        sig.addKeyInfo(cert);

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

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testWhirlpool() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
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
        
        String digestAlgorithm = "http://www.w3.org/2007/05/xmldsig-more#whirlpool";
        
        XMLSignature sig = signUsingDOM(
                "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, key,
                "http://www.w3.org/2001/10/xml-exc-c14n#", digestAlgorithm
        );
        
        // Add KeyInfo
        sig.addKeyInfo(cert);

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

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testSHA3_224() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
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
        
        String digestAlgorithm = "http://www.w3.org/2007/05/xmldsig-more#sha3-224";
        
        XMLSignature sig = signUsingDOM(
                "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, key,
                "http://www.w3.org/2001/10/xml-exc-c14n#", digestAlgorithm
        );
        
        // Add KeyInfo
        sig.addKeyInfo(cert);

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

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }

    @Test
    public void testSHA3_256() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
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
        
        String digestAlgorithm = "http://www.w3.org/2007/05/xmldsig-more#sha3-256";
        
        XMLSignature sig = signUsingDOM(
                "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, key,
                "http://www.w3.org/2001/10/xml-exc-c14n#", digestAlgorithm
        );
        
        // Add KeyInfo
        sig.addKeyInfo(cert);

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

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testSHA3_384() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
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
        
        String digestAlgorithm = "http://www.w3.org/2007/05/xmldsig-more#sha3-384";
        
        XMLSignature sig = signUsingDOM(
                "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, key,
                "http://www.w3.org/2001/10/xml-exc-c14n#", digestAlgorithm
        );
        
        // Add KeyInfo
        sig.addKeyInfo(cert);

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

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testSHA3_512() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
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
        
        String digestAlgorithm = "http://www.w3.org/2007/05/xmldsig-more#sha3-512";
        
        XMLSignature sig = signUsingDOM(
                "http://www.w3.org/2000/09/xmldsig#rsa-sha1", document, localNames, key,
                "http://www.w3.org/2001/10/xml-exc-c14n#", digestAlgorithm
        );
        
        // Add KeyInfo
        sig.addKeyInfo(cert);

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

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }

    
}
