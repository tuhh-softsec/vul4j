/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.plexus.archiver.gzip;

import java.io.IOException;
import org.codehaus.plexus.archiver.AbstractArchiver;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.ResourceIterator;
import org.codehaus.plexus.archiver.exceptions.EmptyArchiveException;

public class GZipArchiver
    extends AbstractArchiver
{

    final GZipCompressor compressor = new GZipCompressor();

    @Override
    protected void execute()
        throws ArchiverException, IOException
    {
        if ( !checkForced() )
        {
            return;
        }

        ResourceIterator iter = getResources();
        if ( !iter.hasNext() )
        {
            throw new EmptyArchiveException( "archive cannot be empty" );
        }
        ArchiveEntry entry = iter.next();
        if ( iter.hasNext() )
        {
            throw new ArchiverException( "There is more than one file in input." );
        }
        compressor.setSource( entry.getResource() );
        compressor.setDestFile( getDestFile() );
        compressor.compress();
    }

    @Override
    public boolean isSupportingForced()
    {
        return true;
    }

    @Override
    protected void close()
    {
        compressor.close();
    }

    @Override
    protected String getArchiveType()
    {
        return "gzip";
    }

}
