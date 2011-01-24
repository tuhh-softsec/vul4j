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
import org.apache.directory.shared.ldap.model.filter.SubstringNode;
import org.apache.directory.shared.ldap.model.filter.UndefinedNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Unit tests class ClassesItem.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class ClassesItemTest
{
    ClassesItem classesItemA;
    ClassesItem classesItemACopy;
    ClassesItem classesItemB;
    ClassesItem classesItemC;


    /**
     * Initialize classesItem instances
     */
    @Before
    public void initNames() throws Exception
    {
        classesItemA = new ClassesItem( new SubstringNode( "aa" ) );
        classesItemACopy = new ClassesItem( new SubstringNode( "aa" ) );
        classesItemB = new ClassesItem( new SubstringNode( "aa" ) );
        classesItemC = new ClassesItem( new SubstringNode( "cc" ) );
    }


    @Test
    public void testEqualsNotInstanceOf() throws Exception
    {
        assertFalse( classesItemA.equals( UndefinedNode.UNDEFINED_NODE ) );
    }


    @Test
    public void testEqualsNull() throws Exception
    {
        assertFalse( classesItemA.equals( null ) );
    }


    @Test
    public void testEqualsReflexive() throws Exception
    {
        assertEquals( classesItemA, classesItemA );
    }


    @Test
    public void testHashCodeReflexive() throws Exception
    {
        assertEquals( classesItemA.hashCode(), classesItemA.hashCode() );
    }


    @Test
    public void testEqualsSymmetric() throws Exception
    {
        assertEquals( classesItemA, classesItemACopy );
        assertEquals( classesItemACopy, classesItemA );
    }


    @Test
    public void testHashCodeSymmetric() throws Exception
    {
        assertEquals( classesItemA.hashCode(), classesItemACopy.hashCode() );
        assertEquals( classesItemACopy.hashCode(), classesItemA.hashCode() );
    }


    @Test
    public void testEqualsTransitive() throws Exception
    {
        assertEquals( classesItemA, classesItemACopy );
        assertEquals( classesItemACopy, classesItemB );
        assertEquals( classesItemA, classesItemB );
    }


    @Test
    public void testHashCodeTransitive() throws Exception
    {
        assertEquals( classesItemA.hashCode(), classesItemACopy.hashCode() );
        assertEquals( classesItemACopy.hashCode(), classesItemB.hashCode() );
        assertEquals( classesItemA.hashCode(), classesItemB.hashCode() );
    }


    @Test
    public void testNotEqualDiffValue() throws Exception
    {
        assertFalse( classesItemA.equals( classesItemC ) );
        assertFalse( classesItemC.equals( classesItemA ) );
    }
}
