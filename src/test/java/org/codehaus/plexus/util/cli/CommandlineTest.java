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

import junit.framework.TestCase;

import java.io.File;

import org.codehaus.plexus.util.cli.shell.CmdShell;

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
        assertNotNull( "The system property basedir was not defined.", baseDir );
    }

    public void testCommandlineWithoutArgumentInConstructor()
    {
        try
        {
            Commandline cmd = new Commandline();
            cmd.setWorkingDirectory( baseDir );
            cmd.createArgument().setValue( "cd" );
            cmd.createArgument().setValue( "." );
            assertEquals( "cd .", cmd.toString() );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
    }

    public void testCommandlineWithArgumentInConstructor()
    {
        try
        {
            Commandline cmd = new Commandline( "cd ." );
            cmd.setWorkingDirectory( baseDir );
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
            Commandline cmd = new Commandline();
            cmd.setWorkingDirectory( baseDir );
            cmd.setExecutable( "echo" );
            assertEquals( "echo", cmd.getExecutable() );
            cmd.createArgument().setValue( "Hello" );
            assertEquals( "echo Hello", cmd.toString() );
            cmd.execute();
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
            Commandline cmd = new Commandline();
            cmd.setWorkingDirectory( baseDir );
            cmd.setExecutable( "echo" );
            cmd.createArgument().setLine( null );
            cmd.createArgument().setLine( "Hello" );
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
            Commandline cmd = new Commandline();
            cmd.setWorkingDirectory( baseDir );
            cmd.createArgument().setValue( "." );
            cmd.createArgument( true ).setValue( "cd" );
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
            Commandline cmd = new Commandline();
            cmd.setWorkingDirectory( baseDir );
            cmd.createArgument().setValue( "more" );
            File f = new File( "test.txt" );
            cmd.createArgument().setFile( f );
            String fileName = f.getAbsolutePath();
            if ( fileName.indexOf( " " ) >= 0 )
            {
                fileName = "\"" + fileName + "\"";
            }
            assertEquals( "more " + fileName, cmd.toString() );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
    }

    public void testQuoteArguments()
    {
        try
        {
            String result = Commandline.quoteArgument( "Hello" );
            System.out.println( result );
            assertEquals( "Hello", result );
            result = Commandline.quoteArgument( "Hello World" );
            System.out.println( result );
            assertEquals( "\"Hello World\"", result );
            result = Commandline.quoteArgument( "\"Hello World\"" );
            System.out.println( result );
            assertEquals( "\'\"Hello World\"\'", result );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
        try
        {
            Commandline.quoteArgument( "\"Hello \'World\'\'" );
            fail();
        }
        catch ( Exception e )
        {
        }
    }

    public void testGetShellCommandLineWindows()
        throws Exception
    {
        Commandline cmd = new Commandline();
        cmd.setShell( new CmdShell() );
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
        Commandline cmd = new Commandline();
        cmd.setShell( new CmdShell() );
        cmd.setExecutable( "c:\\Program Files\\xxx" );
        cmd.addArguments( new String[] { "c:\\Documents and Settings\\whatever", "b" } );
        String[] shellCommandline = cmd.getShellCommandline();
    
        assertEquals( "Command line size", 4, shellCommandline.length );
    
        assertEquals( "cmd.exe", shellCommandline[0] );
        assertEquals( "/X", shellCommandline[1] );
        assertEquals( "/C", shellCommandline[2] );
        String expectedShellCmd = "\"c:" + File.separator + "Program Files" + File.separator + "xxx\" \"c:\\Documents and Settings\\whatever\" b";
        expectedShellCmd = "\"" + expectedShellCmd + "\"";
        assertEquals( expectedShellCmd, shellCommandline[3] );
    }

    /**
     * Test the command line generated for the bash shell
     * @throws Exception
     */
//    public void testGetShellCommandLineBash()
//        throws Exception
//    {
//        Commandline cmd = new Commandline();
//        cmd.setShell( "/bin/bash" );
//        cmd.setShellArgs( new String[] { "-c" } );
//        cmd.setExecutable( "/bin/echo" );
//        cmd.addArguments( new String[] { "hello world" } );
//        String[] shellCommandline = cmd.getShellCommandline();
//
//        assertEquals( "Command line size", 3, shellCommandline.length );
//
//        assertEquals( "/bin/bash", shellCommandline[0] );
//        assertEquals( "-c", shellCommandline[1] );
//        String expectedShellCmd = File.separator + "bin" + File.separator + "echo \\\"hello world\\\"";
//        expectedShellCmd = "\"" + expectedShellCmd + "\"";
//        assertEquals( expectedShellCmd, shellCommandline[2] );
//    }

    public void testGetShellCommandLineNonWindows()
        throws Exception
    {
        Commandline cmd = new Commandline();
        cmd.setShell( null );
        cmd.setExecutable( "/usr/bin" );
        cmd.addArguments( new String[] { "a", "b" } );
        String[] shellCommandline = cmd.getShellCommandline();

        assertEquals( "Command line size", 3, shellCommandline.length );

        assertEquals( File.separator + "usr" + File.separator + "bin", shellCommandline[0] );
        assertEquals( "a", shellCommandline[1] );
        assertEquals( "b", shellCommandline[2] );
    }

    public void testEnvironment()
        throws Exception
    {
        Commandline cmd = new Commandline();
        cmd.addEnvironment( "name", "value" );
        assertEquals( "name=value", cmd.getEnvironmentVariables()[0] );
    }
}
