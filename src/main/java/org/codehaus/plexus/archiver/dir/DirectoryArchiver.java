package org.codehaus.plexus.archiver.dir;

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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.plexus.archiver.AbstractArchiver;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.CommandLineException;

/**
 * A plexus archiver implementation that stores the files to archive in a
 * directory.
 */
public class DirectoryArchiver
    extends AbstractArchiver
{
    public void resetArchiver()
    {
        setIncludeEmptyDirs( false );
        getDirs().clear();
        getFiles().clear();
        setIncludeEmptyDirs( true );
    }

    public void createArchive()
        throws ArchiverException, IOException
    {
        //Most of this method was copied from org.codehaus.plexus.archiver.tar.TarArchiver
        //and modified to store files in a directory, not a tar archive.
        Map listFiles = getFiles();
        if ( listFiles == null || listFiles.size() == 0 )
        {
            throw new ArchiverException( "You must set at least one file." );
        }

        File destDirectory = getDestFile();
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

        // Check if we don't add directory file in itself
        for ( Iterator iter = getFiles().keySet().iterator(); iter.hasNext(); )
        {
            String fileName = (String) iter.next();
            ArchiveEntry fileToAdd = (ArchiveEntry) getFiles().get( fileName );
            if ( destDirectory.equals( fileToAdd.getFile() ) )
            {
                throw new ArchiverException( "The destination directory cannot include itself." );
            }
        }

        getLogger().info( "Copying " + listFiles.size() + " files to " + destDirectory.getAbsolutePath() );

        try
        {
            for ( Iterator iter = getFiles().keySet().iterator(); iter.hasNext(); )
            {
                String fileName = (String) iter.next();
                ArchiveEntry f = (ArchiveEntry) getFiles().get( fileName );
                String destDir = destDirectory.getCanonicalPath();
                fileName = destDir + File.separator + fileName;
                copyFile( f, fileName );
            }
        }
        catch ( IOException ioe )
        {
            String message = "Problem copying files : " + ioe.getMessage();
            throw new ArchiverException( message, ioe );
        }
    }

    /**
     * Copies the specified file to the specified path, creating any ancestor directory
     * structure as necessary.
     *
     * @param file  The file to copy (IOException will be thrown if this does not exist)
     * @param vPath The fully qualified path to copy the file to.
     * @throws ArchiverException If there is a problem creating the directory structure
     * @throws IOException       If there is a problem copying the file
     */
    protected void copyFile( ArchiveEntry entry, String vPath )
        throws ArchiverException, IOException
    {
        // don't add "" to the archive
        if ( vPath.length() <= 0 )
        {
            return;
        }

        File inFile = entry.getFile();
        File outFile = new File( vPath );

        if ( outFile.exists() && outFile.lastModified() >= inFile.lastModified() )
        {
            //already up to date...
            return;
        }

        outFile.setLastModified( inFile.lastModified() );

        if ( ! inFile.isDirectory() )
        {
            if ( ! outFile.getParentFile().exists() )
            {
                //create the parent directory...
                if ( ! outFile.getParentFile().mkdirs() )
                {
                    //Failure, unable to create specified directory for some unknown reason.
                    throw new ArchiverException( "Unable to create directory or parent directory of " + outFile );
                }
            }
            FileUtils.copyFile( inFile, outFile );
            chmod( outFile, entry.getMode() );
        }
        else
        { //file is a directory
            if ( outFile.exists() )
            {
                if ( ! outFile.isDirectory() )
                {
                    //should we just delete the file and replace it with a directory?
                    //throw an exception, let the user delete the file manually.
                    throw new ArchiverException(
                        "Expected directory and found file at copy destination of " + inFile + " to " + outFile );
                }
            }
            else if ( ! outFile.mkdirs() )
            {
                //Failure, unable to create specified directory for some unknown reason.
                throw new ArchiverException( "Unable to create directory or parent directory of " + outFile );
            }
        }
    }

    private void chmod( File file, int mode )
        throws ArchiverException
    {
        if ( ! Os.isFamily( "unix" ) )
        {
            return;
        }

        String m = Integer.toOctalString( mode & 0xfff );

        try
        {
            Commandline commandline = new Commandline();

            commandline.setWorkingDirectory( file.getParentFile().getAbsolutePath() );

            commandline.setExecutable( "chmod" );

            commandline.createArgument().setValue( m  );

            commandline.createArgument().setValue( file.getAbsolutePath() );

            CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();

            CommandLineUtils.StringStreamConsumer stdout = new CommandLineUtils.StringStreamConsumer();

            int exitCode = CommandLineUtils.executeCommandLine( commandline, stderr, stdout );

            if ( exitCode != 0 )
            {
                getLogger().warn( "-------------------------------" );
                getLogger().warn( "Standard error:" );
                getLogger().warn( "-------------------------------" );
                getLogger().warn( stderr.getOutput() );
                getLogger().warn( "-------------------------------" );
                getLogger().warn( "Standard output:" );
                getLogger().warn( "-------------------------------" );
                getLogger().warn( stdout.getOutput() );
                getLogger().warn( "-------------------------------" );

                throw new ArchiverException( "chmod exit code was: " + exitCode );
            }
        }
        catch ( CommandLineException e )
        {
            throw new ArchiverException( "Error while executing chmod.", e );
        }
    }
}
