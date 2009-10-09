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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.naming.NamingException;

import org.apache.directory.shared.ldap.NotImplementedException;
import org.apache.directory.shared.ldap.constants.MetaSchemaConstants;
import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.apache.directory.shared.ldap.entry.Value;
import org.apache.directory.shared.ldap.ldif.LdifEntry;
import org.apache.directory.shared.ldap.ldif.LdifReader;
import org.apache.directory.shared.ldap.ldif.LdifUtils;
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
import org.apache.directory.shared.ldap.schema.registries.AbstractSchemaLoader;
import org.apache.directory.shared.ldap.schema.registries.Registries;
import org.apache.directory.shared.ldap.schema.registries.Schema;
import org.apache.directory.shared.ldap.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Loads schema data from LDIF files containing entries representing schema
 * objects, using the meta schema format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Revision$
 */
public class LdifSchemaLoader extends AbstractSchemaLoader
{
    /** ldif file extension used */
    private static final String LDIF_EXT = "ldif";
    
    /** ou=schema LDIF file name */
    private static final String OU_SCHEMA_LDIF = "ou=schema." + LDIF_EXT;
    
    /** static class logger */
    private static final Logger LOG = LoggerFactory.getLogger( LdifSchemaLoader.class );

    /** Speedup for DEBUG mode */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /**
     * the administrator DN - very ADS specific but we need some DN here for
     * the modifiers name when the system modifies by itself enabled and 
     * disabled schemas in the repository.
     */
    private static final String ADMIN_SYSTEM_DN = "uid=admin,ou=system";

    /** the factory that generates respective SchemaObjects from LDIF entries */
    private final SchemaEntityFactory factory = new SchemaEntityFactory();
    
    /** directory containing the schema LDIF file for ou=schema */
    private final File baseDirectory;
    
    /** a filter for listing all the LDIF files within a directory */
    private final FilenameFilter ldifFilter = new FilenameFilter()
    {
        public boolean accept( File file, String name )
        {
            return name.endsWith( LDIF_EXT );
        }
    };


