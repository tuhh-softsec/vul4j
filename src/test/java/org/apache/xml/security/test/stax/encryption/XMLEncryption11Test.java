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
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.stax.ext.InboundXMLSec;
import org.apache.xml.security.stax.ext.OutboundXMLSec;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.XMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.test.dom.DSNamespaceContext;
import org.apache.xml.security.test.stax.signature.TestSecurityEventListener;
import org.apache.xml.security.test.stax.utils.StAX2DOM;
import org.apache.xml.security.test.stax.utils.XMLSecEventAllocator;
import org.apache.xml.security.test.stax.utils.XmlReaderToWriter;
import org.apache.xml.security.utils.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class XMLEncryption11Test extends org.junit.Assert {

    private String cardNumber;
    private int nodeCount = 0;

    private XMLInputFactory xmlInputFactory;
    private DocumentBuilderFactory documentBuilderFactory;

    @Before
    public void setUp() throws Exception {

        Class<?> c = this.getClass().getClassLoader().loadClass("org.bouncycastle.jce.provider.BouncyCastleProvider");
        if (null == Security.getProvider("BC")) {
            Security.addProvider((Provider) c.newInstance());
        }

        org.apache.xml.security.Init.init();

        xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setEventAllocator(new XMLSecEventAllocator());

        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setIgnoringComments(false);
        documentBuilderFactory.setCoalescing(false);
        documentBuilderFactory.setIgnoringElementContentWhitespace(false);

        String filename = "org/w3c/www/interop/xmlenc-core-11/plaintext.xml";
        DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
        Document doc = db.parse(this.getClass().getClassLoader().getResourceAsStream(filename));

        cardNumber = retrieveCCNumber(doc);
        nodeCount = countNodes(doc);
    }

    /**
     * rsa-oaep-mgf1p, Digest:SHA256, MGF:SHA1, PSource: None
     */
    @org.junit.Test
    public void testKeyWrappingRSA2048Outbound() throws Exception {
        String keystore = "org/w3c/www/interop/xmlenc-core-11/RSA-2048_SHA256WithRSA.jks";
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(this.getClass().getClassLoader().getResourceAsStream(keystore), "passwd".toCharArray());

        Certificate cert = keyStore.getCertificate("importkey");

        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
                keyStore.getEntry("importkey", new KeyStore.PasswordProtection("passwd".toCharArray()));
        PrivateKey rsaKey = pkEntry.getPrivateKey();

        String filename = "org/w3c/www/interop/xmlenc-core-11/cipherText__RSA-2048__aes128-gcm__rsa-oaep-mgf1p.xml";

        Document dd = decryptElement(filename, rsaKey, (X509Certificate) cert);
        checkDecryptedDoc(dd, true);
    }

    /**
     * rsa-oaep-mgf1p, Digest:SHA256, MGF:SHA1, PSource: None
     */
    @org.junit.Test
    public void testKeyWrappingRSA2048EncryptDecrypt() throws Exception {
        String keystore = "org/w3c/www/interop/xmlenc-core-11/RSA-2048_SHA256WithRSA.jks";
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(this.getClass().getClassLoader().getResourceAsStream(keystore), "passwd".toCharArray());

        Certificate cert = keyStore.getCertificate("importkey");

        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
                keyStore.getEntry("importkey", new KeyStore.PasswordProtection("passwd".toCharArray()));
        PrivateKey rsaKey = pkEntry.getPrivateKey();
        X509Certificate x509Certificate = (X509Certificate) pkEntry.getCertificate();

        // Perform encryption
        String filename = "org/w3c/www/interop/xmlenc-core-11/plaintext.xml";

        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(256);
        SecretKey sessionKey = keygen.generateKey();

        SecurePart securePart =
                new SecurePart(new QName("urn:example:po", "PurchaseOrder"), SecurePart.Modifier.Element);

        Document ed = encryptDocument(filename, securePart, x509Certificate.getPublicKey(),
                "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p", null, null,
                sessionKey, "http://www.w3.org/2009/xmlenc11#aes128-gcm",
                null);
        // XMLUtils.outputDOM(ed.getFirstChild(), System.out);

        // Perform decryption
        Document dd = decryptElement(ed, rsaKey, (X509Certificate) cert);
        // XMLUtils.outputDOM(dd.getFirstChild(), System.out);
        checkDecryptedDoc(dd, true);
    }

    /**
     * rsa-oaep-mgf1p, Digest:SHA256, MGF:SHA1, PSource: None
     */
    @org.junit.Test
    public void testKeyWrappingRSA3072() throws Exception {
        String keystore = "org/w3c/www/interop/xmlenc-core-11/RSA-3072_SHA256WithRSA.jks";
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(this.getClass().getClassLoader().getResourceAsStream(keystore), "passwd".toCharArray());

        Certificate cert = keyStore.getCertificate("importkey");

        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
                keyStore.getEntry("importkey", new KeyStore.PasswordProtection("passwd".toCharArray()));
        PrivateKey rsaKey = pkEntry.getPrivateKey();

        // Perform encryption
        String filename = "org/w3c/www/interop/xmlenc-core-11/cipherText__RSA-3072__aes192-gcm__rsa-oaep-mgf1p__Sha256.xml";

        Document dd = decryptElement(filename, rsaKey, (X509Certificate) cert);
        checkDecryptedDoc(dd, true);
    }

    /**
     * rsa-oaep-mgf1p, Digest:SHA256, MGF:SHA1, PSource: None
     */
    @org.junit.Test
    public void testKeyWrappingRSA3072EncryptDecrypt() throws Exception {
        String keystore = "org/w3c/www/interop/xmlenc-core-11/RSA-3072_SHA256WithRSA.jks";
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(this.getClass().getClassLoader().getResourceAsStream(keystore), "passwd".toCharArray());

        Certificate cert = keyStore.getCertificate("importkey");

        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
                keyStore.getEntry("importkey", new KeyStore.PasswordProtection("passwd".toCharArray()));
        PrivateKey rsaKey = pkEntry.getPrivateKey();
        X509Certificate x509Certificate = (X509Certificate) pkEntry.getCertificate();

        // Perform encryption
        String filename = "org/w3c/www/interop/xmlenc-core-11/plaintext.xml";

        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(256);
        SecretKey sessionKey = keygen.generateKey();

        SecurePart securePart =
                new SecurePart(new QName("urn:example:po", "PurchaseOrder"), SecurePart.Modifier.Element);

        Document ed = encryptDocument(filename, securePart,
                x509Certificate.getPublicKey(), "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p",
                "http://www.w3.org/2001/04/xmlenc#sha256",
                null,
                sessionKey, "http://www.w3.org/2009/xmlenc11#aes192-gcm",
                null);
        // XMLUtils.outputDOM(ed.getFirstChild(), System.out);

        // Perform decryption
        Document dd = decryptElement(ed, rsaKey, (X509Certificate) cert);
        // XMLUtils.outputDOM(dd.getFirstChild(), System.out);
        checkDecryptedDoc(dd, true);
    }

    /**
     * rsa-oaep, Digest:SHA384, MGF:SHA1, PSource: None
     */
    @org.junit.Test
    public void testKeyWrappingRSA3072OAEP() throws Exception {
        String keystore = "org/w3c/www/interop/xmlenc-core-11/RSA-3072_SHA256WithRSA.jks";
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(this.getClass().getClassLoader().getResourceAsStream(keystore), "passwd".toCharArray());

        Certificate cert = keyStore.getCertificate("importkey");

        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
                keyStore.getEntry("importkey", new KeyStore.PasswordProtection("passwd".toCharArray()));
        PrivateKey rsaKey = pkEntry.getPrivateKey();

        String filename = "org/w3c/www/interop/xmlenc-core-11/cipherText__RSA-3072__aes256-gcm__rsa-oaep__Sha384-MGF_Sha1.xml";

        Document dd = decryptElement(filename, rsaKey, (X509Certificate) cert);
        // XMLUtils.outputDOM(dd.getFirstChild(), System.out);
        checkDecryptedDoc(dd, true);
    }

    /**
     * rsa-oaep, Digest:SHA384, MGF:SHA1, PSource: None
     */
    @org.junit.Test
    public void testKeyWrappingRSA3072OAEPEncryptDecrypt() throws Exception {
        String keystore = "org/w3c/www/interop/xmlenc-core-11/RSA-3072_SHA256WithRSA.jks";
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(this.getClass().getClassLoader().getResourceAsStream(keystore), "passwd".toCharArray());

        Certificate cert = keyStore.getCertificate("importkey");

        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
                keyStore.getEntry("importkey", new KeyStore.PasswordProtection("passwd".toCharArray()));
        PrivateKey rsaKey = pkEntry.getPrivateKey();
        X509Certificate x509Certificate = (X509Certificate) pkEntry.getCertificate();

        // Perform encryption
        String filename = "org/w3c/www/interop/xmlenc-core-11/plaintext.xml";

        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(256);
        SecretKey sessionKey = keygen.generateKey();

        SecurePart securePart =
                new SecurePart(new QName("urn:example:po", "PurchaseOrder"), SecurePart.Modifier.Element);

        Document ed = encryptDocument(filename, securePart,
                x509Certificate.getPublicKey(), "http://www.w3.org/2009/xmlenc11#rsa-oaep",
                "http://www.w3.org/2001/04/xmldsig-more#sha384",
                "http://www.w3.org/2009/xmlenc11#mgf1sha1",
                sessionKey, "http://www.w3.org/2009/xmlenc11#aes256-gcm",
                null);
        // XMLUtils.outputDOM(ed.getFirstChild(), System.out);

        // Perform decryption
        Document dd = decryptElement(ed, rsaKey, (X509Certificate) cert);
        // XMLUtils.outputDOM(dd.getFirstChild(), System.out);
        checkDecryptedDoc(dd, true);
    }

    /**
     * rsa-oaep, Digest:SHA512, MGF:SHA1, PSource: Specified 8 bytes
     */
    @org.junit.Test
    public void testKeyWrappingRSA4096() throws Exception {
        String keystore = "org/w3c/www/interop/xmlenc-core-11/RSA-4096_SHA256WithRSA.jks";
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(this.getClass().getClassLoader().getResourceAsStream(keystore), "passwd".toCharArray());

        Certificate cert = keyStore.getCertificate("importkey");

        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
                keyStore.getEntry("importkey", new KeyStore.PasswordProtection("passwd".toCharArray()));
        PrivateKey rsaKey = pkEntry.getPrivateKey();

        String filename = "org/w3c/www/interop/xmlenc-core-11/cipherText__RSA-4096__aes256-gcm__rsa-oaep__Sha512-MGF_Sha1_PSource.xml";

        Document dd = decryptElement(filename, rsaKey, (X509Certificate) cert);
        // XMLUtils.outputDOM(dd.getFirstChild(), System.out);
        checkDecryptedDoc(dd, true);
    }

    /**
     * rsa-oaep, Digest:SHA512, MGF:SHA1, PSource: Specified 8 bytes
     */
    @org.junit.Test
    public void testKeyWrappingRSA4096EncryptDecrypt() throws Exception {
        String keystore = "org/w3c/www/interop/xmlenc-core-11/RSA-4096_SHA256WithRSA.jks";

        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(this.getClass().getClassLoader().getResourceAsStream(keystore), "passwd".toCharArray());

        Certificate cert = keyStore.getCertificate("importkey");

        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
                keyStore.getEntry("importkey", new KeyStore.PasswordProtection("passwd".toCharArray()));
        PrivateKey rsaKey = pkEntry.getPrivateKey();
        X509Certificate x509Certificate = (X509Certificate) pkEntry.getCertificate();

        // Perform encryption
        String filename = "org/w3c/www/interop/xmlenc-core-11/plaintext.xml";

        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(256);
        SecretKey sessionKey = keygen.generateKey();

        SecurePart securePart =
                new SecurePart(new QName("urn:example:po", "PurchaseOrder"), SecurePart.Modifier.Element);

        Document ed = encryptDocument(filename, securePart,
                x509Certificate.getPublicKey(), "http://www.w3.org/2009/xmlenc11#rsa-oaep",
                "http://www.w3.org/2001/04/xmlenc#sha512",
                "http://www.w3.org/2009/xmlenc11#mgf1sha1",
                sessionKey, "http://www.w3.org/2009/xmlenc11#aes256-gcm",
                Base64.decode("ZHVtbXkxMjM=".getBytes("UTF-8")));
        // XMLUtils.outputDOM(ed.getFirstChild(), System.out);

        // Perform decryption
        Document dd = decryptElement(ed, rsaKey, (X509Certificate) cert);
        // XMLUtils.outputDOM(dd.getFirstChild(), System.out);
        checkDecryptedDoc(dd, true);
    }

    /**
     * rsa-oaep, Digest:SHA512, MGF:SHA512, PSource: Specified 8 bytes
     */
    @org.junit.Test
    public void testKeyWrappingRSA4096MGFSHA512EncryptDecrypt() throws Exception {
        String keystore = "org/w3c/www/interop/xmlenc-core-11/RSA-4096_SHA256WithRSA.jks";

        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(this.getClass().getClassLoader().getResourceAsStream(keystore), "passwd".toCharArray());

        Certificate cert = keyStore.getCertificate("importkey");

        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
                keyStore.getEntry("importkey", new KeyStore.PasswordProtection("passwd".toCharArray()));
        PrivateKey rsaKey = pkEntry.getPrivateKey();
        X509Certificate x509Certificate = (X509Certificate) pkEntry.getCertificate();

        // Perform encryption
        String filename = "org/w3c/www/interop/xmlenc-core-11/plaintext.xml";

        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(256);
        SecretKey sessionKey = keygen.generateKey();

        SecurePart securePart =
                new SecurePart(new QName("urn:example:po", "PurchaseOrder"), SecurePart.Modifier.Element);

        Document ed = encryptDocument(filename, securePart,
                x509Certificate.getPublicKey(), "http://www.w3.org/2009/xmlenc11#rsa-oaep",
                "http://www.w3.org/2001/04/xmlenc#sha512",
                "http://www.w3.org/2009/xmlenc11#mgf1sha512",
                sessionKey, "http://www.w3.org/2009/xmlenc11#aes256-gcm",
                Base64.decode("ZHVtbXkxMjM=".getBytes("UTF-8")));
        // XMLUtils.outputDOM(ed.getFirstChild(), System.out);

        // Perform decryption
        Document dd = decryptElement(ed, rsaKey, (X509Certificate) cert);
        // XMLUtils.outputDOM(dd.getFirstChild(), System.out);
        checkDecryptedDoc(dd, true);
    }

    /**
     * rsa-oaep, Digest:SHA512, MGF:SHA512, PSource: Specified 8 bytes
     */
    @org.junit.Test
    public void testAESGCMAuthentication() throws Exception {

        String keystore = "org/w3c/www/interop/xmlenc-core-11/RSA-4096_SHA256WithRSA.jks";

        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(this.getClass().getClassLoader().getResourceAsStream(keystore), "passwd".toCharArray());

        Certificate cert = keyStore.getCertificate("importkey");

        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
                keyStore.getEntry("importkey", new KeyStore.PasswordProtection("passwd".toCharArray()));
        PrivateKey rsaKey = pkEntry.getPrivateKey();
        X509Certificate x509Certificate = (X509Certificate) pkEntry.getCertificate();

        // Perform encryption
        String filename = "org/w3c/www/interop/xmlenc-core-11/plaintext.xml";

        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(256);
        SecretKey sessionKey = keygen.generateKey();

        SecurePart securePart =
                new SecurePart(new QName("urn:example:po", "PurchaseOrder"), SecurePart.Modifier.Element);

        Document ed = encryptDocument(filename, securePart,
                x509Certificate.getPublicKey(), "http://www.w3.org/2009/xmlenc11#rsa-oaep",
                "http://www.w3.org/2001/04/xmlenc#sha512",
                "http://www.w3.org/2009/xmlenc11#mgf1sha512",
                sessionKey, "http://www.w3.org/2009/xmlenc11#aes256-gcm",
                Base64.decode("ZHVtbXkxMjM=".getBytes("UTF-8")));
        // XMLUtils.outputDOM(ed.getFirstChild(), System.out);

        NodeList nl = ed.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherValue");
        Element cipherValue = (Element) nl.item(1);
        String elementText = cipherValue.getTextContent();
        elementText = elementText.substring(0, 100) + 0 + elementText.substring(100);
        cipherValue.setTextContent(elementText);

        // Perform decryption
        try {
            Document dd = decryptElementStAX(ed, rsaKey, (X509Certificate) cert);
        } catch (XMLStreamException e) {
            Assert.assertTrue(e.getCause() instanceof IOException);
            Assert.assertTrue(e.getCause().getCause() instanceof BadPaddingException);
            Assert.assertEquals("mac check in GCM failed", e.getCause().getCause().getMessage());
        }
    }

    /**
     * Method decryptElement
     * <p/>
     * Take a key, encryption type and a file, find an encrypted element
     * decrypt it and return the resulting document
     */
    private Document decryptElement(String filename, Key rsaKey, X509Certificate rsaCert) throws Exception {
        DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
        Document doc = db.parse(this.getClass().getClassLoader().getResourceAsStream(filename));

        return decryptElement(doc, rsaKey, rsaCert);
    }

    /**
     * Method decryptElement
     * <p/>
     * Take a key, encryption type and a document, find an encrypted element
     * decrypt it and return the resulting document
     */
    private Document decryptElement(Document doc, Key rsaKey, X509Certificate rsaCert) throws Exception {
        Document clonedDocument = (Document) doc.cloneNode(true);
        decryptElementDOM(doc, rsaKey, rsaCert);
        return decryptElementStAX(clonedDocument, rsaKey, rsaCert);
    }

    /**
     * Decrypt using StAX API
     */
    private Document decryptElementStAX(Document doc, Key rsaKey, X509Certificate rsaCert) throws Exception {
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setDecryptionKey(rsaKey);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        TestSecurityEventListener securityEventListener = new TestSecurityEventListener();

        final XMLStreamReader xmlStreamReader =
                xmlInputFactory.createXMLStreamReader(new DOMSource(doc));

        XMLStreamReader securityStreamReader =
                inboundXMLSec.processInMessage(xmlStreamReader, null, securityEventListener);

        return StAX2DOM.readDoc(documentBuilderFactory.newDocumentBuilder(), securityStreamReader);
    }

    /**
     * Decrypt using DOM API
     */
    private Document decryptElementDOM(Document doc, Key rsaKey, X509Certificate rsaCert) throws Exception {

        // Create the XMLCipher element
        XMLCipher cipher = XMLCipher.getInstance();

        // Need to pre-load the Encrypted Data so we can get the key info
        Element ee =
                (Element) doc.getElementsByTagNameNS(
                        "http://www.w3.org/2001/04/xmlenc#", "EncryptedData"
                ).item(0);
        cipher.init(XMLCipher.DECRYPT_MODE, null);
        EncryptedData encryptedData = cipher.loadEncryptedData(doc, ee);

        KeyInfo ki = encryptedData.getKeyInfo();
        EncryptedKey encryptedKey = ki.itemEncryptedKey(0);

        XMLCipher cipher2 = XMLCipher.getInstance();
        cipher2.init(XMLCipher.UNWRAP_MODE, rsaKey);
        Key key =
                cipher2.decryptKey(
                        encryptedKey, encryptedData.getEncryptionMethod().getAlgorithm()
                );

        cipher.init(XMLCipher.DECRYPT_MODE, key);
        Document dd = cipher.doFinal(doc, ee);

        return dd;
    }

    /**
     * Encrypt a Document using the given parameters.
     */
    private Document encryptDocument(String filename, SecurePart securePart, Key encryptedKey, String encryptedKeyAlgo,
                                     String digestMethodAlgo, String mgfAlgo, Key sessionKey, String encryptionMethodAlgo,
                                     byte[] oaepParams)
            throws Exception {

        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions =
                new XMLSecurityConstants.Action[]{XMLSecurityConstants.ENCRYPT};
        properties.setOutAction(actions);

        properties.setEncryptionTransportKey(encryptedKey);
        properties.setEncryptionKeyTransportAlgorithm(encryptedKeyAlgo);
        properties.setEncryptionKeyTransportDigestAlgorithm(digestMethodAlgo);
        properties.setEncryptionKeyTransportMGFAlgorithm(mgfAlgo);
        properties.setEncryptionKeyTransportOAEPParams(oaepParams);

        properties.setEncryptionKey(sessionKey);
        properties.setEncryptionSymAlgorithm(encryptionMethodAlgo);

        properties.addEncryptionPart(securePart);

        OutboundXMLSec outboundXMLSec = XMLSec.getOutboundXMLSec(properties);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLStreamWriter xmlStreamWriter = outboundXMLSec.processOutMessage(baos, "UTF-8");

        InputStream sourceDocument =
                this.getClass().getClassLoader().getResourceAsStream(filename);
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);

        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();

        Document document =
                documentBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(baos.toByteArray()));

        NodeList nodeList = document.getElementsByTagNameNS("urn:example:po", "PaymentInfo");
        Assert.assertEquals(nodeList.getLength(), 0);

        NodeList encryptionMethodElements = document.getElementsByTagNameNS(XMLSecurityConstants.NS_XMLENC, "EncryptionMethod");
        Assert.assertEquals(2, encryptionMethodElements.getLength());
        Assert.assertEquals(encryptionMethodAlgo, ((Element) encryptionMethodElements.item(0)).getAttribute("Algorithm"));
        Assert.assertEquals(encryptedKeyAlgo, ((Element) encryptionMethodElements.item(1)).getAttribute("Algorithm"));

        if (digestMethodAlgo != null) {
            NodeList digestMethodElements = document.getElementsByTagNameNS(XMLSecurityConstants.NS_DSIG, "DigestMethod");
            Assert.assertEquals(1, digestMethodElements.getLength());
            Assert.assertEquals(digestMethodAlgo, ((Element) digestMethodElements.item(0)).getAttribute("Algorithm"));
        }
        if (mgfAlgo != null) {
            NodeList mfgElements = document.getElementsByTagNameNS(XMLSecurityConstants.NS_XMLENC11, "MGF");
            Assert.assertEquals(1, mfgElements.getLength());
            Assert.assertEquals(mgfAlgo, ((Element) mfgElements.item(0)).getAttribute("Algorithm"));
        }
        if (oaepParams != null) {
            NodeList oaepParamsElements = document.getElementsByTagNameNS(XMLSecurityConstants.NS_XMLENC, "OAEPparams");
            Assert.assertEquals(1, oaepParamsElements.getLength());
            Assert.assertArrayEquals(oaepParams, Base64.decode((Element) oaepParamsElements.item(0)));
        }
        return document;
    }


    /**
     * Method countNodes
     * <p/>
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

    /**
     * Method retrieveCCNumber
     * <p/>
     * Retrieve the credit card number from the payment info document
     *
     * @param doc The document to retrieve the card number from
     * @return The retrieved credit card number
     * @throws javax.xml.xpath.XPathExpressionException
     *
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

        String expression = "//x:Number/text()";
        Node ccnumElt =
                (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);

        if (ccnumElt != null) {
            return ccnumElt.getNodeValue();
        }

        return null;
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
}
