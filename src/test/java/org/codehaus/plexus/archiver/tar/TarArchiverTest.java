package org.codehaus.plexus.archiver.tar;

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.Archiver;

public class TarArchiverTest extends PlexusTestCase
{
    public TarArchiverTest( String testName )
    {
        super( testName );
    }
    
    public void testCreateArchive() throws Exception
    {
        TarArchiver archiver = (TarArchiver) lookup( Archiver.ROLE, "tar" );
        archiver.addDirectory( new File( "src" ) );
        archiver.setDestFile( new File( "target/output/archive.tar" ) );
        archiver.createArchive();
    }
}
