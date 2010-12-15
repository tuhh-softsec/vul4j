/*
 * Copyright  1999-2009 The Apache Software Foundation.
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
package org.apache.xml.security.test.external.org.apache.xalan.XPathAPI;



import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xml.security.utils.Constants;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * This test is to ensure that the owner element of an Attribute is on the
 * ancestor-or-self axis.
 *
 * @author $Author$
 */
public class AttributeAncestorOrSelfTest extends TestCase {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(
                    AttributeAncestorOrSelfTest.class.getName());


   /**
    * Method suite
    *
    *
    */
   public static Test suite() {
      return new TestSuite(AttributeAncestorOrSelfTest.class);
   }

   /**
    * Constructor AttributeAncestorOrSelf
    *
    * @param Name_
    */
   public AttributeAncestorOrSelfTest(String Name_) {
      super(Name_);
   }

   /**
    * Method main
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading",
                                AttributeAncestorOrSelfTest.class.getName() };

      junit.textui.TestRunner.main(testCaseName);
   }

   /**
    * Process input args and execute the XPath.
    *
    * @param xmlString
    * @param ctxNodeStr
    * @param evalStr
    *
    * @throws Exception
    */
   static private boolean isAncestorOf(
           String xmlString, String ctxNodeStr, String evalStr)
              throws Exception {

      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

      dfactory.setValidating(false);
      dfactory.setNamespaceAware(true);

      DocumentBuilder db = dfactory.newDocumentBuilder();
      Document document =
         db.parse(new ByteArrayInputStream(_nodeSetInput1.getBytes()));
      Element nscontext = document.createElementNS(null, "nscontext");

      nscontext.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:ds", "http://www.w3.org/2000/09/xmldsig#");

      Node ctxNode = XPathAPI.selectSingleNode(document, ctxNodeStr, nscontext);
      XObject include = XPathAPI.eval(ctxNode, evalStr, nscontext);

      return include.bool();
   }

   //J-
   static final String _nodeSetInput1 =
     "<?xml version=\"1.0\"?>\n"
   + "<ds:Signature xmlns:ds='http://www.w3.org/2000/09/xmldsig#'>" + "\n"
   + "<ds:Object Id='id1'>" + "\n"
   + "<!-- the comment -->and text"
   + "</ds:Object>" + "\n"
   + "</ds:Signature>";
   //J+

   /**
    * Method test01
    *
    * @throws Exception
    */
   public static void test01() throws Exception {

      String ctxNodeStr = "/ds:Signature/ds:Object";
      String evalStr = "ancestor-or-self::ds:Signature";

      assertTrue("Bad " + ctxNodeStr + " " + evalStr + "  ",
                 isAncestorOf(_nodeSetInput1, ctxNodeStr, evalStr));
   }

   /**
    * Method test02
    *
    * @throws Exception
    */
   public static void test02() throws Exception {

      String ctxNodeStr = "/ds:Signature/ds:Object/text()";
      String evalStr = "ancestor-or-self::ds:Signature";

      assertTrue("Bad " + ctxNodeStr + " " + evalStr + "  "  ,
                 isAncestorOf(_nodeSetInput1, ctxNodeStr, evalStr));
   }

   /**
    * Method test03
    *
    * @throws Exception
    */
   public static void test03() throws Exception {

      String ctxNodeStr = "/ds:Signature/ds:Object/@Id";
      String evalStr = "ancestor-or-self::ds:Object";

      assertTrue("Bad " + ctxNodeStr + " " + evalStr + "  " ,
                 isAncestorOf(_nodeSetInput1, ctxNodeStr, evalStr));
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
