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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.archiver.util.DefaultArchivedFileSet;
import org.codehaus.plexus.archiver.util.DefaultFileSet;
import org.codehaus.plexus.archiver.util.FilterSupport;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.resources.PlexusIoArchivedResourceCollection;
import org.codehaus.plexus.components.io.resources.PlexusIoFileResourceCollection;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceWithAttributes;
import org.codehaus.plexus.components.io.resources.proxy.PlexusIoProxyResourceCollection;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.util.IOUtil;


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
     *   <li>Instances of {@link ArchiveEntry}, which are passed back by
     *     {@link #getResources()} without modifications.</li>
     *   <li>Instances of {@link PlexusIoResourceCollection}, which
     *     are converted into an {@link Iterator} over instances of
     *     {@link ArchiveEntry} by {@link #getResources()}.
     * </ul>
     */
    private List resources = new ArrayList();

    private boolean includeEmptyDirs = true;

    private int fileMode = -1;

    private int directoryMode = -1;

    private int defaultFileMode = -1;

    private int defaultDirectoryMode = -1;

    private boolean forced = true;

    private FilterSupport filterSupport;

    private List finalizers;

    private File dotFileDirectory;

    private String duplicateBehavior = Archiver.DUPLICATES_SKIP;

    // contextualized.
    private ArchiverManager archiverManager;

    public String getDuplicateBehavior()
    {
        return duplicateBehavior;
    }

    public void setDuplicateBehavior( String duplicate )
    {
        if ( !Archiver.DUPLICATES_VALID_BEHAVIORS.contains( duplicate ) )
        {
            throw new IllegalArgumentException( "Invalid duplicate-file behavior: \'" + duplicate
                + "\'. Please specify one of: " + Archiver.DUPLICATES_VALID_BEHAVIORS );
        }
        
        this.duplicateBehavior = duplicate;
    }

    public final void setFileMode( int mode )
    {
        fileMode = ( mode & UnixStat.PERM_MASK ) | UnixStat.FILE_FLAG;
    }
    
    public final void setDefaultFileMode( int mode )
    {
        defaultFileMode = ( mode & UnixStat.PERM_MASK ) | UnixStat.FILE_FLAG;
    }
    
    public final int getOverrideFileMode()
    {
        return fileMode;
    }
    
    public final int getFileMode()
    {
        if ( fileMode < 0 )
        {
            if ( defaultFileMode < 0 )
            {
                return DEFAULT_FILE_MODE;
            }
            
            return defaultFileMode;
        }
        
        return fileMode;
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
    
    public final void setDirectoryMode( int mode )
    {
        directoryMode = ( mode & UnixStat.PERM_MASK ) | UnixStat.DIR_FLAG;
    }
    
    public final void setDefaultDirectoryMode( int mode )
    {
        defaultDirectoryMode = ( mode & UnixStat.PERM_MASK ) | UnixStat.DIR_FLAG;
    }
    
    public final int getOverrideDirectoryMode()
    {
        return directoryMode;
    }
    
    public final int getDirectoryMode()
    {
        if ( directoryMode < 0 )
        {
            if ( defaultDirectoryMode < 0 )
            {
                return DEFAULT_DIR_MODE;
            }
            
            return defaultDirectoryMode;
        }
        
        return directoryMode;
    }

    public final int getDefaultDirectoryMode()
    {
        return defaultDirectoryMode;
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

    public void setIncludeEmptyDirs( boolean includeEmptyDirs )
    {
        this.includeEmptyDirs = includeEmptyDirs;
    }

    public void addDirectory( File directory )
        throws ArchiverException
    {
        addDirectory( directory, "" );
    }

    public void addDirectory( File directory, String prefix )
        throws ArchiverException
    {
        addDirectory( directory, prefix, null, null );
    }

    public void addDirectory( File directory, String[] includes, String[] excludes )
        throws ArchiverException
    {
        addDirectory( directory, "", includes, excludes );
    }

    public void addDirectory( File directory, String prefix, String[] includes, String[] excludes )
        throws ArchiverException
    {
        DefaultFileSet fileSet = new DefaultFileSet();
        fileSet.setDirectory( directory );
        fileSet.setPrefix( prefix );
        fileSet.setIncludes( includes );
        fileSet.setExcludes( excludes );
        fileSet.setIncludingEmptyDirectories( includeEmptyDirs );
        addFileSet( fileSet );
    }

    public void addFileSet( FileSet fileSet )
        throws ArchiverException
    {
        File directory = fileSet.getDirectory();
        if ( directory == null )
        {
            throw new ArchiverException( "The file sets base directory is null." );
        }
        
        if ( !directory.isDirectory() )
        {
            throw new ArchiverException( directory.getAbsolutePath() + " isn't a directory." );
        }
        
        PlexusIoFileResourceCollection collection = new PlexusIoFileResourceCollection( getLogger() );
        
        collection.setIncludes( fileSet.getIncludes() );
        collection.setExcludes( fileSet.getExcludes() );
        collection.setBaseDir( directory );
        collection.setFileSelectors( fileSet.getFileSelectors() );
        collection.setIncludingEmptyDirectories( fileSet.isIncludingEmptyDirectories() );
        collection.setPrefix( fileSet.getPrefix() );
        collection.setCaseSensitive( fileSet.isCaseSensitive() );
        collection.setUsingDefaultExcludes( fileSet.isUsingDefaultExcludes() );
        
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

    public void addFile( File inputFile, String destFileName )
        throws ArchiverException
    {
        int fileMode = getOverrideFileMode();
        
        addFile( inputFile, destFileName, fileMode );
    }

    protected ArchiveEntry asArchiveEntry( PlexusIoResource resource, String destFileName, int permissions )
        throws ArchiverException
    {
        if ( !resource.isExisting() )
        {
            throw new ArchiverException( resource.getName() + " not found." );
        }
        
        if ( resource.isFile() )
        {
            return ArchiveEntry.createFileEntry( destFileName, resource, permissions );
        }
        else
        {
            return ArchiveEntry.createDirectoryEntry( destFileName, resource, permissions );
        }
    }

    protected ArchiveEntry asArchiveEntry( PlexusIoResourceCollection collection, PlexusIoResource resource )
        throws ArchiverException
    {
        try
        {
            final String destFileName = collection.getName( resource );
            
            int permissions = -1;
            if ( resource instanceof PlexusIoResourceWithAttributes )
            {
                PlexusIoResourceAttributes attrs = ((PlexusIoResourceWithAttributes) resource ).getAttributes();
                
                if ( attrs != null )
                {
                    permissions = attrs.getOctalMode();
                }
            }
            
            return asArchiveEntry( resource, destFileName, permissions );
        }
        catch ( IOException e )
        {
            throw new ArchiverException( e.getMessage(), e );
        }
    }
    
    public void addResource( PlexusIoResource resource, String destFileName, int permissions )
        throws ArchiverException
    {
        resources.add( asArchiveEntry( resource, destFileName, permissions ) );
    }
    
    public void addFile( File inputFile, String destFileName, int permissions )
        throws ArchiverException
    {
        if ( !inputFile.isFile() || !inputFile.exists() )
        {
            throw new ArchiverException( inputFile.getAbsolutePath() + " isn't a file." );
        }

        FileInputStream fileStream = null;

        destFileName = destFileName.replace( '\\', '/' );

        if ( permissions < 0 )
        {
            permissions = getOverrideFileMode();
        }
        
        try
        {
            // do a null check here, to avoid creating a file stream if there are no filters...
            if ( filterSupport != null )
            {
                fileStream = new FileInputStream( inputFile );

                if ( include( fileStream, destFileName ) )
                {
                    resources.add( ArchiveEntry.createFileEntry( destFileName, inputFile, permissions ) );
                }
            }
            else
            {
                resources.add( ArchiveEntry.createFileEntry( destFileName, inputFile, permissions ) );
            }
        }
        catch ( IOException e )
        {
            throw new ArchiverException( "Failed to determine inclusion status for: " + inputFile, e );
        }
        catch ( ArchiveFilterException e )
        {
            throw new ArchiverException( "Failed to determine inclusion status for: " + inputFile, e );
        }
        finally
        {
            IOUtil.close( fileStream );
        }
    }

    public ResourceIterator getResources()
        throws ArchiverException
    {
        return new ResourceIterator()
        {
            private final Iterator addedResourceIter = resources.iterator();
            private PlexusIoResourceCollection currentResourceCollection;
            private Iterator ioResourceIter;
            private ArchiveEntry nextEntry;
            
            private Set seenEntries = new HashSet();

            public boolean hasNext()
                throws ArchiverException
            {
                if ( nextEntry == null )
                {
                    if ( ioResourceIter == null )
                    {
                        if ( addedResourceIter.hasNext() )
                        {
                            Object o = addedResourceIter.next();
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
                                catch ( IOException e )
                                {
                                    throw new ArchiverException( e.getMessage(), e );
                                }
                                
                                return hasNext();
                            }
                            else
                            {
                                throw new IllegalStateException( "An invalid resource of type: "
                                    + o.getClass().getName() + " was added to archiver: " + this.getClass().getName() );
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
                            PlexusIoResource resource = (PlexusIoResource) ioResourceIter.next();
                            nextEntry = asArchiveEntry( currentResourceCollection, resource );
                        }
                        else
                        {
                            ioResourceIter = null;
                            return hasNext();
                        }
                    }
                }
                
                if ( nextEntry != null && seenEntries.contains( nextEntry.getName() ) )
                {
                    String path = nextEntry.getName();
                    
                    if ( Archiver.DUPLICATES_PRESERVE.equals( duplicateBehavior ) || Archiver.DUPLICATES_SKIP.equals( duplicateBehavior ) )
                    {
                        getLogger().info( path + " already added, skipping" );
                        
                        nextEntry = null;
                        return hasNext();
                    }
                    else if ( Archiver.DUPLICATES_FAIL.equals( duplicateBehavior ) )
                    {
                        throw new ArchiverException( "Duplicate file " + path + " was found and the duplicate "
                            + "attribute is 'fail'." );
                    }
                    else
                    {
                        // duplicate equal to add, so we continue
                        getLogger().debug( "duplicate file " + path + " found, adding." );
                    }
                }
                
                return nextEntry != null;
            }

            public ArchiveEntry next()
                throws ArchiverException
            {
                if ( !hasNext() )
                {
                    throw new NoSuchElementException();
                }
                
                ArchiveEntry next = nextEntry;
                nextEntry = null;
                
                seenEntries.add( next.getName() );
                
                return next;
            }
        };
    }

    public Map getFiles()
    {
        try
        {
            final Map map = new HashMap();
            for ( ResourceIterator iter = getResources();  iter.hasNext();  )
            {
                ArchiveEntry entry = (ArchiveEntry) iter.next();
                if ( includeEmptyDirs  ||  entry.getType() == ArchiveEntry.FILE )
                {
                    map.put( entry.getName(), entry );
                }
            }
            return map;
        }
        catch ( ArchiverException e )
        {
            throw new UndeclaredThrowableException( e );
        }
    }

    public File getDestFile()
    {
        return destFile;
    }

    public void setDestFile( File destFile )
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
            for ( ResourceIterator iter = getResources();  iter.hasNext();  )
            {
                ArchiveEntry entry = (ArchiveEntry) iter.next();
                if ( entry.getType() == ArchiveEntry.DIRECTORY )
                {
                    map.put( entry.getName(), entry );
                }
            }
            return map;
        }
        catch ( ArchiverException e )
        {
            throw new UndeclaredThrowableException( e );
        }
    }

    protected PlexusIoResourceCollection asResourceCollection( ArchivedFileSet fileSet )
        throws ArchiverException
    {
        File archiveFile = fileSet.getArchive();

        final PlexusIoResourceCollection resources;
        try
        {
            resources = archiverManager.getResourceCollection( archiveFile );
        }
        catch ( NoSuchArchiverException e )
        {
            throw new ArchiverException( "Error adding archived file-set. PlexusIoResourceCollection not found for: " + archiveFile, e );
        }

        if ( resources instanceof PlexusIoArchivedResourceCollection )
        {
            ( (PlexusIoArchivedResourceCollection) resources ).setFile( fileSet.getArchive() );
        }
        else
        {
            throw new ArchiverException( "Expected "
                                         + PlexusIoArchivedResourceCollection.class.getName()
                                         + ", got " + resources.getClass().getName() );
        }

        final PlexusIoProxyResourceCollection proxy = new PlexusIoProxyResourceCollection();
        
        proxy.setSrc( resources );
        proxy.setExcludes( fileSet.getExcludes() );
        proxy.setIncludes( fileSet.getIncludes() );
        proxy.setIncludingEmptyDirectories( fileSet.isIncludingEmptyDirectories() );
        proxy.setCaseSensitive( fileSet.isCaseSensitive() );
        proxy.setPrefix( fileSet.getPrefix() );
        proxy.setUsingDefaultExcludes( fileSet.isUsingDefaultExcludes() );
        proxy.setFileSelectors( fileSet.getFileSelectors() );
        
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
    public void addResources( PlexusIoResourceCollection collection )
        throws ArchiverException
    {
        resources.add( collection );
    }
    
    public void addArchivedFileSet( final ArchivedFileSet fileSet )
        throws ArchiverException
    {
        final PlexusIoResourceCollection resourceCollection = asResourceCollection( fileSet );
        addResources( resourceCollection );
    }

    /**
     * @since 1.0-alpha-7
     */
    public void addArchivedFileSet( File archiveFile, String prefix, String[] includes, String[] excludes )
        throws ArchiverException
    {
        DefaultArchivedFileSet fileSet = new DefaultArchivedFileSet();
        fileSet.setArchive( archiveFile );
        fileSet.setPrefix( prefix );
        fileSet.setIncludes( includes );
        fileSet.setExcludes( excludes );
        fileSet.setIncludingEmptyDirectories( includeEmptyDirs );
        addArchivedFileSet( fileSet );
    }

    /**
     * @since 1.0-alpha-7
     */
    public void addArchivedFileSet( File archiveFile, String prefix )
        throws ArchiverException
    {
        addArchivedFileSet( archiveFile, prefix, null, null );
    }

    /**
     * @since 1.0-alpha-7
     */
    public void addArchivedFileSet( File archiveFile, String[] includes, String[] excludes )
        throws ArchiverException
    {
        addArchivedFileSet( archiveFile, null, includes, excludes );
    }

    /**
     * @since 1.0-alpha-7
     */
    public void addArchivedFileSet( File archiveFile )
        throws ArchiverException
    {
        addArchivedFileSet( archiveFile, null, null, null );
    }

    /**
     * Allows us to pull the ArchiverManager instance out of the container without
     * causing a chicken-and-egg instantiation/composition problem.
     */
    public void contextualize( Context context )
        throws ContextException
    {
        PlexusContainer container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );

        try
        {
            archiverManager = (ArchiverManager) container.lookup( ArchiverManager.ROLE );
        }
        catch ( ComponentLookupException e )
        {
            throw new ContextException( "Error retrieving ArchiverManager instance: " + e.getMessage(), e );
        }
    }

    public boolean isForced()
    {
        return forced;
    }

    public void setForced( boolean forced )
    {
        this.forced = forced;
    }

    public void setArchiveFilters( List filters )
    {
        filterSupport = new FilterSupport( filters, getLogger() );
    }

    public void addArchiveFinalizer( ArchiveFinalizer finalizer )
    {
        if ( finalizers == null )
        {
            finalizers = new ArrayList();
        }

        finalizers.add( finalizer );
    }

    public void setArchiveFinalizers( List archiveFinalizers )
    {
        finalizers = archiveFinalizers;
    }

    public void setDotFileDirectory( File dotFileDirectory )
    {
        this.dotFileDirectory = dotFileDirectory;
    }

    protected boolean isUptodate()
        throws ArchiverException
    {
        File zipFile = getDestFile();
        long destTimestamp = zipFile.lastModified();
        if ( destTimestamp == 0 )
        {
            getLogger().debug( "isUp2date: false (Destination " + zipFile.getPath() + " not found.)" );
            return false; // File doesn't yet exist
        }

        Iterator it = resources.iterator();
        if ( !it.hasNext() )
        {
            getLogger().debug( "isUp2date: false (No input files.)" );
            return false; // No timestamp to compare
        }

        while ( it.hasNext() )
        {
            Object o = it.next();
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
                catch ( IOException e )
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
            for ( Iterator it = finalizers.iterator(); it.hasNext(); )
            {
                ArchiveFinalizer finalizer = (ArchiveFinalizer) it.next();

                finalizer.finalizeArchiveCreation( this );
            }
        }
    }

    private boolean include( InputStream in, String path )
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
        catch ( IOException e )
        {
            String msg = "Problem creating " + getArchiveType() + ": " + e.getMessage();

            StringBuffer revertBuffer = new StringBuffer();
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
            for ( Iterator it = finalizers.iterator(); it.hasNext(); )
            {
                ArchiveFinalizer finalizer = (ArchiveFinalizer) it.next();

                List virtualFiles = finalizer.getVirtualFiles();

                if ( ( virtualFiles != null ) && !virtualFiles.isEmpty() )
                {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean revert( StringBuffer messageBuffer )
    {
        return true;
    }

    protected void validate()
        throws ArchiverException, IOException
    {
    }

    protected abstract String getArchiveType();

    protected abstract void close()
        throws IOException;

    protected void cleanUp()
    {
        resources.clear();
    }

    protected abstract void execute()
        throws ArchiverException, IOException;

}
