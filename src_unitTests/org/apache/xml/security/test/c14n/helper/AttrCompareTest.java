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
package org.apache.xml.security.test.c14n.helper;



import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xml.security.c14n.helper.AttrCompare;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;


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
         attr = doc.createAttributeNS(null, QName);
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
      Element root = doc.createElementNS(null, documentElement);

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
      Attr attr0 = createAttr(doc, "xmlns", "http://default/", Constants.NamespaceSpecNS);
      Attr attr1 = createAttr(doc, "xmlns:b", "http://val1/", Constants.NamespaceSpecNS);

      root.setAttributeNode(attr0);
      root.setAttributeNode(attr1);

      NamedNodeMap nnm = root.getAttributes();

      assertEquals("nnm.getLength()", nnm.getLength(), 2);

      Attr attr00 = (Attr) nnm.item(0);
      Attr attr10 = (Attr) nnm.item(1);

      assertNotNull("Attribute attr00", attr00);
      assertNotNull("Attribute attr10", attr10);

      AttrCompare attrCompare = new AttrCompare();

      assertTrue(attr0 + " < " + attr1, attrCompare.compare(attr0, attr1) < 0);
      assertTrue(attr1 + " < " + attr0, attrCompare.compare(attr1, attr0) > 0);
   }

   public static void testA2() throws ParserConfigurationException {

      Document doc = createDoc("documentElement");
      Attr attr0 = doc.createAttributeNS(null, "foo");
      Attr attr1 = doc.createAttributeNS("http://goo", "goo:foo");

      System.out.println("Attr1: " + attr1 + " (" + attr1.getLocalName()  +")");


      AttrCompare attrCompare = new AttrCompare();

      assertTrue(attr0 + " < " + attr1, attrCompare.compare(attr0, attr1) < 0);
      assertTrue(attr1 + " < " + attr0, attrCompare.compare(attr1, attr0) > 0);

   }


   /**
    * Method testA2
    *
    * @throws ParserConfigurationException
    */
   public static void _testA2() throws ParserConfigurationException {

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

      assertTrue(attr0 + " < " + attr1, attrCompare.compare(attr0, attr1) < 0);
      assertTrue(attr1 + " < " + attr0, attrCompare.compare(attr1, attr0) > 0);
   }

   /**
    * This test uses the attrs[] array to compare every attribute against
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
         createAttr(doc, "xmlns", "http://example.org", Constants.NamespaceSpecNS),
         createAttr(doc, "xmlns:a", "http://www.w3.org", Constants.NamespaceSpecNS),
         createAttr(doc, "xmlns:b", "http://www.ietf.org", Constants.NamespaceSpecNS),
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
            Attr attr0 = attrs[i];
            Attr attr1 = attrs[j];
            assertTrue(attr0 + " < " + attr1, attrCompare.compare(attr0, attr1) < 0);
            assertTrue(attr1 + " < " + attr0, attrCompare.compare(attr1, attr0) > 0);
         }
      }
   }

   static {
      org.apache.xml.security.Init.init();
   }
}    //public class AttrCompareTest extends TestCase

