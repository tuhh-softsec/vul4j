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
package org.codehaus.plexus.archiver.bzip2;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.BasePlexusArchiverTest;
import org.codehaus.plexus.archiver.exceptions.EmptyArchiveException;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author Emmanuel Venisse
 */
public class BZip2ArchiverTest
    extends BasePlexusArchiverTest
{

    public void testCreateArchive()
        throws Exception
    {
        ZipArchiver zipArchiver = (ZipArchiver) lookup( Archiver.ROLE, "zip" );
        zipArchiver.addDirectory( getTestFile( "src" ) );
        zipArchiver.setDestFile( getTestFile( "target/output/archiveForbz2.zip" ) );
        zipArchiver.createArchive();
        BZip2Archiver archiver = (BZip2Archiver) lookup( Archiver.ROLE, "bzip2" );
        String[] inputFiles = new String[ 1 ];
        inputFiles[0] = "archiveForbz2.zip";
        archiver.addDirectory( getTestFile( "target/output" ), inputFiles, null );
        archiver.setDestFile( getTestFile( "target/output/archive.bz2" ) );
        archiver.createArchive();
    }

    public void testCreateEmptyArchive()
        throws Exception
    {
        BZip2Archiver archiver = (BZip2Archiver) lookup( Archiver.ROLE, "bzip2" );
        archiver.setDestFile( getTestFile( "target/output/empty.bz2" ) );
        try
        {
            archiver.createArchive();

            fail( "Creating empty archive should throw EmptyArchiveException" );
        }
        catch ( EmptyArchiveException ignore )
        {
        }
    }

    public void testCreateResourceCollection()
        throws Exception
    {
        final File pomFile = new File( "pom.xml" );
        final File bz2File = new File( "target/output/pom.xml.bz2" );
        BZip2Archiver bzip2Archiver = (BZip2Archiver) lookup( Archiver.ROLE, "bzip2" );
        bzip2Archiver.setDestFile( bz2File );
        bzip2Archiver.addFile( pomFile, "pom.xml" );
        FileUtils.removePath( bz2File.getPath() );
        bzip2Archiver.createArchive();

        System.out.println( "Created: " + bz2File.getAbsolutePath() );

        final File zipFile = new File( "target/output/pom.zip" );
        ZipArchiver zipArchiver = (ZipArchiver) lookup( Archiver.ROLE, "zip" );
        zipArchiver.setDestFile( zipFile );
        zipArchiver.addArchivedFileSet( bz2File, "prfx/" );
        FileUtils.removePath( zipFile.getPath() );
        zipArchiver.createArchive();

        final ZipFile juZipFile = new ZipFile( zipFile );
        final ZipEntry zipEntry = juZipFile.getEntry( "prfx/target/output/pom.xml" );
        final InputStream archivePom = juZipFile.getInputStream( zipEntry );
        final InputStream pom = new FileInputStream( pomFile );

        assertTrue( Arrays.equals( IOUtil.toByteArray( pom ), IOUtil.toByteArray( archivePom ) ) );
        archivePom.close();
        pom.close();
        juZipFile.close();
    }

    /**
     * Tests the .bzip2 archiver is forced set to true, and after that
     * tests the behavior when the forced is set to false.
     *
     * @throws Exception
     */
    public void testBz2IsForcedBehaviour() throws Exception
    {
        BZip2Archiver bZip2Archiver = (BZip2Archiver) createArchiver( "bzip2" );

        assertTrue( bZip2Archiver.isSupportingForced() );
        bZip2Archiver.createArchive();

        final long creationTime = bZip2Archiver.getDestFile().lastModified();

        waitUntilNewTimestamp( bZip2Archiver.getDestFile(), creationTime );

        bZip2Archiver = (BZip2Archiver) createArchiver( "bzip2" );

        bZip2Archiver.setForced( true );
        bZip2Archiver.createArchive();

        final long firstRunTime = bZip2Archiver.getDestFile().lastModified();

        assertFalse( creationTime == firstRunTime );

        bZip2Archiver = (BZip2Archiver) createArchiver( "bzip2" );

        bZip2Archiver.setForced( false );
        bZip2Archiver.createArchive();

        final long secondRunTime = bZip2Archiver.getDestFile().lastModified();

        assertEquals( firstRunTime, secondRunTime );
    }

}
