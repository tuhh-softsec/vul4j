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

import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id$
 */
public abstract class CommandLineUtils
{
    private static Map processes = Collections.synchronizedMap( new HashMap() );

    static
    {
        Runtime.getRuntime().addShutdownHook( new Thread( "CommandlineUtil shutdown" )
        {
            public void run()
            {
                if ( ( processes != null ) && ( processes.size() > 0 ) )
                {
                    System.err.println( "Destroying " + processes.size() + " processes" );
                    for ( Iterator it = processes.values().iterator(); it.hasNext(); )
                    {
                        System.err.println( "Destroying process.." );
                        ( (Process) it.next() ).destroy();

                    }
                    System.err.println( "Destroyed " + processes.size() + " processes" );
                }
            }
        } );
    }

    public static class StringStreamConsumer
        implements StreamConsumer
    {
        private StringBuffer string = new StringBuffer();

        private String ls = System.getProperty( "line.separator" );

        public void consumeLine( String line )
        {
            string.append( line ).append( ls );
        }

        public String getOutput()
        {
            return string.toString();
        }
    }

    public static int executeCommandLine( Commandline cl, StreamConsumer systemOut, StreamConsumer systemErr )
        throws CommandLineException
    {
        return executeCommandLine( cl, null, systemOut, systemErr, 0 );
    }

    public static int executeCommandLine( Commandline cl, StreamConsumer systemOut, StreamConsumer systemErr, int timeoutInSeconds )
        throws CommandLineException
    {
        return executeCommandLine( cl, null, systemOut, systemErr, timeoutInSeconds );
    }

    public static int executeCommandLine( Commandline cl, InputStream systemIn, StreamConsumer systemOut, StreamConsumer systemErr )
        throws CommandLineException
    {
        return executeCommandLine( cl, systemIn, systemOut, systemErr, 0 );
    }

    /**
     * @param cl
     * @param systemIn
     * @param systemOut
     * @param systemErr
     * @param timeoutInSeconds
     * @return
     * @throws CommandLineException or CommandLineTimeOutException if time out occurs
     */
    public static int executeCommandLine( Commandline cl, InputStream systemIn, StreamConsumer systemOut, StreamConsumer systemErr, int timeoutInSeconds )
        throws CommandLineException
    {
        if ( cl == null )
        {
            throw new IllegalArgumentException( "cl cannot be null." );
        }

        Process p;

        p = cl.execute();

        processes.put( new Long( cl.getPid() ), p );

        StreamFeeder inputFeeder = null;

        if ( systemIn != null )
        {
            inputFeeder = new StreamFeeder( systemIn, p.getOutputStream() );
        }

        StreamPumper outputPumper = new StreamPumper( p.getInputStream(), systemOut );

        StreamPumper errorPumper = new StreamPumper( p.getErrorStream(), systemErr );

        if ( inputFeeder != null )
        {
            inputFeeder.start();
        }

        outputPumper.start();

        errorPumper.start();

        try
        {
            int returnValue;
            if ( timeoutInSeconds <= 0 )
            {
                returnValue = p.waitFor();
            }
            else
            {
                long now = System.currentTimeMillis();
                long timeoutInMillis = 1000L * timeoutInSeconds;
                long finish = now + timeoutInMillis;
                while ( isAlive( p ) && ( System.currentTimeMillis() < finish ) )
                {
                    Thread.sleep( 10 );
                }
                if ( isAlive( p ) )
                {
                    throw new InterruptedException( "Process timeout out after " + timeoutInSeconds + " seconds" );
                }
                returnValue = p.exitValue();
            }

            if ( inputFeeder != null )
            {
                synchronized ( inputFeeder )
                {
                    while ( !inputFeeder.isDone() )
                    {
                        inputFeeder.wait();
                    }
                }
            }

            synchronized ( outputPumper )
            {
                while ( !outputPumper.isDone() )
                {
                    outputPumper.wait();
                }
            }

            synchronized ( errorPumper )
            {
                while ( !errorPumper.isDone() )
                {
                    errorPumper.wait();
                }
            }

            processes.remove( new Long( cl.getPid() ) );

            return returnValue;
        }
        catch ( InterruptedException ex )
        {
            killProcess( cl.getPid() );
            throw new CommandLineTimeOutException( "Error while executing external command, process killed.", ex );
        }
        finally
        {
            if ( inputFeeder != null )
            {
                inputFeeder.close();
            }

            outputPumper.close();

            errorPumper.close();
        }
    }

