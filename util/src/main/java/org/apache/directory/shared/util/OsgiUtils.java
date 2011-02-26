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
package org.apache.directory.shared.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utilities for OSGi environments and embedding OSGi containers.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OsgiUtils
{
    /** A logger */
    private static final Logger LOG = LoggerFactory.getLogger( OsgiUtils.class );
    
    
    /**
     * All the packages that are exported from all bundles found on the system 
     * classpath. The provided filter if not null is used to prune classpath 
     * elements. Any uses terms found are stripped from the bundles.
     *
     * @return All the exported packages of all bundles on the classpath.
     */
    public static Set<String> getAllBundleExports( FileFilter filter, Set<String> pkgs )
    {
        if ( pkgs == null )
        {
            pkgs = new HashSet<String>();
        }
        
        Set<File> candidates = getClasspathCandidates( filter );
        
        for ( File candidate : candidates )
        {
            String exports = getBundleExports( candidate );
            
            if ( exports == null )
            {
                LOG.debug( "No export found for candidate: {}", candidate );
                continue;
            }
            
            LOG.debug( "Processing exports for candidate: {}\n\n{}\n", candidate, exports );
            splitIntoPackages( exports, pkgs );
        }
        
        return pkgs;
    }

    
    /**
     * Splits an Package-Export OSGi Manifest Attribute value into packages 
     * while stripping away the key/value properties.
     *
     * @param exports The Package-Export OSGi Manifest Attribute value.
     * @return The set of exported packages without properties.
     */
    public static Set<String> splitIntoPackages( String exports, Set<String> pkgs )
    {
        if ( pkgs == null )
        {
            pkgs = new HashSet<String>();
        }
        
        int index = 0;
        boolean inPkg = true;
        boolean inProps = false;
        StringBuilder pkg = new StringBuilder();
        
        while ( index < exports.length() )
        {
            if ( inPkg && exports.charAt( index ) != ';' )
            {
                pkg.append( exports.charAt( index ) );
                index++;
            }
            else if ( inPkg && exports.charAt( index ) == ';' )
            {
                inPkg = false;
                inProps = true;
                
                pkgs.add( pkg.toString() );
                LOG.debug( "Added package: {}", pkg.toString() );
                pkg.setLength( 0 );
                
                index += 8;
            }
            else if ( inProps && exports.charAt( index ) == '"' 
                && index + 1 < exports.length()
                && exports.charAt( index + 1 ) == ',' )
            {
                inPkg = true;
                inProps = false;
                index += 2;
            }
            else if ( inProps )
            {
                index++;
            }
            else
            {
                LOG.error( "Unexpected parser condition throwing IllegalStateException." );
                throw new IllegalStateException( "Should never get here!" );
            }
        }
        
        return pkgs;
    }
    
    
    public static Set<File> getClasspathCandidates( FileFilter filter )
    {
        Set<File> candidates = new HashSet<File>();
        String[] cpElements = System.getProperty( "java.class.path" ).split( ":" );
        
        for ( String element : cpElements )
        {
            File candidate = new File( element );
            
            if ( candidate.isFile() )
            {
                if ( filter != null && filter.accept( candidate ) )
                {
                    candidates.add( candidate );
                    LOG.info( "Accepted candidate with filter: {}", candidate.toString() );
                }
                else if ( filter == null && candidate.getName().endsWith( ".jar" ) )
                {
                    candidates.add( candidate );
                    LOG.info( "Accepted candidate without filter: {}", candidate.toString() );
                }
                else
                {
                    LOG.info( "Rejecting candidate: {}", candidate.toString() );
                }
            }
        }
        
        return candidates;
    }

    
    /**
     * Gets the attribute value for the Export-Bundle OSGi Manifest Attribute.
     * 
     * @param bundle The absolute path to a file bundle.
     * @return The value as it appears in the Manifest, as a comma delimited 
     * list of packages with possible "uses" phrases appended to each package 
     * or null if the attribute does not exist.
     */
    public static String getBundleExports( File bundle )
    {   
        JarFile jar;
        try
        {
            jar = new JarFile( bundle );
            Manifest manifest = jar.getManifest();
            
            Attributes attrs = manifest.getMainAttributes();
            for ( Object key : attrs.keySet() )
            {
                if ( key.toString().equals( "Export-Package" ) )
                {
                    return attrs.get( key ).toString();
                }
            }
            
            return null;
        }
        catch ( IOException e )
        {
            LOG.error( "Failed to open jar file or manifest.", e );
            throw new RuntimeException( "Failed to open jar file or manifest.", e );
        }
    }
}
