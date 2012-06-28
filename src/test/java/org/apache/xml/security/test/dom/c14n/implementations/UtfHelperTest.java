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
package org.apache.xml.security.test.dom.c14n.implementations;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.xml.security.c14n.implementations.UtfHelpper;

public class UtfHelperTest extends org.junit.Assert {
    
    @org.junit.Test
    public void testBug40156() {
        String s = "\u00e4\u00f6\u00fc";
        byte a[] = UtfHelpper.getStringInUtf8(s);
        try {
            byte correct[] = s.getBytes("UTF8");
            boolean equals = Arrays.equals(correct, a);
            assertTrue(equals);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @org.junit.Test
    public void testUtf() throws Exception {
        int chunk = 1 << 16; 
        int j = 0;
        ByteArrayOutputStream charByCharOs = new ByteArrayOutputStream();
        ByteArrayOutputStream strOs = new ByteArrayOutputStream();

        char chs[] = new char[chunk];
        for (int i = 0; i < chunk; i++) {
            int ch = (chunk * j) + i;
            if (ch == 0xDBFF) {
                ch = 1;
            }
            chs[i] = (char)ch;
            UtfHelpper.writeCharToUtf8((char)ch, charByCharOs);
        }
        String str = new String(chs);
        byte a[] = UtfHelpper.getStringInUtf8(str);
        try {
            // System.out.println("chunk:"+j);
            byte correct[] = str.getBytes("UTF8");
            assertTrue("UtfHelper.getStringInUtf8 false", Arrays.equals(correct, a));
            assertTrue(
                "UtfHelper.getStringInUtf8 false", 
                Arrays.equals(correct, charByCharOs.toByteArray())
            );
            UtfHelpper.writeStringToUtf8(str, strOs);
            assertTrue(
                "UtfHelper.writeStringToUtf8 false",
                Arrays.equals(correct, strOs.toByteArray())
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
}
