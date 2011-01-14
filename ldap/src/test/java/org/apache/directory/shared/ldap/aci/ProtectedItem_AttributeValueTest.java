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
import org.apache.directory.shared.ldap.aci.protectedItem.AttributeValueItem;
import org.apache.directory.shared.ldap.entry.DefaultEntryAttribute;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Unit tests class ProtectedItem.AttributeValue.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class ProtectedItem_AttributeValueTest
{
    AttributeValueItem attributeValueA;
    AttributeValueItem attributeValueACopy;
    AttributeValueItem attributeValueB;
    AttributeValueItem attributeValueC;


    /**
     * Initialize name instances
     */
    @Before
    public void initNames() throws Exception
    {

        EntryAttribute attrA = new DefaultEntryAttribute( "aa" );
        attrA.add( "aa" );
        EntryAttribute attrB = new DefaultEntryAttribute( "bb" );
        attrB.add( "bb" );
        EntryAttribute attrC = new DefaultEntryAttribute( "cc" );
        attrC.add( "cc" );
        EntryAttribute attrD = new DefaultEntryAttribute( "dd" );
        attrD.add( "dd" );

        Set<EntryAttribute> colA = new HashSet<EntryAttribute>();
        colA.add( attrA );
        colA.add( attrB );
        colA.add( attrC );
        Set<EntryAttribute> colB = new HashSet<EntryAttribute>();
        colB.add( attrA );
        colB.add( attrB );
        colB.add( attrC );
        Set<EntryAttribute> colC = new HashSet<EntryAttribute>();
        colC.add( attrB );
        colC.add( attrC );
        colC.add( attrD );

        attributeValueA = new AttributeValueItem( colA );
        attributeValueACopy = new AttributeValueItem( colA );
        attributeValueB = new AttributeValueItem( colB );
        attributeValueC = new AttributeValueItem( colC );
    }


    @Test
    public void testEqualsNull() throws Exception
    {
        assertFalse( attributeValueA.equals( null ) );
    }


    @Test
    public void testEqualsReflexive() throws Exception
    {
        assertEquals( attributeValueA, attributeValueA );
    }


    @Test
    public void testHashCodeReflexive() throws Exception
    {
        assertEquals( attributeValueA.hashCode(), attributeValueA.hashCode() );
    }


    @Test
    public void testEqualsSymmetric() throws Exception
    {
        assertEquals( attributeValueA, attributeValueACopy );
        assertEquals( attributeValueACopy, attributeValueA );
    }


    @Test
    public void testHashCodeSymmetric() throws Exception
    {
        assertEquals( attributeValueA.hashCode(), attributeValueACopy.hashCode() );
        assertEquals( attributeValueACopy.hashCode(), attributeValueA.hashCode() );
    }


    @Test
    public void testEqualsTransitive() throws Exception
    {
        assertEquals( attributeValueA, attributeValueACopy );
        assertEquals( attributeValueACopy, attributeValueB );
        assertEquals( attributeValueA, attributeValueB );
    }


    @Test
    public void testHashCodeTransitive() throws Exception
    {
        assertEquals( attributeValueA.hashCode(), attributeValueACopy.hashCode() );
        assertEquals( attributeValueACopy.hashCode(), attributeValueB.hashCode() );
        assertEquals( attributeValueA.hashCode(), attributeValueB.hashCode() );
    }


    @Test
    public void testNotEqualDiffValue() throws Exception
    {
        assertFalse( attributeValueA.equals( attributeValueC ) );
        assertFalse( attributeValueC.equals( attributeValueA ) );
    }
}
