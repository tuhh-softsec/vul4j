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
package org.apache.xml.security.test.dom.encryption;

import java.io.File;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.KeyName;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.keys.keyresolver.KeyResolver;
import org.apache.xml.security.test.dom.DSNamespaceContext;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Interop test for XML Encryption
 *
 * @author Berin Lautenbach
 */
public class BaltimoreEncTest extends org.junit.Assert {

    private static String cardNumber;
    private static String rsaCertSerialNumber;
    private static String testDecryptString;
    private static int nodeCount = 0;
    private static byte[] jebBytes;
    private static byte[] jobBytes;
    private static byte[] jedBytes;
    private static PrivateKey rsaKey;
    private boolean haveISOPadding;
    private boolean haveKeyWraps;

    static org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(BaltimoreEncTest.class);

    /**
     *  Constructor BaltimoreEncTest
     */
    public BaltimoreEncTest() throws Exception {
        // Create the comparison strings
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

        // Test decrypt
        testDecryptString = "top secret message\n";

        // Count the nodes in the document as a secondary test
        nodeCount = countNodes(doc);

        // Create the keys
        jebBytes = "abcdefghijklmnopqrstuvwx".getBytes("ASCII");
        jobBytes = "abcdefghijklmnop".getBytes("ASCII");
        jedBytes = "abcdefghijklmnopqrstuvwxyz012345".getBytes("ASCII");

        // Certificate information
        rsaCertSerialNumber = "1014918766910";

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

        // Initialise the library
        org.apache.xml.security.Init.init();

        // Register our key resolver
        KeyResolver.register("org.apache.xml.security.test.dom.encryption.BobKeyResolver", false);

        // Check what algorithms are available

        haveISOPadding = false;
        String algorithmId = 
            JCEMapper.translateURItoJCEID(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);

        if (algorithmId != null) {
            try {
                if (Cipher.getInstance(algorithmId) != null) {
                    haveISOPadding = true;
                }
            } catch (NoSuchAlgorithmException nsae) {
                //
            } catch (NoSuchPaddingException nspe) {
                //
            }
        }

        haveKeyWraps = 
            (JCEMapper.translateURItoJCEID(EncryptionConstants.ALGO_ID_KEYWRAP_AES128) != null);
    }

    /**
     * Method test_five_content_3des_cbc
     *
     * Check the merlin-enc-five element content test for 3DES
     */
    @org.junit.Test
    public void test_five_content_3des_cbc() throws Exception {
        if (haveISOPadding) {
            String filename = 
                "src/test/resources/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-content-tripledes-cbc.xml";

            Document dd = decryptElement(filename);
            checkDecryptedDoc(dd, true);
        } else {
            log.warn(
                "Skipping test test_five_content_3des_cbs as necessary "
                + "crypto algorithms are not available"
            );
        }
    }

    /**
     * Method test_five_content_aes256_cbc
     *
     * Check the merlin-enc-five element content test for AES256
     */
    @org.junit.Test
    public void test_five_content_aes256_cbc() throws Exception {

        if (haveISOPadding) {
            String filename = 
                "src/test/resources/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-content-aes256-cbc-prop.xml";

            Document dd = decryptElement(filename);
            checkDecryptedDoc(dd, true);
        } else {
            log.warn(
                "Skipping test test_five_content_aes256_cbc as necessary "
                + "crypto algorithms are not available"
            );
        }
    }

    /**
     * Method test_five_content_aes128_cbc_kw_aes192
     *
     * Check the merlin-enc-five element content test for AES128 with
     * AES 192 key wrap
     */
    @org.junit.Test
    public void test_five_content_aes128_cbc_kw_aes192() throws Exception {
        if (haveISOPadding && haveKeyWraps) {
            String filename = 
                "src/test/resources/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-content-aes128-cbc-kw-aes192.xml";

            Document dd = decryptElement(filename);
            checkDecryptedDoc(dd, true);
        } else {
            log.warn(
                "Skipping test test_five_content_aes128_cbc_kw_aes192 as necessary "
                + "crypto algorithms are not available"
            );
        }
    }

