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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.ldif.LdifEntry;
import org.apache.directory.shared.ldap.ldif.LdifReader;
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
 * This class is used only for tests.
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
        super( new SchemaEntityFactory() );
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
    public List<Throwable> loadWithDependencies( Schema schema, Registries registries, boolean check ) throws Exception
    {
        // Relax the controls at first
        List<Throwable> errors = new ArrayList<Throwable>();
        boolean wasRelaxed = registries.isRelaxed();
        registries.setRelaxed( true );

        Stack<String> beenthere = new Stack<String>();
        Map<String,Schema> notLoaded = new HashMap<String,Schema>();
        
        for ( Schema candidate : schemaMap.values() )
        {
            if ( ! registries.isSchemaLoaded( candidate.getSchemaName() ) )
            {
                notLoaded.put( candidate.getSchemaName(), candidate );
            }
        }
        
        loadDepsFirst( schema, beenthere, notLoaded, schema, registries );
        
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
        
        // We set the registries to Permissive, so that we don't care about the order
        // the SchemaObjects are loaded.
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

                registerComparator( registries, entry, schema );
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
                
                registerSyntaxChecker( registries, entry, schema );
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
                
                registerNormalizer( registries, entry, schema );
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

                registerMatchingRule( registries, entry, schema );
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
                
                registerSyntax( registries, entry, schema );
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

                registerAttributeType( registries, entry, schema );
            }
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

                registerMatchingRuleUse( registries, entry, schema );
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

                registerNameForm( registries, entry, schema );
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
                
                registerDitContentRule( registries, entry, schema );
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
                
                registerDitStructureRule( registries, entry, schema );
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

                registerObjectClass( registries, entry, schema );
            }
        }
    }
}
