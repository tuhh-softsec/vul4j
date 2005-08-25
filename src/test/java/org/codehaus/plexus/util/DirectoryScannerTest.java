package org.codehaus.plexus.util;

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
}
