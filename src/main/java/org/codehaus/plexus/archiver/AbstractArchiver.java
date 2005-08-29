package org.codehaus.plexus.archiver;

/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * @version $Revision$ $Date$
 */
public abstract class AbstractArchiver extends AbstractLogEnabled
    implements Archiver
{
    private Logger logger;

    private String basedir;

    private String[] excludesPattern;

    private String[] includesPattern;
    
    private File destFile;

    private Map filesMap = new HashMap();
    
    private Map dirsMap = new HashMap();

    private String prefix;
    
    private boolean includeEmptyDirs = true;

    public void addDirectory( File directory )
        throws ArchiverException
    {
        addDirectory( directory, "" );
    }

    public void addDirectory( File directory, String prefix )
        throws ArchiverException
    {
        addDirectory( directory, prefix, null, null );
    }

    public void addDirectory( File directory, String[] includes, String[] excludes )
        throws ArchiverException
    {
        addDirectory( directory, "", includes, excludes );
    }
    
    public void addDirectory( File directory, String prefix, String[] includes, String[] excludes )
        throws ArchiverException
    {
        String[] includesPattern = null;
        String[] excludesPattern = null;
        if ( includes != null )
        {
            includesPattern = new String[includes.length];
            for ( int i = 0; i < includes.length; i++ )
            {
                String pattern;
                pattern = includes[i].replace( '/', File.separatorChar ).replace(
                    '\\', File.separatorChar );
                if ( pattern.endsWith( File.separator ) )
                {
                    pattern += "**";
                }
                includesPattern[i] = pattern;
            }
        }
        if ( excludes != null )
        {
            excludesPattern = new String[excludes.length];
            for ( int i = 0; i < excludes.length; i++ )
            {
                String pattern;
                pattern = excludes[i].replace( '/', File.separatorChar ).replace(
                    '\\', File.separatorChar );
                if ( pattern.endsWith( File.separator ) )
                {
                    pattern += "**";
                }
                excludesPattern[i] = pattern;
            }
        }
        
        DirectoryScanner scanner = new DirectoryScanner();
        if ( includes != null )
        {
            scanner.setIncludes( includesPattern );
        }

        if ( excludes != null )
        {
            scanner.setExcludes( excludesPattern );
        }
        
        if ( !directory.isDirectory() )
        {
            throw new ArchiverException( directory.getAbsolutePath() + " isn't a directory." );
        }

        String basedir = directory.getAbsolutePath();
        scanner.setBasedir( basedir );
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        for ( int i = 0; i < files.length; i++ ) {
            String file = files[i];
            file = file.replace( '\\', '/' );
            if ( prefix != null )
            {
                filesMap.put( prefix + file, new File( basedir, file ) );
            }
            else
            {
                filesMap.put( file, new File( basedir, file ) );
            }
        }
        
        if ( includeEmptyDirs )
        {
            String[] dirs = scanner.getIncludedDirectories();
            for ( int i = 0; i < dirs.length; i++ ) {
                String dir = dirs[i];
                dir = dir.replace( '\\', '/' );
                if ( prefix != null )
                {
                    getDirs().put( prefix + dir, new File( basedir, dir ) );
                }
                else
                {
                    getDirs().put( dir, new File( basedir, dir ) );
                }
            }
        }
    }

    public void addFile( File inputFile, String destFileName )
        throws ArchiverException
    {
        if ( !inputFile.isFile() || !inputFile.exists() )
        {
            throw new ArchiverException( inputFile.getAbsolutePath() + " isn't a file." );
        }

        destFileName = destFileName.replace( '\\', '/' );
        filesMap.put( destFileName, inputFile );
    }

    protected Map getFiles()
    {
        if ( !includeEmptyDirs ) return filesMap;
        
        Map resources = new HashMap();

        resources.putAll( filesMap );

        resources.putAll( getDirs() );
        
        return resources;
    }

    public File getDestFile()
    {
        return destFile;
    }

    public void setDestFile( File destFile )
    {
        this.destFile = destFile;
        destFile.getParentFile().mkdirs();
    }

    protected Logger getLogger()
    {
        if ( logger == null )
        {
            if ( super.getLogger() != null )
            {
                logger = super.getLogger();
            }
            else
            {
                logger = new ConsoleLogger( Logger.LEVEL_INFO, "console" );
            }
        }

        return logger;
    }

    public boolean getIncludeEmptyDirs()
    {
        return includeEmptyDirs;
    }

    public void setIncludeEmptyDirs(boolean includeEmptyDirs)
    {
        this.includeEmptyDirs = includeEmptyDirs;
    }

    public Map getDirs()
    {
        return dirsMap;
    }
}
