package org.codehaus.plexus.archiver.tar;

import org.codehaus.plexus.archiver.snappy.SnappyUnArchiver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


/**
 * Extension of {@link org.codehaus.plexus.archiver.tar.TarFile} for snappy compressed files.
 */
public class SnappyTarFile extends TarFile
{
    /**
     * Creates a new instance with the given file.
     */
    public SnappyTarFile(File file)
    {
        super( file );
    }

    protected InputStream getInputStream( File file )
            throws IOException
    {
        return SnappyUnArchiver.getSnappyInputStream( super.getInputStream( file ));
    }
}
