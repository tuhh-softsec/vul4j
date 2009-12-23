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
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.schema.normalizers.OidNormalizer;
import org.apache.directory.shared.ldap.schema.registries.AttributeTypeRegistry;
import org.apache.directory.shared.ldap.schema.registries.ComparatorRegistry;
import org.apache.directory.shared.ldap.schema.registries.DITContentRuleRegistry;
import org.apache.directory.shared.ldap.schema.registries.DITStructureRuleRegistry;
import org.apache.directory.shared.ldap.schema.registries.LdapSyntaxRegistry;
import org.apache.directory.shared.ldap.schema.registries.MatchingRuleRegistry;
import org.apache.directory.shared.ldap.schema.registries.MatchingRuleUseRegistry;
import org.apache.directory.shared.ldap.schema.registries.NameFormRegistry;
import org.apache.directory.shared.ldap.schema.registries.NormalizerRegistry;
import org.apache.directory.shared.ldap.schema.registries.ObjectClassRegistry;
import org.apache.directory.shared.ldap.schema.registries.OidRegistry;
import org.apache.directory.shared.ldap.schema.registries.Registries;
import org.apache.directory.shared.ldap.schema.registries.Schema;
import org.apache.directory.shared.ldap.schema.registries.SchemaLoader;
import org.apache.directory.shared.ldap.schema.registries.SyntaxCheckerRegistry;


