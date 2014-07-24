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
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
 * A set of test-cases for Signature verification with various PublicKey algorithms
 */
public class PKSignatureVerificationTest extends AbstractSignatureVerificationTest {

    private XMLInputFactory xmlInputFactory;
    private TransformerFactory transformerFactory = TransformerFactory.newInstance();
    private KeyPair rsaKeyPair, ecKeyPair;
    
    @Before
    public void setUp() throws Exception {
        Init.init(PKSignatureVerificationTest.class.getClassLoader().getResource("security-config.xml").toURI(),
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
        
        KeyPairGenerator rsaKpg = KeyPairGenerator.getInstance("RSA");
        rsaKpg.initialize(2048);
        rsaKeyPair = rsaKpg.genKeyPair();
        
        ecKeyPair = KeyPairGenerator.getInstance("EC").genKeyPair();
    }
    
    @Test
    public void testRSA_SHA1() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        String signatureAlgorithm = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        signUsingDOM(
                signatureAlgorithm, document, localNames, rsaKeyPair.getPrivate(),
                "http://www.w3.org/2001/10/xml-exc-c14n#", "http://www.w3.org/2000/09/xmldsig#sha1"
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
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader =
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testRSA_SHA256() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        signUsingDOM(
                signatureAlgorithm, document, localNames, rsaKeyPair.getPrivate(),
                "http://www.w3.org/2001/10/xml-exc-c14n#", "http://www.w3.org/2000/09/xmldsig#sha1"
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
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader =
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testRSA_SHA384() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        signUsingDOM(
                signatureAlgorithm, document, localNames, rsaKeyPair.getPrivate(),
                "http://www.w3.org/2001/10/xml-exc-c14n#", "http://www.w3.org/2000/09/xmldsig#sha1"
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
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader =
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testRSA_SHA512() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        signUsingDOM(
                signatureAlgorithm, document, localNames, rsaKeyPair.getPrivate(),
                "http://www.w3.org/2001/10/xml-exc-c14n#", "http://www.w3.org/2000/09/xmldsig#sha1"
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
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader =
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testRSA_RIPEMD160() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        signUsingDOM(
                signatureAlgorithm, document, localNames, rsaKeyPair.getPrivate(),
                "http://www.w3.org/2001/10/xml-exc-c14n#", "http://www.w3.org/2000/09/xmldsig#sha1"
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
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader =
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testRSA_SHA1_MGF1() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        String signatureAlgorithm = "http://www.w3.org/2007/05/xmldsig-more#sha1-rsa-MGF1";
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        signUsingDOM(
                signatureAlgorithm, document, localNames, rsaKeyPair.getPrivate(),
                "http://www.w3.org/2001/10/xml-exc-c14n#", "http://www.w3.org/2000/09/xmldsig#sha1"
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
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader =
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testRSA_SHA224_MGF1() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        String signatureAlgorithm = "http://www.w3.org/2007/05/xmldsig-more#sha224-rsa-MGF1";
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        signUsingDOM(
                signatureAlgorithm, document, localNames, rsaKeyPair.getPrivate(),
                "http://www.w3.org/2001/10/xml-exc-c14n#", "http://www.w3.org/2000/09/xmldsig#sha1"
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
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader =
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testRSA_SHA256_MGF1() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        String signatureAlgorithm = "http://www.w3.org/2007/05/xmldsig-more#sha256-rsa-MGF1";
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        signUsingDOM(
                signatureAlgorithm, document, localNames, rsaKeyPair.getPrivate(),
                "http://www.w3.org/2001/10/xml-exc-c14n#", "http://www.w3.org/2000/09/xmldsig#sha1"
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
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader =
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testRSA_SHA384_MGF1() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        String signatureAlgorithm = "http://www.w3.org/2007/05/xmldsig-more#sha384-rsa-MGF1";
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        signUsingDOM(
                signatureAlgorithm, document, localNames, rsaKeyPair.getPrivate(),
                "http://www.w3.org/2001/10/xml-exc-c14n#", "http://www.w3.org/2000/09/xmldsig#sha1"
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
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader =
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testRSA_SHA512_MGF1() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        String signatureAlgorithm = "http://www.w3.org/2007/05/xmldsig-more#sha512-rsa-MGF1";
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        signUsingDOM(
                signatureAlgorithm, document, localNames, rsaKeyPair.getPrivate(),
                "http://www.w3.org/2001/10/xml-exc-c14n#", "http://www.w3.org/2000/09/xmldsig#sha1"
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
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader =
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testECDSA_SHA1() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        signUsingDOM(
                signatureAlgorithm, document, localNames, ecKeyPair.getPrivate(),
                "http://www.w3.org/2001/10/xml-exc-c14n#", "http://www.w3.org/2000/09/xmldsig#sha1"
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
        properties.setSignatureVerificationKey(ecKeyPair.getPublic());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader =
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testECDSA_SHA224() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224";
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        signUsingDOM(
                signatureAlgorithm, document, localNames, ecKeyPair.getPrivate(),
                "http://www.w3.org/2001/10/xml-exc-c14n#", "http://www.w3.org/2000/09/xmldsig#sha1"
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
        properties.setSignatureVerificationKey(ecKeyPair.getPublic());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader =
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testECDSA_SHA256() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        signUsingDOM(
                signatureAlgorithm, document, localNames, ecKeyPair.getPrivate(),
                "http://www.w3.org/2001/10/xml-exc-c14n#", "http://www.w3.org/2000/09/xmldsig#sha1"
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
        properties.setSignatureVerificationKey(ecKeyPair.getPublic());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader =
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testECDSA_SHA384() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        signUsingDOM(
                signatureAlgorithm, document, localNames, ecKeyPair.getPrivate(),
                "http://www.w3.org/2001/10/xml-exc-c14n#", "http://www.w3.org/2000/09/xmldsig#sha1"
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
        properties.setSignatureVerificationKey(ecKeyPair.getPublic());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader =
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testECDSA_SHA512() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        signUsingDOM(
                signatureAlgorithm, document, localNames, ecKeyPair.getPrivate(),
                "http://www.w3.org/2001/10/xml-exc-c14n#", "http://www.w3.org/2000/09/xmldsig#sha1"
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
        properties.setSignatureVerificationKey(ecKeyPair.getPublic());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader =
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void testECDSA_RIPEMD160() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        String signatureAlgorithm = "http://www.w3.org/2007/05/xmldsig-more#ecdsa-ripemd160";
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        signUsingDOM(
                signatureAlgorithm, document, localNames, ecKeyPair.getPrivate(),
                "http://www.w3.org/2001/10/xml-exc-c14n#", "http://www.w3.org/2000/09/xmldsig#sha1"
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
        properties.setSignatureVerificationKey(ecKeyPair.getPublic());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader =
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    
}
