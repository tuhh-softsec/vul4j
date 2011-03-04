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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.directory.shared.ldap.model.entry.DefaultEntryAttribute;
import org.apache.directory.shared.ldap.model.entry.EntryAttribute;
import org.apache.directory.shared.ldap.model.entry.EntryAttributeSerializer;
import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.apache.directory.shared.ldap.schemamanager.impl.DefaultSchemaManager;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;

/**
 * Test the EntryAttribute Serialization
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class SchemaAwareEntryAttributeSerializerTest
{
    private static byte[] data1 = new byte[] {0x01, 0x02, 0x03, 0x04};
    private static byte[] data2 = new byte[] {0x05, 0x06, 0x07, 0x08};
    private static byte[] data3 = new byte[] {0x09, 0x0A, 0x0B, 0x0C};
    
    private static AttributeType cn = null;
    private static AttributeType userCertificate = null;

    private static SchemaManager schemaManager;

    /**
     * Initialize OIDs maps for normalization
     */
    @BeforeClass
    public static void setup() throws Exception
    {
        schemaManager = new DefaultSchemaManager();
        cn = schemaManager.getAttributeType( "cn" );
        userCertificate = schemaManager.getAttributeType( "userCertificate" );
    }
    
    
    @Test
    public void testEntryAttributeNoStringValueSerialization() throws IOException
    {
        EntryAttribute attribute1 = new DefaultEntryAttribute( cn );
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        EntryAttributeSerializer.serialize( attribute1, out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        EntryAttribute attribute2 = EntryAttributeSerializer.deserialize( schemaManager, in );

        assertEquals( attribute1, attribute2 );
    }
    
    
    @Test
    public void testEntryAttributeOneStringValueSerialization() throws IOException
    {
        EntryAttribute attribute1 = new DefaultEntryAttribute( "CommonName", cn, "test" );
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        EntryAttributeSerializer.serialize( attribute1, out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        EntryAttribute attribute2 = EntryAttributeSerializer.deserialize( schemaManager, in );

        assertEquals( attribute1, attribute2 );
        assertEquals( "CommonName", attribute2.getUpId() );
    }
    
    
    @Test
    public void testEntryAttributeManyStringValuesSerialization() throws IOException
    {
        EntryAttribute attribute1 = new DefaultEntryAttribute( "CN", cn, "test1", "test2", "test3" );
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        EntryAttributeSerializer.serialize( attribute1, out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        EntryAttribute attribute2 = EntryAttributeSerializer.deserialize( schemaManager, in );

        assertEquals( attribute1, attribute2 );
        assertEquals( "CN", attribute2.getUpId() );
    }


    @Test
    public void testEntryAttributeNoBinaryValueSerialization() throws IOException
    {
        EntryAttribute attribute1 = new DefaultEntryAttribute( userCertificate );
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        EntryAttributeSerializer.serialize( attribute1, out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        EntryAttribute attribute2 = EntryAttributeSerializer.deserialize( schemaManager, in );

        assertEquals( attribute1, attribute2 );
    }
    
    
    @Test
    public void testEntryAttributeOneBinaryValueSerialization() throws IOException
    {
        EntryAttribute attribute1 = new DefaultEntryAttribute( userCertificate, data1 );
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        EntryAttributeSerializer.serialize( attribute1, out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        EntryAttribute attribute2 = EntryAttributeSerializer.deserialize( schemaManager, in );

        assertEquals( attribute1, attribute2 );
    }
    
    
    @Test
    public void testEntryAttributeManyBinaryValuesSerialization() throws IOException
    {
        EntryAttribute attribute1 = new DefaultEntryAttribute( "UserCertificate", userCertificate, data1, data2, data3 );
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        EntryAttributeSerializer.serialize( attribute1, out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        EntryAttribute attribute2 = EntryAttributeSerializer.deserialize( schemaManager, in );

        assertEquals( attribute1, attribute2 );
        assertEquals( "UserCertificate", attribute2.getUpId() );
    }
}
