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
import java.io.FileInputStream;
import java.io.IOException;

import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Key;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Assert;

import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.keys.content.KeyName;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.serialize.DOMSerializer;
import org.apache.xml.serialize.Method;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Interop test for XML Encryption
 *
 * @author Berin Lautenbach
 */

/*

Tests for merlin-xmlenc-five not currently done

bad-encrypt-content-aes128-cbc-kw-aes192.xml
decryption-transform-except.xml 
decryption-transform.xml        
encrypt-content-aes192-cbc-dh-sha512.xml
encrypt-data-tripledes-cbc-rsa-oaep-mgf1p-sha256.xml
encrypt-element-aes256-cbc-carried-kw-aes256.xml
encrypt-element-aes256-cbc-kw-aes256-dh-ripemd160.xml
encrypt-element-aes256-cbc-retrieved-kw-aes256.xml
encsig-hmac-sha256-dh.xml
encsig-hmac-sha256-kw-tripledes-dh.xml
encsig-hmac-sha256-rsa-1_5.xml
encsig-hmac-sha256-rsa-oaep-mgf1p.xml
encsig-ripemd160-hmac-ripemd160-kw-tripledes.xml
encsig-sha256-hmac-sha256-kw-aes128.xml
encsig-sha384-hmac-sha384-kw-aes192.xml
encsig-sha512-hmac-sha512-kw-aes256.xml

*/
public class BaltimoreEncTest extends TestCase {

	private static String cardNumber;
	private static String rsaCertSerialNumber;
	private static String testDecryptString;
	private static int nodeCount = 0;
	private static byte[] bobBytes;
	private static byte[] jebBytes;
	private static byte[] jobBytes;
	private static byte[] jedBytes;
	private static PrivateKey rsaKey;

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

		// Test decrypt
		testDecryptString = new String("top secret message\n");

		// Count the nodes in the document as a secondary test
		nodeCount = countNodes(doc);

		// Create the keys
		bobBytes = 
			"abcdefghijklmnopqrstuvwx".getBytes("ASCII");
		jebBytes =
			"abcdefghijklmnopqrstuvwx".getBytes("ASCII");
		jobBytes = 
			"abcdefghijklmnop".getBytes("ASCII");
		jedBytes = 
			"abcdefghijklmnopqrstuvwxyz012345".getBytes("ASCII");

		// Certificate information
		rsaCertSerialNumber = new String("1014918766910");

		// rsaKey
		FileInputStream infile = 
			new FileInputStream("data/ie/baltimore/merlin-examples/merlin-xmlenc-five/rsa.p8");
		byte[] pkcs8Bytes = new byte[10240];
		int inputSz = infile.read(pkcs8Bytes);
		infile.close();

		PKCS8EncodedKeySpec pkcs8Spec = 
			new PKCS8EncodedKeySpec(pkcs8Bytes);

		// Create a key factory 
		KeyFactory keyFactory = 
			KeyFactory.getInstance("RSA");
			rsaKey = keyFactory.generatePrivate(pkcs8Spec);
	
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

	/*
	 * Check we have retrieved a Credit Card number and that it is OK
	 * Check that the document has the correct number of nodes
	 */

	private void checkDecryptedDoc(Document d, boolean doNodeCheck) throws Exception {

		String cc = retrieveCCNumber(d);
		log.debug("Retrieved Credit Card : " + cc);
		assertTrue(cc, ((cc!= null) && (cc.equals(cardNumber))));

		// Test cc numbers

		if (doNodeCheck) {
			int myNodeCount = countNodes(d);

			assertTrue("Node count mismatches", 
					   ((myNodeCount > 0) && myNodeCount == nodeCount));
		}
	}

	/**
	 * Check a decrypt of data was OK
	 */

	private void checkDecryptedData(byte [] data) throws Exception {

		String input = new String(data, "ASCII");
        Assert.assertEquals(testDecryptString, input);
	}

	/**
	 * Method test_five_content_3des_cbc
	 *
	 * Check the merlin-enc-five element content test for 3DES
	 *
	 */


