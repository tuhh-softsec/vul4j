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
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nonnull;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.codehaus.plexus.archiver.AbstractUnArchiver;
import org.codehaus.plexus.archiver.ArchiverException;
import static org.codehaus.plexus.archiver.util.Streams.bufferedInputStream;
import static org.codehaus.plexus.archiver.util.Streams.bufferedOutputStream;
import static org.codehaus.plexus.archiver.util.Streams.copyFully;
import static org.codehaus.plexus.archiver.util.Streams.fileInputStream;
import static org.codehaus.plexus.archiver.util.Streams.fileOutputStream;

/**
 * @author philip.lourandos
 * @since 3.3
 */
public class XZUnArchiver extends AbstractUnArchiver
{

    private static final String OPERATION_XZ = "xz";

    public XZUnArchiver()
    {
    }

    public XZUnArchiver( File source )
    {
        super( source );
    }

    @Override
    protected void execute() throws ArchiverException
    {
        if ( getSourceFile().lastModified() > getDestFile().lastModified() )
        {
            getLogger().info( "Expanding " + getSourceFile().getAbsolutePath() + " to "
                                  + getDestFile().getAbsolutePath() );

            copyFully( getXZInputStream( bufferedInputStream( fileInputStream( getSourceFile(), OPERATION_XZ ) ) ),
                       bufferedOutputStream( fileOutputStream( getDestFile(), OPERATION_XZ ) ), OPERATION_XZ );

        }
    }

    public static @Nonnull
    XZCompressorInputStream getXZInputStream( InputStream in )
        throws ArchiverException
    {
        try
        {
            return new XZCompressorInputStream( in );
        }
        catch ( IOException ioe )
        {
            throw new ArchiverException( "Trouble creating BZIP2 compressor, invalid file ?", ioe );
        }
    }

    @Override
    protected void execute( String path, File outputDirectory ) throws ArchiverException
    {
        throw new UnsupportedOperationException( "Targeted execution not supported in xz format" );

    }

}
