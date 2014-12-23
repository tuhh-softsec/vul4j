package org.codehaus.plexus.archiver.snappy;

/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.Compressor;
import org.codehaus.plexus.util.IOUtil;
import org.xerial.snappy.SnappyOutputStream;

import java.io.IOException;

import static org.codehaus.plexus.archiver.util.Streams.bufferedOutputStream;
import static org.codehaus.plexus.archiver.util.Streams.fileOutputStream;

/**
 * Snappy compression
 */
public class SnappyCompressor
    extends Compressor
{
    private SnappyOutputStream zOut;
    
    /**
     * perform the Snappy compression operation.
     */
    public void compress()
        throws ArchiverException
    {
        try
        {
            zOut = new SnappyOutputStream( bufferedOutputStream( fileOutputStream( getDestFile() ) ) );
            compress( getSource(), zOut );
        }
        catch ( IOException ioe )
        {
            String msg = "Problem creating snappy " + ioe.getMessage();
            throw new ArchiverException( msg, ioe );
        }
    }

    public void close()
    {
        IOUtil.close( zOut );
        zOut = null;
    }
}
