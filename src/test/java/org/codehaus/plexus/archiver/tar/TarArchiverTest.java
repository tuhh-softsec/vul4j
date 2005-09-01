package org.codehaus.plexus.archiver.tar;

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
import org.codehaus.plexus.archiver.UnixStat;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

/**
 * @author Emmanuel Venisse
 * @version $Id$
 */
public class TarArchiverTest
    extends PlexusTestCase
{
    public void testCreateArchive()
        throws Exception
    {
        TarArchiver archiver = (TarArchiver) lookup( Archiver.ROLE, "tar" );

        archiver.setDefaultDirectoryMode( 0500 );
        archiver.getOptions().setDirMode( 0500 );

        archiver.setDefaultFileMode( 0400 );
        archiver.getOptions().setMode( 0400 );

        archiver.addDirectory( getTestFile( "src" ) );
        archiver.setDefaultFileMode( 0640 );
        archiver.getOptions().setMode( 0640 );

        archiver.addFile( getTestFile( "src/test/resources/manifests/manifest1.mf" ), "one.txt" );
        archiver.addFile( getTestFile( "src/test/resources/manifests/manifest2.mf" ), "two.txt", 0664 );
        archiver.setDestFile( getTestFile( "target/output/archive.tar" ) );
        archiver.createArchive();

        TarInputStream tis;

        tis = new TarInputStream( new BufferedInputStream( new FileInputStream( archiver.getDestFile() ) ) );
        TarEntry te;

        while ( ( te = tis.getNextEntry() ) != null )
        {
            if ( te.isDirectory() )
            {
                assertEquals( 0500, te.getMode() & UnixStat.PERM_MASK );
            }
            else
            {
                if ( te.getName().equals( "one.txt" ) )
                {
                    assertEquals( 0640, te.getMode() & UnixStat.PERM_MASK );
                }
                else if ( te.getName().equals( "two.txt" ) )
                {
                    assertEquals( 0664, te.getMode() & UnixStat.PERM_MASK );
                }
                else
                {
                    assertEquals( 0400, te.getMode() & UnixStat.PERM_MASK );
                }

            }
        }

    }
}
