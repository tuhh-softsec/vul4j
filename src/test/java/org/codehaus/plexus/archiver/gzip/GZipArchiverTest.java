package org.codehaus.plexus.archiver.gzip;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

/**
 * @author Emmanuel Venisse
 * @version $Id$
 */
public class GZipArchiverTest
    extends PlexusTestCase
{
    public void testCreateArchive()
        throws Exception
    {
        ZipArchiver zipArchiver = (ZipArchiver) lookup( Archiver.ROLE, "zip" );
        zipArchiver.addDirectory( getTestFile( "src" ) );
        zipArchiver.setDestFile( getTestFile( "target/output/archiveForGzip.zip" ) );
        zipArchiver.createArchive();
        GZipArchiver archiver = (GZipArchiver) lookup( Archiver.ROLE, "gzip" );
        String[] inputFiles = new String[ 1 ];
        inputFiles[ 0 ] = "archiveForGzip.zip";
        archiver.addDirectory( getTestFile( "target/output" ), inputFiles, null );
        archiver.setDestFile( getTestFile( "target/output/archive.gzip" ) );
        archiver.createArchive();
    }
}
