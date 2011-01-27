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
package org.apache.directory.shared.ldap.codec.decorators;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.Asn1Container;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.ldap.codec.AttributeValueAssertion;
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.search.AndFilter;
import org.apache.directory.shared.ldap.codec.search.AttributeValueAssertionFilter;
import org.apache.directory.shared.ldap.codec.search.ConnectorFilter;
import org.apache.directory.shared.ldap.codec.search.ExtensibleMatchFilter;
import org.apache.directory.shared.ldap.codec.search.Filter;
import org.apache.directory.shared.ldap.codec.search.NotFilter;
import org.apache.directory.shared.ldap.codec.search.OrFilter;
import org.apache.directory.shared.ldap.codec.search.PresentFilter;
import org.apache.directory.shared.ldap.codec.search.SubstringFilter;
import org.apache.directory.shared.ldap.model.entry.Value;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.filter.AndNode;
import org.apache.directory.shared.ldap.model.filter.ApproximateNode;
import org.apache.directory.shared.ldap.model.filter.BranchNode;
import org.apache.directory.shared.ldap.model.filter.BranchNormalizedVisitor;
import org.apache.directory.shared.ldap.model.filter.EqualityNode;
import org.apache.directory.shared.ldap.model.filter.ExprNode;
import org.apache.directory.shared.ldap.model.filter.ExtensibleNode;
import org.apache.directory.shared.ldap.model.filter.GreaterEqNode;
import org.apache.directory.shared.ldap.model.filter.LeafNode;
import org.apache.directory.shared.ldap.model.filter.LessEqNode;
import org.apache.directory.shared.ldap.model.filter.NotNode;
import org.apache.directory.shared.ldap.model.filter.OrNode;
import org.apache.directory.shared.ldap.model.filter.PresenceNode;
import org.apache.directory.shared.ldap.model.filter.SimpleNode;
import org.apache.directory.shared.ldap.model.filter.SubstringNode;
import org.apache.directory.shared.ldap.model.message.SearchRequest;


