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
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.xml.security.stax.ext.OutboundXMLSec;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.XMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.test.stax.utils.XmlReaderToWriter;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * A set of test-cases for Signature creation with various HMAC algorithms
 */
public class SignatureHMACCreationTest extends AbstractSignatureCreationTest {
    
    public SignatureHMACCreationTest() throws Exception {
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
    public void testHMACSHA1() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        
        // Set the key up
        byte[] hmacKey = "secret".getBytes("ASCII");
        String signatureAlgorithm = "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
        SecretKey key = new SecretKeySpec(hmacKey, signatureAlgorithm);
        properties.setSignatureKey(key);
        
        properties.setSignatureAlgorithm(signatureAlgorithm);
        
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
        verifyUsingDOM(document, key, properties.getSignatureSecureParts());
    }
    
    @Test
    public void testHMACSHA_224() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        
        // Set the key up
        byte[] hmacKey = "secret".getBytes("ASCII");
        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha224";
        SecretKey key = new SecretKeySpec(hmacKey, signatureAlgorithm);
        properties.setSignatureKey(key);
        
        properties.setSignatureAlgorithm(signatureAlgorithm);
        
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
        verifyUsingDOM(document, key, properties.getSignatureSecureParts());
    }
    
    @Test
    public void testHMACSHA_256() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        
        // Set the key up
        byte[] hmacKey = "secret".getBytes("ASCII");
        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
        SecretKey key = new SecretKeySpec(hmacKey, signatureAlgorithm);
        properties.setSignatureKey(key);
        
        properties.setSignatureAlgorithm(signatureAlgorithm);
        
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
        verifyUsingDOM(document, key, properties.getSignatureSecureParts());
    }
    
    @Test
    public void testHMACSHA_384() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        
        // Set the key up
        byte[] hmacKey = "secret".getBytes("ASCII");
        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
        SecretKey key = new SecretKeySpec(hmacKey, signatureAlgorithm);
        properties.setSignatureKey(key);
        
        properties.setSignatureAlgorithm(signatureAlgorithm);
        
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
        verifyUsingDOM(document, key, properties.getSignatureSecureParts());
    }
    
    @Test
    public void testHMACSHA_512() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        
        // Set the key up
        byte[] hmacKey = "secret".getBytes("ASCII");
        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
        SecretKey key = new SecretKeySpec(hmacKey, signatureAlgorithm);
        properties.setSignatureKey(key);
        
        properties.setSignatureAlgorithm(signatureAlgorithm);
        
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
        verifyUsingDOM(document, key, properties.getSignatureSecureParts());
    }
    
    @Test
    public void testRIPEMD160() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        properties.setActions(actions);
        
        // Set the key up
        byte[] hmacKey = "secret".getBytes("ASCII");
        String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160";
        SecretKey key = new SecretKeySpec(hmacKey, signatureAlgorithm);
        properties.setSignatureKey(key);
        
        properties.setSignatureAlgorithm(signatureAlgorithm);
        
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
        verifyUsingDOM(document, key, properties.getSignatureSecureParts());
    }

    
}
