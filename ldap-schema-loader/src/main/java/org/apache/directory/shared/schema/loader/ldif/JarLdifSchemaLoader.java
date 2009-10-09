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
package org.apache.directory.shared.schema.loader.ldif;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.directory.shared.ldap.NotImplementedException;
import org.apache.directory.shared.ldap.constants.MetaSchemaConstants;
import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.apache.directory.shared.ldap.entry.Value;
import org.apache.directory.shared.ldap.ldif.LdifEntry;
import org.apache.directory.shared.ldap.ldif.LdifReader;
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
import org.apache.directory.shared.ldap.schema.SyntaxChecker;
import org.apache.directory.shared.ldap.schema.ldif.extractor.ResourceMap;
import org.apache.directory.shared.ldap.schema.ldif.extractor.SchemaLdifExtractor;
import org.apache.directory.shared.ldap.schema.registries.AbstractSchemaLoader;
import org.apache.directory.shared.ldap.schema.registries.Registries;
import org.apache.directory.shared.ldap.schema.registries.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Loads schema data from LDIF files containing entries representing schema
 * objects, using the meta schema format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Revision$
 */
public class JarLdifSchemaLoader extends AbstractSchemaLoader
{
    /** ldif file extension used */
    private static final String LDIF_EXT = "ldif";
    
    /** static class logger */
    private static final Logger LOG = LoggerFactory.getLogger( JarLdifSchemaLoader.class );

    /** Speedup for DEBUG mode */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /** the factory that generates respective SchemaObjects from LDIF entries */
    private final SchemaEntityFactory factory = new SchemaEntityFactory();
    
    /** a map of all the resources in this jar */
    private static final Map<String,Boolean> RESOURCE_MAP = ResourceMap.getResources( Pattern.compile( ".*schema/ou=schema.*" ) );

    
    /**
     * Creates a new LDIF based SchemaLoader. The constructor checks to make
     * sure the supplied base directory exists and contains a schema.ldif file
     * and if not complains about it.
     *
     * @param baseDirectory the schema LDIF base URL
     * @throws Exception if the base directory does not exist or does not
     * a valid schema.ldif file
     */
    public JarLdifSchemaLoader() throws Exception
    {
        initializeSchemas();
    }

    
    private final URL getResource( String resource, String msg ) throws Exception
    {
        if ( RESOURCE_MAP.get( resource ) )
        {
            return SchemaLdifExtractor.getUniqueResource( resource, msg );
        }
        else
        {
            return new File( resource ).toURI().toURL();
        }
    }
    

