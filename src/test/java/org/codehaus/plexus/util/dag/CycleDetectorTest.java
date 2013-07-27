package org.codehaus.plexus.util.dag;

/*
 * Copyright The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import junit.framework.TestCase;

import java.util.List;

/**
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id$
 */
public class CycleDetectorTest extends TestCase
{

    public void testCycyleDetection()
    {
        // No cycle
        //
        // a --> b --->c
        //
        try
        {
            final DAG dag1 = new DAG();

            dag1.addEdge( "a", "b" );

            dag1.addEdge( "b", "c" );

        }
        catch ( CycleDetectedException e )
        {

            fail( "Cycle should not be detected" );
        }

        //
        //  a --> b --->c
        //  ^           |
        //  |           |
        //   -----------|

        try
        {
            final DAG dag2 = new DAG();

            dag2.addEdge( "a", "b" );

            dag2.addEdge( "b", "c" );

            dag2.addEdge( "c", "a" );

            fail( "Cycle should be detected" );

        }
        catch ( CycleDetectedException e )
        {

            final List<String> cycle = e.getCycle();

            assertNotNull( "Cycle should be not null", cycle );

            assertTrue( "Cycle contains 'a'", cycle.contains( "a" ) );

            assertTrue( "Cycle contains 'b'", cycle.contains( "b" ) );

            assertTrue( "Cycle contains 'c'", cycle.contains( "c" ) );

        }

        //        | --> c
        //  a --> b
        //  |     | --> d
        //   --------->
        try
        {
            final DAG dag3 = new DAG();

            dag3.addEdge( "a", "b" );

            dag3.addEdge( "b", "c" );

            dag3.addEdge( "b", "d" );

            dag3.addEdge( "a", "d" );

        }
        catch ( CycleDetectedException e )
        {
            fail( "Cycle should not be detected" );
        }

        //  ------------
        //  |           |
        //  V     | --> c
        //  a --> b
        //  |     | --> d
        //   --------->
        try
        {
            final DAG dag4 = new DAG();

            dag4.addEdge( "a", "b" );

            dag4.addEdge( "b", "c" );

            dag4.addEdge( "b", "d" );

            dag4.addEdge( "a", "d" );

            dag4.addEdge( "c", "a" );

            fail( "Cycle should be detected" );

        }
        catch ( CycleDetectedException e )
        {
            final List<String> cycle = e.getCycle();

            assertNotNull( "Cycle should be not null", cycle );

            assertEquals( "Cycle contains 'a'", "a", ( String ) cycle.get( 0 ) );

            assertEquals( "Cycle contains 'b'", "b", cycle.get( 1 ) );

            assertEquals( "Cycle contains 'c'", "c", cycle.get( 2 ) );

            assertEquals( "Cycle contains 'a'", "a", ( String ) cycle.get( 3 ) );
        }


        //        f --> g --> h
        //        |
        //        |
        //  a --> b ---> c --> d
        //        ^            |
        //        |            V
        //        ------------ e

        final DAG dag5 = new DAG();

        try
        {

            dag5.addEdge( "a", "b" );

            dag5.addEdge( "b", "c" );

            dag5.addEdge( "b", "f" );

            dag5.addEdge( "f", "g" );

            dag5.addEdge( "g", "h" );

            dag5.addEdge( "c", "d" );

            dag5.addEdge( "d", "e" );

            dag5.addEdge( "e", "b" );

            fail( "Cycle should be detected" );

        }
        catch ( CycleDetectedException e )
        {
            final List<String> cycle = e.getCycle();

            assertNotNull( "Cycle should be not null", cycle );

            assertEquals( "Cycle contains 5 elements", 5, cycle.size() );

            assertEquals( "Cycle contains 'b'", "b", ( String ) cycle.get( 0 ) );

            assertEquals( "Cycle contains 'c'", "c", cycle.get( 1 ) );

            assertEquals( "Cycle contains 'd'", "d", cycle.get( 2 ) );

            assertEquals( "Cycle contains 'e'", "e", ( String ) cycle.get( 3 ) );

            assertEquals( "Cycle contains 'b'", "b", ( String ) cycle.get( 4 ) );

            assertTrue( "Edge exixst", dag5.hasEdge( "a", "b" ) );

            assertTrue( "Edge exixst", dag5.hasEdge( "b", "c" ) );

            assertTrue( "Edge exixst", dag5.hasEdge( "b", "f" ) );

            assertTrue( "Edge exixst", dag5.hasEdge( "f", "g" ) );

            assertTrue( "Edge exixst", dag5.hasEdge( "g", "h" ) );

            assertTrue( "Edge exixst", dag5.hasEdge( "c", "d" ) );

            assertTrue( "Edge exixst", dag5.hasEdge( "d", "e" ) );

            assertFalse( dag5.hasEdge( "e", "b" ) );
        }

    }


}
