package org.codehaus.plexus.archiver.tar;

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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.UnixStat;
import org.codehaus.plexus.archiver.bzip2.BZip2Compressor;
import org.codehaus.plexus.archiver.gzip.GZipCompressor;
import org.codehaus.plexus.archiver.util.Compressor;
import org.codehaus.plexus.archiver.zip.ArchiveFileComparator;
import org.codehaus.plexus.components.io.resources.PlexusIoFileResource;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author Emmanuel Venisse
 * @version $Id$
 */
public class TarArchiverTest
    extends PlexusTestCase
{
    public void testCreateArchive()
        throws Exception
    {
        TarArchiver archiver = (TarArchiver) lookup( Archiver.ROLE, "tar" );

        archiver.setDirectoryMode( 0500 );
        archiver.getOptions().setDirMode( 0500 );

        archiver.setFileMode( 0400 );
        archiver.getOptions().setMode( 0400 );

        archiver.addDirectory( getTestFile( "src" ) );
        archiver.setFileMode( 0640 );
        archiver.getOptions().setMode( 0640 );

        archiver.addFile( getTestFile( "src/test/resources/manifests/manifest1.mf" ), "one.txt" );
        archiver.addFile( getTestFile( "src/test/resources/manifests/manifest2.mf" ), "two.txt", 0664 );
        archiver.setDestFile( getTestFile( "target/output/archive.tar" ) );
        archiver.createArchive();

        TarInputStream tis;

        tis = new TarInputStream( new BufferedInputStream( new FileInputStream( archiver.getDestFile() ) ) );
        TarEntry te;

        while ( ( te = tis.getNextEntry() ) != null )
        {
            if ( te.isDirectory() )
            {
                assertEquals( 0500, te.getMode() & UnixStat.PERM_MASK );
            }
            else
            {
                if ( te.getName().equals( "one.txt" ) )
                {
                    assertEquals( 0640, te.getMode() & UnixStat.PERM_MASK );
                }
                else if ( te.getName().equals( "two.txt" ) )
                {
                    assertEquals( 0664, te.getMode() & UnixStat.PERM_MASK );
                }
                else
                {
                    assertEquals( 0400, te.getMode() & UnixStat.PERM_MASK );
                }

            }
        }

    }

    private class TarHandler
    {
        File createTarFile()
            throws Exception
        {
            final File srcDir = new File("src");
            final File tarFile = new File( "target/output/src.tar" );
            TarArchiver tarArchiver = (TarArchiver) lookup( Archiver.ROLE, "tar" );
            tarArchiver.setDestFile( tarFile );
            tarArchiver.addDirectory( srcDir, null, FileUtils.getDefaultExcludes() );
            FileUtils.removePath( tarFile.getPath() );
            tarArchiver.createArchive();
            return tarFile;
        }

        File createTarfile2( File tarFile )
            throws Exception
        {
            final File tarFile2 = new File( "target/output/src2.tar" );
            TarArchiver tarArchiver2 = (TarArchiver) lookup( Archiver.ROLE, "tar" );
            tarArchiver2.setDestFile( tarFile2 );
            tarArchiver2.addArchivedFileSet( tarFile, "prfx/" );
            FileUtils.removePath( tarFile2.getPath() );
            tarArchiver2.createArchive();
            return tarFile2;
        }

        TarFile newTarFile( File tarFile )
        {
            return new TarFile( tarFile );
        }
    }

    private class GZipTarHandler extends TarHandler
    {

        File createTarFile()
            throws Exception
        {
            File file = super.createTarFile();
            File compressedFile = new File( file.getPath() + ".gz" );
            Compressor compressor = new GZipCompressor();
            compressor.setSource( new PlexusIoFileResource( file ) );
            compressor.setDestFile( compressedFile );
            compressor.compress();
            compressor.close();
            return compressedFile;
        }

        TarFile newTarFile( File tarFile )
        {
            return new GZipTarFile( tarFile );
        }
    }

    private class BZip2TarHandler extends TarHandler
    {

        File createTarFile()
            throws Exception
        {
            File file = super.createTarFile();
            File compressedFile = new File( file.getPath() + ".bz2" );
            Compressor compressor = new BZip2Compressor();
            compressor.setSource( new PlexusIoFileResource( file ) );
            compressor.setDestFile( compressedFile );
            compressor.compress();
            compressor.close();
            return compressedFile;
        }

        TarFile newTarFile( File tarFile )
        {
            return new BZip2TarFile( tarFile );
        }
    }

    public void testUncompressedResourceCollection()
        throws Exception
    {
        testCreateResourceCollection( new TarHandler() );
    }

    public void testGzipCompressedResourceCollection()
        throws Exception
    {
        testCreateResourceCollection( new GZipTarHandler() );
    }

    public void testBzip2CompressedResourceCollection()
        throws Exception
    {
        testCreateResourceCollection( new BZip2TarHandler() );
    }

    private void testCreateResourceCollection( TarHandler tarHandler )
        throws Exception
    {
        final File tarFile = tarHandler.createTarFile();
        final File tarFile2 = tarHandler.createTarfile2( tarFile );
        final TarFile cmp1 = tarHandler.newTarFile( tarFile );
        final TarFile cmp2 = new TarFile( tarFile2 );
        ArchiveFileComparator.assertEquals( cmp1, cmp2, "prfx/" );
        cmp1.close();
        cmp2.close();
    }
}
