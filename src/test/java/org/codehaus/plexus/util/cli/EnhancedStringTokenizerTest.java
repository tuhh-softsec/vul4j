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

import junit.framework.TestCase;

public class EnhancedStringTokenizerTest extends TestCase
{
    /**
     * @param testName
     */
    public EnhancedStringTokenizerTest( final String testName )
    {
        super( testName );
    }

    /*
     * @see TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();
    }

    public void test1()
    {
        EnhancedStringTokenizer est = new EnhancedStringTokenizer( "this is a test string" );
        StringBuilder sb = new StringBuilder();
        while ( est.hasMoreTokens() )
        {
            sb.append( est.nextToken() );
            sb.append( " " );
        }
        assertEquals( "this is a test string ", sb.toString() );
    }

    public void test2()
    {
        EnhancedStringTokenizer est = new EnhancedStringTokenizer( "1,,,3,,4", "," );
        assertEquals( "Token 1", "1", est.nextToken() );
        assertEquals( "Token 2", "", est.nextToken() );
        assertEquals( "Token 3", "", est.nextToken() );
        assertEquals( "Token 4", "3", est.nextToken() );
        assertEquals( "Token 5", "", est.nextToken() );
        assertEquals( "Token 6", "4", est.nextToken() );
    }

    public void test3()
    {
        EnhancedStringTokenizer est = new EnhancedStringTokenizer( "1,,,3,,4", ",", true );
        assertEquals( "Token 1", "1", est.nextToken() );
        assertEquals( "Token 2", ",", est.nextToken() );
        assertEquals( "Token 3", "", est.nextToken() );
        assertEquals( "Token 4", ",", est.nextToken() );
        assertEquals( "Token 5", "", est.nextToken() );
        assertEquals( "Token 6", ",", est.nextToken() );
        assertEquals( "Token 7", "3", est.nextToken() );
        assertEquals( "Token 8", ",", est.nextToken() );
        assertEquals( "Token 9", "", est.nextToken() );
        assertEquals( "Token 10", ",", est.nextToken() );
        assertEquals( "Token 11", "4", est.nextToken() );
    }

    public void testMultipleDelim()
    {
        EnhancedStringTokenizer est = new EnhancedStringTokenizer( "1 2|3|4", " |", true );
        assertEquals( "Token 1", "1", est.nextToken() );
        assertEquals( "Token 2", " ", est.nextToken() );
        assertEquals( "Token 3", "2", est.nextToken() );
        assertEquals( "Token 4", "|", est.nextToken() );
        assertEquals( "Token 5", "3", est.nextToken() );
        assertEquals( "Token 6", "|", est.nextToken() );
        assertEquals( "Token 7", "4", est.nextToken() );
        assertEquals( "est.hasMoreTokens()", false, est.hasMoreTokens() );
    }

    public void testEmptyString()
    {
        EnhancedStringTokenizer est = new EnhancedStringTokenizer( "" );
        assertEquals( "est.hasMoreTokens()", false, est.hasMoreTokens() );
        try
        {
            est.nextToken();
            fail();
        }
        catch ( Exception e )
        {
        }
    }

    public void testSimpleString()
    {
        EnhancedStringTokenizer est = new EnhancedStringTokenizer( "a " );
        assertEquals( "Token 1", "a", est.nextToken() );
        assertEquals( "Token 2", "", est.nextToken() );
        assertEquals( "est.hasMoreTokens()", false, est.hasMoreTokens() );
    }
}
