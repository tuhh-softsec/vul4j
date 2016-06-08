package org.codehaus.plexus.archiver.tar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import org.codehaus.plexus.archiver.util.Streams;

/**
 * Extension of {@link TarFile} for gzip compressed files.
 */
public class GZipTarFile
    extends TarFile
{

    /**
     * Creates a new instance with the given file.
     */
    public GZipTarFile( File file )
    {
        super( file );
    }

    @Override
    protected InputStream getInputStream( File file )
        throws IOException
    {
        return Streams.bufferedInputStream( new GZIPInputStream( super.getInputStream( file ) ) );
    }

}
