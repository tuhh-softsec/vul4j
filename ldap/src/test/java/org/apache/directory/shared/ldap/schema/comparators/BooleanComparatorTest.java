/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.shared.ldap.schema.comparators;


import static org.junit.Assert.assertEquals;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test the Boolean comparator
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class BooleanComparatorTest
{
    private BooleanComparator comparator;


    @Before
    public void init()
    {
        comparator = new BooleanComparator( null );
    }


    @Test
    public void testNullBooleans()
    {
        assertEquals( 0, comparator.compare( null, null ) );
        assertEquals( -1, comparator.compare( null, "TRUE" ) );
        assertEquals( -1, comparator.compare( null, "FALSE" ) );
        assertEquals( 1, comparator.compare( "TRUE", null ) );
        assertEquals( 1, comparator.compare( "FALSE", null ) );
    }


    @Test
    public void testBooleans()
    {
        assertEquals( 0, comparator.compare( "TRUE", "TRUE" ) );
        assertEquals( 0, comparator.compare( "FALSE", "FALSE" ) );
        assertEquals( -1, comparator.compare( "FALSE", "TRUE" ) );
        assertEquals( 1, comparator.compare( "TRUE", "FALSE" ) );
    }
}
