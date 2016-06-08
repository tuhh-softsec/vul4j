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
package org.codehaus.plexus.archiver.diags;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import javax.annotation.Nonnull;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchivedFileSet;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.FileSet;
import org.codehaus.plexus.archiver.ResourceIterator;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection;

@SuppressWarnings(
{
    "UnusedDeclaration", "deprecation"
} )
public class DelgatingArchiver implements Archiver
{

    private final Archiver target;

    public DelgatingArchiver( Archiver target )
    {
        this.target = target;
    }

    @Override
    public void createArchive()
        throws ArchiverException, IOException
    {
        target.createArchive();
    }

    @Deprecated
    @Override
    public void addDirectory( @Nonnull File directory )
        throws ArchiverException
    {
        target.addDirectory( directory );
    }

    @Deprecated
    @Override
    public void addDirectory( @Nonnull File directory, String prefix )
        throws ArchiverException
    {
        target.addDirectory( directory, prefix );
    }

    @Deprecated
    @Override
    public void addDirectory( @Nonnull File directory, String[] includes, String[] excludes )
        throws ArchiverException
    {
        target.addDirectory( directory, includes, excludes );
    }

    @Override
    public void addDirectory( @Nonnull File directory, String prefix, String[] includes, String[] excludes )
        throws ArchiverException
    {
        target.addDirectory( directory, prefix, includes, excludes );
    }

    @Override
    public void addFileSet( @Nonnull FileSet fileSet )
        throws ArchiverException
    {
        target.addFileSet( fileSet );
    }

    @Override
    public void addSymlink( String symlinkName, String symlinkDestination )
        throws ArchiverException
    {
        target.addSymlink( symlinkName, symlinkDestination );
    }

    @Override
    public void addSymlink( String symlinkName, int permissions, String symlinkDestination )
        throws ArchiverException
    {
        target.addSymlink( symlinkName, permissions, symlinkDestination );
    }

    @Override
    public void addFile( @Nonnull File inputFile, @Nonnull String destFileName )
        throws ArchiverException
    {
        target.addFile( inputFile, destFileName );
    }

    @Override
    public void addFile( @Nonnull File inputFile, @Nonnull String destFileName, int permissions )
        throws ArchiverException
    {
        target.addFile( inputFile, destFileName, permissions );
    }

    @Override
    public void addArchivedFileSet( @Nonnull File archiveFile )
        throws ArchiverException
    {
        target.addArchivedFileSet( archiveFile );
    }

    @Deprecated
    @Override
    public void addArchivedFileSet( @Nonnull File archiveFile, String prefix )
        throws ArchiverException
    {
        target.addArchivedFileSet( archiveFile, prefix );
    }

    @Override
    public void addArchivedFileSet( File archiveFile, String[] includes, String[] excludes )
        throws ArchiverException
    {
        target.addArchivedFileSet( archiveFile, includes, excludes );
    }

    @Override
    public void addArchivedFileSet( @Nonnull File archiveFile, String prefix, String[] includes, String[] excludes )
        throws ArchiverException
    {
        target.addArchivedFileSet( archiveFile, prefix, includes, excludes );
    }

    @Override
    public void addArchivedFileSet( ArchivedFileSet fileSet )
        throws ArchiverException
    {
        target.addArchivedFileSet( fileSet );
    }

    @Override
    public void addArchivedFileSet( ArchivedFileSet fileSet, Charset charset )
        throws ArchiverException
    {
        target.addArchivedFileSet( fileSet, charset );
    }

    @Override
    public void addResource( PlexusIoResource resource, String destFileName, int permissions )
        throws ArchiverException
    {
        target.addResource( resource, destFileName, permissions );
    }

    @Override
    public void addResources( PlexusIoResourceCollection resources )
        throws ArchiverException
    {
        target.addResources( resources );
    }

    @Override
    public File getDestFile()
    {
        return target.getDestFile();
    }

    @Override
    public void setDestFile( File destFile )
    {
        target.setDestFile( destFile );
    }

    @Override
    public void setFileMode( int mode )
    {
        target.setFileMode( mode );
    }

    @Override
    public int getFileMode()
    {
        return target.getFileMode();
    }

    @Override
    public int getOverrideFileMode()
    {
        return target.getOverrideFileMode();
    }

    @Override
    public void setDefaultFileMode( int mode )
    {
        target.setDefaultFileMode( mode );
    }

    @Override
    public int getDefaultFileMode()
    {
        return target.getDefaultFileMode();
    }

    @Override
    public void setDirectoryMode( int mode )
    {
        target.setDirectoryMode( mode );
    }

    @Override
    public int getDirectoryMode()
    {
        return target.getDirectoryMode();
    }

    @Override
    public int getOverrideDirectoryMode()
    {
        return target.getOverrideDirectoryMode();
    }

    @Override
    public void setDefaultDirectoryMode( int mode )
    {
        target.setDefaultDirectoryMode( mode );
    }

    @Override
    public int getDefaultDirectoryMode()
    {
        return target.getDefaultDirectoryMode();
    }

    @Override
    public boolean getIncludeEmptyDirs()
    {
        return target.getIncludeEmptyDirs();
    }

    @Override
    public void setIncludeEmptyDirs( boolean includeEmptyDirs )
    {
        target.setIncludeEmptyDirs( includeEmptyDirs );
    }

    @Override
    public void setDotFileDirectory( File dotFileDirectory )
    {
        target.setDotFileDirectory( dotFileDirectory );
    }

    @Nonnull
    @Override
    public ResourceIterator getResources()
        throws ArchiverException
    {
        return target.getResources();
    }

    @Override
    public Map<String, ArchiveEntry> getFiles()
    {
        return target.getFiles();
    }

    @Override
    public boolean isForced()
    {
        return target.isForced();
    }

    @Override
    public void setForced( boolean forced )
    {
        target.setForced( forced );
    }

    @Override
    public boolean isSupportingForced()
    {
        return target.isSupportingForced();
    }

    @Override
    public String getDuplicateBehavior()
    {
        return target.getDuplicateBehavior();
    }

    @Override
    public void setDuplicateBehavior( String duplicate )
    {
        target.setDuplicateBehavior( duplicate );
    }

    @Override
    public void setUseJvmChmod( boolean useJvmChmod )
    {
        target.setUseJvmChmod( useJvmChmod );
    }

    @Override
    public boolean isUseJvmChmod()
    {
        return target.isUseJvmChmod();
    }

    @Override
    public boolean isIgnorePermissions()
    {
        return target.isIgnorePermissions();
    }

    @Override
    public void setIgnorePermissions( boolean ignorePermissions )
    {
        target.setIgnorePermissions( ignorePermissions );
    }

}
