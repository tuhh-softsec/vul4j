package org.apache.xml.security.test;

import java.io.File;
import junit.framework.*;
import org.apache.xml.security.utils.XMLUtils;


/**
 *
 * @author $Author$
 */

public class InteropTest extends TestCase {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(InteropTest.class.getName());

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
          * xss4j-20011029.zip#/xss4j/data/dsig
          * in the directory
          * data/com/ibm/xss4j-20011029/
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

      XMLUtils.spitOutVersions(cat);

      boolean useTextUI = true;

      if (useTextUI) {
         junit.textui.TestRunner.run(suite());
      } else {
         String[] testCaseName = { "-noloading", InteropTest.class.getName() };

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