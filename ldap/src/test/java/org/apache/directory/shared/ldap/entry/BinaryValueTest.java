/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.directory.shared.ldap.entry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.apache.directory.shared.ldap.entry.BinaryValue;
import org.apache.directory.shared.ldap.entry.Value;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.schema.Normalizer;
import org.apache.directory.shared.ldap.schema.SyntaxChecker;
import org.apache.directory.shared.ldap.util.StringTools;
import org.junit.Test;

/**
 * 
 * Test the BinaryValue class
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BinaryValueTest
{
    private static final byte[] BYTES1 = new byte[]{0x01, 0x02, 0x03, 0x04};
    private static final byte[] BYTES2 = new byte[]{(byte)0x81, (byte)0x82, (byte)0x83, (byte)0x84};
    private static final byte[] INVALID_BYTES = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05};
    private static final byte[] BYTES_MOD = new byte[]{0x11, 0x02, 0x03, 0x04};
    
    private static final Normalizer BINARY_NORMALIZER = new Normalizer( "1.1.1" )
    {
        private static final long serialVersionUID = 1L;
        
        public Value<?> normalize( Value<?> value ) throws LdapException
        {
            if ( value.isBinary() )
            {
                byte[] val = value.getBytes();
                // each byte will be changed to be > 0, and spaces will be trimmed
                byte[] newVal = new byte[ val.length ];
                int i = 0;
                
                for ( byte b:val )
                {
                    newVal[i++] = (byte)(b & 0x007F); 
                }
                
                return new BinaryValue( StringTools.trim( newVal ) );
            }

            throw new IllegalStateException( "expected byte[] to normalize" );
        }

        public String normalize( String value ) throws LdapException
        {
            throw new IllegalStateException( "expected byte[] to normalize" );
        }
};

    
    /**
     * A binary normalizer which set the normalized value to a empty byte array
     */
    private static final Normalizer BINARY_NORMALIZER_EMPTY = new Normalizer( "1.1.1" )
    {
        private static final long serialVersionUID = 1L;
        
        public Value<?> normalize( Value<?> value ) throws LdapException
        {
            if ( value.isBinary() )
            {
                return new BinaryValue( StringTools.EMPTY_BYTES );
            }

            throw new IllegalStateException( "expected byte[] to normalize" );
        }

        public String normalize( String value ) throws LdapException
        {
            throw new IllegalStateException( "expected byte[] to normalize" );
        }
    };

    
    private static final SyntaxChecker BINARY_CHECKER = new SyntaxChecker( "1.1.1" )
    {
        public boolean isValidSyntax( Object value )
        {
            if ( value == null )
            {
                return true;
            }
            
            return ((byte[])value).length < 5 ;
        }
    };
    
    
    /**
     * Serialize a BinaryValue
     */
    private ByteArrayOutputStream serializeValue( BinaryValue value ) throws IOException
    {
        ObjectOutputStream oOut = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try
        {
            oOut = new ObjectOutputStream( out );
            oOut.writeObject( value );
        }
        catch ( IOException ioe )
        {
            throw ioe;
        }
        finally
        {
            try
            {
                if ( oOut != null )
                {
                    oOut.flush();
                    oOut.close();
                }
            }
            catch ( IOException ioe )
            {
                throw ioe;
            }
        }
        
        return out;
    }
    
    
    /**
     * Deserialize a BinaryValue
     */
    private BinaryValue deserializeValue( ByteArrayOutputStream out ) throws IOException, ClassNotFoundException
    {
        ObjectInputStream oIn = null;
        ByteArrayInputStream in = new ByteArrayInputStream( out.toByteArray() );

        try
        {
            oIn = new ObjectInputStream( in );

            BinaryValue value = ( BinaryValue ) oIn.readObject();

            return value;
        }
        catch ( IOException ioe )
        {
            throw ioe;
        }
        finally
        {
            try
            {
                if ( oIn != null )
                {
                    oIn.close();
                }
            }
            catch ( IOException ioe )
            {
                throw ioe;
            }
        }
    }
    
    
    @Test
    public void testHashCode()
    {
        BinaryValue cbv = new BinaryValue();
        assertEquals( 0, cbv.hashCode() );
        
        cbv.set( StringTools.EMPTY_BYTES );
        int h = Arrays.hashCode( StringTools.EMPTY_BYTES );
        assertEquals( h, cbv.hashCode() );
        
        h = Arrays.hashCode( BYTES1 );
        cbv.set( BYTES1 );
        assertEquals( h, cbv.hashCode() );
    }


    @Test
    public void testBinaryValueNull() throws LdapException
    {
        BinaryValue cbv = new BinaryValue( (byte[])null );
        
        assertNull( cbv.get() );
        assertFalse( cbv.isNormalized() );
        assertTrue( cbv.isValid( BINARY_CHECKER ) );
        assertTrue( cbv.isNull() );
        assertNull( cbv.getNormalizedValue() );
    }


    @Test
    public void testBinaryValueEmpty() throws LdapException
    {
        BinaryValue cbv = new BinaryValue( StringTools.EMPTY_BYTES );
        
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, cbv.getBytes() ) );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, cbv.getCopy() ) );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, cbv.getReference() ) );
        assertFalse( cbv.isNormalized() );
        assertTrue( cbv.isValid( BINARY_CHECKER ) );
        assertFalse( cbv.isNull() );
        assertNotNull( cbv.getNormalizedValue() );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, cbv.getNormalizedValue() ) );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, cbv.getNormalizedValueReference() ) );
    }


    @Test
    public void testBinaryValue() throws LdapException
    {
        BinaryValue cbv = new BinaryValue( BYTES1 );
        
        assertTrue( Arrays.equals( BYTES1, cbv.getBytes() ) );
        assertTrue( Arrays.equals( BYTES1, cbv.getCopy() ) );
        assertTrue( Arrays.equals( BYTES1, cbv.getReference() ) );
        assertFalse( cbv.isNormalized() );
        assertTrue( cbv.isValid( BINARY_CHECKER ) );
        assertFalse( cbv.isNull() );
        assertNotNull( cbv.getNormalizedValue() );
        assertTrue( Arrays.equals( BYTES1, cbv.getNormalizedValue() ) );
    }


    @Test
    public void testSetByteArray() throws LdapException
    {
        BinaryValue cbv = new BinaryValue();
        
        cbv.set( BYTES1 );
        
        assertTrue( Arrays.equals( BYTES1, cbv.getBytes() ) );
        assertTrue( Arrays.equals( BYTES1, cbv.getCopy() ) );
        assertTrue( Arrays.equals( BYTES1, cbv.getReference() ) );
        assertFalse( cbv.isNormalized() );
        assertTrue( cbv.isValid( BINARY_CHECKER ) );
        assertFalse( cbv.isNull() );
        assertNotNull( cbv.getNormalizedValue() );
        assertTrue( Arrays.equals( BYTES1, cbv.getNormalizedValue() ) );
    }


    @Test
    public void testGetNormalizedValueCopy()  throws LdapException
    {
        BinaryValue cbv = new BinaryValue( BYTES2 );
        
        assertTrue( Arrays.equals( BYTES2, cbv.getBytes() ) );
        assertTrue( Arrays.equals( BYTES2, cbv.getCopy() ) );
        assertTrue( Arrays.equals( BYTES2, cbv.getReference() ) );
        assertFalse( cbv.isNormalized() );
        assertTrue( cbv.isValid( BINARY_CHECKER ) );
        assertFalse( cbv.isNull() );
        assertNotNull( cbv.getNormalizedValue() );
        assertTrue( Arrays.equals( BYTES2, cbv.getNormalizedValue() ) );
        
        cbv.normalize( BINARY_NORMALIZER );
        byte[] copy = cbv.getNormalizedValueCopy();
        assertTrue( Arrays.equals( BYTES1, copy ) );
        cbv.getNormalizedValueReference()[0]=0x11;
        assertTrue( Arrays.equals( BYTES1, copy ) );
    }


    @Test
    public void testNormalizeNormalizer() throws LdapException
    {
        BinaryValue cbv = new BinaryValue();
        
        cbv.normalize( BINARY_NORMALIZER );
        assertTrue( cbv.isNormalized() );
        assertEquals( null, cbv.getNormalizedValue() );
        
        cbv.set( StringTools.EMPTY_BYTES );
        cbv.normalize( BINARY_NORMALIZER );
        assertTrue( cbv.isNormalized() );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, cbv.getBytes() ) );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, cbv.getNormalizedValue() ) );
        
        cbv.set( BYTES1 );
        cbv.normalize( BINARY_NORMALIZER );
        assertTrue( cbv.isNormalized() );
        assertTrue( Arrays.equals( BYTES1, cbv.getBytes() ) );
        assertTrue( Arrays.equals( BYTES1, cbv.getNormalizedValue() ) );

        cbv.set( BYTES2 );
        cbv.normalize( BINARY_NORMALIZER );
        assertTrue( cbv.isNormalized() );
        assertTrue( Arrays.equals( BYTES2, cbv.getBytes() ) );
        assertTrue( Arrays.equals( BYTES1, cbv.getNormalizedValue() ) );
    }


    @Test
    public void testCompareToValueOfbyte() throws LdapException
    {
        BinaryValue cbv1 = new BinaryValue();
        BinaryValue cbv2 = new BinaryValue();
        
        assertEquals( 0, cbv1.compareTo( cbv2 ) );
        
        cbv1.set(  BYTES1 );
        assertEquals( 1, cbv1.compareTo( cbv2 ) );

        cbv2.set(  BYTES2 );
        assertEquals( 1, cbv1.compareTo( cbv2 ) );
        
        cbv2.normalize( BINARY_NORMALIZER );
        assertEquals( 0, cbv1.compareTo( cbv2 ) );
        
        cbv1.set( BYTES2 );
        assertEquals( -1, cbv1.compareTo( cbv2 ) );
    }


    @Test
    public void testEquals() throws LdapException
    {
        BinaryValue cbv1 = new BinaryValue();
        BinaryValue cbv2 = new BinaryValue();
        
        assertEquals( cbv1, cbv2 );
        
        cbv1.set(  BYTES1 );
        assertNotSame( cbv1, cbv2 );

        cbv2.set(  BYTES2 );
        assertNotSame( cbv1, cbv2 );
        
        cbv2.normalize( BINARY_NORMALIZER );
        assertEquals( cbv1, cbv2 );
        
        cbv1.set( BYTES2 );
        assertNotSame( cbv1, cbv2 );
    }


    @Test
    public void testClone()
    {
        BinaryValue cbv = new BinaryValue();
        BinaryValue copy = cbv.clone();
        
        assertEquals( cbv, copy );
        
        cbv.set( BYTES1 );
        assertNotSame( cbv, copy );
        
        copy = cbv.clone();
        assertEquals( cbv, copy );

        cbv.getReference()[0] = 0x11;
        
        assertTrue( Arrays.equals( BYTES_MOD, cbv.getBytes() ) );
        assertTrue( Arrays.equals( BYTES1, copy.getBytes() ) );
    }


    @Test
    public void testGetCopy()
    {
        BinaryValue cbv = new BinaryValue();
        
        assertNull( cbv.getCopy() );
        
        cbv.set( StringTools.EMPTY_BYTES );
        assertNotNull( cbv.getCopy() );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, cbv.getCopy() ) );
        
        cbv.set( BYTES1 );
        byte[] copy = cbv.getCopy();
        
        assertTrue( Arrays.equals( BYTES1, copy ) );

        cbv.getReference()[0] = 0x11;
        assertTrue( Arrays.equals( BYTES1, copy ) );
        assertTrue( Arrays.equals( BYTES_MOD, cbv.getBytes() ) );
    }


    @Test
    public void testCompareTo() throws LdapException
    {
        BinaryValue cbv1 = new BinaryValue();
        BinaryValue cbv2 = new BinaryValue();
        
        assertEquals( 0, cbv1.compareTo( cbv2 ) );
        
        cbv1.set( BYTES1 );
        assertEquals( 1, cbv1.compareTo( cbv2 ) );
        assertEquals( -1, cbv2.compareTo( cbv1 ) );
        
        cbv2.set( BYTES1 );
        assertEquals( 0, cbv1.compareTo( cbv2 ) );

        // Now check that the equals method works on normalized values.
        cbv1.set( BYTES2 );
        cbv2.set( BYTES1 );
        cbv1.normalize( BINARY_NORMALIZER );
        assertEquals( 0, cbv1.compareTo( cbv2 ) );
        
        cbv1.set( BYTES1 );
        cbv2.set( BYTES2 );
        assertEquals( 1, cbv1.compareTo( cbv2 ) );

        cbv1.set( BYTES2 );
        cbv2.set( BYTES1 );
        assertEquals( -1, cbv1.compareTo( cbv2 ) );
    }


    @Test
    public void testToString()
    {
        BinaryValue cbv = new BinaryValue();
        
        assertEquals( "null", cbv.toString() );

        cbv.set( StringTools.EMPTY_BYTES );
        assertEquals( "''", cbv.toString() );

        cbv.set( BYTES1 );
        assertEquals( "'0x01 0x02 0x03 0x04 '", cbv.toString() );
    }


    @Test
    public void testGetReference()
    {
        BinaryValue cbv = new BinaryValue();
        
        assertNull( cbv.getReference() );
        
        cbv.set( StringTools.EMPTY_BYTES );
        assertNotNull( cbv.getReference() );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, cbv.getReference() ) );
        
        cbv.set( BYTES1 );
        byte[] reference = cbv.getReference();
        
        assertTrue( Arrays.equals( BYTES1, reference ) );

        cbv.getReference()[0] = 0x11;
        assertTrue( Arrays.equals( BYTES_MOD, reference ) );
        assertTrue( Arrays.equals( BYTES_MOD, cbv.getBytes() ) );
    }


    @Test
    public void testGet()
    {
        BinaryValue cbv = new BinaryValue();
        
        assertNull( cbv.get() );
        
        cbv.set( StringTools.EMPTY_BYTES );
        assertNotNull( cbv.get() );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, cbv.getBytes() ) );
        
        cbv.set( BYTES1 );
        byte[] get = cbv.getBytes();
        
        assertTrue( Arrays.equals( BYTES1, get ) );

        cbv.getReference()[0] = 0x11;
        assertTrue( Arrays.equals( BYTES1, get ) );
        assertTrue( Arrays.equals( BYTES_MOD, cbv.getBytes() ) );
    }


    @Test
    public void testGetNormalizedValue() throws LdapException
    {
        BinaryValue cbv = new BinaryValue();
        
        assertFalse( cbv.isNormalized() );

        cbv.normalize( BINARY_NORMALIZER );
        byte[] value = cbv.getNormalizedValue();
        assertNull( value );
        assertTrue( cbv.isNormalized() );
        
        cbv.set( BYTES2 );
        cbv.normalize( BINARY_NORMALIZER );
        value = cbv.getNormalizedValue();
        assertTrue( Arrays.equals( BYTES1, value ) );
        cbv.getNormalizedValueReference()[0]=0x11;
        assertFalse( Arrays.equals( BYTES_MOD, value ) );
    }


    @Test
    public void testGetNormalizedValueReference() throws LdapException
    {
        BinaryValue cbv = new BinaryValue();
        
        assertFalse( cbv.isNormalized() );

        cbv.normalize( BINARY_NORMALIZER );
        byte[] value = cbv.getNormalizedValueReference();
        assertNull( value );
        assertTrue( cbv.isNormalized() );
        
        cbv.set( BYTES2 );
        cbv.normalize( BINARY_NORMALIZER );
        value = cbv.getNormalizedValueReference();
        assertTrue( Arrays.equals( BYTES1, value ) );
        cbv.getNormalizedValueReference()[0]=0x11;
        assertTrue( Arrays.equals( BYTES_MOD, value ) );
    }


    @Test
    public void testIsNull()
    {
        BinaryValue cbv = new BinaryValue();
        
        assertTrue( cbv.isNull() );
        
        cbv.set( StringTools.EMPTY_BYTES );
        assertFalse( cbv.isNull() );
        
        cbv.set( BYTES1 );
        assertFalse( cbv.isNull() );
    }


    @Test
    public void testIsValid() throws LdapException
    {
        BinaryValue cbv = new BinaryValue();
        
        assertFalse( cbv.isValid() );
        cbv.isValid( BINARY_CHECKER );
        assertTrue( cbv.isValid() );
        
        cbv.set( StringTools.EMPTY_BYTES );
        assertFalse( cbv.isValid() );
        cbv.isValid( BINARY_CHECKER );
        assertTrue( cbv.isValid() );
        
        cbv.set( BYTES1 );
        assertFalse( cbv.isNull() );
        cbv.isValid( BINARY_CHECKER );
        assertTrue( cbv.isValid() );

        cbv.set( INVALID_BYTES );
        assertFalse( cbv.isNull() );
        cbv.isValid( BINARY_CHECKER );
        assertFalse( cbv.isValid() );
    }


    @Test
    public void testIsValidSyntaxChecker() throws LdapException
    {
        BinaryValue cbv = new BinaryValue();
        
        assertTrue( cbv.isValid( BINARY_CHECKER ) ) ;
        
        cbv.set( StringTools.EMPTY_BYTES );
        assertTrue( cbv.isValid( BINARY_CHECKER ) );
        
        cbv.set( BYTES1 );
        assertTrue( cbv.isValid( BINARY_CHECKER ) );

        cbv.set( INVALID_BYTES );
        assertFalse( cbv.isValid( BINARY_CHECKER ) );
    }


    @Test
    public void testNormalize() throws LdapException
    {
        BinaryValue cbv = new BinaryValue();
        
        cbv.normalize();
        assertTrue( cbv.isNormalized() );
        assertEquals( null, cbv.getNormalizedValue() );
        
        cbv.set( StringTools.EMPTY_BYTES );
        cbv.normalize();
        assertTrue( cbv.isNormalized() );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, cbv.getNormalizedValue() ) );
        
        cbv.set( BYTES2 );
        cbv.normalize();
        assertTrue( cbv.isNormalized() );
        assertTrue( Arrays.equals( BYTES2, cbv.getNormalizedValue() ) );
    }


    @Test
    public void testSet() throws LdapException
    {
        BinaryValue cbv = new BinaryValue();
        
        cbv.set( null );
        assertNull( cbv.get() );
        assertFalse( cbv.isNormalized() );
        assertTrue( cbv.isValid( BINARY_CHECKER ) );
        assertTrue( cbv.isNull() );

        cbv.set( StringTools.EMPTY_BYTES );
        assertNotNull( cbv.get() );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, cbv.getBytes() ) );
        assertFalse( cbv.isNormalized() );
        assertTrue( cbv.isValid( BINARY_CHECKER ) );
        assertFalse( cbv.isNull() );

        cbv.set( BYTES1 );
        assertNotNull( cbv.get() );
        assertTrue( Arrays.equals( BYTES1, cbv.getBytes() ) );
        assertFalse( cbv.isNormalized() );
        assertTrue( cbv.isValid( BINARY_CHECKER ) );
        assertFalse( cbv.isNull() );
    }


    @Test
    public void testIsNormalized() throws LdapException
    {
        BinaryValue cbv = new BinaryValue();
        
        assertFalse( cbv.isNormalized() );
        
        cbv.set( BYTES2 );
        assertFalse( cbv.isNormalized() );
        
        cbv.normalize( BINARY_NORMALIZER );
        
        assertTrue( Arrays.equals( BYTES1, cbv.getNormalizedValue() ) );
        assertTrue( cbv.isNormalized() );
        
        cbv.set( BYTES2 );
        assertTrue( cbv.isNormalized() );

        cbv.set( BYTES_MOD );
        assertFalse( cbv.isNormalized() );
    }


    @Test
    public void testSetNormalized() throws LdapException
    {
        BinaryValue cbv = new BinaryValue();
        
        assertFalse( cbv.isNormalized() );
        
        cbv.setNormalized( true );
        assertTrue( cbv.isNormalized() );
        
        cbv.set( BYTES2 );
        assertFalse( cbv.isNormalized() );
        
        cbv.normalize( BINARY_NORMALIZER );
        
        assertTrue( Arrays.equals( BYTES1, cbv.getNormalizedValue() ) );
        assertTrue( cbv.isNormalized() );
        
        cbv.setNormalized( false );
        assertTrue( Arrays.equals( BYTES2, cbv.getNormalizedValue() ) );
        assertTrue( cbv.isNormalized() );

        cbv.normalize( BINARY_NORMALIZER );
    }
    
    
    /**
     * Test the serialization of a CBV with a value and a normalized value
     */
    @Test
    public void testSerializeStandard() throws LdapException, IOException, ClassNotFoundException
    {
        BinaryValue cbv = new BinaryValue();
        cbv.setNormalized( true );
        cbv.set( BYTES2 );
        cbv.normalize( BINARY_NORMALIZER );
        cbv.isValid( BINARY_CHECKER );

        BinaryValue cbvSer = deserializeValue( serializeValue( cbv ) );
         assertNotSame( cbv, cbvSer );
         assertTrue( Arrays.equals( cbv.getReference(), cbvSer.getReference() ) );
         assertTrue( Arrays.equals( cbv.getNormalizedValueReference(), cbvSer.getNormalizedValueReference() ) );
         assertTrue( cbvSer.isNormalized() );
         assertFalse( cbvSer.isValid() );
    }
    
    
    /**
     * Test the serialization of a CBV with a value and no normalized value
     */
    @Test
    public void testSerializeNotNormalized() throws LdapException, IOException, ClassNotFoundException
    {
        BinaryValue cbv = new BinaryValue();
        cbv.setNormalized( false );
        cbv.set( BYTES2 );
        cbv.isValid( BINARY_CHECKER );

        BinaryValue cbvSer = deserializeValue( serializeValue( cbv ) );
         assertNotSame( cbv, cbvSer );
         assertTrue( Arrays.equals( cbv.getReference(), cbvSer.getReference() ) );
         assertTrue( Arrays.equals( cbv.getReference(), cbvSer.getNormalizedValueReference() ) );
         assertTrue( cbvSer.isNormalized() );
         assertFalse( cbvSer.isValid() );
    }
    
    
    /**
     * Test the serialization of a CBV with a value and an empty normalized value
     */
    @Test
    public void testSerializeEmptyNormalized() throws LdapException, IOException, ClassNotFoundException
    {
        BinaryValue cbv = new BinaryValue();
        cbv.setNormalized( true );
        cbv.set( BYTES2 );
        cbv.isValid( BINARY_CHECKER );
        cbv.normalize( BINARY_NORMALIZER_EMPTY );

        BinaryValue cbvSer = deserializeValue( serializeValue( cbv ) );
         assertNotSame( cbv, cbvSer );
         assertTrue( Arrays.equals( cbv.getReference(), cbvSer.getReference() ) );
         assertTrue( Arrays.equals( cbv.getNormalizedValueReference(), cbvSer.getNormalizedValueReference() ) );
         assertTrue( cbvSer.isNormalized() );
         assertFalse( cbvSer.isValid() );
    }
    
    
    /**
     * Test the serialization of a CBV with a null value
     */
    @Test
    public void testSerializeNullValue() throws LdapException, IOException, ClassNotFoundException
    {
        BinaryValue cbv = new BinaryValue();
        cbv.setNormalized( true );
        cbv.set( null );
        cbv.isValid( BINARY_CHECKER );
        cbv.normalize( BINARY_NORMALIZER );

        BinaryValue cbvSer = deserializeValue( serializeValue( cbv ) );
         assertNotSame( cbv, cbvSer );
         assertTrue( Arrays.equals( cbv.getReference(), cbvSer.getReference() ) );
         assertTrue( Arrays.equals( cbv.getNormalizedValueReference(), cbvSer.getNormalizedValueReference() ) );
         assertTrue( cbvSer.isNormalized() );
         assertFalse( cbvSer.isValid() );
    }
    
    
    /**
     * Test the serialization of a CBV with an empty value
     */
    @Test
    public void testSerializeEmptyValue() throws LdapException, IOException, ClassNotFoundException
    {
        BinaryValue cbv = new BinaryValue();
        cbv.setNormalized( true );
        cbv.set( StringTools.EMPTY_BYTES );
        cbv.isValid( BINARY_CHECKER );
        cbv.normalize( BINARY_NORMALIZER );

        BinaryValue cbvSer = deserializeValue( serializeValue( cbv ) );
         assertNotSame( cbv, cbvSer );
         assertTrue( Arrays.equals( cbv.getReference(), cbvSer.getReference() ) );
         assertTrue( Arrays.equals( cbv.getNormalizedValueReference(), cbvSer.getNormalizedValueReference() ) );
         assertTrue( cbvSer.isNormalized() );
         assertFalse( cbvSer.isValid() );
    }
    
    
    /**
     * Test the serialization of a CBV with an empty value not normalized
     */
    @Test
    public void testSerializeEmptyValueNotNormalized() throws LdapException, IOException, ClassNotFoundException
    {
        BinaryValue cbv = new BinaryValue();
        cbv.setNormalized( false );
        cbv.set( StringTools.EMPTY_BYTES );
        cbv.isValid( BINARY_CHECKER );

        BinaryValue cbvSer = deserializeValue( serializeValue( cbv ) );
         assertNotSame( cbv, cbvSer );
         assertTrue( Arrays.equals( cbv.getReference(), cbvSer.getReference() ) );
         assertTrue( Arrays.equals( cbv.getNormalizedValueReference(), cbvSer.getNormalizedValueReference() ) );
         assertTrue( cbvSer.isNormalized() );
         assertFalse( cbvSer.isValid() );
    }
}
