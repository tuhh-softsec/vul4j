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
 * Class TestSignature
 *
 * @author $Author$
 * @version $Revision$
 */
public class TestSignature {

   /**
    * Method main
    *
    * @param unused
    */
   public static void main(String unused[]) {

      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      //J-
      String merlinsDir =
         "data/ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/";
      String ourDir =
         "data/org/apache/xml/security/temp/key/";
      String filenames[] = { merlinsDir +
                             /* 0 */ "signature-keyname.xml",
                             merlinsDir +
                             /* 1 */ "signature-retrievalmethod-rawx509crt.xml",
                             merlinsDir +
                             /* 2 */ "signature-x509-crt-crl.xml",
                             merlinsDir +
                             /* 3 */ "signature-x509-crt.xml",
                             merlinsDir +
                             /* 4 */ "signature-x509-is.xml",
                             merlinsDir +
                             /* 5 */ "signature-x509-ski.xml",
                             merlinsDir +
                             /* 6 */ "signature-x509-sn.xml",
                             ourDir +
                             /* 7 */ "signature-retrievalmethod-x509data.xml"
                             };
      //J+
      int start = 0;
      int end = filenames.length;

      // int end = filenames.length;
      for (int file_to_verify = start; file_to_verify < end; file_to_verify++) {
         try {
            String filename = filenames[file_to_verify];
            File f = new File(filename);

            System.out.println("");
            System.out.println(
               "#########################################################");
            System.out.println("Try to verify " + f.toURL().toString());

            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc =
               db.parse(new java.io.FileInputStream(filename));

            //create a namespace context for use in the XPath expression below
            Element nscontext = XMLUtils.createDSctx(doc, "ds",
                                                     Constants.SignatureSpecNS);

            //retrieve the signature Element from the document
            Element sigElement = (Element) XPathAPI.selectSingleNode(doc,
                                    "//ds:Signature[1]", nscontext);

            //Creates a XMLSignature from the element and uses the filename as
            //the baseURI. That URI is prepended to all relative URIs.
            XMLSignature signature =
               new XMLSignature(sigElement,
                                (new File(filename)).toURL().toString());

            signature.addResourceResolver(new OfflineResolver());

            //Get the KeyInfo object, which might contain some clues as to what
            //key was used to create the signature. It might also contain the
            //full cert.
            KeyInfo ki = signature.getKeyInfo();

            ki.addStorageResolver(new StorageResolver(new org.apache.xml
               .security.keys.storage.implementations
               .CertsInFilesystemDirectoryResolver(merlinsDir + "certs")));

            if (ki != null) {

               //First try to see if it is an X509Cert
               X509Certificate cert =
                  signature.getKeyInfo().getX509Certificate();

               if (cert != null) {

                  //check if the signature is valid using the cert
                  System.out.println("Check: "
                                     + signature.checkSignatureValue(cert));
               } else {

                  //Maybe it's a public key
                  PublicKey pk = signature.getKeyInfo().getPublicKey();

                  if (pk != null) {

                     //check if the signature is valid using the public key
                     System.out.println("Check: "
                                        + signature.checkSignatureValue(pk));
                  } else {

                     //No X509Cert or PublicKey could be found.
                     System.out
                        .println("Could not find Certificate or PublicKey");
                  }
               }
            } else {

               //If the signature did not contain any KeyInfo element
               System.out.println("Could not find ds:KeyInfo");
            }
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
   }
}
