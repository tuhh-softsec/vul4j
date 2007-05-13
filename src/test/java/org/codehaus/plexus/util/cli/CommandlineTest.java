package org.codehaus.plexus.util.cli;

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
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.cli.shell.BourneShell;
import org.codehaus.plexus.util.cli.shell.CmdShell;
import org.codehaus.plexus.util.cli.shell.Shell;

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
            assertEquals( "\"cd .\"", cmd.toString() );
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
            assertEquals( "\"cd .\"", cmd.toString() );
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
            assertEquals( "\"echo Hello\"", cmd.toString() );
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
            assertEquals( "\"cd .\"", cmd.toString() );
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
            assertEquals( "\"more " + fileName + "\"", cmd.toString() );
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
        cmd.addArguments( new String[] { "a", "b" } );
        String[] shellCommandline = cmd.getShellCommandline();

        assertEquals( "Command line size", 4, shellCommandline.length );

        assertEquals( "cmd.exe", shellCommandline[0] );
        assertEquals( "/X", shellCommandline[1] );
        assertEquals( "/C", shellCommandline[2] );
        String expectedShellCmd = "\"c:" + File.separator + "Program Files" + File.separator + "xxx\" a b";
        expectedShellCmd = "\"" + expectedShellCmd + "\"";
        assertEquals( expectedShellCmd, shellCommandline[3] );
    }

    public void testGetShellCommandLineWindowsWithSeveralQuotes()
        throws Exception
    {
        Commandline cmd = new Commandline( new CmdShell() );
        cmd.setExecutable( "c:\\Program Files\\xxx" );
        cmd.addArguments( new String[] { "c:\\Documents and Settings\\whatever", "b" } );
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
        cmd.addArguments( new String[] { "hello world" } );

        String[] shellCommandline = cmd.getShellCommandline();

        assertEquals( "Command line size", 3, shellCommandline.length );

        assertEquals( "/bin/bash", shellCommandline[0] );
        assertEquals( "-c", shellCommandline[1] );
        String expectedShellCmd = "/bin/echo \"hello world\"";
        if (Os.isFamily( "windows" ))
        {
            expectedShellCmd = "\\bin\\echo \"hello world\"";
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
        cmd.addArguments( new String[] { "\'hello world\'" } );

        String[] shellCommandline = cmd.getShellCommandline();

        assertEquals( "Command line size", 3, shellCommandline.length );

        assertEquals( "/bin/bash", shellCommandline[0] );
        assertEquals( "-c", shellCommandline[1] );
        String expectedShellCmd = "/bin/echo \'hello world\'";
        if (Os.isFamily( "windows" ))
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
        cmd.addArguments( new String[] { "a", "b" } );
        String[] shellCommandline = cmd.getShellCommandline();

        assertEquals( "Command line size", 3, shellCommandline.length );

        assertEquals( "/bin/bash", shellCommandline[0] );
        assertEquals( "-c", shellCommandline[1] );
        
        
        
        if ( Os.isFamily( "windows" ) )
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
}
