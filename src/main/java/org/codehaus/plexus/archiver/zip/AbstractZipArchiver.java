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

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import java.util.concurrent.ExecutionException;
import java.util.zip.CRC32;

import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.codehaus.plexus.archiver.AbstractArchiver;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.ResourceIterator;
import org.codehaus.plexus.archiver.UnixStat;
import org.codehaus.plexus.archiver.util.ResourceUtils;
import org.codehaus.plexus.components.io.functions.SymlinkDestinationSupplier;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import javax.annotation.WillClose;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.Hashtable;
import java.util.Stack;

import static org.codehaus.plexus.archiver.util.Streams.bufferedOutputStream;
import static org.codehaus.plexus.archiver.util.Streams.fileOutputStream;

/**
 * @version $Revision$ $Date$
 */
@SuppressWarnings( { "NullableProblems", "UnusedDeclaration" } )
public abstract class AbstractZipArchiver
    extends AbstractArchiver
{

    private String comment;

    /**
     * Encoding to use for filenames, defaults to the platform's
     * default encoding.
     */
    private String encoding = "UTF8";

    private boolean doCompress = true;

    private boolean recompressAddedZips = true;

    private boolean doUpdate = false;

    // shadow of the above if the value is altered in execute
    private boolean savedDoUpdate = false;

    protected String archiveType = "zip";

    private boolean doFilesonly = false;

    protected final Hashtable<String, String> entries = new Hashtable<String, String>();

	protected final AddedDirs addedDirs = new AddedDirs();

    private static final long EMPTY_CRC = new CRC32().getValue();

    protected boolean doubleFilePass = false;

    protected boolean skipWriting = false;

    /**
     * @deprecated Use {@link Archiver#setDuplicateBehavior(String)} instead.
     */
    protected final String duplicate = Archiver.DUPLICATES_SKIP;

    /**
     * true when we are adding new files into the Zip file, as opposed
     * to adding back the unchanged files
     */
    protected boolean addingNewFiles = false;

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
     * than precompiled pages, rendering precompilation useless.</p>	 *
     * <p/>
     * plexus-archiver chooses to round up.
     * <p/>
     * Java versions up to java7 round timestamp down, which means we add a heuristic value (which is slightly questionable)
     * Java versions from 8 and up round timestamp up.
     * s
     */
    private static final boolean isJava7OrLower =
        Integer.parseInt( System.getProperty( "java.version" ).split( "\\." )[1] ) <= 7;

    // Renamed version of original file, if it exists
    private File renamedFile = null;

    private File zipFile;

    private boolean success;

    private ConcurrentJarCreator zOut;

    protected ZipArchiveOutputStream zipArchiveOutputStream;

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

    public boolean isRecompressAddedZips()
    {
        return recompressAddedZips;
    }

    public void setRecompressAddedZips( boolean recompressAddedZips )
    {
        this.recompressAddedZips = recompressAddedZips;
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
     * If true, emulate Sun's jar utility by not adding parent directories;
     * optional, defaults to false.
     *
     * @param f true to emilate sun jar utility
     */
    public void setFilesonly( boolean f )
    {
        doFilesonly = f;
    }

    public boolean isFilesonly()
    {
        return doFilesonly;
    }


    protected void execute()
        throws ArchiverException, IOException
    {
        if ( !checkForced() )
        {
            return;
        }

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

        finalizeZipOutputStream( zOut );
    }

    protected void finalizeZipOutputStream( ConcurrentJarCreator zOut )
        throws IOException, ArchiverException
    {
    }

    private void createArchiveMain()
        throws ArchiverException, IOException
    {
        //noinspection deprecation
        if ( !Archiver.DUPLICATES_SKIP.equals( duplicate ) )
        {
            //noinspection deprecation
            setDuplicateBehavior( duplicate );
        }

        ResourceIterator iter = getResources();
        if ( !iter.hasNext() && !hasVirtualFiles() )
        {
            throw new ArchiverException( "You must set at least one file." );
        }

        zipFile = getDestFile();

        if ( zipFile == null )
        {
            throw new ArchiverException( "You must set the destination " + archiveType + "file." );
        }

        if ( zipFile.exists() && !zipFile.isFile() )
        {
            throw new ArchiverException( zipFile + " isn't a file." );
        }

        if ( zipFile.exists() && !zipFile.canWrite() )
        {
            throw new ArchiverException( zipFile + " is read-only." );
        }

        // Whether or not an actual update is required -
        // we don't need to update if the original file doesn't exist

        addingNewFiles = true;

        if ( doUpdate && !zipFile.exists() )
        {
            doUpdate = false;
            getLogger().debug( "ignoring update attribute as " + archiveType + " doesn't exist." );
        }

        success = false;

        if ( doUpdate )
        {
            renamedFile = FileUtils.createTempFile( "zip", ".tmp", zipFile.getParentFile() );
            renamedFile.deleteOnExit();

            try
            {
                FileUtils.rename( zipFile, renamedFile );
            }
            catch ( SecurityException e )
            {
                getLogger().debug( e.toString() );
                throw new ArchiverException(
                    "Not allowed to rename old file (" + zipFile.getAbsolutePath() + ") to temporary file", e );
            }
            catch ( IOException e )
            {
                getLogger().debug( e.toString() );
                throw new ArchiverException(
                    "Unable to rename old file (" + zipFile.getAbsolutePath() + ") to temporary file", e );
            }
        }

        String action = doUpdate ? "Updating " : "Building ";

        getLogger().info( action + archiveType + ": " + zipFile.getAbsolutePath() );

        if ( !skipWriting )
        {
            zipArchiveOutputStream =
                new ZipArchiveOutputStream( bufferedOutputStream( fileOutputStream( zipFile, "zip" ) ) );
            zipArchiveOutputStream.setEncoding( encoding );
            zipArchiveOutputStream.setCreateUnicodeExtraFields(
                ZipArchiveOutputStream.UnicodeExtraFieldPolicy.NOT_ENCODEABLE );
            zipArchiveOutputStream.setMethod(
                doCompress ? ZipArchiveOutputStream.DEFLATED : ZipArchiveOutputStream.STORED );

            zOut = new ConcurrentJarCreator(Runtime.getRuntime().availableProcessors());
        }
        initZipOutputStream( zOut );

        // Add the new files to the archive.
        addResources( iter, zOut );

        // If we've been successful on an update, delete the
        // temporary file
        if ( doUpdate )
        {
            if ( !renamedFile.delete() )
            {
                getLogger().warn( "Warning: unable to delete temporary file " + renamedFile.getName() );
            }
        }
        success = true;
    }

    /**
     * Add the given resources.
     *
     * @param resources the resources to add
     * @param zOut      the stream to write to
     */
    @SuppressWarnings( { "JavaDoc" } )
    protected final void addResources( ResourceIterator resources, ConcurrentJarCreator zOut )
        throws IOException, ArchiverException
    {
        while ( resources.hasNext() )
        {
            ArchiveEntry entry = resources.next();
            String name = entry.getName();
            name = name.replace( File.separatorChar, '/' );

            if ( "".equals( name ) )
            {
                continue;
            }

            if ( entry.getResource().isDirectory() && !name.endsWith( "/" ) )
            {
                name = name + "/";
            }

            addParentDirs( entry, null, name, zOut);

            if ( entry.getResource().isFile() )
            {
                zipFile( entry, zOut, name );
            }
            else
            {
                zipDir( entry.getResource(), zOut, name, entry.getMode(), encoding );
            }
        }
    }

    /**
     * Ensure all parent dirs of a given entry have been added.
     * <p/>
     * This method is computed in terms of the potentially remapped entry (that may be disconnected from the file system)
     * we do not *relly* know the entry's connection to the file system so establishing the attributes of the parents can
     * be impossible and is not really supported.
     */
    @SuppressWarnings( { "JavaDoc" } )
    private void addParentDirs(ArchiveEntry archiveEntry, File baseDir, String entry, ConcurrentJarCreator zOut)
        throws IOException
    {
        if ( !doFilesonly && getIncludeEmptyDirs() )
        {
			Stack<String> directories = addedDirs.asStringStack(entry);

            while ( !directories.isEmpty() )
            {
                String dir = directories.pop();
                File f;
                if ( baseDir != null )
                {
                    f = new File( baseDir, dir );
                }
                else
                {
                    f = new File( dir );
                }
                // the
                // At this point we could do something like read the atr
                final PlexusIoResource res = new AnonymousResource( f );
                zipDir( res, zOut, dir, archiveEntry.getDefaultDirMode(), encoding );
            }
        }
    }

	/**
     * Adds a new entry to the archive, takes care of duplicates as well.
     *
     * @param in                 the stream to read data for the entry from.
     * @param zOut               the stream to write to.
     * @param vPath              the name this entry shall have in the archive.
     * @param lastModified       last modification time for the entry.
     * @param fromArchive        the original archive we are copying this
     * @param symlinkDestination
     */
    @SuppressWarnings( { "JavaDoc" } )
    protected void zipFile( @WillClose InputStream in, ConcurrentJarCreator zOut, String vPath, long lastModified,
                            File fromArchive, int mode, String symlinkDestination )
        throws IOException, ArchiverException
    {
        getLogger().debug( "adding entry " + vPath );

        entries.put( vPath, vPath );

        if ( !skipWriting )
        {
            ZipArchiveEntry ze = new ZipArchiveEntry( vPath );
            setTime( ze, lastModified );

            byte[] header = new byte[4];
            int read = in.read( header );

            boolean compressThis = doCompress;
            if ( !recompressAddedZips && isZipHeader( header ) )
            {
                compressThis = false;
            }

            ze.setMethod( compressThis ? ZipArchiveEntry.DEFLATED : ZipArchiveEntry.STORED );
            ze.setUnixMode( UnixStat.FILE_FLAG | mode );

            InputStream payload;
            if ( ze.isUnixSymlink() )
            {
                ZipEncoding enc = ZipEncodingHelper.getZipEncoding( getEncoding() );
                final byte[] bytes = enc.encode( symlinkDestination ).array();
                payload = new ByteArrayInputStream( bytes );
            }
            else
            {
                payload = maybeSequence( header, read, in );
            }
            zOut.addArchiveEntry( ze, createInputStreamSupplier( payload ) );

        }
    }

    private InputStream maybeSequence( byte[] header, int hdrBytes, InputStream in )
    {
        return hdrBytes > 0 ? new SequenceInputStream( new ByteArrayInputStream( header, 0, hdrBytes ), in ) : in;
    }

    private boolean isZipHeader( byte[] header )
    {
        return header[0] == 0x50 && header[1] == 0x4b && header[2] == 3 && header[3] == 4;
    }

    /**
     * Method that gets called when adding from java.io.File instances.
     * <p/>
     * <p>This implementation delegates to the six-arg version.</p>
     *  @param entry the file to add to the archive
     * @param zOut  the stream to write to
	 * @param vPath the name this entry shall have in the archive
	 */
    @SuppressWarnings( { "JavaDoc" } )
    protected void zipFile( ArchiveEntry entry, ConcurrentJarCreator zOut, String vPath )
        throws IOException, ArchiverException
    {
        final PlexusIoResource resource = entry.getResource();
        if ( ResourceUtils.isSame( resource, getDestFile() ) )
        {
            throw new ArchiverException( "A zip file cannot include itself" );
        }

        final boolean b = entry.getResource() instanceof SymlinkDestinationSupplier;
        String symlinkTarget = b ? ( (SymlinkDestinationSupplier) entry.getResource() ).getSymlinkDestination() : null;
        InputStream in = entry.getInputStream();
        try
        {
            zipFile( in, zOut, vPath, resource.getLastModified(), null, entry.getMode(), symlinkTarget );
        }
        catch ( IOException e )
        {
            throw new ArchiverException( "IOException when zipping r" + entry.getName() + ": " + e.getMessage(), e );
        }
    }

    private void setTime( java.util.zip.ZipEntry zipEntry, long lastModified )
    {
        zipEntry.setTime( lastModified + ( isJava7OrLower ? 1999 : 0 ) );

		/*   Consider adding extended file stamp support.....

		X5455_ExtendedTimestamp ts =  new X5455_ExtendedTimestamp();
		ts.setModifyJavaTime(new Date(lastModified));
		if (zipEntry.getExtra() != null){
			// Uh-oh. What do we do now.
			throw new IllegalStateException("DIdnt expect to see xtradata here ?");

		}  else {
			zipEntry.setExtra(ts.getLocalFileDataData());
		}
        */
    }

    protected void zipDir( PlexusIoResource dir, ConcurrentJarCreator zOut, String vPath, int mode,
                           String encodingToUse )
        throws IOException
    {
        if ( addedDirs.update(vPath)  )
        {
            return;
        }

        getLogger().debug("adding directory " + vPath);

        if ( !skipWriting )
        {
            final boolean isSymlink = dir instanceof SymlinkDestinationSupplier;

            if ( isSymlink && vPath.endsWith( File.separator ) )
            {
                vPath = vPath.substring( 0, vPath.length() - 1 );
            }

            ZipArchiveEntry ze = new ZipArchiveEntry( vPath );

            /*
             * ZipOutputStream.putNextEntry expects the ZipEntry to
             * know its size and the CRC sum before you start writing
             * the data when using STORED mode - unless it is seekable.
             *
             * This forces us to process the data twice.
             */

            if ( isSymlink )
            {
                mode = UnixStat.LINK_FLAG | mode;
            }

            if ( dir != null && dir.isExisting() )
            {
                setTime( ze, dir.getLastModified() );
            }
            else
            {
                // ZIPs store time with a granularity of 2 seconds, round up
                setTime( ze, System.currentTimeMillis() );
            }
            if ( !isSymlink )
            {
                ze.setSize( 0 );
                ze.setMethod( ZipArchiveEntry.STORED );
                // This is faintly ridiculous:
                ze.setCrc( EMPTY_CRC );
            }
            ze.setUnixMode( mode );

            if ( !isSymlink )
            {
                zOut.addArchiveEntry( ze, createInputStreamSupplier( new ByteArrayInputStream( "".getBytes() ) ) );
            }
            else
            {
                String symlinkDestination = ( (SymlinkDestinationSupplier) dir ).getSymlinkDestination();
                ZipEncoding enc = ZipEncodingHelper.getZipEncoding( encodingToUse );
                final byte[] bytes = enc.encode( symlinkDestination ).array();
                ze.setMethod( ZipArchiveEntry.DEFLATED );
                zOut.addArchiveEntry( ze, createInputStreamSupplier( new ByteArrayInputStream( bytes ) ) );
            }
        }
    }

    private InputStreamSupplier createInputStreamSupplier( final InputStream inputStream )
    {
        return new InputStreamSupplier()
        {
            public InputStream get()
            {
                return inputStream;
            }
        };
    }

    /**
     * Create an empty zip file
     *
     * @param zipFile The file
     * @return true for historic reasons
     */
    @SuppressWarnings( { "JavaDoc" } )
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
            empty[0] = 80; // P
            empty[1] = 75; // K
            empty[2] = 5;
            empty[3] = 6;
            // remainder zeros
            os.write( empty );
        }
        catch ( IOException ioe )
        {
            throw new ArchiverException( "Could not create empty ZIP archive " + "(" + ioe.getMessage() + ")", ioe );
        }
        finally
        {
            IOUtil.close( os );
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
        throws IOException
    {
        super.cleanUp();
        addedDirs.clear();
        entries.clear();
        addingNewFiles = false;
        doUpdate = savedDoUpdate;
        success = false;
        zOut = null;
        renamedFile = null;
        zipFile = null;
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
//        duplicate = "add";
        archiveType = "zip";
        doCompress = true;
        doUpdate = false;
        doFilesonly = false;
        encoding = null;
    }

    /**
     * method for subclasses to override
     *
	 * @param zOut The output stream
	 */
    protected void initZipOutputStream( ConcurrentJarCreator zOut )
        throws ArchiverException, IOException
    {
    }

    /**
     * method for subclasses to override
     */
    public boolean isSupportingForced()
    {
        return true;
    }

    protected boolean revert( StringBuffer messageBuffer )
    {
        int initLength = messageBuffer.length();

        // delete a bogus ZIP file (but only if it's not the original one)
        if ( ( !doUpdate || renamedFile != null ) && !zipFile.delete() )
        {
            messageBuffer.append( " (and the archive is probably corrupt but I could not delete it)" );
        }

        if ( doUpdate && renamedFile != null )
        {
            try
            {
                FileUtils.rename( renamedFile, zipFile );
            }
            catch ( IOException e )
            {
                messageBuffer.append( " (and I couldn't rename the temporary file " );
                messageBuffer.append( renamedFile.getName() );
                messageBuffer.append( " back)" );
            }
        }

        return messageBuffer.length() == initLength;
    }

    protected void close()
        throws IOException
    {
        // Close the output stream.
        try
        {
            if ( zipArchiveOutputStream != null )
            {
                zOut.writeTo( zipArchiveOutputStream );
                zipArchiveOutputStream.close();
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
        catch ( InterruptedException e )
        {
            IOException ex = new IOException( "InterruptedException exception" );
            ex.initCause( e.getCause() );
            throw ex;
        }
        catch ( ExecutionException e )
        {
            IOException ex = new IOException( "Execution exception" );
            ex.initCause( e.getCause() );
            throw ex;
        }
    }

    protected String getArchiveType()
    {
        return archiveType;
    }

}
