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


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
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
 * Warn: this works, but not a super performer. Need to profile to see how much time the regex matching
 *       is taking (this is definitely the hotspot)
 *       NOT DOCUMENTED atm
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SingleLdifSchemaLoader extends AbstractSchemaLoader
{

    private static final Logger LOG = LoggerFactory.getLogger( SingleLdifSchemaLoader.class );

    private RandomAccessFile schemaFile;

    private Map<String, SchemaMarker> markerMap = new HashMap<String, SchemaMarker>();

    private LdifReader ldifParser = new LdifReader();


    public SingleLdifSchemaLoader()
    {
        try
        {
            URL resource = getClass().getClassLoader().getResource( "schema-all.ldif" );

            LOG.debug( "URL of the all schema ldif file {}", resource );

            File tempSchemaFile = File.createTempFile( "all-schema", ".ldif" );
            LOG.debug( "storing the all schema file at {}", tempSchemaFile.getAbsolutePath() );

            InputStream in = resource.openStream();
            OutputStream out = new FileOutputStream( tempSchemaFile );

            byte[] buf = new byte[1024 * 1024];
            while ( true )
            {
                int read = in.read( buf );
                if ( read > 0 )
                {
                    out.write( buf, 0, read );
                    continue;
                }

                break;
            }

            in.close();
            out.close();

            schemaFile = new RandomAccessFile( tempSchemaFile, "r" );

            initializeSchemas();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }


    private void initializeSchemas() throws Exception
    {
        Pattern schemaStartPattern = Pattern.compile( "dn:\\s*cn=[a-z0-9-_]*\\s*,\\s*ou\\s*=\\s*schema" );

        String readLine = null;

        SchemaMarker prevMarker = null;

        while ( true )
        {
            int startLineOffset = ( int ) schemaFile.getFilePointer();

            readLine = schemaFile.readLine();

            if ( readLine == null )
            {
                break;
            }

            Matcher matcher = schemaStartPattern.matcher( readLine );
            if ( matcher.matches() )
            {
                if ( prevMarker != null )
                {
                    prevMarker.setEnd( startLineOffset );
                }

                LdifEntry entry = readLdif( readLine );
                Schema schema = getSchema( entry.getEntry() );
                schemaMap.put( schema.getSchemaName(), schema );

                SchemaMarker marker = new SchemaMarker( startLineOffset );
                markerMap.put( schema.getSchemaName(), marker );
                prevMarker = marker;
            }
        }

        prevMarker.setEnd( ( int ) schemaFile.getFilePointer() );
    }


    private LdifEntry readLdif( String startLine ) throws Exception
    {
        StringBuilder sb = new StringBuilder( startLine );
        sb.append( '\n' );

        while ( true )
        {
            startLine = schemaFile.readLine();
            if ( startLine.length() == 0 )
            {
                break;
            }
            else
            {
                sb.append( startLine );
                sb.append( '\n' );
            }
        }

        return ldifParser.parseLdif( sb.toString() ).get( 0 );
    }


    private List<Entry> getMatchingEntries( Pattern regex, int endOffset ) throws LdapException
    {

        try
        {
            List<Entry> entries = new ArrayList<Entry>();
            String s = null;

            // a perf improvement hack 
            boolean matchesFound = false;

            while ( ( s = schemaFile.readLine() ) != null )
            {
                Matcher matcher = regex.matcher( s );
                if ( matcher.matches() )
                {
                    matchesFound = true;
                    entries.add( readLdif( s ).getEntry() );
                }
                else if ( matchesFound )
                {
                    break;
                }

                if ( schemaFile.getFilePointer() >= endOffset )
                {
                    break;
                }
            }

            return entries;
        }
        catch ( Exception e )
        {
            throw new LdapException( e.getMessage(), e );
        }

    }


    private Pattern getPattern( Schema schema, String schemaObjectTypeRdn )
    {
        Pattern regex = Pattern.compile(
            "dn:\\s*m-oid=[0-9\\.]*" + ",ou=" + schemaObjectTypeRdn + ",cn=" + schema.getSchemaName() + ",ou=schema",
            Pattern.CASE_INSENSITIVE );

        return regex;
    }


    private List<Entry> loadSchemaObjects( String schemaObjectType, Schema... schemas ) throws LdapException,
        IOException
    {
        List<Entry> scObjTypeList = new ArrayList<Entry>();

        if ( schemas == null )
        {
            return scObjTypeList;
        }

        for ( Schema schema : schemas )
        {
            SchemaMarker marker = markerMap.get( schema.getSchemaName() );
            schemaFile.seek( marker.getStart() );

            Pattern scObjEntryRegex = getPattern( schema, schemaObjectType );

            scObjTypeList.addAll( getMatchingEntries( scObjEntryRegex, marker.getEnd() ) );
        }

        return scObjTypeList;
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
