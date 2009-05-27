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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection;

/**
 * @version $Revision$ $Date$
 */
public interface Archiver
{
    /**
     * Default value for the dirmode attribute.
     */
    int DEFAULT_DIR_MODE = UnixStat.DIR_FLAG | UnixStat.DEFAULT_DIR_PERM;

    /**
     * Default value for the filemode attribute.
     */
    int DEFAULT_FILE_MODE = UnixStat.FILE_FLAG | UnixStat.DEFAULT_FILE_PERM;
    
    String ROLE = Archiver.class.getName();

    public static final String DUPLICATES_ADD = "add";

    public static final String DUPLICATES_PRESERVE = "preserve";

    public static final String DUPLICATES_SKIP = "skip";

    public static final String DUPLICATES_FAIL = "fail";
    
    public static final Set DUPLICATES_VALID_BEHAVIORS = new HashSet()
    {
        private static final long serialVersionUID = 1L;

        {
            add( DUPLICATES_ADD );
            add( DUPLICATES_PRESERVE );
            add( DUPLICATES_SKIP );
            add( DUPLICATES_FAIL );
        }
    };
    
    void createArchive()
        throws ArchiverException, IOException;

    /**
     * Obsolete, use {@link #addFileSet(FileSet)}.
     */
    void addDirectory( File directory )
        throws ArchiverException;

    /**
     * Obsolete, use {@link #addFileSet(FileSet)}.
     */
    void addDirectory( File directory, String prefix )
        throws ArchiverException;

    /**
     * Obsolete, use {@link #addFileSet(FileSet)}.
     */
    void addDirectory( File directory, String[] includes, String[] excludes )
        throws ArchiverException;

    /**
     * Obsolete, use {@link #addFileSet(FileSet)}.
     */
    void addDirectory( File directory, String prefix, String[] includes, String[] excludes )
        throws ArchiverException;

    /**
     * Adds the given file set to the archive.
     * This method is basically obsoleting {@link #addDirectory(File)},
     * {@link #addDirectory(File, String)}, {@link #addDirectory(File, String[], String[])},
     * and {@link #addDirectory(File, String, String[], String[])}. However, as these
     * methods are in widespread use, they cannot easily be made deprecated.
     * @throws ArchiverException Adding the file set failed.
     * @since 1.0-alpha-9
     */
    void addFileSet( FileSet fileSet ) throws ArchiverException;

    void addFile( File inputFile, String destFileName )
        throws ArchiverException;

    void addFile( File inputFile, String destFileName, int permissions )
        throws ArchiverException;

    void addArchivedFileSet( File archiveFile )
        throws ArchiverException;

    void addArchivedFileSet( File archiveFile, String prefix )
        throws ArchiverException;

    void addArchivedFileSet( File archiveFile, String[] includes, String[] excludes )
        throws ArchiverException;

    void addArchivedFileSet( File archiveFile, String prefix, String[] includes, String[] excludes )
        throws ArchiverException;

    /**
     * Adds the given archive file set to the archive.
     * This method is basically obsoleting {@link #addArchivedFileSet(File)},
     * {@link #addArchivedFileSet(File, String[], String[])}, and
     * {@link #addArchivedFileSet(File, String, String[], String[])}.
     * However, as these methods are in widespread use, they cannot easily
     * be made deprecated.
     * @since 1.0-alpha-9
     */
    void addArchivedFileSet( ArchivedFileSet fileSet )
        throws ArchiverException;

    /**
     * Adds the given resource collection to the archive.
     * @since 1.0-alpha-10
     */
    void addResource( PlexusIoResource resource, String destFileName, int permissions )
        throws ArchiverException;

    /**
     * Adds the given resource collection to the archive.
     * @since 1.0-alpha-10
     */
    void addResources( PlexusIoResourceCollection resources )
        throws ArchiverException;

    File getDestFile();

    void setDestFile( File destFile );
    
    void setFileMode( int mode );
    
