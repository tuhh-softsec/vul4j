
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
package org.apache.xml.security.test.external.org.apache.xalan.XPathAPI;



// This file uses 4 space indents, no tabs.
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
import org.apache.xml.utils.TreeWalker;
import org.apache.xml.utils.DOMBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

// Imported JAVA API for XML Parsing 1.0 classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

// Imported Serializer classes
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;


/**
 * Testcase for testing the <A HREF="http://nagoya.apache.org/bugzilla/show_bug.cgi?id=1425">Xalan Bug 1425</A>.
 *
 * This fails with Xalan v2.1.0 and works with Xalan v2.2D6.
 */
public class XalanBug1425Test extends TestCase {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(XalanBug1425Test.class.getName());

   /** Field xercesVerStr */
   static String xercesVerStr = XMLUtils.getXercesVersion();

   /** Field xalanVerStr */
   static String xalanVerStr = XMLUtils.getXalanVersion();

   /**
    * Method suite
    *
    * @return
    */
   public static Test suite() {
      return new TestSuite(XalanBug1425Test.class);
   }

   /**
    * Constructor XalanBug1425Test
    *
    * @param Name_
    */
   public XalanBug1425Test(String Name_) {
      super(Name_);
   }

   /**
    * Method main
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading",
                                XalanBug1425Test.class.getName() };

      junit.textui.TestRunner.main(testCaseName);
   }

   /**
    * Process input args and execute the XPath.
    *
    * @param xmlString
    * @param xpath
    * @return
    * @throws Exception
    */
   static private boolean containsDocumentElement(
           String xmlString, String xpath) throws Exception {

      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dfactory.newDocumentBuilder();
      Document doc = db.parse(new ByteArrayInputStream(xmlString.getBytes()));

      // Set up an identity transformer to use as serializer.
      Transformer serializer =
         TransformerFactory.newInstance().newTransformer();

      serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

      // Use the simple XPath API to select a nodeIterator.
      // System.out.println("Querying DOM using " + xpath);
      NodeIterator nl = XPathAPI.selectNodeIterator(doc, xpath);

      // Serialize the found nodes to System.out.
      // System.out.println("<output>");
      String[] nodeTypeString = new String[]{ "", "ELEMENT", "ATTRIBUTE",
                                              "TEXT_NODE", "CDATA_SECTION",
                                              "ENTITY_REFERENCE", "ENTITY",
                                              "PROCESSING_INSTRUCTION",
                                              "COMMENT", "DOCUMENT",
                                              "DOCUMENT_TYPE",
                                              "DOCUMENT_FRAGMENT", "NOTATION" };
      int i = 0;
      Node n;

      while ((n = nl.nextNode()) != null) {

         // System.out.println("<node" + ++i + " nodeType=\"" + nodeTypeString[n.getNodeType()] + "\">");
         // serializer.transform(new DOMSource(n), new StreamResult(System.out));
         // System.out.println("</node" + i + ">");
         // System.out.println();
         if (n == doc.getDocumentElement()) {
            return true;
         }
      }

      // System.out.println("</output>");
      return false;
   }

   /**
    * Method testBad01
    *
    * @throws Exception
    */
   public static void testBad01() throws Exception {

      String xml = "<doc><a /> </doc><!-- -->";
      String desc = " # mixed content and following comment";
      String xpath = "(//.)";

      assertTrue("Bad " + xml + desc + "  " + xalanVerStr,
                 containsDocumentElement(xml, xpath));
   }

   /**
    * Method testBad02
    *
    * @throws Exception
    */
   public static void testBad02() throws Exception {

      String xml = "<doc><a /> </doc><?pi?>";
      String desc = " # mixed content and following PI";
      String xpath = "(//.)";

      assertTrue("Bad " + xml + desc + "  " + xalanVerStr,
                 containsDocumentElement(xml, xpath));
   }

   /**
    * Method testBad03
    *
    * @throws Exception
    */
   public static void testBad03() throws Exception {

      String xml = "<doc><a /><b /></doc><!-- -->";
      String desc = " # mixed content and following comment";
      String xpath = "(//.)";

      assertTrue("Bad " + xml + desc + "  " + xalanVerStr,
                 containsDocumentElement(xml, xpath));
   }

   /**
    * Method testGood01
    *
    * @throws Exception
    */
   public static void testGood01() throws Exception {

      String xml = "<doc><a /></doc><!-- -->";
      String desc = " # 'clean' content and following comment";
      String xpath = "(//.)";

      assertTrue("Good " + xml + desc, containsDocumentElement(xml, xpath));
   }

   /**
    * Method testGood02
    *
    * @throws Exception
    */
   public static void testGood02() throws Exception {

      String xml = "<doc><a /> </doc>";
      String desc = " # mixed content and nothing follows";
      String xpath = "(//.)";

      assertTrue("Good " + xml + desc, containsDocumentElement(xml, xpath));
   }

   /**
    * Method testGood03
    *
    * @throws Exception
    */
   public static void testGood03() throws Exception {

      String xml = "<!-- --><doc><a /> </doc>";
      String desc = " # mixed content and preceding comment";
      String xpath = "(//.)";

      assertTrue("Good " + xml + desc, containsDocumentElement(xml, xpath));
   }

   /**
    * Method testGood04
    *
    * @throws Exception
    */
   public static void testGood04() throws Exception {

      String xml = "<?pi?><doc><a /> </doc>";
      String desc = " # mixed content and preceding PI";
      String xpath = "(//.)";

      assertTrue("Good " + xml + desc, containsDocumentElement(xml, xpath));
   }

   /**
    * Method testGood05
    *
    * @throws Exception
    */
   public static void testGood05() throws Exception {

      String xml = "<doc><a /><b /></doc>";
      String desc = " # mixed ElemContent";
      String xpath = "(//.)";

      assertTrue("Good " + xml + desc, containsDocumentElement(xml, xpath));
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
