package org.codehaus.plexus.util;

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

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class InterpolationFilterReaderTest
    extends TestCase
{
    /*
     * Added and commented by jdcasey@03-Feb-2005 because it is a bug in the
     * InterpolationFilterReader.
     * kenneyw@15-04-2005 fixed the bug.
     */
    public void testShouldNotInterpolateExpressionAtEndOfDataWithInvalidEndToken()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "test", "TestValue" );

        String testStr = "This is a ${test";

        assertEquals( "This is a ${test", interpolate( testStr, m ) );
    }

    /*
     * kenneyw@14-04-2005 Added test to check above fix.
     */
    public void testShouldNotInterpolateExpressionWithMissingEndToken()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "test", "TestValue" );

        String testStr = "This is a ${test, really";

        assertEquals( "This is a ${test, really", interpolate( testStr, m ) );
    }

    public void testShouldNotInterpolateWithMalformedStartToken()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "test", "testValue" );

        String foo = "This is a $!test} again";

        assertEquals( "This is a $!test} again", interpolate( foo, m ) );
    }

    public void testShouldNotInterpolateWithMalformedEndToken()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "test", "testValue" );

        String foo = "This is a ${test!} again";

        assertEquals( "This is a ${test!} again", interpolate( foo, m, "${", "$}" ) );
    }

    public void testInterpolationWithMulticharDelimiters()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "test", "testValue" );

        String foo = "This is a ${test$} again";

        assertEquals( "This is a testValue again", interpolate( foo, m, "${", "$}" ) );
    }



    public void testDefaultInterpolationWithNonInterpolatedValueAtEnd()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "name", "jason" );
        m.put( "noun", "asshole" );

        String foo = "${name} is an ${noun}. ${not.interpolated}";

        assertEquals( "jason is an asshole. ${not.interpolated}", interpolate( foo, m ) );
    }

    public void testDefaultInterpolationWithInterpolatedValueAtEnd()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "name", "jason" );
        m.put( "noun", "asshole" );

        String foo = "${name} is an ${noun}";

        assertEquals( "jason is an asshole", interpolate( foo, m ) );
    }

    public void testInterpolationWithSpecifiedBoundaryTokens()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "name", "jason" );
        m.put( "noun", "asshole" );

        String foo = "@name@ is an @noun@. @not.interpolated@ baby @foo@. @bar@";

        assertEquals( "jason is an asshole. @not.interpolated@ baby @foo@. @bar@", interpolate( foo, m, "@", "@" ) );
    }

    public void testInterpolationWithSpecifiedBoundaryTokensWithNonInterpolatedValueAtEnd()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "name", "jason" );
        m.put( "noun", "asshole" );

        String foo = "@name@ is an @foobarred@";

        assertEquals( "jason is an @foobarred@", interpolate( foo, m, "@", "@" ) );
    }

    public void testInterpolationWithSpecifiedBoundaryTokensWithInterpolatedValueAtEnd()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "name", "jason" );
        m.put( "noun", "asshole" );

        String foo = "@name@ is an @noun@";

        assertEquals( "jason is an asshole", interpolate( foo, m, "@", "@" ) );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private String interpolate( String input, Map context )
        throws Exception
    {
        return IOUtil.toString( new InterpolationFilterReader(  new StringReader( input ), context ) );
    }

    private String interpolate( String input, Map context, String startToken, String endToken )
        throws Exception
    {
        return IOUtil.toString( new InterpolationFilterReader( new StringReader( input ),
                                                               context, startToken, endToken ) );
    }
}
