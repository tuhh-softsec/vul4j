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
package org.apache.xml.security.samples.signature.contract;



import java.io.*;
import java.lang.reflect.*;
import java.security.*;
import java.security.cert.*;
import java.util.*;
import javax.xml.transform.TransformerException;
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
 * In the past the protokol to sign data (like a contract) from more than one people
 * looks like this:
 * 1. A signes the hash of the data => SignatureA
 * 2. B signes SignatureA => SignatureB
 * 3. C signes SignatureB => SignatureC
 *
 * To verify e.g. signature C the following steps were necessary:
 * 1. Verify signature C thereby decrypt SignatureC (SignatureB)
 * 2. Verify signature B thereby decrypt SignatureB (SignatureA)
 * 3. Verify signature A thereby decrypt SignatureA (hash of the data)
 * 4. Compare the calculated hash of the sent contract with the decrypted SignatureA result
 *
 * XML-Signatures are more flexible in this way.
 * It is possible to sign data in steps from different signers and
 * verify a signature independent from the others signatures.
 * Furthermore all the signed data and the signatures can be hold in one file.
 *
 * @author Rene Kollmorgen <Rene.Kollmorgen@softwareag.com>
 */
public class ThreeSignerContractSign {

   /**
    * Method main
    *
    * @param unused
    * @throws Exception
    */
   public static void main(String unused[]) throws Exception {
      //J-
      File signatureFile = new File("threeSignerContract.xml");
      String BaseURI = signatureFile.toURL().toString();
      //J+
      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
      org.w3c.dom.Document doc = db.newDocument();
      Element contract = doc.createElement("contract");

      // create contract ////////////////////////////////////////////
      doc.appendChild(contract);

      // beautifying //////
      Element condition1 = doc.createElement("condition1");

      condition1.setAttribute("Id", "cond1");
      condition1.appendChild(
         doc.createTextNode(
            "condition1 not covered in first signature, only binding for the second and third signer"));

      Element condition2 = doc.createElement("condition2");

      condition2.appendChild(doc.createTextNode("condition2"));

      Element condition3 = doc.createElement("condition3");

      condition3.appendChild(doc.createTextNode("condition3"));
      contract.appendChild(doc.createTextNode("\n"));
      contract.appendChild(condition1);
      contract.appendChild(doc.createTextNode("\n"));
      contract.appendChild(condition2);
      contract.appendChild(doc.createTextNode("\n"));
      contract.appendChild(condition3);
      contract.appendChild(doc.createTextNode("\n"));

      //J-
      String id1 = "firstSigner";
      String id2 = "secondSigner";
      String id3 = "thirdSigner";

      // sign the whole contract and no signature and exclude condition1
      String xp1Old = "not(ancestor-or-self::ds:Signature)"
            + " and not(ancestor-or-self::node()[@Id='cond1'])";

      // sign the contract with condition2 and codition3 and no signature
      String xp1 = "not(ancestor-or-self::ds:Signature)" + "\n"
                + " and (" + "\n"
                + "    (ancestor-or-self::node() = /contract/condition2) " + "\n"
                + " or (ancestor-or-self::node() = /contract/condition3) " + "\n"
                + " or (self::node() = /contract) " + "\n"
                + " or ((parent::node() = /contract) and (self::text()))" + "\n"
                + ")";

      // sign the whole contract and no signature but the first
      String xp2 = "not(ancestor-or-self::ds:Signature)" + "\n"
                 + " or ancestor-or-self::ds:Signature[@Id='" + id1 + "']";

      // sign the whole contract and no signature but the first and the second
      String xp3 = "not(ancestor-or-self::ds:Signature)" + "\n"
                 + " or ancestor-or-self::ds:Signature[@Id='" + id1 + "']" + "\n"
                 + " or ancestor-or-self::ds:Signature[@Id='" + id2 + "']";
      //J+
      //////////////////////////////////////////////////////////////////
      // first signer //////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////
      XMLSignature firstSigner =
         new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

      firstSigner.setId(id1);
      contract.appendChild(firstSigner.getElement());

      String rootnamespace = contract.getNamespaceURI();
      boolean rootprefixed = (rootnamespace != null)
                             && (rootnamespace.length() > 0);
      String rootlocalname = contract.getNodeName();
      Transforms transforms = new Transforms(doc);
      XPathContainer xpath = new XPathContainer(doc);

      xpath.setXPathNamespaceContext("ds", Constants.SignatureSpecNS);
      xpath.setXPath("\n" + xp1 + "\n");
      transforms.addTransform(Transforms.TRANSFORM_XPATH,
                              xpath.getElementPlusReturns());
      firstSigner.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);

