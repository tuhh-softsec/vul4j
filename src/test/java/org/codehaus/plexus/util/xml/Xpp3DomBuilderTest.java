package org.codehaus.plexus.util.xml;

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
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Test the Xpp3DomBuilder.
 *
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 * @version $Id$
 */
public class Xpp3DomBuilderTest
    extends TestCase
{

    private static final String LS = System.getProperty( "line.separator" );

    public void testBuildFromReader()
        throws Exception
    {
        String domString = createDomString();

        Xpp3Dom dom = Xpp3DomBuilder.build( new StringReader( domString ) );

        Xpp3Dom expectedDom = createExpectedDom();

        assertEquals( "check DOMs match", expectedDom, dom );
    }

    public void testBuildTrimming()
        throws Exception
    {
        String domString = createDomString();

        Xpp3Dom dom = Xpp3DomBuilder.build( new StringReader( domString ), true );

        assertEquals( "test with trimming on", "element1", dom.getChild( "el1" ).getValue() );

        dom = Xpp3DomBuilder.build( new StringReader( domString ), false );

        assertEquals( "test with trimming off", " element1\n ", dom.getChild( "el1" ).getValue() );
    }

    public void testBuildFromXpp3Dom()
        throws Exception
    {
        Xpp3Dom expectedDom = createExpectedDom();
        Xpp3Dom dom = null;

        XmlPullParser parser = new MXParser();

        String domString = "<newRoot><configuration>" + createDomString() + "</configuration></newRoot>";
        parser.setInput( new StringReader( domString ) );

        int eventType = parser.getEventType();

        boolean configurationClosed = false;
        boolean newRootClosed = false;
        boolean rootClosed = false;

        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                String rawName = parser.getName();

                if ( "root".equals( rawName  ) )
                {
                    dom = Xpp3DomBuilder.build( parser );
                }
            }
            else if ( eventType == XmlPullParser.END_TAG )
            {
                String rawName = parser.getName();

                if ( "configuration".equals( rawName ) )
                {
                    configurationClosed = true;
                }
                else if ( "newRoot".equals( rawName ) )
                {
                    newRootClosed = true;
                }
                else if ( "root".equals( rawName  ) )
                {
                    rootClosed = true;
                }
            }
            eventType = parser.next();
        }

        assertEquals( "Check DOM matches", expectedDom, dom );
        assertFalse( "Check closing root was consumed", rootClosed );
        assertTrue( "Check continued to parse configuration", configurationClosed );
        assertTrue( "Check continued to parse newRoot", newRootClosed );
    }

    /**
     * Test we get an error from the parser, and don't hit the IllegalStateException.
     */
    public void testUnclosedXml()
    {
        String domString = "<newRoot>" + createDomString();
        try
        {
            Xpp3DomBuilder.build( new StringReader( domString ) );
        }
        catch ( XmlPullParserException expected )
        {
            // correct
            assertTrue( true );
        }
        catch ( IOException expected )
        {
            // this will do too
            assertTrue( true );
        }
    }

    public void testEscapingInContent()
        throws IOException, XmlPullParserException
    {
        Xpp3Dom dom = Xpp3DomBuilder.build( new StringReader( getEncodedString() ) );

        assertEquals( "Check content value", "\"text\"", dom.getChild( "el" ).getValue() );
        assertEquals( "Check content value", "<b>\"text\"</b>", dom.getChild( "ela" ).getValue() );
        assertEquals( "Check content value", "<b>\"text\"</b>", dom.getChild( "elb" ).getValue() );

        StringWriter w = new StringWriter();
        Xpp3DomWriter.write( w, dom );
        assertEquals( "Compare stringified DOMs", getExpectedString(), w.toString() );
    }

    public void testEscapingInAttributes()
        throws IOException, XmlPullParserException
    {
        String s = getAttributeEncodedString();
        Xpp3Dom dom = Xpp3DomBuilder.build( new StringReader( s ) );

        assertEquals( "Check attribute value", "<foo>", dom.getChild( "el" ).getAttribute( "att" ) );

        StringWriter w = new StringWriter();
        Xpp3DomWriter.write( w, dom );
        String newString = w.toString();
        assertEquals( "Compare stringified DOMs", newString, s );
    }

    private static String getAttributeEncodedString()
    {
        StringBuilder domString = new StringBuilder();
        domString.append( "<root>" );
        domString.append( LS );
        domString.append( "  <el att=\"&lt;foo&gt;\">bar</el>" );
        domString.append( LS );
        domString.append( "</root>" );

        return domString.toString();
    }

    private static String getEncodedString()
    {
        StringBuilder domString = new StringBuilder();
        domString.append( "<root>\n" );
        domString.append( "  <el>\"text\"</el>\n" );
        domString.append( "  <ela><![CDATA[<b>\"text\"</b>]]></ela>\n" );
        domString.append( "  <elb>&lt;b&gt;&quot;text&quot;&lt;/b&gt;</elb>\n" );
        domString.append( "</root>" );

        return domString.toString();
    }

    private static String getExpectedString()
    {
        StringBuilder domString = new StringBuilder();
        domString.append( "<root>" );
        domString.append( LS );
        domString.append( "  <el>&quot;text&quot;</el>" );
        domString.append( LS );
        domString.append( "  <ela>&lt;b&gt;&quot;text&quot;&lt;/b&gt;</ela>" );
        domString.append( LS );
        domString.append( "  <elb>&lt;b&gt;&quot;text&quot;&lt;/b&gt;</elb>" );
        domString.append( LS );
        domString.append( "</root>" );

        return domString.toString();
    }

    //
    // HELPER METHODS
    //

    private static String createDomString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append( "<root>\n" );
        buf.append( " <el1> element1\n </el1>\n" );
        buf.append( " <el2 att2='attribute2&#10;nextline'>\n" );
        buf.append( "  <el3 att3='attribute3'>element3</el3>\n" );
        buf.append( " </el2>\n" );
        buf.append( " <el4></el4>\n" );
        buf.append( " <el5/>\n" );
        buf.append( " <el6 xml:space=\"preserve\">  do not trim  </el6>\n" );
        buf.append( "</root>\n" );

        return buf.toString();
    }

    private static Xpp3Dom createExpectedDom()
    {
        Xpp3Dom expectedDom = new Xpp3Dom( "root" );
        Xpp3Dom el1 = new Xpp3Dom( "el1" );
        el1.setValue( "element1" );
        expectedDom.addChild( el1 );
        Xpp3Dom el2 = new Xpp3Dom( "el2" );
        el2.setAttribute( "att2", "attribute2\nnextline" );
        expectedDom.addChild( el2 );
        Xpp3Dom el3 = new Xpp3Dom( "el3" );
        el3.setAttribute( "att3", "attribute3" );
        el3.setValue( "element3" );
        el2.addChild( el3 );
        Xpp3Dom el4 = new Xpp3Dom( "el4" );
        el4.setValue( "" );
        expectedDom.addChild( el4 );
        Xpp3Dom el5 = new Xpp3Dom( "el5" );
        expectedDom.addChild( el5 );
        Xpp3Dom el6 = new Xpp3Dom( "el6" );
        el6.setAttribute( "xml:space", "preserve" );
        el6.setValue( "  do not trim  " );
        expectedDom.addChild( el6 );
        return expectedDom;
    }
}
