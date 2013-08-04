package org.codehaus.plexus.util;

import junit.framework.TestCase;

/*
 * Copyright 2011 The Codehaus Foundation.
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

public class PerfTest
    extends TestCase
{

    String src = "012345578901234556789012345678901234456789012345678901234567890";

    private final int oops = 100;

    public void testSubString()
    {
        StringBuilder res = new StringBuilder();
        int len = src.length();
        for ( int cnt = 0; cnt < oops; cnt++ )
        {
            for ( int i = 0; i < len - 5; i++ )
            {
                res.append( src.substring( i, i+4 ) );
            }
        }
        int i = res.length();
        System.out.println( "i = " + i );
    }

    public void testResDir()
    {
        StringBuilder res = new StringBuilder();
        int len = src.length();
        for ( int cnt = 0; cnt < oops; cnt++ )
        {
            for ( int i = 0; i < len - 5; i++ )
            {
                res.append( src, i, i+4  );
            }
        }
        int i = res.length();
        System.out.println( "i = " + i );
    }
}
