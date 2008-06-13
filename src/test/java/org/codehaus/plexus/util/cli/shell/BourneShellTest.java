package org.codehaus.plexus.util.cli.shell;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.Commandline;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class BourneShellTest
    extends TestCase
{

    protected Shell newShell()
    {
        return new BourneShell();
    }

    public void testQuoteWorkingDirectoryAndExecutable()
    {
        Shell sh = newShell();

        sh.setWorkingDirectory( "/usr/local/bin" );
        sh.setExecutable( "chmod" );

        String executable = StringUtils.join( sh.getShellCommandLine( new String[]{} ).iterator(), " " );

        assertEquals( "/bin/sh -c cd /usr/local/bin && chmod", executable );
    }

    public void testQuoteWorkingDirectoryAndExecutable_WDPathWithSingleQuotes()
    {
        Shell sh = newShell();

        sh.setWorkingDirectory( "/usr/local/'something else'" );
        sh.setExecutable( "chmod" );

        String executable = StringUtils.join( sh.getShellCommandLine( new String[]{} ).iterator(), " " );

        assertEquals( "/bin/sh -c cd \"/usr/local/\'something else\'\" && chmod", executable );
    }

    public void testQuoteWorkingDirectoryAndExecutable_WDPathWithSingleQuotes_BackslashFileSep()
    {
        Shell sh = newShell();

        sh.setWorkingDirectory( "\\usr\\local\\'something else'" );
        sh.setExecutable( "chmod" );

        String executable = StringUtils.join( sh.getShellCommandLine( new String[]{} ).iterator(), " " );

        assertEquals( "/bin/sh -c cd \"\\usr\\local\\\'something else\'\" && chmod", executable );
    }

    public void testPreserveSingleQuotesOnArgument()
    {
        Shell sh = newShell();

        sh.setWorkingDirectory( "/usr/bin" );
        sh.setExecutable( "chmod" );

        String[] args = { "\'some arg with spaces\'" };

        List shellCommandLine = sh.getShellCommandLine( args );

        String cli = StringUtils.join( shellCommandLine.iterator(), " " );
        System.out.println( cli );
        assertTrue( cli.endsWith( args[0] ) );
    }

    public void testAddSingleQuotesOnArgumentWithSpaces()
    {
        Shell sh = newShell();

        sh.setWorkingDirectory( "/usr/bin" );
        sh.setExecutable( "chmod" );

        String[] args = { "some arg with spaces" };

        List shellCommandLine = sh.getShellCommandLine( args );

        String cli = StringUtils.join( shellCommandLine.iterator(), " " );
        System.out.println( cli );
        assertTrue( cli.endsWith( "\'" + args[0] + "\'" ) );
    }
    
    public void testArgumentsWithsemicolon()
    {
        
        System.out.println( "---- semi colon tests ----" );
        
        Shell sh = newShell();

        sh.setWorkingDirectory( "/usr/bin" );
        sh.setExecutable( "chmod" );

        String[] args = { ";some&argwithunix$chars" };

        List shellCommandLine = sh.getShellCommandLine( args );

        String cli = StringUtils.join( shellCommandLine.iterator(), " " );
        System.out.println( cli );        
        assertTrue( cli.endsWith( "\'" + args[0] + "\'" ) );

        Commandline commandline = new Commandline(newShell());
        commandline.setExecutable( "chmod" );
        commandline.getShell().setQuotedArgumentsEnabled( true );
        commandline.createArgument().setValue( "--password" );
        commandline.createArgument().setValue( ";password");
        
        String[] lines = commandline.getShellCommandline();
        System.out.println( Arrays.asList( lines ));
        
        assertEquals( "/bin/sh", lines[0] );
        assertEquals( "-c", lines[1] );
        assertEquals( "chmod --password ';password'", lines[2] );

        commandline = new Commandline(newShell());
        commandline.setExecutable( "chmod" );
        commandline.getShell().setQuotedArgumentsEnabled( true );
        commandline.createArg().setValue( "--password" );
        commandline.createArg().setValue( ";password");
        lines = commandline.getShellCommandline();
        System.out.println( Arrays.asList( lines ));
        
        assertEquals( "/bin/sh", lines[0] );
        assertEquals( "-c", lines[1] );
        assertEquals( "chmod --password ';password'", lines[2] );       
        
        commandline = new Commandline(new CmdShell());
        commandline.getShell().setQuotedArgumentsEnabled( true );
        commandline.createArgument().setValue( "--password" );
        commandline.createArgument().setValue( ";password");
        lines = commandline.getShellCommandline();
        System.out.println( Arrays.asList( lines ));
        
        assertEquals( "cmd.exe", lines[0] );
        assertEquals( "/X", lines[1] );
        assertEquals( "/C", lines[2] );
        assertEquals( "\"--password ;password\"", lines[3] );
         
    }    

}
