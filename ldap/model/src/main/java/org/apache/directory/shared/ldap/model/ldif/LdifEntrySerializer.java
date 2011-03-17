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
package org.apache.directory.shared.ldap.model.ldif;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class which serialize and deserialize a LdifEntry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifEntrySerializer
{
    /** The LoggerFactory used by this class */
    protected static final Logger LOG = LoggerFactory.getLogger( LdifEntrySerializer.class );

    /**
     * Private constructor.
     */
    private LdifEntrySerializer()
    {
    }

    
    /**
     * Serializes a LdifEntry instance.
     * 
     * @param principal The LdifEntry instance to serialize
     * @param out The stream into which we will write the serialized instance
     * @throws IOException If the stream can't be written
     */
    public static void serialize( LdifEntry ldifEntry, ObjectOutput out ) throws IOException
    {
        // The changeType
        ldifEntry.writeExternal( out );
        
        out.flush();
    }
    
    
    /**
     * Deserializes a LdifEntry instance.
     * 
     * @param schemaManager The SchemaManager (can be null)
     * @param in The input stream from which the LdifEntry is read
     * @return a deserialized LdifEntry
     * @throws IOException If the stream can't be read
     */
    public static LdifEntry deserialize( SchemaManager schemaManager, ObjectInput in )
        throws IOException, LdapInvalidDnException
    {
        LdifEntry ldifEntry = new LdifEntry();
        
        try
        {
            ldifEntry.readExternal( in );
        }
        catch ( ClassNotFoundException cnfe )
        {
            throw new IOException( cnfe.getMessage() );
        }

        return ldifEntry;
    }
}
