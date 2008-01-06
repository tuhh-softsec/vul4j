package org.codehaus.plexus.archiver.zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.archiver.ArchiveFile;
import org.codehaus.plexus.archiver.ArchiveFile.Entry;
import org.codehaus.plexus.util.IOUtil;

import junit.framework.Assert;


/**
 * A utility class, which allows to compare archive files.
 */
public class ArchiveFileComparator
{
    /**
     * Creates a map with the archive files contents. The map keys
     * are the names of file entries in the archive file. The map
     * values are the respective archive entries.
     */
    private static Map getFileEntries( ArchiveFile file )
        throws IOException
    {
        final Map map = new HashMap();
        for ( java.util.Enumeration en = file.getEntries();  en.hasMoreElements();  )
        {
            Entry ze = (Entry) en.nextElement();
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
    private static void assertEquals( ArchiveFile file1, Entry entry1,
                                     ArchiveFile file2, Entry entry2 )
        throws Exception
    {
        Assert.assertEquals( entry1.isDirectory(), entry2.isDirectory() );
        Assert.assertEquals( entry1.getLastModificationTime(), entry2.getLastModificationTime() );

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
    public static void assertEquals( ArchiveFile file1, ArchiveFile file2, String prefix )
        throws Exception
    {
        final Map map1 = getFileEntries( file1 );
        final Map map2 = getFileEntries( file2 );
        for ( java.util.Iterator iter = map1.keySet().iterator();  iter.hasNext();  )
        {
            final String name1 = (String) iter.next();
            final String name2 = prefix == null ? name1 : (prefix + name1);
            Entry ze1 = (Entry) map1.get( name1 );
            Entry ze2 = (Entry) map2.remove( name2 );
            Assert.assertNotNull( ze2 );
            assertEquals( file1, ze1, file2, ze2 );
        }
        Assert.assertTrue( map2.isEmpty() );
    }
}
