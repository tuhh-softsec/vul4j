/*
 * Created on 2003-09-21
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.codehaus.plexus.util.dag;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id$
 */
public class CycleDetector
{

    private final static Integer NOT_VISTITED = new Integer( 0 );

    private final static Integer VISITING = new Integer( 1 );

    private final static Integer VISITED = new Integer( 2 );


    public static List hasCycle( final DAG graph )
    {
        final List verticies = graph.getVerticies();

        final Map vertexStateMap = new HashMap();

        List retValue = null;

        for ( final Iterator iter = verticies.iterator(); iter.hasNext(); )
        {
            final Vertex vertex = ( Vertex ) iter.next();

            if ( isNotVisited( vertex, vertexStateMap ) )
            {
                retValue = introducesCycle( vertex, vertexStateMap );

                if ( retValue != null )
                {
                    break;
                }
            }
        }

        return retValue;

    }


    /**
     * This method will be called when an egde leading to given vertex was added
     * and we want to check if introduction of this edge has not resulted
     * in apparition of cycle in the graph
     *
     * @param vertex
     * @param vertexStateMap
     * @return
     */
    public static List introducesCycle( final Vertex vertex, final Map vertexStateMap )
    {
        final LinkedList cycleStack = new LinkedList();

        final boolean hasCycle = dfsVisit( vertex, cycleStack, vertexStateMap );

        if ( hasCycle )
        {
            // we have a situation like: [b, a, c, d, b, f, g, h].
            // Label of Vertex which introduced  the cycle is at the first position in the list
            // We have to find second occurence of this label and use its position in the list
            // for getting the sublist of vertex labels of cycle paricipants
            //
            // So in our case we are seraching for [b, a, c, d, b]
            final String label = ( String ) cycleStack.getFirst();

            final int pos = cycleStack.lastIndexOf( label );

            final List cycle = cycleStack.subList( 0, pos + 1 );

            Collections.reverse( cycle );

            return cycle;
        }

        return null;
    }


    public static List introducesCycle( final Vertex vertex )
    {

        final Map vertexStateMap = new HashMap();

        return introducesCycle( vertex, vertexStateMap );

    }

    /**
     * @param vertex
     * @param vertexStateMap
     * @return
     */
    private static boolean isNotVisited( final Vertex vertex, final Map vertexStateMap )
    {
        if ( !vertexStateMap.containsKey( vertex ) )
        {
            return true;
        }
        
        final Integer state = ( Integer ) vertexStateMap.get( vertex );

        return NOT_VISTITED.equals( state );
    }

    /**
     * @param vertex
     * @param vertexStateMap
     * @return
     */
    private static boolean isVisiting( final Vertex vertex, final Map vertexStateMap )
    {
        final Integer state = ( Integer ) vertexStateMap.get( vertex );

        return VISITING.equals( state );
    }

    private static boolean dfsVisit( final Vertex vertex, final LinkedList cycle, final Map vertexStateMap )
    {
        cycle.addFirst( vertex.getLabel() );

        vertexStateMap.put( vertex, VISITING );

        final List verticies = vertex.getChildren();

        for ( final Iterator iter = verticies.iterator(); iter.hasNext(); )
        {
            final Vertex v = ( Vertex ) iter.next();

            if ( isNotVisited( v, vertexStateMap ) )
            {
                final boolean hasCycle = dfsVisit( v, cycle, vertexStateMap );

                if ( hasCycle )
                {
                    return true;
                }
            }
            else if ( isVisiting( v, vertexStateMap ) )
            {
                cycle.addFirst( v.getLabel() );

                return true;
            }
        }
        vertexStateMap.put( vertex, VISITED );

        cycle.removeFirst();

        return false;

    }



}