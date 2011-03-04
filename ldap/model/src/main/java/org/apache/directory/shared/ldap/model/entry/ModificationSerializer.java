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

import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class which serialize and deserialize a Modification.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModificationSerializer
{
    /** The LoggerFactory used by this class */
    protected static final Logger LOG = LoggerFactory.getLogger( ModificationSerializer.class );

    /**
     * Private constructor.
     */
    private ModificationSerializer()
    {
    }

    
    /**
     * Serializes a Modification instance.
     * 
     * @param principal The Modification instance to serialize
     * @param out The stream into which we will write the serialized instance
     * @throws IOException If the stream can't be written
     */
    public static void serialize( Modification modification, ObjectOutput out ) throws IOException
    {
        // The operation
        out.writeInt( modification.getOperation().getValue() );
        
        // The EntryAttribute
        EntryAttributeSerializer.serialize( modification.getAttribute(), out );
        
        out.flush();
    }
    
    
    /**
     * Deserializes a Modification instance.
     * 
     * @param schemaManager The schemaManager instance
     * @param in The input stream from which the Modification is read
     * @return a deserialized Modification
     * @throws IOException If the stream can't be read
     */
    public static Modification deserialize( SchemaManager schemaManager, ObjectInput in ) throws IOException
    {
        // The operation
        ModificationOperation operation = ModificationOperation.getOperation( in.readInt() );

        // The EntryAttribute
        EntryAttribute attribute = EntryAttributeSerializer.deserialize( schemaManager, in );

        Modification modification = new DefaultModification( operation, attribute );

        return modification;
    }
}
