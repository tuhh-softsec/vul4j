package org.codehaus.plexus.util.xml;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;

public class PrettyPrintXMLWriter
    implements XMLWriter
{

    private PrintWriter writer;

    private LinkedList elementStack = new LinkedList();

    private boolean tagInProgress;

    private int depth;

    private String lineIndenter;

    private boolean readyForNewLine;

    private boolean tagIsEmpty;

    private boolean documentStarted;

    private String encoding;

    private String docType;

    public PrettyPrintXMLWriter( PrintWriter writer, String lineIndenter )
    {
        this.writer = writer;
        this.lineIndenter = lineIndenter;
    }

    public PrettyPrintXMLWriter( Writer writer, String lineIndenter )
    {
        this( new PrintWriter( writer ), lineIndenter );
    }

    public PrettyPrintXMLWriter( PrintWriter writer )
    {
        this( writer, "  " );
    }

    public PrettyPrintXMLWriter( Writer writer )
    {
        this( new PrintWriter( writer ) );
    }

    public void setEncoding( String encoding )
    {
        if ( documentStarted )
        {
            throw new IllegalStateException( "encoding should be set before starting writing the document." );
        }
        this.encoding = encoding;
    }

    public void setDocType( String docType )
    {
        if ( documentStarted )
        {
            throw new IllegalStateException( "docType should be set before starting writing the document." );
        }
        this.docType = docType;

    }

    public void startElement( String name )
    {
        if ( !documentStarted )
        {
            writeDocumentHeaders();
            documentStarted = true;
        }
        tagIsEmpty = false;
        finishTag();
        write( "<" );
        write( name );
        elementStack.addLast( name );
        tagInProgress = true;
        depth++;
        readyForNewLine = true;
        tagIsEmpty = true;
    }

    public void writeText( String text )
    {
        writeText( text, true );
    }

    public void writeMarkup( String text )
    {
        writeText( text, false );
    }

    private void writeText( String text, boolean escapeHtml )
    {
        readyForNewLine = false;
        tagIsEmpty = false;
        finishTag();
        if ( escapeHtml )
        {
            text = text.replaceAll( "&", "&amp;" );
            text = text.replaceAll( "<", "&lt;" );
            text = text.replaceAll( ">", "&gt;" );
        }
        write( text );
    }

    public void addAttribute( String key, String value )
    {
        write( " " );
        write( key );
        write( "=\"" );
        write( value );
        write( "\"" );
    }

    public void endElement()
    {
        depth--;
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

    private void write( String str )
    {
        writer.write( str );
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

    protected void endOfLine()
    {
        write( "\n" );
        for ( int i = 0; i < depth; i++ )
        {
            write( lineIndenter );
        }
    }

    private void writeDocumentHeaders()
    {
        write( "<?xml version=\"1.0\"" );
        if ( encoding != null )
        {
            write( " encoding=\"" + encoding + "\"" );
        }
        write( "?>" );
        //
        endOfLine();
        if ( docType != null )
        {
            write( "<!DOCTYPE " );
            write( docType );
            write( ">" );
            endOfLine();
        }
    }
}
