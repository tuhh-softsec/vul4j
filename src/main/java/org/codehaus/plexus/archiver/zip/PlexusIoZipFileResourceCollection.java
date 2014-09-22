package org.codehaus.plexus.archiver.zip;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoArchiveResourceCollection;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

public class PlexusIoZipFileResourceCollection
    extends AbstractPlexusIoArchiveResourceCollection
{

    /**
     * The zip file resource collections role hint.
     */
    public static final String ROLE_HINT = "zip";

    protected Iterator<PlexusIoResource> getEntries()
        throws IOException
    {
        final File f = getFile();
        if ( f == null )
        {
            throw new IOException( "The tar archive file has not been set." );
        }
        final org.apache.commons.compress.archivers.zip.ZipFile zipFile = new org.apache.commons.compress.archivers.zip.ZipFile( f );
        final Enumeration en = zipFile.getEntries();
        return new Iterator<PlexusIoResource>()
        {
            public boolean hasNext()
            {
                return en.hasMoreElements();
            }

            public PlexusIoResource next()
            {
                final ZipArchiveEntry entry = (ZipArchiveEntry) en.nextElement();

                return new ZipResource( zipFile, entry );
            }

            public void remove()
            {
                throw new UnsupportedOperationException( "Removing isn't implemented." );
            }
        };
    }
}
