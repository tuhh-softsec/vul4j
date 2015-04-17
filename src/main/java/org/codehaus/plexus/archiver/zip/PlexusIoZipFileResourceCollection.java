package org.codehaus.plexus.archiver.zip;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Iterator;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoArchiveResourceCollection;
import org.codehaus.plexus.components.io.resources.EncodingSupported;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

public class PlexusIoZipFileResourceCollection
    extends AbstractPlexusIoArchiveResourceCollection implements EncodingSupported
{

    /**
     * The zip file resource collections role hint.
     */
    public static final String ROLE_HINT = "zip";

    private Charset charset = Charset.forName( "UTF-8" );

    protected Iterator<PlexusIoResource> getEntries()
        throws IOException
    {
        final File f = getFile();
        if ( f == null )
        {
            throw new IOException( "The tar archive file has not been set." );
        }
        final ZipFile zipFile = new ZipFile( f, charset.name() );
        return new CloseableIterator( zipFile );
    }

    class CloseableIterator
        implements Iterator<PlexusIoResource>, Closeable
    {
        final Enumeration en;

        private final ZipFile zipFile;

        public CloseableIterator( ZipFile zipFile )
        {
            this.en = zipFile.getEntriesInPhysicalOrder();
            this.zipFile = zipFile;
        }

        public boolean hasNext ( ) {
            return en.hasMoreElements();
        }

        public PlexusIoResource next()
        {
            final ZipArchiveEntry entry = (ZipArchiveEntry) en.nextElement();

            return new ZipResource( zipFile, entry, getStreamTransformer() );
        }

        public void remove()
        {
            throw new UnsupportedOperationException( "Removing isn't implemented." );
        }

        public void close()
            throws IOException
        {
            zipFile.close();
        }
    }

    public void setEncoding( Charset charset )
    {
       this.charset = charset;
    }
}
