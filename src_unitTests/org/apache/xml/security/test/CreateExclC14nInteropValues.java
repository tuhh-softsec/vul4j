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
package org.apache.xml.security.test;



import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.*;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.signature.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.transforms.params.*;


/**
 * Class CreateExclC14nInteropValues
 *
 * @author $Author$
 * @version $Revision$
 */
public class CreateExclC14nInteropValues {

   /**
    * Method main
    *
    * @param unused
    * @throws Exception
    */
   public static void main(String unused[]) throws Exception {

      org.apache.xml.security.Init.init();

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.newDocument();
      String directory = "data/org/apache/xml/security/c14n/outExcl/";
      File signatureFile = new File(directory + "apacheSignature.xml");
      XMLSignature xmlSignature = new XMLSignature(doc,
                                     signatureFile.toURL().toString(),
                                     XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

      doc.appendChild(xmlSignature.getElement());

      {

         // ref 0
         Transforms tf = new Transforms(doc);

         {
            XPathContainer xc = new XPathContainer(doc);

            xc.setXPath(
               "self::Parent or (parent::Parent and not(self::Child)) or self::GrandChild or parent::GrandChild");
            tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
         }

         xmlSignature.addDocument("iaikTests.example1.xml", tf);
      }

      {

         // ref 1
         Transforms tf = new Transforms(doc);

         {
            XPathContainer xc = new XPathContainer(doc);

            xc.setXPath(
               "self::Parent or (parent::Parent and not(self::Child)) or self::GrandChild or parent::GrandChild");
            tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
         }

         tf.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
         xmlSignature.addDocument("iaikTests.example1.xml", tf);
      }

      {

         // ref 2
         Transforms tf = new Transforms(doc);

         {
            XPathContainer xc = new XPathContainer(doc);

            xc.setXPathNamespaceContext("xmlns:default", "http://example.org");
            xc.setXPath(
               "self::Parent or (parent::Parent and not(self::default:Child)) or self::GrandChild or parent::GrandChild");
            tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
         }

         xmlSignature.addDocument("iaikTests.example2.xml", tf);
      }

      {

         // ref 3
         Transforms tf = new Transforms(doc);

         {
            XPathContainer xc = new XPathContainer(doc);

            xc.setXPathNamespaceContext("xmlns:default", "http://example.org");
            xc.setXPath(
               "self::Parent or (parent::Parent and not(self::default:Child)) or self::GrandChild or parent::GrandChild");
            tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
         }

         tf.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
         xmlSignature.addDocument("iaikTests.example2.xml", tf);
      }

      {

         // ref 4
         Transforms tf = new Transforms(doc);

         {
            XPathContainer xc = new XPathContainer(doc);

            xc.setXPathNamespaceContext("xmlns:default",
                                        "http://example.org/default");
            xc.setXPathNamespaceContext("xmlns:ns1", "http://example.org/ns1");
            xc.setXPath(
               "self::default:Parent or (parent::default:Parent and not(self::default:Child)) or self::ns1:GrandChild or parent::ns1:GrandChild or self::default:GrandChild or parent::default:GrandChild");
            tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
         }

         xmlSignature.addDocument("iaikTests.example3.xml", tf);
      }

      {

         // ref 5
         Transforms tf = new Transforms(doc);

         {
            XPathContainer xc = new XPathContainer(doc);

            xc.setXPathNamespaceContext("xmlns:default",
                                        "http://example.org/default");
            xc.setXPathNamespaceContext("xmlns:ns1", "http://example.org/ns1");
            xc.setXPath(
               "self::default:Parent or (parent::default:Parent and not(self::default:Child)) or self::ns1:GrandChild or parent::ns1:GrandChild or self::default:GrandChild or parent::default:GrandChild");
            tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
         }

         tf.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
         xmlSignature.addDocument("iaikTests.example3.xml", tf);
      }

      {

         // ref 6
         Transforms tf = new Transforms(doc);

         {
            XPathContainer xc = new XPathContainer(doc);

            xc.setXPathNamespaceContext("xmlns:ns1", "http://example.org/ns1");
            xc.setXPath(
               "self::Parent or (parent::Parent and not(self::Child)) or self::ns1:GrandChild or parent::ns1:GrandChild");
            tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
         }

         xmlSignature.addDocument("iaikTests.example4.xml", tf);
      }

      {

         // ref 7
         Transforms tf = new Transforms(doc);

         {
            XPathContainer xc = new XPathContainer(doc);

            xc.setXPathNamespaceContext("xmlns:ns1", "http://example.org/ns1");
            xc.setXPath(
               "self::Parent or (parent::Parent and not(self::Child)) or self::ns1:GrandChild or parent::ns1:GrandChild");
            tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
         }

         {
            InclusiveNamespaces incNS = new InclusiveNamespaces(doc, "ns2");

            tf.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS,
                            incNS.getElement());
         }

         xmlSignature.addDocument("iaikTests.example4.xml", tf);
      }

      {

         // ref 8
         ObjectContainer obj = new ObjectContainer(doc);
         String id = "object1";

         obj.setId(id);

         String xmlStr = "" + "<included    xml:lang='de'>" + "\n"
                         + "<notIncluded xml:lang='de'>" + "\n"
                         + "<notIncluded xml:lang='uk'>" + "\n"
                         + "<included                 >" + "\n" + "</included>"
                         + "\n" + "</notIncluded>" + "\n" + "</notIncluded>"
                         + "\n" + "</included>";
         Document importDoc =
            db.parse(new ByteArrayInputStream(xmlStr.getBytes()));

         obj.getElement().appendChild(doc.createTextNode("\n"));
         obj.getElement()
            .appendChild(doc.importNode(importDoc.getDocumentElement(), true));
         obj.getElement().appendChild(doc.createTextNode("\n"));
         xmlSignature.appendObject(obj);

         // ref apache_8
         Transforms tf = new Transforms(doc);

         {
            XPathContainer xc = new XPathContainer(doc);

            xc.setXPath("self::node()[local-name()='included']");
            tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
         }

         xmlSignature.addDocument("#" + id, tf);
      }

      {

         // ref 9
         ObjectContainer obj = new ObjectContainer(doc);
         String id = "object2";

         obj.setId(id);

         String xmlStr = "" + "<included    xml:lang='uk'>" + "\n"
                         + "<notIncluded xml:lang='de'>" + "\n"
                         + "<notIncluded xml:lang='uk'>" + "\n"
                         + "<included                 >" + "\n" + "</included>"
                         + "\n" + "</notIncluded>" + "\n" + "</notIncluded>"
                         + "\n" + "</included>";
         Document importDoc =
            db.parse(new ByteArrayInputStream(xmlStr.getBytes()));

         obj.getElement().appendChild(doc.createTextNode("\n"));
         obj.getElement()
            .appendChild(doc.importNode(importDoc.getDocumentElement(), true));
         obj.getElement().appendChild(doc.createTextNode("\n"));
         xmlSignature.appendObject(obj);

         // ref apache_8
         Transforms tf = new Transforms(doc);

         {
            XPathContainer xc = new XPathContainer(doc);

            xc.setXPath("self::node()[local-name()='included']");
            tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
         }

         xmlSignature.addDocument("#" + id, tf);
      }

      {

         // ref 10
         ObjectContainer obj = new ObjectContainer(doc);
         String id = "object3";

         obj.setId(id);

         String xmlStr = "" + "<included    xml:lang='de'>" + "\n"
                         + "<notIncluded xml:lang='de'>" + "\n"
                         + "<notIncluded xml:lang='uk'>" + "\n"
                         + "<included    xml:lang='de'>" + "\n" + "</included>"
                         + "\n" + "</notIncluded>" + "\n" + "</notIncluded>"
                         + "\n" + "</included>";
         Document importDoc =
            db.parse(new ByteArrayInputStream(xmlStr.getBytes()));

         obj.getElement().appendChild(doc.createTextNode("\n"));
         obj.getElement()
            .appendChild(doc.importNode(importDoc.getDocumentElement(), true));
         obj.getElement().appendChild(doc.createTextNode("\n"));
         xmlSignature.appendObject(obj);

         // ref apache_8
         Transforms tf = new Transforms(doc);

         {
            XPathContainer xc = new XPathContainer(doc);

            xc.setXPath("self::node()[local-name()='included']");
            tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
         }

         xmlSignature.addDocument("#" + id, tf);
      }

      {

         // ref 11
         ObjectContainer obj = new ObjectContainer(doc);
         String id = "object4";

         obj.setId(id);

         String xmlStr = "" + "<included    xml:lang='de'>" + "\n"
                         + "<included    xml:lang='de'>" + "\n"
                         + "<notIncluded xml:lang='uk'>" + "\n"
                         + "<included                 >" + "\n" + "</included>"
                         + "\n" + "</notIncluded>" + "\n" + "</included>"
                         + "\n" + "</included>";
         Document importDoc =
            db.parse(new ByteArrayInputStream(xmlStr.getBytes()));

         obj.getElement().appendChild(doc.createTextNode("\n"));
         obj.getElement()
            .appendChild(doc.importNode(importDoc.getDocumentElement(), true));
         obj.getElement().appendChild(doc.createTextNode("\n"));
         xmlSignature.appendObject(obj);

         Transforms tf = new Transforms(doc);

         {
            XPathContainer xc = new XPathContainer(doc);

            xc.setXPath("self::node()[local-name()='included']");
            tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
         }

         xmlSignature.addDocument("#" + id, tf);
      }

      {

         // ref 12
         ObjectContainer obj = new ObjectContainer(doc);
         String id = "object5";

         obj.setId(id);

         String xmlStr = "" + "<included                         xml:lang='de'>"
                         + "\n"
                         + "<included                         xml:lang='de'>"
                         + "\n"
                         + "<notIncluded xml:space='preserve' xml:lang='uk'>"
                         + "\n" + "<included                 >" + "\n"
                         + "</included>" + "\n" + "</notIncluded>" + "\n"
                         + "</included>" + "\n" + "</included>";
         Document importDoc =
            db.parse(new ByteArrayInputStream(xmlStr.getBytes()));

         obj.getElement().appendChild(doc.createTextNode("\n"));
         obj.getElement()
            .appendChild(doc.importNode(importDoc.getDocumentElement(), true));
         obj.getElement().appendChild(doc.createTextNode("\n"));
         xmlSignature.appendObject(obj);

         Transforms tf = new Transforms(doc);

         {
            XPathContainer xc = new XPathContainer(doc);

            xc.setXPath("self::node()[local-name()='included']");
            tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
         }

         xmlSignature.addDocument("#" + id, tf);
      }

      {

         // ref 13
         ObjectContainer obj = new ObjectContainer(doc);
         String id = "object6";

         obj.setId(id);

         String xmlStr = "" + "<included   xml:space='preserve'  xml:lang='de'>"
                         + "\n"
                         + "<included                         xml:lang='de'>"
                         + "\n"
                         + "<notIncluded                      xml:lang='uk'>"
                         + "\n" + "<included>" + "\n" + "</included>" + "\n"
                         + "</notIncluded>" + "\n" + "</included>" + "\n"
                         + "</included>";
         Document importDoc =
            db.parse(new ByteArrayInputStream(xmlStr.getBytes()));

         obj.getElement().appendChild(doc.createTextNode("\n"));
         obj.getElement()
            .appendChild(doc.importNode(importDoc.getDocumentElement(), true));
         obj.getElement().appendChild(doc.createTextNode("\n"));
         xmlSignature.appendObject(obj);

         Transforms tf = new Transforms(doc);

         {
            XPathContainer xc = new XPathContainer(doc);

            xc.setXPath("self::node()[local-name()='included']");
            tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
         }

         xmlSignature.addDocument("#" + id, tf);
      }

      {

         // ref 13b
         String id = "object6";
         Transforms tf = new Transforms(doc);

         {
            XPathContainer xc = new XPathContainer(doc);

            xc.setXPath("self::node()[local-name()='included']");
            tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
            tf.addTransform(Transforms.TRANSFORM_C14N_OMIT_COMMENTS);
         }

         xmlSignature.addDocument("#" + id, tf);
      }

      {

         // ref 13c
         String id = "object6";
         Transforms tf = new Transforms(doc);

         {
            XPathContainer xc = new XPathContainer(doc);

            xc.setXPath("self::node()[local-name()='included']");
            tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
            tf.addTransform(Transforms.TRANSFORM_C14N_OMIT_COMMENTS);
            tf.addTransform(Transforms.TRANSFORM_C14N_OMIT_COMMENTS);
         }

         xmlSignature.addDocument("#" + id, tf);
         // xmlSignature.addDocument("#" + id, tf, org.apache.xml.security.algorithms.MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1, "ref13c", null);
      }

      String secretKey = "secret";

      xmlSignature.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                           + "\" are used for signing ("
                                           + secretKey.length() + " octets)");
      xmlSignature.sign(xmlSignature.createSecretKey(secretKey.getBytes()));

      FileOutputStream fos = new FileOutputStream(signatureFile);

      XMLUtils.outputDOM(doc, fos);
      fos.close();

      int length = xmlSignature.getSignedInfo().getLength();

      for (int i = 0; i < length; i++) {
         String fname = directory + "c14n-" + i + "-apache.xml";

         System.out.println(fname);
         JavaUtils
            .writeBytesToFilename(fname, xmlSignature.getSignedInfo()
               .getReferencedContentAfterTransformsItem(i).getBytes());
      }

      XMLSignature s = new XMLSignature(doc.getDocumentElement(),
                                        signatureFile.toURL().toString());
      boolean verify =
         s.checkSignatureValue(s.createSecretKey("secret".getBytes()));

      System.out.println("verify=" + verify);

      System.out.println("");

      XMLUtils.outputDOMc14nWithComments(doc, System.out);
   }
}
