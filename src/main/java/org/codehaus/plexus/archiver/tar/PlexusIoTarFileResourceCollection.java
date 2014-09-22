package org.codehaus.plexus.archiver.tar;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoArchiveResourceCollection;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;


public class PlexusIoTarFileResourceCollection
    extends AbstractPlexusIoArchiveResourceCollection
{
    /**
     * The zip file resource collections role hint.
     */
    public static final String ROLE_HINT = "tar";

    protected TarFile newTarFile( File file )
    {
        return new TarFile( file );
    }

    protected Iterator<PlexusIoResource> getEntries()
        throws IOException
    {
        final File f = getFile();
        if ( f == null )
        {
            throw new IOException( "The tar archive file has not been set." );
        }
        final TarFile tarFile = newTarFile( f );
        final Enumeration en = tarFile.getEntries();
        return new Iterator<PlexusIoResource>()
        {
            public boolean hasNext()
            {
                return en.hasMoreElements();
            }

            public PlexusIoResource next()
            {
                final TarArchiveEntry entry = (TarArchiveEntry) en.nextElement();
                return new TarResource( tarFile, entry );
            }

            public void remove()
            {
                throw new UnsupportedOperationException( "Removing isn't implemented." );
            }
        };
    }
}
