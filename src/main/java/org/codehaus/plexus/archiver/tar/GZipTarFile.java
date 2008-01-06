package org.codehaus.plexus.archiver.tar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;


/**
 * Extension of {@link TarFile} for bzip2 compressed files.
 */
public class GZipTarFile extends TarFile
{
    /**
     * Creates a new instance with the given file.
     */
    public GZipTarFile( File file )
    {
        super( file );
    }

    protected InputStream getInputStream( File file )
        throws IOException
    {
        return new GZIPInputStream( super.getInputStream( file ) ){
            public void close()
                throws IOException
            {
                super.close();
            }
        };
    }
}
