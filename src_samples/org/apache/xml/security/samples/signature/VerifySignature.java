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
import org.apache.xml.security.keys.content.x509.*;
import org.apache.xml.security.keys.keyresolver.*;
import org.apache.xml.security.keys.storage.*;
import org.apache.xml.security.keys.storage.implementations.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.Init;
import org.apache.xml.security.samples.utils.resolver.OfflineResolver;


/**
 *
 *
 *
 *
 * @author $Author$
 *
 */
public class VerifySignature {

   /**
    * Method main
    *
    * @param unused
    */
   public static void main(String unused[]) {

      boolean schemaValidate = false;
      final String signatureSchemaFile = "data/xmldsig-core-schema.xsd";
      String signatureFileName =
         "data/ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloping-rsa.xml";

      if (schemaValidate) {
         System.out.println("We do schema-validation");
      }

      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      if (schemaValidate) {
         dbf.setAttribute("http://apache.org/xml/features/validation/schema",
                          Boolean.TRUE);
         dbf.setAttribute(
            "http://apache.org/xml/features/dom/defer-node-expansion",
            Boolean.TRUE);
         dbf.setValidating(true);
         dbf.setAttribute("http://xml.org/sax/features/validation",
                          Boolean.TRUE);
      }

      dbf.setNamespaceAware(true);
      dbf.setAttribute("http://xml.org/sax/features/namespaces", Boolean.TRUE);

      if (schemaValidate) {
         dbf.setAttribute(
            "http://apache.org/xml/properties/schema/external-schemaLocation",
            Constants.SignatureSpecNS + " " + signatureSchemaFile);
      }

      try {

         // File f = new File("signature.xml");
         File f = new File(signatureFileName);

         System.out.println("Try to verify " + f.toURL().toString());

         javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();

         db.setErrorHandler(new org.apache.xml.security.utils
            .IgnoreAllErrorHandler());

         if (schemaValidate) {
            db.setEntityResolver(new org.xml.sax.EntityResolver() {

               public org.xml.sax.InputSource resolveEntity(
                       String publicId, String systemId)
                          throws org.xml.sax.SAXException {

                  if (systemId.endsWith("xmldsig-core-schema.xsd")) {
                     try {
                        return new org.xml.sax.InputSource(
                           new FileInputStream(signatureSchemaFile));
                     } catch (FileNotFoundException ex) {
                        throw new org.xml.sax.SAXException(ex);
                     }
                  } else {
                     return null;
                  }
               }
            });
         }

         org.w3c.dom.Document doc = db.parse(new java.io.FileInputStream(f));
         Element nscontext = XMLUtils.createDSctx(doc, "ds",
                                                  Constants.SignatureSpecNS);
         Element sigElement = (Element) XPathAPI.selectSingleNode(doc,
                                 "//ds:Signature[1]", nscontext);
         XMLSignature signature = new XMLSignature(sigElement,
                                                   f.toURL().toString());

         signature.addResourceResolver(new OfflineResolver());

         // XMLUtils.outputDOMc14nWithComments(signature.getElement(), System.out);
         KeyInfo ki = signature.getKeyInfo();

         if (ki != null) {
            if (ki.containsX509Data()) {
               System.out
                  .println("Could find a X509Data element in the KeyInfo");
            }

            X509Certificate cert = signature.getKeyInfo().getX509Certificate();

            if (cert != null) {
               System.out.println(
                  "I try to verify the signature using the X509 Certificate: "
                  + cert);
               System.out.println("The XML signature in file "
                                  + f.toURL().toString() + " is "
                                  + (signature.checkSignatureValue(cert)
                                     ? "valid (good)"
                                     : "invalid !!!!! (bad)"));
            } else {
               System.out.println("Did not find a Certificate");

               PublicKey pk = signature.getKeyInfo().getPublicKey();

               if (pk != null) {
                  System.out.println(
                     "I try to verify the signature using the public key: "
                     + pk);
                  System.out.println("The XML signature in file "
                                     + f.toURL().toString() + " is "
                                     + (signature.checkSignatureValue(pk)
                                        ? "valid (good)"
                                        : "invalid !!!!! (bad)"));
               } else {
                  System.out.println(
                     "Did not find a public key, so I can't check the signature");
               }
            }
         } else {
            System.out.println("Did not find a KeyInfo");
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
