/*
 * Copyright 2007 The Codehaus Foundation.
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
package org.codehaus.plexus.archiver.zip;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Iterator;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.codehaus.plexus.components.io.functions.SymlinkDestinationSupplier;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoArchiveResourceCollection;
import org.codehaus.plexus.components.io.resources.EncodingSupported;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoURLResource;

public class PlexusIoZipFileResourceCollection
    extends AbstractPlexusIoArchiveResourceCollection
    implements EncodingSupported
{

    /**
     * The zip file resource collections role hint.
     */
    public static final String ROLE_HINT = "zipFile";

    /**
     * The zip file resource collections role hint for jar files.
     */
    public static final String JAR_ROLE_HINT = "jarFile";

    private Charset charset = Charset.forName( "UTF-8" );

    public PlexusIoZipFileResourceCollection()
    {

    }

    @Override
    public boolean isConcurrentAccessSupported()
    {
        // Maybe we could support concurrent some time in the future
        return false;
    }

    @Override
    protected Iterator<PlexusIoResource> getEntries()
        throws IOException
    {
        final File f = getFile();
        if ( f == null )
        {
            throw new IOException( "The zip file has not been set." );
        }
        final URLClassLoader urlClassLoader = new URLClassLoader( new URL[]
        {
            f.toURI().toURL()
        }, null )
        {

            @Override
            public URL getResource( String name )
            {
                return findResource( name );
            }

        };

        final URL url = new URL( "jar:" + f.toURI().toURL() + "!/" );
        final ZipFile zipFile = new ZipFile( f, charset != null ? charset.name() : "UTF8" );
        final Enumeration<ZipArchiveEntry> en = zipFile.getEntriesInPhysicalOrder();
        return new ZipFileResourceIterator( en, url, zipFile, urlClassLoader );
    }

    private static class ZipFileResourceIterator
        implements Iterator<PlexusIoResource>, Closeable
    {

        private class ZipFileResource
            extends PlexusIoURLResource
        {

            private ZipFileResource( ZipArchiveEntry entry )
            {
                super( entry.getName(),
                       entry.getTime() == -1 ? PlexusIoResource.UNKNOWN_MODIFICATION_DATE : entry.getTime(),
                       entry.isDirectory() ? PlexusIoResource.UNKNOWN_RESOURCE_SIZE : entry.getSize(),
                       !entry.isDirectory(), entry.isDirectory(), true );

            }

            @Override
            public URL getURL()
                throws IOException
            {
                String spec = getName();
                if ( spec.startsWith( "/" ) )
                {
                    // Code path for PLXCOMP-170. Note that urlClassloader does not seem to produce correct
                    // urls for this. Which again means files loaded via this path cannot have file names
                    // requiring url encoding
                    spec = "./" + spec;
                    return new URL( url, spec );
                }
                return urlClassLoader.getResource( spec );
            }

        }

        private class ZipFileSymlinkResource
            extends ZipFileResource
            implements SymlinkDestinationSupplier
        {

            private final ZipArchiveEntry entry;

            private ZipFileSymlinkResource( ZipArchiveEntry entry )
            {
                super( entry );

                this.entry = entry;
            }

            @Override
            public String getSymlinkDestination()
                throws IOException
            {
                return zipFile.getUnixSymlink( entry );
            }

            @Override
            public boolean isSymbolicLink()
            {
                return true;
            }

        }

        private final Enumeration<ZipArchiveEntry> en;

        private final URL url;

        private final ZipFile zipFile;

        private final URLClassLoader urlClassLoader;

        public ZipFileResourceIterator( Enumeration<ZipArchiveEntry> en, URL url, ZipFile zipFile,
                                        URLClassLoader urlClassLoader )
        {
            this.en = en;
            this.url = url;
            this.zipFile = zipFile;
            this.urlClassLoader = urlClassLoader;
        }

        @Override
        public boolean hasNext()
        {
            return en.hasMoreElements();
        }

        @Override
        public PlexusIoResource next()
        {
            final ZipArchiveEntry entry = en.nextElement();
            return entry.isUnixSymlink()
                       ? new ZipFileSymlinkResource( entry )
                       : new ZipFileResource( entry );

        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException( "Removing isn't implemented." );
        }

        @Override
        public void close()
            throws IOException
        {
            try
            {
                urlClassLoader.close();
            }
            finally
            {
                zipFile.close();
            }
        }

    }

    @Override
    public void setEncoding( Charset charset )
    {
        this.charset = charset;
    }

}
