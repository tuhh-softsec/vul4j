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
package org.apache.xml.security.test.dom.keys.keyresolver;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;

import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.keys.content.x509.XMLX509IssuerSerial;
import org.apache.xml.security.keys.content.x509.XMLX509SKI;
import org.apache.xml.security.keys.content.x509.XMLX509SubjectName;
import org.apache.xml.security.keys.keyresolver.KeyResolver;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.keyresolver.implementations.PrivateKeyResolver;
import org.apache.xml.security.keys.keyresolver.implementations.SecretKeyResolver;
import org.apache.xml.security.keys.keyresolver.implementations.SingleKeyResolver;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.keys.storage.implementations.KeyStoreResolver;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * KeyResolver test.
 */
public class KeyResolverTest extends org.junit.Assert {

    private static final String BASEDIR = System.getProperty("basedir");
    private static final String SEP = System.getProperty("file.separator");

    public KeyResolverTest() {
        org.apache.xml.security.Init.init();
    }

    /**
     * Test key resolvers through a KeyInfo.
     */
    @org.junit.Test
    public void testKeyResolvers() throws Exception {
        char[] pwd = "secret".toCharArray();
        KeyStore ks = KeyStore.getInstance("JCEKS");
        FileInputStream fis = null;
        if (BASEDIR != null && !"".equals(BASEDIR)) {
            fis = new FileInputStream(BASEDIR + SEP + "src/test/resources/test.jceks");
        } else {
            fis = new FileInputStream("src/test/resources/test.jceks");
        }
        ks.load(fis, pwd);

        X509Certificate cert = (X509Certificate)ks.getCertificate("rsakey");
        PublicKey publicKey = cert.getPublicKey();
        PrivateKey privateKey = (PrivateKey) ks.getKey("rsakey", pwd);
        SecretKey secretKey = (SecretKey) ks.getKey("des3key", pwd);

        StorageResolver storage = new StorageResolver(new KeyStoreResolver(ks));
        KeyResolverSpi privateKeyResolver = new PrivateKeyResolver(ks, pwd);
        KeyResolverSpi secretKeyResolver = new SecretKeyResolver(ks, pwd);

        DocumentBuilder db = XMLUtils.createDocumentBuilder(false);
        Document doc = db.newDocument();

        KeyInfo ki;
        X509Data x509data;

        // X509Certificate hint
        ki = new KeyInfo(doc);
        ki.addStorageResolver(storage);
        x509data = new X509Data(doc);
        x509data.add(new XMLX509Certificate(doc, cert));
        ki.add(x509data);
        assertEquals(publicKey, ki.getPublicKey());

        assertNull(ki.getPrivateKey());
        ki.registerInternalKeyResolver(privateKeyResolver);
        assertEquals(privateKey, ki.getPrivateKey());

        // Issuer/Serial hint
        ki = new KeyInfo(doc);
        ki.addStorageResolver(storage);
        x509data = new X509Data(doc);
        x509data.add(new XMLX509IssuerSerial(doc, cert.getIssuerX500Principal().getName(), cert.getSerialNumber()));
        ki.add(x509data);
        assertEquals(publicKey, ki.getPublicKey());

        ki.registerInternalKeyResolver(privateKeyResolver);
        assertEquals(privateKey, ki.getPrivateKey());

        // SubjectName hint
        ki = new KeyInfo(doc);
        ki.addStorageResolver(storage);
        x509data = new X509Data(doc);
        x509data.add(new XMLX509SubjectName(doc, cert.getSubjectX500Principal().getName()));
        ki.add(x509data);
        assertEquals(publicKey, ki.getPublicKey());

        ki.registerInternalKeyResolver(privateKeyResolver);
        assertEquals(privateKey, ki.getPrivateKey());

        // SKI hint
        ki = new KeyInfo(doc);
        ki.addStorageResolver(storage);
        x509data = new X509Data(doc);
        x509data.add(new XMLX509SKI(doc, cert));
        ki.add(x509data);
        assertEquals(publicKey, ki.getPublicKey());

        ki.registerInternalKeyResolver(privateKeyResolver);
        assertEquals(privateKey, ki.getPrivateKey());

        // KeyName hint
        String rsaKeyName = "rsakey";
        ki = new KeyInfo(doc);
        ki.addKeyName(rsaKeyName);
        ki.registerInternalKeyResolver(new SingleKeyResolver(rsaKeyName, publicKey));
        assertEquals(publicKey, ki.getPublicKey());

        ki = new KeyInfo(doc);
        ki.addKeyName(rsaKeyName);
        ki.registerInternalKeyResolver(privateKeyResolver);
        assertEquals(privateKey, ki.getPrivateKey());

        ki = new KeyInfo(doc);
        ki.addKeyName(rsaKeyName);
        ki.registerInternalKeyResolver(new SingleKeyResolver(rsaKeyName, privateKey));
        assertEquals(privateKey, ki.getPrivateKey());

        String des3KeyName = "des3key";
        ki = new KeyInfo(doc);
        ki.addKeyName(des3KeyName);
        ki.registerInternalKeyResolver(secretKeyResolver);
        assertEquals(secretKey, ki.getSecretKey());

        ki = new KeyInfo(doc);
        ki.addKeyName(des3KeyName);
        ki.registerInternalKeyResolver(new SingleKeyResolver(des3KeyName, secretKey));
        assertEquals(secretKey, ki.getSecretKey());
    }
    
