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
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * @version $Revision$ $Date$
 */
public abstract class AbstractArchiver extends AbstractLogEnabled
    implements Archiver
{
    private String basedir;

    private String[] excludesPattern;

    private String[] includesPattern;
    
    private File destFile;

    private Map filesMap = new HashMap();

    private String prefix;

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
            throw new ArchiverException( directory.getName() + " isn't a directory." );
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
    }

    public void addFile( File inputFile, String destFileName )
        throws ArchiverException
    {
        if ( !inputFile.isFile() || !inputFile.exists() )
        {
            throw new ArchiverException( inputFile + " isn't a file." );
        }

        destFileName = destFileName.replace( '\\', '/' );
        filesMap.put( destFileName, inputFile );
    }

    protected Map getFiles()
    {
        return filesMap;
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
}
