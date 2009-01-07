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
