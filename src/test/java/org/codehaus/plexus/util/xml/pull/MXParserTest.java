package org.codehaus.plexus.util.xml.pull;

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

import junit.framework.TestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class MXParserTest
    extends TestCase
{
    public void testHexadecimalEntities()
        throws Exception
    {
        MXParser parser = new MXParser();

        parser.defineEntityReplacementText( "test", "replacement" );

        String input = "<root>&#x41;</root>";

        parser.setInput( new StringReader( input ) );

        assertEquals( XmlPullParser.START_TAG, parser.next() );

        assertEquals( XmlPullParser.TEXT, parser.next() );

        assertEquals( "A", parser.getText() );

        assertEquals( XmlPullParser.END_TAG, parser.next() );
    }

    public void testDecimalEntities()
        throws Exception
    {
        MXParser parser = new MXParser();

        parser.defineEntityReplacementText( "test", "replacement" );

        String input = "<root>&#65;</root>";

        parser.setInput( new StringReader( input ) );

        assertEquals( XmlPullParser.START_TAG, parser.next() );

        assertEquals( XmlPullParser.TEXT, parser.next() );

        assertEquals( "A", parser.getText() );

        assertEquals( XmlPullParser.END_TAG, parser.next() );
    }

    public void testPredefinedEntities()
        throws Exception
    {
        MXParser parser = new MXParser();

        parser.defineEntityReplacementText( "test", "replacement" );

        String input = "<root>&lt;&gt;&amp;&apos;&quot;</root>";

        parser.setInput( new StringReader( input ) );

        assertEquals( XmlPullParser.START_TAG, parser.next() );

        assertEquals( XmlPullParser.TEXT, parser.next() );

        assertEquals( "<>&'\"", parser.getText() );

        assertEquals( XmlPullParser.END_TAG, parser.next() );
    }

    public void testCustomEntities()
        throws Exception
    {
        MXParser parser = new MXParser();

        String input = "<root>&myentity;</root>";

        parser.setInput( new StringReader( input ) );

        parser.defineEntityReplacementText( "myentity", "replacement" );

        assertEquals( XmlPullParser.START_TAG, parser.next() );

        assertEquals( XmlPullParser.TEXT, parser.next() );

        assertEquals( "replacement", parser.getText() );

        assertEquals( XmlPullParser.END_TAG, parser.next() );
    }
}
