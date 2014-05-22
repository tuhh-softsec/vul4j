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
package org.apache.xml.security.test.dom.algorithms;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.security.Key;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.test.dom.DSNamespaceContext;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A test to make sure that the various Signature HMAC algorithms are working
 */
public class HMACSignatureAlgorithmTest extends org.junit.Assert {

    static {
        org.apache.xml.security.Init.init();
    }
    
    public HMACSignatureAlgorithmTest() throws Exception {
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
    
    @org.junit.Test
    public void testHMACSHA1() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        byte[] hmacKey = "secret".getBytes("ASCII");
        SecretKey key = new SecretKeySpec(hmacKey, "http://www.w3.org/2000/09/xmldsig#hmac-sha1");
        
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        sign("http://www.w3.org/2000/09/xmldsig#hmac-sha1", document, localNames, key);
        // XMLUtils.outputDOM(document, System.out);
        verify(document, key, localNames);
    }
    
    @org.junit.Test
    public void testHMACMD5() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        byte[] hmacKey = "secret".getBytes("ASCII");
        SecretKey key = new SecretKeySpec(hmacKey, "http://www.w3.org/2001/04/xmldsig-more#hmac-md5");
        
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        sign("http://www.w3.org/2001/04/xmldsig-more#hmac-md5", document, localNames, key);
        // XMLUtils.outputDOM(document, System.out);
        verify(document, key, localNames);
    }
    
    @org.junit.Test
    public void testHMACSHA_224() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        byte[] hmacKey = "secret".getBytes("ASCII");
        SecretKey key = new SecretKeySpec(hmacKey, "http://www.w3.org/2001/04/xmldsig-more#hmac-sha224");
        
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        sign("http://www.w3.org/2001/04/xmldsig-more#hmac-sha224", document, localNames, key);
        // XMLUtils.outputDOM(document, System.out);
        verify(document, key, localNames);
    }
    
    @org.junit.Test
    public void testHMACSHA_256() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        byte[] hmacKey = "secret".getBytes("ASCII");
        SecretKey key = new SecretKeySpec(hmacKey, "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256");
        
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        sign("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256", document, localNames, key);
        // XMLUtils.outputDOM(document, System.out);
        verify(document, key, localNames);
    }
    
    @org.junit.Test
    public void testHMACSHA_384() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        byte[] hmacKey = "secret".getBytes("ASCII");
        SecretKey key = new SecretKeySpec(hmacKey, "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384");
        
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        sign("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384", document, localNames, key);
        // XMLUtils.outputDOM(document, System.out);
        verify(document, key, localNames);
    }
    
    @org.junit.Test
    public void testHMACSHA_512() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        byte[] hmacKey = "secret".getBytes("ASCII");
        SecretKey key = new SecretKeySpec(hmacKey, "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512");
        
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        sign("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512", document, localNames, key);
        // XMLUtils.outputDOM(document, System.out);
        verify(document, key, localNames);
    }
    
    @org.junit.Test
    public void testHMACRIPEMD160() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        byte[] hmacKey = "secret".getBytes("ASCII");
        SecretKey key = new SecretKeySpec(hmacKey, "http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160");
        
        List<String> localNames = new ArrayList<String>();
        localNames.add("PaymentInfo");
        
        sign("http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160", document, localNames, key);
        // XMLUtils.outputDOM(document, System.out);
        verify(document, key, localNames);
    }

    private XMLSignature sign(
        String algorithm,
        Document document,
        List<String> localNames,
        Key signingKey
    ) throws Exception {
        String c14nMethod = "http://www.w3.org/2001/10/xml-exc-c14n#";
        XMLSignature sig = new XMLSignature(document, "", algorithm, c14nMethod);

        Element root = document.getDocumentElement();
        root.appendChild(sig.getElement());

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());

        for (String localName : localNames) {
            String expression = "//*[local-name()='" + localName + "']";
            NodeList elementsToSign =
                    (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
            for (int i = 0; i < elementsToSign.getLength(); i++) {
                Element elementToSign = (Element)elementsToSign.item(i);
                Assert.assertNotNull(elementToSign);
                String id = UUID.randomUUID().toString();
                elementToSign.setAttributeNS(null, "Id", id);
                elementToSign.setIdAttributeNS(null, "Id", true);

                Transforms transforms = new Transforms(document);
                transforms.addTransform(c14nMethod);
                String digestMethod = "http://www.w3.org/2000/09/xmldsig#sha1";
                sig.addDocument("#" + id, transforms, digestMethod);
            }
        }

        sig.sign(signingKey);

        String expression = "//ds:Signature[1]";
        Element sigElement =
                (Element) xpath.evaluate(expression, document, XPathConstants.NODE);
        Assert.assertNotNull(sigElement);

        return sig;
    }
    
    private void verify(
        Document document,
        Key key,
        List<String> localNames
    ) throws Exception {
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());

        String expression = "//dsig:Signature[1]";
        Element sigElement =
            (Element) xpath.evaluate(expression, document, XPathConstants.NODE);
        Assert.assertNotNull(sigElement);

        for (String name : localNames) {
            expression = "//*[local-name()='" + name + "']";
            Element signedElement =
                (Element) xpath.evaluate(expression, document, XPathConstants.NODE);
            Assert.assertNotNull(signedElement);
            signedElement.setIdAttributeNS(null, "Id", true);
        }

        XMLSignature signature = new XMLSignature(sigElement, "");

        Assert.assertTrue(signature.checkSignatureValue(key));
    }

}
