package org.codehaus.plexus.archiver.zip;

import java.io.File;
import java.lang.reflect.Method;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author Jason van Zyl
 */
public class ZipUnArchiverTest
    extends PlexusTestCase
{

    public void testExtractingZipPreservesExecutableFlag()
        throws Exception
    {

        String s = "target/zip-unarchiver-tests";
        File testZip = new File( getBasedir(), "src/test/jars/test.zip" );
        File outputDirectory = new File( getBasedir(), s );

        FileUtils.deleteDirectory( outputDirectory );

        ZipUnArchiver zu = (ZipUnArchiver) lookup( UnArchiver.ROLE, "zip" );
        zu.setSourceFile( testZip );
        zu.extract( "", outputDirectory );
        File testScript = new File( outputDirectory, "test.sh" );

        final Method canExecute;
        try
        {
            canExecute = File.class.getMethod( "canExecute" );
            canExecute.invoke( testScript );
            assertTrue( (Boolean) canExecute.invoke( testScript ) );
        }
        catch ( NoSuchMethodException ignore )
        {
        }
    }

    public void testZeroFileModeInZip()
        throws Exception
    {

        String s = "target/zip-unarchiver-filemode-tests";
        File testZip = new File( getBasedir(), "src/test/resources/zeroFileMode/foobar.zip" );
        File outputDirectory = new File( getBasedir(), s );

        FileUtils.deleteDirectory( outputDirectory );

        ZipUnArchiver zu = (ZipUnArchiver) lookup( UnArchiver.ROLE, "zip" );
        zu.setSourceFile( testZip );
        zu.setIgnorePermissions( false );
        zu.extract( "", outputDirectory );


        File testScript = new File( outputDirectory, "foo.txt" );

        final Method canRead;
        try
        {
            canRead = File.class.getMethod( "canRead" );
            canRead.invoke( testScript );
            assertTrue( (Boolean) canRead.invoke( testScript ) );
        }
        catch ( NoSuchMethodException ignore )
        {
        }
    }
    private void runUnarchiver( String path, FileSelector[] selectors, boolean[] results )
        throws Exception
    {
        String s = "target/zip-unarchiver-tests";

        File testJar = new File( getBasedir(), "src/test/jars/test.jar" );

        File outputDirectory = new File( getBasedir(), s );

        ZipUnArchiver zu = (ZipUnArchiver) lookup( UnArchiver.ROLE, "zip" );
        zu.setSourceFile( testJar );
        zu.setFileSelectors( selectors );

        FileUtils.deleteDirectory( outputDirectory );

        zu.extract( path, outputDirectory );

        File f0 = new File( getBasedir(), s + "/resources/artifactId/test.properties" );

        assertEquals( results[0], f0.exists() );

        File f1 = new File( getBasedir(), s + "/resources/artifactId/directory/test.properties" );

        assertEquals( results[1], f1.exists() );

        File f2 = new File( getBasedir(), s + "/META-INF/MANIFEST.MF" );

        assertEquals( results[2], f2.exists() );
    }

    public void testExtractingADirectoryFromAJarFile()
        throws Exception
    {
        runUnarchiver( "resources/artifactId", null, new boolean[]{ true, true, false } );
        runUnarchiver( "", null, new boolean[]{ true, true, true } );
    }

    public void testSelectors()
        throws Exception
    {
        IncludeExcludeFileSelector fileSelector = new IncludeExcludeFileSelector();
        runUnarchiver( "", new FileSelector[]{ fileSelector }, new boolean[]{ true, true, true } );
        fileSelector.setExcludes( new String[]{ "**/test.properties" } );
        runUnarchiver( "", new FileSelector[]{ fileSelector }, new boolean[]{ false, false, true } );
        fileSelector.setIncludes( new String[]{ "**/test.properties" } );
        fileSelector.setExcludes( null );
        runUnarchiver( "", new FileSelector[]{ fileSelector }, new boolean[]{ true, true, false } );
        fileSelector.setExcludes( new String[]{ "resources/artifactId/directory/test.properties" } );
        runUnarchiver( "", new FileSelector[]{ fileSelector }, new boolean[]{ true, false, false } );
    }
}
