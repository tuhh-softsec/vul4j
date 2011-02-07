/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.shared.util;


import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * A test case for the UTFUtils methods 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class UnicodeTest
{

    private FileOutputStream fos = null;
    private FileInputStream fis = null;


    /**
     * 
     * Creates a new instance of UnicodeTest.
     *
     */
    public UnicodeTest()
    {
        try
        {
            File tmpFile = File.createTempFile( "UTFUtils", "test" );
            tmpFile.deleteOnExit();
            fos = new FileOutputStream( tmpFile );
            fis = new FileInputStream( tmpFile );
        }
        catch ( IOException e )
        {
        }
    }


    /**
     * 
     * Test write/read of a null string
     *
     * @throws Exception
     */
    @Test
    public void testNullString() throws Exception
    {
        ObjectOutputStream dos = new ObjectOutputStream( fos );
        ObjectInputStream dis = new ObjectInputStream( fis );
        String testString = null;
        Unicode.writeUTF(dos, testString);
        dos.flush();
        dos.close();
        assertEquals( testString, Unicode.readUTF(dis) );
        dis.close();
    }


    /**
     * 
     * Test write/read of an empty string
     *
     * @throws Exception
     */
    @Test
    public void testEmptyString() throws Exception
    {
        ObjectOutputStream dos = new ObjectOutputStream( fos );
        ObjectInputStream dis = new ObjectInputStream( fis );
        String testString = "";
        Unicode.writeUTF(dos, testString);
        dos.flush();
        dos.close();
        assertEquals( testString, Unicode.readUTF(dis) );
        dis.close();
    }


    /**
     * 
     * Test write/read of a large string (> 64Kb)
     *
     * @throws Exception
     */
    @Test
    public void testLargeString() throws Exception
    {
        ObjectOutputStream dos = new ObjectOutputStream( fos );
        ObjectInputStream dis = new ObjectInputStream( fis );
        char[] fill = new char[196622]; // 65535 * 3 + 17
        Arrays.fill( fill, '\u00fc' ); // German &&uuml
        String testString = new String( fill );
        Unicode.writeUTF(dos, testString);
        dos.flush();
        dos.close();
        assertEquals( testString, Unicode.readUTF(dis) );
        dis.close();
    }


    @Test
    public void testOneByteChar()
    {
        char res = Unicode.bytesToChar(new byte[]
                {0x30});

        assertEquals( '0', res );
    }


    @Test
    public void testOneByteChar00()
    {
        char res = Unicode.bytesToChar(new byte[]
                {0x00});

        assertEquals( 0x00, res );
    }


    @Test
    public void testOneByteChar7F()
    {
        char res = Unicode.bytesToChar(new byte[]
                {0x7F});

        assertEquals( 0x7F, res );
    }


    @Test
    public void testTwoBytesChar()
    {
        char res = Unicode.bytesToChar(new byte[]
                {(byte) 0xCE, (byte) 0x91});

        assertEquals(0x0391, res);
    }


    @Test
    public void testThreeBytesChar()
    {
        char res = Unicode.bytesToChar(new byte[]
                {(byte) 0xE2, (byte) 0x89, (byte) 0xA2});

        assertEquals(0x2262, res);
    }


    @Test
    public void testcharToBytesOne()
    {
        assertEquals( "0x00 ", Strings.dumpBytes(Unicode.charToBytes((char) 0x0000)) );
        assertEquals( "0x61 ", Strings.dumpBytes(Unicode.charToBytes('a')) );
        assertEquals("0x7F ", Strings.dumpBytes(Unicode.charToBytes((char) 0x007F)));
    }


    @Test
    public void testcharToBytesTwo()
    {
        assertEquals( "0xC2 0x80 ", Strings.dumpBytes(Unicode.charToBytes((char) 0x0080)) );
        assertEquals( "0xC3 0xBF ", Strings.dumpBytes(Unicode.charToBytes((char) 0x00FF)) );
        assertEquals("0xC4 0x80 ", Strings.dumpBytes(Unicode.charToBytes((char) 0x0100)));
        assertEquals("0xDF 0xBF ", Strings.dumpBytes(Unicode.charToBytes((char) 0x07FF)));
    }


    @Test
    public void testcharToBytesThree()
    {
        assertEquals( "0xE0 0xA0 0x80 ", Strings.dumpBytes(Unicode.charToBytes((char) 0x0800)) );
        assertEquals( "0xE0 0xBF 0xBF ", Strings.dumpBytes(Unicode.charToBytes((char) 0x0FFF)) );
        assertEquals("0xE1 0x80 0x80 ", Strings.dumpBytes(Unicode.charToBytes((char) 0x1000)));
        assertEquals("0xEF 0xBF 0xBF ", Strings.dumpBytes(Unicode.charToBytes((char) 0xFFFF)));
    }
}