/**
 * A class used to manage access to Schema and Registries. It's associated
 * with a SchemaLoader, in charge of loading schema from the disk.
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
     * Load some schema into the registries. The Registries is checked after the 
     * schema have been loaded, and if there is an error, the method returns false
     * and the registries is kept intact.
     * <br>
     * The schema must be enabled, and only enabled SchemaObject will be loaded.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     * 
     * @param schema the schema to load
     * @return true if the schema have been loaded and the registries is consistent
     * @throws Exception @TODO 
     */
    boolean load( Schema... schema ) throws Exception;


    /**
     * Load some schema into the registries. The Registries is checked after the 
     * schema have been loaded, and if there is an error, the method returns false
     * and the registries is kept intact.
     * <br>
     * The schema must be enabled, and only enabled SchemaObject will be loaded.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     * 
     * @param schema the schema' name to load
     * @return true if the schema have been loaded and the registries is consistent
     * @throws Exception @TODO 
     */
    boolean load( String... schema ) throws Exception;


    /**
     * Load some schema into the registries, and loads all of the schema they depend
     * on. The Registries is checked after the schema have been loaded, and if there 
     * is an error, the method returns false and the registries is kept intact.
     * <br>
     * The schema must be enabled, and only enabled SchemaObject will be loaded.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     * 
     * @param schema the schema to load
     * @return true if the schema have been loaded and the registries is consistent
     * @throws Exception @TODO 
     */
    boolean loadWithDeps( Schema... schema ) throws Exception;


    /**
     * Load some schema into the registries, and loads all of the schema they depend
     * on. The Registries is checked after the schema have been loaded, and if there 
     * is an error, the method returns false and the registries is kept intact.
     * <br>
     * The schema must be enabled, and only enabled SchemaObject will be loaded.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     * 
     * @param schema the schema' name to load
     * @return true if the schema have been loaded and the registries is consistent
     * @throws Exception @TODO 
     */
    boolean loadWithDeps( String... schema ) throws Exception;


    /**
     * Load schema into the registries, even if there are some errors in the schema. 
     * The Registries is checked after the schema have been loaded. Even if we have 
     * errors, the registries will be updated.
     * <br>
     * The schema must be enabled, and only enabled SchemaObject will be loaded.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     * 
     * @param schema the schema to load, if enabled
     * @return true if the schema have been loaded
     * @throws Exception @TODO 
     */
    boolean loadRelaxed( Schema... schema ) throws Exception;


    /**
     * Load schema into the registries, even if there are some errors in the schema. 
     * The Registries is checked after the schema have been loaded. Even if we have 
     * errors, the registries will be updated.
     * <br>
     * The schema must be enabled, and only enabled SchemaObject will be loaded.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     * 
     * @param schema the schema' name to load, if enabled
     * @return true if the schema have been loaded and the registries is consistent
     * @throws Exception @TODO 
     */
    boolean loadRelaxed( String... schema ) throws Exception;


    /**
     * Load some schema into the registries, and loads all of the schema they depend
     * on. The Registries is checked after the schema have been loaded. Even if we have 
     * errors, the registries will be updated.
     * <br>
     * The schema must be enabled, and only enabled SchemaObject will be loaded.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     * 
     * @param schema the schema to load
     * @return true if the schema have been loaded
     * @throws Exception @TODO 
     */
    boolean loadWithDepsRelaxed( Schema... schema ) throws Exception;


    /**
     * Load some schema into the registries, and loads all of the schema they depend
     * on. The Registries is checked after the schema have been loaded. Even if we have 
     * errors, the registries will be updated.
     * <br>
     * The schema must be enabled, and only enabled SchemaObject will be loaded.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     * 
     * @param schema the schema' name to load
     * @return true if the schema have been loaded
     * @throws Exception @TODO 
     */
    boolean loadWithDepsRelaxed( String... schema ) throws Exception;


    /**
     * Load schema into the Registries, even if they are disabled. The disabled
     * SchemaObject from an enabled schema will also be loaded. The Registries will
     * be checked after the schema have been loaded. Even if we have errors, the
     * Registries will be updated.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     *
     * @param schema The schema to load
     * @return true if the schema have been loaded
     * @throws Exception @TODO 
     */
    boolean loadDisabled( Schema... schema ) throws Exception;


    /**
     * Load schema into the Registries, even if they are disabled. The disabled
     * SchemaObject from an enabled schema will also be loaded. The Registries will
     * be checked after the schema have been loaded. Even if we have errors, the
     * Registries will be updated.
     * <br>
     * If any error was met, the {@link #getErrors} method will contain them
     *
     * @param schema The schema' name to load
     * @return true if the schema have been loaded
     * @throws Exception @TODO 
     */
    boolean loadDisabled( String... schema ) throws Exception;


    /**
     * Load all the enabled schema into the Registries. The Registries is strict,
     * any inconsistent schema will be rejected. 
     *
     * @return true if the schema have been loaded
     * @throws Exception @TODO
     */
    boolean loadAllEnabled() throws Exception;


    /**
     * Load all the enabled schema into the Registries. The Registries is relaxed,
     * even inconsistent schema will be loaded. 
     *
     * @return true if the schema have been loaded
     * @throws Exception if something went wrong
     */
    boolean loadAllEnabledRelaxed() throws Exception;


    /**
     * Unload the given set of schema
     *
     * @param schema The list of Schema to unload
     * @return true if all the schema have been unloaded
     * @throws Exception if something went wrong
     */
    boolean unload( Schema... schema ) throws Exception;


    /**
     * Unload the given set of schema
     *
     * @param schema The list of Schema to unload
     * @return true if all the schema have been unloaded
     * @throws Exception if something went wrong
     */
    boolean unload( String... schema ) throws Exception;


    //---------------------------------------------------------------------------------
    // Other Schema methods
    //---------------------------------------------------------------------------------
    /**
     * Enables a set of schema, and returns true if all the schema have been
     * enabled, with all the dependent schema, and if the registries is 
     * still consistent.
     * 
     * If the modification is ok, the Registries will be updated. 
     * 
     *  @param schema The list of schema to enable
     *  @return true if the Registries is still consistent, false otherwise.
     *  @throws Exception if something went wrong
     */
    boolean enable( Schema... schema ) throws Exception;


    /**
     * Enables a set of schema, and returns true if all the schema have been
     * enabled, with all the dependent schema, and if the registries is 
     * still consistent.
     * 
     * If the modification is ok, the Registries will be updated.
     *  
     *  @param schema The list of schema name to enable
     *  @return true if the Registries is still consistent, false otherwise.
     *  @throws Exception if something went wrong
     */
    boolean enable( String... schema ) throws Exception;


    /**
     * Enables a set of schema, and returns true if all the schema have been
     * enabled, with all the dependent schema. No check is done, the Registries
     * might become inconsistent after this operation.
     * 
     *  @param schema The list of schema to enable
     *  @return true if all the schema have been enabled
     */
    boolean enableRelaxed( Schema... schema );


    /**
     * Enables a set of schema, and returns true if all the schema have been
     * enabled, with all the dependent schema. No check is done, the Registries
     * might become inconsistent after this operation.
     * 
     *  @param schema The list of schema names to enable
     *  @return true if all the schema have been enabled
     */
    boolean enableRelaxed( String... schema );


    /**
     * @return the list of all the enabled schema
     */
    List<Schema> getEnabled();


    /**
     * Tells if the given Schema is enabled
     *
     * @param schemaName The schema name
     * @return true if the schema is enabled
     */
    boolean isEnabled( String schemaName );


    /**
     * Tells if the given Schema is enabled
     *
     * @param schema The schema
     * @return true if the schema is enabled
     */
    boolean isEnabled( Schema schema );


    /**
     * Disables a set of schema, and returns true if all the schema have been
     * disabled, with all the dependent schema, and if the registries is 
     * still consistent.
     * 
     * If the modification is ok, the Registries will be updated. 
     * 
     *  @param schema The list of schema to disable
     *  @return true if the Registries is still consistent, false otherwise.
     *  @throws Exception if something went wrong
     */
    boolean disable( Schema... schema ) throws Exception;


    /**
     * Disables a set of schema, and returns true if all the schema have been
     * disabled, with all the dependent schema, and if the registries is 
     * still consistent.
     * 
     * If the modification is ok, the Registries will be updated. 
     * 
     *  @param schema The list of schema names to disable
     *  @return true if the Registries is still consistent, false otherwise.
     *  @throws Exception if something went wrong
     */
    boolean disable( String... schema ) throws Exception;


    /**
     * Disables a set of schema, and returns true if all the schema have been
     * disabled, with all the dependent schema. The Registries is not checked
     * and can be inconsistent after this operation
     * 
     * If the modification is ok, the Registries will be updated. 
     * 
     *  @param schema The list of schema to disable
     *  @return true if all the schema have been disabled
     */
    boolean disabledRelaxed( Schema... schema );


    /**
     * Disables a set of schema, and returns true if all the schema have been
     * disabled, with all the dependent schema. The Registries is not checked
     * and can be inconsistent after this operation
     * 
     * If the modification is ok, the Registries will be updated. 
     * 
     *  @param schema The list of schema names to disable
     *  @return true if all the schema have been disabled
     */
    boolean disabledRelaxed( String... schema );


    /**
     * @return the list of all the disabled schema
     */
    List<Schema> getDisabled();


    /**
     * Tells if the given Schema is disabled
     *
     * @param schemaName The schema name
     * @return true if the schema is disabled
     */
    boolean isDisabled( String schemaName );


    /**
     * Tells if the given Schema is disabled
     *
     * @param schema The schema
     * @return true if the schema is disabled
     */
    boolean isDisabled( Schema schema );


    /**
     * Check that the schema are consistent regarding the current Registries.
     * 
     * @param schema The schema to check
     * @return true if the schema can be loaded in the registries
     * @throws Exception if something went wrong
     */
    boolean verify( Schema... schema ) throws Exception;


    /**
     * Check that the schema are consistent regarding the current Registries.
     * 
     * @param schema The schema names to check
     * @return true if the schema can be loaded in the registries
     * @throws Exception if something went wrong
     */
    boolean verify( String... schema ) throws Exception;


    /**
     * @return The Registries
     */
    Registries getRegistries();


    /**
     * Lookup for an AttributeType in the AttributeType registry
     * 
     * @param oid the OID we are looking for
     * @return The found AttributeType 
     * @throws NamingException if the OID is not found in the AttributeType registry
     */
    AttributeType lookupAttributeTypeRegistry( String oid ) throws NamingException;


    /**
     * Lookup for a Comparator in the Comparator registry
     * 
     * @param oid the OID we are looking for
     * @return The found Comparator 
     * @throws NamingException if the OID is not found in the Comparator registry
     */
    LdapComparator<?> lookupComparatorRegistry( String oid ) throws NamingException;


    /**
     * Lookup for a MatchingRule in the MatchingRule registry
     * 
     * @param oid the OID we are looking for
     * @return The found MatchingRule 
     * @throws NamingException if the OID is not found in the MatchingRule registry
     */
    MatchingRule lookupMatchingRuleRegistry( String oid ) throws NamingException;


    /**
     * Lookup for a Normalizer in the Normalizer registry
     * 
     * @param oid the OID we are looking for
     * @return The found Normalizer 
     * @throws NamingException if the OID is not found in the Normalizer registry
     */
    Normalizer lookupNormalizerRegistry( String oid ) throws NamingException;


    /**
     * Lookup for a ObjectClass in the ObjectClass registry
     * 
     * @param oid the OID we are looking for
     * @return The found ObjectClass 
     * @throws NamingException if the OID is not found in the ObjectClass registry
     */
    ObjectClass lookupObjectClassRegistry( String oid ) throws NamingException;


    /**
     * Lookup for an LdapSyntax in the LdapSyntax registry
     * 
     * @param oid the OID we are looking for
     * @return The found LdapSyntax 
     * @throws NamingException if the OID is not found in the LdapSyntax registry
     */
    LdapSyntax lookupLdapSyntaxRegistry( String oid ) throws NamingException;


    /**
     * Lookup for a SyntaxChecker in the SyntaxChecker registry
     * 
     * @param oid the OID we are looking for
     * @return The found SyntaxChecker 
     * @throws NamingException if the OID is not found in the SyntaxChecker registry
     */
    SyntaxChecker lookupSyntaxCheckerRegistry( String oid ) throws NamingException;


    /**
     * Get an immutable reference on the AttributeType registry
     * 
     * @return A reference to the AttributeType registry.
     */
    AttributeTypeRegistry getAttributeTypeRegistry();


    /**
     * Get an immutable reference on the Comparator registry
     * 
     * @return A reference to the Comparator registry.
     */
    ComparatorRegistry getComparatorRegistry();


    /**
     * Get an immutable reference on the DITContentRule registry
     * 
     * @return A reference to the DITContentRule registry.
     */
    DITContentRuleRegistry getDITContentRuleRegistry();


    /**
     * Get an immutable reference on the DITStructureRule registry
     * 
     * @return A reference to the DITStructureRule registry.
     */
    DITStructureRuleRegistry getDITStructureRuleRegistry();


    /**
     * Get an immutable reference on the MatchingRule registry
     * 
     * @return A reference to the MatchingRule registry.
     */
    MatchingRuleRegistry getMatchingRuleRegistry();


    /**
     * Get an immutable reference on the MatchingRuleUse registry
     * 
     * @return A reference to the MatchingRuleUse registry.
     */
    MatchingRuleUseRegistry getMatchingRuleUseRegistry();


    /**
     * Get an immutable reference on the Normalizer registry
     * 
     * @return A reference to the Normalizer registry.
     */
    NormalizerRegistry getNormalizerRegistry();


    /**
     * Get an immutable reference on the NameForm registry
     * 
     * @return A reference to the NameForm registry.
     */
    NameFormRegistry getNameFormRegistry();


    /**
     * Get an immutable reference on the ObjectClass registry
     * 
     * @return A reference to the ObjectClass registry.
     */
    ObjectClassRegistry getObjectClassRegistry();


    /**
     * Get an immutable reference on the LdapSyntax registry
     * 
     * @return A reference to the LdapSyntax registry.
     */
    LdapSyntaxRegistry getLdapSyntaxRegistry();


    /**
     * Get an immutable reference on the SyntaxChecker registry
     * 
     * @return A reference to the SyntaxChecker registry.
     */
    SyntaxCheckerRegistry getSyntaxCheckerRegistry();


    /**
     * Get an immutable reference on the Normalizer mapping
     * 
     * @return A reference to the Normalizer mapping
     */
    Map<String, OidNormalizer> getNormalizerMapping();


    /**
     * Associate a new Registries to the SchemaManager
     *
     * @param registries The new Registries
     */
    void setRegistries( Registries registries );


    /**
     * @return The errors obtained when checking the registries
     */
    List<Throwable> getErrors();


    /**
     * Associate a Schema loader to this SchemaManager
     *
     * @param schemaLoader The schema loader to use
     */
    void setSchemaLoader( SchemaLoader schemaLoader );


    /**
     * @return the namingContext
     */
    LdapDN getNamingContext();


    /**
     * Initializes the schemaervice
     *
     * @throws Exception If the initialization fails
     */
    void initialize() throws Exception;


    /**
     * @return The used loader
     */
    SchemaLoader getLoader();


    /**
     * Registers a new SchemaObject. The registries will be updated only if it's
     * consistent after this addition, if the SchemaManager is in Strict mode.
     * If something went wrong during this operation, the 
     * SchemaManager.getErrors() will give the list of generated errors.
     *
     * @param schemaObject the SchemaObject to register
     * @return true if the addition has been made, false if there were some errors
     * @throws Exception if the SchemaObject is already registered or
     * the registration operation is not supported
     */
    boolean add( SchemaObject schemaObject ) throws Exception;


    /**
     * Unregisters a new SchemaObject. The registries will be updated only if it's
     * consistent after this deletion, if the SchemaManager is in Strict mode.
     * If something went wrong during this operation, the 
     * SchemaManager.getErrors() will give the list of generated errors.
     *
     * @param schemaObject the SchemaObject to unregister
     * @return true if the deletion has been made, false if there were some errors
     * @throws Exception if the SchemaObject is not registered or
     * the deletion operation is not supported
     */
    boolean delete( SchemaObject schemaObject ) throws Exception;


    /**
     * Removes the registered attributeType from the attributeTypeRegistry 
     * 
     * @param attributeTypeOid the attributeType OID to unregister
     * @return SchemaObject the unregistered schema entity
     * @throws NamingException if the attributeType is invalid
     */
    SchemaObject unregisterAttributeType( String attributeTypeOid ) throws NamingException;


    /**
     * Removes the registered Comparator from the ComparatorRegistry 
     * 
     * @param comparatorOid the Comparator OID to unregister
     * @return SchemaObject the unregistered schema entity
     * @throws NamingException if the Comparator is invalid
     */
    SchemaObject unregisterComparator( String comparatorOid ) throws NamingException;


    /**
     * Removes the registered DitControlRule from the DitControlRuleRegistry 
     * 
     * @param ditControlRuleOid the DitControlRule OID to unregister
     * @return SchemaObject the unregistered schema entity
     * @throws NamingException if the DitControlRule is invalid
     */
    SchemaObject unregisterDitControlRule( String ditControlRuleOid ) throws NamingException;


    /**
     * Removes the registered DitStructureRule from the DitStructureRuleRegistry 
     * 
     * @param ditStructureRuleOid the DitStructureRule OID to unregister
     * @return SchemaObject the unregistered schema entity
     * @throws NamingException if the DitStructureRule is invalid
     */
    SchemaObject unregisterDitStructureRule( String ditStructureRuleOid ) throws NamingException;


    /**
     * Removes the registered MatchingRule from the MatchingRuleRegistry 
     * 
     * @param matchingRuleOid the MatchingRuleRule OID to unregister
     * @return SchemaObject the unregistered schema entity
     * @throws NamingException if the MatchingRule is invalid
     */
    SchemaObject unregisterMatchingRule( String matchingRuleOid ) throws NamingException;


    /**
     * Removes the registered MatchingRuleUse from the MatchingRuleUseRegistry 
     * 
     * @param matchingRuleUseOid the MatchingRuleUse OID to unregister
     * @return SchemaObject the unregistered schema entity
     * @throws NamingException if the MatchingRuleUse is invalid
     */
    SchemaObject unregisterMatchingRuleUse( String matchingRuleUseOid ) throws NamingException;


    /**
     * Removes the registered NameForm from the NameFormRegistry 
     * 
     * @param nameFormOid the NameForm OID to unregister
     * @return SchemaObject the unregistered schema entity
     * @throws NamingException if the NameForm is invalid
     */
    SchemaObject unregisterNameForm( String nameFormOid ) throws NamingException;


    /**
     * Removes the registered Normalizer from the NormalizerRegistry 
     * 
     * @param normalizerOid the Normalizer OID to unregister
     * @return SchemaObject the unregistered schema entity
     * @throws NamingException if the Normalizer is invalid
     */
    SchemaObject unregisterNormalizer( String normalizerOid ) throws NamingException;


    /**
     * Removes the registered ObjectClass from the ObjectClassRegistry 
     * 
     * @param objectClassOid the ObjectClass OID to unregister
     * @return SchemaObject the unregistered schema entity
     * @throws NamingException if the ObjectClass is invalid
     */
    SchemaObject unregisterObjectClass( String objectClassOid ) throws NamingException;


    /**
     * Removes the registered LdapSyntax from the LdapSyntaxRegistry 
     * 
     * @param ldapSyntaxOid the LdapSyntax OID to unregister
     * @return SchemaObject the unregistered schema entity
     * @throws NamingException if the LdapSyntax is invalid
     */
    SchemaObject unregisterLdapSyntax( String ldapSyntaxOid ) throws NamingException;


    /**
     * Removes the registered SyntaxChecker from the SyntaxCheckerRegistry 
     * 
     * @param syntaxCheckerOid the SyntaxChecker OID to unregister
     * @return SchemaObject the unregistered schema entity
     * @throws NamingException if the SyntaxChecker is invalid
     */
    SchemaObject unregisterSyntaxChecker( String syntaxCheckerOid ) throws NamingException;


    /**
     * Returns a reference to the global OidRegistry
     *
     * @return The the global OidRegistry
     */
    OidRegistry getGlobalOidRegistry();


    /**
     * Gets a schema that has been loaded into these Registries.
     * 
     * @param schemaName the name of the schema to lookup
     * @return the loaded Schema if one corresponding to the name exists
     */
    Schema getLoadedSchema( String schemaName );


    /**
     * Tells if the specific schema is loaded
     *
     * @param schemaName The schema we want to check
     * @return true if the schema is laoded
     */
    boolean isSchemaLoaded( String schemaName );
    
    
    /**
     * Get the list of Schema names which has the given schema name as a dependence
     *
     * @param schemaName The Schema name for which we want to get the list of dependent schema
     * @return The list of dependent schema
     */
    Set<String> listDependentSchemaNames( String schemaName );
}