    /**
     * Method test_five_content_3des_cbc_kw_aes128
     *
     * Check the merlin-enc-five element content test for 3DES with
     * AES 128 key wrap
     */
    @org.junit.Test
    public void test_five_content_3des_cbc_kw_aes128() throws Exception {

        if (haveISOPadding && haveKeyWraps) {
            String filename = 
                "src/test/resources/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-element-tripledes-cbc-kw-aes128.xml";

            Document dd = decryptElement(filename);
            checkDecryptedDoc(dd, true);
        } else {
            log.warn(
                "Skipping test test_five_content_3des_cbc_kw_aes128 as necessary "
                + "crypto algorithms are not available"
            );
        }
    }

    /**
     * Method test_five_content_aes128_cbc_kw_rsa_15
     *
     * Check the merlin-enc-five element content test for AES128 with
     * RSA key wrap (PKCS 1.5 padding)
     */
    @org.junit.Test
    public void test_five_content_aes128_cbc_rsa_15() throws Exception {
        if (haveISOPadding) {
            String filename = 
                "src/test/resources/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-element-aes128-cbc-rsa-1_5.xml";

            Document dd = decryptElement(filename);
            checkDecryptedDoc(dd, true);
        } else {
            log.warn(
                "Skipping test test_five_content_aes128_cbc_rsa_15 as necessary "
                + "crypto algorithms are not available"
            );
        }
    }

    /**
     * Method test_five_element_aes192_cbc_ref
     *
     * Check the merlin-enc-five element data test for AES192 with
     * a CipherReference element
     */
    @org.junit.Test
    public void test_five_element_aes192_cbc_ref() throws Exception {
        if (haveISOPadding) {
            String filename = 
                "src/test/resources/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-element-aes192-cbc-ref.xml";

            Document dd = decryptElement(filename);
            // Note - we don't check the node count, as it will be different
            // due to the encrypted text remainin in the reference nodes
            checkDecryptedDoc(dd, false);
        } else {
            log.warn(
                "Skipping test test_five_element_aes192_cbc_ref as necessary "
                + "crypto algorithms are not available"
            );
        }
    }

    /**
     * Method test_five_data_aes128_cbc
     *
     * Check the merlin-enc-five element data test for AES128 with no
     * key wrap
     */
    @org.junit.Test
    public void test_five_data_aes128_cbc() throws Exception {
        if (haveISOPadding) {
            String filename = 
                "src/test/resources/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-aes128-cbc.xml";

            byte[] decrypt = decryptData(filename);
            checkDecryptedData(decrypt);
        } else {
            log.warn(
                "Skipping test test_five_data_aes128_cbc as necessary "
                + "crypto algorithms are not available"
            );
        }
    }

    /**
     * Method test_five_data_aes256_cbc_3des
     *
     * Check the merlin-enc-five element data test for AES256 with 3DES
     * key wrap
     */
    @org.junit.Test
    public void test_five_data_aes256_cbc_3des() throws Exception {
        if (haveISOPadding && haveKeyWraps) {
            String filename = 
                "src/test/resources/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-aes256-cbc-kw-tripledes.xml";

            byte[] decrypt = decryptData(filename);
            checkDecryptedData(decrypt);
        } else {
            log.warn(
                "Skipping test test_five_data_aes256_cbc_3des as necessary "
                + "crypto algorithms are not available"
            );
        }
    }

    /**
     * Method test_five_data_aes192_cbc_aes256
     *
     * Check the merlin-enc-five element data test for AES192 with AES256
     * key wrap
     */
    @org.junit.Test
    public void test_five_data_aes192_cbc_aes256() throws Exception {
        if (haveISOPadding && haveKeyWraps) {
            String filename = 
                "src/test/resources/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-aes192-cbc-kw-aes256.xml";

            byte[] decrypt = decryptData(filename);
            checkDecryptedData(decrypt);
        } else {
            log.warn(
                "Skipping test test_five_data_aes192_cbc_aes256 as necessary "
                + "crypto algorithms are not available"
            );
        }
    }

