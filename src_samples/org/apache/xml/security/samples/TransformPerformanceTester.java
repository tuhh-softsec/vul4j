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
package org.apache.xml.security.samples;



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
 * Class TransformPerformanceTester
 *
 * 300 * simple_gif_detached       took 136,376 seconds
 * 300 * pureedge_xfilter2         took 574,797 seconds
 * 300 * pureedge_apachefilter     took 595,617 seconds
 * 300 * xfilter2spec_xfilter2     took  75,408 seconds
 * 300 * xfilter2spec_apachefilter took  58,624 seconds
 *
 * 400 * simple_gif_detached       took 196,733 seconds
 * 400 * pureedge_xfilter2         took 767,404 seconds
 * 400 * pureedge_apachefilter     took 805,648 seconds
 * 400 * xfilter2spec_xfilter2     took  81,367 seconds
 * 400 * xfilter2spec_apachefilter took  72,013 seconds
 *
 * 500 * simple_gif_detached took 246,054 seconds
 * 500 * xfilter2spec_xfilter2_1 took  98,842 seconds
 * 500 * xfilter2spec_xfilter2_2 took 122,236 seconds
 * 500 * xfilter2spec_xfilter2_3 took 122,917 seconds
 * 500 * xfilter2spec_apachefilter_1 took 120,563 seconds
 * 500 * xfilter2spec_apachefilter_2 took 109,898 seconds
 * 500 * xfilter2spec_apachefilter_3 took 113,383 seconds
 *
 * 600 * simple_gif_detached took 294,503 seconds
 * 600 * pureedge_xfilter2 took 1.144,616 seconds
 * 600 * pureedge_apachefilter took 1.205,243 seconds
 * 600 * xfilter2spec_xfilter2_1 took 109,337 seconds
 * 600 * xfilter2spec_xfilter2_2 took 122,206 seconds
 * 600 * xfilter2spec_xfilter2_3 took 138,91 seconds
 * 600 * xfilter2spec_apachefilter_1 took 130,297 seconds
 * 600 * xfilter2spec_apachefilter_2 took 123,268 seconds
 * 600 * xfilter2spec_apachefilter_3 took 131,178 seconds
 *
 * @author $Author$
 * @version $Revision$
 */
public class TransformPerformanceTester {

