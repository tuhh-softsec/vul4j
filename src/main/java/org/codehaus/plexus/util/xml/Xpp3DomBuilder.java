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
        List<Xpp3Dom> elements = new ArrayList<Xpp3Dom>();

        List<StringBuilder> values = new ArrayList<StringBuilder>();

        int eventType = parser.getEventType();

        boolean spacePreserve = false;

        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                spacePreserve = false;

                String rawName = parser.getName();

                Xpp3Dom childConfiguration = new Xpp3Dom( rawName );

                int depth = elements.size();

                if ( depth > 0 )
                {
                    Xpp3Dom parent = elements.get( depth - 1 );

                    parent.addChild( childConfiguration );
                }

                elements.add( childConfiguration );

                if ( parser.isEmptyElementTag() )
                {
                    values.add( null );
                }
                else
                {
                    values.add( new StringBuilder() );
                }

                int attributesSize = parser.getAttributeCount();

                for ( int i = 0; i < attributesSize; i++ )
                {
                    String name = parser.getAttributeName( i );

                    String value = parser.getAttributeValue( i );

                    childConfiguration.setAttribute( name, value );

                    spacePreserve = spacePreserve || ( "xml:space".equals( name ) && "preserve".equals( value ) );
                }
            }
            else if ( eventType == XmlPullParser.TEXT )
            {
                int depth = values.size() - 1;

                @SuppressWarnings( "MismatchedQueryAndUpdateOfStringBuilder" )
                StringBuilder valueBuffer = values.get( depth );

                String text = parser.getText();

                if ( trim && !spacePreserve )
                {
                    text = text.trim();
                }

                valueBuffer.append( text );
            }
            else if ( eventType == XmlPullParser.END_TAG )
            {
                int depth = elements.size() - 1;

                Xpp3Dom finishedConfiguration = elements.remove( depth );

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
