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
package org.apache.directory.shared.ldap.aci;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashSet;
import java.util.Set;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.aci.ProtectedItem.MaxValueCount;
import org.apache.directory.shared.ldap.aci.ProtectedItem.MaxValueCountItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Unit tests class ProtectedItem.MaxValueCount.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent(threads = 6)
public class ProtectedItem_MaxValueCountTest
{
    MaxValueCount maxValueCountA;
    MaxValueCount maxValueCountACopy;
    MaxValueCount maxValueCountB;
    MaxValueCount maxValueCountC;


    /**
     * Initialize name instances
     */
    @Before
    public void initNames() throws Exception
    {

        MaxValueCountItem mvciA = new MaxValueCountItem( "aa", 1 );
        MaxValueCountItem mvciB = new MaxValueCountItem( "bb", 2 );
        MaxValueCountItem mvciC = new MaxValueCountItem( "cc", 3 );
        MaxValueCountItem mvciD = new MaxValueCountItem( "dd", 4 );

        Set<MaxValueCountItem> colA = new HashSet<MaxValueCountItem>();
        colA.add( mvciA );
        colA.add( mvciB );
        colA.add( mvciC );
        Set<MaxValueCountItem> colB = new HashSet<MaxValueCountItem>();
        colB.add( mvciA );
        colB.add( mvciB );
        colB.add( mvciC );
        Set<MaxValueCountItem> colC = new HashSet<MaxValueCountItem>();
        colC.add( mvciB );
        colC.add( mvciC );
        colC.add( mvciD );

        maxValueCountA = new MaxValueCount( colA );
        maxValueCountACopy = new MaxValueCount( colA );
        maxValueCountB = new MaxValueCount( colB );
        maxValueCountC = new MaxValueCount( colC );
    }


    @Test
    public void testEqualsNull() throws Exception
    {
        assertFalse( maxValueCountA.equals( null ) );
    }


    @Test
    public void testEqualsReflexive() throws Exception
    {
        assertEquals( maxValueCountA, maxValueCountA );
    }


    @Test
    public void testHashCodeReflexive() throws Exception
    {
        assertEquals( maxValueCountA.hashCode(), maxValueCountA.hashCode() );
    }


    @Test
    public void testEqualsSymmetric() throws Exception
    {
        assertEquals( maxValueCountA, maxValueCountACopy );
        assertEquals( maxValueCountACopy, maxValueCountA );
    }


    @Test
    public void testHashCodeSymmetric() throws Exception
    {
        assertEquals( maxValueCountA.hashCode(), maxValueCountACopy.hashCode() );
        assertEquals( maxValueCountACopy.hashCode(), maxValueCountA.hashCode() );
    }


    @Test
    public void testEqualsTransitive() throws Exception
    {
        assertEquals( maxValueCountA, maxValueCountACopy );
        assertEquals( maxValueCountACopy, maxValueCountB );
        assertEquals( maxValueCountA, maxValueCountB );
    }


    @Test
    public void testHashCodeTransitive() throws Exception
    {
        assertEquals( maxValueCountA.hashCode(), maxValueCountACopy.hashCode() );
        assertEquals( maxValueCountACopy.hashCode(), maxValueCountB.hashCode() );
        assertEquals( maxValueCountA.hashCode(), maxValueCountB.hashCode() );
    }


    @Test
    public void testNotEqualDiffValue() throws Exception
    {
        assertFalse( maxValueCountA.equals( maxValueCountC ) );
        assertFalse( maxValueCountC.equals( maxValueCountA ) );
    }
}