    /**
     * Gets the shell environment variables for this process. Note that the returned mapping from variable names to
     * values will always be case-sensitive regardless of the platform, i.e. <code>getSystemEnvVars().get("path")</code>
     * and <code>getSystemEnvVars().get("PATH")</code> will in general return different values. However, on platforms
     * with case-insensitive environment variables like Windows, all variable names will be normalized to upper case.
     *
     * @return The shell environment variables, can be empty but never <code>null</code>.
     * @throws IOException If the environment variables could not be queried from the shell.
     */
    public static Properties getSystemEnvVars()
        throws IOException
    {
        return getSystemEnvVars( !Os.isFamily( Os.FAMILY_WINDOWS ) );
    }

    /**
     * Return the shell environment variables. If <code>caseSensitive == true</code>, then envar
     * keys will all be upper-case.
     *
     * @param caseSensitive Whether environment variable keys should be treated case-sensitively.
     * @return Properties object of (possibly modified) envar keys mapped to their values.
     * @throws IOException
     */
    public static Properties getSystemEnvVars( boolean caseSensitive )
        throws IOException
    {
        Process p = null;

        Properties envVars = new Properties();

        Runtime r = Runtime.getRuntime();

        //If this is windows set the shell to command.com or cmd.exe with correct arguments.
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            if ( Os.isFamily( Os.FAMILY_WIN9X ) )
            {
                p = r.exec( "command.com /c set" );
            }
            else
            {
                p = r.exec( "cmd.exe /c set" );
            }
        }
        else
        {
            p = r.exec( "env" );
        }

        BufferedReader br = new BufferedReader( new InputStreamReader( p.getInputStream() ) );

        String line;

        String lastKey = null;
        String lastVal = null;

        while ( ( line = br.readLine() ) != null )
        {
            int idx = line.indexOf( '=' );

            if ( idx > 0 )
            {
                lastKey = line.substring( 0, idx );

                if ( !caseSensitive )
                {
                    lastKey = lastKey.toUpperCase( Locale.ENGLISH );
                }

                lastVal = line.substring( idx + 1 );

                envVars.setProperty( lastKey, lastVal );
            }
            else if ( lastKey != null )
            {
                lastVal += "\n" + line;

                envVars.setProperty( lastKey, lastVal );
            }
        }

