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

import java.io.ByteArrayInputStream;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerFactory;

import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EncryptContentTest extends org.junit.Assert {

    static org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(EncryptContentTest.class);

    private static final String DATA =
        "<users>\n" +
        "  <user>\n" +
        "    <firstname>Bugs</firstname>\n" +
        "    <lastname>Bunny</lastname>\n" +
        "    <age>34</age>\n" +
        "    <serial>Y10</serial>\n" +
        "  </user>\n" +
        "</users>\n";
    
    private static final String MULTIPLE_USER_DATA =
        "<users>\n" +
        "  <user>\n" +
        "    <firstname>Bugs</firstname>\n" +
        "    <lastname>Bunny</lastname>\n" +
        "  </user>\n" +
        "  <user>\n" +
        "    <firstname>Daffy</firstname>\n" +
        "    <lastname>Duck</lastname>\n" +
        "  </user>\n" +
        "</users>\n";

    private DocumentBuilder db;
    private SecretKey secretKey;
    private boolean haveISOPadding;

    public EncryptContentTest() throws Exception {
        org.apache.xml.security.Init.init();
        db = XMLUtils.createDocumentBuilder(false);

        byte[] bits192 = "abcdefghijklmnopqrstuvwx".getBytes();
        DESedeKeySpec keySpec = new DESedeKeySpec(bits192);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        secretKey = keyFactory.generateSecret(keySpec);

        TransformerFactory tf = TransformerFactory.newInstance();
        tf.newTransformer();

        // Determine if we have ISO 10126 Padding - needed for Bulk AES or
        // 3DES encryption

        haveISOPadding = false;
        String algorithmId = 
            JCEMapper.translateURItoJCEID(
                org.apache.xml.security.utils.EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128
            );

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
    }

    @org.junit.Test
    public void testContentRemoved() throws Exception {
        if (!haveISOPadding) {
            log.warn("Test testContentRemoved skipped as necessary algorithms not available");
            return;
        }

        Document doc = db.parse(new ByteArrayInputStream(DATA.getBytes("UTF8")));
        NodeList dataToEncrypt = doc.getElementsByTagName("user");

        XMLCipher dataCipher = XMLCipher.getInstance(XMLCipher.TRIPLEDES);
        dataCipher.init(XMLCipher.ENCRYPT_MODE, secretKey);

        for (int i = 0; i < dataToEncrypt.getLength(); i++) {
            dataCipher.doFinal(doc,(Element) dataToEncrypt.item(i), true);
        }

        // Check that user content has been removed
        Element user = (Element) dataToEncrypt.item(0);
        Node child = user.getFirstChild();
        while (child != null && child.getNodeType() != Node.ELEMENT_NODE) {
            child = child.getNextSibling();
        }
        
        // child should be EncryptedData, if not throw exception
        Element childElem = (Element) child;
        if (!childElem.getLocalName().equals("EncryptedData")) {
            // t.transform(new DOMSource(doc), new StreamResult(System.out));
            throw new Exception("Element content not replaced");
        }
        
        // there shouldn't be any more children elements
        Node sibling = childElem.getNextSibling();
        while (sibling != null && sibling.getNodeType() != Node.ELEMENT_NODE) {
            sibling = sibling.getNextSibling();
        }
        
        if (sibling != null) {
            // t.transform(new DOMSource(doc), new StreamResult(System.out));
            throw new Exception("Sibling element content not replaced");
        }

        // t.transform(new DOMSource(doc), new StreamResult(System.out));
    }
    
    /**
     * See SANTUARIO-301:
     * https://issues.apache.org/jira/browse/SANTUARIO-301
     */
    @org.junit.Test
    public void testMultipleKeyInfoElements() throws Exception {
        if (!haveISOPadding) {
            log.warn("Test testMultipleKeyInfoElements skipped as necessary algorithms not available");
            return;
        }

        Document doc = db.parse(new ByteArrayInputStream(MULTIPLE_USER_DATA.getBytes("UTF8")));
        NodeList dataToEncrypt = doc.getElementsByTagName("user");

        XMLCipher dataCipher = XMLCipher.getInstance(XMLCipher.TRIPLEDES);
        dataCipher.init(XMLCipher.ENCRYPT_MODE, secretKey);

        KeyInfo keyInfo = new KeyInfo(doc);
        keyInfo.addKeyName("mykey");

        EncryptedData encryptedData = dataCipher.getEncryptedData();
        encryptedData.setKeyInfo(keyInfo);
        
        for (int i = 0; i < dataToEncrypt.getLength(); i++) {
            dataCipher.doFinal(doc,(Element) dataToEncrypt.item(i), true);
        }

        NodeList keyInfoList = doc.getElementsByTagNameNS(Constants.SignatureSpecNS, "KeyInfo");
        assertEquals(keyInfoList.getLength(), 2);
    }
    
}
