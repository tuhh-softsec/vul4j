
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



import java.io.ByteArrayOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xml.security.utils.Base64;


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
    * @return
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
    * @throws Exception
    *
    * @throws java.io.UnsupportedEncodingException
    * $todo$ Extend more tests
    */
   public static void testA1() throws Exception {

      String textData = "Hallo";
      String result0 = Base64.encode(textData.getBytes("UTF-8"));

      assertNotNull("Result of encoding result0", result0);

      byte resultBytes[] = Base64.decode(result0);
      String resultStr = new String(resultBytes, "UTF-8");

      assertEquals("Result of decoding", 0, textData.compareTo(resultStr));
      ByteArrayOutputStream os=new ByteArrayOutputStream();
      Base64.decode(result0.getBytes(),os);
      resultStr = new String(os.toByteArray(), "UTF-8");
      assertEquals("Result of decoding", 0, textData.compareTo(resultStr));
   }

   /**
    * Method testWrap1
    *
	* Test for correct line wrapping at end of an exactly 76 char string
	*
    * @throws java.io.UnsupportedEncodingException
    * @throws Exception
    */

	public static void testWrap1() 
		throws java.io.UnsupportedEncodingException,Exception {

		String inputData = "The quick brown fox jumps over the lazy dog and some extr";
		String expectedResult = "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZyBhbmQgc29tZSBleHRy";
		String result = Base64.encode(inputData.getBytes("UTF-8"));
		assertEquals("Result of encoding", result, expectedResult);

		String result2 = new String(Base64.decode(result), "UTF-8");
		assertEquals("Result of encoding", result2, inputData);
        ByteArrayOutputStream os=new ByteArrayOutputStream();
        Base64.decode(expectedResult.getBytes(),os);
          result2 = new String(os.toByteArray(), "UTF-8");
          assertEquals("Result of encoding", result2, inputData);

	}

   /**
    * Method testWrap2
    *
	* Test for correct line wrapping after more than 76 characters
	*
    * @throws java.io.UnsupportedEncodingException
    * @throws Exception
    */

	public static void testWrap2() 
		throws java.io.UnsupportedEncodingException, Exception {

		String inputData = "The quick brown fox jumps over the lazy dog and some extra text that will cause a line wrap";
		String expectedResult = "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZyBhbmQgc29tZSBleHRy\nYSB0ZXh0IHRoYXQgd2lsbCBjYXVzZSBhIGxpbmUgd3JhcA==";
		String result = Base64.encode(inputData.getBytes("UTF-8"));
		assertEquals("Result of encoding", result, expectedResult);

		String result2 = new String(Base64.decode(result), "UTF-8");
		assertEquals("Result of encoding", result2, inputData);
        ByteArrayOutputStream os=new ByteArrayOutputStream();
        Base64.decode(expectedResult.getBytes(),os);
          result2 = new String(os.toByteArray(), "UTF-8");
          assertEquals("Result of encoding", result2, inputData);
	}

   static {
      org.apache.xml.security.Init.init();
   }
}
