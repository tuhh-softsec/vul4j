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
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315Excl;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315ExclWithComments;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315WithComments;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 *
 * @author Christian Geuer-Pollmann
 */
public class Canonicalizer20010315ExclusiveTest extends TestCase {

   static {
      org.apache.xml.security.Init.init();
   }

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(
                Canonicalizer20010315ExclusiveTest.class.getName());

   /**
    * Method suite
    *
    *
    */
   public static Test suite() {
      return new TestSuite(Canonicalizer20010315ExclusiveTest.class);
   }

   /**
    *  Constructor Canonicalizer20010315ExclusiveTest
    *
    *  @param Name_
    */
   public Canonicalizer20010315ExclusiveTest(String Name_) {
      super(Name_);
   }

   /**
    * Method main
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading",
                                Canonicalizer20010315ExclusiveTest.class
                                   .getName() };

      junit.textui.TestRunner.main(testCaseName);
   }

   /** Field dbf */
   DocumentBuilderFactory dbf;

   /** Field db */
   DocumentBuilder db;

   /**
    * Method setUp
    *
    * @throws ParserConfigurationException
    */
   public void setUp() throws ParserConfigurationException {

      this.dbf = DocumentBuilderFactory.newInstance();

      this.dbf.setNamespaceAware(true);

      this.db = this.dbf.newDocumentBuilder();
   }

   /**
    * Method testA
    *
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    * @throws XMLSecurityException
    * @throws XMLSignatureException
    * @throws org.apache.xml.security.keys.keyresolver.KeyResolverException
    */
   public void testA()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException,
                  XMLSignatureException, XMLSecurityException,
                  org.apache.xml.security.keys.keyresolver
                     .KeyResolverException {

      File fileIn = new File(getAbsolutePath(
         "data/ie/baltimore/merlin-examples/ec-merlin-iaikTests-two/signature.xml") );

      // File fileIn = new File("signature.xml");
      assertTrue("file exists", fileIn.exists());

      Document doc = this.db.parse(fileIn);
      Element signatureElement =
         (Element) doc.getElementsByTagNameNS(Constants.SignatureSpecNS,
                                              Constants._TAG_SIGNATURE).item(0);
      XMLSignature xmlSignature = new XMLSignature(signatureElement,
                                     fileIn.toURL().toString());
      boolean verify =
         xmlSignature
            .checkSignatureValue(xmlSignature.getKeyInfo().getPublicKey());
      int length = xmlSignature.getSignedInfo().getLength();
      int numberOfPositiveReferences = 0;

      for (int i = 0; i < length; i++) {
         boolean singleResult =
            xmlSignature.getSignedInfo().getVerificationResult(i);

         if (singleResult) {
            numberOfPositiveReferences++;
         }
      }

      assertTrue("Verification failed; only " + numberOfPositiveReferences
                 + "/" + length + " matched", verify);
   }

   /**
    * Method test221
    *
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    * @throws XMLSecurityException
    * @throws XMLSignatureException
    */
   public void test221()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException,
                  XMLSignatureException, XMLSecurityException {

      Document doc =
         this.db
            .parse(getAbsolutePath("data/org/apache/xml/security/c14n/inExcl/example2_2_1.xml") );
      Node root = doc.getElementsByTagNameNS("http://example.net",
                                             "elem2").item(0);
      Canonicalizer20010315 c = new Canonicalizer20010315WithComments();
      byte[] reference = JavaUtils.getBytesFromFile(getAbsolutePath(
         "data/org/apache/xml/security/c14n/inExcl/example2_2_1_c14nized.xml") );
      byte[] result = c.engineCanonicalizeSubTree(root);
      boolean equals = JavaUtils.binaryCompare(reference, result);

      /*
      if (!equals) {
         JavaUtils.writeBytesToFilename("data/org/apache/xml/security/c14n/inExcl/example2_2_1_c14nized.apache.xml", result);
      }
      */

      assertTrue(equals);
   }

