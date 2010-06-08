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
package org.apache.directory.shared.ldap.entry;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.entry.BinaryValue;
import org.apache.directory.shared.ldap.entry.Value;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.LdapComparator;
import org.apache.directory.shared.ldap.schema.SyntaxChecker;
import org.apache.directory.shared.ldap.schema.comparators.StringComparator;
import org.apache.directory.shared.ldap.schema.normalizers.DeepTrimToLowerNormalizer;
import org.apache.directory.shared.ldap.schema.normalizers.NoOpNormalizer;
import org.apache.directory.shared.ldap.schema.syntaxCheckers.OctetStringSyntaxChecker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that the StringValue class works properly as expected.
 *
 * Some notes while conducting tests:
 *
 * <ul>
 *   <li>comparing values with different types - how does this behave</li>
 *   <li>exposing access to at from value or to a comparator?</li>
 * </ul>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent(threads = 6)
public class StringValueAttributeTypeTest
{
    private EntryUtils.S s;
    private EntryUtils.AT at;
    private EntryUtils.MR mr;

    
    /**
     * Initialize an AttributeType and the associated MatchingRule 
     * and Syntax
     */
    @Before public void initAT()
    {
        s = new EntryUtils.S( "1.1.1.1", false );
        s.setSyntaxChecker( new OctetStringSyntaxChecker() );
        mr = new EntryUtils.MR( "1.1.2.1" );
        mr.setSyntax( s );
        mr.setLdapComparator( new StringComparator( "1.1.2.1" ) );
        mr.setNormalizer( new DeepTrimToLowerNormalizer( "1.1.2.1" ) );
        at = new EntryUtils.AT( "1.1.3.1" );
        at.setEquality( mr );
        at.setOrdering( mr );
        at.setSubstring( mr );
        at.setSyntax( s );
    }
    
    
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
            value.serialize( oOut );
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
    private StringValue deserializeValue( ByteArrayOutputStream out, AttributeType at ) throws IOException, ClassNotFoundException
    {
        ObjectInputStream oIn = null;
        ByteArrayInputStream in = new ByteArrayInputStream( out.toByteArray() );

        try
        {
            oIn = new ObjectInputStream( in );

            StringValue value = new StringValue( at );
            value.deserialize( oIn );

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
     * Test the constructor with a null value
     */
    @Test 
    public void testClientStringValueNullValue()
    {
        AttributeType attribute = EntryUtils.getIA5StringAttributeType();
        
        StringValue value = new StringValue( attribute, null );
        
        assertNull( value.get() );
        assertTrue( value.isNull() );
    }
    
    
    /**
     * Test the getNormalizedValue method
     */
    @Test public void testGetNormalizedValue()
    {
        AttributeType attribute = EntryUtils.getIA5StringAttributeType();
        
        StringValue sv = new StringValue( attribute, null );
        
        assertFalse( sv.isNormalized() );
        assertNull( sv.getNormalizedValue() );
        assertTrue( sv.isNormalized() );

        sv = new StringValue( attribute, "" );
        assertFalse( sv.isNormalized() );
        assertEquals( "", sv.getNormalizedValue() );
        assertTrue( sv.isNormalized() );

        sv = new StringValue( attribute, "TEST" );
        assertFalse( sv.isNormalized() );
        assertEquals( "test", sv.getNormalizedValue() );
        assertTrue( sv.isNormalized() );
    }
    

    /**
     * Test the isValid method
     * 
     * The SyntaxChecker does not accept values longer than 5 chars.
     */
    @Test public void testIsValid()
    {
        AttributeType attribute = EntryUtils.getIA5StringAttributeType();
        
        StringValue sv = new StringValue( attribute, null );
        assertTrue( sv.isValid() );

        sv = new StringValue( attribute, "" );
        assertTrue( sv.isValid() );

        sv = new StringValue( attribute, "TEST" );
        assertTrue( sv.isValid() );

        sv = new StringValue( attribute, "testlong" );
        assertFalse( sv.isValid() );
    }
    
    
    /**
     * Test the normalize method
     */
    @Test
    public void testNormalize() throws LdapException
    {
        AttributeType attribute = EntryUtils.getIA5StringAttributeType();
        StringValue sv = new StringValue( attribute );

        sv.normalize();
        assertEquals( null, sv.getNormalizedValue() );
        
        sv = new StringValue( attribute, "" );
        sv.normalize();
        assertEquals( "", sv.getNormalizedValue() );

        sv = new StringValue( attribute, "  This is    a   TEST  " );
        sv.normalize();
        assertEquals( "this is a test", sv.getNormalizedValue() );
    }
    

    /**
     * Test the instanceOf method
     */
    @Test
    public void testInstanceOf() throws LdapException
    {
        AttributeType attribute = EntryUtils.getIA5StringAttributeType();
        StringValue ssv = new StringValue( attribute );
        
        assertTrue( ssv.instanceOf( attribute ) );
        
        attribute = EntryUtils.getBytesAttributeType();
        
        assertFalse( ssv.instanceOf( attribute ) );
    }    
    

    /**
     * Test the getAttributeType method
     */
    @Test
    public void testgetAttributeType()
    {
        AttributeType attribute = EntryUtils.getIA5StringAttributeType();
        StringValue ssv = new StringValue( attribute );
        
        assertEquals( attribute, ssv.getAttributeType() );
    }    

    
    /**
     * Test the equals method
     */
    @Test public void testEquals()
    {
        AttributeType at1 = EntryUtils.getIA5StringAttributeType();
        AttributeType at2 = EntryUtils.getBytesAttributeType();
        
        StringValue value1 = new StringValue( at1, "test" );
        StringValue value2 = new StringValue( at1, "test" );
        StringValue value3 = new StringValue( at1, "TEST" );
        StringValue value4 = new StringValue( at1, "tes" );
        StringValue value5 = new StringValue( at1, null );
        BinaryValue valueBytes = new BinaryValue( at2, new byte[]{0x01} );
        StringValue valueString = new StringValue( at, "test" );
        
        assertTrue( value1.equals( value1 ) );
        assertTrue( value1.equals( value2 ) );
        assertTrue( value1.equals( value3 ) );
        assertFalse( value1.equals( value4 ) );
        assertFalse( value1.equals( value5 ) );
        assertFalse( value1.equals( "test" ) );
        assertFalse( value1.equals( null ) );
        
        assertFalse( value1.equals( valueString ) );
        assertFalse( value1.equals( valueBytes ) );
    }

    
    
    
    
    
    
    
    
    
    /**
     * Test the constructor with bad AttributeType
     */
    @Test public void testBadConstructor()
    {
        try
        {
            new StringValue( null, null );
            fail();
        }
        catch ( IllegalArgumentException iae )
        {
            // Expected...
        }
        
        // create a AT without any syntax
        AttributeType attribute = new EntryUtils.AT( "1.1.3.1" );
        
        try
        {
            new StringValue( attribute );
            fail();
        }
        catch ( IllegalArgumentException iae )
        {
            // Expected...
        }
    }


    /**
     * Tests to make sure the hashCode method is working properly.
     * @throws Exception on errors
     */
    @Test public void testHashCode()
    {
        AttributeType at1 = EntryUtils.getCaseIgnoringAttributeNoNumbersType();
        StringValue v0 = new StringValue( at1, "Alex" );
        StringValue v1 = new StringValue( at1, "ALEX" );
        StringValue v2 = new StringValue( at1, "alex" );
        
        assertEquals( v0.hashCode(), v1.hashCode() );
        assertEquals( v0.hashCode(), v2.hashCode() );
        assertEquals( v1.hashCode(), v2.hashCode() );
        
        assertEquals( v0, v1 );
        assertEquals( v0, v2 );
        assertEquals( v1, v2 );
        
        assertTrue( v0.isValid() );
        assertTrue( v1.isValid() );
        assertTrue( v2.isValid() );

        StringValue v3 = new StringValue( at1, "Timber" );
        
        assertTrue( v3.isValid() );
        assertNotSame( v0.hashCode(), v3.hashCode() );

        StringValue v4 = new StringValue( at, "Alex" );
        
        assertNotSame( v0.hashCode(), v4.hashCode() );
    }
    
    
    /**
     * Test the compareTo method
     */
    @Test
    public void testCompareTo()
    {
        AttributeType at1 = EntryUtils.getCaseIgnoringAttributeNoNumbersType();
        StringValue v0 = new StringValue( at1, "Alex" );
        StringValue v1 = new StringValue( at1, "ALEX" );
        
        assertEquals( 0, v0.compareTo( v1 ) );
        assertEquals( 0, v1.compareTo( v0 ) );

        StringValue v2 = new StringValue( at1, null );
        
        assertEquals( 1, v0.compareTo( v2 ) );
        assertEquals( -1, v2.compareTo( v0 ) );
    }


    /**
     * Test the clone method
     */
    @Test
    public void testClone() throws LdapException
    {
        AttributeType at1 = EntryUtils.getCaseIgnoringAttributeNoNumbersType();
        StringValue sv = new StringValue( at1, "Test" );
        
        StringValue sv1 = sv.clone();
        
        assertEquals( sv, sv1 );
        
        sv = new StringValue( "" );
        
        assertNotSame( sv, sv1 );
        assertEquals( "", sv.getString() );
        
        sv = new StringValue( "  This is    a   TEST  " );
        sv1 = sv.clone();
        
        assertEquals( sv, sv1 );
        
        sv.normalize();
        
        assertEquals( sv, sv1 );
    }
    

    /**
     * Presumes an attribute which constrains it's values to some constant
     * strings: LOW, MEDIUM, HIGH.  Normalization does nothing. MatchingRules
     * are exact case matching.
     *
     * @throws Exception on errors
     */
    @Test public void testConstrainedString()
    {
        s.setSyntaxChecker( new SyntaxChecker( "1.1.1.1" ) {
            private static final long serialVersionUID = 0L;

            public boolean isValidSyntax( Object value )
            {
                if ( value instanceof String )
                {
                    String strval = ( String ) value;
                    return strval.equals( "HIGH" ) || strval.equals( "LOW" ) || strval.equals( "MEDIUM" );
                }
                return false;
            }
        });

        mr.setSyntax( s );
        mr.setLdapComparator( new LdapComparator<String>( mr.getOid() )
        {
            private static final long serialVersionUID = 0L;

            public int compare( String o1, String o2 )
            {
                if ( o1 == null )
                {
                    if ( o2 == null )
                    {
                        return 0;
                    }
                    else
                    {
                        return -1;
                    }
                }
                else if ( o2 == null )
                {
                    return 1;
                }

                int i1 = getValue( o1 );
                int i2 = getValue( o2 );

                if ( i1 == i2 ) { return 0; }
                if ( i1 > i2 ) { return 1; }
                if ( i1 < i2 ) { return -1; }

                throw new IllegalStateException( "should not get here at all" );
            }

            public int getValue( String val )
            {
                if ( val.equals( "LOW" ) ) { return 0; }
                if ( val.equals( "MEDIUM" ) ) { return 1; }
                if ( val.equals( "HIGH" ) ) { return 2; }
                throw new IllegalArgumentException( "Not a valid value" );
            }
        } );
        
        mr.setNormalizer( new NoOpNormalizer( mr.getOid() ) );
        at.setEquality( mr );
        at.setSyntax( s );

        // check that normalization and syntax checks work as expected
        StringValue value = new StringValue( at, "HIGH" );
        assertEquals( value.get(), value.get() );
        assertTrue( value.isValid() );
        value = new StringValue( at, "high" );
        assertFalse( value.isValid() );

        // create a bunch to best tested for equals and in containers
        StringValue v0 = new StringValue( at, "LOW" );
        assertTrue( v0.isValid() );
        StringValue v1 = new StringValue( at, "LOW" );
        assertTrue( v1.isValid() );
        StringValue v2 = new StringValue( at, "MEDIUM" );
        assertTrue( v2.isValid() );
        StringValue v3 = new StringValue( at, "HIGH" );
        assertTrue( v3.isValid() );
        StringValue v4 = new StringValue( at );
        assertFalse( v4.isValid() );
        StringValue v5 = new StringValue( at );
        assertFalse( v5.isValid() );

        // check equals
        assertTrue( v0.equals( v1 ) );
        assertTrue( v1.equals( v0 ) );
        assertEquals( 0, v0.compareTo( v1 ) );

        assertTrue( v4.equals( v5 ) );
        assertTrue( v5.equals( v4 ) );
        assertEquals( 0, v4.compareTo( v5 ) );

        assertFalse( v2.equals( v3 ) );
        assertFalse( v3.equals( v2 ) );
        assertTrue( v2.compareTo( v3 ) < 0 );
        assertTrue( v3.compareTo( v2 ) > 0 );

        // add all except v1 and v5 to a set
        HashSet<StringValue> set = new HashSet<StringValue>();
        set.add( v0 );
        set.add( v2 );
        set.add( v3 );
        set.add( v4 );

        // check contains method
        assertTrue( "since v1.equals( v0 ) and v0 was added then this should be true", set.contains( v1 ) );
        assertTrue( "since v4.equals( v5 ) and v4 was added then this should be true", set.contains( v5 ) );

        // check ordering based on the comparator
        List<Value<String>> list = new ArrayList<Value<String>>();
        list.add( v1 );
        list.add( v3 );
        list.add( v5 );
        list.add( v0 );
        list.add( v2 );
        list.add( v4 );

        Collections.sort( list );

        // null ones are at first 2 indices
        assertTrue( "since v4 equals v5 and has no value either could be at index 0 & 1", list.get( 0 ).equals( v4 ) );
        assertTrue( "since v4 equals v5 and has no value either could be at index 0 & 1", list.get( 0 ).equals( v5 ) );
        assertTrue( "since v4 equals v5 and has no value either could be at index 0 & 1", list.get( 1 ).equals( v4 ) );
        assertTrue( "since v4 equals v5 and has no value either could be at index 0 & 1", list.get( 1 ).equals( v5 ) );

        // low ones are at the 3rd and 4th indices
        assertTrue( "since v0 equals v1 either could be at index 2 & 3", list.get( 2 ).equals( v0 ) );
        assertTrue( "since v0 equals v1 either could be at index 2 & 3", list.get( 2 ).equals( v1 ) );
        assertTrue( "since v0 equals v1 either could be at index 2 & 3", list.get( 3 ).equals( v0 ) );
        assertTrue( "since v0 equals v1 either could be at index 2 & 3", list.get( 3 ).equals( v1 ) );

        // medium then high next
        assertTrue( "since v2 \"MEDIUM\" should be at index 4", list.get( 4 ).equals( v2 ) );
        assertTrue( "since v3 \"HIGH\" should be at index 5", list.get( 5 ).equals( v3 ) );

        assertEquals( 6, list.size() );
    }


    /**
     * Creates a string value with an attribute type that is of a syntax
     * which accepts anything.  Also there is no normalization since the
     * value is the same as the normalized value.  This makes the at technically
     * a binary value however it can be dealt with as a string so this test
     * is still OK.
     * @throws Exception on errors
     */
    @Test public void testAcceptAllNoNormalization()
    {
        // check that normalization and syntax checks work as expected
        StringValue value = new StringValue( at, "hello" );
        assertEquals( value.get(), value.get() );
        assertTrue( value.isValid() );

        // create a bunch to best tested for equals and in containers
        StringValue v0 = new StringValue( at, "hello" );
        StringValue v1 = new StringValue( at, "hello" );
        StringValue v2 = new StringValue( at, "next0" );
        StringValue v3 = new StringValue( at, "next1" );
        StringValue v4 = new StringValue( at );
        StringValue v5 = new StringValue( at );

        // check equals
        assertTrue( v0.equals( v1 ) );
        assertTrue( v1.equals( v0 ) );
        assertTrue( v4.equals( v5 ) );
        assertTrue( v5.equals( v4 ) );
        assertFalse( v2.equals( v3 ) );
        assertFalse( v3.equals( v2 ) );

        // add all except v1 and v5 to a set
        HashSet<StringValue> set = new HashSet<StringValue>();
        set.add( v0 );
        set.add( v2 );
        set.add( v3 );
        set.add( v4 );

        // check contains method
        assertTrue( "since v1.equals( v0 ) and v0 was added then this should be true", set.contains( v1 ) );
        assertTrue( "since v4.equals( v5 ) and v4 was added then this should be true", set.contains( v5 ) );

        // check ordering based on the comparator
        ArrayList<StringValue> list = new ArrayList<StringValue>();
        list.add( v1 );
        list.add( v3 );
        list.add( v5 );
        list.add( v0 );
        list.add( v2 );
        list.add( v4 );

        Comparator<StringValue> c = new Comparator<StringValue>()
        {
            public int compare( StringValue o1, StringValue o2 )
            {
                String n1 = null;
                String n2 = null;
                
                if ( o1 != null )
                {
                    n1 = o1.getString();
                }

                if ( o2 != null )
                {
                    n2 = o2.getString();
                }

                if ( n1 == null )
                {
                    return ( n2 == null ) ? 0 : -1;
                }
                else if ( n2 == null )
                {
                    return 1;
                }

                return mr.getLdapComparator().compare( n1, n2 );
            }
        };

        Collections.sort( list, c );

        assertTrue( "since v4 equals v5 and has no value either could be at index 0 & 1", list.get( 0 ).equals( v4 ) );
        assertTrue( "since v4 equals v5 and has no value either could be at index 0 & 1", list.get( 0 ).equals( v5 ) );
        assertTrue( "since v4 equals v5 and has no value either could be at index 0 & 1", list.get( 1 ).equals( v4 ) );
        assertTrue( "since v4 equals v5 and has no value either could be at index 0 & 1", list.get( 1 ).equals( v5 ) );

        assertTrue( "since v0 equals v1 either could be at index 2 & 3", list.get( 2 ).equals( v0 ) );
        assertTrue( "since v0 equals v1 either could be at index 2 & 3", list.get( 2 ).equals( v1 ) );
        assertTrue( "since v0 equals v1 either could be at index 2 & 3", list.get( 3 ).equals( v0 ) );
        assertTrue( "since v0 equals v1 either could be at index 2 & 3", list.get( 3 ).equals( v1 ) );

        assertTrue( "since v2 \"next0\" should be at index 4", list.get( 4 ).equals( v2 ) );
        assertTrue( "since v3 \"next1\" should be at index 5", list.get( 5 ).equals( v3 ) );

        assertEquals( 6, list.size() );
    }

    
    /**
     * Test serialization of a StringValue which has a normalized value
     */
    @Test public void testNormalizedStringValueSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        // First check with a value which will be normalized
        StringValue ssv = new StringValue( at, "  Test   Test  " );
        
        ssv.normalize();
        String normalized = ssv.getNormalizedValue();
        
        assertEquals( "test test", normalized );
        assertEquals( "  Test   Test  ", ssv.getString() );
        
        StringValue ssvSer = deserializeValue( serializeValue( ssv ), at );
        
        assertEquals( ssv, ssvSer );
   }


    /**
     * Test serialization of a StringValue which does not have a normalized value
     */
    @Test public void testNoNormalizedStringValueSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        // First check with a value which will be normalized
        StringValue ssv = new StringValue( at, "test" );
        
        ssv.normalize();
        String normalized = ssv.getNormalizedValue();
        
        assertEquals( "test", normalized );
        assertEquals( "test", ssv.getString() );
        
        StringValue ssvSer = deserializeValue( serializeValue( ssv ), at );
        
        assertEquals( ssv, ssvSer );
   }


