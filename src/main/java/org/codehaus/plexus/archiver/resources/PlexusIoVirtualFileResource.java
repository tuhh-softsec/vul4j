/*
 * Copyright 2014 The Codehaus Foundation.
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
package org.codehaus.plexus.archiver.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.annotation.Nonnull;
import org.codehaus.plexus.components.io.attributes.AttributeUtils;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.functions.ResourceAttributeSupplier;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoResource;

/**
 * A file resource that does not necessarily exist (anywhere).
 */
public class PlexusIoVirtualFileResource
    extends AbstractPlexusIoResource
    implements ResourceAttributeSupplier
{

    private final File file;

    protected PlexusIoVirtualFileResource( File file, String name )
    {
        super( name, file.lastModified(), file.length(), file.isFile(), file.isDirectory(), file.exists() );
        this.file = file;
    }

    protected static String getName( File file )
    {
        return file.getPath().replace( '\\', '/' );
    }

    /**
     * Returns the resources file.
     */
    public File getFile()
    {
        return file;
    }

    @Nonnull
    @Override
    public InputStream getContents()
        throws IOException
    {
        throw new UnsupportedOperationException( "We're not really sure we can do this" );
        //return new FileInputStream( getFile() );
    }

    @Override
    public URL getURL()
        throws IOException
    {
        return getFile().toURI().toURL();
    }

    @Override
    public long getSize()
    {
        return getFile().length();
    }

    @Override
    public boolean isDirectory()
    {
        return getFile().isDirectory();
    }

    @Override
    public boolean isExisting()
    {
        return getFile().exists();
    }

    @Override
    public boolean isFile()
    {
        return getFile().isFile();
    }

    @Override
    public PlexusIoResourceAttributes getAttributes()
    {
        return null;
    }

    @Override
    public long getLastModified()
    {
        if ( file.exists() )
        {
            return AttributeUtils.getLastModified( getFile() );
        }
        else
        {
            return System.currentTimeMillis();
        }
    }

    @Override public boolean isSymbolicLink()
    {
        return getAttributes().isSymbolicLink();
    }

}
