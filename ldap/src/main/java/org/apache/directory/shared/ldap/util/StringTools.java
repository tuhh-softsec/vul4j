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


import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.entry.BinaryValue;
import org.apache.directory.shared.ldap.entry.StringValue;
import org.apache.directory.shared.ldap.schema.syntaxCheckers.UuidSyntaxChecker;

import org.apache.directory.shared.util.Hex;
import org.apache.directory.shared.util.Strings;


/**
 * Various string manipulation methods that are more efficient then chaining
 * string operations: all is done in the same buffer without creating a bunch of
 * string objects.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class StringTools
{
    /**
     * Private constructor
     */
    private StringTools()
    {
    }


    // ~ Static fields/initializers
    // -----------------------------------------------------------------


    // The following methods are taken from org.apache.commons.lang.StringUtils

    /**
     * Creates a regular expression from an LDAP substring assertion filter
     * specification.
     * 
     * @param initialPattern
     *            the initial fragment before wildcards
     * @param anyPattern
     *            fragments surrounded by wildcards if any
     * @param finalPattern
     *            the final fragment after last wildcard if any
     * @return the regular expression for the substring match filter
     * @throws PatternSyntaxException
     *             if a syntactically correct regular expression cannot be
     *             compiled
     */
    public static Pattern getRegex( String initialPattern, String[] anyPattern, String finalPattern )
        throws PatternSyntaxException
    {
        StringBuffer buf = new StringBuffer();

        if ( initialPattern != null )
        {
            buf.append( '^' ).append( Pattern.quote( initialPattern ) );
        }

        if ( anyPattern != null )
        {
            for ( int i = 0; i < anyPattern.length; i++ )
            {
                buf.append( ".*" ).append( Pattern.quote( anyPattern[i] ) );
            }
        }

        if ( finalPattern != null )
        {
            buf.append( ".*" ).append( Pattern.quote( finalPattern ) );
        }
        else
        {
            buf.append( ".*" );
        }

        return Pattern.compile( buf.toString() );
    }


    /**
     * Generates a regular expression from an LDAP substring match expression by
     * parsing out the supplied string argument.
     * 
     * @param ldapRegex
     *            the substring match expression
     * @return the regular expression for the substring match filter
     * @throws PatternSyntaxException
     *             if a syntactically correct regular expression cannot be
     *             compiled
     */
    public static Pattern getRegex( String ldapRegex ) throws PatternSyntaxException
    {
        if ( ldapRegex == null )
        {
            throw new PatternSyntaxException( I18n.err( I18n.ERR_04429 ), "null", -1 );
        }

        List<String> any = new ArrayList<String>();
        String remaining = ldapRegex;
        int index = remaining.indexOf( '*' );

        if ( index == -1 )
        {
            throw new PatternSyntaxException( I18n.err( I18n.ERR_04430 ), remaining, -1 );
        }

        String initialPattern = null;

        if ( remaining.charAt( 0 ) != '*' )
        {
            initialPattern = remaining.substring( 0, index );
        }

        remaining = remaining.substring( index + 1, remaining.length() );

        while ( ( index = remaining.indexOf( '*' ) ) != -1 )
        {
            any.add( remaining.substring( 0, index ) );
            remaining = remaining.substring( index + 1, remaining.length() );
        }

        String finalPattern = null;
        if ( !remaining.endsWith( "*" ) && remaining.length() > 0 )
        {
            finalPattern = remaining;
        }

        if ( any.size() > 0 )
        {
            String[] anyStrs = new String[any.size()];

            for ( int i = 0; i < anyStrs.length; i++ )
            {
                anyStrs[i] = any.get( i );
            }

            return getRegex( initialPattern, anyStrs, finalPattern );
        }

        return getRegex( initialPattern, null, finalPattern );
    }


    /**
     * Splits apart a OS separator delimited set of paths in a string into
     * multiple Strings. File component path strings are returned within a List
     * in the order they are found in the composite path string. Optionally, a
     * file filter can be used to filter out path strings to control the
     * components returned. If the filter is null all path components are
     * returned.
     * 
     * @param paths
     *            a set of paths delimited using the OS path separator
     * @param filter
     *            a FileFilter used to filter the return set
     * @return the filter accepted path component Strings in the order
     *         encountered
     */
    @SuppressWarnings("PMD.CollapsibleIfStatements") // Used because of comments
    public static List<String> getPaths( String paths, FileFilter filter )
    {
        int start = 0;
        int stop = -1;
        String path = null;
        List<String> list = new ArrayList<String>();

        // Abandon with no values if paths string is null
        if ( paths == null || paths.trim().equals( "" ) )
        {
            return list;
        }

        final int max = paths.length() - 1;

        // Loop spliting string using OS path separator: terminate
        // when the start index is at the end of the paths string.
        while ( start < max )
        {
            stop = paths.indexOf( File.pathSeparatorChar, start );

            // The is no file sep between the start and the end of the string
            if ( stop == -1 )
            {
                // If we have a trailing path remaining without ending separator
                if ( start < max )
                {
                    // Last path is everything from start to the string's end
                    path = paths.substring( start );

                    // Protect against consecutive separators side by side
                    if ( !path.trim().equals( "" ) )
                    {
                        // If filter is null add path, if it is not null add the
                        // path only if the filter accepts the path component.
                        if ( filter == null || filter.accept( new File( path ) ) )
                        {
                            list.add( path );
                        }
                    }
                }

                break; // Exit loop no more path components left!
            }

            // There is a separator between start and the end if we got here!
            // start index is now at 0 or the index of last separator + 1
            // stop index is now at next separator in front of start index
            path = paths.substring( start, stop );

            // Protect against consecutive separators side by side
            if ( !path.trim().equals( "" ) )
            {
                // If filter is null add path, if it is not null add the path
                // only if the filter accepts the path component.
                if ( filter == null || filter.accept( new File( path ) ) )
                {
                    list.add( path );
                }
            }

            // Advance start index past separator to start of next path comp
            start = stop + 1;
        }

        return list;
    }


    // ~ Methods
    // ------------------------------------------------------------------------------------

    /**
     * 
     * Helper method to render an object which can be a String or a byte[]
     *
     * @return A string representing the object
     */
    public static String dumpObject( Object object )
    {
        if ( object != null )
        {
            if ( object instanceof String )
            {
                return (String) object;
            }
            else if ( object instanceof byte[] )
            {
                return Strings.dumpBytes((byte[]) object);
            }
            else if ( object instanceof StringValue )
            {
                return ( ( StringValue ) object ).get();
            }
            else if ( object instanceof BinaryValue )
            {
                return Strings.dumpBytes(((BinaryValue) object).get());
            }
            else
            {
                return "<unknown type>";
            }
        }
        else
        {
            return "";
        }
    }


    // Empty checks
    // -----------------------------------------------------------------------


    // Case conversion
    // -----------------------------------------------------------------------


    // Equals
    // -----------------------------------------------------------------------


    /**
     * Build an AttributeType froma byte array. An AttributeType contains
     * only chars within [0-9][a-z][A-Z][-.].
     *  
     * @param bytes The bytes containing the AttributeType
     * @return The AttributeType as a String
     */
    public static String getType( byte[] bytes)
    {
        if ( bytes == null )
        {
            return null;
        }
        
        char[] chars = new char[bytes.length];
        int pos = 0;
        
        for ( byte b:bytes )
        {
            chars[pos++] = (char)b;
        }
        
        return new String( chars );
    }


    /**
     * converts the bytes of a UUID to string
     *  
     * @param bytes bytes of a UUID
     * @return UUID in string format
     */
    public static String uuidToString( byte[] bytes )
    {
        if ( bytes == null || bytes.length != 16 )
        {
            return "Invalid UUID";
        }

        char[] hex = Hex.encodeHex(bytes);
        StringBuffer sb = new StringBuffer();
        sb.append( hex, 0, 8 );
        sb.append( '-' );
        sb.append( hex, 8, 4 );
        sb.append( '-' );
        sb.append( hex, 12, 4 );
        sb.append( '-' );
        sb.append( hex, 16, 4 );
        sb.append( '-' );
        sb.append( hex, 20, 12 );

        return sb.toString().toLowerCase();
    }


    /**
     * converts the string representation of an UUID to bytes
     *  
     * @param string the string representation of an UUID
     * @return the bytes, null if the the syntax is not valid
     */
    public static byte[] uuidToBytes( String string )
    {
        if ( !new UuidSyntaxChecker().isValidSyntax( string ) )
        {
            return null;
        }

        char[] chars = string.toCharArray();
        byte[] bytes = new byte[16];
        bytes[0] = Hex.getHexValue(chars[0], chars[1]);
        bytes[1] = Hex.getHexValue(chars[2], chars[3]);
        bytes[2] = Hex.getHexValue(chars[4], chars[5]);
        bytes[3] = Hex.getHexValue(chars[6], chars[7]);

        bytes[4] = Hex.getHexValue(chars[9], chars[10]);
        bytes[5] = Hex.getHexValue(chars[11], chars[12]);

        bytes[6] = Hex.getHexValue(chars[14], chars[15]);
        bytes[7] = Hex.getHexValue(chars[16], chars[17]);

        bytes[8] = Hex.getHexValue(chars[19], chars[20]);
        bytes[9] = Hex.getHexValue(chars[21], chars[22]);

        bytes[10] = Hex.getHexValue(chars[24], chars[25]);
        bytes[11] = Hex.getHexValue(chars[26], chars[27]);
        bytes[12] = Hex.getHexValue(chars[28], chars[29]);
        bytes[13] = Hex.getHexValue(chars[30], chars[31]);
        bytes[14] = Hex.getHexValue(chars[32], chars[33]);
        bytes[15] = Hex.getHexValue(chars[34], chars[35]);

        return bytes;
    }

}
