/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "<WebSig>" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Institute for
 * Data Communications Systems, <http://www.nue.et-inf.uni-siegen.de/>.
 * The development of this software was partly funded by the European
 * Commission in the <WebSig> project in the ISIS Programme.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.security.test.encryption;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.xml.security.encryption.*;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.serialize.DOMSerializer;
import org.apache.xml.serialize.Method;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.KeyName;
import org.apache.xml.security.test.interop.InteropTest;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Interop test for XML Encryption
 *
 * @author Berin Lautenbach
 */
public class BaltimoreEncTest extends TestCase {

	private static String cardNumber;
	private static int nodeCount = 0;
	private static final byte[] bobBytes = 
		"abcdefghijklmnopqrstuvwx".getBytes();


	/** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(BaltimoreEncTest.class.getName());
	
	/**
	 * Method suite
	 *
	 *
	 */
	public static Test suite() throws Exception {
		return new TestSuite(BaltimoreEncTest.class);
	}

	/**
	 *  Constructor BaltimoreEncTest
	 *
	 *  @param Name_
	 */
	public BaltimoreEncTest(String Name_) {
		super(Name_);
	}
	
	/**
	 * Method main
	 *
	 * @param args
	 */

	protected void setUp() throws Exception {
		
		String[] testCaseName = { "-noloading",
								  BaltimoreEncTest.class.getName() };

		// Create the comparison strings

		DocumentBuilderFactory dbf =
			DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setAttribute("http://xml.org/sax/features/namespaces", Boolean.TRUE);
		
		String filename = 
			"data/ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml";
		File f = new File(filename);

		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new java.io.FileInputStream(f));

		cardNumber = retrieveCCNumber(doc);

		// Count the nodes in the document as a secondary test
		nodeCount = countNodes(doc);

		// Initialise the library and get out of here

		org.apache.xml.security.Init.init();
	}

	/**
	 * Method retrieveCCNumber
	 *
	 * Retrieve the credit card number from the payment info document
	 *
	 * @param doc The document to retreive the card number from
	 * @return The retrieved credit card number
	 */

	public static String retrieveCCNumber (Document doc) 
		throws javax.xml.transform.TransformerException {

		Element nscontext =
			XMLUtils.createDSctx(doc, "x",
                              "urn:example:po");
		Node ccnumElt = 
			XPathAPI.selectSingleNode(doc, "//x:Number/text()", nscontext);
		
		if (ccnumElt != null)
			return ccnumElt.getNodeValue();

		return null;

	}

	/**
	 * Method test_five_content_3des_cbc
	 *
	 * Check the merlin-enc-five element content test for 3DES
	 *
	 */

	public void test_five_content_3des_cbc() throws Exception {

		String filename = "data/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-content-tripledes-cbc.xml";

		Document dd = decryptElement(filename, XMLCipher.TRIPLEDES);

		String cc = retrieveCCNumber(dd);

		// Compare the retrieved number to the stored number

		assertTrue(cc, ((cc != null) && (cc.equals(cardNumber))));
		
		// Test my numbers

		int myNodeCount = countNodes(dd);

		assertTrue("Node count mismatches", 
				   ((myNodeCount > 0) && myNodeCount == nodeCount));
    }

	/**
	 * Method decryptElement
	 *
	 * Take a key, encryption type and a file, find an encrypted element
	 * decrypt it and return the resulting document
	 *
	 * @param filename File to decrypt from
	 * @param encType String representing the encryption type
	 * @param key Key to use for decryption
	 */

	public Document decryptElement (String filename, String encType) 
		throws Exception {

		XMLCipher cipher;
		
		// Parse the document in question

		javax.xml.parsers.DocumentBuilderFactory dbf =
			javax.xml.parsers.DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setAttribute("http://xml.org/sax/features/namespaces", Boolean.TRUE);
		File f = new File(filename);

		DocumentBuilder db = dbf.newDocumentBuilder();
		
		Document doc = db.parse(new java.io.FileInputStream(f));
		
		// Now we have the document, lets build the XMLCipher element

		Element ee = null;
		
		// Create the XMLCipher element
		
		cipher = XMLCipher.getInstance(encType);

		// Need to pre-load the Encrypted Data so we can get the key info

		ee = (Element) doc.getElementsByTagName("EncryptedData").item(0);
		EncryptedData encryptedData = cipher.loadEncryptedData(doc, ee);
		KeyInfo ki = encryptedData.getKeyInfo();
   
		SecretKey key = null;

		if (ki != null) {
			KeyName keyName = ki.itemKeyName(0);
			if (keyName != null) {
				key = mapKeyName(keyName.getKeyName());
			}
		}
		cipher.init(XMLCipher.DECRYPT_MODE, key);
		Document dd = cipher.doFinal(doc, ee);

		return dd;
			
    }

	/** 
	 * Method mapKeyName
	 *
	 * Create a secret key from a key name for merlin-five
	 *
	 * @param name Name to map a key from
	 */

	public SecretKey mapKeyName(String name) throws Exception {

		if (name.equals("bob")) {

			// Bob is a DESEDE key

			DESedeKeySpec keySpec = new DESedeKeySpec(bobBytes);
			SecretKeyFactory keyFactory = 
				SecretKeyFactory.getInstance("DESede");
			SecretKey key = keyFactory.generateSecret(keySpec);

			return key;

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

		if (n == null)
			return 0;  // Paranoia

		int count = 1;  // Always count myself
		Node c = n.getFirstChild();

		while (c != null) {

			count += countNodes(c);
			c = c.getNextSibling();

		}

		return count;

	}

    private static void dump(Document document) {
        OutputFormat of = new OutputFormat();
        of.setIndenting(true);
        of.setMethod(Method.XML);
        of.setOmitDocumentType(true);
        of.setOmitXMLDeclaration(true);
        DOMSerializer serializer = new XMLSerializer(System.out, of);
        try {
            serializer.serialize(document);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static String toString(Element element) {
        OutputFormat of = new OutputFormat();
        of.setIndenting(true);
        of.setMethod(Method.XML);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DOMSerializer serializer = new XMLSerializer(baos, of);
        try {
            serializer.serialize(element);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return (baos.toString());
    }

    private static String toString(Document document) {
        OutputFormat of = new OutputFormat();
        of.setIndenting(true);
        of.setMethod(Method.XML);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DOMSerializer serializer = new XMLSerializer(baos, of);
        try {
            serializer.serialize(document);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return (baos.toString());
    }
}
