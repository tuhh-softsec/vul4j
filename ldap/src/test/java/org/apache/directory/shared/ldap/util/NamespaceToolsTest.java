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
package org.apache.directory.shared.ldap.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.name.DN;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test the NameToolsTest class
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class NamespaceToolsTest
{
    @Test
    public void testNullRealm()
    {
        assertEquals( "", NamespaceTools.inferLdapName( null ) );
    }


    @Test
    public void testEmptyRealm()
    {
        assertEquals( "", NamespaceTools.inferLdapName( "" ) );
    }


    @Test
    public void testSingleElemRealm()
    {
        assertEquals( "dc=test", NamespaceTools.inferLdapName( "test" ) );
    }


    @Test
    public void testTwoElemsRealm()
    {
        assertEquals( "dc=test,dc=com", NamespaceTools.inferLdapName( "test.com" ) );
    }


    @Test
    public void testFullRealm()
    {
        assertEquals( "dc=CS,dc=UCL,dc=AC,dc=UK", NamespaceTools.inferLdapName( "CS.UCL.AC.UK" ) );
    }


    @Test
    public void testHasCompositeComponents() throws LdapException
    {
        assertTrue( NamespaceTools.hasCompositeComponents( "givenName=Alex+sn=Karasulu" ) );
        assertTrue( NamespaceTools.hasCompositeComponents( "givenName=Alex+sn=Karasulu+age=13" ) );
        assertFalse( NamespaceTools.hasCompositeComponents( "cn=One\\+Two" ) );
        assertFalse( NamespaceTools.hasCompositeComponents( "cn=Alex" ) );
    }


    @Test
    public void testGetCompositeComponents() throws LdapException
    {
        String[] args = NamespaceTools.getCompositeComponents( "givenName=Alex+sn=Karasulu" );
        assertEquals( "expecting two parts : ", 2, args.length );
        assertEquals( "givenName=Alex", args[0] );
        assertEquals( "sn=Karasulu", args[1] );

        args = NamespaceTools.getCompositeComponents( "givenName=Alex+sn=Karasulu+age=13" );
        assertEquals( "expecting two parts : ", 3, args.length );
        assertEquals( "givenName=Alex", args[0] );
        assertEquals( "sn=Karasulu", args[1] );
        assertEquals( "age=13", args[2] );

        args = NamespaceTools.getCompositeComponents( "cn=One\\+Two" );
        assertEquals( "expecting one part : ", 1, args.length );
        assertEquals( "cn=One\\+Two", args[0] );

        args = NamespaceTools.getCompositeComponents( "cn=Alex" );
        assertEquals( "expecting one part : ", 1, args.length );
        assertEquals( "cn=Alex", args[0] );
    }
    
    
    @Test
    public void testGetRelativeName() throws LdapInvalidDnException
    {
        // test the basis case first with the root
        DN ancestor = new DN( "" );
        DN descendant = new DN( "ou=system" );
        DN relativeName = NamespaceTools.getRelativeName( ancestor, descendant );
        assertEquals( relativeName.toString(), "ou=system" );
        
        ancestor = new DN( "ou=system" );
        descendant = new DN( "ou=users,ou=system" );
        relativeName = NamespaceTools.getRelativeName( ancestor, descendant );
        assertEquals( relativeName.toString(), "ou=users" );
    }
}
