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
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.Init;
import org.apache.xml.security.stax.ext.InboundXMLSec;
import org.apache.xml.security.stax.ext.XMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.impl.resourceResolvers.ResolverHttp;
import org.apache.xml.security.stax.impl.securityToken.KeyNameSecurityToken;
import org.apache.xml.security.stax.impl.securityToken.X509IssuerSerialSecurityToken;
import org.apache.xml.security.stax.impl.securityToken.X509SecurityToken;
import org.apache.xml.security.stax.impl.securityToken.X509SubjectNameSecurityToken;
import org.apache.xml.security.stax.securityEvent.DefaultTokenSecurityEvent;
import org.apache.xml.security.stax.securityEvent.KeyNameTokenSecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEventConstants;
import org.apache.xml.security.stax.securityEvent.X509TokenSecurityEvent;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
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

/**
 * This test is to ensure interoperability with the examples provided by Merlin Huges
 * from Baltimore using KeyTools XML. These test vectors are located in the directory
 * <CODE>data/ie/baltimore/merlin-examples/</CODE>.
 */
public class BaltimoreTest extends org.junit.Assert {

    // Define the Keys
    private static final String DSA_Y_15 =
            "115203950979070769597657569663716864200265690506131586710935599127492300629516054244303718491264700507050531664369568983959612733142807939657310552030830047581310927762782113239508532964275507679031853214612302688824126378201645385366744606832999486491789495152132626085258574701982291168980000698378860886873";
    private static final String DSA_P_15 =
            "121033250678995538022022124214072656199829294480692899035850141795699542545378201726193497344869641617273048303017322131526064041655960510339688462254212325543249651574552121248151721671060674713763786027111260872491665074056568193597793098934224508869545165272139556565195175269711053169607670995531444433119";
    private static final String DSA_Q_15 =
            "823717057890271871604122239238242495719695602139";
    private static final String DSA_G_15 =
            "113709439310240579695634427940532553339153309050965923053944743350349492610217223233554103433240275709268290855590822451558081648385087332128571652948927700423837483999268638666380469524185563535721798130108422578679112065013488826955558565315260470798091784763365661341500996656179557254055807134339468077740";

    private static final String DSA_Y_23 =
            "80026536890415588226993967074802366345532111071996614329958273726987312793428976563150962820221958657035350883783333114440594283158882947014404379349024727056511062396105922288543622677857064953798194192629372470477877685764866927778171632085895970792043576543410064885269033444006824418538675423762259516373";
    private static final String DSA_P_23 =
            "155815845848136428348513787535580769292891787794606906010809242781927942460027924470889128454634145310504964035836740413935833768776146975366062988606794626729218709107639643307263523837539818364822163124440107294709050662439623469573847261172409545075745852010154062349090119229706651663921501121076348530353";
    private static final String DSA_Q_23 =
            "754720990747214486912441906510972542486475779877";
    private static final String DSA_G_23 =
            "111292836411933361753715009819562115824198729809679289876790990949014814123080630068100046684028425631677048464500864615731436519223776555031901385958247163146625548407735868798008172684224658036609181933662459888361728882309490715627267690599122521795008079205873131205682998421914439581619661363918474561824";

    private static final String RSA_MOD_15 =
            "119329599520775465924022606372296074200605535144668022371076132699687141255709737174045123732723708369916816841003864525367987020386846259527839447764154836329045089822539554380766373753731213041983461774133690352071525882671690061465545291631004133563847076169228588340351767773527127920590711638729665138889";
    public static final String RSA_MOD_23 =
            "120296660343377233375194603471583429595399323282961590789247283561085360125953681886041302745068082823944886660427610854189129731366720128135385763222506487464694459023332289451444021068559820208783293910859489936002104550722931306546063549539810916052250046814103434685758992623250037867084412382118349636279";
    private static final String RSA_PUB =
            "65537";

    private XMLInputFactory xmlInputFactory;
    private TransformerFactory transformerFactory = TransformerFactory.newInstance();

