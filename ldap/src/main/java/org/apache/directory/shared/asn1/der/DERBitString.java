/*
 * Copyright (c) 2000 - 2006 The Legion Of The Bouncy Castle (http://www.bouncycastle.org)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 */

package org.apache.directory.shared.asn1.der;


import java.io.IOException;


/**
 * DER Bit String object.
 */
public class DERBitString extends DERObject
{
    /**
     * Basic DERObject constructor.
     */
    public DERBitString(byte[] value)
    {
        super( BIT_STRING, value );
    }
    
    
    /**
     * Gets the internal representation of a BitString as bytes 
     * 
     * @param length the number of bits
     * @param bytes The flags in an Int
     * @return A byte array containing the bytes and the initial unused bits
     */
    public DERBitString( int length, int value )
    {
        super( BIT_STRING, null );

        int nbBytes = length / 8 + ( length % 8 != 0 ? 1 : 0 );
        this.value = new byte[ nbBytes + 1 ];
        
        this.value[0] = (byte)( ( 8 - ( length % 8 ) ) % 8 );
        this.value[1] = (byte)( value >>> 24 );
        this.value[2] = (byte)( ( value >> 16 ) & 0x00ff ); 
        this.value[3] = (byte)( ( value >> 8 ) & 0x00ff ); 
        this.value[4] = (byte)( value & 0x00ff );
    }
    
    
    public byte[] getOctets()
    {
        return value;
    }


    public void encode( ASN1OutputStream out ) throws IOException
    {
        out.writeEncoded( BIT_STRING, value );
    }
}
