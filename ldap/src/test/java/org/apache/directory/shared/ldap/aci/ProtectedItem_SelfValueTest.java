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

import org.apache.directory.shared.ldap.aci.ProtectedItem.SelfValue;
import org.junit.Before;
import org.junit.Test;


/**
 * Unit tests class ProtectedItem.SelfValue.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ProtectedItem_SelfValueTest
{
    SelfValue selfValueA;
    SelfValue selfValueACopy;
    SelfValue selfValueB;
    SelfValue selfValueC;


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

        selfValueA = new SelfValue( colA );
        selfValueACopy = new SelfValue( colA );
        selfValueB = new SelfValue( colB );
        selfValueC = new SelfValue( colC );
    }


    @Test
    public void testEqualsNull() throws Exception
    {
        assertFalse( selfValueA.equals( null ) );
    }


    @Test
    public void testEqualsReflexive() throws Exception
    {
        assertEquals( selfValueA, selfValueA );
    }


    @Test
    public void testHashCodeReflexive() throws Exception
    {
        assertEquals( selfValueA.hashCode(), selfValueA.hashCode() );
    }


    @Test
    public void testEqualsSymmetric() throws Exception
    {
        assertEquals( selfValueA, selfValueACopy );
        assertEquals( selfValueACopy, selfValueA );
    }


    @Test
    public void testHashCodeSymmetric() throws Exception
    {
        assertEquals( selfValueA.hashCode(), selfValueACopy.hashCode() );
        assertEquals( selfValueACopy.hashCode(), selfValueA.hashCode() );
    }


    @Test
    public void testEqualsTransitive() throws Exception
    {
        assertEquals( selfValueA, selfValueACopy );
        assertEquals( selfValueACopy, selfValueB );
        assertEquals( selfValueA, selfValueB );
    }


    @Test
    public void testHashCodeTransitive() throws Exception
    {
        assertEquals( selfValueA.hashCode(), selfValueACopy.hashCode() );
        assertEquals( selfValueACopy.hashCode(), selfValueB.hashCode() );
        assertEquals( selfValueA.hashCode(), selfValueB.hashCode() );
    }


    @Test
    public void testNotEqualDiffValue() throws Exception
    {
        assertFalse( selfValueA.equals( selfValueC ) );
        assertFalse( selfValueC.equals( selfValueA ) );
    }
}
