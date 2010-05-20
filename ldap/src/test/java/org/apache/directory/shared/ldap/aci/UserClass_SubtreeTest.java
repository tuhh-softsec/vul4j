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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.directory.shared.ldap.aci.UserClass.Subtree;
import org.apache.directory.shared.ldap.subtree.BaseSubtreeSpecification;
import org.apache.directory.shared.ldap.subtree.SubtreeSpecification;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Unit tests class UserClass.Subtree.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class UserClass_SubtreeTest
{
    Subtree subtreeA;
    Subtree subtreeACopy;
    Subtree subtreeB;
    Subtree subtreeC;


    /**
     * Initialize name instances
     */
    @Before
    public void initNames() throws Exception
    {
        SubtreeSpecification subtreeSpecA = new BaseSubtreeSpecification();
        SubtreeSpecification subtreeSpecB = new BaseSubtreeSpecification( );
        SubtreeSpecification subtreeSpecC = new BaseSubtreeSpecification( );

        Collection<SubtreeSpecification> colA = new ArrayList<SubtreeSpecification>();
        colA.add( subtreeSpecA );
        colA.add( subtreeSpecB );
        colA.add( subtreeSpecC );
        Collection<SubtreeSpecification> colB = new ArrayList<SubtreeSpecification>();
        colB.add( subtreeSpecA );
        colB.add( subtreeSpecB );
        colB.add( subtreeSpecC );
        Collection<SubtreeSpecification> colC = new ArrayList<SubtreeSpecification>();
        colC.add( subtreeSpecB );
        colC.add( subtreeSpecC );
        colC.add( subtreeSpecA );

        subtreeA = new Subtree( colA );
        subtreeACopy = new Subtree( colA );
        subtreeB = new Subtree( colB );
        subtreeC = new Subtree( colC );
    }


    @Test
    public void testEqualsNull() throws Exception
    {
        assertFalse( subtreeA.equals( null ) );
    }


    @Test
    public void testEqualsReflexive() throws Exception
    {
        assertEquals( subtreeA, subtreeA );
    }


    @Test
    public void testHashCodeReflexive() throws Exception
    {
        assertEquals( subtreeA.hashCode(), subtreeA.hashCode() );
    }


    @Test
    @Ignore
    public void testEqualsSymmetric() throws Exception
    {
        assertEquals( subtreeA, subtreeACopy );
        assertEquals( subtreeACopy, subtreeA );
    }


    @Test
    @Ignore
    public void testHashCodeSymmetric() throws Exception
    {
        assertEquals( subtreeA.hashCode(), subtreeACopy.hashCode() );
        assertEquals( subtreeACopy.hashCode(), subtreeA.hashCode() );
    }


    @Test
    @Ignore
    public void testEqualsTransitive() throws Exception
    {
        assertEquals( subtreeA, subtreeACopy );
        assertEquals( subtreeACopy, subtreeB );
        assertEquals( subtreeA, subtreeB );
    }


    @Test
    @Ignore
    public void testHashCodeTransitive() throws Exception
    {
        assertEquals( subtreeA.hashCode(), subtreeACopy.hashCode() );
        assertEquals( subtreeACopy.hashCode(), subtreeB.hashCode() );
        assertEquals( subtreeA.hashCode(), subtreeB.hashCode() );
    }


    @Test
    public void testNotEqualDiffValue() throws Exception
    {
        assertFalse( subtreeA.equals( subtreeC ) );
        assertFalse( subtreeC.equals( subtreeA ) );
    }
}
