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
package org.apache.directory.shared.ldap.model.name;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.apache.directory.shared.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;


/**
 * Test the class AttributeTypeAndValue
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class AvaTest
{
    /** A null schemaManager used in tests */
    SchemaManager schemaManager = null;
    
    /**
     * Test a null AttributeTypeAndValue
     */
    @Test
    public void testAttributeTypeAndValueNull()
    {
        Ava atav = new Ava();
        assertEquals( "", atav.toString() );
        assertEquals( "", atav.getUpName() );
    }


    /**
     * Test a null type for an AttributeTypeAndValue
     */
    @Test
    public void testAttributeTypeAndValueNullType() throws LdapException
    {
        try
        {
            new Ava( schemaManager, null, (String)null );
            fail();
        }
        catch ( LdapException ine )
        {
            assertTrue( true );
        }

    }

    /**
     * Test an invalid type for an AttributeTypeAndValue
     */
    @Test
    public void testAttributeTypeAndValueInvalidType() throws LdapException
    {
        try
        {
            new Ava( schemaManager, "  ", (String)null );
            fail();
        }
        catch ( LdapException ine )
        {
            assertTrue( true );
        }
    }


    /**
     * Test a valid type for an AttributeTypeAndValue
     */
    @Test
    public void testAttributeTypeAndValueValidType() throws LdapException
    {
        Ava atav = new Ava( schemaManager, "A", (String)null );
        assertEquals( "A=", atav.toString() );
        assertEquals( "a=", atav.getNormName() );
        assertEquals( "A=", atav.getUpName() );
        
        atav = new Ava( schemaManager, "  A  ", (String)null );
        assertEquals( "a=", atav.getNormName() );
        assertEquals( "  A  =", atav.toString() );
        assertEquals( "  A  =", atav.getUpName() );
        
        try
        {
            atav = new Ava( schemaManager, null, (String)null );
            fail();
        }
        catch ( LdapInvalidDnException lide )
        {
            assertTrue( true );
        }
    }

    /**
     * test an empty AttributeTypeAndValue
     */
    @Test
    public void testLdapRDNEmpty()
    {
        try
        {
            new Ava( schemaManager, "", "" );
            fail( "Should not occurs ... " );
        }
        catch ( LdapException ine )
        {
            assertTrue( true );
        }
    }


    /**
     * test a simple AttributeTypeAndValue : a = b
     */
    @Test
    public void testLdapRDNSimple() throws LdapException
    {
        Ava atav = new Ava( schemaManager, "a", "b" );
        assertEquals( "a=b", atav.toString() );
        assertEquals( "a=b", atav.getUpName() );
    }


    /**
     * Compares two equals atavs
     */
    @Test
    public void testCompareToEquals() throws LdapException
    {
        Ava atav1 = new Ava( schemaManager, "a", "b" );
        Ava atav2 = new Ava( schemaManager, "a", "b" );

        assertTrue( atav1.equals( atav2 ) );
    }


    /**
     * Compares two equals atavs but with a type in different case
     */
    @Test
    public void testCompareToEqualsCase() throws LdapException
    {
        Ava atav1 = new Ava( schemaManager, "a", "b" );
        Ava atav2 = new Ava( schemaManager, "A", "b" );

        assertTrue( atav1.equals( atav2 ) );
    }


    /**
     * Compare two atavs : the first one is superior because its type is
     * superior
     */
    @Test
    public void testCompareAtav1TypeSuperior() throws LdapException
    {
        Ava atav1 = new Ava( schemaManager, "b", "b" );
            
        Ava atav2 = new Ava( schemaManager, "a", "b" );

        assertFalse( atav1.equals( atav2 ) );
    }


    /**
     * Compare two atavs : the second one is superior because its type is
     * superior
     */
    @Test
    public void testCompareAtav2TypeSuperior() throws LdapException
    {
        Ava atav1 = new Ava( schemaManager, "a", "b" );
        Ava atav2 = new Ava( schemaManager, "b", "b" );

        assertFalse( atav1.equals( atav2 ) );
    }


    /**
     * Compare two atavs : the first one is superior because its type is
     * superior
     */
    @Test
    public void testCompareAtav1ValueSuperior() throws LdapException
    {
        Ava atav1 = new Ava( schemaManager, "a", "b" );
        Ava atav2 = new Ava( schemaManager, "a", "a" );

        assertFalse( atav1.equals( atav2 ) );
    }


    /**
     * Compare two atavs : the second one is superior because its type is
     * superior
     */
    @Test
    public void testCompareAtav2ValueSuperior() throws LdapException
    {
        Ava atav1 = new Ava( schemaManager, "a", "a" );
        Ava atav2 = new Ava( schemaManager, "a", "b" );

        assertFalse( atav1.equals( atav2 ) );
    }


    @Test
    public void testNormalize() throws LdapException
    {
        Ava atav = new Ava( schemaManager, " A ", "a" );

        assertEquals( "a=a", atav.normalize() );

    }


    /** Serialization tests ------------------------------------------------- */

    /**
     * Test serialization of a simple ATAV
     */
    @Test
    public void testStringAtavSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Ava atav = new Ava( schemaManager, "CN", "Test" );

        atav.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        out.writeObject( atav );

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Ava atav2 = (Ava)in.readObject();

        assertEquals( atav, atav2 );
    }


    @Test
    public void testBinaryAtavSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        byte[] upValue = Strings.getBytesUtf8("  Test  ");

        Ava atav = new Ava( schemaManager, "CN", upValue );

        atav.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        out.writeObject( atav );

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Ava atav2 = (Ava)in.readObject();

        assertEquals( atav, atav2 );
    }


    /**
     * Test serialization of a simple ATAV
     */
    @Test
    public void testNullAtavSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Ava atav = new Ava();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        try
        {
            out.writeObject( atav );
            fail();
        }
        catch ( IOException ioe )
        {
            assertTrue( true );
        }
    }


    @Test
    public void testNullNormValueSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Ava atav = new Ava( schemaManager, "CN", (String)null );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        try
        {
            out.writeObject( atav );
            fail();
        }
        catch ( IOException ioe )
        {
            String message = ioe.getMessage();
            assertEquals( "Cannot serialize an wrong ATAV, the upValue should not be null", message );
        }
    }


    @Test
    public void testNullUpValueSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Ava atav = new Ava( schemaManager, "CN", (String)null );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        try
        {
            out.writeObject( atav );
            fail();
        }
        catch ( IOException ioe )
        {
            String message = ioe.getMessage();
            assertEquals( "Cannot serialize an wrong ATAV, the upValue should not be null", message );
        }
    }


    @Test
    public void testEmptyNormValueSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Ava atav = new Ava( schemaManager, "CN", "test" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        out.writeObject( atav );

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Ava atav2 = (Ava)in.readObject();

        assertEquals( atav, atav2 );
    }


    @Test
    public void testEmptyUpValueSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Ava atav = new Ava( schemaManager, "CN", "" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        out.writeObject( atav );

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Ava atav2 = (Ava)in.readObject();

        assertEquals( atav, atav2 );
    }


    /**
     * Test serialization of a simple ATAV
     */
    @Test
    public void testStringAtavStaticSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Ava atav = new Ava( schemaManager, "CN", "Test" );

        atav.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        AvaSerializer.serialize(atav, out);
        out.flush();

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Ava atav2 = AvaSerializer.deserialize( schemaManager, in );

        assertEquals( atav, atav2 );
    }


    @Test
    public void testBinaryAtavStaticSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        byte[] upValue = Strings.getBytesUtf8("  Test  ");

        Ava atav = new Ava( schemaManager, "CN", upValue );

        atav.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        AvaSerializer.serialize(atav, out);
        out.flush();

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Ava atav2 = AvaSerializer.deserialize( schemaManager, in );

        assertEquals( atav, atav2 );
    }


    /**
     * Test static serialization of a simple ATAV
     */
    @Test
    public void testNullAtavStaticSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Ava atav = new Ava();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        try
        {
            AvaSerializer.serialize(atav, out);
            fail();
        }
        catch ( IOException ioe )
        {
            assertTrue( true );
        }
    }


    @Test
    public void testNullUpValueStaticSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Ava atav = new Ava( schemaManager, "CN", (String)null );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        try
        {
            AvaSerializer.serialize(atav, out);
            fail();
        }
        catch ( IOException ioe )
        {
            String message = ioe.getMessage();
            assertEquals( "Cannot serialize an wrong ATAV, the upValue should not be null", message );
        }
    }


    @Test
    public void testEmptyNormValueStaticSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Ava atav = new Ava( schemaManager, "CN", "" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        AvaSerializer.serialize(atav, out);
        out.flush();

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Ava atav2 = AvaSerializer.deserialize( schemaManager, in );

        assertEquals( atav, atav2 );
    }


    @Test
    public void testEmptyUpValueStaticSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Ava atav = new Ava( schemaManager, "CN", "" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        AvaSerializer.serialize(atav, out);
        out.flush();

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Ava atav2 = AvaSerializer.deserialize( schemaManager, in );

        assertEquals( atav, atav2 );
    }
}
