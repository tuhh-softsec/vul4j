package org.apache.xml.security.test;

import java.io.File;
import junit.framework.*;
import org.apache.xml.security.utils.XMLUtils;

public class ModuleTest extends TestCase {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(ModuleTest.class.getName());

   public ModuleTest(String test) {
      super(test);
   }

   /**
    * Method suite
    *
    * @return
    */
   public static Test suite() {

      TestSuite suite =
         new TestSuite("All org.apache.xml.security.test JUnit Tests");

      //J-
      suite.addTest(org.apache.xml.security.test.c14n.helper.C14nHelperTest.suite());
      suite.addTest(org.apache.xml.security.test.c14n.implementations.Canonicalizer20010315Test.suite());
      suite.addTest(org.apache.xml.security.test.c14n.implementations.Canonicalizer20010315ExclusiveTest.suite());
      suite.addTest(org.apache.xml.security.test.external.org.apache.xalan.XPathAPI.XalanBug1425Test.suite());
      suite.addTest(org.apache.xml.security.test.external.org.apache.xalan.XPathAPI.AttributeAncestorOrSelf.suite());
      suite.addTest(org.apache.xml.security.test.signature.XMLSignatureInputTest.suite());
      suite.addTest(org.apache.xml.security.test.transforms.implementations.TransformBase64DecodeTest.suite());
      suite.addTest(org.apache.xml.security.test.utils.resolver.ResourceResolverSpiTest.suite());
      suite.addTest(org.apache.xml.security.test.utils.Base64Test.suite());
      suite.addTest(org.apache.xml.security.test.algorithms.implementations.KeyWrapTest.suite());
      suite.addTest(org.apache.xml.security.test.algorithms.implementations.BlockEncryptionTest.suite());
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