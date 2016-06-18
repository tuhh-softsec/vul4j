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
import org.codehaus.plexus.archiver.bzip2.BZip2Archiver;

/**
 * @author Dan Tran
 */
public class TarBZip2UnArchiverTest
    extends PlexusTestCase
{

    public void testExtract()
        throws Exception
    {
        TarArchiver tarArchiver = (TarArchiver) lookup( Archiver.ROLE, "tar" );
        tarArchiver.setLongfile( TarLongFileMode.posix );

        String fileName1 = "TarBZip2UnArchiverTest1.txt";
        String fileName2 = "TarBZip2UnArchiverTest2.txt";
        File file1InTar = getTestFile( "target/output/" + fileName1 );
        File file2InTar = getTestFile( "target/output/" + fileName2 );
        file1InTar.delete();
        file2InTar.delete();

        File testBZip2File = getTestFile( "target/output/archive.tar.bz2" );

        tarArchiver.addFile( getTestFile( "src/test/resources/manifests/manifest1.mf" ), fileName1 );
        tarArchiver.addFile( getTestFile( "src/test/resources/manifests/manifest2.mf" ), fileName2, 0664 );
        tarArchiver.setDestFile( getTestFile( "target/output/archive.tar" ) );
        tarArchiver.createArchive();

        BZip2Archiver bzip2Archiver = (BZip2Archiver) lookup( Archiver.ROLE, "bzip2" );

        bzip2Archiver.setDestFile( testBZip2File );
        bzip2Archiver.addFile( getTestFile( "target/output/archive.tar" ), "dontcare" );
        bzip2Archiver.createArchive();

        TarBZip2UnArchiver tarBZip2UnArchiver = (TarBZip2UnArchiver) lookup( UnArchiver.ROLE, "tbz2" );

        tarBZip2UnArchiver.setDestDirectory( getTestFile( "target/output" ) );
        tarBZip2UnArchiver.setSourceFile( testBZip2File );
        tarBZip2UnArchiver.extract();

        assertTrue( file1InTar.exists() );
        assertTrue( file2InTar.exists() );

        //makesure we place the source file back
        assertEquals( testBZip2File, tarBZip2UnArchiver.getSourceFile() );

    }

    public void testLookup()
        throws Exception
    {
        lookup( UnArchiver.ROLE, "tar.bz2" );
    }

}