    /**
     * Scans for LDIF files just describing the various schema contained in
     * the schema repository.
     *
     * @throws Exception
     */
    private void initializeSchemas() throws Exception
    {
        if ( IS_DEBUG )
        {
            LOG.debug( "Initializing schema" );
        }
        
        for ( String file : RESOURCE_MAP.keySet() )
        {
            Pattern pat = Pattern.compile( ".*schema/ou=schema/cn=[a-z]*\\." + LDIF_EXT );
            
            if ( pat.matcher( file ).matches() )
            {
                URL resource = getResource( file, "schema LDIF file" );
                
                try
                {
                    LdifReader reader = new LdifReader( resource.openStream() );
                    LdifEntry entry = reader.next();
                    Schema schema = factory.getSchema( entry.getEntry() );
                    schemaMap.put( schema.getSchemaName(), schema );
                    
                    if ( IS_DEBUG )
                    {
                        LOG.debug( "Schema Initialized ... \n{}", schema );
                    }
                }
                catch ( Exception e )
                {
                    LOG.error( "Failed to load schema LDIF file " + file, e );
                    throw e;
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public Schema getSchema( String schemaName ) throws Exception
    {
        return this.schemaMap.get( schemaName );
    }


    /**
     * {@inheritDoc}
     */
    public void loadWithDependencies( Collection<Schema> schemas, Registries registries ) throws Exception
    {
        Map<String,Schema> notLoaded = new HashMap<String,Schema>();
        
        for ( Schema schema : schemas )
        {
            if ( ! registries.isSchemaLoaded( schema.getSchemaName() ) )
            {
                notLoaded.put( schema.getSchemaName(), schema );
            }
        }
        
        for ( Schema schema : notLoaded.values() )
        {
            Stack<String> beenthere = new Stack<String>();
            super.loadDepsFirst( schema, beenthere, notLoaded, schema, registries );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void loadWithDependencies( Schema schema, Registries registries ) throws Exception
    {
        Stack<String> beenthere = new Stack<String>();
        Map<String,Schema> notLoaded = new HashMap<String,Schema>();
        
        for ( Schema candidate : schemaMap.values() )
        {
            if ( ! registries.isSchemaLoaded( candidate.getSchemaName() ) )
            {
                notLoaded.put( candidate.getSchemaName(), candidate );
            }
        }
        
        super.loadDepsFirst( schema, beenthere, notLoaded, schema, registries );
    }


    /**
     * Loads a single schema if it has not been loaded already.  If the schema
     * load request was made because some other schema depends on this one then
     * the schema is checked to see if it is disabled.  If disabled it is 
     * enabled with a write to disk and then loaded. Listeners are notified that
     * the schema has been loaded.
     * 
     * {@inheritDoc}
     */
    public void load( Schema schema, Registries registries, boolean isDepLoad ) throws Exception
    {
        // if we're loading a dependency and it has not been enabled on 
        // disk then we cannot enable on disk from within a jar.  So the
        // enableSchema method just marks the Schema as enabled.
        if ( schema.isDisabled() && isDepLoad )
        {
            schema.enable();
        }
        
        if ( registries.isSchemaLoaded( schema.getSchemaName() ) )
        {
            LOG.info( "Will not attempt to load already loaded '{}' " +
            		"schema: \n{}", schema.getSchemaName(), schema );
            return;
        }
        
        LOG.info( "Loading {} schema: \n{}", schema.getSchemaName(), schema );
        
        registries.schemaLoaded( schema );
        
        try
        {
            loadComparators( schema, registries );
            loadNormalizers( schema, registries );
            loadSyntaxCheckers( schema, registries );
            loadSyntaxes( schema, registries );
            loadMatchingRules( schema, registries );
            loadAttributeTypes( schema, registries );
            loadObjectClasses( schema, registries );
            loadMatchingRuleUses( schema, registries );
            loadDitContentRules( schema, registries );
            loadNameForms( schema, registries );
            loadDitStructureRules( schema, registries );
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage() );
        }

        notifyListenerOrRegistries( schema, registries );
    }

    
    /**
     * Utility method to get the path for a schema directory.
     *
     * @param schema the schema to get the path for
     * @return the path for the specific schema directory
     */
    private final String getSchemaDirectory( Schema schema )
    {
        return "schema/ou=schema/cn=" + schema.getSchemaName();
    }
    
    
    /**
     * Loads the Comparators from LDIF files in the supplied schema into the 
     * supplied registries.
     *
     * @param schema the schema for which comparators are loaded
     * @param registries the registries which are loaded with comparators
     * @throws Exception if there are failures accessing comparator information
     * stored in LDIF files
     */
    private void loadComparators( Schema schema, Registries registries ) throws Exception
    {
        String comparatorsDirectory = getSchemaDirectory( schema ) 
            + "/" + SchemaConstants.COMPARATORS_PATH;
        
        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + comparatorsDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "comparator LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();
                LdapComparator<?> comparator = 
                    factory.getLdapComparator( entry.getEntry(), registries, schema.getSchemaName() );
                comparator.setOid( entry.get( MetaSchemaConstants.M_OID_AT ).getString() );

                if ( schema.isEnabled() && comparator.isEnabled() )
                {
                    comparator.applyRegistries( registries );
                }

                registries.register( comparator );
            }
        }
    }
    
    
    /**
     * Loads the SyntaxCheckers from LDIF files in the supplied schema into the 
     * supplied registries.
     *
     * @param schema the schema for which syntaxCheckers are loaded
     * @param targetRegistries the registries which are loaded with syntaxCheckers
     * @throws Exception if there are failures accessing syntaxChecker 
     * information stored in LDIF files
     */
    private void loadSyntaxCheckers( Schema schema, Registries registries ) throws Exception
    {
        String syntaxCheckersDirectory = getSchemaDirectory( schema ) 
            +  "/" + SchemaConstants.SYNTAX_CHECKERS_PATH;

        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + syntaxCheckersDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "syntaxChecker LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();
                SyntaxChecker syntaxChecker = 
                    factory.getSyntaxChecker( entry.getEntry(), registries, schema.getSchemaName() );

                if ( schema.isEnabled() && syntaxChecker.isEnabled() )
                {
                    syntaxChecker.applyRegistries( registries );
                }

                registries.register( syntaxChecker );
            }
        }
    }
    
    
    /**
     * Loads the Normalizers from LDIF files in the supplied schema into the 
     * supplied registries.
     *
     * @param schema the schema for which normalizers are loaded
     * @param registries the registries which are loaded with normalizers
     * @throws Exception if there are failures accessing normalizer information
     * stored in LDIF files
     */
    private void loadNormalizers( Schema schema, Registries registries ) throws Exception
    {
        String normalizersDirectory = getSchemaDirectory( schema )
            + "/" + SchemaConstants.NORMALIZERS_PATH;

        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + normalizersDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "normalizer LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();
                Normalizer normalizer =
                    factory.getNormalizer( entry.getEntry(), registries, schema.getSchemaName() );

                if ( schema.isEnabled() && normalizer.isEnabled() )
                {
                    normalizer.applyRegistries( registries );
                }

                registries.register( normalizer );
            }
        }
    }
    
    
    /**
     * Loads the MatchingRules from LDIF files in the supplied schema into the 
     * supplied registries.
     *
     * @param schema the schema for which matchingRules are loaded
     * @param registries the registries which are loaded with matchingRules
     * @throws Exception if there are failures accessing matchingRule 
     * information stored in LDIF files
     */
    private void loadMatchingRules( Schema schema, Registries registries ) throws Exception
    {
        String matchingRulesDirectory = getSchemaDirectory( schema )
            + "/" + SchemaConstants.MATCHING_RULES_PATH;
        
        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + matchingRulesDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "matchingRules LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();
                MatchingRule matchingRule = factory.getMatchingRule( 
                    entry.getEntry(), registries, schema.getSchemaName() );

                if ( matchingRule.isEnabled() )
                {
                    matchingRule.applyRegistries( registries );
                }
                
                registries.register( matchingRule );
            }
        }
    }
    
    
    /**
     * Loads the Syntaxes from LDIF files in the supplied schema into the 
     * supplied registries.
     *
     * @param schema the schema for which syntaxes are loaded
     * @param registries the registries which are loaded with syntaxes
     * @throws Exception if there are failures accessing comparator information
     * stored in LDIF files
     */
    private void loadSyntaxes( Schema schema, Registries registries ) throws Exception
    {
        String syntaxesDirectory = getSchemaDirectory( schema )
            + "/" + SchemaConstants.SYNTAXES_PATH;

        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + syntaxesDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "syntax LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();
                LdapSyntax syntax = factory.getSyntax( 
                    entry.getEntry(), registries, schema.getSchemaName() );

                if ( syntax.isEnabled() )
                {
                    syntax.applyRegistries( registries );
                }

                registries.register( syntax );
            }
        }
    }

    
    /**
     * Loads the AttributeTypes from LDIF files in the supplied schema into the 
     * supplied registries.
     *
     * @param schema the schema for which attributeTypes are loaded
     * @param registries the registries which are loaded with attributeTypes
     * @throws Exception if there are failures accessing attributeTypes 
     * information stored in LDIF files
     */
    private void loadAttributeTypes( Schema schema, Registries registries ) throws Exception
    {
    	/*
    	 * AT's can depend on another AT via the superior relationship.  
    	 * Because we separate each schema object into its own LDIF file and
    	 * the file system scan producing the listing of files used to order
    	 * the LDIF loads does not consider these dependencies we can have
    	 * a situation where the superior may not be loaded when a dependent
    	 * AT is loaded.
    	 * 
    	 * For this reason we must defer the loading of some LDIF entries 
    	 * until their superior AT is actually loaded.  This hash stores
    	 * LDIF entries keyed by the name of their superior AT.  When the
    	 * superior is loaded, the deferred entries depending on that superior
    	 * are loaded and the list of dependent entries are removed from this 
    	 * hash map.
    	 * 
    	 * NOTE: Because we don't have an OID and must use a potentially 
    	 * case varying String as the key in this map, we reduce the String to
    	 * it's lower cased cannonical form.
    	 */
    	Map<String,List<LdifEntry>> deferredEntries = new HashMap<String, List<LdifEntry>>();

    	// check that the attributeTypes directory exists for the schema
        String attributeTypesDirectory = getSchemaDirectory( schema )
            + "/" + SchemaConstants.ATTRIBUTES_TYPE_PATH;
        
        // get list of attributeType LDIF schema files in attributeTypes
        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + attributeTypesDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "attributeType LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();
                loadAttributeType( schema, deferredEntries, entry, registries );
            }
        }

        
        if ( ! deferredEntries.isEmpty() )
        {
        	for ( String missingSuperior : deferredEntries.keySet() )
        	{
        		if ( registries.getObjectClassRegistry().containsName( missingSuperior ) )
        		{
        			for ( LdifEntry entry : deferredEntries.get( missingSuperior ) )
        			{
        				if ( loadAttributeType( schema, deferredEntries, entry, registries ) )
        				{
        					LOG.error( "Still failed to load schema entry: {}", entry );
        				}
        			}
        		}
        	}
        }
    }
    
    
    /**
     * Recursive method that loads an AT, and other deferred ATs that depend
     * on it.  This is separated from and used by 
     * {@link #loadAttributeTypes(Schema, Registries)} to enter into this 
     * method.
     * 
     * If the AT being loaded has deferred entries waiting on it to be loaded,
     * then the AT is loaded then it's deferred entries are loaded by making a
     * recursive call to this method.  This begins the process a new making 
     * sure all deferred descendants in the AT hierarchy are load.
     * 
     * @param schema the schema we are loading
     * @param deferredEntries map of deferred AT LDIF entries
     * @param entry the AT LDIF entry to load
     * @param registries the registries the schema objects are loaded into
     * @return true if the AT is loaded, false otherwise
     * @throws Exception if there any failures looking up or registering
     */
    private boolean loadAttributeType( Schema schema, Map<String,List<LdifEntry>> deferredEntries, LdifEntry entry,
    		Registries registries ) throws Exception
    {
        // get superior name and if exists check if loaded, defer if not
        EntryAttribute superior = entry.getEntry().get( MetaSchemaConstants.M_SUP_ATTRIBUTE_TYPE_AT );
        
        if ( superior != null )
        {
        	String superiorName = superior.getString().toLowerCase();
        	
        	if ( ! registries.getAttributeTypeRegistry().containsName( superiorName ) )
        	{
        		List<LdifEntry> dependents = deferredEntries.get( superiorName );
        		
        		if ( dependents == null )
        		{
        			dependents = new ArrayList<LdifEntry>();
        			deferredEntries.put( superiorName, dependents );
        		}

        		dependents.add( entry );
        		return false;  // - return false if deferred, true when loaded
        	}
        }
        
        AttributeType attributeType = factory.getAttributeType( entry.getEntry(), registries, schema.getSchemaName() );
        
        if ( attributeType.isEnabled() )
        {
            attributeType.applyRegistries( registries );
        }
        
        registries.register( attributeType );

        // after registering AT check if any deferred entries depend on it
        if ( attributeType.getNames() != null )
        {
        	for ( String name : attributeType.getNames() )
        	{
        		if ( deferredEntries.containsKey( name.toLowerCase() ) )
        		{
        			List<LdifEntry> deferredList = deferredEntries.get( name.toLowerCase() );
        			List<LdifEntry> copiedList = new ArrayList<LdifEntry>( deferredList );
        			
        			for ( LdifEntry deferred : copiedList )
        			{
        				if ( loadAttributeType( schema, deferredEntries, deferred, registries ) )
        				{
        					deferredList.remove( deferred );
        				}
        			}
        			
        			if ( deferredList.isEmpty() )
        			{
        				deferredEntries.remove( name.toLowerCase() );
        			}
        		}
        	}
        }
        
        return true;
    }


    /**
     * Loads the MatchingRuleUses from LDIF files in the supplied schema into the 
     * supplied registries.
     *
     * @param schema the schema for which matchingRuleUses are loaded
     * @param registries the registries which are loaded with matchingRuleUses
     * @throws Exception if there are failures accessing matchingRuleUse 
     * information stored in LDIF files
     */
    private void loadMatchingRuleUses( Schema schema, Registries registries ) throws Exception
    {
        String matchingRuleUsesDirectory = getSchemaDirectory( schema )
            + "/" + SchemaConstants.MATCHING_RULE_USE_PATH;
        
        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + matchingRuleUsesDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "matchingRuleUse LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();
                MatchingRuleUse matchingRuleUse = null;
            
                // TODO add factory method to generate the matchingRuleUse
                if ( true )
                {
                    throw new NotImplementedException( "Need to implement factory " +
                    		"method for creating a matchingRuleUse" );
                }
                
                registries.register( matchingRuleUse );
            }
        }
    }


    /**
     * Loads the NameForms from LDIF files in the supplied schema into the 
     * supplied registries.
     *
     * @param schema the schema for which nameForms are loaded
     * @param registries the registries which are loaded with nameForms
     * @throws Exception if there are failures accessing nameForm information
     * stored in LDIF files
     */
    private void loadNameForms( Schema schema, Registries registries ) throws Exception
    {
        String nameFormsDirectory = getSchemaDirectory( schema ) + "/" + SchemaConstants.NAME_FORMS_PATH;

        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + nameFormsDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "nameForm LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();
                NameForm nameForm = null;

                // TODO add factory method to generate the nameForm
                if ( true )
                {
                    throw new NotImplementedException( "Need to implement factory " +
                            "method for creating a nameForm" );
                }
                
                registries.register( nameForm );
            }
        }
    }


    /**
     * Loads the DitContentRules from LDIF files in the supplied schema into the 
     * supplied registries.
     *
     * @param schema the schema for which ditContentRules are loaded
     * @param registries the registries which are loaded with ditContentRules
     * @throws Exception if there are failures accessing ditContentRules 
     * information stored in LDIF files
     */
    private void loadDitContentRules( Schema schema, Registries registries ) throws Exception
    {
        String ditContentRulesDirectory = getSchemaDirectory( schema ) + "/" + 
            SchemaConstants.DIT_CONTENT_RULES_PATH;

        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + ditContentRulesDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "ditContentRule LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();
                DITContentRule ditContentRule = null;
                
                // TODO add factory method to generate the ditContentRule
                if ( true )
                {
                    throw new NotImplementedException( "Need to implement factory " +
                            "method for creating a ditContentRule" );
                }
                
                registries.register( ditContentRule );
            }
        }
    }


    /**
     * Loads the ditStructureRules from LDIF files in the supplied schema into 
     * the supplied registries.
     *
     * @param schema the schema for which ditStructureRules are loaded
     * @param registries the registries which are loaded with ditStructureRules
     * @throws Exception if there are failures accessing ditStructureRule 
     * information stored in LDIF files
     */
    private void loadDitStructureRules( Schema schema, Registries registries ) throws Exception
    {
        String ditStructureRulesDirectory = getSchemaDirectory( schema )
            + "/" + SchemaConstants.DIT_STRUCTURE_RULES_PATH;

        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + ditStructureRulesDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "ditStructureRule LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
            
                LdifEntry entry = reader.next();
                DITStructureRule ditStructureRule = null;
            
                // TODO add factory method to generate the ditContentRule
                if ( true )
                {
                    throw new NotImplementedException( "Need to implement factory " +
                            "method for creating a ditStructureRule" );
                }
                
                registries.register( ditStructureRule );
            }
        }
    }


    /**
     * Loads the ObjectClasses from LDIF files in the supplied schema into the 
     * supplied registries.
     *
     * @param schema the schema for which objectClasses are loaded
     * @param registries the registries which are loaded with objectClasses
     * @throws Exception if there are failures accessing objectClass information
     * stored in LDIF files
     */
    private void loadObjectClasses( Schema schema, Registries registries ) throws Exception
    {
    	/*
    	 * OC's can depend on other OCs via their list of superior OCs.  
    	 * Because we separate each schema object into its own LDIF file and
    	 * the file system scan producing the listing of files used to order
    	 * the LDIF loads does not consider these dependencies we can have
    	 * a situation where the superiors may not be loaded when a dependent
    	 * OC is loaded.
    	 * 
    	 * For this reason we must defer the loading of some LDIF entries 
    	 * until their superior OCs are actually loaded.  This hash stores
    	 * LDIF entries keyed by the name of their superior OCs.  When a
    	 * superior is loaded, the deferred entries depending on that superior
    	 * are loaded and the list of dependent entries are removed from this 
    	 * hash map.
    	 * 
    	 * NOTE: Because we don't have an OID and must use a potentially 
    	 * case varying String as the key in this map, we reduce the String to
    	 * it's lower cased cannonical form.
    	 */
    	Map<String,List<LdifEntry>> deferredEntries = new HashMap<String, List<LdifEntry>>();

    	// get objectClasses directory, check if exists, return if not
    	String objectClassesDirectory = getSchemaDirectory( schema ) + "/" + SchemaConstants.OBJECT_CLASSES_PATH;

        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + objectClassesDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "objectClass LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();
                loadObjectClass( schema, deferredEntries, entry, registries );
            }
            
            if ( ! deferredEntries.isEmpty() )
            {
            	for ( String missingSuperior : deferredEntries.keySet() )
            	{
            		if ( registries.getObjectClassRegistry().containsName( missingSuperior ) )
            		{
            			for ( LdifEntry entry : deferredEntries.get( missingSuperior ) )
            			{
            				if ( loadObjectClass( schema, deferredEntries, entry, registries ) )
            				{
            					LOG.error( "Still failed to load schema entry: {}", entry );
            				}
            			}
            		}
            	}
            }
        }
    }
    
    
    /**
     * Recursive method that loads an OC, and other deferred OCs that may
     * depend on the initial OC loaded.  This is separated from and used by
     * {@link #loadObjectClasses(Schema, Registries)} to enter into this 
     * method.
     * 
     * If the OC being loaded has deferred entries waiting on it to be loaded,
     * then the OC is loaded then it's deferred entries are loaded by making a
     * recursive call to this method.  This begins the process a new making 
     * sure all deferred descendants in the OC hierarchy are load.
     * 
     * @param schema the schema we are loading
     * @param deferredEntries map of deferred OC LDIF entries
     * @param entry the OC LDIF entry to load
     * @param registries the registries the schema objects are loaded into
     * @return true if the OC is loaded, false otherwise
     * @throws Exception if there any failures looking up or registering
     */
    private boolean loadObjectClass( Schema schema, Map<String,List<LdifEntry>> deferredEntries, LdifEntry entry, 
    		Registries registries ) throws Exception
	{
        // get superior name and if exists check if loaded, defer if not
        EntryAttribute superiors = entry.getEntry().get( MetaSchemaConstants.M_SUP_OBJECT_CLASS_AT );
        
        if ( superiors != null )
        {
        	for ( Value<?> value : superiors )
        	{
        		String superiorName = value.getString().toLowerCase();
        		
            	if ( ! registries.getObjectClassRegistry().containsName( superiorName ) )
            	{
            		List<LdifEntry> dependents = deferredEntries.get( superiorName );
            		
            		if ( dependents == null )
            		{
            			dependents = new ArrayList<LdifEntry>();
            			deferredEntries.put( superiorName, dependents );
            		}

            		dependents.add( entry );
            		return false;  // - return false if deferred, true when loaded
            	}
        	}
        }
        
        ObjectClass objectClass = factory.getObjectClass( entry.getEntry(), registries, schema.getSchemaName() );

        if ( objectClass.isEnabled() )
        {
            objectClass.applyRegistries( registries );
        }

        registries.register( objectClass );

        // after registering AT check if any deferred entries depend on it
        if ( objectClass.getNames() != null )
        {
        	for ( String name : objectClass.getNames() )
        	{
        		if ( deferredEntries.containsKey( name.toLowerCase() ) )
        		{
        			List<LdifEntry> deferredList = deferredEntries.get( name.toLowerCase() );
        			List<LdifEntry> copiedList = new ArrayList<LdifEntry>( deferredList );
        			
        			for ( LdifEntry deferred : copiedList )
        			{
        				if ( loadObjectClass( schema, deferredEntries, deferred, registries ) )
        				{
        					deferredList.remove( deferred );
        				}
        			}
        			
        			if ( deferredList.isEmpty() )
        			{
        				deferredEntries.remove( name.toLowerCase() );
        			}
        		}
        	}
        }
        
        return true;
    }
}
