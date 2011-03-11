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
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.directory.shared.ldap.model.entry.DefaultEntryAttribute;
import org.apache.directory.shared.ldap.model.entry.DefaultModification;
import org.apache.directory.shared.ldap.model.entry.EntryAttribute;
import org.apache.directory.shared.ldap.model.entry.Modification;
import org.apache.directory.shared.ldap.model.entry.ModificationOperation;
import org.apache.directory.shared.ldap.model.entry.ModificationSerializer;
import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.apache.directory.shared.ldap.schemamanager.impl.DefaultSchemaManager;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;


/**
 * Test the DefaultModification class
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class SchemaAwareModificationTest
{
    private static SchemaManager schemaManager;
    private static AttributeType CN_AT;

    /**
     * Initialize OIDs maps for normalization
     */
    @BeforeClass
    public static void setup() throws Exception
    {
        schemaManager = new DefaultSchemaManager();
        CN_AT = schemaManager.getAttributeType( "cn" );
    }

    
    /**
     * Serialize a DefaultModification
     */
    private ByteArrayOutputStream serializeValue( Modification modification ) throws IOException
    {
        ObjectOutputStream oOut = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try
        {
            oOut = new ObjectOutputStream( out );
            
            ModificationSerializer.serialize( modification, oOut );
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
     * Deserialize a DefaultModification
     */
    private Modification deserializeValue( ByteArrayOutputStream out ) throws IOException, ClassNotFoundException
    {
        ObjectInputStream oIn = null;
        ByteArrayInputStream in = new ByteArrayInputStream( out.toByteArray() );

        try
        {
            oIn = new ObjectInputStream( in );
            Modification modification = ModificationSerializer.deserialize( schemaManager, oIn );

            return modification;
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
    public void testCreateServerModification()
    {
        EntryAttribute attribute = new DefaultEntryAttribute( "cn", CN_AT );
        attribute.add( "test1", "test2" );
        
        Modification mod = new DefaultModification( CN_AT, ModificationOperation.ADD_ATTRIBUTE, attribute );
        Modification clone = mod.clone();
        
        attribute.remove( "test2" );
        
        EntryAttribute clonedAttribute = clone.getAttribute();
        
        assertEquals( 1, mod.getAttribute().size() );
        assertTrue( mod.getAttribute().contains( "test1" ) );

        assertEquals( 2, clonedAttribute.size() );
        assertTrue( clone.getAttribute().contains( "test1" ) );
        assertTrue( clone.getAttribute().contains( "test2" ) );
    }
    
    
    @Test
    public void testSerializationModificationADD() throws ClassNotFoundException, IOException
    {
        EntryAttribute attribute = new DefaultEntryAttribute( "cn", CN_AT );
        attribute.add( "test1", "test2" );
        
        DefaultModification mod = new DefaultModification( ModificationOperation.ADD_ATTRIBUTE, attribute );
        
        Modification modSer = deserializeValue( serializeValue( mod ) );
        
        assertEquals( mod, modSer );
    }
    
    
    @Test
    public void testSerializationModificationREPLACE() throws ClassNotFoundException, IOException
    {
        EntryAttribute attribute = new DefaultEntryAttribute( "cn", CN_AT );
        attribute.add( "test1", "test2" );
        
        DefaultModification mod = new DefaultModification( ModificationOperation.REPLACE_ATTRIBUTE, attribute );
        
        Modification modSer = deserializeValue( serializeValue( mod ) );
        
        assertEquals( mod, modSer );
    }
    
    
    @Test
    public void testSerializationModificationREMOVE() throws ClassNotFoundException, IOException
    {
        EntryAttribute attribute = new DefaultEntryAttribute( "cn", CN_AT );
        attribute.add( "test1", "test2" );
        
        DefaultModification mod = new DefaultModification( ModificationOperation.REMOVE_ATTRIBUTE, attribute );
        
        Modification modSer = deserializeValue( serializeValue( mod ) );
        
        assertEquals( mod, modSer );
    }
    
    
    @Test
    public void testSerializationModificationNoAttribute() throws ClassNotFoundException, IOException
    {
        DefaultModification mod = new DefaultModification();
        
        mod.setOperation( ModificationOperation.ADD_ATTRIBUTE );
        
        Modification modSer = deserializeValue( serializeValue( mod ) );
        
        assertEquals( mod, modSer );
    }
}
