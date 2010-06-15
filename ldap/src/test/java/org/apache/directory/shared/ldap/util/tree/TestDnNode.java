/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.shared.ldap.util.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.name.DN;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test the Dn Nodes 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class TestDnNode
{
    /** A structure to hold all the DNs */
    DnBranchNode<DN> dnLookupTree;
    DN dn1;
    DN dn2;
    DN dn3;
    DN dn4;
    DN dn5;
    DN dn6;

    /**
     * Create the elements we will test
     */
    @Before
    public void setUp()  throws Exception
    {
        dnLookupTree = new DnBranchNode<DN>();
        
        dn1 = new DN( "dc=directory,dc=apache,dc=org" );
        dn2 = new DN( "dc=mina,dc=apache,dc=org" );
        dn3 = new DN( "dc=test,dc=com" );
        dn4 = new DN( "dc=acme,dc=com" );
        dn5 = new DN( "dc=acme,c=us,dc=com" );
        dn6 = new DN( "dc=empty" );

        dnLookupTree.add( dn1, dn1 );
        dnLookupTree.add( dn2, dn2 );
        dnLookupTree.add( dn3, dn3 );
        dnLookupTree.add( dn4, dn4 );
        dnLookupTree.add( dn5, dn5 );
        dnLookupTree.add( dn6, dn6 );
    }
    
    
    /**
     * Clean the tree
     *
     */
    @After
    public void tearDown()
    {
        dnLookupTree = null;
    }
    
    
    /**
     * Test the addition of a single DN
     */
    @Test public void testNewTree() throws LdapException
    {
        /** A structure to hold all the DNs */
        DnBranchNode<DN> dnLookupTree = new DnBranchNode<DN>();
        
        DN suffix = new DN( "dc=example, dc=com" );
        
        dnLookupTree.add( suffix, suffix );
        
        assertNotNull( dnLookupTree );
        assertTrue( dnLookupTree instanceof DnBranchNode );
        assertTrue( ((DnBranchNode<DN>)dnLookupTree).contains( "dc=com" ) );
        
        DnNode<DN> child = ((DnBranchNode<DN>)dnLookupTree).getChild( "dc=com" );
        assertTrue( child instanceof DnBranchNode );
        assertTrue( ((DnBranchNode<DN>)child).contains( "dc=example" ) );

        child = ((DnBranchNode<DN>)child).getChild( "dc=example" );
        assertEquals( suffix, ((DnLeafNode<DN>)child).getElement() );
    }


    /**
     * Test additions in a tree 
     */
    @Test
    public void testComplexTreeCreation() throws LdapException
    {
        
        assertTrue( dnLookupTree.hasParentElement( dn1 ) );
        assertTrue( dnLookupTree.hasParentElement( dn2 ) );
        assertTrue( dnLookupTree.hasParentElement( dn3 ) );
        assertTrue( dnLookupTree.hasParentElement( dn4 ) );
        assertTrue( dnLookupTree.hasParentElement( dn5 ) );
        assertTrue( dnLookupTree.hasParentElement( dn6 ) );
        assertTrue( dnLookupTree.hasParentElement( new DN( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParentElement( new DN(  "dc=directory,dc=apache,dc=root" ) ) );
    }
    
    
    /**
     * Test that we can add an entry twice without any problem
     * TODO testAddEntryTwice.
     *
     */
    @Test
    public void testAddEntryTwice() throws LdapException
    {
        assertEquals( 6, dnLookupTree.size() );

        dnLookupTree.add( dn1, dn1 );
        
        assertEquals( 6, dnLookupTree.size() );
    }

    /**
     * test the deletion of elements in a tree
     */
    @Test
    public void testComplexTreeDeletion() throws LdapException
    {
        dnLookupTree.remove( dn3 );
        assertEquals( 5, dnLookupTree.size() );
        assertTrue( dnLookupTree.hasParentElement( dn1 ) );
        assertTrue( dnLookupTree.hasParentElement( dn2 ) );
        assertTrue( dnLookupTree.hasParentElement( dn4 ) );
        assertTrue( dnLookupTree.hasParentElement( dn5 ) );
        assertTrue( dnLookupTree.hasParentElement( dn6 ) );
        assertTrue( dnLookupTree.hasParentElement( new DN( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParentElement( new DN(  "dc=directory,dc=apache,dc=root" ) ) );

        dnLookupTree.remove( dn6 );
        assertEquals( 4, dnLookupTree.size() );
        assertTrue( dnLookupTree.hasParentElement( dn1 ) );
        assertTrue( dnLookupTree.hasParentElement( dn2 ) );
        assertTrue( dnLookupTree.hasParentElement( dn4 ) );
        assertTrue( dnLookupTree.hasParentElement( dn5 ) );
        assertFalse( dnLookupTree.hasParentElement( new DN( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParentElement( new DN(  "dc=directory,dc=apache,dc=root" ) ) );

        dnLookupTree.remove( dn1 );
        assertEquals( 3, dnLookupTree.size() );
        assertTrue( dnLookupTree.hasParentElement( dn2 ) );
        assertTrue( dnLookupTree.hasParentElement( dn4 ) );
        assertTrue( dnLookupTree.hasParentElement( dn5 ) );
        assertFalse( dnLookupTree.hasParentElement( new DN( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParentElement( new DN(  "dc=directory,dc=apache,dc=root" ) ) );

        // Should not change anything
        dnLookupTree.remove( dn3 );
        assertEquals( 3, dnLookupTree.size() );
        assertTrue( dnLookupTree.hasParentElement( dn2 ) );
        assertTrue( dnLookupTree.hasParentElement( dn4 ) );
        assertTrue( dnLookupTree.hasParentElement( dn5 ) );
        assertFalse( dnLookupTree.hasParentElement( new DN( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParentElement( new DN(  "dc=directory,dc=apache,dc=root" ) ) );

        dnLookupTree.remove( dn5 );
        assertEquals( 2, dnLookupTree.size() );
        assertTrue( dnLookupTree.hasParentElement( dn2 ) );
        assertTrue( dnLookupTree.hasParentElement( dn4 ) );
        assertFalse( dnLookupTree.hasParentElement( new DN( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParentElement( new DN(  "dc=directory,dc=apache,dc=root" ) ) );

        dnLookupTree.remove( dn2 );
        assertEquals( 1, dnLookupTree.size() );
        assertTrue( dnLookupTree.hasParentElement( dn4 ) );
        assertFalse( dnLookupTree.hasParentElement( new DN( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParentElement( new DN(  "dc=directory,dc=apache,dc=root" ) ) );

        dnLookupTree.remove( dn4 );
        assertEquals( 0, dnLookupTree.size() );
        assertFalse( dnLookupTree.hasParentElement( new DN( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParentElement( new DN(  "dc=directory,dc=apache,dc=root" ) ) );
    }
}
