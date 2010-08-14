/*
 * Copyright 2008-2010 The Apache Software Foundation.
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

import java.io.FileInputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.XPathContainer;
import org.apache.xml.security.utils.Constants;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests creating and validating an XML Signature with an XPath Transform.
 * Tests bug #44617.
 *
 * @author Frank Cornelis
 */
public class XmlSecTest extends TestCase {
    
    private static final String BASEDIR = 
        System.getProperty("basedir") == null ? "./": System.getProperty("basedir");

    static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog
            (XmlSecTest.class.getName());

    public static Test suite() {
       return new TestSuite(XmlSecTest.class);
    }

    public void testCheckXmlSignatureSoftwareStack() throws Exception {
	checkXmlSignatureSoftwareStack(false);
    }
    public void testCheckXmlSignatureSoftwareStackWithCert() throws Exception {
	checkXmlSignatureSoftwareStack(true);
    }
    private void checkXmlSignatureSoftwareStack(boolean cert) throws Exception {
	Init.init();
	DocumentBuilderFactory documentBuilderFactory = 
	    DocumentBuilderFactory.newInstance();
	documentBuilderFactory.setNamespaceAware(true);
	DocumentBuilder documentBuilder = 
	    documentBuilderFactory.newDocumentBuilder();
	Document testDocument = documentBuilder.newDocument();

	Element rootElement = 
	    testDocument.createElementNS("urn:namespace", "tns:document");
	rootElement.setAttributeNS
	    (Constants.NamespaceSpecNS, "xmlns:tns", "urn:namespace");
	testDocument.appendChild(rootElement);
	Element childElement = 
	    testDocument.createElementNS("urn:childnamespace", "t:child");
	childElement.setAttributeNS
	    (Constants.NamespaceSpecNS, "xmlns:t", "urn:childnamespace");
	childElement.appendChild(testDocument.createTextNode("hello world"));
	rootElement.appendChild(childElement);

	PrivateKey privateKey = null;
	PublicKey publicKey = null;
	X509Certificate signingCert = null;
	if (cert) {
            // get key & self-signed certificate from keystore
            String fs = System.getProperty("file.separator");
            FileInputStream fis = new FileInputStream
                (BASEDIR + fs + "data" + fs + "test.jks");
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(fis, "changeit".toCharArray());
            signingCert = (X509Certificate) ks.getCertificate("mullan");
	    publicKey = signingCert.getPublicKey();
            privateKey = (PrivateKey) ks.getKey("mullan", "changeit".toCharArray());
	} else {
	    KeyPair keyPair = KeyPairGenerator.getInstance("DSA").generateKeyPair();
	    publicKey = keyPair.getPublic();
	    privateKey = keyPair.getPrivate();
	}

	XMLSignature signature = new XMLSignature(testDocument, "",
				XMLSignature.ALGO_ID_SIGNATURE_DSA,
				Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);

	Element signatureElement = signature.getElement();
	rootElement.appendChild(signatureElement);

	Transforms transforms = new Transforms(testDocument);
	XPathContainer xpath = new XPathContainer(testDocument);
	xpath.setXPathNamespaceContext("ds", Constants.SignatureSpecNS);
	xpath.setXPath("not(ancestor-or-self::ds:Signature)");
	transforms.addTransform(Transforms.TRANSFORM_XPATH, xpath
				.getElementPlusReturns());
	transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
	signature.addDocument("", transforms,
				MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1);

	if (cert) {
	    signature.addKeyInfo(signingCert);
	} else {
	    signature.addKeyInfo(publicKey);
	}

	Element nsElement = testDocument.createElementNS(null, "nsElement");
	nsElement.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:ds",
				Constants.SignatureSpecNS);

	signature.sign(privateKey);

	// TransformerFactory tf = TransformerFactory.newInstance();
	// Transformer t = tf.newTransformer();
	// t.transform(new DOMSource(testDocument), new StreamResult(System.out));

	NodeList signatureElems = XPathAPI.selectNodeList(testDocument,
				"//ds:Signature", nsElement);
	signatureElement = (Element) signatureElems.item(0);
	XMLSignature signatureToVerify = new XMLSignature(signatureElement, "");

	boolean signResult = signatureToVerify.checkSignatureValue(publicKey);

	assertTrue(signResult);
    }
}
