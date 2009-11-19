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
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.ldif.LdifEntry;
import org.apache.directory.shared.ldap.ldif.LdifReader;
import org.apache.directory.shared.ldap.schema.ldif.extractor.ResourceMap;
import org.apache.directory.shared.ldap.schema.ldif.extractor.SchemaLdifExtractor;
import org.apache.directory.shared.ldap.schema.registries.AbstractSchemaLoader;
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
            Pattern pat = Pattern.compile( ".*schema/ou=schema/cn=[a-z0-9-_]*\\." + LDIF_EXT );
            
            if ( pat.matcher( file ).matches() )
            {
                URL resource = getResource( file, "schema LDIF file" );
                InputStream in = resource.openStream();
                
                try
                {
                    LdifReader reader = new LdifReader( in );
                    LdifEntry entry = reader.next();
                    Schema schema = getSchema( entry.getEntry() );
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
                finally
                {
                    in.close();
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     *
    public List<Throwable> loadWithDependencies( Registries registries, boolean check, Schema... schemas ) throws Exception
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
     *
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
        loadComparators( schema );
        loadNormalizers( schema );
        loadSyntaxCheckers( schema );
        loadSyntaxes( schema );
        loadMatchingRules( schema );
        loadAttributeTypes( schema );
        loadObjectClasses( schema );
        loadMatchingRuleUses( schema );
        loadDitContentRules( schema );
        loadNameForms( schema );
        loadDitStructureRules( schema );

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
     * {@inheritDoc}
     */
    public List<Entry> loadComparators( Schema schema ) throws Exception
    {
        String comparatorsDirectory = getSchemaDirectory( schema ) 
            + "/" + SchemaConstants.COMPARATORS_PATH;
        
        List<Entry> comparatorList = new ArrayList<Entry>();
        
        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + comparatorsDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "comparator LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();

                comparatorList.add( entry.getEntry() );
            }
        }
        
        return comparatorList;
    }
    
    
    /**
     * Loads the SyntaxCheckers from LDIF files in the supplied schema into the 
     * supplied registries.
     *
     * @param schema the schema for which syntaxCheckers are loaded
     * @throws Exception if there are failures accessing syntaxChecker 
     * information stored in LDIF files
     */
    public List<Entry> loadSyntaxCheckers( Schema schema ) throws Exception
    {
        String syntaxCheckersDirectory = getSchemaDirectory( schema ) 
            +  "/" + SchemaConstants.SYNTAX_CHECKERS_PATH;

        List<Entry> syntaxCheckerList = new ArrayList<Entry>();

        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + syntaxCheckersDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "syntaxChecker LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();
                
                syntaxCheckerList.add( entry.getEntry() );
            }
        }
        
        return syntaxCheckerList;
    }
    
    
    /**
     * Loads the Normalizers from LDIF files in the supplied schema into the 
     * supplied registries.
     *
     * @param schema the schema for which normalizers are loaded
     * @throws Exception if there are failures accessing normalizer information
     * stored in LDIF files
     */
    public List<Entry> loadNormalizers( Schema schema ) throws Exception
    {
        String normalizersDirectory = getSchemaDirectory( schema )
            + "/" + SchemaConstants.NORMALIZERS_PATH;

        List<Entry> normalizerList = new ArrayList<Entry>();

        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + normalizersDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "normalizer LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();
                
                normalizerList.add( entry.getEntry() );
            }
        }
        
        return normalizerList;
    }
    
    
    /**
     * Loads the MatchingRules from LDIF files in the supplied schema into the 
     * supplied registries.
     *
     * @param schema the schema for which matchingRules are loaded
     * @throws Exception if there are failures accessing matchingRule 
     * information stored in LDIF files
     */
    public List<Entry> loadMatchingRules( Schema schema ) throws Exception
    {
        String matchingRulesDirectory = getSchemaDirectory( schema )
            + "/" + SchemaConstants.MATCHING_RULES_PATH;
        
        List<Entry> matchingRuleList = new ArrayList<Entry>();

        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + matchingRulesDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "matchingRules LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();

                matchingRuleList.add( entry.getEntry() );
            }
        }
        
        return matchingRuleList;
    }
    
    
    /**
     * Loads the Syntaxes from LDIF files in the supplied schema into the 
     * supplied registries.
     *
     * @param schema the schema for which syntaxes are loaded
     * @throws Exception if there are failures accessing comparator information
     * stored in LDIF files
     */
    public List<Entry> loadSyntaxes( Schema schema ) throws Exception
    {
        String syntaxesDirectory = getSchemaDirectory( schema )
            + "/" + SchemaConstants.SYNTAXES_PATH;

        List<Entry> syntaxList = new ArrayList<Entry>();

        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + syntaxesDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "syntax LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();
                
                syntaxList.add( entry.getEntry() );
            }
        }
        
        return syntaxList;
    }

    
    /**
     * Loads the AttributeTypes from LDIF files in the supplied schema into the 
     * supplied registries.
     *
     * @param schema the schema for which attributeTypes are loaded
     * @throws Exception if there are failures accessing attributeTypes 
     * information stored in LDIF files
     */
    public List<Entry> loadAttributeTypes( Schema schema ) throws Exception
    {
    	// check that the attributeTypes directory exists for the schema
        String attributeTypesDirectory = getSchemaDirectory( schema )
            + "/" + SchemaConstants.ATTRIBUTES_TYPE_PATH;
        
        List<Entry> attributeTypeList = new ArrayList<Entry>();

        // get list of attributeType LDIF schema files in attributeTypes
        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + attributeTypesDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "attributeType LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();

                attributeTypeList.add( entry.getEntry() );
            }
        }
        
        return attributeTypeList;
    }


    /**
     * Loads the MatchingRuleUses from LDIF files in the supplied schema into the 
     * supplied registries.
     *
     * @param schema the schema for which matchingRuleUses are loaded
     * @throws Exception if there are failures accessing matchingRuleUse 
     * information stored in LDIF files
     */
    public List<Entry> loadMatchingRuleUses( Schema schema ) throws Exception
    {
        String matchingRuleUsesDirectory = getSchemaDirectory( schema )
            + "/" + SchemaConstants.MATCHING_RULE_USE_PATH;
        
        List<Entry> matchingRuleUseList = new ArrayList<Entry>();

        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + matchingRuleUsesDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "matchingRuleUse LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();

                matchingRuleUseList.add( entry.getEntry() );
            }
        }
        
        return matchingRuleUseList;
    }


    /**
     * Loads the NameForms from LDIF files in the supplied schema into the 
     * supplied registries.
     *
     * @param schema the schema for which nameForms are loaded
     * @throws Exception if there are failures accessing nameForm information
     * stored in LDIF files
     */
    public List<Entry> loadNameForms( Schema schema ) throws Exception
    {
        String nameFormsDirectory = getSchemaDirectory( schema ) + "/" + SchemaConstants.NAME_FORMS_PATH;

        List<Entry> nameFormList = new ArrayList<Entry>();

        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + nameFormsDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "nameForm LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();

                nameFormList.add( entry.getEntry() );
            }
        }
        
        return nameFormList;
    }


    /**
     * Loads the DitContentRules from LDIF files in the supplied schema into the 
     * supplied registries.
     *
     * @param schema the schema for which ditContentRules are loaded
     * @throws Exception if there are failures accessing ditContentRules 
     * information stored in LDIF files
     */
    public List<Entry> loadDitContentRules( Schema schema ) throws Exception
    {
        String ditContentRulesDirectory = getSchemaDirectory( schema ) + "/" + 
            SchemaConstants.DIT_CONTENT_RULES_PATH;

        List<Entry> ditContentRulesList = new ArrayList<Entry>();

        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + ditContentRulesDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "ditContentRule LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();
                
                ditContentRulesList.add( entry.getEntry() );
            }
        }
        
        return ditContentRulesList;
    }


    /**
     * Loads the ditStructureRules from LDIF files in the supplied schema into 
     * the supplied registries.
     *
     * @param schema the schema for which ditStructureRules are loaded
     * @throws Exception if there are failures accessing ditStructureRule 
     * information stored in LDIF files
     */
    public List<Entry> loadDitStructureRules( Schema schema ) throws Exception
    {
        String ditStructureRulesDirectory = getSchemaDirectory( schema )
            + "/" + SchemaConstants.DIT_STRUCTURE_RULES_PATH;

        List<Entry> ditStructureRuleList = new ArrayList<Entry>();

        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + ditStructureRulesDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "ditStructureRule LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
            
                LdifEntry entry = reader.next();
                
                ditStructureRuleList.add( entry.getEntry() );
            }
        }
        
        return ditStructureRuleList;
    }


    /**
     * Loads the ObjectClasses from LDIF files in the supplied schema into the 
     * supplied registries.
     *
     * @param schema the schema for which objectClasses are loaded
     * @throws Exception if there are failures accessing objectClass information
     * stored in LDIF files
     */
    public List<Entry> loadObjectClasses( Schema schema ) throws Exception
    {
    	// get objectClasses directory, check if exists, return if not
    	String objectClassesDirectory = getSchemaDirectory( schema ) + "/" + SchemaConstants.OBJECT_CLASSES_PATH;

        List<Entry> objectClassList = new ArrayList<Entry>();

        for ( String resourcePath : RESOURCE_MAP.keySet() )
        {
            Pattern regex = Pattern.compile( ".*" + objectClassesDirectory + "/m-oid=.*\\." + LDIF_EXT );
            
            if ( regex.matcher( resourcePath ).matches() )
            {
                URL resource = getResource( resourcePath, "objectClass LDIF file" );
                LdifReader reader = new LdifReader( resource.openStream() );
                LdifEntry entry = reader.next();

                objectClassList.add( entry.getEntry() );
            }
        }
        
        return objectClassList;
    }
}
