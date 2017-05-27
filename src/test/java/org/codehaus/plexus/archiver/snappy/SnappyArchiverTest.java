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
package org.codehaus.plexus.archiver.snappy;

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
 * Tests for the snappy archiver
 */
public class SnappyArchiverTest
    extends BasePlexusArchiverTest
{

    public void testCreateArchive()
        throws Exception
    {
        ZipArchiver zipArchiver = (ZipArchiver) lookup( Archiver.ROLE, "zip" );
        zipArchiver.addDirectory( getTestFile( "src" ) );
        zipArchiver.setDestFile( getTestFile( "target/output/archiveForSnappy.zip" ) );
        zipArchiver.createArchive();
        SnappyArchiver archiver = (SnappyArchiver) lookup( Archiver.ROLE, "snappy" );
        String[] inputFiles = new String[ 1 ];
        inputFiles[0] = "archiveForSnappy.zip";
        archiver.addDirectory( getTestFile( "target/output" ), inputFiles, null );
        archiver.setDestFile( getTestFile( "target/output/archive.snappy" ) );
        archiver.createArchive();
    }

    public void testCreateEmptyArchive()
        throws Exception
    {
        SnappyArchiver archiver = (SnappyArchiver) lookup( Archiver.ROLE, "snappy" );
        archiver.setDestFile( getTestFile( "target/output/empty.snappy" ) );
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
        final File snappyFile = new File( "target/output/pom.xml.snappy" );
        SnappyArchiver SnappyArchiver = (SnappyArchiver) lookup( Archiver.ROLE, "snappy" );
        SnappyArchiver.setDestFile( snappyFile );
        SnappyArchiver.addFile( pomFile, "pom.xml" );
        FileUtils.removePath( snappyFile.getPath() );
        SnappyArchiver.createArchive();

        System.out.println( "Created: " + snappyFile.getAbsolutePath() );

        final File zipFile = new File( "target/output/pom.zip" );
        ZipArchiver zipArchiver = (ZipArchiver) lookup( Archiver.ROLE, "zip" );
        zipArchiver.setDestFile( zipFile );
        zipArchiver.addArchivedFileSet( snappyFile, "prfx/" );
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
     * Tests the Snappy archiver is forced set to true, and after that
     * tests the behavior when the forced is set to false.
     *
     * @throws Exception
     */
    public void testsnappyIsForcedBehaviour() throws Exception
    {
        SnappyArchiver SnappyArchiver = (SnappyArchiver) createArchiver( "snappy" );

        assertTrue( SnappyArchiver.isSupportingForced() );
        SnappyArchiver.createArchive();

        final long creationTime = SnappyArchiver.getDestFile().lastModified();

        waitUntilNewTimestamp( SnappyArchiver.getDestFile(), creationTime );

        SnappyArchiver = (SnappyArchiver) createArchiver( "snappy" );

        SnappyArchiver.setForced( true );
        SnappyArchiver.createArchive();

        final long firstRunTime = SnappyArchiver.getDestFile().lastModified();

        assertFalse( creationTime == firstRunTime );

        SnappyArchiver = (SnappyArchiver) createArchiver( "snappy" );

        SnappyArchiver.setForced( false );
        SnappyArchiver.createArchive();

        final long secondRunTime = SnappyArchiver.getDestFile().lastModified();

        assertEquals( firstRunTime, secondRunTime );
    }

}
