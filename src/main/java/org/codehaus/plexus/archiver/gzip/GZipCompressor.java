package org.codehaus.plexus.archiver.gzip;

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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * @version $Revision$ $Date$
 */
public class GZipCompressor
    extends Compressor
{
    private GZIPOutputStream zOut;

    /**
     * perform the GZip compression operation.
     */
    public void compress()
        throws ArchiverException
    {
        try
        {
            zOut = new GZIPOutputStream( new FileOutputStream( getDestFile() ) );
            compressFile( getSourceFile(), zOut );
        }
        catch ( IOException ioe )
        {
            String msg = "Problem creating gzip " + ioe.getMessage();
            throw new ArchiverException( msg, ioe );
        }
    }

    public void close()
    {
        if ( zOut != null )
        {
            try
            {
                // close up
                zOut.close();
            }
            catch ( IOException e )
            {
                // do nothing
            }
            
            zOut = null;
        }
    }
}
