package org.codehaus.plexus.util.cli.shell;

import org.codehaus.plexus.util.StringUtils;

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

}
