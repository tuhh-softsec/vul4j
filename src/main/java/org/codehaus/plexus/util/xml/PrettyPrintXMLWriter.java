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

import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.plexus.util.StringUtils;

/**
 * Implementation of XMLWriter which emits nicely formatted documents.
 *
 * @version $Id$
 */
public class PrettyPrintXMLWriter
    implements XMLWriter
{
    /** Line separator ("\n" on UNIX) */
    protected static final String LS = System.getProperty( "line.separator" );

    private PrintWriter writer;

    private LinkedList elementStack = new LinkedList();

    private boolean tagInProgress;

    private int depth;

    private String lineIndenter;

    private String lineSeparator;

    private String encoding;

    private String docType;

    private boolean readyForNewLine;

    private boolean tagIsEmpty;

    /**
     * @param writer not null
     * @param lineIndenter could be null, but the normal way is some spaces.
     */
    public PrettyPrintXMLWriter( PrintWriter writer, String lineIndenter )
    {
        this( writer, lineIndenter, null, null );
    }

    /**
     * @param writer not null
     * @param lineIndenter could be null, but the normal way is some spaces.
     */
    public PrettyPrintXMLWriter( Writer writer, String lineIndenter )
    {
        this( new PrintWriter( writer ), lineIndenter );
    }

    /**
     * @param writer not null
     */
    public PrettyPrintXMLWriter( PrintWriter writer )
    {
        this( writer, null, null );
    }

    /**
     * @param writer not null
     */
    public PrettyPrintXMLWriter( Writer writer )
    {
        this( new PrintWriter( writer ) );
    }

    /**
     * @param writer not null
     * @param lineIndenter could be null, but the normal way is some spaces.
     * @param encoding could be null or invalid.
     * @param doctype could be null.
     */
    public PrettyPrintXMLWriter( PrintWriter writer, String lineIndenter, String encoding, String doctype )
    {
        this( writer, lineIndenter, LS, encoding, doctype );
    }

    /**
     * @param writer not null
     * @param lineIndenter could be null, but the normal way is some spaces.
     * @param encoding could be null or invalid.
     * @param doctype could be null.
     */
    public PrettyPrintXMLWriter( Writer writer, String lineIndenter, String encoding, String doctype )
    {
        this( new PrintWriter( writer ), lineIndenter, encoding, doctype );
    }

    /**
     * @param writer not null
     * @param encoding could be null or invalid.
     * @param doctype could be null.
     */
    public PrettyPrintXMLWriter( PrintWriter writer, String encoding, String doctype )
    {
        this( writer, "  ", encoding, doctype );
    }

    /**
     * @param writer not null
     * @param encoding could be null or invalid.
     * @param doctype could be null.
     */
    public PrettyPrintXMLWriter( Writer writer, String encoding, String doctype )
    {
        this( new PrintWriter( writer ), encoding, doctype );
    }

    /**
     * @param writer not null
     * @param lineIndenter could be null, but the normal way is some spaces.
     * @param lineSeparator could be null, but the normal way is valid line separator ("\n" on UNIX).
     * @param encoding could be null or invalid.
     * @param doctype could be null.
     */
    public PrettyPrintXMLWriter( PrintWriter writer, String lineIndenter, String lineSeparator, String encoding, String doctype )
    {
        setWriter( writer );

        setLineIndenter( lineIndenter );

        setLineSeparator( lineSeparator );

        setEncoding( encoding );

        setDocType( doctype );

        if ( doctype != null || encoding != null )
        {
            writeDocumentHeaders();
        }
    }

    /** {@inheritDoc} */
    public void startElement( String name )
    {
        tagIsEmpty = false;

        finishTag();

        write( "<" );

        write( name );

        elementStack.addLast( name );

        tagInProgress = true;

        setDepth( getDepth() + 1 );

        readyForNewLine = true;

        tagIsEmpty = true;
    }

    /** {@inheritDoc} */
    public void writeText( String text )
    {
        writeText( text, true );
    }

    /** {@inheritDoc} */
    public void writeMarkup( String text )
    {
        writeText( text, false );
    }

    private void writeText( String text, boolean escapeXml )
    {
        readyForNewLine = false;

        tagIsEmpty = false;

        finishTag();

        if ( escapeXml )
        {
            text = escapeXml( text );
        }

        write( StringUtils.unifyLineSeparators( text, lineSeparator ) );
    }

    private static final Pattern amp = Pattern.compile( "&" );
    private static final Pattern lt = Pattern.compile( "<" );
    private static final Pattern gt = Pattern.compile( ">" );
    private static final Pattern dqoute = Pattern.compile( "\"" );
    private static final Pattern sqoute = Pattern.compile( "\'" );

    private static String escapeXml( String text )
    {
        if (text.indexOf('&') >= 0){
            text = amp.matcher( text ).replaceAll( "&amp;" );
        }
        if (text.indexOf('<') >= 0){
            text = lt.matcher( text ).replaceAll( "&lt;" );
        }
        if (text.indexOf('>') >= 0){
            text = gt.matcher( text ).replaceAll( "&gt;" );
        }
        if (text.indexOf('"') >= 0){
            text = dqoute.matcher( text ).replaceAll( "&quot;" );
        }
        if (text.indexOf('\'') >= 0){
            text = sqoute.matcher( text ).replaceAll( "&apos;" );
        }

        return text;
    }

    private static final String crlf_str = "\r\n";

    private static final Pattern crlf = Pattern.compile( crlf_str );
    private static final Pattern lowers = Pattern.compile( "([\000-\037])" );


    private static String escapeXmlAttribute( String text )
    {
        text = escapeXml( text );

        // Windows
        Matcher crlfmatcher = crlf.matcher( text );
        if (text.contains( crlf_str ))
        {
            text = crlfmatcher.replaceAll( "&#10;" );
        }

        Matcher m = lowers.matcher( text );
        StringBuffer b = new StringBuffer();
        while ( m.find() )
        {
            m = m.appendReplacement( b, "&#" + Integer.toString( m.group( 1 ).charAt( 0 ) ) + ";" );
        }
        m.appendTail( b );

        return b.toString();
    }

    /** {@inheritDoc} */
    public void addAttribute( String key, String value )
    {
        write( " " );

        write( key );

        write( "=\"" );

        write( escapeXmlAttribute( value ) );

        write( "\"" );
    }

    /** {@inheritDoc} */
    public void endElement()
    {
        setDepth( getDepth() - 1 );

        if ( tagIsEmpty )
        {
            write( "/" );

            readyForNewLine = false;

            finishTag();

            elementStack.removeLast();
        }
        else
        {
            finishTag();

            write( "</" + elementStack.removeLast() + ">" );
        }

        readyForNewLine = true;
    }

    /**
     * Write a string to the underlying writer
     * @param str
     */
    private void write( String str )
    {
        getWriter().write( str );
    }

    private void finishTag()
    {
        if ( tagInProgress )
        {
            write( ">" );
        }

        tagInProgress = false;

        if ( readyForNewLine )
        {
            endOfLine();
        }
        readyForNewLine = false;

        tagIsEmpty = false;
    }

    /**
     * Get the string used as line indenter
     *
     * @return the line indenter
     */
    protected String getLineIndenter()
    {
        return lineIndenter;
    }

    /**
     * Set the string used as line indenter
     *
     * @param lineIndenter new line indenter, could be null, but the normal way is some spaces.
     */
    protected void setLineIndenter( String lineIndenter )
    {
        this.lineIndenter = lineIndenter;
    }

    /**
     * Get the string used as line separator or LS if not set.
     *
     * @return the line separator
     * @see #LS
     */
    protected String getLineSeparator()
    {
        return lineSeparator;
    }

    /**
     * Set the string used as line separator
     *
     * @param lineSeparator new line separator, could be null but the normal way is valid line separator
     * ("\n" on UNIX).
     */
    protected void setLineSeparator( String lineSeparator )
    {
        this.lineSeparator = lineSeparator;
    }

    /**
     * Write the end of line character (using specified line separator)
     * and start new line with indentation
     *
     * @see #getLineIndenter()
     * @see #getLineSeparator()
     */
    protected void endOfLine()
    {
        write( getLineSeparator() );

        for ( int i = 0; i < getDepth(); i++ )
        {
            write( getLineIndenter() );
        }
    }

    private void writeDocumentHeaders()
    {
        write( "<?xml version=\"1.0\"" );

        if ( getEncoding() != null )
        {
            write( " encoding=\"" + getEncoding() + "\"" );
        }

        write( "?>" );

        endOfLine();

        if ( getDocType() != null )
        {
            write( "<!DOCTYPE " );

            write( getDocType() );

            write( ">" );

            endOfLine();
        }
    }

    /**
     * Set the underlying writer
     *
     * @param writer not null writer
     */
    protected void setWriter( PrintWriter writer )
    {
        if ( writer == null )
        {
            throw new IllegalArgumentException( "writer could not be null");
        }

        this.writer = writer;
    }

    /**
     * Get the underlying writer
     *
     * @return the underlying writer
     */
    protected PrintWriter getWriter()
    {
        return writer;
    }

    /**
     * Set the depth in the xml indentation
     *
     * @param depth new depth
     */
    protected void setDepth( int depth )
    {
        this.depth = depth;
    }

    /**
     * Get the current depth in the xml indentation
     *
     * @return the current depth
     */
    protected int getDepth()
    {
        return depth;
    }

    /**
     * Set the encoding in the xml
     *
     * @param encoding new encoding
     */
    protected void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }

    /**
     * Get the current encoding in the xml
     *
     * @return the current encoding
     */
    protected String getEncoding()
    {
        return encoding;
    }

    /**
     * Set the docType in the xml
     *
     * @param docType new docType
     */
    protected void setDocType( String docType )
    {
        this.docType = docType;
    }

    /**
     * Get the docType in the xml
     *
     * @return the current docType
     */
    protected String getDocType()
    {
        return docType;
    }

    /**
     * @return the current elementStack;
     */
    protected LinkedList getElementStack()
    {
        return elementStack;
    }
}
