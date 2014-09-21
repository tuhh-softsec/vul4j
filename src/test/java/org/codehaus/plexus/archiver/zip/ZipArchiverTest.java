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

import org.apache.commons.compress.archivers.zip.*;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.BasePlexusArchiverTest;
import org.codehaus.plexus.archiver.UnixStat;
import org.codehaus.plexus.archiver.util.ArchiveEntryUtils;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributeUtils;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.Os;

import java.io.*;
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
    
    public void testCreateArchiveWithDetectedModes()
        throws Exception
    {
        
        String[] executablePaths = {
            "path/to/executable",
            "path/to/executable.bat"
        };
        
        String[] confPaths = {
            "path/to/etc/file",
            "path/to/etc/file2"
        };
        
        String[] logPaths = {
            "path/to/logs/log.txt"
        };
        
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
            tmpDir = File.createTempFile( "zip-with-chmod.", ".dir" );
            tmpDir.delete();
            
            tmpDir.mkdirs();

			for (String executablePath : executablePaths) {
				writeFile(tmpDir, executablePath, exeMode);
			}

			for (String confPath : confPaths) {
				writeFile(tmpDir, confPath, confMode);
			}

			for (String logPath : logPaths) {
				writeFile(tmpDir, logPath, logMode);
			}
            
            {
                Map attributesByPath = PlexusIoResourceAttributeUtils.getFileAttributesByPath( tmpDir );
				for (String path : executablePaths) {
					PlexusIoResourceAttributes attrs = (PlexusIoResourceAttributes) attributesByPath.get(path);
					if (attrs == null) {
						attrs = (PlexusIoResourceAttributes) attributesByPath.get(new File(tmpDir, path).getAbsolutePath());
					}

					assertNotNull(attrs);
					assertEquals("Wrong mode for: " + path + "; expected: " + exeMode, exeMode, attrs.getOctalMode());
				}

				for (String path : confPaths) {
					PlexusIoResourceAttributes attrs = (PlexusIoResourceAttributes) attributesByPath.get(path);
					if (attrs == null) {
						attrs = (PlexusIoResourceAttributes) attributesByPath.get(new File(tmpDir, path).getAbsolutePath());
					}

					assertNotNull(attrs);
					assertEquals("Wrong mode for: " + path + "; expected: " + confMode, confMode, attrs.getOctalMode());
				}

				for (String path : logPaths) {
					PlexusIoResourceAttributes attrs = (PlexusIoResourceAttributes) attributesByPath.get(path);
					if (attrs == null) {
						attrs = (PlexusIoResourceAttributes) attributesByPath.get(new File(tmpDir, path).getAbsolutePath());
					}

					assertNotNull(attrs);
					assertEquals("Wrong mode for: " + path + "; expected: " + logMode, logMode, attrs.getOctalMode());
				}
            }
            
            File zipFile = getTestFile( "target/output/zip-with-modes.zip" );
            
            ZipArchiver archiver = (ZipArchiver) lookup( Archiver.ROLE, "zip" );
            archiver.setDestFile( zipFile );

            archiver.addDirectory( tmpDir );
            archiver.createArchive();
            
            assertTrue( zipFile.exists() );
            
            File zipFile2 = getTestFile( "target/output/zip-with-modes-L2.zip" );
            
            archiver = (ZipArchiver) lookup( Archiver.ROLE, "zip" );
            archiver.setDestFile( zipFile2 );

            archiver.addArchivedFileSet( zipFile );
            archiver.createArchive();

            org.apache.commons.compress.archivers.zip.ZipFile zf = new org.apache.commons.compress.archivers.zip.ZipFile( zipFile2 );

			for (String path : executablePaths) {
				ZipArchiveEntry ze = zf.getEntry(path);

				int mode = ze.getUnixMode() & UnixStat.PERM_MASK;

				assertEquals("Wrong mode for: " + path + "; expected: " + exeMode, exeMode, mode);
			}

			for (String path : confPaths) {
				ZipArchiveEntry ze = zf.getEntry(path);

				int mode = ze.getUnixMode() & UnixStat.PERM_MASK;

				assertEquals("Wrong mode for: " + path + "; expected: " + confMode, confMode, mode);
			}

			for (String path : logPaths) {
				ZipArchiveEntry ze = zf.getEntry(path);

				int mode = ze.getUnixMode() & UnixStat.PERM_MASK;

				assertEquals("Wrong mode for: " + path + "; expected: " + logMode, logMode, mode);
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

        createArchive(archiver);
    }

    private ZipArchiver newArchiver( String name ) throws Exception {
        ZipArchiver archiver = (ZipArchiver) lookup( Archiver.ROLE, "zip" );

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

        archiver.setDestFile( getTestFile( "target/output/" + name ) );

        return archiver;
    }

    private void createArchive( ZipArchiver archiver )
        throws ArchiverException, IOException
    {
        archiver.createArchive();

        org.apache.commons.compress.archivers.zip.ZipFile zf = new org.apache.commons.compress.archivers.zip.ZipFile( archiver.getDestFile() );

        Enumeration e = zf.getEntries();

        while ( e.hasMoreElements() )
        {
            ZipArchiveEntry ze = (ZipArchiveEntry) e.nextElement();
            if ( ze.isDirectory() )
            {
                if ( ze.getName().startsWith( "worldwritable" ) )
                {
                    assertEquals( 0777, UnixStat.PERM_MASK & ze.getUnixMode() );
                }
                else if ( ze.getName().startsWith( "groupwritable" ) )
                {
                    assertEquals( 0070, UnixStat.PERM_MASK & ze.getUnixMode() );
                }
                else
                {
                    assertEquals( 0500, UnixStat.PERM_MASK & ze.getUnixMode() );
                }
            }
            else
            {
                if ( ze.getName().equals( "one.txt" ) )
                {
                    assertEquals( 0640, UnixStat.PERM_MASK & ze.getUnixMode() );
                }
                else if ( ze.getName().equals( "two.txt" ) )
                {
                    assertEquals( 0664, UnixStat.PERM_MASK & ze.getUnixMode() );
                }
                else
                {
                    assertEquals( 0400, UnixStat.PERM_MASK & ze.getUnixMode() );
                }
            }

        }
    }

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
        assertEquals(l2, l3);
	}

	// Used to investigate extrafields
	public void testLookAtExtraZipFields_from_macos() throws IOException {
		FileInputStream fis = new FileInputStream("src/test/resources/zip-timestamp/macOsZipFile.zip");
		ZipInputStream zis = new ZipInputStream(fis);
		final java.util.zip.ZipEntry evenEntry = zis.getNextEntry();
		final ZipExtraField[] parse = ExtraFieldUtils.parse(evenEntry.getExtra());
		System.out.println(Arrays.asList(parse));
		final java.util.zip.ZipEntry oddEntry = zis.getNextEntry();

		System.out.println(Arrays.asList(ExtraFieldUtils.parse(oddEntry.getExtra())));

		System.out.println("oddEntry.getTime() = " + new Date(oddEntry.getTime()).toString());
		System.out.println("oddEntry.getName() = " + oddEntry.getName());
		System.out.println("new String(oddEntry.getExtra()) = " + new String(oddEntry.getExtra()));
		System.out.println("evenEntry.getName() = " + evenEntry.getName());
		System.out.println("evenEntry.getTime() = " + new Date(evenEntry.getTime()).toString());
		System.out.println("new String(evenEntry.getExtra()) = " + new String(evenEntry.getExtra()));

	}

	// Used to investigate date roundtrip behaviour across zip versions
	public void testZipStuff() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		// name the file inside the zip  file
		final File oddFile = new File("src/test/resources/zip-timestamp/file-with-odd-time.txt");
		final File evenFile = new File("src/test/resources/zip-timestamp/file-with-even-time.txt");
		final ZipEntry oddZe = new ZipEntry(oddFile.getName());
		oddZe.setTime(oddFile.lastModified());
		zos.putNextEntry(oddZe);
		final ZipEntry evenZe = new ZipEntry(evenFile.getName());
		evenZe.setTime(evenFile.lastModified());
		zos.putNextEntry(evenZe);
		zos.close();

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ZipInputStream zipInputStream = new ZipInputStream(bais);
		final java.util.zip.ZipEntry oddEntry = zipInputStream.getNextEntry();
		System.out.println("oddEntry.getTime() = " + new Date(oddEntry.getTime()).toString());
		System.out.println("oddEntry.getName() = " + oddEntry.getName());
		final java.util.zip.ZipEntry evenEntry = zipInputStream.getNextEntry();
		System.out.println("evenEntry.getName() = " + evenEntry.getName());
		System.out.println("evenEntry.getTime() = " + new Date(evenEntry.getTime()).toString());
	}


	public void notestJustThatOne()
			throws Exception
	{
		final File srcDir = new File("src");
		String[] inc = { "test/java/org/codehaus/plexus/archiver/zip/ZipShortTest.java"};
		final File zipFile = new File( "target/output/zz1.zip" );

		final File zipFile2 = new File( "target/output/zz2.zip" );
		ZipArchiver zipArchiver2 = (ZipArchiver) lookup( Archiver.ROLE, "zip" );
		zipArchiver2.setDestFile( zipFile2 );

		// Bugbug: This does not work on 1.8....?
		zipArchiver2.addArchivedFileSet( zipFile );
		FileUtils.removePath(zipFile2.getPath());
		zipArchiver2.createArchive();
	}

	public void testCreateResourceCollection()
        throws Exception
    {
        final File srcDir = new File("src");
        final File zipFile = new File( "target/output/src.zip" );
        ZipArchiver zipArchiver = (ZipArchiver) lookup( Archiver.ROLE, "zip" );
        zipArchiver.setDestFile( zipFile );
        zipArchiver.addDirectory( srcDir, null, FileUtils.getDefaultExcludes() );
        FileUtils.removePath( zipFile.getPath() );
        zipArchiver.createArchive();

        final File zipFile2 = new File( "target/output/src2.zip" );
        ZipArchiver zipArchiver2 = (ZipArchiver) lookup( Archiver.ROLE, "zip" );
        zipArchiver2.setDestFile( zipFile2 );
        zipArchiver2.addArchivedFileSet( zipFile, "prfx/" );
        FileUtils.removePath( zipFile2.getPath() );
        zipArchiver2.createArchive();

        final org.apache.commons.compress.archivers.zip.ZipFile cmp1 = new org.apache.commons.compress.archivers.zip.ZipFile( zipFile );
        final org.apache.commons.compress.archivers.zip.ZipFile cmp2 = new org.apache.commons.compress.archivers.zip.ZipFile( zipFile2 );
        ArchiveFileComparator.assertEquals( cmp1, cmp2, "prfx/" );
        cmp1.close();
        cmp2.close();
    }
}
