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
package org.apache.xml.security.samples.signature;



import java.io.*;
import java.lang.reflect.*;
import java.security.*;
import java.security.cert.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.*;
import javax.xml.transform.TransformerException;
import javax.xml.parsers.*;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.c14n.helper.XPathContainer;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.*;
import org.apache.xml.security.keys.*;
import org.apache.xml.security.keys.content.*;
import org.apache.xml.security.keys.content.x509.*;
import org.apache.xml.security.keys.keyresolver.*;
import org.apache.xml.security.keys.storage.*;
import org.apache.xml.security.keys.storage.implementations.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.Init;
import org.apache.xml.security.samples.utils.resolver.OfflineResolver;
import org.apache.xml.serialize.*;


/**
 * Class CreateDonaldsAdditionalURISignature
 *
 * @author $Author$
 * @version $Revision$
 */
public class CreateDonaldsAdditionalURISignature {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(CreateMerlinsExampleSixteen.class.getName());

   static Document createDocument(DocumentBuilder db) throws Exception {
      Document doc = db.newDocument();
      Element root = doc.createElement("container");
      Element contents = doc.createElement("signedContents");

      doc.appendChild(root);
      XMLUtils.addReturnToElement(root);
      root.appendChild(contents);
      XMLUtils.addReturnToElement(root);
      contents.appendChild(doc.createTextNode("\nSigned item\n\nfor questions, contact geuer-pollmann@nue.et-inf.uni-siegen.de\n"));

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
           DocumentBuilder db, PrivateKey privk, PublicKey pubkey, String SignatureURI, String DigestURI, String filename)
              throws Exception {

      Document doc = createDocument(db);
      Element root = doc.getDocumentElement();

      File f = new File(filename);
      XMLSignature signature = new XMLSignature(doc, f.toURL().toString(),
                                                SignatureURI);
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
           DocumentBuilder db, byte[] mackey, String SignatureURI, String DigestURI, String filename)
              throws Exception {
      System.out.println(SignatureURI + "  ---   " + DigestURI);


      Document doc = createDocument(db);
      Element root = doc.getDocumentElement();

      File f = new File(filename);
      XMLSignature signature = new XMLSignature(doc, f.toURL().toString(),
                                                SignatureURI);
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
