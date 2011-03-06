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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class which serialize and deserialize an AttributeTypeAndValue
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class AvaSerializer
{
    /** The LoggerFactory used by this class */
    protected static final Logger LOG = LoggerFactory.getLogger( AvaSerializer.class );


    /**
     * Private constructor.
     */
    private AvaSerializer()
    {
    }


    /**
     * Serialize an AttributeTypeAndValue object.
     * 
     * An AttributeTypeAndValue is composed of  a type and a value.
     * The data are stored following the structure :
     * 
     * <li>upName</li> The User provided ATAV
     * <li>start</li> The position of this ATAV in the Dn
     * <li>length</li> The ATAV length
     * <li>upType</li> The user Provided Type
     * <li>normType</li> The normalized AttributeType
     * <li>isHR<li> Tells if the value is a String or not
     * <p>
     * if the value is a String :
     * <li>upValue</li> The User Provided value.
     * <li>value</li> The normalized value.
     * <p>
     * if the value is binary :
     * <li>upValueLength</li>
     * <li>upValue</li> The User Provided value.
     * <li>valueLength</li>
     * <li>value</li> The normalized value.
     *
     * @param ava the AttributeTypeAndValue to serialize
     * @param out the OutputStream in which the atav will be serialized
     * @throws IOException If we can't serialize the atav
     */
    public static void serialize( Ava ava, ObjectOutput out ) throws IOException
    {
        ava.writeExternal( out );
        out.flush();
    }
    
    
    /**
     * Deserialize an AttributeTypeAndValue object
     * 
     * We read back the data to create a new ATAV. The structure 
     * read is exposed in the {@link Ava#writeExternal(ObjectOutput)}
     * method<p>
     * 
     * @param in the input stream
     * @throws IOException If the input stream can't be read
     * @return The constructed AttributeTypeAndValue
     */
    public static Ava deserialize( SchemaManager schemaManager, ObjectInput in ) 
        throws IOException, LdapInvalidDnException
    {
        Ava ava = new Ava( schemaManager );
        
        try
        {
            ava.readExternal( in );
        }
        catch ( ClassNotFoundException cnfe )
        {
            throw new IOException( cnfe.getMessage() );
        }
        
        return ava;
    }
}
