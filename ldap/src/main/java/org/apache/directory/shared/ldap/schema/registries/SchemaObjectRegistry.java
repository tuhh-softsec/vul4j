/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.shared.ldap.schema.registries;


import java.util.Iterator;

import javax.naming.NamingException;

import org.apache.directory.shared.ldap.schema.SchemaObject;


/**
 * Common schema object registry interface.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface SchemaObjectRegistry<T extends SchemaObject> extends Iterable<T>
{
    /**
     * Checks to see if an SchemaObject exists in the registry.
     * 
     * @param id the object identifier or name of the SchemaObject
     * @return true if a SchemaObject definition exists for the oid, false
     * otherwise
     */
    boolean contains( String id );
    
    
    /**
     * Gets the name of the schema this schema object is associated with.
     *
     * @param id the object identifier or the name
     * @return the schema name
     * @throws NamingException if the schema object does not exist
     */
    String getSchemaName( String id ) throws NamingException;

    
    /**
     * Gets an iterator over the registered schema objects in the registry.
     *
     * @return an Iterator of homogeneous schema objects
     */
    Iterator<T> iterator();

    
    /**
     * Gets an iterator over the registered schema objects'OID in the registry.
     *
     * @return an Iterator of OIDs
     */
    Iterator<String> oidsIterator();

    
    /**
     * Looks up a SchemaObject by its unique Object Identifier or by name.
     *
     * @param id the object identifier or name
     * @return the SchemaObject instance for the id
     * @throws NamingException if the SchemaObject does not exist
     */
    T lookup( String id ) throws NamingException;


    /**
     * Registers a new SchemaObject with this registry.
     *
     * @param schemaObject the SchemaObject to register
     * @throws NamingException if the SchemaObject is already registered or
     * the registration operation is not supported
     */
    void register( T schemaObject ) throws NamingException;


    /**
     * Removes the SchemaObject registered with this registry.
     * 
     * @param numericOid the numeric identifier
     * @throws NamingException if the numeric identifier is invalid
     */
    void unregister( String numericOid ) throws NamingException;
}
