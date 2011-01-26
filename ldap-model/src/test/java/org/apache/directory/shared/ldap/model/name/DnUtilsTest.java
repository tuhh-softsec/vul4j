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
package org.apache.directory.shared.ldap.model.name;


import static org.junit.Assert.assertEquals;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.name.DnUtils;
import org.apache.directory.shared.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;



/**
 * Test the class DnUtils
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class DnUtilsTest
{
    // ~ Methods
    // ------------------------------------------------------------------------------------

    /**
     * Test the DnUtils AreEquals method
     */
    @Test
    public void testAreEqualsFull()
    {
        // Full compare
        assertEquals( 6, Strings.areEquals("azerty".getBytes(), 0, "azerty") );
    }


    /**
     * Test the DnUtils AreEquals method
     */
    @Test
    public void testAreEqualsDiff()
    {
        // First character is !=
        assertEquals( -1, Strings.areEquals("azerty".getBytes(), 0, "Azerty") );
    }


    /**
     * Test the DnUtils AreEquals method
     */
    @Test
    public void testAreEqualsEmpty()
    {
        // Compare to an empty string
        assertEquals( -1, Strings.areEquals("azerty".getBytes(), 0, "") );
    }


    /**
     * Test the DnUtils AreEquals method
     */
    @Test
    public void testAreEqualsFirstCharDiff()
    {
        // First character is !=
        assertEquals( -1, Strings.areEquals("azerty".getBytes(), 0, "Azerty") );
    }


    /**
     * Test the DnUtils AreEquals method
     */
    @Test
    public void testAreEqualsMiddleCharDiff()
    {
        // First character is !=
        assertEquals( -1, Strings.areEquals("azerty".getBytes(), 0, "azeRty") );
    }


    /**
     * Test the DnUtils AreEquals method
     */
    @Test
    public void testAreEqualsLastCharDiff()
    {
        // First character is !=
        assertEquals( -1, Strings.areEquals("azerty".getBytes(), 0, "azertY") );
    }


    /**
     * Test the DnUtils AreEquals method
     */
    @Test
    public void testAreEqualsCharByChar()
    {
        // Index must be incremented after each comparison
        assertEquals( 1, Strings.areEquals("azerty".getBytes(), 0, "a") );
        assertEquals( 2, Strings.areEquals("azerty".getBytes(), 1, "z") );
        assertEquals( 3, Strings.areEquals("azerty".getBytes(), 2, "e") );
        assertEquals( 4, Strings.areEquals("azerty".getBytes(), 3, "r") );
        assertEquals( 5, Strings.areEquals("azerty".getBytes(), 4, "t") );
        assertEquals( 6, Strings.areEquals("azerty".getBytes(), 5, "y") );
    }


    /**
     * Test the DnUtils AreEquals method
     */
    @Test
    public void testAreEqualsTooShort()
    {
        // length too short
        assertEquals( -1, Strings.areEquals("azerty".getBytes(), 0, "azertyiop") );
    }


    /**
     * Test the DnUtils AreEquals method
     */
    @Test
    public void testAreEqualsTooShortMiddle()
    {
        // length too short
        assertEquals( -1, Strings.areEquals("azerty".getBytes(), 0, "ertyiop") );
    }


    /**
     * Test the DnUtils AreEquals method
     */
    @Test
    public void testAreEqualsLastChar()
    {
        // last character
        assertEquals( 6, Strings.areEquals("azerty".getBytes(), 5, "y") );
    }


    /**
     * Test the DnUtils AreEquals method
     */
    @Test
    public void testAreEqualsMiddle()
    {
        // In the middle
        assertEquals( 4, Strings.areEquals("azerty".getBytes(), 2, "er") );
    }


    @Test
    public void testGetCompositeComponents() throws LdapException
    {
        String[] args = DnUtils.getCompositeComponents("givenName=Alex+sn=Karasulu");
        assertEquals( "expecting two parts : ", 2, args.length );
        assertEquals( "givenName=Alex", args[0] );
        assertEquals( "sn=Karasulu", args[1] );

        args = DnUtils.getCompositeComponents("givenName=Alex+sn=Karasulu+age=13");
        assertEquals( "expecting two parts : ", 3, args.length );
        assertEquals( "givenName=Alex", args[0] );
        assertEquals( "sn=Karasulu", args[1] );
        assertEquals( "age=13", args[2] );

        args = DnUtils.getCompositeComponents("cn=One\\+Two");
        assertEquals( "expecting one part : ", 1, args.length );
        assertEquals( "cn=One\\+Two", args[0] );

        args = DnUtils.getCompositeComponents("cn=Alex");
        assertEquals( "expecting one part : ", 1, args.length );
        assertEquals( "cn=Alex", args[0] );
    }


    @Test
    public void testGetRelativeName() throws LdapInvalidDnException
    {
        // test the basis case first with the root
        Dn ancestor = new Dn( "" );
        Dn descendant = new Dn( "ou=system" );
        Dn relativeName = DnUtils.getRelativeName(ancestor, descendant);
        assertEquals( relativeName.toString(), "ou=system" );

        ancestor = new Dn( "ou=system" );
        descendant = new Dn( "ou=users,ou=system" );
        relativeName = DnUtils.getRelativeName(ancestor, descendant);
        assertEquals( relativeName.toString(), "ou=users" );
    }
}
