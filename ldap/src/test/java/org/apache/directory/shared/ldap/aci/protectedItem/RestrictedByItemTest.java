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

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.model.filter.UndefinedNode;
import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Unit tests class RestrictedByItem.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class RestrictedByItemTest
{
    RestrictedByItem restrictedByItemA;
    RestrictedByItem restrictedByItemACopy;
    RestrictedByItem restrictedByItemB;
    RestrictedByItem restrictedByItemC;
    RestrictedByItem restrictedByItemD;
    Set<RestrictedByElem> elemsA;
    Set<RestrictedByElem> elemsB;
    Set<RestrictedByElem> elemsC;
    Set<RestrictedByElem> elemsD;


    /**
     * Initialize maxValueCountItem instances
     */
    @Before
    public void initNames() throws Exception
    {
        elemsA = new HashSet<RestrictedByElem>();
        elemsA.add( new RestrictedByElem( new AttributeType("aa"), new AttributeType("aa") ) );
        elemsA.add( new RestrictedByElem( new AttributeType("aa"), new AttributeType("bb") ) );
        elemsA.add( new RestrictedByElem( new AttributeType("aa"), new AttributeType("cc") ) );
        // Sets aren't ordered, so adding order must not matter
        elemsB = new HashSet<RestrictedByElem>();
        elemsB.add( new RestrictedByElem( new AttributeType("aa"), new AttributeType("bb") ) );
        elemsB.add( new RestrictedByElem( new AttributeType("aa"), new AttributeType("cc") ) );
        elemsB.add( new RestrictedByElem( new AttributeType("aa"), new AttributeType("aa") ) );
        elemsC = new HashSet<RestrictedByElem>();
        elemsC.add( new RestrictedByElem( new AttributeType("aa"), new AttributeType("aa") ) );
        elemsC.add( new RestrictedByElem( new AttributeType("bb"), new AttributeType("bb") ) );
        elemsC.add( new RestrictedByElem( new AttributeType("aa"), new AttributeType("cc") ) );
        elemsD = new HashSet<RestrictedByElem>();
        elemsD.add( new RestrictedByElem( new AttributeType("aa"), new AttributeType("aa") ) );
        elemsD.add( new RestrictedByElem( new AttributeType("aa"), new AttributeType("bb") ) );
        elemsD.add( new RestrictedByElem( new AttributeType("aa"), new AttributeType("dd") ) );
        restrictedByItemA = new RestrictedByItem( elemsA );
        restrictedByItemACopy = new RestrictedByItem( elemsA );
        restrictedByItemB = new RestrictedByItem( elemsB );
        restrictedByItemC = new RestrictedByItem( elemsC );
        restrictedByItemD = new RestrictedByItem( elemsD );
    }


    @Test
    public void testEqualsNotInstanceOf() throws Exception
    {
        assertFalse( restrictedByItemA.equals( UndefinedNode.UNDEFINED_NODE ) );
    }


    @Test
    public void testEqualsNull() throws Exception
    {
        assertFalse( restrictedByItemA.equals( null ) );
    }


    @Test
    public void testEqualsReflexive() throws Exception
    {
        assertEquals( restrictedByItemA, restrictedByItemA );
    }


    @Test
    public void testHashCodeReflexive() throws Exception
    {
        assertEquals( restrictedByItemA.hashCode(), restrictedByItemA.hashCode() );
    }


    @Test
    public void testEqualsSymmetric() throws Exception
    {
        assertEquals( restrictedByItemA, restrictedByItemACopy );
        assertEquals( restrictedByItemACopy, restrictedByItemA );
    }


    @Test
    public void testHashCodeSymmetric() throws Exception
    {
        assertEquals( restrictedByItemA.hashCode(), restrictedByItemACopy.hashCode() );
        assertEquals( restrictedByItemACopy.hashCode(), restrictedByItemA.hashCode() );
    }


    @Test
    public void testEqualsTransitive() throws Exception
    {
        assertEquals( restrictedByItemA, restrictedByItemACopy );
        assertEquals( restrictedByItemACopy, restrictedByItemB );
        assertEquals( restrictedByItemA, restrictedByItemB );
    }


    @Test
    public void testHashCodeTransitive() throws Exception
    {
        assertEquals( restrictedByItemA.hashCode(), restrictedByItemACopy.hashCode() );
        assertEquals( restrictedByItemACopy.hashCode(), restrictedByItemB.hashCode() );
        assertEquals( restrictedByItemA.hashCode(), restrictedByItemB.hashCode() );
    }


    @Test
    public void testNotEqualDiffValue() throws Exception
    {
        assertFalse( restrictedByItemA.equals( restrictedByItemC ) );
        assertFalse( restrictedByItemC.equals( restrictedByItemA ) );
        assertFalse( restrictedByItemA.equals( restrictedByItemD ) );
        assertFalse( restrictedByItemD.equals( restrictedByItemA ) );
    }
}
