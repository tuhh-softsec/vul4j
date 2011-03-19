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

import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.name.Ava;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.apache.directory.shared.ldap.schemamanager.impl.DefaultSchemaManager;
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
    public void testAvaEmpty()
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
    public void testAvaSimple() throws LdapException
    {
        Ava atav = new Ava( schemaManager, "cn", "b" );
        assertEquals( "cn=b", atav.toString() );
        assertEquals( "2.5.4.3=b", atav.getNormName() );
        assertEquals( "cn=b", atav.getUpName() );
    }




    /**
     * test a simple AttributeTypeAndValue : a = b
     */
    @Test
    public void testAvaSimpleNorm() throws LdapException
    {
        Ava atav = new Ava( schemaManager, " CommonName ", " This is    a TEST " );
        assertEquals( " CommonName = This is    a TEST ", atav.toString() );
        assertEquals( "2.5.4.3=this is a test", atav.getNormName() );
        assertEquals( " CommonName = This is    a TEST ", atav.getUpName() );
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
}
