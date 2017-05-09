package org.codehaus.plexus.archiver.snappy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.annotation.WillNotClose;
import org.codehaus.plexus.components.io.attributes.FileAttributes;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.resources.PlexusIoCompressedFileResourceCollection;
import org.codehaus.plexus.util.IOUtil;

/**
 * Implementation of {@link org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection} for
 * snappy compressed files.
 */
public class PlexusIoSnappyResourceCollection
    extends PlexusIoCompressedFileResourceCollection
{

    @Nonnull
    @Override
    protected @WillNotClose
    InputStream getInputStream( File file )
        throws IOException
    {
        InputStream fis = new FileInputStream( file );
        try
        {
            final InputStream result = SnappyUnArchiver.getSnappyInputStream( fis );
            fis = null;
            return result;
        }
        finally
        {
            IOUtil.close( fis );
        }
    }

    @Override protected PlexusIoResourceAttributes getAttributes( File file ) throws IOException
    {
        return new FileAttributes( file, new HashMap<Integer, String>(), new HashMap<Integer, String>() );
    }

    @Override
    protected String getDefaultExtension()
    {
        return ".snappy";
    }

}
