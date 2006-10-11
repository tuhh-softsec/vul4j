package org.codehaus.plexus.archiver.bzip2;

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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @version $Revision$ $Date$
 */
public class BZip2Compressor
    extends Compressor
{
    private CBZip2OutputStream zOut;
    
    /**
     * perform the GZip compression operation.
     */
    public void compress()
        throws ArchiverException
    {
        try
        {
            BufferedOutputStream bos =
                new BufferedOutputStream( new FileOutputStream( getDestFile() ) );
            bos.write( 'B' );
            bos.write( 'Z' );
            zOut = new CBZip2OutputStream( bos );
            compressFile( getSourceFile(), zOut );
        }
        catch ( IOException ioe )
        {
            String msg = "Problem creating bzip2 " + ioe.getMessage();
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
                //ignore
            }
            
            zOut = null;
        }
    }
}
