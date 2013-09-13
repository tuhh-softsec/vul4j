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
package org.apache.xml.security.test.dom.signature;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.xml.security.Init;
import org.apache.xml.security.signature.Reference;
import org.apache.xml.security.signature.SignedInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.reference.ReferenceData;
import org.apache.xml.security.signature.reference.ReferenceNodeSetData;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.implementations.ResolverXPointer;

/**
 * Test a Signature and Validation, and check that we have access to the Element(s) that was
 * validated.
 */
public class SignatureReferenceTest extends org.junit.Assert {
    public static final String DS_NS = "http://www.w3.org/2000/09/xmldsig#";

    private static final String BASEDIR = 
        System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
    public static final String KEYSTORE_DIRECTORY = BASEDIR + "/src/test/resources/";
    public static final String KEYSTORE_PASSWORD_STRING = "changeit";
    public static final char[] KEYSTORE_PASSWORD = KEYSTORE_PASSWORD_STRING.toCharArray();

    public SignatureReferenceTest() throws Exception {
        Init.init();
        ElementProxy.setDefaultPrefix(Constants.SignatureSpecNS, "ds");
    }

    @org.junit.Test
    public void testSigningVerifyingReference() throws Throwable {
        Document doc = getOriginalDocument();
        XMLSignature signature = signDocument(doc);

        PublicKey pubKey = getPublicKey();
        assertTrue(signature.checkSignatureValue(pubKey));
        
        // Check the reference(s)
        SignedInfo signedInfo = signature.getSignedInfo();
        assertTrue(signedInfo.getLength() == 1);
        Reference reference = signedInfo.item(0);
        ReferenceData referenceData = reference.getReferenceData();
        assertNotNull(referenceData);
        assertTrue(referenceData instanceof ReferenceNodeSetData);
        
        // Test the cached Element
        Element referenceElement = 
            (Element)((ReferenceNodeSetData)referenceData).iterator().next();
        assertNotNull(referenceElement);
        assertTrue("root".equals(referenceElement.getLocalName()));
        
        Element originalElement =
            (Element) doc.getElementsByTagNameNS("http://ns.example.org/", "root").item(0);
        assertNotNull(originalElement);
        assertEquals(referenceElement, originalElement);
    }

    /**
     * Loads the 'localhost' keystore from the test keystore.
     * 
     * @return test keystore.
     * @throws Exception
     */
    private KeyStore getKeyStore() throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        InputStream ksis = new FileInputStream(KEYSTORE_DIRECTORY + "test.jks");
        ks.load(ksis, KEYSTORE_PASSWORD);
        ksis.close();
        return ks;
    }

    private PublicKey getPublicKey() throws Exception {
        KeyStore keyStore = getKeyStore();
        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (keyStore.isKeyEntry(alias)) {
                return keyStore.getCertificate(alias).getPublicKey();
            }
        }
        return null;
    }

    private PrivateKey getPrivateKey() throws Exception {
        KeyStore keyStore = getKeyStore();
        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (keyStore.isKeyEntry(alias)) {
                return (PrivateKey) keyStore.getKey(alias, KEYSTORE_PASSWORD);
            }
        }
        return null;
    }

    private Document getOriginalDocument() throws Throwable {
        DocumentBuilder db = XMLUtils.createDocumentBuilder(false);
        Document doc = db.newDocument();

        Element rootElement = doc.createElementNS("http://ns.example.org/", "root");
        rootElement.appendChild(doc.createTextNode("Hello World!"));
        doc.appendChild(rootElement);

        return doc;
    }

    private XMLSignature signDocument(Document doc) throws Throwable {
        XMLSignature sig = new XMLSignature(doc, "", XMLSignature.ALGO_ID_SIGNATURE_DSA);
        Element root = doc.getDocumentElement();
        root.appendChild(sig.getElement());

        sig.getSignedInfo().addResourceResolver(new ResolverXPointer());

        Transforms transforms = new Transforms(doc);
        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
        sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);

        sig.addKeyInfo(getPublicKey());
        sig.sign(getPrivateKey());

        return sig;
    }
}
