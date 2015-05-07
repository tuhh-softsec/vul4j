package org.codehaus.plexus.archiver.tar;

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

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.codehaus.plexus.archiver.AbstractArchiver;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.ResourceIterator;
import org.codehaus.plexus.archiver.util.ResourceUtils;
import org.codehaus.plexus.archiver.util.Streams;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.functions.SymlinkDestinationSupplier;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.xerial.snappy.SnappyOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import static org.codehaus.plexus.archiver.util.Streams.bufferedOutputStream;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Revision$ $Date$
 */
public class TarArchiver
    extends AbstractArchiver
{
    /**
     * Indicates whether the user has been warned about long files already.
     */
    private boolean longWarningGiven = false;

    private TarLongFileMode longFileMode = TarLongFileMode.warn;

    private TarCompressionMethod compression = TarCompressionMethod.none;

    private TarOptions options = new TarOptions();

    private TarArchiveOutputStream tOut;

    /**
     * Set how to handle long files, those with a path&gt;100 chars.
     * Optional, default=warn.
     * <p/>
     * Allowable values are
     * <ul>
     * <li>  truncate - paths are truncated to the maximum length
     * <li>  fail - paths greater than the maximum cause a build exception
     * <li>  warn - paths greater than the maximum cause a warning and GNU is used
     * <li>  gnu - GNU extensions are used for any paths greater than the maximum.
     * <li>  posix - posix extensions are used for any paths greater than the maximum.
     * <li>  posixwarn - posix extensions are used (with warning) for any paths greater than the maximum.
     * <li>  omit - paths greater than the maximum are omitted from the archive
     * </ul>
     *
     * @param mode the mode to handle long file names.
     */
    public void setLongfile( TarLongFileMode mode )
    {
        this.longFileMode = mode;
    }

    /**
     * Set compression method.
     * Allowable values are
     * <ul>
     * <li>  none - no compression
     * <li>  gzip - Gzip compression
     * <li>  bzip2 - Bzip2 compression
     * </ul>
     *
     * @param mode the compression method.
     */
    public void setCompression( TarCompressionMethod mode )
    {
        this.compression = mode;
    }

    protected void execute()
        throws ArchiverException, IOException
    {
        if ( !checkForced() )
        {
            return;
        }

        ResourceIterator iter = getResources();
        if ( !iter.hasNext() )
        {
            throw new ArchiverException( "You must set at least one file." );
        }

        File tarFile = getDestFile();

        if ( tarFile == null )
        {
            throw new ArchiverException( "You must set the destination tar file." );
        }
        if ( tarFile.exists() && !tarFile.isFile() )
        {
            throw new ArchiverException( tarFile + " isn't a file." );
        }
        if ( tarFile.exists() && !tarFile.canWrite() )
        {
            throw new ArchiverException( tarFile + " is read-only." );
        }

        getLogger().info( "Building tar: " + tarFile.getAbsolutePath() );

        final OutputStream bufferedOutputStream = bufferedOutputStream( new FileOutputStream( tarFile ) );
        tOut =
            new TarArchiveOutputStream( compress( compression, bufferedOutputStream ), "UTF8" );
        if ( longFileMode.isTruncateMode() )
        {
            tOut.setLongFileMode( TarArchiveOutputStream.LONGFILE_TRUNCATE );
        }
        else if ( longFileMode.isPosixMode() || longFileMode.isPosixWarnMode() )
        {
            tOut.setLongFileMode( TarArchiveOutputStream.LONGFILE_POSIX );
			// Todo: Patch 2.5.1   for this fix. Also make closeable fix on 2.5.1
			tOut.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX);
        }
        else if ( longFileMode.isFailMode() || longFileMode.isOmitMode() )
        {
            tOut.setLongFileMode( TarArchiveOutputStream.LONGFILE_ERROR );
        }
        else
        {
            // warn or GNU
            tOut.setLongFileMode( TarArchiveOutputStream.LONGFILE_GNU );
        }

        longWarningGiven = false;
        try
        {
            while ( iter.hasNext() )
            {
                ArchiveEntry entry = iter.next();
                // Check if we don't add tar file in itself
                if ( ResourceUtils.isSame( entry.getResource(), tarFile ) )
                {
                    throw new ArchiverException( "A tar file cannot include itself." );
                }
                String fileName = entry.getName();
                String name = StringUtils.replace( fileName, File.separatorChar, '/' );

                tarFile( entry, tOut, name );
            }
        } finally
        {
            IOUtil.close( tOut );
        }
    }

    /**
     * tar a file
     *
     * @param entry the file to tar
     * @param tOut  the output stream
     * @param vPath the path name of the file to tar
     * @throws IOException on error
     */
    protected void tarFile( ArchiveEntry entry, TarArchiveOutputStream tOut, String vPath )
        throws ArchiverException, IOException
    {


        // don't add "" to the archive
        if ( vPath.length() <= 0 )
        {
            return;
        }

        if ( entry.getResource().isDirectory() && !vPath.endsWith( "/" ) )
        {
            vPath += "/";
        }

        if ( vPath.startsWith( "/" ) && !options.getPreserveLeadingSlashes() )
        {
            int l = vPath.length();
            if ( l <= 1 )
            {
                // we would end up adding "" to the archive
                return;
            }
            vPath = vPath.substring( 1, l );
        }

        int pathLength = vPath.length();
        InputStream fIn = null;

        try
        {
            TarArchiveEntry te;
			if (  !longFileMode.isGnuMode() && pathLength >= org.apache.commons.compress.archivers.tar.TarConstants.NAMELEN )
			{
        		int maxPosixPathLen = org.apache.commons.compress.archivers.tar.TarConstants.NAMELEN + org.apache.commons.compress.archivers.tar.TarConstants.PREFIXLEN;
            	if ( longFileMode.isPosixMode() )
            	{
            	}
            	else if ( longFileMode.isPosixWarnMode() )
            	{
            		if ( pathLength > maxPosixPathLen )
            		{
                        getLogger().warn( "Entry: " + vPath + " longer than " + maxPosixPathLen + " characters." );
                        if ( !longWarningGiven )
                        {
                            getLogger().warn( "Resulting tar file can only be processed "
                                              + "successfully by GNU compatible tar commands" );
                            longWarningGiven = true;
                        }
            		}
            	}
            	else if ( longFileMode.isOmitMode() )
                {
                    getLogger().info( "Omitting: " + vPath );
                    return;
                }
                else if ( longFileMode.isWarnMode() )
                {
                    getLogger().warn( "Entry: " + vPath + " longer than " + org.apache.commons.compress.archivers.tar.TarConstants.NAMELEN + " characters." );
                    if ( !longWarningGiven )
                    {
                        getLogger().warn( "Resulting tar file can only be processed "
                                          + "successfully by GNU compatible tar commands" );
                        longWarningGiven = true;
                    }
                }
                else if ( longFileMode.isFailMode() )
                {
                    throw new ArchiverException( "Entry: " + vPath + " longer than " + org.apache.commons.compress.archivers.tar.TarConstants.NAMELEN
                                                 + " characters." );
                }
                else
                {
                    throw new IllegalStateException("Non gnu mode should never get here?");
                }
            }

            if ( entry.getType() == ArchiveEntry.SYMLINK )
            {
                final SymlinkDestinationSupplier plexusIoSymlinkResource = (SymlinkDestinationSupplier) entry.getResource();
                te = new TarArchiveEntry( vPath, TarArchiveEntry.LF_SYMLINK);
                te.setLinkName( plexusIoSymlinkResource.getSymlinkDestination() );
            }
            else
            {
                te = new TarArchiveEntry( vPath );
            }

            long teLastModified = entry.getResource().getLastModified();
            te.setModTime( teLastModified == PlexusIoResource.UNKNOWN_MODIFICATION_DATE ? System.currentTimeMillis()
                               : teLastModified );

            if (entry.getType() == ArchiveEntry.SYMLINK){
                te.setSize( 0 );

            } else
            if ( !entry.getResource().isDirectory()  )
            {
                final long size = entry.getResource().getSize();
                te.setSize( size == PlexusIoResource.UNKNOWN_RESOURCE_SIZE ? 0 : size );
            }
            te.setMode( entry.getMode() );

            PlexusIoResourceAttributes attributes = entry.getResourceAttributes();

            te.setUserName( ( attributes != null && attributes.getUserName() != null ) ? attributes.getUserName()
                                : options.getUserName() );
            te.setGroupName((attributes != null && attributes.getGroupName() != null) ? attributes.getGroupName()
					: options.getGroup());

            final int userId =
                ( attributes != null && attributes.getUserId() != null ) ? attributes.getUserId() : options.getUid();
            if ( userId >= 0 )
            {
                te.setUserId( userId );
            }

            final int groupId =
                ( attributes != null && attributes.getGroupId() != null ) ? attributes.getGroupId() : options.getGid();
            if ( groupId >= 0 )
            {
                te.setGroupId( groupId );
            }

            tOut.putArchiveEntry(te);

            try {
                if (entry.getResource().isFile() && !(entry.getType() == ArchiveEntry.SYMLINK)) {
                    fIn = entry.getInputStream();

                    Streams.copyFullyDontCloseOutput(fIn, tOut, "xAR");
                }

            } catch (Throwable e){
                getLogger().warn("When creating tar entry", e);
            } finally {
                tOut.closeArchiveEntry();
            }
    }
        finally
        {
            IOUtil.close( fIn );
        }
    }

    /**
     * Valid Modes for Compression attribute to Tar Task
     */
    public class TarOptions
    {
        private String userName = "";

        private String groupName = "";

        private int uid;

        private int gid;

        private boolean preserveLeadingSlashes = false;

        /**
         * The username for the tar entry
         * This is not the same as the UID.
         *
         * @param userName the user name for the tar entry.
         */
        public void setUserName( String userName )
        {
            this.userName = userName;
        }

        /**
         * @return the user name for the tar entry
         */
        public String getUserName()
        {
            return userName;
        }

        /**
         * The uid for the tar entry
         * This is not the same as the User name.
         *
         * @param uid the id of the user for the tar entry.
         */
        public void setUid( int uid )
        {
            this.uid = uid;
        }

        /**
         * @return the uid for the tar entry
         */
        public int getUid()
        {
            return uid;
        }

        /**
         * The groupname for the tar entry; optional, default=""
         * This is not the same as the GID.
         *
         * @param groupName the group name string.
         */
        public void setGroup( String groupName )
        {
            this.groupName = groupName;
        }

        /**
         * @return the group name string.
         */
        public String getGroup()
        {
            return groupName;
        }

        /**
         * The GID for the tar entry; optional, default="0"
         * This is not the same as the group name.
         *
         * @param gid the group id.
         */
        public void setGid( int gid )
        {
            this.gid = gid;
        }

        /**
         * @return the group identifier.
         */
        public int getGid()
        {
            return gid;
        }

        /**
         * @return the leading slashes flag.
         */
        public boolean getPreserveLeadingSlashes()
        {
            return preserveLeadingSlashes;
        }

        /**
         * Flag to indicates whether leading `/'s should
         * be preserved in the file names.
         * Optional, default is <code>false</code>.
         *
         * @param preserveLeadingSlashes the leading slashes flag.
         */
        public void setPreserveLeadingSlashes( boolean preserveLeadingSlashes )
        {
            this.preserveLeadingSlashes = preserveLeadingSlashes;
        }
    }

    /**
     * Valid Modes for Compression attribute to Tar Task
     */
    public static enum TarCompressionMethod
    {
        none, gzip, bzip2, snappy

    }

    private OutputStream compress( TarCompressionMethod tarCompressionMethod, final OutputStream ostream )
        throws IOException
    {
        if ( TarCompressionMethod.gzip.equals( tarCompressionMethod ))
        {
            return new GZIPOutputStream( ostream );
        }
        else if ( TarCompressionMethod.bzip2.equals( tarCompressionMethod) )
        {
            return new BZip2CompressorOutputStream( ostream );
        }
        else if ( TarCompressionMethod.snappy.equals( tarCompressionMethod ))
        {
            return new SnappyOutputStream( ostream );
        }
        return ostream;
    }

    public boolean isSupportingForced()
    {
        return true;
    }

    protected void cleanUp()
        throws IOException
    {
        super.cleanUp();
        IOUtil.close( tOut );
    }

    protected void close()
        throws IOException
    {
        IOUtil.close( tOut );
    }

    protected String getArchiveType()
    {
        return "TAR";
    }
}
