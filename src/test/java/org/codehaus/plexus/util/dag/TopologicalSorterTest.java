package org.codehaus.plexus.util.dag;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id$
 */
public class TopologicalSorterTest extends TestCase
{
    public void testDfs()
    {
        // a --> b --->c
        //
        // result a,b,c
        DAG dag1 = new DAG();

        dag1.addEdge( "a", "b" );
        dag1.addEdge( "b", "c" );

        List expected1 = new ArrayList();

        expected1.add( "c" );
        expected1.add( "b" );
        expected1.add( "a" );

        List actual1 = TopologicalSorter.sort( dag1 );

        assertEquals( "Order is different then expected", expected1, actual1 );

        //
        //  a <-- b <---c
        //
        // result c, b, a
        DAG dag2 = new DAG();

        dag2.addVertex( "a" );
        dag2.addVertex( "b" );
        dag2.addVertex( "c" );
        dag2.addEdge( "b", "a" );
        dag2.addEdge( "c", "b" );

        List expected2 = new ArrayList();

        expected2.add( "a" );
        expected2.add( "b" );
        expected2.add( "c" );

        List actual2 = TopologicalSorter.sort( dag2 );

        assertEquals( "Order is different then expected", expected2, actual2 );

        //
        //  a --> b --> c --> e
        //        |     |     |
        //        |     V     V
        //          --> d <-- f  --> g
        // result d, g, f, c, b, a
        DAG dag3 = new DAG();
        // force order of nodes in the graph
        dag3.addVertex( "a" );
        dag3.addVertex( "b" );
        dag3.addVertex( "c" );
        dag3.addVertex( "d" );
        dag3.addVertex( "e" );
        dag3.addVertex( "f" );

        dag3.addEdge( "a", "b" );
        dag3.addEdge( "b", "c" );
        dag3.addEdge( "b", "d" );
        dag3.addEdge( "c", "d" );
        dag3.addEdge( "c", "e" );
        dag3.addEdge( "f", "d" );
        dag3.addEdge( "e", "f" );
        dag3.addEdge( "f", "g" );
        List expected3 = new ArrayList();

        expected3.add( "d" );
        expected3.add( "g" );
        expected3.add( "f" );
        expected3.add( "e" );
        expected3.add( "c" );
        expected3.add( "b" );
        expected3.add( "a" );


        List actual3 = TopologicalSorter.sort( dag3 );

        assertEquals( "Order is different then expected", expected3, actual3 );

        //
        //  a --> b --> c --> e
        //        |     |     |
        //        |     V     V
        //          --> d <-- f
        // result d, f, e, c, b, a
        DAG dag4 = new DAG();
        // force order of nodes in the graph
        dag4.addVertex( "f" );
        dag4.addVertex( "e" );
        dag4.addVertex( "d" );
        dag4.addVertex( "c" );
        dag4.addVertex( "a" );
        dag4.addVertex( "b" );
        dag4.addEdge( "a", "b" );
        dag4.addEdge( "b", "c" );
        dag4.addEdge( "b", "d" );
        dag4.addEdge( "c", "d" );
        dag4.addEdge( "c", "e" );
        dag4.addEdge( "f", "d" );
        dag4.addEdge( "e", "f" );
        List expected4 = new ArrayList();

        expected4.add( "d" );
        expected4.add( "f" );
        expected4.add( "e" );
        expected4.add( "c" );
        expected4.add( "b" );
        expected4.add( "a" );


        List actual4 = TopologicalSorter.sort( dag4 );

        assertEquals( "Order is different then expected", expected4, actual4 );
    }
}
