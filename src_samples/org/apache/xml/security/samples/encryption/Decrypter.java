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
package org.apache.xml.security.samples.encryption;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.security.Key;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.EncryptionConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;

/**
 * This sample demonstrates how to decrypt data inside an xml document.
 *
 * @author Vishal Mahajan (Sun Microsystems)
 */
public class Decrypter {

    /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(
            Decrypter.class.getName());

    static {
        org.apache.xml.security.Init.init();
    }

    private static Document loadEncryptionDocument() throws Exception {

        String fileName = "encryptedInfo.xml";
        File encryptionFile = new File(fileName);
        javax.xml.parsers.DocumentBuilderFactory dbf =
            javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(encryptionFile);
        System.out.println(
            "Encryption document loaded from " +
            encryptionFile.toURL().toString());
        return document;
    }

    private static SecretKey loadKeyEncryptionKey() throws Exception {

        String fileName = "kek";
        String jceAlgorithmName = "DESede";

        File kekFile = new File(fileName);

        DESedeKeySpec keySpec =
            new DESedeKeySpec(JavaUtils.getBytesFromFile(fileName));
        SecretKeyFactory skf =
             SecretKeyFactory.getInstance(jceAlgorithmName);
        SecretKey key = skf.generateSecret(keySpec);
         
        System.out.println(
            "Key encryption key loaded from " + kekFile.toURL().toString());
        return key;
    }

    private static void outputDocToFile(Document doc, String fileName)
        throws Exception {
        File encryptionFile = new File(fileName);
        FileOutputStream f = new FileOutputStream(encryptionFile);

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(f);
        transformer.transform(source, result);

        f.close();
        System.out.println(
            "Wrote document containing decrypted data to " +
            encryptionFile.toURL().toString());
    }

    public static void main(String unused[]) throws Exception {

        Document document = loadEncryptionDocument();

        Element encryptedDataElement =
            (Element) document.getElementsByTagNameNS(
                EncryptionConstants.EncryptionSpecNS,
                EncryptionConstants._TAG_ENCRYPTEDDATA).item(0);

        /*
         * Load the key to be used for decrypting the xml data
         * encryption key.
         */
        Key kek = loadKeyEncryptionKey();

        String providerName = "BC";

        XMLCipher xmlCipher =
            XMLCipher.getInstance();
        /*
         * The key to be used for decrypting xml data would be obtained
         * from the keyinfo of the EncrypteData using the kek.
         */
        xmlCipher.init(XMLCipher.DECRYPT_MODE, null);
        xmlCipher.setKEK(kek);
        /*
         * The following doFinal call replaces the encrypted data with
         * decrypted contents in the document.
         */
        xmlCipher.doFinal(document, encryptedDataElement);

        outputDocToFile(document, "decryptedInfo.xml");
    }
}
