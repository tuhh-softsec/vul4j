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
package org.apache.xml.security.test;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xml.security.utils.XMLUtils;


/**
 * All org.apache.xml.security.test JUnit Tests
 *
 * @author Christian Geuer-Pollmann
 */
public class AllTests extends TestCase {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(
			    AllTests.class.getName());

   public AllTests(String test) {
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
      suite.addTest(org.apache.xml.security.test.ModuleTest.suite());
      suite.addTest(org.apache.xml.security.test.InteropTest.suite());
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
      log.debug("java.class.path            : " + System.getProperty("java.class.path"));
      log.debug("java.library.path          : " + System.getProperty("java.library.path"));
      log.debug("java.runtime.name          : " + System.getProperty("java.runtime.name"));
      log.debug("java.runtime.version       : " + System.getProperty("java.runtime.version"));
      log.debug("java.specification.name    : " + System.getProperty("java.specification.name"));
      log.debug("java.specification.vendor  : " + System.getProperty("java.specification.vendor"));
      log.debug("java.specification.version : " + System.getProperty("java.specification.version"));
      log.debug("java.vendor                : " + System.getProperty("java.vendor"));
      log.debug("java.version               : " + System.getProperty("java.version"));
      log.debug("java.vm.info               : " + System.getProperty("java.vm.info"));
      log.debug("java.vm.name               : " + System.getProperty("java.vm.name"));
      log.debug("java.vm.version            : " + System.getProperty("java.vm.version"));
      log.debug("os.arch                    : " + System.getProperty("os.arch"));
      log.debug("os.name                    : " + System.getProperty("os.name"));
      log.debug("os.version                 : " + System.getProperty("os.version"));

      boolean useTextUI = true;

      if (useTextUI) {
         // int counter = 100;
         // long start = System.currentTimeMillis();
         // for (int i=0; i<counter; i++) {
            junit.textui.TestRunner.run(suite());
         // }
         // long end = System.currentTimeMillis();
         // double delta = end - start;
         // System.out.println(counter + " full tests took " + java.text.DecimalFormat.getInstance().format(delta / 1000.) + " seconds");

      } else {
         String[] testCaseName = { "-noloading", AllTests.class.getName() };

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
    if (System.getProperty("basedir")==null) {
        System.setProperty("basedir",System.getProperty("user.dir"));
    }
      org.apache.xml.security.Init.init();
   }
}
