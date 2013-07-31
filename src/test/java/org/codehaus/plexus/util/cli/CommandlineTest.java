package org.codehaus.plexus.util.cli;

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

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.shell.BourneShell;
import org.codehaus.plexus.util.cli.shell.CmdShell;
import org.codehaus.plexus.util.cli.shell.Shell;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;

public class CommandlineTest
    extends TestCase
{
    private String baseDir;

    /**
     * @param testName
     */
    public CommandlineTest( final String testName )
    {
        super( testName );
    }

    /*
     * @see TestCase#setUp()
     */
    public void setUp()
        throws Exception
    {
        super.setUp();
        baseDir = System.getProperty( "basedir" );

        if ( baseDir == null )
        {
            baseDir = new File( "." ).getCanonicalPath();
        }
    }

    public void testCommandlineWithoutCommandInConstructor()
    {
        try
        {
            Commandline cmd = new Commandline( new Shell() );
            cmd.setWorkingDirectory( baseDir );
            cmd.createArgument().setValue( "cd" );
            cmd.createArgument().setValue( "." );

            // NOTE: cmd.toString() uses CommandLineUtils.toString( String[] ), which *quotes* the result.
            assertEquals( "cd .", cmd.toString() );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
    }

    public void testCommandlineWithCommandInConstructor()
    {
        try
        {
            Commandline cmd = new Commandline( "cd .", new Shell() );
            cmd.setWorkingDirectory( baseDir );

            // NOTE: cmd.toString() uses CommandLineUtils.toString( String[] ), which *quotes* the result.
            assertEquals( "cd .", cmd.toString() );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
    }

    public void testExecute()
    {
        try
        {
            // allow it to detect the proper shell here.
            Commandline cmd = new Commandline();
            cmd.setWorkingDirectory( baseDir );
            cmd.setExecutable( "echo" );
            assertEquals( "echo", cmd.getShell().getOriginalExecutable() );
            cmd.createArgument().setValue( "Hello" );

            StringWriter swriter = new StringWriter();
            Process process = cmd.execute();

            Reader reader = new InputStreamReader( process.getInputStream() );

            char[] chars = new char[16];
            int read = -1;
            while ( ( read = reader.read( chars ) ) > -1 )
            {
                swriter.write( chars, 0, read );
            }

            String output = swriter.toString().trim();

            assertEquals( "Hello", output );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
    }

    public void testSetLine()
    {
        try
        {
            Commandline cmd = new Commandline( new Shell() );
            cmd.setWorkingDirectory( baseDir );
            cmd.setExecutable( "echo" );
            cmd.createArgument().setLine( null );
            cmd.createArgument().setLine( "Hello" );

            // NOTE: cmd.toString() uses CommandLineUtils.toString( String[] ), which *quotes* the result.
            assertEquals( "echo Hello", cmd.toString() );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
    }

    public void testCreateCommandInReverseOrder()
    {
        try
        {
            Commandline cmd = new Commandline( new Shell() );
            cmd.setWorkingDirectory( baseDir );
            cmd.createArgument().setValue( "." );
            cmd.createArgument( true ).setValue( "cd" );

            // NOTE: cmd.toString() uses CommandLineUtils.toString( String[] ), which *quotes* the result.
            assertEquals( "cd .", cmd.toString() );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
    }

    public void testSetFile()
    {
        try
        {
            Commandline cmd = new Commandline( new Shell() );
            cmd.setWorkingDirectory( baseDir );
            cmd.createArgument().setValue( "more" );
            File f = new File( "test.txt" );
            cmd.createArgument().setFile( f );
            String fileName = f.getAbsolutePath();
            if ( fileName.indexOf( " " ) >= 0 )
            {
                fileName = "\"" + fileName + "\"";
            }

            // NOTE: cmd.toString() uses CommandLineUtils.toString( String[] ), which *quotes* the result.
            assertEquals( "more " + fileName, cmd.toString() );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
    }

    public void testGetShellCommandLineWindows()
        throws Exception
    {
        Commandline cmd = new Commandline( new CmdShell() );
        cmd.setExecutable( "c:\\Program Files\\xxx" );
        cmd.addArguments( new String[] {
            "a",
            "b"
        } );
        String[] shellCommandline = cmd.getShellCommandline();

        assertEquals( "Command line size", 4, shellCommandline.length );

        assertEquals( "cmd.exe", shellCommandline[0] );
        assertEquals( "/X", shellCommandline[1] );
        assertEquals( "/C", shellCommandline[2] );
        String expectedShellCmd = "\"c:" + File.separator + "Program Files" + File.separator
                                  + "xxx\" a b";
        expectedShellCmd = "\"" + expectedShellCmd + "\"";
        assertEquals( expectedShellCmd, shellCommandline[3] );
    }

    public void testGetShellCommandLineWindowsWithSeveralQuotes()
        throws Exception
    {
        Commandline cmd = new Commandline( new CmdShell() );
        cmd.setExecutable( "c:\\Program Files\\xxx" );
        cmd.addArguments( new String[] {
            "c:\\Documents and Settings\\whatever",
            "b"
        } );
        String[] shellCommandline = cmd.getShellCommandline();

        assertEquals( "Command line size", 4, shellCommandline.length );

        assertEquals( "cmd.exe", shellCommandline[0] );
        assertEquals( "/X", shellCommandline[1] );
        assertEquals( "/C", shellCommandline[2] );
        String expectedShellCmd = "\"c:" + File.separator + "Program Files" + File.separator
                                  + "xxx\" \"c:\\Documents and Settings\\whatever\" b";
        expectedShellCmd = "\"" + expectedShellCmd + "\"";
        assertEquals( expectedShellCmd, shellCommandline[3] );
    }

    /**
     * Test the command line generated for the bash shell
     * @throws Exception
     */
    public void testGetShellCommandLineBash()
        throws Exception
    {
        Commandline cmd = new Commandline( new BourneShell() );
        cmd.setExecutable( "/bin/echo" );
        cmd.addArguments( new String[] {
            "hello world"
        } );

        String[] shellCommandline = cmd.getShellCommandline();

        assertEquals( "Command line size", 3, shellCommandline.length );

        assertEquals( "/bin/sh", shellCommandline[0] );
        assertEquals( "-c", shellCommandline[1] );
        String expectedShellCmd = "/bin/echo \'hello world\'";
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            expectedShellCmd = "\\bin\\echo \'hello world\'";
        }
        assertEquals( expectedShellCmd, shellCommandline[2] );
    }

    /**
    * Test the command line generated for the bash shell
    * @throws Exception
    */
    public void testGetShellCommandLineBash_WithWorkingDirectory()
        throws Exception
    {
        Commandline cmd = new Commandline( new BourneShell() );
        cmd.setExecutable( "/bin/echo" );
        cmd.addArguments( new String[] {
            "hello world"
        } );
        File root = File.listRoots()[0];
        File workingDirectory = new File( root, "path with spaces" );
        cmd.setWorkingDirectory( workingDirectory );

        String[] shellCommandline = cmd.getShellCommandline();

        assertEquals( "Command line size", 3, shellCommandline.length );

        assertEquals( "/bin/sh", shellCommandline[0] );
        assertEquals( "-c", shellCommandline[1] );
        String expectedShellCmd = "cd \"" + root.getAbsolutePath()
                                  + "path with spaces\" && /bin/echo \'hello world\'";
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            expectedShellCmd = "cd \"" + root.getAbsolutePath()
                               + "path with spaces\" && \\bin\\echo \'hello world\'";
        }
        assertEquals( expectedShellCmd, shellCommandline[2] );
    }

    /**
     * Test the command line generated for the bash shell
     * @throws Exception
     */
    public void testGetShellCommandLineBash_WithSingleQuotedArg()
        throws Exception
    {
        Commandline cmd = new Commandline( new BourneShell() );
        cmd.setExecutable( "/bin/echo" );
        cmd.addArguments( new String[] {
            "\'hello world\'"
        } );

        String[] shellCommandline = cmd.getShellCommandline();

        assertEquals( "Command line size", 3, shellCommandline.length );

        assertEquals( "/bin/sh", shellCommandline[0] );
        assertEquals( "-c", shellCommandline[1] );
        String expectedShellCmd = "/bin/echo \'hello world\'";
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            expectedShellCmd = "\\bin\\echo \'hello world\'";
        }
        assertEquals( expectedShellCmd, shellCommandline[2] );
    }

    public void testGetShellCommandLineNonWindows()
        throws Exception
    {
        Commandline cmd = new Commandline( new BourneShell() );
        cmd.setExecutable( "/usr/bin" );
        cmd.addArguments( new String[] {
            "a",
            "b"
        } );
        String[] shellCommandline = cmd.getShellCommandline();

        assertEquals( "Command line size", 3, shellCommandline.length );

        assertEquals( "/bin/sh", shellCommandline[0] );
        assertEquals( "-c", shellCommandline[1] );

        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            assertEquals( "\\usr\\bin a b", shellCommandline[2] );
        }
        else
        {
            assertEquals( "/usr/bin a b", shellCommandline[2] );
        }
    }

    public void testEnvironment()
        throws Exception
    {
        Commandline cmd = new Commandline();
        cmd.addEnvironment( "name", "value" );
        assertEquals( "name=value", cmd.getEnvironmentVariables()[0] );
    }

    public void testEnvironmentWitOverrideSystemEnvironment()
        throws Exception
    {
        Commandline cmd = new Commandline();
        cmd.addSystemEnvironment();
        cmd.addEnvironment( "JAVA_HOME", "/usr/jdk1.5" );
        String[] environmentVariables = cmd.getEnvironmentVariables();

        for ( int i = 0, size = environmentVariables.length; i < size; i++ )
        {
            if ( "JAVA_HOME=/usr/jdk1.5".equals( environmentVariables[i] ) )
            {
                return;
            }
        }

        fail( "can't find JAVA_HOME=/usr/jdk1.5" );
    }

    /**
     * Test an executable with a single apostrophe <code>'</code> in its path
     *
     * @throws Exception
     */
    public void testQuotedPathWithSingleApostrophe()
        throws Exception
    {
        File dir = new File( System.getProperty( "basedir" ), "target/test/quotedpath'test" );
        createAndCallScript( dir, "echo Quoted" );

        dir = new File( System.getProperty( "basedir" ), "target/test/quoted path'test" );
        createAndCallScript( dir, "echo Quoted" );
    }

    /**
     * Test an executable with a single quotation mark <code>\"</code> in its path only for non Windows box.
     *
     * @throws Exception
     */
    public void testQuotedPathWithQuotationMark()
        throws Exception
    {
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            System.out.println( "testQuotedPathWithQuotationMark() skipped on Windows" );
            return;
        }

        File dir = new File( System.getProperty( "basedir" ), "target/test/quotedpath\"test" );
        createAndCallScript( dir, "echo Quoted" );

        dir = new File( System.getProperty( "basedir" ), "target/test/quoted path\"test" );
        createAndCallScript( dir, "echo Quoted" );
    }

    /**
     * Test an executable with a single quotation mark <code>\"</code> and <code>'</code> in its path only for non
     * Windows box.
     *
     * @throws Exception
     */
    public void testQuotedPathWithQuotationMarkAndApostrophe()
        throws Exception
    {
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            System.out.println( "testQuotedPathWithQuotationMarkAndApostrophe() skipped on Windows" );
            return;
        }

        File dir = new File( System.getProperty( "basedir" ), "target/test/quotedpath\"'test" );
        createAndCallScript( dir, "echo Quoted" );

        dir = new File( System.getProperty( "basedir" ), "target/test/quoted path\"'test" );
        createAndCallScript( dir, "echo Quoted" );
    }

    /**
     * Test an executable with a quote in its path and no space
     *
     * @throws Exception
     */
    public void testOnlyQuotedPath()
        throws Exception
    {
        File dir = new File( System.getProperty( "basedir" ), "target/test/quotedpath\'test" );

        File javaHome = new File( System.getProperty( "java.home" ) );
        File java;
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            java = new File( javaHome, "/bin/java.exe" );
        }
        else
        {
            java = new File( javaHome, "/bin/java" );
        }

        if ( !java.exists() )
        {
            throw new IOException( java.getAbsolutePath() + " doesn't exist" );
        }

        createAndCallScript( dir, java.getAbsolutePath() + " -version" );
    }

    public void testDollarSignInArgumentPath()
        throws Exception
    {
        File dir = new File( System.getProperty( "basedir" ), "target/test" );
        if ( !dir.exists() )
        {
            assertTrue( "Can't create dir:" + dir.getAbsolutePath(), dir.mkdirs() );
        }

        FileWriter writer = null;
        try
        {
            writer = new FileWriter( new File( dir, "test$1.txt" ) );
            IOUtil.copy( "Success", writer );
        }
        finally
        {
            IOUtil.close( writer );
        }

        Commandline cmd = new Commandline();
        //cmd.getShell().setShellCommand( "/bin/sh" );
        cmd.getShell().setQuotedArgumentsEnabled( true );
        cmd.setExecutable( "cat" );
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            cmd.setExecutable( "dir" );
        }
        cmd.setWorkingDirectory( dir );
        cmd.createArg().setLine( "test$1.txt" );

        executeCommandLine( cmd );
    }

    public void testTimeOutException() throws Exception
    {
        File javaHome = new File( System.getProperty( "java.home" ) );
        File java;
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            java = new File( javaHome, "/bin/java.exe" );
        }
        else
        {
            java = new File( javaHome, "/bin/java" );
        }

        if ( !java.exists() )
        {
            throw new IOException( java.getAbsolutePath() + " doesn't exist" );
        }
        
        Commandline cli = new Commandline();
        cli.setExecutable( java.getAbsolutePath() );
        cli.createArg().setLine( "-version" );
        CommandLineUtils.StringStreamConsumer err = new CommandLineUtils.StringStreamConsumer();
        try
        {
            // if the os is faster than 1s to execute java -version the unit will fail :-)
            CommandLineUtils.executeCommandLine( cli, new DefaultConsumer(), err, 1 );
        }
        catch ( CommandLineTimeOutException e )
        {
            // it works
        }
        
    }
    
    /**
     * Make the file executable for Unix box.
     *
     * @param path not null
     * @throws IOException if any
     */
    private static void makeExecutable( File path )
        throws IOException
    {
        if ( path == null )
        {
            throw new IllegalArgumentException( "The file is null" );
        }

        if ( !path.isFile() )
        {
            throw new IllegalArgumentException( "The file '" + path.getAbsolutePath() + "' should be a file" );
        }

        if ( !Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            Process proc = Runtime.getRuntime().exec( new String[] { "chmod", "a+x", path.getAbsolutePath() } );
            while ( true )
            {
                try
                {
                    proc.waitFor();
                    break;
                }
                catch ( InterruptedException e )
                {
                    // ignore
                }
            }
        }
    }

    /**
     * Create and execute a script file in the given dir with the given content. The script file will be called
     * <code>echo.bat</code> for Windows box, otherwise <code>echo</code>.
     *
     * @param dir the parent dir where echo.bat or echo will be created
     * @param content the content of the script file
     * @throws Exception if any
     */
    private static void createAndCallScript( File dir, String content )
        throws Exception
    {
        if ( !dir.exists() )
        {
            assertTrue( "Can't create dir:" + dir.getAbsolutePath(), dir.mkdirs() );
        }

        // Create a script file
        File bat;
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            bat = new File( dir, "echo.bat" );
        }
        else
        {
            bat = new File( dir, "echo" );
        }

        Writer w = new FileWriter( bat );
        try
        {
            IOUtil.copy( content, w );
        }
        finally
        {
            IOUtil.close( w );
        }

        // Change permission
        makeExecutable( bat );

        Commandline cmd = new Commandline();
        cmd.setExecutable( bat.getAbsolutePath() );
        cmd.setWorkingDirectory( dir );

        // Execute the script file
        executeCommandLine( cmd );
    }

    /**
     * Execute the command line
     *
     * @param cmd not null
     * @throws Exception if any
     */
    private static void executeCommandLine( Commandline cmd )
        throws Exception
    {
        CommandLineUtils.StringStreamConsumer err = new CommandLineUtils.StringStreamConsumer();

        try
        {
            System.out.println( "Command line is: " + StringUtils.join( cmd.getShellCommandline(), " " ) );

            int exitCode = CommandLineUtils.executeCommandLine( cmd, new DefaultConsumer(), err );

            if ( exitCode != 0 )
            {
                String msg = "Exit code: " + exitCode + " - " + err.getOutput();
                throw new Exception( msg.toString() );
            }
        }
        catch ( CommandLineException e )
        {
            throw new Exception( "Unable to execute command: " + e.getMessage(), e );
        }
    }
}
