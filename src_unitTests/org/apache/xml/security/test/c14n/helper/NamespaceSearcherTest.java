
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
import java.util.Map;
import java.util.HashMap;
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
import org.apache.xpath.XPathAPI;
import org.apache.xml.security.c14n.helper.NamespaceSearcher;
import org.apache.xml.security.utils.Constants;


/**
 * Unit test for {@link org.apache.xml.security.c14n.NamespaceSearcher#doCompare}
 *
 * @author Christian Geuer-Pollmann
 */
public class NamespaceSearcherTest extends TestCase {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(NamespaceSearcherTest.class.getName());

   /**
    * Method suite
    *
    * @return
    */
   public static Test suite() {
      return new TestSuite(NamespaceSearcherTest.class);
   }

   /**
    * Constructor AttrCompareTest
    *
    * @param Name_
    */
   public NamespaceSearcherTest(String Name_) {
      super(Name_);
   }

   /**
    * Method main
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading",
                                NamespaceSearcherTest.class.getName() };

      junit.textui.TestRunner.main(testCaseName);
   }

   /**
    * Method test1
    *
    * @throws Exception
    */
   public void test1() throws Exception {
      //J-
      String input = ""
         + "<included                                 xmlns:ietf='http://www.ietf.org/' >"
         + "<notIncluded                              xmlns:w3c='http://www.w3c.org/'   >"
         + "<notIncluded                              xmlns:ietf='http://www.ietf.org/' >"
         + "<included    xmlns='http://www.ietf.org'                                    >"
         + "<included                                                                   >"
         + "<included    xmlns='http://www.w3c.org'                                     >"
         + "<included                                                                   >"
         + "<notIncluded xmlns='http://www.ietf.org'                                    >"
         + "<notIncluded                                                                >"
         + "<notIncluded                              xmlns:w3c='http://www.w3c.org/'   >"
         + "<notIncluded                                                                >"
         + "<notIncluded xmlns='http://www.w3c.org'                                     >"
         + "<notIncluded                                                                >"
         + "<notIncluded                                                                >"
         + "<included                                 xmlns:ietf='http://www.ietf.org/' >"
         + "</included>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</included>"
         + "</included>"
         + "</included>"
         + "</included>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</included>";
      //J+
      String xpath = "(//*[local-name()='included'] | //@* | //namespace::*)";
      boolean invisibleDefault = true;
      boolean invisibleNonDefault = true;
      boolean visibleDefault = true;
      boolean visibleNonDefault = true;

      invisibleAncestorsContainDefaultNS(input, xpath, invisibleDefault,
                                         invisibleNonDefault, visibleDefault,
                                         visibleNonDefault);
   }

   /**
    * Method test2
    *
    * @throws Exception
    */
   public void test2() throws Exception {
      //J-
      String input = ""
         + "<included    >"
         + "<notIncluded >"
         + "<notIncluded >"
         + "<included    >"
         + "<included    >"
         + "<included    >"
         + "<included    >"
         + "<notIncluded >"
         + "<notIncluded >"
         + "<notIncluded >"
         + "<notIncluded >"
         + "<notIncluded >"
         + "<notIncluded >"
         + "<notIncluded >"
         + "<included    >"
         + "</included>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</included>"
         + "</included>"
         + "</included>"
         + "</included>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</included>";
      //J+
      String xpath = "(//*[local-name()='included'] | //@* | //namespace::*)";
      boolean invisibleDefault = false;
      boolean invisibleNonDefault = false;
      boolean visibleDefault = false;
      boolean visibleNonDefault = false;

      invisibleAncestorsContainDefaultNS(input, xpath, invisibleDefault,
                                         invisibleNonDefault, visibleDefault,
                                         visibleNonDefault);
   }

