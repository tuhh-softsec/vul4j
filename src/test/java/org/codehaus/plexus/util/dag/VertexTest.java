package org.codehaus.plexus.util.dag;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
