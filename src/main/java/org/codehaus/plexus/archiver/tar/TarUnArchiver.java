package org.codehaus.plexus.archiver.tar;

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
import org.codehaus.plexus.archiver.bzip2.CBZip2InputStream;
import org.codehaus.plexus.archiver.util.EnumeratedAttribute;
import org.codehaus.plexus.archiver.zip.AbstractZipUnArchiver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Revision$ $Date$
 */
public class TarUnArchiver
    extends AbstractZipUnArchiver
{
    /**
     * compression method
     */
    private UntarCompressionMethod compression = new UntarCompressionMethod();

    /**
     * Set decompression algorithm to use; default=none.
     * <p/>
     * Allowable values are
     * <ul>
     * <li>none - no compression
     * <li>gzip - Gzip compression
     * <li>bzip2 - Bzip2 compression
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

    protected void execute()
        throws ArchiverException, IOException
    {
        TarInputStream tis = null;
        try
        {
            getLogger().info( "Expanding: " + getSourceFile() + " into " + getDestDirectory() );
            tis = new TarInputStream( compression.decompress( getSourceFile(),
                                                              new BufferedInputStream(
                                                                  new FileInputStream( getSourceFile() ) ) ) );
            TarEntry te;

            while ( ( te = tis.getNextEntry() ) != null )
            {
                extractFile( getSourceFile(), getDestDirectory(), tis, te.getName(), te.getModTime(),
                             te.isDirectory() );
            }
            getLogger().debug( "expand complete" );

        }
        catch ( IOException ioe )
        {
            throw new ArchiverException( "Error while expanding " + getSourceFile().getAbsolutePath(), ioe );
        }
        finally
        {
            if ( tis != null )
            {
                try
                {
                    tis.close();
                }
                catch ( IOException e )
                {
                    // ignore
                }
            }
        }
    }

    /**
     * Valid Modes for Compression attribute to Untar Task
     */
    public static final class UntarCompressionMethod
        extends EnumeratedAttribute
    {

        // permissible values for compression attribute

        /**
         * No compression
         */
        private static final String NONE = "none";

        /**
         * GZIP compression
         */
        private static final String GZIP = "gzip";

        /**
         * BZIP2 compression
         */
        private static final String BZIP2 = "bzip2";


        /**
         * Constructor
         */
        public UntarCompressionMethod()
        {
            super();
            try
            {
                setValue( NONE );
            }
            catch ( ArchiverException ae )
            {
                //Do nothing
            }
        }

        /**
         * Constructor
         */
        public UntarCompressionMethod( String method )
        {
            super();
            try
            {
                setValue( method );
            }
            catch ( ArchiverException ae )
            {
                //Do nothing
            }
        }

        /**
         * Get valid enumeration values
         *
         * @return valid values
         */
        public String[] getValues()
        {
            return new String[]{NONE, GZIP, BZIP2};
        }

        /**
         * This method wraps the input stream with the
         * corresponding decompression method
         *
         * @param file    provides location information for BuildException
         * @param istream input stream
         * @return input stream with on-the-fly decompression
         * @throws IOException    thrown by GZIPInputStream constructor
         * @throws BuildException thrown if bzip stream does not
         *                        start with expected magic values
         */
        private InputStream decompress( final File file, final InputStream istream )
            throws IOException, ArchiverException
        {
            final String value = getValue();
            if ( GZIP.equals( value ) )
            {
                return new GZIPInputStream( istream );
            }
            else
            {
                if ( BZIP2.equals( value ) )
                {
                    final char[] magic = new char[]{'B', 'Z'};
                    for ( int i = 0; i < magic.length; i++ )
                    {
                        if ( istream.read() != magic[ i ] )
                        {
                            throw new ArchiverException( "Invalid bz2 file." + file.toString() );
                        }
                    }
                    return new CBZip2InputStream( istream );
                }
            }
            return istream;
        }
    }
}
