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
import org.apache.directory.shared.ldap.aci.ProtectedItem.AttributeType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Unit tests class ProtectedItem.AttributeType.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent(threads = 6)
public class ProtectedItem_AttributeTypeTest
{
    AttributeType attributeTypeA;
    AttributeType attributeTypeACopy;
    AttributeType attributeTypeB;
    AttributeType attributeTypeC;


    /**
     * Initialize name instances
     */
    @Before
    public void initNames() throws Exception
    {
        Set<String> colA = new HashSet<String>();
        colA.add( "aa" );
        colA.add( "bb" );
        colA.add( "cc" );
        Set<String> colB = new HashSet<String>();
        colB.add( "aa" );
        colB.add( "bb" );
        colB.add( "cc" );
        Set<String> colC = new HashSet<String>();
        colC.add( "bb" );
        colC.add( "cc" );
        colC.add( "dd" );

        attributeTypeA = new AttributeType( colA );
        attributeTypeACopy = new AttributeType( colA );
        attributeTypeB = new AttributeType( colB );
        attributeTypeC = new AttributeType( colC );
    }


    @Test
    public void testEqualsNull() throws Exception
    {
        assertFalse( attributeTypeA.equals( null ) );
    }


    @Test
    public void testEqualsReflexive() throws Exception
    {
        assertEquals( attributeTypeA, attributeTypeA );
    }


    @Test
    public void testHashCodeReflexive() throws Exception
    {
        assertEquals( attributeTypeA.hashCode(), attributeTypeA.hashCode() );
    }


    @Test
    public void testEqualsSymmetric() throws Exception
    {
        assertEquals( attributeTypeA, attributeTypeACopy );
        assertEquals( attributeTypeACopy, attributeTypeA );
    }


    @Test
    public void testHashCodeSymmetric() throws Exception
    {
        assertEquals( attributeTypeA.hashCode(), attributeTypeACopy.hashCode() );
        assertEquals( attributeTypeACopy.hashCode(), attributeTypeA.hashCode() );
    }


    @Test
    public void testEqualsTransitive() throws Exception
    {
        assertEquals( attributeTypeA, attributeTypeACopy );
        assertEquals( attributeTypeACopy, attributeTypeB );
        assertEquals( attributeTypeA, attributeTypeB );
    }


    @Test
    public void testHashCodeTransitive() throws Exception
    {
        assertEquals( attributeTypeA.hashCode(), attributeTypeACopy.hashCode() );
        assertEquals( attributeTypeACopy.hashCode(), attributeTypeB.hashCode() );
        assertEquals( attributeTypeA.hashCode(), attributeTypeB.hashCode() );
    }


    @Test
    public void testNotEqualDiffValue() throws Exception
    {
        assertFalse( attributeTypeA.equals( attributeTypeC ) );
        assertFalse( attributeTypeC.equals( attributeTypeA ) );
    }
}
