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
import java.util.*;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
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
import org.apache.xml.security.transforms.params.XPathContainer;
import org.apache.xml.security.Init;
import org.apache.xml.security.samples.utils.resolver.OfflineResolver;
import org.apache.xml.serialize.*;


/**
 * These ones can be used to create Signatures which can be collected
 * using your text editors cut-and-paste feature to create a file wich
 * contains multiple signatures which remain valid after cut-and-paste.
 *
 * This program creates a Signature which can be used for cut-and-paste to be
 * put into a larger document.
 *
 * @author $Author$
 */
public class CreateCollectableSignature {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(CreateCollectableSignature.class.getName());

   /** Field passphrase */
   public static final String passphrase =
      "The super-mega-secret public static passphrase";

   /**
    * Method main
    *
    * @param unused
    * @throws Exception
    */
   public static void main(String unused[]) throws Exception {
      //J-
      File signatureFile = new File("collectableSignature.xml");
      String BaseURI = signatureFile.toURL().toString();
      //J+
      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
      org.w3c.dom.Document doc = db.newDocument();
      Element rootElement = doc.createElement("root");

      doc.appendChild(rootElement);

      /*
      Element signedResourceElement = doc.createElementNS("http://custom/", "custom:signedContent");
      signedResourceElement.setAttribute("xmlns:custom", "http://custom/");
      signedResourceElement.setAttribute("Id", "id0");
      */
      Element signedResourceElement = doc.createElement("signedContent");

      signedResourceElement.appendChild(doc.createTextNode("Signed Text\n"));
      rootElement.appendChild(signedResourceElement);

      XMLSignature sig = new XMLSignature(doc, BaseURI,
                                          XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

      signedResourceElement.appendChild(sig.getElement());

      {
         String rootnamespace = signedResourceElement.getNamespaceURI();
         boolean rootprefixed = (rootnamespace != null)
                                && (rootnamespace.length() > 0);
         String rootlocalname = signedResourceElement.getNodeName();
         Transforms transforms = new Transforms(doc);
         XPathContainer xpath = new XPathContainer(doc);

         xpath.setXPathNamespaceContext("ds", Constants.SignatureSpecNS);

         if (rootprefixed) {
            xpath.setXPathNamespaceContext("root", rootnamespace);
         }

         //J-
         String xpathStr = "\n"
          + "count(                                                                 " + "\n"
          + " ancestor-or-self::" + (rootprefixed ? "root:" : "") + rootlocalname + "" + "\n"
          + " |                                                                     " + "\n"
          + " here()/ancestor::" + (rootprefixed ? "root:" : "") + rootlocalname + "[1] " + "\n"
          + ") <= count(                                                             " + "\n"
          + " ancestor-or-self::" + (rootprefixed ? "root:" : "") + rootlocalname + "" + "\n"
          + ")                                                                      " + "\n"
          + " and                                                                   " + "\n"
          + "count(                                                                 " + "\n"
          + " ancestor-or-self::ds:Signature                                        " + "\n"
          + " |                                                                     " + "\n"
          + " here()/ancestor::ds:Signature[1]                                      " + "\n"
          + ") > count(                                                             " + "\n"
          + " ancestor-or-self::ds:Signature                                        " + "\n"
          + ")                                                                      " + "\n"



          ;
         //J+
         xpath.setXPath(xpathStr);
         transforms.addTransform(Transforms.TRANSFORM_XPATH,
                                 xpath.getElementPlusReturns());
         sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
      }

      {
         sig.getKeyInfo()
            .add(new KeyName(doc, CreateCollectableSignature.passphrase));
         System.out.println("Start signing");
         sig.sign(sig
            .createSecretKey(CreateCollectableSignature.passphrase.getBytes()));
         System.out.println("Finished signing");
      }

      FileOutputStream f = new FileOutputStream(signatureFile);

      XMLUtils.outputDOMc14nWithComments(doc, f);
      f.close();
      System.out.println("Wrote signature to " + BaseURI);

      SignedInfo s = sig.getSignedInfo();

      for (int i = 0; i < s.getSignedContentLength(); i++) {
         System.out.println("################ Signed Resource " + i
                            + " ################");
         System.out.println(new String(s.getSignedContentItem(i)));
         System.out.println();
      }
   }

   static {
      org.apache.xml.security.Init.init();

      // org.apache.xml.security.utils.Constants.setSignatureSpecNSprefix("");
   }
}
