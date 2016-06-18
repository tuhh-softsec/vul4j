package org.codehaus.plexus.archiver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Enumeration;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.tar.TarArchiver;
import org.codehaus.plexus.archiver.tar.TarLongFileMode;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author Erik Engstrom
 */
public class DuplicateFilesTest
    extends PlexusTestCase
{

    private static final File file1 = getTestFile( "src/test/resources/group-writable/foo.txt" );

    private static final File file2 = getTestFile( "src/test/resources/world-writable/foo.txt" );

    private static final File destination = getTestFile( "target/output/duplicateFiles" );

    public void setUp()
        throws Exception
    {
        super.setUp();
        getContainer().getLoggerManager().setThreshold( Logger.LEVEL_DEBUG );
    }

    public void testZipArchiver()
        throws Exception
    {
        Archiver archiver = (Archiver) lookup( Archiver.ROLE, "zip" );
        archiver.setDuplicateBehavior( Archiver.DUPLICATES_SKIP );

        File archive = createArchive( archiver, "zip" );

        org.apache.commons.compress.archivers.zip.ZipFile zf =
            new org.apache.commons.compress.archivers.zip.ZipFile( archive );

        Enumeration e = zf.getEntries();
        int entryCount = 0;
        while ( e.hasMoreElements() )
        {
            ZipArchiveEntry entry = (ZipArchiveEntry) e.nextElement();
            System.out.println( entry.getName() );
            entryCount++;
        }
        // Zip file should have 2 entries, 1 for the directory and one for foo.txt
        assertEquals( 2, entryCount );
        testArchive( archive, "zip" );
    }

    public void testDirArchiver()
        throws Exception
    {
        Archiver archiver = (Archiver) lookup( Archiver.ROLE, "dir" );
        createArchive( archiver, "dir" );
        testFinalFile( "target/output/duplicateFiles.dir/duplicateFiles/foo.txt" );

    }

    public void testTarArchiver()
        throws Exception
    {
        TarArchiver archiver = (TarArchiver) lookup( Archiver.ROLE, "tar" );
        archiver.setLongfile( TarLongFileMode.posix );
        archiver.setDuplicateBehavior( Archiver.DUPLICATES_SKIP );

        File archive = createArchive( archiver, "tar" );
        TarArchiveInputStream tis;

        tis = new TarArchiveInputStream( new BufferedInputStream( new FileInputStream( archive ) ) );
        int entryCount = 0;
        while ( ( tis.getNextEntry() ) != null )
        {
            entryCount++;
        }
        assertEquals( 1, entryCount );
        testArchive( archive, "tar" );
        tis.close();
    }

    private File createArchive( Archiver archiver, String outputFileExt )
        throws Exception
    {
        archiver.addFile( file1, "duplicateFiles/foo.txt" );
        archiver.addFile( file2, "duplicateFiles/foo.txt" );

        // delete it if it exists to ensure it is actually empty
        if ( destination.exists() )
        {
            destination.delete();
        }

        File archive = getTestFile( "target/output/duplicateFiles." + outputFileExt );
        if ( archive.exists() )
        {
            if ( archive.isDirectory() )
            {
                FileUtils.deleteDirectory( archive );
            }
            else
            {
                archive.delete();
            }
        }

        archiver.setDestFile( archive );
        archiver.createArchive();
        return archive;
    }

    private void testArchive( File archive, String role )
        throws Exception
    {
        // Check the content of the archive by extracting it

        UnArchiver unArchiver = (UnArchiver) lookup( UnArchiver.ROLE, role );
        unArchiver.setSourceFile( archive );

        unArchiver.setDestDirectory( getTestFile( "target/output/" ) );
        unArchiver.extract();

        assertTrue( destination.exists() );
        assertTrue( destination.isDirectory() );
        testFinalFile( "target/output/duplicateFiles/foo.txt" );
    }

    private void testFinalFile( String path )
        throws Exception
    {
        File outputFile = getTestFile( path );
        assertTrue( outputFile.exists() );
        BufferedReader reader = new BufferedReader( new FileReader( outputFile ) );
        String firstLine = reader.readLine();
        reader.close();
        reader = new BufferedReader( new FileReader( file2 ) );
        String expectedFirstLine = reader.readLine();
        reader.close();
        assertEquals( expectedFirstLine, firstLine );
    }

}
