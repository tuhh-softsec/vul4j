
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
package org.apache.xml.security.test.utils;



import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xml.security.exceptions.Base64DecodingException;


/**
 * Unit test for {@link org.apache.xml.security.utils.Base64}
 *
 * @author Christian Geuer-Pollmann
 */
public class Base64Test extends TestCase {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(Base64Test.class.getName());

   /**
    * Method suite
    *
    *
    */
   public static Test suite() {
      return new TestSuite(Base64Test.class);
   }

   /**
    *
    * @param Name_
    */
   public Base64Test(String Name_) {
      super(Name_);
   }

   /**
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading", Base64Test.class.getName() };

      junit.textui.TestRunner.main(testCaseName);

      // junit.swingui.TestRunner.main(testCaseName);
   }    //public static void main(String[] args)

   /**
    * Method testA1
    *
    * @throws java.io.UnsupportedEncodingException
    * $todo$ Extend more tests
    */
   public static void testA1() throws java.io.UnsupportedEncodingException, Base64DecodingException {

      String textData = "Hallo";
      String result0 =
         org.apache.xml.security.utils.Base64
            .encode(textData.getBytes("UTF-8"));

      assertNotNull("Result of encoding result0", result0);

      byte resultBytes[] = org.apache.xml.security.utils.Base64.decode(result0);
      String resultStr = new String(resultBytes, "UTF-8");

      assertEquals("Result of decoding", 0, textData.compareTo(resultStr));
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
