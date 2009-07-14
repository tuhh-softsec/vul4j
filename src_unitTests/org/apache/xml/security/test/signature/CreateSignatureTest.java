/*
 * Copyright  1999-2009 The Apache Software Foundation.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.ObjectContainer;
import org.apache.xml.security.test.TestUtils;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.XPathContainer;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests that create signatures.
 *
 * @author Sean Mullan
 */
public class CreateSignatureTest extends TestCase {

    /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog
	    (CreateSignatureTest.class.getName());

    private static final String BASEDIR = System.getProperty("basedir");
    private static final String SEP = System.getProperty("file.separator");

    public static Test suite() {
        return new TestSuite(CreateSignatureTest.class);
    }

    public CreateSignatureTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] testCaseName = { "-noloading",
                                  CreateSignatureTest.class.getName() };

        junit.textui.TestRunner.main(testCaseName);
    }

    
    /**
     * Test for bug 36044 - Canonicalizing an empty node-set throws an 
     * ArrayIndexOutOfBoundsException.
     */
    public void testEmptyNodeSet() throws Exception {

        Document doc = db.newDocument();
        Element envelope = doc.createElementNS("http://www.usps.gov/",
                                               "Envelope");
        envelope.appendChild(doc.createTextNode("\n"));
        doc.appendChild(envelope);

        XMLSignature sig = 
	    new XMLSignature(doc, null, XMLSignature.ALGO_ID_SIGNATURE_DSA);

        ObjectContainer object1 = new ObjectContainer(doc);
        object1.setId("object-1");
        object1.setMimeType("text/plain");
        sig.appendObject(object1);

        ObjectContainer object2 = new ObjectContainer(doc);

        object2.setId("object-2");
        object2.setMimeType("text/plain");
        object2.setEncoding("http://www.w3.org/2000/09/xmldsig#base64");
        object2.appendChild(doc.createTextNode("SSBhbSB0aGUgdGV4dC4="));
        sig.appendObject(object2);

        Transforms transforms = new Transforms(doc);
        XPathContainer xpathC = new XPathContainer(doc);

        xpathC.setXPath("self::text()");
        transforms.addTransform(Transforms.TRANSFORM_XPATH,
                                xpathC.getElementPlusReturns());
        sig.addDocument("#object-1", transforms,
                        Constants.ALGO_ID_DIGEST_SHA1, null,
                        "http://www.w3.org/2000/09/xmldsig#Object");

        KeyStore ks = KeyStore.getInstance("JKS");
	FileInputStream fis = null;
        if (BASEDIR != null && !"".equals(BASEDIR)) {
            fis = new FileInputStream(BASEDIR + SEP + 
		  "data/org/apache/xml/security/samples/input/keystore.jks");
	} else {
            fis = new FileInputStream(
		  "data/org/apache/xml/security/samples/input/keystore.jks");
	}
        ks.load(fis, "xmlsecurity".toCharArray());
        PrivateKey privateKey = (PrivateKey) ks.getKey("test",
                                 "xmlsecurity".toCharArray());

	sig.sign(privateKey);
    }
    
    KeyPair kp = null;    
    javax.xml.parsers.DocumentBuilder db;
    
    protected void setUp() throws Exception {
    	javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory
        .newInstance();
    	dbf.setNamespaceAware(true);
    	db = dbf.newDocumentBuilder();
    	org.apache.xml.security.Init.init();
    	kp=KeyPairGenerator.getInstance("RSA").genKeyPair();
    }
    public void testOne() throws Exception {        
        doVerify(doSign()); 
        doVerify(doSign());
    }

    public void testTwo() throws Exception {
	doSignWithCert();
    }

    public void testWithNSPrefixDisabled() throws Exception {
	String prefix = Constants.getSignatureSpecNSprefix();
	try {
            Constants.setSignatureSpecNSprefix("");
            doSign();
            Constants.setSignatureSpecNSprefix(prefix);
	} catch (Exception e) {
            Constants.setSignatureSpecNSprefix(prefix);
	    throw e;
	}
    }

    String doSign() throws Exception {
        PrivateKey privateKey = kp.getPrivate();
        org.w3c.dom.Document doc = db.newDocument();
        doc.appendChild(doc.createComment(" Comment before "));
        Element root = doc.createElementNS("",
                "RootElement");

        doc.appendChild(root);
        root.appendChild(doc.createTextNode("Some simple text\n"));

        Element canonElem = XMLUtils.createElementInSignatureSpace(doc,
                Constants._TAG_CANONICALIZATIONMETHOD);
        canonElem.setAttributeNS(null, Constants._ATT_ALGORITHM,
                Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

        SignatureAlgorithm signatureAlgorithm = new SignatureAlgorithm(doc,
                XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
        XMLSignature sig = new XMLSignature(doc, null, signatureAlgorithm
                .getElement(), canonElem);

        root.appendChild(sig.getElement());
        doc.appendChild(doc.createComment(" Comment after "));
        Transforms transforms = new Transforms(doc);
        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
        sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);

        sig.addKeyInfo(kp.getPublic());
        sig.sign(privateKey);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        XMLUtils.outputDOMc14nWithComments(doc, bos);
        return new String(bos.toByteArray());
    }

    String doSignWithCert() throws Exception {
	KeyStore ks = KeyStore.getInstance("JKS");
	FileInputStream fis = null;
        if (BASEDIR != null && !"".equals(BASEDIR)) {
            fis = new FileInputStream(BASEDIR + SEP + 
		  "data/test.jks");
	} else {
            fis = new FileInputStream("data/test.jks");
	}
        ks.load(fis, "changeit".toCharArray());
        PrivateKey privateKey = (PrivateKey) ks.getKey("mullan",
                                 "changeit".toCharArray());
        org.w3c.dom.Document doc = db.newDocument();
        X509Certificate signingCert = (X509Certificate) ks.getCertificate("mullan");
        doc.appendChild(doc.createComment(" Comment before "));
        Element root = doc.createElementNS("",
                "RootElement");

        doc.appendChild(root);
        root.appendChild(doc.createTextNode("Some simple text\n"));

        Element canonElem = XMLUtils.createElementInSignatureSpace(doc,
                Constants._TAG_CANONICALIZATIONMETHOD);
        canonElem.setAttributeNS(null, Constants._ATT_ALGORITHM,
                Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

        SignatureAlgorithm signatureAlgorithm = new SignatureAlgorithm(doc,
                XMLSignature.ALGO_ID_SIGNATURE_DSA);
        XMLSignature sig = new XMLSignature(doc, null, signatureAlgorithm
                .getElement(), canonElem);

        root.appendChild(sig.getElement());
        doc.appendChild(doc.createComment(" Comment after "));
        Transforms transforms = new Transforms(doc);
        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
        sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);

        sig.addKeyInfo(signingCert);
        sig.sign(privateKey);
	X509Certificate cert = sig.getKeyInfo().getX509Certificate();
	sig.checkSignatureValue(cert.getPublicKey());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        XMLUtils.outputDOMc14nWithComments(doc, bos);
        return new String(bos.toByteArray());
    }

    void doVerify(String signedXML) throws Exception {
        org.w3c.dom.Document doc = db.parse(new ByteArrayInputStream(signedXML.getBytes()));
        Element nscontext = TestUtils.createDSctx(doc, "ds",Constants.SignatureSpecNS);
        Element sigElement = (Element) XPathAPI.selectSingleNode(doc,"//ds:Signature[1]", nscontext);
        XMLSignature signature = new XMLSignature(sigElement, "");
        KeyInfo ki = signature.getKeyInfo();

        if (ki == null) {
        	throw new RuntimeException("No keyinfo");
        }
        PublicKey pk = signature.getKeyInfo().getPublicKey();

        if (pk == null) {
        	throw new RuntimeException("No public key");
        }
        assertTrue(signature.checkSignatureValue(pk) );
    }
}
