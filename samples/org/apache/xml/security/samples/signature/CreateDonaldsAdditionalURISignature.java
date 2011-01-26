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
package org.apache.xml.security.samples.signature;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilder;

import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class CreateDonaldsAdditionalURISignature
 *
 * @author $Author$
 */
public class CreateDonaldsAdditionalURISignature {

    /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(
            CreateDonaldsAdditionalURISignature.class.getName());

    static Document createDocument(DocumentBuilder db) throws Exception {
        Document doc = db.newDocument();
        Element root = doc.createElementNS(null, "container");
        Element contents = doc.createElementNS(null, "signedContents");

        doc.appendChild(root);
        XMLUtils.addReturnToElement(root);
        root.appendChild(contents);
        XMLUtils.addReturnToElement(root);
        contents.appendChild(
            doc.createTextNode(
                "\nSigned item\n\nfor questions, contact geuer-pollmann@nue.et-inf.uni-siegen.de\n"));

        return doc;
    }

    /**
     * Method signAndWrite
     *
     * @param db
     * @param privk
     * @param pubkey
     * @param SignatureURI
     * @param DigestURI
     * @param filename
     * @throws Exception
     */
    public static void signAndWrite(
        DocumentBuilder db, PrivateKey privk, PublicKey pubkey, 
        String SignatureURI, String DigestURI, String filename
    ) throws Exception {

        Document doc = createDocument(db);
        Element root = doc.getDocumentElement();

        File f = new File(filename);
        XMLSignature signature = 
            new XMLSignature(doc, f.toURI().toURL().toString(), SignatureURI);
        Transforms transforms = new Transforms(doc);

        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        signature.addDocument("", transforms, DigestURI);
        signature.addKeyInfo(pubkey);
        root.appendChild(signature.getElement());
        XMLUtils.addReturnToElement(root);
        signature.sign(privk);

        FileOutputStream fos = new FileOutputStream(f);

        XMLUtils.outputDOMc14nWithComments(doc, fos);

        // System.out.println(new String(signature.getSignedInfo().getReferencedContentAfterTransformsItem(0).getBytes()));
    }

    /**
     * Method macAndWrite
     *
     * @param db
     * @param mackey
     * @param SignatureURI
     * @param DigestURI
     * @param filename
     * @throws Exception
     */
    public static void macAndWrite(
        DocumentBuilder db, byte[] mackey, String SignatureURI, String DigestURI, String filename
    ) throws Exception {
        System.out.println(SignatureURI + "  ---   " + DigestURI);

        Document doc = createDocument(db);
        Element root = doc.getDocumentElement();

        File f = new File(filename);
        XMLSignature signature = 
            new XMLSignature(doc, f.toURI().toURL().toString(), SignatureURI);
        Transforms transforms = new Transforms(doc);

        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        signature.addDocument("", transforms, DigestURI);

        SecretKey secretKey = signature.createSecretKey(mackey);

        root.appendChild(signature.getElement());
        XMLUtils.addReturnToElement(root);
        signature.sign(secretKey);

        FileOutputStream fos = new FileOutputStream(f);

        XMLUtils.outputDOMc14nWithComments(doc, fos);

        // System.out.println(new String(signature.getSignedInfo().getReferencedContentAfterTransformsItem(0).getBytes()));
    }

    /**
     * Method main
     *
     * @param unused
     * @throws Exception
     */
    public static void main(String unused[]) throws Exception {

        org.apache.xml.security.Init.init();

        javax.xml.parsers.DocumentBuilderFactory dbf =
            javax.xml.parsers.DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();

        // test digests in references
        macAndWrite(db, "secret".getBytes("UTF-8"),
                    XMLSignature.ALGO_ID_MAC_HMAC_SHA1,
                    MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1,
                    "MacSha1_DigestSha1.xml");
        macAndWrite(db, "secret".getBytes("UTF-8"),
                    XMLSignature.ALGO_ID_MAC_HMAC_SHA1,
                    MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256,
                    "MacSha1_DigestSha256.xml");
        macAndWrite(db, "secret".getBytes("UTF-8"),
                    XMLSignature.ALGO_ID_MAC_HMAC_SHA1,
                    MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA384,
                    "MacSha1_DigestSha384.xml");
        macAndWrite(db, "secret".getBytes("UTF-8"),
                    XMLSignature.ALGO_ID_MAC_HMAC_SHA1,
                    MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA512,
                    "MacSha1_DigestSha512.xml");
        macAndWrite(db, "secret".getBytes("UTF-8"),
                    XMLSignature.ALGO_ID_MAC_HMAC_SHA1,
                    MessageDigestAlgorithm.ALGO_ID_DIGEST_RIPEMD160,
                    "MacSha1_DigestRipemd160.xml");
        macAndWrite(db, "secret".getBytes("UTF-8"),
                    XMLSignature.ALGO_ID_MAC_HMAC_SHA1,
                    MessageDigestAlgorithm.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5,
                    "MacSha1_DigestMd5.xml");

        // test digests in hmacs
        macAndWrite(db, "secret".getBytes("UTF-8"),
                    XMLSignature.ALGO_ID_MAC_HMAC_SHA1,
                    MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1,
                    "MacSha1_DigestSha1.xml");
        macAndWrite(db, "secret".getBytes("UTF-8"),
                    XMLSignature.ALGO_ID_MAC_HMAC_SHA256,
                    MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1,
                    "MacSha256_DigestSha1.xml");
        macAndWrite(db, "secret".getBytes("UTF-8"),
                    XMLSignature.ALGO_ID_MAC_HMAC_SHA384,
                    MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1,
                    "MacSha384_DigestSha1.xml");
        macAndWrite(db, "secret".getBytes("UTF-8"),
                    XMLSignature.ALGO_ID_MAC_HMAC_SHA512,
                    MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1,
                    "MacSha512_DigestSha1.xml");
        macAndWrite(db, "secret".getBytes("UTF-8"),
                    XMLSignature.ALGO_ID_MAC_HMAC_RIPEMD160,
                    MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1,
                    "MacRipemd160_DigestSha1.xml");
        macAndWrite(db, "secret".getBytes("UTF-8"),
                    XMLSignature.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5,
                    MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1,
                    "MacMd5_DigestSha1.xml");

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
        KeyPair keyPair = kpg.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey pubkey = keyPair.getPublic();

        // test digests in RSA
        signAndWrite(db, privateKey, pubkey,
                     XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1,
                     MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1,
                    "SignatureRsaSha1_DigestSha1.xml");
        signAndWrite(db, privateKey, pubkey,
                     XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256,
                     MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1,
                    "SignatureRsaSha256_DigestSha1.xml");
        signAndWrite(db, privateKey, pubkey,
                     XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA384,
                     MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1,
                     "SignatureRsaSha384_DigestSha1.xml");
        signAndWrite(db, privateKey, pubkey,
                     XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA512,
                     MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1,
                     "SignatureRsaSha512_DigestSha1.xml");
        signAndWrite(db, privateKey, pubkey,
                     XMLSignature.ALGO_ID_SIGNATURE_RSA_RIPEMD160,
                     MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1,
                     "SignatureRsaRipemd160_DigestSha1.xml");
        signAndWrite(db, privateKey, pubkey,
                     XMLSignature.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5,
                     MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1,
                     "SignatureRsaMd5_DigestSha1.xml");
    }
    
}
