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

import org.codehaus.plexus.util.Os;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jason van Zyl
 * @version $Id$
 */
public class BourneShell
    extends Shell
{

    public BourneShell()
    {
        this(false);
    }

    public BourneShell( boolean isLoginShell )
    {
        setUnconditionalQuoting( true );
        setShellCommand( "/bin/sh" );
        setArgumentQuoteDelimiter( '\'' );
        setExecutableQuoteDelimiter( '\'' );
        setSingleQuotedArgumentEscaped( true );
        setSingleQuotedExecutableEscaped( false );
        setQuotedExecutableEnabled( true );
        setArgumentEscapePattern("'\\%s'");

        if ( isLoginShell )
        {
            addShellArg( "-l" );
        }
    }

    /** {@inheritDoc} */
    public String getExecutable()
    {
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            return super.getExecutable();
        }

        return quoteOneItem( super.getOriginalExecutable(), true );
    }

    public List<String> getShellArgsList()
    {
        List<String> shellArgs = new ArrayList<String>();
        List<String> existingShellArgs = super.getShellArgsList();

        if ( ( existingShellArgs != null ) && !existingShellArgs.isEmpty() )
        {
            shellArgs.addAll( existingShellArgs );
        }

        shellArgs.add( "-c" );

        return shellArgs;
    }

    public String[] getShellArgs()
    {
        String[] shellArgs = super.getShellArgs();
        if ( shellArgs == null )
        {
            shellArgs = new String[0];
        }

        if ( ( shellArgs.length > 0 ) && !shellArgs[shellArgs.length - 1].equals( "-c" ) )
        {
            String[] newArgs = new String[shellArgs.length + 1];

            System.arraycopy( shellArgs, 0, newArgs, 0, shellArgs.length );
            newArgs[shellArgs.length] = "-c";

            shellArgs = newArgs;
        }

        return shellArgs;
    }

    protected String getExecutionPreamble()
    {
        if ( getWorkingDirectoryAsString() == null )
        {
            return null;
        }

        String dir = getWorkingDirectoryAsString();
        StringBuilder sb = new StringBuilder();
        sb.append( "cd " );

        sb.append( quoteOneItem( dir, false ) );
        sb.append( " && " );

        return sb.toString();
    }

    /**
     * <p>Unify quotes in a path for the Bourne Shell.</p>
     *
     * <pre>
     * BourneShell.quoteOneItem(null)                       = null
     * BourneShell.quoteOneItem("")                         = ''
     * BourneShell.quoteOneItem("/test/quotedpath'abc")     = '/test/quotedpath'"'"'abc'
     * BourneShell.quoteOneItem("/test/quoted path'abc")    = '/test/quoted pat'"'"'habc'
     * BourneShell.quoteOneItem("/test/quotedpath\"abc")    = '/test/quotedpath"abc'
     * BourneShell.quoteOneItem("/test/quoted path\"abc")   = '/test/quoted path"abc'
     * BourneShell.quoteOneItem("/test/quotedpath\"'abc")   = '/test/quotedpath"'"'"'abc'
     * BourneShell.quoteOneItem("/test/quoted path\"'abc")  = '/test/quoted path"'"'"'abc'
     * </pre>
     *
     * @param path not null path.
     * @return the path unified correctly for the Bourne shell.
     */
    protected String quoteOneItem( String path, boolean isExecutable )
    {
        if ( path == null )
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append( "'" );
        sb.append( path.replace( "'", "'\"'\"'" ) );
        sb.append( "'" );

        return sb.toString();
    }
}
