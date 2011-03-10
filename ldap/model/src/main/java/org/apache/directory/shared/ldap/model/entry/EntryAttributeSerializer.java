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
package org.apache.directory.shared.ldap.model.entry;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class which serialize and deserialize a EntryAttribute.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryAttributeSerializer
{
    /** The LoggerFactory used by this class */
    protected static final Logger LOG = LoggerFactory.getLogger( EntryAttributeSerializer.class );

    /**
     * Private constructor.
     */
    private EntryAttributeSerializer()
    {
    }

    
    /**
     * Serializes a EntryAttribute instance.
     * 
     * @param principal The EntryAttribute instance to serialize
     * @param out The stream into which we will write the serialized instance
     * @throws IOException If the stream can't be written
     */
    public static void serialize( EntryAttribute attribute, ObjectOutput out ) throws IOException
    {
        attribute.writeExternal( out );
        out.flush();
    }
    
    
    /**
     * Deserializes a EntryAttribute instance.
     * 
     * @param attributeType The attributeType instance
     * @param in The input stream from which the EntryAttribute is read
     * @return a deserialized EntryAttribute
     * @throws IOException If the stream can't be read
     */
    public static EntryAttribute deserialize( SchemaManager schemaManager, ObjectInput in ) throws IOException
    {
        EntryAttribute entryAttribute = new DefaultEntryAttribute();
        
        try
        {
            entryAttribute.readExternal( in );
        }
        catch ( ClassNotFoundException cnfe )
        {
            throw new IOException( cnfe.getMessage() );
        }
        
        AttributeType attributeType = null;
        
        if ( schemaManager != null )
        {
            if ( !Strings.isEmpty( entryAttribute.getId() ) )
            { 
                attributeType = schemaManager.getAttributeType( entryAttribute.getId() );
            }
            
            entryAttribute.setAttributeType( attributeType );
        }
        
        return entryAttribute;
    }
}
