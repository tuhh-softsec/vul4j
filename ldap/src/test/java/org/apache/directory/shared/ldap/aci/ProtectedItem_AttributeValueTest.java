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

import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.aci.ProtectedItem.AttributeValue;
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
    AttributeValue attributeValueA;
    AttributeValue attributeValueACopy;
    AttributeValue attributeValueB;
    AttributeValue attributeValueC;


    /**
     * Initialize name instances
     */
    @Before
    public void initNames() throws Exception
    {

        Attribute attrA = new BasicAttribute( "aa" );
        attrA.add( "aa" );
        Attribute attrB = new BasicAttribute( "bb" );
        attrB.add( "bb" );
        Attribute attrC = new BasicAttribute( "cc" );
        attrC.add( "cc" );
        Attribute attrD = new BasicAttribute( "dd" );
        attrD.add( "dd" );

        Set<Attribute> colA = new HashSet<Attribute>();
        colA.add( attrA );
        colA.add( attrB );
        colA.add( attrC );
        Set<Attribute> colB = new HashSet<Attribute>();
        colB.add( attrA );
        colB.add( attrB );
        colB.add( attrC );
        Set<Attribute> colC = new HashSet<Attribute>();
        colC.add( attrB );
        colC.add( attrC );
        colC.add( attrD );

        attributeValueA = new AttributeValue( colA );
        attributeValueACopy = new AttributeValue( colA );
        attributeValueB = new AttributeValue( colB );
        attributeValueC = new AttributeValue( colC );
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
