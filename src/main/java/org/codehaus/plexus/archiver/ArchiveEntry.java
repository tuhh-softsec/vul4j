/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.plexus.archiver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nonnull;
import org.codehaus.plexus.archiver.resources.PlexusIoVirtualSymlinkResource;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.functions.ResourceAttributeSupplier;
import org.codehaus.plexus.components.io.resources.PlexusIoFileResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection;
import org.codehaus.plexus.components.io.resources.ResourceFactory;
import static org.codehaus.plexus.components.io.resources.ResourceFactory.createResource;

public class ArchiveEntry
{

    public static final String ROLE = ArchiveEntry.class.getName();

    public static final int FILE = 1;

    public static final int DIRECTORY = 2;

    public static final int SYMLINK = 3;

    @Nonnull private PlexusIoResource resource;

    private final String name;

    private final int type;

    private final int mode;

    private final int defaultDirMode;  // Sometimes a directory needs to be created. Which mode should it be ?
    // this mode is at the time of the creation of the archive entry, which is an important distinction

    private PlexusIoResourceAttributes attributes;

    private final boolean addSynchronously;

    /**
     * @param name the filename as it will appear in the archive. This is platform-specific
     * normalized with File.separatorChar
     * @param resource original filename
     * @param type FILE or DIRECTORY
     * @param mode octal unix style permissions
     * @param collection
     * @param defaultDirMode
     */
    private ArchiveEntry( String name, @Nonnull PlexusIoResource resource, int type, int mode,
                          PlexusIoResourceCollection collection, int defaultDirMode )
    {
        try
        {
            this.name = name;
            this.defaultDirMode = defaultDirMode;
            this.resource = collection != null ? collection.resolve( resource ) : resource;
            this.attributes = ( resource instanceof ResourceAttributeSupplier )
                                  ? ( (ResourceAttributeSupplier) resource ).getAttributes() : null;
            this.type = type;
            int permissions = mode;

            if ( mode == PlexusIoResourceAttributes.UNKNOWN_OCTAL_MODE && this.attributes == null )
            {
                permissions = resource.isFile()
                                  ? Archiver.DEFAULT_FILE_MODE
                                  : resource.isSymbolicLink()
                                        ? Archiver.DEFAULT_SYMLILNK_MODE
                                        : Archiver.DEFAULT_DIR_MODE;

            }

            this.mode = permissions == PlexusIoResourceAttributes.UNKNOWN_OCTAL_MODE
                            ? permissions
                            : ( permissions & UnixStat.PERM_MASK ) | ( type == FILE
                                                                       ? UnixStat.FILE_FLAG
                                                                       : type == SYMLINK
                                                                         ? UnixStat.LINK_FLAG
                                                                         : UnixStat.DIR_FLAG );

            this.addSynchronously = ( collection != null && !collection.isConcurrentAccessSupported() );
        }
        catch ( IOException e )
        {
            throw new ArchiverException( "Error resolving resource " + resource.getName(), e );
        }
    }

    /**
     * @return the filename of this entry in the archive.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return The original file that will be stored in the archive.
     *
     * @deprecated As of 1.0-alpha-10, file entries are no longer backed
     * by files, but by instances of {@link PlexusIoResource}.
     * Consequently, you should use {@link #getInputStream()}-
     */
    @Deprecated
    public File getFile()
    {
        if ( resource instanceof PlexusIoFileResource )
        {
            return ( (PlexusIoFileResource) resource ).getFile();
        }
        return null;
    }

    /**
     * @return The resource contents.
     */
    public InputStream getInputStream()
        throws IOException
    {
        return resource.getContents();
    }

    /**
     * @return FILE or DIRECTORY
     */
    public int getType()
    {
        return type;
    }

