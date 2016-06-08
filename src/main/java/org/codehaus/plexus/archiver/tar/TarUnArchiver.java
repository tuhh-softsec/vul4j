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
package org.codehaus.plexus.archiver.tar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.codehaus.plexus.archiver.AbstractUnArchiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.Streams;
import org.codehaus.plexus.util.IOUtil;
import org.iq80.snappy.SnappyInputStream;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 */
public class TarUnArchiver
    extends AbstractUnArchiver
{

    public TarUnArchiver()
    {
    }

    public TarUnArchiver( File sourceFile )
    {
        super( sourceFile );
    }

    /**
     * compression method
     */
    private UntarCompressionMethod compression = UntarCompressionMethod.NONE;

    /**
     * Set decompression algorithm to use; default=none.
     * <p/>
     * Allowable values are
     * <ul>
     * <li>none - no compression</li>
     * <li>gzip - Gzip compression</li>
     * <li>bzip2 - Bzip2 compression</li>
     * <li>snappy - Snappy compression</li>
     * <li>xz - Xz compression</li>
     * </ul>
     *
     * @param method compression method
     */
    public void setCompression( UntarCompressionMethod method )
    {
        compression = method;
    }

    /**
     * No encoding support in Untar.
     */
    public void setEncoding( String encoding )
    {
        getLogger().warn( "The TarUnArchiver doesn't support the encoding attribute" );
    }

    @Override
    protected void execute()
        throws ArchiverException
    {
        execute( getSourceFile(), getDestDirectory() );
    }

    @Override
    protected void execute( String path, File outputDirectory )
    {
        execute( new File( path ), getDestDirectory() );
    }

    protected void execute( File sourceFile, File destDirectory )
        throws ArchiverException
    {
        TarArchiveInputStream tis = null;
        try
        {
            getLogger().info( "Expanding: " + sourceFile + " into " + destDirectory );
            TarFile tarFile = new TarFile( sourceFile );
            tis = new TarArchiveInputStream(
                decompress( compression, sourceFile, new BufferedInputStream( new FileInputStream( sourceFile ) ) ) );
            TarArchiveEntry te;
            while ( ( te = tis.getNextTarEntry() ) != null )
            {
                TarResource fileInfo = new TarResource( tarFile, te );
                if ( isSelected( te.getName(), fileInfo ) )
                {
                    final String symlinkDestination = te.isSymbolicLink() ? te.getLinkName() : null;
                    extractFile( sourceFile, destDirectory, tis, te.getName(), te.getModTime(), te.isDirectory(),
                                 te.getMode() != 0 ? te.getMode() : null, symlinkDestination );

                }
            }
            getLogger().debug( "expand complete" );
            tis.close();
            tis = null;
        }
        catch ( IOException ioe )
        {
            throw new ArchiverException( "Error while expanding " + sourceFile.getAbsolutePath(), ioe );
        }
        finally
        {
            IOUtil.close( tis );
        }
    }

    /**
     * This method wraps the input stream with the
     * corresponding decompression method
     *
     * @param file provides location information for BuildException
     * @param istream input stream
     *
     * @return input stream with on-the-fly decompression
     *
     * @throws IOException thrown by GZIPInputStream constructor
     */
    private InputStream decompress( UntarCompressionMethod compression, final File file, final InputStream istream )
        throws IOException, ArchiverException
    {
        if ( compression == UntarCompressionMethod.GZIP )
        {
            return Streams.bufferedInputStream( new GZIPInputStream( istream ) );
        }
        else if ( compression == UntarCompressionMethod.BZIP2 )
        {
            return new BZip2CompressorInputStream( istream );
        }
        else if ( compression == UntarCompressionMethod.SNAPPY )
        {
            return new SnappyInputStream( istream, true );
        }
        else if ( compression == UntarCompressionMethod.XZ )
        {
            return new XZCompressorInputStream( istream );
        }
        return istream;
    }

    /**
     * Valid Modes for Compression attribute to Untar Task
     */
    public static enum UntarCompressionMethod
    {

        NONE( "none" ),
        GZIP( "gzip" ),
        BZIP2( "bzip2" ),
        SNAPPY( "snappy" ),
        XZ( "xz" );

        final String value;

        /**
         * Constructor
         */
        UntarCompressionMethod( String value )
        {
            this.value = value;
        }

    }

}
