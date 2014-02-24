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
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.stax.ext.OutboundXMLSec;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.XMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.test.stax.utils.XmlReaderToWriter;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * A set of test-cases for Signature + Encryption.
 */
public class SignatureEncryptionTest extends AbstractSignatureCreationTest {
    
    @Test
    public void testSignatureEncryptionCreation() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        actions.add(XMLSecurityConstants.ENCRYPT);
        properties.setActions(actions);
        
        // Set the key up
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(
            this.getClass().getClassLoader().getResource("transmitter.jks").openStream(), 
            "default".toCharArray()
        );
        Key key = keyStore.getKey("transmitter", "default".toCharArray());
        properties.setSignatureKey(key);
        X509Certificate cert = (X509Certificate)keyStore.getCertificate("transmitter");
        properties.setSignatureCerts(new X509Certificate[]{cert});
        
        // Set the key up
        SecretKey encryptionKey = generateDESSecretKey();
        properties.setEncryptionKey(encryptionKey);
        properties.setEncryptionSymAlgorithm("http://www.w3.org/2001/04/xmlenc#tripledes-cbc");
        
        SecurePart signatureSecurePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Element);
        properties.addSignaturePart(signatureSecurePart);
        
        SecurePart encryptionSecurePart = 
            new SecurePart(new QName("urn:example:po", "PurchaseOrder"), SecurePart.Modifier.Content);
        properties.addEncryptionPart(encryptionSecurePart);
        
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

        // Decrypt using DOM API
        Document doc = 
            decryptUsingDOM("http://www.w3.org/2001/04/xmlenc#tripledes-cbc", encryptionKey, null, document);
        
        // Check the CreditCard decrypted ok
        NodeList nodeList = doc.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Verify using DOM
        verifyUsingDOM(document, cert, properties.getSignatureSecureParts());
    }
    
    @Test
    public void testEnvelopedSignatureEncryptionCreation() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        actions.add(XMLSecurityConstants.ENCRYPT);
        properties.setActions(actions);
        
        // Set the key up
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(
            this.getClass().getClassLoader().getResource("transmitter.jks").openStream(), 
            "default".toCharArray()
        );
        Key key = keyStore.getKey("transmitter", "default".toCharArray());
        properties.setSignatureKey(key);
        X509Certificate cert = (X509Certificate)keyStore.getCertificate("transmitter");
        properties.setSignatureCerts(new X509Certificate[]{cert});
        
        // Set the key up
        SecretKey encryptionKey = generateDESSecretKey();
        properties.setEncryptionKey(encryptionKey);
        properties.setEncryptionSymAlgorithm("http://www.w3.org/2001/04/xmlenc#tripledes-cbc");
        
        SecurePart signatureSecurePart = 
            new SecurePart(
                    new QName("urn:example:po", "PurchaseOrder"),
                    SecurePart.Modifier.Content,
                    new String[]{
                            "http://www.w3.org/2000/09/xmldsig#enveloped-signature",
                            "http://www.w3.org/TR/2001/REC-xml-c14n-20010315"
                    },
                    "http://www.w3.org/2000/09/xmldsig#sha1"
            );
        properties.addSignaturePart(signatureSecurePart);
        
        SecurePart encryptionSecurePart = 
            new SecurePart(new QName("urn:example:po", "PurchaseOrder"), SecurePart.Modifier.Content);
        properties.addEncryptionPart(encryptionSecurePart);
        
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

        // Decrypt using DOM API
        Document doc = 
            decryptUsingDOM("http://www.w3.org/2001/04/xmlenc#tripledes-cbc", encryptionKey, null, document);
        
        // Check the CreditCard decrypted ok
        NodeList nodeList = doc.getElementsByTagNameNS("urn:example:po", "CreditCard");
        Assert.assertEquals(nodeList.getLength(), 1);
        
        // Verify using DOM
        verifyUsingDOM(document, cert, properties.getSignatureSecureParts());
    }
    
    /**
     * Generate a secret key
     */
    private SecretKey generateDESSecretKey() throws Exception {
        byte[] bits192 = "abcdefghijklmnopqrstuvwx".getBytes();
        DESedeKeySpec keySpec = new DESedeKeySpec(bits192);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        return keyFactory.generateSecret(keySpec);
    }
    
    /**
     * Decrypt the document using DOM API and run some tests on the decrypted Document.
     */
    private Document decryptUsingDOM(
        String algorithm, 
        SecretKey secretKey,
        Key wrappingKey,
        Document document
    ) throws Exception {
        XMLCipher cipher = XMLCipher.getInstance(algorithm);
        cipher.init(XMLCipher.DECRYPT_MODE, secretKey);
        if (wrappingKey != null) {
            cipher.setKEK(wrappingKey);
        }
        
        NodeList nodeList = document.getElementsByTagNameNS(
                XMLSecurityConstants.TAG_xenc_EncryptedData.getNamespaceURI(),
                XMLSecurityConstants.TAG_xenc_EncryptedData.getLocalPart()
            );
        Element ee = (Element)nodeList.item(0);
        return cipher.doFinal(document, ee);
    }
}
