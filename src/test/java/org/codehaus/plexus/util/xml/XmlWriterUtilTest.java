package org.codehaus.plexus.util.xml;

/*
 * Copyright 2008 The Codehaus Foundation.
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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Writer;

import junit.framework.TestCase;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.WriterFactory;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class XmlWriterUtilTest
    extends TestCase
{
    private OutputStream output;

    private Writer writer;

    private XMLWriter xmlWriter;

    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        output = new ByteArrayOutputStream();
        writer = WriterFactory.newXmlWriter( output );
        xmlWriter = new PrettyPrintXMLWriter( writer );
    }

    /** {@inheritDoc} */
    protected void tearDown()
        throws Exception
    {
        super.tearDown();

        xmlWriter = null;
        writer = null;
        output = null;
    }

    /**
     * Test method for {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeLineBreak(org.codehaus.plexus.util.xml.XMLWriter)}.
     *
     * @throws Exception if any
     */
    public void testWriteLineBreakXMLWriter()
        throws Exception
    {
        XmlWriterUtil.writeLineBreak( xmlWriter );
        writer.close();
        assertTrue( StringUtils.countMatches( output.toString(), "\n" ) == 1 );
    }

    /**
     * Test method for {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeLineBreak(org.codehaus.plexus.util.xml.XMLWriter, int)}.
     *
     * @throws Exception if any
     */
    public void testWriteLineBreakXMLWriterInt()
        throws Exception
    {
        XmlWriterUtil.writeLineBreak( xmlWriter, 10 );
        writer.close();
        assertTrue( StringUtils.countMatches( output.toString(), "\n" ) == 10 );
    }

    /**
     * Test method for {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeLineBreak(org.codehaus.plexus.util.xml.XMLWriter, int, int)}.
     *
     * @throws Exception if any
     */
    public void testWriteLineBreakXMLWriterIntInt()
        throws Exception
    {
        XmlWriterUtil.writeLineBreak( xmlWriter, 10, 2 );
        writer.close();
        assertTrue( StringUtils.countMatches( output.toString(), "\n" ) == 10 );
        assertTrue( StringUtils.countMatches( output.toString(), StringUtils
            .repeat( " ", 2 * XmlWriterUtil.DEFAULT_INDENTATION_SIZE ) ) == 1 );
    }

    /**
     * Test method for {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeLineBreak(org.codehaus.plexus.util.xml.XMLWriter, int, int, int)}.
     *
     * @throws Exception if any
     */
    public void testWriteLineBreakXMLWriterIntIntInt()
        throws Exception
    {
        XmlWriterUtil.writeLineBreak( xmlWriter, 10, 2, 4 );
        writer.close();
        assertTrue( StringUtils.countMatches( output.toString(), "\n" ) == 10 );
        assertTrue( StringUtils.countMatches( output.toString(), StringUtils.repeat( " ", 2 * 4 ) ) == 1 );
    }

    /**
     * Test method for {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeCommentLineBreak(org.codehaus.plexus.util.xml.XMLWriter)}.
     *
     * @throws Exception if any
     */
    public void testWriteCommentLineBreakXMLWriter()
        throws Exception
    {
        XmlWriterUtil.writeCommentLineBreak( xmlWriter );
        writer.close();
        StringBuffer sb = new StringBuffer();
        sb.append( "<!-- ====================================================================== -->" ).append( '\n' );
        assertEquals( output.toString(), sb.toString() );
        assertTrue( output.toString().length() == XmlWriterUtil.DEFAULT_COLUMN_LINE );
    }

    /**
     * Test method for {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeCommentLineBreak(org.codehaus.plexus.util.xml.XMLWriter, int)}.
     *
     * @throws Exception if any
     */
    public void testWriteCommentLineBreakXMLWriterInt()
        throws Exception
    {
        XmlWriterUtil.writeCommentLineBreak( xmlWriter, 20 );
        writer.close();
        assertEquals( output.toString(), "<!-- ========== -->\n" );

        tearDown();
        setUp();

        XmlWriterUtil.writeCommentLineBreak( xmlWriter, 10 );
        writer.close();
        assertEquals( output.toString(), output.toString(), "<!--  -->\n" );
    }

    /**
     * Test method for {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeComment(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String)}.
     *
     * @throws Exception if any
     */
    public void testWriteCommentXMLWriterString()
        throws Exception
    {
        XmlWriterUtil.writeComment( xmlWriter, "hello" );
        writer.close();
        StringBuffer sb = new StringBuffer();
        sb.append( "<!-- hello                                                                  -->" ).append( '\n' );
        assertEquals( output.toString(), sb.toString() );
        assertTrue( output.toString().length() == XmlWriterUtil.DEFAULT_COLUMN_LINE );

        tearDown();
        setUp();

        XmlWriterUtil.writeComment( xmlWriter,
                                    "hellooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" );
        writer.close();
        sb = new StringBuffer();
        sb.append( "<!-- hellooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo -->" )
            .append( '\n' );
        assertEquals( output.toString(), sb.toString() );
        assertTrue( output.toString().length() >= XmlWriterUtil.DEFAULT_COLUMN_LINE );

        tearDown();
        setUp();

        XmlWriterUtil.writeComment( xmlWriter, "hello\nworld" );
        writer.close();
        writer.close();
        sb = new StringBuffer();
        sb.append( "<!-- hello                                                                  -->" ).append( '\n' );
        sb.append( "<!-- world                                                                  -->" ).append( '\n' );
        assertEquals( output.toString(), sb.toString() );
        assertTrue( output.toString().length() == 2 * XmlWriterUtil.DEFAULT_COLUMN_LINE );
    }

    /**
     * Test method for {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeComment(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String, int)}.
     *
     * @throws Exception if any
     */
    public void testWriteCommentXMLWriterStringInt()
        throws Exception
    {
        String indent = StringUtils.repeat( " ", 2 * XmlWriterUtil.DEFAULT_INDENTATION_SIZE );

        XmlWriterUtil.writeComment( xmlWriter, "hello", 2 );
        writer.close();
        StringBuffer sb = new StringBuffer();
        sb.append( indent );
        sb.append( "<!-- hello                                                                  -->" ).append( '\n' );
        assertEquals( output.toString(), sb.toString() );
        assertTrue( output.toString().length() == XmlWriterUtil.DEFAULT_COLUMN_LINE + 2
            * XmlWriterUtil.DEFAULT_INDENTATION_SIZE );

        tearDown();
        setUp();

        XmlWriterUtil.writeComment( xmlWriter, "hello\nworld", 2 );
        writer.close();
        sb = new StringBuffer();
        sb.append( indent );
        sb.append( "<!-- hello                                                                  -->" ).append( '\n' );
        sb.append( indent );
        sb.append( "<!-- world                                                                  -->" ).append( '\n' );
        assertEquals( output.toString(), sb.toString() );
        assertTrue( output.toString().length() == 2 * XmlWriterUtil.DEFAULT_COLUMN_LINE + 2 * indent.length() );
    }

    /**
     * Test method for {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeComment(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String, int, int)}.
     *
     * @throws Exception if any
     */
    public void testWriteCommentXMLWriterStringIntInt()
        throws Exception
    {
        String repeat = StringUtils.repeat( " ", 2 * 4 );

        XmlWriterUtil.writeComment( xmlWriter, "hello", 2, 4 );
        writer.close();
        StringBuffer sb = new StringBuffer();
        sb.append( repeat );
        sb.append( "<!-- hello                                                                  -->" ).append( '\n' );
        assertEquals( output.toString(), sb.toString() );
        assertTrue( output.toString().length() == XmlWriterUtil.DEFAULT_COLUMN_LINE + 2 * 4 );

        tearDown();
        setUp();

        XmlWriterUtil.writeComment( xmlWriter, "hello\nworld", 2, 4 );
        writer.close();
        sb = new StringBuffer();
        sb.append( repeat );
        sb.append( "<!-- hello                                                                  -->" ).append( '\n' );
        sb.append( repeat );
        sb.append( "<!-- world                                                                  -->" ).append( '\n' );
        assertEquals( output.toString(), sb.toString() );
        assertTrue( output.toString().length() == 2 * XmlWriterUtil.DEFAULT_COLUMN_LINE + 2 * repeat.length() );
    }

    /**
     * Test method for {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeComment(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String, int, int, int)}.
     *
     * @throws Exception if any
     */
    public void testWriteCommentXMLWriterStringIntIntInt()
        throws Exception
    {
        String indent = StringUtils.repeat( " ", 2 * 4 );

        XmlWriterUtil.writeComment( xmlWriter, "hello", 2, 4, 50 );
        writer.close();
        StringBuffer sb = new StringBuffer();
        sb.append( indent );
        sb.append( "<!-- hello                                    -->" ).append( '\n' );
        assertEquals( output.toString(), sb.toString() );
        assertTrue( output.toString().length() == 50 + 2 * 4 );

        tearDown();
        setUp();

        XmlWriterUtil.writeComment( xmlWriter, "hello", 2, 4, 10 );
        writer.close();
        sb = new StringBuffer();
        sb.append( indent );
        sb.append( "<!-- hello -->" ).append( '\n' );
        assertEquals( output.toString(), sb.toString() );
        assertTrue( output.toString().length() >= 10 + 2 * 4 );
    }

    /**
     * Test method for {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeCommentText(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String, int)}.
     *
     * @throws Exception if any
     */
    public void testWriteCommentTextXMLWriterStringInt()
        throws Exception
    {
        XmlWriterUtil.writeCommentText( xmlWriter, "hello", 0 );
        writer.close();
        StringBuffer sb = new StringBuffer();
        sb.append( '\n' );
        sb.append( "<!-- ====================================================================== -->" ).append( '\n' );
        sb.append( "<!-- hello                                                                  -->" ).append( '\n' );
        sb.append( "<!-- ====================================================================== -->" ).append( '\n' );
        sb.append( '\n' );
        assertEquals( output.toString(), sb.toString() );
        assertTrue( output.toString().length() == 3 * 80 + 2 );

        tearDown();
        setUp();

        String indent = StringUtils.repeat( " ", 2 * 2 );

        XmlWriterUtil.writeCommentText( xmlWriter, "hello world with end of line\n and "
            + "loooooooooooooooooooooooooooooooooooooooooooooooooooooonnnnnnnnnnong line", 2 );
        writer.close();
        sb = new StringBuffer();
        sb.append( '\n' );
        sb.append( indent ).append( "<!-- ====================================================================== -->" )
            .append( '\n' );
        sb.append( indent ).append( "<!-- hello world with end of line                                           -->" )
            .append( '\n' );
        sb.append( indent ).append( "<!-- and                                                                    -->" )
            .append( '\n' );
        sb.append( indent ).append( "<!-- loooooooooooooooooooooooooooooooooooooooooooooooooooooonnnnnnnnnnong   -->" )
            .append( '\n' );
        sb.append( indent ).append( "<!-- line                                                                   -->" )
            .append( '\n' );
        sb.append( indent ).append( "<!-- ====================================================================== -->" )
            .append( '\n' );
        sb.append( '\n' );
        sb.append( indent );
        assertEquals( output.toString(), sb.toString() );
    }

    /**
     * Test method for {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeCommentText(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String, int, int)}.
     *
     * @throws Exception if any
     */
    public void testWriteCommentTextXMLWriterStringIntInt()
        throws Exception
    {
        String indent = StringUtils.repeat( " ", 2 * 4 );

        XmlWriterUtil.writeCommentText( xmlWriter, "hello", 2, 4 );
        writer.close();
        StringBuffer sb = new StringBuffer();
        sb.append( '\n' );
        sb.append( indent ).append( "<!-- ====================================================================== -->" )
            .append( '\n' );
        sb.append( indent ).append( "<!-- hello                                                                  -->" )
            .append( '\n' );
        sb.append( indent ).append( "<!-- ====================================================================== -->" )
            .append( '\n' );
        sb.append( '\n' );
        sb.append( indent );
        assertEquals( output.toString(), sb.toString() );
        assertTrue( output.toString().length() == 3 * 80 + 4 * 2 * 4 + 2 );
    }

    /**
     * Test method for {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeCommentText(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String, int, int, int)}.
     *
     * @throws Exception if any
     */
    public void testWriteCommentTextXMLWriterStringIntIntInt()
        throws Exception
    {
        String indent = StringUtils.repeat( " ", 2 * 4 );

        XmlWriterUtil.writeCommentText( xmlWriter, "hello", 2, 4, 50 );
        writer.close();
        StringBuffer sb = new StringBuffer();
        sb.append( '\n' );
        sb.append( indent ).append( "<!-- ======================================== -->" ).append( '\n' );
        sb.append( indent ).append( "<!-- hello                                    -->" ).append( '\n' );
        sb.append( indent ).append( "<!-- ======================================== -->" ).append( '\n' );
        sb.append( '\n' );
        sb.append( indent );
        assertEquals( output.toString(), sb.toString() );
        assertTrue( output.toString().length() == 3 * 50 + 4 * 2 * 4 + 2 );
    }

    /**
     * Test method for {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeComment(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String)}.
     *
     * @throws Exception if any
     */
    public void testWriteCommentNull()
        throws Exception
    {
        XmlWriterUtil.writeComment( xmlWriter, null );
        writer.close();
        StringBuffer sb = new StringBuffer();
        sb.append( "<!-- null                                                                   -->" ).append( '\n' );
        assertEquals( output.toString(), sb.toString() );
    }

    /**
     * Test method for {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeComment(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String)}.
     *
     * @throws Exception if any
     */
    public void testWriteCommentShort()
        throws Exception
    {
        XmlWriterUtil.writeComment( xmlWriter, "This is a short text" );
        writer.close();
        StringBuffer sb = new StringBuffer();
        sb.append( "<!-- This is a short text                                                   -->" ).append( '\n' );
        assertEquals( output.toString(), sb.toString() );
    }

    /**
     * Test method for {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeComment(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String)}.
     *
     * @throws Exception if any
     */
    public void testWriteCommentLong()
        throws Exception
    {
        XmlWriterUtil.writeComment( xmlWriter, "Maven is a software project management and comprehension tool. "
            + "Based on the concept of a project object model (POM), Maven can manage a project's build, reporting "
            + "and documentation from a central piece of information." );
        writer.close();
        StringBuffer sb = new StringBuffer();
        sb.append( "<!-- Maven is a software project management and comprehension tool. Based   -->" ).append( '\n' );
        sb.append( "<!-- on the concept of a project object model (POM), Maven can manage a     -->" ).append( '\n' );
        sb.append( "<!-- project's build, reporting and documentation from a central piece of   -->" ).append( '\n' );
        sb.append( "<!-- information.                                                           -->" ).append( '\n' );
        assertEquals( output.toString(), sb.toString() );
    }

}
