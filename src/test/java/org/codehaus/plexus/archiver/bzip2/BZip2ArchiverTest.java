package org.codehaus.plexus.archiver.bzip2;

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

public class BZip2ArchiverTest extends PlexusTestCase
{
    public BZip2ArchiverTest( String testName )
    {
        super( testName );
    }
    
    public void testCreateArchive() throws Exception
    {
        ZipArchiver zipArchiver = (ZipArchiver) lookup( Archiver.ROLE, "zip" );
        zipArchiver.addDirectory( new File( "src" ) );
        zipArchiver.setDestFile( new File( "target/output/archiveForbz2.zip" ) );
        zipArchiver.createArchive();
        BZip2Archiver archiver = (BZip2Archiver) lookup( Archiver.ROLE, "bzip2" );
        String[] inputFiles = new String[1];
        inputFiles[0] = "archiveForbz2.zip";
        archiver.addDirectory( new File( "target/output") , inputFiles, null );
        archiver.setDestFile( new File( "target/output/archive.bz2" ) );
        archiver.createArchive();
    }
}
