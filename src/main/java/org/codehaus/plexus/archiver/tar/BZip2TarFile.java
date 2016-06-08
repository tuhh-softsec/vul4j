package org.codehaus.plexus.archiver.tar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.codehaus.plexus.archiver.bzip2.BZip2UnArchiver;

/**
 * Extension of {@link TarFile} for bzip2 compressed files.
 */
public class BZip2TarFile extends TarFile
{

    /**
     * Creates a new instance with the given file.
     */
    public BZip2TarFile( File file )
    {
        super( file );
    }

    @Override
    protected InputStream getInputStream( File file )
        throws IOException
    {
        return BZip2UnArchiver.getBZip2InputStream( super.getInputStream( file ) );
    }

}
