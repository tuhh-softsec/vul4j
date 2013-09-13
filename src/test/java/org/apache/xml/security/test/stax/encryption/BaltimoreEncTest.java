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
package org.apache.xml.security.test.stax.encryption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.stax.ext.InboundXMLSec;
import org.apache.xml.security.stax.ext.XMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.test.dom.DSNamespaceContext;
import org.apache.xml.security.test.stax.signature.TestSecurityEventListener;
import org.apache.xml.security.test.stax.utils.StAX2DOM;
import org.apache.xml.security.test.stax.utils.XMLSecEventAllocator;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Interop test for XML Encryption
 * 
 * @author $Author: coheigea $
 * @version $Revision: 1236690 $ $Date: 2012-01-27 14:07:10 +0000 (Fri, 27 Jan 2012) $
 */
public class BaltimoreEncTest extends org.junit.Assert {
    
    private static String cardNumber;
    private static int nodeCount = 0;
    private static PrivateKey rsaKey;

    private XMLInputFactory xmlInputFactory;
    private TransformerFactory transformerFactory = TransformerFactory.newInstance();
    
    @Before
    public void setUp() throws Exception {
        org.apache.xml.security.Init.init();

        xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setEventAllocator(new XMLSecEventAllocator());

        String filename = 
                "src/test/resources/ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml";
        String basedir = System.getProperty("basedir");
        if (basedir != null && !"".equals(basedir)) {
            filename = basedir + "/" + filename;
        }
        File f = new File(filename);

        DocumentBuilder db = XMLUtils.createDocumentBuilder(false);
        Document doc = db.parse(new java.io.FileInputStream(f));

        cardNumber = retrieveCCNumber(doc);
        
        // Count the nodes in the document as a secondary test
        nodeCount = countNodes(doc);
        
        // rsaKey
        filename = "src/test/resources/ie/baltimore/merlin-examples/merlin-xmlenc-five/rsa.p8";
        if (basedir != null && !"".equals(basedir)) {
            filename = basedir + "/" + filename;
        }

        byte[] pkcs8Bytes = JavaUtils.getBytesFromFile(filename);
        PKCS8EncodedKeySpec pkcs8Spec = new PKCS8EncodedKeySpec(pkcs8Bytes);

        // Create a key factory 
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        rsaKey = keyFactory.generatePrivate(pkcs8Spec);
    }

    @Test
    public void test_five_content_3des_cbc() throws Exception {
        // Read in document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-content-tripledes-cbc.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        byte[] keyBytes = "abcdefghijklmnopqrstuvwx".getBytes("ASCII");
        SecretKey secretKey = new SecretKeySpec(keyBytes, "DESede");

        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        
        // Decrypt
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setDecryptionKey(secretKey);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);
         
        document = StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);

        checkDecryptedDoc(document, true);
    }
    
    @Test
    public void test_five_content_aes256_cbc() throws Exception {
        // Read in document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-content-aes256-cbc-prop.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        byte[] keyBytes = "abcdefghijklmnopqrstuvwxyz012345".getBytes("ASCII");
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        
        // Decrypt
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setDecryptionKey(secretKey);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);
         
        document = StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);

        checkDecryptedDoc(document, true);
    }
    
    @Test
    public void test_five_content_aes128_cbc_kw_aes192() throws Exception {
        // Read in document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-content-aes128-cbc-kw-aes192.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        byte[] keyBytes = "abcdefghijklmnopqrstuvwx".getBytes("ASCII");
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        
        // Decrypt
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setDecryptionKey(secretKey);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);
         
        document = StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);

        checkDecryptedDoc(document, true);
    }
    
    @Test
    public void test_five_content_3des_cbc_kw_aes128() throws Exception {
        // Read in document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-element-tripledes-cbc-kw-aes128.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        byte[] keyBytes = "abcdefghijklmnop".getBytes("ASCII");
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        
        // Decrypt
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setDecryptionKey(secretKey);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);
         
        document = StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);

        checkDecryptedDoc(document, true);
    }
    
    @Test
    public void test_five_content_aes128_cbc_rsa_15() throws Exception {
        // Read in document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-element-aes128-cbc-rsa-1_5.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        
        // Decrypt
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setDecryptionKey(rsaKey);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);
         
        document = StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);

        checkDecryptedDoc(document, true);
    }
    
    @Test
    public void test_five_data_aes128_cbc() throws Exception {
        // Read in document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-aes128-cbc.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        byte[] keyBytes = "abcdefghijklmnop".getBytes("ASCII");
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        
        // Decrypt
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setDecryptionKey(secretKey);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);
         
        document = StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void test_five_data_aes256_cbc_3des() throws Exception {
        // Read in document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-aes256-cbc-kw-tripledes.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        byte[] keyBytes = "abcdefghijklmnopqrstuvwx".getBytes("ASCII");
        SecretKey secretKey = new SecretKeySpec(keyBytes, "DESede");

        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        
        // Decrypt
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setDecryptionKey(secretKey);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);
         
        document = StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void test_five_data_aes192_cbc_aes256() throws Exception {
        // Read in document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-aes192-cbc-kw-aes256.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        byte[] keyBytes = "abcdefghijklmnopqrstuvwxyz012345".getBytes("ASCII");
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        
        // Decrypt
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setDecryptionKey(secretKey);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);
         
        document = StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    @Test
    public void test_five_data_3des_cbc_rsa_oaep() throws Exception {
        // Read in document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-tripledes-cbc-rsa-oaep-mgf1p.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        
        // Decrypt
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setDecryptionKey(rsaKey);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();
        XMLStreamReader securityStreamReader = 
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);
         
        document = StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    /*
     * Check we have retrieved a Credit Card number and that it is OK
     * Check that the document has the correct number of nodes
     */
    private void checkDecryptedDoc(Document d, boolean doNodeCheck) throws Exception {

        String cc = retrieveCCNumber(d);
        assertTrue(cc, ((cc != null) && (cc.equals(cardNumber))));

        // Test cc numbers
        if (doNodeCheck) {
            int myNodeCount = countNodes(d);

            assertTrue(
                "Node count mismatches", 
                ((myNodeCount > 0) && myNodeCount == nodeCount)
            );
        }
    }
    
    /**
     * Method retrieveCCNumber
     *
     * Retrieve the credit card number from the payment info document
     *
     * @param doc The document to retrieve the card number from
     * @return The retrieved credit card number
     * @throws XPathExpressionException 
     */
    private static String retrieveCCNumber(Document doc) 
        throws javax.xml.transform.TransformerException, 
        XPathExpressionException {
        
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        Map<String, String> namespace = new HashMap<String, String>();
        namespace.put("x", "urn:example:po");
        DSNamespaceContext context = new DSNamespaceContext(namespace);
        xpath.setNamespaceContext(context);

        String expression =  "//*[local-name()='Number']";
        Node ccnumElt = 
            (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);

        if (ccnumElt != null) {
            return ccnumElt.getTextContent();
        }

        return null;
    }
    
    /**
     * Method countNodes
     *
     * Recursively count the number of nodes in the document
     *
     * @param n Node to count beneath
     */
    private static int countNodes(Node n) {

        if (n == null) {
            return 0;  // Paranoia
        }

        int count = 1;  // Always count myself
        Node c = n.getFirstChild();

        while (c != null) {
            count += countNodes(c);
            c = c.getNextSibling();
        }

        return count;
    }
    
}
