package org.codehaus.plexus.util;

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

import junit.framework.TestCase;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class PathToolTest
    extends TestCase
{
    /**
     * @throws Exception
     */
    public void testGetRelativePath()
        throws Exception
    {
        assertEquals( PathTool.getRelativePath( null, null ), "" );
        assertEquals( PathTool.getRelativePath( null, "/usr/local/java/bin" ), "" );
        assertEquals( PathTool.getRelativePath( "/usr/local/", null ), "" );
        assertEquals( PathTool.getRelativePath( "/usr/local/", "/usr/local/java/bin" ), ".." );
        assertEquals( PathTool.getRelativePath( "/usr/local/", "/usr/local/java/bin/java.sh" ), "../.." );
        assertEquals( PathTool.getRelativePath( "/usr/local/java/bin/java.sh", "/usr/local/" ), "" );
    }

    /**
     * @throws Exception
     */
    public void testGetDirectoryComponent()
        throws Exception
    {
        assertEquals( PathTool.getDirectoryComponent( null ), "" );
        assertEquals( PathTool.getDirectoryComponent( "/usr/local/java/bin" ), "/usr/local/java" );
        assertEquals( PathTool.getDirectoryComponent( "/usr/local/java/bin/" ), "/usr/local/java/bin" );
        assertEquals( PathTool.getDirectoryComponent( "/usr/local/java/bin/java.sh" ), "/usr/local/java/bin" );
    }

    /**
     * @throws Exception
     */
    public void testCalculateLink()
        throws Exception
    {
        assertEquals( PathTool.calculateLink( "/index.html", "../.." ), "../../index.html" );
        assertEquals( PathTool.calculateLink( "http://plexus.codehaus.org/plexus-utils/index.html", "../.." ),
                      "http://plexus.codehaus.org/plexus-utils/index.html" );
        assertEquals( PathTool.calculateLink( "/usr/local/java/bin/java.sh", "../.." ),
                      "../../usr/local/java/bin/java.sh" );
        assertEquals( PathTool.calculateLink( "../index.html", "/usr/local/java/bin" ),
                      "/usr/local/java/bin/../index.html" );
        assertEquals( PathTool.calculateLink( "../index.html", "http://plexus.codehaus.org/plexus-utils" ),
                      "http://plexus.codehaus.org/plexus-utils/../index.html" );
    }

    /**
     * @throws Exception
     */
    public void testGetRelativeWebPath()
        throws Exception
    {
        assertEquals( PathTool.getRelativeWebPath( null, null ), "" );
        assertEquals( PathTool.getRelativeWebPath( null, "http://plexus.codehaus.org/" ), "" );
        assertEquals( PathTool.getRelativeWebPath( "http://plexus.codehaus.org/", null ), "" );
        assertEquals( PathTool.getRelativeWebPath( "http://plexus.codehaus.org/",
                                                   "http://plexus.codehaus.org/plexus-utils/index.html" ),
                      "plexus-utils/index.html" );
        assertEquals( PathTool.getRelativeWebPath( "http://plexus.codehaus.org/plexus-utils/index.html",
                                                   "http://plexus.codehaus.org/" ), "../../" );
    }

    /**
     * @throws Exception
     */
    public void testGetRelativeFilePath()
        throws Exception
    {
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            assertEquals( PathTool.getRelativeFilePath( null, null ), "" );
            assertEquals( PathTool.getRelativeFilePath( null, "c:\\tools\\java\\bin" ), "" );
            assertEquals( PathTool.getRelativeFilePath( "c:\\tools\\java", null ), "" );
            assertEquals( PathTool.getRelativeFilePath( "c:\\tools", "c:\\tools\\java\\bin" ), "java\\bin" );
            assertEquals( PathTool.getRelativeFilePath( "c:\\tools", "c:\\tools\\java\\bin\\" ), "java\\bin\\" );
            assertEquals( PathTool.getRelativeFilePath( "c:\\tools\\java\\bin", "c:\\tools" ), "..\\.." );
            assertEquals( PathTool.getRelativeFilePath( "c:\\tools\\", "c:\\tools\\java\\bin\\java.exe" ),
                          "java\\bin\\java.exe" );
            assertEquals( PathTool.getRelativeFilePath( "c:\\tools\\java\\bin\\java.sh", "c:\\tools" ), "..\\..\\.." );
            assertEquals( PathTool.getRelativeFilePath( "c:\\tools", "c:\\bin" ), "..\\bin" );
            assertEquals( PathTool.getRelativeFilePath( "c:\\bin", "c:\\tools" ), "..\\tools" );
            assertEquals( PathTool.getRelativeFilePath( "c:\\bin", "c:\\bin" ), "" );
        }
        else
        {
            assertEquals( PathTool.getRelativeFilePath( null, null ), "" );
            assertEquals( PathTool.getRelativeFilePath( null, "/usr/local/java/bin" ), "" );
            assertEquals( PathTool.getRelativeFilePath( "/usr/local", null ), "" );
            assertEquals( PathTool.getRelativeFilePath( "/usr/local", "/usr/local/java/bin" ), "java/bin" );
            assertEquals( PathTool.getRelativeFilePath( "/usr/local", "/usr/local/java/bin/" ), "java/bin/" );
            assertEquals( PathTool.getRelativeFilePath( "/usr/local/java/bin", "/usr/local/" ), "../../" );
            assertEquals( PathTool.getRelativeFilePath( "/usr/local/", "/usr/local/java/bin/java.sh" ),
                          "java/bin/java.sh" );
            assertEquals( PathTool.getRelativeFilePath( "/usr/local/java/bin/java.sh", "/usr/local/" ), "../../../" );
            assertEquals( PathTool.getRelativeFilePath( "/usr/local/", "/bin" ), "../../bin" );
            assertEquals( PathTool.getRelativeFilePath( "/bin", "/usr/local" ), "../usr/local" );
            assertEquals( PathTool.getRelativeFilePath( "/bin", "/bin" ), "" );
        }
    }
}
