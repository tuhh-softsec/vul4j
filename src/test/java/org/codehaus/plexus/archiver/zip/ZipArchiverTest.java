package org.codehaus.plexus.archiver.zip;

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

import org.apache.commons.compress.archivers.zip.ExtraFieldUtils;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipExtraField;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.BoundedInputStream;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.BasePlexusArchiverTest;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.UnixStat;
import org.codehaus.plexus.archiver.util.ArchiveEntryUtils;
import org.codehaus.plexus.archiver.util.DefaultArchivedFileSet;
import org.codehaus.plexus.archiver.util.DefaultFileSet;
import org.codehaus.plexus.archiver.util.Streams;
import org.codehaus.plexus.components.io.attributes.Java7FileAttributes;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributeUtils;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.attributes.SimpleResourceAttributes;
import org.codehaus.plexus.components.io.functions.ContentSupplier;
import org.codehaus.plexus.components.io.functions.InputStreamTransformer;
import org.codehaus.plexus.components.io.resources.PlexusIoFileResource;
import org.codehaus.plexus.components.io.resources.PlexusIoFileResourceCollection;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection;
import org.codehaus.plexus.components.io.resources.ResourceFactory;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.Os;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Emmanuel Venisse
 * @version $Id$
 */
@SuppressWarnings( "OctalInteger" )
public class ZipArchiverTest
    extends BasePlexusArchiverTest
{

    private Logger logger;

    public void setUp()
        throws Exception
    {
        super.setUp();

        logger = new ConsoleLogger( Logger.LEVEL_DEBUG, "test" );
    }

    public void testImplicitPermissions()
        throws IOException
    {
        File zipFile = getTestFile( "target/output/zip-with-implicit-dirmode.zip" );

        ZipArchiver archiver = getZipArchiver( zipFile );

        archiver.setDefaultDirectoryMode( 0777 );
        archiver.setDirectoryMode( 0641 );
        archiver.setFileMode( 0222 );
        archiver.addFile( new File( "pom.xml" ), "fizz/buzz/pom.xml" );
        archiver.setDefaultDirectoryMode( 0530 );
        archiver.setDirectoryMode( -1 ); // Not forced mode
        archiver.setFileMode( 0111 );
        archiver.addFile( new File( "pom.xml" ), "fazz/bazz/pam.xml" );
        archiver.createArchive();

        assertTrue( zipFile.exists() );
        ZipFile zf = new ZipFile( zipFile );
        ZipArchiveEntry fizz = zf.getEntry( "fizz/" );
        assertEquals( 040641, fizz.getUnixMode() );
        ZipArchiveEntry pom = zf.getEntry( "fizz/buzz/pom.xml" );
        assertEquals( 0100222, pom.getUnixMode() );

        ZipArchiveEntry fazz = zf.getEntry( "fazz/" );
        assertEquals( 040530, fazz.getUnixMode() );
        ZipArchiveEntry pam = zf.getEntry( "fazz/bazz/pam.xml" );
        assertEquals( 0100111, pam.getUnixMode() );
    }


    public void testOverddidenPermissions()
        throws IOException
    {
        File zipFile = getTestFile( "target/output/zip-with-overriden-modes.zip" );

        ZipArchiver archiver = getZipArchiver( zipFile );
        archiver.setDefaultDirectoryMode( 0777 );
        archiver.setDirectoryMode( 0641 );
        archiver.setFileMode( 0777 );
        archiver.addDirectory( new File( "src/test/resources/symlinks/src" ) );
        archiver.createArchive();

        assertTrue( zipFile.exists() );
        ZipFile zf = new ZipFile( zipFile );
        ZipArchiveEntry fizz = zf.getEntry( "symDir" );
        assertTrue( fizz.isUnixSymlink() );
        ZipArchiveEntry symR = zf.getEntry( "symR" );
        assertTrue( symR.isUnixSymlink() );
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
            System.out.println(
                "Cannot execute test: " + e.getMethodName() + " on " + System.getProperty( "os.name" ) );
            return;
        }

        File tmpDir = null;
        try
        {
            tmpDir = File.createTempFile( "zip-with-chmod.", ".dir" );
            tmpDir.delete();

            tmpDir.mkdirs();

            for ( String executablePath : executablePaths )
            {
                writeFile( tmpDir, executablePath, exeMode );
            }

            for ( String confPath : confPaths )
            {
                writeFile( tmpDir, confPath, confMode );
            }

            for ( String logPath : logPaths )
            {
                writeFile( tmpDir, logPath, logMode );
            }

            {
                Map<String, PlexusIoResourceAttributes> attributesByPath =
                    PlexusIoResourceAttributeUtils.getFileAttributesByPath( tmpDir );
                for ( String path : executablePaths )
                {
                    PlexusIoResourceAttributes attrs = attributesByPath.get( path );
                    if ( attrs == null )
                    {
                        attrs = attributesByPath.get( new File( tmpDir, path ).getAbsolutePath() );
                    }

                    assertNotNull( attrs );
                    assertEquals( "Wrong mode for: " + path + "; expected: " + exeMode, exeMode, attrs.getOctalMode() );
                }

                for ( String path : confPaths )
                {
                    PlexusIoResourceAttributes attrs = attributesByPath.get( path );
                    if ( attrs == null )
                    {
                        attrs = attributesByPath.get( new File( tmpDir, path ).getAbsolutePath() );
                    }

                    assertNotNull( attrs );
                    assertEquals( "Wrong mode for: " + path + "; expected: " + confMode, confMode,
                                  attrs.getOctalMode() );
                }

                for ( String path : logPaths )
                {
                    PlexusIoResourceAttributes attrs = attributesByPath.get( path );
                    if ( attrs == null )
                    {
                        attrs = attributesByPath.get( new File( tmpDir, path ).getAbsolutePath() );
                    }

                    assertNotNull( attrs );
                    assertEquals( "Wrong mode for: " + path + "; expected: " + logMode, logMode, attrs.getOctalMode() );
                }
            }

            File zipFile = getTestFile( "target/output/zip-with-modes.zip" );

            ZipArchiver archiver = getZipArchiver( zipFile );

            archiver.addDirectory( tmpDir );
            archiver.createArchive();

            assertTrue( zipFile.exists() );

            File zipFile2 = getTestFile( "target/output/zip-with-modes-L2.zip" );

            archiver = getZipArchiver();
            archiver.setDestFile( zipFile2 );

            archiver.addArchivedFileSet( zipFile );
            archiver.createArchive();

            ZipFile zf = new ZipFile( zipFile2 );

            for ( String path : executablePaths )
            {
                ZipArchiveEntry ze = zf.getEntry( path );

                int mode = ze.getUnixMode() & UnixStat.PERM_MASK;

                assertEquals( "Wrong mode for: " + path + "; expected: " + exeMode, exeMode, mode );
            }

            for ( String path : confPaths )
            {
                ZipArchiveEntry ze = zf.getEntry( path );

                int mode = ze.getUnixMode() & UnixStat.PERM_MASK;

                assertEquals( "Wrong mode for: " + path + "; expected: " + confMode, confMode, mode );
            }

            for ( String path : logPaths )
            {
                ZipArchiveEntry ze = zf.getEntry( path );

                int mode = ze.getUnixMode() & UnixStat.PERM_MASK;

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

    private ZipArchiver getZipArchiver()
    {
        try
        {
            return (ZipArchiver) lookup( Archiver.ROLE, "zip" );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private ZipArchiver getZipArchiver( File destFile )
    {
        final ZipArchiver zipArchiver = getZipArchiver();
        zipArchiver.setDestFile( destFile );
        return zipArchiver;
    }

    private ZipUnArchiver getZipUnArchiver( File testJar )
        throws Exception
    {
        ZipUnArchiver zu = (ZipUnArchiver) lookup( UnArchiver.ROLE, "zip" );
        zu.setSourceFile( testJar );
        return zu;
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
        ZipArchiver archiver = newArchiver( "archive1.zip" );

        createArchive( archiver );
    }

    public void testAddArchivedFileSet()
        throws Exception
    {
        File toBeAdded = new File( "src/test/resources/test.zip" );
        DefaultArchivedFileSet sfd = DefaultArchivedFileSet.archivedFileSet( toBeAdded );
        File zipFIle = getTestFile( "target/output/withZip.zip" );
        final ZipArchiver zipArchiver = getZipArchiver( zipFIle );
        InputStreamTransformer is = new InputStreamTransformer()
        {
            @Nonnull
            public InputStream transform( @Nonnull PlexusIoResource resource, @Nonnull InputStream inputStream )
                throws IOException
            {
                return new BoundedInputStream( inputStream, 3 );
            }
        };
        sfd.setStreamTransformer( is );
        zipArchiver.addArchivedFileSet( sfd );
        zipArchiver.createArchive();

        final ZipUnArchiver zipUnArchiver = getZipUnArchiver( zipFIle );
        File destFile = new File( "target/output/withZip" );
        destFile.mkdirs();
        zipUnArchiver.setDestFile( destFile );
        zipUnArchiver.extract();
        File a3byteFile = new File( destFile,
                                    "Users/kristian/lsrc/plexus/plexus-archiver/src/main/java/org/codehaus/plexus/archiver/zip/ZipArchiver.java" );
        assertTrue( a3byteFile.exists() );
        assertTrue( a3byteFile.length() == 3 );
    }

    public void testCreateArchiveWithStreamTransformer()
        throws IOException
    {
        InputStreamTransformer is = new InputStreamTransformer()
        {
            @Nonnull
            public InputStream transform( @Nonnull PlexusIoResource resource, @Nonnull InputStream inputStream )
                throws IOException
            {
                return new BoundedInputStream( inputStream, 3 );
            }
        };

        final ZipArchiver zipArchiver = getZipArchiver( getTestFile( "target/output/all3bytes.zip" ) );
        File zipFIle = new File( "src/test/resources/test.zip" );
        DefaultArchivedFileSet afs = new DefaultArchivedFileSet( zipFIle );
        afs.setStreamTransformer( is );
        afs.setPrefix( "azip/" );
        zipArchiver.addArchivedFileSet( afs );

        DefaultFileSet dfs = new DefaultFileSet( new File( "src/test/resources/mjar179" ) );
        dfs.setStreamTransformer( is );
        dfs.setPrefix( "mj179/" );
        zipArchiver.addFileSet( dfs );

        PlexusIoFileResourceCollection files = new PlexusIoFileResourceCollection();
        files.setBaseDir( new File( "src/test/resources" ) );
        files.setStreamTransformer( is );
        files.setPrefix( "plexus/" );
        zipArchiver.addResources( files );

        zipArchiver.createArchive();


    }

    private ZipArchiver newArchiver( String name )
        throws Exception
    {
        ZipArchiver archiver = getZipArchiver( getTestFile( "target/output/" + name ) );

        archiver.setFileMode( 0640 );
        archiver.addFile( getTestFile( "src/test/resources/manifests/manifest1.mf" ), "one.txt" );
        archiver.addFile( getTestFile( "src/test/resources/manifests/manifest2.mf" ), "two.txt", 0664 );

        // reset default file mode for files included from now on
        archiver.setFileMode( 0400 );
        archiver.setDirectoryMode( 0777 );
        archiver.addDirectory( getTestFile( "src/test/resources/world-writable/" ), "worldwritable/" );

        archiver.setDirectoryMode( 0070 );
        archiver.addDirectory( getTestFile( "src/test/resources/group-writable/" ), "groupwritable/" );

        archiver.setDirectoryMode( 0500 );
        archiver.setFileMode( 0400 );
        archiver.addDirectory( getTestFile( "src" ) );

        return archiver;
    }

    private void fileModeAssert(int expected, int actual){
        assertEquals( Integer.toString( expected, 8 ), Integer.toString( actual, 8 ));
    }
    private void createArchive( ZipArchiver archiver )
        throws ArchiverException, IOException
    {
        archiver.createArchive();

        ZipFile zf = new ZipFile( archiver.getDestFile() );

        Enumeration e = zf.getEntries();

        while ( e.hasMoreElements() )
        {
            ZipArchiveEntry ze = (ZipArchiveEntry) e.nextElement();
            if ( ze.isDirectory() )
            {
                if ( ze.getName().startsWith( "worldwritable" ) )
                {
                    fileModeAssert( 0777, UnixStat.PERM_MASK & ze.getUnixMode() );
                }
                else if ( ze.getName().startsWith( "groupwritable" ) )
                {
                    fileModeAssert( 0070, UnixStat.PERM_MASK & ze.getUnixMode() );
                }
                else
                {
                    fileModeAssert( 0500, UnixStat.PERM_MASK & ze.getUnixMode() );
                }
            }
            else
            {
                if ( ze.getName().equals( "one.txt" ) )
                {
                    fileModeAssert( 0640, UnixStat.PERM_MASK & ze.getUnixMode() );
                }
                else if ( ze.getName().equals( "two.txt" ) )
                {
                    fileModeAssert( 0664, UnixStat.PERM_MASK & ze.getUnixMode() );
                }
                else if ( ze.isUnixSymlink() )
                {
                    //         assertEquals( ze.getName(), 0500, UnixStat.PERM_MASK & ze.getUnixMode() );
                }
                else
                {
                    fileModeAssert( 0400, UnixStat.PERM_MASK & ze.getUnixMode() );
                }
            }

        }
    }

    public void testSymlinkZip()
        throws Exception
    {
        final File zipFile = getTestFile( "target/output/pasymlinks.zip" );
        final ZipArchiver zipArchiver = getZipArchiver( zipFile );
        PlexusIoFileResourceCollection files = new PlexusIoFileResourceCollection();
        files.setFollowingSymLinks( false );
        files.setBaseDir( new File( "src/test/resources/symlinks" ) );
        files.setPrefix( "plexus/" );
        zipArchiver.addResources( files );
        zipArchiver.createArchive();
        final File output = getTestFile( "target/output/unzipped" );
        output.mkdirs();
        final ZipUnArchiver zipUnArchiver = getZipUnArchiver( zipFile );
        zipUnArchiver.setDestFile( output );
        zipUnArchiver.extract();
        File symDir = new File( "target/output/unzipped/plexus/src/symDir" );
        PlexusIoResourceAttributes fa = Java7FileAttributes.uncached( symDir );
        assertTrue( fa.isSymbolicLink() );
    }


    @SuppressWarnings( "ResultOfMethodCallIgnored" )
    public void testSymlinkFileSet()
        throws Exception
    {
        final File zipFile = getTestFile( "target/output/pasymlinks-fileset.zip" );
        final ZipArchiver zipArchiver = getZipArchiver( zipFile );
        final DefaultFileSet fs = new DefaultFileSet();
        fs.setPrefix( "bzz/" );
        fs.setDirectory( new File( "src/test/resources/symlinks/src" ) );
        zipArchiver.addFileSet( fs );
        zipArchiver.createArchive();
        final File output = getTestFile( "target/output/unzipped/symlFs" );
        output.mkdirs();
        final ZipUnArchiver zipUnArchiver = getZipUnArchiver( zipFile );
        zipUnArchiver.setDestFile( output );
        zipUnArchiver.extract();
        File symDir = new File( output, "bzz/symDir" );
        PlexusIoResourceAttributes fa = Java7FileAttributes.uncached( symDir );
        assertTrue( fa.isSymbolicLink() );
    }

    /*
     */

    public void testForced()
        throws Exception
    {
        ZipArchiver archiver = newArchiver( "archive2.zip" );

        assertTrue( archiver.isForced() );
        File f = archiver.getDestFile();
        if ( f.exists() )
        {
            FileUtils.fileDelete( f.getPath() );
        }
        assertFalse( f.exists() );
        createArchive( archiver );
        long l1 = f.lastModified();
        assertTrue( f.exists() );

        archiver = newArchiver( "archive2.zip" );
        waitUntilNewTimestamp( archiver.getDestFile(), l1 );
        createArchive( archiver );
        long l2 = f.lastModified();
        assertTrue( f.exists() );
        assertTrue( l2 > l1 );

        archiver = newArchiver( "archive2.zip" );
        assertTrue( archiver.isSupportingForced() );
        archiver.setForced( false );
        assertFalse( archiver.isForced() );

        createArchive( archiver );
        long l3 = f.lastModified();
        assertTrue( f.exists() );
        assertEquals( l2, l3 );
    }

    // Used to investigate extrafields
    public void testLookAtExtraZipFields_from_macos()
        throws IOException
    {
        InputStream fis = Streams.fileInputStream( new File( "src/test/resources/zip-timestamp/macOsZipFile.zip" ) );
        ZipInputStream zis = new ZipInputStream( fis );
        final java.util.zip.ZipEntry evenEntry = zis.getNextEntry();
        final ZipExtraField[] parse = ExtraFieldUtils.parse( evenEntry.getExtra() );
        System.out.println( Arrays.asList( parse ) );
        final java.util.zip.ZipEntry oddEntry = zis.getNextEntry();

        System.out.println( Arrays.asList( ExtraFieldUtils.parse( oddEntry.getExtra() ) ) );

        System.out.println( "oddEntry.getTime() = " + new Date( oddEntry.getTime() ).toString() );
        System.out.println( "oddEntry.getName() = " + oddEntry.getName() );
        System.out.println( "new String(oddEntry.getExtra()) = " + new String( oddEntry.getExtra() ) );
        System.out.println( "evenEntry.getName() = " + evenEntry.getName() );
        System.out.println( "evenEntry.getTime() = " + new Date( evenEntry.getTime() ).toString() );
        System.out.println( "new String(evenEntry.getExtra()) = " + new String( evenEntry.getExtra() ) );

    }

    // Used to investigate date roundtrip behaviour across zip versions
    public void testZipStuff()
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream( baos );
        // name the file inside the zip  file
        final File oddFile = new File( "src/test/resources/zip-timestamp/file-with-odd-time.txt" );
        final File evenFile = new File( "src/test/resources/zip-timestamp/file-with-even-time.txt" );
        final ZipEntry oddZe = new ZipEntry( oddFile.getName() );
        oddZe.setTime( oddFile.lastModified() );
        zos.putNextEntry( oddZe );
        final ZipEntry evenZe = new ZipEntry( evenFile.getName() );
        evenZe.setTime( evenFile.lastModified() );
        zos.putNextEntry( evenZe );
        zos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        ZipInputStream zipInputStream = new ZipInputStream( bais );
        final java.util.zip.ZipEntry oddEntry = zipInputStream.getNextEntry();
        System.out.println( "oddEntry.getTime() = " + new Date( oddEntry.getTime() ).toString() );
        System.out.println( "oddEntry.getName() = " + oddEntry.getName() );
        final java.util.zip.ZipEntry evenEntry = zipInputStream.getNextEntry();
        System.out.println( "evenEntry.getName() = " + evenEntry.getName() );
        System.out.println( "evenEntry.getTime() = " + new Date( evenEntry.getTime() ).toString() );
    }


    public void notestJustThatOne()
        throws Exception
    {
        final File srcDir = new File( "src" );
        String[] inc = { "test/java/org/codehaus/plexus/archiver/zip/ZipShortTest.java" };
        final File zipFile = new File( "target/output/zz1.zip" );

        final File zipFile2 = new File( "target/output/zz2.zip" );
        ZipArchiver zipArchiver2 = getZipArchiver( zipFile2 );

        // Bugbug: This does not work on 1.8....?
        zipArchiver2.addArchivedFileSet( zipFile );
        FileUtils.removePath( zipFile2.getPath() );
        zipArchiver2.createArchive();
    }

    public void testCreateResourceCollection()
        throws Exception
    {
        final File srcDir = new File( "src" );
        final File zipFile = new File( "target/output/src.zip" );
        ZipArchiver zipArchiver = getZipArchiver( zipFile );
        zipArchiver.addDirectory( srcDir, null, FileUtils.getDefaultExcludes() );
        zipArchiver.setEncoding( "UTF-8" );
        FileUtils.removePath( zipFile.getPath() );
        zipArchiver.createArchive();

        final File zipFile2 = new File( "target/output/src2.zip" );
        ZipArchiver zipArchiver2 = getZipArchiver( zipFile2 );
        zipArchiver2.addArchivedFileSet( zipFile, "prfx/" );
        zipArchiver2.setEncoding( "UTF-8" );
        FileUtils.removePath( zipFile2.getPath() );
        zipArchiver2.createArchive();

        final ZipFile cmp1 = new ZipFile( zipFile );
        final ZipFile cmp2 = new ZipFile( zipFile2 );
        ArchiveFileComparator.assertEquals( cmp1, cmp2, "prfx/" );
        cmp1.close();
        cmp2.close();
    }

    public void testDefaultUTF8()
        throws IOException
    {
        final ZipArchiver zipArchiver = getZipArchiver( new File( "target/output/utf8-default.zip" ) );
        zipArchiver.addDirectory( new File( "src/test/resources/miscUtf8" ) );
        zipArchiver.createArchive();
    }

    public void testDefaultUTF8withUTF8()
        throws IOException
    {
        final ZipArchiver zipArchiver = getZipArchiver( new File( "target/output/utf8-with_utf.zip" ) );
        zipArchiver.setEncoding( "UTF-8" );
        zipArchiver.addDirectory( new File( "src/test/resources/miscUtf8" ) );
        zipArchiver.createArchive();
    }

    public void testForcedFileModes()
        throws IOException
    {
        File step1file = new File( "target/output/forced-file-mode.zip" );
        {
            final ZipArchiver zipArchiver = getZipArchiver( step1file );
            zipArchiver.setFileMode( 0077 );
            zipArchiver.setDirectoryMode( 0007 );
            PlexusIoResourceAttributes attrs = new SimpleResourceAttributes( 123, "fred", 22, "filntstones", 0111 );
            PlexusIoResource resource =
                ResourceFactory.createResource( new File( "src/test/resources/folders/File.txt" ), "Test.txt", null,
                                                attrs );
            zipArchiver.addResource( resource, "Test2.txt", 0707 );
            PlexusIoFileResourceCollection files = new PlexusIoFileResourceCollection();
            files.setBaseDir( new File( "src/test/resources/folders" ) );
            files.setPrefix( "sixsixsix/" );
            zipArchiver.addResources( files );

            zipArchiver.createArchive();

            ZipFile zf = new ZipFile( step1file );
            fileModeAssert( 040007, zf.getEntry( "sixsixsix/a/" ).getUnixMode() );
            fileModeAssert( 0100077, zf.getEntry( "sixsixsix/b/FileInB.txt" ).getUnixMode() );
            fileModeAssert( 0100707, zf.getEntry( "Test2.txt" ).getUnixMode() );
            zf.close();
        }

        File Step2file = new File( "target/output/forced-file-mode-from-zip.zip" );
        {
            final ZipArchiver za2 = getZipArchiver( Step2file );
            za2.setFileMode( 0666 );
            za2.setDirectoryMode( 0676 );

            PlexusIoZipFileResourceCollection zipSrc = new PlexusIoZipFileResourceCollection();
            zipSrc.setFile( step1file );
            zipSrc.setPrefix( "zz/" );
            za2.addResources( zipSrc );
            za2.createArchive();
            ZipFile zf = new ZipFile( Step2file );
            fileModeAssert( 040676, zf.getEntry( "zz/sixsixsix/a/" ).getUnixMode() );
            fileModeAssert( 0100666, zf.getEntry( "zz/Test2.txt" ).getUnixMode() );
            zf.close();
        }

        File step3file = new File( "target/output/forced-file-mode-from-zip2.zip" );
        {
            final ZipArchiver za2 = getZipArchiver( step3file );
            za2.setFileMode( 0666 );
            za2.setDirectoryMode( 0676 );

            PlexusArchiverZipFileResourceCollection zipSrc = new PlexusArchiverZipFileResourceCollection();
            zipSrc.setFile( step1file );
            zipSrc.setPrefix( "zz/" );
            za2.addResources( zipSrc );
            za2.createArchive();
            ZipFile zf = new ZipFile( Step2file );
            fileModeAssert( 040676, zf.getEntry( "zz/sixsixsix/a/" ).getUnixMode() );
            fileModeAssert( 0100666, zf.getEntry( "zz/Test2.txt" ).getUnixMode() );
            zf.close();
        }
    }
}
