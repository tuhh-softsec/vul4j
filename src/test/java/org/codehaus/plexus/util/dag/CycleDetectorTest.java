package org.codehaus.plexus.util.dag;

import junit.framework.TestCase;

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

        assertFalse(
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
        assertTrue( "Cycle should be detected", CycleDetector.hasCycle( dag2 ) );

        //        | --> c
        //  a --> b
        //  |     | --> d
        //   --------->
        DAG dag3 = new DAG();
        dag3.addEdge( "a", "b" );
        dag3.addEdge( "b", "c" );
        dag3.addEdge( "b", "d" );
        dag3.addEdge( "a", "d" );
        assertFalse(
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
        assertTrue( "Cycle should be detected", CycleDetector.hasCycle( dag4 ) );
    }

}
