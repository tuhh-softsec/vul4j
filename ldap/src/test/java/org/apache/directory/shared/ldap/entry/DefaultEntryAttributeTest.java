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

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Iterator;

import javax.naming.directory.InvalidAttributeValueException;

import org.apache.directory.shared.ldap.entry.BinaryValue;
import org.apache.directory.shared.ldap.entry.DefaultEntryAttribute;
import org.apache.directory.shared.ldap.entry.StringValue;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.apache.directory.shared.ldap.entry.Value;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.exception.LdapInvalidAttributeValueException;
import org.apache.directory.shared.ldap.schema.syntaxCheckers.Ia5StringSyntaxChecker;
import org.apache.directory.shared.ldap.util.StringTools;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test the DefaultEntryAttribute class
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DefaultEntryAttributeTest
{
    private static final Value<String> NULL_STRING_VALUE = new StringValue( (String)null );
    private static final Value<byte[]> NULL_BINARY_VALUE = new BinaryValue( (byte[])null );
    private static final byte[] BYTES1 = new byte[]{ 'a', 'b' };
    private static final byte[] BYTES2 = new byte[]{ 'b' };
    private static final byte[] BYTES3 = new byte[]{ 'c' };
    private static final byte[] BYTES4 = new byte[]{ 'd' };
    
    private static final StringValue STR_VALUE1 = new StringValue( "a" );
    private static final StringValue STR_VALUE2 = new StringValue( "b" );
    private static final StringValue STR_VALUE3 = new StringValue( "c" );
    private static final StringValue STR_VALUE4 = new StringValue( "d" );

    private static final BinaryValue BIN_VALUE1 = new BinaryValue( BYTES1 );
    private static final BinaryValue BIN_VALUE2 = new BinaryValue( BYTES2 );
    private static final BinaryValue BIN_VALUE3 = new BinaryValue( BYTES3 );
    private static final BinaryValue BIN_VALUE4 = new BinaryValue( BYTES4 );

    
    
    /**
     * Serialize a DefaultEntryAttribute
     */
    private ByteArrayOutputStream serializeValue( DefaultEntryAttribute value ) throws IOException
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
     * Deserialize a DefaultEntryAttribute
     */
    private DefaultEntryAttribute deserializeValue( ByteArrayOutputStream out ) throws IOException, ClassNotFoundException
    {
        ObjectInputStream oIn = null;
        ByteArrayInputStream in = new ByteArrayInputStream( out.toByteArray() );

        try
        {
            oIn = new ObjectInputStream( in );

            DefaultEntryAttribute value = ( DefaultEntryAttribute ) oIn.readObject();

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

    
    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
    }


    /**
     * Test method new DefaultEntryAttribute()
     */
    @Test
    public void testDefaultClientAttribute()
    {
        EntryAttribute attr = new DefaultEntryAttribute();
        
        assertFalse( attr.isHR() );
        assertEquals( 0, attr.size() );
        assertNull( attr.getId() );
        assertNull( attr.getUpId() );
    }


    /**
     * Test method new DefaultEntryAttribute( String )
     */
    @Test
    public void testDefaultClientAttributeString()
    {
        EntryAttribute attr = new DefaultEntryAttribute( "TEST" );
        
        assertFalse( attr.isHR() );
        assertEquals( 0, attr.size() );
        assertEquals( "test", attr.getId() );
        assertEquals( "TEST", attr.getUpId() );
    }


    /**
     * Test method new DefaultEntryAttribute( String, Value... )
     */
    @Test
    public void testDefaultClientAttributeStringValueArray()
    {
        EntryAttribute attr = new DefaultEntryAttribute( "Test", STR_VALUE1, STR_VALUE2 );
        
        assertTrue( attr.isHR() );
        assertEquals( 2, attr.size() );
        assertTrue( attr.contains( "a" ) );
        assertTrue( attr.contains( "b" ) );
        assertEquals( "test", attr.getId() );
        assertEquals( "Test", attr.getUpId() );
    }


    /**
     * Test method 
     */
    @Test
    public void testDefaultClientAttributeStringStringArray()
    {
        EntryAttribute attr = new DefaultEntryAttribute( "Test", "a", "b" );
        
        assertTrue( attr.isHR() );
        assertEquals( 2, attr.size() );
        assertTrue( attr.contains( "a" ) );
        assertTrue( attr.contains( "b" ) );
        assertEquals( "test", attr.getId() );
        assertEquals( "Test", attr.getUpId() );
    }


    /**
     * Test method 
     */
    @Test
    public void testDefaultClientAttributeStringBytesArray()
    {
        EntryAttribute attr = new DefaultEntryAttribute( "Test", BYTES1, BYTES2 );
        
        assertFalse( attr.isHR() );
        assertEquals( 2, attr.size() );
        assertTrue( attr.contains( BYTES1 ) );
        assertTrue( attr.contains( BYTES2 ) );
        assertEquals( "test", attr.getId() );
        assertEquals( "Test", attr.getUpId() );
    }


    /**
     * Test method getBytes()
     */
    @Test
    public void testGetBytes() throws InvalidAttributeValueException, LdapException
    {
        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );
        
        attr1.add( (byte[])null );
        assertNull( attr1.getBytes() );

        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );
        
        attr2.add( BYTES1, BYTES2 );
        assertTrue( Arrays.equals( BYTES1, attr2.getBytes() ) );
        
        EntryAttribute attr3 = new DefaultEntryAttribute( "test" );
        
        attr3.add( "a", "b" );
        
        try
        {
            attr3.getBytes();
            fail();
        }
        catch ( LdapInvalidAttributeValueException ivae )
        {
            assertTrue( true );
        }
    }


    /**
     * Test method getString()
     */
    @Test
    public void testGetString() throws InvalidAttributeValueException, LdapException
    {
        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );
        
        attr1.add( (String)null );
        assertEquals( "", attr1.getString() );

        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );
        
        attr2.add( "a", "b" );
        assertEquals( "a", attr2.getString() );
        
        EntryAttribute attr3 = new DefaultEntryAttribute( "test" );
        
        attr3.add( BYTES1, BYTES2 );
        
        try
        {
            attr3.getString();
            fail();
        }
        catch ( LdapInvalidAttributeValueException ivae )
        {
            assertTrue( true );
        }
    }


    /**
     * Test method getId()
     */
    @Test
    public void testGetId()
    {
        EntryAttribute attr = new DefaultEntryAttribute();

        assertNull( attr.getId() );
        
        attr.setId( "test" );
        assertEquals( "test", attr.getId() );
        
        attr.setId(  "  TEST  " );
        assertEquals( "test", attr.getId() );
    }


    /**
     * Test method SetId(String)
     */
    @Test
    public void testSetId()
    {
        EntryAttribute attr = new DefaultEntryAttribute();

        try
        {
            attr.setId( null );
            fail();
        }
        catch ( IllegalArgumentException iae )
        {
            assertTrue( true );
        }
        
        try
        {
            attr.setId( "" );
            fail();
        }
        catch ( IllegalArgumentException iae )
        {
            assertTrue( true );
        }
        
        try
        {
            attr.setId( "  " );
            fail();
        }
        catch ( IllegalArgumentException iae )
        {
            assertTrue( true );
        }
        
        attr.setId( "Test" );
        assertEquals( "test", attr.getId() );
        
        attr.setId( " Test " );
        assertEquals( "test", attr.getId() );
    }


    /**
     * Test method getUpId()
     */
    @Test
    public void testGetUpId()
    {
        EntryAttribute attr = new DefaultEntryAttribute();

        assertNull( attr.getUpId() );
        
        attr.setUpId( "test" );
        assertEquals( "test", attr.getUpId() );
        
        attr.setUpId(  "  TEST  " );
        assertEquals( "  TEST  ", attr.getUpId() );
    }


    /**
     * Test method setUpId(String)
     */
    @Test
    public void testSetUpId()
    {
        EntryAttribute attr = new DefaultEntryAttribute();

        try
        {
            attr.setUpId( null );
            fail();
        }
        catch ( IllegalArgumentException iae )
        {
            assertTrue( true );
        }
        
        try
        {
            attr.setUpId( "" );
            fail();
        }
        catch ( IllegalArgumentException iae )
        {
            assertTrue( true );
        }
        
        try
        {
            attr.setUpId( "  " );
            fail();
        }
        catch ( IllegalArgumentException iae )
        {
            assertTrue( true );
        }
        
        attr.setUpId( "Test" );
        assertEquals( "Test", attr.getUpId() );
        assertEquals( "test", attr.getId() );
        
        attr.setUpId( " Test " );
        assertEquals( " Test ", attr.getUpId() );
        assertEquals( "test", attr.getId() );
    }


    /**
     * Test method isValid( SyntaxChecker ) 
     */
    @Test
    public void testIsValidSyntaxChecker() throws LdapException
    {
        EntryAttribute attr = new DefaultEntryAttribute( "test" );
        
        attr.add( "test", "another test" );
        
        assertTrue( attr.isValid( new Ia5StringSyntaxChecker() ) );
        
        attr.add( "é" );
        assertFalse( attr.isValid( new Ia5StringSyntaxChecker() ) );
    }


    /**
     * Test method iterator()
     */
    @Test
    public void testIterator()
    {
        EntryAttribute attr = new DefaultEntryAttribute();
        attr.add(  "a", "b", "c" );
        
        Iterator<Value<?>> iter = attr.iterator();
        
        assertTrue( iter.hasNext() );
        
        String[] values = new String[]{ "a", "b", "c" };
        int pos = 0;
        
        for ( Value<?> val:attr )
        {
            assertTrue( val instanceof StringValue );
            assertEquals( values[pos++], val.getString() );
        }
    }


    /**
     * Test method add(Value...)
     */
    @Test
    public void testAddValueArray() throws InvalidAttributeValueException, LdapException
    {
        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );
        
        int nbAdded = attr1.add( new StringValue( (String)null ) );
        assertEquals( 1, nbAdded );
        assertTrue( attr1.isHR() );
        assertEquals( NULL_STRING_VALUE, attr1.get() );
        
        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr2.add( new BinaryValue( (byte[])null ) );
        assertEquals( 1, nbAdded );
        assertFalse( attr2.isHR() );
        assertEquals( NULL_BINARY_VALUE, attr2.get() );
        
        EntryAttribute attr3 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr3.add( new StringValue( "a" ), new StringValue( "b" ) );
        assertEquals( 2, nbAdded );
        assertTrue( attr3.isHR() );
        assertTrue( attr3.contains( "a" ) );
        assertTrue( attr3.contains( "b" ) );
        
        EntryAttribute attr4 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr4.add( new BinaryValue( BYTES1 ), new BinaryValue( BYTES2 ) );
        assertEquals( 2, nbAdded );
        assertFalse( attr4.isHR() );
        assertTrue( attr4.contains( BYTES1 ) );
        assertTrue( attr4.contains( BYTES2 ) );
        
        EntryAttribute attr5 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr5.add( new StringValue( "c" ), new BinaryValue( BYTES1 ) );
        assertEquals( 2, nbAdded );
        assertTrue( attr5.isHR() );
        assertTrue( attr5.contains( "ab" ) );
        assertTrue( attr5.contains( "c" ) );

        EntryAttribute attr6 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr6.add( new BinaryValue( BYTES1 ), new StringValue( "c" ) );
        assertEquals( 2, nbAdded );
        assertFalse( attr6.isHR() );
        assertTrue( attr6.contains( BYTES1 ) );
        assertTrue( attr6.contains( BYTES3 ) );

        EntryAttribute attr7 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr7.add( new BinaryValue( (byte[])null ), new StringValue( "c" ) );
        assertEquals( 2, nbAdded );
        assertFalse( attr7.isHR() );
        assertTrue( attr7.contains( NULL_BINARY_VALUE ) );
        assertTrue( attr7.contains( BYTES3 ) );

        EntryAttribute attr8 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr8.add( new StringValue( (String)null ), new BinaryValue( BYTES1 ) );
        assertEquals( 2, nbAdded );
        assertTrue( attr8.isHR() );
        assertTrue( attr8.contains( NULL_STRING_VALUE ) );
        assertTrue( attr8.contains( "ab" ) );
    }


    /**
     * Test method add( String... )
     */
    @Test
    public void testAddStringArray() throws InvalidAttributeValueException, LdapException
    {
        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );
        
        int nbAdded = attr1.add( (String)null );
        assertEquals( 1, nbAdded );
        assertTrue( attr1.isHR() );
        assertEquals( NULL_STRING_VALUE, attr1.get() );
        
        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr2.add( "" );
        assertEquals( 1, nbAdded );
        assertTrue( attr2.isHR() );
        assertEquals( "", attr2.getString() );
        
        EntryAttribute attr3 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr3.add( "t" );
        assertEquals( 1, nbAdded );
        assertTrue( attr3.isHR() );
        assertEquals( "t", attr3.getString() );
        
        EntryAttribute attr4 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr4.add( "a", "b", "c", "d" );
        assertEquals( 4, nbAdded );
        assertTrue( attr4.isHR() );
        assertEquals( "a", attr4.getString() );
        assertTrue( attr4.contains( "a" ) );
        assertTrue( attr4.contains( "b" ) );
        assertTrue( attr4.contains( "c" ) );
        assertTrue( attr4.contains( "d" ) );
        
        nbAdded = attr4.add( "e" );
        assertEquals( 1, nbAdded );
        assertTrue( attr4.isHR() );
        assertEquals( "a", attr4.getString() );
        assertTrue( attr4.contains( "a" ) );
        assertTrue( attr4.contains( "b" ) );
        assertTrue( attr4.contains( "c" ) );
        assertTrue( attr4.contains( "d" ) );
        assertTrue( attr4.contains( "e" ) );
        
        nbAdded = attr4.add( BYTES1 );
        assertEquals( 0, nbAdded );
        assertTrue( attr4.isHR() );
        assertEquals( "a", attr4.getString() );
        assertTrue( attr4.contains( "a" ) );
        assertTrue( attr4.contains( "b" ) );
        assertTrue( attr4.contains( "c" ) );
        assertTrue( attr4.contains( "d" ) );
        assertTrue( attr4.contains( "e" ) );
        assertFalse( attr4.contains( "ab" ) );
        
        EntryAttribute attr5 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr5.add( "a", "b", (String)null, "d" );
        assertEquals( 4, nbAdded );
        assertTrue( attr5.isHR() );
        assertTrue( attr5.contains( "a" ) );
        assertTrue( attr5.contains( "b" ) );
        assertTrue( attr5.contains( (String)null ) );
        assertTrue( attr5.contains( "d" ) );

        EntryAttribute attr6 = new DefaultEntryAttribute( "test" );
        
        attr6.setHR( false );
        nbAdded = attr6.add( "a", (String)null );
        assertEquals( 2, nbAdded );
        assertFalse( attr6.isHR() );
        assertTrue( attr6.contains( new byte[]{'a'} ) );
        assertTrue( attr6.contains( (byte[])null ) );
        
        EntryAttribute attr7 = new DefaultEntryAttribute( "test" );
        
        attr7.add( "a", "b" );
        assertEquals( 2, attr7.size() );
        
        assertEquals( 1, attr7.add( "b", "c" ) );
        assertEquals( 3, attr7.size() );
        assertTrue( attr7.contains( "a", "b", "c" ) );
    }


    /**
     * Test method add( byte[]... )
     */
    @Test
    public void testAddByteArray() throws InvalidAttributeValueException, LdapException
    {
        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );
        
        int nbAdded = attr1.add( (byte[])null );
        assertEquals( 1, nbAdded );
        assertFalse( attr1.isHR() );
        assertTrue( Arrays.equals( NULL_BINARY_VALUE.getBytes(), attr1.getBytes() ) );
        
        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr2.add( StringTools.EMPTY_BYTES );
        assertEquals( 1, nbAdded );
        assertFalse( attr2.isHR() );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, attr2.getBytes() ) );
        
        EntryAttribute attr3 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr3.add( BYTES1 );
        assertEquals( 1, nbAdded );
        assertFalse( attr3.isHR() );
        assertTrue( Arrays.equals( BYTES1, attr3.getBytes() ) );
        
        EntryAttribute attr4 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr4.add( BYTES1, BYTES2, BYTES3, BYTES4 );
        assertEquals( 4, nbAdded );
        assertFalse( attr4.isHR() );
        assertTrue( attr4.contains( BYTES1 ) );
        assertTrue( attr4.contains( BYTES2 ) );
        assertTrue( attr4.contains( BYTES3 ) );
        assertTrue( attr4.contains( BYTES4 ) );
        
        EntryAttribute attr5 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr5.add( BYTES1, BYTES2, (byte[])null, BYTES3 );
        assertEquals( 4, nbAdded );
        assertFalse( attr5.isHR() );
        assertTrue( attr5.contains( BYTES1 ) );
        assertTrue( attr5.contains( BYTES2 ) );
        assertTrue( attr5.contains( (byte[])null ) );
        assertTrue( attr5.contains( BYTES3 ) );

        EntryAttribute attr6 = new DefaultEntryAttribute( "test" );
        
        attr6.setHR( true );
        nbAdded = attr6.add( BYTES1, (byte[])null );
        assertEquals( 0, nbAdded );
        assertTrue( attr6.isHR() );
        assertFalse( attr6.contains( "ab" ) );
        assertFalse( attr6.contains( (String)null ) );
    }


    /**
     * Test method clear()
     */
    @Test
    public void testClear() throws InvalidAttributeValueException, LdapException
    {
        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );
        
        assertEquals( 0, attr1.size() );
        
        attr1.add( (String)null );
        assertEquals( 1, attr1.size() );
        assertTrue( attr1.isHR() );
        attr1.clear();
        assertTrue( attr1.isHR() );
        assertEquals( 0, attr1.size() );

        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );
        attr2.add( BYTES1, BYTES2 );
        assertEquals( 2, attr2.size() );
        assertFalse( attr2.isHR() );
        attr2.clear();
        assertFalse( attr2.isHR() );
        assertEquals( 0, attr2.size() );
    }


    /**
     * Test method contains( Value... )
     */
    @Test
    public void testContainsValueArray() throws InvalidAttributeValueException, LdapException
    {
        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );
        
        assertEquals( 0, attr1.size() );
        assertFalse( attr1.contains( STR_VALUE1 ) );
        assertFalse( attr1.contains( NULL_STRING_VALUE ) );
        
        attr1.add( (String)null );
        assertEquals( 1, attr1.size() );
        assertTrue( attr1.contains( NULL_STRING_VALUE ) );
        
        attr1.remove( (String)null );
        assertFalse( attr1.contains( NULL_STRING_VALUE ) );
        assertEquals( 0, attr1.size() );
        
        attr1.add(  "a", "b", "c" );
        assertEquals( 3, attr1.size() );
        assertTrue( attr1.contains( STR_VALUE1 ) );
        assertTrue( attr1.contains( STR_VALUE2 ) );
        assertTrue( attr1.contains( STR_VALUE3 ) );
        assertTrue( attr1.contains( STR_VALUE1, STR_VALUE3 ) );
        assertFalse( attr1.contains( STR_VALUE4 ) );
        assertFalse( attr1.contains( NULL_STRING_VALUE ) );
        assertTrue( attr1.contains( STR_VALUE1, BIN_VALUE2 ) );

        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );
        assertEquals( 0, attr2.size() );
        assertFalse( attr2.contains( BYTES1 ) );
        assertFalse( attr2.contains( NULL_BINARY_VALUE ) );
        
        attr2.add( (byte[])null );
        assertEquals( 1, attr2.size() );
        assertTrue( attr2.contains( NULL_BINARY_VALUE ) );
        
        attr2.remove( (byte[])null );
        assertFalse( attr2.contains( NULL_BINARY_VALUE ) );
        assertEquals( 0, attr2.size() );
        
        attr2.add( BYTES1, BYTES2, BYTES3 );
        assertEquals( 3, attr2.size() );
        assertTrue( attr2.contains( BIN_VALUE1 ) );
        assertTrue( attr2.contains( BIN_VALUE2 ) );
        assertTrue( attr2.contains( BIN_VALUE3 ) );
        assertFalse( attr2.contains( NULL_BINARY_VALUE ) );
        assertTrue( attr2.contains( STR_VALUE2, BIN_VALUE1 ) );
    }


    /**
     * Test method contains( String... )
     */
    @Test
    public void testContainsStringArray() throws InvalidAttributeValueException, LdapException
    {
        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );
        
        assertEquals( 0, attr1.size() );
        assertFalse( attr1.contains( "a" ) );
        assertFalse( attr1.contains( (String)null ) );
        
        attr1.add( (String)null );
        assertEquals( 1, attr1.size() );
        assertTrue( attr1.contains( (String)null ) );
        
        attr1.remove( (String)null );
        assertFalse( attr1.contains( (String)null ) );
        assertEquals( 0, attr1.size() );
        
        attr1.add(  "a", "b", "c" );
        assertEquals( 3, attr1.size() );
        assertTrue( attr1.contains( "a" ) );
        assertTrue( attr1.contains( "b" ) );
        assertTrue( attr1.contains( "c" ) );
        assertFalse( attr1.contains( "e" ) );
        assertFalse( attr1.contains( (String)null ) );

        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );
        assertEquals( 0, attr2.size() );
        assertFalse( attr2.contains( BYTES1 ) );
        assertFalse( attr2.contains( (byte[])null ) );
        
        attr2.add( (byte[])null );
        assertEquals( 1, attr2.size() );
        assertTrue( attr2.contains( (byte[])null ) );
        
        attr2.remove( (byte[])null );
        assertFalse( attr2.contains( (byte[])null ) );
        assertEquals( 0, attr2.size() );
        
        attr2.add( BYTES1, BYTES2, BYTES3 );
        assertEquals( 3, attr2.size() );
        assertTrue( attr2.contains( "ab" ) );
        assertTrue( attr2.contains( "b" ) );
        assertTrue( attr2.contains( "c" ) );
        assertFalse( attr2.contains( (String)null ) );
    }


    /**
     * Test method contains( byte... )
     */
    @Test
    public void testContainsByteArray() throws InvalidAttributeValueException, LdapException
    {
        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );
        
        assertEquals( 0, attr1.size() );
        assertFalse( attr1.contains( BYTES1 ) );
        assertFalse( attr1.contains( (byte[])null ) );
        
        attr1.add( (byte[])null );
        assertEquals( 1, attr1.size() );
        assertTrue( attr1.contains( (byte[])null ) );
        
        attr1.remove( (byte[])null );
        assertFalse( attr1.contains( (byte[])null ) );
        assertEquals( 0, attr1.size() );
        
        attr1.add(  BYTES1, BYTES2, BYTES3 );
        assertEquals( 3, attr1.size() );
        assertTrue( attr1.contains( BYTES1 ) );
        assertTrue( attr1.contains( BYTES2 ) );
        assertTrue( attr1.contains( BYTES3 ) );
        assertFalse( attr1.contains( BYTES4 ) );
        assertFalse( attr1.contains( (byte[])null ) );

        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );
        assertEquals( 0, attr2.size() );
        assertFalse( attr2.contains( "a" ) );
        assertFalse( attr2.contains( (String)null ) );
        
        attr2.add( (String)null );
        assertEquals( 1, attr2.size() );
        assertTrue( attr2.contains( (String)null ) );
        
        attr2.remove( (String)null );
        assertFalse( attr2.contains( (String)null ) );
        assertEquals( 0, attr2.size() );
        
        attr2.add( "ab", "b", "c" );
        assertEquals( 3, attr2.size() );
        assertTrue( attr2.contains( BYTES1 ) );
        assertTrue( attr2.contains( BYTES2 ) );
        assertTrue( attr2.contains( BYTES3 ) );
        assertFalse( attr2.contains( (byte[])null ) );
    }


    /**
     * Test method get()
     */
    @Test
    public void testGet() throws InvalidAttributeValueException, LdapException
    {
        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );
        
        attr1.add( (String)null );
        assertEquals( NULL_STRING_VALUE,attr1.get() );

        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );
        
        attr2.add( "a", "b", "c" );
        assertEquals( "a", attr2.get().getString() );
        
        attr2.remove( "a" );
        assertEquals( "b", attr2.get().getString() );

        attr2.remove( "b" );
        assertEquals( "c", attr2.get().getString() );

        attr2.remove( "c" );
        assertNull( attr2.get() );

        EntryAttribute attr3 = new DefaultEntryAttribute( "test" );
        
        attr3.add( BYTES1, BYTES2, BYTES3 );
        assertTrue( Arrays.equals( BYTES1, attr3.get().getBytes() ) );
        
        attr3.remove( BYTES1 );
        assertTrue( Arrays.equals( BYTES2, attr3.get().getBytes() ) );

        attr3.remove( BYTES2 );
        assertTrue( Arrays.equals( BYTES3, attr3.get().getBytes() ) );

        attr3.remove( BYTES3 );
        assertNull( attr2.get() );
    }


    /**
     * Test method getAll()
     */
    @Test
    public void testGetAll()
    {
        EntryAttribute attr = new DefaultEntryAttribute( "test" );
        
        Iterator<Value<?>> iterator = attr.getAll(); 
        assertFalse( iterator.hasNext() );
        
        attr.add( NULL_STRING_VALUE );
        iterator = attr.getAll(); 
        assertTrue( iterator.hasNext() );
        
        Value<?> value = iterator.next();
        assertEquals( NULL_STRING_VALUE, value );
        
        attr.clear();
        iterator = attr.getAll(); 
        assertFalse( iterator.hasNext() );
        
        attr.add(  "a", "b", "c" );
        iterator = attr.getAll(); 
        assertTrue( iterator.hasNext() );
        assertEquals( "a", iterator.next().getString() );
        assertEquals( "b", iterator.next().getString() );
        assertEquals( "c", iterator.next().getString() );
        assertFalse( iterator.hasNext() );
    }


    /**
     * Test method size()
     */
    @Test
    public void testSize() throws InvalidAttributeValueException, LdapException
    {
        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );

        assertEquals( 0, attr1.size() );
        
        attr1.add( (String)null );
        assertEquals( 1, attr1.size() );

        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );
        
        attr2.add( "a", "b" );
        assertEquals( 2, attr2.size() );
        
        attr2.clear();
        assertEquals( 0, attr2.size() );
    }


    /**
     * Test method remove( Value... )
     */
    @Test
    public void testRemoveValueArray() throws InvalidAttributeValueException, LdapException
    {
        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );

        assertFalse( attr1.remove( STR_VALUE1 ) );

        attr1.setHR( true );
        assertFalse( attr1.remove( STR_VALUE1 ) );
        
        attr1.add( "a", "b", "c" );
        assertTrue( attr1.remove( STR_VALUE1 ) );
        assertEquals( 2, attr1.size() );
        
        assertTrue( attr1.remove( STR_VALUE2, STR_VALUE3 ) );
        assertEquals( 0, attr1.size() );
        
        assertFalse( attr1.remove( STR_VALUE4 ) );
        
        attr1.clear();
        attr1.add( "a", "b", "c" );
        assertFalse( attr1.remove( STR_VALUE2, STR_VALUE4 ) );
        assertEquals( 2, attr1.size() );
        
        attr1.clear();
        attr1.add( "a", (String)null, "b" );
        assertTrue( attr1.remove( NULL_STRING_VALUE, STR_VALUE1 ) );
        assertEquals( 1, attr1.size() );
        
        attr1.clear();
        attr1.add( "a", (String)null, "b" );
        attr1.add( BYTES3 );
        assertTrue( attr1.remove( NULL_STRING_VALUE, STR_VALUE1 ) );
        assertEquals( 1, attr1.size() );
        
        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );

        assertFalse( attr2.remove( BIN_VALUE1 ) );

        attr2.setHR( true );
        assertFalse( attr2.remove( BIN_VALUE1 ) );
        
        attr2.clear();
        attr2.add( BYTES1, BYTES2, BYTES3 );
        assertFalse( attr2.remove( BIN_VALUE1 ) );
        assertEquals( 0, attr2.size() );
        
        assertFalse( attr2.remove( BIN_VALUE2, BIN_VALUE3 ) );
        assertEquals( 0, attr2.size() );
        
        assertFalse( attr2.remove( BIN_VALUE4 ) );
        
        attr2.clear();
        attr2.add( BYTES1, BYTES2, BYTES3 );
        assertFalse( attr2.remove( BIN_VALUE2, STR_VALUE4 ) );
        assertEquals( 0, attr2.size() );
        
        attr2.clear();
        attr2.add( BYTES1, (byte[])null, BYTES3 );
        assertFalse( attr2.remove( NULL_STRING_VALUE, BIN_VALUE1 ) );
        assertEquals( 0, attr2.size() );
        
        attr2.clear();
        attr2.add( BYTES1, (byte[])null, BYTES2 );
        attr2.add( "c" );
        assertEquals( 1, attr2.size() );
        assertFalse( attr2.remove( NULL_STRING_VALUE, BIN_VALUE1, STR_VALUE3 ) );
        assertEquals( 0, attr2.size() );
    }


    /**
     * Test method remove( byte... )
     */
    @Test
    public void testRemoveByteArray() throws InvalidAttributeValueException, LdapException
    {
        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );

        assertFalse( attr1.remove( BYTES1 ) );

        attr1.setHR( false );
        assertFalse( attr1.remove( BYTES1 ) );
        
        attr1.add( BYTES1, BYTES2, BYTES3 );
        assertTrue( attr1.remove( BYTES1 ) );
        assertEquals( 2, attr1.size() );
        
        assertTrue( attr1.remove( BYTES2, BYTES3 ) );
        assertEquals( 0, attr1.size() );
        
        assertFalse( attr1.remove( BYTES4 ) );
        
        attr1.clear();
        attr1.add( BYTES1, BYTES2, BYTES3 );
        assertFalse( attr1.remove( BYTES3, BYTES4 ) );
        assertEquals( 2, attr1.size() );
        
        attr1.clear();
        attr1.add( BYTES1, (byte[])null, BYTES2 ) ;
        assertTrue( attr1.remove( (byte[])null, BYTES1 ) );
        assertEquals( 1, attr1.size() );
        
        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );
        
        attr2.add( "ab", "b", "c" );
        
        assertFalse( attr2.remove( (byte[])null ) );
        assertTrue( attr2.remove( BYTES1, BYTES2 ) );
        assertFalse( attr2.remove( BYTES4 ) );
    }


    /**
     * Test method remove( String... )
     */
    @Test
    public void testRemoveStringArray() throws InvalidAttributeValueException, LdapException
    {
        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );

        assertFalse( attr1.remove( "a" ) );

        attr1.setHR( true );
        assertFalse( attr1.remove( "a" ) );
        
        attr1.add( "a", "b", "c" );
        assertTrue( attr1.remove( "a" ) );
        assertEquals( 2, attr1.size() );
        
        assertTrue( attr1.remove( "b", "c" ) );
        assertEquals( 0, attr1.size() );
        
        assertFalse( attr1.remove( "d" ) );
        
        attr1.clear();
        attr1.add( "a", "b", "c" );
        assertFalse( attr1.remove( "b", "e" ) );
        assertEquals( 2, attr1.size() );
        
        attr1.clear();
        attr1.add( "a", (String)null, "b" );
        assertTrue( attr1.remove( (String )null, "a" ) );
        assertEquals( 1, attr1.size() );
        
        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );
        
        attr2.add( BYTES1, BYTES2, BYTES3 );
        
        assertFalse( attr2.remove( (String)null ) );
        assertTrue( attr2.remove( "ab", "c" ) );
        assertFalse( attr2.remove( "d" ) );
    }


    /**
     * Test method put( String... )
     */
    @Test
    public void testPutStringArray() throws InvalidAttributeValueException, LdapException
    {
        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );
        
        int nbAdded = attr1.add( (String)null );
        assertEquals( 1, nbAdded );
        assertTrue( attr1.isHR() );
        assertEquals( NULL_STRING_VALUE, attr1.get() );
        
        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr2.add( "" );
        assertEquals( 1, nbAdded );
        assertTrue( attr2.isHR() );
        assertEquals( "", attr2.getString() );
        
        EntryAttribute attr3 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr3.add( "t" );
        assertEquals( 1, nbAdded );
        assertTrue( attr3.isHR() );
        assertEquals( "t", attr3.getString() );
        
        EntryAttribute attr4 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr4.add( "a", "b", "c", "d" );
        assertEquals( 4, nbAdded );
        assertTrue( attr4.isHR() );
        assertTrue( attr4.contains( "a" ) );
        assertTrue( attr4.contains( "b" ) );
        assertTrue( attr4.contains( "c" ) );
        assertTrue( attr4.contains( "d" ) );
        
        EntryAttribute attr5 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr5.add( "a", "b", (String)null, "d" );
        assertEquals( 4, nbAdded );
        assertTrue( attr5.isHR() );
        assertTrue( attr5.contains( "a" ) );
        assertTrue( attr5.contains( "b" ) );
        assertTrue( attr5.contains( (String)null ) );
        assertTrue( attr5.contains( "d" ) );

        EntryAttribute attr6 = new DefaultEntryAttribute( "test" );
        
        attr6.setHR( false );
        nbAdded = attr6.add( "a", (String)null );
        assertEquals( 2, nbAdded );
        assertFalse( attr6.isHR() );
        assertTrue( attr6.contains( new byte[]{'a'} ) );
        assertTrue( attr6.contains( (byte[])null ) );
    }


    /**
     * Test method put( byte[]... )
     */
    @Test
    public void testPutByteArray() throws InvalidAttributeValueException, LdapException
    {
        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );
        
        int nbAdded = attr1.add( (byte[])null );
        assertEquals( 1, nbAdded );
        assertFalse( attr1.isHR() );
        assertTrue( Arrays.equals( NULL_BINARY_VALUE.getBytes(), attr1.getBytes() ) );
        
        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr2.add( StringTools.EMPTY_BYTES );
        assertEquals( 1, nbAdded );
        assertFalse( attr2.isHR() );
        assertTrue( Arrays.equals( StringTools.EMPTY_BYTES, attr2.getBytes() ) );
        
        EntryAttribute attr3 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr3.add( BYTES1 );
        assertEquals( 1, nbAdded );
        assertFalse( attr3.isHR() );
        assertTrue( Arrays.equals( BYTES1, attr3.getBytes() ) );
        
        EntryAttribute attr4 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr4.add( BYTES1, BYTES2 );
        assertEquals( 2, nbAdded );
        assertFalse( attr4.isHR() );
        assertTrue( attr4.contains( BYTES1 ) );
        assertTrue( attr4.contains( BYTES2 ) );
        
        nbAdded = attr4.add( BYTES3, BYTES4 );
        assertEquals( 2, nbAdded );
        assertFalse( attr4.isHR() );
        assertTrue( attr4.contains( BYTES3 ) );
        assertTrue( attr4.contains( BYTES4 ) );
        
        EntryAttribute attr5 = new DefaultEntryAttribute( "test" );
        
        nbAdded = attr5.add( BYTES1, BYTES2, (byte[])null, BYTES3 );
        assertEquals( 4, nbAdded );
        assertFalse( attr5.isHR() );
        assertTrue( attr5.contains( BYTES1 ) );
        assertTrue( attr5.contains( BYTES2 ) );
        assertTrue( attr5.contains( (byte[])null ) );
        assertTrue( attr5.contains( BYTES3 ) );

        EntryAttribute attr6 = new DefaultEntryAttribute( "test" );
        
        attr6.setHR( true );
        nbAdded = attr6.add( BYTES1, (byte[])null );
        assertEquals( 0, nbAdded );
        assertTrue( attr6.isHR() );
        assertFalse( attr6.contains( "ab" ) );
        assertFalse( attr6.contains( (String)null ) );
    }


    /**
     * Test method put( Value... )
     */
    @Test
    public void testPutValueArray() throws InvalidAttributeValueException, LdapException
    {
        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );
        
        assertEquals( 0, attr1.size() );
        
        attr1.add( NULL_STRING_VALUE );
        assertEquals( 1, attr1.size() );
        assertTrue( attr1.contains( NULL_STRING_VALUE ) );
        
        attr1.clear();
        attr1.add( STR_VALUE1, STR_VALUE2, STR_VALUE3 );
        assertEquals( 3, attr1.size() );
        assertTrue( attr1.contains( STR_VALUE1 ) );
        assertTrue( attr1.contains( STR_VALUE2 ) );
        assertTrue( attr1.contains( STR_VALUE3 ) );

        attr1.clear();
        attr1.add( STR_VALUE1, NULL_STRING_VALUE, STR_VALUE3 );
        assertEquals( 3, attr1.size() );
        assertTrue( attr1.contains( STR_VALUE1 ) );
        assertTrue( attr1.contains( NULL_STRING_VALUE ) );
        assertTrue( attr1.contains( STR_VALUE3 ) );
        
        attr1.clear();
        attr1.add( STR_VALUE1, NULL_STRING_VALUE, BIN_VALUE3 );
        assertEquals( 3, attr1.size() );
        assertTrue( attr1.contains( STR_VALUE1 ) );
        assertTrue( attr1.contains( NULL_STRING_VALUE ) );
        assertTrue( attr1.contains( STR_VALUE3 ) );
        

        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );
        assertEquals( 0, attr2.size() );
        
        attr2.add( NULL_BINARY_VALUE );
        assertEquals( 1, attr2.size() );
        assertTrue( attr2.contains( NULL_BINARY_VALUE ) );
        
        attr2.clear();
        attr2.add( BIN_VALUE1, BIN_VALUE2, BIN_VALUE3 );
        assertEquals( 3, attr2.size() );
        assertTrue( attr2.contains( BIN_VALUE1 ) );
        assertTrue( attr2.contains( BIN_VALUE2 ) );
        assertTrue( attr2.contains( BIN_VALUE3 ) );
        
        attr2.clear();
        attr2.add( BIN_VALUE1, NULL_BINARY_VALUE, STR_VALUE3 );
        assertEquals( 3, attr2.size() );
        assertTrue( attr2.contains( BIN_VALUE1 ) );
        assertTrue( attr2.contains( NULL_BINARY_VALUE ) );
        assertTrue( attr2.contains( BIN_VALUE3 ) );
    }


    /**
     * Test method toString()
     */
    @Test
    public void testToString() throws InvalidAttributeValueException, LdapException
    {
        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );
        
        assertEquals( "    test: (null)\n", attr1.toString() );
        
        attr1.add( "a" );
        assertEquals( "    test: a\n", attr1.toString() );
        
        attr1.add( "b" );
        assertEquals( "    test: a\n    test: b\n", attr1.toString() );

        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );

        attr2.add( BYTES1 );
        assertEquals( "    test: '0x61 0x62 '\n", attr2.toString() );

        attr2.add( BYTES3 );
        assertEquals( "    test: '0x61 0x62 '\n    test: '0x63 '\n", attr2.toString() );
    }


    /**
     * Test method hashCode()
     */
    @Test
    public void testHashCode() throws InvalidAttributeValueException, LdapException
    {
        EntryAttribute attr = new DefaultEntryAttribute();
        assertEquals( 37, attr.hashCode() );
        
        attr.setHR( true );
        assertEquals( 37*17 + 1231, attr.hashCode() );
        
        attr.setHR(  false );
        assertEquals( 37*17 + 1237, attr.hashCode() );

        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );
        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );
        
        assertEquals( attr1.hashCode(), attr2.hashCode() );
        
        attr1.add( "a", "b", "c" );
        attr2.add( "a", "b", "c" );
        assertEquals( attr1.hashCode(), attr2.hashCode() );
        
        attr1.add( "d" );
        attr2.add( "d" );
        assertEquals( attr1.hashCode(), attr2.hashCode() );

        attr1.add( NULL_STRING_VALUE );
        attr2.add(  NULL_STRING_VALUE );
        assertEquals( attr1.hashCode(), attr2.hashCode() );

        // Order mess up the hashCode
        attr1.clear();
        attr2.clear();
        attr1.add( "a", "b", "c" );
        attr2.add( "c", "b", "a" );
        assertNotSame( attr1.hashCode(), attr2.hashCode() );
        
        EntryAttribute attr3 = new DefaultEntryAttribute( "test" );
        EntryAttribute attr4 = new DefaultEntryAttribute( "test" );
        
        attr3.add( BYTES1, BYTES2 );
        attr4.add( BYTES1, BYTES2 );
        assertEquals( attr3.hashCode(), attr4.hashCode() );
        
        attr3.add( BYTES3 );
        attr4.add( BYTES3 );
        assertEquals( attr3.hashCode(), attr4.hashCode() );
        
        attr3.add( NULL_BINARY_VALUE );
        attr4.add(  NULL_BINARY_VALUE );
        assertEquals( attr3.hashCode(), attr4.hashCode() );

        // Order mess up the hashCode
        attr3.clear();
        attr4.clear();
        attr3.add( BYTES1, BYTES2 );
        attr4.add( BYTES2, BYTES1 );
        assertNotSame( attr3.hashCode(), attr4.hashCode() );
    }


    /**
     * Test method testEquals()
     */
    @Test
    public void testEquals()
    {
        EntryAttribute attr1 = new DefaultEntryAttribute( "test" );
        
        assertFalse( attr1.equals( null ) );
        
        EntryAttribute attr2 = new DefaultEntryAttribute( "test" );
        
        assertTrue( attr1.equals( attr2 ) );
        
        attr2.setId( "TEST" );
        assertTrue( attr1.equals( attr2 ) );

        attr1.setId( "tset" );
        assertFalse( attr1.equals( attr2 ) );
        
        attr1.setUpId( "TEST" );
        assertTrue( attr1.equals( attr2 ) );
        
        attr1.add( "a", "b", "c" );
        attr2.add( "c", "b", "a" );
        assertTrue( attr1.equals( attr2 ) );
        
        attr1.setHR( true );
        attr2.setHR( false );
        assertFalse( attr1.equals( attr2 ) );
        
        EntryAttribute attr3 = new DefaultEntryAttribute( "test" );
        EntryAttribute attr4 = new DefaultEntryAttribute( "test" );
        
        attr3.add( NULL_BINARY_VALUE );
        attr4.add( NULL_BINARY_VALUE );
        assertTrue( attr3.equals( attr4 ) );
        
        EntryAttribute attr5 = new DefaultEntryAttribute( "test" );
        EntryAttribute attr6 = new DefaultEntryAttribute( "test" );
        
        attr5.add( NULL_BINARY_VALUE );
        attr6.add( NULL_STRING_VALUE );
        assertFalse( attr5.equals( attr6 ) );

        EntryAttribute attr7 = new DefaultEntryAttribute( "test" );
        EntryAttribute attr8 = new DefaultEntryAttribute( "test" );
        
        attr7.add( "a" );
        attr8.add( BYTES2 );
        assertFalse( attr7.equals( attr8 ) );

        EntryAttribute attr9 = new DefaultEntryAttribute( "test" );
        EntryAttribute attr10 = new DefaultEntryAttribute( "test" );
        
        attr7.add( "a" );
        attr7.add( BYTES2 );
        attr8.add( "a", "b" );
        assertTrue( attr9.equals( attr10 ) );
    }


    /**
     * Test method testClone()
     */
    @Test
    public void testClone()
    {
        EntryAttribute attr = new DefaultEntryAttribute( "test" );
        
        EntryAttribute clone = attr.clone();
        
        assertEquals( attr, clone );
        attr.setId( "new" );
        assertEquals( "test", clone.getId() );
        
        attr.add( "a", (String)null, "b" );
        clone = attr.clone();
        assertEquals( attr, clone );
        
        attr.remove( "a" );
        assertNotSame( attr, clone );
        
        clone = attr.clone();
        assertEquals( attr, clone );

        attr.setHR( false );
        assertNotSame( attr, clone );
    }
    
    
    /**
     * Test the serialization of a complete client attribute
     */
    @Test
    public void testSerializeCompleteAttribute() throws LdapException, IOException, ClassNotFoundException
    {
        DefaultEntryAttribute dca = new DefaultEntryAttribute( "CommonName" );
        dca.setHR( true );
        dca.setId( "CN" );
        dca.add( "test1", "test2" );

        DefaultEntryAttribute dcaSer = deserializeValue( serializeValue( dca ) );
        assertEquals( dca.toString(), dcaSer.toString() );
        assertEquals( "cn", dcaSer.getId() );
        assertEquals( "CN", dcaSer.getUpId() );
        assertEquals( "test1", dcaSer.getString() );
        assertTrue( dcaSer.contains( "test2", "test1" ) );
        assertTrue( dcaSer.isHR() );
        assertFalse( dcaSer.isValid() );
    }
    
    
    /**
     * Test the serialization of a client attribute with no value
     */
    @Test
    public void testSerializeAttributeWithNoValue() throws LdapException, IOException, ClassNotFoundException
    {
        DefaultEntryAttribute dca = new DefaultEntryAttribute( "CommonName" );
        dca.setHR( true );
        dca.setId( "CN" );

        DefaultEntryAttribute dcaSer = deserializeValue( serializeValue( dca ) );
        assertEquals( dca.toString(), dcaSer.toString() );
        assertEquals( "cn", dcaSer.getId() );
        assertEquals( "CN", dcaSer.getUpId() );
        assertEquals( 0, dcaSer.size() );
        assertTrue( dcaSer.isHR() );
        assertTrue( dcaSer.isValid() );
    }
    
    
    /**
     * Test the serialization of a client attribute with a null value
     */
    @Test
    public void testSerializeAttributeNullValue() throws LdapException, IOException, ClassNotFoundException
    {
        DefaultEntryAttribute dca = new DefaultEntryAttribute( "CommonName" );
        dca.setHR( true );
        dca.setId( "CN" );
        dca.add( (String)null );

        DefaultEntryAttribute dcaSer = deserializeValue( serializeValue( dca ) );
        assertEquals( dca.toString(), dcaSer.toString() );
        assertEquals( "cn", dcaSer.getId() );
        assertEquals( "CN", dcaSer.getUpId() );
        assertEquals( "", dcaSer.getString() );
        assertEquals( 1, dcaSer.size() );
        assertTrue( dcaSer.contains( (String)null ) );
        assertTrue( dcaSer.isHR() );
        assertFalse( dcaSer.isValid() );
    }
    
    
    /**
     * Test the serialization of a client attribute with a binary value
     */
    @Test
    public void testSerializeAttributeBinaryValue() throws LdapException, IOException, ClassNotFoundException
    {
        DefaultEntryAttribute dca = new DefaultEntryAttribute( "UserPassword" );
        dca.setHR( false );
        byte[] password = StringTools.getBytesUtf8( "secret" );
        dca.add( password );

        DefaultEntryAttribute dcaSer = deserializeValue( serializeValue( dca ) );
        assertEquals( dca.toString(), dcaSer.toString() );
        assertEquals( "userpassword", dcaSer.getId() );
        assertEquals( "UserPassword", dcaSer.getUpId() );
        assertTrue( Arrays.equals( dca.getBytes(), dcaSer.getBytes() ) );
        assertEquals( 1, dcaSer.size() );
        assertTrue( dcaSer.contains( password ) );
        assertFalse( dcaSer.isHR() );
        assertFalse( dcaSer.isValid() );
    }
}
