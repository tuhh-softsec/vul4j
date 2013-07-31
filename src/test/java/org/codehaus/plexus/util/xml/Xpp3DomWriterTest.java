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

import java.io.StringWriter;

/**
 * @author Edwin Punzalan
 */
public class Xpp3DomWriterTest
    extends TestCase
{
    private static final String LS = System.getProperty( "line.separator" );

    public void testWriter()
    {
        StringWriter writer = new StringWriter();

        Xpp3DomWriter.write( writer, createXpp3Dom() );

        assertEquals( "Check if output matches", createExpectedXML( true ), writer.toString() );
    }

    public void testWriterNoEscape()
    {
        StringWriter writer = new StringWriter();

        Xpp3DomWriter.write( new PrettyPrintXMLWriter( writer ), createXpp3Dom(), false );

        assertEquals( "Check if output matches", createExpectedXML( false ), writer.toString() );
    }

    private String createExpectedXML( boolean escape )
    {
        StringBuilder buf = new StringBuilder();
        buf.append( "<root>" );
        buf.append( LS );
        buf.append( "  <el1>element1</el1>" );
        buf.append( LS );
        buf.append( "  <el2 att2=\"attribute2&#10;nextline\">" );
        buf.append( LS );
        buf.append( "    <el3 att3=\"attribute3\">element3</el3>" );
        buf.append( LS );
        buf.append( "  </el2>" );
        buf.append( LS );
        buf.append( "  <el4></el4>" );
        buf.append( LS );
        buf.append( "  <el5/>" );
        buf.append( LS );
        buf.append( "  <el6 att6=\"attribute6&#10;&amp;&quot;&apos;&lt;&gt;\">" );
        buf.append( LS );
        if ( escape )
        {
            buf.append( "    <el7>element7" + LS + "&amp;&quot;&apos;&lt;&gt;</el7>" );
        }
        else
        {
            buf.append( "    <el7>element7"+ LS + "&\"\'<></el7>" );
        }
        buf.append( LS );
        buf.append( "  </el6>" );
        buf.append( LS );
        buf.append( "</root>" );

        return buf.toString();
    }

    private Xpp3Dom createXpp3Dom()
    {
        Xpp3Dom dom = new Xpp3Dom( "root" );

        Xpp3Dom el1 = new Xpp3Dom( "el1" );
        el1.setValue( "element1" );
        dom.addChild( el1 );

        Xpp3Dom el2 = new Xpp3Dom( "el2" );
        el2.setAttribute( "att2", "attribute2\nnextline" );
        dom.addChild( el2 );

        Xpp3Dom el3 = new Xpp3Dom( "el3" );
        el3.setAttribute( "att3", "attribute3" );
        el3.setValue( "element3" );
        el2.addChild( el3 );

        Xpp3Dom el4 = new Xpp3Dom( "el4" );
        el4.setValue( "" );
        dom.addChild( el4 );

        Xpp3Dom el5 = new Xpp3Dom( "el5" );
        dom.addChild( el5 );

        // test escaping
        Xpp3Dom el6 = new Xpp3Dom( "el6" );
        el6.setAttribute( "att6", "attribute6\n&\"'<>" );
        dom.addChild( el6 );

        Xpp3Dom el7 = new Xpp3Dom( "el7" );
        el7.setValue( "element7\n&\"\'<>" );
        el6.addChild( el7 );

        return dom;
    }
}
