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
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;

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
import org.apache.directory.shared.ldap.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Document this class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class Registries implements SchemaLoaderListener
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
        attributeTypeRegistry = new AttributeTypeRegistry( oidRegistry );
        comparatorRegistry = new ComparatorRegistry( oidRegistry );
        ditContentRuleRegistry = new DITContentRuleRegistry( oidRegistry );
        ditStructureRuleRegistry = new DITStructureRuleRegistry( oidRegistry );
        ldapSyntaxRegistry = new LdapSyntaxRegistry( oidRegistry );
        matchingRuleRegistry = new MatchingRuleRegistry( oidRegistry );
        matchingRuleUseRegistry = new MatchingRuleUseRegistry( oidRegistry );
        nameFormRegistry = new NameFormRegistry( oidRegistry );
        normalizerRegistry = new NormalizerRegistry( oidRegistry );
        objectClassRegistry = new ObjectClassRegistry( oidRegistry );
        syntaxCheckerRegistry = new SyntaxCheckerRegistry( oidRegistry );
        schemaObjectsBySchemaName = new ConcurrentHashMap<String, Set<SchemaWrapper>>();
        usedBy = new ConcurrentHashMap<SchemaWrapper, Set<SchemaWrapper>>();
        using = new ConcurrentHashMap<SchemaWrapper, Set<SchemaWrapper>>();
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
     *
     * @return a list of exceptions encountered while resolving entities
     */
    public List<Throwable> checkRefInteg()
    {
        ArrayList<Throwable> errors = new ArrayList<Throwable>();

        // Check the ObjectClasses
        for ( ObjectClass objectClass : objectClassRegistry )
        {
            resolve( objectClass, errors );
        }

        // Check the AttributeTypes
        for ( AttributeType attributeType : attributeTypeRegistry )
        {
            resolve( attributeType, errors );
        }

        // Check the MatchingRules
        for ( MatchingRule matchingRule : matchingRuleRegistry )
        {
            resolve( matchingRule, errors );
        }

        // Check the LdapSyntax
        for ( LdapSyntax ldapSyntax : ldapSyntaxRegistry )
        {
            resolve( ldapSyntax, errors );
        }

        return errors;
    }

    
    /**
     * Attempts to resolve the SyntaxChecker associated with a Syntax.
     *
     * @param syntax the LdapSyntax to resolve the SyntaxChecker of
     * @param errors the list of errors to add exceptions to
     * @return true if it succeeds, false otherwise
     */
    private boolean resolve( LdapSyntax syntax, List<Throwable> errors )
    {
        if ( syntax == null )
        {
            return true;
        }

        try
        {
            syntax.getSyntaxChecker();
            return true;
        }
        catch ( Exception e )
        {
            errors.add( e );
            return false;
        }
    }


    /**
     * Check if the Comparator and the syntax are existing for a matchingRule
     */
    private boolean resolve( MatchingRule mr, List<Throwable> errors )
    {
        boolean isSuccess = true;

        if ( mr == null )
        {
            return true;
        }

        try
        {
            if ( mr.getLdapComparator() == null )
            {
                String schema = matchingRuleRegistry.getSchemaName( mr.getOid() );
                errors.add( new NullPointerException( "matchingRule " + mr.getName() + " in schema " + schema
                    + " with OID " + mr.getOid() + " has a null comparator" ) );
                isSuccess = false;
            }
        }
        catch ( Exception e )
        {
            errors.add( e );
            isSuccess = false;
        }

        try
        {
            isSuccess &= resolve( mr.getSyntax(), errors );

            if ( mr.getSyntax() == null )
            {
                String schema = matchingRuleRegistry.getSchemaName( mr.getOid() );
                errors.add( new NullPointerException( "matchingRule " + mr.getName() + " in schema " + schema
                    + " with OID " + mr.getOid() + " has a null Syntax" ) );
                isSuccess = false;
            }
        }
        catch ( Exception e )
        {
            errors.add( e );
            isSuccess = false;
        }

        return isSuccess;
    }


    /**
     * Check the inheritance, and the existence of MatchingRules and LdapSyntax
     * for an attribute 
     */
    private boolean resolve( AttributeType at, List<Throwable> errors )
    {
        boolean isSuccess = true;

        boolean hasMatchingRule = false;

        if ( at == null )
        {
            return true;
        }

        try
        {
            isSuccess &= resolve( at.getSuperior(), errors );
        }
        catch ( Exception e )
        {
            errors.add( e );
            isSuccess = false;
        }

        try
        {
            isSuccess &= resolve( at.getEquality(), errors );

            if ( at.getEquality() != null )
            {
                hasMatchingRule |= true;
            }
        }
        catch ( Exception e )
        {
            errors.add( e );
            isSuccess = false;
        }

        try
        {
            isSuccess &= resolve( at.getOrdering(), errors );

            if ( at.getOrdering() != null )
            {
                hasMatchingRule |= true;
            }
        }
        catch ( Exception e )
        {
            errors.add( e );
            isSuccess = false;
        }

        try
        {
            isSuccess &= resolve( at.getSubstring(), errors );

            if ( at.getSubstring() != null )
            {
                hasMatchingRule |= true;
            }
        }
        catch ( Exception e )
        {
            errors.add( e );
            isSuccess = false;
        }

        try
        {
            isSuccess &= resolve( at.getSyntax(), errors );

            if ( at.getSyntax() == null )
            {
                String schema = attributeTypeRegistry.getSchemaName( at.getOid() );

                errors.add( new NullPointerException( "attributeType " + at.getName() + " in schema " + schema
                    + " with OID " + at.getOid() + " has a null Syntax" ) );

                isSuccess = false;
            }
        }
        catch ( Exception e )
        {
            errors.add( e );
            isSuccess = false;
        }

        return isSuccess;
    }


    private boolean resolve( ObjectClass oc, List<Throwable> errors )
    {
        boolean isSuccess = true;

        if ( oc == null )
        {
            return true;
        }

        List<ObjectClass> superiors = oc.getSuperiors();

        if ( ( superiors == null ) || ( superiors.size() == 0 ) )
        {
            isSuccess = false;
        }
        else
        {
            for ( ObjectClass superior : superiors )
            {
                isSuccess &= resolve( superior, errors );
            }
        }

        for ( AttributeType attributeType : oc.getMayAttributeTypes() )
        {
            isSuccess &= resolve( attributeType, errors );
        }

        for ( AttributeType attributeType : oc.getMustAttributeTypes() )
        {
            isSuccess &= resolve( attributeType, errors );
        }

        return isSuccess;
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
    public void unregister( SchemaObject schemaObject ) throws NamingException
    {
        LOG.debug( "Unregistering {}:{}", schemaObject.getObjectType(), schemaObject.getOid() );

        String oid = schemaObject.getOid();
        
        // First call the specific registry's register method
        switch ( schemaObject.getObjectType() )
        {
            case ATTRIBUTE_TYPE : 
                attributeTypeRegistry.unregister( oid );
                break;
                
            case COMPARATOR : 
                comparatorRegistry.unregister( oid );
                break;
                
            case DIT_CONTENT_RULE : 
                ditContentRuleRegistry.unregister( oid );
                break;
                
            case DIT_STRUCTURE_RULE : 
                ditStructureRuleRegistry.unregister( oid );
                break;
                
            case LDAP_SYNTAX : 
                ldapSyntaxRegistry.unregister( oid );
                break;
                
            case MATCHING_RULE : 
                matchingRuleRegistry.unregister( oid );
                break;
                
            case MATCHING_RULE_USE : 
                matchingRuleUseRegistry.unregister( oid );
                break;
                
            case NAME_FORM : 
                nameFormRegistry.unregister( oid );
                break;
                
            case NORMALIZER : 
                normalizerRegistry.unregister( oid );
                break;
                
            case OBJECT_CLASS : 
                objectClassRegistry.unregister( oid );
                break;
                
            case SYNTAX_CHECKER : 
                syntaxCheckerRegistry.unregister( oid );
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
    private String dumpUsedBy()
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
    private String dumpUsing()
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
                reference.getObjectType(), reference.getOid() );
            
            return false;
        }
        
        Set<SchemaWrapper> used = usedBy.get( referenceWrapper );

        if ( !used.contains( refereeWrapper ) )
        {
            LOG.debug( "The {}:{} is not referenced by any " + message, 
                reference.getObjectType(), reference.getOid() );
            
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
            if ( checkReferences( syntax, syntax.getSyntaxChecker(), "SyntaxChecker" ) )
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
            if ( checkReferences( matchingRule, matchingRule.getSyntax(), "Syntax" ) )
            {
                return false;
            }

            // Check the references : MR -> N 
            if ( checkReferences( matchingRule, matchingRule.getNormalizer(), "Normalizer" ) )
            {
                return false;
            }

            // Check the references : MR -> C and C -> MR 
            if ( checkReferences( matchingRule, matchingRule.getLdapComparator(), "Comparator" ) )
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
                    if ( checkReferences( objectClass, may, "AttributeType" ) )
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
                    if ( checkReferences( objectClass, must, "AttributeType" ) )
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
                    if ( checkReferences( objectClass, superior, "ObjectClass" ) )
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
            if ( checkReferences( attributeType, attributeType.getSyntax(), "AttributeType" ) )
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
                if ( checkReferences( attributeType, attributeType.getEquality(), "AttributeType" ) )
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
                if ( checkReferences( attributeType, attributeType.getOrdering(), "AttributeType" ) )
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
                if ( checkReferences( attributeType, attributeType.getSubstring(), "AttributeType" ) )
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
                if ( checkReferences( attributeType, superior, "AttributeType" ) )
                {
                    return false;
                }
            }
        }

        return true;
    }
}
