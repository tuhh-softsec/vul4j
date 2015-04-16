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

import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

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

    /**
     * Default value for the symlinkmode attribute.
     */
    int DEFAULT_SYMLILNK_MODE = UnixStat.LINK_FLAG | UnixStat.DEFAULT_LINK_PERM;

    String ROLE = Archiver.class.getName();

    public static final String DUPLICATES_ADD = "add";

    public static final String DUPLICATES_PRESERVE = "preserve";

    public static final String DUPLICATES_SKIP = "skip";

    public static final String DUPLICATES_FAIL = "fail";

    public static final Set<String> DUPLICATES_VALID_BEHAVIORS = new HashSet<String>()
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
     * You can use "inline" of this method in your
     * IDE to get the proper implementation for this release.
     */
    @Deprecated
    void addDirectory( @Nonnull File directory )
        throws ArchiverException;

    /**
     * Obsolete, use {@link #addFileSet(FileSet)}.
     * You can use "inline" of this method in your
     * IDE to get the proper implementation for this release.
     */
    @Deprecated
    void addDirectory( @Nonnull File directory, String prefix )
        throws ArchiverException;

    /**
     * Obsolete, use {@link #addFileSet(FileSet)}.You can use "inline" of this method in your
     * IDE to get the proper implementation for this release.
     */
    @Deprecated
    void addDirectory( @Nonnull File directory, String[] includes, String[] excludes )
        throws ArchiverException;

    /**
     * Obsolete, use {@link #addFileSet(FileSet)}.
     * You can use "inline" of this method in your
     * IDE to get the proper implementation for this release.
     */
    void addDirectory( @Nonnull File directory, String prefix, String[] includes, String[] excludes )
        throws ArchiverException;

    /**
     * Adds the given file set to the archive. This method is basically obsoleting {@link #addDirectory(File)},
     * {@link #addDirectory(File, String)}, {@link #addDirectory(File, String[], String[])}, and
     * {@link #addDirectory(File, String, String[], String[])}. However, as these methods are in widespread use, they
     * cannot easily be made deprecated.
     * 
     * @throws ArchiverException
     *             Adding the file set failed.
     * @since 1.0-alpha-9
     */
    void addFileSet( @Nonnull FileSet fileSet )
        throws ArchiverException;

    void addSymlink(String symlinkName, String symlinkDestination)
        throws ArchiverException;

    void addSymlink(String symlinkName, int permissions, String symlinkDestination)
        throws ArchiverException;

    void addFile( @Nonnull File inputFile, @Nonnull String destFileName )
        throws ArchiverException;

    void addFile( @Nonnull File inputFile, @Nonnull String destFileName, int permissions )
        throws ArchiverException;
    /**
     * Obsolete, use {@link #addArchivedFileSet(ArchivedFileSet)}. You can use "inline" of this method in your
     * IDE to get the proper implementation for this release.
     * @deprecated  Will go away in next major version
     */
    void addArchivedFileSet( @Nonnull File archiveFile )
        throws ArchiverException;

    /**
     * Obsolete, use {@link #addArchivedFileSet(ArchivedFileSet)}. You can use "inline" of this method in your
     * IDE to get the proper implementation for this release.
     * @deprecated  Will go away in next major version
     */
    @Deprecated
    void addArchivedFileSet( @Nonnull File archiveFile, String prefix )
        throws ArchiverException;

    /**
     * Obsolete, use {@link #addArchivedFileSet(ArchivedFileSet)}. You can use "inline" of this method in your
     * IDE to get the proper implementation for this release.
     * @deprecated  Will go away in next major version
     */
    void addArchivedFileSet( File archiveFile, String[] includes, String[] excludes )
        throws ArchiverException;

    /**
     * Obsolete, use {@link #addArchivedFileSet(ArchivedFileSet)}. You can use "inline" of this method in your
     * IDE to get the proper implementation for this release.
     * @deprecated  Will go away in next major version
     */
    void addArchivedFileSet( @Nonnull File archiveFile, String prefix, String[] includes, String[] excludes )
        throws ArchiverException;

    /**
     * Adds the given archive file set to the archive. This method is basically obsoleting
     * {@link #addArchivedFileSet(File)}, {@link #addArchivedFileSet(File, String[], String[])}, and
     * {@link #addArchivedFileSet(File, String, String[], String[])}. However, as these methods are in widespread use,
     * they cannot easily be made deprecated.
     *
     * Stream transformers are supported on this method
     * 
     * @since 1.0-alpha-9
     */
    void addArchivedFileSet( ArchivedFileSet fileSet )
        throws ArchiverException;


    /**
     * Adds the given archive file set to the archive. This method is basically obsoleting
     * {@link #addArchivedFileSet(File)}, {@link #addArchivedFileSet(File, String[], String[])}, and
     * {@link #addArchivedFileSet(File, String, String[], String[])}. However, as these methods are in widespread use,
     * they cannot easily be made deprecated.
     *
     * @param charset the encoding to use, particularly useful to specific non-standard filename encodings
     *                 for some kinds of archives (for instance zip files)
     *
     * Stream transformers are supported on this method
     *
     * @param fileSet the fileSet to add
     * @param charset
     * @since 1.0-alpha-9
     */
    void addArchivedFileSet( ArchivedFileSet fileSet, Charset charset )
        throws ArchiverException;

    /**
     * Adds the given resource collection to the archive.
     *
     * Stream transformers are *not* supported on this method
     *
     * @since 1.0-alpha-10
     */
    void addResource( PlexusIoResource resource, String destFileName, int permissions )
        throws ArchiverException;

    /**
     * Adds the given resource collection to the archive.
     *
     * Stream transformers are supported om this method
     *
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

    /**
     * This is the forced mode that should be used regardless if set, otherwise falls back to default.
     * @param mode
     */
    void setDirectoryMode( int mode );

    /**
     * Gets the forced mode for directories, falling back to default if none is forced.
     * @return
     */
    int getDirectoryMode();

    int getOverrideDirectoryMode();

    /**
     * This is the "default" value we should use if no other value is specified
     * @param mode
     */
    void setDefaultDirectoryMode( int mode );

    int getDefaultDirectoryMode();

    boolean getIncludeEmptyDirs();

    void setIncludeEmptyDirs( boolean includeEmptyDirs );

    void setDotFileDirectory( File dotFileDirectory );

    /**
     * Returns an iterator over instances of {@link ArchiveEntry}, which have previously been added by calls to
     * {@link #addResources(PlexusIoResourceCollection)}, {@link #addResource(PlexusIoResource, String, int)},
     * {@link #addFileSet(FileSet)}, etc.
     * 
     * @since 1.0-alpha-10
     */
	@Nonnull
    ResourceIterator getResources()
        throws ArchiverException;

    /**
     * @deprecated Use {@link #getResources()}
     */
    Map<String,ArchiveEntry> getFiles();

    /**
     * <p>
     * Returns, whether recreating the archive is forced (default). Setting this option to false means, that the
     * archiver should compare the timestamps of included files with the timestamp of the target archive and rebuild the
     * archive only, if the latter timestamp precedes the former timestamps. Checking for timestamps will typically
     * offer a performance gain (in particular, if the following steps in a build can be suppressed, if an archive isn't
     * recrated) on the cost that you get inaccurate results from time to time. In particular, removal of source files
     * won't be detected.
     * </p>
     * <p>
     * An archiver doesn't necessarily support checks for uptodate. If so, setting this option to true will simply be
     * ignored. The method {@link #isSupportingForced()} may be called to check whether an archiver does support
     * uptodate checks.
     * </p>
     * 
     * @return True, if the target archive should always be created; false otherwise
     * @see #setForced(boolean)
     * @see #isSupportingForced()
     */
    boolean isForced();

    /**
     * <p>
     * Sets, whether recreating the archive is forced (default). Setting this option to false means, that the archiver
     * should compare the timestamps of included files with the timestamp of the target archive and rebuild the archive
     * only, if the latter timestamp precedes the former timestamps. Checking for timestamps will typically offer a
     * performance gain (in particular, if the following steps in a build can be suppressed, if an archive isn't
     * recrated) on the cost that you get inaccurate results from time to time. In particular, removal of source files
     * won't be detected.
     * </p>
     * <p>
     * An archiver doesn't necessarily support checks for uptodate. If so, setting this option to true will simply be
     * ignored. The method {@link #isSupportingForced()} may be called to check whether an archiver does support
     * uptodate checks.
     * </p>
     * 
     * @param forced
     *            True, if the target archive should always be created; false otherwise
     * @see #isForced()
     * @see #isSupportingForced()
     */
    void setForced( boolean forced );

    /**
     * Returns, whether the archive supports uptodate checks. If so, you may set {@link #setForced(boolean)} to true.
     * 
     * @return True, if the archiver does support uptodate checks, false otherwise
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

    /**
	 * For java7 and above, new java method will be used, regardless of this setting
     * to use or not the jvm method for file permissions : user all <b>not active for group permissions</b>
     * 
     * @since 1.1
     * @param useJvmChmod
     */
    void setUseJvmChmod( boolean useJvmChmod );

    /**
	 * For java7 and above, new java method will be used, regardless of this setting
     * @since 1.1
     * @return
     */
    boolean isUseJvmChmod();

    /**
     * @since 1.1
     */
    boolean isIgnorePermissions();

    /**
     * @since 1.1
     */
    void setIgnorePermissions( final boolean ignorePermissions );
}
