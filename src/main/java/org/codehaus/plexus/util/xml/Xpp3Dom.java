package org.codehaus.plexus.util.xml;

import org.codehaus.plexus.util.xml.pull.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Xpp3Dom
{
    protected String name;

    protected String value;

    protected Map attributes;

    protected List childList;

    protected Map childMap;

    protected Xpp3Dom parent;

    public Xpp3Dom( String name )
    {
        this.name = name;
        childList = new ArrayList();
        childMap = new HashMap();
    }

    // ----------------------------------------------------------------------
    // Name handling
    // ----------------------------------------------------------------------

    public String getName()
    {
        return name;
    }

    // ----------------------------------------------------------------------
    // Value handling
    // ----------------------------------------------------------------------

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    // ----------------------------------------------------------------------
    // Attribute handling
    // ----------------------------------------------------------------------

    public String[] getAttributeNames()
    {
        if ( null == attributes )
        {
            return new String[0];
        }
        else
        {
            return (String[]) attributes.keySet().toArray( new String[0] );
        }
    }

    public String getAttribute( String name )
    {
        return ( null != attributes ) ? (String) attributes.get( name ) : null;
    }

    public void setAttribute( String name, String value )
    {
        if ( null == attributes )
        {
            attributes = new HashMap();
        }

        attributes.put( name, value );
    }

    // ----------------------------------------------------------------------
    // Child handling
    // ----------------------------------------------------------------------

    public Xpp3Dom getChild( int i )
    {
        return (Xpp3Dom) childList.get( i );
    }

    public Xpp3Dom getChild( String name )
    {
        return (Xpp3Dom) childMap.get( name );
    }

    public void addChild( Xpp3Dom xpp3Dom )
    {
        xpp3Dom.setParent( this );
        childList.add( xpp3Dom );
        childMap.put( xpp3Dom.getName(), xpp3Dom );
    }

    public Xpp3Dom[] getChildren()
    {
        if ( null == childList )
        {
            return new Xpp3Dom[0];
        }
        else
        {
            return (Xpp3Dom[]) childList.toArray( new Xpp3Dom[0] );
        }
    }

    public Xpp3Dom[] getChildren( String name )
    {
        if ( null == childList )
        {
            return new Xpp3Dom[0];
        }
        else
        {
            ArrayList children = new ArrayList();
            int size = this.childList.size();

            for ( int i = 0; i < size; i++ )
            {
                Xpp3Dom configuration = (Xpp3Dom) this.childList.get( i );
                if ( name.equals( configuration.getName() ) )
                {
                    children.add( configuration );
                }
            }

            return (Xpp3Dom[]) children.toArray( new Xpp3Dom[0] );
        }
    }

    public int getChildCount()
    {
        if ( null == childList )
        {
            return 0;
        }

        return childList.size();
    }

    // ----------------------------------------------------------------------
    // Parent handling
    // ----------------------------------------------------------------------

    public Xpp3Dom getParent()
    {
        return parent;
    }

    public void setParent( Xpp3Dom parent )
    {
        this.parent = parent;
    }

    // ----------------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------------

    private static void write( Xpp3Dom xpp3Dom, XMLWriter xmlWriter )
    {
        // TODO: move to XMLWriter?
        xmlWriter.startElement( xpp3Dom.getName() );
        String[] attributeNames = xpp3Dom.getAttributeNames();
        for ( int i = 0; i < attributeNames.length; i++ )
        {
            String attributeName = attributeNames[i];
            xmlWriter.addAttribute( attributeName, xpp3Dom.getAttribute( attributeName ) );
        }
        Xpp3Dom[] children = xpp3Dom.getChildren();
        for ( int i = 0; i < children.length; i++ )
        {
            write( children[i], xmlWriter );
        }
        xmlWriter.endElement();
    }

    public void writeToSerializer( String namespace, XmlSerializer serializer )
        throws IOException
    {
        // TODO: Xpp3DomWriter?
        SerializerXMLWriter xmlWriter = new SerializerXMLWriter( namespace, serializer );
        write( this, xmlWriter );
        if ( xmlWriter.getExceptions().size() > 0 )
        {
            throw (IOException) xmlWriter.getExceptions().get( 0 );
        }
    }

    // ----------------------------------------------------------------------
    // Standard object handling
    // ----------------------------------------------------------------------

    public boolean equals( Object obj )
    {
        if ( obj == this )
        {
            return true;
        }

        if ( !( obj instanceof Xpp3Dom ) )
        {
            return false;
        }

        Xpp3Dom dom = (Xpp3Dom) obj;

        if ( name == null ? dom.name != null : !dom.name.equals( name ) )
        {
            return false;
        }
        else if ( value == null ? dom.value != null : !dom.value.equals( value ) )
        {
            return false;
        }
        else if ( attributes == null ? dom.attributes != null : !dom.attributes.equals( attributes ) )
        {
            return false;
        }
        else if ( childList == null ? dom.childList != null : !dom.childList.equals( childList ) )
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public int hashCode()
    {
        int result = 17;
        result = 37 * result + ( name != null ? name.hashCode() : 0 );
        result = 37 * result + ( value != null ? value.hashCode() : 0 );
        result = 37 * result + ( attributes != null ? attributes.hashCode() : 0 );
        result = 37 * result + ( childList != null ? childList.hashCode() : 0 );
        return result;
    }

    public String toString()
    {
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new PrettyPrintXMLWriter( writer );
        write( this, xmlWriter );
        return writer.toString();
    }
}
