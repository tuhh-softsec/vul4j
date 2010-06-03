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
package org.apache.directory.shared.ldap.util;


import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.junit.Test;


/**
 * A test case for the UTFUtils methods 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class UTFUtilsTest
{

    private FileOutputStream fos = null;
    private FileInputStream fis = null;


    /**
     * 
     * Creates a new instance of UTFUtilsTest.
     *
     */
    public UTFUtilsTest()
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
        UTFUtils.writeUTF( dos, testString );
        dos.flush();
        dos.close();
        assertEquals( testString, UTFUtils.readUTF( dis ) );
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
        UTFUtils.writeUTF( dos, testString );
        dos.flush();
        dos.close();
        assertEquals( testString, UTFUtils.readUTF( dis ) );
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
        UTFUtils.writeUTF( dos, testString );
        dos.flush();
        dos.close();
        assertEquals( testString, UTFUtils.readUTF( dis ) );
        dis.close();
    }
}
