package org.codehaus.plexus.archiver.tar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.bzip2.BZip2Compressor;
import org.codehaus.plexus.archiver.gzip.GZipCompressor;
import org.codehaus.plexus.archiver.util.Compressor;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import static org.codehaus.plexus.components.io.resources.ResourceFactory.createResource;

/**
 * Test case for {@link TarFile}.
 */
public class TarFileTest
    extends PlexusTestCase
{

    private interface TarFileCreator
    {

        TarFile newTarFile( File file ) throws IOException;

    }

    /**
     * Test for the uncompressed tar file, {@link TarFile}.
     */
    public void testTarFile() throws Exception
    {
        testTarFile( null, null, new TarFileCreator()
        {

            @Override
            public TarFile newTarFile( File file )
                throws IOException
            {
                return new TarFile( file );
            }

        } );
    }

    /**
     * Test for the gzip compressed tar file, {@link GZipTarFile}.
     */
    public void testGZipTarFile() throws Exception
    {
        final GZipCompressor compressor = new GZipCompressor();
        testTarFile( compressor, ".gz", new TarFileCreator()
        {

            @Override
            public TarFile newTarFile( File file )
                throws IOException
            {
                return new GZipTarFile( file );
            }

        } );
    }

    /**
     * Test for the bzip2 compressed tar file, {@link BZip2TarFile}.
     */
    public void testBZip2TarFile() throws Exception
    {
        final BZip2Compressor compressor = new BZip2Compressor();
        testTarFile( compressor, ".bz2", new TarFileCreator()
        {

            @Override
            public TarFile newTarFile( File file )
                throws IOException
            {
                return new BZip2TarFile( file );
            }

        } );
    }

    private void testTarFile( Compressor compressor, String extension,
                              TarFileCreator tarFileCreator )
        throws Exception
    {
        File file = new File( "target/output/TarFileTest.tar" );
        final TarArchiver archiver = (TarArchiver) lookup( Archiver.ROLE, "tar" );
        archiver.setLongfile( TarLongFileMode.posix );
        archiver.setDestFile( file );
        archiver.addDirectory( new File( "src" ) );
        FileUtils.removePath( file.getPath() );
        archiver.createArchive();
        if ( compressor != null )
        {
            final File compressedFile = new File( file.getPath() + extension );
            compressor.setSource( createResource( file, file.getName() ) );
            compressor.setDestFile( compressedFile );
            compressor.compress();
            compressor.close();
            file = compressedFile;
        }
        final TarFile tarFile = tarFileCreator.newTarFile( file );

        for ( Enumeration en = tarFile.getEntries(); en.hasMoreElements(); )
        {
            final TarArchiveEntry te = (TarArchiveEntry) en.nextElement();
            if ( te.isDirectory() || te.isSymbolicLink() )
            {
                continue;
            }
            final File teFile = new File( "src", te.getName() );
            final InputStream teStream = tarFile.getInputStream( te );
            final InputStream fileStream = new FileInputStream( teFile );
            assertTrue( Arrays.equals( IOUtil.toByteArray( teStream ), IOUtil.toByteArray( fileStream ) ) );
            teStream.close();
            fileStream.close();
        }

        tarFile.close();
    }

}
