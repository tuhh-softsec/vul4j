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
package org.apache.directory.shared.ldap.schema.loader.ldif;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.ldif.LdifEntry;
import org.apache.directory.shared.ldap.ldif.LdifReader;
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

        if ( !baseDirectory.exists() )
        {
            String msg = "Provided baseDirectory '" + baseDirectory.getAbsolutePath() + "' does not exist.";
            LOG.error( msg );
            throw new IllegalArgumentException( msg );
        }

        File schemaLdif = new File( baseDirectory, OU_SCHEMA_LDIF );

        if ( !schemaLdif.exists() )
        {
            String msg = I18n.err( I18n.ERR_10004, schemaLdif.getAbsolutePath() );
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
            File file = new File( schemaDirectory, ldifFile );

            try
            {
                LdifReader reader = new LdifReader( file );
                LdifEntry entry = reader.next();
                reader.close();
                Schema schema = getSchema( entry.getEntry() );

                if ( schema == null )
                {
                    // The entry was not a schema, skip it
                    continue;
                }

                schemaMap.put( schema.getSchemaName(), schema );

                if ( IS_DEBUG )
                {
                    LOG.debug( "Schema Initialized ... \n{}", schema );
                }
            }
            catch ( Exception e )
            {
                LOG.error( I18n.err( I18n.ERR_10003, ldifFile ), e );
                throw e;
            }
        }
    }


    /**
     * {@inheritDoc}
     *
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
     *
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
        
        loadComparators( schema );
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
     * Utility method to get the file for a schema directory.
     *
     * @param schema the schema to get the file for
     * @return the file for the specific schema directory
     */
    private final File getSchemaDirectory( Schema schema )
    {
        return new File( new File( baseDirectory, SchemaConstants.OU_SCHEMA ), "cn=" + schema.getSchemaName() );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadComparators( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> comparatorList = new ArrayList<Entry>();

        if ( schemas == null )
        {
            return comparatorList;
        }

        for ( Schema schema : schemas )
        {
            File comparatorsDirectory = new File( getSchemaDirectory( schema ), SchemaConstants.COMPARATORS_PATH );

            if ( !comparatorsDirectory.exists() )
            {
                return comparatorList;
            }

            File[] comparators = comparatorsDirectory.listFiles( ldifFilter );

            for ( File ldifFile : comparators )
            {
                LdifReader reader = new LdifReader( ldifFile );
                LdifEntry entry = reader.next();
                reader.close();

                comparatorList.add( entry.getEntry() );
            }
        }

        return comparatorList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadSyntaxCheckers( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> syntaxCheckerList = new ArrayList<Entry>();

        if ( schemas == null )
        {
            return syntaxCheckerList;
        }

        for ( Schema schema : schemas )
        {
            File syntaxCheckersDirectory = new File( getSchemaDirectory( schema ), SchemaConstants.SYNTAX_CHECKERS_PATH );

            if ( !syntaxCheckersDirectory.exists() )
            {
                return syntaxCheckerList;
            }

            File[] syntaxCheckerFiles = syntaxCheckersDirectory.listFiles( ldifFilter );

            for ( File ldifFile : syntaxCheckerFiles )
            {
                LdifReader reader = new LdifReader( ldifFile );
                LdifEntry entry = reader.next();
                reader.close();

                syntaxCheckerList.add( entry.getEntry() );
            }
        }

        return syntaxCheckerList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadNormalizers( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> normalizerList = new ArrayList<Entry>();

        if ( schemas == null )
        {
            return normalizerList;
        }

        for ( Schema schema : schemas )
        {
            File normalizersDirectory = new File( getSchemaDirectory( schema ), SchemaConstants.NORMALIZERS_PATH );

            if ( !normalizersDirectory.exists() )
            {
                return normalizerList;
            }

            File[] normalizerFiles = normalizersDirectory.listFiles( ldifFilter );

            for ( File ldifFile : normalizerFiles )
            {
                LdifReader reader = new LdifReader( ldifFile );
                LdifEntry entry = reader.next();
                reader.close();

                normalizerList.add( entry.getEntry() );
            }
        }

        return normalizerList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadMatchingRules( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> matchingRuleList = new ArrayList<Entry>();

        if ( schemas == null )
        {
            return matchingRuleList;
        }

        for ( Schema schema : schemas )
        {
            File matchingRulesDirectory = new File( getSchemaDirectory( schema ), SchemaConstants.MATCHING_RULES_PATH );

            if ( !matchingRulesDirectory.exists() )
            {
                return matchingRuleList;
            }

            File[] matchingRuleFiles = matchingRulesDirectory.listFiles( ldifFilter );

            for ( File ldifFile : matchingRuleFiles )
            {
                LdifReader reader = new LdifReader( ldifFile );
                LdifEntry entry = reader.next();
                reader.close();

                matchingRuleList.add( entry.getEntry() );
            }
        }

        return matchingRuleList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadSyntaxes( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> syntaxList = new ArrayList<Entry>();

        if ( schemas == null )
        {
            return syntaxList;
        }

        for ( Schema schema : schemas )
        {
            File syntaxesDirectory = new File( getSchemaDirectory( schema ), SchemaConstants.SYNTAXES_PATH );

            if ( !syntaxesDirectory.exists() )
            {
                return syntaxList;
            }

            File[] syntaxFiles = syntaxesDirectory.listFiles( ldifFilter );

            for ( File ldifFile : syntaxFiles )
            {
                LdifReader reader = new LdifReader( ldifFile );
                LdifEntry entry = reader.next();
                reader.close();

                syntaxList.add( entry.getEntry() );
            }
        }

        return syntaxList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadAttributeTypes( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> attributeTypeList = new ArrayList<Entry>();

        if ( schemas == null )
        {
            return attributeTypeList;
        }

        for ( Schema schema : schemas )
        {
            // check that the attributeTypes directory exists for the schema
            File attributeTypesDirectory = new File( getSchemaDirectory( schema ), SchemaConstants.ATTRIBUTES_TYPE_PATH );

            if ( !attributeTypesDirectory.exists() )
            {
                return attributeTypeList;
            }

            // get list of attributeType LDIF schema files in attributeTypes
            File[] attributeTypeFiles = attributeTypesDirectory.listFiles( ldifFilter );

            for ( File ldifFile : attributeTypeFiles )
            {
                LdifReader reader = new LdifReader( ldifFile );
                LdifEntry entry = reader.next();
                reader.close();

                attributeTypeList.add( entry.getEntry() );
            }
        }

        return attributeTypeList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadMatchingRuleUses( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> matchingRuleUseList = new ArrayList<Entry>();

        if ( schemas == null )
        {
            return matchingRuleUseList;
        }

        for ( Schema schema : schemas )
        {
            File matchingRuleUsesDirectory = new File( getSchemaDirectory( schema ),
                SchemaConstants.MATCHING_RULE_USE_PATH );

            if ( !matchingRuleUsesDirectory.exists() )
            {
                return matchingRuleUseList;
            }

            File[] matchingRuleUseFiles = matchingRuleUsesDirectory.listFiles( ldifFilter );

            for ( File ldifFile : matchingRuleUseFiles )
            {
                LdifReader reader = new LdifReader( ldifFile );
                LdifEntry entry = reader.next();
                reader.close();

                matchingRuleUseList.add( entry.getEntry() );
            }
        }

        return matchingRuleUseList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadNameForms( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> nameFormList = new ArrayList<Entry>();

        if ( schemas == null )
        {
            return nameFormList;
        }

        for ( Schema schema : schemas )
        {
            File nameFormsDirectory = new File( getSchemaDirectory( schema ), SchemaConstants.NAME_FORMS_PATH );

            if ( !nameFormsDirectory.exists() )
            {
                return nameFormList;
            }

            File[] nameFormFiles = nameFormsDirectory.listFiles( ldifFilter );

            for ( File ldifFile : nameFormFiles )
            {
                LdifReader reader = new LdifReader( ldifFile );
                LdifEntry entry = reader.next();
                reader.close();

                nameFormList.add( entry.getEntry() );
            }
        }

        return nameFormList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadDitContentRules( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> ditContentRuleList = new ArrayList<Entry>();

        if ( schemas == null )
        {
            return ditContentRuleList;
        }

        for ( Schema schema : schemas )
        {
            File ditContentRulesDirectory = new File( getSchemaDirectory( schema ),
                SchemaConstants.DIT_CONTENT_RULES_PATH );

            if ( !ditContentRulesDirectory.exists() )
            {
                return ditContentRuleList;
            }

            File[] ditContentRuleFiles = ditContentRulesDirectory.listFiles( ldifFilter );

            for ( File ldifFile : ditContentRuleFiles )
            {
                LdifReader reader = new LdifReader( ldifFile );
                LdifEntry entry = reader.next();
                reader.close();

                ditContentRuleList.add( entry.getEntry() );
            }
        }

        return ditContentRuleList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadDitStructureRules( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> ditStructureRuleList = new ArrayList<Entry>();

        if ( schemas == null )
        {
            return ditStructureRuleList;
        }

        for ( Schema schema : schemas )
        {
            File ditStructureRulesDirectory = new File( getSchemaDirectory( schema ),
                SchemaConstants.DIT_STRUCTURE_RULES_PATH );

            if ( !ditStructureRulesDirectory.exists() )
            {
                return ditStructureRuleList;
            }

            File[] ditStructureRuleFiles = ditStructureRulesDirectory.listFiles( ldifFilter );

            for ( File ldifFile : ditStructureRuleFiles )
            {
                LdifReader reader = new LdifReader( ldifFile );
                LdifEntry entry = reader.next();
                reader.close();

                ditStructureRuleList.add( entry.getEntry() );
            }
        }

        return ditStructureRuleList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadObjectClasses( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> objectClassList = new ArrayList<Entry>();

        if ( schemas == null )
        {
            return objectClassList;
        }

        for ( Schema schema : schemas )
        {
            // get objectClasses directory, check if exists, return if not
            File objectClassesDirectory = new File( getSchemaDirectory( schema ), SchemaConstants.OBJECT_CLASSES_PATH );

            if ( !objectClassesDirectory.exists() )
            {
                return objectClassList;
            }

            // get list of objectClass LDIF files from directory and load
            File[] objectClassFiles = objectClassesDirectory.listFiles( ldifFilter );

            for ( File ldifFile : objectClassFiles )
            {
                LdifReader reader = new LdifReader( ldifFile );
                LdifEntry entry = reader.next();
                reader.close();

                objectClassList.add( entry.getEntry() );
            }
        }

        return objectClassList;
    }
}
