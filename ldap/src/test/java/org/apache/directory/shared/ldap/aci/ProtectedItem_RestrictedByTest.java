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

import org.apache.directory.shared.ldap.aci.ProtectedItem.RestrictedBy;
import org.apache.directory.shared.ldap.aci.ProtectedItem.RestrictedByItem;
import org.junit.Before;
import org.junit.Test;


/**
 * Unit tests class ProtectedItem.RestrictedBy.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ProtectedItem_RestrictedByTest
{
    RestrictedBy restrictedByA;
    RestrictedBy restrictedByACopy;
    RestrictedBy restrictedByB;
    RestrictedBy restrictedByC;


    /**
     * Initialize name instances
     */
    @Before
    public void initNames() throws Exception
    {
        RestrictedByItem rbiA = new RestrictedByItem( "aa", "aa" );
        RestrictedByItem rbiB = new RestrictedByItem( "bb", "bb" );
        RestrictedByItem rbiC = new RestrictedByItem( "cc", "cc" );
        RestrictedByItem rbiD = new RestrictedByItem( "dd", "dd" );

        Set<RestrictedByItem> colA = new HashSet<RestrictedByItem>();
        colA.add( rbiA );
        colA.add( rbiB );
        colA.add( rbiC );
        Set<RestrictedByItem> colB = new HashSet<RestrictedByItem>();
        colB.add( rbiA );
        colB.add( rbiB );
        colB.add( rbiC );
        Set<RestrictedByItem> colC = new HashSet<RestrictedByItem>();
        colC.add( rbiB );
        colC.add( rbiC );
        colC.add( rbiD );

        restrictedByA = new RestrictedBy( colA );
        restrictedByACopy = new RestrictedBy( colA );
        restrictedByB = new RestrictedBy( colB );
        restrictedByC = new RestrictedBy( colC );
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
