
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
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.c14n.helper.C14nHelper;


/**
 *
 *
 *
 *
 * @author Christian Geuer-Pollmann
 *
 */
public class C14nHelperTest extends TestCase {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(C14nHelperTest.class.getName());

   /**
    * Method suite
    *
    * @return
    */
   public static Test suite() {
      return new TestSuite(C14nHelperTest.class);
   }

   /**
    * Constructor AttrCompareTest
    *
    * @param Name_
    */
   public C14nHelperTest(String Name_) {
      super(Name_);
   }

   /**
    * Method main
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading", C14nHelperTest.class.getName() };

      junit.textui.TestRunner.main(testCaseName);
   }

   /**
    * Method testNamespaceIsAbsolute01
    *
    */
   public void testNamespaceIsAbsolute01() {

      String namespaceURI = "http://www.w3.org/Signature/";

      assertTrue("URI fails: \"" + namespaceURI + "\"",
                 C14nHelper.namespaceIsAbsolute(namespaceURI));
   }

   /**
    *
    *
    * @see <A HREF="http://lists.w3.org/Archives/Public/w3c-ietf-xmldsig/2001JulSep/0068.html">The list</A>
    */
   public void testNamespaceIsAbsolute02() {

      String namespaceURI = "http://www.w3.org/../blah";

      assertTrue("URI fails: \"" + namespaceURI + "\"",
                 C14nHelper.namespaceIsAbsolute(namespaceURI));
   }

   /**
    * Method testNamespaceIsAbsolute03
    *
    */
   public void testNamespaceIsAbsolute03() {

      // unknown protocol?
      String namespaceURI = "hxxp://www.w3.org/";

      assertTrue("URI fails: \"" + namespaceURI + "\"",
                 C14nHelper.namespaceIsAbsolute(namespaceURI));
   }

   /**
    * Method testNamespaceIsRelative01
    *
    */
   public void testNamespaceIsRelative01() {

      String namespaceURI = "../blah";

      assertTrue("URI fails: \"" + namespaceURI + "\"",
                 C14nHelper.namespaceIsRelative(namespaceURI));
   }

   /**
    * Method testNamespaceIsRelative02
    *
    */
   public void testNamespaceIsRelative02() {

      String namespaceURI = "blah";

      assertTrue("URI fails: \"" + namespaceURI + "\"",
                 C14nHelper.namespaceIsRelative(namespaceURI));
   }

   /**
    * Method testNamespaceIsRelative03
    *
    */
   public void __testNamespaceIsRelative03() {

      String namespaceURI = "http://...";

      assertTrue("URI fails: \"" + namespaceURI + "\"",
                 C14nHelper.namespaceIsRelative(namespaceURI));
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
