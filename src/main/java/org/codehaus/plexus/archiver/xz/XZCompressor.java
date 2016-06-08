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

import java.io.IOException;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.Compressor;
import static org.codehaus.plexus.archiver.util.Streams.bufferedOutputStream;
import static org.codehaus.plexus.archiver.util.Streams.fileOutputStream;

/**
 * @author philip.lourandos
 * @since 3.3
 */
public class XZCompressor extends Compressor
{

    private XZCompressorOutputStream xzOut;

    public XZCompressor()
    {
    }

    @Override
    public void compress() throws ArchiverException
    {
        try
        {
            xzOut = new XZCompressorOutputStream( bufferedOutputStream( fileOutputStream( getDestFile() ) ) );
            compress( getSource(), xzOut );
        }
        catch ( IOException ioe )
        {
            throw new ArchiverException( "Problem creating xz " + ioe.getMessage(), ioe );
        }
    }

    @Override
    public void close()
    {
        try
        {
            if ( this.xzOut != null )
            {
                this.xzOut.close();
                xzOut = null;
            }
        }
        catch ( final IOException e )
        {
            throw new ArchiverException( "Failure closing target.", e );
        }
    }

}
