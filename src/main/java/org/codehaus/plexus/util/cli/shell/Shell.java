package org.codehaus.plexus.util.cli.shell;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Class that abstracts the Shell functionality,
 * with subclases for shells that behave particularly, like
 * <ul>
 * <li><code>command.com</code></li>
 * <li><code>cmd.exe</code></li>
 * </ul>
 * </p>
 *
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @since 1.2
 * @version $Id$
 */
public class Shell
    implements Cloneable
{
    private String shellCommand;

    private List shellArgs = new ArrayList();

    private boolean quotedArgumentsEnabled = true;

    private String executable;

    private File workingDir;

    private boolean quotedExecutableEnabled = true;

    private boolean doubleQuotedArgumentEscaped = false;

    private boolean singleQuotedArgumentEscaped = false;

    private boolean doubleQuotedExecutableEscaped = false;

    private boolean singleQuotedExecutableEscaped = false;

    /**
     * Set the command to execute the shell (eg. COMMAND.COM, /bin/bash,...)
     *
     * @param shellCommand
     */
    public void setShellCommand( String shellCommand )
    {
        this.shellCommand = shellCommand;
    }

    /**
     * Get the command to execute the shell
     *
     * @return
     */
    public String getShellCommand()
    {
        return shellCommand;
    }

    /**
     * Set the shell arguments when calling a command line (not the executable arguments)
     * (eg. /X /C for CMD.EXE)
     *
     * @param shellArgs
     */
    public void setShellArgs( String[] shellArgs )
    {
        this.shellArgs.clear();
        this.shellArgs.addAll( Arrays.asList( shellArgs ) );
    }

    /**
     * Get the shell arguments
     *
     * @return
     */
    public String[] getShellArgs()
    {
        if ( shellArgs == null || shellArgs.isEmpty() )
        {
            return null;
        }
        else
        {
            return (String[]) shellArgs.toArray( new String[shellArgs.size()] );
        }
    }

    /**
     * Get the command line for the provided executable and arguments in this shell
     *
     * @param executable executable that the shell has to call
     * @param arguments  arguments for the executable, not the shell
     * @return List with one String object with executable and arguments quoted as needed
     */
    public List getCommandLine( String executable, String[] arguments )
    {
        return getRawCommandLine( executable, arguments );
    }

    private List getRawCommandLine( String executable, String[] arguments )
    {
        List commandLine = new ArrayList();
        try
        {
            StringBuffer sb = new StringBuffer();

            if ( executable != null )
            {
                if ( isQuotedExecutableEnabled() )
                {
                    sb.append( CommandLineUtils.quote( executable, isSingleQuotedExecutableEscaped(),
                                                       isDoubleQuotedExecutableEscaped(), false ) );
                }
                else
                {
                    sb.append( executable );
                }
            }
            for ( int i = 0; i < arguments.length; i++ )
            {
                if ( sb.length() > 0 )
                {
                    sb.append( " " );
                }

                if ( isQuotedArgumentsEnabled() )
                {
                    sb.append( CommandLineUtils.quote( arguments[i], isSingleQuotedArgumentEscaped(),
                                                       isDoubleQuotedArgumentEscaped(), false ) );
                }
                else
                {
                    sb.append( arguments[i] );
                }
            }

            commandLine.add( sb.toString() );
        }
        catch ( CommandLineException e )
        {
            throw new RuntimeException( e.getMessage() );
        }

        return commandLine;
    }

    protected boolean isDoubleQuotedArgumentEscaped()
    {
        return doubleQuotedArgumentEscaped;
    }

    protected boolean isSingleQuotedArgumentEscaped()
    {
        return singleQuotedArgumentEscaped;
    }

    protected boolean isDoubleQuotedExecutableEscaped()
    {
        return doubleQuotedExecutableEscaped;
    }

    protected boolean isSingleQuotedExecutableEscaped()
    {
        return singleQuotedExecutableEscaped;
    }

    /**
     * Get the full command line to execute, including shell command, shell arguments,
     * executable and executable arguments
     *
     * @param arguments  arguments for the executable, not the shell
     * @return List of String objects, whose array version is suitable to be used as argument
     *         of Runtime.getRuntime().exec()
     */
    public List getShellCommandLine( String[] arguments )
    {

        List commandLine = new ArrayList();

        if ( getShellCommand() != null )
        {
            commandLine.add( getShellCommand() );
        }

        if ( getShellArgs() != null )
        {
            commandLine.addAll( getShellArgsList() );
        }

        commandLine.addAll( getCommandLine( getExecutable(), arguments ) );

        return commandLine;

    }

    public List getShellArgsList()
    {
        return shellArgs;
    }

    public void addShellArg( String arg )
    {
        this.shellArgs.add( arg );
    }

    public void setQuotedArgumentsEnabled( boolean quotedArgumentsEnabled )
    {
        this.quotedArgumentsEnabled = quotedArgumentsEnabled;
    }

    public boolean isQuotedArgumentsEnabled()
    {
        return quotedArgumentsEnabled;
    }

    public void setQuotedExecutableEnabled( boolean quotedExecutableEnabled )
    {
        this.quotedExecutableEnabled = quotedExecutableEnabled;
    }

    public boolean isQuotedExecutableEnabled()
    {
        return quotedExecutableEnabled;
    }

    /**
     * Sets the executable to run.
     */
    public void setExecutable( String executable )
    {
        if ( executable == null || executable.length() == 0 )
        {
            return;
        }
        this.executable = executable.replace( '/', File.separatorChar ).replace( '\\', File.separatorChar );
    }

    public String getExecutable()
    {
        return executable;
    }

    /**
     * Sets execution directory.
     */
    public void setWorkingDirectory( String path )
    {
        if ( path != null )
        {
            workingDir = new File( path );
        }
    }

    /**
     * Sets execution directory.
     */
    public void setWorkingDirectory( File workingDir )
    {
        this.workingDir = workingDir;
    }

    public File getWorkingDirectory()
    {
        return workingDir;
    }

    public void clearArguments()
    {
        shellArgs.clear();
    }

    public Object clone()
    {
        Shell shell = new Shell();
        shell.setExecutable( getExecutable() );
        shell.setWorkingDirectory( getWorkingDirectory() );
        shell.setShellArgs( getShellArgs() );
        return shell;
    }

    public String getOriginalExecutable()
    {
        return executable;
    }

    public List getOriginalCommandLine( String executable, String[] arguments )
    {
        return getRawCommandLine( executable, arguments );
    }

    protected void setDoubleQuotedArgumentEscaped( boolean doubleQuotedArgumentEscaped )
    {
        this.doubleQuotedArgumentEscaped = doubleQuotedArgumentEscaped;
    }

    protected void setDoubleQuotedExecutableEscaped( boolean doubleQuotedExecutableEscaped )
    {
        this.doubleQuotedExecutableEscaped = doubleQuotedExecutableEscaped;
    }

    protected void setSingleQuotedArgumentEscaped( boolean singleQuotedArgumentEscaped )
    {
        this.singleQuotedArgumentEscaped = singleQuotedArgumentEscaped;
    }

    protected void setSingleQuotedExecutableEscaped( boolean singleQuotedExecutableEscaped )
    {
        this.singleQuotedExecutableEscaped = singleQuotedExecutableEscaped;
    }

}
