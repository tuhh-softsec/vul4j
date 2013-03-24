/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.xml.security.test.dom.utils;

import java.io.ByteArrayOutputStream;

import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.utils.XMLUtils;


/**
 * Unit test for {@link org.apache.xml.security.utils.Base64}
 *
 * @author Christian Geuer-Pollmann
 */
public class Base64Test extends org.junit.Assert {

    static org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(Base64Test.class);
    
    static {
        org.apache.xml.security.Init.init();
    }

    @org.junit.Test
    public void testA1() throws Exception {
        String textData = "Hallo";
        String result0 = Base64.encode(textData.getBytes("UTF-8"));
        assertNotNull("Result of encoding result0", result0);

        byte resultBytes[] = Base64.decode(result0);
        String resultStr = new String(resultBytes, "UTF-8");

        assertEquals("Result of decoding", 0, textData.compareTo(resultStr));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Base64.decode(result0.getBytes(),os);
        resultStr = new String(os.toByteArray(), "UTF-8");
        assertEquals("Result of decoding", 0, textData.compareTo(resultStr));
    }

    @org.junit.Test
    public void testWrap1() throws java.io.UnsupportedEncodingException,Exception {
        String inputData = "The quick brown fox jumps over the lazy dog and some extr";
        String expectedResult = 
            "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZyBhbmQgc29tZSBleHRy";
        String result = Base64.encode(inputData.getBytes("UTF-8"));
        assertEquals("Result of encoding", result, expectedResult);

        String result2 = new String(Base64.decode(result), "UTF-8");
        assertEquals("Result of encoding", result2, inputData);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Base64.decode(expectedResult.getBytes(),os);
        result2 = new String(os.toByteArray(), "UTF-8");
        assertEquals("Result of encoding", result2, inputData);
    }

    @org.junit.Test
    public void testWrap2() throws java.io.UnsupportedEncodingException, Exception {

        String inputData = 
            "The quick brown fox jumps over the lazy dog and some extra text that will cause a line wrap";
        String expectedResult = null;
        if (XMLUtils.ignoreLineBreaks()) {
            expectedResult = 
                "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZyBhbmQgc29tZSBleHRyYSB0ZXh0IHRoYXQgd2lsbCBjYXVzZSBhIGxpbmUgd3JhcA==";
        } else {
            expectedResult = 
                "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZyBhbmQgc29tZSBleHRy\nYSB0ZXh0IHRoYXQgd2lsbCBjYXVzZSBhIGxpbmUgd3JhcA==";
        }
        String result = Base64.encode(inputData.getBytes("UTF-8"));
        assertEquals("Result of encoding", result, expectedResult);

        String result2 = new String(Base64.decode(result), "UTF-8");
        assertEquals("Result of encoding", result2, inputData);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Base64.decode(expectedResult.getBytes(),os);
        result2 = new String(os.toByteArray(), "UTF-8");
        assertEquals("Result of encoding", result2, inputData);
    }

}