   /**
    * Method test3
    *
    * @throws Exception
    */
   public void test3() throws Exception {
      //J-
      String input = ""
         + "<included                                 xmlns:ietf='http://www.ietf.org/' >"
         // this default is not in the direct invisible ancestors
         + "<notIncluded xmlns='http://www.ietf2.org' xmlns:w3c='http://www.w3c.org/'   >"
         + "<notIncluded                              xmlns:ietf='http://www.ietf.org/' >"
         + "<included    xmlns='http://www.ietf.org'                                    >"
         + "<included                                                                   >"
         + "<included    xmlns='http://www.w3c.org'                                     >"
         + "<included                                                                   >"
         + "<notIncluded                                                                >"
         + "<notIncluded                                                                >"
         + "<notIncluded                              xmlns:w3c='http://www.w3c.org/'   >"
         + "<notIncluded                                                                >"
         + "<notIncluded                                                                >"
         + "<notIncluded                                                                >"
         + "<notIncluded                                                                >"
         + "<included                                 xmlns:ietf='http://www.ietf.org/' >"
         + "</included>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</included>"
         + "</included>"
         + "</included>"
         + "</included>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</included>";
      //J+
      String xpath = "(//*[local-name()='included'] | //@* | //namespace::*)";
      boolean invisibleDefault = false;
      boolean invisibleNonDefault = true;
      boolean visibleDefault = true;
      boolean visibleNonDefault = true;

      invisibleAncestorsContainDefaultNS(input, xpath, invisibleDefault,
                                         invisibleNonDefault, visibleDefault,
                                         visibleNonDefault);
   }

   /**
    * Method test4
    *
    * @throws Exception
    */
   public void test4() throws Exception {
      //J-
      String input = ""
         + "<included                                 xmlns:ietf='http://www.ietf.org/' >"
         + "<notIncluded                              xmlns:ietf='http://www.ietf.org/' >"
         + "<notIncluded                                                                >"
         + "<included    xmlns='http://www.ietf.org'                                    >"
         + "<included                                                                   >"
         + "<included    xmlns='http://www.w3c.org'                                     >"
         + "<included                                                                   >"
         + "<notIncluded xmlns='http://www.ietf.org'                                    >"
         + "<notIncluded                                                                >"
         + "<notIncluded                                                                >"
         + "<notIncluded                                                                >"
         + "<notIncluded xmlns='http://www.w3c.org'                                     >"
         + "<notIncluded                                                                >"
         + "<notIncluded                                                                >"
         + "<included                                 xmlns:ietf='http://www.ietf.org/' >"
         + "</included>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</included>"
         + "</included>"
         + "</included>"
         + "</included>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</included>";
      //J+
      String xpath = "(//*[local-name()='included'] | //@* | //namespace::*)";
      boolean invisibleDefault = true;
      boolean invisibleNonDefault = false;
      boolean visibleDefault = true;
      boolean visibleNonDefault = true;

      invisibleAncestorsContainDefaultNS(input, xpath, invisibleDefault,
                                         invisibleNonDefault, visibleDefault,
                                         visibleNonDefault);
   }

   /**
    * Method test5
    *
    * @throws Exception
    */
   public void test5() throws Exception {
      //J-
      String input = ""
         + "<included                                 xmlns:ietf='http://www.ietf.org/' >"
         + "<notIncluded                              xmlns:w3c='http://www.w3c.org/'   >"
         + "<notIncluded                              xmlns:ietf='http://www.ietf.org/' >"
         + "<included                                                                   >"
         + "<included                                                                   >"
         + "<included                                                                   >"
         + "<included                                                                   >"
         + "<notIncluded xmlns='http://www.ietf.org'                                    >"
         + "<notIncluded                                                                >"
         + "<notIncluded                              xmlns:w3c='http://www.w3c.org/'   >"
         + "<notIncluded                                                                >"
         + "<notIncluded xmlns='http://www.w3c.org'                                     >"
         + "<notIncluded                                                                >"
         + "<notIncluded                                                                >"
         + "<included                                 xmlns:ietf='http://www.ietf.org/' >"
         + "</included>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</included>"
         + "</included>"
         + "</included>"
         + "</included>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</included>";
      //J+
      String xpath = "(//*[local-name()='included'] | //@* | //namespace::*)";
      boolean invisibleDefault = true;
      boolean invisibleNonDefault = true;
      boolean visibleDefault = false;
      boolean visibleNonDefault = true;

      invisibleAncestorsContainDefaultNS(input, xpath, invisibleDefault,
                                         invisibleNonDefault, visibleDefault,
                                         visibleNonDefault);
   }

