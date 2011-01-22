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
 * Various unicode manipulation methods that are more efficient then chaining
 * operations: all is done in the same buffer without creating a bunch of string
 * objects.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class Unicode
{
    /**
     * Count the number of bytes needed to return an Unicode char. This can be
     * from 1 to 6.
     *
     * @param bytes The bytes to read
     * @param pos Position to start counting. It must be a valid start of a
     *            encoded char !
     * @return The number of bytes to create a char, or -1 if the encoding is
     *         wrong. TODO : Should stop after the third byte, as a char is only
     *         2 bytes long.
     */
    public static int countBytesPerChar( byte[] bytes, int pos )
    {
        if ( bytes == null )
        {
            return -1;
        }

        if ( ( bytes[pos] & UnicodeConstants.UTF8_MULTI_BYTES_MASK ) == 0 )
        {
            return 1;
        }
        else if ( ( bytes[pos] & UnicodeConstants.UTF8_TWO_BYTES_MASK ) == UnicodeConstants.UTF8_TWO_BYTES )
        {
            return 2;
        }
        else if ( ( bytes[pos] & UnicodeConstants.UTF8_THREE_BYTES_MASK ) == UnicodeConstants.UTF8_THREE_BYTES )
        {
            return 3;
        }
        else if ( ( bytes[pos] & UnicodeConstants.UTF8_FOUR_BYTES_MASK ) == UnicodeConstants.UTF8_FOUR_BYTES )
        {
            return 4;
        }
        else if ( ( bytes[pos] & UnicodeConstants.UTF8_FIVE_BYTES_MASK ) == UnicodeConstants.UTF8_FIVE_BYTES )
        {
            return 5;
        }
        else if ( ( bytes[pos] & UnicodeConstants.UTF8_SIX_BYTES_MASK ) == UnicodeConstants.UTF8_SIX_BYTES )
        {
            return 6;
        }
        else
        {
            return -1;
        }
    }

    /**
     * Return the Unicode char which is coded in the bytes at position 0.
     *
     * @param bytes The byte[] represntation of an Unicode string.
     * @return The first char found.
     */
    public static char bytesToChar( byte[] bytes )
    {
        return bytesToChar( bytes, 0 );
    }

    /**
     * Return the Unicode char which is coded in the bytes at the given
     * position.
     *
     * @param bytes The byte[] represntation of an Unicode string.
     * @param pos The current position to start decoding the char
     * @return The decoded char, or -1 if no char can be decoded TODO : Should
     *         stop after the third byte, as a char is only 2 bytes long.
     */
    public static char bytesToChar( byte[] bytes, int pos )
    {
        if ( bytes == null )
        {
            return ( char ) -1;
        }

        if ( ( bytes[pos] & UnicodeConstants.UTF8_MULTI_BYTES_MASK ) == 0 )
        {
            return ( char ) bytes[pos];
        }
        else
        {
            if ( ( bytes[pos] & UnicodeConstants.UTF8_TWO_BYTES_MASK ) == UnicodeConstants.UTF8_TWO_BYTES )
            {
                // Two bytes char
                return ( char ) ( ( ( bytes[pos] & 0x1C ) << 6 ) + // 110x-xxyy
                                                                    // 10zz-zzzz
                                                                    // ->
                                                                    // 0000-0xxx
                                                                    // 0000-0000
                    ( ( bytes[pos] & 0x03 ) << 6 ) + // 110x-xxyy 10zz-zzzz
                                                        // -> 0000-0000
                                                        // yy00-0000
                ( bytes[pos + 1] & 0x3F ) // 110x-xxyy 10zz-zzzz -> 0000-0000
                                            // 00zz-zzzz
                ); // -> 0000-0xxx yyzz-zzzz (07FF)
            }
            else if ( ( bytes[pos] & UnicodeConstants.UTF8_THREE_BYTES_MASK ) == UnicodeConstants.UTF8_THREE_BYTES )
            {
                // Three bytes char
                return ( char ) (
                // 1110-tttt 10xx-xxyy 10zz-zzzz -> tttt-0000-0000-0000
                ( ( bytes[pos] & 0x0F ) << 12 )
                    // 1110-tttt 10xx-xxyy 10zz-zzzz -> 0000-xxxx-0000-0000
                    + ( ( bytes[pos + 1] & 0x3C ) << 6 )
                    // 1110-tttt 10xx-xxyy 10zz-zzzz -> 0000-0000-yy00-0000
                    + ( ( bytes[pos + 1] & 0x03 ) << 6 )
                // 1110-tttt 10xx-xxyy 10zz-zzzz -> 0000-0000-00zz-zzzz
                + ( bytes[pos + 2] & 0x3F )
                // -> tttt-xxxx yyzz-zzzz (FF FF)
                );
            }
            else if ( ( bytes[pos] & UnicodeConstants.UTF8_FOUR_BYTES_MASK ) == UnicodeConstants.UTF8_FOUR_BYTES )
            {
                // Four bytes char
                return ( char ) (
                // 1111-0ttt 10uu-vvvv 10xx-xxyy 10zz-zzzz -> 000t-tt00
                // 0000-0000 0000-0000
                ( ( bytes[pos] & 0x07 ) << 18 )
                    // 1111-0ttt 10uu-vvvv 10xx-xxyy 10zz-zzzz -> 0000-00uu
                    // 0000-0000 0000-0000
                    + ( ( bytes[pos + 1] & 0x30 ) << 16 )
                    // 1111-0ttt 10uu-vvvv 10xx-xxyy 10zz-zzzz -> 0000-0000
                    // vvvv-0000 0000-0000
                    + ( ( bytes[pos + 1] & 0x0F ) << 12 )
                    // 1111-0ttt 10uu-vvvv 10xx-xxyy 10zz-zzzz -> 0000-0000
                    // 0000-xxxx 0000-0000
                    + ( ( bytes[pos + 2] & 0x3C ) << 6 )
                    // 1111-0ttt 10uu-vvvv 10xx-xxyy 10zz-zzzz -> 0000-0000
                    // 0000-0000 yy00-0000
                    + ( ( bytes[pos + 2] & 0x03 ) << 6 )
                // 1111-0ttt 10uu-vvvv 10xx-xxyy 10zz-zzzz -> 0000-0000
                // 0000-0000 00zz-zzzz
                + ( bytes[pos + 3] & 0x3F )
                // -> 000t-ttuu vvvv-xxxx yyzz-zzzz (1FFFFF)
                );
            }
            else if ( ( bytes[pos] & UnicodeConstants.UTF8_FIVE_BYTES_MASK ) == UnicodeConstants.UTF8_FIVE_BYTES )
            {
                // Five bytes char
                return ( char ) (
                // 1111-10tt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz ->
                // 0000-00tt 0000-0000 0000-0000 0000-0000
                ( ( bytes[pos] & 0x03 ) << 24 )
                    // 1111-10tt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz ->
                    // 0000-0000 uuuu-uu00 0000-0000 0000-0000
                    + ( ( bytes[pos + 1] & 0x3F ) << 18 )
                    // 1111-10tt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz ->
                    // 0000-0000 0000-00vv 0000-0000 0000-0000
                    + ( ( bytes[pos + 2] & 0x30 ) << 12 )
                    // 1111-10tt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz ->
                    // 0000-0000 0000-0000 wwww-0000 0000-0000
                    + ( ( bytes[pos + 2] & 0x0F ) << 12 )
                    // 1111-10tt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz ->
                    // 0000-0000 0000-0000 0000-xxxx 0000-0000
                    + ( ( bytes[pos + 3] & 0x3C ) << 6 )
                    // 1111-10tt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz ->
                    // 0000-0000 0000-0000 0000-0000 yy00-0000
                    + ( ( bytes[pos + 3] & 0x03 ) << 6 )
                // 1111-10tt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz ->
                // 0000-0000 0000-0000 0000-0000 00zz-zzzz
                + ( bytes[pos + 4] & 0x3F )
                // -> 0000-00tt uuuu-uuvv wwww-xxxx yyzz-zzzz (03 FF FF FF)
                );
            }
            else if ( ( bytes[pos] & UnicodeConstants.UTF8_FIVE_BYTES_MASK ) == UnicodeConstants.UTF8_FIVE_BYTES )
            {
                // Six bytes char
                return ( char ) (
                // 1111-110s 10tt-tttt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz
                // ->
                // 0s00-0000 0000-0000 0000-0000 0000-0000
                ( ( bytes[pos] & 0x01 ) << 30 )
                    // 1111-110s 10tt-tttt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz
                    // ->
                    // 00tt-tttt 0000-0000 0000-0000 0000-0000
                    + ( ( bytes[pos + 1] & 0x3F ) << 24 )
                    // 1111-110s 10tt-tttt 10uu-uuuu 10vv-wwww 10xx-xxyy
                    // 10zz-zzzz ->
                    // 0000-0000 uuuu-uu00 0000-0000 0000-0000
                    + ( ( bytes[pos + 2] & 0x3F ) << 18 )
                    // 1111-110s 10tt-tttt 10uu-uuuu 10vv-wwww 10xx-xxyy
                    // 10zz-zzzz ->
                    // 0000-0000 0000-00vv 0000-0000 0000-0000
                    + ( ( bytes[pos + 3] & 0x30 ) << 12 )
                    // 1111-110s 10tt-tttt 10uu-uuuu 10vv-wwww 10xx-xxyy
                    // 10zz-zzzz ->
                    // 0000-0000 0000-0000 wwww-0000 0000-0000
                    + ( ( bytes[pos + 3] & 0x0F ) << 12 )
                    // 1111-110s 10tt-tttt 10uu-uuuu 10vv-wwww 10xx-xxyy
                    // 10zz-zzzz ->
                    // 0000-0000 0000-0000 0000-xxxx 0000-0000
                    + ( ( bytes[pos + 4] & 0x3C ) << 6 )
                    // 1111-110s 10tt-tttt 10uu-uuuu 10vv-wwww 10xx-xxyy
                    // 10zz-zzzz ->
                    // 0000-0000 0000-0000 0000-0000 yy00-0000
                    + ( ( bytes[pos + 4] & 0x03 ) << 6 )
                // 1111-110s 10tt-tttt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz
                // ->
                // 0000-0000 0000-0000 0000-0000 00zz-zzzz
                + ( bytes[pos + 5] & 0x3F )
                // -> 0stt-tttt uuuu-uuvv wwww-xxxx yyzz-zzzz (7F FF FF FF)
                );
            }
            else
            {
                return ( char ) -1;
            }
        }
    }

    /**
     * Return the number of bytes that hold an Unicode char.
     *
     * @param car The character to be decoded
     * @return The number of bytes to hold the char. TODO : Should stop after
     *         the third byte, as a char is only 2 bytes long.
     */
    public static int countNbBytesPerChar( char car )
    {
        if ( ( car & UnicodeConstants.CHAR_ONE_BYTE_MASK ) == 0 )
        {
            return 1;
        }
        else if ( ( car & UnicodeConstants.CHAR_TWO_BYTES_MASK ) == 0 )
        {
            return 2;
        }
        else if ( ( car & UnicodeConstants.CHAR_THREE_BYTES_MASK ) == 0 )
        {
            return 3;
        }
        else if ( ( car & UnicodeConstants.CHAR_FOUR_BYTES_MASK ) == 0 )
        {
            return 4;
        }
        else if ( ( car & UnicodeConstants.CHAR_FIVE_BYTES_MASK ) == 0 )
        {
            return 5;
        }
        else if ( ( car & UnicodeConstants.CHAR_SIX_BYTES_MASK ) == 0 )
        {
            return 6;
        }
        else
        {
            return -1;
        }
    }

    /**
     * Count the number of bytes included in the given char[].
     *
     * @param chars The char array to decode
     * @return The number of bytes in the char array
     */
    public static int countBytes( char[] chars )
    {
        if ( chars == null )
        {
            return 0;
        }

        int nbBytes = 0;
        int currentPos = 0;

        while ( currentPos < chars.length )
        {
            int nbb = countNbBytesPerChar( chars[currentPos] );

            // If the number of bytes necessary to encode a character is
            // above 3, we will need two UTF-16 chars
            currentPos += ( nbb < 4 ? 1 : 2 );
            nbBytes += nbb;
        }

        return nbBytes;
    }

    /**
     * Count the number of chars included in the given byte[].
     *
     * @param bytes The byte array to decode
     * @return The number of char in the byte array
     */
    public static int countChars( byte[] bytes )
    {
        if ( bytes == null )
        {
            return 0;
        }

        int nbChars = 0;
        int currentPos = 0;

        while ( currentPos < bytes.length )
        {
            currentPos += countBytesPerChar(bytes, currentPos);
            nbChars++;
        }

        return nbChars;
    }

    /**
     * Return the Unicode char which is coded in the bytes at the given
     * position.
     *
     * @param car The character to be transformed to an array of bytes
     *
     * @return The byte array representing the char
     *
     * TODO : Should stop after the third byte, as a char is only 2 bytes long.
     */
    public static byte[] charToBytes( char car )
    {
        byte[] bytes = new byte[countNbBytesPerChar(car)];

        if ( car <= 0x7F )
        {
            // Single byte char
            bytes[0] = ( byte ) car;
            return bytes;
        }
        else if ( car <= 0x7FF )
        {
            // two bytes char
            bytes[0] = ( byte ) ( 0x00C0 + ( ( car & 0x07C0 ) >> 6 ) );
            bytes[1] = ( byte ) ( 0x0080 + ( car & 0x3F ) );
        }
        else
        {
            // Three bytes char
            bytes[0] = ( byte ) ( 0x00E0 + ( ( car & 0xF000 ) >> 12 ) );
            bytes[1] = ( byte ) ( 0x0080 + ( ( car & 0x0FC0 ) >> 6 ) );
            bytes[2] = ( byte ) ( 0x0080 + ( car & 0x3F ) );
        }

        return bytes;
    }

    /**
     * Check if the current char is in the unicodeSubset : all chars but
     * '\0', '(', ')', '*' and '\'
     *
     * @param str The string to check
     * @param pos Position of the current char
     * @return True if the current char is in the unicode subset
     */
    public static boolean isUnicodeSubset( String str, int pos )
    {
        if ( ( str == null ) || ( str.length() <= pos ) || ( pos < 0 ) )
        {
            return false;
        }

        char c = str.charAt( pos );

        return ( ( c > 127 ) || UnicodeConstants.UNICODE_SUBSET[c] );
    }

    /**
     * Check if the current char is in the unicodeSubset : all chars but
     * '\0', '(', ')', '*' and '\'
     *
     * @param c The char to check
     * @return True if the current char is in the unicode subset
     */
    public static boolean isUnicodeSubset( char c )
    {
        return ( ( c > 127 ) || UnicodeConstants.UNICODE_SUBSET[c] );
    }
}
