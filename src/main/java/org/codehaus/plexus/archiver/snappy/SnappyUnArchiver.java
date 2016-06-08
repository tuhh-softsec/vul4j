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
package org.codehaus.plexus.archiver.snappy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nonnull;
import org.codehaus.plexus.archiver.AbstractUnArchiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.iq80.snappy.SnappyInputStream;
import static org.codehaus.plexus.archiver.util.Streams.bufferedInputStream;
import static org.codehaus.plexus.archiver.util.Streams.bufferedOutputStream;
import static org.codehaus.plexus.archiver.util.Streams.copyFully;
import static org.codehaus.plexus.archiver.util.Streams.fileInputStream;
import static org.codehaus.plexus.archiver.util.Streams.fileOutputStream;

/**
 * Unarchiver for snappy-compressed files.
 */
public class SnappyUnArchiver
    extends AbstractUnArchiver
{

    private final static String OPERATION_SNAPPY = "snappy";

    public SnappyUnArchiver()
    {
    }

    public SnappyUnArchiver( File sourceFile )
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

            copyFully(
                getSnappyInputStream( bufferedInputStream( fileInputStream( getSourceFile(), OPERATION_SNAPPY ) ) ),
                bufferedOutputStream( fileOutputStream( getDestFile(), OPERATION_SNAPPY ) ), OPERATION_SNAPPY );

        }
    }

    public static @Nonnull
    SnappyInputStream getSnappyInputStream( InputStream bis )
        throws ArchiverException
    {
        try
        {
            return new SnappyInputStream( bis );
        }
        catch ( IOException e )
        {
            throw new ArchiverException( "Trouble creating Snappy compressor, invalid file ?", e );
        }
    }

    @Override
    protected void execute( String path, File outputDirectory )
    {
        throw new UnsupportedOperationException( "Targeted extraction not supported in Snappy format." );
    }

}