/**
 * A decorator for the SearchRequest message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchRequestDecorator extends MessageDecorator
{
    /** The searchRequest length */
    private int searchRequestLength;

    /** The attributeDescriptionList length */
    private int attributeDescriptionListLength;

    /** A temporary storage for a terminal Filter */
    private Filter terminalFilter;

    /** The current filter. This is used while decoding a PDU */
    private Filter currentFilter;

    /** The global filter. This is used while decoding a PDU */
    private Filter topFilter;

    /** The SearchRequest TLV id */
    private int tlvId;


    /**
     * Makes a SearchRequest encodable.
     *
     * @param decoratedMessage the decorated SearchRequest
     */
    public SearchRequestDecorator( SearchRequest decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated SearchRequest
     */
    public SearchRequest getSearchRequest()
    {
        return ( SearchRequest ) getDecoratedMessage();
    }


    /**
     * Stores the encoded length for the SearchRequest
     * @param searchRequestLength The encoded length
     */
    public void setSearchRequestLength( int searchRequestLength )
    {
        this.searchRequestLength = searchRequestLength;
    }


    /**
     * @return The encoded SearchRequest's length
     */
    public int getSearchRequestLength()
    {
        return searchRequestLength;
    }


    /**
     * Stores the encoded length for the list of attributes
     * @param attributeDescriptionListLength The encoded length of the attributes
     */
    public void setAttributeDescriptionListLength( int attributeDescriptionListLength )
    {
        this.attributeDescriptionListLength = attributeDescriptionListLength;
    }


    /**
     * @return The encoded SearchRequest's attributes length
     */
    public int getAttributeDescriptionListLength()
    {
        return attributeDescriptionListLength;
    }


    public Filter getCurrentFilter()
    {
        return currentFilter;
    }


    /**
     * Set the SearchRequest PDU TLV's Id
     * @param tlvId The TLV id
     */
    public void setTlvId( int tlvId )
    {
        this.tlvId = tlvId;
    }


    /**
     * Gets the search filter associated with this search request.
     *
     * @return the expression node for the root of the filter expression tree.
     */
    public Filter getFilter()
    {
        return topFilter;
    }


    /**
     * Gets the search filter associated with this search request.
     *
     * @return the expression node for the root of the filter expression tree.
     */
    public ExprNode getFilterNode()
    {
        return transform( topFilter );
    }


    /**
     * Get the terminal filter
     *
     * @return Returns the terminal filter.
     */
    public Filter getTerminalFilter()
    {
        return terminalFilter;
    }


    /**
     * Set the terminal filter
     *
     * @param terminalFilter the teminalFilter.
     */
    public void setTerminalFilter( Filter terminalFilter )
    {
        this.terminalFilter = terminalFilter;
    }


    /**
     * {@inheritDoc}
     */
    public void setFilter( ExprNode filter )
    {
        topFilter = transform( filter );
    }


    /**
     * {@inheritDoc}
     */
    public void setFilter( String filter ) throws LdapException
    {
        getSearchRequest().setFilter( filter );
        this.currentFilter = transform( getSearchRequest().getFilter() );
    }


    /**
     * Set the current filter
     *
     * @param filter The filter to set.
     */
    public void setCurrentFilter( Filter filter )
    {
        currentFilter = filter;
    }


    /**
     * Add a current filter. We have two cases :
     * - there is no previous current filter : the filter
     * is the top level filter
     * - there is a previous current filter : the filter is added
     * to the currentFilter set, and the current filter is changed
     *
     * In any case, the previous current filter will always be a
     * ConnectorFilter when this method is called.
     *
     * @param localFilter The filter to set.
     */
    public void addCurrentFilter( Filter localFilter ) throws DecoderException
    {
        if ( currentFilter != null )
        {
            // Ok, we have a parent. The new Filter will be added to
            // this parent, and will become the currentFilter if it's a connector.
            ( ( ConnectorFilter ) currentFilter ).addFilter( localFilter );
            localFilter.setParent( currentFilter, currentFilter.getTlvId() );

            if ( localFilter instanceof ConnectorFilter )
            {
                currentFilter = localFilter;
            }
        }
        else
        {
            // No parent. This Filter will become the root.
            currentFilter = localFilter;
            currentFilter.setParent( null, tlvId );
            topFilter = localFilter;
        }
    }


    /**
     * This method is used to clear the filter's stack for terminated elements. An element
     * is considered as terminated either if :
     *  - it's a final element (ie an element which cannot contains a Filter)
     *  - its current length equals its expected length.
     *
     * @param container The container being decoded
     */
    public void unstackFilters( Asn1Container container )
    {
        LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer ) container;

        TLV tlv = ldapMessageContainer.getCurrentTLV();
        TLV localParent = tlv.getParent();
        Filter localFilter = terminalFilter;

        // The parent has been completed, so fold it
        while ( ( localParent != null ) && ( localParent.getExpectedLength() == 0 ) )
        {
            int parentTlvId = localFilter.getParent() != null ? localFilter.getParent().getTlvId() : localFilter
                .getParentTlvId();

            if ( localParent.getId() != parentTlvId )
            {
                localParent = localParent.getParent();
            }
            else
            {
                Filter filterParent = localFilter.getParent();

                // We have a special case with PresentFilter, which has not been
                // pushed on the stack, so we need to get its parent's parent
                if ( localFilter instanceof PresentFilter )
                {
                    if ( filterParent == null )
                    {
                        // We don't have parent, get out
                        break;
                    }

                    filterParent = filterParent.getParent();
                }
                else if ( filterParent instanceof Filter )
                {
                    filterParent = filterParent.getParent();
                }

                if ( filterParent instanceof Filter )
                {
                    // The parent is a filter ; it will become the new currentFilter
                    // and we will loop again.
                    currentFilter = ( Filter ) filterParent;
                    localFilter = currentFilter;
                    localParent = localParent.getParent();
                }
                else
                {
                    // We can stop the recursion, we have reached the searchResult Object
                    break;
                }
            }
        }
    }


    /**
     * Transform the Filter part of a SearchRequest to an ExprNode
     *
     * @param filter The filter to be transformed
     * @return An ExprNode
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private ExprNode transform( Filter filter )
    {
        if ( filter != null )
        {
            // Transform OR, AND or NOT leaves
            if ( filter instanceof ConnectorFilter)
            {
                BranchNode branch = null;

                if ( filter instanceof AndFilter)
                {
                    branch = new AndNode();
                }
                else if ( filter instanceof OrFilter)
                {
                    branch = new OrNode();
                }
                else if ( filter instanceof NotFilter)
                {
                    branch = new NotNode();
                }

                List<Filter> filtersSet = ( ( ConnectorFilter ) filter ).getFilterSet();

                // Loop on all AND/OR children
                if ( filtersSet != null )
                {
                    for ( Filter node : filtersSet )
                    {
                        branch.addNode( transform( node ) );
                    }
                }

                return branch;
            }
            else
            {
                // Transform PRESENT or ATTRIBUTE_VALUE_ASSERTION
                LeafNode branch = null;

                if ( filter instanceof PresentFilter )
                {
                    branch = new PresenceNode( ( ( PresentFilter ) filter ).getAttributeDescription() );
                }
                else if ( filter instanceof AttributeValueAssertionFilter )
                {
                    AttributeValueAssertion ava = ( ( AttributeValueAssertionFilter ) filter ).getAssertion();

                    // Transform =, >=, <=, ~= filters
                    switch ( ( ( AttributeValueAssertionFilter ) filter ).getFilterType() )
                    {
                        case LdapConstants.EQUALITY_MATCH_FILTER:
                            branch = new EqualityNode( ava.getAttributeDesc(), ava.getAssertionValue() );

                            break;

                        case LdapConstants.GREATER_OR_EQUAL_FILTER:
                            branch = new GreaterEqNode( ava.getAttributeDesc(), ava.getAssertionValue() );

                            break;

                        case LdapConstants.LESS_OR_EQUAL_FILTER:
                            branch = new LessEqNode( ava.getAttributeDesc(), ava.getAssertionValue() );

                            break;

                        case LdapConstants.APPROX_MATCH_FILTER:
                            branch = new ApproximateNode( ava.getAttributeDesc(), ava.getAssertionValue() );

                            break;
                    }

                }
                else if ( filter instanceof SubstringFilter )
                {
                    // Transform Substring filters
                    SubstringFilter substrFilter = ( SubstringFilter ) filter;
                    String initialString = null;
                    String finalString = null;
                    List<String> anyString = null;

                    if ( substrFilter.getInitialSubstrings() != null )
                    {
                        initialString = substrFilter.getInitialSubstrings();
                    }

                    if ( substrFilter.getFinalSubstrings() != null )
                    {
                        finalString = substrFilter.getFinalSubstrings();
                    }

                    if ( substrFilter.getAnySubstrings() != null )
                    {
                        anyString = new ArrayList<String>();

                        for ( String any : substrFilter.getAnySubstrings() )
                        {
                            anyString.add( any );
                        }
                    }

                    branch = new SubstringNode( anyString, substrFilter.getType(), initialString, finalString );
                }
                else if ( filter instanceof ExtensibleMatchFilter )
                {
                    // Transform Extensible Match Filter
                    ExtensibleMatchFilter extFilter = ( ExtensibleMatchFilter ) filter;
                    String matchingRule = null;

                    Value<?> value = extFilter.getMatchValue();

                    if ( extFilter.getMatchingRule() != null )
                    {
                        matchingRule = extFilter.getMatchingRule();
                    }

                    branch = new ExtensibleNode( extFilter.getType(), value, matchingRule, extFilter.isDnAttributes() );
                }

                return branch;
            }
        }
        else
        {
            // We have found nothing to transform. Return null then.
            return null;
        }
    }


    /**
     * Transform an ExprNode filter to a Filter
     *
     * @param exprNode The filter to be transformed
     * @return A filter
     */
    private static Filter transform( ExprNode exprNode )
    {
        if ( exprNode != null )
        {
            Filter filter = null;

            // Transform OR, AND or NOT leaves
            if ( exprNode instanceof BranchNode )
            {
                if ( exprNode instanceof AndNode )
                {
                    filter = new AndFilter();
                }
                else if ( exprNode instanceof OrNode )
                {
                    filter = new OrFilter();
                }
                else if ( exprNode instanceof NotNode )
                {
                    filter = new NotFilter();
                }

                List<ExprNode> children = ( (BranchNode) exprNode ).getChildren();

                // Loop on all AND/OR children
                if ( children != null )
                {
                    for ( ExprNode child : children )
                    {
                        try
                        {
                            ( ( ConnectorFilter ) filter ).addFilter( transform( child ) );
                        }
                        catch ( DecoderException de )
                        {
                            return null;
                        }
                    }
                }
            }
            else
            {
                if ( exprNode instanceof PresenceNode )
                {
                    // Transform Presence Node
                    filter = new PresentFilter();
                    ( ( PresentFilter ) filter ).setAttributeDescription( ( ( PresenceNode ) exprNode ).getAttribute() );
                }
                else if ( exprNode instanceof SimpleNode<?> )
                {
                    if ( exprNode instanceof EqualityNode<?> )
                    {
                        filter = new AttributeValueAssertionFilter( LdapConstants.EQUALITY_MATCH_FILTER );
                        AttributeValueAssertion assertion = new AttributeValueAssertion();
                        assertion.setAttributeDesc( ( ( EqualityNode<?> ) exprNode ).getAttribute() );
                        assertion.setAssertionValue( ( ( EqualityNode<?> ) exprNode ).getValue() );
                        ( ( AttributeValueAssertionFilter ) filter ).setAssertion( assertion );
                    }
                    else if ( exprNode instanceof GreaterEqNode<?> )
                    {
                        filter = new AttributeValueAssertionFilter( LdapConstants.GREATER_OR_EQUAL_FILTER );
                        AttributeValueAssertion assertion = new AttributeValueAssertion();
                        assertion.setAttributeDesc( ( ( GreaterEqNode<?> ) exprNode ).getAttribute() );
                        assertion.setAssertionValue( ( ( GreaterEqNode<?> ) exprNode ).getValue() );
                        ( ( AttributeValueAssertionFilter ) filter ).setAssertion( assertion );
                    }
                    else if ( exprNode instanceof LessEqNode<?> )
                    {
                        filter = new AttributeValueAssertionFilter( LdapConstants.LESS_OR_EQUAL_FILTER );
                        AttributeValueAssertion assertion = new AttributeValueAssertion();
                        assertion.setAttributeDesc( ( ( LessEqNode<?> ) exprNode ).getAttribute() );
                        assertion.setAssertionValue( ( ( LessEqNode<?> ) exprNode ).getValue() );
                        ( ( AttributeValueAssertionFilter ) filter ).setAssertion( assertion );
                    }
                    else if ( exprNode instanceof ApproximateNode<?> )
                    {
                        filter = new AttributeValueAssertionFilter( LdapConstants.APPROX_MATCH_FILTER );
                        AttributeValueAssertion assertion = new AttributeValueAssertion();
                        assertion.setAttributeDesc( ( (ApproximateNode<?>) exprNode ).getAttribute() );
                        assertion.setAssertionValue( ( ( ApproximateNode<?> ) exprNode ).getValue() );
                        ( ( AttributeValueAssertionFilter ) filter ).setAssertion( assertion );
                    }
                }
                else if ( exprNode instanceof SubstringNode )
                {
                    // Transform Substring Nodes
                    filter = new SubstringFilter();

                    ( ( SubstringFilter ) filter ).setType( ( ( SubstringNode ) exprNode ).getAttribute() );
                    String initialString = ( ( SubstringNode ) exprNode ).getInitial();
                    String finalString = ( ( SubstringNode ) exprNode ).getFinal();
                    List<String> anyStrings = ( ( SubstringNode ) exprNode ).getAny();

                    if ( initialString != null )
                    {
                        ( ( SubstringFilter ) filter ).setInitialSubstrings( initialString );
                    }

                    if ( finalString != null )
                    {
                        ( ( SubstringFilter ) filter ).setFinalSubstrings( finalString );
                    }

                    if ( anyStrings != null )
                    {
                        for ( String any : anyStrings )
                        {
                            ( ( SubstringFilter ) filter ).addAnySubstrings( any );
                        }
                    }
                }
                else if ( exprNode instanceof ExtensibleNode )
                {
                    // Transform Extensible Node
                    filter = new ExtensibleMatchFilter();

                    String attribute = ( ( ExtensibleNode ) exprNode ).getAttribute();
                    String matchingRule = ( ( ExtensibleNode ) exprNode ).getMatchingRuleId();
                    boolean dnAttributes = ( ( ExtensibleNode ) exprNode ).hasDnAttributes();
                    Value<?> value = ( ( ExtensibleNode ) exprNode ).getValue();

                    if ( attribute != null )
                    {
                        ( ( ExtensibleMatchFilter ) filter ).setType( attribute );
                    }

                    if ( matchingRule != null )
                    {
                        ( ( ExtensibleMatchFilter ) filter ).setMatchingRule( matchingRule );
                    }

                    ( ( ExtensibleMatchFilter ) filter ).setMatchValue( value );
                    ( ( ExtensibleMatchFilter ) filter ).setDnAttributes( dnAttributes );
                }
            }

            return filter;
        }
        else
        {
            // We have found nothing to transform. Return null then.
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int hash = 37;

        if ( getSearchRequest().getBase() != null )
        {
            hash = hash * 17 + getSearchRequest().getBase().hashCode();
        }

        hash = hash * 17 + getSearchRequest().getDerefAliases().hashCode();
        hash = hash * 17 + getSearchRequest().getScope().hashCode();
        hash = hash * 17 + Long.valueOf( getSearchRequest().getSizeLimit() ).hashCode();
        hash = hash * 17 + getSearchRequest().getTimeLimit();
        hash = hash * 17 + ( getSearchRequest().getTypesOnly() ? 0 : 1 );

        List<String> attributes = getSearchRequest().getAttributes();
        if ( attributes != null )
        {
            hash = hash * 17 + attributes.size();

            // Order doesn't matter, thus just add hashCode
            for ( String attr : attributes )
            {
                hash = hash + attr.hashCode();
            }
        }

        BranchNormalizedVisitor visitor = new BranchNormalizedVisitor();
        getSearchRequest().getFilter().accept( visitor );
        hash = hash * 17 + currentFilter.toString().hashCode();
        hash = hash * 17 + super.hashCode();

        return hash;
    }


    /**
     * Checks to see if two search requests are equal. The Lockable properties
     * and the get/set context specific parameters are not consulted to
     * determine equality. The filter expression tree comparison will normalize
     * the child order of filter branch nodes then generate a string
     * representation which is comparable. For the time being this is a very
     * costly operation.
     *
     * @param obj the object to check for equality to this SearchRequest
     * @return true if the obj is a SearchRequest and equals this SearchRequest,
     *         false otherwise
     */
    public boolean equals( Object obj )
    {
        if ( obj == this )
        {
            return true;
        }

        if ( !super.equals( obj ) )
        {
            return false;
        }

        SearchRequest req = ( SearchRequest ) obj;

        if ( !req.getBase().equals( getSearchRequest().getBase() ) )
        {
            return false;
        }

        if ( req.getDerefAliases() != getSearchRequest().getDerefAliases() )
        {
            return false;
        }

        if ( req.getScope() != getSearchRequest().getScope() )
        {
            return false;
        }

        if ( req.getSizeLimit() != getSearchRequest().getSizeLimit() )
        {
            return false;
        }

        if ( req.getTimeLimit() != getSearchRequest().getTimeLimit() )
        {
            return false;
        }

        if ( req.getTypesOnly() != getSearchRequest().getTypesOnly() )
        {
            return false;
        }

        List<String> attributes = getSearchRequest().getAttributes();
        if ( req.getAttributes() == null && attributes != null && attributes.size() > 0 )
        {
            return false;
        }

        if ( req.getAttributes() != null && attributes == null && req.getAttributes().size() > 0 )
        {
            return false;
        }

        if ( req.getAttributes() != null && attributes != null )
        {
            if ( req.getAttributes().size() != attributes.size() )
            {
                return false;
            }

            for ( String attribute : attributes )
            {
                if ( ! req.getAttributes().contains( attribute ) )
                {
                    return false;
                }
            }
        }

        BranchNormalizedVisitor visitor = new BranchNormalizedVisitor();
        req.getFilter().accept( visitor );
        getSearchRequest().getFilter().accept( visitor );

        String myFilterString = currentFilter.toString();
        String reqFilterString = req.getFilter().toString();

        return myFilterString.equals( reqFilterString );
    }


    /**
     * Return a string the represent a SearchRequest
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "    SearchRequest\n" );
        sb.append( "        baseDn : '" ).append( getSearchRequest().getBase() ).append( "'\n" );

        if ( currentFilter != null )
        {
            sb.append( "        filter : '" );
            sb.append( currentFilter.toString() );
            sb.append( "'\n" );
        }

        sb.append( "        scope : " );

        switch ( getSearchRequest().getScope() )
        {
            case OBJECT:
                sb.append( "base object" );
                break;

            case ONELEVEL:
                sb.append( "single level" );
                break;

            case SUBTREE:
                sb.append( "whole subtree" );
                break;
        }

        sb.append( '\n' );

        sb.append( "        typesOnly : " ).append( getSearchRequest().getTypesOnly() ).append( '\n' );

        sb.append( "        Size Limit : " );

        if ( getSearchRequest().getSizeLimit() == 0L )
        {
            sb.append( "no limit" );
        }
        else
        {
            sb.append( getSearchRequest().getSizeLimit() );
        }

        sb.append( '\n' );

        sb.append( "        Time Limit : " );

        if ( getSearchRequest().getTimeLimit() == 0 )
        {
            sb.append( "no limit" );
        }
        else
        {
            sb.append( getSearchRequest().getTimeLimit() );
        }

        sb.append( '\n' );

        sb.append( "        Deref Aliases : " );

        switch ( getSearchRequest().getDerefAliases() )
        {
            case NEVER_DEREF_ALIASES:
                sb.append( "never Deref Aliases" );
                break;

            case DEREF_IN_SEARCHING:
                sb.append( "deref In Searching" );
                break;

            case DEREF_FINDING_BASE_OBJ:
                sb.append( "deref Finding Base Obj" );
                break;

            case DEREF_ALWAYS:
                sb.append( "deref Always" );
                break;
        }

        sb.append( '\n' );
        sb.append( "        attributes : " );

        boolean isFirst = true;

        List<String> attributes = getSearchRequest().getAttributes();
        if ( attributes != null )
        {
            for ( String attribute : attributes )
            {
                if ( isFirst )
                {
                    isFirst = false;
                }
                else
                {
                    sb.append(", ");
                }

                sb.append('\'').append(attribute).append('\'');
            }
        }

        sb.append( '\n' );

        // The controls
        sb.append( super.toString() );

        return sb.toString();
    }
}
