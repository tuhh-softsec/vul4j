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
import org.apache.directory.shared.ldap.aci.protectedItem.RestrictedByElem;
import org.apache.directory.shared.ldap.aci.protectedItem.RestrictedByItem;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Unit tests class ProtectedItem.RestrictedBy.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class ProtectedItem_RestrictedByTest
{
    RestrictedByItem restrictedByA;
    RestrictedByItem restrictedByACopy;
    RestrictedByItem restrictedByB;
    RestrictedByItem restrictedByC;


    /**
     * Initialize name instances
     */
    @Before
    public void initNames() throws Exception
    {
        RestrictedByElem rbiA = new RestrictedByElem( new AttributeType( "aa" ), new AttributeType( "aa" ) );
        RestrictedByElem rbiB = new RestrictedByElem( new AttributeType( "bb" ), new AttributeType( "bb" ) );
        RestrictedByElem rbiC = new RestrictedByElem( new AttributeType( "cc" ), new AttributeType( "cc" ) );
        RestrictedByElem rbiD = new RestrictedByElem( new AttributeType( "dd" ), new AttributeType( "dd" ) );

        Set<RestrictedByElem> colA = new HashSet<RestrictedByElem>();
        colA.add( rbiA );
        colA.add( rbiB );
        colA.add( rbiC );
        Set<RestrictedByElem> colB = new HashSet<RestrictedByElem>();
        colB.add( rbiA );
        colB.add( rbiB );
        colB.add( rbiC );
        Set<RestrictedByElem> colC = new HashSet<RestrictedByElem>();
        colC.add( rbiB );
        colC.add( rbiC );
        colC.add( rbiD );

        restrictedByA = new RestrictedByItem( colA );
        restrictedByACopy = new RestrictedByItem( colA );
        restrictedByB = new RestrictedByItem( colB );
        restrictedByC = new RestrictedByItem( colC );
    }


    @Test
    public void testEqualsNull() throws Exception
    {
        assertFalse( restrictedByA.equals( null ) );
    }


    @Test
    public void testEqualsReflexive() throws Exception
    {
        assertEquals( restrictedByA, restrictedByA );
    }


    @Test
    public void testHashCodeReflexive() throws Exception
    {
        assertEquals( restrictedByA.hashCode(), restrictedByA.hashCode() );
    }


    @Test
    public void testEqualsSymmetric() throws Exception
    {
        assertEquals( restrictedByA, restrictedByACopy );
        assertEquals( restrictedByACopy, restrictedByA );
    }


    @Test
    public void testHashCodeSymmetric() throws Exception
    {
        assertEquals( restrictedByA.hashCode(), restrictedByACopy.hashCode() );
        assertEquals( restrictedByACopy.hashCode(), restrictedByA.hashCode() );
    }


    @Test
    public void testEqualsTransitive() throws Exception
    {
        assertEquals( restrictedByA, restrictedByACopy );
        assertEquals( restrictedByACopy, restrictedByB );
        assertEquals( restrictedByA, restrictedByB );
    }


    @Test
    public void testHashCodeTransitive() throws Exception
    {
        assertEquals( restrictedByA.hashCode(), restrictedByACopy.hashCode() );
        assertEquals( restrictedByACopy.hashCode(), restrictedByB.hashCode() );
        assertEquals( restrictedByA.hashCode(), restrictedByB.hashCode() );
    }


    @Test
    public void testNotEqualDiffValue() throws Exception
    {
        assertFalse( restrictedByA.equals( restrictedByC ) );
        assertFalse( restrictedByC.equals( restrictedByA ) );
    }
}
