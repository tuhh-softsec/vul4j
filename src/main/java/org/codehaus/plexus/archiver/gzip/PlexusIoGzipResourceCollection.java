package org.codehaus.plexus.archiver.gzip;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import javax.annotation.Nonnull;
import org.codehaus.plexus.archiver.util.Streams;
import org.codehaus.plexus.components.io.attributes.FileAttributes;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.resources.PlexusIoCompressedFileResourceCollection;
import org.codehaus.plexus.util.IOUtil;

/**
 * Abstract base class for compressed files, aka singleton
 * resource collections.
 */
public class PlexusIoGzipResourceCollection
    extends PlexusIoCompressedFileResourceCollection
{

    @Override
    protected String getDefaultExtension()
    {
        return ".gz";
    }

    @Nonnull
    @Override
    protected InputStream getInputStream( File file )
        throws IOException
    {
        InputStream fis = new FileInputStream( file );
        try
        {
            InputStream result = Streams.bufferedInputStream( new GZIPInputStream( fis ) );
            fis = null;
            return result;
        }
        finally
        {
            IOUtil.close( fis );
        }
    }

    @Override
    protected PlexusIoResourceAttributes getAttributes( File file )
        throws IOException
    {
        return new FileAttributes( file, new HashMap<Integer, String>(), new HashMap<Integer, String>() );
    }

}
