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
package org.apache.directory.shared.ldap.name;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.name.Ava;
import org.apache.directory.shared.ldap.model.name.AvaSerializer;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.apache.directory.shared.ldap.schemamanager.impl.DefaultSchemaManager;
import org.apache.directory.shared.util.Strings;
import org.junit.BeforeClass;
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

    private static SchemaManager schemaManager;

    @BeforeClass
    public static void setup() throws Exception
    {
        schemaManager = new DefaultSchemaManager();
    }
    
    /**
     * Test a null AttributeTypeAndValue
     */
    @Test
    public void testAttributeTypeAndValueNull()
    {
        Ava atav = new Ava( schemaManager );
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
        Ava atav = new Ava( schemaManager, "CN", (String)null );
        assertEquals( "CN=", atav.toString() );
        assertEquals( "2.5.4.3=", atav.getNormName() );
        assertEquals( "CN=", atav.getUpName() );
        
        atav = new Ava( schemaManager, "  CN  ", (String)null );
        assertEquals( "  CN  =", atav.toString() );
        assertEquals( "2.5.4.3=", atav.getNormName() );
        assertEquals( "  CN  =", atav.getUpName() );

        atav = new Ava( schemaManager, "cn", (String)null );
        assertEquals( "cn=", atav.toString() );
        assertEquals( "2.5.4.3=", atav.getNormName() );
        assertEquals( "cn=", atav.getUpName() );
        
        atav = new Ava( schemaManager, "  cn  ", (String)null );
        assertEquals( "  cn  =", atav.toString() );
        assertEquals( "2.5.4.3=", atav.getNormName() );
        assertEquals( "  cn  =", atav.getUpName() );
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
        Ava atav = new Ava( schemaManager, "cn", "b" );
        assertEquals( "cn=b", atav.toString() );
        assertEquals( "2.5.4.3=b", atav.getNormName() );
        assertEquals( "cn=b", atav.getUpName() );
    }


    /**
     * Compares two equals atavs
     */
    @Test
    public void testCompareToEquals() throws LdapException
    {
        Ava atav1 = new Ava( schemaManager, "cn", "b" );
        Ava atav2 = new Ava( schemaManager, "cn", "b" );

        assertTrue( atav1.equals( atav2 ) );
    }


    /**
     * Compares two equals atavs but with a type in different case
     */
    @Test
    public void testCompareToEqualsCase() throws LdapException
    {
        Ava atav1 = new Ava( schemaManager, "cn", "b" );
        Ava atav2 = new Ava( schemaManager, "CN", "b" );

        assertTrue( atav1.equals( atav2 ) );
    }


    /** Serialization tests ------------------------------------------------- */

    /**
     * Test serialization of a simple ATAV
     */
    @Test
    public void testStringAtavSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Ava atav = new Ava( schemaManager, "CN", "Test" );

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
        byte[] normValue = Strings.getBytesUtf8("Test");

        Ava atav = new Ava( schemaManager, "userPKCS12", normValue );

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
        Ava atav = new Ava( schemaManager );

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
    public void testNullUpValueSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Ava atav = new Ava( schemaManager, "uid", (String)null );

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

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        AvaSerializer.serialize(atav, out);
        out.flush();

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Ava atav2 = AvaSerializer.deserialize(in);

        assertEquals( atav, atav2 );
    }


    @Test
    public void testBinaryAtavStaticSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        byte[] upValue = Strings.getBytesUtf8("  Test  ");

        Ava atav = new Ava( schemaManager, "userPKCS12", upValue );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        AvaSerializer.serialize( atav, out );
        out.flush();

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Ava atav2 = AvaSerializer.deserialize(in);

        assertEquals( atav, atav2 );
    }


    /**
     * Test static serialization of a simple ATAV
     */
    @Test
    public void testNullAtavStaticSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Ava atav = new Ava( schemaManager );

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


    @Test( expected = IOException.class )
    public void testNullNormValueStaticSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Ava atav = new Ava( schemaManager, "UID", (String)null );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        AvaSerializer.serialize(atav, out);
        fail();
    }


    @Test
    public void testEmptyNormValueStaticSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Ava atav = new Ava( schemaManager, "UID", (String)"" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        AvaSerializer.serialize(atav, out);
        out.flush();

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Ava atav2 = AvaSerializer.deserialize(in);

        assertEquals( atav, atav2 );
    }
}
