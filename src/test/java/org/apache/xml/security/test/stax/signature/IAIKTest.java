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
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
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
import javax.xml.transform.stream.StreamSource;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.Init;
import org.apache.xml.security.stax.ext.InboundXMLSec;
import org.apache.xml.security.stax.ext.XMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.impl.securityToken.KeyNameSecurityToken;
import org.apache.xml.security.stax.impl.securityToken.X509IssuerSerialSecurityToken;
import org.apache.xml.security.stax.impl.securityToken.X509SecurityToken;
import org.apache.xml.security.stax.impl.securityToken.X509SubjectNameSecurityToken;
import org.apache.xml.security.stax.securityEvent.DefaultTokenSecurityEvent;
import org.apache.xml.security.stax.securityEvent.KeyNameTokenSecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEventConstants;
import org.apache.xml.security.stax.securityEvent.X509TokenSecurityEvent;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.test.stax.utils.StAX2DOM;
import org.apache.xml.security.test.stax.utils.TestUtils;
import org.apache.xml.security.test.stax.utils.XMLSecEventAllocator;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * This test is to ensure interoperability with the examples provided by the IAIK
 * XML Signature implementation. Thanks to Gregor Karlinger who provided these
 * test vectors. They are located in the directory <CODE>data/at/iaik/ixsil/</CODE>.
 */
public class IAIKTest extends org.junit.Assert {
    
    // Define the Keys
    private static final String DSA_Y = 
            "33765117117947274661410382382650381161343617353664210170104406353610701044610078240124960165589268013959628883158481521066490826259260800878347905093229352801096566573507150438307560047568318507187154066482564350264253492725510108330786058643267447097509233135065057837400865193836500518383366090134577741053";
    private static final String DSA_P = 
            "91634265413269728335373456840902298947347457680573446480385884712203252882476860316549099567586720335306748578940814977769093940974266715233397005957274714637390846659304524279579796384844387472915589310715455237001400834751102257352922064898227481939437670342534515495271294497038496656824770631295812638999";
    private static final String DSA_Q = 
            "1429042367422631366787309673414805238266287675163";
    private static final String DSA_G = 
            "55996752437939033808848513898546387171938363874894496914563143236312388388575433783546897866725079197988900114055877651265845210275099560192808554894952746896447422004598952101382809226581856515647962078133491799837520059128557664983865646964858235956075258101815411978037059289614937207339691043148996572947";
    
    private static final String RSA_MOD = 
            "123741519167989388559377626745542702486926628431631931688706922056140679850039257167520167484412112276535334078519003803614712993739893643126140460918237455879023461779027296599477635539211426788386258873478147007239191180574000289143927884425647619290073015083375160571949522764083669597074190296532088216887";
    private static final String RSA_PUB = 
            "3";
    
    private XMLInputFactory xmlInputFactory;
    private TransformerFactory transformerFactory = TransformerFactory.newInstance();
    
    @Before
    public void setUp() throws Exception {
        Init.init(IAIKTest.class.getClassLoader().getResource("security-config.xml").toURI(),
                this.getClass());
        org.apache.xml.security.Init.init();
        
        xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setEventAllocator(new XMLSecEventAllocator());
    }
    

