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
package org.apache.xml.security.test.encryption;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.EncryptionMethod;
import org.apache.xml.security.encryption.CipherData;
import org.apache.xml.security.transforms.params.XPathContainer;
import org.apache.xml.security.utils.IdResolver;
import org.apache.xml.security.keys.KeyInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 *
 * @author  Axl Mattheus
 * @author  Berin Lautenbach
 */
public class XMLCipherTester extends TestCase {

	/** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(XMLCipherTester.class.getName());
    
    private String documentName;
    private String elementName;
    private String elementIndex;
    private XMLCipher cipher;

	private boolean haveISOPadding;
	private boolean haveKeyWraps;

	private String tstBase64EncodedString;

    public XMLCipherTester(String test) {
       super(test);
    }

    protected void setUp() {
        documentName = System.getProperty("org.apache.xml.enc.test.doc",
            "./build.xml");
        elementName = System.getProperty("org.apache.xml.enc.test.elem",
            "path");
        elementIndex = System.getProperty("org.apache.xml.enc.test.idx",
            "0");

		tstBase64EncodedString = new String("YmNkZWZnaGlqa2xtbm9wcRrPXjQ1hvhDFT+EdesMAPE4F6vlT+y0HPXe0+nAGLQ8");

		// Determine if we have ISO 10126 Padding - needed for Bulk AES or
		// 3DES encryption

		haveISOPadding = false;
		String algorithmId = 
			JCEMapper.translateURItoJCEID(org.apache.xml.security.utils.EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);

		if (algorithmId != null) {
			try {
				if (Cipher.getInstance(algorithmId) != null)
					haveISOPadding = true;
			} catch (NoSuchAlgorithmException nsae) {
			} catch (NoSuchPaddingException nspe) {
			}
		}

		haveKeyWraps = (JCEMapper.translateURItoJCEID(org.apache.xml.security.utils.EncryptionConstants.ALGO_ID_KEYWRAP_AES128) != null);

    }

    protected void tearDown() {
    }

    private Document document() {
        Document d = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File f = new File(documentName);
            d = db.parse(f);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return (d);
    }

    private String element() {
        return (elementName);
    }

    private int index() {
        int result = -1;

        try {
            result = Integer.parseInt(elementIndex);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            System.exit(-1);
        }

        return (result);
    }

	/**
	 * Test encryption using a generated AES 128 bit key that is
	 * encrypted using a AES 192 bit key.  Then reverse using the KEK
	 */

	public void testAES128ElementAES192KWCipherUsingKEK() throws Exception {

		Document d = document(); // source
		Document ed = null;
		Document dd = null;
		Element e = (Element) d.getElementsByTagName(element()).item(index());
		Element ee = null;

		String source = null;
		String target = null;

        if (haveISOPadding && haveKeyWraps) {

			source = toString(d);

			// Set up a Key Encryption Key
			byte[] bits192 = "abcdefghijklmnopqrstuvwx".getBytes();
			Key kek = new SecretKeySpec(bits192, "AES");

			// Generate a traffic key
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			keygen.init(128);
			Key key = keygen.generateKey();

            cipher = XMLCipher.getInstance(XMLCipher.AES_192_KeyWrap);
			cipher.init(XMLCipher.WRAP_MODE, kek);
			EncryptedKey encryptedKey = cipher.encryptKey(d, key);

            // encrypt
            cipher = XMLCipher.getInstance(XMLCipher.AES_128);
            cipher.init(XMLCipher.ENCRYPT_MODE, key);
			EncryptedData builder = cipher.getEncryptedData();

			KeyInfo builderKeyInfo = builder.getKeyInfo();
			if (builderKeyInfo == null) {
				builderKeyInfo = new KeyInfo(d);
				builder.setKeyInfo(builderKeyInfo);
			}

			builderKeyInfo.add(encryptedKey);

            ed = cipher.doFinal(d, e);

            //decrypt
			key = null;
            ee = (Element) ed.getElementsByTagName("xenc:EncryptedData").item(0);
            cipher = XMLCipher.getInstance(XMLCipher.AES_128);
            cipher.init(XMLCipher.DECRYPT_MODE, null);
			cipher.setKEK(kek);
			dd = cipher.doFinal(ed, ee);

            target = toString(dd);

			Assert.assertEquals(source, target);
		}
		else {
			log.warn("Test testAES128ElementAES192KWCipherUsingKEK skipped as necessary algorithms not available");
		}
    }
  