    /**
     * Encrypt some data, embedded the data encryption key
     * in the message using the key transport algorithm rsa-1_5.
     * Decrypt the data by resolving the Key Encryption Key.
     * This test verifies if a KeyResolver can return a PrivateKey.
     */
    @org.junit.Test
    public void testResolvePrivateKey() throws Exception {
        // See if AES-128 is available...
        String algorithmId = 
            JCEMapper.translateURItoJCEID(
                    org.apache.xml.security.utils.EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128
                );
        boolean haveAES = false;
        if (algorithmId != null) {
            try {
                if (Cipher.getInstance(algorithmId) != null) {
                    haveAES = true;
                }
            } catch (NoSuchAlgorithmException nsae) {
                //
            } catch (NoSuchPaddingException nspe) {
                //
            }
        }
        
        if (!haveAES) {
            return;
        }
        
        // Create a sample XML document
        Document document = XMLUtils.createDocumentBuilder(false).newDocument();

        Element rootElement = document.createElement("root");
        document.appendChild(rootElement);
        Element elem = document.createElement("elem");
        Text text = document.createTextNode("text");
        elem.appendChild(text);
        rootElement.appendChild(elem);

        // Create a data encryption key
        byte[] keyBytes = { 0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7 };
        SecretKeySpec dataEncryptKey = new SecretKeySpec(keyBytes, "AES");

        // Create public and private keys
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(
                new BigInteger(
                    "8710a2bcb2f3fdac177f0ae0461c2dd0ebf72e0d88a5400583a7d8bdabd6" +
                    "ae009d30cfdf6acb5b6a64cdc730bc630a39d946d08babffe62ea20a87e37c93b3b0e8a8e576045b" +
                    "bddfbde83ca9bfa180fe6a5f5eee60661936d728314e809201ef52cd71d9fa3c8ce83f9d30ab5e08" +
                    "1539219e7e45dd6a60be65ac95d2049b8f21", 16),
                new BigInteger("10001", 16));
        
        RSAPrivateKeySpec privKeySpec = new RSAPrivateKeySpec(
                new BigInteger(
                    "8710a2bcb2f3fdac177f0ae0461c2dd0ebf72e0d88a5400583a7d8bdabd" +
                    "6ae009d30cfdf6acb5b6a64cdc730bc630a39d946d08babffe62ea20a87e37c93b3b0e8a8e576045" +
                    "bbddfbde83ca9bfa180fe6a5f5eee60661936d728314e809201ef52cd71d9fa3c8ce83f9d30ab5e0" +
                    "81539219e7e45dd6a60be65ac95d2049b8f21", 16),
                new BigInteger(
                    "20c39e569c2aa80cc91e5e6b0d56e49e5bbf78827bf56a546c1d996c597" +
                    "5187cb9a50fa828e5efe51d52f5d112c20bc700b836facadca6e0051afcdfe866841e37d207c0295" +
                    "36ff8674b301e2198b2c56abb0a0313f8ff84c1fcd6fa541aa6e5d9c018fab4784d2940def5dc709" +
                    "ddc714d73b6c23b5d178eaa5933577b8e8ae9", 16));
        
        RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);
        RSAPrivateKey privKey = (RSAPrivateKey) keyFactory.generatePrivate(privKeySpec);

