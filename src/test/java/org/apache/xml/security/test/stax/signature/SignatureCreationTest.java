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
import java.security.cert.X509Certificate;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.stax.crypto.CryptoType;
import org.apache.xml.security.stax.ext.OutboundXMLSec;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.XMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.test.dom.DSNamespaceContext;
import org.apache.xml.security.test.stax.utils.XMLSecEventAllocator;
import org.apache.xml.security.test.stax.utils.XmlReaderToWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A set of test-cases for Signature creation.
 */
public class SignatureCreationTest extends org.junit.Assert {

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
    public void testSignatureCreation() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions = 
            new XMLSecurityConstants.Action[]{XMLSecurityConstants.SIGNATURE};
        properties.setOutAction(actions);
        properties.loadSignatureKeyStore(
            this.getClass().getClassLoader().getResource("transmitter.jks"), "default".toCharArray()
        );
        properties.setSignatureUser("transmitter");
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Content);
        properties.addSignaturePart(securePart);
        
        // Set the key up
        Key key = properties.getSignatureCrypto().getPrivateKey("transmitter", "default");
        properties.setSignatureKey(key);

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
        
        // Verify using DOM
        CryptoType cryptoType = new CryptoType(CryptoType.TYPE.ALIAS);
        cryptoType.setAlias(properties.getSignatureUser());
        X509Certificate[] x509Certificates = properties.getSignatureCrypto().getX509Certificates(cryptoType);
        verifyUsingDOM(document, x509Certificates[0], properties.getSignatureSecureParts());
    }
    
    @Test
    public void testMultipleElements() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions = 
            new XMLSecurityConstants.Action[]{XMLSecurityConstants.SIGNATURE};
        properties.setOutAction(actions);
        properties.loadSignatureKeyStore(
            this.getClass().getClassLoader().getResource("transmitter.jks"), "default".toCharArray()
        );
        properties.setSignatureUser("transmitter");
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Content);
        properties.addSignaturePart(securePart);
        securePart = 
                new SecurePart(new QName("urn:example:po", "ShippingAddress"), SecurePart.Modifier.Content);
        properties.addSignaturePart(securePart);
        
        // Set the key up
        Key key = properties.getSignatureCrypto().getPrivateKey("transmitter", "default");
        properties.setSignatureKey(key);

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
        
        // Verify using DOM
        CryptoType cryptoType = new CryptoType(CryptoType.TYPE.ALIAS);
        cryptoType.setAlias(properties.getSignatureUser());
        X509Certificate[] x509Certificates = properties.getSignatureCrypto().getX509Certificates(cryptoType);
        verifyUsingDOM(document, x509Certificates[0], properties.getSignatureSecureParts());
    }
    
    @Test
    public void testHMACSignatureCreation() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions = 
            new XMLSecurityConstants.Action[]{XMLSecurityConstants.SIGNATURE};
        properties.setOutAction(actions);
        properties.loadSignatureKeyStore(
            this.getClass().getClassLoader().getResource("transmitter.jks"), "default".toCharArray()
        );
        properties.setSignatureUser("transmitter");
        properties.setSignatureAlgorithm("http://www.w3.org/2000/09/xmldsig#hmac-sha1");
        
        SecurePart securePart = 
                new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Content);
        properties.addSignaturePart(securePart);
        
        // Set the key up
        byte[] hmacKey = "secret".getBytes("ASCII");
        SecretKey key = new SecretKeySpec(hmacKey, "http://www.w3.org/2000/09/xmldsig#hmac-sha1");
        properties.setSignatureKey(key);

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
        
        // Verify using DOM
        verifyUsingDOM(document, key, properties.getSignatureSecureParts());
    }
    
    @Test
    public void testStrongSignatureCreation() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions = 
            new XMLSecurityConstants.Action[]{XMLSecurityConstants.SIGNATURE};
        properties.setOutAction(actions);
        properties.loadSignatureKeyStore(
            this.getClass().getClassLoader().getResource("transmitter.jks"), "default".toCharArray()
        );
        properties.setSignatureUser("transmitter");
        properties.setSignatureAlgorithm("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
        properties.setSignatureDigestAlgorithm("http://www.w3.org/2001/04/xmlenc#sha256");
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Content);
        properties.addSignaturePart(securePart);
        
        // Set the key up
        Key key = properties.getSignatureCrypto().getPrivateKey("transmitter", "default");
        properties.setSignatureKey(key);

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
        
        // Verify using DOM
        CryptoType cryptoType = new CryptoType(CryptoType.TYPE.ALIAS);
        cryptoType.setAlias(properties.getSignatureUser());
        X509Certificate[] x509Certificates = properties.getSignatureCrypto().getX509Certificates(cryptoType);
        verifyUsingDOM(document, x509Certificates[0], properties.getSignatureSecureParts());
    }
    
    @Test
    public void testECDSASignatureCreation() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions = 
            new XMLSecurityConstants.Action[]{XMLSecurityConstants.SIGNATURE};
        properties.setOutAction(actions);
        properties.loadSignatureKeyStore(
            this.getClass().getClassLoader().getResource(
                "org/apache/xml/security/samples/input/ecdsa.jks"), "security".toCharArray()
        );
        properties.setSignatureUser("ECDSA");
        properties.setSignatureAlgorithm("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1");
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Content);
        properties.addSignaturePart(securePart);
        
        // Set the key up
        Key key = properties.getSignatureCrypto().getPrivateKey("ECDSA", "security");
        properties.setSignatureKey(key);

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
        
        // Verify using DOM
        CryptoType cryptoType = new CryptoType(CryptoType.TYPE.ALIAS);
        cryptoType.setAlias(properties.getSignatureUser());
        X509Certificate[] x509Certificates = properties.getSignatureCrypto().getX509Certificates(cryptoType);
        verifyUsingDOM(document, x509Certificates[0], properties.getSignatureSecureParts());
    }
    
    @Test
    public void testStrongECDSASignatureCreation() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions = 
            new XMLSecurityConstants.Action[]{XMLSecurityConstants.SIGNATURE};
        properties.setOutAction(actions);
        properties.loadSignatureKeyStore(
            this.getClass().getClassLoader().getResource(
                "org/apache/xml/security/samples/input/ecdsa.jks"), "security".toCharArray()
        );
        properties.setSignatureUser("ECDSA");
        properties.setSignatureAlgorithm("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256");
        properties.setSignatureCanonicalizationAlgorithm("http://www.w3.org/2001/10/xml-exc-c14n#");
        properties.setSignatureDigestAlgorithm("http://www.w3.org/2001/04/xmlenc#sha256");
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Content);
        properties.addSignaturePart(securePart);
        
        // Set the key up
        Key key = properties.getSignatureCrypto().getPrivateKey("ECDSA", "security");
        properties.setSignatureKey(key);

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
        
        // Verify using DOM
        CryptoType cryptoType = new CryptoType(CryptoType.TYPE.ALIAS);
        cryptoType.setAlias(properties.getSignatureUser());
        X509Certificate[] x509Certificates = properties.getSignatureCrypto().getX509Certificates(cryptoType);
        verifyUsingDOM(document, x509Certificates[0], properties.getSignatureSecureParts());
    }

    @Test
    public void testDifferentC14nMethod() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions = 
            new XMLSecurityConstants.Action[]{XMLSecurityConstants.SIGNATURE};
        properties.setOutAction(actions);
        properties.loadSignatureKeyStore(
            this.getClass().getClassLoader().getResource("transmitter.jks"), "default".toCharArray()
        );
        properties.setSignatureUser("transmitter");
        properties.setSignatureCanonicalizationAlgorithm("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Content);
        properties.addSignaturePart(securePart);
        
        // Set the key up
        Key key = properties.getSignatureCrypto().getPrivateKey("transmitter", "default");
        properties.setSignatureKey(key);

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
        
        // Verify using DOM
        CryptoType cryptoType = new CryptoType(CryptoType.TYPE.ALIAS);
        cryptoType.setAlias(properties.getSignatureUser());
        X509Certificate[] x509Certificates = properties.getSignatureCrypto().getX509Certificates(cryptoType);
        verifyUsingDOM(document, x509Certificates[0], properties.getSignatureSecureParts());
    }
    
    @Test
    public void testC14n11Method() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions = 
            new XMLSecurityConstants.Action[]{XMLSecurityConstants.SIGNATURE};
        properties.setOutAction(actions);
        properties.loadSignatureKeyStore(
            this.getClass().getClassLoader().getResource("transmitter.jks"), "default".toCharArray()
        );
        properties.setSignatureUser("transmitter");
        properties.setSignatureCanonicalizationAlgorithm("http://www.w3.org/2006/12/xml-c14n11");
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Content);
        properties.addSignaturePart(securePart);
        
        // Set the key up
        Key key = properties.getSignatureCrypto().getPrivateKey("transmitter", "default");
        properties.setSignatureKey(key);

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
        
        // Verify using DOM
        CryptoType cryptoType = new CryptoType(CryptoType.TYPE.ALIAS);
        cryptoType.setAlias(properties.getSignatureUser());
        X509Certificate[] x509Certificates = properties.getSignatureCrypto().getX509Certificates(cryptoType);
        verifyUsingDOM(document, x509Certificates[0], properties.getSignatureSecureParts());
    }


    /**
     * Verify the document using DOM
     */
    private void verifyUsingDOM(
        Document document,
        X509Certificate cert,
        List<SecurePart> secureParts
    ) throws Exception {
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());

        String expression = "//dsig:Signature[1]";
        Element sigElement = 
            (Element) xpath.evaluate(expression, document, XPathConstants.NODE);
        Assert.assertNotNull(sigElement);
        
        for (SecurePart securePart : secureParts) {
            expression = "//*[local-name()='" + securePart.getName().getLocalPart() + "']";
            Element signedElement = 
                (Element)xpath.evaluate(expression, document, XPathConstants.NODE);
            Assert.assertNotNull(signedElement);
            signedElement.setIdAttributeNS(null, "Id", true);
        }
        
        XMLSignature signature = new XMLSignature(sigElement, "");
        KeyInfo ki = signature.getKeyInfo();
        Assert.assertNotNull(ki);

        Assert.assertTrue(signature.checkSignatureValue(cert));
    }
    
    /**
     * Verify the document using DOM
     */
    private void verifyUsingDOM(
        Document document,
        SecretKey secretKey,
        List<SecurePart> secureParts
    ) throws Exception {
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());

        String expression = "//dsig:Signature[1]";
        Element sigElement = 
            (Element) xpath.evaluate(expression, document, XPathConstants.NODE);
        Assert.assertNotNull(sigElement);
        
        for (SecurePart securePart : secureParts) {
            expression = "//*[local-name()='" + securePart.getName().getLocalPart() + "']";
            Element signedElement = 
                (Element)xpath.evaluate(expression, document, XPathConstants.NODE);
            Assert.assertNotNull(signedElement);
            signedElement.setIdAttributeNS(null, "Id", true);
        }
        
        XMLSignature signature = new XMLSignature(sigElement, "");
        Assert.assertTrue(signature.checkSignatureValue(secretKey));
    }

}
