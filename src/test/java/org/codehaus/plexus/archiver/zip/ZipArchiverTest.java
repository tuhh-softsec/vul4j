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

import java.util.Enumeration;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.UnixStat;

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
        archiver.addDirectory( getTestFile( "src/test/resources/ww/" ), "worldwritable/" );
        
        archiver.setDefaultDirectoryMode( 0070 );
        archiver.addDirectory( getTestFile( "src/test/resources/gw/" ), "groupwritable/" );
        
        archiver.setDestFile( getTestFile( "target/output/archive.zip" ) );
        archiver.createArchive();
        
        ZipFile zf = new ZipFile( archiver.getDestFile() );
        
        Enumeration e = zf.getEntries();

        while ( e.hasMoreElements() )
        {
            ZipEntry ze = (ZipEntry) e.nextElement();
            if ( ze.isDirectory() )
            {
            	if ( ze.getName().startsWith( "worldwritable") )
            	{
            		assertEquals( 0777, UnixStat.PERM_MASK & ze.getUnixMode() );
            	}
            	else if ( ze.getName().startsWith( "groupwritable") )
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
            	if ( ze.getName().equals("one.txt"))
            	{
            		assertEquals( 0640, UnixStat.PERM_MASK & ze.getUnixMode() );
            	}
            	else if ( ze.getName().equals("two.txt"))
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
}
