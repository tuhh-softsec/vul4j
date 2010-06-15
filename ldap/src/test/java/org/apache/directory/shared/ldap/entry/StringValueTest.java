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

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.schema.normalizers.DeepTrimToLowerNormalizer;
import org.apache.directory.shared.ldap.schema.syntaxCheckers.Ia5StringSyntaxChecker;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * Test the StringValue class
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class StringValueTest
{
    //----------------------------------------------------------------------------------
    // Helper method
    //----------------------------------------------------------------------------------
    /**
     * Serialize a StringValue
     */
    private ByteArrayOutputStream serializeValue( StringValue value ) throws IOException
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
     * Deserialize a StringValue
     */
    private StringValue deserializeValue( ByteArrayOutputStream out ) throws IOException, ClassNotFoundException
    {
        ObjectInputStream oIn = null;
        ByteArrayInputStream in = new ByteArrayInputStream( out.toByteArray() );

        try
        {
            oIn = new ObjectInputStream( in );

            StringValue value = ( StringValue ) oIn.readObject();

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


    //----------------------------------------------------------------------------------
    // Test the clone() method
    //----------------------------------------------------------------------------------
    /**
     * Test cloning an empty value
     */
    @Test
    public void testCloneEmptyValue() throws LdapException
    {
        StringValue sv = new StringValue();
        
        StringValue sv1 = (StringValue)sv.clone();
        
        assertEquals( sv, sv1 );
        
        StringValue sv2 = new StringValue( "" );
        
        assertNotSame( sv2, sv1 );
        assertNull( sv1.get() );
        assertEquals( "", sv2.getString() );
    }


    /**
     * Test cloning a value
     */
    @Test
    public void testCloneValue() throws LdapException
    {
        StringValue sv = new StringValue( "  This is    a   TEST  " );
        
        StringValue sv1 = (StringValue)sv.clone();
        
        sv1 = sv.clone();
        
        assertEquals( sv, sv1 );
        assertEquals( "  This is    a   TEST  ", sv.getString() );

        sv.normalize( new DeepTrimToLowerNormalizer( "1.1.1" ) );
        
        assertNotSame( sv, sv1 );
        assertEquals( "  This is    a   TEST  ", sv1.getString() );
        assertEquals( "  This is    a   TEST  ", sv1.getNormalizedValue() );
        assertEquals( "  This is    a   TEST  ", sv.getString() );
        assertEquals( "this is a test", sv.getNormalizedValue() );
    }


    /**
     * Test method for {@link org.apache.directory.shared.ldap.entry.StringValue#hashCode()}.
     */
    @Test
    public void testHashCode()
    {
        StringValue csv = new StringValue( "test" );
        
        int hash = "test".hashCode();
        assertEquals( hash, csv.hashCode() );
        
        csv = new StringValue();
        hash = "".hashCode();
        assertEquals( hash, csv.hashCode() );
    }


    /**
     * Test method for {@link org.apache.directory.shared.ldap.entry.StringValue#ClientStringValue()}.
     */
    @Test
    public void testClientStringValueNull() throws LdapException
    {
        StringValue csv = new StringValue();
        
        assertNull( csv.get() );
        assertFalse( csv.isNormalized() );
        assertTrue( csv.isValid( new Ia5StringSyntaxChecker() ) );
        assertTrue( csv.isNull() );
        assertNull( csv.getNormalizedValue() );
    }


    /**
     * Test method for {@link org.apache.directory.shared.ldap.entry.StringValue#ClientStringValue(java.lang.String)}.
     */
    @Test
    public void testClientStringValueEmpty() throws LdapException
    {
        StringValue csv = new StringValue( "" );
        
        assertNotNull( csv.get() );
        assertEquals( "", csv.getString() );
        assertFalse( csv.isNormalized() );
        assertTrue( csv.isValid( new Ia5StringSyntaxChecker() ) );
        assertFalse( csv.isNull() );
        assertNotNull( csv.getNormalizedValue() );
        assertEquals( "", csv.getNormalizedValue() );
    }


    /**
     * Test method for {@link org.apache.directory.shared.ldap.entry.StringValue#ClientStringValue(java.lang.String)}.
     */
    @Test
    public void testClientStringValueString() throws LdapException
    {
        StringValue csv = new StringValue( "test" );
        
        assertEquals( "test", csv.get() );
        assertFalse( csv.isNormalized() );
        assertTrue( csv.isValid( new Ia5StringSyntaxChecker() ) );
        assertFalse( csv.isNull() );
        assertNotNull( csv.getNormalizedValue() );
        assertEquals( "test", csv.getNormalizedValue() );
    }


    /**
     * Test method for {@link org.apache.directory.shared.ldap.entry.StringValue#get()}.
     */
    @Test
    public void testGet()
    {
        StringValue sv = new StringValue( "test" );
        assertEquals( "test", sv.get() );
        
        StringValue sv2 = new StringValue( "" );
        assertEquals( "", sv2.get() );
    }


    /**
     * Test method for {@link org.apache.directory.shared.ldap.entry.StringValue#getCopy()}.
     */
    @Test
    public void testGetCopy()
    {
        StringValue sv = new StringValue( "test" );
        
        assertEquals( "test", sv.get() );
        
        StringValue sv2 = new StringValue( "" );
        assertEquals( "", sv2.get() );
    }


    /**
     * Test method for {@link org.apache.directory.shared.ldap.entry.StringValue#set(java.lang.String)}.
     */
    @Test
    public void testSet() throws LdapException
    {
        StringValue sv = new StringValue( (String)null );
        
        assertNull( sv.get() );
        assertFalse( sv.isNormalized() );
        assertTrue( sv.isValid( new Ia5StringSyntaxChecker() ) );
        assertTrue( sv.isNull() );

        sv = new StringValue( "" );
        assertNotNull( sv.get() );
        assertEquals( "", sv.get() );
        assertFalse( sv.isNormalized() );
        assertTrue( sv.isValid( new Ia5StringSyntaxChecker() ) );
        assertFalse( sv.isNull() );

        sv = new StringValue( "Test" );
        assertNotNull( sv.get() );
        assertEquals( "Test", sv.get() );
        assertFalse( sv.isNormalized() );
        assertTrue( sv.isValid( new Ia5StringSyntaxChecker() ) );
        assertFalse( sv.isNull() );
    }


    /**
     * Test method for {@link org.apache.directory.shared.ldap.entry.StringValue#isNull()}.
     */
    @Test
    public void testIsNull()
    {
        StringValue sv = new StringValue( (String)null );
        assertTrue( sv.isNull() );
        
        sv = new StringValue( "test" );
        assertFalse( sv.isNull() );
    }

    
    /**
     * Test method for {@link org.apache.directory.shared.ldap.entry.StringValue#isNormalized()}.
     */
    @Test
    public void testIsNormalized() throws LdapException
    {
        StringValue sv = new StringValue( "  This is    a   TEST  " );
        
        assertFalse( sv.isNormalized() );
        
        sv.normalize( new DeepTrimToLowerNormalizer( "1.1.1" ) );
        
        assertEquals( "this is a test", sv.getNormalizedValue() );
        assertTrue( sv.isNormalized() );
        
        sv = new StringValue( "test" );
        assertFalse( sv.isNormalized() );
    }


    /**
     * Test method for {@link org.apache.directory.shared.ldap.entry.StringValue#setNormalized(boolean)}.
     */
    @Test
    public void testSetNormalized() throws LdapException
    {
        StringValue sv = new StringValue();
        
        assertFalse( sv.isNormalized() );
        
        sv.setNormalized( true );
        assertTrue( sv.isNormalized() );
        
        sv = new StringValue( "  This is    a   TEST  " );
        assertFalse( sv.isNormalized() );
        
        sv.normalize( new DeepTrimToLowerNormalizer( "1.1.1" ) );
        
        assertEquals( "this is a test", sv.getNormalizedValue() );
        assertTrue( sv.isNormalized() );
        
        sv.setNormalized( false );
        assertEquals( "this is a test", sv.getNormalizedValue() );
        assertFalse( sv.isNormalized() );

        sv.normalize( new DeepTrimToLowerNormalizer( "1.1.1" ) );
    }


    /**
     * Test method for {@link org.apache.directory.shared.ldap.entry.StringValue#getNormalizedValue()}.
     */
    @Test
    public void testGetNormalizedValue() throws LdapException
    {
        StringValue sv = new StringValue();
        
        assertEquals( null, sv.getNormalizedValue() );
        
        sv = new StringValue( "  This is    a   TEST  " );
        assertEquals( "  This is    a   TEST  ", sv.getNormalizedValue() );
        
        sv.normalize( new DeepTrimToLowerNormalizer( "1.1.1" ) );
        
        assertEquals( "this is a test", sv.getNormalizedValue() );
    }


    /**
     * Test method for {@link org.apache.directory.shared.ldap.entry.StringValue#getNormalizedValue()}.
     */
    @Test
    public void getNormalizedValueCopy() throws LdapException
    {
        StringValue sv = new StringValue();
        
        assertEquals( null, sv.getNormalizedValue() );
        
        sv = new StringValue( "  This is    a   TEST  " );
        assertEquals( "  This is    a   TEST  ", sv.getNormalizedValue() );
        
        sv.normalize( new DeepTrimToLowerNormalizer( "1.1.1" ) );
        
        assertEquals( "this is a test", sv.getNormalizedValue() );
    }

    
    /**
     * Test method for {@link org.apache.directory.shared.ldap.entry.StringValue#normalize(org.apache.directory.shared.ldap.schema.Normalizer)}.
     */
    @Test
    public void testNormalize() throws LdapException
    {
        StringValue sv = new StringValue();

        sv.normalize( new DeepTrimToLowerNormalizer( "1.1.1" ) );
        assertEquals( null, sv.getNormalizedValue() );
        
        sv = new StringValue( "" );
        sv.normalize( new DeepTrimToLowerNormalizer( "1.1.1" ) );
        assertEquals( "", sv.getNormalizedValue() );

        sv = new StringValue(  "  This is    a   TEST  " );
        assertEquals( "  This is    a   TEST  ", sv.getNormalizedValue() );
        
        sv.normalize( new DeepTrimToLowerNormalizer( "1.1.1" ) );
        
        assertEquals( "this is a test", sv.getNormalizedValue() );
    }


    /**
     * Test method for {@link org.apache.directory.shared.ldap.entry.StringValue#isValid(org.apache.directory.shared.ldap.schema.SyntaxChecker)}.
     */
    @Test
    public void testIsValid() throws LdapException
    {
        StringValue sv = new StringValue( "Test" );
        
        assertTrue( sv.isValid( new Ia5StringSyntaxChecker() ) );
        
        sv = new StringValue( "é" );
        assertFalse( sv.isValid( new Ia5StringSyntaxChecker() ) );
    }


    /**
     * Test method for {@link org.apache.directory.shared.ldap.entry.StringValue#compareTo(org.apache.directory.shared.ldap.entry.Value)}.
     */
    @Test
    public void testCompareTo() throws LdapException
    {
        StringValue sv1 = new StringValue();
        StringValue sv2 = new StringValue();
        
        assertEquals( 0, sv1.compareTo( sv2 ) );
        
        sv1 = new StringValue( "Test" );
        assertEquals( 1, sv1.compareTo( sv2 ) );
        assertEquals( -1, sv2.compareTo( sv1 ) );
        
        sv2 = new StringValue( "Test" );
        assertEquals( 0, sv1.compareTo( sv2 ) );

        // Now check that the equals method works on normalized values.
        sv1 = new StringValue(  "  This is    a TEST   " );
        sv2 = new StringValue( "this is a test" );
        sv1.normalize( new DeepTrimToLowerNormalizer( "1.1.1" ) );
        assertEquals( 0, sv1.compareTo( sv2 ) );
        
        sv1 = new StringValue( "a" );
        sv2 = new StringValue( "b" );
        assertEquals( -1, sv1.compareTo( sv2 ) );

        sv1 = new StringValue( "b" );
        sv2 = new StringValue( "a" );
        assertEquals( 1, sv1.compareTo( sv2 ) );
    }


    /**
     * Test method for {@link org.apache.directory.shared.ldap.entry.StringValue#equals(java.lang.Object)}.
     */
    @Test
    public void testEquals() throws LdapException
    {
        StringValue sv1 = new StringValue();
        StringValue sv2 = new StringValue();
        
        assertEquals( sv1, sv2 );
        
        sv1 = new StringValue( "Test" );
        assertNotSame( sv1, sv2 );
        
        sv2 = new StringValue( "Test" );
        assertEquals( sv1, sv2 );

        // Now check that the equals method works on normalized values.
        sv1 = new StringValue( "  This is    a TEST   " );
        sv2 = new StringValue( "this is a test" );
        sv1.normalize( new DeepTrimToLowerNormalizer( "1.1.1" ) );
        assertEquals( sv1, sv2 );
    }


    /**
     * Test method for {@link org.apache.directory.shared.ldap.entry.StringValue#toString()}.
     */
    @Test
    public void testToString()
    {
        StringValue sv = new StringValue();
        
        assertEquals( "null", sv.toString() );

        sv = new StringValue( "" );
        assertEquals( "", sv.toString() );

        sv = new StringValue( "Test" );
        assertEquals( "Test", sv.toString() );
    }
    
    
    /**
     * Test the serialization of a CSV with a value and a normalized value
     */
    @Test
    public void testSerializeStandard() throws LdapException, IOException, ClassNotFoundException
    {
        StringValue csv = new StringValue( "TEST");
        csv.setNormalized( true );
        csv.normalize( new DeepTrimToLowerNormalizer( "1.1.1" ) );
        csv.isValid( new Ia5StringSyntaxChecker() );

        StringValue csvSer = deserializeValue( serializeValue( csv ) );
        assertNotSame( csv, csvSer );
        assertEquals( csv.get(), csvSer.get() );
        assertEquals( csv.getNormalizedValue(), csvSer.getNormalizedValue() );
        assertTrue( csvSer.isNormalized() );
        assertFalse( csvSer.isValid() );
    }
    
    
    /**
     * Test the serialization of a CSV with a value and no normalized value
     */
    @Test
    public void testSerializeNotNormalized() throws LdapException, IOException, ClassNotFoundException
    {
        StringValue csv = new StringValue( "Test" );
        csv.setNormalized( false );
        csv.isValid( new Ia5StringSyntaxChecker() );

        StringValue csvSer = deserializeValue( serializeValue( csv ) );
         assertNotSame( csv, csvSer );
         assertEquals( csv.get(), csvSer.get() );
         assertEquals( csv.get(), csvSer.getNormalizedValue() );
         assertFalse( csvSer.isNormalized() );
         assertFalse( csvSer.isValid() );
    }
    
    
    /**
     * Test the serialization of a CSV with a value and an empty normalized value
     */
    @Test
    public void testSerializeEmptyNormalized() throws LdapException, IOException, ClassNotFoundException
    {
        StringValue csv = new StringValue( "  " );
        csv.setNormalized( true );
        csv.isValid( new Ia5StringSyntaxChecker() );
        csv.normalize( new DeepTrimToLowerNormalizer( "1.1.1" ) );

        StringValue csvSer = deserializeValue( serializeValue( csv ) );
         assertNotSame( csv, csvSer );
         assertEquals( csv.get(), csvSer.get() );
         assertEquals( csv.getNormalizedValue(), csvSer.getNormalizedValue() );
         assertTrue( csvSer.isNormalized() );
         assertFalse( csvSer.isValid() );
    }
    
    
    /**
     * Test the serialization of a CSV with a null value
     */
    @Test
    public void testSerializeNullValue() throws LdapException, IOException, ClassNotFoundException
    {
        StringValue csv = new StringValue( (String)null );
        csv.setNormalized( true );
        csv.isValid( new Ia5StringSyntaxChecker() );
        csv.normalize( new DeepTrimToLowerNormalizer( "1.1.1" ) );

        StringValue csvSer = deserializeValue( serializeValue( csv ) );
         assertNotSame( csv, csvSer );
         assertEquals( csv.get(), csvSer.get() );
         assertEquals( csv.getNormalizedValue(), csvSer.getNormalizedValue() );
         assertTrue( csvSer.isNormalized() );
         assertFalse( csvSer.isValid() );
    }
    
    
    /**
     * Test the serialization of a CSV with an empty value
     */
    @Test
    public void testSerializeEmptyValue() throws LdapException, IOException, ClassNotFoundException
    {
        StringValue csv = new StringValue( "" );
        csv.setNormalized( true );
        csv.isValid( new Ia5StringSyntaxChecker() );
        csv.normalize( new DeepTrimToLowerNormalizer( "1.1.1" ) );

        StringValue csvSer = deserializeValue( serializeValue( csv ) );
         assertNotSame( csv, csvSer );
         assertEquals( csv.get(), csvSer.get() );
         assertEquals( csv.getNormalizedValue(), csvSer.getNormalizedValue() );
         assertTrue( csvSer.isNormalized() );
         assertFalse( csvSer.isValid() );
    }
    
    
    /**
     * Test the serialization of a CSV with an empty value not normalized
     */
    @Test
    public void testSerializeEmptyValueNotNormalized() throws LdapException, IOException, ClassNotFoundException
    {
        StringValue csv = new StringValue( "" );
        csv.setNormalized( false );
        csv.isValid( new Ia5StringSyntaxChecker() );

        StringValue csvSer = deserializeValue( serializeValue( csv ) );
         assertNotSame( csv, csvSer );
         assertEquals( csv.get(), csvSer.get() );
         assertEquals( csv.getNormalizedValue(), csvSer.getNormalizedValue() );
         assertFalse( csvSer.isNormalized() );
         assertFalse( csvSer.isValid() );
    }
}
