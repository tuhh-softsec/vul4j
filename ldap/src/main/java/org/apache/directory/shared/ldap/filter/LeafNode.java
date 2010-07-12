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
package org.apache.directory.shared.ldap.filter;

import org.apache.directory.shared.ldap.schema.AttributeType;


/**
 * Abstract base class for leaf nodes within the expression filter tree.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LeafNode extends AbstractExprNode
{
    /** attributeType on which this leaf is based */
    protected AttributeType attributeType;
    
    /** attribute on which this leaf is based */
    protected String attribute;


    /**
     * Creates a leaf node.
     * 
     * @param attributeType the attribute this node is based on
     * @param assertionType the type of this leaf node
     */
    protected LeafNode( AttributeType attributeType, AssertionType assertionType )
    {
        super( assertionType );
        this.attributeType = attributeType;
        
        if ( attributeType != null )
        {
            this.attribute = attributeType.getName();
            isSchemaAware = true;
        }
    }


    /**
     * Creates a leaf node.
     * 
     * @param attributeType the attribute this node is based on
     * @param assertionType the type of this leaf node
     */
    protected LeafNode( String attribute, AssertionType assertionType )
    {
        super( assertionType );
        this.attributeType = null;
        this.attribute = attribute;
        isSchemaAware = false;
    }
    
    
    /**
     * Gets whether this node is a leaf - the answer is always true here.
     * 
     * @return true always
     */
    public final boolean isLeaf()
    {
        return true;
    }


    /**
     * Gets the attributeType this leaf node is based on.
     * 
     * @return the attributeType asserted
     */
    public final AttributeType getAttributeType()
    {
        return attributeType;
    }


    /**
     * Gets the attribute this leaf node is based on.
     * 
     * @return the attribute asserted
     */
    public final String getAttribute()
    {
        return attribute;
    }
    
    
    /**
     * Sets the attributeType this leaf node is based on.
     * 
     * @param attributeType the attributeType that is asserted by this filter node
     */
    public void setAttributeType( AttributeType attributeType )
    {
        this.attributeType = attributeType;
        
        if ( attributeType != null )
        {
            attribute = attributeType.getName();
            isSchemaAware = true;
        }
    }
    
    
    /**
     * Sets the attribute this leaf node is based on.
     * 
     * @param attribute the attribute that is asserted by this filter node
     */
    public void setAttribute( String attribute )
    {
        this.attribute = attribute;
        isSchemaAware = false;
    }

    
    /**
     * @see org.apache.directory.shared.ldap.filter.ExprNode#accept(
     *      org.apache.directory.shared.ldap.filter.FilterVisitor)
     * 
     * @param visitor the filter expression tree structure visitor
     * @return The modified element
     */
    public final Object accept( FilterVisitor visitor )
    {
        if ( visitor.canVisit( this ) )
        {
            return visitor.visit( this );
        }
        else
        {
            return null;
        }
    }


    /**
     * @see Object#hashCode()
     * @return the instance's hash code 
     */
    public int hashCode()
    {
        int h = 37;
        
        h = h*17 + super.hashCode();
        
        if ( attributeType != null )
        {
            h = h*17 + attributeType.hashCode();
        }
        else
        {
            h = h*17 + attribute.hashCode();
        }
        
        return h;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object other )
    {
        if ( this == other )
        {
            return true;
        }

        if ( !( other instanceof LeafNode ) )
        {
            return false;
        }
        
        LeafNode otherNode = (LeafNode)other;

        if ( other.getClass() != this.getClass() )
        {
            return false;
        }
            
        if ( attributeType != null )
        {
            return attributeType.equals( otherNode.getAttributeType() );
        }
        else
        {
            return attribute.equalsIgnoreCase( otherNode.getAttribute() );
        }
    }
}