    /**
     * Method test_five_data_3des_cbc_rsa_oaep
     *
     * Check the merlin-enc-five element data test for 3DES with
     * RSA key wrap (OAEP and no parameters)
     */
    @org.junit.Test
    public void test_five_data_3des_cbc_rsa_oaep() throws Exception {
        if (haveISOPadding) {
            String filename = 
                "src/test/resources/ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-tripledes-cbc-rsa-oaep-mgf1p.xml";

            byte[] decrypt = decryptData(filename);
            checkDecryptedData(decrypt);
        } else {
            log.warn(
                "Skipping test test_five_data_3des_cbc_rsa_oaep as necessary "
                + "crypto algorithms are not available"
            );
        }
    }

    /**
     * Method decryptElement
     *
     * Take a key, encryption type and a file, find an encrypted element
     * decrypt it and return the resulting document
     *
     * @param filename File to decrypt from
     */
    private Document decryptElement(String filename) throws Exception {
        XMLCipher cipher;

        // Parse the document in question

        String basedir = System.getProperty("basedir");
        if (basedir != null && !"".equals(basedir)) {
            filename = basedir + "/" + filename;
        }
        File f = new File(filename);

        DocumentBuilder db = XMLUtils.createDocumentBuilder(false);
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
    private byte[] decryptData(String filename) throws Exception {

        XMLCipher cipher;

        // Parse the document in question
        String basedir = System.getProperty("basedir");
        if (basedir != null && !"".equals(basedir)) {
            filename = basedir + "/" + filename;
        }
        File f = new File(filename);

        DocumentBuilder db = XMLUtils.createDocumentBuilder(false);
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

        return cipher.decryptToByteArray(ee);
    }

    /** 
     * Method mapKeyName
     *
     * Create a secret key from a key name for merlin-five
     *
     * @param name Name to map a key from
     */
    private SecretKey mapKeyName(String name) throws Exception {
        if (name.equals("job")) {
            // Job is a AES-128 key
            SecretKey key = new SecretKeySpec(jobBytes, "AES");
            return key;
        }
        if (name.equals("jeb")) {
            // Jeb is a AES-192 key
            SecretKey key = new SecretKeySpec(jebBytes, "AES");
            return key;
        }
        if (name.equals("jed")) {
            // Jed is a AES-256 key
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
    private Key findKey(EncryptedData encryptedData) throws Exception {
        KeyInfo ki = encryptedData.getKeyInfo();

        Key key = null;
        Key kek = null;

        if (ki == null) {
            return null;
        }

        // First check for a known key name
        KeyName keyName = ki.itemKeyName(0);
        if (keyName != null) {
            return (mapKeyName(keyName.getKeyName()));
        }

        // Decrypt any encryptedKey structures
        EncryptedKey encryptedKey = ki.itemEncryptedKey(0);
        if (encryptedKey == null) {
            return null;
        }

        KeyInfo kiek = encryptedKey.getKeyInfo();
        if (kiek == null) {
            return null;
        }

        KeyName kekKeyName = kiek.itemKeyName(0);
        if (kekKeyName != null) {
            kek = mapKeyName(kekKeyName.getKeyName());
        } else {
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
            cipher.init(XMLCipher.UNWRAP_MODE, kek);
            key = 
                cipher.decryptKey(
                    encryptedKey, encryptedData.getEncryptionMethod().getAlgorithm()
                );
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
        log.debug("Retrieved Credit Card : " + cc);
        assertTrue(cc, ((cc!= null) && (cc.equals(cardNumber))));

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
     * Check a decrypt of data was OK
     */
    private void checkDecryptedData(byte[] data) throws Exception {
        String input = new String(data, "ASCII");
        assertEquals(testDecryptString, input);
    }
}
