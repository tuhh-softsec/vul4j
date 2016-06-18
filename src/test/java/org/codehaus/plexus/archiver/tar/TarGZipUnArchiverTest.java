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
package org.codehaus.plexus.archiver.tar;

import java.io.File;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.gzip.GZipArchiver;

/**
 * @author Dan Tran
 */
public class TarGZipUnArchiverTest
    extends PlexusTestCase
{

    public void testExtract()
        throws Exception
    {
        TarArchiver tarArchiver = (TarArchiver) lookup( Archiver.ROLE, "tar" );
        tarArchiver.setLongfile( TarLongFileMode.posix );

        String fileName1 = "TarGZipUnArchiverTest1.txt";
        String fileName2 = "TarGZipUnArchiverTest2.txt";
        File file1InTar = getTestFile( "target/output/" + fileName1 );
        File file2InTar = getTestFile( "target/output/" + fileName2 );
        file1InTar.delete();
        file2InTar.delete();

        tarArchiver.addFile( getTestFile( "src/test/resources/manifests/manifest1.mf" ), fileName1 );
        tarArchiver.addFile( getTestFile( "src/test/resources/manifests/manifest2.mf" ), fileName2, 0664 );
        tarArchiver.setDestFile( getTestFile( "target/output/archive.tar" ) );
        tarArchiver.createArchive();

        GZipArchiver gzipArchiver = (GZipArchiver) lookup( Archiver.ROLE, "gzip" );

        File testGZipFile = getTestFile( "target/output/archive.tar.gz" );
        gzipArchiver.setDestFile( testGZipFile );
        gzipArchiver.addFile( getTestFile( "target/output/archive.tar" ), "dontcare" );
        gzipArchiver.createArchive();

        TarGZipUnArchiver tarGZipUnArchiver = (TarGZipUnArchiver) lookup( UnArchiver.ROLE, "tgz" );
        tarGZipUnArchiver.setDestDirectory( getTestFile( "target/output" ) );
        tarGZipUnArchiver.setSourceFile( testGZipFile );
        tarGZipUnArchiver.extract();

        assertTrue( file1InTar.exists() );
        assertTrue( file2InTar.exists() );

        //make sure we place the source file back
        assertEquals( testGZipFile, tarGZipUnArchiver.getSourceFile() );

    }

    public void testLookup()
        throws Exception
    {
        lookup( UnArchiver.ROLE, "tar.gz" );
    }

}
