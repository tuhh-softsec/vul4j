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
import org.apache.directory.shared.ldap.model.entry.DefaultEntryAttribute;
import org.apache.directory.shared.ldap.model.entry.EntryAttribute;
import org.apache.directory.shared.ldap.filter.UndefinedNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Unit tests class AttributeValueItem.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class AttributeValueItemTest
{
    AttributeValueItem attributeValueItemA;
    AttributeValueItem attributeValueItemACopy;
    AttributeValueItem attributeValueItemB;
    AttributeValueItem attributeValueItemC;
    AttributeValueItem attributeValueItemD;
    Set<EntryAttribute> attributeA;
    Set<EntryAttribute> attributeB;
    Set<EntryAttribute> attributeC;
    Set<EntryAttribute> attributeD;


    /**
     * Initialize maxValueCountItem instances
     */
    @Before
    public void initNames() throws Exception
    {
        attributeA = new HashSet<EntryAttribute>();
        attributeA.add( new DefaultEntryAttribute( "aa", "aa" ) );
        attributeA.add( new DefaultEntryAttribute( "aa", "bb" ) );
        attributeA.add( new DefaultEntryAttribute( "aa", "cc" ) );
        // Sets aren't ordered, so adding order must not matter
        attributeB = new HashSet<EntryAttribute>();
        attributeB.add( new DefaultEntryAttribute( "aa", "bb" ) );
        attributeB.add( new DefaultEntryAttribute( "aa", "cc" ) );
        attributeB.add( new DefaultEntryAttribute( "aa", "aa" ) );
        attributeC = new HashSet<EntryAttribute>();
        attributeC.add( new DefaultEntryAttribute( "aa", "aa" ) );
        attributeC.add( new DefaultEntryAttribute( "bb", "bb" ) );
        attributeC.add( new DefaultEntryAttribute( "aa", "cc" ) );
        attributeD = new HashSet<EntryAttribute>();
        attributeD.add( new DefaultEntryAttribute( "aa", "aa" ) );
        attributeD.add( new DefaultEntryAttribute( "aa", "bb" ) );
        attributeD.add( new DefaultEntryAttribute( "aa", "dd" ) );
        attributeValueItemA = new AttributeValueItem( attributeA );
        attributeValueItemACopy = new AttributeValueItem( attributeA );
        attributeValueItemB = new AttributeValueItem( attributeB );
        attributeValueItemC = new AttributeValueItem( attributeC );
        attributeValueItemD = new AttributeValueItem( attributeD );
    }


    @Test
    public void testEqualsNotInstanceOf() throws Exception
    {
        assertFalse( attributeValueItemA.equals( UndefinedNode.UNDEFINED_NODE ) );
    }


    @Test
    public void testEqualsNull() throws Exception
    {
        assertFalse( attributeValueItemA.equals( null ) );
    }


    @Test
    public void testEqualsReflexive() throws Exception
    {
        assertEquals( attributeValueItemA, attributeValueItemA );
    }


    @Test
    public void testHashCodeReflexive() throws Exception
    {
        assertEquals( attributeValueItemA.hashCode(), attributeValueItemA.hashCode() );
    }


    @Test
    public void testEqualsSymmetric() throws Exception
    {
        assertEquals( attributeValueItemA, attributeValueItemACopy );
        assertEquals( attributeValueItemACopy, attributeValueItemA );
    }


    @Test
    public void testHashCodeSymmetric() throws Exception
    {
        assertEquals( attributeValueItemA.hashCode(), attributeValueItemACopy.hashCode() );
        assertEquals( attributeValueItemACopy.hashCode(), attributeValueItemA.hashCode() );
    }


    @Test
    public void testEqualsTransitive() throws Exception
    {
        assertEquals( attributeValueItemA, attributeValueItemACopy );
        assertEquals( attributeValueItemACopy, attributeValueItemB );
        assertEquals( attributeValueItemA, attributeValueItemB );
    }


    @Test
    public void testHashCodeTransitive() throws Exception
    {
        assertEquals( attributeValueItemA.hashCode(), attributeValueItemACopy.hashCode() );
        assertEquals( attributeValueItemACopy.hashCode(), attributeValueItemB.hashCode() );
        assertEquals( attributeValueItemA.hashCode(), attributeValueItemB.hashCode() );
    }


    @Test
    public void testNotEqualDiffValue() throws Exception
    {
        assertFalse( attributeValueItemA.equals( attributeValueItemC ) );
        assertFalse( attributeValueItemC.equals( attributeValueItemA ) );
        assertFalse( attributeValueItemA.equals( attributeValueItemD ) );
        assertFalse( attributeValueItemD.equals( attributeValueItemA ) );
    }
}