   /**
    * Method test222
    *
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    * @throws XMLSecurityException
    * @throws XMLSignatureException
    */
   public void test222()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException,
                  XMLSignatureException, XMLSecurityException {

      Document doc =
         this.db
            .parse(getAbsolutePath("data/org/apache/xml/security/c14n/inExcl/example2_2_2.xml"));
      Node root = doc.getElementsByTagNameNS("http://example.net",
                                             "elem2").item(0);
      Canonicalizer20010315 c = new Canonicalizer20010315WithComments();
      byte[] reference = JavaUtils.getBytesFromFile(getAbsolutePath(
         "data/org/apache/xml/security/c14n/inExcl/example2_2_2_c14nized.xml"));
      byte[] result = c.engineCanonicalizeSubTree(root);
      boolean equals = JavaUtils.binaryCompare(reference, result);

      assertTrue(equals);
   }

   /**
    * Method test221excl
    *
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    * @throws XMLSecurityException
    * @throws XMLSignatureException
    */
   public void test221excl()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException,
                  XMLSignatureException, XMLSecurityException {

      Document doc =
         this.db
            .parse(getAbsolutePath("data/org/apache/xml/security/c14n/inExcl/example2_2_1.xml"));
      Node root = doc.getElementsByTagNameNS("http://example.net",
                                             "elem2").item(0);
      Canonicalizer20010315Excl c = new Canonicalizer20010315ExclWithComments();
      byte[] reference = JavaUtils.getBytesFromFile(getAbsolutePath(
         "data/org/apache/xml/security/c14n/inExcl/example2_2_c14nized_exclusive.xml") );
      byte[] result = c.engineCanonicalizeSubTree(root);
      boolean equals = JavaUtils.binaryCompare(reference, result);

      assertTrue(equals);
   }

   /**
    * Method test222excl
    *
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    * @throws XMLSecurityException
    * @throws XMLSignatureException
    */
   public void test222excl()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException,
                  XMLSignatureException, XMLSecurityException {

      Document doc =
         this.db
            .parse(getAbsolutePath("data/org/apache/xml/security/c14n/inExcl/example2_2_2.xml"));
      Node root = doc.getElementsByTagNameNS("http://example.net",
                                             "elem2").item(0);
      Canonicalizer20010315Excl c = new Canonicalizer20010315ExclWithComments();
      byte[] reference = JavaUtils.getBytesFromFile(getAbsolutePath(
         "data/org/apache/xml/security/c14n/inExcl/example2_2_c14nized_exclusive.xml") );
      byte[] result = c.engineCanonicalizeSubTree(root);
      boolean equals = JavaUtils.binaryCompare(reference, result);

      assertTrue(equals);
   }
   
   /**
    * Method test223excl
    *
    * Provided by Gabriel McGoldrick - see e-mail of 21/11/03
    *
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    * @throws XMLSecurityException
    * @throws XMLSignatureException
    */
   public void test223excl()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException,
                  XMLSignatureException, XMLSecurityException {

      Document doc =
         this.db
            .parse(getAbsolutePath("data/org/apache/xml/security/c14n/inExcl/example2_2_3.xml"));
      XMLUtils.circumventBug2650(doc);
      NodeList nodes = XPathAPI.selectNodeList(doc.getDocumentElement(),
                                 "(//. | //@* | //namespace::*)[ancestor-or-self::p]");
      Canonicalizer20010315Excl c = new Canonicalizer20010315ExclWithComments();
      byte[] reference = JavaUtils.getBytesFromFile(
      		getAbsolutePath("data/org/apache/xml/security/c14n/inExcl/example2_2_3_c14nized_exclusive.xml") );
      byte[] result = c.engineCanonicalizeXPathNodeSet(nodes);
      boolean equals = JavaUtils.binaryCompare(reference, result);
      if (!equals) {
          log.warn("Error output = " + new String(result));
      }
      assertTrue(equals);
   }
   
   private String getAbsolutePath(String path)
   {
   	  String basedir = System.getProperty("basedir");
   	  if(basedir != null && !"".equals(basedir)) {
   		path = basedir + "/" + path;
   	  }
   	  return path;
   }
   
}
