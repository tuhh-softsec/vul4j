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
import org.apache.directory.shared.ldap.aci.protectedItem.MaxValueCountElem;
import org.apache.directory.shared.ldap.aci.protectedItem.MaxValueCountItem;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Unit tests class ProtectedItem.MaxValueCount.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class ProtectedItem_MaxValueCountTest
{
    MaxValueCountItem maxValueCountA;
    MaxValueCountItem maxValueCountACopy;
    MaxValueCountItem maxValueCountB;
    MaxValueCountItem maxValueCountC;


    /**
     * Initialize name instances
     */
    @Before
    public void initNames() throws Exception
    {

        MaxValueCountElem mvciA = new MaxValueCountElem( new AttributeType( "aa" ), 1 );
        MaxValueCountElem mvciB = new MaxValueCountElem( new AttributeType( "bb" ), 2 );
        MaxValueCountElem mvciC = new MaxValueCountElem( new AttributeType( "cc" ), 3 );
        MaxValueCountElem mvciD = new MaxValueCountElem( new AttributeType( "dd" ), 4 );

        Set<MaxValueCountElem> colA = new HashSet<MaxValueCountElem>();
        colA.add( mvciA );
        colA.add( mvciB );
        colA.add( mvciC );
        Set<MaxValueCountElem> colB = new HashSet<MaxValueCountElem>();
        colB.add( mvciA );
        colB.add( mvciB );
        colB.add( mvciC );
        Set<MaxValueCountElem> colC = new HashSet<MaxValueCountElem>();
        colC.add( mvciB );
        colC.add( mvciC );
        colC.add( mvciD );

        maxValueCountA = new MaxValueCountItem( colA );
        maxValueCountACopy = new MaxValueCountItem( colA );
        maxValueCountB = new MaxValueCountItem( colB );
        maxValueCountC = new MaxValueCountItem( colC );
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
