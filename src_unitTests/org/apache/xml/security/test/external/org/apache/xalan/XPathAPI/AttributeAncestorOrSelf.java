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



import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;


/**
 * This test is to ensure that the owner element of an Attribute is on the
 * ancestor-or-self axis.
 *
 * @author $Author$
 */
public class AttributeAncestorOrSelf extends TestCase {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(AttributeAncestorOrSelf.class.getName());

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
      return new TestSuite(AttributeAncestorOrSelf.class);
   }

   /**
    * Constructor AttributeAncestorOrSelf
    *
    * @param Name_
    */
   public AttributeAncestorOrSelf(String Name_) {
      super(Name_);
   }

   /**
    * Method main
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading",
                                AttributeAncestorOrSelf.class.getName() };

      junit.textui.TestRunner.main(testCaseName);
   }

   /**
    * Process input args and execute the XPath.
    *
    * @param xmlString
    * @param ctxNodeStr
    * @param evalStr
    * @return
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

      assertTrue("Bad " + ctxNodeStr + " " + evalStr + "  " + xalanVerStr,
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

      assertTrue("Bad " + ctxNodeStr + " " + evalStr + "  " + xalanVerStr,
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

      assertTrue("Bad " + ctxNodeStr + " " + evalStr + "  " + xalanVerStr,
                 isAncestorOf(_nodeSetInput1, ctxNodeStr, evalStr));
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
