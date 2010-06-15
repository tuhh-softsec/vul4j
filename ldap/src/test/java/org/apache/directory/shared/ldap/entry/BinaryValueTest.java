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

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.entry.BinaryValue;
import org.apache.directory.shared.ldap.entry.Value;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.schema.Normalizer;
import org.apache.directory.shared.ldap.schema.SyntaxChecker;
import org.apache.directory.shared.ldap.util.StringTools;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * Test the BinaryValue class
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
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
        BinaryValue bv = new BinaryValue();
        assertEquals( 0, bv.hashCode() );
        
        bv = new BinaryValue( StringTools.EMPTY_BYTES );
        int h = Arrays.hashCode( StringTools.EMPTY_BYTES );
        assertEquals( h, bv.hashCode() );
        
        h = Arrays.hashCode( BYTES1 );
        bv = new BinaryValue( BYTES1 );
        assertEquals( h, bv.hashCode() );
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
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, cbv.get() ) );
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
        assertTrue( Arrays.equals( BYTES1, cbv.get() ) );
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
        BinaryValue bv = new BinaryValue();
        
        bv = new BinaryValue( BYTES1 );
        
        assertTrue( Arrays.equals( BYTES1, bv.getBytes() ) );
        assertTrue( Arrays.equals( BYTES1, bv.get() ) );
        assertTrue( Arrays.equals( BYTES1, bv.getReference() ) );
        assertFalse( bv.isNormalized() );
        assertTrue( bv.isValid( BINARY_CHECKER ) );
        assertFalse( bv.isNull() );
        assertNotNull( bv.getNormalizedValue() );
        assertTrue( Arrays.equals( BYTES1, bv.getNormalizedValue() ) );
    }


    @Test
    public void testGetNormalizedValueCopy()  throws LdapException
    {
        BinaryValue cbv = new BinaryValue( BYTES2 );
        
        assertTrue( Arrays.equals( BYTES2, cbv.getBytes() ) );
        assertTrue( Arrays.equals( BYTES2, cbv.get() ) );
        assertTrue( Arrays.equals( BYTES2, cbv.getReference() ) );
        assertFalse( cbv.isNormalized() );
        assertTrue( cbv.isValid( BINARY_CHECKER ) );
        assertFalse( cbv.isNull() );
        assertNotNull( cbv.getNormalizedValue() );
        assertTrue( Arrays.equals( BYTES2, cbv.getNormalizedValue() ) );
        
        cbv.normalize( BINARY_NORMALIZER );
        byte[] copy = cbv.getNormalizedValue();
        assertTrue( Arrays.equals( BYTES1, copy ) );
        cbv.getNormalizedValueReference()[0]=0x11;
        assertTrue( Arrays.equals( BYTES1, copy ) );
    }


    @Test
    public void testNormalizeNormalizer() throws LdapException
    {
        BinaryValue bv = new BinaryValue();
        
        bv.normalize( BINARY_NORMALIZER );
        assertTrue( bv.isNormalized() );
        assertEquals( null, bv.getNormalizedValue() );
        
        bv = new BinaryValue( StringTools.EMPTY_BYTES );
        bv.normalize( BINARY_NORMALIZER );
        assertTrue( bv.isNormalized() );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, bv.getBytes() ) );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, bv.getNormalizedValue() ) );
        
        bv = new BinaryValue( BYTES1 );
        bv.normalize( BINARY_NORMALIZER );
        assertTrue( bv.isNormalized() );
        assertTrue( Arrays.equals( BYTES1, bv.getBytes() ) );
        assertTrue( Arrays.equals( BYTES1, bv.getNormalizedValue() ) );

        bv = new BinaryValue( BYTES2 );
        bv.normalize( BINARY_NORMALIZER );
        assertTrue( bv.isNormalized() );
        assertTrue( Arrays.equals( BYTES2, bv.getBytes() ) );
        assertTrue( Arrays.equals( BYTES1, bv.getNormalizedValue() ) );
    }


    @Test
    public void testCompareToValueOfbyte() throws LdapException
    {
        BinaryValue bv1 = new BinaryValue();
        BinaryValue bv2 = new BinaryValue();
        
        assertEquals( 0, bv1.compareTo( bv2 ) );
        
        bv1 = new BinaryValue( BYTES1 );
        assertEquals( 1, bv1.compareTo( bv2 ) );

        bv2 = new BinaryValue( BYTES2 );
        assertEquals( 1, bv1.compareTo( bv2 ) );
        
        bv2.normalize( BINARY_NORMALIZER );
        assertEquals( 0, bv1.compareTo( bv2 ) );
        
        bv1 = new BinaryValue( BYTES2 );
        assertEquals( -1, bv1.compareTo( bv2 ) );
    }


    @Test
    public void testEquals() throws LdapException
    {
        BinaryValue bv1 = new BinaryValue();
        BinaryValue bv2 = new BinaryValue();
        
        assertEquals( bv1, bv2 );
        
        bv1 = new BinaryValue( BYTES1 );
        assertNotSame( bv1, bv2 );

        bv2 = new BinaryValue( BYTES2 );
        assertNotSame( bv1, bv2 );
        
        bv2.normalize( BINARY_NORMALIZER );
        assertEquals( bv1, bv2 );
        
        bv1 = new BinaryValue( BYTES2 );
        assertNotSame( bv1, bv2 );
    }


    @Test
    public void testClone()
    {
        BinaryValue bv = new BinaryValue();
        BinaryValue copy = bv.clone();
        
        assertEquals( bv, copy );
        
        bv = new BinaryValue( BYTES1 );
        assertNotSame( bv, copy );
        
        copy = bv.clone();
        assertEquals( bv, copy );

        bv.getReference()[0] = 0x11;
        
        assertTrue( Arrays.equals( BYTES_MOD, bv.getBytes() ) );
        assertTrue( Arrays.equals( BYTES1, copy.getBytes() ) );
    }


    @Test
    public void testGetCopy()
    {
        BinaryValue bv = new BinaryValue();
        
        assertNull( bv.get() );
        
        bv = new BinaryValue( StringTools.EMPTY_BYTES );
        assertNotNull( bv.get() );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, bv.get() ) );
        
        bv = new BinaryValue( BYTES1 );
        byte[] copy = bv.get();
        
        assertTrue( Arrays.equals( BYTES1, copy ) );

        bv.getReference()[0] = 0x11;
        assertTrue( Arrays.equals( BYTES1, copy ) );
        assertTrue( Arrays.equals( BYTES_MOD, bv.getBytes() ) );
    }


    @Test
    public void testCompareTo() throws LdapException
    {
        BinaryValue bv1 = new BinaryValue();
        BinaryValue bv2 = new BinaryValue();
        
        assertEquals( 0, bv1.compareTo( bv2 ) );
        
        bv1 = new BinaryValue( BYTES1 );
        assertEquals( 1, bv1.compareTo( bv2 ) );
        assertEquals( -1, bv2.compareTo( bv1 ) );
        
        bv2 = new BinaryValue( BYTES1 );
        assertEquals( 0, bv1.compareTo( bv2 ) );

        // Now check that the equals method works on normalized values.
        bv1 = new BinaryValue( BYTES2 );
        bv2 = new BinaryValue( BYTES1 );
        bv1.normalize( BINARY_NORMALIZER );
        assertEquals( 0, bv1.compareTo( bv2 ) );
        
        bv1 = new BinaryValue( BYTES1 );
        bv2 = new BinaryValue( BYTES2 );
        assertEquals( 1, bv1.compareTo( bv2 ) );

        bv1 = new BinaryValue( BYTES2 );
        bv2 = new BinaryValue( BYTES1 );
        assertEquals( -1, bv1.compareTo( bv2 ) );
    }


    @Test
    public void testToString()
    {
        BinaryValue bv = new BinaryValue();
        
        assertEquals( "null", bv.toString() );

        bv = new BinaryValue( StringTools.EMPTY_BYTES );
        assertEquals( "''", bv.toString() );

        bv = new BinaryValue( BYTES1 );
        assertEquals( "'0x01 0x02 0x03 0x04 '", bv.toString() );
    }


    @Test
    public void testGetReference()
    {
        BinaryValue bv = new BinaryValue();
        
        assertNull( bv.getReference() );
        
        bv = new BinaryValue( StringTools.EMPTY_BYTES );
        assertNotNull( bv.getReference() );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, bv.getReference() ) );
        
        bv = new BinaryValue( BYTES1 );
        byte[] reference = bv.getReference();
        
        assertTrue( Arrays.equals( BYTES1, reference ) );

        bv.getReference()[0] = 0x11;
        assertTrue( Arrays.equals( BYTES_MOD, reference ) );
        assertTrue( Arrays.equals( BYTES_MOD, bv.getBytes() ) );
    }


    @Test
    public void testGet()
    {
        BinaryValue bv = new BinaryValue();
        
        assertNull( bv.get() );
        
        bv = new BinaryValue( StringTools.EMPTY_BYTES );
        assertNotNull( bv.get() );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, bv.getBytes() ) );
        
        bv = new BinaryValue( BYTES1 );
        byte[] get = bv.getBytes();
        
        assertTrue( Arrays.equals( BYTES1, get ) );

        bv.getReference()[0] = 0x11;
        assertTrue( Arrays.equals( BYTES1, get ) );
        assertTrue( Arrays.equals( BYTES_MOD, bv.getBytes() ) );
    }


    @Test
    public void testGetNormalizedValue() throws LdapException
    {
        BinaryValue bv = new BinaryValue();
        
        assertFalse( bv.isNormalized() );

        bv.normalize( BINARY_NORMALIZER );
        byte[] value = bv.getNormalizedValue();
        assertNull( value );
        assertTrue( bv.isNormalized() );
        
        bv = new BinaryValue( BYTES2 );
        bv.normalize( BINARY_NORMALIZER );
        value = bv.getNormalizedValue();
        assertTrue( Arrays.equals( BYTES1, value ) );
        bv.getNormalizedValueReference()[0]=0x11;
        assertFalse( Arrays.equals( BYTES_MOD, value ) );
    }


    @Test
    public void testGetNormalizedValueReference() throws LdapException
    {
        BinaryValue bv = new BinaryValue();
        
        assertFalse( bv.isNormalized() );

        bv.normalize( BINARY_NORMALIZER );
        byte[] value = bv.getNormalizedValueReference();
        assertNull( value );
        assertTrue( bv.isNormalized() );
        
        bv = new BinaryValue( BYTES2 );
        bv.normalize( BINARY_NORMALIZER );
        value = bv.getNormalizedValueReference();
        assertTrue( Arrays.equals( BYTES1, value ) );
        bv.getNormalizedValueReference()[0]=0x11;
        assertTrue( Arrays.equals( BYTES_MOD, value ) );
    }


    @Test
    public void testIsNull()
    {
        BinaryValue bv = new BinaryValue();
        
        assertTrue( bv.isNull() );
        
        bv = new BinaryValue( StringTools.EMPTY_BYTES );
        assertFalse( bv.isNull() );
        
        bv = new BinaryValue( BYTES1 );
        assertFalse( bv.isNull() );
    }


    @Test
    public void testIsValid() throws LdapException
    {
        BinaryValue bv = new BinaryValue();
        
        assertFalse( bv.isValid() );
        bv.isValid( BINARY_CHECKER );
        assertTrue( bv.isValid() );
        
        bv = new BinaryValue( StringTools.EMPTY_BYTES );
        assertFalse( bv.isValid() );
        bv.isValid( BINARY_CHECKER );
        assertTrue( bv.isValid() );
        
        bv = new BinaryValue( BYTES1 );
        assertFalse( bv.isNull() );
        bv.isValid( BINARY_CHECKER );
        assertTrue( bv.isValid() );

        bv = new BinaryValue( INVALID_BYTES );
        assertFalse( bv.isNull() );
        bv.isValid( BINARY_CHECKER );
        assertFalse( bv.isValid() );
    }


    @Test
    public void testIsValidSyntaxChecker() throws LdapException
    {
        BinaryValue bv = new BinaryValue();
        
        assertTrue( bv.isValid( BINARY_CHECKER ) ) ;
        
        bv = new BinaryValue( StringTools.EMPTY_BYTES );
        assertTrue( bv.isValid( BINARY_CHECKER ) );
        
        bv = new BinaryValue( BYTES1 );
        assertTrue( bv.isValid( BINARY_CHECKER ) );

        bv = new BinaryValue( INVALID_BYTES );
        assertFalse( bv.isValid( BINARY_CHECKER ) );
    }


    @Test
    public void testNormalize() throws LdapException
    {
        BinaryValue bv = new BinaryValue();
        
        bv.normalize();
        assertTrue( bv.isNormalized() );
        assertEquals( null, bv.getNormalizedValue() );
        
        bv = new BinaryValue( StringTools.EMPTY_BYTES );
        bv.normalize();
        assertTrue( bv.isNormalized() );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, bv.getNormalizedValue() ) );
        
        bv = new BinaryValue( BYTES2 );
        bv.normalize();
        assertTrue( bv.isNormalized() );
        assertTrue( Arrays.equals( BYTES2, bv.getNormalizedValue() ) );
    }


    @Test
    public void testSet() throws LdapException
    {
        BinaryValue bv = new BinaryValue( (byte[])null );
        
        assertNull( bv.get() );
        assertFalse( bv.isNormalized() );
        assertTrue( bv.isValid( BINARY_CHECKER ) );
        assertTrue( bv.isNull() );

        bv = new BinaryValue( StringTools.EMPTY_BYTES );
        assertNotNull( bv.get() );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, bv.getBytes() ) );
        assertFalse( bv.isNormalized() );
        assertTrue( bv.isValid( BINARY_CHECKER ) );
        assertFalse( bv.isNull() );

        bv = new BinaryValue( BYTES1 );
        assertNotNull( bv.get() );
        assertTrue( Arrays.equals( BYTES1, bv.getBytes() ) );
        assertFalse( bv.isNormalized() );
        assertTrue( bv.isValid( BINARY_CHECKER ) );
        assertFalse( bv.isNull() );
    }


    @Test
    public void testIsNormalized() throws LdapException
    {
        BinaryValue bv = new BinaryValue();
        assertFalse( bv.isNormalized() );
        
        bv = new BinaryValue( BYTES2 );
        assertFalse( bv.isNormalized() );
        
        bv.normalize( BINARY_NORMALIZER );
        
        assertTrue( Arrays.equals( BYTES1, bv.getNormalizedValue() ) );
        assertTrue( bv.isNormalized() );
        
        bv = new BinaryValue( BYTES2 );
        assertFalse( bv.isNormalized() );

        bv = new BinaryValue( BYTES_MOD );
        assertFalse( bv.isNormalized() );
    }


    @Test
    public void testSetNormalized() throws LdapException
    {
        BinaryValue bv = new BinaryValue();
        
        assertFalse( bv.isNormalized() );
        
        bv.setNormalized( true );
        assertTrue( bv.isNormalized() );
        
        bv = new BinaryValue( BYTES2 );
        assertFalse( bv.isNormalized() );
        
        bv.normalize( BINARY_NORMALIZER );
        
        assertTrue( Arrays.equals( BYTES1, bv.getNormalizedValue() ) );
        assertTrue( bv.isNormalized() );
        
        bv.setNormalized( false );
        assertTrue( Arrays.equals( BYTES2, bv.getNormalizedValue() ) );
        assertTrue( bv.isNormalized() );

        bv.normalize( BINARY_NORMALIZER );
    }
    
    
    /**
     * Test the serialization of a CBV with a value and a normalized value
     */
    @Test
    public void testSerializeStandard() throws LdapException, IOException, ClassNotFoundException
    {
        BinaryValue bv = new BinaryValue();
        bv.setNormalized( true );
        bv = new BinaryValue( BYTES2 );
        bv.normalize( BINARY_NORMALIZER );
        bv.isValid( BINARY_CHECKER );

        BinaryValue cbvSer = deserializeValue( serializeValue( bv ) );
         assertNotSame( bv, cbvSer );
         assertTrue( Arrays.equals( bv.getReference(), cbvSer.getReference() ) );
         assertTrue( Arrays.equals( bv.getNormalizedValueReference(), cbvSer.getNormalizedValueReference() ) );
         assertTrue( cbvSer.isNormalized() );
         assertFalse( cbvSer.isValid() );
    }
    
    
    /**
     * Test the serialization of a CBV with a value and no normalized value
     */
    @Test
    public void testSerializeNotNormalized() throws LdapException, IOException, ClassNotFoundException
    {
        BinaryValue bv = new BinaryValue();
        bv.setNormalized( false );
        bv = new BinaryValue( BYTES2 );
        bv.isValid( BINARY_CHECKER );

        BinaryValue cbvSer = deserializeValue( serializeValue( bv ) );
         assertNotSame( bv, cbvSer );
         assertTrue( Arrays.equals( bv.getReference(), cbvSer.getReference() ) );
         assertTrue( Arrays.equals( bv.getReference(), cbvSer.getNormalizedValueReference() ) );
         assertTrue( cbvSer.isNormalized() );
         assertFalse( cbvSer.isValid() );
    }
    
    
    /**
     * Test the serialization of a CBV with a value and an empty normalized value
     */
    @Test
    public void testSerializeEmptyNormalized() throws LdapException, IOException, ClassNotFoundException
    {
        BinaryValue bv = new BinaryValue();
        bv.setNormalized( true );
        bv = new BinaryValue( BYTES2 );
        bv.isValid( BINARY_CHECKER );
        bv.normalize( BINARY_NORMALIZER_EMPTY );

        BinaryValue cbvSer = deserializeValue( serializeValue( bv ) );
         assertNotSame( bv, cbvSer );
         assertTrue( Arrays.equals( bv.getReference(), cbvSer.getReference() ) );
         assertTrue( Arrays.equals( bv.getNormalizedValueReference(), cbvSer.getNormalizedValueReference() ) );
         assertTrue( cbvSer.isNormalized() );
         assertFalse( cbvSer.isValid() );
    }
    
    
    /**
     * Test the serialization of a CBV with a null value
     */
    @Test
    public void testSerializeNullValue() throws LdapException, IOException, ClassNotFoundException
    {
        BinaryValue bv = new BinaryValue();
        bv.setNormalized( true );
        bv = new BinaryValue( (byte[])null );
        bv.isValid( BINARY_CHECKER );
        bv.normalize( BINARY_NORMALIZER );

        BinaryValue cbvSer = deserializeValue( serializeValue( bv ) );
         assertNotSame( bv, cbvSer );
         assertTrue( Arrays.equals( bv.getReference(), cbvSer.getReference() ) );
         assertTrue( Arrays.equals( bv.getNormalizedValueReference(), cbvSer.getNormalizedValueReference() ) );
         assertTrue( cbvSer.isNormalized() );
         assertFalse( cbvSer.isValid() );
    }
    
    
    /**
     * Test the serialization of a CBV with an empty value
     */
    @Test
    public void testSerializeEmptyValue() throws LdapException, IOException, ClassNotFoundException
    {
        BinaryValue bv = new BinaryValue();
        bv.setNormalized( true );
        bv = new BinaryValue( StringTools.EMPTY_BYTES );
        bv.isValid( BINARY_CHECKER );
        bv.normalize( BINARY_NORMALIZER );

        BinaryValue cbvSer = deserializeValue( serializeValue( bv ) );
         assertNotSame( bv, cbvSer );
         assertTrue( Arrays.equals( bv.getReference(), cbvSer.getReference() ) );
         assertTrue( Arrays.equals( bv.getNormalizedValueReference(), cbvSer.getNormalizedValueReference() ) );
         assertTrue( cbvSer.isNormalized() );
         assertFalse( cbvSer.isValid() );
    }
    
    
    /**
     * Test the serialization of a CBV with an empty value not normalized
     */
    @Test
    public void testSerializeEmptyValueNotNormalized() throws LdapException, IOException, ClassNotFoundException
    {
        BinaryValue bv = new BinaryValue();
        bv.setNormalized( false );
        bv = new BinaryValue( StringTools.EMPTY_BYTES );
        bv.isValid( BINARY_CHECKER );

        BinaryValue cbvSer = deserializeValue( serializeValue( bv ) );
         assertNotSame( bv, cbvSer );
         assertTrue( Arrays.equals( bv.getReference(), cbvSer.getReference() ) );
         assertTrue( Arrays.equals( bv.getNormalizedValueReference(), cbvSer.getNormalizedValueReference() ) );
         assertTrue( cbvSer.isNormalized() );
         assertFalse( cbvSer.isValid() );
    }
}
