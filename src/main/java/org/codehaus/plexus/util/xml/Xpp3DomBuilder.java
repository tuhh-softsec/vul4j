package org.codehaus.plexus.util.xml;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Xpp3DomBuilder
{
    private static final boolean DEFAULT_TRIM = true;

    public static Xpp3Dom build( Reader reader )
        throws XmlPullParserException, IOException
    {
        return build( reader, DEFAULT_TRIM );
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

                values.add( new StringBuffer() );

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

                String accumulatedValue = ( values.remove( depth ) ).toString();

                if ( finishedConfiguration.getChildCount() == 0 )
                {
                    String finishedValue;

                    if ( 0 == accumulatedValue.length() )
                    {
                        finishedValue = null;
                    }
                    else
                    {
                        finishedValue = accumulatedValue;
                    }

                    finishedConfiguration.setValue( finishedValue );
                }

                if ( 0 == depth )
                {
                    return finishedConfiguration;
                }
            }

            eventType = parser.next();
        }
        throw new IllegalStateException( "End of document found before returning to 0 depth" );
    }

}