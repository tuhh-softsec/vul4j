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


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.apache.directory.shared.ldap.aci.UserClass.Name;
import org.apache.directory.shared.ldap.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.name.DN;
import org.junit.Before;
import org.junit.Test;


/**
 * Unit tests class UserClass.Name.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class NameTest
{
    private Name nameInstanceA;
    private Name nameInstanceB;
    private Name nameInstanceC;
    private Name nameInstanceD;


    /**
     * Initialize name instances
     * 
     * nameInstanceA is equal to nameInstanceB
     * NameInstanceC is different
     */
    @Before
    public void initNames() throws LdapInvalidDnException
    {
        Set<DN> dnSetA = new HashSet<DN>();
        dnSetA.add( new DN( "a=aa" ) );
        dnSetA.add( new DN( "b=bb" ) );

        Set<DN> dnSetB = new HashSet<DN>();
        dnSetB.add( new DN( "b=bb" ) );
        dnSetB.add( new DN( "a=aa" ) );

        Set<DN> dnSetC = new HashSet<DN>();
        dnSetC.add( new DN( "a=aa" ) );
        dnSetC.add( new DN( "b=bb" ) );

        Set<DN> dnSetD = new HashSet<DN>();
        dnSetD.add( new DN( "b=bb" ) );
        dnSetD.add( new DN( "c=cc" ) );

        nameInstanceA = new Name( dnSetA );
        nameInstanceB = new Name( dnSetB );
        nameInstanceC = new Name( dnSetC );
        nameInstanceD = new Name( dnSetD );
    }


    /**
     * Tests for equality.
     */
    @Test
    public void testEqual() throws Exception
    {
        assertFalse( nameInstanceA.equals( null ) );
        assertTrue( nameInstanceA.equals( nameInstanceA ) );
        assertTrue( nameInstanceA.equals( nameInstanceB ) );
        assertTrue( nameInstanceB.equals( nameInstanceA ) );
        assertTrue( nameInstanceB.equals( nameInstanceC ) );
        assertTrue( nameInstanceA.equals( nameInstanceC ) );
        assertFalse( nameInstanceA.equals( nameInstanceD ) );
        assertFalse( nameInstanceB.equals( nameInstanceD ) );
    }


    /**
     * Tests for hashCode.
     * 
     * Only test hashCode for equal object as they must have an equal hashCode.
     * For non equal object the hashCode can be equal.
     */
    @Test
    public void testHashCode() throws Exception
    {
        assertTrue( nameInstanceA.hashCode() == nameInstanceB.hashCode() );
        assertTrue( nameInstanceB.hashCode() == nameInstanceC.hashCode() );
        assertTrue( nameInstanceA.hashCode() == nameInstanceC.hashCode() );
    }
}