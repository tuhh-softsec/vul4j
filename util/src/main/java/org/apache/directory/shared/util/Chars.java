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
 * Various Character methods are kept here.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class Chars
{
    /** &lt;alpha> ::= [0x41-0x5A] | [0x61-0x7A] */
    public static final boolean[] ALPHA =
        {
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  false, false, false, false, false,
            false, true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  false, false, false, false, false
        };
    /** &lt;alpha-lower-case> ::= [0x61-0x7A] */
    public static final boolean[] ALPHA_LOWER_CASE =
        {
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  false, false, false, false, false
        };
    /** &lt;alpha-upper-case> ::= [0x41-0x5A] */
    public static final boolean[] ALPHA_UPPER_CASE =
        {
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
        };
    /** &lt;alpha-digit> | &lt;digit> */
    public static final boolean[] ALPHA_DIGIT =
        {
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  false, false, false, false, false, false,
            false, true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  false, false, false, false, false,
            false, true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  false, false, false, false, false
        };
    /** &lt;alpha> | &lt;digit> | '-' */
    public static final boolean[] CHAR =
        {
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, true,  false, false,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  false, false, false, false, false, false,
            false, true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  false, false, false, false, false,
            false, true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  true,  false, false, false, false, false
        };
    /** '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' */
    public static final boolean[] DIGIT =
        {
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false
        };
    /** &lt;hex> ::= [0x30-0x39] | [0x41-0x46] | [0x61-0x66] */
    public static final boolean[] HEX =
        {
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            true,  true,  true,  true,  true,  true,  true,  true,
            true,  true,  false, false, false, false, false, false,
            false, true,  true,  true,  true,  true,  true,  false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, true,  true,  true,  true,  true,  true,  false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false };

    /* *
    * Test if the current character is equal to a specific character.
    *
    * @param chars The buffer which contains the data
    * @param index
    *            Current position in the buffer
    * @param car The character we want to compare with the current buffer position
    * @return <code>true</code> if the current character equals the given character.
    */
    public static boolean isCharASCII( char[] chars, int index, char car )
    {
        if ( ( chars == null ) || ( chars.length == 0 ) || ( index < 0 ) || ( index >= chars.length ) )
        {
            return false;
        }
        else
        {
            return ( ( chars[index] == car ) ? true : false );
        }
    }

    /**
     * Test if the current character is equal to a specific character.
     *
     * @param string The String which contains the data
     * @param index Current position in the string
     * @param car The character we want to compare with the current string
     *            position
     * @return <code>true</code> if the current character equals the given
     *         character.
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

    /**
     * Test if the current character is equal to a specific character.
     *
     * @param string The String which contains the data
     * @param index Current position in the string
     * @param car The character we want to compare with the current string
     *            position
     * @return <code>true</code> if the current character equals the given
     *         character.
     */
    public static boolean isICharASCII( String string, int index, char car )
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
            return ( ( string.charAt( index ) | 0x20 ) & car ) == car;
        }
    }

    /**
     * Test if the current character is equal to a specific character.
     *
     * @param bytes The String which contains the data
     * @param index Current position in the string
     * @param car The character we want to compare with the current string
     *            position
     * @return <code>true</code> if the current character equals the given
     *         character.
     */
    public static boolean isICharASCII( byte[] bytes, int index, char car )
    {
        if ( bytes == null )
        {
            return false;
        }

        int length = bytes.length;

        if ( ( length == 0 ) || ( index < 0 ) || ( index >= length ) )
        {
            return false;
        }
        else
        {
            return ( ( bytes[ index ] | 0x20 ) & car ) == car;
        }
    }

    /**
     * Test if the current byte is an Alpha character :
     * &lt;alpha> ::= [0x41-0x5A] | [0x61-0x7A]
     *
     * @param c The byte to test
     *
     * @return <code>true</code> if the byte is an Alpha
     *         character
     */
    public static boolean isAlpha( byte c )
    {
        return ( ( c > 0 ) && ( c <= 127 ) && ALPHA[c] );
    }

    /**
     * Test if the current character is an Alpha character :
     * &lt;alpha> ::= [0x41-0x5A] | [0x61-0x7A]
     *
     * @param c The char to test
     *
     * @return <code>true</code> if the character is an Alpha
     *         character
     */
    public static boolean isAlpha( char c )
    {
        return ( ( c > 0 ) && ( c <= 127 ) && ALPHA[c] );
    }

    /**
     * Test if the current character is an Alpha character : &lt;alpha> ::=
     * [0x41-0x5A] | [0x61-0x7A]
     *
     * @param bytes The buffer which contains the data
     * @param index Current position in the buffer
     * @return <code>true</code> if the current character is an Alpha
     *         character
     */
    public static boolean isAlphaASCII( byte[] bytes, int index )
    {
        if ( ( bytes == null ) || ( bytes.length == 0 ) || ( index < 0 ) || ( index >= bytes.length ) )
        {
            return false;
        }
        else
        {
            byte c = bytes[index];

            if ( ( ( c | 0x7F ) != 0x7F ) || !ALPHA[c] )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    /**
     * Test if the current character is an Alpha character : &lt;alpha> ::=
     * [0x41-0x5A] | [0x61-0x7A]
     *
     * @param chars The buffer which contains the data
     * @param index Current position in the buffer
     * @return <code>true</code> if the current character is an Alpha
     *         character
     */
    public static boolean isAlphaASCII( char[] chars, int index )
    {
        if ( ( chars == null ) || ( chars.length == 0 ) || ( index < 0 ) || ( index >= chars.length ) )
        {
            return false;
        }
        else
        {
            char c = chars[index];

            if ( ( c > 127 ) || !ALPHA[c] )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    /**
     * Test if the current character is an Alpha character : &lt;alpha> ::=
     * [0x41-0x5A] | [0x61-0x7A]
     *
     * @param string The string which contains the data
     * @param index Current position in the string
     * @return <code>true</code> if the current character is an Alpha
     *         character
     */
    public static boolean isAlphaASCII( String string, int index )
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
            char c = string.charAt( index );

            if ( ( c > 127 ) || !ALPHA[c] )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    /**
     * Test if the current character is a lowercased Alpha character : <br/>
     * &lt;alpha> ::= [0x61-0x7A]
     *
     * @param string The string which contains the data
     * @param index Current position in the string
     * @return <code>true</code> if the current character is a lower Alpha
     *         character
     */
    public static boolean isAlphaLowercaseASCII( String string, int index )
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
            char c = string.charAt( index );

            if ( ( c > 127 ) || !ALPHA_LOWER_CASE[c] )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    /**
     * Test if the current character is a uppercased Alpha character : <br/>
     * &lt;alpha> ::= [0x61-0x7A]
     *
     * @param string The string which contains the data
     * @param index Current position in the string
     * @return <code>true</code> if the current character is a lower Alpha
     *         character
     */
    public static boolean isAlphaUppercaseASCII( String string, int index )
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
            char c = string.charAt( index );

            if ( ( c > 127 ) || !ALPHA_UPPER_CASE[c] )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    /**
     * Check if the current character is an 7 bits ASCII CHAR (between 0 and
     * 127).
     * &lt;char> ::= &lt;alpha> | &lt;digit>
     *
     * @param string The string which contains the data
     * @param index Current position in the string
     * @return The position of the next character, if the current one is a CHAR.
     */
    public static boolean isAlphaDigit( String string, int index )
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
            char c = string.charAt( index );

            if ( ( c > 127 ) || !ALPHA_DIGIT[c] )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    /**
     * Check if the current character is an 7 bits ASCII CHAR (between 0 and
     * 127). &lt;char> ::= &lt;alpha> | &lt;digit> | '-'
     *
     * @param bytes The buffer which contains the data
     * @param index Current position in the buffer
     * @return The position of the next character, if the current one is a CHAR.
     */
    public static boolean isAlphaDigitMinus( byte[] bytes, int index )
    {
        if ( ( bytes == null ) || ( bytes.length == 0 ) || ( index < 0 ) || ( index >= bytes.length ) )
        {
            return false;
        }
        else
        {
            byte c = bytes[index];

            if ( ( ( c | 0x7F ) != 0x7F ) || !CHAR[c] )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    /**
     * Check if the current character is an 7 bits ASCII CHAR (between 0 and
     * 127). &lt;char> ::= &lt;alpha> | &lt;digit> | '-'
     *
     * @param chars The buffer which contains the data
     * @param index Current position in the buffer
     * @return The position of the next character, if the current one is a CHAR.
     */
    public static boolean isAlphaDigitMinus( char[] chars, int index )
    {
        if ( ( chars == null ) || ( chars.length == 0 ) || ( index < 0 ) || ( index >= chars.length ) )
        {
            return false;
        }
        else
        {
            char c = chars[index];

            if ( ( c > 127 ) || !CHAR[c] )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    /**
     * Check if the current character is an 7 bits ASCII CHAR (between 0 and
     * 127). &lt;char> ::= &lt;alpha> | &lt;digit> | '-'
     *
     * @param string The string which contains the data
     * @param index Current position in the string
     * @return The position of the next character, if the current one is a CHAR.
     */
    public static boolean isAlphaDigitMinus( String string, int index )
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
            char c = string.charAt( index );

            if ( ( c > 127 ) || !CHAR[c] )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    /**
     * Test if the current character is a bit, ie 0 or 1.
     *
     * @param string
     *            The String which contains the data
     * @param index
     *            Current position in the string
     * @return <code>true</code> if the current character is a bit (0 or 1)
     */
    public static boolean isBit( String string, int index )
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
            char c =  string.charAt(  index );
            return ( ( c == '0' ) || ( c == '1' ) );
        }
    }

    /**
     * Test if the current character is a digit &lt;digit> ::= '0' | '1' | '2' |
     * '3' | '4' | '5' | '6' | '7' | '8' | '9'
     *
     * @param bytes The buffer which contains the data
     * @return <code>true</code> if the current character is a Digit
     */
    public static boolean isDigit( byte[] bytes )
    {
        if ( ( bytes == null ) || ( bytes.length == 0 ) )
        {
            return false;
        }
        else
        {
            return ( ( ( ( bytes[0] | 0x7F ) != 0x7F ) || !DIGIT[bytes[0]] ) ? false : true );
        }
    }

    /**
     * Test if the current character is a digit &lt;digit> ::= '0' | '1' | '2' |
     * '3' | '4' | '5' | '6' | '7' | '8' | '9'
     *
     * @param car the character to test
     *
     * @return <code>true</code> if the character is a Digit
     */
    public static boolean isDigit( char car )
    {
        return ( car >= '0' ) && ( car <= '9' );
    }

    /**
     * Test if the current character is a digit &lt;digit> ::= '0' | '1' | '2' |
     * '3' | '4' | '5' | '6' | '7' | '8' | '9'
     *
     * @param bytes The buffer which contains the data
     * @param index Current position in the buffer
     * @return <code>true</code> if the current character is a Digit
     */
    public static boolean isDigit( byte[] bytes, int index )
    {
        if ( ( bytes == null ) || ( bytes.length == 0 ) || ( index < 0 ) || ( index >= bytes.length ) )
        {
            return false;
        }
        else
        {
            return ( ( ( ( bytes[index] | 0x7F ) !=  0x7F ) || !DIGIT[bytes[index]] ) ? false : true );
        }
    }

    /**
     * Test if the current character is a digit &lt;digit> ::= '0' | '1' | '2' |
     * '3' | '4' | '5' | '6' | '7' | '8' | '9'
     *
     * @param chars The buffer which contains the data
     * @param index Current position in the buffer
     * @return <code>true</code> if the current character is a Digit
     */
    public static boolean isDigit( char[] chars, int index )
    {
        if ( ( chars == null ) || ( chars.length == 0 ) || ( index < 0 ) || ( index >= chars.length ) )
        {
            return false;
        }
        else
        {
            return ( ( ( chars[index] > 127 ) || !DIGIT[chars[index]] ) ? false : true );
        }
    }

    /**
     * Test if the current character is a digit &lt;digit> ::= '0' | '1' | '2' |
     * '3' | '4' | '5' | '6' | '7' | '8' | '9'
     *
     * @param string The string which contains the data
     * @param index Current position in the string
     * @return <code>true</code> if the current character is a Digit
     */
    public static boolean isDigit( String string, int index )
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
            char c = string.charAt(  index  );
            return ( ( ( c > 127 ) || !DIGIT[c] ) ? false : true );
        }
    }

    /**
     * Test if the current character is a digit &lt;digit> ::= '0' | '1' | '2' |
     * '3' | '4' | '5' | '6' | '7' | '8' | '9'
     *
     * @param chars The buffer which contains the data
     * @return <code>true</code> if the current character is a Digit
     */
    public static boolean isDigit( char[] chars )
    {
        if ( ( chars == null ) || ( chars.length == 0 ) )
        {
            return false;
        }
        else
        {
            return ( ( ( chars[0] > 127 ) || !DIGIT[chars[0]] ) ? false : true );
        }
    }

    /**
     * Check if the current byte is an Hex Char
     * &lt;hex> ::= [0x30-0x39] | [0x41-0x46] | [0x61-0x66]
     *
     * @param b The byte we want to check
     * @return <code>true</code> if the current byte is a Hex byte
     */
    public static boolean isHex( byte b )
    {
        return ( ( b | 0x7F ) == 0x7F ) || HEX[b];
    }

    /**
     * Check if the current character is an Hex Char &lt;hex> ::= [0x30-0x39] |
     * [0x41-0x46] | [0x61-0x66]
     *
     * @param bytes The buffer which contains the data
     * @param index Current position in the buffer
     * @return <code>true</code> if the current character is a Hex Char
     */
    public static boolean isHex( byte[] bytes, int index )
    {
        if ( ( bytes == null ) || ( bytes.length == 0 ) || ( index < 0 ) || ( index >= bytes.length ) )
        {
            return false;
        }
        else
        {
            byte c = bytes[index];

            if ( ( ( c | 0x7F ) != 0x7F ) || !HEX[c] )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    /**
     * Check if the current character is an Hex Char &lt;hex> ::= [0x30-0x39] |
     * [0x41-0x46] | [0x61-0x66]
     *
     * @param chars The buffer which contains the data
     * @param index Current position in the buffer
     * @return <code>true</code> if the current character is a Hex Char
     */
    public static boolean isHex( char[] chars, int index )
    {
        if ( ( chars == null ) || ( chars.length == 0 ) || ( index < 0 ) || ( index >= chars.length ) )
        {
            return false;
        }
        else
        {
            char c = chars[index];

            if ( ( c > 127 ) || !HEX[c] )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    /**
     * Check if the current character is an Hex Char &lt;hex> ::= [0x30-0x39] |
     * [0x41-0x46] | [0x61-0x66]
     *
     * @param string The string which contains the data
     * @param index Current position in the string
     * @return <code>true</code> if the current character is a Hex Char
     */
    public static boolean isHex( String string, int index )
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
            char c = string.charAt( index );

            if ( ( c > 127 ) || !HEX[c] )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }
}
