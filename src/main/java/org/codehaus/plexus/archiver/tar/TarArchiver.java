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

import org.codehaus.plexus.archiver.AbstractArchiver;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnixStat;
import org.codehaus.plexus.archiver.bzip2.CBZip2OutputStream;
import org.codehaus.plexus.archiver.util.EnumeratedAttribute;
import org.codehaus.plexus.util.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

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

    private TarLongFileMode longFileMode = new TarLongFileMode();

    private TarCompressionMethod compression = new TarCompressionMethod();

    private TarOptions options = new TarOptions();

    /**
     *
     */
    public TarOptions getOptions()
    {
        return options;
    }

    /**
     * Set all tar options
     *
     * @param options options
     */
    public void setOptions( TarOptions options )
    {
        this.options = options;

        // FIXME: do these options have precedence over
        // setDefaultFileMode / setDefaultDirMode
        // or the other way around? Assuming these
        // take precedende since they're more specific.
        // Better refactor this when usage is known.

        setDefaultFileMode( options.getMode() );

        setDefaultDirectoryMode( options.getMode() );
    }

    /**
     * Override AbstractArchiver.setDefaultFileMode to
     * update TarOptions.
     */
    public void setDefaultFileMode( int mode )
    {
        super.setDefaultFileMode( mode );

        options.setMode( mode );
    }

    /**
     * Override AbstractArchiver.setDefaultDirectoryMode to
     * update TarOptions.
     */
    public void setDefaultDirectoryMode( int mode )
    {
        super.setDefaultDirectoryMode( mode );

        options.setDirMode( mode );
    }


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

    public void createArchive()
        throws ArchiverException, IOException
    {
    	if ( ! checkForced() )
    	{
    		return;
    	}
        
        Map archiveEntries = getFiles();

        if ( archiveEntries == null || archiveEntries.size() == 0 )
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

        // Check if we don't add tar file in inself
        if ( containsFile( tarFile, archiveEntries.values() ) )
        {
            throw new ArchiverException( "A tar file cannot include itself." );
        }

        getLogger().info( "Building tar : " + tarFile.getAbsolutePath() );

        TarOutputStream tOut = null;
        try
        {
            tOut = new TarOutputStream(
                compression.compress( new BufferedOutputStream( new FileOutputStream( tarFile ) ) ) );
            tOut.setDebug( true );
            if ( longFileMode.isTruncateMode() )
            {
                tOut.setLongFileMode( TarOutputStream.LONGFILE_TRUNCATE );
            }
            else if ( longFileMode.isFailMode() || longFileMode.isOmitMode() )
            {
                tOut.setLongFileMode( TarOutputStream.LONGFILE_ERROR );
            }
            else
            {
                // warn or GNU
                tOut.setLongFileMode( TarOutputStream.LONGFILE_GNU );
            }

            longWarningGiven = false;
            for ( Iterator iter = archiveEntries.keySet().iterator(); iter.hasNext(); )
            {
                String fileName = (String) iter.next();
                String name = StringUtils.replace( fileName, File.separatorChar, '/' );

                ArchiveEntry entry = (ArchiveEntry) archiveEntries.get( fileName );

                tarFile( entry, tOut, name );
            }
        }
        catch ( IOException ioe )
        {
            String message = "Problem creating TAR : " + ioe.getMessage();
            throw new ArchiverException( message, ioe );
        }
        finally
        {
            if ( tOut != null )
            {
                try
                {
                    // close up
                    tOut.close();
                }
                catch ( IOException e )
                {
                    // ignore
                }
            }
        }
    }

    // TODO: use Collection.contains; need to create suitable compare
    // method in ArchiveEntry.
    private static boolean containsFile( File file, Collection list )
    {
        for ( Iterator i = list.iterator(); i.hasNext(); )
        {
            File fileToAdd = ( (ArchiveEntry) i.next() ).getFile();

            if ( file.equals( fileToAdd ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * tar a file
     *
     * @param entry the file to tar
     * @param tOut  the output stream
     * @param vPath the path name of the file to tar
     * @throws IOException on error
     */
    protected void tarFile( ArchiveEntry entry, TarOutputStream tOut, String vPath )
        throws ArchiverException, IOException
    {
        FileInputStream fIn = null;

        // don't add "" to the archive
        if ( vPath.length() <= 0 )
        {
            return;
        }

        if ( entry.getFile().isDirectory() && !vPath.endsWith( "/" ) )
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

        try
        {
            if ( vPath.length() >= TarConstants.NAMELEN )
            {
                if ( longFileMode.isOmitMode() )
                {
                    getLogger().info( "Omitting: " + vPath );
                    return;
                }
                else if ( longFileMode.isWarnMode() )
                {
                    getLogger().warn( "Entry: " + vPath + " longer than "
                                      + TarConstants.NAMELEN + " characters." );
                    if ( !longWarningGiven )
                    {
                        getLogger().warn( "Resulting tar file can only be processed "
                                          + "successfully by GNU compatible tar commands" );
                        longWarningGiven = true;
                    }
                }
                else if ( longFileMode.isFailMode() )
                {
                    throw new ArchiverException( "Entry: " + vPath + " longer than " + TarConstants.NAMELEN
                                                 + "characters." );
                }
            }

            TarEntry te = new TarEntry( vPath );
            te.setModTime( entry.getFile().lastModified() );

            if ( !entry.getFile().isDirectory() )
            {
                te.setSize( entry.getFile().length() );
                te.setMode( entry.getMode() );
            }
            else
            {
                te.setMode( entry.getMode() );
            }
            te.setUserName( options.getUserName() );
            te.setGroupName( options.getGroup() );
            te.setUserId( options.getUid() );
            te.setGroupId( options.getGid() );

            tOut.putNextEntry( te );

            if ( !entry.getFile().isDirectory() )
            {
                fIn = new FileInputStream( entry.getFile() );

                byte[] buffer = new byte[8 * 1024];
                int count = 0;
                do
                {
                    tOut.write( buffer, 0, count );
                    count = fIn.read( buffer, 0, buffer.length );
                }
                while ( count != -1 );
            }

            tOut.closeEntry();
        }
        finally
        {
            if ( fIn != null )
            {
                fIn.close();
            }
        }
    }

    /**
     * Valid Modes for Compression attribute to Tar Task
     */
    public class TarOptions
    {
        /**
         * @deprecated
         */
        private int fileMode = UnixStat.FILE_FLAG | UnixStat.DEFAULT_FILE_PERM;

        /**
         * @deprecated
         */
        private int dirMode = UnixStat.DIR_FLAG | UnixStat.DEFAULT_DIR_PERM;

        private String userName = "";

        private String groupName = "";

        private int uid;

        private int gid;

        private boolean preserveLeadingSlashes = false;

        /**
         * A 3 digit octal string, specify the user, group and
         * other modes in the standard Unix fashion;
         * optional, default=0644
         *
         * @param octalString a 3 digit octal string.
         * @deprecated use AbstractArchiver.setDefaultFileMode(int)
         */
        public void setMode( String octalString )
        {
            setMode( Integer.parseInt( octalString, 8 ) );
        }

        /**
         * @param mode unix file mode
         * @deprecated use AbstractArchiver.setDefaultFileMode(int)
         */
        public void setMode( int mode )
        {
            this.fileMode = UnixStat.FILE_FLAG | ( mode & UnixStat.PERM_MASK );
        }

        /**
         * @return the current mode.
         * @deprecated use AbstractArchiver.getDefaultFileMode()
         */
        public int getMode()
        {
            return fileMode;
        }

        /**
         * A 3 digit octal string, specify the user, group and
         * other modes in the standard Unix fashion;
         * optional, default=0755
         *
         * @param octalString a 3 digit octal string.
         * @since Ant 1.6
         * @deprecated use AbstractArchiver.setDefaultDirectoryMode(int)
         */
        public void setDirMode( String octalString )
        {
            setDirMode( Integer.parseInt( octalString, 8 ) );
        }

        /**
         * @param mode unix directory mode
         * @deprecated use AbstractArchiver.setDefaultDirectoryMode(int)
         */
        public void setDirMode( int mode )
        {
            this.dirMode = UnixStat.DIR_FLAG | ( mode & UnixStat.PERM_MASK );
        }

        /**
         * @return the current directory mode
         * @since Ant 1.6
         * @deprecated use AbstractArchiver.getDefaultDirectoryMode()
         */
        public int getDirMode()
        {
            return dirMode;
        }

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
    public static final class TarCompressionMethod
        extends EnumeratedAttribute
    {

        // permissible values for compression attribute

        /**
         * No compression
         */
        private static final String NONE = "none";

        /**
         * GZIP compression
         */
        private static final String GZIP = "gzip";

        /**
         * BZIP2 compression
         */
        private static final String BZIP2 = "bzip2";


        /**
         * Default constructor
         */
        public TarCompressionMethod()
        {
            super();
            try
            {
                setValue( NONE );
            }
            catch ( ArchiverException ae )
            {
                //Do nothing
            }
        }

        /**
         * Get valid enumeration values.
         *
         * @return valid enumeration values
         */
        public String[] getValues()
        {
            return new String[]{NONE, GZIP, BZIP2};
        }

        /**
         * This method wraps the output stream with the
         * corresponding compression method
         *
         * @param ostream output stream
         * @return output stream with on-the-fly compression
         * @throws IOException thrown if file is not writable
         */
        private OutputStream compress( final OutputStream ostream )
            throws IOException
        {
            final String value = getValue();
            if ( GZIP.equals( value ) )
            {
                return new GZIPOutputStream( ostream );
            }
            else
            {
                if ( BZIP2.equals( value ) )
                {
                    ostream.write( 'B' );
                    ostream.write( 'Z' );
                    return new CBZip2OutputStream( ostream );
                }
            }
            return ostream;
        }
    }

	public boolean isSupportingForced() {
		return true;
	}
}
