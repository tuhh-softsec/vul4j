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
package org.apache.xml.security.test;



import junit.framework.*;
import org.apache.xml.security.utils.XMLUtils;


/**
 * All org.apache.xml.security.test JUnit Tests
 *
 * @author Christian Geuer-Pollmann
 */
public class AllTests {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(AllTests.class.getName());

   /**
    * Method suite
    *
    * @return
    */
   public static Test suite() {

      TestSuite suite =
         new TestSuite("All org.apache.xml.security.test JUnit Tests");

      //J-
      suite.addTest(org.apache.xml.security.test.c14n.helper.AttrCompareTest.suite());
      suite.addTest(org.apache.xml.security.test.c14n.helper.C14nHelperTest.suite());
      suite.addTest(org.apache.xml.security.test.c14n.helper.NamespaceSearcherTest.suite());
      suite.addTest(org.apache.xml.security.test.c14n.implementations.Canonicalizer20010315Test.suite());
      suite.addTest(org.apache.xml.security.test.c14n.implementations.Canonicalizer20010315WithoutXPathSupportTest.suite());
      suite.addTest(org.apache.xml.security.test.external.org.apache.xalan.XPathAPI.XalanBug1425Test.suite());
      suite.addTest(org.apache.xml.security.test.external.org.apache.xalan.XPathAPI.AttributeAncestorOrSelf.suite());
      suite.addTest(org.apache.xml.security.test.signature.XMLSignatureInputTest.suite());
      suite.addTest(org.apache.xml.security.test.transforms.implementations.TransformBase64DecodeTest.suite());
      suite.addTest(org.apache.xml.security.test.utils.resolver.ResourceResolverSpiTest.suite());
      suite.addTest(org.apache.xml.security.test.utils.Base64Test.suite());

      // interoperability tests using test vectors from other implementations
      suite.addTest(org.apache.xml.security.test.interop.BaltimoreTest.suite());
      suite.addTest(org.apache.xml.security.test.interop.IAIKTest.suite());
      suite.addTest(org.apache.xml.security.test.interop.IBMTest.suite());
      //J+
      return suite;
   }

   /**
    * Method main
    *
    * @param args
    */
   public static void main(String[] args) {

      String xercesVerStr = "Apache Xerces "
                            + org.apache.xerces.framework.Version.fVersion;
      String xalanVerStr =
         "Apache Xalan  "
         + XMLUtils.getXalanVersion();

      cat.info(xercesVerStr);
      cat.info(xalanVerStr);

      boolean useTextUI = true;

      if (useTextUI) {
         junit.textui.TestRunner.run(suite());
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
      org.apache.xml.security.Init.init();
   }
}
