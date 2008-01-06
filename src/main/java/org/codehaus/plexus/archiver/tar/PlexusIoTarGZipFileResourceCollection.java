package org.codehaus.plexus.archiver.tar;

import java.io.File;

public class PlexusIoTarGZipFileResourceCollection
    extends PlexusIoTarFileResourceCollection
{
    protected TarFile newTarFile( File file )
    {
        return new GZipTarFile( file );
    }
}
