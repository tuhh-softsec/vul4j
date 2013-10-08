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

/***************************************************************************************************
 * CruiseControl, a Continuous Integration Toolkit Copyright (c) 2001-2003, ThoughtWorks, Inc. 651 W
 * Washington Ave. Suite 500 Chicago, IL 60661 USA All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met: + Redistributions of source code must retain the
 * above copyright notice, this list of conditions and the following disclaimer. + Redistributions
 * in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution. +
 * Neither the name of ThoughtWorks, Inc., CruiseControl, nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **************************************************************************************************/

/*
 * ====================================================================
 * Copyright 2003-2004 The Apache Software Foundation.
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
 * ====================================================================
 */

import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.shell.BourneShell;
import org.codehaus.plexus.util.cli.shell.CmdShell;
import org.codehaus.plexus.util.cli.shell.CommandShell;
import org.codehaus.plexus.util.cli.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

/**
 * <p/>
 * Commandline objects help handling command lines specifying processes to
 * execute.
 * </p>
 * <p/>
 * The class can be used to define a command line as nested elements or as a
 * helper to define a command line by an application.
 * </p>
 * <p/>
 * <code>
 * &lt;someelement&gt;<br>
 * &nbsp;&nbsp;&lt;acommandline executable="/executable/to/run"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;argument value="argument 1" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;argument line="argument_1 argument_2 argument_3" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;argument value="argument 4" /&gt;<br>
 * &nbsp;&nbsp;&lt;/acommandline&gt;<br>
 * &lt;/someelement&gt;<br>
 * </code>
 * </p>
 * <p/>
 * The element <code>someelement</code> must provide a method
 * <code>createAcommandline</code> which returns an instance of this class.
 * </p>
 *
 * @author thomas.haas@softwired-inc.com
 * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
 */
