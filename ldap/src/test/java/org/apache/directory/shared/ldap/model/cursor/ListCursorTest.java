/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.directory.shared.ldap.model.cursor;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;


/**
 * Tests the ListCursor class.  The assertXxxx() methods defined in this class
 * can be collected in an abstract test case class that can be used to test
 * the behavior of any Cursor implementation down the line.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class ListCursorTest
{
    @Test
    public void testEmptyList() throws Exception
    {
        ListCursor<String> cursor = new ListCursor<String>();

        // close test
        cursor.close();
        assertClosed( cursor, "cursor.isClosed() should return true after closing the cursor", true );
    }


    @Test
    public void testSingleElementList() throws Exception
    {
        ListCursor<String> cursor = new ListCursor<String>( Collections.singletonList( "singleton" ) );
        cursor.close();

        // close test
        cursor.close();
        assertClosed( cursor, "cursor.isClosed() should return true after closing the cursor", true );

        // bad bounds: start = end is senseless
        try
        {
            cursor = new ListCursor<String>( Collections.singletonList( "singleton" ), 0 );
            cursor.close();
            Assert.fail("when the start = end bounds this is senseless and should complain");
        }
        catch ( IllegalArgumentException e )
        {
            Assert.assertNotNull(e);
        }

        // bad bounds: start = end is senseless
        try
        {
            cursor = new ListCursor<String>( 1, Collections.singletonList( "singleton" ) );
            cursor.close();
            Assert.fail("when the start = end bounds this is senseless and should complain");
        }
        catch ( IllegalArgumentException e )
        {
            Assert.assertNotNull(e);
        }

        // bad bounds: start > end is senseless
        try
        {
            cursor = new ListCursor<String>( 5, Collections.singletonList( "singleton" ) );
            cursor.close();
            Assert.fail("when the start = end bounds this is senseless and should complain");
        }
        catch ( IllegalArgumentException e )
        {
            Assert.assertNotNull(e);
        }

        // bad bounds: end < start is senseless too in another way :)
        try
        {
            cursor = new ListCursor<String>( Collections.singletonList( "singleton" ), -5 );
            cursor.close();
            Assert.fail("when the start = end bounds this is senseless and should complain");
        }
        catch ( IllegalArgumentException e )
        {
            Assert.assertNotNull(e);
        }

        // bad bounds: start out of range
        try
        {
            cursor = new ListCursor<String>( -5, Collections.singletonList( "singleton" ) );
            cursor.close();
            Assert.fail("when the start = end bounds this is senseless and should complain");
        }
        catch ( IllegalArgumentException e )
        {
            Assert.assertNotNull(e);
        }

        // bad bounds: end out of range
        try
        {
            cursor = new ListCursor<String>( Collections.singletonList( "singleton" ), 5 );
            cursor.close();
            Assert.fail("when the start = end bounds this is senseless and should complain");
        }
        catch ( IllegalArgumentException e )
        {
            Assert.assertNotNull(e);
        }
    }


    @Test
    public void testManyElementList() throws Exception
    {
        List<String> list = new ArrayList<String>();
        list.add( "item 1" );
        list.add( "item 2" );
        list.add( "item 3" );
        list.add( "item 4" );
        list.add( "item 5" );

        // test with bounds of the list itself
        ListCursor<String> cursor = new ListCursor<String>( list );
        cursor.close();

        // test with nonzero lower bound
        cursor = new ListCursor<String>( 1, list );
        cursor.close();

        // test with nonzero lower bound and upper bound
        cursor = new ListCursor<String>( 1, list, 4 );

        // close test
        cursor.close();
        assertClosed( cursor, "cursor.isClosed() should return true after closing the cursor", true );

        // bad bounds: start = end is senseless
        try
        {
            cursor = new ListCursor<String>( list, 0 );
            cursor.close();
            Assert.fail("when the start = end bounds this is senseless and should complain");
        }
        catch ( IllegalArgumentException e )
        {
            Assert.assertNotNull(e);
        }

        // bad bounds: start = end is senseless
        try
        {
            cursor = new ListCursor<String>( 5, list );
            cursor.close();
            Assert.fail("when the start = end bounds this is senseless and should complain");
        }
        catch ( IllegalArgumentException e )
        {
            Assert.assertNotNull(e);
        }

        // bad bounds: start > end is senseless
        try
        {
            cursor = new ListCursor<String>( 10, list );
            cursor.close();
            Assert.fail("when the start = end bounds this is senseless and should complain");
        }
        catch ( IllegalArgumentException e )
        {
            Assert.assertNotNull(e);
        }

        // bad bounds: end < start is senseless too in another way :)
        try
        {
            cursor = new ListCursor<String>( list, -5 );
            cursor.close();
            Assert.fail("when the start = end bounds this is senseless and should complain");
        }
        catch ( IllegalArgumentException e )
        {
            Assert.assertNotNull(e);
        }

        // bad bounds: start out of range
        try
        {
            cursor = new ListCursor<String>( -5, list );
            cursor.close();
            Assert.fail("when the start = end bounds this is senseless and should complain");
        }
        catch ( IllegalArgumentException e )
        {
            Assert.assertNotNull(e);
        }

        // bad bounds: end out of range
        try
        {
            cursor = new ListCursor<String>( list, 10 );
            cursor.close();
            Assert.fail("when the start = end bounds this is senseless and should complain");
        }
        catch ( IllegalArgumentException e )
        {
            Assert.assertNotNull(e);
        }
    }


    protected void assertClosed( Cursor<?> cursor, String msg, boolean expected )
    {
        try
        {
            assertEquals( msg, expected, cursor.isClosed() );
        }
        catch ( Exception e )
        {
            Assert.fail("cursor.isClosed() test should not fail after closing the cursor");
        }

        try
        {
            cursor.close();
        }
        catch ( Exception e )
        {
            Assert.fail("cursor.close() after closing the cursor should not fail with exceptions");
        }


        try
        {
            cursor.afterLast();
            Assert.fail("cursor.afterLast() after closing the cursor should fail with an IOException");
        }
        catch ( Exception e )
        {
            Assert.assertNotNull(e);
        }

        try
        {
            cursor.beforeFirst();
            Assert.fail("cursor.beforeFirst() after closing the cursor should fail with an IOException");
        }
        catch ( Exception e )
        {
            Assert.assertNotNull(e);
        }

        try
        {
            cursor.first();
            Assert.fail("cursor.first() after closing the cursor should fail with an IOException");
        }
        catch ( Exception e )
        {
            Assert.assertNotNull(e);
        }

        try
        {
            cursor.get();
            Assert.fail("cursor.get() after closing the cursor should fail with an IOException");
        }
        catch ( Exception e )
        {
            Assert.assertNotNull(e);
        }

        try
        {
            cursor.last();
            Assert.fail("cursor.last() after closing the cursor should fail with an IOException");
        }
        catch ( Exception e )
        {
            Assert.assertNotNull(e);
        }

        try
        {
            cursor.next();
            Assert.fail("cursor.next() after closing the cursor should fail with an IOException");
        }
        catch ( Exception e )
        {
            Assert.assertNotNull(e);
        }

        try
        {
            cursor.previous();
            Assert.fail("cursor.previous() after closing the cursor should fail with an IOException");
        }
        catch ( Exception e )
        {
            Assert.assertNotNull(e);
        }
    }
}
