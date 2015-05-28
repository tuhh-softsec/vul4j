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

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.archiver.util.FilterSupport;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.components.io.attributes.Java7Reflector;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.functions.ResourceAttributeSupplier;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoResourceCollection;
import org.codehaus.plexus.components.io.resources.EncodingSupported;
import org.codehaus.plexus.components.io.resources.PlexusIoArchivedResourceCollection;
import org.codehaus.plexus.components.io.resources.PlexusIoFileResourceCollection;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection;
import org.codehaus.plexus.components.io.resources.proxy.PlexusIoProxyResourceCollection;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.Os;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.codehaus.plexus.archiver.util.DefaultArchivedFileSet.archivedFileSet;
import static org.codehaus.plexus.archiver.util.DefaultFileSet.fileSet;

/**
 * @version $Id$
 */
public abstract class AbstractArchiver
    extends AbstractLogEnabled
    implements Archiver, Contextualizable, FilterEnabled, FinalizerEnabled
{

    private Logger logger;

    private File destFile;

    /**
     * A list of the following objects:
     * <ul>
     * <li>Instances of {@link ArchiveEntry}, which are passed back by {@link #getResources()} without modifications.</li>
     * <li>Instances of {@link PlexusIoResourceCollection}, which are converted into an {@link Iterator} over instances
     * of {@link ArchiveEntry} by {@link #getResources()}.
     * </ul>
     */
    private final List<Object> resources = new ArrayList<Object>();

    private boolean includeEmptyDirs = true;

    private int forcedFileMode = -1; // Will always be used

    private int forcedDirectoryMode = -1; // Will always be used

    private int defaultFileMode = -1; // Optionally used if a value is needed

    private int defaultDirectoryMode = -1; // Optionally used if a value is needed

    private boolean forced = true;

    private FilterSupport filterSupport;

    private List<ArchiveFinalizer> finalizers;

    private File dotFileDirectory;

    private String duplicateBehavior = Archiver.DUPLICATES_SKIP;

    // On lunix-like systems, we replace windows backslashes with forward slashes
    private final boolean replacePathSlashesToJavaPaths = File.separatorChar == '/';

    private final List<Closeable> closeables = new ArrayList<Closeable>(  );
    /**
     * since 2.2 is on by default
     *
     * @since 1.1
     */
    private boolean useJvmChmod = true;

    // contextualized.
    private ArchiverManager archiverManager;

    /**
     * @since 1.1
     */
    private boolean ignorePermissions = false;

    public String getDuplicateBehavior()
    {
        return duplicateBehavior;
    }

    public void setDuplicateBehavior( final String duplicate )
    {
        if ( !Archiver.DUPLICATES_VALID_BEHAVIORS.contains( duplicate ) )
        {
            throw new IllegalArgumentException(
                "Invalid duplicate-file behavior: \'" + duplicate + "\'. Please specify one of: "
                    + Archiver.DUPLICATES_VALID_BEHAVIORS );
        }

        duplicateBehavior = duplicate;
    }

    public final void setFileMode( final int mode )
    {
        if ( mode >= 0 )
        {
            forcedFileMode = ( mode & UnixStat.PERM_MASK ) | UnixStat.FILE_FLAG;
        }
        else
        {
            forcedFileMode = -1;
        }
    }

    public final void setDefaultFileMode( final int mode )
    {
        defaultFileMode = ( mode & UnixStat.PERM_MASK ) | UnixStat.FILE_FLAG;
    }

    public final int getOverrideFileMode()
    {
        return forcedFileMode;
    }

    public final int getFileMode()
    {
        if ( forcedFileMode < 0 )
        {
            if ( defaultFileMode < 0 )
            {
                return DEFAULT_FILE_MODE;
            }

            return defaultFileMode;
        }

        return forcedFileMode;
    }

    public final int getDefaultFileMode()
    {
        return defaultFileMode;
    }

    /**
     * @deprecated Use {@link Archiver#getDefaultFileMode()}.
     */
    public final int getRawDefaultFileMode()
    {
        return getDefaultFileMode();
    }

    public final void setDirectoryMode( final int mode )
    {
        if ( mode >= 0 )
        {
            forcedDirectoryMode = ( mode & UnixStat.PERM_MASK ) | UnixStat.DIR_FLAG;
        }
        else
        {
            forcedDirectoryMode = -1;
        }
    }

    public final void setDefaultDirectoryMode( final int mode )
    {
        defaultDirectoryMode = ( mode & UnixStat.PERM_MASK ) | UnixStat.DIR_FLAG;
    }

    public final int getOverrideDirectoryMode()
    {
        return forcedDirectoryMode;
    }

    public final int getDirectoryMode()
    {
        if ( forcedDirectoryMode < 0 )
        {
            if ( defaultDirectoryMode < 0 )
            {
                return DEFAULT_DIR_MODE;
            }

            return defaultDirectoryMode;
        }

        return forcedDirectoryMode;
    }

    public final int getDefaultDirectoryMode()
    {
        if ( defaultDirectoryMode < 0 )
        {
            return DEFAULT_DIR_MODE;
        }
        else
        {
            return defaultDirectoryMode;
        }
    }

    /**
     * @deprecated Use {@link Archiver#getDefaultDirectoryMode()}.
     */
    public final int getRawDefaultDirectoryMode()
    {
        return getDefaultDirectoryMode();
    }

    public boolean getIncludeEmptyDirs()
    {
        return includeEmptyDirs;
    }

    public void setIncludeEmptyDirs( final boolean includeEmptyDirs )
    {
        this.includeEmptyDirs = includeEmptyDirs;
    }

    public void addDirectory( @Nonnull final File directory )
        throws ArchiverException
    {
        addFileSet(
            fileSet( directory ).prefixed( "" ).includeExclude( null, null ).includeEmptyDirs( includeEmptyDirs ) );
    }

    public void addDirectory( @Nonnull final File directory, final String prefix )
        throws ArchiverException
    {
        addFileSet(
            fileSet( directory ).prefixed( prefix ).includeExclude( null, null ).includeEmptyDirs( includeEmptyDirs ) );
    }

    public void addDirectory( @Nonnull final File directory, final String[] includes, final String[] excludes )
        throws ArchiverException
    {
        addFileSet( fileSet( directory ).prefixed( "" ).includeExclude( includes, excludes ).includeEmptyDirs(
            includeEmptyDirs ) );
    }

    public void addDirectory( @Nonnull final File directory, final String prefix, final String[] includes,
                              final String[] excludes )
        throws ArchiverException
    {
        addFileSet( fileSet( directory ).prefixed( prefix ).includeExclude( includes, excludes ).includeEmptyDirs(
            includeEmptyDirs ) );
    }

    public void addFileSet( @Nonnull final FileSet fileSet )
        throws ArchiverException
    {
        final File directory = fileSet.getDirectory();
        if ( directory == null )
        {
            throw new ArchiverException( "The file sets base directory is null." );
        }

        if ( !directory.isDirectory() )
        {
            throw new ArchiverException( directory.getAbsolutePath() + " isn't a directory." );
        }

        // The PlexusIoFileResourceCollection contains platform-specific File.separatorChar which
        // is an interesting cause of grief, see PLXCOMP-192
        final PlexusIoFileResourceCollection collection = new PlexusIoFileResourceCollection();
        collection.setFollowingSymLinks( !isSymlinkSupported() );

        collection.setIncludes( fileSet.getIncludes() );
        collection.setExcludes( fileSet.getExcludes() );
        collection.setBaseDir( directory );
        collection.setFileSelectors( fileSet.getFileSelectors() );
        collection.setIncludingEmptyDirectories( fileSet.isIncludingEmptyDirectories() );
        collection.setPrefix( fileSet.getPrefix() );
        collection.setCaseSensitive( fileSet.isCaseSensitive() );
        collection.setUsingDefaultExcludes( fileSet.isUsingDefaultExcludes() );
        collection.setStreamTransformer( fileSet.getStreamTransformer() );

        if ( getOverrideDirectoryMode() > -1 || getOverrideFileMode() > -1 )
        {
            collection.setOverrideAttributes( -1, null, -1, null, getOverrideFileMode(), getOverrideDirectoryMode() );
        }

        if ( getDefaultDirectoryMode() > -1 || getDefaultFileMode() > -1 )
        {
            collection.setDefaultAttributes( -1, null, -1, null, getDefaultFileMode(), getDefaultDirectoryMode() );
        }

        addResources( collection );
    }

    private boolean isSymlinkSupported()
    {
        return Os.isFamily( Os.FAMILY_UNIX ) && Java7Reflector.isAtLeastJava7();
    }

    public void addFile( @Nonnull final File inputFile, @Nonnull final String destFileName )
        throws ArchiverException
    {
        final int fileMode = getOverrideFileMode();

        addFile( inputFile, destFileName, fileMode );
    }

    public void addSymlink( String symlinkName, String symlinkDestination )
        throws ArchiverException
    {
        final int fileMode = getOverrideFileMode();

        addSymlink( symlinkName, fileMode, symlinkDestination );
    }

    public void addSymlink( String symlinkName, int permissions, String symlinkDestination )
        throws ArchiverException
    {
        doAddResource( ArchiveEntry.createSymlinkEntry( symlinkName, permissions, symlinkDestination,
                                                        getDirectoryMode() ) );
    }

    protected ArchiveEntry asArchiveEntry( @Nonnull final PlexusIoResource resource, final String destFileName,
                                           final int permissions, PlexusIoResourceCollection collection )
        throws ArchiverException
    {
        if ( !resource.isExisting() )
        {
            throw new ArchiverException( resource.getName() + " not found." );
        }

        if ( resource.isFile() )
        {
            return ArchiveEntry.createFileEntry( destFileName, resource, permissions, collection,
                                                 getDirectoryMode() );
        }
        else
        {
            return ArchiveEntry.createDirectoryEntry( destFileName, resource, permissions, getDirectoryMode() );
        }
    }

    protected ArchiveEntry asArchiveEntry( final PlexusIoResourceCollection collection,
                                           final PlexusIoResource resource )
        throws ArchiverException
    {
        final String destFileName = collection.getName( resource );

        int permissions = -1;
        if ( resource instanceof ResourceAttributeSupplier)
        {
            final PlexusIoResourceAttributes attrs = ( (ResourceAttributeSupplier) resource ).getAttributes();

            if ( attrs != null )
            {
                permissions = attrs.getOctalMode();
            }
        }

        return asArchiveEntry( resource, destFileName, permissions, collection );
    }

    public void addResource( final PlexusIoResource resource, final String destFileName, final int permissions )
        throws ArchiverException
    {
        doAddResource( asArchiveEntry( resource, destFileName, permissions, null ) );
    }

    public void addFile( @Nonnull final File inputFile, @Nonnull String destFileName, int permissions )
        throws ArchiverException
    {
        if ( !inputFile.isFile() || !inputFile.exists() )
        {
            throw new ArchiverException( inputFile.getAbsolutePath() + " isn't a file." );
        }

        InputStream in = null;

        if ( replacePathSlashesToJavaPaths )
        {
            destFileName = destFileName.replace( '\\', '/' );
        }

        if ( permissions < 0 )
        {
            permissions = getOverrideFileMode();
        }

        try
        {
            // do a null check here, to avoid creating a file stream if there are no filters...
            if ( filterSupport != null )
            {
                in = new FileInputStream( inputFile );

                if ( include( in, destFileName ) )
                {
                    doAddResource(
                        ArchiveEntry.createFileEntry( destFileName, inputFile, permissions, getDirectoryMode() ) );
                }
            }
            else
            {
                doAddResource(
                    ArchiveEntry.createFileEntry( destFileName, inputFile, permissions, getDirectoryMode() ) );
            }
        }
        catch ( final IOException e )
        {
            throw new ArchiverException( "Failed to determine inclusion status for: " + inputFile, e );
        }
        catch ( final ArchiveFilterException e )
        {
            throw new ArchiverException( "Failed to determine inclusion status for: " + inputFile, e );
        }
        finally
        {
            IOUtil.close( in );
        }
    }

    @Nonnull
    public ResourceIterator getResources()
        throws ArchiverException
    {
        return new ResourceIterator()
        {
            private final Iterator addedResourceIter = resources.iterator();

            private PlexusIoResourceCollection currentResourceCollection;

            private Iterator ioResourceIter;

            private ArchiveEntry nextEntry;

            private final Set<String> seenEntries = new HashSet<String>();

            public boolean hasNext()
            {
                do
                {
                    if ( nextEntry == null )
                    {
                        if ( ioResourceIter == null )
                        {
                            if ( addedResourceIter.hasNext() )
                            {
                                final Object o = addedResourceIter.next();
                                if ( o instanceof ArchiveEntry )
                                {
                                    nextEntry = (ArchiveEntry) o;
                                }
                                else if ( o instanceof PlexusIoResourceCollection )
                                {
                                    currentResourceCollection = (PlexusIoResourceCollection) o;

                                    try
                                    {
                                        ioResourceIter = currentResourceCollection.getResources();
                                    }
                                    catch ( final IOException e )
                                    {
                                        throw new ArchiverException( e.getMessage(), e );
                                    }
                                }
                                else
                                {
                                    return throwIllegalResourceType( o );
                                }
                            }
                            else
                            {
                                nextEntry = null;
                            }
                        }
                        else
                        {
                            if ( ioResourceIter.hasNext() )
                            {
                                final PlexusIoResource resource = (PlexusIoResource) ioResourceIter.next();
                                nextEntry = asArchiveEntry( currentResourceCollection, resource );
                            }
                            else
                            {
                                // this will leak handles in the IO iterator if the iterator is not fully consumed.
                                // alternately we'd have to make this method return a Closeable iterator back
                                // to the client and ditch the whole issue onto the client.
                                // this does not really make any sense either, might equally well change the
                                // api into something that is not broken by design.
                                addCloseable( ioResourceIter );
                                ioResourceIter = null;
                            }
                        }
                    }

                    if ( nextEntry != null && seenEntries.contains( normalizedForDuplicateCheck(nextEntry) ))
                    {
                        final String path = nextEntry.getName();

                        if ( Archiver.DUPLICATES_PRESERVE.equals( duplicateBehavior )
                            || Archiver.DUPLICATES_SKIP.equals( duplicateBehavior ) )
                        {
                            if ( nextEntry.getType() == ArchiveEntry.FILE )
                            {
                                getLogger().debug( path + " already added, skipping" );
                            }

                            nextEntry = null;
                        }
                        else if ( Archiver.DUPLICATES_FAIL.equals( duplicateBehavior ) )
                        {
                            throw new ArchiverException(
                                "Duplicate file " + path + " was found and the duplicate " + "attribute is 'fail'." );
                        }
                        else
                        {
                            // duplicate equal to add, so we continue
                            getLogger().debug( "duplicate file " + path + " found, adding." );
                        }
                    }
                }
                while ( nextEntry == null && !( ioResourceIter == null && !addedResourceIter.hasNext() ) );

                return nextEntry != null;
            }

            private boolean throwIllegalResourceType( Object o )
            {
                throw new IllegalStateException(
                    "An invalid resource of type: " + o.getClass().getName() + " was added to archiver: "
                        + getClass().getName() );
            }

            public ArchiveEntry next()
            {
                if ( !hasNext() )
                {
                    throw new NoSuchElementException();
                }

                final ArchiveEntry next = nextEntry;
                nextEntry = null;

                seenEntries.add( normalizedForDuplicateCheck(next) );

                return next;
            }

            public void remove()
            {
                throw new UnsupportedOperationException( "Does not support iterator" );
            }

            private String normalizedForDuplicateCheck(ArchiveEntry entry){
                return entry.getName().replace( '\\', '/'  );
            }

        };

    }

    private static void closeIfCloseable( Object resource )
        throws IOException
    {
        if ( resource == null )
        {
            return;
        }
        if ( resource instanceof Closeable )
        {
            ( (Closeable) resource ).close();
        }

    }

    private static void closeQuietlyIfCloseable( Object resource )
    {
        try
        {
            closeIfCloseable( resource );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }


    public Map<String, ArchiveEntry> getFiles()
    {
        try
        {
            final Map<String, ArchiveEntry> map = new HashMap<String, ArchiveEntry>();
            for ( final ResourceIterator iter = getResources(); iter.hasNext(); )
            {
                final ArchiveEntry entry = iter.next();
                if ( includeEmptyDirs || entry.getType() == ArchiveEntry.FILE )
                {
                    map.put( entry.getName(), entry );
                }
            }
            return map;
        }
        catch ( final ArchiverException e )
        {
            throw new UndeclaredThrowableException( e );
        }
    }

    public File getDestFile()
    {
        return destFile;
    }

    public void setDestFile( final File destFile )
    {
        this.destFile = destFile;

        if ( destFile != null )
        {
            destFile.getParentFile().mkdirs();
        }
    }

    protected Logger getLogger()
    {
        if ( logger == null )
        {
            if ( super.getLogger() != null )
            {
                logger = super.getLogger();
            }
            else
            {
                logger = new ConsoleLogger( Logger.LEVEL_INFO, "console" );
            }
        }

        return logger;
    }

    public Map getDirs()
    {
        try
        {
            final Map map = new HashMap();
            for ( final ResourceIterator iter = getResources(); iter.hasNext(); )
            {
                final ArchiveEntry entry = iter.next();
                if ( entry.getType() == ArchiveEntry.DIRECTORY )
                {
                    map.put( entry.getName(), entry );
                }
            }
            return map;
        }
        catch ( final ArchiverException e )
        {
            throw new UndeclaredThrowableException( e );
        }
    }

    protected PlexusIoResourceCollection asResourceCollection( final ArchivedFileSet fileSet, Charset charset )
        throws ArchiverException
    {
        final File archiveFile = fileSet.getArchive();

        final PlexusIoResourceCollection resources;
        try
        {
            resources = archiverManager.getResourceCollection( archiveFile );
        }
        catch ( final NoSuchArchiverException e )
        {
            throw new ArchiverException(
                "Error adding archived file-set. PlexusIoResourceCollection not found for: " + archiveFile, e );
        }

        if (resources instanceof EncodingSupported ) {
            ((EncodingSupported)resources).setEncoding( charset );
        }

        if ( resources instanceof PlexusIoArchivedResourceCollection )
        {
            ( (PlexusIoArchivedResourceCollection) resources ).setFile( fileSet.getArchive() );
        }
        else
        {
            throw new ArchiverException( "Expected " + PlexusIoArchivedResourceCollection.class.getName() + ", got "
                                             + resources.getClass().getName() );
        }

        if ( resources instanceof AbstractPlexusIoResourceCollection )
        {
            ( (AbstractPlexusIoResourceCollection) resources ).setStreamTransformer( fileSet.getStreamTransformer() );
        }
        final PlexusIoProxyResourceCollection proxy = new PlexusIoProxyResourceCollection( resources );

        proxy.setExcludes( fileSet.getExcludes() );
        proxy.setIncludes( fileSet.getIncludes() );
        proxy.setIncludingEmptyDirectories( fileSet.isIncludingEmptyDirectories() );
        proxy.setCaseSensitive( fileSet.isCaseSensitive() );
        proxy.setPrefix( fileSet.getPrefix() );
        proxy.setUsingDefaultExcludes( fileSet.isUsingDefaultExcludes() );
        proxy.setFileSelectors( fileSet.getFileSelectors() );
        proxy.setStreamTransformer( fileSet.getStreamTransformer() );

        if ( getOverrideDirectoryMode() > -1 || getOverrideFileMode() > -1 )
        {
            proxy.setOverrideAttributes( -1, null, -1, null, getOverrideFileMode(), getOverrideDirectoryMode() );
        }

        if ( getDefaultDirectoryMode() > -1 || getDefaultFileMode() > -1 )
        {
            proxy.setDefaultAttributes( -1, null, -1, null, getDefaultFileMode(), getDefaultDirectoryMode() );
        }

        return proxy;
    }

    /**
     * Adds a resource collection to the archive.
     */
    public void addResources( final PlexusIoResourceCollection collection )
        throws ArchiverException
    {
        doAddResource( collection );
    }

    private void doAddResource(Object item){
        resources.add( item );
    }

    public void addArchivedFileSet( final ArchivedFileSet fileSet )
        throws ArchiverException
    {
        final PlexusIoResourceCollection resourceCollection = asResourceCollection( fileSet, null );
        addResources( resourceCollection );
    }

    public void addArchivedFileSet( final ArchivedFileSet fileSet, Charset charset )
        throws ArchiverException
    {
        final PlexusIoResourceCollection resourceCollection = asResourceCollection( fileSet, charset );
        addResources( resourceCollection );
    }

    /**
     * @since 1.0-alpha-7
     */
    public void addArchivedFileSet( @Nonnull final File archiveFile, final String prefix, final String[] includes,
                                    final String[] excludes )
        throws ArchiverException
    {
        addArchivedFileSet(
            archivedFileSet( archiveFile ).prefixed( prefix ).includeExclude( includes, excludes ).includeEmptyDirs(
                includeEmptyDirs ) );
    }

    /**
     * @since 1.0-alpha-7
     */
    public void addArchivedFileSet( @Nonnull final File archiveFile, final String prefix )
        throws ArchiverException
    {
        addArchivedFileSet( archivedFileSet( archiveFile ).prefixed( prefix ).includeEmptyDirs( includeEmptyDirs ) );
    }

    /**
     * @since 1.0-alpha-7
     */
    public void addArchivedFileSet( @Nonnull final File archiveFile, final String[] includes, final String[] excludes )
        throws ArchiverException
    {
        addArchivedFileSet(
            archivedFileSet( archiveFile ).includeExclude( includes, excludes ).includeEmptyDirs( includeEmptyDirs ) );
    }

    /**
     * @since 1.0-alpha-7
     */
    public void addArchivedFileSet( @Nonnull final File archiveFile )
        throws ArchiverException
    {
        addArchivedFileSet( archivedFileSet( archiveFile ).includeEmptyDirs( includeEmptyDirs ) );
    }

    /**
     * Allows us to pull the ArchiverManager instance out of the container without causing a chicken-and-egg
     * instantiation/composition problem.
     */
    public void contextualize( final Context context )
        throws ContextException
    {
        final PlexusContainer container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );

        try
        {
            archiverManager = (ArchiverManager) container.lookup( ArchiverManager.ROLE );
        }
        catch ( final ComponentLookupException e )
        {
            throw new ContextException( "Error retrieving ArchiverManager instance: " + e.getMessage(), e );
        }
    }

    public boolean isForced()
    {
        return forced;
    }

    public void setForced( final boolean forced )
    {
        this.forced = forced;
    }

    public void setArchiveFilters( final List filters )
    {
        filterSupport = new FilterSupport( filters, getLogger() );
    }

    public void addArchiveFinalizer( final ArchiveFinalizer finalizer )
    {
        if ( finalizers == null )
        {
            finalizers = new ArrayList<ArchiveFinalizer>();
        }

        finalizers.add( finalizer );
    }

    public void setArchiveFinalizers( final List<ArchiveFinalizer> archiveFinalizers )
    {
        finalizers = archiveFinalizers;
    }

    public void setDotFileDirectory( final File dotFileDirectory )
    {
        this.dotFileDirectory = dotFileDirectory;
    }

    protected boolean isUptodate()
        throws ArchiverException
    {
        final File zipFile = getDestFile();
        final long destTimestamp = zipFile.lastModified();
        if ( destTimestamp == 0 )
        {
            getLogger().debug( "isUp2date: false (Destination " + zipFile.getPath() + " not found.)" );
            return false; // File doesn't yet exist
        }

        final Iterator it = resources.iterator();
        if ( !it.hasNext() )
        {
            getLogger().debug( "isUp2date: false (No input files.)" );
            return false; // No timestamp to compare
        }

        while ( it.hasNext() )
        {
            final Object o = it.next();
            final long l;
            if ( o instanceof ArchiveEntry )
            {
                l = ( (ArchiveEntry) o ).getResource().getLastModified();
            }
            else if ( o instanceof PlexusIoResourceCollection )
            {
                try
                {
                    l = ( (PlexusIoResourceCollection) o ).getLastModified();
                }
                catch ( final IOException e )
                {
                    throw new ArchiverException( e.getMessage(), e );
                }
            }
            else
            {
                throw new IllegalStateException( "Invalid object type: " + o.getClass().getName() );
            }
            if ( l == PlexusIoResource.UNKNOWN_MODIFICATION_DATE )
            {
                // Don't know what to do. Safe thing is to assume not up2date.
                getLogger().debug( "isUp2date: false (Resource with unknown modification date found.)" );
                return false;
            }
            if ( l > destTimestamp )
            {
                getLogger().debug( "isUp2date: false (Resource with newer modification date found.)" );
                return false;
            }
        }

        getLogger().debug( "isUp2date: true" );
        return true;
    }

    protected boolean checkForced()
        throws ArchiverException
    {
        if ( !isForced() && isSupportingForced() && isUptodate() )
        {
            getLogger().debug( "Archive " + getDestFile() + " is uptodate." );
            return false;
        }
        return true;
    }

    public boolean isSupportingForced()
    {
        return false;
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
            for ( final ArchiveFinalizer finalizer : finalizers )
            {
                finalizer.finalizeArchiveCreation( this );
            }
        }
    }

    private boolean include( final InputStream in, final String path )
        throws ArchiveFilterException
    {
        return ( filterSupport == null ) || filterSupport.include( in, path );
    }

    public final void createArchive()
        throws ArchiverException, IOException
    {
        validate();
        try
        {
            try
            {
                if ( dotFileDirectory != null )
                {
                    addArchiveFinalizer( new DotDirectiveArchiveFinalizer( dotFileDirectory ) );
                }

                runArchiveFinalizers();

                execute();
            }
            finally
            {
                close();
            }
        }
        catch ( final IOException e )
        {
            String msg = "Problem creating " + getArchiveType() + ": " + e.getMessage();

            final StringBuffer revertBuffer = new StringBuffer();
            if ( !revert( revertBuffer ) )
            {
                msg += revertBuffer.toString();
            }

            throw new ArchiverException( msg, e );
        }
        finally
        {
            cleanUp();
        }
    }

    protected boolean hasVirtualFiles()
    {
        if ( finalizers != null )
        {
            for ( final ArchiveFinalizer finalizer : finalizers )
            {
                final List virtualFiles = finalizer.getVirtualFiles();

                if ( ( virtualFiles != null ) && !virtualFiles.isEmpty() )
                {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean revert( final StringBuffer messageBuffer )
    {
        return true;
    }

    protected void validate()
        throws ArchiverException, IOException
    {
    }

    protected abstract String getArchiveType();

    private void addCloseable(Object maybeCloseable){
        if (maybeCloseable instanceof  Closeable)
            closeables.add( (Closeable) maybeCloseable );

    }
    private void closeIterators()
    {
        for ( Closeable closeable : closeables )
        {
            closeQuietlyIfCloseable( closeable );
        }

    }
    protected abstract void close()
        throws IOException;

    protected void cleanUp()
        throws IOException
    {
        closeIterators();

        for ( Object resource : resources )
        {
            if ( resource instanceof PlexusIoProxyResourceCollection )
            {
                resource = ( (PlexusIoProxyResourceCollection) resource ).getSrc();
            }

            closeIfCloseable( resource );
        }
        resources.clear();
    }


    protected abstract void execute()
        throws ArchiverException, IOException;

    /**
     * @since 1.1
     */
    public boolean isUseJvmChmod()
    {
        return useJvmChmod;
    }

    /**
     * @since 1.1
     */
    public void setUseJvmChmod( final boolean useJvmChmod )
    {
        this.useJvmChmod = useJvmChmod;
    }

    /**
     * @since 1.1
     */
    public boolean isIgnorePermissions()
    {
        return ignorePermissions;
    }

    /**
     * @since 1.1
     */
    public void setIgnorePermissions( final boolean ignorePermissions )
    {
        this.ignorePermissions = ignorePermissions;
    }

}
