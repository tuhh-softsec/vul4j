package org.codehaus.plexus.archiver.zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.codehaus.plexus.archiver.ArchiveFile;
import org.codehaus.plexus.util.IOUtil;
import org.junit.Assert;

/**
 * A utility class, which allows to compare archive files.
 */
public class ArchiveFileComparator
{

    public interface TarArchiveEntryConsumer
    {

        void accept( TarArchiveEntry entry ) throws IOException;

    }

    public static void forEachTarArchiveEntry( ArchiveFile file, TarArchiveEntryConsumer consumer )
        throws IOException
    {
        for ( java.util.Enumeration en = file.getEntries(); en.hasMoreElements(); )
        {
            TarArchiveEntry ze = (TarArchiveEntry) en.nextElement();
            if ( !ze.isDirectory() )
            {
                consumer.accept( ze );
            }
        }
    }

    /**
     * Creates a map with the archive files contents. The map keys
     * are the names of file entries in the archive file. The map
     * values are the respective archive entries.
     */
    private static Map<String, TarArchiveEntry> getFileEntries( ArchiveFile file )
        throws IOException
    {
        final Map map = new HashMap();
        for ( java.util.Enumeration en = file.getEntries(); en.hasMoreElements(); )
        {
            TarArchiveEntry ze = (TarArchiveEntry) en.nextElement();
            if ( ze.isDirectory() )
            {
                continue;
            }
            if ( map.put( ze.getName(), ze ) != null )
            {
                Assert.fail( "Multiple archive file entries named " + ze.getName() + " found." );
            }
        }
        return map;
    }

    private static Map getFileEntries( ZipFile file )
        throws IOException
    {
        final Map map = new HashMap();
        for ( java.util.Enumeration en = file.getEntries(); en.hasMoreElements(); )
        {
            ZipArchiveEntry ze = (ZipArchiveEntry) en.nextElement();
            if ( ze.isDirectory() )
            {
                continue;
            }
            if ( map.put( ze.getName(), ze ) != null )
            {
                Assert.fail( "Multiple archive file entries named " + ze.getName() + " found." );
            }
        }
        return map;
    }

    /**
     * Called to compare the given archive entries.
     */
    private static void assertEquals( ArchiveFile file1, TarArchiveEntry entry1,
                                      ArchiveFile file2, TarArchiveEntry entry2 )
        throws IOException
    {
        Assert.assertEquals( entry1.isDirectory(), entry2.isDirectory() );
        Assert.assertEquals( entry1.isSymbolicLink(), entry2.isSymbolicLink() );
        Assert.assertEquals( entry1.getModTime().getTime(), entry2.getModTime().getTime() );

        final InputStream is1 = file1.getInputStream( entry1 );
        final InputStream is2 = file2.getInputStream( entry2 );
        final byte[] bytes1 = IOUtil.toByteArray( is1 );
        final byte[] bytes2 = IOUtil.toByteArray( is2 );
        Assert.assertTrue( Arrays.equals( bytes1, bytes2 ) );
        is1.close();
        is2.close();
    }

    private static void assertEquals( ArchiveFile file1, TarArchiveEntry entry1,
                                      ZipFile file2, ZipArchiveEntry entry2 )
        throws IOException
    {
        Assert.assertEquals( entry1.isDirectory(), entry2.isDirectory() );

        final InputStream is1 = file1.getInputStream( entry1 );
        final InputStream is2 = file2.getInputStream( entry2 );
        final byte[] bytes1 = IOUtil.toByteArray( is1 );
        final byte[] bytes2 = IOUtil.toByteArray( is2 );
        Assert.assertTrue( Arrays.equals( bytes1, bytes2 ) );
        is1.close();
        is2.close();
    }

    private static void assertEquals( ZipFile file1, ZipArchiveEntry entry1,
                                      ZipFile file2, ZipArchiveEntry entry2 )
        throws Exception
    {
        Assert.assertEquals( entry1.isDirectory(), entry2.isDirectory() );
        Assert.assertEquals( entry1.isUnixSymlink(), entry2.isUnixSymlink() );
        long timeDelta = entry1.getLastModifiedDate().getTime() - entry2.getLastModifiedDate().getTime();
        Assert.assertTrue( Math.abs( timeDelta ) <= 1000 );

        final InputStream is1 = file1.getInputStream( entry1 );
        final InputStream is2 = file2.getInputStream( entry2 );
        final byte[] bytes1 = IOUtil.toByteArray( is1 );
        final byte[] bytes2 = IOUtil.toByteArray( is2 );
        Assert.assertTrue( Arrays.equals( bytes1, bytes2 ) );
        is1.close();
        is2.close();
    }

    /**
     * Called to compare the given archive files.
     */
    public static void assertEquals( final ArchiveFile file1, final ArchiveFile file2, final String prefix )
        throws Exception
    {

        final Map<String, TarArchiveEntry> map2 = getFileEntries( file2 );
        forEachTarArchiveEntry( file1, new TarArchiveEntryConsumer()
        {

            public void accept( TarArchiveEntry ze1 )
                throws IOException
            {
                final String name1 = ze1.getName();
                final String name2 = prefix == null ? name1 : ( prefix + name1 );
                TarArchiveEntry ze2 = map2.remove( name2 );
                Assert.assertNotNull( ze2 );
                assertEquals( file1, ze1, file2, ze2 );

            }

        } );
        Assert.assertTrue( map2.isEmpty() );
    }

    public static void assertEquals( final ArchiveFile file1, final ZipFile file2, final String prefix )
        throws Exception
    {
        final Map<String, ZipArchiveEntry> map2 = getFileEntries( file2 );
        forEachTarArchiveEntry( file1, new TarArchiveEntryConsumer()
        {

            public void accept( TarArchiveEntry ze1 )
                throws IOException
            {
                final String name1 = ze1.getName();
                final String name2 = prefix == null ? name1 : ( prefix + name1 );
                ZipArchiveEntry ze2 = map2.remove( name2 );
                Assert.assertNotNull( ze2 );
                assertEquals( file1, ze1, file2, ze2 );
            }

        } );
        Assert.assertTrue( map2.isEmpty() );
    }

    public static void assertEquals( org.apache.commons.compress.archivers.zip.ZipFile file1,
                                     org.apache.commons.compress.archivers.zip.ZipFile file2, String prefix )
        throws Exception
    {
        final Map map1 = getFileEntries( file1 );
        final Map map2 = getFileEntries( file2 );
        for ( Object o : map1.keySet() )
        {
            final String name1 = (String) o;
            final String name2 = prefix == null ? name1 : ( prefix + name1 );
            ZipArchiveEntry ze1 = (ZipArchiveEntry) map1.get( name1 );
            ZipArchiveEntry ze2 = (ZipArchiveEntry) map2.remove( name2 );
            Assert.assertNotNull( "Missing " + name1 + "in file 2", ze2 );
            assertEquals( file1, ze1, file2, ze2 );
        }
        Assert.assertTrue( map2.isEmpty() );
    }

}
