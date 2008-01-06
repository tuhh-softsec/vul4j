package org.codehaus.plexus.archiver.tar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

import org.codehaus.plexus.archiver.ArchiveFile;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoArchiveResourceCollection;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;


public class PlexusIoTarFileResourceCollection extends AbstractPlexusIoArchiveResourceCollection
{
    /**
     * The zip file resource collections role hint.
     */
    public static final String ROLE_HINT = "tar";

    protected TarFile newTarFile( File file ) {
        return new TarFile( file );
    }

    protected Iterator getEntries() throws IOException
    {
        final File f = getFile();
        if ( f == null )
        {
            throw new IOException( "The zip file has not been set." );
        }
        final URL url = new URL( "jar:" + f.toURI().toURL() + "!/");
        final TarFile tarFile = newTarFile( f );
        final Enumeration en = tarFile.getEntries();
        return new Iterator(){
            public boolean hasNext()
            {
                return en.hasMoreElements();
            }
            public Object next()
            {
                final ArchiveFile.Entry entry = (ArchiveFile.Entry) en.nextElement();
                final AbstractPlexusIoResource res = new AbstractPlexusIoResource(){
                    public InputStream getContents()
                        throws IOException
                    {
                        return tarFile.getInputStream( entry );
                    }
                    public URL getURL()
                        throws IOException
                    {
                        return null;
                    }
                };
                final boolean dir = entry.isDirectory();
                res.setName( entry.getName() );
                res.setDirectory( dir );
                res.setExisting( true );
                res.setFile( !dir );
                long l = entry.getLastModificationTime();
                res.setLastModified( l == -1 ? PlexusIoResource.UNKNOWN_MODIFICATION_DATE : l );
                res.setSize( dir ? PlexusIoResource.UNKNOWN_RESOURCE_SIZE : entry.getSize() );
                return res;
            }
            public void remove()
            {
                throw new UnsupportedOperationException( "Removing isn't implemented." );
            }
        };
    }
}
