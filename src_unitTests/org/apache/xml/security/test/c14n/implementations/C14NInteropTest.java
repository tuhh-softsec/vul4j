/*
 * Copyright  1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.xml.security.test.c14n.implementations;



import java.io.File;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.xml.security.c14n.implementations.Canonicalizer20010315OmitComments;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.test.interop.InteropTest;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.XPath2FilterContainer;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Unit test for {@link org.apache.xml.security.c14n.implementations.Canonicalizer20010315WithXPath}
 *
 * @author Christian Geuer-Pollmann
 */
public class C14NInteropTest extends InteropTest {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(C14NInteropTest.class.getName());

    static {
        org.apache.xml.security.Init.init();
    }
    
   /**
    * Method suite
    *
    *
    */
   public static Test suite() {
      return new TestSuite(C14NInteropTest.class);
   }

   /**
    *  Constructor Canonicalizer20010315WithXPathTest
    *
    *  @param Name_
    */
   public C14NInteropTest(String Name_) {
      super(Name_);
   }

   /**
    * Method main
    *
    * @param args
    */
   public static void main_(String[] args) {

      String[] testCaseName = { "-noloading", C14NInteropTest.class.getName() };

      org.apache.xml.security.Init.init();
      junit.textui.TestRunner.main(testCaseName);
   }

   /**
    * Method test_Y1
    *
    * @throws Exception
    */
   public void test_Y1() throws Exception {

      boolean success = t("data/interop/c14n/Y1", "exc-signature.xml");

      assertTrue(success);
   }

   /**
    * Method test_Y2
    *
    * @throws Exception
    */
   public void test_Y2() throws Exception {

      boolean success = t("data/interop/c14n/Y2", "signature-joseph-exc.xml");

      assertTrue(success);
   }

   /**
    * Method test_Y3
    *
    * @throws Exception
    */
   public void test_Y3() throws Exception {

      boolean success = t("data/interop/c14n/Y3", "signature.xml");

      assertTrue(success);
   }

   /**
    * Method test_Y4
    *
    * @throws Exception
    */
   public void test_Y4() throws Exception {

      boolean success = t("data/interop/c14n/Y4", "signature.xml");

      assertTrue(success);
   }

   /**
    * Method _test_Y4_stripped
    *
    * @throws Exception
    */
   public void test_Y4_stripped() throws Exception {

      // boolean success = t("data/interop/c14n/Y4", "signatureStripped.xml");
      boolean success = t("data/interop/c14n/Y4", "signature.xml");

      assertTrue(success);
   }

   /**
    * Method t
    *
    * @param directory
    * @param file
    *
    * @throws Exception
    */
   public boolean t(String directory, String file) throws Exception
   {

   	  String basedir = System.getProperty("basedir");
   	  if(basedir != null && !"".equals(basedir)) {
   		directory = basedir + "/" + directory;
   	  }
   	
      File f = new File(directory + "/" + file);
      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
      org.w3c.dom.Document doc = db.parse(f);
      long start = System.currentTimeMillis();

      //XMLUtils.circumventBug2650(doc);

      long end = System.currentTimeMillis();

      log.debug("fixSubtree took " + (int) (end - start));

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
               log.debug("Reference " + i + " was OK");
            } else {
               log.debug("Reference " + i + " failed");

               failures++;

               /*
               XMLSignatureInput result =
                  signature.getSignedInfo()
                     .getReferencedContentAfterTransformsItem(i);

               JavaUtils.writeBytesToFilename("data/temp" + "/c14n-" + i
                                              + "-apache.txt", result
                                                 .getBytes());
               */
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
      Element A_C = doc.createElementNS(null, "C");

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

      log.debug("Created document");


      Canonicalizer20010315OmitComments c = new Canonicalizer20010315OmitComments();
      System.out.println(new String(c.engineCanonicalizeSubTree(doc)));

      XMLSignature sig = new XMLSignature(doc, "", XMLSignature.ALGO_ID_MAC_HMAC_SHA1);
      A_A.appendChild(sig.getElement());

      Transforms transforms = new Transforms(doc);

      XPath2FilterContainer xf2_1 = XPath2FilterContainer.newInstanceIntersect(doc, "//self::node()[local-name() = 'B']");
      transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER, xf2_1.getElement());

      XPath2FilterContainer xf2_2 = XPath2FilterContainer.newInstanceSubtract(doc, "//namespace::*[local-name()='B']");
      transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER, xf2_2.getElement());

      log.info("Created signature object");

      sig.addDocument("", transforms);

      log.info("Reference added");

      sig.sign(sig.createSecretKey("secret".getBytes()));

      log.info("Signing finished");

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
      log.info("finished");

      System.out.println("###########################");
      System.out.println(new String(s.getBytes()));
   }
}
