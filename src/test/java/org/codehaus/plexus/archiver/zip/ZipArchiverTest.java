package org.codehaus.plexus.archiver.zip;

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;

public class ZipArchiverTest extends PlexusTestCase
{
    public ZipArchiverTest( String testName )
    {
        super( testName );
    }
    
    public void testCreateArchive() throws Exception
    {
        ZipArchiver archiver = new ZipArchiver();
        archiver.addDirectory( new File( "src" ) );
        archiver.setDestFile( new File( "target/toto.zip" ) );
        archiver.createArchive();
    }
}
