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
package org.apache.xml.security.samples.transforms;



import java.io.*;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.signature.*;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.transforms.params.*;
import org.apache.xml.security.utils.*;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.crypto.*;


/**
 * Sample for the <I>XML Signature XPath Filter v2.0</I>
 *
 * @author $Author$
 * @see <A HREF="http://www.w3.org/TR/xmldsig-filter2/">XPath Filter v2.0 (TR)</A>
 * @see <A HREF=http://www.w3.org/Signature/Drafts/xmldsig-xfilter2/">XPath Filter v2.0 (editors copy)</A>
 */
public class SampleTransformXPath2Filter {

   /**
    * Method main
    *
    * @param args
    * @throws Exception
    */
   public static void main(String args[]) throws Exception {

      org.apache.xml.security.Init.init();

      boolean verbose = true;

      create("withComments.xml", true, verbose);
      System.out.println();
      System.out.println();
      System.out.println();
      create("omitComments.xml", false, verbose);
      System.out.println();
      System.out.println();
      System.out.println();
      check("withComments.xml");
   }

   /**
    * Method create
    *
    * @param filename
    * @param withComments
    * @param verbose
    * @throws Exception
    */
   public static void create(
           String filename, boolean withComments, boolean verbose)
              throws Exception {

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      DocumentBuilder db = dbf.newDocumentBuilder();
      //J-
      String inputDoc =
"<Document>\n" +
"     <ToBeSigned>\n" +
"       <!-- comment -->\n" +
"       <Data />\n" +
"       <NotToBeSigned>\n" +
"         <ReallyToBeSigned>\n" +
"           <!-- comment -->\n" +
"           <Data />\n" +
"         </ReallyToBeSigned>\n" +
"       </NotToBeSigned>\n" +
"     </ToBeSigned>\n" +
"     <ToBeSigned>\n" +
"       <Data />\n" +
"       <NotToBeSigned>\n" +
"         <Data />\n" +
"       </NotToBeSigned>\n" +
"     </ToBeSigned>\n" +
"</Document>";
      //J+
      Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
      XMLSignature sig = new XMLSignature(doc, null,
                                          XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

      doc.getDocumentElement().appendChild(sig.getElement());
      doc.getDocumentElement().appendChild(doc.createTextNode("\n"));

      Transforms transforms = new Transforms(doc);

      String filters[][] = {{XPath2FilterContainer.INTERSECT, "//ToBeSigned"},
                            {XPath2FilterContainer.SUBTRACT,  "//NotToBeSigned"},
                            {XPath2FilterContainer.UNION,     "//ReallyToBeSigned"}};
      //J-

      transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER, XPath2FilterContainer.newInstances(doc, filters));
      if (withComments) {
         transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
      }

      /*
      XPath2FilterContainer x = XPath2FilterContainer.newInstanceSubtract(doc, "here()/ancestor::ds:Signature[1]");
      x.setXPathNamespaceContext("ds", Constants.SignatureSpecNS);
      transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER, x.getElement());
      */
      //J+
      sig.addDocument("#xpointer(/)", transforms);

      String secretKey = "secret";

      sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                  + "\" are used for signing ("
                                  + secretKey.length() + " octets)");
      sig.sign(sig.createSecretKey(secretKey.getBytes()));

      Canonicalizer c14n =
         Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
      byte[] full = c14n.canonicalizeSubtree(doc);
      FileOutputStream fos = new FileOutputStream(filename);

      try {
         fos.write(full);
      } finally {
         fos.close();
      }

      if (verbose) {
         System.out.println(
            "-------------------------------------------------------------");
         System.out.println("Input to the transforms is");
         System.out.println(
            "-------------------------------------------------------------");
         System.out
            .println(new String(sig.getSignedInfo().item(0).getTransformsInput()
               .getBytes()));
         System.out.println(
            "-------------------------------------------------------------");
         System.out
            .println("The signed octets (output of the transforms) are ");
         System.out.println(
            "-------------------------------------------------------------");
         System.out
            .println(new String(sig.getSignedInfo().item(0)
               .getTransformsOutput().getBytes()));
         System.out.println(
            "-------------------------------------------------------------");
         System.out.println("The document is ");
         System.out.println(
            "-------------------------------------------------------------");
         System.out.println(new String(full));
         System.out.println(
            "-------------------------------------------------------------");
      }

      JavaUtils.writeBytesToFilename("xfilter2.html",
      sig.getSignedInfo().item(0).getHTMLRepresentation().getBytes());
   }

   /**
    * Method check
    *
    * @param filename
    * @throws Exception
    */
   public static void check(String filename) throws Exception {

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(new FileInputStream(filename));
      NodeList sigs = doc.getElementsByTagNameNS(Constants.SignatureSpecNS, Constants._TAG_SIGNATURE);

      XMLSignature sig = new XMLSignature((Element)sigs.item(0), new File(filename).toURL().toString());
      boolean check = sig.checkSignatureValue(sig.createSecretKey("secret".getBytes()));

         System.out.println(
            "-------------------------------------------------------------");
      System.out.println("Verification of " + filename + ": " + check);
         System.out.println(
            "-------------------------------------------------------------");
         System.out
            .println(new String(sig.getSignedInfo().item(0)
               .getTransformsOutput().getBytes()));
         System.out.println(
            "-------------------------------------------------------------");

   }
}
