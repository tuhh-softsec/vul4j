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

import java.util.List;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.schema.registries.Registries;
import org.apache.directory.shared.ldap.schema.registries.Schema;
import org.apache.directory.shared.ldap.schema.registries.SchemaLoader;

/**
 * A class used to manage access to the Schemas and Registries. It's associated 
 * with a SchemaLoader, in charge of loading the schemas from the disk.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface SchemaManager
{
    //---------------------------------------------------------------------------------
    // Schema loading methods
    //---------------------------------------------------------------------------------
    /**
     * Load some Schemas into the registries. The Registries is checked after the 
     * schemas have been loaded, and if there is an error, the method returns false
     * and the registries is kept intact.
     * <br>
     * The Schemas must be enabled, and only enabled SchemaObject will be loaded.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     * 
     * @param schemas the Schemas to load
     * @return true if the schemas have been loaded and the registries is consistent
     * @throws Exception @TODO 
     */
    boolean load( Schema... schemas ) throws Exception;

    
    /**
     * Load some Schemas into the registries. The Registries is checked after the 
     * schemas have been loaded, and if there is an error, the method returns false
     * and the registries is kept intact.
     * <br>
     * The Schemas must be enabled, and only enabled SchemaObject will be loaded.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     * 
     * @param schemas the Schemas' name to load
     * @return true if the schemas have been loaded and the registries is consistent
     * @throws Exception @TODO 
     */
    boolean load( String... schemas ) throws Exception;


    /**
     * Load some Schemas into the registries, and loads all of the schemas they depend
     * on. The Registries is checked after the schemas have been loaded, and if there 
     * is an error, the method returns false and the registries is kept intact.
     * <br>
     * The Schemas must be enabled, and only enabled SchemaObject will be loaded.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     * 
     * @param schemas the Schemas to load
     * @return true if the schemas have been loaded and the registries is consistent
     * @throws Exception @TODO 
     */
    boolean loadWithDeps( Schema... schemas ) throws Exception;


    /**
     * Load some Schemas into the registries, and loads all of the schemas they depend
     * on. The Registries is checked after the schemas have been loaded, and if there 
     * is an error, the method returns false and the registries is kept intact.
     * <br>
     * The Schemas must be enabled, and only enabled SchemaObject will be loaded.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     * 
     * @param schemas the Schemas' name to load
     * @return true if the schemas have been loaded and the registries is consistent
     * @throws Exception @TODO 
     */
    boolean loadWithDeps( String... schemas ) throws Exception;
    
    
    /**
     * Load Schemas into the registries, even if there are some errors in the schemas. 
     * The Registries is checked after the schemas have been loaded. Even if we have 
     * errors, the registries will be updated.
     * <br>
     * The Schemas must be enabled, and only enabled SchemaObject will be loaded.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     * 
     * @param schemas the Schemas to load, if enabled
     * @return true if the schemas have been loaded
     * @throws Exception @TODO 
     */
    boolean loadRelaxed( Schema... schemas ) throws Exception;
    
    
    /**
     * Load Schemas into the registries, even if there are some errors in the schemas. 
     * The Registries is checked after the schemas have been loaded. Even if we have 
     * errors, the registries will be updated.
     * <br>
     * The Schemas must be enabled, and only enabled SchemaObject will be loaded.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     * 
     * @param schemas the Schemas' name to load, if enabled
     * @return true if the schemas have been loaded and the registries is consistent
     * @throws Exception @TODO 
     */
    boolean loadRelaxed( String... schemas ) throws Exception;


    /**
     * Load some Schemas into the registries, and loads all of the schemas they depend
     * on. The Registries is checked after the schemas have been loaded. Even if we have 
     * errors, the registries will be updated.
     * <br>
     * The Schemas must be enabled, and only enabled SchemaObject will be loaded.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     * 
     * @param schemas the Schemas to load
     * @return true if the schemas have been loaded
     * @throws Exception @TODO 
     */
    boolean loadWithDepsRelaxed( Schema... schemas ) throws Exception;


    /**
     * Load some Schemas into the registries, and loads all of the schemas they depend
     * on. The Registries is checked after the schemas have been loaded. Even if we have 
     * errors, the registries will be updated.
     * <br>
     * The Schemas must be enabled, and only enabled SchemaObject will be loaded.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     * 
     * @param schemas the Schemas' name to load
     * @return true if the schemas have been loaded
     * @throws Exception @TODO 
     */
    boolean loadWithDepsRelaxed( String... schemas ) throws Exception;
    
    
    /**
     * Load Schemas into the Registries, even if they are disabled. The disabled
     * SchemaObject from an enabled schema will also be loaded. The Registries will
     * be checked after the schemas have been loaded. Even if we have errors, the
     * Registries will be updated.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     *
     * @param schemas The Schemas to load
     * @return true if the schemas have been loaded
     * @throws Exception @TODO 
     */
    boolean loadDisabled( Schema... schemas ) throws Exception;
    
    
    /**
     * Load Schemas into the Registries, even if they are disabled. The disabled
     * SchemaObject from an enabled schema will also be loaded. The Registries will
     * be checked after the schemas have been loaded. Even if we have errors, the
     * Registries will be updated.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     *
     * @param schemas The Schemas' name to load
     * @return true if the schemas have been loaded
     * @throws Exception @TODO 
     */
    boolean loadDisabled( String... schemas ) throws Exception;
    
    
    /**
     * Load all the enabled schema into the Registries. The Registries is strict,
     * any inconsistent schema will be rejected. 
     *
     * @return true if the schemas have been loaded
     * @throws Exception @TODO
     */
    boolean loadAllEnabled() throws Exception;
    
    
    /**
     * Load all the enabled schema into the Registries. The Registries is relaxed,
     * even inconsistent schema will be loaded. 
     *
     * @return true if the schemas have been loaded
     * @throws Exception @TODO
     */
    boolean loadAllEnabledRelaxed() throws Exception;
    
    boolean unload( Schema... schemas );
    boolean unload( String... schemas );
    
    //---------------------------------------------------------------------------------
    // Other Schema methods
    //---------------------------------------------------------------------------------
    /**
     * Enables a set of Schemas, and returns true if all the schema have been
     * enabled, with all the dependent schemas, and if the registries is 
     * still consistent.
     * 
     * If the modification is ok, the Registries will be updated. 
     * 
     *  @param schemas The list of schemas to enable
     *  @return true if the Registries is still consistent, false otherwise.
     */
    boolean enable( Schema... schemas ) throws Exception;
    

    /**
     * Enables a set of Schemas, and returns true if all the schema have been
     * enabled, with all the dependent schemas, and if the registries is 
     * still consistent.
     * 
     * If the modification is ok, the Registries will be updated.
     *  
     *  @param schemas The list of schemas to enable
     *  @return true if the Registries is still consistent, false otherwise.
     */
    boolean enable( String... schemas ) throws Exception;

    boolean enableRelaxed( Schema... schemas );
    boolean enableRelaxed( String... schemas );
    
    
    boolean disable( Schema... schemas );
    boolean disable( String... schemas );

    boolean disabledRelaxed( Schema... schemas );
    boolean disabledRelaxed( String... schemas );


    /**
     * Check that the Schemas are consistent regarding the current Registries.
     * 
     * @param schemas The schemas to check
     * @return true if the schemas can be loaded in the registries
     */
    boolean verify( Schema... schemas );
    boolean verify( String... schemas );
    
    
    /**
     * @return The Registries
     */
    Registries getRegistries();
    void setRegistries( Registries registries );
    
    
    /**
     * @return The errors obtained when checking the registries
     */
    List<Throwable> getErrors();
    
    
    void setSchemaLoader( SchemaLoader schemaLoader );


    /**
     * @return the namingContext
     */
    LdapDN getNamingContext();


    /**
     * Initializes the SchemaService
     *
     * @throws Exception If the initialization fails
     */
    void initialize() throws Exception;
    
    
    /**
     * @return The used loader
     */
    SchemaLoader getLoader();
}
