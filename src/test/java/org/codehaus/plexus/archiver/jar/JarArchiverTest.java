package org.codehaus.plexus.archiver.jar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import org.codehaus.plexus.archiver.ArchiverException;
import org.junit.Test;

public class JarArchiverTest
    extends BaseJarArchiverTest
{

    @Test
    public void testCreateManifestOnlyJar()
        throws IOException, ManifestException, ArchiverException
    {
        File jarFile = File.createTempFile( "JarArchiverTest.", ".jar" );
        jarFile.deleteOnExit();

        JarArchiver archiver = getJarArchiver();
        archiver.setDestFile( jarFile );

        Manifest manifest = new Manifest();
        Manifest.Attribute attribute = new Manifest.Attribute( "Main-Class", getClass().getName() );

        manifest.addConfiguredAttribute( attribute );

        archiver.addConfiguredManifest( manifest );

        archiver.createArchive();
    }

    @Test
    public void testNonCompressed()
        throws IOException, ManifestException, ArchiverException
    {
        File jarFile = new File( "target/output/jarArchiveNonCompressed.jar" );

        JarArchiver archiver = getJarArchiver();
        archiver.setDestFile( jarFile );
        archiver.setCompress( false );
        archiver.addDirectory( new File( "src/test/resources/mjar179" ) );
        archiver.createArchive();
    }

    @Test
    public void testVeryLargeJar()
        throws IOException, ManifestException, ArchiverException
    {
        // Generate some number of random files that is likely to be
        // two or three times the number of available file handles
        File tmpDir = File.createTempFile( "veryLargeJar", null );
        tmpDir.delete();
        tmpDir.mkdirs();
        Random rand = new Random();
        for ( int i = 0; i < 45000; i++ )
        {
            File f = new File( tmpDir, "file" + i );
            f.deleteOnExit();
            FileOutputStream out = new FileOutputStream( f );
            byte[] data = new byte[ 512 ]; // 512bytes per file
            rand.nextBytes( data );
            out.write( data );
            out.flush();
            out.close();
        }

        File jarFile = new File( "target/output/veryLargeJar.jar" );

        JarArchiver archiver = getJarArchiver();
        archiver.setDestFile( jarFile );
        archiver.addDirectory( tmpDir );
        archiver.createArchive();
    }

    @Override
    protected JarArchiver getJarArchiver()
    {
        return new JarArchiver();
    }
}
