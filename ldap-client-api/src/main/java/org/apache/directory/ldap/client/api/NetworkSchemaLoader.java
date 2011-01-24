/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.apache.directory.ldap.client.api;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.ldap.model.cursor.Cursor;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.filter.SearchScope;
import org.apache.directory.shared.ldap.model.message.Response;
import org.apache.directory.shared.ldap.model.message.SearchResultEntry;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.schema.registries.Schema;
import org.apache.directory.shared.ldap.model.schema.registries.AbstractSchemaLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A schema loader which uses LdapConnection to load schema.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NetworkSchemaLoader extends AbstractSchemaLoader
{
    /** the connection to the ldap server */
    private LdapConnection connection;

    /** the schema base Dn */
    private static final String SCHEMA_BASE = "ou=schema";

    /** the search filter */
    private static final String FILTER = "(objectClass=*)";

    /** the logger */
    private static final Logger LOG = LoggerFactory.getLogger( NetworkSchemaLoader.class );


    /**
     * Creates a new instance of NetworkSchemaLoader.
     *
     * @param connection the LDAP connection
     * @throws Exception if the connection is not authenticated or if there are any problems
     *                   while loading the schema entries
     */
    public NetworkSchemaLoader( LdapConnection connection ) throws Exception
    {
        if ( !connection.isAuthenticated() )
        {
            throw new IllegalArgumentException( "connection is not authenticated" );
        }

        this.connection = connection;
        initializeSchemas();
    }


    private void initializeSchemas() throws Exception
    {
        List<Entry> schemaEntries = searchSchemaObjects( SCHEMA_BASE, "(objectClass=metaSchema)" );

        LOG.debug( "initializing schemas {}", schemaEntries );
        for ( Entry entry : schemaEntries )
        {
            Schema schema = getSchema( entry );
            schemaMap.put( schema.getSchemaName(), schema );
        }
    }


    /**
     * searches with ONE LEVEL scope under the given Dn and retrieves all the schema objects
     * 
     * @param baseDn the Dn of the schema entry under which the schema objects are present
     *               e.x ou=attributeTypes,cn=apache,ou=schema
     * @param filter optional search filter, if null the default fileter {@link #FILTER} is used
     * @return a list of entries of the schema objects 
     * @throws LdapException
     */
    private List<Entry> searchSchemaObjects( String baseDn, String filter ) throws LdapException
    {
        try
        {
            LOG.debug( "searching under the dn {} for schema objects", baseDn );

            List<Entry> entries = new ArrayList<Entry>();

            if ( filter == null )
            {
                filter = FILTER;
            }

            Cursor<Response> cursor = connection.search( new Dn( baseDn ), filter, SearchScope.ONELEVEL, "*", "+" );

            while ( cursor.next() )
            {
                Entry entry = ( ( SearchResultEntry ) cursor.get() ).getEntry();
                entries.add( entry );
            }

            cursor.close();

            return entries;
        }
        catch ( LdapException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            throw new LdapException( e );
        }
    }


    /**
     * @see #searchSchemaObjects(String, String)
     */
    private List<Entry> searchSchemaObjects( String baseDn ) throws LdapException
    {
        return searchSchemaObjects( baseDn, FILTER );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadAttributeTypes( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> atEntries = new ArrayList<Entry>();

        for ( Schema s : schemas )
        {
            List<Entry> entries = searchSchemaObjects( "ou=attributeTypes,cn=" + s.getSchemaName() + "," + SCHEMA_BASE );
            atEntries.addAll( entries );
        }

        return atEntries;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadComparators( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> comparatorsEntries = new ArrayList<Entry>();

        for ( Schema s : schemas )
        {
            List<Entry> entries = searchSchemaObjects( "ou=comparators,cn=" + s.getSchemaName() + "," + SCHEMA_BASE );
            comparatorsEntries.addAll( entries );
        }

        return comparatorsEntries;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadDitContentRules( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> ditContentRulesEntries = new ArrayList<Entry>();

        for ( Schema s : schemas )
        {
            List<Entry> entries = searchSchemaObjects( "ou=ditContentRules,cn=" + s.getSchemaName() + "," + SCHEMA_BASE );
            ditContentRulesEntries.addAll( entries );
        }

        return ditContentRulesEntries;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadDitStructureRules( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> ditStructureRulesEntries = new ArrayList<Entry>();

        for ( Schema s : schemas )
        {
            List<Entry> entries = searchSchemaObjects( "ou=ditStructureRules,cn=" + s.getSchemaName() + ","
                + SCHEMA_BASE );
            ditStructureRulesEntries.addAll( entries );
        }

        return ditStructureRulesEntries;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadMatchingRuleUses( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> matchingRuleUsesEntries = new ArrayList<Entry>();

        for ( Schema s : schemas )
        {
            List<Entry> entries = searchSchemaObjects( "ou=matchingRuleUse,cn=" + s.getSchemaName() + "," + SCHEMA_BASE );
            matchingRuleUsesEntries.addAll( entries );
        }

        return matchingRuleUsesEntries;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadMatchingRules( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> matchingRulesEntries = new ArrayList<Entry>();

        for ( Schema s : schemas )
        {
            List<Entry> entries = searchSchemaObjects( "ou=matchingRules,cn=" + s.getSchemaName() + "," + SCHEMA_BASE );
            matchingRulesEntries.addAll( entries );
        }

        return matchingRulesEntries;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadNameForms( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> nameFormsEntries = new ArrayList<Entry>();

        for ( Schema s : schemas )
        {
            List<Entry> entries = searchSchemaObjects( "ou=nameForms,cn=" + s.getSchemaName() + "," + SCHEMA_BASE );
            nameFormsEntries.addAll( entries );
        }

        return nameFormsEntries;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadNormalizers( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> normalizersEntries = new ArrayList<Entry>();

        for ( Schema s : schemas )
        {
            List<Entry> entries = searchSchemaObjects( "ou=normalizers,cn=" + s.getSchemaName() + "," + SCHEMA_BASE );
            normalizersEntries.addAll( entries );
        }

        return normalizersEntries;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadObjectClasses( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> objectClassesEntries = new ArrayList<Entry>();

        for ( Schema s : schemas )
        {
            List<Entry> entries = searchSchemaObjects( "ou=objectClasses,cn=" + s.getSchemaName() + "," + SCHEMA_BASE );
            objectClassesEntries.addAll( entries );
        }

        return objectClassesEntries;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadSyntaxCheckers( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> syntaxCheckersEntries = new ArrayList<Entry>();

        for ( Schema s : schemas )
        {
            List<Entry> entries = searchSchemaObjects( "ou=syntaxCheckers,cn=" + s.getSchemaName() + "," + SCHEMA_BASE );
            syntaxCheckersEntries.addAll( entries );
        }

        return syntaxCheckersEntries;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadSyntaxes( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> syntaxesEntries = new ArrayList<Entry>();

        for ( Schema s : schemas )
        {
            List<Entry> entries = searchSchemaObjects( "ou=syntaxes,cn=" + s.getSchemaName() + "," + SCHEMA_BASE );
            syntaxesEntries.addAll( entries );
        }

        return syntaxesEntries;
    }

}
