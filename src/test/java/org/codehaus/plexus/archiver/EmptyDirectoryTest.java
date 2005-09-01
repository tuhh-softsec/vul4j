package org.codehaus.plexus.archiver;

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

import java.io.File;

/**
 * @author Daniel Krisher
 * @version $Id$
 */
public class EmptyDirectoryTest
    extends PlexusTestCase
{
    public void testZipArchiver()
        throws Exception
    {
        testEmptyDirectory( "zip" );
    }

    public void testJarArchiver()
        throws Exception
    {
        // No JAR UnArchiver implementation :(
//        testEmptyDirectory( "jar" );
    }

    public void testTarArchiver()
        throws Exception
    {
        testEmptyDirectory( "tar" );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void testEmptyDirectory( String role )
        throws Exception
    {
        Archiver archiver = (Archiver) lookup( Archiver.ROLE, role );

        // Should default to true...
        assertTrue( archiver.getIncludeEmptyDirs() );

        // create an empty directory to store in the zip archive
        File emptyDir = getTestFile( "target/output/emptyTest/TmpEmptyDir" );

        // delete it if it exists to ensure it is actually empty
        if ( emptyDir.exists() )
        {
            emptyDir.delete();
        }
        emptyDir.mkdirs();
        archiver.addDirectory( emptyDir.getParentFile() );

        File archive = getTestFile( "target/output/emptyDirArchive.zip" );
        if ( archive.exists() )
        {
            archive.delete();
        }

        archiver.setDestFile( archive );
        archiver.createArchive();

        // delete the empty dir, we will extract it from the archive
        emptyDir.delete();

        // Check the content of the archive by extracting it

        UnArchiver unArchiver = (UnArchiver) lookup( UnArchiver.ROLE, role );
        unArchiver.setSourceFile( archive );

        unArchiver.setDestDirectory( getTestFile( "target/output/emptyTest" ) );
        unArchiver.extract();

        assertTrue( emptyDir.exists() );
        assertTrue( emptyDir.isDirectory() );
    }
}