	/**
	 * Test encryption using a generated AES 256 bit key that is
	 * encrypted using an RSA key.  Reverse using KEK
	 */

	public void testAES128ElementRSAKWCipherUsingKEK() throws Exception {

		Document d = document(); // source
		Document ed = null;
		Document dd = null;
		Element e = (Element) d.getElementsByTagName(element()).item(index());
		Element ee = null;

		String source = null;
		String target = null;

        if (haveISOPadding) {

			source = toString(d);

            // Generate an RSA key
            KeyPairGenerator rsaKeygen = KeyPairGenerator.getInstance("RSA");
            KeyPair kp = rsaKeygen.generateKeyPair();
            PrivateKey priv = kp.getPrivate();
            PublicKey pub = kp.getPublic();
            
			// Generate a traffic key
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			keygen.init(256);
			Key key = keygen.generateKey();

            
            cipher = XMLCipher.getInstance(XMLCipher.RSA_v1dot5);
			cipher.init(XMLCipher.WRAP_MODE, pub);
			EncryptedKey encryptedKey = cipher.encryptKey(d, key);

            // encrypt
            cipher = XMLCipher.getInstance(XMLCipher.AES_256);
            cipher.init(XMLCipher.ENCRYPT_MODE, key);
			EncryptedData builder = cipher.getEncryptedData();

			KeyInfo builderKeyInfo = builder.getKeyInfo();
			if (builderKeyInfo == null) {
				builderKeyInfo = new KeyInfo(d);
				builder.setKeyInfo(builderKeyInfo);
			}

			builderKeyInfo.add(encryptedKey);

            ed = cipher.doFinal(d, e);
            log.debug("Encrypted document");
            log.debug(toString(ed));


            //decrypt
			key = null;
            ee = (Element) ed.getElementsByTagName("xenc:EncryptedData").item(0);
            cipher = XMLCipher.getInstance(XMLCipher.AES_128);
            cipher.init(XMLCipher.DECRYPT_MODE, null);
			cipher.setKEK(priv);
			dd = cipher.doFinal(ed, ee);

            target = toString(dd);
            log.debug("Output document");
            log.debug(target);

			Assert.assertEquals(source, target);
		}
		else {
			log.warn("Test testAES128ElementRSAKWCipherUsingKEK skipped as necessary algorithms not available");
		}
    }

	/**
	 * Test encryption using a generated AES 192 bit key that is
	 * encrypted using a 3DES key.  Then reverse by decrypting 
	 * EncryptedKey by hand
	 */

