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

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @version $Revision$ $Date$
 */
public abstract class AbstractArchiver extends AbstractLogEnabled
    implements Archiver
{
    /**
     * Default value for the dirmode attribute.
     */
    public static final int DEFAULT_DIR_MODE =
        UnixStat.DIR_FLAG  | UnixStat.DEFAULT_DIR_PERM;

    /**
     * Default value for the filemode attribute.
     */
    public static final int DEFAULT_FILE_MODE =
        UnixStat.FILE_FLAG | UnixStat.DEFAULT_FILE_PERM;

    private Logger logger;

    private File destFile;

    private Map filesMap = new LinkedHashMap();
    
    private Map dirsMap = new LinkedHashMap();

    private int defaultFileMode = DEFAULT_FILE_MODE;
    
    private boolean includeEmptyDirs = true;

    private int defaultDirectoryMode = DEFAULT_DIR_MODE;
    
    
    public void setDefaultFileMode( int mode )
    {
        defaultFileMode = ( mode & UnixStat.PERM_MASK ) | UnixStat.FILE_FLAG;
    }

    public int getDefaultFileMode()
    {
        return defaultFileMode;
    }
    
    public void setDefaultDirectoryMode( int mode )
    {
        defaultDirectoryMode = ( mode & UnixStat.PERM_MASK ) | UnixStat.DIR_FLAG;
    }

    public int getDefaultDirectoryMode()
    {
        return defaultDirectoryMode;
    }
    
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

        if ( includeEmptyDirs )
        {
            String [] dirs = scanner.getIncludedDirectories();

            for ( int i = 0; i < dirs.length; i++ )
            {
                String sourceDir = dirs[i].replace( '\\', '/' );
                
                String targetDir = ( prefix == null ? "" : prefix ) + sourceDir;

                getDirs().put( targetDir, ArchiveEntry.createEntry( targetDir,
                        new File( basedir, sourceDir ), getDefaultFileMode(), getDefaultDirectoryMode() ) );
            }
        }

        String[] files = scanner.getIncludedFiles();
        
        for ( int i = 0; i < files.length; i++ )
        {
            String sourceFile = files[i].replace( '\\', '/' );
            
            String targetFile = ( prefix == null ? "" : prefix ) + sourceFile;

            filesMap.put( targetFile, ArchiveEntry.createEntry( targetFile,
                new File( basedir, sourceFile ), getDefaultFileMode(), getDefaultDirectoryMode() ) );
        }
        
    }

    public void addFile( File inputFile, String destFileName )
        throws ArchiverException
    {
        addFile( inputFile, destFileName, getDefaultFileMode() );
    }

    public void addFile( File inputFile, String destFileName, int permissions )
        throws ArchiverException
    {
        if ( !inputFile.isFile() || !inputFile.exists() )
        {
            throw new ArchiverException( inputFile.getAbsolutePath() + " isn't a file." );
        }

        destFileName = destFileName.replace( '\\', '/' );
        filesMap.put( destFileName, ArchiveEntry.createFileEntry(
            destFileName, inputFile, permissions ) );
    }

    // TODO: convert this to Collection?
    // Only con is that some archivers change the filename
    // to contain just forward slashes; they could update
    // the Name of the ArchiveEntry..?
    protected Map getFiles()
    {
        if ( !includeEmptyDirs )
        {
        	return filesMap;
        }
        
        Map resources = new LinkedHashMap();

        resources.putAll( getDirs() );
        
        resources.putAll( filesMap );
        
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
