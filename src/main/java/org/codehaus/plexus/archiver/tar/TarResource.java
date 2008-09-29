package org.codehaus.plexus.archiver.tar;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.attributes.SimpleResourceAttributes;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceWithAttributes;

public class TarResource
    extends AbstractPlexusIoResource
    implements PlexusIoResourceWithAttributes
{

    private final TarFile tarFile;
    private final TarEntry entry;
    private PlexusIoResourceAttributes attributes;

    public TarResource( TarFile tarFile, TarEntry entry )
    {
        this.tarFile = tarFile;
        this.entry = entry;
        final boolean dir = entry.isDirectory();
        
        setName( entry.getName() );
        setDirectory( dir );
        setExisting( true );
        setFile( !dir );
        
        long l = entry.getLastModificationTime();
        setLastModified( l == -1 ? PlexusIoResource.UNKNOWN_MODIFICATION_DATE : l );
        setSize( dir ? PlexusIoResource.UNKNOWN_RESOURCE_SIZE : entry.getSize() );
    }

    public synchronized PlexusIoResourceAttributes getAttributes()
    {
        if ( attributes == null )
        {
            attributes = new SimpleResourceAttributes();
            attributes.setUserId( entry.getUserId() );
            attributes.setUserName( entry.getUserName() );
            attributes.setGroupId( entry.getGroupId() );
            attributes.setGroupName( entry.getGroupName() );
            attributes.setOctalMode( entry.getMode() );
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
        return tarFile.getInputStream( entry );
    }

}
