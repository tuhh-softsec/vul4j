
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
package org.apache.xml.security.samples.keys;



import java.io.*;
import java.lang.reflect.*;
import java.security.*;
import java.security.cert.*;
import java.util.*;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.*;
import org.apache.xml.security.keys.content.*;
import org.apache.xml.security.keys.content.x509.*;
import org.apache.xml.security.keys.keyresolver.*;
import org.apache.xml.security.keys.storage.*;
import org.apache.xml.security.keys.storage.implementations.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.Init;


/**
 *
 * @author $Author$
 */
public class RetrievePublicKeys {

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
                             /* 7 */ "signature-retrievalmethod-x509data.xml",
                             ourDir +
                             /* 8 */ "signature-retrievalmethod-dsavalue.xml",
                             ourDir +
                             /* 9 */ "retrieval-from-same-doc.xml"
                             };
      //J+
      int start = 0;
      int end = filenames.length;
      // int end = filenames.length;
      for (int filetoverify = start; filetoverify < end;
              filetoverify++) {
         String filename = filenames[filetoverify];

         System.out.println(
            "#########################################################");
         System.out.println("Try to verify " + filename);

         try {
            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc =
               db.parse(new java.io.FileInputStream(filename));
            Element nscontext = XMLUtils.createDSctx(doc, "ds", Constants.SignatureSpecNS);

            Element kiElement = (Element) XPathAPI.selectSingleNode(doc,
                                   "//ds:KeyInfo[1]", nscontext);
            KeyInfo ki = new KeyInfo(kiElement,
                                     (new File(filename)).toURL().toString());
            StorageResolver storageResolver = new StorageResolver(
               new CertsInFilesystemDirectoryResolver(merlinsDir + "certs"));

            ki.addStorageResolver(storageResolver);

            PublicKey pk = ki.getPublicKey();

            System.out.println("PublicKey" + ((pk != null)
                                              ? " found:"
                                              : " not found!!!"));

            if (pk != null) {
               System.out.println("   Format: " + pk.getFormat());
               System.out.println("   Algorithm: " + pk.getAlgorithm());
            }

            System.out.println("   Key: " + pk);
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
   }
}