	public void testAES192ElementAES256KWCipher() throws Exception {

		Document d = document(); // source
		Document ed = null;
		Document dd = null;
		Element e = (Element) d.getElementsByTagName(element()).item(index());
		Element ee = null;

		String source = null;
		String target = null;

        if (haveISOPadding && haveKeyWraps) {

			source = toString(d);

			// Set up a Key Encryption Key
			byte[] bits192 = "abcdefghijklmnopqrstuvwx".getBytes();
            DESedeKeySpec keySpec = new DESedeKeySpec(bits192);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            Key kek = keyFactory.generateSecret(keySpec);

			// Generate a traffic key
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			keygen.init(192);
			Key key = keygen.generateKey();

            cipher = XMLCipher.getInstance(XMLCipher.TRIPLEDES_KeyWrap);
			cipher.init(XMLCipher.WRAP_MODE, kek);
			EncryptedKey encryptedKey = cipher.encryptKey(d, key);

            // encrypt
            cipher = XMLCipher.getInstance(XMLCipher.AES_192);
            cipher.init(XMLCipher.ENCRYPT_MODE, key);
			EncryptedData builder = cipher.getEncryptedData();

			KeyInfo builderKeyInfo = builder.getKeyInfo();
			if (builderKeyInfo == null) {
				builderKeyInfo = new KeyInfo(d);
				builder.setKeyInfo(builderKeyInfo);
			}

			builderKeyInfo.add(encryptedKey);

            ed = cipher.doFinal(d, e);

            //decrypt
			key = null;
            ee = (Element) ed.getElementsByTagName("xenc:EncryptedData").item(0);
            cipher = XMLCipher.getInstance();
            cipher.init(XMLCipher.DECRYPT_MODE, null);

			EncryptedData encryptedData = cipher.loadEncryptedData(ed, ee);
			
			if(encryptedData == null) {
				System.out.println("ed is null");
			}
			else if (encryptedData.getKeyInfo() == null) {
				System.out.println("ki is null");
			}
			EncryptedKey ek = encryptedData.getKeyInfo().itemEncryptedKey(0);

			if (ek != null) {
				XMLCipher keyCipher = XMLCipher.getInstance();
				keyCipher.init(XMLCipher.UNWRAP_MODE, kek);
				key = keyCipher.decryptKey(ek, encryptedData.getEncryptionMethod().getAlgorithm());
			}

			// Create a new cipher just to be paranoid
			XMLCipher cipher3 = XMLCipher.getInstance();
			cipher3.init(XMLCipher.DECRYPT_MODE, key);
            dd = cipher3.doFinal(ed, ee);

            target = toString(dd);

			Assert.assertEquals(source, target);
		}
		else {
			log.warn("Test testAES192ElementAES256KWCipher skipped as necessary algorithms not available");
		}
    }

    public void testTrippleDesElementCipher() throws Exception {
        Document d = document(); // source
        Document ed = null;      // target
        Document dd = null;      // target
        Element e = (Element) d.getElementsByTagName(element()).item(index());
        Element ee = null;

        String source = null;
        String target = null;

        if (haveISOPadding) {

			source = toString(d);

            // prepare for encryption
            byte[] passPhrase = "24 Bytes per DESede key!".getBytes();
            DESedeKeySpec keySpec = new DESedeKeySpec(passPhrase);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey key = keyFactory.generateSecret(keySpec);

            // encrypt
            cipher = XMLCipher.getInstance(XMLCipher.TRIPLEDES);
            cipher.init(XMLCipher.ENCRYPT_MODE, key);
            ed = cipher.doFinal(d, e);

            //decrypt
            cipher = XMLCipher.getInstance(XMLCipher.TRIPLEDES);
            cipher.init(XMLCipher.DECRYPT_MODE, key);
            ee = (Element) ed.getElementsByTagName("xenc:EncryptedData").item(0);
			EncryptedData encryptedData = cipher.loadEncryptedData(ed, ee);
			Assert.assertEquals(encryptedData.getEncryptionMethod().getAlgorithm(), 
								XMLCipher.TRIPLEDES);
            dd = cipher.doFinal(ed, ee);

            target = toString(dd);
			Assert.assertEquals(source, target);
		}
		else {
			log.warn("Test testTrippleDesElementCipher skipped as necessary algorithms not available");
		}
    }

