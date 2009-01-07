package org.codehaus.plexus.util;

/*
 * Copyright The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;

/**
 * Base class for testcases doing tests with files.
 *
 * @author Dan T. Tran
 */
public class DirectoryScannerTest
    extends FileBasedTestCase
{
    private static String testDir = getTestDirectory().getPath() ;

    private void createTestFiles()
        throws IOException
    {
        FileUtils.mkdir( testDir );
        this.createFile( new File ( testDir + "/scanner1.dat") , 0 );
        this.createFile( new File ( testDir + "/scanner2.dat") , 0 );
        this.createFile( new File ( testDir + "/scanner3.dat") , 0 );
        this.createFile( new File ( testDir + "/scanner4.dat") , 0 );
        this.createFile( new File ( testDir + "/scanner5.dat") , 0 );
    }

    public void testGeneral()
      throws IOException
    {
        this.createTestFiles();

        String includes = "scanner1.dat,scanner2.dat,scanner3.dat,scanner4.dat,scanner5.dat" ;
        String excludes = "scanner1.dat,scanner2.dat" ;

        List fileNames = FileUtils.getFiles( new File ( testDir ), includes, excludes, false );

        assertEquals( "Wrong number of results.", 3, fileNames.size() );
        assertTrue( "3 not found.", fileNames.contains( new File( "scanner3.dat" ) ) );
        assertTrue( "4 not found.", fileNames.contains( new File( "scanner4.dat" ) ) );
        assertTrue( "5 not found.", fileNames.contains( new File( "scanner5.dat" ) ) );

    }

    public void testIncludesExcludesWithWhiteSpaces()
      throws IOException
    {
        this.createTestFiles();

        String includes = "scanner1.dat,\n  \n,scanner2.dat  \n\r, scanner3.dat\n, \tscanner4.dat,scanner5.dat\n," ;

        String excludes = "scanner1.dat,\n  \n,scanner2.dat  \n\r,," ;

        List fileNames = FileUtils.getFiles( new File ( testDir ), includes, excludes, false );

        assertEquals( "Wrong number of results.", 3, fileNames.size() );
        assertTrue( "3 not found.", fileNames.contains( new File( "scanner3.dat" ) ) );
        assertTrue( "4 not found.", fileNames.contains( new File( "scanner4.dat" ) ) );
        assertTrue( "5 not found.", fileNames.contains( new File( "scanner5.dat" ) ) );
    }

    private void createTestDirectories()
        throws IOException
    {
        FileUtils.mkdir( testDir + File.separator + "directoryTest" );
        FileUtils.mkdir( testDir + File.separator + "directoryTest" + File.separator + "testDir123" );
        FileUtils.mkdir( testDir + File.separator + "directoryTest" + File.separator + "test_dir_123" );
        FileUtils.mkdir( testDir + File.separator + "directoryTest" + File.separator + "test-dir-123" );
        this.createFile( new File( testDir + File.separator + "directoryTest" + File.separator + "testDir123"
            + File.separator + "file1.dat" ), 0 );
        this.createFile( new File( testDir + File.separator + "directoryTest" + File.separator + "test_dir_123"
            + File.separator + "file1.dat" ), 0 );
        this.createFile( new File( testDir + File.separator + "directoryTest" + File.separator + "test-dir-123"
            + File.separator + "file1.dat" ), 0 );
    }

    public void testDirectoriesWithHyphens()
        throws IOException
    {
        this.createTestDirectories();

        DirectoryScanner ds = new DirectoryScanner();
        String[] includes = { "**/*.dat" };
        String[] excludes = { "" };
        ds.setIncludes( includes );
        ds.setExcludes( excludes );
        ds.setBasedir( new File( testDir + File.separator + "directoryTest" ) );
        ds.setCaseSensitive( true );
        ds.scan();

        String[] files = ds.getIncludedFiles();
        assertEquals( "Wrong number of results.", 3, files.length );
    }
}
