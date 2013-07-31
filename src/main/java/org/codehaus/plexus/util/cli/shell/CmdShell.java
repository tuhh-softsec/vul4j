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

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Implementation to call the CMD Shell present on Windows NT, 2000 and XP
 * </p>
 *
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @since 1.2
 * @version $Id$
 */
public class CmdShell
    extends Shell
{
    public CmdShell()
    {
        setShellCommand( "cmd.exe" );
        setQuotedExecutableEnabled( true );
        setShellArgs( new String[]{"/X", "/C"} );
    }

    /**
     * <p>
     * Specific implementation that quotes all the command line.
     * </p>
     * <p>
     * Workaround for http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6468220
     * </p>
     * <p>
     * From cmd.exe /? output:
     * </p>
     *
     * <pre>
     *      If /C or /K is specified, then the remainder of the command line after
     *      the switch is processed as a command line, where the following logic is
     *      used to process quote (&quot;) characters:
     *
     *      1.  If all of the following conditions are met, then quote characters
     *      on the command line are preserved:
     *
     *      - no /S switch
     *      - exactly two quote characters
     *      - no special characters between the two quote characters,
     *      where special is one of: &amp;&lt;&gt;()@&circ;|
     *      - there are one or more whitespace characters between the
     *      the two quote characters
     *      - the string between the two quote characters is the name
     *      of an executable file.
     *
     *      2.  Otherwise, old behavior is to see if the first character is
     *      a quote character and if so, strip the leading character and
     *      remove the last quote character on the command line, preserving
     *      any text after the last quote character.
     * </pre>
     *
     *<p>
     * Always quoting the entire command line, regardless of these conditions
     * appears to make Windows processes invoke successfully.
     * </p>
     */
    public List<String> getCommandLine( String executable, String[] arguments )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "\"" );
        sb.append( super.getCommandLine( executable, arguments ).get( 0 ) );
        sb.append( "\"" );

        return Arrays.asList( new String[] { sb.toString() } );
    }
}
