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
package org.apache.xml.security.test.c14n.implementations;



import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.c14n.implementations.*;
import org.apache.xml.security.signature.*;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.transforms.params.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.test.resource.TestVectorResolver;
import org.apache.xml.security.test.interop.InteropTest;
import java.util.*;


/**
 * Unit test for {@link org.apache.xml.security.c14n.implementations.Canonicalizer20010315WithXPath}
 *
 * @author Christian Geuer-Pollmann
 */
public class C14NInterop extends InteropTest {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(C14NInterop.class.getName());

   /**
    * Method suite
    *
    * @return
    */
   public static Test suite() {
      return new TestSuite(C14NInterop.class);
   }

   /**
    *  Constructor Canonicalizer20010315WithXPathTest
    *
    *  @param Name_
    */
   public C14NInterop(String Name_) {
      super(Name_);
   }

   /**
    * Method main
    *
    * @param args
    */
   public static void main_(String[] args) {

      String[] testCaseName = { "-noloading", C14NInterop.class.getName() };

      org.apache.xml.security.Init.init();
      junit.textui.TestRunner.main(testCaseName);
   }

   /**
    * Method test_Y1
    *
    * @throws Exception
    */
   public void _test_Y1() throws Exception {

      boolean success = t("data/interop/c14n/Y1", "exc-signature.xml");

      assertTrue(success);
   }

   /**
    * Method test_Y2
    *
    * @throws Exception
    */
   public void _test_Y2() throws Exception {

      boolean success = t("data/interop/c14n/Y2", "signature-joseph-exc.xml");

      assertTrue(success);
   }

   /**
    * Method test_Y3
    *
    * @throws Exception
    */
   public void _test_Y3() throws Exception {

      boolean success = t("data/interop/c14n/Y3", "signature.xml");

      assertTrue(success);
   }

   /**
    * Method test_Y4
    *
    * @throws Exception
    */
   public void _test_Y4() throws Exception {

      boolean success = t("data/interop/c14n/Y4", "signature.xml");

      assertTrue(success);
   }

   /**
    * Method _test_Y4_stripped
    *
    * @throws Exception
    */
   public void test_Y4_stripped() throws Exception {

      boolean success = t("data/interop/c14n/Y4", "signatureStripped.xml");

      assertTrue(success);
   }

   /**
    * Method t
    *
    * @param directory
    * @param file
    * @return
    * @throws Exception
    */
   public boolean t(String directory, String file) throws Exception {

      File f = new File(directory + "/" + file);
      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
      org.w3c.dom.Document doc = db.parse(f);
      long start = System.currentTimeMillis();

      XMLUtils.circumventBug2650(doc);

      long end = System.currentTimeMillis();

      cat.debug("fixSubtree took " + (int) (end - start));

      Element sigElement =
         (Element) doc.getElementsByTagNameNS(Constants.SignatureSpecNS,
                                              Constants._TAG_SIGNATURE).item(0);
      XMLSignature signature = new XMLSignature(sigElement,
                                                f.toURL().toString());
      boolean verify =
         signature.checkSignatureValue(signature.getKeyInfo().getPublicKey());
      int failures = 0;

      if (!verify) {
         for (int i = 0; i < signature.getSignedInfo().getLength(); i++) {
            boolean refVerify =
               signature.getSignedInfo().getVerificationResult(i);

            if (refVerify) {
               cat.debug("Reference " + i + " was OK");
            } else {
               cat.debug("Reference " + i + " failed");

               failures++;

               XMLSignatureInput result =
                  signature.getSignedInfo()
                     .getReferencedContentAfterTransformsItem(i);

               JavaUtils.writeBytesToFilename(directory + "/c14n-" + i
                                              + "-apache.txt", result
                                                 .getBytes());
            }
         }
      }

      return verify;
   }

   /**
    * Method main
    *
    * @param args
    * @throws Exception
    */
   public static void main(String[] args) throws Exception {

      org.apache.xml.security.Init.init();

      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.newDocument();
      String nsA = "http://www.a.com/";
      String nsB = "http://www.b.com/";

      // String nsC = "http://www.c.com/";
      Element A_A = doc.createElementNS(nsA, "A:A");
      Element A_B = doc.createElementNS(nsA, "A:B");
      Element A_C = doc.createElement("C");

      // Element A_C = doc.createElementNS(nsC, "A:C");
      // Element A_D = doc.createElementNS(nsC, "A:D");
      // Element A_E = doc.createElementNS(nsC, "A:E");
      A_A.setAttributeNS(Constants.NamespaceSpecNS, "xmlns", "google://jsdfl/");
      A_A.setAttributeNS(Constants.XML_LANG_SPACE_SpecNS, "xml:lang", "de");
      A_A.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:A", nsA);
      A_A.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:B", nsB);
      A_A.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:C", "http://c.com/");
      A_B.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:D", "http://c.com/");
      A_B.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:E", "http://c.com/");
      A_C.setAttributeNS(Constants.NamespaceSpecNS, "xmlns", "");
      doc.appendChild(A_A);
      A_A.appendChild(A_B);
      A_B.appendChild(A_C);

      cat.info("Created document");


      Canonicalizer20010315OmitComments c = new Canonicalizer20010315OmitComments();
      System.out.println(new String(c.engineCanonicalizeSubTree(doc)));

      XMLSignature sig = new XMLSignature(doc, "", XMLSignature.ALGO_ID_MAC_HMAC_SHA1);
      A_A.appendChild(sig.getElement());

      Transforms transforms = new Transforms(doc);

      XPath2FilterContainer xf2_1 = XPath2FilterContainer.newInstanceIntersect(doc, "//self::node()[local-name() = 'B']");
      transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER, xf2_1.getElement());

      XPath2FilterContainer xf2_2 = XPath2FilterContainer.newInstanceSubtract(doc, "//namespace::*[local-name()='B']");
      transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER, xf2_2.getElement());

      cat.info("Created signature object");

      sig.addDocument("", transforms);

      cat.info("Reference added");

      sig.sign(sig.createSecretKey("secret".getBytes()));

      cat.info("Signing finished");

      XMLSignatureInput s = sig.getSignedInfo().getReferencedContentAfterTransformsItem(0);
      Set nodes = s.getNodeSet();
      Iterator it = nodes.iterator();
      while (it.hasNext()) {
         Node n = (Node) it.next();
         if (n.getNodeType() == Node.ATTRIBUTE_NODE) {
            Element e = ((Attr)n).getOwnerElement();
            System.out.println("<" + e.getTagName() + " " + n + " />");
         } else if (n.getNodeType() == Node.ELEMENT_NODE) {
            System.out.println("<" + ((Element)n).getTagName() + " />");
         }
      }
      cat.info("finished");

      System.out.println("###########################");
      System.out.println(new String(s.getBytes()));
   }
}