	public void test_five_content_3des_cbc() throws Exception {

		String filename = "data/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-content-tripledes-cbc.xml";

		Document dd = decryptElement(filename);
		checkDecryptedDoc(dd, true);
    }

	/**
	 * Method test_five_content_aes256_cbc
	 *
	 * Check the merlin-enc-five element content test for AES256
	 *
	 */

	public void test_five_content_aes256_cbc() throws Exception {

		String filename = "data/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-content-aes256-cbc-prop.xml";

		Document dd = decryptElement(filename);
		checkDecryptedDoc(dd, true);
    }

	/**
	 * Method test_five_content_aes128_cbc_kw_aes192
	 *
	 * Check the merlin-enc-five element content test for AES128 with
	 * AES 192 key wrap
	 *
	 */

	public void test_five_content_aes128_cbc_kw_aes192() throws Exception {

		String filename = "data/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-content-aes128-cbc-kw-aes192.xml";

		Document dd = decryptElement(filename);
		checkDecryptedDoc(dd, true);

    }

	/**
	 * Method test_five_content_3des_cbc_kw_aes128
	 *
	 * Check the merlin-enc-five element content test for 3DES with
	 * AES 128 key wrap
	 *
	 */

	public void test_five_content_3des_cbc_kw_aes128() throws Exception {

		String filename = "data/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-element-tripledes-cbc-kw-aes128.xml";

		Document dd = decryptElement(filename);
		checkDecryptedDoc(dd, true);

    }

	/**
	 * Method test_five_content_aes128_cbc_kw_rsa_15
	 *
	 * Check the merlin-enc-five element content test for AES128 with
	 * RSA key wrap (PKCS 1.5 padding)
	 *
	 */

	public void test_five_content_aes128_cbc_rsa_15() throws Exception {

		String filename = "data/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-element-aes128-cbc-rsa-1_5.xml";

		Document dd = decryptElement(filename);
		checkDecryptedDoc(dd, true);

    }

	/**
	 * Method test_five_element_aes192_cbc_ref
	 *
	 * Check the merlin-enc-five element data test for AES192 with
	 * a CipherReference element
	 *
	 */

	public void test_five_element_aes192_cbc_ref() throws Exception {

		String filename = "data/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-element-aes192-cbc-ref.xml";

		Document dd = decryptElement(filename);
		// Note - we don't check the node count, as it will be different
		// due to the encrypted text remainin in the reference nodes
		checkDecryptedDoc(dd, false);
    }

	/**
	 * Method test_five_data_aes128_cbc
	 *
	 * Check the merlin-enc-five element data test for AES128 with no
	 * key wrap
	 *
	 */

	public void test_five_data_aes128_cbc() throws Exception {

		String filename = "data/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-aes128-cbc.xml";

		byte[] decrypt = decryptData(filename);
		checkDecryptedData(decrypt);
    }

	/**
	 * Method test_five_data_aes256_cbc_3des
	 *
	 * Check the merlin-enc-five element data test for AES256 with 3DES
	 * key wrap
	 *
	 */

	public void test_five_data_aes256_cbc_3des() throws Exception {

		String filename = "data/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-aes256-cbc-kw-tripledes.xml";

		byte[] decrypt = decryptData(filename);
		checkDecryptedData(decrypt);
    }

	/**
	 * Method test_five_data_aes192_cbc_aes256
	 *
	 * Check the merlin-enc-five element data test for AES192 with AES256
	 * key wrap
	 *
	 */

	public void test_five_data_aes192_cbc_aes256() throws Exception {

		String filename = "data/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-aes192-cbc-kw-aes256.xml";

		byte[] decrypt = decryptData(filename);
		checkDecryptedData(decrypt);
    }

	/**
	 * Method test_five_data_3des_cbc_rsa_oaep
	 *
	 * Check the merlin-enc-five element data test for 3DES with
	 * RSA key wrap (OAEP and no parameters)
	 *
	 */

