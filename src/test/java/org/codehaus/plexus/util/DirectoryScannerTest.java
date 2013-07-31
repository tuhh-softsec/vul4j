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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for testcases doing tests with files.
 * 
 * @author Dan T. Tran
 */
public class DirectoryScannerTest
    extends FileBasedTestCase
{
    private static String testDir = getTestDirectory().getPath();

    public void testCrossPlatformIncludesString()
        throws IOException, URISyntaxException
    {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir( new File( getTestResourcesDir() + File.separator + "directory-scanner" ).getCanonicalFile() );

        String fs;
        if ( File.separatorChar == '/' )
        {
            fs = "\\";
        }
        else
        {
            fs = "/";
        }

        ds.setIncludes( new String[] { "foo" + fs } );
        ds.addDefaultExcludes();
        ds.scan();

        String[] files = ds.getIncludedFiles();
        assertEquals( 1, files.length );
    }

    public void testCrossPlatformExcludesString()
        throws IOException, URISyntaxException
    {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir( new File( getTestResourcesDir() + File.separator + "directory-scanner" ).getCanonicalFile() );
        ds.setIncludes( new String[] { "**" } );

        String fs;
        if ( File.separatorChar == '/' )
        {
            fs = "\\";
        }
        else
        {
            fs = "/";
        }

        ds.setExcludes( new String[] { "foo" + fs } );
        ds.addDefaultExcludes();
        ds.scan();

        String[] files = ds.getIncludedFiles();
        assertEquals( 0, files.length );
    }

    private String getTestResourcesDir()
        throws URISyntaxException
    {
        ClassLoader cloader = Thread.currentThread().getContextClassLoader();
        URL resource = cloader.getResource( "test.txt" );
        if ( resource == null )
        {
            fail( "Cannot locate test-resources directory containing 'test.txt' in the classloader." );
        }

        File file = new File( new URI( resource.toExternalForm() ).normalize().getPath() );

        return file.getParent();
    }

    private void createTestFiles()
        throws IOException
    {
        FileUtils.mkdir( testDir );
        this.createFile( new File( testDir + "/scanner1.dat" ), 0 );
        this.createFile( new File( testDir + "/scanner2.dat" ), 0 );
        this.createFile( new File( testDir + "/scanner3.dat" ), 0 );
        this.createFile( new File( testDir + "/scanner4.dat" ), 0 );
        this.createFile( new File( testDir + "/scanner5.dat" ), 0 );
    }

    public void testGeneral()
        throws IOException
    {
        this.createTestFiles();

        String includes = "scanner1.dat,scanner2.dat,scanner3.dat,scanner4.dat,scanner5.dat";
        String excludes = "scanner1.dat,scanner2.dat";

        List fileNames = FileUtils.getFiles( new File( testDir ), includes, excludes, false );

        assertEquals( "Wrong number of results.", 3, fileNames.size() );
        assertTrue( "3 not found.", fileNames.contains( new File( "scanner3.dat" ) ) );
        assertTrue( "4 not found.", fileNames.contains( new File( "scanner4.dat" ) ) );
        assertTrue( "5 not found.", fileNames.contains( new File( "scanner5.dat" ) ) );

    }

    public void testIncludesExcludesWithWhiteSpaces()
        throws IOException
    {
        this.createTestFiles();

        String includes = "scanner1.dat,\n  \n,scanner2.dat  \n\r, scanner3.dat\n, \tscanner4.dat,scanner5.dat\n,";

        String excludes = "scanner1.dat,\n  \n,scanner2.dat  \n\r,,";

        List fileNames = FileUtils.getFiles( new File( testDir ), includes, excludes, false );

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

    public void testAntExcludesOverrideIncludes()
        throws IOException
    {
        printTestHeader();

        File dir = new File( testDir, "regex-dir" );
        dir.mkdirs();

        String[] excludedPaths = { "target/foo.txt" };

        createFiles( dir, excludedPaths );

        String[] includedPaths = { "src/main/resources/project/target/foo.txt" };

        createFiles( dir, includedPaths );

        DirectoryScanner ds = new DirectoryScanner();

        String[] includes = { "**/target/*" };
        String[] excludes = { "target/*" };

        // This doesn't work, since excluded patterns refine included ones, meaning they operate on
        // the list of paths that passed the included patterns, and can override them.
        // String[] includes = {"**src/**/target/**/*" };
        // String[] excludes = { "**/target/**/*" };

        ds.setIncludes( includes );
        ds.setExcludes( excludes );
        ds.setBasedir( dir );
        ds.scan();

        assertInclusionsAndExclusions( ds.getIncludedFiles(), excludedPaths, includedPaths );
    }

    public void testAntExcludesOverrideIncludesWithExplicitAntPrefix()
        throws IOException
    {
        printTestHeader();

        File dir = new File( testDir, "regex-dir" );
        dir.mkdirs();

        String[] excludedPaths = { "target/foo.txt" };

        createFiles( dir, excludedPaths );

        String[] includedPaths = { "src/main/resources/project/target/foo.txt" };

        createFiles( dir, includedPaths );

        DirectoryScanner ds = new DirectoryScanner();

        String[] includes =
            { SelectorUtils.ANT_HANDLER_PREFIX + "**/target/**/*" + SelectorUtils.PATTERN_HANDLER_SUFFIX };
        String[] excludes = { SelectorUtils.ANT_HANDLER_PREFIX + "target/**/*" + SelectorUtils.PATTERN_HANDLER_SUFFIX };

        // This doesn't work, since excluded patterns refine included ones, meaning they operate on
        // the list of paths that passed the included patterns, and can override them.
        // String[] includes = {"**src/**/target/**/*" };
        // String[] excludes = { "**/target/**/*" };

        ds.setIncludes( includes );
        ds.setExcludes( excludes );
        ds.setBasedir( dir );
        ds.scan();

        assertInclusionsAndExclusions( ds.getIncludedFiles(), excludedPaths, includedPaths );
    }

    public void testRegexIncludeWithExcludedPrefixDirs()
        throws IOException
    {
        printTestHeader();

        File dir = new File( testDir, "regex-dir" );
        dir.mkdirs();

        String[] excludedPaths = { "src/main/foo.txt" };

        createFiles( dir, excludedPaths );

        String[] includedPaths = { "src/main/resources/project/target/foo.txt" };

        createFiles( dir, includedPaths );

        String regex = ".+/target.*";

        DirectoryScanner ds = new DirectoryScanner();

        String includeExpr = SelectorUtils.REGEX_HANDLER_PREFIX + regex + SelectorUtils.PATTERN_HANDLER_SUFFIX;

        String[] includes = { includeExpr };
        ds.setIncludes( includes );
        ds.setBasedir( dir );
        ds.scan();

        assertInclusionsAndExclusions( ds.getIncludedFiles(), excludedPaths, includedPaths );
    }

    public void testRegexExcludeWithNegativeLookahead()
        throws IOException
    {
        printTestHeader();

        File dir = new File( testDir, "regex-dir" );
        try
        {
            FileUtils.deleteDirectory( dir );
        }
        catch ( IOException e )
        {
        }

        dir.mkdirs();

        String[] excludedPaths = { "target/foo.txt" };

        createFiles( dir, excludedPaths );

        String[] includedPaths = { "src/main/resources/project/target/foo.txt" };

        createFiles( dir, includedPaths );

        String regex = "(?!.*src/).*target.*";

        DirectoryScanner ds = new DirectoryScanner();

        String excludeExpr = SelectorUtils.REGEX_HANDLER_PREFIX + regex + SelectorUtils.PATTERN_HANDLER_SUFFIX;

        String[] excludes = { excludeExpr };
        ds.setExcludes( excludes );
        ds.setBasedir( dir );
        ds.scan();

        assertInclusionsAndExclusions( ds.getIncludedFiles(), excludedPaths, includedPaths );
    }

    public void testRegexWithSlashInsideCharacterClass()
        throws IOException
    {
        printTestHeader();

        File dir = new File( testDir, "regex-dir" );
        try
        {
            FileUtils.deleteDirectory( dir );
        }
        catch ( IOException e )
        {
        }

        dir.mkdirs();

        String[] excludedPaths = { "target/foo.txt", "target/src/main/target/foo.txt" };

        createFiles( dir, excludedPaths );

        String[] includedPaths = { "module/src/main/target/foo.txt" };

        createFiles( dir, includedPaths );

        // NOTE: The portion "[^/]" is the interesting part of this pattern.
        String regex = "(?!((?!target/)[^/]+/)*src/).*target.*";

        DirectoryScanner ds = new DirectoryScanner();

        String excludeExpr = SelectorUtils.REGEX_HANDLER_PREFIX + regex + SelectorUtils.PATTERN_HANDLER_SUFFIX;

        String[] excludes = { excludeExpr };
        ds.setExcludes( excludes );
        ds.setBasedir( dir );
        ds.scan();

        assertInclusionsAndExclusions( ds.getIncludedFiles(), excludedPaths, includedPaths );
    }

    private void printTestHeader()
    {
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        System.out.println( "Test: " + ste.getMethodName() );
    }

    private void assertInclusionsAndExclusions( String[] files, String[] excludedPaths, String[] includedPaths )
    {
        Arrays.sort( files );

        System.out.println( "Included files: " );
        for ( int i = 0; i < files.length; i++ )
        {
            System.out.println( files[i] );
        }

        List failedToExclude = new ArrayList();
        for ( int i = 0; i < excludedPaths.length; i++ )
        {
            String alt = excludedPaths[i].replace( '/', '\\' );
            System.out.println( "Searching for exclusion as: " + excludedPaths[i] + "\nor: " + alt );
            if ( Arrays.binarySearch( files, excludedPaths[i] ) > -1 || Arrays.binarySearch( files, alt ) > -1 )
            {
                failedToExclude.add( excludedPaths[i] );
            }
        }

        List failedToInclude = new ArrayList();
        for ( int i = 0; i < includedPaths.length; i++ )
        {
            String alt = includedPaths[i].replace( '/', '\\' );
            System.out.println( "Searching for inclusion as: " + includedPaths[i] + "\nor: " + alt );
            if ( Arrays.binarySearch( files, includedPaths[i] ) < 0 && Arrays.binarySearch( files, alt ) < 0 )
            {
                failedToInclude.add( includedPaths[i] );
            }
        }

        StringBuilder buffer = new StringBuilder();
        if ( !failedToExclude.isEmpty() )
        {
            buffer.append( "Should NOT have included:\n" ).append(
                                                                   StringUtils.join( failedToExclude.iterator(),
                                                                                     "\n\t- " ) );
        }

        if ( !failedToInclude.isEmpty() )
        {
            if ( buffer.length() > 0 )
            {
                buffer.append( "\n\n" );
            }

            buffer.append( "Should have included:\n" )
                  .append( StringUtils.join( failedToInclude.iterator(), "\n\t- " ) );
        }

        if ( buffer.length() > 0 )
        {
            fail( buffer.toString() );
        }
    }

    private void createFiles( File dir, String[] paths )
        throws IOException
    {
        for ( int i = 0; i < paths.length; i++ )
        {
            String path = paths[i].replace( '/', File.separatorChar ).replace( '\\', File.separatorChar );
            File file = new File( dir, path );

            if ( path.endsWith( File.separator ) )
            {
                file.mkdirs();
            }
            else
            {
                if ( file.getParentFile() != null )
                {
                    file.getParentFile().mkdirs();
                }

                createFile( file, 0 );
            }
        }
    }
}
