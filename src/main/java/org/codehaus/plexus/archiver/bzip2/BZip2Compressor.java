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
package org.codehaus.plexus.archiver.bzip2;

import java.io.IOException;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.Compressor;
import static org.codehaus.plexus.archiver.util.Streams.bufferedOutputStream;
import static org.codehaus.plexus.archiver.util.Streams.fileOutputStream;

public class BZip2Compressor
    extends Compressor
{

    private BZip2CompressorOutputStream zOut;

    /**
     * perform the BZip2 compression operation.
     */
    @Override
    public void compress()
        throws ArchiverException
    {
        try
        {
            zOut = new BZip2CompressorOutputStream( bufferedOutputStream( fileOutputStream( getDestFile() ) ) );
            // BUffering of the source stream seems to have little/no impact
            compress( getSource(), zOut );
        }
        catch ( IOException ioe )
        {
            String msg = "Problem creating bzip2 " + ioe.getMessage();
            throw new ArchiverException( msg, ioe );
        }
    }

    @Override
    public void close()
    {
        try
        {
            if ( this.zOut != null )
            {
                this.zOut.close();
                zOut = null;
            }
        }
        catch ( final IOException e )
        {
            throw new ArchiverException( "Failure closing target.", e );
        }
    }

}
