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

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.model.filter.UndefinedNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Unit tests class MaxImmSubItem.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class MaxImmSubItemTest
{
    MaxImmSubItem maxImmSubItemA;
    MaxImmSubItem maxImmSubItemACopy;
    MaxImmSubItem maxImmSubItemB;
    MaxImmSubItem maxImmSubItemC;


    /**
     * Initialize maxImmSubItem instances
     */
    @Before
    public void initNames() throws Exception
    {
        maxImmSubItemA = new MaxImmSubItem( 1 );
        maxImmSubItemACopy = new MaxImmSubItem( 1 );
        maxImmSubItemB = new MaxImmSubItem( 1 );
        maxImmSubItemC = new MaxImmSubItem( 2 );
    }


    @Test
    public void testEqualsNotInstanceOf() throws Exception
    {
        assertFalse( maxImmSubItemA.equals( UndefinedNode.UNDEFINED_NODE ) );
    }


    @Test
    public void testEqualsNull() throws Exception
    {
        assertFalse( maxImmSubItemA.equals( null ) );
    }


    @Test
    public void testEqualsReflexive() throws Exception
    {
        assertEquals( maxImmSubItemA, maxImmSubItemA );
    }


    @Test
    public void testHashCodeReflexive() throws Exception
    {
        assertEquals( maxImmSubItemA.hashCode(), maxImmSubItemA.hashCode() );
    }


    @Test
    public void testEqualsSymmetric() throws Exception
    {
        assertEquals( maxImmSubItemA, maxImmSubItemACopy );
        assertEquals( maxImmSubItemACopy, maxImmSubItemA );
    }


    @Test
    public void testHashCodeSymmetric() throws Exception
    {
        assertEquals( maxImmSubItemA.hashCode(), maxImmSubItemACopy.hashCode() );
        assertEquals( maxImmSubItemACopy.hashCode(), maxImmSubItemA.hashCode() );
    }


    @Test
    public void testEqualsTransitive() throws Exception
    {
        assertEquals( maxImmSubItemA, maxImmSubItemACopy );
        assertEquals( maxImmSubItemACopy, maxImmSubItemB );
        assertEquals( maxImmSubItemA, maxImmSubItemB );
    }


    @Test
    public void testHashCodeTransitive() throws Exception
    {
        assertEquals( maxImmSubItemA.hashCode(), maxImmSubItemACopy.hashCode() );
        assertEquals( maxImmSubItemACopy.hashCode(), maxImmSubItemB.hashCode() );
        assertEquals( maxImmSubItemA.hashCode(), maxImmSubItemB.hashCode() );
    }


    @Test
    public void testNotEqualDiffValue() throws Exception
    {
        assertFalse( maxImmSubItemA.equals( maxImmSubItemC ) );
        assertFalse( maxImmSubItemC.equals( maxImmSubItemA ) );
    }
}