    @Before
    public void setUp() throws Exception {
        Init.init(BaltimoreTest.class.getClassLoader().getResource("security-config.xml").toURI(),
                this.getClass());
        org.apache.xml.security.Init.init();

        xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setEventAllocator(new XMLSecEventAllocator());
    }


    @Test
    public void test_fifteen_enveloping_hmac_sha1() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloping-hmac-sha1.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        // Set up the Key
        byte[] hmacKey = "secret".getBytes("ASCII");
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

        document = StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);

        // Check the SecurityEvents
        checkSignatureToken(securityEventListener, key,
                SecurityTokenConstants.KeyIdentifier_NoKeyInfo);
    }

    @Test
    public void test_fifteen_enveloping_hmac_sha1_40() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloping-hmac-sha1-40.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        // Set up the Key
        byte[] hmacKey = "secret".getBytes("ASCII");
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
        XMLStreamReader securityStreamReader = inboundXMLSec.processInMessage(xmlStreamReader);

        try {
            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
            fail("Failure expected on a short HMAC length");
        } catch (XMLStreamException ex) {
            Assert.assertTrue(ex.getCause() instanceof XMLSecurityException);
            Assert.assertEquals("INVALID signature -- core validation failed.", ex.getCause().getMessage());
        }
    }

    // See SANTUARIO-320
    @Test
    public void test_fifteen_enveloped_dsa() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloped-dsa.xml");
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

        // Check the SecurityEvents
        checkSignatureToken(securityEventListener, getPublicKey("DSA", 15),
                SecurityTokenConstants.KeyIdentifier_KeyValue);
    }

    @Test
    public void test_fifteen_enveloping_b64_dsa() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloping-b64-dsa.xml");
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

        // Check the SecurityEvents
        checkSignatureToken(securityEventListener, getPublicKey("DSA", 15),
                SecurityTokenConstants.KeyIdentifier_KeyValue);
    }

    @Test
    public void test_fifteen_enveloping_dsa() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloping-dsa.xml");
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

        // Check the SecurityEvents
        checkSignatureToken(securityEventListener, getPublicKey("DSA", 15),
                SecurityTokenConstants.KeyIdentifier_KeyValue);
    }

    @Test
    public void test_fifteen_enveloping_rsa() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloping-rsa.xml");
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

        // Check the SecurityEvents
        checkSignatureToken(securityEventListener, getPublicKey("RSA", 15),
                SecurityTokenConstants.KeyIdentifier_KeyValue);
    }

    // See SANTUARIO-319
    @Test
    public void test_fifteen_external_b64_dsa() throws Exception {

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
                            "ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-external-b64-dsa.xml");
            DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
            Document document = builder.parse(sourceDocument);

            // Set up the Key
            Key publicKey = getPublicKey("DSA", 15);

            // XMLUtils.outputDOM(document, System.out);

            // Convert Document to a Stream Reader
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(document), new StreamResult(baos));
            final XMLStreamReader xmlStreamReader =
                    xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));

            // Verify signature
            XMLSecurityProperties properties = new XMLSecurityProperties();
            properties.setSignatureVerificationKey(publicKey);
            InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
            TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
            XMLStreamReader securityStreamReader =
                    inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
            
            // Check the SecurityEvents
            checkSignatureToken(securityEventListener, getPublicKey("DSA", 15),
                    SecurityTokenConstants.KeyIdentifier_KeyValue);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    // See SANTUARIO-319
    @Test
    public void test_fifteen_external_dsa() throws Exception {

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
                            "ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-external-dsa.xml");
            DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
            Document document = builder.parse(sourceDocument);

            // Set up the Key
            Key publicKey = getPublicKey("DSA", 15);

            // XMLUtils.outputDOM(document, System.out);

            // Convert Document to a Stream Reader
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(document), new StreamResult(baos));
            final XMLStreamReader xmlStreamReader =
                    xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));

            // Verify signature
            XMLSecurityProperties properties = new XMLSecurityProperties();
            properties.setSignatureVerificationKey(publicKey);
            InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
            TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
            XMLStreamReader securityStreamReader =
                    inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
            
            // Check the SecurityEvents
            checkSignatureToken(securityEventListener, getPublicKey("DSA", 15),
                    SecurityTokenConstants.KeyIdentifier_KeyValue);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    @Test
    public void test_twenty_three_enveloping_hmac_sha1() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/signature-enveloping-hmac-sha1.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        // Set up the Key
        byte[] hmacKey = "secret".getBytes("ASCII");
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

        document = StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);

        // Check the SecurityEvents
        checkSignatureToken(securityEventListener, key,
                SecurityTokenConstants.KeyIdentifier_NoKeyInfo);
    }

    @Test
    public void test_twenty_three_enveloping_hmac_sha1_40() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/signature-enveloping-hmac-sha1-40.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        // Set up the Key
        byte[] hmacKey = "secret".getBytes("ASCII");
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
        XMLStreamReader securityStreamReader = inboundXMLSec.processInMessage(xmlStreamReader);

        try {
            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
            fail("Failure expected on a short HMAC length");
        } catch (XMLStreamException ex) {
            Assert.assertTrue(ex.getCause() instanceof XMLSecurityException);
            Assert.assertEquals("INVALID signature -- core validation failed.", ex.getCause().getMessage());
        }
    }

    // See SANTUARIO-320
    @Test
    public void test_twenty_three_enveloped_dsa() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/signature-enveloped-dsa.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        // Set up the Key
        Key publicKey = getPublicKey("DSA", 23);

        // XMLUtils.outputDOM(document, System.out);

        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader =
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));

        // Verify signature
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setSignatureVerificationKey(publicKey);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader =
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
        
        // Check the SecurityEvents
        checkSignatureToken(securityEventListener, getPublicKey("DSA", 23),
                SecurityTokenConstants.KeyIdentifier_KeyValue);
    }

    @Test
    public void test_twenty_three_enveloping_b64_dsa() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/signature-enveloping-b64-dsa.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);

        // Set up the Key
        Key publicKey = getPublicKey("DSA", 23);

        // XMLUtils.outputDOM(document, System.out);

        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader =
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));

        // Verify signature
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setSignatureVerificationKey(publicKey);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader =
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
        
        // Check the SecurityEvents
        checkSignatureToken(securityEventListener, getPublicKey("DSA", 23),
                SecurityTokenConstants.KeyIdentifier_KeyValue);
    }

    @Test
    public void test_twenty_three_enveloping_dsa() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/signature-enveloping-dsa.xml");
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

        // Check the SecurityEvents
        checkSignatureToken(securityEventListener, getPublicKey("DSA", 23),
                SecurityTokenConstants.KeyIdentifier_KeyValue);
    }

    @Test
    public void test_twenty_three_enveloping_rsa() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/signature-enveloping-rsa.xml");
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

        // Check the SecurityEvents
        checkSignatureToken(securityEventListener, getPublicKey("RSA", 23),
                SecurityTokenConstants.KeyIdentifier_KeyValue);
    }

    // See Santuario-319
    @Test
    public void test_twenty_three_external_b64_dsa() throws Exception {

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
                            "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/signature-external-b64-dsa.xml");
            DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
            Document document = builder.parse(sourceDocument);

            // Set up the Key
            Key publicKey = getPublicKey("DSA", 23);

            // XMLUtils.outputDOM(document, System.out);

            // Convert Document to a Stream Reader
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(document), new StreamResult(baos));
            final XMLStreamReader xmlStreamReader =
                    xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));

            // Verify signature
            XMLSecurityProperties properties = new XMLSecurityProperties();
            properties.setSignatureVerificationKey(publicKey);
            InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
            TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
            XMLStreamReader securityStreamReader =
                    inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
            
            // Check the SecurityEvents
            checkSignatureToken(securityEventListener, getPublicKey("RSA", 23),
                    SecurityTokenConstants.KeyIdentifier_KeyValue);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    // See SANTUARIO-319
    @Test
    public void test_twenty_three_external_dsa() throws Exception {

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
                            "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/signature-external-dsa.xml");
            DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
            Document document = builder.parse(sourceDocument);

            // Set up the Key
            Key publicKey = getPublicKey("DSA", 23);

            // XMLUtils.outputDOM(document, System.out);

            // Convert Document to a Stream Reader
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(document), new StreamResult(baos));
            final XMLStreamReader xmlStreamReader =
                    xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));

            // Verify signature
            XMLSecurityProperties properties = new XMLSecurityProperties();
            properties.setSignatureVerificationKey(publicKey);
            InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
            TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
            XMLStreamReader securityStreamReader =
                    inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
            
            // Check the SecurityEvents
            checkSignatureToken(securityEventListener, getPublicKey("RSA", 23),
                    SecurityTokenConstants.KeyIdentifier_KeyValue);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    // See SANTUARIO-319
    @Test
    public void test_signature_keyname() throws Exception {

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
                            "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/signature-keyname.xml");
            DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
            Document document = builder.parse(sourceDocument);

            // Set up the Key
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            InputStream sourceCert =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/certs/lugh.crt");

            Certificate cert = cf.generateCertificate(sourceCert);

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

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
            
            // Check the SecurityEvents
            checkSignatureToken(securityEventListener, cert.getPublicKey(),
                    SecurityTokenConstants.KeyIdentifier_KeyName);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    // See SANTUARIO-319
    @Test
    public void test_signature_retrievalmethod_rawx509crt() throws Exception {

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
                            "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/signature-retrievalmethod-rawx509crt.xml");
            DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
            Document document = builder.parse(sourceDocument);

            // Set up the Key
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            InputStream sourceCert =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/certs/balor.crt");

            Certificate cert = cf.generateCertificate(sourceCert);

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
            XMLStreamReader securityStreamReader = inboundXMLSec.processInMessage(xmlStreamReader);

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    // See SANTUARIO-319
    @Test
    public void test_signature_x509_crt_crl() throws Exception {

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
                            "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/signature-x509-crt-crl.xml");
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
            XMLStreamReader securityStreamReader = inboundXMLSec.processInMessage(xmlStreamReader);

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    // See SANTUARIO-319
    @Test
    public void test_signature_x509_crt() throws Exception {

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
                            "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/signature-x509-crt.xml");
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
            XMLStreamReader securityStreamReader = inboundXMLSec.processInMessage(xmlStreamReader);

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    // See SANTUARIO-319
    @Test
    public void test_signature_x509_is() throws Exception {

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
                            "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/signature-x509-is.xml");
            DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
            Document document = builder.parse(sourceDocument);

            // XMLUtils.outputDOM(document, System.out);

            // Set up the Key
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            InputStream sourceCert =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/certs/macha.crt");

            Certificate cert = cf.generateCertificate(sourceCert);
            
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

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
            
            // Check the SecurityEvents
            checkSignatureToken(securityEventListener, cert.getPublicKey(),
                    SecurityTokenConstants.KeyIdentifier_IssuerSerial);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    // See SANTUARIO-319
    @Test
    public void test_signature_x509_ski() throws Exception {

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
                            "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/signature-x509-ski.xml");
            DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
            Document document = builder.parse(sourceDocument);

            // XMLUtils.outputDOM(document, System.out);

            // Set up the Key
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            InputStream sourceCert =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/certs/nemain.crt");
            
            Certificate cert = cf.generateCertificate(sourceCert);

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

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    // See SANTUARIO-319
    @Test
    public void test_signature_x509_sn() throws Exception {

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
                            "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/signature-x509-sn.xml");
            DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
            Document document = builder.parse(sourceDocument);

            // XMLUtils.outputDOM(document, System.out);

            // Set up the Key
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            InputStream sourceCert =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/certs/badb.crt");
            
            Certificate cert = cf.generateCertificate(sourceCert);

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

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
            
            // Check the SecurityEvents
            checkSignatureToken(securityEventListener, cert.getPublicKey(),
                    SecurityTokenConstants.KeyIdentifier_X509SubjectName);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    @Test
    public void testExcSignature() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-exc-c14n-one/exc-signature.xml");
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
        XMLStreamReader securityStreamReader = inboundXMLSec.processInMessage(xmlStreamReader);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void test_signature_keyname_18() throws Exception {

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
                            "ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-keyname.xml");
            DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
            Document document = builder.parse(sourceDocument);

            // Set up the Key
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            InputStream sourceCert =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/certs/lugh.crt");

            Certificate cert = cf.generateCertificate(sourceCert);

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

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
            
            // Check the SecurityEvents
            checkSignatureToken(securityEventListener, cert.getPublicKey(),
                    SecurityTokenConstants.KeyIdentifier_KeyName);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

 // See SANTUARIO-319
    @Test
    public void test_signature_retrievalmethod_rawx509crt_18() throws Exception {

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
                            "ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-retrievalmethod-rawx509crt.xml");
            DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
            Document document = builder.parse(sourceDocument);

            // Set up the Key
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            InputStream sourceCert =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/certs/balor.crt");

            Certificate cert = cf.generateCertificate(sourceCert);

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
            XMLStreamReader securityStreamReader = inboundXMLSec.processInMessage(xmlStreamReader);

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    // See SANTUARIO-319
    @Test
    public void test_signature_x509_crt_crl_18() throws Exception {

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
                            "ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-x509-crt-crl.xml");
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
            XMLStreamReader securityStreamReader = inboundXMLSec.processInMessage(xmlStreamReader);

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    // See SANTUARIO-319
    @Test
    public void test_signature_x509_crt_18() throws Exception {

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
                            "ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-x509-crt.xml");
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
            XMLStreamReader securityStreamReader = inboundXMLSec.processInMessage(xmlStreamReader);

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    // See SANTUARIO-319
    @Test
    public void test_signature_x509_is_18() throws Exception {

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
                            "ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-x509-is.xml");
            DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
            Document document = builder.parse(sourceDocument);

            // XMLUtils.outputDOM(document, System.out);

            // Set up the Key
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            InputStream sourceCert =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/certs/macha.crt");

            Certificate cert = cf.generateCertificate(sourceCert);
            
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

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
            
            // Check the SecurityEvents
            checkSignatureToken(securityEventListener, cert.getPublicKey(),
                    SecurityTokenConstants.KeyIdentifier_IssuerSerial);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    // See SANTUARIO-319
    @Test
    public void test_signature_x509_ski_18() throws Exception {

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
                            "ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-x509-ski.xml");
            DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
            Document document = builder.parse(sourceDocument);

            // XMLUtils.outputDOM(document, System.out);

            // Set up the Key
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            InputStream sourceCert =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/certs/nemain.crt");
            
            Certificate cert = cf.generateCertificate(sourceCert);

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

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }

    // See SANTUARIO-319
    @Test
    public void test_signature_x509_sn_18() throws Exception {

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
                            "ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-x509-sn.xml");
            DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
            Document document = builder.parse(sourceDocument);

            // XMLUtils.outputDOM(document, System.out);

            // Set up the Key
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            InputStream sourceCert =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/certs/badb.crt");
            
            Certificate cert = cf.generateCertificate(sourceCert);

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

            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
            
            // Check the SecurityEvents
            checkSignatureToken(securityEventListener, cert.getPublicKey(),
                    SecurityTokenConstants.KeyIdentifier_X509SubjectName);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
            HttpRequestRedirectorProxy.stopHttpEngine();
        }
    }


    private static PublicKey getPublicKey(String algo, int number)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory kf = KeyFactory.getInstance(algo);
        KeySpec kspec = null;
        if (algo.equalsIgnoreCase("DSA")) {
            if (number == 15) {
                kspec = new DSAPublicKeySpec(new BigInteger(DSA_Y_15),
                        new BigInteger(DSA_P_15),
                        new BigInteger(DSA_Q_15),
                        new BigInteger(DSA_G_15));
            } else if (number == 23) {
                kspec = new DSAPublicKeySpec(new BigInteger(DSA_Y_23),
                        new BigInteger(DSA_P_23),
                        new BigInteger(DSA_Q_23),
                        new BigInteger(DSA_G_23));
            }
        } else if (algo.equalsIgnoreCase("RSA")) {
            if (number == 15) {
                kspec = new RSAPublicKeySpec(new BigInteger(RSA_MOD_15),
                        new BigInteger(RSA_PUB));
            } else if (number == 23) {
                kspec = new RSAPublicKeySpec(new BigInteger(RSA_MOD_23),
                        new BigInteger(RSA_PUB));
            }
        } else {
            throw new RuntimeException("Unsupported key algorithm " + algo);
        }
        return kf.generatePublic(kspec);
    }

    private void checkSignatureToken(
            TestSecurityEventListener securityEventListener,
            Key key,
            SecurityTokenConstants.KeyIdentifier keyIdentifier
    ) throws XMLSecurityException {
        if (SecurityTokenConstants.KeyIdentifier_KeyValue.equals(keyIdentifier)) {

        } else if (SecurityTokenConstants.KeyIdentifier_NoKeyInfo.equals(keyIdentifier)) {
            DefaultTokenSecurityEvent tokenEvent =
                    (DefaultTokenSecurityEvent) securityEventListener.getSecurityEvent(SecurityEventConstants.DefaultToken);
            assertNotNull(tokenEvent);
            Key processedKey = tokenEvent.getSecurityToken().getSecretKey().values().iterator().next();
            assertEquals(processedKey, key);
        } else if (SecurityTokenConstants.KeyIdentifier_KeyName.equals(keyIdentifier)) {
            KeyNameTokenSecurityEvent tokenEvent =
                    (KeyNameTokenSecurityEvent) securityEventListener.getSecurityEvent(SecurityEventConstants.KeyNameToken);
            assertNotNull(tokenEvent);
            if (key instanceof SecretKey) {
                Key processedKey = tokenEvent.getSecurityToken().getSecretKey().values().iterator().next();
                assertEquals(processedKey, key);
            } else {
                Key processedKey = tokenEvent.getSecurityToken().getPublicKey();
                assertEquals(processedKey, key);
            }
            assertNotNull(((KeyNameSecurityToken) tokenEvent.getSecurityToken()).getKeyName());
        } else {
            X509TokenSecurityEvent tokenEvent =
                    (X509TokenSecurityEvent) securityEventListener.getSecurityEvent(SecurityEventConstants.X509Token);
            assertNotNull(tokenEvent);
            X509SecurityToken x509SecurityToken =
                    (X509SecurityToken) tokenEvent.getSecurityToken();
            assertNotNull(x509SecurityToken);
            if (SecurityTokenConstants.KeyIdentifier_X509SubjectName.equals(keyIdentifier)) {
                Key processedKey = x509SecurityToken.getPublicKey();
                assertEquals(processedKey, key);
                assertNotNull(((X509SubjectNameSecurityToken) x509SecurityToken).getSubjectName());
            } else if (SecurityTokenConstants.KeyIdentifier_IssuerSerial.equals(keyIdentifier)) {
                Key processedKey = x509SecurityToken.getPublicKey();
                assertEquals(processedKey, key);
                assertNotNull(((X509IssuerSerialSecurityToken) x509SecurityToken).getIssuerName());
                assertNotNull(((X509IssuerSerialSecurityToken) x509SecurityToken).getSerialNumber());
            }
        }
    }
}
