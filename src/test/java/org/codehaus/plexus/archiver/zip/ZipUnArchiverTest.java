package org.codehaus.plexus.archiver.zip;

import org.codehaus.plexus.PlexusTestCase;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public class ZipUnArchiverTest
    extends PlexusTestCase
{
    public void testExtractingADirectoryFromAJarFile()
        throws Exception
    {
        String s = "target/zip-unarchiver-tests";

        File testJar = new File( getBasedir(), "src/test/jars/test.jar" );

        File outputDirectory = new File( getBasedir(), s );

        ZipUnArchiver zu = new ZipUnArchiver( testJar );

        zu.extract( "resources/artifactId", outputDirectory );

        File f0 = new File( getBasedir(), s + "/resources/artifactId/test.properties" );

        assertTrue( f0.exists() );

        File f1 = new File( getBasedir(), s + "/resources/artifactId/directory/test.properties" );

        assertTrue( f1.exists() );
    }
}
