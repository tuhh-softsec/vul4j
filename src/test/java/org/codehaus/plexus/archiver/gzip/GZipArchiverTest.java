package org.codehaus.plexus.archiver.gzip;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

/**
 * @author Emmanuel Venisse
 * @version $Id$
 */
public class GZipArchiverTest
    extends PlexusTestCase
{
    public void testCreateArchive() throws Exception
    {
        ZipArchiver zipArchiver = (ZipArchiver) lookup( Archiver.ROLE, "zip" );
        zipArchiver.addDirectory( getTestFile( "src" ) );
        zipArchiver.setDestFile( getTestFile( "target/output/archiveForGzip.zip" ) );
        zipArchiver.createArchive();
        GZipArchiver archiver = (GZipArchiver) lookup( Archiver.ROLE, "gzip" );
        String[] inputFiles = new String[1];
        inputFiles[0] = "archiveForGzip.zip";
        archiver.addDirectory( getTestFile( "target/output") , inputFiles, null );
        archiver.setDestFile( getTestFile( "target/output/archive.gzip" ) );
        archiver.createArchive();
    }
}