    /**
     * @return octal user/group/other unix like permissions.
     */
    public int getMode()
    {
        if ( mode != -1 )
        {
            return mode;
        }

        if ( attributes != null && attributes.getOctalMode() > -1 )
        {
            return attributes.getOctalMode();
        }

        return ( ( type == FILE
                   ? Archiver.DEFAULT_FILE_MODE
                   : type == SYMLINK
                     ? Archiver.DEFAULT_SYMLILNK_MODE
                     : Archiver.DEFAULT_DIR_MODE ) & UnixStat.PERM_MASK )
                   | ( type == FILE
                       ? UnixStat.FILE_FLAG
                       : type == SYMLINK
                         ? UnixStat.LINK_FLAG
                         : UnixStat.DIR_FLAG );

    }

    /**
     * Indicates if this entry should be added to the archive synchronously
     * before adding the next entry and/or accessing the next entry of {@link ResourceIterator}.
     *
     * @return {@code true} if this entry should be added synchronously
     */
    public boolean shouldAddSynchronously()
    {
        return addSynchronously;
    }

    public static ArchiveEntry createFileEntry( String target, PlexusIoResource resource, int permissions,
                                                PlexusIoResourceCollection collection, int defaultDirectoryPermissions )
        throws ArchiverException
    {
        if ( resource.isDirectory() )
        {
            throw new ArchiverException( "Not a file: " + resource.getName() );
        }
        final int type = resource.isSymbolicLink() ? SYMLINK : FILE;
        return new ArchiveEntry( target, resource, type, permissions, collection, defaultDirectoryPermissions );
    }

    public static ArchiveEntry createFileEntry( String target, File file, int permissions,
                                                int defaultDirectoryPermissions )
        throws ArchiverException, IOException
    {
        if ( !file.isFile() )
        {
            throw new ArchiverException( "Not a file: " + file );
        }

        final PlexusIoResource res = ResourceFactory.createResource( file );

        final int type;
        if ( res.isSymbolicLink() )
        {
            type = SYMLINK;
            permissions = permissions & ~( UnixStat.FILE_FLAG ); // remove file flag again .doh.
        }
        else
        {
            type = FILE; // File flag was there already. This is a bit of a mess !
        }

        return new ArchiveEntry( target, res, type, permissions, null, defaultDirectoryPermissions );
    }

    public static ArchiveEntry createDirectoryEntry( String target, @Nonnull PlexusIoResource resource, int permissions,
                                                     int defaultDirectoryPermissions )
        throws ArchiverException
    {
        if ( !resource.isDirectory() )
        {
            throw new ArchiverException( "Not a directory: " + resource.getName() );
        }
        final int type;
        if ( resource.isSymbolicLink() )
        {
            type = SYMLINK;
            permissions = permissions & ~( UnixStat.DIR_FLAG ); // remove dir flag again .doh.
        }
        else
        {
            type = DIRECTORY; // Dir flag was there already. This is a bit of a mess !

        }
        return new ArchiveEntry( target, resource, type, permissions, null, defaultDirectoryPermissions );
    }

    public static ArchiveEntry createDirectoryEntry( String target, final File file, int permissions,
                                                     int defaultDirMode1 )
        throws ArchiverException, IOException
    {
        if ( !file.isDirectory() )
        {
            throw new ArchiverException( "Not a directory: " + file );
        }

        final PlexusIoResource res = createResource( file );
        return new ArchiveEntry( target, res, DIRECTORY, permissions, null, defaultDirMode1 );
    }

    public static ArchiveEntry createSymlinkEntry( String symlinkName, int permissions, String symlinkDestination,
                                                   int defaultDirectoryPermissions )
    {
        final ArchiveEntry archiveEntry = new ArchiveEntry(
            symlinkName, new PlexusIoVirtualSymlinkResource( new File( symlinkName ), symlinkDestination ), SYMLINK,
            permissions, null, defaultDirectoryPermissions );

        return archiveEntry;
    }

    public PlexusIoResourceAttributes getResourceAttributes()
    {
        return attributes;
    }

    public void setResourceAttributes( PlexusIoResourceAttributes attributes )
    {
        this.attributes = attributes;
    }

    public @Nonnull
    PlexusIoResource getResource()
    {
        return resource;
    }

    public int getDefaultDirMode()
    {
        return defaultDirMode;
    }

}
