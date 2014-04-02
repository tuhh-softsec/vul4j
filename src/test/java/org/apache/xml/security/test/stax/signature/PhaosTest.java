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

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.Init;
import org.apache.xml.security.stax.ext.InboundXMLSec;
import org.apache.xml.security.stax.ext.XMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.impl.resourceResolvers.ResolverHttp;
import org.apache.xml.security.test.stax.utils.HttpRequestRedirectorProxy;
import org.apache.xml.security.test.stax.utils.StAX2DOM;
import org.apache.xml.security.test.stax.utils.TestUtils;
import org.apache.xml.security.test.stax.utils.XMLSecEventAllocator;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.implementations.ResolverDirectHTTP;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * This is a testcase to validate all "phaos-xmldsig-three"
 * testcases from Phaos
 */
public class PhaosTest extends org.junit.Assert {

    private XMLInputFactory xmlInputFactory;
    private TransformerFactory transformerFactory = TransformerFactory.newInstance();

    @Before
    public void setUp() throws Exception {
        Init.init(PhaosTest.class.getClassLoader().getResource("security-config.xml").toURI(),
                this.getClass());
        org.apache.xml.security.Init.init();

        xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setEventAllocator(new XMLSecEventAllocator());
    }


    // See SANTUARIO-319
    @Test
    public void test_signature_dsa_detached() throws Exception {

        Proxy proxy = HttpRequestRedirectorProxy.startHttpEngine();

        try {
            ResolverHttp.setProxy(proxy);

            ResolverDirectHTTP resolverDirectHTTP = new ResolverDirectHTTP();
            resolverDirectHTTP.engineSetProperty("http.proxy.host", ((InetSocketAddress) proxy.address()).getAddress().getHostAddress());
            resolverDirectHTTP.engineSetProperty("http.proxy.port", "" + ((InetSocketAddress) proxy.address()).getPort());

            TestUtils.switchAllowNotSameDocumentReferences(true);

            // Read in plaintext document
            InputStream sourceDocument =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "com/phaos/phaos-xmldsig-three/signature-dsa-detached.xml");
            DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
            Document document = builder.parse(sourceDocument);

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

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    // See Santuario-320
    @Test
    public void test_signature_dsa_enveloped() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "com/phaos/phaos-xmldsig-three/signature-dsa-enveloped.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

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

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }

    @Test
    public void test_signature_dsa_enveloping() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "com/phaos/phaos-xmldsig-three/signature-dsa-enveloping.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

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

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }

    // See SANTUARIO-319
    @Test
    public void test_signature_hmac_sha1_exclusive_c14n_comments_detached() throws Exception {

        Proxy proxy = HttpRequestRedirectorProxy.startHttpEngine();

        try {
            ResolverHttp.setProxy(proxy);

            ResolverDirectHTTP resolverDirectHTTP = new ResolverDirectHTTP();
            resolverDirectHTTP.engineSetProperty("http.proxy.host", ((InetSocketAddress) proxy.address()).getAddress().getHostAddress());
            resolverDirectHTTP.engineSetProperty("http.proxy.port", "" + ((InetSocketAddress) proxy.address()).getPort());

            TestUtils.switchAllowNotSameDocumentReferences(true);

            // Read in plaintext document
            InputStream sourceDocument =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "com/phaos/phaos-xmldsig-three/signature-hmac-sha1-exclusive-c14n-comments-detached.xml");
            DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
            Document document = builder.parse(sourceDocument);

            // Set up the key
            byte[] hmacKey = "test".getBytes("ASCII");
            SecretKey key = new SecretKeySpec(hmacKey, "http://www.w3.org/2000/09/xmldsig#hmac-sha1");

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

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    // See Santuario-320
    @Test
    public void test_signature_hmac_sha1_exclusive_c14n_enveloped() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "com/phaos/phaos-xmldsig-three/signature-hmac-sha1-exclusive-c14n-enveloped.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        // Set up the key
        byte[] hmacKey = "test".getBytes("ASCII");
        SecretKey key = new SecretKeySpec(hmacKey, "http://www.w3.org/2000/09/xmldsig#hmac-sha1");

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

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }

    @Test
    public void test_signature_rsa_detached_b64_transform() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "com/phaos/phaos-xmldsig-three/signature-rsa-detached-b64-transform.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        // Set up the key
        byte[] hmacKey = "test".getBytes("ASCII");
        SecretKey key = new SecretKeySpec(hmacKey, "http://www.w3.org/2000/09/xmldsig#hmac-sha1");

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

        try {
            TestUtils.switchDoNotThrowExceptionForManifests(true);
            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
        } finally {
            TestUtils.switchDoNotThrowExceptionForManifests(false);
        }
    }

    // See SANTUARIO-319
    @Test
    public void test_signature_rsa_detached() throws Exception {

        Proxy proxy = HttpRequestRedirectorProxy.startHttpEngine();

        try {
            ResolverHttp.setProxy(proxy);

            ResolverDirectHTTP resolverDirectHTTP = new ResolverDirectHTTP();
            resolverDirectHTTP.engineSetProperty("http.proxy.host", ((InetSocketAddress) proxy.address()).getAddress().getHostAddress());
            resolverDirectHTTP.engineSetProperty("http.proxy.port", "" + ((InetSocketAddress) proxy.address()).getPort());

            TestUtils.switchAllowNotSameDocumentReferences(true);

            // Read in plaintext document
            InputStream sourceDocument =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "com/phaos/phaos-xmldsig-three/signature-rsa-detached.xml");
            DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
            Document document = builder.parse(sourceDocument);

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

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    // See Santuario-320
    @Test
    public void test_signature_rsa_enveloped_bad_digest_val() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "com/phaos/phaos-xmldsig-three/signature-rsa-enveloped-bad-digest-val.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

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
        XMLStreamReader securityStreamReader = inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);
        try {
            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
            fail("Failure expected on a bad digest");
        } catch (XMLStreamException ex) {
            Assert.assertTrue(ex.getCause() instanceof XMLSecurityException);
            Assert.assertEquals("INVALID signature -- core validation failed.", ex.getCause().getMessage());
        }
    }

    // See Santuario-320
    @Test
    public void test_signature_rsa_enveloped() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "com/phaos/phaos-xmldsig-three/signature-rsa-enveloped.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

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

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }

    @Test
    public void test_signature_rsa_enveloping() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "com/phaos/phaos-xmldsig-three/signature-rsa-enveloping.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

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

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }

}
