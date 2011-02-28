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
package org.apache.directory.shared.ldap.aci.protectedItem;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashSet;
import java.util.Set;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.model.filter.UndefinedNode;
import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Unit tests class MaxValueCountItem.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class MaxValueCountItemTest
{
    MaxValueCountItem maxValueCountItemA;
    MaxValueCountItem maxValueCountItemACopy;
    MaxValueCountItem maxValueCountItemB;
    MaxValueCountItem maxValueCountItemC;
    MaxValueCountItem maxValueCountItemD;
    Set<MaxValueCountElem> itemsA;
    Set<MaxValueCountElem> itemsB;
    Set<MaxValueCountElem> itemsC;
    Set<MaxValueCountElem> itemsD;


    /**
     * Initialize maxValueCountItem instances
     */
    @Before
    public void initNames() throws Exception
    {
        itemsA = new HashSet<MaxValueCountElem>();
        itemsA.add( new MaxValueCountElem( new AttributeType("aa"), 1 ) );
        itemsA.add( new MaxValueCountElem( new AttributeType("aa"), 2 ) );
        itemsA.add( new MaxValueCountElem( new AttributeType("aa"), 3 ) );
        // Sets aren't ordered, so adding order must not matter
        itemsB = new HashSet<MaxValueCountElem>();
        itemsB.add( new MaxValueCountElem( new AttributeType("aa"), 2 ) );
        itemsB.add( new MaxValueCountElem( new AttributeType("aa"), 3 ) );
        itemsB.add( new MaxValueCountElem( new AttributeType("aa"), 1 ) );
        itemsC = new HashSet<MaxValueCountElem>();
        itemsC.add( new MaxValueCountElem( new AttributeType("aa"), 1 ) );
        itemsC.add( new MaxValueCountElem( new AttributeType("bb"), 2 ) );
        itemsC.add( new MaxValueCountElem( new AttributeType("aa"), 3 ) );
        itemsD = new HashSet<MaxValueCountElem>();
        itemsD.add( new MaxValueCountElem( new AttributeType("aa"), 1 ) );
        itemsD.add( new MaxValueCountElem( new AttributeType("aa"), 2 ) );
        itemsD.add( new MaxValueCountElem( new AttributeType("aa"), 4 ) );
        maxValueCountItemA = new MaxValueCountItem( itemsA );
        maxValueCountItemACopy = new MaxValueCountItem( itemsA );
        maxValueCountItemB = new MaxValueCountItem( itemsB );
        maxValueCountItemC = new MaxValueCountItem( itemsC );
        maxValueCountItemD = new MaxValueCountItem( itemsD );
    }


    @Test
    public void testEqualsNotInstanceOf() throws Exception
    {
        assertFalse( maxValueCountItemA.equals( UndefinedNode.UNDEFINED_NODE ) );
    }


    @Test
    public void testEqualsNull() throws Exception
    {
        assertFalse( maxValueCountItemA.equals( null ) );
    }


    @Test
    public void testEqualsReflexive() throws Exception
    {
        assertEquals( maxValueCountItemA, maxValueCountItemA );
    }


    @Test
    public void testHashCodeReflexive() throws Exception
    {
        assertEquals( maxValueCountItemA.hashCode(), maxValueCountItemA.hashCode() );
    }


    @Test
    public void testEqualsSymmetric() throws Exception
    {
        assertEquals( maxValueCountItemA, maxValueCountItemACopy );
        assertEquals( maxValueCountItemACopy, maxValueCountItemA );
    }


    @Test
    public void testHashCodeSymmetric() throws Exception
    {
        assertEquals( maxValueCountItemA.hashCode(), maxValueCountItemACopy.hashCode() );
        assertEquals( maxValueCountItemACopy.hashCode(), maxValueCountItemA.hashCode() );
    }


    @Test
    public void testEqualsTransitive() throws Exception
    {
        assertEquals( maxValueCountItemA, maxValueCountItemACopy );
        assertEquals( maxValueCountItemACopy, maxValueCountItemB );
        assertEquals( maxValueCountItemA, maxValueCountItemB );
    }


    @Test
    public void testHashCodeTransitive() throws Exception
    {
        assertEquals( maxValueCountItemA.hashCode(), maxValueCountItemACopy.hashCode() );
        assertEquals( maxValueCountItemACopy.hashCode(), maxValueCountItemB.hashCode() );
        assertEquals( maxValueCountItemA.hashCode(), maxValueCountItemB.hashCode() );
    }


    @Test
    public void testNotEqualDiffValue() throws Exception
    {
        assertFalse( maxValueCountItemA.equals( maxValueCountItemC ) );
        assertFalse( maxValueCountItemC.equals( maxValueCountItemA ) );
        assertFalse( maxValueCountItemA.equals( maxValueCountItemD ) );
        assertFalse( maxValueCountItemD.equals( maxValueCountItemA ) );
    }
}
