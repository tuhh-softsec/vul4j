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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nonnull;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.codehaus.plexus.archiver.AbstractUnArchiver;
import org.codehaus.plexus.archiver.ArchiverException;
import static org.codehaus.plexus.archiver.util.Streams.bufferedInputStream;
import static org.codehaus.plexus.archiver.util.Streams.bufferedOutputStream;
import static org.codehaus.plexus.archiver.util.Streams.copyFully;
import static org.codehaus.plexus.archiver.util.Streams.fileInputStream;
import static org.codehaus.plexus.archiver.util.Streams.fileOutputStream;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 */
public class BZip2UnArchiver
    extends AbstractUnArchiver
{

    private final static String OPERATION_BZIP2 = "bzip2";

    public BZip2UnArchiver()
    {
    }

    public BZip2UnArchiver( File sourceFile )
    {
        super( sourceFile );
    }

    @Override
    protected void execute()
        throws ArchiverException
    {
        if ( getSourceFile().lastModified() > getDestFile().lastModified() )
        {
            getLogger().info(
                "Expanding " + getSourceFile().getAbsolutePath() + " to " + getDestFile().getAbsolutePath() );

            copyFully( getBZip2InputStream( bufferedInputStream( fileInputStream( getSourceFile(), OPERATION_BZIP2 ) ) ),
                       bufferedOutputStream( fileOutputStream( getDestFile(), OPERATION_BZIP2 ) ), OPERATION_BZIP2 );
        }
    }

    public static @Nonnull
    BZip2CompressorInputStream getBZip2InputStream( InputStream bis )
        throws ArchiverException
    {
        try
        {
            // Note that bis must be buffered for performance. Does not need buffering around BZip2CompressorInputStream
            return new BZip2CompressorInputStream( bis );
        }
        catch ( IOException e )
        {
            throw new ArchiverException( "Trouble creating BZIP2 compressor, invalid file ?", e );
        }
    }

    @Override
    protected void execute( String path, File outputDirectory )
    {
        throw new UnsupportedOperationException( "Targeted extraction not supported in BZIP2 format." );
    }

}
