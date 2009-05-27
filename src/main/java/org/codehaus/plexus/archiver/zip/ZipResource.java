package org.codehaus.plexus.archiver.zip;

import org.codehaus.plexus.archiver.UnixStat;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.attributes.SimpleResourceAttributes;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoResourceWithAttributes;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ZipResource
    extends AbstractPlexusIoResourceWithAttributes
{

    private final ZipFile zipFile;
    private final ZipEntry entry;
    private PlexusIoResourceAttributes attributes;

    public ZipResource( ZipFile zipFile, ZipEntry entry )
    {
        this.zipFile = zipFile;
        this.entry = entry;
        final boolean dir = entry.isDirectory();
        
        setName( entry.getName() );
        
        setFile( !dir );
        setDirectory( dir );
        
        setExisting( true );
        setFile( !dir );
        
        long l = entry.getLastModificationTime();
        setLastModified( l == -1 ? PlexusIoResource.UNKNOWN_MODIFICATION_DATE : l );
        setSize( dir ? PlexusIoResource.UNKNOWN_RESOURCE_SIZE : entry.getSize() );
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
            attributes = new SimpleResourceAttributes();
            attributes.setOctalMode( mode );
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
