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
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.archiver.util.FilterSupport;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Revision$ $Date$
 * @todo there should really be constructors which take the source file.
 */
public abstract class AbstractUnArchiver
    extends AbstractLogEnabled
    implements UnArchiver, FinalizerEnabled, FilterEnabled
{
    private File destDirectory;

    private File destFile;

    private File sourceFile;

    private boolean overwrite = true;

    private FilterSupport filterSupport;

    private List finalizers;

    public AbstractUnArchiver()
    {
    }

    public AbstractUnArchiver( File sourceFile )
    {
        this.sourceFile = sourceFile;
    }

    public File getDestDirectory()
    {
        return destDirectory;
    }

    public void setDestDirectory( File destDirectory )
    {
        this.destDirectory = destDirectory;
    }

    public File getDestFile()
    {
        return destFile;
    }

    public void setDestFile( File destFile )
    {
        this.destFile = destFile;
    }

    public File getSourceFile()
    {
        return sourceFile;
    }

    public void setSourceFile( File sourceFile )
    {
        this.sourceFile = sourceFile;
    }

    public boolean isOverwrite()
    {
        return overwrite;
    }

    public void setOverwrite( boolean b )
    {
        overwrite = b;
    }

    public final void extract()
        throws ArchiverException, IOException
    {
        validate();
        execute();
        runArchiveFinalizers();
    }

    public final void extract( String path, File outputDirectory )
        throws ArchiverException, IOException
    {
        validate( path, outputDirectory );
        execute( path, outputDirectory );
        runArchiveFinalizers();
    }

    public void setArchiveFilters( List filters )
    {
        filterSupport = new FilterSupport( filters, getLogger() );
    }

    public void setArchiveFinalizers( List archiveFinalizers )
    {
        this.finalizers = archiveFinalizers;
    }

    private final void runArchiveFinalizers()
        throws ArchiverException
    {
        if ( finalizers != null )
        {
            for ( Iterator it = finalizers.iterator(); it.hasNext(); )
            {
                ArchiveFinalizer finalizer = (ArchiveFinalizer) it.next();

                finalizer.finalizeArchiveExtraction( this );
            }
        }
    }

    protected boolean include( InputStream inputStream, String name )
        throws ArchiveFilterException
    {
        return ( filterSupport == null || filterSupport.include( inputStream, name ) );
    }

    protected void validate( String path, File outputDirectory )
    {
    }

    protected void validate()
        throws ArchiverException
    {
        if ( sourceFile == null )
        {
            throw new ArchiverException( "The source file isn't defined." );
        }

        if ( sourceFile.isDirectory() )
        {
            throw new ArchiverException( "The source must not be a directory." );
        }

        if ( !sourceFile.exists() )
        {
            throw new ArchiverException( "The source file " + sourceFile + " doesn't exist." );
        }

        if ( destDirectory == null && destFile == null )
        {
            throw new ArchiverException( "The destination isn't defined." );
        }

        if ( destDirectory != null && destFile != null )
        {
            throw new ArchiverException( "You must choose between a destination directory and a destination file." );
        }

        if ( destDirectory != null && !destDirectory.isDirectory() )
        {
            destFile = destDirectory;
            destDirectory = null;
        }

        if ( destFile != null && destFile.isDirectory() )
        {
            destDirectory = destFile;
            destFile = null;
        }
    }

    protected abstract void execute()
        throws ArchiverException, IOException;

    protected abstract void execute( String path, File outputDirectory )
        throws ArchiverException, IOException;

}
