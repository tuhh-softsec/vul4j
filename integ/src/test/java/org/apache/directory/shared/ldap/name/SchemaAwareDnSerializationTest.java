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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.apache.directory.shared.ldap.schemamanager.impl.DefaultSchemaManager;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;

/**
 * Test the Dn Serialization
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class SchemaAwareDnSerializationTest
{
    private static SchemaManager schemaManager;

    /**
     * Initialize OIDs maps for normalization
     */
    @BeforeClass
    public static void setup() throws Exception
    {
        schemaManager = new DefaultSchemaManager();
    }

    
    @Test
    public void testDnFullSerialization() throws IOException, LdapException, ClassNotFoundException
    {
        Dn dn1 = new Dn( schemaManager, "gn=john + cn=doe, dc=example, dc=com" );
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        dn1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Dn dn2 = new Dn( schemaManager );
        dn2.readExternal( in );

        assertEquals( dn1, dn2 );
    }


    @Test
    public void testDnEmptySerialization() throws IOException, LdapException, ClassNotFoundException
    {
        Dn dn1 = new Dn( schemaManager );
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        dn1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Dn dn2 = new Dn( schemaManager );
        dn2.readExternal( in );

        assertEquals( dn1, dn2 );
    }


    @Test
    public void testDnSimpleSerialization() throws IOException, LdapException, ClassNotFoundException
    {
        Dn dn1 = new Dn( schemaManager, "Cn = Doe" );
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        dn1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Dn dn2 = new Dn( schemaManager );
        dn2.readExternal( in );

        assertEquals( dn1, dn2 );
        assertEquals( "Cn = Doe", dn2.getName() );
        assertEquals( "2.5.4.3=doe", dn2.getNormName() );
    }
    /**
     * Test the serialization of a Dn
     *
     * @throws Exception
     */
    @Test
    public void testNameSerialization() throws Exception
    {
        Dn dn = new Dn( "ou= Some   People   + dc=  And   Some anImAls,dc = eXample,dc= cOm" );
        dn.normalize( schemaManager );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        dn.writeExternal( out );

        byte[] data = baos.toByteArray();
        ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( data ) );
        
        Dn dn2 = new Dn( schemaManager );
        dn2.readExternal( in );

        assertEquals( dn, dn2 );
    }


    @Test
    public void testSerializeEmptyDN() throws Exception
    {
        Dn dn = Dn.EMPTY_DN;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        dn.writeExternal( out );

        byte[] data = baos.toByteArray();
        ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( data ) );
        
        Dn dn2 = new Dn( schemaManager );
        dn2.readExternal( in );

        assertEquals( dn, dn2 );
    }


    /**
     * Test the serialization of a Dn
     *
     * @throws Exception
     */
    @Test
    public void testNameStaticSerialization() throws Exception
    {
        Dn dn = new Dn( "ou= Some   People   + dc=  And   Some anImAls,dc = eXample,dc= cOm" );
        dn.normalize( schemaManager );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        dn.writeExternal( out );

        byte[] data = baos.toByteArray();
        ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( data ) );
        
        Dn dn2 = new Dn( schemaManager );
        dn2.readExternal( in );

        assertEquals( dn, dn2 );
    }


    @Ignore
    @Test public void testSerializationPerfs() throws Exception
    {
        Dn dn = new Dn( "ou= Some   People   + dc=  And   Some anImAls,dc = eXample,dc= cOm" );
        dn.normalize( schemaManager );

        long t0 = System.currentTimeMillis();

        for ( int i = 0; i < 1000; i++ )
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream( baos );

            dn.writeExternal( out );

            byte[] data = baos.toByteArray();
            ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( data ) );

            Dn dn1 = new Dn( schemaManager );
            dn1.readExternal( in );
        }

        long t1 = System.currentTimeMillis();

        System.out.println( "delta :" + ( t1 - t0) );

        long t2 = System.currentTimeMillis();

        for ( int i = 0; i < 1000000; i++ )
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream( baos );

            dn.writeExternal( out );

            byte[] data = baos.toByteArray();
            ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( data ) );

            Dn dn1 = new Dn( schemaManager );
            dn1.readExternal( in );

            dn.normalize( schemaManager );
        }

        long t3 = System.currentTimeMillis();

        System.out.println( "delta :" + ( t3 - t2) );

        //assertEquals( dn, DnSerializer.deserialize( in ) );
    }

    @Test
    public void testStaticSerializeEmptyDN() throws Exception
    {
        Dn dn = Dn.EMPTY_DN;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        dn.writeExternal( out );

        byte[] data = baos.toByteArray();
        ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( data ) );
        
        Dn dn2 = new Dn( schemaManager );
        dn2.readExternal( in );

        assertEquals( dn, dn2 );
    }
}
