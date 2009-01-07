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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.WriterFactory;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Common XML utilities methods.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.5.7
 */
public class XmlUtil
{
    /** The default line indenter size i.e. 2. */
    public static final int DEFAULT_INDENTATION_SIZE = 2;

    /** The default line separator ("\n" on UNIX) */
    public static final String DEFAULT_LINE_SEPARATOR = System.getProperty( "line.separator" );

    /**
     * Determines if a given File shall be handled as XML.
     *
     * @param f not null file
     * @return <code>true</code> if the given file has XML content, <code>false</code> otherwise.
     */
    public static boolean isXml( File f )
    {
        if ( f == null )
        {
            throw new IllegalArgumentException( "f could not be null." );
        }

        if ( !f.isFile() )
        {
            throw new IllegalArgumentException( "The file '" + f.getAbsolutePath() + "' is not a file." );
        }

        Reader reader = null;
        try
        {
            reader = ReaderFactory.newXmlReader( f );
            XmlPullParser parser = new MXParser();
            parser.setInput( reader );
            parser.nextToken();

            return true;
        }
        catch ( Exception e )
        {
            return false;
        }
        finally
        {
            IOUtil.close( reader );
        }
    }

    /**
     * Pretty format the input reader. For instance, the following input:
     * <pre>
     * &lt;div&gt;&lt;b&gt;content&lt;/b&gt;&lt;/div&gt;
     * </pre>
     * becomes
     * <pre>
     * &lt;div&gt;
     *   &lt;b&gt;content&lt;/b&gt;
     * &lt;/div&gt;
     * </pre>
     *
     * @param reader not null
     * @param writer not null
     * @throws IOException if any or invalid xml content
     * @see #prettyFormat(Reader, Writer, int, String)
     * @see ReaderFactory to read an xml content
     * @see WriterFactory to write an xml content
     */
    public static void prettyFormat( Reader reader, Writer writer )
        throws IOException
    {
        prettyFormat( reader, writer, DEFAULT_INDENTATION_SIZE, DEFAULT_LINE_SEPARATOR );
    }

    /**
     * Pretty format the input reader. For instance, the following input:
     * <pre>
     * &lt;div&gt;&lt;b&gt;content&lt;/b&gt;&lt;/div&gt;
     * </pre>
     * becomes
     * <pre>
     * &lt;div&gt;
     *   &lt;b&gt;content&lt;/b&gt;
     * &lt;/div&gt;
     * </pre>
     *
     * @param reader not null
     * @param writer not null
     * @param indentSize positive number for the indentation
     * @param lineSeparator the wanted line separator
     * @throws IOException if any or invalid xml content
     * @see ReaderFactory to read an xml content
     * @see WriterFactory to write an xml content
     */
    public static void prettyFormat( Reader reader, Writer writer, int indentSize, String lineSeparator )
        throws IOException
    {
        if ( reader == null )
        {
            throw new IllegalArgumentException( "The reader is null" );
        }
        if ( writer == null )
        {
            throw new IllegalArgumentException( "The writer is null" );
        }
        if ( indentSize < 0 )
        {
            indentSize = 0;
        }

        PrettyPrintXMLWriter xmlWriter = new PrettyPrintXMLWriter( writer );
        xmlWriter.setLineIndenter( StringUtils.repeat( " ", indentSize ) );
        xmlWriter.setLineSeparator( lineSeparator );

        XmlPullParser parser = new MXParser();
        try
        {
            parser.setInput( reader );

            prettyFormatInternal( parser, xmlWriter );
        }
        catch ( XmlPullParserException e )
        {
            throw new IOException( "Unable to parse the XML: " + e.getMessage() );
        }
    }

    /**
     * Pretty format the input stream. For instance, the following input:
     * <pre>
     * &lt;div&gt;&lt;b&gt;content&lt;/b&gt;&lt;/div&gt;
     * </pre>
     * becomes
     * <pre>
     * &lt;div&gt;
     *   &lt;b&gt;content&lt;/b&gt;
     * &lt;/div&gt;
     * </pre>
     *
     * @param is not null
     * @param os not null
     * @throws IOException if any or invalid xml content
     * @see #prettyFormat(InputStream, OutputStream, int, String)
     */
    public static void prettyFormat( InputStream is, OutputStream os )
        throws IOException
    {
        prettyFormat( is, os, DEFAULT_INDENTATION_SIZE, DEFAULT_LINE_SEPARATOR );
    }

