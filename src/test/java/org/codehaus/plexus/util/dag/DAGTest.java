package org.codehaus.plexus.util.dag;

import java.util.Set;

import junit.framework.TestCase;

/**
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id$
 */
public class DAGTest extends TestCase
{
    public void testDAG()
    {
        DAG dag = new DAG();
        dag.addVertex( "a" );
        assertEquals( 1, dag.getVerticies().size() );
        assertEquals( "a", dag.getVertex( "a" ).getLabel() );

        dag.addVertex( "a" );
        assertEquals( 1, dag.getVerticies().size() );
        assertEquals( "a", dag.getVertex( "a" ).getLabel() );

        dag.addVertex( "b" );
        assertEquals( 2, dag.getVerticies().size() );
        assertEquals( "a", dag.getVertex( "a" ).getLabel() );
        assertEquals( "b", dag.getVertex( "b" ).getLabel() );
        assertFalse( dag.hasEdge( "a", "b" ) );
        assertFalse( dag.hasEdge( "b", "a" ) );

        dag.addEdge( "c", "d" );
        assertEquals( 4, dag.getVerticies().size() );
        assertEquals( "a", dag.getVertex( "a" ).getLabel() );
        assertEquals( "b", dag.getVertex( "b" ).getLabel() );
        assertEquals( "c", dag.getVertex( "c" ).getLabel() );
        assertEquals( "d", dag.getVertex( "d" ).getLabel() );
        assertFalse( dag.hasEdge( "a", "b" ) );
        assertFalse( dag.hasEdge( "b", "a" ) );
        assertFalse( dag.hasEdge( "a", "c" ) );
        assertFalse( dag.hasEdge( "a", "d" ) );
        assertTrue( dag.hasEdge( "c", "d" ) );

        assertFalse( dag.hasEdge( "d", "c" ) );

        final Set labels = dag.getLabels();
        assertEquals( 4, labels.size() );
        assertTrue( labels.contains( "a" ) );
        assertTrue( labels.contains( "b" ) );
        assertTrue( labels.contains( "c" ) );
        assertTrue( labels.contains( "d" ) );


    }
}
