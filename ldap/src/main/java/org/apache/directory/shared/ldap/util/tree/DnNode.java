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
package org.apache.directory.shared.ldap.util.tree;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;

import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.exception.LdapUnwillingToPerformException;
import org.apache.directory.shared.ldap.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.name.RDN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An class for nodes in a tree designed to quickly lookup hierarchical DN.
 * Branch nodes in this tree refers to child nodes. Leaf nodes in the tree
 * don't have any children. <br/>
 * A node may cotain a reference to an object whose suffix is the path through the
 * nodes of the tree from the root.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DnNode<N>
{
    /** The logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger( DnNode.class );

    /** The stored element */
    private N element;

    /** The node's key */
    private RDN rdn;

    /** The node's DN */
    private DN dn;

    /** The node's depth in the tree */
    private int depth;

    /** The parent, if any */
    private DnNode<N> parent;

    /** Stores the list of all the descendant */
    private Map<RDN, DnNode<N>> children;

    //-------------------------------------------------------------------------
    // Helper methods
    //-------------------------------------------------------------------------
    /**
     * Check that the DN is not null
     */
    private void checkDn( DN dn ) throws LdapException
    {
        if ( ( dn == null ) || dn.isEmpty() )
        {
            String message = "Cannot process an empty DN";
            LOG.error( message );
            throw new LdapUnwillingToPerformException( ResultCodeEnum.UNWILLING_TO_PERFORM, message );
        }
    }

    /**
     * Create a new DnNode, recursively creating all the intermediate nodes.
     */
    private DnNode<N> createNode( DN dn, N element, int nbRdns ) throws LdapException
    {
        checkDn( dn );

        DnNode<N> rootNode = null;

        // No parent : add from the current position
        for ( RDN rdn : dn.getRdns() )
        {
            if ( nbRdns == 0 )
            {
                break;
            }

            if ( rootNode == null )
            {
                // Create the new top node
                DnNode<N> node = new DnNode<N>( element );
                node.rdn = rdn;
                node.dn = dn;
                node.depth = dn.size() + depth;

                rootNode = node;
            }
            else
            {
                DnNode<N> node = new DnNode<N>();
                node.rdn = rdn;
                node.dn = rootNode.dn.getParent();
                node.depth = node.dn.size() + depth;
                rootNode.parent = node;
                node.children.put( rootNode.rdn, rootNode );
                rootNode = node;
            }

            nbRdns--;
        }

        return rootNode;
    }


    /**
     * Creates a new instance of DnNode.
     */
    public DnNode()
    {
        children = new ConcurrentHashMap<RDN, DnNode<N>>();
        dn = DN.EMPTY_DN;
        rdn = RDN.EMPTY_RDN;
    }


    /**
     * Creates a new instance of DnNode.
     *
     * @param element the element to store
     */
    public DnNode( N element )
    {
        this.element = element;
        children = new ConcurrentHashMap<RDN, DnNode<N>>();
    }


    /**
     * Creates a new instance of DnNode.
     *
     * @param element the element to store
     */
    public DnNode( DN dn, N element )
    {
        if ( ( dn == null ) || ( dn.isEmpty() ) )
        {
            children = new ConcurrentHashMap<RDN, DnNode<N>>();
            this.dn = DN.EMPTY_DN;

            return;
        }

        try
        {
            DnNode<N> rootNode = createNode( dn, element, dn.size() );

            // Now copy back the created node into this
            this.children = rootNode.children;
            this.depth = rootNode.depth;
            this.dn = rootNode.dn;
            this.element = rootNode.element;
            this.rdn = rootNode.rdn;
            this.parent = null;
        }
        catch ( LdapException le )
        {
            // Special cas e: the DN is empty, this is not allowed
            throw new IllegalArgumentException( le.getMessage() );
        }
    }


    /**
     * Tells if the implementation is a leaf node. If it's a branch node
     * then false is returned.
     *
     * @return <code>true</code> if the class is a leaf node, false otherwise.
     */
    public boolean isLeaf()
    {
        return !hasChildren();
    }


    /**
     * Tells if the implementation is a leaf node. If it's a branch node
     * then false is returned.
     *
     * @param dn The DN we want to check
     * @return <code>true</code> if this is a leaf node, false otherwise.
     */
    public boolean isLeaf( DN dn )
    {
        DnNode<N> node = getNode( dn );

        if ( node == null )
        {
            return false;
        }

        return node.children.size() == 0;
    }


    /**
     * Returns the number of entries under this node. It includes
     * the node itself, plus the number of all it children and descendants.
     *
     * @return The number of descendents
     */
    public int size()
    {
        // The node itself
        int size = 1;

        // Iterate through the children if any
        if ( children.size() !=0 )
        {
            for ( DnNode<N> node : children.values() )
            {
                size += node.size();
            }
        }

        return size;
    }


    /**
     * @return Return the stored element, if any
     */
    public N getElement()
    {
        return element;
    }


    /**
     * @return Return the stored element, if any
     * @param dn The DN we want to get the element for
     */
    public N getElement( DN dn )
    {
        DnNode<N> node = getNode( dn );

        if ( node == null )
        {
            return null;
        }

        return node.element;
    }


    /**
     * @return True if the Node stores an element. BranchNode may not hold any
     * element.
     */
    public boolean hasElement()
    {
        return element != null;
    }


    /**
     * @return True if the Node stores an element. BranchNode may not hold any
     * element.
     * @param dn The DN we want to get the element for
     */
    public boolean hasElement( DN dn )
    {
        DnNode<N> node = getNode( dn );

        if ( node == null )
        {
            return false;
        }

        return node.element != null;
    }


    /**
     * Tells if the current DnNode has some children or not
     *
     * @return <code>true</code> if the node has some children
     */
    public boolean hasChildren()
    {
        return ( children != null ) && children.size() != 0;
    }


    /**
     * Tells if a node has some children or not
     *
     * @return <code>true</code> if the node has some children
     */
    public boolean hasChildren( DN dn ) throws LdapException
    {
        checkDn( dn );

        DnNode<N> node = getNode( dn );

        return ( node != null ) && node.hasChildren();
    }


    /**
     * @return The list of DnNode
     */
    public Map<RDN, DnNode<N>> getChildren()
    {
        return children;
    }


    /**
     * @return The parent DnNode, if any
     */
    public DnNode<N> getParent()
    {
        return parent;
    }


    /**
     * @return True if the current DnNode has a parent
     */
    public boolean hasParent()
    {
        return parent != null;
    }


    /**
     * Tells if there is a parent for a given DN,. This parent should be a
     * subset of the given dn.<br>
     * For instance, if we have stored dc=acme, dc=org into the tree,
     * the DN: ou=example, dc=acme, dc=org will have a parent
     * <br>For the DN ou=apache, dc=org, there is no parent, so false will be returned.
     *
     * @param dn the normalized distinguished name to resolve to a parent
     * @return true if there is a parent associated with the normalized dn
     */
    public boolean hasParent( DN dn )
    {
        List<RDN> rdns = dn.getRdns();

        DnNode<N> currentNode = this;
        DnNode<N> parent = null;

        // Iterate through all the RDN until we find the associated partition
        for ( int i = rdns.size() - 1; i >= 0; i-- )
        {
            RDN rdn = rdns.get( i );

            if ( rdn.equals( currentNode.rdn ) )
            {
                parent = currentNode;
            }
            else if ( currentNode.hasChildren() )
            {
                currentNode = currentNode.children.get( rdn );

                if ( currentNode == null )
                {
                    break;
                }

                parent = currentNode;
            }
            else
            {
                break;
            }
        }

        return( parent != null );
    }


    /**
     * Add a new node in the tree. The added node won't have any element.
     *
     * @param dn The node's DN
     * @throws NamingException
     */
    public void add( DN dn ) throws LdapException
    {
        add( dn, null );
    }


    /**
     * Add a new node in the tree. We can't add a node if its DN is empty.
     *
     * @param dn The node's DN
     * @param element The element to associate with this Node. Can be null.
     * @throws NamingException
     */
    public void add( DN dn, N element ) throws LdapException
    {
        checkDn( dn );

        // We first have to find the Node which will be the parent
        DnNode<N> parent = getNode( dn );

        if ( parent == null )
        {
            // No parent : add a new node to the root
            DnNode<N> childNode = createNode( dn, element, dn.size() );
            childNode.parent = this;
            children.put( childNode.rdn, childNode );
        }
        else
        {
            // We have a parent. Add the new node to the found parent
            int nbRdns = dn.size() - parent.depth;

            if ( nbRdns == 0 )
            {
                // That means the added DN is already present.
                String message = "Cannot add a node with a DN already existing in the tree";
                LOG.error( message );
                throw new LdapUnwillingToPerformException( ResultCodeEnum.UNWILLING_TO_PERFORM, message );
            }

            DnNode<N> rootNode = createNode( dn, element, nbRdns );

            // done. now, add the newly created tree to the parent node
            rootNode.parent = parent;
            parent.children.put( rootNode.rdn, rootNode );
        }
    }


    /**
     * Removes an element from the tree.
     *
     * @param element The element to remove
     */
    public void remove( DN dn ) throws LdapException
    {
        checkDn( dn );

        // Find the parent first : we won't be able to remove
        // a node if it's not present in the tree !
        DnNode<N> parent = getNode( dn );

        if ( parent == null )
        {
            return;
        }

        // Now, check that this parent has the same DN than the one
        // we gave and that there is no children
        if ( ( dn.size() != parent.depth ) || parent.hasChildren() )
        {
            return;
        }

        // Ok, no children, same DN, let's remove what we can.
        parent = parent.getParent();

        for ( RDN rdn : dn.getRdns() )
        {
            parent.children.remove( rdn );

            if ( parent.children.size() > 0 )
            {
                // We have to stop here, because the parent's node is shared with other Node.
                break;
            }

            parent = parent.getParent();
        }
    }


    /**
     * Tells if the current DnBranchNode contains another node associated
     * with an rdn.
     *
     * @param rdn The name we are looking for
     * @return <code>true</code> if the tree instance contains this name
     */
    public boolean contains( RDN rdn )
    {
        return children.containsKey( rdn );
    }


    /**
     * Get's a child using an rdn string.
     *
     * @param rdn the rdn to use as the node key
     * @return the child node corresponding to the rdn.
     */
    public DnNode<N> getChild( RDN rdn )
    {
        if ( children.containsKey( rdn ) )
        {
            return children.get( rdn );
        }

        return null;
    }


    /**
     * @return The Node's RDN
     */
    public RDN getRdn()
    {
        return rdn;
    }


    /**
     * Get the parent element of a given DN, if present in the tree. This parent should be a
     * subset of the given dn.<br>
     * For instance, if we have stored dc=acme, dc=org into the tree,
     * the DN: ou=example, dc=acme, dc=org will have a parent, and
     * dc=acme, dc=org will be returned.
     * <br>For the DN ou=apache, dc=org, there is no parent, so null will be returned.
     *
     * @param dn the normalized distinguished name to resolve to a parent
     * @return the parent associated with the normalized dn
     *
    public N getParentElement( DN dn )
    {
        List<RDN> rdns = dn.getRdns();

        DnNode<N> currentNode = this;
        DnNode<N> parent = null;

        // Iterate through all the RDN until we find the associated partition
        for ( int i = rdns.size() - 1; i >= 0; i-- )
        {
            if ( currentNode == null )
            {
                // We can stop here : there is no more node in the tree
                break;
            }

            RDN rdn = rdns.get( i );

            if ( currentNode.rdn.equals( rdn ) )
            {
                parent = currentNode;
                currentNode = children.get( rdn );
                continue;
            }

            break;
        }

        if ( parent != null )
        {
            return parent.getElement();
        }
        else
        {
            return null;
        }
    }


    /**
     * Get the Node for a given DN, if present in the tree.<br>
     * For instance, if we have stored dc=acme, dc=org into the tree,
     * the DN: ou=example, dc=acme, dc=org will have a parent, and
     * dc=acme, dc=org will be returned.
     * <br>For the DN ou=apache, dc=org, there is no parent, so null will be returned.
     *
     * @param dn the normalized distinguished name to resolve to a parent
     * @return the Node associated with the normalized dn
     */
    public DnNode<N> getNode( DN dn )
    {
        List<RDN> rdns = dn.getRdns();

        DnNode<N> currentNode = this;
        DnNode<N> parent = null;

        // Iterate through all the RDN until we find the associated partition
        for ( int i = rdns.size() - 1; i >= 0; i-- )
        {
            RDN rdn = rdns.get( i );

            if ( rdn.equals( currentNode.rdn ) )
            {
                parent = currentNode;
            }
            else if ( currentNode.hasChildren() )
            {
                currentNode = currentNode.children.get( rdn );

                if ( currentNode == null )
                {
                    break;
                }

                parent = currentNode;
            }
            else
            {
                break;
            }
        }

        if ( parent != null )
        {
            return parent;
        }
        else
        {
            return null;
        }
    }


    private String toString( String tabs )
    {
        if ( rdn == null )
        {
            return tabs;
        }

        StringBuilder sb = new StringBuilder();
        sb.append( tabs );

        boolean hasChildren = hasChildren();

        if ( isLeaf() )
        {
            sb.append( "Leaf[" ).append( rdn ).append( "]: " ).append( "'" ).append( element ).append( "'" );
            return sb.toString();
        }

        sb.append( "Branch[" ).append( rdn ).append( "]: " );

        if ( element != null )
        {
            sb.append( "'" ).append( element ).append( "'" );
        }

        tabs += "    ";

        sb.append( '\n' );

        boolean isFirst = true;

        if ( hasChildren )
        {
            for ( RDN rdn : children.keySet() )
            {
                if ( isFirst )
                {
                    isFirst = false;
                }
                else
                {
                    sb.append( "\n" );
                }

                DnNode<N> child = children.get( rdn );

                sb.append( child.toString( tabs ) );
            }
        }

        return sb.toString();
    }

    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return toString( "" );
    }
}