    /**
     * Pretty format the input stream. For instance, the following input:
     * <pre>
     * &lt;div&gt;&lt;b&gt;content&lt;/b&gt;&lt;/div&gt;
     * </pre>
     * becomes
     * <pre>
     * &lt;div&gt;
     *   &lt;b&gt;content&lt;/b&gt;
     * &lt;/div&gt;
     * </pre>
     *
     * @param is not null
     * @param os not null
     * @param indentSize positive number for the indentation
     * @param lineSeparator the wanted line separator
     * @throws IOException if any or invalid xml content
     */
    public static void prettyFormat( InputStream is, OutputStream os, int indentSize, String lineSeparator )
        throws IOException
    {
        if ( is == null )
        {
            throw new IllegalArgumentException( "The is is null" );
        }
        if ( os == null )
        {
            throw new IllegalArgumentException( "The os is null" );
        }
        if ( indentSize < 0 )
        {
            indentSize = 0;
        }

        Reader reader = null;

        Writer out = new OutputStreamWriter( os );
        PrettyPrintXMLWriter xmlWriter = new PrettyPrintXMLWriter( out );
        xmlWriter.setLineIndenter( StringUtils.repeat( " ", indentSize ) );
        xmlWriter.setLineSeparator( lineSeparator );

        XmlPullParser parser = new MXParser();
        try
        {
            reader = ReaderFactory.newXmlReader( is );

            parser.setInput( reader );

            prettyFormatInternal( parser, xmlWriter );
        }
        catch ( XmlPullParserException e )
        {
            throw new IOException( "Unable to parse the XML: " + e.getMessage() );
        }
        finally
        {
            IOUtil.close( reader );
            IOUtil.close( out );
        }
    }

    /**
     * @param parser not null
     * @param writer not null
     * @throws XmlPullParserException if any
     * @throws IOException if any
     */
    private static void prettyFormatInternal( XmlPullParser parser, PrettyPrintXMLWriter writer )
        throws XmlPullParserException, IOException
    {
        boolean hasTag = false;
        boolean hasComment = false;
        int eventType = parser.getEventType();
        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                hasTag = true;
                if ( hasComment )
                {
                    writer.writeText( writer.getLineIndenter() );
                    hasComment = false;
                }
                writer.startElement( parser.getName() );
                for ( int i = 0; i < parser.getAttributeCount(); i++ )
                {
                    String key = parser.getAttributeName( i );
                    String value = parser.getAttributeValue( i );
                    writer.addAttribute( key, value );
                }
            }
            else if ( eventType == XmlPullParser.TEXT )
            {
                String text = parser.getText();
                if ( !text.trim().equals( "" ) )
                {
                    text = StringUtils.removeDuplicateWhitespace( text );
                    writer.writeText( text );
                }
            }
            else if ( eventType == XmlPullParser.END_TAG )
            {
                hasTag = false;
                writer.endElement();
            }
            else if ( eventType == XmlPullParser.COMMENT )
            {
                hasComment = true;
                if ( !hasTag )
                {
                    writer.writeMarkup( writer.getLineSeparator() );
                    for ( int i = 0; i < writer.getDepth(); i++ )
                    {
                        writer.writeMarkup( writer.getLineIndenter() );
                    }
                }
                writer.writeMarkup( "<!--" + parser.getText().trim() + " -->" );
                if ( !hasTag )
                {
                    writer.writeMarkup( writer.getLineSeparator() );
                    for ( int i = 0; i < writer.getDepth() - 1; i++ )
                    {
                        writer.writeMarkup( writer.getLineIndenter() );
                    }
                }
            }
            else if ( eventType == XmlPullParser.DOCDECL )
            {
                writer.writeMarkup( "<!DOCTYPE" + parser.getText() + ">" );
                writer.endOfLine();
            }
            else if ( eventType == XmlPullParser.PROCESSING_INSTRUCTION )
            {
                writer.writeMarkup( "<?" + parser.getText() + "?>" );
                writer.endOfLine();
            }
            else if ( eventType == XmlPullParser.CDSECT )
            {
                writer.writeMarkup( "<![CDATA[" + parser.getText() + "]]>" );
            }
            else if ( eventType == XmlPullParser.ENTITY_REF )
            {
                writer.writeMarkup( "&" + parser.getName() + ";" );
            }

            eventType = parser.nextToken();
        }
    }
}
