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


public class UTFUtils
{

    /**
     * In UTF every char may have 1-3 bytes size
     * Max writeUTF bytes / max char symbol size
     */
    public static final int SPLIT_SIZE = 65535 / 3;


    /**
     * 
     * Do writeUTF a string regardless of it's length. null value will be written with length=0 and value 'null' to
     * recognize when reading if it's an empty string or a null string
     *
     * @param objectOutput The objectOutput to write to
     * @param str The value to write
     * @throws IOException If the value can't be written to the file
     */
    public static void writeUTF( ObjectOutput objectOutput, String str ) throws IOException
    {

        if ( str == null )
        {
            objectOutput.writeInt( 0 );
            objectOutput.writeUTF( "null" );
        }
        else
        {
            int strLength = str.length();
            int iterations = strLength / SPLIT_SIZE;

            // Length of string
            objectOutput.writeInt( strLength );

            if ( iterations == 0 )
            {
                // String too short, no iterations needed, just write it
                objectOutput.writeUTF( str );
            }
            else
            {
                if ( strLength % SPLIT_SIZE > 0 )
                {
                    ++iterations;
                }

                for ( int i = 0; i < iterations; ++i )
                {
                    int beginIndex = i * SPLIT_SIZE;
                    int lastIndex = beginIndex + SPLIT_SIZE;

                    if ( lastIndex > strLength )
                    {
                        objectOutput.writeUTF( str.substring( beginIndex, strLength ) );
                    }
                    else
                    {
                        objectOutput.writeUTF( str.substring( beginIndex, lastIndex ) );
                    }
                }
            }
        }
    }


    /**
     * 
     * Do readUTF a string regardless of it's length. null value is written with length=0 and value 'null' to
     * recognize when reading if it's an empty string or a null string
     *
     * @param objectInput The objectInput to read from
     * @return The value
     * @throws IOException If the vale can't be read
     */
    public static String readUTF( ObjectInput objectInput ) throws IOException
    {
        StringBuffer stringBuffer = null;
        int strLength = objectInput.readInt();

        if ( strLength == 0 )
        {
            stringBuffer = new StringBuffer( objectInput.readUTF() );
            if ( stringBuffer.toString().equals( "null" ) )
            {
                return null;
            }
        }
        else
        {
            int iterations = strLength / SPLIT_SIZE;

            if ( strLength % SPLIT_SIZE > 0 )
            {
                ++iterations;
            }

            stringBuffer = new StringBuffer( strLength );
            for ( int i = 0; i < iterations; ++i )
            {
                stringBuffer.append( objectInput.readUTF() );
            }
        }
        return stringBuffer.toString();
    }

}
