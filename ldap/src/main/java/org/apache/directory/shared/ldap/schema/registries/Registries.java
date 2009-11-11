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
package org.apache.directory.shared.ldap.schema.registries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.directory.shared.ldap.exception.LdapSchemaViolationException;
import org.apache.directory.shared.ldap.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.DITContentRule;
import org.apache.directory.shared.ldap.schema.DITStructureRule;
import org.apache.directory.shared.ldap.schema.LdapComparator;
import org.apache.directory.shared.ldap.schema.LdapSyntax;
import org.apache.directory.shared.ldap.schema.MatchingRule;
import org.apache.directory.shared.ldap.schema.MatchingRuleUse;
import org.apache.directory.shared.ldap.schema.NameForm;
import org.apache.directory.shared.ldap.schema.Normalizer;
import org.apache.directory.shared.ldap.schema.ObjectClass;
import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.shared.ldap.schema.SchemaWrapper;
import org.apache.directory.shared.ldap.schema.SyntaxChecker;
import org.apache.directory.shared.ldap.schema.syntaxCheckers.OctetStringSyntaxChecker;
import org.apache.directory.shared.ldap.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Document this class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class Registries implements SchemaLoaderListener, Cloneable
{
    /** A logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger( Registries.class );

    /**
     * A String name to Schema object map for those schemas loaded into this
     * registry.
     */
    protected Map<String, Schema> loadedSchemas = new HashMap<String, Schema>();

    /** The AttributeType registry */
    protected AttributeTypeRegistry attributeTypeRegistry;
    
    /** The ObjectClass registry */
    protected ObjectClassRegistry objectClassRegistry;

    /** The LdapSyntax registry */
    protected ComparatorRegistry comparatorRegistry;

    /** The DitContentRule registry */
    protected DITContentRuleRegistry ditContentRuleRegistry;

    /** The DitStructureRule registry */
    protected DITStructureRuleRegistry ditStructureRuleRegistry;

    /** The MatchingRule registry */
    protected MatchingRuleRegistry matchingRuleRegistry;

    /** The MatchingRuleUse registry */
    protected MatchingRuleUseRegistry matchingRuleUseRegistry;

    /** The NameForm registry */
    protected NameFormRegistry nameFormRegistry;

    /** The Normalizer registry */
    protected NormalizerRegistry normalizerRegistry;

    /** The OID registry */
    protected OidRegistry oidRegistry;

    /** The SyntaxChecker registry */
    protected SyntaxCheckerRegistry syntaxCheckerRegistry;

    /** The LdapSyntax registry */
    protected LdapSyntaxRegistry ldapSyntaxRegistry;
    
    /** A map storing all the schema objects associated with a schema */
    private Map<String, Set<SchemaWrapper>> schemaObjectsBySchemaName;
    
    /** A flag indicating that the Registries is relaxed or not */
    private boolean isRelaxed;
    
    /** A flag indicating that disabled SchemaObject are accepted */
    private boolean disabledAccepted;
    
    /** Two flags for RELAXED and STRUCT */
    public static final boolean STRICT = false;
    public static final boolean RELAXED = true;
    
    /**
     *  A map storing a relation between a SchemaObject and all the 
     *  referencing SchemaObjects.
     */
    protected Map<SchemaWrapper, Set<SchemaWrapper>> usedBy;
    
    /**
     *  A map storing a relation between a SchemaObject and all the 
     *  SchemaObjects it uses.
     */
    protected Map<SchemaWrapper, Set<SchemaWrapper>> using;
    

    /**
     * Creates a new instance of Registries.
     *
     * @param oidRegistry the OID registry
     */
    public Registries()
    {
        this.oidRegistry = new OidRegistry();
        attributeTypeRegistry = new DefaultAttributeTypeRegistry( oidRegistry );
        comparatorRegistry = new DefaultComparatorRegistry( oidRegistry );
        ditContentRuleRegistry = new DefaultDITContentRuleRegistry( oidRegistry );
        ditStructureRuleRegistry = new DefaultDITStructureRuleRegistry( oidRegistry );
        ldapSyntaxRegistry = new DefaultLdapSyntaxRegistry( oidRegistry );
        matchingRuleRegistry = new DefaultMatchingRuleRegistry( oidRegistry );
        matchingRuleUseRegistry = new DefaultMatchingRuleUseRegistry( oidRegistry );
        nameFormRegistry = new DefaultNameFormRegistry( oidRegistry );
        normalizerRegistry = new DefaultNormalizerRegistry( oidRegistry );
        objectClassRegistry = new DefaultObjectClassRegistry( oidRegistry );
        syntaxCheckerRegistry = new DefaultSyntaxCheckerRegistry( oidRegistry );
        schemaObjectsBySchemaName = new HashMap<String, Set<SchemaWrapper>>();
        usedBy = new HashMap<SchemaWrapper, Set<SchemaWrapper>>();
        using = new HashMap<SchemaWrapper, Set<SchemaWrapper>>();
        
        isRelaxed = STRICT;
        disabledAccepted = false;
    }

    
    /**
     * @return The AttributeType registry
     */
    public AttributeTypeRegistry getAttributeTypeRegistry()
    {
        return attributeTypeRegistry;
    }

    
    /**
     * @return The Comparator registry
     */
    public ComparatorRegistry getComparatorRegistry()
    {
        return comparatorRegistry;
    }

    
    /**
     * @return The DITContentRule registry
     */
    public DITContentRuleRegistry getDitContentRuleRegistry()
    {
        return ditContentRuleRegistry;
    }

    
    /**
     * @return The DITStructureRule registry
     */
    public DITStructureRuleRegistry getDitStructureRuleRegistry()
    {
        return ditStructureRuleRegistry;
    }

    
    /**
     * @return The MatchingRule registry
     */
    public MatchingRuleRegistry getMatchingRuleRegistry()
    {
        return matchingRuleRegistry;
    }

    
    /**
     * @return The MatchingRuleUse registry
     */
    public MatchingRuleUseRegistry getMatchingRuleUseRegistry()
    {
        return matchingRuleUseRegistry;
    }

    
    /**
     * @return The NameForm registry
     */
    public NameFormRegistry getNameFormRegistry()
    {
        return nameFormRegistry;
    }

    
    /**
     * @return The Normalizer registry
     */
    public NormalizerRegistry getNormalizerRegistry()
    {
        return normalizerRegistry;
    }

    
    /**
     * @return The ObjectClass registry
     */
    public ObjectClassRegistry getObjectClassRegistry()
    {
        return objectClassRegistry;
    }

    
    /**
     * @return The getOid registry
     */
    public OidRegistry getOidRegistry()
    {
        return oidRegistry;
    }

    
    /**
     * @return The SyntaxChecker registry
     */
    public SyntaxCheckerRegistry getSyntaxCheckerRegistry()
    {
        return syntaxCheckerRegistry;
    }

    
    /**
     * @return The LdapSyntax registry
     */
    public LdapSyntaxRegistry getLdapSyntaxRegistry()
    {
        return ldapSyntaxRegistry;
    }
    
    
    /**
     * Get an OID from a name. As we have many possible registries, we 
     * have to look in all of them to get the one containing the OID.
     *
     * @param name The name we are looking at
     * @return The associated OID
     */
    public String getOid( String name )
    {
        // we have many possible Registries to look at.
        // AttributeType
        try
        {
            AttributeType attributeType = attributeTypeRegistry.lookup( name );
            
            return attributeType.getOid();
        }
        catch ( NamingException ne )
        {
            // Fall down to the next registry
        }
        
        // ObjectClass
        try
        {
            ObjectClass objectClass = objectClassRegistry.lookup( name );
            
            return objectClass.getOid();
        }
        catch ( NamingException ne )
        {
            // Fall down to the next registry
        }

        // LdapSyntax
        try
        {
            LdapSyntax ldapSyntax = ldapSyntaxRegistry.lookup( name );
            
            return ldapSyntax.getOid();
        }
        catch ( NamingException ne )
        {
            // Fall down to the next registry
        }
        
        // MatchingRule
        try
        {
            MatchingRule matchingRule = matchingRuleRegistry.lookup( name );
            
            return matchingRule.getOid();
        }
        catch ( NamingException ne )
        {
            // Fall down to the next registry
        }
        
        // MatchingRuleUse
        try
        {
            MatchingRuleUse matchingRuleUse = matchingRuleUseRegistry.lookup( name );
            
            return matchingRuleUse.getOid();
        }
        catch ( NamingException ne )
        {
            // Fall down to the next registry
        }
        
        // NameForm
        try
        {
            NameForm nameForm = nameFormRegistry.lookup( name );
            
            return nameForm.getOid();
        }
        catch ( NamingException ne )
        {
            // Fall down to the next registry
        }
        
        // DITContentRule
        try
        {
            DITContentRule ditContentRule = ditContentRuleRegistry.lookup( name );
            
            return ditContentRule.getOid();
        }
        catch ( NamingException ne )
        {
            // Fall down to the next registry
        }

        // DITStructureRule
        try
        {
            DITStructureRule ditStructureRule = ditStructureRuleRegistry.lookup( name );
            
            return ditStructureRule.getOid();
        }
        catch ( NamingException ne )
        {
            // No more registries to look at...
            return null;
        }
    }


    /**
     * Gets a schema that has been loaded into these Registries.
     * 
     * @param schemaName the name of the schema to lookup
     * @return the loaded Schema if one corresponding to the name exists
     */
    public Schema getLoadedSchema( String schemaName )
    {
        return loadedSchemas.get( schemaName );
    }


    /**
     * Checks to see if a particular Schema is loaded.
     *
     * @param schemaName the name of the Schema to check
     * @return true if the Schema is loaded, false otherwise
     */
    public boolean isSchemaLoaded( String schemaName )
    {
        return loadedSchemas.containsKey( schemaName );
    }


    // ------------------------------------------------------------------------
    // Code used to sanity check the resolution of entities in registries
    // ------------------------------------------------------------------------
    /**
     * Attempts to resolve the dependent schema objects of all entities that
     * refer to other objects within the registries.  Null references will be
     * handed appropriately.
     * The order in which the SchemaObjects must be :
     * <li/>1) Normalizers, Comparators and SyntaxCheckers (as they depend on nothing)
     * <li/>2) Syntaxes (depend on SyntaxCheckers)
     * <li/>3) MatchingRules (depend on Syntaxes, Normalizers and Comparators
     * <li/>4) AttributeTypes (depend on MatchingRules, Syntaxes and AttributeTypes : in this case, we first handle the superior)
     * <li/>5) ObjectClasses (depend on AttributeTypes and ObjectClasses)
     * <br/><br/>
     * Later, when we will support them :
     * <li/>6) MatchingRuleUses (depend on matchingRules and AttributeTypes)
     * <li/>7) DitContentRules (depend on ObjectClasses and AttributeTypes)
     * <li/>8) NameForms (depends on ObjectClasses and AttributeTypes)
     * <li/>9) DitStructureRules (depends onNameForms and DitStructureRules)      * 
     *
     * @return a list of exceptions encountered while resolving entities
     */
    public List<Throwable> checkRefInteg()
    {
        ArrayList<Throwable> errors = new ArrayList<Throwable>();

        // Step 1 :
        // We start with Normalizers, Comparators and SyntaxCheckers
        // as they depend on nothing
        // Check the Normalizers
        for ( Normalizer normalizer : normalizerRegistry )
        {
            resolve( normalizer, errors );
        }

        // Check the Comparators
        for ( LdapComparator<?> comparator : comparatorRegistry )
        {
            resolve( comparator, errors );
        }
        
        // Check the SyntaxCheckers
        for ( SyntaxChecker syntaxChecker : syntaxCheckerRegistry )
        {
            resolve( syntaxChecker, errors );
        }

        // Step 2 :
        // Check the LdapSyntaxes
        for ( LdapSyntax ldapSyntax : ldapSyntaxRegistry )
        {
            resolve( ldapSyntax, errors );
        }
        
        // Step 3 :
        // Check the matchingRules
        for ( MatchingRule matchingRule : matchingRuleRegistry )
        {
            resolve( matchingRule, errors );
        }
        
        // Step 4 :
        // Check the AttributeTypes
        for ( AttributeType attributeType : attributeTypeRegistry )
        {
            resolve( attributeType, errors );
        }
        
        //  Step 5 :
        // Check the ObjectClasses
        for ( ObjectClass objectClass : objectClassRegistry )
        {
            resolve( objectClass, errors );
        }

        // Step 6-9 aren't yet defined
        return errors;
    }
    
    
    /**
     * Build the usedBy and using references from the stored elements.
     */
    public void buildReferences()
    {
        // First start with the Syntaxes
        for ( LdapSyntax syntax : ldapSyntaxRegistry )
        {
            SyntaxChecker syntaxChecker = null;
            
            // Each syntax should reference a SyntaxChecker with the same OID
            try
            {
                syntaxChecker = syntaxCheckerRegistry.lookup( syntax.getOid() );
            }
            catch ( NamingException ne )
            {
                // There is no SyntaxChecker : default to the OctetString SyntaxChecker
                syntaxChecker = new OctetStringSyntaxChecker( syntax.getOid() );
            }
            
            addReference( syntax, syntaxChecker );
        }

        // Then the MatchingRules
        for ( MatchingRule matchingRule : matchingRuleRegistry )
        {
            // each matching rule references a Syntax, a Comparator and a Normalizer
            // If we don't have a Syntax, this is an error
            LdapSyntax syntax = null;
            
            try
            {
                syntax = ldapSyntaxRegistry.lookup( matchingRule.getSyntaxOid() );
            }
            catch ( NamingException ne )
            {
                
            }
        }
    }

    
    /**
     * Attempts to resolve the SyntaxChecker associated with a Syntax.
     *
     * @param syntax the LdapSyntax to resolve the SyntaxChecker of
     * @param errors the list of errors to add exceptions to
     */
    private void resolve( LdapSyntax syntax, List<Throwable> errors )
    {
        // A LdapSyntax must point to a valid SyntaxChecker
        // or to the OctetString SyntaxChecker
        try
        {
            syntax.applyRegistries( this );
        }
        catch ( NamingException e )
        {
            errors.add( e );
        }
    }


    /**
     * Attempts to resolve the Normalizer
     *
     * @param normalizer the Normalizer
     * @param errors the list of errors to add exceptions to
     */
    private void resolve( Normalizer normalizer, List<Throwable> errors )
    {
        // This is currently doing nothing.
        try
        {
            normalizer.applyRegistries( this );
        }
        catch ( NamingException e )
        {
            errors.add( e );
        }
    }


    /**
     * Attempts to resolve the LdapComparator
     *
     * @param comparator the LdapComparator
     * @param errors the list of errors to add exceptions to
     */
    private void resolve( LdapComparator<?> comparator, List<Throwable> errors )
    {
        // This is currently doing nothing.
        try
        {
            comparator.applyRegistries( this );
        }
        catch ( NamingException e )
        {
            errors.add( e );
        }
    }


    /**
     * Attempts to resolve the SyntaxChecker
     *
     * @param normalizer the SyntaxChecker
     * @param errors the list of errors to add exceptions to
     */
    private void resolve( SyntaxChecker syntaxChecker, List<Throwable> errors )
    {
        // This is currently doing nothing.
        try
        {
            syntaxChecker.applyRegistries( this );
        }
        catch ( NamingException e )
        {
            errors.add( e );
        }
    }


    /**
     * Check if the Comparator, Normalizer and the syntax are 
     * existing for a matchingRule
     */
    private void resolve( MatchingRule matchingRule, List<Throwable> errors )
    {
        // A Matching rule must point to a valid Syntax
        try
        {
            matchingRule.applyRegistries( this );
        }
        catch ( NamingException e )
        {
            errors.add( e );
        }
    }

    
    /**
     * Check AttributeType referential integrity
     */
    private void resolveRecursive( AttributeType attributeType, Set<String> processed, List<Throwable> errors )
    {
        // Process the Superior, if any
        String superiorOid = attributeType.getSuperiorOid();
        AttributeType superior = null;
        
        if ( superiorOid != null )
        {
            // Check if the Superior is present in the registries
            try
            {
                superior = attributeTypeRegistry.lookup( superiorOid );
            }
            catch ( NamingException ne )
            {
                // This AT's superior has not been loaded into the Registries.
                if ( !processed.contains( superiorOid ) )
                {
                    errors.add( ne );
                }
            }
            
            // We now have to process the superior, if it hasn"'t been 
            // processed yet.
            if ( ( superior != null ) && !processed.contains( superiorOid ) )
            {
                resolveRecursive( superior, processed, errors );
            }
        }
        
        // Process the Syntax. If it's null, the attributeType must have 
        // a Superior.
        String syntaxOid = attributeType.getSyntaxOid();
        
        if ( syntaxOid != null )
        {
            // Check if the Syntax is present in the registries
            try
            {
                ldapSyntaxRegistry.lookup( syntaxOid );
            }
            catch ( NamingException ne )
            {
                // This AT's syntax has not been loaded into the Registries.
                errors.add( ne );
            }
        }
        else
        {
            // No Syntax : get it from the AttributeType's superior
            if ( superior == null )
            {
                // This is an error. if the AT does not have a Syntax,
                // then it must have a superior, which syntax is get from.
                Throwable error = new LdapSchemaViolationException( 
                    "The AttributeType " + attributeType.getOid() + " does not have a superior" +
                    " nor a Syntax. This is invalid", ResultCodeEnum.INVALID_ATTRIBUTE_SYNTAX );
                errors.add( error );
            }
        }
        
        // Process the EQUALITY MatchingRule. It may be null, but if it's not
        // it must have been processed before
        String equalityOid = attributeType.getEqualityOid();
        
        if ( equalityOid != null )
        {
            // Check if the MatchingRule is present in the registries
            try
            {
                matchingRuleRegistry.lookup( equalityOid );
            }
            catch ( NamingException ne )
            {
                // This AT's EQUALITY matchingRule has not been loaded into the Registries.
                errors.add( ne );
            }
        }
        
        // Process the ORDERING MatchingRule. It may be null, but if it's not
        // it must have been processed before
        String orderingOid = attributeType.getOrderingOid();
        
        if ( orderingOid != null )
        {
            // Check if the MatchingRule is present in the registries
            try
            {
                matchingRuleRegistry.lookup( orderingOid );
            }
            catch ( NamingException ne )
            {
                // This AT's ORDERING matchingRule has not been loaded into the Registries.
                errors.add( ne );
            }
        }
        
        // Process the SUBSTR MatchingRule. It may be null, but if it's not
        // it must have been processed before
        String substringOid = attributeType.getSubstringOid();
        
        if ( substringOid != null )
        {
            // Check if the MatchingRule is present in the registries
            try
            {
                matchingRuleRegistry.lookup( substringOid );
            }
            catch ( NamingException ne )
            {
                // This AT's SUBSTR matchingRule has not been loaded into the Registries.
                errors.add( ne );
            }
        }
        
        // All is done for this AttributeType, let's apply the registries
        try
        {
            attributeType.applyRegistries( this );
        }
        catch ( NamingException ne )
        {
            // Do nothing. We may have a broken AT, 
            // but at this point, it doesn't matter.
        }
    }


    /**
     * Check the inheritance, and the existence of MatchingRules and LdapSyntax
     * for an attribute 
     */
    private void resolve( AttributeType attributeType, List<Throwable> errors )
    {
        // This set is used to avoid having more than one error
        // for an AttributeType. It's mandatory when processing
        // a Superior, as it may be broken and referenced more than once. 
        Set<String> processed = new HashSet<String>();
        
        // Call the recursive method, as we may have superiors to deal with
        resolveRecursive( attributeType, processed, errors );
    }


    private void resolve( ObjectClass objectClass, List<Throwable> errors )
    {
        // This set is used to avoid having more than one error
        // for an ObjectClass. It's mandatory when processing
        // the Superiors, as they may be broken and referenced more than once. 
        Set<String> processed = new HashSet<String>();

        
        // Call the recursive method, as we may have superiors to deal with
        resolveRecursive( objectClass, processed, errors );
    }
    
    private void resolveRecursive( ObjectClass objectClass, Set<String> processed, List<Throwable> errors )
    {
        // Process the Superiors, if any
        
        List<String> superiorOids = objectClass.getSuperiorOids();
        ObjectClass superior = null;
        
        for ( String superiorOid : superiorOids )
        {
            // Check if the Superior is present in the registries
            try
            {
                superior = objectClassRegistry.lookup( superiorOid );
            }
            catch ( NamingException ne )
            {
                // This OC's superior has not been loaded into the Registries.
                if ( !processed.contains( superiorOid ) )
                {
                    errors.add( ne );
                }
            }
            
            // We now have to process the superior, if it hasn't been 
            // processed yet.
            if ( ( superior != null ) && !processed.contains( superiorOid ) )
            {
                resolveRecursive( superior, processed, errors );
            }
        }
        
        // Process the MAY attributeTypes.  
        for ( String mayOid : objectClass.getMayAttributeTypeOids() )
        {
            // Check if the MAY AttributeType is present in the registries
            try
            {
                attributeTypeRegistry.lookup( mayOid );
            }
            catch ( NamingException ne )
            {
                // This AT has not been loaded into the Registries.
                errors.add( ne );
            }
        }
        
        // Process the MUST attributeTypes.  
        for ( String mustOid : objectClass.getMustAttributeTypeOids() )
        {
            // Check if the MUST AttributeType is present in the registries
            try
            {
                attributeTypeRegistry.lookup( mustOid );
            }
            catch ( NamingException ne )
            {
                // This AT has not been loaded into the Registries.
                errors.add( ne );
            }
        }
        
        // All is done for this ObjectClass, let's apply the registries
        try
        {
            objectClass.applyRegistries( this );
        }
        catch ( NamingException ne )
        {
            // Do nothing. We may have a broken OC, 
            // but at this point, it doesn't matter.
        }
    }


    /**
     * Merely adds the schema to the set of loaded schemas.  Does not
     * actually do any work to add schema objects to registries.
     * 
     * {@inheritDoc}
     */
	public void schemaLoaded( Schema schema ) 
	{
		this.loadedSchemas.put( schema.getSchemaName(), schema );
	}


    /**
     * Merely removes the schema from the set of loaded schemas.  Does not
     * actually do any work to remove schema objects from registries.
     * 
     * {@inheritDoc}
     */
	public void schemaUnloaded(Schema schema) 
	{
		this.loadedSchemas.remove( schema.getSchemaName() );
	}


	/**
	 * Gets an unmodifiable Map of schema names to loaded Schema objects. 
	 * 
	 * @return the map of loaded Schema objects
	 */
	public Map<String, Schema> getLoadedSchemas() 
	{
		return Collections.unmodifiableMap( loadedSchemas );
	}
	
	
	/**
	 * @return Gets a reference to the Map associating a schemaName to
	 * its contained SchemaObjects
	 */
	public Map<String, Set<SchemaWrapper>> getObjectBySchemaName()
	{
	    return schemaObjectsBySchemaName;
	}
	
	
	/**
	 * Create a new schema association with its content
	 *
	 * @param schemaName The schema name
	 */
	public Set<SchemaWrapper> addSchema( String schemaName )
	{
	    Set<SchemaWrapper> content = new HashSet<SchemaWrapper>();
	    schemaObjectsBySchemaName.put( schemaName, content );
	    
	    return content;
	}
	

	/**
	 * Add the given AttributeTtpe into the Registries
	 *
	 * @param attributeType The AttributeType to register
	 * @throws NamingException If the registering failed
	 */
    public void register( AttributeType attributeType ) throws NamingException
    {
        LOG.debug( "Registering AttributeType: {}:{}", attributeType.getOid(), attributeType.getName() );
        
        String schemaName = StringTools.toLowerCase( attributeType.getSchemaName() );

        // First, clone the Registries so that we don't mess with the 
        // current registries if something goes wrong
        // Register the new AT into the cloned ATRegistry
        attributeTypeRegistry.register( attributeType );
        
        if ( isStrict() )
        {
            // Update the AT with the cloned registries
            attributeType.applyRegistries( this );
            
            // Update the referenced objects (Using/UsedBy)
            // The Syntax,
            addReference( attributeType, attributeType.getSyntax() );
    
            // The Superior if any
            addReference( attributeType, attributeType.getSuperior() );
    
            // The MatchingRules
            addReference( attributeType, attributeType.getEquality() );
            addReference( attributeType, attributeType.getOrdering() );
            addReference( attributeType, attributeType.getSubstring() );
        }        
    }
    
	
    /**
     * Add the given LdapComparator into the Registries
     *
     * @param comparator The LdapComparator to register
     * @throws NamingException If the registering failed
     */
    public void register( LdapComparator<?> comparator ) throws NamingException
    {
        LOG.debug( "Registering Comparator: {}:{}", comparator.getOid(), comparator.getName() );

        comparatorRegistry.register( comparator );
        
        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( "registered " + comparator.getName() + " for OID {}", comparator.getOid() );
        }
    }
    
        
	public void register( SchemaObject schemaObject ) throws NamingException
	{
	    LOG.debug( "Registering {}:{}", schemaObject.getObjectType(), schemaObject.getOid() );
	    
	    String schemaName = StringTools.toLowerCase( schemaObject.getSchemaName() );
	    
	    // First call the specific registry's register method
	    switch ( schemaObject.getObjectType() )
	    {
	        case ATTRIBUTE_TYPE :
	            attributeTypeRegistry.register( (AttributeType)schemaObject );
	            break;
	            
	        case COMPARATOR :
	            comparatorRegistry.register( (LdapComparator<?>)schemaObject );
	            break;
	            
            case DIT_CONTENT_RULE : 
                ditContentRuleRegistry.register( (DITContentRule)schemaObject );
                break;
                
            case DIT_STRUCTURE_RULE : 
                ditStructureRuleRegistry.register( (DITStructureRule)schemaObject );
                break;
                
            case LDAP_SYNTAX : 
                ldapSyntaxRegistry.register( (LdapSyntax)schemaObject );
                break;
                
            case MATCHING_RULE : 
                matchingRuleRegistry.register( (MatchingRule)schemaObject );
                break;
                
            case MATCHING_RULE_USE : 
                matchingRuleUseRegistry.register( (MatchingRuleUse)schemaObject );
                break;
                
            case NAME_FORM : 
                nameFormRegistry.register( (NameForm)schemaObject );
                break;
                
            case NORMALIZER : 
                normalizerRegistry.register( (Normalizer)schemaObject );
                break;
                
            case OBJECT_CLASS : 
                objectClassRegistry.register( (ObjectClass)schemaObject );
                break;
                
            case SYNTAX_CHECKER : 
                syntaxCheckerRegistry.register( (SyntaxChecker)schemaObject );
                break;
	    }
	    
	    // And register the schemaObject within its schema
	    Set<SchemaWrapper> content = schemaObjectsBySchemaName.get( schemaName );
	    
	    if ( content == null )
	    {
	        content = new HashSet<SchemaWrapper>();
	        schemaObjectsBySchemaName.put( StringTools.toLowerCase( schemaObject.getSchemaName() ), content );
	    }
	    
	    SchemaWrapper schemaWrapper = new SchemaWrapper( schemaObject );
	    
	    if ( content.contains( schemaWrapper ) )
	    {
	        // Already present !
	        // What should we do ?
	        LOG.info( "Registering of {}:{} failed, is already present in the Registries", 
	            schemaObject.getObjectType(), schemaObject.getOid() );
	    }
	    else
	    {
	        // Create the association
	        content.add( schemaWrapper );
	    }
	}


	/**
	 * Unregister a SchemaObject from the registries
	 *
	 * @param schemaObject The SchemaObject we want to deregister
	 * @throws NamingException If the removal failed
	 */
    public SchemaObject unregister( SchemaObject schemaObject ) throws NamingException
    {
        LOG.debug( "Unregistering {}:{}", schemaObject.getObjectType(), schemaObject.getOid() );

        String oid = schemaObject.getOid();
        SchemaObject unregistered = null;
        
        // First call the specific registry's register method
        switch ( schemaObject.getObjectType() )
        {
            case ATTRIBUTE_TYPE : 
                unregistered = attributeTypeRegistry.unregister( oid );
                break;
                
            case COMPARATOR : 
                unregistered = comparatorRegistry.unregister( oid );
                break;
                
            case DIT_CONTENT_RULE : 
                unregistered = ditContentRuleRegistry.unregister( oid );
                break;
                
            case DIT_STRUCTURE_RULE : 
                unregistered = ditStructureRuleRegistry.unregister( oid );
                break;
                
            case LDAP_SYNTAX : 
                unregistered = ldapSyntaxRegistry.unregister( oid );
                break;
                
            case MATCHING_RULE : 
                unregistered = matchingRuleRegistry.unregister( oid );
                break;
                
            case MATCHING_RULE_USE : 
                unregistered = matchingRuleUseRegistry.unregister( oid );
                break;
                
            case NAME_FORM : 
                unregistered = nameFormRegistry.unregister( oid );
                break;
                
            case NORMALIZER : 
                unregistered = normalizerRegistry.unregister( oid );
                break;
                
            case OBJECT_CLASS : 
                unregistered = objectClassRegistry.unregister( oid );
                break;
                
            case SYNTAX_CHECKER : 
                unregistered = syntaxCheckerRegistry.unregister( oid );
                break;
        }
        
        // And unregister the schemaObject within its schema
        Set<SchemaWrapper> content = schemaObjectsBySchemaName.get( StringTools.toLowerCase( schemaObject.getSchemaName() ) );
        
        SchemaWrapper schemaWrapper = new SchemaWrapper( schemaObject );
        
        if ( content.contains( schemaWrapper ) )
        {
            // remove the schemaObject
            content.remove( schemaWrapper );
        }
        else
        {
            // Not present !!
            // What should we do ?
            LOG.debug( "Unregistering of {}:{} failed, not found in Registries", 
                schemaObject.getObjectType(), schemaObject.getOid() );
        }
        
        return unregistered;
    }
    
    
    /**
     * Checks if a specific SchemaObject is referenced by any other SchemaObject.
     *
     * @param schemaObject The SchemaObject we are looking for
     * @return true if there is at least one SchemaObjetc referencing the given one
     */
    public boolean isReferenced( SchemaObject schemaObject )
    {
        SchemaWrapper wrapper = new SchemaWrapper( schemaObject );
        
        Set<SchemaWrapper> set = usedBy.get( wrapper );
        
        boolean referenced = ( set != null ) && ( set.size() != 0 );
        
        if ( LOG.isDebugEnabled() )
        {
            if ( referenced )
            {
                LOG.debug( "The {}:{} is referenced", schemaObject.getObjectType(),
                    schemaObject.getOid() );
            }
            else
            {
                LOG.debug( "The {}:{} is not referenced", schemaObject.getObjectType(),
                    schemaObject.getOid() );
            }
        }
        
        return referenced;
    }

    
    /**
     * Gets the Set of SchemaObjects referencing the given SchemaObject
     *
     * @param schemaObject The SchemaObject we are looking for
     * @return The Set of referencing SchemaObject, or null 
     */
    public Set<SchemaWrapper> getUsedBy( SchemaObject schemaObject )
    {
        SchemaWrapper wrapper = new SchemaWrapper( schemaObject );
        
        return usedBy.get( wrapper );
    }

    
    /**
     * Dump the UsedBy data structure as a String
     */
    public String dumpUsedBy()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append(  "USED BY :\n" );
        
        for ( SchemaWrapper wrapper : usedBy.keySet() )
        {
            sb.append( wrapper.get().getObjectType() ).append( '[' ).append( wrapper.get().getOid() ).append( "] : {" );
            
            boolean isFirst = true;
            
            for ( SchemaWrapper uses : usedBy.get( wrapper) )
            {
                if ( isFirst )
                {
                    isFirst = false;
                }
                else
                {
                    sb.append( ", " );
                }
                
                sb.append( uses.get().getObjectType() ).append( '[' ).append( wrapper.get().getOid() ).append( "]" );
            }
            
            sb.append( "}\n" );
        }
        
        return sb.toString();
    }

    
    /**
     * Dump the Using data structure as a String
     */
    public String dumpUsing()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append(  "USING :\n" );

        for ( SchemaWrapper wrapper : using.keySet() )
        {
            sb.append( wrapper.get().getObjectType() ).append( '[' ).append( wrapper.get().getOid() ).append( "] : {" );
            
            boolean isFirst = true;
            
            for ( SchemaWrapper uses : using.get( wrapper) )
            {
                if ( isFirst )
                {
                    isFirst = false;
                }
                else
                {
                    sb.append( ", " );
                }
                
                sb.append( uses.get().getObjectType() ).append( '[' ).append( wrapper.get().getOid() ).append( "]" );
            }
            
            sb.append( "}\n" );
        }
        
        return sb.toString();
    }
    
    /**
     * Gets the Set of SchemaObjects referenced by the given SchemaObject
     *
     * @param schemaObject The SchemaObject we are looking for
     * @return The Set of referenced SchemaObject, or null 
     */
    public Set<SchemaWrapper> getUsing( SchemaObject schemaObject )
    {
        SchemaWrapper wrapper = new SchemaWrapper( schemaObject );
        
        return using.get( wrapper );
    }
    
    
    /**
     * Add an association between a SchemaObject an the SchemaObject it refers
     *
     * @param reference The base SchemaObject
     * @param referee The SchemaObject pointing on the reference
     */
    private void addUsing( SchemaObject reference, SchemaObject referee )
    {
        if ( ( reference == null ) || ( referee == null ) )
        {
            return;
        }
        
        SchemaWrapper wrapper = new SchemaWrapper( reference );
        
        Set<SchemaWrapper> uses = getUsing( reference );
        
        if ( uses == null )
        {
            uses = new HashSet<SchemaWrapper>();
        }
        
        uses.add( new SchemaWrapper( referee ) );
        
        // Put back the set (this is a concurrentHashMap, it won't be replaced implicitly
        using.put( wrapper, uses );
    }
    
    
    /**
     * Add an association between a SchemaObject an the SchemaObject it refers
     *
     * @param reference The base SchemaObject
     * @param referee The SchemaObject pointing on the reference
     */
    public void addReference( SchemaObject reference, SchemaObject referee )
    {
        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( dump( "add", reference, referee ) );
        }

        addUsing( reference, referee );
        addUsedBy( referee, reference );

        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( dumpUsedBy() );
            LOG.debug( dumpUsing() );
        }
    }


    /**
     * Add an association between a SchemaObject an the SchemaObject that refers it
     *
     * @param reference The base SchemaObject
     * @param referee The SchemaObject pointing on the reference
     */
    private void addUsedBy( SchemaObject referee, SchemaObject reference )
    {
        if ( ( reference == null ) || ( referee == null ) )
        {
            return;
        }
        
        SchemaWrapper wrapper = new SchemaWrapper( referee );
        
        Set<SchemaWrapper> uses = getUsedBy( referee );
        
        if ( uses == null )
        {
            uses = new HashSet<SchemaWrapper>();
        }
        
        uses.add( new SchemaWrapper( reference ) );
        
        // Put back the set (this is a concurrentHashMap, it won't be replaced implicitly
        usedBy.put( wrapper, uses );
    }
    
    
    /**
     * Del an association between a SchemaObject an the SchemaObject it refers
     *
     * @param reference The base SchemaObject
     * @param referee The SchemaObject pointing on the reference
     */
    private void delUsing( SchemaObject reference, SchemaObject referee )
    {
        if ( ( reference == null ) || ( referee == null ) )
        {
            return;
        }
        
        Set<SchemaWrapper> uses = getUsing( reference );
        
        if ( uses == null )
        {
            return;
        }
        
        uses.remove( new SchemaWrapper( referee ) );
        
        SchemaWrapper wrapper = new SchemaWrapper( reference );
        
        if ( uses.size() == 0 )
        {
            using.remove( wrapper );
        }
        else
        {
            using.put( wrapper, uses );
        }
        
        return;
    }


    /**
     * Del an association between a SchemaObject an the SchemaObject that refers it
     *
     * @param reference The base SchemaObject
     * @param referee The SchemaObject pointing on the reference
     */
    private void delUsedBy( SchemaObject referee, SchemaObject reference )
    {
        if ( ( reference == null ) || ( referee == null ) )
        {
            return;
        }

        Set<SchemaWrapper> uses = getUsedBy( referee );
        
        if ( uses == null )
        {
            return;
        }
        
        uses.remove( new SchemaWrapper( reference ) );
        
        SchemaWrapper wrapper = new SchemaWrapper( referee );

        if ( uses.size() == 0 )
        {
            usedBy.remove( wrapper );
        }
        else
        {
            usedBy.put( wrapper, uses );
        }
        
        return;
    }
    
    
    /**
     * Delete an association between a SchemaObject an the SchemaObject it refers
     *
     * @param reference The base SchemaObject
     * @param referee The SchemaObject pointing on the reference
     */
    public void delReference( SchemaObject reference, SchemaObject referee )
    {
        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( dump( "del", reference, referee ) );
        }
        
        delUsing( reference, referee );
        delUsedBy( referee, reference );
        
        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( dumpUsedBy() );
            LOG.debug( dumpUsing() );
        }
    }
    

    /**
     * Dump the reference operation as a String
     */
    private String dump( String op, SchemaObject reference, SchemaObject referee )
    {
        return op + " : " + reference.getObjectType() + "[" + reference.getOid() + "]/[" + referee.getObjectType() + "[" + referee.getOid() +"]";
    }

    
    private boolean checkReferences( SchemaObject reference, SchemaObject referee, String message )
    {
        SchemaWrapper referenceWrapper = new SchemaWrapper( reference );
        SchemaWrapper refereeWrapper = new SchemaWrapper( referee );
        
        // Check the references : Syntax -> SyntaxChecker
        if ( !using.containsKey( referenceWrapper ) )
        {
            LOG.debug( "The Syntax {}:{} does not reference any " + message, 
                reference.getObjectType(), reference.getOid() );
            
            return false;
        }
        
        Set<SchemaWrapper> usings = using.get( referenceWrapper );

        if ( !usings.contains( refereeWrapper ) )
        {
            LOG.debug( "The {}:{} does not reference any " + message, 
                reference.getObjectType(), reference.getOid() );
            
            return false;
        }
        
        // Check the referees : SyntaxChecker -> Syntax
        if ( !usedBy.containsKey( refereeWrapper ) )
        {
            LOG.debug( "The {}:{} is not referenced by any " + message, 
                referee.getObjectType(), referee.getOid() );
            
            return false;
        }
        
        Set<SchemaWrapper> used = usedBy.get( refereeWrapper );

        if ( !used.contains( referenceWrapper ) )
        {
            LOG.debug( "The {}:{} is not referenced by any " + message, 
                referee.getObjectType(), referee.getOid() );
            
            return false;
        }
        
        return true;
    }

    
    /**
     * Check the registries for invalid relations. This check stops at the first error.
     *
     * @return true if the Registries is consistent, false otherwise
     */
    public boolean check()
    {
        // Check the Syntaxes : check for a SyntaxChecker
        LOG.debug( "Checking Syntaxes" );
        
        for ( LdapSyntax syntax : ldapSyntaxRegistry )
        {
            // Check that each Syntax has a SyntaxChecker
            if ( syntax.getSyntaxChecker() == null )
            {
                LOG.debug( "The Syntax {} has no SyntaxChecker", syntax );
                
                return false;
            }
            
            if ( !syntaxCheckerRegistry.contains( syntax.getSyntaxChecker().getOid() ) )
            {
                LOG.debug( "Cannot find the SyntaxChecker {} for the Syntax {}",
                    syntax.getSyntaxChecker().getOid(), syntax );
                
                return false;
            }
            
            // Check the references : Syntax -> SyntaxChecker and SyntaxChecker -> Syntax 
            if ( !checkReferences( syntax, syntax.getSyntaxChecker(), "SyntaxChecker" ) )
            {
                return false;
            }
        }

        // Check the MatchingRules : check for a Normalizer, a Comparator and a Syntax
        LOG.debug( "Checking MatchingRules..." );
        
        for ( MatchingRule matchingRule : matchingRuleRegistry )
        {
            // Check that each MatchingRule has a Normalizer
            if ( matchingRule.getNormalizer() == null )
            {
                LOG.debug( "The MatchingRule {} has no Normalizer", matchingRule );
                
                return false;
            }
            
            // Check that each MatchingRule has a Normalizer
            if ( !normalizerRegistry.contains( matchingRule.getNormalizer().getOid() ) )
            {
                LOG.debug( "Cannot find the Normalizer {} for the MatchingRule {}",
                    matchingRule.getNormalizer().getOid(), matchingRule );
                
                return false;
            }

            // Check that each MatchingRule has a Comparator
            if ( matchingRule.getLdapComparator() == null )
            {
                LOG.debug( "The MatchingRule {} has no Comparator", matchingRule );
                
                return false;
            }
            
            if ( !comparatorRegistry.contains( matchingRule.getLdapComparator().getOid() ) )
            {
                LOG.debug( "Cannot find the Comparator {} for the MatchingRule {}",
                    matchingRule.getLdapComparator().getOid(), matchingRule );
                
                return false;
            }
            
            // Check that each MatchingRule has a Syntax
            if ( matchingRule.getSyntax() == null )
            {
                LOG.debug( "The MatchingRule {} has no Syntax", matchingRule );
                
                return false;
            }

            if ( !ldapSyntaxRegistry.contains( matchingRule.getSyntax().getOid() ) )
            {
                LOG.debug( "Cannot find the Syntax {} for the MatchingRule {}",
                    matchingRule.getSyntax().getOid(), matchingRule );
                
                return false;
            }

            // Check the references : MR -> S and S -> MR 
            if ( !checkReferences( matchingRule, matchingRule.getSyntax(), "Syntax" ) )
            {
                return false;
            }

            // Check the references : MR -> N 
            if ( !checkReferences( matchingRule, matchingRule.getNormalizer(), "Normalizer" ) )
            {
                return false;
            }

            // Check the references : MR -> C and C -> MR 
            if ( !checkReferences( matchingRule, matchingRule.getLdapComparator(), "Comparator" ) )
            {
                return false;
            }
        }
        
        // Check the ObjectClasses : check for MAY, MUST, SUPERIORS
        LOG.debug( "Checking ObjectClasses..." );
        
        for ( ObjectClass objectClass : objectClassRegistry )
        {
            // Check that each ObjectClass has all the MAY AttributeTypes
            if ( objectClass.getMayAttributeTypes() != null )
            {
                for ( AttributeType may:objectClass.getMayAttributeTypes() )
                {
                    if ( !attributeTypeRegistry.contains( may.getOid() ) )
                    {
                        LOG.debug( "Cannot find the AttributeType {} for the ObjectClass {} MAY",
                            may, objectClass );
                        
                        return false;
                    }

                    // Check the references : OC -> AT  and AT -> OC (MAY) 
                    if ( !checkReferences( objectClass, may, "AttributeType" ) )
                    {
                        return false;
                    }
                }
            }
            
            // Check that each ObjectClass has all the MUST AttributeTypes
            if ( objectClass.getMustAttributeTypes() != null )
            {
                for ( AttributeType must:objectClass.getMustAttributeTypes() )
                {
                    if ( !attributeTypeRegistry.contains( must.getOid() ) )
                    {
                        LOG.debug( "Cannot find the AttributeType {} for the ObjectClass {} MUST",
                            must, objectClass );
                        
                        return false;
                    }

                    // Check the references : OC -> AT  and AT -> OC (MUST) 
                    if ( !checkReferences( objectClass, must, "AttributeType" ) )
                    {
                        return false;
                    }
                }
            }
            
            // Check that each ObjectClass has all the SUPERIORS ObjectClasses
            if ( objectClass.getSuperiors() != null )
            {
                for ( ObjectClass superior:objectClass.getSuperiors() )
                {
                    if ( !objectClassRegistry.contains( objectClass.getOid() ) )
                    {
                        LOG.debug( "Cannot find the ObjectClass {} for the ObjectClass {} SUPERIORS",
                            superior, objectClass );
                        
                        return false;
                    }

                    // Check the references : OC -> OC  and OC -> OC (SUPERIORS) 
                    if ( !checkReferences( objectClass, superior, "ObjectClass" ) )
                    {
                        return false;
                    }
                }
            }
        }
        
        // Check the AttributeTypes : check for MatchingRules, Syntaxes
        LOG.debug( "Checking AttributeTypes..." );
        
        for ( AttributeType attributeType : attributeTypeRegistry )
        {
            // Check that each AttributeType has a SYNTAX 
            if ( attributeType.getSyntax() == null )
            {
                LOG.debug( "The AttributeType {} has no Syntax", attributeType );
                
                return false;
            }
            
            if ( !ldapSyntaxRegistry.contains( attributeType.getSyntax().getOid() ) )
            {
                LOG.debug( "Cannot find the Syntax {} for the AttributeType {}",
                    attributeType.getSyntax().getOid(), attributeType );
                
                return false;
            }
            
            // Check the references for AT -> S and S -> AT
            if ( !checkReferences( attributeType, attributeType.getSyntax(), "AttributeType" ) )
            {
                return false;
            }
            
            // Check the EQUALITY MatchingRule
            if ( attributeType.getEquality() != null )
            {
                if ( !matchingRuleRegistry.contains( attributeType.getEquality().getOid() ) )
                {
                    LOG.debug( "Cannot find the MatchingRule {} for the AttributeType {}",
                        attributeType.getEquality().getOid(), attributeType );
                    
                    return false;
                }

                // Check the references for AT -> MR and MR -> AT
                if ( !checkReferences( attributeType, attributeType.getEquality(), "AttributeType" ) )
                {
                    return false;
                }
            }
            
            // Check the ORDERING MatchingRule
            if ( attributeType.getOrdering() != null )
            {
                if ( !matchingRuleRegistry.contains( attributeType.getOrdering().getOid() ) )
                {
                    LOG.debug( "Cannot find the MatchingRule {} for the AttributeType {}",
                        attributeType.getOrdering().getOid(), attributeType );
                    
                    return false;
                }

                // Check the references for AT -> MR and MR -> AT
                if ( !checkReferences( attributeType, attributeType.getOrdering(), "AttributeType" ) )
                {
                    return false;
                }
            }

            // Check the SUBSTR MatchingRule
            if ( attributeType.getSubstring() != null )
            {
                if ( !matchingRuleRegistry.contains( attributeType.getSubstring().getOid() ) )
                {
                    LOG.debug( "Cannot find the MatchingRule {} for the AttributeType {}",
                        attributeType.getSubstring().getOid(), attributeType );
                    
                    return false;
                }

                // Check the references for AT -> MR and MR -> AT
                if ( !checkReferences( attributeType, attributeType.getSubstring(), "AttributeType" ) )
                {
                    return false;
                }
            }
            
            // Check the SUP
            if ( attributeType.getSuperior() != null )
            {
                AttributeType superior =attributeType.getSuperior();
                
                if ( !attributeTypeRegistry.contains( superior.getOid() ) )
                {
                    LOG.debug( "Cannot find the AttributeType {} for the AttributeType {} SUPERIOR",
                        superior, attributeType );
                    
                    return false;
                }

                // Check the references : AT -> AT  and AT -> AT (SUPERIOR) 
                if ( !checkReferences( attributeType, superior, "AttributeType" ) )
                {
                    return false;
                }
            }
        }

        return true;
    }
    
    
    /**
     * Clone the Registries
     */
    public Registries clone() throws CloneNotSupportedException
    {
        // First clone the structure
        Registries clone = (Registries)super.clone();
        
        // We have to clone every SchemaObject registries now
        clone.attributeTypeRegistry = (AttributeTypeRegistry)attributeTypeRegistry.clone();
        clone.comparatorRegistry = comparatorRegistry.clone();
        clone.ditContentRuleRegistry = ditContentRuleRegistry.clone();
        clone.ditStructureRuleRegistry = ditStructureRuleRegistry.clone();
        clone.ldapSyntaxRegistry = ldapSyntaxRegistry.clone();
        clone.matchingRuleRegistry = matchingRuleRegistry.clone();
        clone.matchingRuleUseRegistry = matchingRuleUseRegistry.clone();
        clone.nameFormRegistry = nameFormRegistry.clone();
        clone.normalizerRegistry = normalizerRegistry.clone();
        clone.objectClassRegistry = objectClassRegistry.clone();
        clone.syntaxCheckerRegistry = syntaxCheckerRegistry.clone();
        
        // Now, clone the oidRegistry
        clone.oidRegistry = oidRegistry.clone();
        
        // Clone the schema list
        clone.loadedSchemas = new HashMap<String, Schema>();
        
        for ( String schemaName : loadedSchemas.keySet() )
        {
            // We don't cone the schemas
            clone.loadedSchemas.put( schemaName, loadedSchemas.get( schemaName ) );
        }
        
        // Last, not least, clone the Using and usedBy structures
        // They will be empty
        clone.using = new HashMap<SchemaWrapper, Set<SchemaWrapper>>();
        clone.usedBy = new HashMap<SchemaWrapper, Set<SchemaWrapper>>();
        
        // Now, check the registries. We don"t care about errors
        checkRefInteg();
        
        // Last, rebuild the using and usedBy references
        buildReferences();
        
        return clone;
    }

    
    /**
     * Tells if the Registries is permissive or if it must be checked 
     * against inconsistencies.
     *
     * @return True if SchemaObjects can be added even if they break the consistency 
     */
    public boolean isRelaxed()
    {
        return isRelaxed;
    }

    
    /**
     * Tells if the Registries is strict.
     *
     * @return True if SchemaObjects cannot be added if they break the consistency 
     */
    public boolean isStrict()
    {
        return !isRelaxed;
    }

    
    /**
     * Change the Registries to a relaxed mode, where invalid SchemaObjects
     * can be registered.
     */
    public void setRelaxed()
    {
        isRelaxed = RELAXED;
    }

    
    /**
     * Change the Registries to a strict mode, where invalid SchemaObjects
     * cannot be registered.
     */
    public void setStrict()
    {
        isRelaxed = STRICT;
    }


    /**
     * Tells if the Registries accept disabled elements.
     *
     * @return True if disabled SchemaObjects can be added 
     */
    public boolean isDisabledAccepted()
    {
        return disabledAccepted;
    }
    
    
    /**
     * Change the Registries behavior regarding disabled SchemaObject element.
     *
     * @param acceptDisabled If <code>false</code>, then the Registries won't accept
     * disabled SchemaObject or enabled SchemaObject from disabled schema 
     */
    public void setDisabledAccepted( boolean disabledAccepted )
    {
        this.disabledAccepted = disabledAccepted;
    }
    
    
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append( "Registries [" );
        
        if ( isRelaxed )
        {
            sb.append( "RELAXED," );
        }
        else
        {
            sb.append( "STRICT," );
        }
        
        if ( disabledAccepted )
        {
            sb.append( " Disabled accepted] :\n" );
        }
        else
        {
            sb.append( " Disabled forbidden] :\n" );
        }
        
        sb.append( "loaded schemas [" );
        boolean isFirst = true;
        
        for ( String schema:loadedSchemas.keySet() )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                sb.append( ", " );
            }
            
            sb.append( schema );
        }
        
        sb.append( "]\n" );
        
        sb.append( "AttributeTypes : " ).append( attributeTypeRegistry.size() ).append( "\n" );
        sb.append( "Comparators : " ).append( comparatorRegistry.size() ).append( "\n" );
        sb.append( "DitContentRules : " ).append( ditContentRuleRegistry.size() ).append( "\n" );
        sb.append( "DitStructureRules : " ).append( ditStructureRuleRegistry.size() ).append( "\n" );
        sb.append( "MatchingRules : " ).append( matchingRuleRegistry.size() ).append( "\n" );
        sb.append( "MatchingRuleUses : " ).append( matchingRuleUseRegistry.size() ).append( "\n" );
        sb.append( "NameForms : " ).append( nameFormRegistry.size() ).append( "\n" );
        sb.append( "Normalizers : " ).append( normalizerRegistry.size() ).append( "\n" );
        sb.append( "ObjectClasses : " ).append( objectClassRegistry.size() ).append( "\n" );
        sb.append( "Syntaxes : " ).append( ldapSyntaxRegistry.size() ).append( "\n" );
        sb.append( "SyntaxCheckers : " ).append( syntaxCheckerRegistry.size() ).append( "\n" );
        
        sb.append( "OidRegistry : " ).append( oidRegistry.size() ).append( '\n' );
        
        return sb.toString();
    }
}