   /**
    * Method main
    *
    * @param args
    * @throws Exception
    */
   public static void main(String args[]) throws Exception {

      org.apache.xml.security.Init.init();

      // checkMerlinsSample();
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      DocumentBuilder db = dbf.newDocumentBuilder();

      //J-
      int counter = 10;
      boolean simple_gif_detached = true;

      boolean pureedge_xfilter2 = true;
      boolean pureedge_apachefilter = true;

      boolean xfilter2spec_xfilter2_1 = true;
      boolean xfilter2spec_xfilter2_2 = true;
      boolean xfilter2spec_xfilter2_3 = true;

      boolean xfilter2spec_apachefilter_1 = true;
      boolean xfilter2spec_apachefilter_2 = true;
      boolean xfilter2spec_apachefilter_3 = true;
      //J+

      if (simple_gif_detached) {
         Document doc = db.newDocument();
         long start = System.currentTimeMillis();
         byte[][] result = null;

         for (int i = 0; i < counter; i++) {
            result = TransformPerformanceTester.simple_gif_detached(doc);

            if (i % 10 == 0) {
               System.out.print(".");
            }
         }

         System.out.println("");

         long end = System.currentTimeMillis();
         double delta = end - start;

         System.out.println(
            counter + " * simple_gif_detached took "
            + java.text.DecimalFormat.getInstance().format(delta / 1000.)
            + " seconds");

         FileOutputStream fos;

         fos = new FileOutputStream("simple_gif_detached.xml");

         fos.write(result[0]);
         fos.close();

         fos = new FileOutputStream("simple_gif_detached.gif");

         fos.write(result[1]);
         fos.close();
      }

      if (pureedge_xfilter2) {
         Document doc =
            db.parse(new FileInputStream("data/com/pureedge/LeaveRequest.xfd"));
         long start = System.currentTimeMillis();
         byte[][] result = null;

         for (int i = 0; i < counter; i++) {
            result = TransformPerformanceTester.pureedge_xfilter2(doc);

            if (i % 10 == 0) {
               System.out.print(".");
            }
         }

         System.out.println("");

         long end = System.currentTimeMillis();
         double delta = end - start;

         System.out.println(
            counter + " * pureedge_xfilter2 took "
            + java.text.DecimalFormat.getInstance().format(delta / 1000.)
            + " seconds");

         FileOutputStream fos;

         fos = new FileOutputStream("pureedge_xfilter2_doc.xml");

         fos.write(result[0]);
         fos.close();

         fos = new FileOutputStream("pureedge_xfilter2_ref.xml");

         fos.write(result[1]);
         fos.close();
      }

      if (pureedge_apachefilter) {
         Document doc =
            db.parse(new FileInputStream("data/com/pureedge/LeaveRequest.xfd"));
         long start = System.currentTimeMillis();
         byte[][] result = null;

         for (int i = 0; i < counter; i++) {
            result = TransformPerformanceTester.pureedge_apachefilter(doc);

            if (i % 10 == 0) {
               System.out.print(".");
            }
         }

         System.out.println("");

         long end = System.currentTimeMillis();
         double delta = end - start;

         System.out.println(
            counter + " * pureedge_apachefilter took "
            + java.text.DecimalFormat.getInstance().format(delta / 1000.)
            + " seconds");

         FileOutputStream fos;

         fos = new FileOutputStream("pureedge_apachefilter_doc.xml");

         fos.write(result[0]);
         fos.close();

         fos = new FileOutputStream("pureedge_apachefilter_ref.xml");

         fos.write(result[1]);
         fos.close();
      }

      if (xfilter2spec_xfilter2_1) {
         String inputDoc = "<Document>\n" + "     <ToBeSigned>\n"
                           + "       <!-- comment -->\n" + "       <Data />\n"
                           + "       <NotToBeSigned>\n"
                           + "         <ReallyToBeSigned>\n"
                           + "           <!-- comment -->\n"
                           + "           <Data />\n"
                           + "         </ReallyToBeSigned>\n"
                           + "       </NotToBeSigned>\n"
                           + "     </ToBeSigned>\n" + "     <ToBeSigned>\n"
                           + "       <Data />\n" + "       <NotToBeSigned>\n"
                           + "         <Data />\n"
                           + "       </NotToBeSigned>\n"
                           + "     </ToBeSigned>\n" + "</Document>";

         //J+
         Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
         long start = System.currentTimeMillis();
         byte[][] result = null;

         for (int i = 0; i < counter; i++) {
            result = TransformPerformanceTester.xfilter2spec_xfilter2_1(doc);

            if (i % 10 == 0) {
               System.out.print(".");
            }
         }

         System.out.println("");

         long end = System.currentTimeMillis();
         double delta = end - start;

         System.out.println(
            counter + " * xfilter2spec_xfilter2_1 took "
            + java.text.DecimalFormat.getInstance().format(delta / 1000.)
            + " seconds");

         FileOutputStream fos;

         fos = new FileOutputStream("xfilter2spec_xfilter2_doc_1.xml");

         fos.write(result[0]);
         fos.close();

         fos = new FileOutputStream("xfilter2spec_xfilter2_ref_1.xml");

         fos.write(result[1]);
         fos.close();
      }

      if (xfilter2spec_xfilter2_2) {
         String inputDoc = "<Document>\n" + "     <ToBeSigned>\n"
                           + "       <!-- comment -->\n" + "       <Data />\n"
                           + "       <NotToBeSigned>\n"
                           + "         <ReallyToBeSigned>\n"
                           + "           <!-- comment -->\n"
                           + "           <Data />\n"
                           + "         </ReallyToBeSigned>\n"
                           + "       </NotToBeSigned>\n"
                           + "     </ToBeSigned>\n" + "     <ToBeSigned>\n"
                           + "       <Data />\n" + "       <NotToBeSigned>\n"
                           + "         <Data />\n"
                           + "       </NotToBeSigned>\n"
                           + "     </ToBeSigned>\n" + "</Document>";

         //J+
         Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
         long start = System.currentTimeMillis();
         byte[][] result = null;

         for (int i = 0; i < counter; i++) {
            result = TransformPerformanceTester.xfilter2spec_xfilter2_2(doc);

            if (i % 10 == 0) {
               System.out.print(".");
            }
         }

         System.out.println("");

         long end = System.currentTimeMillis();
         double delta = end - start;

         System.out.println(
            counter + " * xfilter2spec_xfilter2_2 took "
            + java.text.DecimalFormat.getInstance().format(delta / 1000.)
            + " seconds");

         FileOutputStream fos;

         fos = new FileOutputStream("xfilter2spec_xfilter2_doc_2.xml");

         fos.write(result[0]);
         fos.close();

         fos = new FileOutputStream("xfilter2spec_xfilter2_ref_2.xml");

         fos.write(result[1]);
         fos.close();
      }

      if (xfilter2spec_xfilter2_3) {
         String inputDoc = "<Document>\n" + "     <ToBeSigned>\n"
                           + "       <!-- comment -->\n" + "       <Data />\n"
                           + "       <NotToBeSigned>\n"
                           + "         <ReallyToBeSigned>\n"
                           + "           <!-- comment -->\n"
                           + "           <Data />\n"
                           + "         </ReallyToBeSigned>\n"
                           + "       </NotToBeSigned>\n"
                           + "     </ToBeSigned>\n" + "     <ToBeSigned>\n"
                           + "       <Data />\n" + "       <NotToBeSigned>\n"
                           + "         <Data />\n"
                           + "       </NotToBeSigned>\n"
                           + "     </ToBeSigned>\n" + "</Document>";

         //J+
         Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
         long start = System.currentTimeMillis();
         byte[][] result = null;

         for (int i = 0; i < counter; i++) {
            result = TransformPerformanceTester.xfilter2spec_xfilter2_3(doc);

            if (i % 10 == 0) {
               System.out.print(".");
            }
         }

         System.out.println("");

         long end = System.currentTimeMillis();
         double delta = end - start;

         System.out.println(
            counter + " * xfilter2spec_xfilter2_3 took "
            + java.text.DecimalFormat.getInstance().format(delta / 1000.)
            + " seconds");

         FileOutputStream fos;

         fos = new FileOutputStream("xfilter2spec_xfilter2_doc_3.xml");

         fos.write(result[0]);
         fos.close();

         fos = new FileOutputStream("xfilter2spec_xfilter2_ref_3.xml");

         fos.write(result[1]);
         fos.close();
      }

      if (xfilter2spec_apachefilter_1) {
         String inputDoc = "<Document>\n" + "     <ToBeSigned>\n"
                           + "       <!-- comment -->\n" + "       <Data />\n"
                           + "       <NotToBeSigned>\n"
                           + "         <ReallyToBeSigned>\n"
                           + "           <!-- comment -->\n"
                           + "           <Data />\n"
                           + "         </ReallyToBeSigned>\n"
                           + "       </NotToBeSigned>\n"
                           + "     </ToBeSigned>\n" + "     <ToBeSigned>\n"
                           + "       <Data />\n" + "       <NotToBeSigned>\n"
                           + "         <Data />\n"
                           + "       </NotToBeSigned>\n"
                           + "     </ToBeSigned>\n" + "</Document>";

         //J+
         Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
         long start = System.currentTimeMillis();
         byte[][] result = null;

         for (int i = 0; i < counter; i++) {
            result =
               TransformPerformanceTester.xfilter2spec_apachefilter_1(doc);

            if (i % 10 == 0) {
               System.out.print(".");
            }
         }

         System.out.println("");

         long end = System.currentTimeMillis();
         double delta = end - start;

         System.out.println(
            counter + " * xfilter2spec_apachefilter_1 took "
            + java.text.DecimalFormat.getInstance().format(delta / 1000.)
            + " seconds");

         FileOutputStream fos;

         fos = new FileOutputStream("xfilter2spec_apachefilter_doc_1.xml");

         fos.write(result[0]);
         fos.close();

         fos = new FileOutputStream("xfilter2spec_apachefilter_ref_1.xml");

         fos.write(result[1]);
         fos.close();
      }

      if (xfilter2spec_apachefilter_2) {
         String inputDoc = "<Document>\n" + "     <ToBeSigned>\n"
                           + "       <!-- comment -->\n" + "       <Data />\n"
                           + "       <NotToBeSigned>\n"
                           + "         <ReallyToBeSigned>\n"
                           + "           <!-- comment -->\n"
                           + "           <Data />\n"
                           + "         </ReallyToBeSigned>\n"
                           + "       </NotToBeSigned>\n"
                           + "     </ToBeSigned>\n" + "     <ToBeSigned>\n"
                           + "       <Data />\n" + "       <NotToBeSigned>\n"
                           + "         <Data />\n"
                           + "       </NotToBeSigned>\n"
                           + "     </ToBeSigned>\n" + "</Document>";

         //J+
         Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
         long start = System.currentTimeMillis();
         byte[][] result = null;

         for (int i = 0; i < counter; i++) {
            result =
               TransformPerformanceTester.xfilter2spec_apachefilter_2(doc);

            if (i % 10 == 0) {
               System.out.print(".");
            }
         }

         System.out.println("");

         long end = System.currentTimeMillis();
         double delta = end - start;

         System.out.println(
            counter + " * xfilter2spec_apachefilter_2 took "
            + java.text.DecimalFormat.getInstance().format(delta / 1000.)
            + " seconds");

         FileOutputStream fos;

         fos = new FileOutputStream("xfilter2spec_apachefilter_doc_2.xml");

         fos.write(result[0]);
         fos.close();

         fos = new FileOutputStream("xfilter2spec_apachefilter_ref_2.xml");

         fos.write(result[1]);
         fos.close();
      }

      if (xfilter2spec_apachefilter_3) {
         String inputDoc = "<Document>\n" + "     <ToBeSigned>\n"
                           + "       <!-- comment -->\n" + "       <Data />\n"
                           + "       <NotToBeSigned>\n"
                           + "         <ReallyToBeSigned>\n"
                           + "           <!-- comment -->\n"
                           + "           <Data />\n"
                           + "         </ReallyToBeSigned>\n"
                           + "       </NotToBeSigned>\n"
                           + "     </ToBeSigned>\n" + "     <ToBeSigned>\n"
                           + "       <Data />\n" + "       <NotToBeSigned>\n"
                           + "         <Data />\n"
                           + "       </NotToBeSigned>\n"
                           + "     </ToBeSigned>\n" + "</Document>";

         //J+
         Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
         long start = System.currentTimeMillis();
         byte[][] result = null;

         for (int i = 0; i < counter; i++) {
            result =
               TransformPerformanceTester.xfilter2spec_apachefilter_3(doc);

            if (i % 10 == 0) {
               System.out.print(".");
            }
         }

         System.out.println("");

         long end = System.currentTimeMillis();
         double delta = end - start;

         System.out.println(
            counter + " * xfilter2spec_apachefilter_3 took "
            + java.text.DecimalFormat.getInstance().format(delta / 1000.)
            + " seconds");

         FileOutputStream fos;

         fos = new FileOutputStream("xfilter2spec_apachefilter_doc_3.xml");

         fos.write(result[0]);
         fos.close();

         fos = new FileOutputStream("xfilter2spec_apachefilter_ref_3.xml");

         fos.write(result[1]);
         fos.close();
      }
   }