    public void testAes128ElementCipher() throws Exception {
        byte[] bits128 = {
            (byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13,
            (byte) 0x14, (byte) 0x15, (byte) 0x16, (byte) 0x17,
            (byte) 0x18, (byte) 0x19, (byte) 0x1A, (byte) 0x1B,
            (byte) 0x1C, (byte) 0x1D, (byte) 0x1E, (byte) 0x1F};
        Key key = new SecretKeySpec(bits128, "AES");

        Document d = document(); // source
        Document ed = null;      // target
        Document dd = null;      // target
        Element e = (Element) d.getElementsByTagName(element()).item(index());
        Element ee = null;

        String source = null;
        String target = null;

        if (haveISOPadding) {

			source = toString(d);

            // encrypt
            cipher = XMLCipher.getInstance(XMLCipher.AES_128);
            cipher.init(XMLCipher.ENCRYPT_MODE, key);
            ed = cipher.doFinal(d, e);

            //decrypt
            cipher = XMLCipher.getInstance(XMLCipher.AES_128);
            cipher.init(XMLCipher.DECRYPT_MODE, key);
            ee = (Element) ed.getElementsByTagName("xenc:EncryptedData").item(0);
			EncryptedData encryptedData = cipher.loadEncryptedData(ed, ee);
			Assert.assertEquals(encryptedData.getEncryptionMethod().getAlgorithm(), 
								XMLCipher.AES_128);
            dd = cipher.doFinal(ed, ee);

            target = toString(dd);
			Assert.assertEquals(source, target);
		}
		else {
			log.warn("Test testAes128ElementCipher skipped as necessary algorithms not available");
		}
    }

    public void testAes192ElementCipher() throws Exception {
        byte[] bits192 = {
            (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B,
            (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F,
            (byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13,
            (byte) 0x14, (byte) 0x15, (byte) 0x16, (byte) 0x17,
            (byte) 0x18, (byte) 0x19, (byte) 0x1A, (byte) 0x1B,
            (byte) 0x1C, (byte) 0x1D, (byte) 0x1E, (byte) 0x1F};
        Key key = new SecretKeySpec(bits192, "AES");

        Document d = document(); // source
        Document ed = null;      // target
        Document dd = null;      // target
        Element e = (Element) d.getElementsByTagName(element()).item(index());
        Element ee = null;

        String source = null;
        String target = null;

        if (haveISOPadding) {

			source = toString(d);

            // encrypt
            cipher = XMLCipher.getInstance(XMLCipher.AES_192);
            cipher.init(XMLCipher.ENCRYPT_MODE, key);
            ed = cipher.doFinal(d, e);

            //decrypt
            cipher = XMLCipher.getInstance(XMLCipher.AES_192);
            cipher.init(XMLCipher.DECRYPT_MODE, key);
            ee = (Element) ed.getElementsByTagName("xenc:EncryptedData").item(0);
			EncryptedData encryptedData = cipher.loadEncryptedData(ed, ee);
			Assert.assertEquals(encryptedData.getEncryptionMethod().getAlgorithm(), 
								XMLCipher.AES_192);
            dd = cipher.doFinal(ed, ee);

            target = toString(dd);
			Assert.assertEquals(source, target);
		}
		else {
			log.warn("Test testAes192ElementCipher skipped as necessary algorithms not available");
		}
    }

    public void testAes265ElementCipher() throws Exception {
        byte[] bits256 = {
            (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03,
            (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07,
            (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B,
            (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F,
            (byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13,
            (byte) 0x14, (byte) 0x15, (byte) 0x16, (byte) 0x17,
            (byte) 0x18, (byte) 0x19, (byte) 0x1A, (byte) 0x1B,
            (byte) 0x1C, (byte) 0x1D, (byte) 0x1E, (byte) 0x1F};
        Key key = new SecretKeySpec(bits256, "AES");

        Document d = document(); // source
        Document ed = null;      // target
        Document dd = null;      // target
        Element e = (Element) d.getElementsByTagName(element()).item(index());
        Element ee = null;

        String source = null;
        String target = null;

        if (haveISOPadding) {

			source = toString(d);

            // encrypt
            cipher = XMLCipher.getInstance(XMLCipher.AES_256);
            cipher.init(XMLCipher.ENCRYPT_MODE, key);
            ed = cipher.doFinal(d, e);

            //decrypt
            cipher = XMLCipher.getInstance(XMLCipher.AES_256);
            cipher.init(XMLCipher.DECRYPT_MODE, key);
            ee = (Element) ed.getElementsByTagName("xenc:EncryptedData").item(0);
			EncryptedData encryptedData = cipher.loadEncryptedData(ed, ee);
			Assert.assertEquals(encryptedData.getEncryptionMethod().getAlgorithm(), 
								XMLCipher.AES_256);
            dd = cipher.doFinal(ed, ee);

            target = toString(dd);
			Assert.assertEquals(source, target);
		}
		else {
			log.warn("Test testAes265ElementCipher skipped as necessary algorithms not available");
		}
    }

    /*
	 * Test case for when the entire document is encrypted and decrypted
	 * In this case the EncryptedData becomes the root element of the document
	 */

    public void testTrippleDesDocumentCipher() throws Exception {
        Document d = document(); // source
        Document ed = null;      // target
        Document dd = null;      // target
        Element e = d.getDocumentElement();
        Element ee = null;

        String source = null;
        String target = null;

        if (haveISOPadding) {

			source = toString(d);

            // prepare for encryption
            byte[] passPhrase = "24 Bytes per DESede key!".getBytes();
            DESedeKeySpec keySpec = new DESedeKeySpec(passPhrase);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey key = keyFactory.generateSecret(keySpec);

            // encrypt
            cipher = XMLCipher.getInstance(XMLCipher.TRIPLEDES);
            cipher.init(XMLCipher.ENCRYPT_MODE, key);
            ed = cipher.doFinal(d, e);

            //decrypt
            cipher = XMLCipher.getInstance(XMLCipher.TRIPLEDES);
            cipher.init(XMLCipher.DECRYPT_MODE, key);
            ee = (Element) ed.getElementsByTagName("xenc:EncryptedData").item(0);
            dd = cipher.doFinal(ed, ee);

            target = toString(dd);

			Assert.assertEquals(source, target);
		}
		else {
			log.warn("Test testTrippleDesDocumentCipher skipped as necessary algorithms not available");
		}
    }

	/*
	 * Test a Cipher Reference
	 */

	public void testSameDocumentCipherReference() throws Exception {

        if (haveISOPadding) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document d = db.newDocument();

			Element docElement = d.createElement("EncryptedDoc");
			d.appendChild(docElement);

			// Create the XMLCipher object
			cipher = XMLCipher.getInstance();

			EncryptedData ed = 
				cipher.createEncryptedData(CipherData.REFERENCE_TYPE,
									   "#CipherTextId");
			EncryptionMethod em =
				cipher.createEncryptionMethod(XMLCipher.AES_128);

			ed.setEncryptionMethod(em);

			org.apache.xml.security.encryption.Transforms xencTransforms =
				cipher.createTransforms(d);
			ed.getCipherData().getCipherReference().setTransforms(xencTransforms);
			org.apache.xml.security.transforms.Transforms dsTransforms =
				xencTransforms.getDSTransforms();

			// An XPath transform
			XPathContainer xpc = new XPathContainer(d);
			xpc.setXPath("self::text()[parent::CipherText[@Id=\"CipherTextId\"]]");
			dsTransforms.addTransform(org.apache.xml.security.transforms.Transforms.TRANSFORM_XPATH, 
									  xpc.getElementPlusReturns());

			// Add a Base64 Transforms
			dsTransforms.addTransform(
									  org.apache.xml.security.transforms.Transforms.TRANSFORM_BASE64_DECODE);

			Element ee = cipher.martial(d, ed);

			docElement.appendChild(ee);

			// Add the cipher text
			Element encryptedElement = d.createElement("CipherText");
			encryptedElement.setAttributeNS(null, "Id", "CipherTextId");
			IdResolver.registerElementById(encryptedElement, "CipherTextId");
			encryptedElement.appendChild(d.createTextNode(tstBase64EncodedString));
			docElement.appendChild(encryptedElement);
			// dump(d);

			// Now the decrypt, with a brand new cipher
			XMLCipher cipherDecrypt = XMLCipher.getInstance();
			Key key = 
				new SecretKeySpec("abcdefghijklmnop".getBytes("ASCII"), "AES");

			cipherDecrypt.init(XMLCipher.DECRYPT_MODE, key);
			byte[] decryptBytes = cipherDecrypt.decryptToByteArray(ee);

			Assert.assertEquals(new String(decryptBytes, "ASCII"), 
								new String("A test encrypted secret"));
		}
		else {
			log.warn("Test testSameDocumentCipherReference skipped as necessary algorithms not available");
		}

	}

	private String toString (Node n)
		throws Exception {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Canonicalizer c14n = Canonicalizer.getInstance
			(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);

		byte[] serBytes = c14n.canonicalizeSubtree(n);
		baos.write(serBytes);
		baos.close();

		return baos.toString("UTF-8");

	}
		
   static {
      org.apache.xml.security.Init.init();
   }

}
