/*
 * Copyright 2016 Codehaus.
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
package org.codehaus.plexus.archiver.xz;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import org.codehaus.plexus.components.io.attributes.FileAttributes;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.resources.PlexusIoCompressedFileResourceCollection;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author lore
 * @since 3.3
 */
public class PlexusIoXZResourceCollection extends PlexusIoCompressedFileResourceCollection
{

    @Override
    protected PlexusIoResourceAttributes getAttributes( File file ) throws IOException
    {
        return new FileAttributes( file, new HashMap<Integer, String>(), new HashMap<Integer, String>() );
    }

    @Override
    protected String getDefaultExtension()
    {
        return ".xz";
    }

    @Override
    protected InputStream getInputStream( File file ) throws IOException
    {
        FileInputStream fileIs = new FileInputStream( file );
        try
        {
            final InputStream result = XZUnArchiver.getXZInputStream( fileIs );
            fileIs = null;
            return result;
        }
        finally
        {
            IOUtil.close( fileIs );
        }
    }

}
