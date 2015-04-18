package org.codehaus.plexus.archiver.zip;

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

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoArchiveResourceCollection;
import org.codehaus.plexus.components.io.resources.EncodingSupported;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoURLResource;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Iterator;


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

    private Charset charset = Charset.forName("UTF-8");

    public PlexusIoZipFileResourceCollection()
    {

    }

    protected Iterator<PlexusIoResource> getEntries()
        throws IOException
    {
        final File f = getFile();
        if ( f == null )
        {
            throw new IOException( "The zip file has not been set." );
        }
        final URLClassLoader urlClassLoader = new URLClassLoader( new URL[]{ f.toURI().toURL() }, null )
        {
            public URL getResource( String name )
            {
                return findResource( name );
            }
        };

        final URL url = new URL( "jar:" + f.toURI().toURL() + "!/" );
        final ZipFile zipFile = new ZipFile( f, charset != null ? charset.name() : "UTF8");
        final Enumeration<ZipArchiveEntry> en = zipFile.getEntriesInPhysicalOrder();
        return new ZipFileResourceIterator( en, url, zipFile, urlClassLoader );
    }

    private static class ZipFileResourceIterator
        implements Iterator<PlexusIoResource>, Closeable
    {
        private final Enumeration<ZipArchiveEntry> en;

        private final URL url;

        private final ZipFile zipFile;

        private final URLClassLoader urlClassLoader;

        public ZipFileResourceIterator( Enumeration<ZipArchiveEntry> en, URL url, ZipFile zipFile, URLClassLoader urlClassLoader )
        {
            this.en = en;
            this.url = url;
            this.zipFile = zipFile;
            this.urlClassLoader = urlClassLoader;
        }

        public boolean hasNext()
        {
            return en.hasMoreElements();
        }

        public PlexusIoResource next()
        {
            final ZipArchiveEntry entry = en.nextElement();
            long l = entry.getTime();
            final long lastModified = l == -1 ? PlexusIoResource.UNKNOWN_MODIFICATION_DATE : l;
            final boolean dir = entry.isDirectory();
            final long size = dir ? PlexusIoResource.UNKNOWN_RESOURCE_SIZE : entry.getSize();

            return new PlexusIoURLResource( entry.getName(), lastModified, size, !dir, dir, true )
            {
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
            };
        }

        public void remove()
        {
            throw new UnsupportedOperationException( "Removing isn't implemented." );
        }

        public void close()
            throws IOException
        {
            zipFile.close();
        }
    }

    public void setEncoding( Charset charset )
    {
        this.charset = charset;
    }
}