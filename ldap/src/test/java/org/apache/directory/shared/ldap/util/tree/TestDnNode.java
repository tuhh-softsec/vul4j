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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.exception.LdapUnwillingToPerformException;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.name.RDN;
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
    //---------------------------------------------------------------------------
    // Test the Add( DN ) operation
    //---------------------------------------------------------------------------
    /**
     * Test the addition of a null DN
     */
    @Test( expected=LdapUnwillingToPerformException.class)
    public void testAddNullDNNoElem() throws LdapException
    {
        DnNode<DN> tree = new DnNode<DN>();

        tree.add( null );
    }


    /**
     * Test the addition of a DN with three RDN
     */
    @Test
    public void testAdd3LevelDNNoElem() throws LdapException
    {
        DnNode<DN> tree = new DnNode<DN>( DN.EMPTY_DN, null );
        DN dn = new DN( "dc=c,dc=b,dc=a" );

        tree.add( dn );

        assertNotNull( tree );

        Map<RDN, DnNode<DN>> children = tree.getChildren();
        assertNotNull( children );

        assertEquals( 1, children.size() );
        assertNull( tree.getElement() );

        DnNode<DN> level1 = children.get( new RDN( "dc=a" ) );
        DnNode<DN> level2 = level1.getChildren().get( new RDN( "dc=b" ) );
        DnNode<DN> level3 = level2.getChildren().get( new RDN( "dc=c" ) );

        assertNotNull( level3 );
        assertFalse( level3.hasElement() );
    }


    /**
     * Test the addition of two DNs not overlapping
     */
    @Test
    public void testAdd2DistinctDNsNoElem() throws LdapException
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn1 = new DN( "dc=b,dc=a" );
        DN dn2 = new DN( "dc=f,dc=e" );

        tree.add( dn1 );
        tree.add( dn2 );

        assertNotNull( tree );

        Map<RDN, DnNode<DN>> children = tree.getChildren();
        assertNotNull( children );

        assertEquals( 2, children.size() );
        assertNull( tree.getElement() );

        DnNode<DN> level1 = children.get( new RDN( "dc=a" ) );
        DnNode<DN> level2 = level1.getChildren().get( new RDN( "dc=b" ) );

        assertNotNull( level2 );
        assertFalse( level2.hasElement() );

        level1 = children.get( new RDN( "dc=e" ) );
        level2 = level1.getChildren().get( new RDN( "dc=f" ) );

        assertNotNull( level2 );
        assertFalse( level2.hasElement() );
    }


    /**
     * Test the addition of two overlapping DNs
     */
    @Test
    public void testAdd2OverlappingDNsNoElem() throws LdapException
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn1 = new DN( "dc=b,dc=a" );
        DN dn2 = new DN( "dc=f,dc=a" );

        tree.add( dn1 );
        tree.add( dn2 );

        assertNotNull( tree );

        Map<RDN, DnNode<DN>> children = tree.getChildren();
        assertNotNull( children );

        assertEquals( 1, children.size() );
        assertNull( tree.getElement() );

        DnNode<DN> level1 = children.get( new RDN( "dc=a" ) );
        DnNode<DN> level2 = level1.getChildren().get( new RDN( "dc=b" ) );

        Map<RDN, DnNode<DN>> childrenDn1 = level1.getChildren();
        assertNotNull( childrenDn1 );

        assertEquals( 2, childrenDn1.size() );
        assertNull( level1.getElement() );

        assertNotNull( level2 );
        assertFalse( level2.hasElement() );

        level1 = children.get( new RDN( "dc=a" ) );
        level2 = level1.getChildren().get( new RDN( "dc=f" ) );

        assertNotNull( level2 );
        assertFalse( level2.hasElement() );
    }


    /**
     * Test the addition of two equal DNs
     */
    @Test( expected=LdapUnwillingToPerformException.class)
    public void testAdd2EqualDNsNoElem() throws LdapException
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn1 = new DN( "dc=b,dc=a" );
        DN dn2 = new DN( "dc=b,dc=a" );

        tree.add( dn1 );
        tree.add( dn2 );
    }


    //---------------------------------------------------------------------------
    // Test the Add( DN, N ) operation
    //---------------------------------------------------------------------------
    /**
     * Test the addition of a null DN
     */
    @Test( expected=LdapUnwillingToPerformException.class)
    public void testAddNullDN() throws LdapException
    {
        DnNode<DN> tree = new DnNode<DN>();

        tree.add( (DN)null, null );
    }


    /**
     * Test the addition of a DN with three RDN
     */
    @Test
    public void testAdd3LevelDN() throws LdapException
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn = new DN( "dc=c,dc=b,dc=a" );

        tree.add( dn, dn );

        assertNotNull( tree );

        Map<RDN, DnNode<DN>> children = tree.getChildren();
        assertNotNull( children );

        assertEquals( 1, children.size() );
        assertNull( tree.getElement() );

        DnNode<DN> level1 = children.get( new RDN( "dc=a" ) );
        DnNode<DN> level2 = level1.getChildren().get( new RDN( "dc=b" ) );
        DnNode<DN> level3 = level2.getChildren().get( new RDN( "dc=c" ) );

        assertNotNull( level3 );
        assertEquals( dn, level3.getElement() );
    }


    /**
     * Test the addition of two DNs not overlapping
     */
    @Test
    public void testAdd2DistinctDNs() throws LdapException
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn1 = new DN( "dc=b,dc=a" );
        DN dn2 = new DN( "dc=f,dc=e" );

        tree.add( dn1, dn1 );
        tree.add( dn2, dn2 );

        assertNotNull( tree );

        Map<RDN, DnNode<DN>> children = tree.getChildren();
        assertNotNull( children );

        assertEquals( 2, children.size() );
        assertNull( tree.getElement() );

        DnNode<DN> level1 = children.get( new RDN( "dc=a" ) );
        DnNode<DN> level2 = level1.getChildren().get( new RDN( "dc=b" ) );

        assertNotNull( level2 );
        assertEquals( dn1, level2.getElement() );

        level1 = children.get( new RDN( "dc=e" ) );
        level2 = level1.getChildren().get( new RDN( "dc=f" ) );

        assertNotNull( level2 );
        assertEquals( dn2, level2.getElement() );
    }


    /**
     * Test the addition of two overlapping DNs
     */
    @Test
    public void testAdd2OverlappingDNs() throws LdapException
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn1 = new DN( "dc=b,dc=a" );
        DN dn2 = new DN( "dc=f,dc=a" );

        tree.add( dn1, dn1 );
        tree.add( dn2, dn2 );

        assertNotNull( tree );

        Map<RDN, DnNode<DN>> children = tree.getChildren();
        assertNotNull( children );

        assertEquals( 1, children.size() );
        assertNull( tree.getElement() );

        DnNode<DN> level1 = children.get( new RDN( "dc=a" ) );
        DnNode<DN> level2 = level1.getChildren().get( new RDN( "dc=b" ) );

        Map<RDN, DnNode<DN>> childrenDn1 = level1.getChildren();
        assertNotNull( childrenDn1 );

        assertEquals( 2, childrenDn1.size() );
        assertNull( level1.getElement() );

        assertNotNull( level2 );
        assertEquals( dn1, level2.getElement() );

        level1 = children.get( new RDN( "dc=a" ) );
        level2 = level1.getChildren().get( new RDN( "dc=f" ) );

        assertNotNull( level2 );
        assertEquals( dn2, level2.getElement() );
    }


    /**
     * Test the addition of two equal DNs
     */
    @Test( expected=LdapUnwillingToPerformException.class)
    public void testAdd2EqualDNs() throws LdapException
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn1 = new DN( "dc=b,dc=a" );
        DN dn2 = new DN( "dc=b,dc=a" );

        tree.add( dn1, dn1 );
        tree.add( dn2, dn2 );
    }


    //---------------------------------------------------------------------------
    // Test the hasChildren method
    //---------------------------------------------------------------------------
    @Test
    public void testHasChildren() throws Exception
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn1 = new DN( "dc=b,dc=a" );
        tree.add( dn1 );

        assertTrue( tree.hasChildren() );
        Map<RDN, DnNode<DN>> children = tree.getChildren();
        assertNotNull( children );

        DnNode<DN> child = children.get( new RDN( "dc=a" ) );
        assertTrue( child.hasChildren() );

        children = child.getChildren();
        child = children.get( new RDN( "dc=b" ) );
        assertFalse( child.hasChildren() );
    }


    //---------------------------------------------------------------------------
    // Test the hasChildren(DN) method
    //---------------------------------------------------------------------------
    @Test
    public void testHasChildrenDN() throws Exception
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn1 = new DN( "dc=b,dc=a" );
        tree.add( dn1 );

        assertTrue( tree.hasChildren( new DN( "dc=a" ) ) );
        assertFalse( tree.hasChildren( dn1 ) );
    }


    //---------------------------------------------------------------------------
    // Test the isLeaf() method
    //---------------------------------------------------------------------------
    @Test
    public void testIsLeaf() throws Exception
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn = new DN( "dc=c,dc=b,dc=a" );
        tree.add( dn );

        assertFalse( tree.isLeaf() );

        DnNode<DN> child = tree.getChild( new RDN( "dc=a" ) );
        assertFalse( child.isLeaf() );

        child = child.getChild( new RDN( "dc=b" ) );
        assertFalse( child.isLeaf() );

        child = child.getChild( new RDN( "dc=c" ) );
        assertTrue( child.isLeaf() );
    }


    //---------------------------------------------------------------------------
    // Test the isLeaf(DN) method
    //---------------------------------------------------------------------------
    @Test
    public void testIsLeafDN() throws Exception
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn1 = new DN( "dc=c,dc=b,dc=a" );
        tree.add( dn1, dn1 );

        DN dn2 = new DN( "dc=e,dc=a" );
        tree.add( dn2 );

        assertFalse( tree.isLeaf( DN.EMPTY_DN ) );
        assertFalse( tree.isLeaf( new DN( "dc=a" ) ) );
        assertFalse( tree.isLeaf( new DN( "dc=b,dc=a" ) ) );
        assertTrue( tree.isLeaf( dn1 ) );
        assertTrue( tree.isLeaf( dn2 ) );
    }


    //---------------------------------------------------------------------------
    // Test the getElement() method
    //---------------------------------------------------------------------------
    @Test
    public void testGetElement() throws Exception
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn = new DN( "dc=c,dc=b,dc=a" );
        tree.add( dn, dn );

        assertNull( tree.getElement() );

        DnNode<DN> child = tree.getChild( new RDN( "dc=a" ) );
        assertNull( child.getElement() );

        child = child.getChild( new RDN( "dc=b" ) );
        assertNull( child.getElement() );

        child = child.getChild( new RDN( "dc=c" ) );
        assertEquals( dn, child.getElement() );
    }


    //---------------------------------------------------------------------------
    // Test the hasElement() method
    //---------------------------------------------------------------------------
    @Test
    public void testHasElement() throws Exception
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn = new DN( "dc=c,dc=b,dc=a" );
        tree.add( dn, dn );

        assertFalse( tree.hasElement() );

        DnNode<DN> child = tree.getChild( new RDN( "dc=a" ) );
        assertFalse( child.hasElement() );

        child = child.getChild( new RDN( "dc=b" ) );
        assertFalse( child.hasElement() );

        child = child.getChild( new RDN( "dc=c" ) );
        assertTrue( child.hasElement() );
    }


    //---------------------------------------------------------------------------
    // Test the getElement(DN) method
    //---------------------------------------------------------------------------
    @Test
    public void testGetElementDN() throws Exception
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn1 = new DN( "dc=c,dc=b,dc=a" );
        tree.add( dn1, dn1 );

        DN dn2 = new DN( "dc=e,dc=a" );
        tree.add( dn2, dn2 );

        assertNull( tree.getElement( DN.EMPTY_DN ) );
        assertNull( tree.getElement( new DN( "dc=a" ) ) );
        assertNull( tree.getElement( new DN( "dc=b,dc=a" ) ) );
        assertEquals( dn1, tree.getElement( dn1 ) );
        assertEquals( dn2, tree.getElement( dn2 ) );
        assertEquals( dn2, tree.getElement( new DN( "dc=g,dc=f,dc=e,dc=a" ) ) );
    }


    //---------------------------------------------------------------------------
    // Test the hasElement(DN) method
    //---------------------------------------------------------------------------
    @Test
    public void testHasElementDN() throws Exception
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn1 = new DN( "dc=c,dc=b,dc=a" );
        tree.add( dn1, dn1 );

        DN dn2 = new DN( "dc=e,dc=a" );
        tree.add( dn2 );

        assertFalse( tree.hasElement( DN.EMPTY_DN ) );
        assertFalse( tree.hasElement( new DN( "dc=a" ) ) );
        assertFalse( tree.hasElement( new DN( "dc=b,dc=a" ) ) );
        assertTrue( tree.hasElement( dn1 ) );
        assertFalse( tree.hasElement( dn2 ) );
    }


    //---------------------------------------------------------------------------
    // Test the size() method
    //---------------------------------------------------------------------------
    @Test
    public void testSize() throws LdapException
    {
        DnNode<DN> tree = new DnNode<DN>();
        assertEquals( 1, tree.size() );

        tree.add( new DN( "dc=b,dc=a" ) );
        assertEquals( 3, tree.size() );

        tree.add( new DN( "dc=f,dc=a" ) );
        assertEquals( 4, tree.size() );

        tree.add( new DN( "dc=a,dc=f,dc=a" ) );
        assertEquals( 5, tree.size() );

        tree.add( new DN( "dc=b,dc=f,dc=a" ) );
        assertEquals( 6, tree.size() );

        tree.add( new DN( "dc=z,dc=t" ) );
        assertEquals( 8, tree.size() );
    }


    //---------------------------------------------------------------------------
    // Test the getParent() method
    //---------------------------------------------------------------------------
    @Test
    public void testGetParent() throws Exception
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn = new DN( "dc=c,dc=b,dc=a" );
        tree.add( dn, dn );

        assertNull( tree.getParent() );

        DnNode<DN> child = tree.getChild( new RDN( "dc=a" ) );
        assertEquals( tree, child.getParent() );

        DnNode<DN> child1 = child.getChild( new RDN( "dc=b" ) );
        assertEquals( child, child1.getParent() );

        child = child1.getChild( new RDN( "dc=c" ) );
        assertEquals( child1, child.getParent() );
    }


    //---------------------------------------------------------------------------
    // Test the getNode(DN) method
    //---------------------------------------------------------------------------
    @Test
    public void testGetNodeDN() throws Exception
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn1 = new DN( "dc=c,dc=b,dc=a" );
        tree.add( dn1, dn1 );

        DN dn2 = new DN( "dc=e,dc=a" );
        tree.add( dn2, dn2 );

        assertNull( tree.getNode( DN.EMPTY_DN ) );

        DnNode<DN> child = tree.getChild( new RDN( "dc=a" ) );
        assertEquals( child, tree.getNode( new DN( "dc=a" ) ) );

        child = child.getChild( new RDN( "dc=b" ) );
        assertEquals( child, tree.getNode( new DN( "dc=b,dc=a" ) ) );

        child = child.getChild( new RDN( "dc=c" ) );
        assertEquals( child, tree.getNode( new DN( "dc=c,dc=b,dc=a" ) ) );

        assertEquals( child, tree.getNode( new DN( "dc=f,dc=e,dc=c,dc=b,dc=a" ) ) );
    }


    //---------------------------------------------------------------------------
    // Test the hasParent() method
    //---------------------------------------------------------------------------
    @Test
    public void testHasParent() throws Exception
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn = new DN( "dc=c,dc=b,dc=a" );
        tree.add( dn, dn );

        assertFalse( tree.hasParent() );

        DnNode<DN> child = tree.getChild( new RDN( "dc=a" ) );
        assertTrue( child.hasParent() );

        DnNode<DN> child1 = child.getChild( new RDN( "dc=b" ) );
        assertTrue( child1.hasParent() );

        child = child1.getChild( new RDN( "dc=c" ) );
        assertTrue( child.hasParent() );
    }


    //---------------------------------------------------------------------------
    // Test the hasParent(DN) method
    //---------------------------------------------------------------------------
    @Test
    public void testHasParentDN() throws Exception
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn1 = new DN( "dc=c,dc=b,dc=a" );
        tree.add( dn1, dn1 );

        DN dn2 = new DN( "dc=e,dc=a" );
        tree.add( dn2, dn2 );

        assertFalse( tree.hasParent( DN.EMPTY_DN ) );

        DnNode<DN> child = tree.getChild( new RDN( "dc=a" ) );
        assertTrue( tree.hasParent( new DN( "dc=a" ) ) );

        child = child.getChild( new RDN( "dc=b" ) );
        assertTrue( tree.hasParent( new DN( "dc=b,dc=a" ) ) );

        child = child.getChild( new RDN( "dc=c" ) );
        assertTrue( tree.hasParent( new DN( "dc=c,dc=b,dc=a" ) ) );

        assertTrue( tree.hasParent( new DN( "dc=f,dc=e,dc=c,dc=b,dc=a" ) ) );
    }


    //---------------------------------------------------------------------------
    // Test the getChild(RDN) method
    //---------------------------------------------------------------------------
    @Test
    public void testGetChildRdn() throws Exception
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn = new DN( "dc=c,dc=b,dc=a" );
        tree.add( dn, dn );

        RDN rdnA = new RDN( "dc=a" );
        RDN rdnB = new RDN( "dc=b" );
        RDN rdnC = new RDN( "dc=c" );

        DnNode<DN> child = tree.getChild( rdnA );
        assertNotNull( child );
        assertEquals( rdnA, child.getRdn() );

        child = child.getChild( rdnB );
        assertNotNull( child );
        assertEquals( rdnB, child.getRdn() );

        child = child.getChild( rdnC );
        assertNotNull( child );
        assertEquals( rdnC, child.getRdn() );
    }


    //---------------------------------------------------------------------------
    // Test the contains(RDN) method
    //---------------------------------------------------------------------------
    @Test
    public void testContains() throws Exception
    {
        DnNode<DN> tree = new DnNode<DN>();
        DN dn = new DN( "dc=c,dc=b,dc=a" );
        tree.add( dn, dn );

        RDN rdnA = new RDN( "dc=a" );
        RDN rdnB = new RDN( "dc=b" );
        RDN rdnC = new RDN( "dc=c" );

        assertTrue( tree.contains( rdnA ) );
        assertFalse( tree.contains( rdnB ) );
        assertFalse( tree.contains( rdnC ) );

        DnNode<DN> child = tree.getChild( rdnA );

        assertFalse( child.contains( rdnA ) );
        assertTrue( child.contains( rdnB ) );
        assertFalse( child.contains( rdnC ) );

        child = child.getChild( rdnB );

        assertFalse( child.contains( rdnA ) );
        assertFalse( child.contains( rdnB ) );
        assertTrue( child.contains( rdnC ) );
    }

    /**
     * test the deletion of elements in a tree
     */
    @Test
    public void testComplexTreeDeletion() throws LdapException
    {
        DnNode<DN> dnLookupTree = new DnNode<DN>();
        DN dn1 = new DN( "dc=directory,dc=apache,dc=org" );
        DN dn2 = new DN( "dc=mina,dc=apache,dc=org" );
        DN dn3 = new DN( "dc=test,dc=com" );
        DN dn4 = new DN( "dc=acme,dc=com" );
        DN dn5 = new DN( "dc=acme,c=us,dc=com" );
        DN dn6 = new DN( "dc=empty" );

        dnLookupTree.add( dn1, dn1 );
        dnLookupTree.add( dn2, dn2 );
        dnLookupTree.add( dn3, dn3 );
        dnLookupTree.add( dn4, dn4 );
        dnLookupTree.add( dn5, dn5 );
        dnLookupTree.add( dn6, dn6 );

        assertEquals( 11, dnLookupTree.size() );

        dnLookupTree.remove( dn3 );
        assertEquals( 10, dnLookupTree.size() );
        assertTrue( dnLookupTree.hasParent( dn1 ) );
        assertTrue( dnLookupTree.hasParent( dn2 ) );
        assertTrue( dnLookupTree.hasParent( dn4 ) );
        assertTrue( dnLookupTree.hasParent( dn5 ) );
        assertTrue( dnLookupTree.hasParent( dn6 ) );
        assertTrue( dnLookupTree.hasParent( new DN( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParent( new DN(  "dc=directory,dc=apache,dc=root" ) ) );

        dnLookupTree.remove( dn6 );
        assertEquals( 9, dnLookupTree.size() );
        assertTrue( dnLookupTree.hasParent( dn1 ) );
        assertTrue( dnLookupTree.hasParent( dn2 ) );
        assertTrue( dnLookupTree.hasParent( dn4 ) );
        assertTrue( dnLookupTree.hasParent( dn5 ) );
        assertFalse( dnLookupTree.hasParent( new DN( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParent( new DN(  "dc=directory,dc=apache,dc=root" ) ) );

        dnLookupTree.remove( dn1 );
        assertEquals( 8, dnLookupTree.size() );
        assertTrue( dnLookupTree.hasParent( dn2 ) );
        assertTrue( dnLookupTree.hasParent( dn4 ) );
        assertTrue( dnLookupTree.hasParent( dn5 ) );
        assertFalse( dnLookupTree.hasParent( new DN( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParent( new DN(  "dc=directory,dc=apache,dc=root" ) ) );

        // Should not change anything
        dnLookupTree.remove( dn3 );
        assertEquals( 8, dnLookupTree.size() );
        assertTrue( dnLookupTree.hasParent( dn2 ) );
        assertTrue( dnLookupTree.hasParent( dn4 ) );
        assertTrue( dnLookupTree.hasParent( dn5 ) );
        assertFalse( dnLookupTree.hasParent( new DN( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParent( new DN(  "dc=directory,dc=apache,dc=root" ) ) );

        dnLookupTree.remove( dn5 );
        assertEquals( 6, dnLookupTree.size() );
        assertTrue( dnLookupTree.hasParent( dn2 ) );
        assertTrue( dnLookupTree.hasParent( dn4 ) );
        assertFalse( dnLookupTree.hasParent( new DN( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParent( new DN(  "dc=directory,dc=apache,dc=root" ) ) );

        dnLookupTree.remove( dn2 );
        assertEquals( 3, dnLookupTree.size() );
        assertTrue( dnLookupTree.hasParent( dn4 ) );
        assertFalse( dnLookupTree.hasParent( new DN( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParent( new DN(  "dc=directory,dc=apache,dc=root" ) ) );

        dnLookupTree.remove( dn4 );
        assertEquals( 1, dnLookupTree.size() );
        assertFalse( dnLookupTree.hasParent( new DN( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParent( new DN(  "dc=directory,dc=apache,dc=root" ) ) );
    }
}
