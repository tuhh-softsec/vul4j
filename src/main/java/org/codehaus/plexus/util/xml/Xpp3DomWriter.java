package org.codehaus.plexus.util.xml;

/*
 * Copyright 2007 The Codehaus Foundation.
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
import java.io.PrintWriter;
import java.io.Writer;

/**
 * @version $Id$
 */
public class Xpp3DomWriter
{
    public static void write( Writer writer, Xpp3Dom dom )
    {
        write( new PrettyPrintXMLWriter( writer ), dom );
    }

    public static void write( PrintWriter writer, Xpp3Dom dom )
    {
        write( new PrettyPrintXMLWriter( writer ), dom );
    }

    public static void write( XMLWriter xmlWriter, Xpp3Dom dom )
    {
        write( xmlWriter, dom, true );
    }

    public static void write( XMLWriter xmlWriter, Xpp3Dom dom, boolean escape )
    {
        // TODO: move to XMLWriter?
        xmlWriter.startElement( dom.getName() );
        String[] attributeNames = dom.getAttributeNames();
        for ( int i = 0; i < attributeNames.length; i++ )
        {
            String attributeName = attributeNames[i];
            xmlWriter.addAttribute( attributeName, dom.getAttribute( attributeName ) );
        }
        Xpp3Dom[] children = dom.getChildren();
        for ( int i = 0; i < children.length; i++ )
        {
            write( xmlWriter, children[i] );
        }

        String value = dom.getValue();
        if ( value != null )
        {
            if ( escape )
            {
                xmlWriter.writeText( value );
            }
            else
            {
                xmlWriter.writeMarkup( value );
            }
        }

        xmlWriter.endElement();
    }


}
