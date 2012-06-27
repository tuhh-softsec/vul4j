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
package org.swssf.xmlsec.test;

import org.swssf.xmlsec.impl.util.TrimmerOutputStream;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class TrimmerOutputStreamTest {

    private final String testString = "Within this class we test if the TrimmerOutputStream works correctly under different conditions";

    @Test
    public void testWriteSingleBytes() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TrimmerOutputStream trimmerOutputStream = new TrimmerOutputStream(baos, 32, 3, 4);

        byte[] testStringBytes = ("<a>" + testString + "</a>").getBytes();
        for (int i = 0; i < testStringBytes.length; i++) {
            trimmerOutputStream.write(testStringBytes[i]);
        }
        trimmerOutputStream.close();

        Assert.assertEquals(baos.size(), testStringBytes.length - 7);
        Assert.assertEquals(baos.toString(), testString);
    }

    @Test
    public void testWriteRandomByteSizes() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TrimmerOutputStream trimmerOutputStream = new TrimmerOutputStream(baos, 32, 3, 4);

        StringBuilder stringBuffer = new StringBuilder("<a>");
        for (int i = 0; i < 100; i++) {
            stringBuffer.append(testString);
        }
        stringBuffer.append("</a>");

        byte[] testStringBytes = stringBuffer.toString().getBytes();

        int written = 0;
        int count = 0;
        do {
            count++;
            trimmerOutputStream.write(testStringBytes, written, count);
            written += count;
        }
        while ((written + count + 1) < testStringBytes.length);

        trimmerOutputStream.write(testStringBytes, written, testStringBytes.length - written);
        trimmerOutputStream.close();

        Assert.assertEquals(baos.size(), testStringBytes.length - 7);
        Assert.assertEquals(baos.toString(), stringBuffer.toString().substring(3, stringBuffer.length() - 4));
    }
}
