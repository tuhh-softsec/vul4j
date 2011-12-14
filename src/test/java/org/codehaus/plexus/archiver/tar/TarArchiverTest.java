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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnixStat;
import org.codehaus.plexus.archiver.bzip2.BZip2Compressor;
import org.codehaus.plexus.archiver.gzip.GZipCompressor;
import org.codehaus.plexus.archiver.util.ArchiveEntryUtils;
import org.codehaus.plexus.archiver.util.Compressor;
import org.codehaus.plexus.archiver.zip.ArchiveFileComparator;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributeUtils;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.resources.PlexusIoFileResource;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.Os;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Emmanuel Venisse
 * @version $Id$
 */
public class TarArchiverTest
    extends PlexusTestCase
{
    
    private Logger logger;
    
    public void setUp()
        throws Exception
    {
        super.setUp();
        
        logger = new ConsoleLogger( Logger.LEVEL_DEBUG, "test" );
    }
    
    public void testCreateArchiveWithDetectedModes()
        throws Exception
    {

        String[] executablePaths = { "path/to/executable", "path/to/executable.bat" };

        String[] confPaths = { "path/to/etc/file", "path/to/etc/file2" };

        String[] logPaths = { "path/to/logs/log.txt" };

        int exeMode = 0777;
        int confMode = 0600;
        int logMode = 0640;

        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            StackTraceElement e = new Throwable().getStackTrace()[0];
            System.out.println( "Cannot execute test: " + e.getMethodName() + " on " + System.getProperty( "os.name" ) );
            return;
        }

        File tmpDir = null;
        try
        {
            tmpDir = File.createTempFile( "tbz2-with-chmod.", ".dir" );
            tmpDir.delete();

            tmpDir.mkdirs();

            for ( int i = 0; i < executablePaths.length; i++ )
            {
                writeFile( tmpDir, executablePaths[i], exeMode );
            }

            for ( int i = 0; i < confPaths.length; i++ )
            {
                writeFile( tmpDir, confPaths[i], confMode );
            }

            for ( int i = 0; i < logPaths.length; i++ )
            {
                writeFile( tmpDir, logPaths[i], logMode );
            }

            {
                Map attributesByPath = PlexusIoResourceAttributeUtils.getFileAttributesByPath( tmpDir );
                for ( int i = 0; i < executablePaths.length; i++ )
                {
                    String path = executablePaths[i];
                    PlexusIoResourceAttributes attrs = (PlexusIoResourceAttributes) attributesByPath.get( path );
                    if ( attrs == null )
                    {
                        attrs =
                            (PlexusIoResourceAttributes) attributesByPath.get( new File( tmpDir, path ).getAbsolutePath() );
                    }

                    assertNotNull( attrs );
                    assertEquals( "Wrong mode for: " + path + "; expected: " + exeMode, exeMode, attrs.getOctalMode() );
                }

                for ( int i = 0; i < confPaths.length; i++ )
                {
                    String path = confPaths[i];
                    PlexusIoResourceAttributes attrs = (PlexusIoResourceAttributes) attributesByPath.get( path );
                    if ( attrs == null )
                    {
                        attrs =
                            (PlexusIoResourceAttributes) attributesByPath.get( new File( tmpDir, path ).getAbsolutePath() );
                    }

                    assertNotNull( attrs );
                    assertEquals( "Wrong mode for: " + path + "; expected: " + confMode, confMode, attrs.getOctalMode() );
                }

                for ( int i = 0; i < logPaths.length; i++ )
                {
                    String path = logPaths[i];
                    PlexusIoResourceAttributes attrs = (PlexusIoResourceAttributes) attributesByPath.get( path );
                    if ( attrs == null )
                    {
                        attrs =
                            (PlexusIoResourceAttributes) attributesByPath.get( new File( tmpDir, path ).getAbsolutePath() );
                    }

                    assertNotNull( attrs );
                    assertEquals( "Wrong mode for: " + path + "; expected: " + logMode, logMode, attrs.getOctalMode() );
                }
            }

            File tarFile = getTestFile( "target/output/tar-with-modes.tar" );

            TarArchiver archiver = (TarArchiver) lookup( Archiver.ROLE, "tar" );
            archiver.setDestFile( tarFile );

            archiver.addDirectory( tmpDir );
            archiver.createArchive();

            assertTrue( tarFile.exists() );

            File tarFile2 = getTestFile( "target/output/tar-with-modes-L2.tar" );

            archiver = (TarArchiver) lookup( Archiver.ROLE, "tar" );
            archiver.setDestFile( tarFile2 );

            archiver.addArchivedFileSet( tarFile );
            archiver.createArchive();

            TarFile tf = new TarFile( tarFile2 );
            
            Map entriesByPath = new LinkedHashMap();
            for( Enumeration e = tf.getEntries(); e.hasMoreElements(); )
            {
                TarEntry te = (TarEntry) e.nextElement();
                entriesByPath.put( te.getName(), te );
            }

            for ( int i = 0; i < executablePaths.length; i++ )
            {
                String path = executablePaths[i];
                TarEntry te = (TarEntry) entriesByPath.get( path );

                int mode = te.getMode() & UnixStat.PERM_MASK;

                assertEquals( "Wrong mode for: " + path + "; expected: " + exeMode, exeMode, mode );
            }

            for ( int i = 0; i < confPaths.length; i++ )
            {
                String path = confPaths[i];
                TarEntry te = (TarEntry) entriesByPath.get( path );

                int mode = te.getMode() & UnixStat.PERM_MASK;

                assertEquals( "Wrong mode for: " + path + "; expected: " + confMode, confMode, mode );
            }

            for ( int i = 0; i < logPaths.length; i++ )
            {
                String path = logPaths[i];
                TarEntry te = (TarEntry) entriesByPath.get( path );

                int mode = te.getMode() & UnixStat.PERM_MASK;

                assertEquals( "Wrong mode for: " + path + "; expected: " + logMode, logMode, mode );
            }
        }
        finally
        {
            if ( tmpDir != null && tmpDir.exists() )
            {
                try
                {
                    FileUtils.forceDelete( tmpDir );
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
            }
        }
    }


    public void testUnicode() throws Exception {
        File tmpDir = getTestFile( "src/test/resources/utf8" );
        TarArchiver archiver = (TarArchiver) lookup( Archiver.ROLE, "tar" );
        File tarFile = getTestFile( "target/output/tar-with-longFileName.tar" );
        archiver.setDestFile(tarFile);
        TarLongFileMode mode = new TarLongFileMode();
        mode.setValue( TarLongFileMode.GNU);
        archiver.setLongfile(mode);
        archiver.addDirectory( tmpDir );
        archiver.createArchive();
        assertTrue( tarFile.exists() );
    }

    private void writeFile( File dir, String fname, int mode )
        throws IOException, ArchiverException
    {
        File file = new File( dir, fname );
        FileWriter writer = null;

        try
        {
            if ( file.getParentFile() != null )
            {
                file.getParentFile().mkdirs();
            }

            writer = new FileWriter( file );
            writer.write( "This is a test file." );
        }
        finally
        {
            IOUtil.close( writer );
        }

        ArchiveEntryUtils.chmod( file, mode, logger, false );
    }

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