        // Encrypt the data encryption key with the key encryption key
        XMLCipher keyCipher = XMLCipher.getInstance(XMLCipher.RSA_v1dot5);
        keyCipher.init(XMLCipher.WRAP_MODE, pubKey);
        EncryptedKey encryptedKey = keyCipher.encryptKey(document, dataEncryptKey);
        
        String keyName = "testResolvePrivateKey";
        KeyInfo kekInfo = new KeyInfo(document);
        kekInfo.addKeyName(keyName);
        encryptedKey.setKeyInfo(kekInfo);

        // Encrypt the data
        XMLCipher xmlCipher = XMLCipher.getInstance(XMLCipher.AES_128);
        xmlCipher.init(XMLCipher.ENCRYPT_MODE, dataEncryptKey);

        EncryptedData encryptedData = xmlCipher.getEncryptedData();
        KeyInfo keyInfo = new KeyInfo(document);
        keyInfo.add(encryptedKey);
        encryptedData.setKeyInfo(keyInfo);

        xmlCipher.doFinal(document, rootElement, true);

        Element encryptedDataElement = (Element) rootElement.getFirstChild();
        assertEquals("EncryptedData", encryptedDataElement.getLocalName());

        // Decrypt the data by resolving the private key used as the KEK
        // First test with an internal KeyResolver
        MyPrivateKeyResolver.pk = privKey;
        MyPrivateKeyResolver.pkName = keyName;
        
        decryptDocument(document, new MyPrivateKeyResolver());

        // Now test with a static KeyResolver
        KeyResolver.registerAtStart(MyPrivateKeyResolver.class.getName(), false);
        KeyResolverSpi resolver = KeyResolver.iterator().next();
        assertEquals(MyPrivateKeyResolver.class.getName(), resolver.getClass().getName());

        decryptDocument(document, null);
    }
    
    private void decryptDocument(Document docSource, KeyResolverSpi internalResolver) throws Exception
    {
        Document document = (Document)docSource.cloneNode(true);
        Element rootElement = document.getDocumentElement();
        Element encryptedDataElement = (Element)rootElement.getFirstChild();

        XMLCipher decryptCipher = XMLCipher.getInstance();
        decryptCipher.init(XMLCipher.DECRYPT_MODE, null);
        if (internalResolver != null) {
            decryptCipher.registerInternalKeyResolver(internalResolver);
        }
        decryptCipher.doFinal(document, encryptedDataElement);

        Element decryptedElement = (Element) rootElement.getFirstChild();
        assertEquals("elem", decryptedElement.getLocalName());
    }

    // A KeyResolver that returns a PrivateKey for a specific KeyName.
    public static class MyPrivateKeyResolver extends KeyResolverSpi {
        
        // We use static variables because KeyResolver.register() demands
        // the use of the default constructor.
        private static PrivateKey pk;
        private static String pkName;
        
        public boolean engineCanResolve(Element element, String BaseURI, StorageResolver storage) {
            return false;
        }

        public PrivateKey engineLookupAndResolvePrivateKey(
            Element element, String BaseURI, StorageResolver storage
        ) throws KeyResolverException {
            if (Constants.SignatureSpecNS.equals(element.getNamespaceURI()) && 
                Constants._TAG_KEYNAME.equals(element.getLocalName())) {
                String keyName = element.getFirstChild().getNodeValue();
                if (pkName.equals(keyName)) {
                    return pk;
                }
            }
            
            return null;
        }
    }
}