   /**
    * Method pureedge_xfilter2
    *
    * @param doc
    * @return
    * @throws Exception
    */
   public static byte[][] pureedge_xfilter2(Document doc) throws Exception {

      XMLSignature sig = new XMLSignature(doc, null,
                                          XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

      doc.getDocumentElement().appendChild(sig.getElement());

      Transforms transforms = new Transforms(doc);

      {
         XPath2FilterContainer xpathContainer =
            XPath2FilterContainer.newInstanceSubtract(doc,
         //J-
            "\n" +
            "/XFDL/page[@sid='PAGE1']/*[@sid='CHECK16' or \n" +
            "                           @sid='CHECK17' or \n" +
            "                           @sid='FIELD47' or \n" +
            "                           @sid='BUTTON2' or \n" +
            "                           @sid='FIELD48']\n" +
            " | \n" +
            "/XFDL/page/triggeritem[not(attribute::sid) | \n"  +
            "                       /XFDL/page/*/triggeritem]\n" +
            " | \n" +
            "here()/ancestor::ds:Signature[1]" +
            "");
            //J+
         xpathContainer.setXPathNamespaceContext("ds",
                                                 Constants.SignatureSpecNS);
         transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                                 xpathContainer.getElement());
      }

      sig.addDocument("", transforms);

      String secretKey = "secret";

      sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                  + "\" are used for signing ("
                                  + secretKey.length() + " octets)");
      sig.sign(sig.createSecretKey(secretKey.getBytes()));

