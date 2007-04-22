package org.codehaus.plexus.util.xml;

import org.codehaus.plexus.util.xml.pull.XmlSerializer;

import java.io.IOException;

/** @author Jason van Zyl */
public class Xpp3DomUtils
{
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

    public void writeToSerializer( String namespace, XmlSerializer serializer, Xpp3Dom dom )
        throws IOException
    {
        // TODO: WARNING! Later versions of plexus-utils psit out an <?xml ?> header due to thinking this is a new document - not the desired behaviour!
        SerializerXMLWriter xmlWriter = new SerializerXMLWriter( namespace, serializer );
        Xpp3DomWriter.write( xmlWriter, dom );
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

        if ( isNotEmpty( selfMergeMode ) && SELF_COMBINATION_OVERRIDE.equals( selfMergeMode ) )
        {
            mergeSelf = false;
        }

        if ( mergeSelf )
        {
            if ( isEmpty( dominant.getValue() ) )
            {
                dominant.setValue( recessive.getValue() );
            }

            String[] recessiveAttrs = recessive.getAttributeNames();
            for ( int i = 0; i < recessiveAttrs.length; i++ )
            {
                String attr = recessiveAttrs[i];

                if ( isEmpty( dominant.getAttribute( attr ) ) )
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

                if ( isNotEmpty( childMergeMode ) && CHILDREN_COMBINATION_APPEND.equals( childMergeMode ) )
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

    public static boolean isNotEmpty( String str )
    {
        return ( str != null && str.length() > 0 );
    }

    public static boolean isEmpty( String str )
    {
        return ( str == null || str.trim().length() == 0 );
    }
}
