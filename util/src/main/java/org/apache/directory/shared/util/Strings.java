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


/**
 * Various string manipulation methods that are more efficient then chaining
 * string operations: all is done in the same buffer without creating a bunch of
 * string objects.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class Strings
{
    /** The default charset, because it's not provided by JDK 1.5 */
    static String defaultCharset = null;
    

    /** Hex chars */
    private static final byte[] HEX_CHAR = new byte[]
        { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    /**
     * Private constructor
     */
    private Strings()
    {
    }

    /**
     * Helper function that dump an array of bytes in hex form
     *
     * @param buffer The bytes array to dump
     * @return A string representation of the array of bytes
     */
    public static String dumpBytes( byte[] buffer )
    {
        if ( buffer == null )
        {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        for ( int i = 0; i < buffer.length; i++ )
        {
            sb.append( "0x" ).append( ( char ) ( HEX_CHAR[( buffer[i] & 0x00F0 ) >> 4] ) ).append(
                ( char ) ( HEX_CHAR[buffer[i] & 0x000F] ) ).append( " " );
        }

        return sb.toString();
    }
    

    /**
     * Helper function that dump a byte in hex form
     * 
     * @param octet The byte to dump
     * @return A string representation of the byte
     */
    public static String dumpByte( byte octet )
    {
        return new String( new byte[]
            { '0', 'x', HEX_CHAR[( octet & 0x00F0 ) >> 4], HEX_CHAR[octet & 0x000F] } );
    }


    /**
     * Helper function that returns a char from an hex
     * 
     * @param hex The hex to dump
     * @return A char representation of the hex
     */
    public static char dumpHex( byte hex )
    {
        return ( char ) HEX_CHAR[hex & 0x000F];
    }


    /**
     * Helper function that dump an array of bytes in hex pair form, 
     * without '0x' and space chars
     * 
     * @param buffer The bytes array to dump
     * @return A string representation of the array of bytes
     */
    public static String dumpHexPairs( byte[] buffer )
    {
        if ( buffer == null )
        {
            return "";
        }
    
        char[] str = new char[buffer.length << 1];
    
        for ( int i = 0, pos = 0; i < buffer.length; i++ )
        {
            str[pos++] = ( char ) ( HEX_CHAR[( buffer[i] & 0x00F0 ) >> 4] );
            str[pos++] = ( char ) ( HEX_CHAR[buffer[i] & 0x000F] );
        }
    
        return new String( str );
    }


    /**
     * Put common code to deepTrim(String) and deepTrimToLower here.
     * 
     * @param str the string to deep trim
     * @param toLowerCase how to normalize for case: upper or lower
     * @return the deep trimmed string
     * @see StringTools#deepTrim( String )
     * 
     * TODO Replace the toCharArray() by substring manipulations
     */
    public static String deepTrim( String str, boolean toLowerCase )
    {
        if ( ( null == str ) || ( str.length() == 0 ) )
        {
            return "";
        }
    
        char ch;
        char[] buf = str.toCharArray();
        char[] newbuf = new char[buf.length];
        boolean wsSeen = false;
        boolean isStart = true;
        int pos = 0;
    
        for ( int i = 0; i < str.length(); i++ )
        {
            ch = buf[i];
    
            // filter out all uppercase characters
            if ( toLowerCase && Character.isUpperCase( ch ) )
            {
                ch = Character.toLowerCase( ch );
            }
    
            // Check to see if we should add space
            if ( Character.isWhitespace( ch ) )
            {
                // If the buffer has had characters added already check last
                // added character. Only append a spc if last character was
                // not whitespace.
                if ( wsSeen )
                {
                    continue;
                }
                else
                {
                    wsSeen = true;
    
                    if ( isStart )
                    {
                        isStart = false;
                    }
                    else
                    {
                        newbuf[pos++] = ch;
                    }
                }
            }
            else
            {
                // Add all non-whitespace
                wsSeen = false;
                isStart = false;
                newbuf[pos++] = ch;
            }
        }
    
        return ( pos == 0 ? "" : new String( newbuf, 0, ( wsSeen ? pos - 1 : pos ) ) );
    }


    /**
     * This does the same thing as a trim but we also lowercase the string while
     * performing the deep trim within the same buffer. This saves us from
     * having to create multiple String and StringBuffer objects and is much
     * more efficient.
     * 
     * @see StringTools#deepTrim( String )
     */
    public static String deepTrimToLower( String string )
    {
        return deepTrim( string, true );
    }


    /**
     * A deep trim of a string remove whitespace from the ends as well as
     * excessive whitespace within the inside of the string between
     * non-whitespace characters. A deep trim reduces internal whitespace down
     * to a single space to perserve the whitespace separated tokenization order
     * of the String.
     * 
     * @param string the string to deep trim.
     * @return the trimmed string.
     */
    public static String deepTrim( String string )
    {
        return deepTrim( string, false );
    }


    /**
     * Trims several consecutive characters into one.
     * 
     * @param str the string to trim consecutive characters of
     * @param ch the character to trim down
     * @return the newly trimmed down string
     */
    public static String trimConsecutiveToOne( String str, char ch )
    {
        if ( ( null == str ) || ( str.length() == 0 ) )
        {
            return "";
        }
    
        char[] buffer = str.toCharArray();
        char[] newbuf = new char[buffer.length];
        int pos = 0;
        boolean same = false;
    
        for ( int i = 0; i < buffer.length; i++ )
        {
            char car = buffer[i];
    
            if ( car == ch )
            {
                if ( same )
                {
                    continue;
                }
                else
                {
                    same = true;
                    newbuf[pos++] = car;
                }
            }
            else
            {
                same = false;
                newbuf[pos++] = car;
            }
        }
    
        return new String( newbuf, 0, pos );
    }
    

    /**
     * Truncates large Strings showing a portion of the String's head and tail
     * with the center cut out and replaced with '...'. Also displays the total
     * length of the truncated string so size of '...' can be interpreted.
     * Useful for large strings in UIs or hex dumps to log files.
     * 
     * @param str the string to truncate
     * @param head the amount of the head to display
     * @param tail the amount of the tail to display
     * @return the center truncated string
     */
    public static String centerTrunc( String str, int head, int tail )
    {
        StringBuffer buf = null;
    
        // Return as-is if String is smaller than or equal to the head plus the
        // tail plus the number of characters added to the trunc representation
        // plus the number of digits in the string length.
        if ( str.length() <= ( head + tail + 7 + str.length() / 10 ) )
        {
            return str;
        }
    
        buf = new StringBuffer();
        buf.append( '[' ).append( str.length() ).append( "][" );
        buf.append( str.substring( 0, head ) ).append( "..." );
        buf.append( str.substring( str.length() - tail ) );
        buf.append( ']' );
        return buf.toString();
    }
    

    /**
     * Gets a hex string from byte array.
     * 
     * @param res the byte array
     * @return the hex string representing the binary values in the array
     */
    public static String toHexString( byte[] res )
    {
        StringBuffer buf = new StringBuffer( res.length << 1 );
        
        for ( int ii = 0; ii < res.length; ii++ )
        {
            String digit = Integer.toHexString( 0xFF & res[ii] );
            
            if ( digit.length() == 1 )
            {
                digit = '0' + digit;
            }
            
            buf.append( digit );
        }
        return buf.toString().toUpperCase();
    }
    

    /**
     * Get byte array from hex string
     * 
     * @param hexString the hex string to convert to a byte array
     * @return the byte form of the hex string.
     */
    public static byte[] toByteArray( String hexString )
    {
        int arrLength = hexString.length() >> 1;
        byte [] buf = new byte[arrLength];
        
        for ( int ii = 0; ii < arrLength; ii++ )
        {
            int index = ii << 1;
            
            String digit = hexString.substring( index, index + 2 );
            buf[ii] = ( byte ) Integer.parseInt( digit, 16 );
        }
        
        return buf;
    }
    

    /**
     * This method is used to insert HTML block dynamically
     * 
     * @param source the HTML code to be processes
     * @param replaceNl if true '\n' will be replaced by &lt;br>
     * @param replaceTag if true '<' will be replaced by &lt; and '>' will be replaced
     *            by &gt;
     * @param replaceQuote if true '\"' will be replaced by &quot;
     * @return the formated html block
     */
    public static String formatHtml( String source, boolean replaceNl, boolean replaceTag,
        boolean replaceQuote )
    {
        StringBuffer buf = new StringBuffer();
        int len = source.length();
    
        for ( int ii = 0; ii < len; ii++ )
        {
            char ch = source.charAt( ii );
            
            switch ( ch )
            {
                case '\"':
                    if ( replaceQuote )
                    {
                        buf.append( "&quot;" );
                    }
                    else
                    {
                        buf.append( ch );
                    }
                    break;
    
                case '<':
                    if ( replaceTag )
                    {
                        buf.append( "&lt;" );
                    }
                    else
                    {
                        buf.append( ch );
                    }
                    break;
    
                case '>':
                    if ( replaceTag )
                    {
                        buf.append( "&gt;" );
                    }
                    else
                    {
                        buf.append( ch );
                    }
                    break;
    
                case '\n':
                    if ( replaceNl )
                    {
                        if ( replaceTag )
                        {
                            buf.append( "&lt;br&gt;" );
                        }
                        else
                        {
                            buf.append( "<br>" );
                        }
                    }
                    else
                    {
                        buf.append( ch );
                    }
                    break;
    
                case '\r':
                    break;
    
                case '&':
                    buf.append( "&amp;" );
                    break;
    
                default:
                    buf.append( ch );
                    break;
            }
        }
    
        return buf.toString();
    }
    

    /**
     * Check if a text is present at the current position in another string.
     * 
     * @param string The string which contains the data
     * @param index Current position in the string
     * @param text The text we want to check
     * @return <code>true</code> if the string contains the text.
     */
    public static boolean areEquals( String string, int index, String text )
    {
        if ( ( string == null ) || ( text == null ) ) 
        {
            return false;
        }
        
        int length1 = string.length();
        int length2 = text.length();
    
        if ( ( length1 == 0 ) || ( length1 <= index ) || ( index < 0 )
            || ( length2 == 0 ) || ( length2 > ( length1 + index ) ) )
        {
            return false;
        }
        else
        {
            return string.substring( index ).startsWith( text );
        }
    }
    

    /**
     * Test if the current character is equal to a specific character. This
     * function works only for character between 0 and 127, as it does compare a
     * byte and a char (which is 16 bits wide)
     * 
     * @param byteArray The buffer which contains the data
     * @param index Current position in the buffer
     * @param car The character we want to compare with the current buffer position
     * @return <code>true</code> if the current character equals the given character.
     */
    public static boolean isCharASCII( byte[] byteArray, int index, char car )
    {
        if ( ( byteArray == null ) || ( byteArray.length == 0 ) || ( index < 0 ) || ( index >= byteArray.length ) )
        {
            return false;
        }
        else
        {
            return ( ( byteArray[index] == car ) ? true : false );
        }
    }

    /**
     * Test if the current character is equal to a specific character.
     * 
     * @param string The String which contains the data
     * @param index Current position in the string
     * @param car The character we want to compare with the current string position
     * @return <code>true</code> if the current character equals the given character.
     */
    public static boolean isCharASCII( String string, int index, char car )
    {
        if ( string == null )
        {
            return false;
        }
        
        int length = string.length();
        
        if ( ( length == 0 ) || ( index < 0 ) || ( index >= length ) )
        {
            return false;
        }
        else
        {
            return string.charAt( index ) == car;
        }
    }
}
