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

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

import org.codehaus.plexus.util.Os;

import junit.framework.TestCase;

public class CommandLineUtilsTest
    extends TestCase
{

    public void testQuoteArguments()
    {
        try
        {
            String result = CommandLineUtils.quote( "Hello" );
            System.out.println( result );
            assertEquals( "Hello", result );
            result = CommandLineUtils.quote( "Hello World" );
            System.out.println( result );
            assertEquals( "\"Hello World\"", result );
            result = CommandLineUtils.quote( "\"Hello World\"" );
            System.out.println( result );
            assertEquals( "\'\"Hello World\"\'", result );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
        try
        {
            CommandLineUtils.quote( "\"Hello \'World\'\'" );
            fail();
        }
        catch ( Exception e )
        {
        }
    }

    /**
     * Tests that case-insensitive environment variables are normalized to upper case.
     */
    public void testGetSystemEnvVarsCaseInsensitive()
        throws Exception
    {
        Properties vars = CommandLineUtils.getSystemEnvVars( false );
        for ( Iterator it = vars.keySet().iterator(); it.hasNext(); )
        {
            String variable = (String) it.next();
            assertEquals( variable.toUpperCase( Locale.ENGLISH ), variable );
        }
    }

    /**
     * Tests that environment variables on Windows are normalized to upper case. Does nothing on Unix platforms.
     */
    public void testGetSystemEnvVarsWindows()
        throws Exception
    {
        if ( !Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            return;
        }
        Properties vars = CommandLineUtils.getSystemEnvVars();
        for ( Iterator it = vars.keySet().iterator(); it.hasNext(); )
        {
            String variable = (String) it.next();
            assertEquals( variable.toUpperCase( Locale.ENGLISH ), variable );
        }
    }

    /**
     * Tests the splitting of a command line into distinct arguments.
     */
    public void testTranslateCommandline()
        throws Exception
    {
        assertCmdLineArgs( new String[] {}, null );
        assertCmdLineArgs( new String[] {}, "" );

        assertCmdLineArgs( new String[] { "foo", "bar" }, "foo bar" );
        assertCmdLineArgs( new String[] { "foo", "bar" }, "   foo   bar   " );

        assertCmdLineArgs( new String[] { "foo", " double quotes ", "bar" }, "foo \" double quotes \" bar" );
        assertCmdLineArgs( new String[] { "foo", " single quotes ", "bar" }, "foo ' single quotes ' bar" );

        assertCmdLineArgs( new String[] { "foo", " \" ", "bar" }, "foo ' \" ' bar" );
        assertCmdLineArgs( new String[] { "foo", " ' ", "bar" }, "foo \" ' \" bar" );
    }

    private void assertCmdLineArgs( String[] expected, String cmdLine )
        throws Exception
    {
        String[] actual = CommandLineUtils.translateCommandline( cmdLine );
        assertNotNull( actual );
        assertEquals( expected.length, actual.length );
        assertEquals( Arrays.asList( expected ), Arrays.asList( actual ) );
    }

}
