package org.codehaus.plexus.archiver.zip;

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.Archiver;

public class ZipArchiverTest extends PlexusTestCase
{
    public ZipArchiverTest( String testName )
    {
        super( testName );
    }
    
    public void testCreateArchive() throws Exception
    {
        ZipArchiver archiver = (ZipArchiver) lookup( Archiver.ROLE, "zip" );
        archiver.addDirectory( new File( "src" ) );
        archiver.setDestFile( new File( "target/output/archive.zip" ) );
        archiver.createArchive();
    }
}
