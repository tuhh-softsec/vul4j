package org.codehaus.plexus.archiver.tar;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.attributes.SimpleResourceAttributes;
import org.codehaus.plexus.components.io.functions.ResourceAttributeSupplier;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

import javax.annotation.Nonnull;

public class TarResource
    extends AbstractPlexusIoResource
    implements ResourceAttributeSupplier
{

    private final TarFile tarFile;
    private final TarArchiveEntry entry;
    private PlexusIoResourceAttributes attributes;

    public TarResource( TarFile tarFile, TarArchiveEntry entry )
    {
        super(entry.getName(), getLastModifiedTime( entry ),  entry.isDirectory() ? PlexusIoResource.UNKNOWN_RESOURCE_SIZE : entry.getSize(),
              !entry.isDirectory(), entry.isDirectory(), true);
        this.tarFile = tarFile;
        this.entry = entry;
    }

    private static long getLastModifiedTime( TarArchiveEntry entry )
    {
        long l = entry.getModTime().getTime();
        return l == -1 ? PlexusIoResource.UNKNOWN_MODIFICATION_DATE : l;
    }

    public synchronized PlexusIoResourceAttributes getAttributes()
    {
        if ( attributes == null )
        {
            attributes = new SimpleResourceAttributes(entry.getUserId(), entry.getUserName(), entry.getGroupId(), entry.getGroupName(), entry.getMode());
        }

        return attributes;
    }

    public synchronized void setAttributes( PlexusIoResourceAttributes attributes )
    {
        this.attributes = attributes;
    }

    public URL getURL()
        throws IOException
    {
        return null;
    }

    @Nonnull
    public InputStream getContents()
        throws IOException
    {
        return tarFile.getInputStream( entry );
    }
}
