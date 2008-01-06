package org.codehaus.plexus.archiver.bzip2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.codehaus.plexus.components.io.resources.PlexusIoCompressedFileResourceCollection;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection;
import org.codehaus.plexus.util.IOUtil;


/**
 * Implementation of {@link PlexusIoResourceCollection} for
 * bzip2 compressed files.
 */
public class PlexusIoBzip2ResourceCollection
    extends PlexusIoCompressedFileResourceCollection
{
    protected InputStream getInputStream( File file )
        throws IOException
    {
        InputStream fis = new FileInputStream( file );
        try
        {
            final InputStream result = BZip2UnArchiver.getBZip2InputStream( fis );
            if ( result == null )
            {
                throw new IOException( file.getPath()
                                       + " is an invalid bzip2 file. " );
            }
            fis = null;
            return result;
        }
        finally
        {
            IOUtil.close( fis );
        }
    }

    protected String getDefaultExtension()
    {
        return ".bz2";
    }
}