public class Commandline
    implements Cloneable
{
    /**
     * @deprecated Use {@link org.codehaus.plexus.util.Os} class instead.
     */
    protected static final String OS_NAME = "os.name";

    /**
     * @deprecated Use {@link org.codehaus.plexus.util.Os} class instead.
     */
    protected static final String WINDOWS = "Windows";

    protected Vector arguments = new Vector();

    //protected Vector envVars = new Vector();
    // synchronized added to preserve synchronize of Vector class
    protected Map envVars = Collections.synchronizedMap( new LinkedHashMap() );

    private long pid = -1;

    private Shell shell;

    /**
     * @deprecated Use {@link Commandline#setExecutable(String)} instead.
     */
    protected String executable;

    /**
     * @deprecated Use {@link Commandline#setWorkingDirectory(File)} or
     * {@link Commandline#setWorkingDirectory(String)} instead.
     */
    private File workingDir;

    /**
     * Create a new command line object.
     * Shell is autodetected from operating system
     *
     * Shell usage is only desirable when generating code for remote execution.
     *
     * @param toProcess
     */
    public Commandline( String toProcess, Shell shell )
    {
        this.shell = shell;

        String[] tmp = new String[0];
        try
        {
            tmp = CommandLineUtils.translateCommandline( toProcess );
        }
        catch ( Exception e )
        {
            System.err.println( "Error translating Commandline." );
        }
        if ( ( tmp != null ) && ( tmp.length > 0 ) )
        {
            setExecutable( tmp[0] );
            for ( int i = 1; i < tmp.length; i++ )
            {
                createArgument().setValue( tmp[i] );
            }
        }
    }

    /**
     * Create a new command line object.
     * Shell is autodetected from operating system
     *
     * Shell usage is only desirable when generating code for remote execution.
     */
    public Commandline( Shell shell )
    {
        this.shell = shell;
    }

    /**
     * Create a new command line object, given a command following POSIX sh quoting rules
     *
     * @param toProcess
     */
    public Commandline( String toProcess )
    {
        setDefaultShell();
        String[] tmp = new String[0];
        try
        {
            tmp = CommandLineUtils.translateCommandline( toProcess );
        }
        catch ( Exception e )
        {
            System.err.println( "Error translating Commandline." );
        }
        if ( ( tmp != null ) && ( tmp.length > 0 ) )
        {
            setExecutable( tmp[0] );
            for ( int i = 1; i < tmp.length; i++ )
            {
                createArgument().setValue( tmp[i] );
            }
        }
    }

    /**
     * Create a new command line object.
     */
    public Commandline()
    {
        setDefaultShell();
    }

    public long getPid()
    {
        if ( pid == -1 )
        {
            pid = Long.parseLong( String.valueOf( System.currentTimeMillis() ) );
        }

        return pid;
    }

    public void setPid( long pid )
    {
        this.pid = pid;
    }

    /**
     * Class to keep track of the position of an Argument.
     */
    // <p>This class is there to support the srcfile and targetfile
    // elements of &lt;execon&gt; and &lt;transform&gt; - don't know
    // whether there might be additional use cases.</p> --SB
    public class Marker
    {

        private int position;

        private int realPos = -1;

        Marker( int position )
        {
            this.position = position;
        }

        /**
         * Return the number of arguments that preceeded this marker.
         * <p/>
         * <p>The name of the executable - if set - is counted as the
         * very first argument.</p>
         */
        public int getPosition()
        {
            if ( realPos == -1 )
            {
                realPos = ( getLiteralExecutable() == null ? 0 : 1 );
                for ( int i = 0; i < position; i++ )
                {
                    Arg arg = (Arg) arguments.elementAt( i );
                    realPos += arg.getParts().length;
                }
            }
            return realPos;
        }
    }

    /**
     * <p>Sets the shell or command-line interpretor for the detected operating system,
     * and the shell arguments.</p>
     */
    private void setDefaultShell()
    {
        //If this is windows set the shell to command.com or cmd.exe with correct arguments.
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            if ( Os.isFamily( Os.FAMILY_WIN9X ) )
            {
                setShell( new CommandShell() );
            }
            else
            {
                setShell( new CmdShell() );
            }
        }
        else
        {
            setShell( new BourneShell() );
        }
    }

    /**
     * Creates an argument object.
     * <p/>
     * <p>Each commandline object has at most one instance of the
     * argument class.  This method calls
     * <code>this.createArgument(false)</code>.</p>
     *
     * @return the argument object.
     * @see #createArgument(boolean)
     * @deprecated Use {@link Commandline#createArg()} instead
     */
    public Argument createArgument()
    {
        return this.createArgument( false );
    }

    /**
     * Creates an argument object and adds it to our list of args.
     * <p/>
     * <p>Each commandline object has at most one instance of the
     * argument class.</p>
     *
     * @param insertAtStart if true, the argument is inserted at the
     *                      beginning of the list of args, otherwise it is appended.
     * @deprecated Use {@link Commandline#createArg(boolean)} instead
     */
    public Argument createArgument( boolean insertAtStart )
    {
        Argument argument = new Argument();
        if ( insertAtStart )
        {
            arguments.insertElementAt( argument, 0 );
        }
        else
        {
            arguments.addElement( argument );
        }
        return argument;
    }

    /**
     * Creates an argument object.
     * <p/>
     * <p>Each commandline object has at most one instance of the
     * argument class.  This method calls
     * <code>this.createArgument(false)</code>.</p>
     *
     * @return the argument object.
     * @see #createArgument(boolean)
     */
    public Arg createArg()
    {
        return this.createArg( false );
    }

    /**
     * Creates an argument object and adds it to our list of args.
     * <p/>
     * <p>Each commandline object has at most one instance of the
     * argument class.</p>
     *
     * @param insertAtStart if true, the argument is inserted at the
     *                      beginning of the list of args, otherwise it is appended.
     */
    public Arg createArg( boolean insertAtStart )
    {
        Arg argument = new Argument();
        if ( insertAtStart )
        {
            arguments.insertElementAt( argument, 0 );
        }
        else
        {
            arguments.addElement( argument );
        }
        return argument;
    }

    /**
     * Adds an argument object to our list of args.
     *
     * @return the argument object.
     * @see #addArg(Arg,boolean)
     */
    public void addArg( Arg argument )
    {
        this.addArg( argument, false );
    }

    /**
     * Adds an argument object to our list of args.
     *
     * @param insertAtStart if true, the argument is inserted at the
     *                      beginning of the list of args, otherwise it is appended.
     */
    public void addArg( Arg argument, boolean insertAtStart )
    {
        if ( insertAtStart )
        {
            arguments.insertElementAt( argument, 0 );
        }
        else
        {
            arguments.addElement( argument );
        }
    }

    /**
     * Sets the executable to run.
     */
    public void setExecutable( String executable )
    {
        shell.setExecutable( executable );
        this.executable = executable;
    }

    /**
     * @return Executable to be run, as a literal string (no shell quoting/munging)
     */
    public String getLiteralExecutable()
    {
        return executable;
    }

    /**
     * Return an executable name, quoted for shell use.
     *
     * Shell usage is only desirable when generating code for remote execution.
     *
     * @return Executable to be run, quoted for shell interpretation
     */
    public String getExecutable()
    {
        String exec = shell.getExecutable();

        if ( exec == null )
        {
            exec = executable;
        }

        return exec;
    }

    public void addArguments( String[] line )
    {
        for ( int i = 0; i < line.length; i++ )
        {
            createArgument().setValue( line[i] );
        }
    }

    /**
     * Add an environment variable
     */
    public void addEnvironment( String name, String value )
    {
        //envVars.add( name + "=" + value );
        envVars.put( name, value );
    }

    /**
     * Add system environment variables
     */
    public void addSystemEnvironment()
        throws Exception
    {
        Properties systemEnvVars = CommandLineUtils.getSystemEnvVars();

        for ( Iterator i = systemEnvVars.keySet().iterator(); i.hasNext(); )
        {
            String key = (String) i.next();
            if ( !envVars.containsKey( key ) )
            {
                addEnvironment( key, systemEnvVars.getProperty( key ) );
            }
        }
    }

    /**
     * Return the list of environment variables
     */
    public String[] getEnvironmentVariables()
        throws CommandLineException
    {
        try
        {
            addSystemEnvironment();
        }
        catch ( Exception e )
        {
            throw new CommandLineException( "Error setting up environmental variables", e );
        }
        String[] environmentVars = new String[envVars.size()];
        int i = 0;
        for ( Iterator iterator = envVars.keySet().iterator(); iterator.hasNext(); )
        {
            String name = (String) iterator.next();
            String value = (String) envVars.get( name );
            environmentVars[i] = name + "=" + value;
            i++;
        }
        return environmentVars;
    }

    /**
     * Returns the executable and all defined arguments.
     */
    public String[] getCommandline()
    {
        final String[] args = getArguments();
        String executable = getLiteralExecutable();

        if ( executable == null )
        {
            return args;
        }
        final String[] result = new String[args.length + 1];
        result[0] = executable;
        System.arraycopy( args, 0, result, 1, args.length );
        return result;
    }

    /**
     * Returns the shell, executable and all defined arguments.
     *
     * Shell usage is only desirable when generating code for remote execution.
     */
    public String[] getShellCommandline()
    {
        // TODO: Provided only for backward compat. with <= 1.4
        verifyShellState();

        return (String[]) getShell().getShellCommandLine( getArguments() ).toArray( new String[0] );
    }

    /**
     * Returns all arguments defined by <code>addLine</code>,
     * <code>addValue</code> or the argument object.
     */
    public String[] getArguments()
    {
        Vector result = new Vector( arguments.size() * 2 );
        for ( int i = 0; i < arguments.size(); i++ )
        {
            Argument arg = (Argument) arguments.elementAt( i );
            String[] s = arg.getParts();
            if ( s != null )
            {
                for ( int j = 0; j < s.length; j++ )
                {
                    result.addElement( s[j] );
                }
            }
        }

        String[] res = new String[result.size()];
        result.copyInto( res );
        return res;
    }

    public String toString()
    {
        return StringUtils.join( getShellCommandline(), " " );
    }

    public int size()
    {
        return getCommandline().length;
    }

    public Object clone()
    {
        Commandline c = new Commandline( (Shell) shell.clone() );
        c.executable = executable;
        c.workingDir = workingDir;
        c.addArguments( getArguments() );
        return c;
    }

    /**
     * Clear out the whole command line.
     */
    public void clear()
    {
        executable = null;
        workingDir = null;
        shell.setExecutable( null );
        shell.clearArguments();
        arguments.removeAllElements();
    }

    /**
     * Clear out the arguments but leave the executable in place for another operation.
     */
    public void clearArgs()
    {
        arguments.removeAllElements();
    }

    /**
     * Return a marker.
     * <p/>
     * <p>This marker can be used to locate a position on the
     * commandline - to insert something for example - when all
     * parameters have been set.</p>
     */
    public Marker createMarker()
    {
        return new Marker( arguments.size() );
    }

    /**
     * Sets execution directory.
     */
    public void setWorkingDirectory( String path )
    {
        shell.setWorkingDirectory( path );
        workingDir = new File( path );
    }

    /**
     * Sets execution directory.
     */
    public void setWorkingDirectory( File workingDirectory )
    {
        shell.setWorkingDirectory( workingDirectory );
        workingDir = workingDirectory;
    }

    public File getWorkingDirectory()
    {
        File workDir = shell.getWorkingDirectory();

        if ( workDir == null )
        {
            workDir = workingDir;
        }

        return workDir;
    }

    /**
     * Executes the command.
     */
    public Process execute()
        throws CommandLineException
    {
        // TODO: Provided only for backward compat. with <= 1.4
        verifyShellState();

        Process process;

        //addEnvironment( "MAVEN_TEST_ENVAR", "MAVEN_TEST_ENVAR_VALUE" );

        String[] environment = getEnvironmentVariables();

        File workingDir = shell.getWorkingDirectory();

        try
        {
            if ( workingDir == null )
            {
                process = Runtime.getRuntime().exec( getCommandline(), environment, workingDir );
            }
            else
            {
                if ( !workingDir.exists() )
                {
                    throw new CommandLineException( "Working directory \"" + workingDir.getPath()
                        + "\" does not exist!" );
                }
                else if ( !workingDir.isDirectory() )
                {
                    throw new CommandLineException( "Path \"" + workingDir.getPath()
                        + "\" does not specify a directory." );
                }

                process = Runtime.getRuntime().exec( getCommandline(), environment, workingDir );
            }
        }
        catch ( IOException ex )
        {
            throw new CommandLineException( "Error while executing process.", ex );
        }

        return process;
    }

    /**
     * @deprecated Remove once backward compat with plexus-utils <= 1.4 is no longer a consideration
     */
    private void verifyShellState()
    {
        if ( shell.getWorkingDirectory() == null )
        {
            shell.setWorkingDirectory( workingDir );
        }

        if ( shell.getOriginalExecutable() == null )
        {
            shell.setExecutable( executable );
        }
    }

    public Properties getSystemEnvVars()
        throws Exception
    {
        return CommandLineUtils.getSystemEnvVars();
    }

    /**
     * Allows to set the shell to be used in this command line.
     *
     * Shell usage is only desirable when generating code for remote execution.
     *
     * @param shell
     * @since 1.2
     */
    public void setShell( Shell shell )
    {
        this.shell = shell;
    }

    /**
     * Get the shell to be used in this command line.
     *
     * Shell usage is only desirable when generating code for remote execution.
     * @since 1.2
     */
    public Shell getShell()
    {
        return shell;
    }

    /**
     * @deprecated Use {@link CommandLineUtils#translateCommandline(String)} instead.
     */
    public static String[] translateCommandline( String toProcess )
        throws Exception
    {
        return CommandLineUtils.translateCommandline( toProcess );
    }

    /**
     * @deprecated Use {@link CommandLineUtils#quote(String)} instead.
     */
    public static String quoteArgument( String argument )
        throws CommandLineException
    {
        return CommandLineUtils.quote( argument );
    }

    /**
     * @deprecated Use {@link CommandLineUtils#toString(String[])} instead.
     */
    public static String toString( String[] line )
    {
        return CommandLineUtils.toString( line );
    }

    public static class Argument
        implements Arg
    {
        private String[] parts;

        /* (non-Javadoc)
         * @see org.codehaus.plexus.util.cli.Argumnt#setValue(java.lang.String)
         */
        public void setValue( String value )
        {
            if ( value != null )
            {
                parts = new String[] { value };
            }
        }

        /* (non-Javadoc)
         * @see org.codehaus.plexus.util.cli.Argumnt#setLine(java.lang.String)
         */
        public void setLine( String line )
        {
            if ( line == null )
            {
                return;
            }
            try
            {
                parts = CommandLineUtils.translateCommandline( line );
            }
            catch ( Exception e )
            {
                System.err.println( "Error translating Commandline." );
            }
        }

        /* (non-Javadoc)
         * @see org.codehaus.plexus.util.cli.Argumnt#setFile(java.io.File)
         */
        public void setFile( File value )
        {
            parts = new String[] { value.getAbsolutePath() };
        }

        /* (non-Javadoc)
         * @see org.codehaus.plexus.util.cli.Argumnt#getParts()
         */
        public String[] getParts()
        {
            return parts;
        }
    }
}
