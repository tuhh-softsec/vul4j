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

import java.io.StringWriter;

import javax.swing.text.html.HTML.Tag;

import org.codehaus.plexus.util.StringUtils;

import junit.framework.TestCase;

/**
 * Test of {@link PrettyPrintXMLWriter}
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class PrettyPrintXMLWriterTest
    extends TestCase
{
    StringWriter w;

    PrettyPrintXMLWriter writer;

    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        initWriter();
    }

    /** {@inheritDoc} */
    protected void tearDown()
        throws Exception
    {
        super.tearDown();

        writer = null;
        w = null;
    }

    private void initWriter()
    {
        w = new StringWriter();
        writer = new PrettyPrintXMLWriter( w );
    }

    public void testDefaultPrettyPrintXMLWriter()
    {
        writer.startElement( Tag.HTML.toString() );

        writeXhtmlHead( writer );

        writeXhtmlBody( writer );

        writer.endElement(); // Tag.HTML

        assertEquals( expectedResult( PrettyPrintXMLWriter.LS ), w.toString() );
    }

    public void testPrettyPrintXMLWriterWithGivenLineSeparator()
    {
        writer.setLineSeparator( "\n" );

        writer.startElement( Tag.HTML.toString() );

        writeXhtmlHead( writer );

        writeXhtmlBody( writer );

        writer.endElement(); // Tag.HTML

        assertEquals( expectedResult( "\n" ), w.toString() );
    }

    public void testPrettyPrintXMLWriterWithGivenLineIndenter()
    {
        writer.setLineIndenter( "    " );

        writer.startElement( Tag.HTML.toString() );

        writeXhtmlHead( writer );

        writeXhtmlBody( writer );

        writer.endElement(); // Tag.HTML

        assertEquals( expectedResult( "    ", PrettyPrintXMLWriter.LS ), w.toString() );
    }

    public void testEscapeXmlAttribute()
    {
        // Windows
        writer.startElement( Tag.DIV.toString() );
        writer.addAttribute( "class", "sect\r\nion" );
        writer.endElement(); // Tag.DIV
        assertEquals( "<div class=\"sect&#10;ion\"/>", w.toString() );

        // Mac
        initWriter();
        writer.startElement( Tag.DIV.toString() );
        writer.addAttribute( "class", "sect\rion" );
        writer.endElement(); // Tag.DIV
        assertEquals( "<div class=\"sect&#13;ion\"/>", w.toString() );

        // Unix
        initWriter();
        writer.startElement( Tag.DIV.toString() );
        writer.addAttribute( "class", "sect\nion" );
        writer.endElement(); // Tag.DIV
        assertEquals( "<div class=\"sect&#10;ion\"/>", w.toString() );
    }

    private void writeXhtmlHead( XMLWriter writer )
    {
        writer.startElement( Tag.HEAD.toString() );
        writer.startElement( Tag.TITLE.toString() );
        writer.writeText( "title" );
        writer.endElement(); // Tag.TITLE
        writer.startElement( Tag.META.toString() );
        writer.addAttribute( "name", "author" );
        writer.addAttribute( "content", "Author" );
        writer.endElement(); // Tag.META
        writer.startElement( Tag.META.toString() );
        writer.addAttribute( "name", "date" );
        writer.addAttribute( "content", "Date" );
        writer.endElement(); // Tag.META
        writer.endElement(); // Tag.HEAD
    }

    private void writeXhtmlBody( XMLWriter writer )
    {
        writer.startElement( Tag.BODY.toString() );
        writer.startElement( Tag.P.toString() );
        writer.writeText( "Paragraph 1, line 1. Paragraph 1, line 2." );
        writer.endElement(); // Tag.P
        writer.startElement( Tag.DIV.toString() );
        writer.addAttribute( "class", "section" );
        writer.startElement( Tag.H2.toString() );
        writer.writeText( "Section title" );
        writer.endElement(); // Tag.H2
        writer.endElement(); // Tag.DIV
        writer.endElement(); // Tag.BODY
    }

    private String expectedResult( String lineSeparator )
    {
        return expectedResult( "  ", lineSeparator );
    }

    private String expectedResult( String lineIndenter, String lineSeparator )
    {
        StringBuilder expected = new StringBuilder();

        expected.append( "<html>" ).append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter, 1 ) ).append( "<head>" ).append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter, 2 ) ).append( "<title>title</title>" )
                .append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter, 2 ) )
                .append( "<meta name=\"author\" content=\"Author\"/>" ).append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter, 2 ) ).append( "<meta name=\"date\" content=\"Date\"/>" )
                .append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter, 1 ) ).append( "</head>" ).append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter, 1 ) ).append( "<body>" ).append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter, 2 ) )
                .append( "<p>Paragraph 1, line 1. Paragraph 1, line 2.</p>" ).append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter, 2 ) ).append( "<div class=\"section\">" )
                .append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter, 3 ) ).append( "<h2>Section title</h2>" )
                .append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter, 2 ) ).append( "</div>" ).append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter, 1 ) ).append( "</body>" ).append( lineSeparator );
        expected.append( "</html>" );

        return expected.toString();
    }
}
