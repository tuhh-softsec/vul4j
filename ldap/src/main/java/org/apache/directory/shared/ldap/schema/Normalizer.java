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
package org.apache.directory.shared.ldap.schema;


import javax.naming.NamingException;

import org.apache.directory.shared.ldap.entry.Value;
import org.apache.directory.shared.ldap.schema.registries.Registries;


/**
 * Converts attribute values to a canonical form.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public abstract class Normalizer extends LoadableSchemaObject
{
    /** The serialversionUID */
    private static final long serialVersionUID = 1L;

    /**
     * The Normalizer base constructor. We use it's MR OID to
     * initialize the SchemaObject instance
     * 
     * @param oid The associated OID. It's the element's MR OID
     */
    protected Normalizer( String oid )
    {
        super( SchemaObjectType.NORMALIZER, oid );
    }


    /**
     * Use this default constructor when the Normalizer must be instantiated
     * before setting the OID.
     */
    protected Normalizer()
    {
        super( SchemaObjectType.NORMALIZER );
    }


	/**
     * Gets the normalized value.
     * 
     * @param value the value to normalize. It must *not* be null !
     * @return the normalized form for a value
     * @throws NamingException if an error results during normalization
     */
    public abstract Value<?> normalize( Value<?> value ) throws NamingException;

    /**
     * Gets the normalized value.
     * 
     * @param value the value to normalize. It must *not* be null !
     * @return the normalized form for a value
     * @throws NamingException if an error results during normalization
     */
    public abstract String normalize( String value ) throws NamingException;
    
    
    /**
     * Associate the registries to the normalizer, if needed.
     *
     * @param registries The Registries
     */
    public void applyRegistries( Registries registries )
    {
        // Do nothing. The extended class will store the Registries if needed
    }
    
}
