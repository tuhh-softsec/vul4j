package org.codehaus.plexus.util.xml;

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

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @version $Id$
 */
public class Xpp3DomBuilder
{
    private static final boolean DEFAULT_TRIM = true;

    public static Xpp3Dom build( Reader reader )
        throws XmlPullParserException, IOException
    {
        return build( reader, DEFAULT_TRIM );
    }

    public static Xpp3Dom build( InputStream is, String encoding )
        throws XmlPullParserException, IOException
    {
        return build( is, encoding, DEFAULT_TRIM );
    }

    public static Xpp3Dom build( InputStream is, String encoding, boolean trim )
        throws XmlPullParserException, IOException
    {
        XmlPullParser parser = new MXParser();

        parser.setInput( is, encoding );

        try
        {
            return build( parser, trim );
        }
        finally
        {
            IOUtil.close( is );
        }
    }

    public static Xpp3Dom build( Reader reader, boolean trim )
        throws XmlPullParserException, IOException
    {
        XmlPullParser parser = new MXParser();

        parser.setInput( reader );

        try
        {
            return build( parser, trim );
        }
        finally
        {
            IOUtil.close( reader );
        }
    }

    public static Xpp3Dom build( XmlPullParser parser )
        throws XmlPullParserException, IOException
    {
        return build( parser, DEFAULT_TRIM );
    }

    public static Xpp3Dom build( XmlPullParser parser, boolean trim )
        throws XmlPullParserException, IOException
    {
        List elements = new ArrayList();

        List values = new ArrayList();

        int eventType = parser.getEventType();

        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                String rawName = parser.getName();

                Xpp3Dom childConfiguration = new Xpp3Dom( rawName );

                int depth = elements.size();

                if ( depth > 0 )
                {
                    Xpp3Dom parent = (Xpp3Dom) elements.get( depth - 1 );

                    parent.addChild( childConfiguration );
                }

                elements.add( childConfiguration );

                if ( parser.isEmptyElementTag() )
                {
                    values.add( null );
                }
                else
                {
                    values.add( new StringBuffer() );
                }

                int attributesSize = parser.getAttributeCount();

                for ( int i = 0; i < attributesSize; i++ )
                {
                    String name = parser.getAttributeName( i );

                    String value = parser.getAttributeValue( i );

                    childConfiguration.setAttribute( name, value );
                }
            }
            else if ( eventType == XmlPullParser.TEXT )
            {
                int depth = values.size() - 1;

                StringBuffer valueBuffer = (StringBuffer) values.get( depth );

                String text = parser.getText();

                if ( trim )
                {
                    text = text.trim();
                }

                valueBuffer.append( text );
            }
            else if ( eventType == XmlPullParser.END_TAG )
            {
                int depth = elements.size() - 1;

                Xpp3Dom finishedConfiguration = (Xpp3Dom) elements.remove( depth );

                /* this Object could be null if it is a singleton tag */
                Object accumulatedValue = values.remove( depth );

                if ( finishedConfiguration.getChildCount() == 0 )
                {
                    if ( accumulatedValue == null )
                    {
                        finishedConfiguration.setValue( null );
                    }
                    else
                    {
                        finishedConfiguration.setValue( accumulatedValue.toString() );
                    }
                }

                if ( depth == 0 )
                {
                    return finishedConfiguration;
                }
            }

            eventType = parser.next();
        }

        throw new IllegalStateException( "End of document found before returning to 0 depth" );
    }
}
