
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



import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(C14nHelperTest.class.getName());

   /**
    * Method suite
    *
    *
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
