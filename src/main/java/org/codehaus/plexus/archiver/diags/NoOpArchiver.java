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
import java.util.Collections;
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

/**
 * An archiver that does nothing. Really.
 */
@SuppressWarnings( "UnusedDeclaration" )
public class NoOpArchiver implements Archiver
{

    boolean useJvmChmod;

    private boolean ignorePermissions;

    @Override
    public void createArchive()
        throws ArchiverException, IOException
    {

    }

    @Override
    public void addDirectory( @Nonnull File directory )
        throws ArchiverException
    {

    }

    @Override
    public void addDirectory( @Nonnull File directory, String prefix )
        throws ArchiverException
    {

    }

    @Override
    public void addDirectory( @Nonnull File directory, String[] includes, String[] excludes )
        throws ArchiverException
    {

    }

    @Override
    public void addDirectory( @Nonnull File directory, String prefix, String[] includes, String[] excludes )
        throws ArchiverException
    {

    }

    @Override
    public void addFileSet( @Nonnull FileSet fileSet )
        throws ArchiverException
    {

    }

    @Override
    public void addSymlink( String symlinkName, String symlinkDestination )
        throws ArchiverException
    {

    }

    @Override
    public void addSymlink( String symlinkName, int permissions, String symlinkDestination )
        throws ArchiverException
    {

    }

    @Override
    public void addFile( @Nonnull File inputFile, @Nonnull String destFileName )
        throws ArchiverException
    {

    }

    @Override
    public void addFile( @Nonnull File inputFile, @Nonnull String destFileName, int permissions )
        throws ArchiverException
    {

    }

    @Override
    public void addArchivedFileSet( @Nonnull File archiveFile )
        throws ArchiverException
    {

    }

    @Override
    public void addArchivedFileSet( @Nonnull File archiveFile, String prefix )
        throws ArchiverException
    {

    }

    @Override
    public void addArchivedFileSet( File archiveFile, String[] includes, String[] excludes )
        throws ArchiverException
    {

    }

    @Override
    public void addArchivedFileSet( @Nonnull File archiveFile, String prefix, String[] includes, String[] excludes )
        throws ArchiverException
    {

    }

    @Override
    public void addArchivedFileSet( ArchivedFileSet fileSet )
        throws ArchiverException
    {

    }

    @Override
    public void addArchivedFileSet( ArchivedFileSet fileSet, Charset charset )
        throws ArchiverException
    {

    }

    @Override
    public void addResource( PlexusIoResource resource, String destFileName, int permissions )
        throws ArchiverException
    {

    }

    @Override
    public void addResources( PlexusIoResourceCollection resources )
        throws ArchiverException
    {

    }

    @Override
    public File getDestFile()
    {
        return null;
    }

    @Override
    public void setDestFile( File destFile )
    {

    }

    @Override
    public void setFileMode( int mode )
    {

    }

    @Override
    public int getFileMode()
    {
        return 0;
    }

    @Override
    public int getOverrideFileMode()
    {
        return 0;
    }

    @Override
    public void setDefaultFileMode( int mode )
    {

    }

    @Override
    public int getDefaultFileMode()
    {
        return 0;
    }

    @Override
    public void setDirectoryMode( int mode )
    {

    }

    @Override
    public int getDirectoryMode()
    {
        return 0;
    }

    @Override
    public int getOverrideDirectoryMode()
    {
        return 0;
    }

    @Override
    public void setDefaultDirectoryMode( int mode )
    {

    }

    @Override
    public int getDefaultDirectoryMode()
    {
        return 0;
    }

    @Override
    public boolean getIncludeEmptyDirs()
    {
        return false;
    }

    @Override
    public void setIncludeEmptyDirs( boolean includeEmptyDirs )
    {

    }

    @Override
    public void setDotFileDirectory( File dotFileDirectory )
    {

    }

    @Nonnull
    @Override
    public ResourceIterator getResources()
        throws ArchiverException
    {
        return new ResourceIterator()
        {

            @Override
            public boolean hasNext()
            {
                return false;
            }

            @Override
            public ArchiveEntry next()
            {
                return null;
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException( "remove" );
            }

        };
    }

    @Override
    public Map<String, ArchiveEntry> getFiles()
    {
        return Collections.emptyMap();
    }

    @Override
    public boolean isForced()
    {
        return false;
    }

    @Override
    public void setForced( boolean forced )
    {

    }

    @Override
    public boolean isSupportingForced()
    {
        return false;
    }

    @Override
    public String getDuplicateBehavior()
    {
        return null;
    }

    @Override
    public void setDuplicateBehavior( String duplicate )
    {

    }

    @Override
    public void setUseJvmChmod( boolean useJvmChmod )
    {
        this.useJvmChmod = useJvmChmod;
    }

    @Override
    public boolean isUseJvmChmod()
    {
        return useJvmChmod;
    }

    @Override
    public boolean isIgnorePermissions()
    {
        return ignorePermissions;
    }

    @Override
    public void setIgnorePermissions( boolean ignorePermissions )
    {
        this.ignorePermissions = ignorePermissions;
    }

}
