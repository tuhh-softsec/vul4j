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
package org.apache.xml.security.test.c14n.helper;



import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactory;
import org.apache.xml.serialize.XMLSerializer;

// import org.apache.xml.serialize.OutputFormat;
import org.w3c.dom.*;
import org.apache.xml.security.c14n.helper.AttrCompare;
import org.apache.xml.security.utils.Constants;


/**
 * Unit test for {@link org.apache.xml.security.c14n.AttrCompare#compare}
 *
 * @author Christian Geuer-Pollmann
 */
public class AttrCompareTest extends TestCase {

   /**
    * Method suite
    *
    * @return
    */
   public static Test suite() {
      return new TestSuite(AttrCompareTest.class);
   }

   /**
    * Constructor AttrCompareTest
    *
    * @param Name_
    */
   public AttrCompareTest(String Name_) {
      super(Name_);
   }

   /**
    * Method main
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading", AttrCompareTest.class.getName() };

      junit.textui.TestRunner.main(testCaseName);
   }

   /**
    * Method createAttr
    *
    * @param doc
    * @param QName
    * @param Value
    * @param NamespaceURI
    * @return
    */
   private static Attr createAttr(Document doc, String QName, String Value,
                                  String NamespaceURI) {

      Attr attr = null;

      if ((NamespaceURI != null) && (NamespaceURI.length() > 0)) {
         attr = doc.createAttributeNS(NamespaceURI, QName);
      } else {
         attr = doc.createAttribute(QName);
      }

      attr.appendChild(doc.createTextNode(Value));

      return attr;
   }

   /**
    * Method createDoc
    *
    * @param documentElement
    * @return
    * @throws ParserConfigurationException
    */
   private static Document createDoc(String documentElement)
           throws ParserConfigurationException {

      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder documentBuilder = dfactory.newDocumentBuilder();

      dfactory.setNamespaceAware(true);

      Document doc = documentBuilder.newDocument();
      Element root = doc.createElement(documentElement);

      doc.appendChild(root);

      return doc;
   }

   /**
    * Method testA1
    *
    * @throws ParserConfigurationException
    */
   public static void testA1() throws ParserConfigurationException {

      Document doc = createDoc("documentElement");
      Element root = doc.getDocumentElement();
      Attr attr0 = createAttr(doc, "xmlns", "http://default/", null);
      Attr attr1 = createAttr(doc, "xmlns:b", "http://val1/", null);

      root.setAttributeNode(attr0);
      root.setAttributeNode(attr1);

      NamedNodeMap nnm = root.getAttributes();

      assertEquals("nnm.getLength()", nnm.getLength(), 2);

      Attr attr00 = (Attr) nnm.item(0);
      Attr attr10 = (Attr) nnm.item(1);

      assertNotNull("Attribute attr00", attr00);
      assertNotNull("Attribute attr10", attr10);

      AttrCompare attrCompare = new AttrCompare();

      assertEquals("attrCompare.compare((Object) attr0, (Object) attr1)", -1,
                   attrCompare.compare((Object) attr0, (Object) attr1));
      assertEquals("attrCompare.compare((Object) attr1, (Object) attr0)", 1,
                   attrCompare.compare((Object) attr1, (Object) attr0));
   }

   /**
    * Method testA2
    *
    * @throws ParserConfigurationException
    */
   public static void testA2() throws ParserConfigurationException {

      Document doc = createDoc("documentElement");
      Element root = doc.getDocumentElement();
      Attr attr0 = createAttr(doc, "aAttr", "val0", null);
      Attr attr1 = createAttr(doc, "bAttr", "val1", null);

      root.setAttributeNode(attr0);
      root.setAttributeNode(attr1);

      NamedNodeMap nnm = root.getAttributes();

      assertEquals("nnm.getLength()", nnm.getLength(), 2);

      Attr attr00 = (Attr) nnm.item(0);
      Attr attr10 = (Attr) nnm.item(1);

      assertNotNull("Attribute attr00", attr00);
      assertNotNull("Attribute attr10", attr10);

      AttrCompare attrCompare = new AttrCompare();

      assertEquals("attrCompare.compare((Object) attr0, (Object) attr1)", -1,
                   attrCompare.compare((Object) attr0, (Object) attr1));
      assertEquals("attrCompare.compare((Object) attr1, (Object) attr0)", 1,
                   attrCompare.compare((Object) attr1, (Object) attr0));
   }

   /**
    * This test uses teh attrs[] array to compare every attribute against
    * the others (and vice versa).
    *
    * The attribute values are taken from example 3.3 Start and End Tags
    * http://www.w3.org/TR/2001/REC-xml-c14n-20010315#Example-SETags
    *
    * @throws ParserConfigurationException
    */
   public static void testComplete() throws ParserConfigurationException {

      /* <e5 xmlns="http://example.org"
       *     xmlns:a="http://www.w3.org"
       *     xmlns:b="http://www.ietf.org"
       *     attr="I'm"
       *     attr2="all"
       *     b:attr="sorted"
       *     a:attr="out"></e5>
       */
      Document doc = createDoc("documentElement");
      Element root = doc.getDocumentElement();

      // This List has to be ordered to verify correctness of the comparison
      //J-
      Attr attrs[] = {
         createAttr(doc, "xmlns", "http://example.org", "http://www.w3.org/2000/xmlns/"),
         createAttr(doc, "xmlns:a", "http://www.w3.org", "http://www.w3.org/2000/xmlns/"),
         createAttr(doc, "xmlns:b", "http://www.ietf.org", "http://www.w3.org/2000/xmlns/"),
         createAttr(doc, "attr", "I'm", null),
         createAttr(doc, "attr2", "all", null),
         createAttr(doc, "b:attr", "sorted", "http://www.ietf.org"),
         createAttr(doc, "a:attr", "out", "http://www.w3.org") };

      //J+
      for (int i = 0; i < attrs.length; i++) {
         root.setAttributeNode(attrs[i]);
      }

      NamedNodeMap nnm = root.getAttributes();

      assertEquals("nnm.getLength()", nnm.getLength(), attrs.length);

      for (int i = 0; i < attrs.length; i++) {
         Attr attr = attrs[i];

         assertNotNull("Attribute attr", attr);
      }

      AttrCompare attrCompare = new AttrCompare();

      for (int i = 0; i < attrs.length; i++) {
         for (int j = i + 1; j < attrs.length; j++) {
            assertEquals("attrCompare.compare((Object) attr0, (Object) attr1)",
                         -1,
                         attrCompare.compare((Object) attrs[i],
                                             (Object) attrs[j]));
            assertEquals("attrCompare.compare((Object) attr1, (Object) attr0)",
                         1, attrCompare.compare((Object) attrs[j],
                                                (Object) attrs[i]));
         }
      }
   }

   static {
      org.apache.xml.security.Init.init();
   }
}    //public class AttrCompareTest extends TestCase

