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

import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.name.DnSerializer;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class which serialize and deserialize an Entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntrySerializer
{
    /** The LoggerFactory used by this class */
    protected static final Logger LOG = LoggerFactory.getLogger( EntrySerializer.class );

    /**
     * Private constructor.
     */
    private EntrySerializer()
    {
    }

    
    /**
     * Serializes a Entry instance.
     * 
     * @param principal The Entry instance to serialize
     * @param out The stream into which we will write the serialized instance
     * @throws IOException If the stream can't be written
     */
    public static void serialize( Entry entry, ObjectOutput out ) throws IOException
    {
        // First, the Dn
        DnSerializer.serialize( entry.getDn(), out );

        // Then the attributes.
        int nbAttributes =  entry.size();
        out.writeInt( nbAttributes );

        // Iterate through the attributes
        if ( nbAttributes > 0 )
        {
            for ( EntryAttribute attribute : entry )
            {
                EntryAttributeSerializer.serialize( attribute, out );
            }
        }
        
        out.flush();
    }
    
    
    /**
     * Deserializes a Entry instance.
     * 
     * @param schemaManager The schemaManager instance
     * @param in The input stream from which the Entry is read
     * @return a deserialized Entry
     * @throws IOException If the stream can't be read
     */
    public static Entry deserialize( SchemaManager schemaManager, ObjectInput in ) throws IOException, LdapInvalidDnException
    {
        // The Dn
        Dn dn = DnSerializer.deserialize( schemaManager, in );

        // The attributes
        int nbAttributes = in.readInt();
        
        EntryAttribute[] attributes = null;
        
        if ( nbAttributes > 0 )
        {
            attributes = new EntryAttribute[ nbAttributes ];
            
            for ( int i = 0; i < nbAttributes; i++ )
            {
                EntryAttribute attribute = EntryAttributeSerializer.deserialize( schemaManager, in );
                attributes[i] = attribute;
            }
        }
        
        Entry entry = new DefaultEntry( schemaManager, dn, attributes );

        return entry;
    }
}
