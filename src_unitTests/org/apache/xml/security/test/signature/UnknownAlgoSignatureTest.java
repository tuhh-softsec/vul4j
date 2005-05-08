/*
 * Copyright  1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.xml.security.test.signature;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Tests cases where signature algorithms are unknown.
 * <p>
 * The source documents are based on that created by the class <code>
 * org.apache.xml.security.samples.signature.CreateEnvelopingSignature</code>
 * </p>
 */
public class UnknownAlgoSignatureTest extends TestCase {

    protected static final String KEYSTORE_TYPE = "JKS";

    protected static final String KEYSTORE_FILE = "data/org/apache/xml/security/samples/input/keystore.jks";

    protected static final String CERT_ALIAS = "test";

    protected static final File SIGNATURE_SOURCE_PATH = new File(
            "data/org/apache/xml/security/temp/signature");

    protected PublicKey publicKey;

    static {
        Init.init();
    }

    public static Test suite() {
        return new TestSuite(UnknownAlgoSignatureTest.class);
    }

    public UnknownAlgoSignatureTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] testCaseName = { "-noloading",
                UnknownAlgoSignatureTest.class.getName() };
        TestRunner.main(testCaseName);
    }

    public void setUp() throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        FileInputStream fis = new FileInputStream(KEYSTORE_FILE);
        keyStore.load(fis, null);
        X509Certificate cert = (X509Certificate) keyStore
                .getCertificate(CERT_ALIAS);
        publicKey = cert.getPublicKey();
    }

    public void testGood() throws ParserConfigurationException, SAXException,
            IOException, TransformerException, XMLSignatureException,
            XMLSecurityException {
        assertTrue(checkSignature(new File(SIGNATURE_SOURCE_PATH,
                "signature-good.xml")));
    }

    public void testBadC14NAlgo() throws ParserConfigurationException,
            SAXException, IOException, TransformerException,
            XMLSecurityException {
        try {
            assertTrue(checkSignature(new File(SIGNATURE_SOURCE_PATH,
                    "signature-bad-c14n-algo.xml")));
            fail("Exception not caught");
        } catch (InvalidCanonicalizerException e) {
            // succeed
        }
    }

    public void testBadSigAlgo() throws ParserConfigurationException,
            SAXException, IOException, TransformerException,
            XMLSecurityException {
        try {
            assertTrue(checkSignature(new File(SIGNATURE_SOURCE_PATH,
                    "signature-bad-sig-algo.xml")));
            fail("Exception not caught");
        } catch (XMLSignatureException e) {
            // succeed
        }
    }

    public void testBadTransformAlgo() throws ParserConfigurationException,
            SAXException, IOException, TransformerException,
            XMLSecurityException {
        try {
            assertTrue(checkSignature(new File(SIGNATURE_SOURCE_PATH,
                    "signature-bad-transform-algo.xml")));
            fail("Exception not caught");
        } catch (XMLSignatureException e) {
            // succeed
        }
    }

    protected boolean checkSignature(File file)
            throws ParserConfigurationException, SAXException, IOException,
            TransformerException, XMLSecurityException {
        Document doc = getDocument(file);
        Element nscontext = XMLUtils.createDSctx(doc, "ds",
                Constants.SignatureSpecNS);
        Element signatureEl = (Element) XPathAPI.selectSingleNode(doc,
                "//ds:Signature[1]", nscontext);
        XMLSignature signature = new XMLSignature(signatureEl, file.toURL()
                .toString());
        return signature.checkSignatureValue(publicKey);
    }

    public static Document getDocument(File file)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new FileInputStream(file));
        return doc;
    }
}