    /**
     * Test serialization of a null StringValue
     */
    @Test public void testNullStringValueSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        // First check with a value which will be normalized
        StringValue ssv = new StringValue( at );
        
        ssv.normalize();
        String normalized = ssv.getNormalizedValue();
        
        assertNull( normalized );
        assertNull( ssv.get() );
        
        StringValue ssvSer = deserializeValue( serializeValue( ssv ), at );
        
        assertEquals( ssv, ssvSer );
   }


    /**
     * Test serialization of an empty StringValue
     */
    @Test public void testEmptyStringValueSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        // First check with a value which will be normalized
        StringValue ssv = new StringValue( at, "" );
        
        ssv.normalize();
        String normalized = ssv.getNormalizedValue();
        
        assertEquals( "", normalized );
        assertEquals( "", ssv.getString() );
        
        StringValue ssvSer = deserializeValue( serializeValue( ssv ), at );
        
        assertEquals( ssv, ssvSer );
    }


    /**
     * Test serialization of an empty StringValue
     */
    @Test public void testStringValueEmptyNormalizedSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        // First check with a value which will be normalized
        StringValue ssv = new StringValue( at, "  " );
        
        assertEquals( "  ", ssv.getString() );
        
        StringValue ssvSer = deserializeValue( serializeValue( ssv ), at );
        
        assertEquals( ssv, ssvSer );
    }
}
