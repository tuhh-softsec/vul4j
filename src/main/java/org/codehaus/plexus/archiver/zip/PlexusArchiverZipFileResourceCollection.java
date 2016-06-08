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

public class PlexusArchiverZipFileResourceCollection
    extends AbstractPlexusIoArchiveResourceCollection implements EncodingSupported
{

    /**
     * The zip file resource collections role hint.
     */
    public static final String ROLE_HINT = "zip";

    private Charset charset = Charset.forName( "UTF-8" );

    @Override
    protected Iterator<PlexusIoResource> getEntries()
        throws IOException
    {
        final File f = getFile();
        if ( f == null )
        {
            throw new IOException( "The tar archive file has not been set." );
        }
        final ZipFile zipFile = new ZipFile( f, charset != null ? charset.name() : "UTF8" );
        return new CloseableIterator( zipFile );
    }

    @Override
    public boolean isConcurrentAccessSupported()
    {
        // Well maybe someday investigate if we can do concurrent zip
        return false;
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

        @Override
        public boolean hasNext()
        {
            return en.hasMoreElements();
        }

        @Override
        public PlexusIoResource next()
        {
            final ZipArchiveEntry entry = (ZipArchiveEntry) en.nextElement();
            return entry.isUnixSymlink()
                       ? new ZipSymlinkResource( zipFile, entry, getStreamTransformer() )
                       : new ZipResource( zipFile, entry, getStreamTransformer() );

        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException( "Removing isn't implemented." );
        }

        @Override
        public void close()
            throws IOException
        {
            zipFile.close();
        }

    }

    @Override
    public void setEncoding( Charset charset )
    {
        this.charset = charset;
    }

}
