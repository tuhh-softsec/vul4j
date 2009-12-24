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
package org.apache.directory.shared.ldap.schema.ldif.extractor;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Extracts LDIF files for the schema repository onto a destination directory.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev: 664295 $
 */
public class SchemaLdifExtractor
{
    private static final String BASE_PATH = "";

    private static final Logger LOG = LoggerFactory.getLogger( SchemaLdifExtractor.class );
    
    private File outputDirectory;


    public SchemaLdifExtractor( File outputDirectory ) throws IOException
    {
        LOG.debug( "BASE_PATH set to {}, outputDirectory set to {}", 
            BASE_PATH, outputDirectory );
        this.outputDirectory = outputDirectory;
    }


    /**
     * Extracts the LDIF files from a Jar file or copies exploded LDIF resources.
     *
     * @param overwrite over write extracted structure if true, false otherwise
     * @throws IOException if schema already extracted and on IO errors
     */
    public void extractOrCopy( boolean overwrite ) throws IOException
    {
        if ( ! outputDirectory.exists() )
        {
            outputDirectory.mkdir();
        }

        File schemaDirectory = new File( outputDirectory, "schema" );

        if ( ! schemaDirectory.exists() )
        {
            schemaDirectory.mkdir();
        }
        else if ( ! overwrite )
        {
            throw new IOException( "Cannot overwrite yet schema output directory already exists: "
                    + schemaDirectory.getAbsolutePath() );
        }

        Pattern pattern = Pattern.compile( ".*schema/ou=schema.*\\.ldif" );
        Map<String,Boolean> list = ResourceMap.getResources( pattern );

        for ( Entry<String,Boolean> entry : list.entrySet() )
        {
            if ( entry.getValue() )
            {
                extractFromJar( entry.getKey() );
            }
            else
            {
                File resource = new File( entry.getKey() );
                copyFile( resource, getDestinationFile( resource ) );
            }
        }
    }
    
    
    /**
     * Extracts the LDIF files from a Jar file or copies exploded LDIF
     * resources without overwriting the resources if the schema has
     * already been extracted.
     *
     * @throws IOException if schema already extracted and on IO errors
     */
    public void extractOrCopy() throws IOException
    {
        extractOrCopy( false );
    }
    
    
    /**
     * Copies a file line by line from the source file argument to the 
     * destination file argument.
     *
     * @param source the source file to copy
     * @param destination the destination to copy the source to
     * @throws IOException if there are IO errors or the source does not exist
     */
    private void copyFile( File source, File destination ) throws IOException
    {
        LOG.debug( "copyFile(): source = {}, destination = {}", source, destination );
        
        if ( ! destination.getParentFile().exists() )
        {
            destination.getParentFile().mkdirs();
        }
        
        if ( ! source.getParentFile().exists() )
        {
            throw new FileNotFoundException( "Cannot copy non-existant " +
            		"source file " + source.getAbsolutePath() );
        }
        
        FileWriter out = new FileWriter( destination );
        BufferedReader in = new BufferedReader( new FileReader( source ) );
        String line;
        while ( null != ( line = in.readLine() ) )
        {
            out.write( line + "\n" ); 
        }
        
        in.close();
        out.flush();
        out.close();
    }

    
    /**
     * Assembles the destination file by appending file components previously
     * pushed on the fileComponentStack argument.
     *
     * @param fileComponentStack stack containing pushed file components
     * @return the assembled destination file
     */
    private File assembleDestinationFile( Stack<String> fileComponentStack )
    {
        File destinationFile = outputDirectory.getAbsoluteFile();
        
        while ( ! fileComponentStack.isEmpty() )
        {
            destinationFile = new File( destinationFile, fileComponentStack.pop() );
        }
        
        return destinationFile;
    }
    
    
    /**
     * Calculates the destination file.
     *
     * @param resource the source file
     * @return the destination file's parent directory
     */
    private File getDestinationFile( File resource )
    {
        File parent = resource.getParentFile();
        Stack<String> fileComponentStack = new Stack<String>();
        fileComponentStack.push( resource.getName() );
        
        while ( parent != null )
        {
            if ( parent.getName().equals( "schema" ) )
            {
                // All LDIF files besides the schema.ldif are under the 
                // schema/schema base path. So we need to add one more 
                // schema component to all LDIF files minus this schema.ldif
                fileComponentStack.push( "schema" );
                
                return assembleDestinationFile( fileComponentStack );
            }

            fileComponentStack.push( parent.getName() );
            
            if ( parent.equals( parent.getParentFile() )
                    || parent.getParentFile() == null )
            {
                throw new IllegalStateException( 
                    "Should not be hitting root without schema/schema pattern." );
            }
            
            parent = parent.getParentFile();
        }

        /*

           this seems retarded so I replaced it for now with what is below it
           will not break from loop above unless parent == null so the if is
           never executed - just the else is executed every time

        if ( parent != null )
        {
            return assembleDestinationFile( fileComponentStack );
        }
        else
        {
            throw new IllegalStateException( "parent cannot be null" );
        }
        
        */

        throw new IllegalStateException( "parent cannot be null" );
    }
    
    
    /**
     * Gets the DBFILE resource from within a jar off the base path.  If another jar
     * with such a DBFILE resource exists then an error will result since the resource
     * is not unique across all the jars.
     *
     * @param resourceName the file name of the resource to load
     * @param resourceDescription human description of the resource
     * @return the InputStream to read the contents of the resource
     * @throws IOException if there are problems reading or finding a unique copy of the resource
     */                                                                                                
    public static InputStream getUniqueResourceAsStream( String resourceName, String resourceDescription ) throws IOException
    {
        resourceName = BASE_PATH + resourceName;
        URL result = getUniqueResource( resourceName, resourceDescription );
        return result.openStream();
    }


    /**
     * Gets a unique resource from a Jar file.
     * 
     * @param resourceName the name of the resource
     * @param resourceDescription the description of the resource
     * @return the URL to the resource in the Jar file
     * @throws IOException if there is an IO error
     */
    public static URL getUniqueResource( String resourceName, String resourceDescription )
            throws IOException
    {
        Enumeration<URL> resources = SchemaLdifExtractor.class.getClassLoader().getResources( resourceName );
        if ( !resources.hasMoreElements() )
        {
            throw new UniqueResourceException( resourceName, resourceDescription );
        }
        URL result = resources.nextElement();
        if ( resources.hasMoreElements() )
        {
            throw new UniqueResourceException( resourceName, result, resources, resourceDescription);
        }
        return result;
    }
    

    /**
     * Extracts the LDIF schema resource from a Jar.
     *
     * @param resource the LDIF schema resource
     * @throws IOException if there are IO errors
     */
    private void extractFromJar( String resource ) throws IOException
    {
        byte[] buf = new byte[512];
        InputStream in = SchemaLdifExtractor.getUniqueResourceAsStream( resource, 
            "LDIF file in schema repository" );

        try
        {
        	File destination = new File( outputDirectory, resource );

            /*
             * Do not overwrite an LDIF file if it has already been extracted.
             */
            if ( destination.exists() )
            {
                return;
            }
        	
        	if ( ! destination.getParentFile().exists() )
        	{
        		destination.getParentFile().mkdirs();
        	}
        	
            FileOutputStream out = new FileOutputStream( destination );
            try
            {
                while ( in.available() > 0 )
                {
                    int readCount = in.read( buf );
                    out.write( buf, 0, readCount );
                }
                out.flush();
            } finally
            {
                out.close();
            }
        }
        finally
        {
            in.close();
        }
    }
}
