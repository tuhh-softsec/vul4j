package org.codehaus.plexus.util.dag;

import junit.framework.TestCase;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id$
 */
public class CycleDetectorTest extends TestCase
{

    public void testCycyleDetection()
    {

        // a --> b --->c
        DAG dag1 = new DAG();
        dag1.addEdge( "a", "b" );
        dag1.addEdge( "b", "c" );

        assertNull(
            "No cycle should be detected",
            CycleDetector.hasCycle( dag1 ) );

        //
        //  a --> b --->c
        //  ^           |
        //  |           |
        //   -----------|

        DAG dag2 = new DAG();
        dag2.addEdge( "a", "b" );
        dag2.addEdge( "b", "c" );
        dag2.addEdge( "c", "a" );
        List cycle2 = CycleDetector.hasCycle( dag2 );

        assertNotNull( "Cycle should be detected",  cycle2 );

        assertTrue( "Cycle contains 'a'", cycle2.contains( "a" ) );
        assertTrue( "Cycle contains 'b'", cycle2.contains( "b" ) );
        assertTrue( "Cycle contains 'c'", cycle2.contains( "c" ) );

        //        | --> c
        //  a --> b
        //  |     | --> d
        //   --------->
        DAG dag3 = new DAG();
        dag3.addEdge( "a", "b" );
        dag3.addEdge( "b", "c" );
        dag3.addEdge( "b", "d" );
        dag3.addEdge( "a", "d" );
        assertNull(
            "Cycle should not be detected",
            CycleDetector.hasCycle( dag3 ) );

        //  ------------
        //  |           |
        //  V     | --> c
        //  a --> b
        //  |     | --> d
        //   --------->
        DAG dag4 = new DAG();
        dag4.addEdge( "a", "b" );
        dag4.addEdge( "b", "c" );
        dag4.addEdge( "b", "d" );
        dag4.addEdge( "a", "d" );
        dag4.addEdge( "c", "a" );
        List cycle4 = CycleDetector.hasCycle( dag4 );
        assertNotNull( "Cycle should be detected", cycle4 );


        assertEquals( "Cycle contains 'a'", "a", (String) cycle4.get( 0 ) );
        assertEquals( "Cycle contains 'b'", "b", cycle4.get(1 ) );
        assertEquals( "Cycle contains 'c'", "c", cycle4.get( 2 ) );
        assertEquals( "Cycle contains 'a'", "a", (String) cycle4.get( 3 ) );


        //        f --> g --> h
        //        |
        //        |
        //  a --> b ---> c --> d
        //        ^            |
        //        |            V
        //        ------------ e
        DAG dag5 = new DAG();
        dag5.addEdge( "a", "b" );
        dag5.addEdge( "b", "c" );
        dag5.addEdge( "c", "d" );
        dag5.addEdge( "d", "e" );
        dag5.addEdge( "e", "b" );
        dag5.addEdge( "b", "f" );
        dag5.addEdge( "f", "g" );
        dag5.addEdge( "g", "h" );
        List cycle5 = CycleDetector.hasCycle( dag5 );

        assertNotNull( "Cycle should be detected", cycle5 );
        
        assertEquals( "Cycle contains 5 elements", 5, cycle5.size()  );
        assertEquals( "Cycle contains 'b'", "b", (String) cycle5.get( 0 ) );
        assertEquals( "Cycle contains 'c'", "c", cycle5.get(1 ) );
        assertEquals( "Cycle contains 'd'", "d", cycle5.get( 2 ) );
        assertEquals( "Cycle contains 'e'", "e", (String) cycle5.get( 3 ) );
        assertEquals( "Cycle contains 'b'", "b", (String) cycle5.get( 4 ) );

    }


    public  void testCycleToString()
    {
        List cycle = new ArrayList();
        cycle.add( "a" );
        cycle.add( "b" );
        cycle.add( "c" );
        cycle.add( "a" );

        String msg = CycleDetector.cycleToString( cycle );
        assertEquals( "Cycle detected: a --> b --> c --> a", msg );
    }

}
