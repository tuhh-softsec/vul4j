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
package org.apache.xml.security.test.transforms.implementations;



import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xpath.XPathAPI;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xml.sax.SAXException;
import org.w3c.dom.*;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.signature.*;
import org.apache.xml.security.transforms.implementations.TransformBase64Decode;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.exceptions.*;


/**
 * Unit test for {@link org.apache.xml.security.transforms.implementations.TransformBase64Decode}
 *
 * @author Christian Geuer-Pollmann
 */
public class TransformBase64DecodeTest extends TestCase {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(TransformBase64DecodeTest.class.getName());

   /**
    * Method suite
    *
    * @return
    */
   public static Test suite() {
      return new TestSuite(TransformBase64DecodeTest.class);
   }

   /**
    * Constructor TransformBase64DecodeTest
    *
    * @param Name_
    */
   public TransformBase64DecodeTest(String Name_) {
      super(Name_);
   }

   /**
    * Method main
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading",
                                TransformBase64DecodeTest.class.getName() };

      junit.textui.TestRunner.main(testCaseName);
   }

   private static Document createDocument() throws ParserConfigurationException {

      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

      dfactory.setNamespaceAware(true);

      DocumentBuilder db = dfactory.newDocumentBuilder();
      Document doc = db.newDocument();

      if (doc == null) {
          throw new RuntimeException("Could not create a Document");
      } else {
         cat.debug("I could create the Document");
      }
      return doc;
   }

   /**
    * Method createElement
    *
    * @return
    * @throws ParserConfigurationException
    */
   private static Element createElement() throws ParserConfigurationException {

      Document doc = TransformBase64DecodeTest.createDocument();

      Element element = XMLUtils.createElementInSignatureSpace(doc, Constants._TAG_TRANSFORMS);

      return element;
   }

   /**
    * Method test1
    *
    * @throws CanonicalizationException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws InvalidTransformException
    * @throws NotYetImplementedException
    * @throws ParserConfigurationException
    * @throws TransformationException
    * @throws XMLSecurityException
    * @throws XMLSignatureException
    */
   public static void test1()
           throws IOException, InvalidTransformException,
                  CanonicalizationException, InvalidCanonicalizerException,
                  TransformationException,
                  XMLSignatureException, XMLSecurityException,
                  ParserConfigurationException {

      // base64 encoded
      String s1 =
         "VGhlIFVSSSBvZiB0aGUgdHJhbnNmb3JtIGlzIGh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1s\n"
         + "ZHNpZyNiYXNlNjQ=";

      // base64 encoded twice
      String s2 =
         "VkdobElGVlNTU0J2WmlCMGFHVWdkSEpoYm5ObWIzSnRJR2x6SUdoMGRIQTZMeTkzZDNjdWR6TXVi\n"
         + "M0puTHpJd01EQXZNRGt2ZUcxcwpaSE5wWnlOaVlYTmxOalE9";
      Document doc = TransformBase64DecodeTest.createDocument();
      Transforms t = new Transforms(doc);
      doc.appendChild(t.getElement());
      t.addTransform(TransformBase64Decode.implementedTransformURI);

      XMLSignatureInput in =
         new XMLSignatureInput(new ByteArrayInputStream(s1.getBytes()));
      XMLSignatureInput out = t.performTransforms(in);
      String result = new String(out.getBytes());

      assertTrue(
         result.equals(
            "The URI of the transform is http://www.w3.org/2000/09/xmldsig#base64"));
   }

   /**
    * Method testTwice
    *
    * @throws CanonicalizationException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws InvalidTransformException
    * @throws NotYetImplementedException
    * @throws ParserConfigurationException
    * @throws TransformationException
    * @throws XMLSecurityException
    * @throws XMLSignatureException
    */
   public static void test2()
           throws IOException, InvalidTransformException,
                  TransformationException, CanonicalizationException,
                  InvalidCanonicalizerException,
                  XMLSignatureException, XMLSecurityException,
                  ParserConfigurationException {

      // base64 encoded twice
      String s2 =
         "VkdobElGVlNTU0J2WmlCMGFHVWdkSEpoYm5ObWIzSnRJR2x6SUdoMGRIQTZMeTkzZDNjdWR6TXVi\n"
         + "M0puTHpJd01EQXZNRGt2ZUcxcwpaSE5wWnlOaVlYTmxOalE9";
      Document doc = TransformBase64DecodeTest.createDocument();
      Transforms t = new Transforms(doc);
      doc.appendChild(t.getElement());

      t.addTransform(TransformBase64Decode.implementedTransformURI);

      XMLSignatureInput in =
         new XMLSignatureInput(new ByteArrayInputStream(s2.getBytes()));
      XMLSignatureInput out = t.performTransforms(t.performTransforms(in));
      String result = new String(out.getBytes());

      assertTrue(
         result.equals(
            "The URI of the transform is http://www.w3.org/2000/09/xmldsig#base64"));
   }

   /**
    * Method test3
    *
    * @throws Exception
    */
   public static void test3() throws Exception {
      //J-
      String input = ""
         + "<Object xmlns:signature='http://www.w3.org/2000/09/xmldsig#'>\n"
         + "<signature:Base64>\n"
         + "VGhlIFVSSSBvZiB0aGU   gdHJhbn<RealText>Nmb  3JtIGlzIG<test/>h0dHA6</RealText>Ly93d3cudzMub3JnLzIwMDAvMDkveG1s\n"
         + "ZHNpZyNiYXNlNjQ=\n"
         + "</signature:Base64>\n"
         + "</Object>\n"
         ;
      //J+
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

      dfactory.setNamespaceAware(true);

      DocumentBuilder db = dfactory.newDocumentBuilder();

      db.setErrorHandler(new org.apache.xml.security.utils
         .IgnoreAllErrorHandler());

      Document doc = db.parse(new ByteArrayInputStream(input.getBytes()));
      XMLUtils.circumventBug2650(doc);
      Element nscontext = XMLUtils.createDSctx(doc, "ds", Constants.SignatureSpecNS);

      Node base64Node = XPathAPI.selectSingleNode(doc, "//ds:Base64", nscontext);
      XMLSignatureInput xmlinput = new XMLSignatureInput(base64Node);

      Document doc2 = TransformBase64DecodeTest.createDocument();
      Transforms t = new Transforms(doc2);
      doc2.appendChild(t.getElement());
      t.addTransform(Transforms.TRANSFORM_BASE64_DECODE);

      XMLSignatureInput out = t.performTransforms(xmlinput);
      String result = new String(out.getBytes());

      assertTrue("\"" + result + "\"", result.equals(
            "The URI of the transform is http://www.w3.org/2000/09/xmldsig#base64"));
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
