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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

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
                        attrs = (PlexusIoResourceAttributes) attributesByPath.get( new File( tmpDir, path ).getAbsolutePath() );
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
                        attrs = (PlexusIoResourceAttributes) attributesByPath.get( new File( tmpDir, path ).getAbsolutePath() );
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
                        attrs = (PlexusIoResourceAttributes) attributesByPath.get( new File( tmpDir, path ).getAbsolutePath() );
                    }
                    
                    assertNotNull( attrs );
                    assertEquals( "Wrong mode for: " + path + "; expected: " + logMode, logMode, attrs.getOctalMode() );
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
            
            ZipFile zf = new ZipFile( zipFile2 );
            
            for ( int i = 0; i < executablePaths.length; i++ )
            {
                String path = executablePaths[i];
                ZipEntry ze = zf.getEntry( path );
                
                int mode = ze.getUnixMode() & UnixStat.PERM_MASK;
                
                assertEquals( "Wrong mode for: " + path + "; expected: " + exeMode, exeMode, mode );
            }
            
            for ( int i = 0; i < confPaths.length; i++ )
            {
                String path = confPaths[i];
                ZipEntry ze = zf.getEntry( path );
                
                int mode = ze.getUnixMode() & UnixStat.PERM_MASK;
                
                assertEquals( "Wrong mode for: " + path + "; expected: " + confMode, confMode, mode );
            }
            
            for ( int i = 0; i < logPaths.length; i++ )
            {
                String path = logPaths[i];
                ZipEntry ze = zf.getEntry( path );
                
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
        
        ArchiveEntryUtils.chmod( file, mode, logger );
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

        ZipFile zf = new ZipFile( archiver.getDestFile() );

        Enumeration e = zf.getEntries();

        while ( e.hasMoreElements() )
        {
            ZipEntry ze = (ZipEntry) e.nextElement();
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

        final ZipFile cmp1 = new ZipFile( zipFile );
        final ZipFile cmp2 = new ZipFile( zipFile2 );
        ArchiveFileComparator.assertEquals( cmp1, cmp2, "prfx/" );
        cmp1.close();
        cmp2.close();
    }
}
