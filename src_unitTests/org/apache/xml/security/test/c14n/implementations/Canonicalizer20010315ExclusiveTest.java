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

      File fileIn = new File(
         "data/ie/baltimore/merlin-examples/ec-merlin-iaikTests-two/signature.xml");

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
            .parse("data/org/apache/xml/security/c14n/inExcl/example2_2_1.xml");
      Node root = doc.getElementsByTagNameNS("http://example.net",
                                             "elem2").item(0);
      Canonicalizer20010315 c = new Canonicalizer20010315WithComments();
      byte[] reference = JavaUtils.getBytesFromFile(
         "data/org/apache/xml/security/c14n/inExcl/example2_2_1_c14nized.xml");
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
            .parse("data/org/apache/xml/security/c14n/inExcl/example2_2_2.xml");
      Node root = doc.getElementsByTagNameNS("http://example.net",
                                             "elem2").item(0);
      Canonicalizer20010315 c = new Canonicalizer20010315WithComments();
      byte[] reference = JavaUtils.getBytesFromFile(
         "data/org/apache/xml/security/c14n/inExcl/example2_2_2_c14nized.xml");
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
            .parse("data/org/apache/xml/security/c14n/inExcl/example2_2_1.xml");
      Node root = doc.getElementsByTagNameNS("http://example.net",
                                             "elem2").item(0);
      Canonicalizer20010315Excl c = new Canonicalizer20010315ExclWithComments();
      byte[] reference = JavaUtils.getBytesFromFile(
         "data/org/apache/xml/security/c14n/inExcl/example2_2_c14nized_exclusive.xml");
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
            .parse("data/org/apache/xml/security/c14n/inExcl/example2_2_2.xml");
      Node root = doc.getElementsByTagNameNS("http://example.net",
                                             "elem2").item(0);
      Canonicalizer20010315Excl c = new Canonicalizer20010315ExclWithComments();
      byte[] reference = JavaUtils.getBytesFromFile(
         "data/org/apache/xml/security/c14n/inExcl/example2_2_c14nized_exclusive.xml");
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
            .parse("data/org/apache/xml/security/c14n/inExcl/example2_2_3.xml");
      XMLUtils.circumventBug2650(doc);
      NodeList nodes = XPathAPI.selectNodeList(doc.getDocumentElement(),
                                 "(//. | //@* | //namespace::*)[ancestor-or-self::p]");
      Canonicalizer20010315Excl c = new Canonicalizer20010315ExclWithComments();
      byte[] reference = JavaUtils.getBytesFromFile(
         "data/org/apache/xml/security/c14n/inExcl/example2_2_3_c14nized_exclusive.xml");
      byte[] result = c.engineCanonicalizeXPathNodeSet(nodes);
      boolean equals = JavaUtils.binaryCompare(reference, result);
      if (!equals) {
          log.warn("Error output = " + new String(result));
      }
      assertTrue(equals);
   }
   
}