    int getFileMode();
    
    int getOverrideFileMode();

    void setDefaultFileMode( int mode );

    int getDefaultFileMode();
    
    void setDirectoryMode( int mode );
    
    int getDirectoryMode();
    
    int getOverrideDirectoryMode();

    void setDefaultDirectoryMode( int mode );

    int getDefaultDirectoryMode();

    boolean getIncludeEmptyDirs();

    void setIncludeEmptyDirs( boolean includeEmptyDirs );

    void setDotFileDirectory( File dotFileDirectory );

    /**
     * Returns an iterator over instances of {@link ArchiveEntry},
     * which have previously been added by calls to
     * {@link #addResources(PlexusIoResourceCollection)},
     *  {@link #addResource(PlexusIoResource, String, int)},
     *  {@link #addFileSet(FileSet)}, etc.
     * @since 1.0-alpha-10
     */
    ResourceIterator getResources() throws ArchiverException;
    
    /**
     * @deprecated Use {@link #getResources()}
     */
    Map getFiles();

    /**
     * <p>Returns, whether recreating the archive is forced (default). Setting
     * this option to false means, that the archiver should compare the
     * timestamps of included files with the timestamp of the target archive
     * and rebuild the archive only, if the latter timestamp precedes the
     * former timestamps. Checking for timestamps will typically offer a
     * performance gain (in particular, if the following steps in a build
     * can be suppressed, if an archive isn't recrated) on the cost that
     * you get inaccurate results from time to time. In particular, removal
     * of source files won't be detected.</p>
     * <p>An archiver doesn't necessarily support checks for uptodate. If
     * so, setting this option to true will simply be ignored. The method
     * {@link #isSupportingForced()} may be called to check whether an
     * archiver does support uptodate checks.</p>
     * @return True, if the target archive should always be created; false
     *   otherwise
     * @see #setForced(boolean)
     * @see #isSupportingForced()
     */
    boolean isForced();

    /**
     * <p>Sets, whether recreating the archive is forced (default). Setting
     * this option to false means, that the archiver should compare the
     * timestamps of included files with the timestamp of the target archive
     * and rebuild the archive only, if the latter timestamp precedes the
     * former timestamps. Checking for timestamps will typically offer a
     * performance gain (in particular, if the following steps in a build
     * can be suppressed, if an archive isn't recrated) on the cost that
     * you get inaccurate results from time to time. In particular, removal
     * of source files won't be detected.</p>
     * <p>An archiver doesn't necessarily support checks for uptodate. If
     * so, setting this option to true will simply be ignored. The method
     * {@link #isSupportingForced()} may be called to check whether an
     * archiver does support uptodate checks.</p>
     * @param forced True, if the target archive should always be created; false
     *   otherwise
     * @see #isForced()
     * @see #isSupportingForced()
     */
    void setForced( boolean forced );

    /**
     * Returns, whether the archive supports uptodate checks. If so, you
     * may set {@link #setForced(boolean)} to true.
     * @return True, if the archiver does support uptodate checks, false
     *   otherwise
     * @see #setForced(boolean)
     * @see #isForced()
     */
    boolean isSupportingForced();

    /**
     * Returns the behavior of this archiver when duplicate files are detected.
     */
    String getDuplicateBehavior();

    /**
     * Set the behavior of this archiver when duplicate files are detected. One of: <br/>
     * <ul>
     * <li>add - Add the duplicates to the archive as duplicate entries</li>
     * <li>skip/preserve - Leave the first entry encountered in the archive, skip the new one</li>
     * <li>fail - throw an {@link ArchiverException}</li>
     * </ul>
     * <br/>
     * See {@link Archiver#DUPLICATES_ADD}, {@link Archiver#DUPLICATES_SKIP}, {@link Archiver#DUPLICATES_PRESERVE},
     * {@link Archiver#DUPLICATES_FAIL}.
     */
    void setDuplicateBehavior( String duplicate );
}