	public void test_five_data_3des_cbc_rsa_oaep() throws Exception {

		String filename = "data/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-tripledes-cbc-rsa-oaep-mgf1p.xml";

		byte[] decrypt = decryptData(filename);
		checkDecryptedData(decrypt);
    }

	/**
	 * Method decryptElement
	 *
	 * Take a key, encryption type and a file, find an encrypted element
	 * decrypt it and return the resulting document
	 *
	 * @param filename File to decrypt from
	 */

	public Document decryptElement (String filename) 
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
		
		cipher = XMLCipher.getInstance();

		// Need to pre-load the Encrypted Data so we can get the key info

		ee = (Element) doc.getElementsByTagName("EncryptedData").item(0);
		cipher.init(XMLCipher.DECRYPT_MODE, null);
		EncryptedData encryptedData = cipher.loadEncryptedData(doc, ee);
   
		Key key = findKey(encryptedData);
		cipher.init(XMLCipher.DECRYPT_MODE, key);
		Document dd = cipher.doFinal(doc, ee);

		return dd;
			
    }

	/**
	 * Method decryptData
	 *
	 * Take a file, find an encrypted element decrypt it and return the 
	 * resulting byte array
	 *
	 * @param filename File to decrypt from
	 */

	public byte[] decryptData (String filename) 
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
		
		cipher = XMLCipher.getInstance();

		// Need to pre-load the Encrypted Data so we can get the key info

		ee = (Element) doc.getElementsByTagName("EncryptedData").item(0);
		cipher.init(XMLCipher.DECRYPT_MODE, null);
		EncryptedData encryptedData = cipher.loadEncryptedData(doc, ee);

		Key key = findKey(encryptedData);
		
		cipher.init(XMLCipher.DECRYPT_MODE, key);
		byte[] dd = cipher.decryptToByteArray(ee);

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
		if (name.equals("job")) {

			// Jeb is a AES-128 key
	        SecretKey key = new SecretKeySpec(jobBytes, "AES");
			return key;
		}
		if (name.equals("jeb")) {

			// Jeb is a AES-192 key
	        SecretKey key = new SecretKeySpec(jebBytes, "AES");
			return key;
		}
		if (name.equals("jed")) {

			// Jeb is a AES-256 key
	        SecretKey key = new SecretKeySpec(jedBytes, "AES");
			return key;
		}

		return null;

	}

	/**
	 * Method findKey
	 *
	 * Given an encryptedData structure, return the key that will decrypt
	 * it
	 *
	 * @param encryptedData EncryptedData to get key for
	 */

	public Key findKey(EncryptedData encryptedData) throws Exception {

		KeyInfo ki = encryptedData.getKeyInfo();
   
		Key key = null;
		Key kek = null;

		if (ki == null)
			return null;
		

		// First check for a known key name
		KeyName keyName = ki.itemKeyName(0);
		if (keyName != null) {
			return (mapKeyName(keyName.getKeyName()));
		}

		// Decrypt any encryptedKey structures
		EncryptedKey encryptedKey = ki.itemEncryptedKey(0);
		
		if (encryptedKey == null)
			return null;
	 
		KeyInfo kiek = encryptedKey.getKeyInfo();
		if (kiek == null) {
			return null;
		}

		KeyName kekKeyName = kiek.itemKeyName(0);
		if (kekKeyName != null) {
			kek = mapKeyName(kekKeyName.getKeyName());
		}
		else {

			X509Data certData = kiek.itemX509Data(0);
			XMLX509Certificate xcert = certData.itemCertificate(0);
			X509Certificate cert = xcert.getX509Certificate();

			if (cert != null) {

				if (cert.getSerialNumber().toString().equals(rsaCertSerialNumber)) {

					kek = rsaKey;
					
				}
			}
		}
		if (kek != null) {
			XMLCipher cipher = XMLCipher.getInstance();
			cipher.init(XMLCipher.DECRYPT_MODE, null);
			cipher.setKEK(kek);
			key = cipher.decryptKey(encryptedKey,
									encryptedData.
									getEncryptionMethod().
									getAlgorithm());
		}
		
		return key;
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
