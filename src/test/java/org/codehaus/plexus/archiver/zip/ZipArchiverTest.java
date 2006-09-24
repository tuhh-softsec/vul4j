package org.codehaus.plexus.archiver.zip;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnixStat;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @author Emmanuel Venisse
 * @version $Id$
 */
public class ZipArchiverTest
    extends PlexusTestCase
{
    public void testCreateArchive()
        throws Exception
    {
        ZipArchiver archiver = newArchiver( "archive1.zip" );

        createArchive(archiver);
    }

    private ZipArchiver newArchiver( String name ) throws Exception {
    	ZipArchiver archiver = (ZipArchiver) lookup( Archiver.ROLE, "zip" );

    	archiver.setDefaultDirectoryMode( 0500 );
        archiver.setDefaultFileMode( 0400 );
        archiver.addDirectory( getTestFile( "src" ) );

        archiver.setDefaultFileMode( 0640 );
        archiver.addFile( getTestFile( "src/test/resources/manifests/manifest1.mf" ), "one.txt" );
        archiver.addFile( getTestFile( "src/test/resources/manifests/manifest2.mf" ), "two.txt", 0664 );

        // reset default file mode for files included from now on
        archiver.setDefaultFileMode( 0400 );
        archiver.setDefaultDirectoryMode( 0777 );
        archiver.addDirectory( getTestFile( "src/test/resources/world-writable/" ), "worldwritable/" );

        archiver.setDefaultDirectoryMode( 0070 );
        archiver.addDirectory( getTestFile( "src/test/resources/group-writable/" ), "groupwritable/" );

        archiver.setDestFile( getTestFile( "target/output/" + name ) );

        return archiver;
	}

	private void createArchive(ZipArchiver archiver) throws ArchiverException, IOException {
        archiver.createArchive();

        ZipFile zf = new ZipFile( archiver.getDestFile() );

        Enumeration e = zf.getEntries();

        while ( e.hasMoreElements() )
        {
            ZipEntry ze = (ZipEntry) e.nextElement();
            if ( ze.isDirectory() )
            {
                if ( ze.getName().startsWith( "worldwritable" ) )
                {
                    assertEquals( 0777, UnixStat.PERM_MASK & ze.getUnixMode() );
                }
                else if ( ze.getName().startsWith( "groupwritable" ) )
                {
                    assertEquals( 0070, UnixStat.PERM_MASK & ze.getUnixMode() );
                }
                else
                {
                    assertEquals( 0500, UnixStat.PERM_MASK & ze.getUnixMode() );
                }
            }
            else
            {
                if ( ze.getName().equals( "one.txt" ) )
                {
                    assertEquals( 0640, UnixStat.PERM_MASK & ze.getUnixMode() );
                }
                else if ( ze.getName().equals( "two.txt" ) )
                {
                    assertEquals( 0664, UnixStat.PERM_MASK & ze.getUnixMode() );
                }
                else
                {
                    assertEquals( 0400, UnixStat.PERM_MASK & ze.getUnixMode() );
                }
            }

        }
	}

	public void testForced()
		throws Exception
	{
        ZipArchiver archiver = newArchiver( "archive2.zip" );

        assertTrue( archiver.isForced() );
        File f = archiver.getDestFile();
        if ( f.exists() )
        {
            FileUtils.fileDelete( f.getPath() );
        }
        assertFalse( f.exists() );
        createArchive( archiver );
        long l1 = f.lastModified();
        assertTrue( f.exists() );

        Thread.sleep( 1 ); // Make sure, that we get a new timestamp
        createArchive( archiver );
        long l2 = f.lastModified();
        assertTrue( f.exists() );
        assertTrue( l2 > l1 );

        assertTrue( archiver.isSupportingForced() );
        archiver.setForced( false );
        assertFalse( archiver.isForced() );
        createArchive( archiver );
        long l3 = f.lastModified();
        assertTrue( f.exists() );
        assertEquals(l2, l3);
	}
}
