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


import org.apache.directory.shared.i18n.I18n;


/**
 * Encoding and decoding of Base64 characters to and from raw bytes.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class Base64
{

    /**
     * Private constructor.
     */
    private Base64()
    {
    }


    /**
     * Encodes binary data to a Base64 encoded characters.
     * 
     * @param data
     *            the array of bytes to encode
     * @return base64-coded character array.
     */
    public static char[] encode( byte[] data )
    {
        char[] out = new char[( ( data.length + 2 ) / 3 ) * 4];

        //
        // 3 bytes encode to 4 chars. Output is always an even
        // multiple of 4 characters.
        //
        for ( int ii = 0, index = 0; ii < data.length; ii += 3, index += 4 )
        {
            boolean isQuadrupel = false;
            boolean isTripel = false;

            int val = ( 0xFF & data[ii] );
            val <<= 8;
            if ( ( ii + 1 ) < data.length )
            {
                val |= ( 0xFF & data[ii + 1] );
                isTripel = true;
            }

            val <<= 8;
            if ( ( ii + 2 ) < data.length )
            {
                val |= ( 0xFF & data[ii + 2] );
                isQuadrupel = true;
            }

            out[index + 3] = ALPHABET[( isQuadrupel ? ( val & 0x3F ) : 64 )];
            val >>= 6;
            out[index + 2] = ALPHABET[( isTripel ? ( val & 0x3F ) : 64 )];
            val >>= 6;
            out[index + 1] = ALPHABET[val & 0x3F];
            val >>= 6;
            out[index + 0] = ALPHABET[val & 0x3F];
        }
        return out;
    }


    /**
     * Decodes a BASE-64 encoded stream to recover the original data. White
     * space before and after will be trimmed away, but no other manipulation of
     * the input will be performed. As of version 1.2 this method will properly
     * handle input containing junk characters (newlines and the like) rather
     * than throwing an error. It does this by pre-parsing the input and
     * generating from that a count of VALID input characters.
     * 
     * @param data
     *            data to decode.
     * @return the decoded binary data.
     */
    public static byte[] decode( char[] data )
    {
        // as our input could contain non-BASE64 data (newlines,
        // whitespace of any sort, whatever) we must first adjust
        // our count of USABLE data so that...
        // (a) we don't misallocate the output array, and
        // (b) think that we miscalculated our data length
        // just because of extraneous throw-away junk

        int tempLen = data.length;

        for ( char c : data )
        {
            if ( ( c > 255 ) || CODES[c] < 0 )
            {
                --tempLen; // ignore non-valid chars and padding
            }
        }
        // calculate required length:
        // -- 3 bytes for every 4 valid base64 chars
        // -- plus 2 bytes if there are 3 extra base64 chars,
        // or plus 1 byte if there are 2 extra.

        int len = ( tempLen / 4 ) * 3;

        if ( ( tempLen % 4 ) == 3 )
        {
            len += 2;
        }

        if ( ( tempLen % 4 ) == 2 )
        {
            len += 1;
        }

        byte[] out = new byte[len];

        int shift = 0; // # of excess bits stored in accum
        int accum = 0; // excess bits
        int index = 0;

        // we now go through the entire array (NOT using the 'tempLen' value)
        for ( char c : data )
        {
            int value = ( c > 255 ) ? -1 : CODES[c];

            if ( value >= 0 ) // skip over non-code
            {
                accum <<= 6; // bits shift up by 6 each time thru
                shift += 6; // loop, with new bits being put in
                accum |= value; // at the bottom. whenever there
                if ( shift >= 8 ) // are 8 or more shifted in, write them
                {
                    shift -= 8; // out (from the top, leaving any excess
                    out[index++] = // at the bottom for next iteration.
                    ( byte ) ( ( accum >> shift ) & 0xff );
                }
            }
            // we will also have skipped processing a padding null byte ('=')
            // here;
            // these are used ONLY for padding to an even length and do not
            // legally
            // occur as encoded data. for this reason we can ignore the fact
            // that
            // no index++ operation occurs in that special case: the out[] array
            // is
            // initialized to all-zero bytes to start with and that works to our
            // advantage in this combination.
        }

        // if there is STILL something wrong we just have to throw up now!
        if ( index != out.length )
        {
            throw new Error( I18n.err( I18n.ERR_04348, index, out.length ) );
        }

        return out;
    }

    /** code characters for values 0..63 */
    private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="
        .toCharArray();

    /** lookup table for converting base64 characters to value in range 0..63 */
    private static final byte[] CODES = new byte[256];

    static
    {
        for ( int ii = 0; ii < 256; ii++ )
        {
            CODES[ii] = -1;
        }

        for ( int ii = 'A'; ii <= 'Z'; ii++ )
        {
            CODES[ii] = ( byte ) ( ii - 'A' );
        }

        for ( int ii = 'a'; ii <= 'z'; ii++ )
        {
            CODES[ii] = ( byte ) ( 26 + ii - 'a' );
        }

        for ( int ii = '0'; ii <= '9'; ii++ )
        {
            CODES[ii] = ( byte ) ( 52 + ii - '0' );
        }

        CODES['+'] = 62;
        CODES['/'] = 63;
    }
}
