package org.codehaus.plexus.archiver.tar;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.util.Streams;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="mailto:viktor@jv-ration.com">Viktor Sadovnikov</a>
 * @version $Revision$ $Date$
 */
public class TarUnArchiverTest extends PlexusTestCase
{

    private void runUnarchiver( FileSelector[] selectors, boolean[] results )
            throws Exception
    {
        String s = "target/tar-unarchiver-tests";

        File testJar = new File( getBasedir(), "src/test/jars/test.tar.gz" );

        File outputDirectory = new File( getBasedir(), s );

        TarUnArchiver tarUn = (TarUnArchiver) lookup( UnArchiver.ROLE, "tar.gz" );
        tarUn.setSourceFile(testJar);
        tarUn.setDestDirectory(outputDirectory);
        tarUn.setFileSelectors(selectors);

        FileUtils.deleteDirectory( outputDirectory );

        tarUn.extract( testJar.getAbsolutePath(), outputDirectory );

        assertFileExistance( s, "/resources/artifactId/test.properties", results[0]);
        assertFileExistance( s, "/resources/artifactId/directory/test.properties", results[1]);
        assertFileExistance( s, "/META-INF/MANIFEST.MF", results[2]);

    }

    private void assertFileExistance( String s, String file, boolean exists ) {
        File f0 = new File( getBasedir(), s + file );
        assertEquals( String.format("Did %s expect to find %s file", exists ? "" : "NOT", f0.getAbsoluteFile()), exists, f0.exists() );
    }

    public void testExtractingADirectory() throws Exception
    {
        runUnarchiver( null, new boolean[]{ true, true, true } );
    }

    public void testSelectors()
            throws Exception
    {
        IncludeExcludeFileSelector fileSelector = new IncludeExcludeFileSelector();
        runUnarchiver( new FileSelector[]{ fileSelector }, new boolean[]{ true, true, true } );

        fileSelector.setExcludes( new String[]{ "**/test.properties" } );
        runUnarchiver( new FileSelector[]{ fileSelector }, new boolean[]{ false, false, true } );

        fileSelector.setIncludes( new String[]{ "**/test.properties" } );
        fileSelector.setExcludes( null );
        runUnarchiver( new FileSelector[]{ fileSelector }, new boolean[]{ true, true, false } );

        fileSelector.setExcludes( new String[]{ "resources/artifactId/directory/test.properties" } );
        runUnarchiver( new FileSelector[]{ fileSelector }, new boolean[]{ true, false, false } );
    }


    private InputStream fis()
        throws FileNotFoundException
    {
        File file = new File( "/Users/kristian/testFile.tar" );
        return Streams.bufferedInputStream( new FileInputStream( file ) );
    }

    private InputStream fisub()
        throws FileNotFoundException
    {
        File file = new File( "/Users/kristian/testFile.tar" );
        return new FileInputStream( file );
    }

    public void testStreams()
        throws IOException
    {
        long start = System.currentTimeMillis();
        OutputStream bzin = new BZip2CompressorOutputStream( new NullOutputStream() );
        IOUtils.copy( fis(), bzin );
        System.out.println( "((System.currentTimeMillis() - start)) = " + ( ( System.currentTimeMillis() - start ) ) );
        start = System.currentTimeMillis();
        bzin = new BZip2CompressorOutputStream( new NullOutputStream() );
        IOUtils.copy( fisub(), bzin );
        System.out.println( "((System.currentTimeMillis() - start)) = " + ( ( System.currentTimeMillis() - start ) ) );
        start = System.currentTimeMillis();
        bzin = new BZip2CompressorOutputStream( new NullOutputStream() );
        IOUtils.copy( fis(), bzin );
        System.out.println( "((System.currentTimeMillis() - start)) = " + ( ( System.currentTimeMillis() - start ) ) );
        start = System.currentTimeMillis();
        bzin = new BZip2CompressorOutputStream( new NullOutputStream() );
        IOUtils.copy( fisub(), bzin );
        System.out.println( "((System.currentTimeMillis() - start)) = " + ( ( System.currentTimeMillis() - start ) ) );
    }
}
