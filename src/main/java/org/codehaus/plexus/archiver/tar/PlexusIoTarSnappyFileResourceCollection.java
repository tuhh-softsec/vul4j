package org.codehaus.plexus.archiver.tar;

import java.io.File;

public class PlexusIoTarSnappyFileResourceCollection
    extends PlexusIoTarFileResourceCollection
{

    @Override
    protected TarFile newTarFile( File file )
    {
        return new SnappyTarFile( file );
    }

}
