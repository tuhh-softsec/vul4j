package org.codehaus.plexus.archiver.diags;

/*
 * Copyright 2014 The Codehaus Foundation.
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

import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchivedFileSet;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.FileSet;
import org.codehaus.plexus.archiver.ResourceIterator;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

@SuppressWarnings( { "UnusedDeclaration", "deprecation" } )
public class DelgatingArchiver implements Archiver
{
    private final Archiver target;

    public DelgatingArchiver( Archiver target )
    {
        this.target = target;
    }

    public void createArchive()
        throws ArchiverException, IOException
    {
        target.createArchive();
    }

    @Deprecated
    public void addDirectory( @Nonnull File directory )
        throws ArchiverException
    {
        target.addDirectory( directory );
    }

    @Deprecated
    public void addDirectory( @Nonnull File directory, String prefix )
        throws ArchiverException
    {
        target.addDirectory( directory, prefix );
    }

    @Deprecated
    public void addDirectory( @Nonnull File directory, String[] includes, String[] excludes )
        throws ArchiverException
    {
        target.addDirectory( directory, includes, excludes );
    }

    public void addDirectory( @Nonnull File directory, String prefix, String[] includes, String[] excludes )
        throws ArchiverException
    {
        target.addDirectory( directory, prefix, includes, excludes );
    }

    public void addFileSet( @Nonnull FileSet fileSet )
        throws ArchiverException
    {
        target.addFileSet( fileSet );
    }

    public void addSymlink( String symlinkName, String symlinkDestination )
        throws ArchiverException
    {
        target.addSymlink( symlinkName, symlinkDestination );
    }

    public void addSymlink( String symlinkName, int permissions, String symlinkDestination )
        throws ArchiverException
    {
        target.addSymlink( symlinkName, permissions, symlinkDestination );
    }

    public void addFile( @Nonnull File inputFile, @Nonnull String destFileName )
        throws ArchiverException
    {
        target.addFile( inputFile, destFileName );
    }

    public void addFile( @Nonnull File inputFile, @Nonnull String destFileName, int permissions )
        throws ArchiverException
    {
        target.addFile( inputFile, destFileName, permissions );
    }

    public void addArchivedFileSet( @Nonnull File archiveFile )
        throws ArchiverException
    {
        target.addArchivedFileSet( archiveFile );
    }

    @Deprecated
    public void addArchivedFileSet( @Nonnull File archiveFile, String prefix )
        throws ArchiverException
    {
        target.addArchivedFileSet( archiveFile, prefix );
    }

    public void addArchivedFileSet( File archiveFile, String[] includes, String[] excludes )
        throws ArchiverException
    {
        target.addArchivedFileSet( archiveFile, includes, excludes );
    }

    public void addArchivedFileSet( @Nonnull File archiveFile, String prefix, String[] includes, String[] excludes )
        throws ArchiverException
    {
        target.addArchivedFileSet( archiveFile, prefix, includes, excludes );
    }

    public void addArchivedFileSet( ArchivedFileSet fileSet )
        throws ArchiverException
    {
        target.addArchivedFileSet( fileSet );
    }

    public void addArchivedFileSet( ArchivedFileSet fileSet, Charset charset )
        throws ArchiverException
    {
        target.addArchivedFileSet( fileSet, charset );
    }

    public void addResource( PlexusIoResource resource, String destFileName, int permissions )
        throws ArchiverException
    {
        target.addResource( resource, destFileName, permissions );
    }

    public void addResources( PlexusIoResourceCollection resources )
        throws ArchiverException
    {
        target.addResources( resources );
    }

    public File getDestFile()
    {
        return target.getDestFile();
    }

    public void setDestFile( File destFile )
    {
        target.setDestFile( destFile );
    }

    public void setFileMode( int mode )
    {
        target.setFileMode( mode );
    }

    public int getFileMode()
    {
        return target.getFileMode();
    }

    public int getOverrideFileMode()
    {
        return target.getOverrideFileMode();
    }

    public void setDefaultFileMode( int mode )
    {
        target.setDefaultFileMode( mode );
    }

    public int getDefaultFileMode()
    {
        return target.getDefaultFileMode();
    }

    public void setDirectoryMode( int mode )
    {
        target.setDirectoryMode( mode );
    }

    public int getDirectoryMode()
    {
        return target.getDirectoryMode();
    }

    public int getOverrideDirectoryMode()
    {
        return target.getOverrideDirectoryMode();
    }

    public void setDefaultDirectoryMode( int mode )
    {
        target.setDefaultDirectoryMode( mode );
    }

    public int getDefaultDirectoryMode()
    {
        return target.getDefaultDirectoryMode();
    }

    public boolean getIncludeEmptyDirs()
    {
        return target.getIncludeEmptyDirs();
    }

    public void setIncludeEmptyDirs( boolean includeEmptyDirs )
    {
        target.setIncludeEmptyDirs( includeEmptyDirs );
    }

    public void setDotFileDirectory( File dotFileDirectory )
    {
        target.setDotFileDirectory( dotFileDirectory );
    }

    @Nonnull
    public ResourceIterator getResources()
        throws ArchiverException
    {
        return target.getResources();
    }

    public Map<String, ArchiveEntry> getFiles()
    {
        return target.getFiles();
    }

    public boolean isForced()
    {
        return target.isForced();
    }

    public void setForced( boolean forced )
    {
        target.setForced( forced );
    }

    public boolean isSupportingForced()
    {
        return target.isSupportingForced();
    }

    public String getDuplicateBehavior()
    {
        return target.getDuplicateBehavior();
    }

    public void setDuplicateBehavior( String duplicate )
    {
        target.setDuplicateBehavior( duplicate );
    }

    public void setUseJvmChmod( boolean useJvmChmod )
    {
        target.setUseJvmChmod( useJvmChmod );
    }

    public boolean isUseJvmChmod()
    {
        return target.isUseJvmChmod();
    }

    public boolean isIgnorePermissions()
    {
        return target.isIgnorePermissions();
    }

    public void setIgnorePermissions( boolean ignorePermissions )
    {
        target.setIgnorePermissions( ignorePermissions );
    }
}
