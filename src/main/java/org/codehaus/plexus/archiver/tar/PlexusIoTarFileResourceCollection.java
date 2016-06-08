/*
 * Copyright 2010-2015 The plexus developers.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.plexus.archiver.tar;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoArchiveResourceCollection;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

public class PlexusIoTarFileResourceCollection
    extends AbstractPlexusIoArchiveResourceCollection implements Closeable
{

    /**
     * The zip file resource collections role hint.
     */
    public static final String ROLE_HINT = "tar";

    protected TarFile newTarFile( File file )
    {
        return new TarFile( file );
    }

    TarFile tarFile = null;

    @Override
    public void close()
        throws IOException
    {
        if ( tarFile != null )
        {
            tarFile.close();
        }
    }

    @Override
    public boolean isConcurrentAccessSupported()
    {
        return false;
    }

    @Override
    protected Iterator<PlexusIoResource> getEntries()
        throws IOException
    {
        final File f = getFile();
        if ( f == null )
        {
            throw new IOException( "The tar archive file has not been set." );
        }
        if ( tarFile == null )
        {
            tarFile = newTarFile( f );
        }
        final Enumeration en = tarFile.getEntries();
        return new Iterator<PlexusIoResource>()
        {

            @Override
            public boolean hasNext()
            {
                return en.hasMoreElements();
            }

            @Override
            public PlexusIoResource next()
            {
                final TarArchiveEntry entry = (TarArchiveEntry) en.nextElement();
                return entry.isSymbolicLink()
                           ? new TarSymlinkResource( tarFile, entry )
                           : new TarResource( tarFile, entry );
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException( "Removing isn't implemented." );
            }

        };
    }

}
