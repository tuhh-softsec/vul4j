package org.codehaus.plexus.util.cli;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
        StringBuffer sb = new StringBuffer();
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
