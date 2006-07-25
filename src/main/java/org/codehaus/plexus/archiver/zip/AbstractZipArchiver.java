package org.codehaus.plexus.archiver.zip;

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

import org.codehaus.plexus.archiver.AbstractArchiver;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchiveFilterException;
import org.codehaus.plexus.archiver.ArchiveFinalizer;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.FilterEnabled;
import org.codehaus.plexus.archiver.FinalizerEnabled;
import org.codehaus.plexus.archiver.UnixStat;
import org.codehaus.plexus.archiver.util.FilterSupport;
import org.codehaus.plexus.util.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.zip.CRC32;

/**
 * @version $Revision$ $Date$
 */
public abstract class AbstractZipArchiver
    extends AbstractArchiver
    implements FilterEnabled, FinalizerEnabled
{

    private String comment;

    /**
     * Encoding to use for filenames, defaults to the platform's
     * default encoding.
     */
    private String encoding;

    private boolean doCompress = true;

    private boolean doUpdate = false;

    // shadow of the above if the value is altered in execute
    private boolean savedDoUpdate = false;

    protected String archiveType = "zip";

    /*
     * Whether the original compression of entries coming from a ZIP
     * archive should be kept (for example when updating an archive).
     */
    //not used: private boolean keepCompression = false;

    private boolean doFilesonly = false;

    protected Hashtable entries = new Hashtable();

    protected String duplicate = "add";

    protected Hashtable addedDirs = new Hashtable();

    private Vector addedFiles = new Vector();

    private static final long EMPTY_CRC = new CRC32().getValue();

    protected boolean doubleFilePass = false;

    protected boolean skipWriting = false;

    /**
     * true when we are adding new files into the Zip file, as opposed
     * to adding back the unchanged files
     */
    protected boolean addingNewFiles = false;

    /**
     * Whether the file modification times will be rounded up to the
     * next even number of seconds.
     */
    private boolean roundUp = true;
    
    private FilterSupport filterSupport;

    private List finalizers;
    
    public void setArchiveFilters( List filters )
    {
        filterSupport = new FilterSupport( filters, getLogger() );
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment( String comment )
    {
        this.comment = comment;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }

    public void setCompress( boolean compress )
    {
        this.doCompress = compress;
    }

    public boolean isCompress()
    {
        return doCompress;
    }

    public void setUpdateMode( boolean update )
    {
        this.doUpdate = update;
        savedDoUpdate = doUpdate;
    }

    public boolean isInUpdateMode()
    {
        return doUpdate;
    }

    /**
     * A 3 digit octal string, specify the user, group and
     * other modes in the standard Unix fashion;
     * optional, default=0644
     *
     * @deprecated use AbstractArchiver.setDefaultFileMode(int) instead.
     */
    public void setFileMode( String octalString )
    {
        setDefaultFileMode( Integer.parseInt( octalString, 8 ) );
    }

    /**
     * @deprecated use AbstractArchiver.getDefaultFileMode() instead.
     */
    public int getFileMode()
    {
        return getDefaultFileMode();
    }

    /**
     * A 3 digit octal string, specify the user, group and
     * other modes in the standard Unix fashion;
     * optional, default=0755
     *
     * @deprecated use AbstractArchiver.setDefaultDirectoryMode(int).
     */
    public void setDirMode( String octalString )
    {
        setDefaultDirectoryMode( Integer.parseInt( octalString, 8 ) );
    }

    /**
     * @deprecated use AbstractArchiver.getDefaultDirectoryMode() instead.
     */
    public int getDirMode()
    {
        return getDefaultDirectoryMode();
    }

    /**
     * If true, emulate Sun's jar utility by not adding parent directories;
     * optional, defaults to false.
     */
    public void setFilesonly( boolean f )
    {
        doFilesonly = f;
    }

    public boolean isFilesonly()
    {
        return doFilesonly;
    }

    /**
     * Whether the file modification times will be rounded up to the
     * next even number of seconds.
     * <p/>
     * <p>Zip archives store file modification times with a
     * granularity of two seconds, so the times will either be rounded
     * up or down.  If you round down, the archive will always seem
     * out-of-date when you rerun the task, so the default is to round
     * up.  Rounding up may lead to a different type of problems like
     * JSPs inside a web archive that seem to be slightly more recent
     * than precompiled pages, rendering precompilation useless.</p>
     */
    public void setRoundUp( boolean r )
    {
        roundUp = r;
    }

    public boolean isRoundUp()
    {
        return roundUp;
    }

    public void createArchive()
        throws ArchiverException, IOException
    {
        if ( doubleFilePass )
        {
            skipWriting = true;
            createArchiveMain();
            skipWriting = false;
            createArchiveMain();
        }
        else
        {
            createArchiveMain();
        }
    }

    private void createArchiveMain()
        throws ArchiverException
    {
        Map archiveEntries = getFiles();

        if ( archiveEntries == null || archiveEntries.size() == 0 )
        {
            new ArchiverException( "You must set at least one file." );
        }

        File zipFile = getDestFile();

        if ( zipFile == null )
        {
            new ArchiverException( "You must set the destination " + archiveType + "file." );
        }

        if ( zipFile.exists() && !zipFile.isFile() )
        {
            new ArchiverException( zipFile + " isn't a file." );
        }

        if ( zipFile.exists() && !zipFile.canWrite() )
        {
            new ArchiverException( zipFile + " is read-only." );
        }

        // Renamed version of original file, if it exists
        File renamedFile = null;
        // Whether or not an actual update is required -
        // we don't need to update if the original file doesn't exist

        addingNewFiles = true;

        if ( doUpdate && !zipFile.exists() )
        {
            doUpdate = false;
            getLogger().debug( "ignoring update attribute as " + archiveType + " doesn't exist." );
        }

        boolean success = false;

        try
        {
            if ( doUpdate )
            {
                renamedFile =
                    FileUtils.createTempFile( "zip", ".tmp",
                                              zipFile.getParentFile() );
                renamedFile.deleteOnExit();

                try
                {
                    FileUtils.rename( zipFile, renamedFile );
                }
                catch ( SecurityException e )
                {
                    getLogger().debug( e.toString() );
                    throw new ArchiverException(
                        "Not allowed to rename old file ("
                        + zipFile.getAbsolutePath()
                        + ") to temporary file", e );
                }
                catch ( IOException e )
                {
                    getLogger().debug( e.toString() );
                    throw new ArchiverException(
                        "Unable to rename old file ("
                        + zipFile.getAbsolutePath()
                        + ") to temporary file", e );
                }
            }

            String action = doUpdate ? "Updating " : "Building ";

            getLogger().info( action + archiveType + ": " + zipFile.getAbsolutePath() );

            ZipOutputStream zOut = null;
            try
            {
                if ( !skipWriting )
                {
                    zOut = new ZipOutputStream( zipFile );

                    zOut.setEncoding( encoding );
                    if ( doCompress )
                    {
                        zOut.setMethod( ZipOutputStream.DEFLATED );
                    }
                    else
                    {
                        zOut.setMethod( ZipOutputStream.STORED );
                    }
                }
                initZipOutputStream( zOut );

                // Add the new files to the archive.
                addResources( getResourcesToAdd( zipFile ), zOut );

                if ( doUpdate )
                {
                    addResources( getResourcesToUpdate( zipFile ), zOut );
                }
                finalizeZipOutputStream( zOut );

                // If we've been successful on an update, delete the
                // temporary file
                if ( doUpdate )
                {
                    if ( !renamedFile.delete() )
                    {
                        getLogger().warn( "Warning: unable to delete temporary file "
                                          + renamedFile.getName() );
                    }
                }
                success = true;
            }
            finally
            {
                // Close the output stream.
                try
                {
                    if ( zOut != null )
                    {
                        zOut.close();
                    }
                }
                catch ( IOException ex )
                {
                    // If we're in this finally clause because of an
                    // exception, we don't really care if there's an
                    // exception when closing the stream. E.g. if it
                    // throws "ZIP file must have at least one entry",
                    // because an exception happened before we added
                    // any files, then we must swallow this
                    // exception. Otherwise, the error that's reported
                    // will be the close() error, which is not the
                    // real cause of the problem.
                    if ( success )
                    {
                        throw ex;
                    }
                }
            }
        }
        catch ( IOException ioe )
        {
            String msg = "Problem creating " + archiveType + ": "
                         + ioe.getMessage();

            // delete a bogus ZIP file (but only if it's not the original one)
            if ( ( !doUpdate || renamedFile != null ) && !zipFile.delete() )
            {
                msg += " (and the archive is probably corrupt but I could not "
                       + "delete it)";
            }

            if ( doUpdate && renamedFile != null )
            {
                try
                {
                    FileUtils.rename( renamedFile, zipFile );
                }
                catch ( IOException e )
                {
                    msg += " (and I couldn't rename the temporary file "
                           + renamedFile.getName() + " back)";
                }
            }

            throw new ArchiverException( msg, ioe );
        }
        finally
        {
            cleanUp();
        }
    }

    protected Map getResourcesToAdd( File file )
        throws IOException
    {
        if ( !file.exists() || !doUpdate )
        {
            return getFiles();
        }

        ZipFile zipFile = new ZipFile( file );

        Map result = new HashMap();

        for ( Iterator iter = getFiles().keySet().iterator(); iter.hasNext(); )
        {
            String fileName = (String) iter.next();
            if ( zipFile.getEntry( fileName ) == null )
            {
                result.put( fileName, getFiles().get( fileName ) );
            }
        }
        return result;
    }

    protected Map getResourcesToUpdate( File file )
        throws IOException
    {
        Map result = new HashMap();

        if ( file.exists() && doUpdate )
        {
            ZipFile zipFile = new ZipFile( file );
            for ( Iterator iter = getFiles().keySet().iterator(); iter.hasNext(); )
            {
                String fileName = (String) iter.next();
                ZipEntry zipEntry;

                if ( ( zipEntry = zipFile.getEntry( fileName ) ) != null )
                {
                    ArchiveEntry currentEntry = (ArchiveEntry) getFiles().get( fileName );
                    if ( zipEntry.getTime() < currentEntry.getFile().lastModified() )
                    {
                        result.put( fileName, getFiles().get( fileName ) );
                    }
                }
            }
        }

        return result;
    }

    /**
     * Add the given resources.
     *
     * @param resources the resources to add
     * @param zOut      the stream to write to
     */
    protected final void addResources( Map resources, ZipOutputStream zOut )
        throws IOException, ArchiverException
    {
        File base = null;

        for ( Iterator iter = resources.keySet().iterator(); iter.hasNext(); )
        {
            String name = (String) iter.next();
            ArchiveEntry entry = (ArchiveEntry) resources.get( name );
            name = name.replace( File.separatorChar, '/' );

            if ( "".equals( name ) )
            {
                continue;
            }

            if ( entry.getFile().isDirectory() && !name.endsWith( "/" ) )
            {
                name = name + "/";
            }

            addParentDirs( base, name, zOut, "" );

            if ( entry.getFile().isFile() )
            {
                zipFile( entry, zOut, name );
            }
            else
            {
                zipDir( entry.getFile(), zOut, name, entry.getMode() );
            }
        }
    }

    /**
     * Ensure all parent dirs of a given entry have been added.
     */
    protected final void addParentDirs( File baseDir, String entry,
                                        ZipOutputStream zOut, String prefix )
        throws IOException
    {
        if ( !doFilesonly && getIncludeEmptyDirs() )
        {
            Stack directories = new Stack();

            // Don't include the last entry itself if it's
            // a dir; it will be added on its own.
            int slashPos = entry.length() - ( entry.endsWith( "/" ) ? 1 : 0 );

            while ( ( slashPos = entry.lastIndexOf( '/', slashPos - 1 ) ) != -1 )
            {
                String dir = entry.substring( 0, slashPos + 1 );

                if ( addedDirs.contains( prefix + dir ) )
                {
                    break;
                }

                directories.push( dir );
            }

            while ( !directories.isEmpty() )
            {
                String dir = (String) directories.pop();
                File f;
                if ( baseDir != null )
                {
                    f = new File( baseDir, dir );
                }
                else
                {
                    f = new File( dir );
                }
                zipDir( f, zOut, prefix + dir, getDefaultDirectoryMode() );
            }
        }
    }

    /**
     * Adds a new entry to the archive, takes care of duplicates as well.
     *
     * @param in           the stream to read data for the entry from.
     * @param zOut         the stream to write to.
     * @param vPath        the name this entry shall have in the archive.
     * @param lastModified last modification time for the entry.
     * @param fromArchive  the original archive we are copying this
     *                     entry from, will be null if we are not copying from an archive.
     * @param mode         the Unix permissions to set.
     */
    protected void zipFile( InputStream in, ZipOutputStream zOut, String vPath,
                            long lastModified, File fromArchive, int mode )
        throws IOException, ArchiverException
    {
        try
        {
            if ( filterSupport != null && !filterSupport.include( in, vPath ) )
            {
                return;
            }
        }
        catch ( ArchiveFilterException e )
        {
            throw new ArchiverException( "Error verifying \'" + vPath + "\' for inclusion: " + e.getMessage(), e );
        }
        
        if ( entries.contains( vPath ) )
        {
            if ( duplicate.equals( "preserve" ) )
            {
                getLogger().info( vPath + " already added, skipping" );
                return;
            }
            else if ( duplicate.equals( "fail" ) )
            {
                throw new ArchiverException( "Duplicate file " + vPath
                                             + " was found and the duplicate "
                                             + "attribute is 'fail'." );
            }
            else
            {
                // duplicate equal to add, so we continue
                getLogger().debug( "duplicate file " + vPath
                                   + " found, adding." );
            }
        }
        else
        {
            getLogger().debug( "adding entry " + vPath );
        }

        entries.put( vPath, vPath );

        if ( !skipWriting )
        {
            ZipEntry ze = new ZipEntry( vPath );
            ze.setTime( lastModified );
            ze.setMethod( doCompress ? ZipEntry.DEFLATED : ZipEntry.STORED );

            /*
            * ZipOutputStream.putNextEntry expects the ZipEntry to
            * know its size and the CRC sum before you start writing
            * the data when using STORED mode - unless it is seekable.
            *
            * This forces us to process the data twice.
            */
            if ( !zOut.isSeekable() && !doCompress )
            {
                long size = 0;
                CRC32 cal = new CRC32();
                if ( !in.markSupported() )
                {
                    // Store data into a byte[]
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    byte[] buffer = new byte[8 * 1024];
                    int count = 0;

                    do
                    {
                        size += count;
                        cal.update( buffer, 0, count );
                        bos.write( buffer, 0, count );
                        count = in.read( buffer, 0, buffer.length );
                    }
                    while ( count != -1 );

                    in = new ByteArrayInputStream( bos.toByteArray() );
                }
                else
                {
                    in.mark( Integer.MAX_VALUE );
                    byte[] buffer = new byte[8 * 1024];
                    int count = 0;

                    do
                    {
                        size += count;
                        cal.update( buffer, 0, count );
                        count = in.read( buffer, 0, buffer.length );
                    }
                    while ( count != -1 );

                    in.reset();
                }
                ze.setSize( size );
                ze.setCrc( cal.getValue() );
            }

            ze.setUnixMode( UnixStat.FILE_FLAG | mode );
            zOut.putNextEntry( ze );

            byte[] buffer = new byte[8 * 1024];
            int count = 0;
            do
            {
                if ( count != 0 )
                {
                    zOut.write( buffer, 0, count );
                }
                count = in.read( buffer, 0, buffer.length );
            }
            while ( count != -1 );
        }

        addedFiles.addElement( vPath );
    }

    /**
     * Method that gets called when adding from java.io.File instances.
     * <p/>
     * <p>This implementation delegates to the six-arg version.</p>
     *
     * @param entry the file to add to the archive
     * @param zOut  the stream to write to
     * @param vPath the name this entry shall have in the archive
     */
    protected void zipFile( ArchiveEntry entry, ZipOutputStream zOut, String vPath )
        throws IOException, ArchiverException
    {
        if ( entry.equals( getDestFile() ) )
        {
            throw new ArchiverException( "A zip file cannot include itself" );
        }

        FileInputStream fIn = new FileInputStream( entry.getFile() );
        try
        {
            // ZIPs store time with a granularity of 2 seconds, round up
            zipFile( fIn, zOut, vPath, entry.getFile().lastModified() + ( roundUp ? 1999 : 0 ), null, entry.getMode() );
        }
        finally
        {
            fIn.close();
        }
    }

    /**
     *
     */
    protected void zipDir( File dir, ZipOutputStream zOut, String vPath,
                           int mode )
        throws IOException
    {
        if ( addedDirs.get( vPath ) != null )
        {
            // don't add directories we've already added.
            // no warning if we try, it is harmless in and of itself
            return;
        }

        getLogger().debug( "adding directory " + vPath );
        addedDirs.put( vPath, vPath );

        if ( !skipWriting )
        {
            ZipEntry ze = new ZipEntry( vPath );

            if ( dir != null && dir.exists() )
            {
                // ZIPs store time with a granularity of 2 seconds, round up
                ze.setTime( dir.lastModified() + ( roundUp ? 1999 : 0 ) );
            }
            else
            {
                // ZIPs store time with a granularity of 2 seconds, round up
                ze.setTime( System.currentTimeMillis() + ( roundUp ? 1999 : 0 ) );
            }
            ze.setSize( 0 );
            ze.setMethod( ZipEntry.STORED );
            // This is faintly ridiculous:
            ze.setCrc( EMPTY_CRC );
            ze.setUnixMode( mode );

            zOut.putNextEntry( ze );
        }
    }

    /**
     * Create an empty zip file
     *
     * @return true for historic reasons
     */
    protected boolean createEmptyZip( File zipFile )
        throws ArchiverException
    {
        // In this case using java.util.zip will not work
        // because it does not permit a zero-entry archive.
        // Must create it manually.
        getLogger().info( "Note: creating empty " + archiveType + " archive " + zipFile );
        OutputStream os = null;
        try
        {
            os = new FileOutputStream( zipFile );
            // Cf. PKZIP specification.
            byte[] empty = new byte[22];
            empty[ 0 ] = 80; // P
            empty[ 1 ] = 75; // K
            empty[ 2 ] = 5;
            empty[ 3 ] = 6;
            // remainder zeros
            os.write( empty );
        }
        catch ( IOException ioe )
        {
            throw new ArchiverException( "Could not create empty ZIP archive "
                                         + "(" + ioe.getMessage() + ")", ioe );
        }
        finally
        {
            if ( os != null )
            {
                try
                {
                    os.close();
                }
                catch ( IOException e )
                {
                    //ignore
                }
            }
        }
        return true;
    }

    /**
     * Do any clean up necessary to allow this instance to be used again.
     * <p/>
     * <p>When we get here, the Zip file has been closed and all we
     * need to do is to reset some globals.</p>
     * <p/>
     * <p>This method will only reset globals that have been changed
     * during execute(), it will not alter the attributes or nested
     * child elements.  If you want to reset the instance so that you
     * can later zip a completely different set of files, you must use
     * the reset method.</p>
     *
     * @see #reset
     */
    protected void cleanUp()
    {
        addedDirs.clear();
        addedFiles.removeAllElements();
        entries.clear();
        addingNewFiles = false;
        doUpdate = savedDoUpdate;
    }

    /**
     * Makes this instance reset all attributes to their default
     * values and forget all children.
     *
     * @see #cleanUp
     */
    public void reset()
    {
        setDestFile( null );
        duplicate = "add";
        archiveType = "zip";
        doCompress = true;
        doUpdate = false;
        doFilesonly = false;
        encoding = null;
    }

    /**
     * method for subclasses to override
     */
    protected void initZipOutputStream( ZipOutputStream zOut )
        throws IOException, ArchiverException
    {
    }

    /**
     * method for subclasses to override
     */
    protected void finalizeZipOutputStream( ZipOutputStream zOut )
        throws IOException, ArchiverException
    {
        runArchiveFinalizers();
    }
    
    public void setArchiveFinalizers( List archiveFinalizers )
    {
        this.finalizers = archiveFinalizers;
    }

    protected List getArchiveFinalizers()
    {
        return finalizers;
    }

    protected void runArchiveFinalizers()
        throws ArchiverException
    {
        if ( finalizers != null )
        {
            for ( Iterator it = finalizers.iterator(); it.hasNext(); )
            {
                ArchiveFinalizer finalizer = (ArchiveFinalizer) it.next();
                
                finalizer.finalizeArchiveCreation( this );
            }
        }
    }
}