    @Test
    public void test_signatureAlgorithms_signatures_hMACSignature() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "at/iaik/ixsil/signatureAlgorithms/signatures/hMACSignature.xml");
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

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
        
        // Check the SecurityEvents
        checkSignatureToken(securityEventListener, key, SecurityTokenConstants.KeyIdentifier_NoKeyInfo);
    }
    
    @Test
    public void test_signatureAlgorithms_signatures_hMACShortSignature() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "at/iaik/ixsil/signatureAlgorithms/signatures/hMACShortSignature.xml");
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
    
    @Test
    public void test_signatureAlgorithms_signatures_dSASignature() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "at/iaik/ixsil/signatureAlgorithms/signatures/dSASignature.xml");
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
        checkSignatureToken(securityEventListener, getPublicKey("DSA"),
                              SecurityTokenConstants.KeyIdentifier_KeyValue);
    }
    
    @Test
    public void test_signatureAlgorithms_signatures_rSASignature() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "at/iaik/ixsil/signatureAlgorithms/signatures/rSASignature.xml");
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
        checkSignatureToken(securityEventListener, getPublicKey("RSA"),
                            SecurityTokenConstants.KeyIdentifier_KeyValue);
    }    
    
    // See SANTUARIO-322
    @Test
    public void test_transforms_signatures_base64DecodeSignature() throws Exception {
        // Set up the Key
        Key publicKey = getPublicKey("RSA");

        final XMLStreamReader xmlStreamReader =
                xmlInputFactory.createXMLStreamReader(
                        new StreamSource(
                                this.getClass().getClassLoader().getResource(
                                        "at/iaik/ixsil/transforms/signatures/base64DecodeSignature.xml").toExternalForm()
                        )
                );
  
        // Verify signature
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setSignatureVerificationKey(publicKey);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
            inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        try {
            TestUtils.switchAllowNotSameDocumentReferences(true);
            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
        }
        
        // Check the SecurityEvents
        checkSignatureToken(securityEventListener, getPublicKey("RSA"),
                            SecurityTokenConstants.KeyIdentifier_KeyValue);
    }    
    
    // See SANTUARIO-322
    @Test
    public void test_transforms_signatures_c14nSignature() throws Exception {
        // Set up the Key
        Key publicKey = getPublicKey("RSA");
        
        final XMLStreamReader xmlStreamReader =
                xmlInputFactory.createXMLStreamReader(
                        new StreamSource(
                                this.getClass().getClassLoader().getResource(
                                        "at/iaik/ixsil/transforms/signatures/c14nSignature.xml").toExternalForm()
                        )
                );
  
        // Verify signature
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setSignatureVerificationKey(publicKey);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
            inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        try {
            TestUtils.switchAllowNotSameDocumentReferences(true);
            StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
        } finally {
            TestUtils.switchAllowNotSameDocumentReferences(false);
        }
        
        // Check the SecurityEvents
        checkSignatureToken(securityEventListener, getPublicKey("RSA"),
                            SecurityTokenConstants.KeyIdentifier_KeyValue);
    }    
    
    @Test
    public void test_transforms_signatures_envelopedSignatureSignature() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "at/iaik/ixsil/transforms/signatures/envelopedSignatureSignature.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        Key publicKey = getPublicKey("RSA");
        
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
        checkSignatureToken(securityEventListener, getPublicKey("RSA"),
                            SecurityTokenConstants.KeyIdentifier_KeyValue);
    }    
    
    private static PublicKey getPublicKey(String algo) 
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory kf = KeyFactory.getInstance(algo);
        KeySpec kspec = null;
        if (algo.equalsIgnoreCase("DSA")) {
            kspec = new DSAPublicKeySpec(new BigInteger(DSA_Y), 
                        new BigInteger(DSA_P), 
                        new BigInteger(DSA_Q), 
                        new BigInteger(DSA_G));
        } else if (algo.equalsIgnoreCase("RSA")) {
            kspec = new RSAPublicKeySpec(new BigInteger(RSA_MOD), 
                    new BigInteger(RSA_PUB));
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
                (DefaultTokenSecurityEvent)securityEventListener.getSecurityEvent(SecurityEventConstants.DefaultToken);
            assertNotNull(tokenEvent);
            Key processedKey = tokenEvent.getSecurityToken().getSecretKey().values().iterator().next();
            assertEquals(processedKey, key);
        } else if (SecurityTokenConstants.KeyIdentifier_KeyName.equals(keyIdentifier)) {
            KeyNameTokenSecurityEvent tokenEvent = 
                (KeyNameTokenSecurityEvent)securityEventListener.getSecurityEvent(SecurityEventConstants.KeyNameToken);
            assertNotNull(tokenEvent);
            Key processedKey = tokenEvent.getSecurityToken().getSecretKey().values().iterator().next();
            assertEquals(processedKey, key);
            assertNotNull(((KeyNameSecurityToken)tokenEvent.getSecurityToken()).getKeyName());
        } else {
            X509TokenSecurityEvent tokenEvent =
                (X509TokenSecurityEvent)securityEventListener.getSecurityEvent(SecurityEventConstants.X509Token);
            assertNotNull(tokenEvent);
            X509SecurityToken x509SecurityToken = 
                (X509SecurityToken)tokenEvent.getSecurityToken();
            assertNotNull(x509SecurityToken);
            if (SecurityTokenConstants.KeyIdentifier_X509SubjectName.equals(keyIdentifier)) {
                Key processedKey = x509SecurityToken.getPublicKey();
                assertEquals(processedKey, key);
                assertNotNull(((X509SubjectNameSecurityToken)x509SecurityToken).getSubjectName());
            } else if (SecurityTokenConstants.KeyIdentifier_IssuerSerial.equals(keyIdentifier)) {
                Key processedKey = x509SecurityToken.getPublicKey();
                assertEquals(processedKey, key);
                assertNotNull(((X509IssuerSerialSecurityToken)x509SecurityToken).getIssuerName());
                assertNotNull(((X509IssuerSerialSecurityToken)x509SecurityToken).getSerialNumber());
            }
        }

    }
}
