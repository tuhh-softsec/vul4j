package org.codehaus.plexus.util.xml;

import org.codehaus.plexus.util.StringUtils;
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

    public static final String CHILDREN_COMBINATION_MODE_ATTRIBUTE = "combine.children";
    
    public static final String CHILDREN_COMBINATION_MERGE = "merge";
    
    public static final String CHILDREN_COMBINATION_APPEND = "append";
    
    /** 
     * This default mode for combining children DOMs during merge means that where element names
     * match, the process will try to merge the element data, rather than putting the dominant
     * and recessive elements (which share the same element name) as siblings in the resulting
     * DOM.
     */ 
    public static final String DEFAULT_CHILDREN_COMBINATION_MODE = CHILDREN_COMBINATION_MERGE;

    public static final String SELF_COMBINATION_MODE_ATTRIBUTE = "combine.self";
    
    public static final String SELF_COMBINATION_OVERRIDE = "override";
    
    public static final String SELF_COMBINATION_MERGE = "merge";
    
    /** 
     * This default mode for combining a DOM node during merge means that where element names
     * match, the process will try to merge the element attributes and values, rather than 
     * overriding the recessive element completely with the dominant one. This means that
     * wherever the dominant element doesn't provide the value or a particular attribute, that
     * value or attribute will be set from the recessive DOM node.
     */ 
    public static final String DEFAULT_SELF_COMBINATION_MODE = SELF_COMBINATION_MERGE;

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

    /**
     * Set the attribute value
     * @param name String not null
     * @param value String not null
     */
    public void setAttribute( String name, String value )
    {
        if ( null == value ) {
            throw new NullPointerException( "Attribute value can not be null" );
        }
        if ( null == name ) {
            throw new NullPointerException( "Attribute name can not be null" );
        }
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

    /**
     * Merges one DOM into another, given a specific algorithm and possible override points for that algorithm.
     * The algorithm is as follows:
     * 
     * 1. if the recessive DOM is null, there is nothing to do...return.
     * 
     * 2. Determine whether the dominant node will suppress the recessive one (flag=mergeSelf).
     * 
     *    A. retrieve the 'combine.self' attribute on the dominant node, and try to match against 'override'...
     *       if it matches 'override', then set mergeSelf == false...the dominant node suppresses the recessive
     *       one completely.
     *       
     *    B. otherwise, use the default value for mergeSelf, which is true...this is the same as specifying
     *       'combine.self' == 'merge' as an attribute of the dominant root node.
     *       
     * 3. If mergeSelf == true
     * 
     *    A. if the dominant root node's value is empty, set it to the recessive root node's value
     *    
     *    B. For each attribute in the recessive root node which is not set in the dominant root node, set it.
     *    
     *    C. Determine whether children from the recessive DOM will be merged or appended to the dominant 
     *       DOM as siblings (flag=mergeChildren).
     *       
     *       i.   if childMergeOverride is set (non-null), use that value (true/false)
     *       
     *       ii.  retrieve the 'combine.children' attribute on the dominant node, and try to match against
     *            'append'...if it matches 'append', then set mergeChildren == false...the recessive children
     *            will be appended as siblings of the dominant children.
     *           
     *       iii. otherwise, use the default value for mergeChildren, which is true...this is the same as
     *            specifying 'combine.children' == 'merge' as an attribute on the dominant root node.
     *    
     *    D. Iterate through the recessive children, and:
     *    
     *       i.   if mergeChildren == true and there is a corresponding dominant child (matched by element name),
     *            merge the two.
     *            
     *       ii.  otherwise, add the recessive child as a new child on the dominant root node.
     */
    private static void mergeIntoXpp3Dom( Xpp3Dom dominant, Xpp3Dom recessive, Boolean childMergeOverride )
    {
        // TODO: share this as some sort of assembler, implement a walk interface?
        if ( recessive == null )
        {
            return;
        }
        
        boolean mergeSelf = true;
        
        String selfMergeMode = dominant.getAttribute( SELF_COMBINATION_MODE_ATTRIBUTE );
        
        if ( StringUtils.isNotEmpty( selfMergeMode ) && SELF_COMBINATION_OVERRIDE.equals( selfMergeMode ) )
        {
            mergeSelf = false;
        }
        
        if ( mergeSelf )
        {
            if ( StringUtils.isEmpty( dominant.getValue() ) )
            {
                dominant.setValue( recessive.getValue() );
            }
            
            String[] recessiveAttrs = recessive.getAttributeNames();
            for ( int i = 0; i < recessiveAttrs.length; i++ )
            {
                String attr = recessiveAttrs[i];
                
                if ( StringUtils.isEmpty( dominant.getAttribute( attr ) ) )
                {
                    dominant.setAttribute( attr, recessive.getAttribute( attr ) ); 
                }
            }
            
            boolean mergeChildren = true;
            
            if ( childMergeOverride != null )
            {
                mergeChildren = childMergeOverride.booleanValue();
            }
            else
            {
                String childMergeMode = dominant.getAttribute( CHILDREN_COMBINATION_MODE_ATTRIBUTE );
                
                if ( StringUtils.isNotEmpty( childMergeMode ) && CHILDREN_COMBINATION_APPEND.equals( childMergeMode ) )
                {
                    mergeChildren = false;
                }
            }
            
            Xpp3Dom[] children = recessive.getChildren();
            for ( int i = 0; i < children.length; i++ )
            {
                Xpp3Dom child = children[i];
                Xpp3Dom childDom = dominant.getChild( child.getName() );
                if ( mergeChildren && childDom != null )
                {
                    mergeIntoXpp3Dom( childDom, child, childMergeOverride );
                }
                else
                {
                    dominant.addChild( new Xpp3Dom( child ) );
                }
            }
        }
    }
    
    /**
     * Merge two DOMs, with one having dominance in the case of collision.
     * 
     * @see #CHILDREN_COMBINATION_MODE_ATTRIBUTE
     * @see #SELF_COMBINATION_MODE_ATTRIBUTE
     * 
     * @param dominant The dominant DOM into which the recessive value/attributes/children will be merged
     * @param recessive The recessive DOM, which will be merged into the dominant DOM
     * @param childMergeOverride Overrides attribute flags to force merging or appending of child elements
     *        into the dominant DOM
     */
    public static Xpp3Dom mergeXpp3Dom( Xpp3Dom dominant, Xpp3Dom recessive, Boolean childMergeOverride )
    {
        if ( dominant != null )
        {
            mergeIntoXpp3Dom( dominant, recessive, childMergeOverride );
            return dominant;
        }
        return recessive;
    }

    /**
     * Merge two DOMs, with one having dominance in the case of collision.
     * Merge mechanisms (vs. override for nodes, or vs. append for children) is determined by
     * attributes of the dominant root node.
     * 
     * @see #CHILDREN_COMBINATION_MODE_ATTRIBUTE
     * @see #SELF_COMBINATION_MODE_ATTRIBUTE
     * 
     * @param dominant The dominant DOM into which the recessive value/attributes/children will be merged
     * @param recessive The recessive DOM, which will be merged into the dominant DOM
     */
    public static Xpp3Dom mergeXpp3Dom( Xpp3Dom dominant, Xpp3Dom recessive )
    {
        if ( dominant != null )
        {
            mergeIntoXpp3Dom( dominant, recessive, null );
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

        if ( name == null ? dom.name != null : !name.equals( dom.name ) )
        {
            return false;
        }
        else if ( value == null ? dom.value != null : !value.equals( dom.value ) )
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
        XMLWriter xmlWriter = new PrettyPrintXMLWriter( writer, "UTF-8", null );
        Xpp3DomWriter.write( xmlWriter, this );
        return writer.toString();
    }

    public String toUnescapedString()
    {
        // TODO: WARNING! Later versions of plexus-utils psit out an <?xml ?> header due to thinking this is a new document - not the desired behaviour!
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new PrettyPrintXMLWriter( writer, "UTF-8", null );
        Xpp3DomWriter.write( xmlWriter, this, false );
        return writer.toString();
    }
}