      {

         // not really secure ///////////////////
         firstSigner.getKeyInfo().add(new KeyName(doc, "First signer key"));

         ////////////////////////////////////////////////
         System.out.println("First signer: Start signing");
         firstSigner
            .sign(firstSigner.createSecretKey("First signer key".getBytes()));
         System.out.println("First signer: Finished signing");
      }

      SignedInfo s = firstSigner.getSignedInfo();

      for (int i = 0; i < s.getSignedContentLength(); i++) {
         System.out.println("################ Signed Resource " + i
                            + " ################");
         System.out.println(new String(s.getSignedContentItem(i)));
         System.out.println();
      }

      //////////////////////////////////////////////////////////////////
      // second signer /////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////
      XMLSignature secondSigner = new XMLSignature(doc, BaseURI,
                                     XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

      secondSigner.setId(id2);
      contract.appendChild(secondSigner.getElement());

      Transforms transforms2 = new Transforms(doc);
      XPathContainer xpath2 = new XPathContainer(doc);

      xpath2.setXPathNamespaceContext("ds", Constants.SignatureSpecNS);
      xpath2.setXPath("\n" + xp2 + "\n");
      transforms2.addTransform(Transforms.TRANSFORM_XPATH,
                               xpath2.getElementPlusReturns());
      secondSigner.addDocument("", transforms2, Constants.ALGO_ID_DIGEST_SHA1);

      {
         secondSigner.getKeyInfo().add(new KeyName(doc, "Second signer key"));
         System.out.println("Second signer: Start signing");
         secondSigner
            .sign(secondSigner.createSecretKey("Second signer key".getBytes()));
         System.out.println("Second signer: Finished signing");
      }

      SignedInfo s2 = secondSigner.getSignedInfo();

      for (int i = 0; i < s2.getSignedContentLength(); i++) {
         System.out.println("################ Signed Resource " + i
                            + " ################");
         System.out.println(new String(s2.getSignedContentItem(i)));
         System.out.println();
      }

      //////////////////////////////////////////////////////////////////
      // third signer //////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////
      XMLSignature thirdSigner =
         new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

      thirdSigner.setId(id3);
      contract.appendChild(thirdSigner.getElement());

      Transforms transforms3 = new Transforms(doc);
      XPathContainer xpath3 = new XPathContainer(doc);

      xpath3.setXPathNamespaceContext("ds", Constants.SignatureSpecNS);
      xpath3.setXPath("\n" + xp3 + "\n");
      transforms3.addTransform(Transforms.TRANSFORM_XPATH,
                               xpath3.getElementPlusReturns());
      thirdSigner.addDocument("", transforms3, Constants.ALGO_ID_DIGEST_SHA1);

      {
         thirdSigner.getKeyInfo().add(new KeyName(doc, "Third signer key"));
         System.out.println("Third signer: Start signing");
         thirdSigner
            .sign(thirdSigner.createSecretKey("Third signer key".getBytes()));
         System.out.println("Third signer: Finished signing");
      }

      SignedInfo s3 = thirdSigner.getSignedInfo();

      for (int i = 0; i < s3.getSignedContentLength(); i++) {
         System.out.println("################ Signed Resource " + i
                            + " ################");
         System.out.println(new String(s3.getSignedContentItem(i)));
         System.out.println();
      }

      //////////////////////////////////////////////////////////////////
      // write away files
      //////////////////////////////////////////////////////////////////
      FileOutputStream f = new FileOutputStream(signatureFile);

      XMLUtils.outputDOMc14nWithComments(doc, f);
      f.close();
      System.out.println("Wrote signature to " + BaseURI);
   }

   static {
      org.apache.xml.security.Init.init();

      // org.apache.xml.security.utils.Constants.setSignatureSpecNSprefix("");
   }
}
