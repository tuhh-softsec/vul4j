package org.codehaus.plexus.archiver.jar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.util.Streams;

/**
 * @author Kristian Rosenvold
 */
public class JdkManifestFactoryTest
    extends PlexusTestCase
{

    public void testGetDefaultManifest()
        throws Exception
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Manifest manifest = JdkManifestFactory.getDefaultManifest();
        manifest.write( byteArrayOutputStream );
        System.out.println( new String( byteArrayOutputStream.toByteArray() ) );
    }

    public void testGetDefaultManifestString()
        throws Exception
    {
        Manifest manifest = getManifest( "src/test/resources/manifests/manifestWithClassPath.mf" );
        Manifest manifestWithout = getManifest( "src/test/resources/manifests/manifest1.mf" );
        String value = manifest.getMainAttributes().getValue( ManifestConstants.ATTRIBUTE_CLASSPATH );
        System.out.println( "value = " + value );
        manifestWithout.getMainAttributes().putValue( ManifestConstants.ATTRIBUTE_CLASSPATH, value );
        String value2 = manifestWithout.getMainAttributes().getValue( ManifestConstants.ATTRIBUTE_CLASSPATH );

        assertEquals( value, value2 );
    }

    public void testIllegals()
        throws ManifestException, IOException
    {
        Manifest manifest = getManifest( "src/test/resources/manifests/manifest6.mf" );
        assertNotNull( manifest );

        try
        {
            getManifest( "src/test/resources/manifests/manifest5.mf" );
            fail( "We expect to fail" );
        }
        catch ( IOException ignore )
        {

        }
    }

    public void testMerge()
        throws ManifestException, IOException
    {
        Manifest manifest1 = getManifest( "src/test/resources/manifests/manifestMerge1.mf" );
        Manifest manifest2 = getManifest( "src/test/resources/manifests/manifestMerge2.mf" );

        Manifest target = new Manifest();
        JdkManifestFactory.merge( target, manifest1, false );

        assertEquals( "001", target.getMainAttributes().getValue( "Bar" ) );
        Attributes fudz = target.getAttributes( "Fudz" );
        assertNotNull( fudz );
        assertEquals( "002", fudz.getValue( "Bar" ) );
        Attributes redz = target.getAttributes( "Redz" );
        assertNotNull( redz );
        assertEquals( "002", redz.getValue( "Baz" ) );

        JdkManifestFactory.merge( target, manifest2, false );

        assertEquals( "001", target.getMainAttributes().getValue( "Bar" ) );
        fudz = target.getAttributes( "Fudz" );
        assertNotNull( fudz );
        assertEquals( "003", fudz.getValue( "Bar" ) );
        redz = target.getAttributes( "Redz" );
        assertNotNull( redz );
        assertEquals( "002", redz.getValue( "Baz" ) );
    }

    public void testDualClassPath()
        throws ManifestException, IOException
    {
        Manifest manifest = getManifest( "src/test/resources/manifests/manifestWithDualClassPath.mf" );
        final Attributes mainAttributes = manifest.getMainAttributes();
        final String attribute = mainAttributes.getValue( "Class-Path" );
        // assertEquals( "../config/ classes12.jar baz", attribute );
    }

    /**
     * Reads a Manifest file.
     */
    private java.util.jar.Manifest getManifest( String filename )
        throws IOException, ManifestException
    {
        InputStream r = Streams.fileInputStream( getTestFile( filename ) );

        try
        {
            return new Manifest( r );
        }
        finally
        {
            r.close();
        }
    }

}
