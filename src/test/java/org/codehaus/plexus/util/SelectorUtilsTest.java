package org.codehaus.plexus.util;

import java.io.File;

import junit.framework.TestCase;

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

public class SelectorUtilsTest
    extends TestCase
{
    public void testMatchPath_DefaultFileSeparator()
    {
        String separator = File.separator;

        // Pattern and target start with file separator
        assertTrue( SelectorUtils.matchPath( separator + "*" + separator + "a.txt", separator + "b" + separator
            + "a.txt" ) );
        // Pattern starts with file separator, target doesn't
        assertFalse( SelectorUtils.matchPath( separator + "*" + separator + "a.txt", "b" + separator + "a.txt" ) );
        // Pattern doesn't start with file separator, target does
        assertFalse( SelectorUtils.matchPath( "*" + separator + "a.txt", separator + "b" + separator + "a.txt" ) );
        // Pattern and target don't start with file separator
        assertTrue( SelectorUtils.matchPath( "*" + separator + "a.txt", "b" + separator + "a.txt" ) );
    }

    public void testMatchPath_UnixFileSeparator()
    {
        String separator = "/";

        // Pattern and target start with file separator
        assertTrue( SelectorUtils.matchPath( separator + "*" + separator + "a.txt", separator + "b" + separator
            + "a.txt", separator, false ) );
        // Pattern starts with file separator, target doesn't
        assertFalse( SelectorUtils.matchPath( separator + "*" + separator + "a.txt", "b" + separator + "a.txt",
                                              separator, false ) );
        // Pattern doesn't start with file separator, target does
        assertFalse( SelectorUtils.matchPath( "*" + separator + "a.txt", separator + "b" + separator + "a.txt",
                                              separator, false ) );
        // Pattern and target don't start with file separator
        assertTrue( SelectorUtils.matchPath( "*" + separator + "a.txt", "b" + separator + "a.txt", separator, false ) );
    }

    public void testMatchPath_WindowsFileSeparator()
    {
        String separator = "\\";

        // Pattern and target start with file separator
        assertTrue( SelectorUtils.matchPath( separator + "*" + separator + "a.txt", separator + "b" + separator
            + "a.txt", separator, false ) );
        // Pattern starts with file separator, target doesn't
        assertFalse( SelectorUtils.matchPath( separator + "*" + separator + "a.txt", "b" + separator + "a.txt",
                                              separator, false ) );
        // Pattern doesn't start with file separator, target does
        assertFalse( SelectorUtils.matchPath( "*" + separator + "a.txt", separator + "b" + separator + "a.txt",
                                              separator, false ) );
        // Pattern and target don't start with file separator
        assertTrue( SelectorUtils.matchPath( "*" + separator + "a.txt", "b" + separator + "a.txt", separator, false ) );
    }
}
