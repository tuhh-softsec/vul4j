/*
 * Copyright 2003-2009 The Apache Software Foundation.
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
package org.apache.xml.security.test;


import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author $Author$
 */

public class InteropTest extends TestCase {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(InteropTest.class.getName());

   public InteropTest(String test) {
      super(test);
   }

   /**
    * Method suite
    *
    *
    */
   public static Test suite() {

      TestSuite suite =
         new TestSuite("All interoperability tests");

      //J-
      // interoperability tests using test vectors from other implementations
      suite.addTest(org.apache.xml.security.test.interop.BaltimoreTest.suite());
      suite.addTest(org.apache.xml.security.test.interop.IAIKTest.suite());
      suite.addTest(org.apache.xml.security.test.interop.RSASecurityTest.suite());
      suite.addTest(org.apache.xml.security.test.c14n.implementations.ExclusiveC14NInterop.suite());

      {
         /*
          * To make interop against the IBM xss4j examples, download the
          * XSS4j from http://www.alphaworks.ibm.com/tech/xmlsecuritysuite
          * and extract the test signatures from
          * xss4j-20030127.zip#/xss4j/data/dsig
          * in the directory
          * data/com/ibm/xss4j-20030127/
          * then the interop test is performed against these values, too.
          */
         String filename = "data/com/ibm/xss4j-20011029/enveloped-rsa.sig";
         File f = new File(filename);
         if (f.exists()) {
            suite.addTest(org.apache.xml.security.test.interop.IBMTest.suite());
         }
      }

      //J+
      return suite;
   }

   /**
    * Method main
    *
    * @param args
    */
   public static void main(String[] args) {

      //XMLUtils.spitOutVersions(log);

      boolean useTextUI = true;

      if (useTextUI) {
         junit.textui.TestRunner.run(suite());
      } else {

         try {
            String lookAndFeelClass =
               "com.incors.plaf.kunststoff.KunststoffLookAndFeel";
            javax.swing.LookAndFeel lnf =
               (javax.swing.LookAndFeel) Class.forName(lookAndFeelClass)
                  .newInstance();

            javax.swing.UIManager.setLookAndFeel(lnf);
         } catch (Exception ex) {}

         //junit.swingui.TestRunner.main(testCaseName);
         junit.textui.TestRunner.run(suite());
      }
   }

   static {
      org.apache.xml.security.Init.init();
   }
}