        return envVars;
    }

    /**
     * Kill a process launched by executeCommandLine methods.
     * Doesn't work correctly on windows, only the cmd process will be destroy but not the sub process (<a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4770092">Bug ID 4770092</a>)
     *
     * @param pid The pid of command return by Commandline.getPid()
     */
    public static void killProcess( long pid )
    {
        Process p = (Process) processes.get( new Long( pid ) );

        if ( p != null )
        {
            p.destroy();
            System.out.println( "killed." );
            processes.remove( new Long( pid ) );
        }
        else
        {
            System.out.println( "don't exist." );
        }
    }

    public static boolean isAlive( long pid )
    {
        return ( processes.get( new Long( pid ) ) != null );
    }

    public static boolean isAlive( Process p ) {
        try
        {
            p.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }

    public static String[] translateCommandline( String toProcess )
        throws Exception
    {
        if ( ( toProcess == null ) || ( toProcess.length() == 0 ) )
        {
            return new String[0];
        }

        // parse with a simple finite state machine

        final int normal = 0;
        final int inQuote = 1;
        final int inDoubleQuote = 2;
        int state = normal;
        StringTokenizer tok = new StringTokenizer( toProcess, "\"\' ", true );
        Vector v = new Vector();
        StringBuffer current = new StringBuffer();

        while ( tok.hasMoreTokens() )
        {
            String nextTok = tok.nextToken();
            switch ( state )
            {
                case inQuote:
                    if ( "\'".equals( nextTok ) )
                    {
                        state = normal;
                    }
                    else
                    {
                        current.append( nextTok );
                    }
                    break;
                case inDoubleQuote:
                    if ( "\"".equals( nextTok ) )
                    {
                        state = normal;
                    }
                    else
                    {
                        current.append( nextTok );
                    }
                    break;
                default:
                    if ( "\'".equals( nextTok ) )
                    {
                        state = inQuote;
                    }
                    else if ( "\"".equals( nextTok ) )
                    {
                        state = inDoubleQuote;
                    }
                    else if ( " ".equals( nextTok ) )
                    {
                        if ( current.length() != 0 )
                        {
                            v.addElement( current.toString() );
                            current.setLength( 0 );
                        }
                    }
                    else
                    {
                        current.append( nextTok );
                    }
                    break;
            }
        }

        if ( current.length() != 0 )
        {
            v.addElement( current.toString() );
        }

        if ( ( state == inQuote ) || ( state == inDoubleQuote ) )
        {
            throw new CommandLineException( "unbalanced quotes in " + toProcess );
        }

        String[] args = new String[v.size()];
        v.copyInto( args );
        return args;
    }

    /**
     * <p>Put quotes around the given String if necessary.</p>
     * <p>If the argument doesn't include spaces or quotes, return it
     * as is. If it contains double quotes, use single quotes - else
     * surround the argument by double quotes.</p>
     *
     * @throws CommandLineException if the argument contains both, single
     *                              and double quotes.
     *
     * @deprecated Use {@link StringUtils#quoteAndEscape(String, char, char[], char[], char, boolean)},
     * {@link StringUtils#quoteAndEscape(String, char, char[], char, boolean)}, or
     * {@link StringUtils#quoteAndEscape(String, char)} instead.
     */
    public static String quote( String argument )
        throws CommandLineException
    {
        return quote( argument, false, false, true );
    }

    /**
     * <p>Put quotes around the given String if necessary.</p>
     * <p>If the argument doesn't include spaces or quotes, return it
     * as is. If it contains double quotes, use single quotes - else
     * surround the argument by double quotes.</p>
     *
     * @throws CommandLineException if the argument contains both, single
     *                              and double quotes.
     *
     * @deprecated Use {@link StringUtils#quoteAndEscape(String, char, char[], char[], char, boolean)},
     * {@link StringUtils#quoteAndEscape(String, char, char[], char, boolean)}, or
     * {@link StringUtils#quoteAndEscape(String, char)} instead.
     */
    public static String quote( String argument, boolean wrapExistingQuotes )
        throws CommandLineException
    {
        return quote( argument, false, false, wrapExistingQuotes );
    }

    /**
     * @deprecated Use {@link StringUtils#quoteAndEscape(String, char, char[], char[], char, boolean)},
     * {@link StringUtils#quoteAndEscape(String, char, char[], char, boolean)}, or
     * {@link StringUtils#quoteAndEscape(String, char)} instead.
     */
    public static String quote( String argument, boolean escapeSingleQuotes, boolean escapeDoubleQuotes,
                                boolean wrapExistingQuotes )
        throws CommandLineException
    {
        if ( argument.indexOf( "\"" ) > -1 )
        {
            if ( argument.indexOf( "\'" ) > -1 )
            {
                throw new CommandLineException( "Can't handle single and double quotes in same argument" );
            }
            else
            {
                if ( escapeSingleQuotes )
                {
                    return "\\\'" + argument + "\\\'";
                }
                else if ( wrapExistingQuotes )
                {
                    return '\'' + argument + '\'';
                }
            }
        }
        else if ( argument.indexOf( "\'" ) > -1 )
        {
            if ( escapeDoubleQuotes )
            {
                return "\\\"" + argument + "\\\"";
            }
            else if ( wrapExistingQuotes )
            {
                return '\"' + argument + '\"';
            }
        }
        else if ( argument.indexOf( " " ) > -1 )
        {
            if ( escapeDoubleQuotes )
            {
                return "\\\"" + argument + "\\\"";
            }
            else
            {
                return '\"' + argument + '\"';
            }
        }

        return argument;
    }

    public static String toString( String[] line )
    {
        // empty path return empty string
        if ( ( line == null ) || ( line.length == 0 ) )
        {
            return "";
        }

        // path containing one or more elements
        final StringBuffer result = new StringBuffer();
        for ( int i = 0; i < line.length; i++ )
        {
            if ( i > 0 )
            {
                result.append( ' ' );
            }
            try
            {
                result.append( StringUtils.quoteAndEscape( line[i], '\"' ) );
            }
            catch ( Exception e )
            {
                System.err.println( "Error quoting argument: " + e.getMessage() );
            }
        }
        return result.toString();
    }

}
