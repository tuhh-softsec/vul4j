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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.xml.security.stax.ext.OutboundXMLSec;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.XMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.test.stax.utils.XmlReaderToWriter;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * A set of test-cases for Signature creation with various PublicKey algorithms
 */
public class PKSignatureCreationTest extends AbstractSignatureCreationTest {
    
    private KeyPair rsaKeyPair, ecKeyPair;
    
    public PKSignatureCreationTest() throws Exception {
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
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        properties.setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier_KeyValue);
        
        String signatureAlgorithm = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
        properties.setSignatureAlgorithm(signatureAlgorithm);
        properties.setSignatureKey(rsaKeyPair.getPrivate());
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        
        SecurePart securePart = new SecurePart(
                new QName("urn:example:po", "PaymentInfo"),
                SecurePart.Modifier.Content,
                new String[]{"http://www.w3.org/2001/10/xml-exc-c14n#"},
                "http://www.w3.org/2000/09/xmldsig#sha1");
        properties.addSignaturePart(securePart);

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
                XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));

        // Verify using DOM
        verifyUsingDOM(document, rsaKeyPair.getPublic(), properties.getSignatureSecureParts());
    }
    
    @Test
    public void testRSA_SHA256() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        properties.setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier_KeyValue);
        
        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
        properties.setSignatureAlgorithm(signatureAlgorithm);
        properties.setSignatureKey(rsaKeyPair.getPrivate());
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        
        SecurePart securePart = new SecurePart(
                new QName("urn:example:po", "PaymentInfo"),
                SecurePart.Modifier.Content,
                new String[]{"http://www.w3.org/2001/10/xml-exc-c14n#"},
                "http://www.w3.org/2000/09/xmldsig#sha1");
        properties.addSignaturePart(securePart);

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
                XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));

        // Verify using DOM
        verifyUsingDOM(document, rsaKeyPair.getPublic(), properties.getSignatureSecureParts());
    }
    
    @Test
    public void testRSA_SHA384() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        properties.setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier_KeyValue);
        
        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
        properties.setSignatureAlgorithm(signatureAlgorithm);
        properties.setSignatureKey(rsaKeyPair.getPrivate());
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        
        SecurePart securePart = new SecurePart(
                new QName("urn:example:po", "PaymentInfo"),
                SecurePart.Modifier.Content,
                new String[]{"http://www.w3.org/2001/10/xml-exc-c14n#"},
                "http://www.w3.org/2000/09/xmldsig#sha1");
        properties.addSignaturePart(securePart);

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
                XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));

        // Verify using DOM
        verifyUsingDOM(document, rsaKeyPair.getPublic(), properties.getSignatureSecureParts());
    }
    
    @Test
    public void testRSA_SHA512() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        properties.setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier_KeyValue);
        
        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
        properties.setSignatureAlgorithm(signatureAlgorithm);
        properties.setSignatureKey(rsaKeyPair.getPrivate());
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        
        SecurePart securePart = new SecurePart(
                new QName("urn:example:po", "PaymentInfo"),
                SecurePart.Modifier.Content,
                new String[]{"http://www.w3.org/2001/10/xml-exc-c14n#"},
                "http://www.w3.org/2000/09/xmldsig#sha1");
        properties.addSignaturePart(securePart);

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
                XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));

        // Verify using DOM
        verifyUsingDOM(document, rsaKeyPair.getPublic(), properties.getSignatureSecureParts());
    }
    
    @Test
    public void testRSA_RIPEMD160() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        properties.setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier_KeyValue);
        
        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
        properties.setSignatureAlgorithm(signatureAlgorithm);
        properties.setSignatureKey(rsaKeyPair.getPrivate());
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        
        SecurePart securePart = new SecurePart(
                new QName("urn:example:po", "PaymentInfo"),
                SecurePart.Modifier.Content,
                new String[]{"http://www.w3.org/2001/10/xml-exc-c14n#"},
                "http://www.w3.org/2000/09/xmldsig#sha1");
        properties.addSignaturePart(securePart);

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
                XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));

        // Verify using DOM
        verifyUsingDOM(document, rsaKeyPair.getPublic(), properties.getSignatureSecureParts());
    }
    
    @Test
    public void testRSA_SHA1_MGF1() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        properties.setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier_KeyValue);
        
        String signatureAlgorithm = "http://www.w3.org/2007/05/xmldsig-more#sha1-rsa-MGF1";
        properties.setSignatureAlgorithm(signatureAlgorithm);
        properties.setSignatureKey(rsaKeyPair.getPrivate());
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        
        SecurePart securePart = new SecurePart(
                new QName("urn:example:po", "PaymentInfo"),
                SecurePart.Modifier.Content,
                new String[]{"http://www.w3.org/2001/10/xml-exc-c14n#"},
                "http://www.w3.org/2000/09/xmldsig#sha1");
        properties.addSignaturePart(securePart);

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
                XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));

        // Verify using DOM
        verifyUsingDOM(document, rsaKeyPair.getPublic(), properties.getSignatureSecureParts());
    }
    
    @Test
    public void testRSA_SHA224_MGF1() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        properties.setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier_KeyValue);
        
        String signatureAlgorithm = "http://www.w3.org/2007/05/xmldsig-more#sha224-rsa-MGF1";
        properties.setSignatureAlgorithm(signatureAlgorithm);
        properties.setSignatureKey(rsaKeyPair.getPrivate());
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        
        SecurePart securePart = new SecurePart(
                new QName("urn:example:po", "PaymentInfo"),
                SecurePart.Modifier.Content,
                new String[]{"http://www.w3.org/2001/10/xml-exc-c14n#"},
                "http://www.w3.org/2000/09/xmldsig#sha1");
        properties.addSignaturePart(securePart);

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
                XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));

        // Verify using DOM
        verifyUsingDOM(document, rsaKeyPair.getPublic(), properties.getSignatureSecureParts());
    }
    
    @Test
    public void testRSA_SHA256_MGF1() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        properties.setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier_KeyValue);
        
        String signatureAlgorithm = "http://www.w3.org/2007/05/xmldsig-more#sha256-rsa-MGF1";
        properties.setSignatureAlgorithm(signatureAlgorithm);
        properties.setSignatureKey(rsaKeyPair.getPrivate());
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        
        SecurePart securePart = new SecurePart(
                new QName("urn:example:po", "PaymentInfo"),
                SecurePart.Modifier.Content,
                new String[]{"http://www.w3.org/2001/10/xml-exc-c14n#"},
                "http://www.w3.org/2000/09/xmldsig#sha1");
        properties.addSignaturePart(securePart);

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
                XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));

        // Verify using DOM
        verifyUsingDOM(document, rsaKeyPair.getPublic(), properties.getSignatureSecureParts());
    }
    
    @Test
    public void testRSA_SHA384_MGF1() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        properties.setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier_KeyValue);
        
        String signatureAlgorithm = "http://www.w3.org/2007/05/xmldsig-more#sha384-rsa-MGF1";
        properties.setSignatureAlgorithm(signatureAlgorithm);
        properties.setSignatureKey(rsaKeyPair.getPrivate());
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        
        SecurePart securePart = new SecurePart(
                new QName("urn:example:po", "PaymentInfo"),
                SecurePart.Modifier.Content,
                new String[]{"http://www.w3.org/2001/10/xml-exc-c14n#"},
                "http://www.w3.org/2000/09/xmldsig#sha1");
        properties.addSignaturePart(securePart);

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
                XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));

        // Verify using DOM
        verifyUsingDOM(document, rsaKeyPair.getPublic(), properties.getSignatureSecureParts());
    }
    
    @Test
    public void testRSA_SHA512_MGF1() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        properties.setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier_KeyValue);
        
        String signatureAlgorithm = "http://www.w3.org/2007/05/xmldsig-more#sha512-rsa-MGF1";
        properties.setSignatureAlgorithm(signatureAlgorithm);
        properties.setSignatureKey(rsaKeyPair.getPrivate());
        properties.setSignatureVerificationKey(rsaKeyPair.getPublic());
        
        SecurePart securePart = new SecurePart(
                new QName("urn:example:po", "PaymentInfo"),
                SecurePart.Modifier.Content,
                new String[]{"http://www.w3.org/2001/10/xml-exc-c14n#"},
                "http://www.w3.org/2000/09/xmldsig#sha1");
        properties.addSignaturePart(securePart);

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
                XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));

        // Verify using DOM
        verifyUsingDOM(document, rsaKeyPair.getPublic(), properties.getSignatureSecureParts());
    }
    
    @Test
    public void testECDSA_SHA1() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        properties.setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier_KeyValue);
        
        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
        properties.setSignatureAlgorithm(signatureAlgorithm);
        properties.setSignatureKey(ecKeyPair.getPrivate());
        properties.setSignatureVerificationKey(ecKeyPair.getPublic());
        
        SecurePart securePart = new SecurePart(
                new QName("urn:example:po", "PaymentInfo"),
                SecurePart.Modifier.Content,
                new String[]{"http://www.w3.org/2001/10/xml-exc-c14n#"},
                "http://www.w3.org/2000/09/xmldsig#sha1");
        properties.addSignaturePart(securePart);

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
                XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));

        // Verify using DOM
        verifyUsingDOM(document, ecKeyPair.getPublic(), properties.getSignatureSecureParts());
    }
    
    @Test
    public void testECDSA_SHA224() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        properties.setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier_KeyValue);
        
        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224";
        properties.setSignatureAlgorithm(signatureAlgorithm);
        properties.setSignatureKey(ecKeyPair.getPrivate());
        properties.setSignatureVerificationKey(ecKeyPair.getPublic());
        
        SecurePart securePart = new SecurePart(
                new QName("urn:example:po", "PaymentInfo"),
                SecurePart.Modifier.Content,
                new String[]{"http://www.w3.org/2001/10/xml-exc-c14n#"},
                "http://www.w3.org/2000/09/xmldsig#sha1");
        properties.addSignaturePart(securePart);

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
                XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));

        // Verify using DOM
        verifyUsingDOM(document, ecKeyPair.getPublic(), properties.getSignatureSecureParts());
    }
    
    @Test
    public void testECDSA_SHA256() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        properties.setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier_KeyValue);
        
        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
        properties.setSignatureAlgorithm(signatureAlgorithm);
        properties.setSignatureKey(ecKeyPair.getPrivate());
        properties.setSignatureVerificationKey(ecKeyPair.getPublic());
        
        SecurePart securePart = new SecurePart(
                new QName("urn:example:po", "PaymentInfo"),
                SecurePart.Modifier.Content,
                new String[]{"http://www.w3.org/2001/10/xml-exc-c14n#"},
                "http://www.w3.org/2000/09/xmldsig#sha1");
        properties.addSignaturePart(securePart);

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
                XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));

        // Verify using DOM
        verifyUsingDOM(document, ecKeyPair.getPublic(), properties.getSignatureSecureParts());
    }
    
    @Test
    public void testECDSA_SHA384() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        properties.setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier_KeyValue);
        
        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
        properties.setSignatureAlgorithm(signatureAlgorithm);
        properties.setSignatureKey(ecKeyPair.getPrivate());
        properties.setSignatureVerificationKey(ecKeyPair.getPublic());
        
        SecurePart securePart = new SecurePart(
                new QName("urn:example:po", "PaymentInfo"),
                SecurePart.Modifier.Content,
                new String[]{"http://www.w3.org/2001/10/xml-exc-c14n#"},
                "http://www.w3.org/2000/09/xmldsig#sha1");
        properties.addSignaturePart(securePart);

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
                XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));

        // Verify using DOM
        verifyUsingDOM(document, ecKeyPair.getPublic(), properties.getSignatureSecureParts());
    }
    
    @Test
    public void testECDSA_SHA512() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        properties.setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier_KeyValue);
        
        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
        properties.setSignatureAlgorithm(signatureAlgorithm);
        properties.setSignatureKey(ecKeyPair.getPrivate());
        properties.setSignatureVerificationKey(ecKeyPair.getPublic());
        
        SecurePart securePart = new SecurePart(
                new QName("urn:example:po", "PaymentInfo"),
                SecurePart.Modifier.Content,
                new String[]{"http://www.w3.org/2001/10/xml-exc-c14n#"},
                "http://www.w3.org/2000/09/xmldsig#sha1");
        properties.addSignaturePart(securePart);

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
                XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));

        // Verify using DOM
        verifyUsingDOM(document, ecKeyPair.getPublic(), properties.getSignatureSecureParts());
    }
    
    @Test
    public void testECDSA_RIPEMD160() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        properties.setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier_KeyValue);
        
        String signatureAlgorithm = "http://www.w3.org/2007/05/xmldsig-more#ecdsa-ripemd160";
        properties.setSignatureAlgorithm(signatureAlgorithm);
        properties.setSignatureKey(ecKeyPair.getPrivate());
        properties.setSignatureVerificationKey(ecKeyPair.getPublic());
        
        SecurePart securePart = new SecurePart(
                new QName("urn:example:po", "PaymentInfo"),
                SecurePart.Modifier.Content,
                new String[]{"http://www.w3.org/2001/10/xml-exc-c14n#"},
                "http://www.w3.org/2000/09/xmldsig#sha1");
        properties.addSignaturePart(securePart);

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
                XMLUtils.createDocumentBuilder(false).parse(new ByteArrayInputStream(baos.toByteArray()));

        // Verify using DOM
        verifyUsingDOM(document, ecKeyPair.getPublic(), properties.getSignatureSecureParts());
    }
    
    
}