    /**
     * Creates a new LDIF based SchemaLoader. The constructor checks to make
     * sure the supplied base directory exists and contains a schema.ldif file
     * and if not complains about it.
     *
     * @param baseDirectory the schema LDIF base directory
     * @throws Exception if the base directory does not exist or does not
     * a valid schema.ldif file
     */
    public LdifSchemaLoader( File baseDirectory ) throws Exception
    {
        this.baseDirectory = baseDirectory;

        if ( ! baseDirectory.exists() )
        {
            String msg = "Provided baseDirectory '" +
                baseDirectory.getAbsolutePath() + "' does not exist.";
            LOG.error( msg );
            throw new IllegalArgumentException( msg );
        }

        File schemaLdif = new File( baseDirectory, OU_SCHEMA_LDIF );
        
        if ( ! schemaLdif.exists() )
        {
            String msg = "Expecting to find a schema.ldif file in provided baseDirectory path '" +
                schemaLdif.getAbsolutePath() + "' but no such file found.";
            LOG.error( msg );
            throw new FileNotFoundException( msg );
        }

        if ( IS_DEBUG )
        {
            LOG.debug( "Using '{}' as the base schema load directory.", baseDirectory );
        }
        
        initializeSchemas();
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
        
        File schemaDirectory = new File( baseDirectory, SchemaConstants.OU_SCHEMA );
        String[] ldifFiles = schemaDirectory.list( ldifFilter );

        for ( String ldifFile : ldifFiles )
        {
            try
            {
                LdifReader reader = new LdifReader( new File( schemaDirectory, ldifFile ) );
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
                LOG.error( "Failed to load schema LDIF file " + ldifFile, e );
                throw e;
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
        notLoaded.put( schema.getSchemaName(), schema );
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
        // disk then enable it on disk before we proceed to load it
        if ( schema.isDisabled() && isDepLoad )
        {
            enableSchema( schema );
        }
        
        if ( registries.isSchemaLoaded( schema.getSchemaName() ) )
        {
            LOG.info( "Will not attempt to load already loaded '{}' " +
            		"schema: \n{}", schema.getSchemaName(), schema );
            return;
        }
        
        LOG.info( "Loading {} schema: \n{}", schema.getSchemaName(), schema );
        
        registries.schemaLoaded( schema );
        
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

        notifyListenerOrRegistries( schema, registries );
    }

    
    /**
     * Utility method used to enable a specific schema on disk in the LDIF
     * based schema repository.  This method will remove the m-disabled AT
     * in the schema file and update the modifiersName and modifyTimestamp.
     * 
     * The modifiersName and modifyTimestamp on the schema.ldif file will
     * also be updated to indicate a change to the schema.
     *
     * @param schema the disabled schema to enable
     * @throws Exception if there are problems writing changes back to disk
     */
    private void enableSchema( Schema schema ) throws Exception
    {
        // -------------------------------------------------------------------
        // Modifying the foo schema foo.ldif file to be enabled but still
        // have to now update the timestamps and update the modifiersName
        // -------------------------------------------------------------------
        
        File schemaLdifFile = new File( new File( baseDirectory, SchemaConstants.OU_SCHEMA ), 
            "cn=" + schema.getSchemaName() + "." + LDIF_EXT );
        LdifReader reader = new LdifReader( schemaLdifFile );
        LdifEntry ldifEntry = reader.next();
        Entry entry = ldifEntry.getEntry();
        
        entry.removeAttributes( "changeType" );
        entry.removeAttributes( SchemaConstants.MODIFIERS_NAME_AT );
        entry.removeAttributes( SchemaConstants.MODIFY_TIMESTAMP_AT );
        entry.removeAttributes( MetaSchemaConstants.M_DISABLED_AT );
        
        entry.add( SchemaConstants.MODIFIERS_NAME_AT, ADMIN_SYSTEM_DN );
        entry.add( SchemaConstants.MODIFY_TIMESTAMP_AT, 
            DateUtils.getGeneralizedTime() );
        
        FileWriter out = new FileWriter( schemaLdifFile );
        out.write( LdifUtils.convertEntryToLdif( entry ) );
        out.flush();
        out.close();
        
        // -------------------------------------------------------------------
        // Now we need to update the timestamp on the schema.ldif file which
        // shows that something changed below the schema directory in schema
        // -------------------------------------------------------------------
        
        schemaLdifFile = new File( baseDirectory, "ou=schema." + LDIF_EXT );
        reader = new LdifReader( schemaLdifFile );
        ldifEntry = reader.next();
        entry = ldifEntry.getEntry();
        
        entry.removeAttributes( "changeType" );
        entry.removeAttributes( SchemaConstants.MODIFIERS_NAME_AT );
        entry.removeAttributes( SchemaConstants.MODIFY_TIMESTAMP_AT );

        entry.add( SchemaConstants.MODIFIERS_NAME_AT, ADMIN_SYSTEM_DN );
        entry.add( SchemaConstants.MODIFY_TIMESTAMP_AT, 
            DateUtils.getGeneralizedTime() );
        
        out = new FileWriter( schemaLdifFile );
        out.write( LdifUtils.convertEntryToLdif( entry ) );
        out.flush();
        out.close();
    }


    /**
     * Utility method to get the file for a schema directory.
     *
     * @param schema the schema to get the file for
     * @return the file for the specific schema directory
     */
    private final File getSchemaDirectory( Schema schema )
    {
        return new File( new File( baseDirectory, SchemaConstants.OU_SCHEMA ), 
            "cn=" + schema.getSchemaName() );
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
        File comparatorsDirectory = new File( getSchemaDirectory( schema ), 
            SchemaConstants.COMPARATORS_PATH );
        
        if ( ! comparatorsDirectory.exists() )
        {
            return;
        }
        
        File[] comparators = comparatorsDirectory.listFiles( ldifFilter );
        
        for ( File ldifFile : comparators )
        {
            LdifReader reader = new LdifReader( ldifFile );
            LdifEntry entry = reader.next();
            LdapComparator<?> comparator = 
                factory.getLdapComparator( entry.getEntry(), registries, schema.getSchemaName() );
            comparator.setOid( entry.get( MetaSchemaConstants.M_OID_AT ).getString() );

            if ( schema.isEnabled() && comparator.isEnabled() )
            {
                comparator.applyRegistries( registries );
            }

            registries.getComparatorRegistry().register( comparator );
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
        File syntaxCheckersDirectory = new File( getSchemaDirectory( schema ), 
            SchemaConstants.SYNTAX_CHECKERS_PATH );
        
        if ( ! syntaxCheckersDirectory.exists() )
        {
            return;
        }
        
        File[] syntaxCheckerFiles = syntaxCheckersDirectory.listFiles( ldifFilter );
        
        for ( File ldifFile : syntaxCheckerFiles )
        {
            LdifReader reader = new LdifReader( ldifFile );
            LdifEntry entry = reader.next();
            SyntaxChecker syntaxChecker = 
                factory.getSyntaxChecker( entry.getEntry(), registries, schema.getSchemaName() );
            try
            {
            	registries.getSyntaxCheckerRegistry().register( syntaxChecker );
            }
            catch ( NamingException e )
            {
            	// Do nothing at this point. Just log the event
                LOG.warn( e.getMessage() );
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
        File normalizersDirectory = new File( getSchemaDirectory( schema ), 
            SchemaConstants.NORMALIZERS_PATH );
        
        if ( ! normalizersDirectory.exists() )
        {
            return;
        }
        
        File[] normalizerFiles = normalizersDirectory.listFiles( ldifFilter );
        
        for ( File ldifFile : normalizerFiles )
        {
            LdifReader reader = new LdifReader( ldifFile );
            LdifEntry entry = reader.next();
            Normalizer normalizer =
                factory.getNormalizer( entry.getEntry(), registries, schema.getSchemaName() );
            
            if ( schema.isEnabled() && normalizer.isEnabled() )
            {
                normalizer.applyRegistries( registries );
            }
            
            registries.getNormalizerRegistry().register( normalizer );
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
        File matchingRulesDirectory = new File( getSchemaDirectory( schema ), 
            SchemaConstants.MATCHING_RULES_PATH );
        
        if ( ! matchingRulesDirectory.exists() )
        {
            return;
        }
        
        File[] matchingRuleFiles = matchingRulesDirectory.listFiles( ldifFilter );
        
        for ( File ldifFile : matchingRuleFiles )
        {
            LdifReader reader = new LdifReader( ldifFile );
            LdifEntry entry = reader.next();
            MatchingRule matchingRule = factory.getMatchingRule( 
                entry.getEntry(), registries, schema.getSchemaName() );

            if ( schema.isEnabled() && matchingRule.isEnabled() )
            {
                matchingRule.applyRegistries( registries );
            }

            registries.getMatchingRuleRegistry().register( matchingRule );
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
        File syntaxesDirectory = new File( getSchemaDirectory( schema ), 
            SchemaConstants.SYNTAXES_PATH );
        
        if ( ! syntaxesDirectory.exists() )
        {
            return;
        }
        
        File[] syntaxFiles = syntaxesDirectory.listFiles( ldifFilter );
        
        for ( File ldifFile : syntaxFiles )
        {
            LdifReader reader = new LdifReader( ldifFile );
            LdifEntry entry = reader.next();
            LdapSyntax syntax = factory.getSyntax( 
                entry.getEntry(), registries, schema.getSchemaName() );

            if ( schema.isEnabled() && syntax.isEnabled() )
            {
                syntax.applyRegistries( registries );
            }

            registries.getLdapSyntaxRegistry().register( syntax );
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
        File attributeTypesDirectory = new File ( getSchemaDirectory( schema ), SchemaConstants.ATTRIBUTES_TYPE_PATH );
        
        if ( ! attributeTypesDirectory.exists() )
        {
            return;
        }
        
        // get list of attributeType LDIF schema files in attributeTypes
        File[] attributeTypeFiles = attributeTypesDirectory.listFiles( ldifFilter );
        
        for ( File ldifFile : attributeTypeFiles )
        {
            LdifReader reader = new LdifReader( ldifFile );
            LdifEntry entry = reader.next();
            loadAttributeType( schema, deferredEntries, entry, registries );
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
        registries.getAttributeTypeRegistry().register( attributeType );
        
        if ( schema.isEnabled() && attributeType.isEnabled() )
        {
            attributeType.applyRegistries( registries );
        }

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
        File matchingRuleUsesDirectory = new File( getSchemaDirectory( schema ),
            SchemaConstants.MATCHING_RULE_USE_PATH );
        
        if ( ! matchingRuleUsesDirectory.exists() )
        {
            return;
        }
        
        File[] matchingRuleUseFiles = matchingRuleUsesDirectory.listFiles( ldifFilter );
        
        for ( File ldifFile : matchingRuleUseFiles )
        {
            LdifReader reader = new LdifReader( ldifFile );
            LdifEntry entry = reader.next();
            MatchingRuleUse matchingRuleUse = null;
            
            // TODO add factory method to generate the matchingRuleUse
            if ( true )
            {
                throw new NotImplementedException( "Need to implement factory " +
                		"method for creating a matchingRuleUse" );
            }
            
            registries.getMatchingRuleUseRegistry().register( matchingRuleUse );
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
        File nameFormsDirectory = new File( getSchemaDirectory( schema ),
            SchemaConstants.NAME_FORMS_PATH );
        
        if ( ! nameFormsDirectory.exists() )
        {
            return;
        }
        
        File[] nameFormFiles = nameFormsDirectory.listFiles( ldifFilter );
        
        for ( File ldifFile : nameFormFiles )
        {
            LdifReader reader = new LdifReader( ldifFile );
            LdifEntry entry = reader.next();
            NameForm nameForm = null;

            // TODO add factory method to generate the nameForm
            if ( true )
            {
                throw new NotImplementedException( "Need to implement factory " +
                        "method for creating a nameForm" );
            }
            
            registries.getNameFormRegistry().register( nameForm );
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
        File ditContentRulesDirectory = new File( getSchemaDirectory( schema ),
            SchemaConstants.DIT_CONTENT_RULES_PATH );
        
        if ( ! ditContentRulesDirectory.exists() )
        {
            return;
        }
        
        File[] ditContentRuleFiles = ditContentRulesDirectory.listFiles( ldifFilter );
        
        for ( File ldifFile : ditContentRuleFiles )
        {
            LdifReader reader = new LdifReader( ldifFile );
            LdifEntry entry = reader.next();
            DITContentRule ditContentRule = null;
            
            // TODO add factory method to generate the ditContentRule
            if ( true )
            {
                throw new NotImplementedException( "Need to implement factory " +
                        "method for creating a ditContentRule" );
            }
            
            registries.getDitContentRuleRegistry().register( ditContentRule );
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
        File ditStructureRulesDirectory = new File( getSchemaDirectory( schema ),
            SchemaConstants.DIT_STRUCTURE_RULES_PATH );
        
        if ( ! ditStructureRulesDirectory.exists() )
        {
            return;
        }
        
        File[] ditStructureRuleFiles = ditStructureRulesDirectory.listFiles( ldifFilter );
        
        for ( File ldifFile : ditStructureRuleFiles )
        {
            LdifReader reader = new LdifReader( ldifFile );
            LdifEntry entry = reader.next();
            DITStructureRule ditStructureRule = null;
            
            // TODO add factory method to generate the ditContentRule
            if ( true )
            {
                throw new NotImplementedException( "Need to implement factory " +
                        "method for creating a ditStructureRule" );
            }
            
            registries.getDitStructureRuleRegistry().register( ditStructureRule );
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
    	File objectClassesDirectory = new File( getSchemaDirectory( schema ), SchemaConstants.OBJECT_CLASSES_PATH );
        
    	if ( ! objectClassesDirectory.exists() )
        {
            return;
        }
        
        // get list of objectClass LDIF files from directory and load
        File[] objectClassFiles = objectClassesDirectory.listFiles( ldifFilter );
       
        for ( File ldifFile : objectClassFiles )
        {
            LdifReader reader = new LdifReader( ldifFile );
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

        if ( schema.isEnabled() && objectClass.isEnabled() )
        {
            objectClass.applyRegistries( registries );
        }

        registries.getObjectClassRegistry().register( objectClass );

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
