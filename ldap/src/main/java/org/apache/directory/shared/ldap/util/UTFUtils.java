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


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


/**
 * The UTFUtils class provides the 2 methods to readUTF and writeUTF a string. See also
 * {@link java.io.DataOutput#writeUTF(String)} and {@link java.io.DataInput#readUTF()}. This util class
 * enhances following given restriction of the interface:<br/>
 * <ul>
 * <li>Write and read a null value throws exception (must be possible)</li>
 * <li>Writing strings large then 65535 encoded bytes throws exception (must be possible)</li>
 * </ul>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class UTFUtils
{

    /**
     * 
     * Writes four bytes of length information to the output stream, followed by the modified UTF-8 representation
     * of every character in the string str. If str is null, the string value 'null' is written with a length of 0
     * instead of throwing an NullPointerException. Each character in the string s  is converted to a group of one,
     * two, or three bytes, depending on the value of the character.
     * 
     * Due to given restrictions (total number of written bytes in a row can't exceed 65535) the total length is
     * written in the length information (four bytes (writeInt)) and the string is split into smaller parts
     * if necessary and written. As each character may be converted to a group of maximum 3 bytes and 65535 bytes
     * can be written at maximum we're on the save side when writing a chunk of only 21845 (65535/3) characters at
     * once.
     * 
     * See also {@link java.io.DataOutput#writeUTF(String)}.
     *
     * @param objectOutput The objectOutput to write to
     * @param str The value to write
     * @throws IOException If the value can't be written to the file
     */
    public static void writeUTF( ObjectOutput objectOutput, String str ) throws IOException
    {
        // Write a 'null' string
        if ( str == null )
        {
            objectOutput.writeInt( 0 );
            objectOutput.writeUTF( "null" );
        }
        else
        {
            // Write length of string
            objectOutput.writeInt( str.length() );

            StringBuffer strBuf = new StringBuffer( str );

            // Write the string in portions not larger than 21845 characters
            while ( strBuf != null )
            {
                if ( strBuf.length() < 21845 )
                {
                    objectOutput.writeUTF( strBuf.substring( 0, strBuf.length() ) );
                    strBuf = null;
                }
                else
                {
                    objectOutput.writeUTF( strBuf.substring( 0, 21845 ) );
                    strBuf.delete( 0, 21845 );
                }
            }
        }
    }


    /**
     * 
     * Reads in a string that has been encoded using a modified UTF-8  format. The general contract of readUTF  is 
     * that it reads a representation of a Unicode character string encoded in modified UTF-8 format; this string of
     * characters is then returned as a String.
     * 
     * First, four bytes are read (readInt) and used to construct an unsigned 16-bit integer in exactly the manner
     * of the readUnsignedShort  method . This integer value is called the UTF length and specifies the number of
     * additional bytes to be read. These bytes are then converted to characters by considering them in groups. The
     * length of each group is computed from the value of the first byte of the group. The byte following a group, if
     * any, is the first byte of the next group.
     *
     *See also {@link java.io.DataInput#readUTF()}.
     *
     * @param objectInput The objectInput to read from
     * @return The read string
     * @throws IOException If the value can't be read
     */
    public static String readUTF( ObjectInput objectInput ) throws IOException
    {
        StringBuffer strBuf = null;

        // Read length of the string
        int strLength = objectInput.readInt();

        // Start reading the string
        strBuf = new StringBuffer( objectInput.readUTF() );

        if ( strLength == 0 && strBuf.toString().equals( "null" ) )
        {
            // The special case of a 'null' string
            return null;
        }
        else
        {
            while ( strLength > strBuf.length() )
            {
                strBuf.append( objectInput.readUTF() );
            }
            return strBuf.toString();
        }
    }

}
