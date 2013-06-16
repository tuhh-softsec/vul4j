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
package org.apache.xml.security.test.stax;

import org.junit.Assert;
import org.junit.Test;

import org.apache.xml.security.stax.impl.util.IVSplittingOutputStream;
import org.apache.xml.security.stax.impl.util.ReplaceableOuputStream;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class IVSplittingOutputStreamTest extends org.junit.Assert {

    private final String testString = "Within this class we test if the IVSplittingOutputStream works correctly under different conditions";

    @Test
    public void testWriteBytes() throws Exception {

        int ivSize = 16;

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        Cipher cipher = Cipher.getInstance("AES/CBC/ISO10126Padding");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IVSplittingOutputStream ivSplittingOutputStream = new IVSplittingOutputStream(byteArrayOutputStream, cipher, secretKey, ivSize);
        ReplaceableOuputStream replaceableOuputStream = new ReplaceableOuputStream(ivSplittingOutputStream);
        ivSplittingOutputStream.setParentOutputStream(replaceableOuputStream);
        byte[] testBytes = testString.getBytes();
        for (int i = 0; i < testBytes.length; i++) {
            replaceableOuputStream.write(testBytes[i]);
        }
        replaceableOuputStream.close();

        Assert.assertEquals(new String(ivSplittingOutputStream.getIv()), testString.substring(0, ivSize));
        Assert.assertEquals(new String(byteArrayOutputStream.toByteArray()), testString.substring(ivSize));
        Assert.assertEquals(new String(ivSplittingOutputStream.getIv()) + new String(byteArrayOutputStream.toByteArray()), testString);
        Assert.assertTrue(ivSplittingOutputStream.isIVComplete());
    }

    @Test
    public void testWriteBytesArray() throws Exception {

        int ivSize = 16;

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        Cipher cipher = Cipher.getInstance("AES/CBC/ISO10126Padding");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IVSplittingOutputStream ivSplittingOutputStream = new IVSplittingOutputStream(byteArrayOutputStream, cipher, secretKey, ivSize);
        ReplaceableOuputStream replaceableOuputStream = new ReplaceableOuputStream(ivSplittingOutputStream);
        ivSplittingOutputStream.setParentOutputStream(replaceableOuputStream);
        replaceableOuputStream.write(testString.getBytes());
        replaceableOuputStream.close();

        Assert.assertEquals(new String(ivSplittingOutputStream.getIv()), testString.substring(0, ivSize));
        Assert.assertEquals(new String(byteArrayOutputStream.toByteArray()), testString.substring(ivSize));
        Assert.assertEquals(new String(ivSplittingOutputStream.getIv()) + new String(byteArrayOutputStream.toByteArray()), testString);
        Assert.assertTrue(ivSplittingOutputStream.isIVComplete());
    }

    @Test
    public void testWriteBytesArrayIVLength() throws Exception {

        int ivSize = 16;

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        Cipher cipher = Cipher.getInstance("AES/CBC/ISO10126Padding");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IVSplittingOutputStream ivSplittingOutputStream = new IVSplittingOutputStream(byteArrayOutputStream, cipher, secretKey, ivSize);
        ReplaceableOuputStream replaceableOuputStream = new ReplaceableOuputStream(ivSplittingOutputStream);
        ivSplittingOutputStream.setParentOutputStream(replaceableOuputStream);

        byte[] testBytes = testString.getBytes();
        for (int i = 0; i < testBytes.length - ivSize; i += ivSize) {
            replaceableOuputStream.write(testBytes, i, ivSize);
        }
        //write last part
        replaceableOuputStream.write(testBytes, testBytes.length - testBytes.length % ivSize, testBytes.length % ivSize);
        replaceableOuputStream.close();

        Assert.assertEquals(new String(ivSplittingOutputStream.getIv()), testString.substring(0, ivSize));
        Assert.assertEquals(new String(byteArrayOutputStream.toByteArray()), testString.substring(ivSize));
        Assert.assertEquals(new String(ivSplittingOutputStream.getIv()) + new String(byteArrayOutputStream.toByteArray()), testString);
        Assert.assertTrue(ivSplittingOutputStream.isIVComplete());
    }

    @Test
    public void testWriteBytesArrayIVLength2() throws Exception {

        int ivSize = 16;

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        Cipher cipher = Cipher.getInstance("AES/CBC/ISO10126Padding");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IVSplittingOutputStream ivSplittingOutputStream = new IVSplittingOutputStream(byteArrayOutputStream, cipher, secretKey, ivSize);
        ReplaceableOuputStream replaceableOuputStream = new ReplaceableOuputStream(ivSplittingOutputStream);
        ivSplittingOutputStream.setParentOutputStream(replaceableOuputStream);

        byte[] testBytes = testString.getBytes();
        replaceableOuputStream.write(testBytes, 0, testBytes.length - ivSize);
        //write last part
        replaceableOuputStream.write(testBytes, testBytes.length - ivSize, ivSize);
        replaceableOuputStream.close();

        Assert.assertEquals(new String(ivSplittingOutputStream.getIv()), testString.substring(0, ivSize));
        Assert.assertEquals(new String(byteArrayOutputStream.toByteArray()), testString.substring(ivSize));
        Assert.assertEquals(new String(ivSplittingOutputStream.getIv()) + new String(byteArrayOutputStream.toByteArray()), testString);
        Assert.assertTrue(ivSplittingOutputStream.isIVComplete());
    }

    @Test
    public void testWriteBytesArrayWithOffset() throws Exception {

        int ivSize = 16;

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        Cipher cipher = Cipher.getInstance("AES/CBC/ISO10126Padding");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IVSplittingOutputStream ivSplittingOutputStream = new IVSplittingOutputStream(byteArrayOutputStream, cipher, secretKey, ivSize);
        ReplaceableOuputStream replaceableOuputStream = new ReplaceableOuputStream(ivSplittingOutputStream);
        ivSplittingOutputStream.setParentOutputStream(replaceableOuputStream);

        byte[] testBytes = testString.getBytes();
        for (int i = 0; i < testBytes.length - 4; i += 4) {
            replaceableOuputStream.write(testBytes, i, 4);
        }
        //write last part
        replaceableOuputStream.write(testBytes, testBytes.length - testBytes.length % 4, testBytes.length % 4);
        replaceableOuputStream.close();

        Assert.assertEquals(new String(ivSplittingOutputStream.getIv()), testString.substring(0, ivSize));
        Assert.assertEquals(new String(byteArrayOutputStream.toByteArray()), testString.substring(ivSize));
        Assert.assertEquals(new String(ivSplittingOutputStream.getIv()) + new String(byteArrayOutputStream.toByteArray()), testString);
        Assert.assertTrue(ivSplittingOutputStream.isIVComplete());
    }
}
