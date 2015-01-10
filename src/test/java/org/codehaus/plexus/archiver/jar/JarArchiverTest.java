package org.codehaus.plexus.archiver.jar;

import org.codehaus.plexus.archiver.ArchiverException;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class JarArchiverTest
    extends TestCase
{

    public void testCreateManifestOnlyJar()
        throws IOException, ManifestException, ArchiverException
    {
        File jarFile = File.createTempFile( "JarArchiverTest.", ".jar" );
        jarFile.deleteOnExit();

        JarArchiver archiver = new JarArchiver();
        archiver.setDestFile( jarFile );

        Manifest manifest = new Manifest();
        Manifest.Attribute attribute = new Manifest.Attribute( "Main-Class", getClass().getName() );

        manifest.addConfiguredAttribute( attribute );

        archiver.addConfiguredManifest( manifest );

        archiver.createArchive();
    }

    public void testNonCompressed()
        throws IOException, ManifestException, ArchiverException
    {
        File jarFile = new File("target/output/jarArchiveNonCompressed.jar" );

        JarArchiver archiver = new JarArchiver();
        archiver.setDestFile( jarFile );
        archiver.setCompress( false );
        archiver.addDirectory( new File( "src/test/resources/mjar179" ) );
        archiver.createArchive();
    }

}
