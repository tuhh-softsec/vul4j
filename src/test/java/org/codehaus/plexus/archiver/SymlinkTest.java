package org.codehaus.plexus.archiver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.dir.DirectoryArchiver;
import org.codehaus.plexus.archiver.tar.TarArchiver;
import org.codehaus.plexus.archiver.tar.TarLongFileMode;
import org.codehaus.plexus.archiver.tar.TarUnArchiver;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;
import org.codehaus.plexus.util.Os;

/**
 * @author Kristian Rosenvold
 */
public class SymlinkTest
    extends PlexusTestCase
{
    public void testSymlinkTar()
        throws Exception
    {
        TarArchiver archiver = (TarArchiver) lookup( Archiver.ROLE, "tar" );
        archiver.setLongfile( TarLongFileMode.posix );

        File dummyContent = getTestFile( "src/test/resources/symlinks/src" );
        archiver.addDirectory( dummyContent );
        final File archiveFile = new File( "target/output/symlinks.tar" );
        archiver.setDestFile( archiveFile );
        archiver.createArchive();
        File output = getTestFile( "target/output/untaredSymlinks" );
        output.mkdirs();
        TarUnArchiver unarchiver = (TarUnArchiver) lookup( UnArchiver.ROLE, "tar" );
        unarchiver.setSourceFile( archiveFile );
        unarchiver.setDestFile( output );
        unarchiver.extract();
    }

    public void testSymlinkZip()
        throws Exception
    {
        ZipArchiver archiver = (ZipArchiver) lookup( Archiver.ROLE, "zip" );

        File dummyContent = getTestFile( "src/test/resources/symlinks/src" );
        archiver.addDirectory( dummyContent );
        final File archiveFile = new File( "target/output/symlinks.zip" );
        archiveFile.delete();
        archiver.setDestFile( archiveFile );
        archiver.createArchive();

        File output = getTestFile( "target/output/unzippedSymlinks" );
        output.mkdirs();
        ZipUnArchiver unarchiver = (ZipUnArchiver) lookup( UnArchiver.ROLE, "zip" );
        unarchiver.setSourceFile( archiveFile );
        unarchiver.setDestFile( output );
        unarchiver.extract();
    }
}
