/*
 * Created on 2003-09-21
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.codehaus.plexus.util.dag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id$
 */
public class Vertex implements Cloneable, Serializable
{
    //------------------------------------------------------------
    //Fields
    //------------------------------------------------------------
    private String label = null;

    List children = new ArrayList();

    List parents = new ArrayList();
    
    
    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     *
     */
    public Vertex( final String label )
    {
        this.label = label;
    }

    // ------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------
    

    /**
     * @return
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * @param vertex
     */
    public void addEdgeTo( final Vertex vertex )
    {
        children.add( vertex );
    }


    /**
     * @param vertex
     */
    public void removeEdgeTo( final Vertex vertex )
    {
        children.remove( vertex );
    }


    /**
     * @param vertex
     */
    public void addEdgeFrom( final Vertex vertex )
    {
        parents.add( vertex );
    }

    public void removeEdgeFrom( final Vertex vertex )
    {

        parents.remove( vertex );

    }


    public List getChildren()
    {
        return children;
    }


    /**
     * Get the labels used by the most direct children.
     *
     * @return the labels used by the most direct children.
     */
    public List getChildLabels()
    {
        final List retValue = new ArrayList( children.size() );

        for ( final Iterator iter = children.iterator(); iter.hasNext(); )
        {
            final Vertex vertex = ( Vertex ) iter.next();

            retValue.add( vertex.getLabel() );
        }
        return retValue;
    }


    /**
     * Get the list the most direct ancestors (parents).
     *
     * @return list of parents
     */
    public List getParents()
    {
        return parents;
    }


    /**
     * Get the labels used by the most direct ancestors (parents).
     *
     * @return the labels used parents
     */
    public List getParentLabels()
    {
        final List retValue = new ArrayList( parents.size() );

        for ( final Iterator iter = parents.iterator(); iter.hasNext(); )
        {
            final Vertex vertex = ( Vertex ) iter.next();

            retValue.add( vertex.getLabel() );
        }
        return retValue;
    }


    /**
     * Indicates if given vertex has no child
     * 
     * @return <code>true</true> if this vertex has no child, <code>false</code> otherwise
     */
    public boolean isLeaf()
    {
        return children.size() == 0;
    }


    /**
     * Indicates if given vertex has no parent
     * 
     * @return <code>true</true> if this vertex has no parent, <code>false</code> otherwise
     */
    public boolean isRoot()
    {
        return parents.size() == 0;
    }


    /**
     * Indicates if there is at least one edee leading to or from given vertex
     * 
     * @return <code>true</true> if this vertex is connected with other vertex,<code>false</code> otherwise
     */
    public boolean isConnected()
    {
        return isRoot() || isLeaf();
    }


    public Object clone() throws CloneNotSupportedException
    {
        // this is what's failing..
        final Object retValue = super.clone();

        return retValue;
    }

    public String toString()
    {
        return "Vertex{" +
               "label='" + label + "'" +
               "}";
    }


}

