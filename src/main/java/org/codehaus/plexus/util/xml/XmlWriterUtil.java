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

import org.codehaus.plexus.util.StringUtils;

/**
 * Utility class for the <code>XmlWriter</code> class.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class XmlWriterUtil
{
    /**
     * The default line indenter i.e. 2.
     */
    public static final int DEFAULT_INDENTATION_SIZE = 2;

    /**
     * The default column before line wrapping i.e. 80.
     */
    public static final int DEFAULT_COLUMN_LINE = 80;

    /**
     * Convenience method to write one <code>CRLF</code>
     *
     * @param writer not null writer
     */
    public static void writeLineBreak( XMLWriter writer )
    {
        writeLineBreak( writer, 1 );
    }

    /**
     * Convenience method to repeat <code>CRLF</code>
     *
     * @param writer not null
     * @param repeat positive number
     */
    public static void writeLineBreak( XMLWriter writer, int repeat )
    {
        for ( int i = 0; i < repeat; i++ )
        {
            writer.writeMarkup( "\n" );
        }
    }

    /**
     * Convenience method to repeat <code>CRLF</code> and to indent the writer
     *
     * @param writer not null
     * @param repeat
     * @param indent positive number
     * @see #DEFAULT_INDENTATION_SIZE
     * @see #writeLineBreak(XMLWriter, int, int, int)
     */
    public static void writeLineBreak( XMLWriter writer, int repeat, int indent )
    {
        writeLineBreak( writer, repeat, indent, DEFAULT_INDENTATION_SIZE );
    }

    /**
     * Convenience method to repeat <code>CRLF</code> and to indent the writer
     *
     * @param writer not null
     * @param repeat
     * @param indent positive number
     * @param indentSize positive number
     */
    public static void writeLineBreak( XMLWriter writer, int repeat, int indent, int indentSize )
    {
        writeLineBreak( writer, repeat );

        if ( indent < 0 )
        {
            indent = 0;
        }

        if ( indentSize < 0 )
        {
            indentSize = 0;
        }

        writer.writeText( StringUtils.repeat( " ", indent * indentSize ) );
    }

    /**
     * Convenience method to write XML comment line break. Its size is 80.
     *
     * @param writer not null
     * @see #DEFAULT_COLUMN_LINE
     * @see #writeCommentLineBreak(XMLWriter, int)
     */
    public static void writeCommentLineBreak( XMLWriter writer )
    {
        writeCommentLineBreak( writer, DEFAULT_COLUMN_LINE );
    }

    /**
     * Convenience method to write XML comment line break.
     *
     * @param writer not null
     * @param columnSize positive number
     */
    public static void writeCommentLineBreak( XMLWriter writer, int columnSize )
    {
        if ( columnSize < 0 )
        {
            columnSize = DEFAULT_COLUMN_LINE;
        }

        writer.writeMarkup( "<!-- " + StringUtils.repeat( "=", columnSize - 10 ) + " -->\n" );
    }

    /**
     * Convenience method to write XML comment line. The <code>comment</code> is splitted to have a size of 80.
     *
     * @param writer not null
     * @param comment
     */
    public static void writeComment( XMLWriter writer, String comment )
    {
        if ( comment == null )
        {
            comment = "null";
        }

        String[] words = StringUtils.split( comment, " " );

        StringBuffer line = new StringBuffer( "<!-- " );
        for ( int i = 0; i < words.length; i++ )
        {
            String[] sentences = StringUtils.split( words[i], "\n" );
            if ( sentences.length > 1 )
            {
                for ( int j = 0; j < sentences.length - 1; j++ )
                {
                    line.append( sentences[j] ).append( ' ' );
                    line.append( StringUtils.repeat( " ", 76 - line.length() ) ).append( "-->" ).append( '\n' );
                    writer.writeMarkup( line.toString() );
                    line = new StringBuffer( "<!-- " );
                }
                line.append( sentences[sentences.length - 1] ).append( ' ' );
            }
            else
            {
                StringBuffer sentenceTmp = new StringBuffer( line.toString() );
                sentenceTmp.append( words[i] ).append( ' ' );
                if ( sentenceTmp.length() > 76 )
                {
                    line.append( StringUtils.repeat( " ", 76 - line.length() ) ).append( "-->" ).append( '\n' );
                    writer.writeMarkup( line.toString() );
                    line = new StringBuffer( "<!-- " );
                    line.append( words[i] ).append( ' ' );
                }
                else
                {
                    line.append( words[i] ).append( ' ' );
                }
            }
        }
        if ( line.length() <= 76 )
        {
            line.append( StringUtils.repeat( " ", 76 - line.length() ) ).append( "-->" ).append( '\n' );
        }
        writer.writeMarkup( line.toString() );
    }

    /**
     * Convenience method to write XML comment between two comment line break.
     * The XML comment block is also indented.
     *
     * @param writer not null
     * @param comment
     * @param indent positive number
     * @see #DEFAULT_INDENTATION_SIZE
     * @see #writeCommentText(XMLWriter, String, int, int)
     */
    public static void writeCommentText( XMLWriter writer, String comment, int indent )
    {
        writeCommentText( writer, comment, indent, DEFAULT_INDENTATION_SIZE );
    }

    /**
     * Convenience method to write XML comment between two comment line break.
     * The XML comment block is also indented.
     *
     * @param writer not null
     * @param comment
     * @param indent positive number
     * @param indentSize positive number
     */
    public static void writeCommentText( XMLWriter writer, String comment, int indent, int indentSize )
    {
        if ( indent < 0 )
        {
            indent = 0;
        }

        if ( indentSize < 0 )
        {
            indentSize = 0;
        }

        writeLineBreak( writer, 1 );

        writer.writeMarkup( StringUtils.repeat( " ", indent * indentSize ) );
        writeCommentLineBreak( writer );

        writer.writeMarkup( StringUtils.repeat( " ", indent * indentSize ) );
        writeComment( writer, comment );

        writer.writeMarkup( StringUtils.repeat( " ", indent * indentSize ) );
        writeCommentLineBreak( writer );

        writeLineBreak( writer, 1, indent );
    }
}
