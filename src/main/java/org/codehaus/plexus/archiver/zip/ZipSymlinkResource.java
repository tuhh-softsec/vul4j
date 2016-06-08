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
package org.codehaus.plexus.archiver.zip;

import java.io.IOException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.codehaus.plexus.components.io.functions.InputStreamTransformer;
import org.codehaus.plexus.components.io.functions.SymlinkDestinationSupplier;

/**
 * A {@link ZipResource} that represents symbolic link.
 */
public class ZipSymlinkResource
    extends ZipResource
    implements SymlinkDestinationSupplier
{

    private final String symlinkDestination;

    public ZipSymlinkResource( ZipFile zipFile, ZipArchiveEntry entry, InputStreamTransformer streamTransformer )
    {
        super( zipFile, entry, streamTransformer );
        try
        {
            symlinkDestination = zipFile.getUnixSymlink( entry );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public String getSymlinkDestination()
        throws IOException
    {
        return symlinkDestination;
    }

    @Override
    public boolean isSymbolicLink()
    {
        return true;
    }

}
