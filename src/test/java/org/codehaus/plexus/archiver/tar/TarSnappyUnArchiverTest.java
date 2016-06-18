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
import org.codehaus.plexus.archiver.snappy.SnappyArchiver;

/**
 * Snappy tar archives
 */
public class TarSnappyUnArchiverTest
    extends PlexusTestCase
{

    public void testExtract()
        throws Exception
    {
        TarArchiver tarArchiver = (TarArchiver) lookup( Archiver.ROLE, "tar" );
        tarArchiver.setLongfile( TarLongFileMode.posix );

        String fileName1 = "TarSnappyUnArchiverTest1.txt";
        String fileName2 = "TarSnappyUnArchiverTest2.txt";
        File file1InTar = getTestFile( "target/output/" + fileName1 );
        File file2InTar = getTestFile( "target/output/" + fileName2 );
        file1InTar.delete();
        file2InTar.delete();

        tarArchiver.addFile( getTestFile( "src/test/resources/manifests/manifest1.mf" ), fileName1 );
        tarArchiver.addFile( getTestFile( "src/test/resources/manifests/manifest2.mf" ), fileName2, 0664 );
        tarArchiver.setDestFile( getTestFile( "target/output/archive.tar" ) );
        tarArchiver.createArchive();

        SnappyArchiver snappyArchiver = (SnappyArchiver) lookup( Archiver.ROLE, "snappy" );

        File testSnappyFile = getTestFile( "target/output/archive.tar.snappy" );
        snappyArchiver.setDestFile( testSnappyFile );
        snappyArchiver.addFile( getTestFile( "target/output/archive.tar" ), "dontcare" );
        snappyArchiver.createArchive();

        TarSnappyUnArchiver tarSnappyUnArchiver = (TarSnappyUnArchiver) lookup( UnArchiver.ROLE, "tar.snappy" );
        tarSnappyUnArchiver.setDestDirectory( getTestFile( "target/output" ) );
        tarSnappyUnArchiver.setSourceFile( testSnappyFile );
        tarSnappyUnArchiver.extract();

        assertTrue( file1InTar.exists() );
        assertTrue( file2InTar.exists() );

        //make sure we place the source file back
        assertEquals( testSnappyFile, tarSnappyUnArchiver.getSourceFile() );
    }

    public void testLookup()
        throws Exception
    {
        lookup( UnArchiver.ROLE, "tar.snappy" );
    }

}
