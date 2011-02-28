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
package org.apache.directory.shared.asn1.util;


import org.apache.directory.shared.i18n.I18n;


/**
 * Implement the Bit String primitive type. A BitString is internally stored as
 * an array of byte.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BitString
{
    /** A null MutableString */
    public static final BitString EMPTY_STRING = new BitString( 1 );

    /** The number of unused bits in the last byte */
    private int nbUnusedBits;

    /** The string is stored in a byte array */
    private byte[] bytes;

    /** Actual length of the byte array */
    private int nbBytes;

    /** Actual length of the bit string */
    private int nbBits;


    /**
     * Creates a BitString with a specific length (length is the number of
     * bits).
     *
     * @param length The BitString length (it's a number of bits)
     */
    public BitString( int length )
    {
        if ( length <= 0 )
        {
            // This is not allowed
            throw new IndexOutOfBoundsException( I18n.err( I18n.ERR_00029_NULL_OR_NEG_LENGTH_NOT_ALLOWED ) );
        }

        nbBits = length;

        // As we store values in bytes, we must divide the length by 8
        nbBytes = ( length / 8 );

        if ( ( length % 8 ) != 0 )
        {
            nbBytes += 1;
        }

        nbUnusedBits = ( 8 - ( length % 8 ) ) & 0x07;

        bytes = new byte[nbBytes];
    }


    /**
     * Creates a BitString from a byte[]. As the first byteis the number of unused bits
     * in the last byte, we have to ignore it.
     *
     * @param bytes The value to store. The first byte contains the number of
     * unused bits
     */
    public BitString( byte[] bytes )
    {
        if ( ( bytes == null ) || ( bytes.length == 0 ) )
        {
            nbBits = -1;
            return;
        }

        setData( bytes );
    }


    /**
     * Set a new BitString in the BitString. It will replace the old BitString,
     * and reset the current length with the new one.
     *
     * @param bytes The string to store
     */
    public void setData( byte[] bytes )
    {
        if ( ( bytes == null ) || ( bytes.length == 0 ) )
        {
            nbBits = -1;
            return;
        }

        // The first byte contains the number of unused bits
        nbUnusedBits = bytes[0] & 0x07;
        nbBytes = bytes.length - 1;
        nbBits = ( nbBytes * 8 ) - nbUnusedBits;
        this.bytes = new byte[nbBytes];

        // We have to transfer the data
        for ( int i = 0; i < nbBytes; i++ )
        {
            this.bytes[i] = bytes[i + 1];
        }
    }


    /**
     * Get the representation of a BitString. A first byte containing the number
     * of unused bits is added
     *
     * @return A byte array which represent the BitString
     */
    public byte[] getData()
    {
        byte[] copy = new byte[bytes.length + 1];

        System.arraycopy( bytes, 0, copy, 1, bytes.length );
        copy[0] = (byte)nbUnusedBits;

        return copy;
    }


    /**
     * Get the number of unused bits
     *
     * @return A byte which represent the number of unused bits
     */
    public byte getUnusedBits()
    {
        return ( byte ) nbUnusedBits;
    }


    /**
     * Set a bit at a specified position.
     * The bits are stored from left to right.
     * For instance, if we have 10 bits, then they are coded as b0 b1 b2 b3 b4 b5 b6 b7 - b8 b9 x x x x x x
     *
     * @param pos The bit to set
     */
    public void setBit( int pos )
    {
        if ( ( pos < 0 ) || ( pos > nbBits ) )
        {
            throw new IndexOutOfBoundsException( I18n.err( I18n.ERR_00030_BIT_NUMBER_OUT_OF_BOUND ) );
        }

        int posBytes = pos>>>3;
        int bitNumber = 7 - pos % 8;
        byte mask = (byte)( 1 << bitNumber );

        bytes[posBytes] |= mask;
    }


    /**
     * Clear a bit at a specified position.
     * The bits are stored from left to right.
     * For instance, if we have 10 bits, then they are coded
     * as b0 b1 b2 b3 b4 b5 b6 b7 - b8 b9 x x x x x x
     *
     * @param pos The bit to clear
     */
    public void clearBit( int pos )
    {
        if ( ( pos < 0 ) || ( pos > nbBits ) )
        {
            throw new IndexOutOfBoundsException( I18n.err( I18n.ERR_00030_BIT_NUMBER_OUT_OF_BOUND ) );
        }

        int posBytes = pos>>>3;
        int bitNumber = 7 - pos % 8;
        byte mask = (byte)( 1 << bitNumber );

        bytes[posBytes] &= ~mask;
    }


    /**
     * Get the bit stored into the BitString at a specific position.
     * The bits are stored from left to right, the LSB on the left and the
     * MSB on the right.<br/>
     * For instance, if we have 10 bits, then they are coded as
     * b0 b1 b2 b3 - b4 b5 b6 b7 - b8 b9 x x - x x x x
     * <pre>
     * With '1001 000x', where x is an unused bit,
     *       ^ ^    ^
     *       | |    |
     *       | |    |
     *       | |    +----- getBit(6) = 0
     *       | +---------- getBit(2) = 0
     *       +------------ getBit(0) = 1
     * </pre>
     * @param pos The position of the requested bit.
     *
     * @return <code>true</code> if the bit is set, <code>false</code> otherwise
     */
    public boolean getBit( int pos )
    {
        if ( pos > nbBits )
        {
            throw new IndexOutOfBoundsException( I18n.err( I18n.ERR_00031_CANNOT_FIND_BIT, pos, nbBits ) );
        }

        int posBytes = pos>>>3;
        int bitNumber = 7 - pos % 8;
        byte mask = (byte)( 1 << bitNumber );

        int res = bytes[posBytes] & mask;

        return res != 0;
    }


    /**
     * @return The number of bits stored in this BitString
     */
    public int size()
    {
        return nbBits;
    }


    /**
     * Return a native String representation of the BitString.
     *
     * @return A String representing the BitString
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for ( int i = 0; i < nbBits; i++ )
        {
            if ( getBit( i ) )
            {
                sb.append( '1' );
            }
            else
            {
                sb.append( '0' );
            }
        }

        return sb.toString();
    }
}
