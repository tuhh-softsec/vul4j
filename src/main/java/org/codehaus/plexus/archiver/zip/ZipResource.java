package org.codehaus.plexus.archiver.zip;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.codehaus.plexus.archiver.UnixStat;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.attributes.SimpleResourceAttributes;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceWithAttributes;

public class ZipResource extends AbstractPlexusIoResource
    implements PlexusIoResourceWithAttributes
{

    private final org.apache.commons.compress.archivers.zip.ZipFile zipFile;
    private final ZipArchiveEntry entry;
    private PlexusIoResourceAttributes attributes;

    public ZipResource( org.apache.commons.compress.archivers.zip.ZipFile zipFile, ZipArchiveEntry entry )
    {
        super(entry.getName(),getLastModofied( entry), entry.isDirectory() ? PlexusIoResource.UNKNOWN_RESOURCE_SIZE : entry.getSize() ,
              !entry.isDirectory(), entry.isDirectory(), true);
        this.zipFile = zipFile;
        this.entry = entry;
    }

    private static long getLastModofied( ZipArchiveEntry entry )
    {
        long l = entry.getLastModifiedDate().getTime();
        return l == -1 ? PlexusIoResource.UNKNOWN_MODIFICATION_DATE : l;
    }

    public synchronized PlexusIoResourceAttributes getAttributes()
    {
        int mode = entry.getUnixMode();
        if ( ( mode & UnixStat.FILE_FLAG ) == UnixStat.FILE_FLAG )
        {
            mode = mode & ~UnixStat.FILE_FLAG;
        }
        else
        {
            mode = mode & ~UnixStat.DIR_FLAG;
        }
        
        if ( attributes == null )
        {
            attributes = new SimpleResourceAttributes(null, null,null,null, mode);
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

    public InputStream getContents()
        throws IOException
    {
        return zipFile.getInputStream( entry );
    }

}
