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

import java.util.List;
import java.util.Map;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.exception.LdapUnwillingToPerformException;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.name.Rdn;
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
    // Test the Add( Dn ) operation
    //---------------------------------------------------------------------------
    /**
     * Test the addition of a null Dn
     */
    @Test( expected=LdapUnwillingToPerformException.class)
    public void testAddNullDNNoElem() throws LdapException
    {
        DnNode<Dn> tree = new DnNode<Dn>();

        tree.add( null );
    }


    /**
     * Test the addition of a Dn with three Rdn
     */
    @Test
    public void testAdd3LevelDNNoElem() throws LdapException
    {
        DnNode<Dn> tree = new DnNode<Dn>( Dn.EMPTY_DN, null );
        Dn dn = new Dn( "dc=c,dc=b,dc=a" );

        tree.add( dn );

        assertNotNull( tree );

        Map<Rdn, DnNode<Dn>> children = tree.getChildren();
        assertNotNull( children );

        assertEquals( 1, children.size() );
        assertNull( tree.getElement() );

        DnNode<Dn> level1 = children.get( new Rdn( "dc=a" ) );
        DnNode<Dn> level2 = level1.getChildren().get( new Rdn( "dc=b" ) );
        DnNode<Dn> level3 = level2.getChildren().get( new Rdn( "dc=c" ) );

        assertNotNull( level3 );
        assertFalse( level3.hasElement() );
    }


    /**
     * Test the addition of two DNs not overlapping
     */
    @Test
    public void testAdd2DistinctDNsNoElem() throws LdapException
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn1 = new Dn( "dc=b,dc=a" );
        Dn dn2 = new Dn( "dc=f,dc=e" );

        tree.add( dn1 );
        tree.add( dn2 );

        assertNotNull( tree );

        Map<Rdn, DnNode<Dn>> children = tree.getChildren();
        assertNotNull( children );

        assertEquals( 2, children.size() );
        assertNull( tree.getElement() );

        DnNode<Dn> level1 = children.get( new Rdn( "dc=a" ) );
        DnNode<Dn> level2 = level1.getChildren().get( new Rdn( "dc=b" ) );

        assertNotNull( level2 );
        assertFalse( level2.hasElement() );

        level1 = children.get( new Rdn( "dc=e" ) );
        level2 = level1.getChildren().get( new Rdn( "dc=f" ) );

        assertNotNull( level2 );
        assertFalse( level2.hasElement() );
    }


    /**
     * Test the addition of two overlapping DNs
     */
    @Test
    public void testAdd2OverlappingDNsNoElem() throws LdapException
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn1 = new Dn( "dc=b,dc=a" );
        Dn dn2 = new Dn( "dc=f,dc=a" );

        tree.add( dn1 );
        tree.add( dn2 );

        assertNotNull( tree );

        Map<Rdn, DnNode<Dn>> children = tree.getChildren();
        assertNotNull( children );

        assertEquals( 1, children.size() );
        assertNull( tree.getElement() );

        DnNode<Dn> level1 = children.get( new Rdn( "dc=a" ) );
        DnNode<Dn> level2 = level1.getChildren().get( new Rdn( "dc=b" ) );

        Map<Rdn, DnNode<Dn>> childrenDn1 = level1.getChildren();
        assertNotNull( childrenDn1 );

        assertEquals( 2, childrenDn1.size() );
        assertNull( level1.getElement() );

        assertNotNull( level2 );
        assertFalse( level2.hasElement() );

        level1 = children.get( new Rdn( "dc=a" ) );
        level2 = level1.getChildren().get( new Rdn( "dc=f" ) );

        assertNotNull( level2 );
        assertFalse( level2.hasElement() );
    }


    /**
     * Test the addition of two equal DNs
     */
    @Test( expected=LdapUnwillingToPerformException.class)
    public void testAdd2EqualDNsNoElem() throws LdapException
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn1 = new Dn( "dc=b,dc=a" );
        Dn dn2 = new Dn( "dc=b,dc=a" );

        tree.add( dn1 );
        tree.add( dn2 );
    }


    //---------------------------------------------------------------------------
    // Test the Add( Dn, N ) operation
    //---------------------------------------------------------------------------
    /**
     * Test the addition of a null Dn
     */
    @Test( expected=LdapUnwillingToPerformException.class)
    public void testAddNullDN() throws LdapException
    {
        DnNode<Dn> tree = new DnNode<Dn>();

        tree.add( (Dn)null, null );
    }


    /**
     * Test the addition of a Dn with three Rdn
     */
    @Test
    public void testAdd3LevelDN() throws LdapException
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn = new Dn( "dc=c,dc=b,dc=a" );

        tree.add( dn, dn );

        assertNotNull( tree );

        Map<Rdn, DnNode<Dn>> children = tree.getChildren();
        assertNotNull( children );

        assertEquals( 1, children.size() );
        assertNull( tree.getElement() );

        DnNode<Dn> level1 = children.get( new Rdn( "dc=a" ) );
        DnNode<Dn> level2 = level1.getChildren().get( new Rdn( "dc=b" ) );
        DnNode<Dn> level3 = level2.getChildren().get( new Rdn( "dc=c" ) );

        assertNotNull( level3 );
        assertEquals( dn, level3.getElement() );
    }


    /**
     * Test the addition of two DNs not overlapping
     */
    @Test
    public void testAdd2DistinctDNs() throws LdapException
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn1 = new Dn( "dc=b,dc=a" );
        Dn dn2 = new Dn( "dc=f,dc=e" );

        tree.add( dn1, dn1 );
        tree.add( dn2, dn2 );

        assertNotNull( tree );

        Map<Rdn, DnNode<Dn>> children = tree.getChildren();
        assertNotNull( children );

        assertEquals( 2, children.size() );
        assertNull( tree.getElement() );

        DnNode<Dn> level1 = children.get( new Rdn( "dc=a" ) );
        DnNode<Dn> level2 = level1.getChildren().get( new Rdn( "dc=b" ) );

        assertNotNull( level2 );
        assertEquals( dn1, level2.getElement() );

        level1 = children.get( new Rdn( "dc=e" ) );
        level2 = level1.getChildren().get( new Rdn( "dc=f" ) );

        assertNotNull( level2 );
        assertEquals( dn2, level2.getElement() );
    }


    /**
     * Test the addition of two overlapping DNs
     */
    @Test
    public void testAdd2OverlappingDNs() throws LdapException
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn1 = new Dn( "dc=b,dc=a" );
        Dn dn2 = new Dn( "dc=f,dc=a" );

        tree.add( dn1, dn1 );
        tree.add( dn2, dn2 );

        assertNotNull( tree );

        Map<Rdn, DnNode<Dn>> children = tree.getChildren();
        assertNotNull( children );

        assertEquals( 1, children.size() );
        assertNull( tree.getElement() );

        DnNode<Dn> level1 = children.get( new Rdn( "dc=a" ) );
        DnNode<Dn> level2 = level1.getChildren().get( new Rdn( "dc=b" ) );

        Map<Rdn, DnNode<Dn>> childrenDn1 = level1.getChildren();
        assertNotNull( childrenDn1 );

        assertEquals( 2, childrenDn1.size() );
        assertNull( level1.getElement() );

        assertNotNull( level2 );
        assertEquals( dn1, level2.getElement() );

        level1 = children.get( new Rdn( "dc=a" ) );
        level2 = level1.getChildren().get( new Rdn( "dc=f" ) );

        assertNotNull( level2 );
        assertEquals( dn2, level2.getElement() );
    }


    /**
     * Test the addition of two equal DNs
     */
    @Test( expected=LdapUnwillingToPerformException.class)
    public void testAdd2EqualDNs() throws LdapException
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn1 = new Dn( "dc=b,dc=a" );
        Dn dn2 = new Dn( "dc=b,dc=a" );

        tree.add( dn1, dn1 );
        tree.add( dn2, dn2 );
    }


    //---------------------------------------------------------------------------
    // Test the hasChildren method
    //---------------------------------------------------------------------------
    @Test
    public void testHasChildren() throws Exception
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn1 = new Dn( "dc=b,dc=a" );
        tree.add( dn1 );

        assertTrue( tree.hasChildren() );
        Map<Rdn, DnNode<Dn>> children = tree.getChildren();
        assertNotNull( children );

        DnNode<Dn> child = children.get( new Rdn( "dc=a" ) );
        assertTrue( child.hasChildren() );

        children = child.getChildren();
        child = children.get( new Rdn( "dc=b" ) );
        assertFalse( child.hasChildren() );
    }


    //---------------------------------------------------------------------------
    // Test the hasChildren(Dn) method
    //---------------------------------------------------------------------------
    @Test
    public void testHasChildrenDN() throws Exception
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn1 = new Dn( "dc=b,dc=a" );
        tree.add( dn1 );

        assertTrue( tree.hasChildren( new Dn( "dc=a" ) ) );
        assertFalse( tree.hasChildren( dn1 ) );
    }


    //---------------------------------------------------------------------------
    // Test the isLeaf() method
    //---------------------------------------------------------------------------
    @Test
    public void testIsLeaf() throws Exception
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn = new Dn( "dc=c,dc=b,dc=a" );
        tree.add( dn );

        assertFalse( tree.isLeaf() );

        DnNode<Dn> child = tree.getChild( new Rdn( "dc=a" ) );
        assertFalse( child.isLeaf() );

        child = child.getChild( new Rdn( "dc=b" ) );
        assertFalse( child.isLeaf() );

        child = child.getChild( new Rdn( "dc=c" ) );
        assertTrue( child.isLeaf() );
    }


    //---------------------------------------------------------------------------
    // Test the isLeaf(Dn) method
    //---------------------------------------------------------------------------
    @Test
    public void testIsLeafDN() throws Exception
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn1 = new Dn( "dc=c,dc=b,dc=a" );
        tree.add( dn1, dn1 );

        Dn dn2 = new Dn( "dc=e,dc=a" );
        tree.add( dn2 );

        assertFalse( tree.isLeaf( Dn.EMPTY_DN ) );
        assertFalse( tree.isLeaf( new Dn( "dc=a" ) ) );
        assertFalse( tree.isLeaf( new Dn( "dc=b,dc=a" ) ) );
        assertTrue( tree.isLeaf( dn1 ) );
        assertTrue( tree.isLeaf( dn2 ) );
    }


    //---------------------------------------------------------------------------
    // Test the getElement() method
    //---------------------------------------------------------------------------
    @Test
    public void testGetElement() throws Exception
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn = new Dn( "dc=c,dc=b,dc=a" );
        tree.add( dn, dn );

        assertNull( tree.getElement() );

        DnNode<Dn> child = tree.getChild( new Rdn( "dc=a" ) );
        assertNull( child.getElement() );

        child = child.getChild( new Rdn( "dc=b" ) );
        assertNull( child.getElement() );

        child = child.getChild( new Rdn( "dc=c" ) );
        assertEquals( dn, child.getElement() );
    }


    //---------------------------------------------------------------------------
    // Test the hasElement() method
    //---------------------------------------------------------------------------
    @Test
    public void testHasElement() throws Exception
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn = new Dn( "dc=c,dc=b,dc=a" );
        tree.add( dn, dn );

        assertFalse( tree.hasElement() );

        DnNode<Dn> child = tree.getChild( new Rdn( "dc=a" ) );
        assertFalse( child.hasElement() );

        child = child.getChild( new Rdn( "dc=b" ) );
        assertFalse( child.hasElement() );

        child = child.getChild( new Rdn( "dc=c" ) );
        assertTrue( child.hasElement() );
    }


    //---------------------------------------------------------------------------
    // Test the getElement(Dn) method
    //---------------------------------------------------------------------------
    @Test
    public void testGetElementDN() throws Exception
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn1 = new Dn( "dc=c,dc=b,dc=a" );
        tree.add( dn1, dn1 );

        Dn dn2 = new Dn( "dc=e,dc=a" );
        tree.add( dn2, dn2 );

        assertNull( tree.getElement( Dn.EMPTY_DN ) );
        assertNull( tree.getElement( new Dn( "dc=a" ) ) );
        assertNull( tree.getElement( new Dn( "dc=b,dc=a" ) ) );
        assertEquals( dn1, tree.getElement( dn1 ) );
        assertEquals( dn2, tree.getElement( dn2 ) );
        assertEquals( dn2, tree.getElement( new Dn( "dc=g,dc=f,dc=e,dc=a" ) ) );
    }


    //---------------------------------------------------------------------------
    // Test the hasElement(Dn) method
    //---------------------------------------------------------------------------
    @Test
    public void testHasElementDN() throws Exception
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn1 = new Dn( "dc=c,dc=b,dc=a" );
        tree.add( dn1, dn1 );

        Dn dn2 = new Dn( "dc=e,dc=a" );
        tree.add( dn2 );

        assertFalse( tree.hasElement( Dn.EMPTY_DN ) );
        assertFalse( tree.hasElement( new Dn( "dc=a" ) ) );
        assertFalse( tree.hasElement( new Dn( "dc=b,dc=a" ) ) );
        assertTrue( tree.hasElement( dn1 ) );
        assertFalse( tree.hasElement( dn2 ) );
    }


    //---------------------------------------------------------------------------
    // Test the size() method
    //---------------------------------------------------------------------------
    @Test
    public void testSize() throws LdapException
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        assertEquals( 1, tree.size() );

        tree.add( new Dn( "dc=b,dc=a" ) );
        assertEquals( 3, tree.size() );

        tree.add( new Dn( "dc=f,dc=a" ) );
        assertEquals( 4, tree.size() );

        tree.add( new Dn( "dc=a,dc=f,dc=a" ) );
        assertEquals( 5, tree.size() );

        tree.add( new Dn( "dc=b,dc=f,dc=a" ) );
        assertEquals( 6, tree.size() );

        tree.add( new Dn( "dc=z,dc=t" ) );
        assertEquals( 8, tree.size() );
    }


    //---------------------------------------------------------------------------
    // Test the getParent() method
    //---------------------------------------------------------------------------
    @Test
    public void testGetParent() throws Exception
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn = new Dn( "dc=c,dc=b,dc=a" );
        tree.add( dn, dn );

        assertNull( tree.getParent() );

        DnNode<Dn> child = tree.getChild( new Rdn( "dc=a" ) );
        assertEquals( tree, child.getParent() );

        DnNode<Dn> child1 = child.getChild( new Rdn( "dc=b" ) );
        assertEquals( child, child1.getParent() );

        child = child1.getChild( new Rdn( "dc=c" ) );
        assertEquals( child1, child.getParent() );
    }


    //---------------------------------------------------------------------------
    // Test the getNode(Dn) method
    //---------------------------------------------------------------------------
    @Test
    public void testGetNodeDN() throws Exception
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn1 = new Dn( "dc=c,dc=b,dc=a" );
        tree.add( dn1, dn1 );

        Dn dn2 = new Dn( "dc=e,dc=a" );
        tree.add( dn2, dn2 );

        assertNull( tree.getNode( Dn.EMPTY_DN ) );

        DnNode<Dn> child = tree.getChild( new Rdn( "dc=a" ) );
        assertEquals( child, tree.getNode( new Dn( "dc=a" ) ) );

        child = child.getChild( new Rdn( "dc=b" ) );
        assertEquals( child, tree.getNode( new Dn( "dc=b,dc=a" ) ) );

        child = child.getChild( new Rdn( "dc=c" ) );
        assertEquals( child, tree.getNode( new Dn( "dc=c,dc=b,dc=a" ) ) );

        assertEquals( child, tree.getNode( new Dn( "dc=f,dc=e,dc=c,dc=b,dc=a" ) ) );
    }


    //---------------------------------------------------------------------------
    // Test the hasParent() method
    //---------------------------------------------------------------------------
    @Test
    public void testHasParent() throws Exception
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn = new Dn( "dc=c,dc=b,dc=a" );
        tree.add( dn, dn );

        assertFalse( tree.hasParent() );

        DnNode<Dn> child = tree.getChild( new Rdn( "dc=a" ) );
        assertTrue( child.hasParent() );

        DnNode<Dn> child1 = child.getChild( new Rdn( "dc=b" ) );
        assertTrue( child1.hasParent() );

        child = child1.getChild( new Rdn( "dc=c" ) );
        assertTrue( child.hasParent() );
    }


    //---------------------------------------------------------------------------
    // Test the hasParent(Dn) method
    //---------------------------------------------------------------------------
    @Test
    public void testHasParentDN() throws Exception
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn1 = new Dn( "dc=c,dc=b,dc=a" );
        tree.add( dn1, dn1 );

        Dn dn2 = new Dn( "dc=e,dc=a" );
        tree.add( dn2, dn2 );

        assertFalse( tree.hasParent( Dn.EMPTY_DN ) );

        DnNode<Dn> child = tree.getChild( new Rdn( "dc=a" ) );
        assertTrue( tree.hasParent( new Dn( "dc=a" ) ) );

        child = child.getChild( new Rdn( "dc=b" ) );
        assertTrue( tree.hasParent( new Dn( "dc=b,dc=a" ) ) );

        child = child.getChild( new Rdn( "dc=c" ) );
        assertTrue( tree.hasParent( new Dn( "dc=c,dc=b,dc=a" ) ) );

        assertTrue( tree.hasParent( new Dn( "dc=f,dc=e,dc=c,dc=b,dc=a" ) ) );
    }


    //---------------------------------------------------------------------------
    // Test the getChild(Rdn) method
    //---------------------------------------------------------------------------
    @Test
    public void testGetChildRdn() throws Exception
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn = new Dn( "dc=c,dc=b,dc=a" );
        tree.add( dn, dn );

        Rdn rdnA = new Rdn( "dc=a" );
        Rdn rdnB = new Rdn( "dc=b" );
        Rdn rdnC = new Rdn( "dc=c" );

        DnNode<Dn> child = tree.getChild( rdnA );
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
    // Test the contains(Rdn) method
    //---------------------------------------------------------------------------
    @Test
    public void testContains() throws Exception
    {
        DnNode<Dn> tree = new DnNode<Dn>();
        Dn dn = new Dn( "dc=c,dc=b,dc=a" );
        tree.add( dn, dn );

        Rdn rdnA = new Rdn( "dc=a" );
        Rdn rdnB = new Rdn( "dc=b" );
        Rdn rdnC = new Rdn( "dc=c" );

        assertTrue( tree.contains( rdnA ) );
        assertFalse( tree.contains( rdnB ) );
        assertFalse( tree.contains( rdnC ) );

        DnNode<Dn> child = tree.getChild( rdnA );

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
        DnNode<Dn> dnLookupTree = new DnNode<Dn>();
        Dn dn1 = new Dn( "dc=directory,dc=apache,dc=org" );
        Dn dn2 = new Dn( "dc=mina,dc=apache,dc=org" );
        Dn dn3 = new Dn( "dc=test,dc=com" );
        Dn dn4 = new Dn( "dc=acme,dc=com" );
        Dn dn5 = new Dn( "dc=acme,c=us,dc=com" );
        Dn dn6 = new Dn( "dc=empty" );

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
        assertTrue( dnLookupTree.hasParent( new Dn( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParent( new Dn(  "dc=directory,dc=apache,dc=root" ) ) );

        dnLookupTree.remove( dn6 );
        assertEquals( 9, dnLookupTree.size() );
        assertTrue( dnLookupTree.hasParent( dn1 ) );
        assertTrue( dnLookupTree.hasParent( dn2 ) );
        assertTrue( dnLookupTree.hasParent( dn4 ) );
        assertTrue( dnLookupTree.hasParent( dn5 ) );
        assertFalse( dnLookupTree.hasParent( new Dn( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParent( new Dn(  "dc=directory,dc=apache,dc=root" ) ) );

        dnLookupTree.remove( dn1 );
        assertEquals( 8, dnLookupTree.size() );
        assertTrue( dnLookupTree.hasParent( dn2 ) );
        assertTrue( dnLookupTree.hasParent( dn4 ) );
        assertTrue( dnLookupTree.hasParent( dn5 ) );
        assertFalse( dnLookupTree.hasParent( new Dn( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParent( new Dn(  "dc=directory,dc=apache,dc=root" ) ) );

        // Should not change anything
        dnLookupTree.remove( dn3 );
        assertEquals( 8, dnLookupTree.size() );
        assertTrue( dnLookupTree.hasParent( dn2 ) );
        assertTrue( dnLookupTree.hasParent( dn4 ) );
        assertTrue( dnLookupTree.hasParent( dn5 ) );
        assertFalse( dnLookupTree.hasParent( new Dn( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParent( new Dn(  "dc=directory,dc=apache,dc=root" ) ) );

        dnLookupTree.remove( dn5 );
        assertEquals( 6, dnLookupTree.size() );
        assertTrue( dnLookupTree.hasParent( dn2 ) );
        assertTrue( dnLookupTree.hasParent( dn4 ) );
        assertFalse( dnLookupTree.hasParent( new Dn( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParent( new Dn(  "dc=directory,dc=apache,dc=root" ) ) );

        dnLookupTree.remove( dn2 );
        assertEquals( 3, dnLookupTree.size() );
        assertTrue( dnLookupTree.hasParent( dn4 ) );
        assertFalse( dnLookupTree.hasParent( new Dn( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParent( new Dn(  "dc=directory,dc=apache,dc=root" ) ) );

        dnLookupTree.remove( dn4 );
        assertEquals( 1, dnLookupTree.size() );
        assertFalse( dnLookupTree.hasParent( new Dn( "dc=nothing,dc=empty" ) ) );
        assertFalse( dnLookupTree.hasParent( new Dn(  "dc=directory,dc=apache,dc=root" ) ) );
    }
    
    
    //---------------------------------------------------------------------------
    // Test the hasParentElement(Dn) method
    //---------------------------------------------------------------------------
    @Test
    public void testHasParentElement() throws Exception
    {
        DnNode<Dn> dnLookupTree = new DnNode<Dn>();
        Dn dn1 = new Dn( "dc=directory,dc=apache,dc=org" );
        Dn dn2 = new Dn( "dc=mina,dc=apache,dc=org" );
        Dn dn3 = new Dn( "dc=test,dc=com" );
        Dn dn4 = new Dn( "dc=acme,dc=com" );
        Dn dn5 = new Dn( "dc=acme,c=us,dc=com" );
        Dn dn6 = new Dn( "dc=empty" );
        
        Dn org = new Dn( "dc=org" );
    
        dnLookupTree.add( dn1, dn1 );
        dnLookupTree.add( dn2, dn2 );
        dnLookupTree.add( dn3, dn3 );
        dnLookupTree.add( dn4, dn4 );
        dnLookupTree.add( dn5 );
        dnLookupTree.add( dn6, dn6 );
        
        // Inject some intermediary nodes
        dnLookupTree.add( org, org );
        
        assertTrue( dnLookupTree.hasParentElement( new Dn( "dc=apache,dc=org" ) ) );
        
        // Check that org has at least one descendant containing an element
        assertTrue( dnLookupTree.hasDescendantElement( org ) );
        
        // check that for one node which has no children with any element, we get false
        assertFalse( dnLookupTree.hasDescendantElement( new Dn( "c=us,dc=com" ) ) );
        
        // Check that we correctly get back all the children
        Dn dn7 = new Dn( "dc=elem,dc=mina,dc=apache,dc=org" );
        dnLookupTree.add( dn7, dn7 );
        
        // With dc=org, we should get back dn1 and dn3
        List<Dn> dns = dnLookupTree.getDescendantElements( org );
        
        assertNotNull( dns );
        assertEquals( 2, dns.size() );
        assertTrue( dns.contains( dn1 ) );
        assertTrue( dns.contains( dn2 ) );
        
        // Same, with a node not having any descendants
        dns = dnLookupTree.getDescendantElements( dn6 );
        assertEquals( 0, dns.size() );
    }
}
