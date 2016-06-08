package org.codehaus.plexus.archiver.tar;

import java.io.File;

public class PlexusIoTarBZip2FileResourceCollection
    extends PlexusIoTarFileResourceCollection
{

    @Override
    protected TarFile newTarFile( File file )
    {
        return new BZip2TarFile( file );
    }

}
