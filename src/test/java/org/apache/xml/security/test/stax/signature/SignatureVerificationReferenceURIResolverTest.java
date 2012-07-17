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

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.stax.config.ResourceResolverMapper;
import org.apache.xml.security.stax.ext.InboundXMLSec;
import org.apache.xml.security.stax.ext.ResourceResolver;
import org.apache.xml.security.stax.ext.XMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.impl.resourceResolvers.ResolverHttp;
import org.apache.xml.security.stax.impl.resourceResolvers.ResolverXPointer;
import org.apache.xml.security.test.dom.DSNamespaceContext;
import org.apache.xml.security.test.stax.utils.StAX2DOM;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class SignatureVerificationReferenceURIResolverTest extends AbstractSignatureVerificationTest {

    @Test
    public void testSignatureVerificationWithExternalFilesystemXMLReference() throws Exception {
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
        X509Certificate cert = (X509Certificate) keyStore.getCertificate("transmitter");

        // Sign using DOM
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");

        ReferenceInfo referenceInfo = new ReferenceInfo(
                "file://" + BASEDIR + "/src/test/resources/ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml",
                "http://www.w3.org/2001/10/xml-exc-c14n#",
                "http://www.w3.org/2000/09/xmldsig#sha1",
                false
        );

        List<ReferenceInfo> referenceInfos = new ArrayList<ReferenceInfo>();
        referenceInfos.add(referenceInfo);

        XMLSignature sig = signUsingDOM(
                "http://www.w3.org/2000/09/xmldsig#rsa-sha1",
                document,
                localNames,
                key,
                referenceInfos
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
        properties.setSignatureVerificationKey(cert.getPublicKey());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        XMLStreamReader securityStreamReader = inboundXMLSec.processInMessage(xmlStreamReader);

        StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
    }

    @Test
    public void testSignatureVerificationWithExternalFilesystemBinaryReference() throws Exception {
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
        X509Certificate cert = (X509Certificate) keyStore.getCertificate("transmitter");

        // Sign using DOM
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");

        ReferenceInfo referenceInfo = new ReferenceInfo(
                "file://" + BASEDIR + "/target/test-classes/org/apache/xml/security/test/stax/signature/SignatureVerificationReferenceURIResolverTest.class",
                null,
                "http://www.w3.org/2000/09/xmldsig#sha1",
                true
        );

        List<ReferenceInfo> referenceInfos = new ArrayList<ReferenceInfo>();
        referenceInfos.add(referenceInfo);

        XMLSignature sig = signUsingDOM(
                "http://www.w3.org/2000/09/xmldsig#rsa-sha1",
                document,
                localNames,
                key,
                referenceInfos
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
        properties.setSignatureVerificationKey(cert.getPublicKey());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        XMLStreamReader securityStreamReader = inboundXMLSec.processInMessage(xmlStreamReader);

        StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
    }

    @Test
    @Ignore
    public void testSignatureVerificationWithExternalHttpReference() throws Exception {
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
        X509Certificate cert = (X509Certificate) keyStore.getCertificate("transmitter");

        // Sign using DOM
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");

        ReferenceInfo referenceInfo = new ReferenceInfo(
                "http://www.apache.org/images/feather-small.gif",
                null,
                "http://www.w3.org/2000/09/xmldsig#sha1",
                true
        );

        List<ReferenceInfo> referenceInfos = new ArrayList<ReferenceInfo>();
        referenceInfos.add(referenceInfo);

        XMLSignature sig = signUsingDOM(
                "http://www.w3.org/2000/09/xmldsig#rsa-sha1",
                document,
                localNames,
                key,
                referenceInfos
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
        properties.setSignatureVerificationKey(cert.getPublicKey());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        XMLStreamReader securityStreamReader = inboundXMLSec.processInMessage(xmlStreamReader);

        StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
    }

    @Test
    public void testBasicSignatureVerificationWithExternalHttpReference() throws Exception {
        //for simplification and to prevent online lookups, we just test if the ResolverHttp class is returned.
        //another option would be to start an embedded jetty instance...

        ResourceResolver resourceResolver = ResourceResolverMapper.getResourceResolver("http://schemas.xmlsoap.org/ws/2005/07/securitypolicy/ws-securitypolicy.xsd");
        Assert.assertNotNull(resourceResolver);
        Assert.assertTrue(resourceResolver instanceof ResolverHttp);
    }

    @Test
    public void testSignatureVerificationWithSameDocumentXPointerIdApostropheReference() throws Exception {
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
        X509Certificate cert = (X509Certificate) keyStore.getCertificate("transmitter");

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());

        String expression = "//*[local-name()='ShippingAddress']";
        Element elementToSign =
                (Element) xpath.evaluate(expression, document, XPathConstants.NODE);
        Assert.assertNotNull(elementToSign);
        String id = UUID.randomUUID().toString();
        elementToSign.setAttributeNS(null, "Id", id);
        elementToSign.setIdAttributeNS(null, "Id", true);

        // Sign using DOM
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");

        ReferenceInfo referenceInfo = new ReferenceInfo(
                "#xpointer(id('" + id + "'))",
                "http://www.w3.org/2001/10/xml-exc-c14n#",
                "http://www.w3.org/2000/09/xmldsig#sha1",
                false
        );

        List<ReferenceInfo> referenceInfos = new ArrayList<ReferenceInfo>();
        referenceInfos.add(referenceInfo);

        XMLSignature sig = signUsingDOM(
                "http://www.w3.org/2000/09/xmldsig#rsa-sha1",
                document,
                localNames,
                key,
                referenceInfos
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
        properties.setSignatureVerificationKey(cert.getPublicKey());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        XMLStreamReader securityStreamReader = inboundXMLSec.processInMessage(xmlStreamReader);

        StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
    }

    @Test
    public void testSignatureVerificationWithSameDocumentXPointerIdDoubleQuoteReference() throws Exception {
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
        X509Certificate cert = (X509Certificate) keyStore.getCertificate("transmitter");

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());

        String expression = "//*[local-name()='ShippingAddress']";
        Element elementToSign =
                (Element) xpath.evaluate(expression, document, XPathConstants.NODE);
        Assert.assertNotNull(elementToSign);
        String id = UUID.randomUUID().toString();
        elementToSign.setAttributeNS(null, "Id", id);
        elementToSign.setIdAttributeNS(null, "Id", true);

        // Sign using DOM
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");

        ReferenceInfo referenceInfo = new ReferenceInfo(
                "#xpointer(id(\"" + id + "\"))",
                "http://www.w3.org/2001/10/xml-exc-c14n#",
                "http://www.w3.org/2000/09/xmldsig#sha1",
                false
        );

        List<ReferenceInfo> referenceInfos = new ArrayList<ReferenceInfo>();
        referenceInfos.add(referenceInfo);

        XMLSignature sig = signUsingDOM(
                "http://www.w3.org/2000/09/xmldsig#rsa-sha1",
                document,
                localNames,
                key,
                referenceInfos
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
        properties.setSignatureVerificationKey(cert.getPublicKey());
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        XMLStreamReader securityStreamReader = inboundXMLSec.processInMessage(xmlStreamReader);

        StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
    }

    @Test
    public void testSignatureVerificationWithSameDocumentXPointerSlashReference() throws Exception {
        //todo complete testcase when we support enveloped signatures
        //ATM we just test if the ResolverXPointer matches /

        ResourceResolver resourceResolver = ResourceResolverMapper.getResourceResolver("#xpointer(/)");
        Assert.assertNotNull(resourceResolver);
        Assert.assertTrue(resourceResolver instanceof ResolverXPointer);

        //only the first call to matches must return true:
        Assert.assertTrue(resourceResolver.matches(null));
        Assert.assertFalse(resourceResolver.matches(null));
        Assert.assertFalse(resourceResolver.matches(null));
    }
}
