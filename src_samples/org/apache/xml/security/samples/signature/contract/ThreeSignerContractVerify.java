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
import java.security.PublicKey;
import java.security.cert.*;
import java.util.*;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;
import org.apache.xml.security.c14n.helper.XPathContainer;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.*;
import org.apache.xml.security.keys.*;
import org.apache.xml.security.keys.content.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.Init;


/**
 *
 * @author Rene Kollmorgen <Rene.Kollmorgen@softwareag.com>
 */
public class ThreeSignerContractVerify {

   /**
    * Method main
    *
    * @param unused
    * @throws Exception
    */
   public static void main(String unused[]) throws Exception {

      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);
      dbf.setAttribute("http://xml.org/sax/features/namespaces", Boolean.TRUE);

      try {

         //File signatureFile = new File("collectableSignature.xml");
         File signatureFile = new File("threeSignerContract.xml");
         String BaseURI = signatureFile.toURL().toString();

         System.out.println("Try to verify "
                            + signatureFile.toURL().toString());

         javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();

         db.setErrorHandler(new org.apache.xml.security.utils
            .IgnoreAllErrorHandler());

         org.w3c.dom.Document doc =
            db.parse(new java.io.FileInputStream(signatureFile));
         Element nscontext = XMLUtils.createDSctx(doc, "ds",
                                                  Constants.SignatureSpecNS);
         NodeList signatureElems = XPathAPI.selectNodeList(doc,
                                      "//ds:Signature", nscontext);

         for (int i = 0; i < signatureElems.getLength(); i++) {
            Element sigElement = (Element) signatureElems.item(i);
            XMLSignature signature = new XMLSignature(sigElement, BaseURI);

            //byte[] secretKey = "secretValue".getBytes();
            Element keyName =
               (Element) sigElement
                  .getElementsByTagNameNS(Constants.SignatureSpecNS, "KeyName")
                     .item(0);
            String keyValue = keyName.getFirstChild().getNodeValue();

            System.out
               .println("The XML signature number " + (i + 1) + " in file "
                        + BaseURI + " is "
                        + (signature
                           .checkSignatureValue(signature
                              .createSecretKey(keyValue.getBytes()))
                           ? "valid (good)"
                           : "invalid !!!!! (bad)"));

            SignedInfo s = signature.getSignedInfo();

            for (int j = 0; j < s.getSignedContentLength(); j++) {
               System.out.println("################ Signed Resource " + i + "/"
                                  + j + " ################");
               System.out.println(new String(s.getSignedContentItem(j)));
               System.out.println();
            }
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
