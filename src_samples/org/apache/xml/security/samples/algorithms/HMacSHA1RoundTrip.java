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
package org.apache.xml.security.samples.algorithms;



import org.w3c.dom.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.signature.*;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import javax.xml.parsers.*;
import java.security.*;
import java.security.cert.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;


/**
 *
 * @author $Author$
 */
public class HMacSHA1RoundTrip {

   /**
    * Method main
    *
    * @param unused
    * @throws Exception
    */
   public static void main(String unused[]) throws Exception {
      //J-
      String keystoreType = "JKS";
      String keystoreFile = "data/org/apache/xml/security/samples/input/keystore.jks";
      String keystorePass = "xmlsecurity";
      String privateKeyAlias = "test";
      String privateKeyPass = "xmlsecurity";
      String certificateAlias = "test";
      File signatureFile = new File("signature.xml");
      //J+
      KeyStore ks = KeyStore.getInstance(keystoreType);
      FileInputStream fis = new FileInputStream(keystoreFile);

      ks.load(fis, keystorePass.toCharArray());

      PrivateKey privateKey = (PrivateKey) ks.getKey(privateKeyAlias,
                                 privateKeyPass.toCharArray());
      X509Certificate cert =
         (X509Certificate) ks.getCertificate(certificateAlias);
      PublicKey publicKey = cert.getPublicKey();
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.newDocument();

      // SignatureAlgorithm sa = SignatureAlgorithm.getInstance(doc, XMLSignature.ALGO_ID_SIGNATURE_DSA, 120);
      SignatureAlgorithm sa = new SignatureAlgorithm(doc,
                                 "http://www.w3.org/2000/09/xmldsig#dsa-sha1");

      sa.initSign(privateKey);
      sa.update("sdjhfkjashkjf".getBytes());

      byte signatureValue[] = sa.sign();

      System.out.println(Base64.encode(signatureValue));
      doc.appendChild(sa.getElement());
      XMLUtils.outputDOM(doc, System.out);
      System.out.println("");
      System.out.println("");

      SignatureAlgorithm verifyer =
         new SignatureAlgorithm(doc.getDocumentElement(), "file:");

      verifyer.initVerify(publicKey);
      verifyer.update("sdjhfkjashkjf".getBytes());

      boolean result = verifyer.verify(signatureValue);

      if (result) {
         System.out.println("It verified");
      } else {
         System.out.println("It failed");
      }
   }

   /**
    * Method mainSha1
    *
    * @param unused
    * @throws Exception
    */
   public static void mainSha1(String unused[]) throws Exception {

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.newDocument();

      // SignatureAlgorithm sa = SignatureAlgorithm.getInstance(doc, XMLSignature.ALGO_ID_SIGNATURE_DSA, 120);
      SignatureAlgorithm sa = new SignatureAlgorithm(doc,
                                 "http://www.w3.org/2000/09/xmldsig#hmac-sha1",
                                 33);

      // SecretKeyFactory skf = SecretKeyFactory.getInstance(sa.getJCEAlgorithmString(), sa.getJCEProviderName());
      byte keybytes[] = "01234567890123456789".getBytes();
      SecretKey sk = new SecretKeySpec(keybytes, sa.getJCEAlgorithmString());

      sa.initSign(sk);
      sa.update("sdjhfkjashkjf".getBytes());

      byte signatureValue[] = sa.sign();

      System.out.println(Base64.encode(signatureValue));
      doc.appendChild(sa.getElement());
      XMLUtils.outputDOM(doc, System.out);
      System.out.println("");
      System.out.println("");

      javax.crypto.Mac a;
      SignatureAlgorithm verifyer =
         new SignatureAlgorithm(doc.getDocumentElement(), "file:");
      SecretKey pk = new SecretKeySpec("01234567890123456789".getBytes(),
                                       verifyer.getJCEAlgorithmString());

      verifyer.initVerify(pk);
      verifyer.update("sdjhfkjashkjf".getBytes());

      boolean result = verifyer.verify(signatureValue);

      if (result) {
         System.out.println("It verified");
      } else {
         System.out.println("It failed");
      }
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
