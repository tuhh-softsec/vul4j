package org.codehaus.plexus.util.cli.shell;

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

import org.codehaus.plexus.util.StringUtils;

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
    private static final char[] DEFAULT_QUOTING_TRIGGER_CHARS = { ' ' };

    private String shellCommand;

    private List<String> shellArgs = new ArrayList<String>();

    private boolean quotedArgumentsEnabled = true;

    private boolean unconditionallyQuote = false;

    private String executable;

    private String workingDir;

    private boolean quotedExecutableEnabled = true;

    private boolean doubleQuotedArgumentEscaped = false;

    private boolean singleQuotedArgumentEscaped = false;

    private boolean doubleQuotedExecutableEscaped = false;

    private boolean singleQuotedExecutableEscaped = false;

    private char argQuoteDelimiter = '\"';

    private char exeQuoteDelimiter = '\"';

    private String argumentEscapePattern = "\\%s";

    /**
     * Toggle unconditional quoting
     *
     * @param unconditionallyQuote
     */
    public void setUnconditionalQuoting(boolean unconditionallyQuote)
    {
        this.unconditionallyQuote = unconditionallyQuote;
    }

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
        if ( ( shellArgs == null ) || shellArgs.isEmpty() )
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
    public List<String> getCommandLine( String executable, String[] arguments )
    {
        return getRawCommandLine( executable, arguments );
    }

    protected String quoteOneItem(String inputString, boolean isExecutable)
    {
        char[] escapeChars = getEscapeChars( isSingleQuotedExecutableEscaped(), isDoubleQuotedExecutableEscaped() );
        return StringUtils.quoteAndEscape(
            inputString,
            isExecutable ? getExecutableQuoteDelimiter() : getArgumentQuoteDelimiter(),
            escapeChars,
            getQuotingTriggerChars(),
            '\\',
            unconditionallyQuote
        );
    }

    protected List<String> getRawCommandLine( String executable, String[] arguments )
    {
        List<String> commandLine = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();

        if ( executable != null )
        {
            String preamble = getExecutionPreamble();
            if ( preamble != null )
            {
                sb.append( preamble );
            }

            if ( isQuotedExecutableEnabled() )
            {
                sb.append( quoteOneItem( getOriginalExecutable(), true ) );
            }
            else
            {
                sb.append( getExecutable() );
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
                sb.append( quoteOneItem( arguments[i], false ) );
            }
            else
            {
                sb.append( arguments[i] );
            }
        }

        commandLine.add( sb.toString() );

        return commandLine;
    }

    protected char[] getQuotingTriggerChars()
    {
        return DEFAULT_QUOTING_TRIGGER_CHARS;
    }

    protected String getExecutionPreamble()
    {
        return null;
    }

    protected char[] getEscapeChars( boolean includeSingleQuote, boolean includeDoubleQuote )
    {
        StringBuilder buf = new StringBuilder( 2 );
        if ( includeSingleQuote )
        {
            buf.append( '\'' );
        }

        if ( includeDoubleQuote )
        {
            buf.append( '\"' );
        }

        char[] result = new char[buf.length()];
        buf.getChars( 0, buf.length(), result, 0 );

        return result;
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

    protected void setArgumentQuoteDelimiter( char argQuoteDelimiter )
    {
        this.argQuoteDelimiter = argQuoteDelimiter;
    }

    protected char getArgumentQuoteDelimiter()
    {
        return argQuoteDelimiter;
    }

    protected void setExecutableQuoteDelimiter( char exeQuoteDelimiter )
    {
        this.exeQuoteDelimiter = exeQuoteDelimiter;
    }

    protected char getExecutableQuoteDelimiter()
    {
        return exeQuoteDelimiter;
    }

    protected void setArgumentEscapePattern(String argumentEscapePattern)
    {
        this.argumentEscapePattern = argumentEscapePattern;
    }

    protected String getArgumentEscapePattern() {
        return argumentEscapePattern;
    }

    /**
     * Get the full command line to execute, including shell command, shell arguments,
     * executable and executable arguments
     *
     * @param arguments  arguments for the executable, not the shell
     * @return List of String objects, whose array version is suitable to be used as argument
     *         of Runtime.getRuntime().exec()
     */
    public List<String> getShellCommandLine( String[] arguments )
    {

        List<String> commandLine = new ArrayList<String>();

        if ( getShellCommand() != null )
        {
            commandLine.add( getShellCommand() );
        }

        if ( getShellArgs() != null )
        {
            commandLine.addAll( getShellArgsList() );
        }

        commandLine.addAll( getCommandLine( getOriginalExecutable(), arguments ) );

        return commandLine;

    }

    public List<String> getShellArgsList()
    {
        return shellArgs;
    }

    public void addShellArg( String arg )
    {
        shellArgs.add( arg );
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
        if ( ( executable == null ) || ( executable.length() == 0 ) )
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
            workingDir = path;
        }
    }

    /**
     * Sets execution directory.
     */
    public void setWorkingDirectory( File workingDir )
    {
        if ( workingDir != null )
        {
            this.workingDir = workingDir.getAbsolutePath();
        }
    }

    public File getWorkingDirectory()
    {
        return workingDir == null ? null : new File( workingDir );
    }

    public String getWorkingDirectoryAsString()
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

    public List<String> getOriginalCommandLine( String executable, String[] arguments )
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
