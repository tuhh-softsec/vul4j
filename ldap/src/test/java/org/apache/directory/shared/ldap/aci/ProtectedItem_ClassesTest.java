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

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.aci.ProtectedItem.Classes;
import org.apache.directory.shared.ldap.filter.ExprNode;
import org.apache.directory.shared.ldap.filter.FilterParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Unit tests class ProtectedItem.Classes.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent(threads = 6)
public class ProtectedItem_ClassesTest
{
    Classes classesA;
    Classes classesACopy;
    Classes classesB;
    Classes classesC;


    /**
     * Initialize name instances
     */
    @Before
    public void initNames() throws Exception
    {
        ExprNode filterA = FilterParser.parse( "(&(cn=test)(sn=test))" );
        ExprNode filterB = FilterParser.parse( "(&(cn=test)(sn=test))" );
        ExprNode filterC = FilterParser.parse( "(&(cn=sample)(sn=sample))" );
        classesA = new Classes( filterA );
        classesACopy = new Classes( filterA );
        classesB = new Classes( filterB );
        classesC = new Classes( filterC );
    }


    @Test
    public void testEqualsNull() throws Exception
    {
        assertFalse( classesA.equals( null ) );
    }


    @Test
    public void testEqualsReflexive() throws Exception
    {
        assertEquals( classesA, classesA );
    }


    @Test
    public void testHashCodeReflexive() throws Exception
    {
        assertEquals( classesA.hashCode(), classesA.hashCode() );
    }


    @Test
    public void testEqualsSymmetric() throws Exception
    {
        assertEquals( classesA, classesACopy );
        assertEquals( classesACopy, classesA );
    }


    @Test
    public void testHashCodeSymmetric() throws Exception
    {
        assertEquals( classesA.hashCode(), classesACopy.hashCode() );
        assertEquals( classesACopy.hashCode(), classesA.hashCode() );
    }


    @Test
    public void testEqualsTransitive() throws Exception
    {
        assertEquals( classesA, classesACopy );
        assertEquals( classesACopy, classesB );
        assertEquals( classesA, classesB );
    }


    @Test
    public void testHashCodeTransitive() throws Exception
    {
        assertEquals( classesA.hashCode(), classesACopy.hashCode() );
        assertEquals( classesACopy.hashCode(), classesB.hashCode() );
        assertEquals( classesA.hashCode(), classesB.hashCode() );
    }


    @Test
    public void testNotEqualDiffValue() throws Exception
    {
        assertFalse( classesA.equals( classesC ) );
        assertFalse( classesC.equals( classesA ) );
    }
}
