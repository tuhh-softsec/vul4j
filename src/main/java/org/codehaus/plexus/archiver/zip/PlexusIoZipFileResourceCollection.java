package org.codehaus.plexus.archiver.zip;

import org.codehaus.plexus.components.io.resources.AbstractPlexusIoArchiveResourceCollection;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

public class PlexusIoZipFileResourceCollection
    extends AbstractPlexusIoArchiveResourceCollection
{

    /**
     * The zip file resource collections role hint.
     */
    public static final String ROLE_HINT = "zip";


    protected Iterator getEntries() throws IOException
    {
        final File f = getFile();
        if ( f == null )
        {
            throw new IOException( "The tar archive file has not been set." );
        }
        final ZipFile zipFile = new ZipFile( f );
        final Enumeration en = zipFile.getEntries();
        return new Iterator(){
            public boolean hasNext()
            {
                return en.hasMoreElements();
            }
            public Object next()
            {
                final ZipEntry entry = (ZipEntry) en.nextElement();
                final ZipResource res = new ZipResource( zipFile, entry );
                
                return res;
            }
            public void remove()
            {
                throw new UnsupportedOperationException( "Removing isn't implemented." );
            }
        };
    }
}