   /**
    * Method test6
    *
    * @throws Exception
    */
   public void test6() throws Exception {
      //J-
      String input = ""
         + "<included                                                                   >"
         + "<notIncluded                              xmlns:w3c='http://www.w3c.org/'   >"
         + "<notIncluded                              xmlns:ietf='http://www.ietf.org/' >"
         + "<included    xmlns='http://www.ietf.org'                                    >"
         + "<included                                                                   >"
         + "<included    xmlns='http://www.w3c.org'                                     >"
         + "<included                                                                   >"
         + "<notIncluded xmlns='http://www.ietf.org'                                    >"
         + "<notIncluded                                                                >"
         + "<notIncluded                              xmlns:w3c='http://www.w3c.org/'   >"
         + "<notIncluded                                                                >"
         + "<notIncluded xmlns='http://www.w3c.org'                                     >"
         + "<notIncluded                                                                >"
         + "<notIncluded                                                                >"
         + "<included                                 xmlns:ietf='http://www.ietf.org/' >"
         + "</included>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</included>"
         + "</included>"
         + "</included>"
         + "</included>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</included>";
      //J+
      String xpath = "(//*[local-name()='included'] | //@* | //namespace::*)";
      boolean invisibleDefault = true;
      boolean invisibleNonDefault = true;
      boolean visibleDefault = true;
      boolean visibleNonDefault = false;

      invisibleAncestorsContainDefaultNS(input, xpath, invisibleDefault,
                                         invisibleNonDefault, visibleDefault,
                                         visibleNonDefault);
   }

   /**
    * Method test7
    *
    * @throws Exception
    */
   public void test7() throws Exception {
      //J-
      String input = ""
         + "<root xmlns:w3c='http://www.w3.org'>"
         + "<e1 />"
         + "</root>";
      //J+
      String xpath = "(//*[local-name()='e1'] | //@* | //namespace::*)";
      boolean invisibleDefault = false;
      boolean invisibleNonDefault = true;
      boolean visibleDefault = false;
      boolean visibleNonDefault = false;

      invisibleAncestorsContainDefaultNS(input, xpath, invisibleDefault,
                                         invisibleNonDefault, visibleDefault,
                                         visibleNonDefault);
   }

   /**
    * Method invisibleAncestorsContainDefaultNS
    *
    * @param input
    * @param xpath
    * @param invisibleDefault
    * @param invisibleNonDefault
    * @param visibleDefault
    * @param visibleNonDefault
    * @throws Exception
    */
   private void invisibleAncestorsContainDefaultNS(
           String input, String xpath, boolean invisibleDefault, boolean invisibleNonDefault, boolean visibleDefault, boolean visibleNonDefault)
              throws Exception {

      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

      dfactory.setNamespaceAware(true);

      DocumentBuilder db = dfactory.newDocumentBuilder();
      Document doc = db.parse(new ByteArrayInputStream(input.getBytes()));
      NodeList nodeList = XPathAPI.selectNodeList(doc, xpath);
      Node ctxNode = doc;

      while (true) {
         Node child = ctxNode.getFirstChild();

         if (child != null) {
            ctxNode = child;
         } else {
            break;
         }
      }

      Map selectedNodes = (Map) new HashMap();

      for (int i = 0; i < nodeList.getLength(); i++) {
         selectedNodes.put(nodeList.item(i), Boolean.TRUE);
      }

      NamespaceSearcher nss = new NamespaceSearcher(ctxNode, selectedNodes,
                                 false);

      assertEquals("invisible default", invisibleDefault,
                   nss.invisibleAncestorsContainDefaultNS());
      assertEquals("invisible other", invisibleNonDefault,
                   nss.invisibleAncestorsContainNonDefaultNS());
      assertEquals("visible default", visibleDefault,
                   nss.visibleAncestorsContainDefaultNS());
      assertEquals("visible other", visibleNonDefault,
                   nss.visibleAncestorsContainNonDefaultNS());
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
