/*
 * Copyright  2004 The Apache Software Foundation.
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xml.security.utils.XMLUtils;

public class ModuleTest extends TestCase {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(ModuleTest.class.getName());

   public ModuleTest(String test) {
      super(test);
   }

   /**
    * Method suite
    *
    *
    */

   public static Test suite() {

      TestSuite suite =
         new TestSuite("All org.apache.xml.security.test JUnit Tests");

      //J-
      suite.addTest(org.apache.xml.security.test.c14n.helper.C14nHelperTest.suite());
      suite.addTest(org.apache.xml.security.test.c14n.implementations.Canonicalizer20010315Test.suite());
      suite.addTest(org.apache.xml.security.test.c14n.implementations.Canonicalizer20010315ExclusiveTest.suite());
      suite.addTest(org.apache.xml.security.test.external.org.apache.xalan.XPathAPI.XalanBug1425Test.suite());
      suite.addTest(org.apache.xml.security.test.external.org.apache.xalan.XPathAPI.AttributeAncestorOrSelfTest.suite());
      suite.addTest(org.apache.xml.security.test.signature.XMLSignatureInputTest.suite());
      suite.addTest(org.apache.xml.security.test.transforms.implementations.TransformBase64DecodeTest.suite());
      suite.addTest(org.apache.xml.security.test.utils.resolver.ResourceResolverSpiTest.suite());
      suite.addTest(org.apache.xml.security.test.utils.Base64Test.suite());
      // suite.addTest(org.apache.xml.security.test.algorithms.implementations.KeyWrapTest.suite());
      // suite.addTest(org.apache.xml.security.test.algorithms.implementations.BlockEncryptionTest.suite());
      //J+

      return suite;
   }

   /**
    * Method main
    *
    * @param args
    */
   public static void main(String[] args) {

      XMLUtils.spitOutVersions(log);

      boolean useTextUI = true;

      if (useTextUI) {
         junit.textui.TestRunner.run(suite());
      } else {
         String[] testCaseName = { "-noloading", ModuleTest.class.getName() };

         try {
            String lookAndFeelClass =
               "com.incors.plaf.kunststoff.KunststoffLookAndFeel";
            javax.swing.LookAndFeel lnf =
               (javax.swing.LookAndFeel) Class.forName(lookAndFeelClass)
                  .newInstance();

            javax.swing.UIManager.setLookAndFeel(lnf);
         } catch (Exception ex) {}

         junit.swingui.TestRunner.main(testCaseName);
      }
   }

   static {
      org.apache.xml.security.Init.init();
   }

}
