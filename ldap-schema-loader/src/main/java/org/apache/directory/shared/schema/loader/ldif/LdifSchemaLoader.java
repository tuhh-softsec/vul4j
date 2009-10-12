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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.directory.shared.ldap.constants.MetaSchemaConstants;
import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.ldif.LdifEntry;
import org.apache.directory.shared.ldap.ldif.LdifReader;
import org.apache.directory.shared.ldap.ldif.LdifUtils;
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
 * This class is used only for tests.
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
        super( new SchemaEntityFactory() );

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
    public List<Throwable> loadWithDependencies( Schema schema, Registries registries, boolean check ) throws Exception
    {
        // Relax the controls at first
        List<Throwable> errors = new ArrayList<Throwable>();
        boolean wasRelaxed = registries.isRelaxed();
        registries.setRelaxed( true );

        Stack<String> beenthere = new Stack<String>();
        Map<String,Schema> notLoaded = new HashMap<String,Schema>();
        notLoaded.put( schema.getSchemaName(), schema );
        super.loadDepsFirst( schema, beenthere, notLoaded, schema, registries );
        
        // At the end, check the registries if required
        if ( check )
        {
            errors = registries.checkRefInteg();
        }
        
        // Restore the Registries isRelaxed flag
        registries.setRelaxed( wasRelaxed );
        
        return errors;
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
            
            registerComparator( registries, entry, schema );
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
            
            registerSyntaxChecker( registries, entry, schema );
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
            
            registerNormalizer( registries, entry, schema );
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

            registerMatchingRule( registries, entry, schema );
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

            registerSyntax( registries, entry, schema );
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
            
            registerAttributeType( registries, entry, schema );
        }
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
            
            registerMatchingRuleUse( registries, entry, schema );
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
            
            registerNameForm( registries, entry, schema );
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
            
            registerDitContentRule( registries, entry, schema );
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
            
            registerDitStructureRule( registries, entry, schema );
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
            
            registerObjectClass( registries, entry, schema );
        }
    }
}
