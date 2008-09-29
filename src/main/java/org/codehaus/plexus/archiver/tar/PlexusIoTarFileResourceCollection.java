package org.codehaus.plexus.archiver.tar;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

import org.codehaus.plexus.components.io.resources.AbstractPlexusIoArchiveResourceCollection;


public class PlexusIoTarFileResourceCollection extends AbstractPlexusIoArchiveResourceCollection
{
    /**
     * The zip file resource collections role hint.
     */
    public static final String ROLE_HINT = "tar";

    protected TarFile newTarFile( File file ) {
        return new TarFile( file );
    }

    protected Iterator getEntries() throws IOException
    {
        final File f = getFile();
        if ( f == null )
        {
            throw new IOException( "The tar archive file has not been set." );
        }
        final TarFile tarFile = newTarFile( f );
        final Enumeration en = tarFile.getEntries();
        return new Iterator(){
            public boolean hasNext()
            {
                return en.hasMoreElements();
            }
            public Object next()
            {
                final TarEntry entry = (TarEntry) en.nextElement();
                final TarResource res = new TarResource( tarFile, entry );
                
                return res;
            }
            public void remove()
            {
                throw new UnsupportedOperationException( "Removing isn't implemented." );
            }
        };
    }
}
