/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.plexus.archiver.dir;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.plexus.archiver.AbstractArchiver;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.ResourceIterator;
import org.codehaus.plexus.archiver.exceptions.EmptyArchiveException;
import org.codehaus.plexus.archiver.util.ArchiveEntryUtils;
import org.codehaus.plexus.archiver.util.ResourceUtils;
import org.codehaus.plexus.components.io.attributes.SymlinkUtils;
import org.codehaus.plexus.components.io.functions.SymlinkDestinationSupplier;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

/**
 * A plexus archiver implementation that stores the files to archive in a directory.
 */
public class DirectoryArchiver
    extends AbstractArchiver
{

    private final List<Runnable> directoryChmods = new ArrayList<Runnable>();

    public void resetArchiver()
        throws IOException
    {
        cleanUp();
    }

    @Override
    public void execute()
        throws ArchiverException, IOException
    {
        // Most of this method was copied from org.codehaus.plexus.archiver.tar.TarArchiver
        // and modified to store files in a directory, not a tar archive.
        final ResourceIterator iter = getResources();
        if ( !iter.hasNext() )
        {
            throw new EmptyArchiveException( "archive cannot be empty" );
        }

        final File destDirectory = getDestFile();
        if ( destDirectory == null )
        {
            throw new ArchiverException( "You must set the destination directory." );
        }
        if ( destDirectory.exists() && !destDirectory.isDirectory() )
        {
            throw new ArchiverException( destDirectory + " is not a directory." );
        }
        if ( destDirectory.exists() && !destDirectory.canWrite() )
        {
            throw new ArchiverException( destDirectory + " is not writable." );
        }

        getLogger().info( "Copying files to " + destDirectory.getAbsolutePath() );

        try
        {
            while ( iter.hasNext() )
            {
                final ArchiveEntry f = iter.next();
                // Check if we don't add directory file in itself
                if ( ResourceUtils.isSame( f.getResource(), destDirectory ) )
                {
                    throw new ArchiverException( "The destination directory cannot include itself." );
                }
                String fileName = f.getName();
                final String destDir = destDirectory.getCanonicalPath();
                fileName = destDir + File.separator + fileName;
                PlexusIoResource resource = f.getResource();
                if ( resource instanceof SymlinkDestinationSupplier )
                {
                    String dest = ( (SymlinkDestinationSupplier) resource ).getSymlinkDestination();
                    File target = new File( dest );
                    SymlinkUtils.createSymbolicLink( new File( fileName ), target );
                }
                else
                {
                    copyFile( f, fileName );
                }
            }

            for ( Runnable directoryChmod : directoryChmods )
            {
                directoryChmod.run();
            }
            directoryChmods.clear();
        }
        catch ( final IOException ioe )
        {
            final String message = "Problem copying files : " + ioe.getMessage();
            throw new ArchiverException( message, ioe );
        }
    }

    /**
     * Copies the specified file to the specified path, creating any ancestor directory structure as necessary.
     *
     * @param entry The file to copy (IOException will be thrown if this does not exist)
     * @param vPath The fully qualified path to copy the file to.
     *
     * @throws ArchiverException If there is a problem creating the directory structure
     * @throws IOException If there is a problem copying the file
     */
    protected void copyFile( final ArchiveEntry entry, final String vPath )
        throws ArchiverException, IOException
    {
        // don't add "" to the archive
        if ( vPath.length() <= 0 )
        {
            return;
        }

        final PlexusIoResource in = entry.getResource();
        final File outFile = new File( vPath );

        final long inLastModified = in.getLastModified();
        final long outLastModified = outFile.lastModified();
        if ( ResourceUtils.isUptodate( inLastModified, outLastModified ) )
        {
            return;
        }

        if ( !in.isDirectory() )
        {
            if ( !outFile.getParentFile().exists() )
            {
                // create the parent directory...
                if ( !outFile.getParentFile().mkdirs() )
                {
                    // Failure, unable to create specified directory for some unknown reason.
                    throw new ArchiverException( "Unable to create directory or parent directory of " + outFile );
                }
            }
            ResourceUtils.copyFile( entry.getInputStream(), outFile );

            setFileModes( entry, outFile, inLastModified );
        }
        else
        { // file is a directory
            if ( outFile.exists() )
            {
                if ( !outFile.isDirectory() )
                {
                    // should we just delete the file and replace it with a directory?
                    // throw an exception, let the user delete the file manually.
                    throw new ArchiverException(
                        "Expected directory and found file at copy destination of " + in.getName() + " to " + outFile );
                }
            }
            else if ( !outFile.mkdirs() )
            {
                // Failure, unable to create specified directory for some unknown reason.
                throw new ArchiverException( "Unable to create directory or parent directory of " + outFile );
            }

            directoryChmods.add( new Runnable()
            {

                @Override
                public void run()
                {
                    setFileModes( entry, outFile, inLastModified );
                }

            } );
        }

    }

    private void setFileModes( ArchiveEntry entry, File outFile, long inLastModified )
    {
        if ( !isIgnorePermissions() )
        {
            ArchiveEntryUtils.chmod( outFile, entry.getMode() );
        }

        outFile.setLastModified( inLastModified == PlexusIoResource.UNKNOWN_MODIFICATION_DATE
                                     ? System.currentTimeMillis()
                                     : inLastModified );
    }

    @Override
    protected void cleanUp()
        throws IOException
    {
        super.cleanUp();
        setIncludeEmptyDirs( false );
        setIncludeEmptyDirs( true );
    }

    @Override
    protected void close()
        throws IOException
    {
    }

    @Override
    protected String getArchiveType()
    {
        return "directory";
    }

}
