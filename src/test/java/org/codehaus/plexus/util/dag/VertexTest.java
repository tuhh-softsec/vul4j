package org.codehaus.plexus.util.dag;

import junit.framework.TestCase;

/**
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id$
 */
public class VertexTest extends TestCase
{
    public void testVertex()
    {

        final Vertex vertex1 = new Vertex( "a" );
        assertEquals( "a", vertex1.getLabel() );
        assertEquals( 0, vertex1.getChildren().size() );
        assertEquals( 0, vertex1.getChildLabels().size() );

        final Vertex vertex2 = new Vertex( "b" );
        assertEquals( "b", vertex2.getLabel() );
        vertex1.addEdgeTo( vertex2 );
        assertEquals( 1, vertex1.getChildren().size() );
        assertEquals( 1, vertex1.getChildLabels().size() );

        assertEquals( vertex2, vertex1.getChildren().get( 0 ) );
        assertEquals( "b", vertex1.getChildLabels().get( 0 ) );

    }
}
