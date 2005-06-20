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

    private static final Xpp3Dom[] EMPTY_DOM_ARRAY = new Xpp3Dom[0];

    public Xpp3Dom( String name )
    {
        this.name = name;
        childList = new ArrayList();
        childMap = new HashMap();
    }

    public Xpp3Dom( Xpp3Dom src )
    {
        this( src.getName() );
        setValue( src.getValue() );

        String[] attributeNames = src.getAttributeNames();
        for ( int i = 0; i < attributeNames.length; i++ )
        {
            String attributeName = attributeNames[i];
            setAttribute( attributeName, src.getAttribute( attributeName ) );
        }

        Xpp3Dom[] children = src.getChildren();
        for ( int i = 0; i < children.length; i++ )
        {
            addChild( new Xpp3Dom( children[i] ) );
        }
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
            return EMPTY_DOM_ARRAY;
        }
        else
        {
            return (Xpp3Dom[]) childList.toArray( EMPTY_DOM_ARRAY );
        }
    }

    public Xpp3Dom[] getChildren( String name )
    {
        if ( null == childList )
        {
            return EMPTY_DOM_ARRAY;
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

            return (Xpp3Dom[]) children.toArray( EMPTY_DOM_ARRAY );
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

    public void removeChild( int i )
    {
        Xpp3Dom child = getChild( i );
        childMap.values().remove( child );
        childList.remove( i );
        // In case of any dangling references
        child.setParent( null );
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

    public void writeToSerializer( String namespace, XmlSerializer serializer )
        throws IOException
    {
        // TODO: WARNING! Later versions of plexus-utils psit out an <?xml ?> header due to thinking this is a new document - not the desired behaviour!
        SerializerXMLWriter xmlWriter = new SerializerXMLWriter( namespace, serializer );
        Xpp3DomWriter.write( xmlWriter, this );
        if ( xmlWriter.getExceptions().size() > 0 )
        {
            throw (IOException) xmlWriter.getExceptions().get( 0 );
        }
    }

    private static void mergeIntoXpp3Dom( Xpp3Dom dominant, Xpp3Dom recessive )
    {
        // TODO: how to mergeXpp3Dom lists rather than override?
        // TODO: share this as some sort of assembler, implement a walk interface?
        if ( recessive == null )
        {
            return;
        }

        Xpp3Dom[] children = recessive.getChildren();
        for ( int i = 0; i < children.length; i++ )
        {
            Xpp3Dom child = children[i];
            Xpp3Dom childDom = dominant.getChild( child.getName() );
            if ( childDom != null )
            {
                mergeIntoXpp3Dom( childDom, child );
            }
            else
            {
                dominant.addChild( new Xpp3Dom( child ) );
            }
        }
    }

    public static Xpp3Dom mergeXpp3Dom( Xpp3Dom dominant, Xpp3Dom recessive )
    {
        if ( dominant != null )
        {
            mergeIntoXpp3Dom( dominant, recessive );
            return dominant;
        }
        return recessive;
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
        // TODO: WARNING! Later versions of plexus-utils psit out an <?xml ?> header due to thinking this is a new document - not the desired behaviour!
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new PrettyPrintXMLWriter( writer );
        Xpp3DomWriter.write( xmlWriter, this );
        return writer.toString();
    }
}
