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



import java.io.File;
import junit.framework.*;
import org.apache.xml.security.utils.XMLUtils;


/**
 * All org.apache.xml.security.test JUnit Tests
 *
 * @author Christian Geuer-Pollmann
 */
public class AllTests extends TestCase {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(AllTests.class.getName());

   public AllTests(String test) {
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

      XMLUtils.spitOutVersions(cat);
      cat.debug("java.class.path            : " + System.getProperty("java.class.path"));
      cat.debug("java.library.path          : " + System.getProperty("java.library.path"));
      cat.debug("java.runtime.name          : " + System.getProperty("java.runtime.name"));
      cat.debug("java.runtime.version       : " + System.getProperty("java.runtime.version"));
      cat.debug("java.specification.name    : " + System.getProperty("java.specification.name"));
      cat.debug("java.specification.vendor  : " + System.getProperty("java.specification.vendor"));
      cat.debug("java.specification.version : " + System.getProperty("java.specification.version"));
      cat.debug("java.vendor                : " + System.getProperty("java.vendor"));
      cat.debug("java.version               : " + System.getProperty("java.version"));
      cat.debug("java.vm.info               : " + System.getProperty("java.vm.info"));
      cat.debug("java.vm.name               : " + System.getProperty("java.vm.name"));
      cat.debug("java.vm.version            : " + System.getProperty("java.vm.version"));
      cat.debug("os.arch                    : " + System.getProperty("os.arch"));
      cat.debug("os.name                    : " + System.getProperty("os.name"));
      cat.debug("os.version                 : " + System.getProperty("os.version"));

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
      org.apache.xml.security.Init.init();
   }
}