      Canonicalizer c14n =
         Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
      byte[] full = c14n.canonicalizeSubtree(doc);
      byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
      byte[][] result = {
         full, ref
      };

      // we remove the signature now
      sig.getElement().getParentNode().removeChild(sig.getElement());

      return result;
   }

   /**
    * Method pureedge_apachefilter
    *
    * @param doc
    * @return
    * @throws Exception
    */
   public static byte[][] pureedge_apachefilter(Document doc) throws Exception {

      XMLSignature sig = new XMLSignature(doc, null,
                                          XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

      doc.getDocumentElement().appendChild(sig.getElement());

      Transforms transforms = new Transforms(doc);

      {
         String includeButSearch = "/";
         String excludeButSearch = "";
         String exclude =
         //J-
            "\n" +
            "/XFDL/page[@sid='PAGE1']/*[@sid='CHECK16' or \n" +
            "                           @sid='CHECK17' or \n" +
            "                           @sid='FIELD47' or \n" +
            "                           @sid='BUTTON2' or \n" +
            "                           @sid='FIELD48']\n" +
            " | \n" +
            "/XFDL/page/triggeritem[not(attribute::sid) | \n"  +
            "                       /XFDL/page/*/triggeritem]\n" +
            " | \n" +
            "here()/ancestor::ds:Signature[1]";
            //J+
         XPathFilterCHGPContainer xpathContainer =
            XPathFilterCHGPContainer.getInstance(doc, includeButSearch,
                                                 excludeButSearch, exclude);

         xpathContainer.setXPathNamespaceContext("ds",
                                                 Constants.SignatureSpecNS);
         transforms.addTransform(Transforms.TRANSFORM_XPATHFILTERCHGP,
                                 xpathContainer.getElement());
      }

      sig.addDocument("", transforms);

      String secretKey = "secret";

      sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                  + "\" are used for signing ("
                                  + secretKey.length() + " octets)");
      sig.sign(sig.createSecretKey(secretKey.getBytes()));

      Canonicalizer c14n =
         Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
      byte[] full = c14n.canonicalizeSubtree(doc);
      byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
      byte[][] result = {
         full, ref
      };

      // we remove the signature now
      sig.getElement().getParentNode().removeChild(sig.getElement());

      return result;
   }

   /**
    * Method simple_gif_detached
    *
    * @param doc
    * @return
    * @throws Exception
    */
   public static byte[][] simple_gif_detached(Document doc) throws Exception {

      XMLSignature sig = new XMLSignature(doc,
                                          new File(".").toURL().toString(),
                                          XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

      doc.appendChild(sig.getElement());
      sig.addDocument("./image.gif");

      String secretKey = "secret";

      sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                  + "\" are used for signing ("
                                  + secretKey.length() + " octets)");
      sig.sign(sig.createSecretKey(secretKey.getBytes()));

      Canonicalizer c14n =
         Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
      byte[] full = c14n.canonicalizeSubtree(doc);
      byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
      byte[][] result = {
         full, ref
      };

      // we remove the signature now
      sig.getElement().getParentNode().removeChild(sig.getElement());

      return result;
   }

   /**
    * Method xfilter2spec_xfilter2_1
    *
    * @param doc
    * @return
    * @throws Exception
    */
   public static byte[][] xfilter2spec_xfilter2_1(Document doc)
           throws Exception {

      XMLSignature sig = new XMLSignature(doc, null,
                                          XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

      doc.getDocumentElement().appendChild(sig.getElement());

      Transforms transforms = new Transforms(doc);

      transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                              XPath2FilterContainer.newInstanceIntersect(doc,
                                 "//ToBeSigned").getElement());
      sig.addDocument("", transforms);

      String secretKey = "secret";

      sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                  + "\" are used for signing ("
                                  + secretKey.length() + " octets)");
      sig.sign(sig.createSecretKey(secretKey.getBytes()));

      Canonicalizer c14n =
         Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
      byte[] full = c14n.canonicalizeSubtree(doc);
      byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
      byte[][] result = {
         full, ref
      };

      // we remove the signature now
      sig.getElement().getParentNode().removeChild(sig.getElement());

      return result;
   }

   /**
    * Method xfilter2spec_xfilter2_2
    *
    * @param doc
    * @return
    * @throws Exception
    */
   public static byte[][] xfilter2spec_xfilter2_2(Document doc)
           throws Exception {

      XMLSignature sig = new XMLSignature(doc, null,
                                          XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

      doc.getDocumentElement().appendChild(sig.getElement());

      Transforms transforms = new Transforms(doc);

      transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                              XPath2FilterContainer.newInstanceIntersect(doc,
                                 "//ToBeSigned").getElement());
      transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                              XPath2FilterContainer.newInstanceSubtract(doc,
                                 "//NotToBeSigned").getElement());
      sig.addDocument("", transforms);

      String secretKey = "secret";

      sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                  + "\" are used for signing ("
                                  + secretKey.length() + " octets)");
      sig.sign(sig.createSecretKey(secretKey.getBytes()));

      Canonicalizer c14n =
         Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
      byte[] full = c14n.canonicalizeSubtree(doc);
      byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
      byte[][] result = {
         full, ref
      };

      // we remove the signature now
      sig.getElement().getParentNode().removeChild(sig.getElement());

      return result;
   }

   /**
    * Method xfilter2spec_xfilter2
    *
    * @param doc
    * @return
    * @throws Exception
    */
   public static byte[][] xfilter2spec_xfilter2_3(Document doc)
           throws Exception {

      XMLSignature sig = new XMLSignature(doc, null,
                                          XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

      doc.getDocumentElement().appendChild(sig.getElement());

      Transforms transforms = new Transforms(doc);

      transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                              XPath2FilterContainer.newInstanceIntersect(doc,
                                 "//ToBeSigned").getElement());
      transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                              XPath2FilterContainer.newInstanceSubtract(doc,
                                 "//NotToBeSigned").getElement());
      transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                              XPath2FilterContainer.newInstanceUnion(doc,
                                 "//ReallyToBeSigned").getElement());
      sig.addDocument("", transforms);

      String secretKey = "secret";

      sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                  + "\" are used for signing ("
                                  + secretKey.length() + " octets)");
      sig.sign(sig.createSecretKey(secretKey.getBytes()));

      Canonicalizer c14n =
         Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
      byte[] full = c14n.canonicalizeSubtree(doc);
      byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
      byte[][] result = {
         full, ref
      };

      // we remove the signature now
      sig.getElement().getParentNode().removeChild(sig.getElement());

      return result;
   }

   /**
    * Method xfilter2spec_apachefilter
    *
    * @param doc
    * @return
    * @throws Exception
    */
   public static byte[][] xfilter2spec_apachefilter_1(Document doc)
           throws Exception {

      XMLSignature sig = new XMLSignature(doc, null,
                                          XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

      doc.getDocumentElement().appendChild(sig.getElement());

      Transforms transforms = new Transforms(doc);

      {
         String includeButSearch = "//ToBeSigned";
         String excludeButSearch = "/";
         String exclude = "here()/ancestor::ds:Signature[1]";
         XPathFilterCHGPContainer xpathContainer =
            XPathFilterCHGPContainer.getInstance(doc, includeButSearch,
                                                 excludeButSearch, exclude);

         xpathContainer.setXPathNamespaceContext("ds",
                                                 Constants.SignatureSpecNS);
         transforms.addTransform(Transforms.TRANSFORM_XPATHFILTERCHGP,
                                 xpathContainer.getElement());
      }

      sig.addDocument("", transforms);

      String secretKey = "secret";

      sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                  + "\" are used for signing ("
                                  + secretKey.length() + " octets)");
      sig.sign(sig.createSecretKey(secretKey.getBytes()));

      Canonicalizer c14n =
         Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
      byte[] full = c14n.canonicalizeSubtree(doc);
      byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
      byte[][] result = {
         full, ref
      };

      // we remove the signature now
      sig.getElement().getParentNode().removeChild(sig.getElement());

      return result;
   }

   /**
    * Method xfilter2spec_apachefilter
    *
    * @param doc
    * @return
    * @throws Exception
    */
   public static byte[][] xfilter2spec_apachefilter_2(Document doc)
           throws Exception {

      XMLSignature sig = new XMLSignature(doc, null,
                                          XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

      doc.getDocumentElement().appendChild(sig.getElement());

      Transforms transforms = new Transforms(doc);

      {
         String includeButSearch = "//ToBeSigned";
         String excludeButSearch = "/ | //NotToBeSigned";
         String exclude = "here()/ancestor::ds:Signature[1]";
         XPathFilterCHGPContainer xpathContainer =
            XPathFilterCHGPContainer.getInstance(doc, includeButSearch,
                                                 excludeButSearch, exclude);

         xpathContainer.setXPathNamespaceContext("ds",
                                                 Constants.SignatureSpecNS);
         transforms.addTransform(Transforms.TRANSFORM_XPATHFILTERCHGP,
                                 xpathContainer.getElement());
      }

      sig.addDocument("", transforms);

      String secretKey = "secret";

      sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                  + "\" are used for signing ("
                                  + secretKey.length() + " octets)");
      sig.sign(sig.createSecretKey(secretKey.getBytes()));

      Canonicalizer c14n =
         Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
      byte[] full = c14n.canonicalizeSubtree(doc);
      byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
      byte[][] result = {
         full, ref
      };

      // we remove the signature now
      sig.getElement().getParentNode().removeChild(sig.getElement());

      return result;
   }

   /**
    * Method xfilter2spec_apachefilter
    *
    * @param doc
    * @return
    * @throws Exception
    */
   public static byte[][] xfilter2spec_apachefilter_3(Document doc)
           throws Exception {

      XMLSignature sig = new XMLSignature(doc, null,
                                          XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

      doc.getDocumentElement().appendChild(sig.getElement());

      Transforms transforms = new Transforms(doc);

      {
         String includeButSearch = "//ToBeSigned | //ReallyToBeSigned";
         String excludeButSearch = "/ | //NotToBeSigned";
         String exclude = "here()/ancestor::ds:Signature[1]";
         XPathFilterCHGPContainer xpathContainer =
            XPathFilterCHGPContainer.getInstance(doc, includeButSearch,
                                                 excludeButSearch, exclude);

         xpathContainer.setXPathNamespaceContext("ds",
                                                 Constants.SignatureSpecNS);
         transforms.addTransform(Transforms.TRANSFORM_XPATHFILTERCHGP,
                                 xpathContainer.getElement());
      }

      sig.addDocument("", transforms);

      String secretKey = "secret";

      sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                  + "\" are used for signing ("
                                  + secretKey.length() + " octets)");
      sig.sign(sig.createSecretKey(secretKey.getBytes()));

      Canonicalizer c14n =
         Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
      byte[] full = c14n.canonicalizeSubtree(doc);
      byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
      byte[][] result = {
         full, ref
      };

      // we remove the signature now
      sig.getElement().getParentNode().removeChild(sig.getElement());

      return result;
   }

   /**
    * Method checkMerlinsSample
    *
    * @throws Exception
    */
   public static void checkMerlinsSample() throws Exception {

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      DocumentBuilder db = dbf.newDocumentBuilder();
      File f = new File(
         "data/ie/baltimore/merlin-examples/merlin-xmldsig-filter2-one/sign-xfdl.xml");
      Document doc = db.parse(new FileInputStream(f));
      XMLSignature sig =
         new XMLSignature((Element) doc
            .getElementsByTagNameNS(Constants.SignatureSpecNS, "Signature")
               .item(0), f.toURL().toString());

      System.out.println("Signature erzeugt");

      boolean v = sig.checkSignatureValue(sig.getKeyInfo().getPublicKey());

      System.out.println("Merlin: " + v);
   }
}
