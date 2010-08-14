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

package org.apache.directory.shared.ldap.schema.loader.ldif;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.ldif.LdifEntry;
import org.apache.directory.shared.ldap.ldif.LdifReader;
import org.apache.directory.shared.ldap.schema.registries.AbstractSchemaLoader;
import org.apache.directory.shared.ldap.schema.registries.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A schema loader based on a single monolithic ldif file containing all the schema partition elements
 * 
 * Performs better than any other existing LDIF schema loaders. NOT DOCUMENTED atm
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SingleLdifSchemaLoader extends AbstractSchemaLoader
{

    private static final Logger LOG = LoggerFactory.getLogger( SingleLdifSchemaLoader.class );

    private String[] schemaObjectTypeRdns = new String[]
        { "attributetypes", "comparators", "ditContentRules", "ditStructureRules", "matchingRules", "matchingRuleUse",
            "nameForms", "normalizers", "objectClasses", "syntaxes", "syntaxCheckers" };

    private Map<String, Map<String, List<Entry>>> scObjEntryMap = new HashMap<String, Map<String, List<Entry>>>();


    public SingleLdifSchemaLoader()
    {
        try
        {
            URL resource = getClass().getClassLoader().getResource( "schema-all.ldif" );

            LOG.debug( "URL of the all schema ldif file {}", resource );

            for ( String s : schemaObjectTypeRdns )
            {
                scObjEntryMap.put( s, new HashMap<String, List<Entry>>() );
            }

            InputStream in = resource.openStream();

            initializeSchemas( in );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }


    private void initializeSchemas( InputStream in ) throws Exception
    {
        Pattern schemaStartPattern = Pattern.compile( "cn=[a-z0-9-_]*\\s*,\\s*ou\\s*=\\s*schema" );

        LdifReader ldifReader = new LdifReader( in );

        Schema currentSchema = null;

        while ( ldifReader.hasNext() )
        {
            LdifEntry ldifEntry = ldifReader.next();
            String dn = ldifEntry.getDn().getName();

            if ( schemaStartPattern.matcher( dn ).matches() )
            {
                Schema schema = getSchema( ldifEntry.getEntry() );
                schemaMap.put( schema.getSchemaName(), schema );
                currentSchema = schema;
            }
            else
            {
                _loadSchemaObject( currentSchema.getSchemaName(), ldifEntry );
            }
        }

        ldifReader.close();
    }


    private void _loadSchemaObject( String schemaName, LdifEntry ldifEntry ) throws Exception
    {
        for ( String scObjTypeRdn : schemaObjectTypeRdns )
        {
            Pattern regex = Pattern.compile( "m-oid=[0-9\\.]*" + ",ou=" + scObjTypeRdn + ",cn=" + schemaName
                + ",ou=schema", Pattern.CASE_INSENSITIVE );

            String dn = ldifEntry.getDn().getName();

            if ( regex.matcher( dn ).matches() )
            {
                Map<String, List<Entry>> m = scObjEntryMap.get( scObjTypeRdn );
                List<Entry> entryList = m.get( schemaName );
                if ( entryList == null )
                {
                    entryList = new ArrayList<Entry>();
                    entryList.add( ldifEntry.getEntry() );
                    m.put( schemaName, entryList );
                }
                else
                {
                    entryList.add( ldifEntry.getEntry() );
                }

                break;
            }
        }
    }


    private List<Entry> loadSchemaObjects( String schemaObjectType, Schema... schemas ) throws LdapException,
        IOException
    {
        Map<String, List<Entry>> m = scObjEntryMap.get( schemaObjectType );
        List<Entry> atList = new ArrayList<Entry>();

        for ( Schema s : schemas )
        {
            List<Entry> preLoaded = m.get( s.getSchemaName() );
            if ( preLoaded != null )
            {
                atList.addAll( preLoaded );
            }
        }

        return atList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadAttributeTypes( Schema... schemas ) throws LdapException, IOException
    {
        return loadSchemaObjects( "attributetypes", schemas );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadComparators( Schema... schemas ) throws LdapException, IOException
    {
        return loadSchemaObjects( "comparators", schemas );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadDitContentRules( Schema... schemas ) throws LdapException, IOException
    {
        return loadSchemaObjects( "ditContentRules", schemas );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadDitStructureRules( Schema... schemas ) throws LdapException, IOException
    {
        return loadSchemaObjects( "ditStructureRules", schemas );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadMatchingRules( Schema... schemas ) throws LdapException, IOException
    {
        return loadSchemaObjects( "matchingRules", schemas );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadMatchingRuleUses( Schema... schemas ) throws LdapException, IOException
    {
        return loadSchemaObjects( "matchingRuleUse", schemas );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadNameForms( Schema... schemas ) throws LdapException, IOException
    {
        return loadSchemaObjects( "nameForms", schemas );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadNormalizers( Schema... schemas ) throws LdapException, IOException
    {
        return loadSchemaObjects( "normalizers", schemas );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadObjectClasses( Schema... schemas ) throws LdapException, IOException
    {
        return loadSchemaObjects( "objectClasses", schemas );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadSyntaxes( Schema... schemas ) throws LdapException, IOException
    {
        return loadSchemaObjects( "syntaxes", schemas );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadSyntaxCheckers( Schema... schemas ) throws LdapException, IOException
    {
        return loadSchemaObjects( "syntaxCheckers", schemas );
    }


    // a simple manual test method
    public static void main( String[] args ) throws Exception
    {
        SingleLdifSchemaLoader loader = new SingleLdifSchemaLoader();

        Schema schema = loader.getSchema( "inetorgperson" );

        System.out.println( schema );

        List<Entry> attrList = loader.loadAttributeTypes( schema );
        assert ( 9 == attrList.size() );
    }

}

class SchemaMarker
{
    private int start;
    private int end;


    public SchemaMarker( int start )
    {
        this.start = start;
    }


    public void setEnd( int end )
    {
        this.end = end;
    }


    public int getStart()
    {
        return start;
    }


    public int getEnd()
    {
        return end;
    }
}
