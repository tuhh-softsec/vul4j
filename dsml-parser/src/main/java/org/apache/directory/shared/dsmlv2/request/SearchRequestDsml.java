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
package org.apache.directory.shared.dsmlv2.request;


import java.util.ArrayList;
import java.util.List; 

import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.dsmlv2.ParserUtils;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.codec.api.LdapConstants;
import org.apache.directory.shared.ldap.model.entry.Value;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.filter.AndNode;
import org.apache.directory.shared.ldap.model.filter.ApproximateNode;
import org.apache.directory.shared.ldap.model.filter.BranchNode;
import org.apache.directory.shared.ldap.model.filter.EqualityNode;
import org.apache.directory.shared.ldap.model.filter.ExprNode;
import org.apache.directory.shared.ldap.model.filter.ExtensibleNode;
import org.apache.directory.shared.ldap.model.filter.GreaterEqNode;
import org.apache.directory.shared.ldap.model.filter.LeafNode;
import org.apache.directory.shared.ldap.model.filter.LessEqNode;
import org.apache.directory.shared.ldap.model.filter.NotNode;
import org.apache.directory.shared.ldap.model.filter.OrNode;
import org.apache.directory.shared.ldap.model.filter.PresenceNode;
import org.apache.directory.shared.ldap.model.filter.SearchScope;
import org.apache.directory.shared.ldap.model.filter.SubstringNode;
import org.apache.directory.shared.ldap.model.message.AliasDerefMode;
import org.apache.directory.shared.ldap.model.message.MessageTypeEnum;
import org.apache.directory.shared.ldap.model.message.SearchRequest;
import org.apache.directory.shared.ldap.model.message.SearchRequestImpl;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;


/**
 * DSML Decorator for SearchRequest
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchRequestDsml 
    extends AbstractResultResponseRequestDsml<SearchRequest>
    implements SearchRequest
{
    /** A temporary storage for a terminal Filter */
    private Filter terminalFilter;

    /** The current filter. This is used while decoding a PDU */
    private Filter currentFilter;

    /** The global filter. This is used while decoding a PDU */
    private Filter topFilter;
    
    
    /**
     * Creates a new getDecoratedMessage() of SearchRequestDsml.
     */
    public SearchRequestDsml( LdapCodecService codec )
    {
        super( codec, new SearchRequestImpl() );
    }


    /**
     * Creates a new getDecoratedMessage() of SearchRequestDsml.
     *
     * @param ldapMessage
     *      the message to decorate
     */
    public SearchRequestDsml( LdapCodecService codec, SearchRequest ldapMessage )
    {
        super( codec, ldapMessage );
    }
    
    


    public Filter getCurrentFilter()
    {
        return currentFilter;
    }


    /**
     * Gets the search filter associated with this search request.
     *
     * @return the expression node for the root of the filter expression tree.
     */
    public Filter getCodecFilter()
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
            localFilter.setParent( currentFilter );

            if ( localFilter instanceof ConnectorFilter )
            {
                currentFilter = localFilter;
            }
        }
        else
        {
            // No parent. This Filter will become the root.
            currentFilter = localFilter;
            currentFilter.setParent( null );
            topFilter = localFilter;
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
     * {@inheritDoc}
     */
    public MessageTypeEnum getType()
    {
        return getDecorated().getType();
    }


    /**
     * {@inheritDoc}
     */
    public Element toDsml( Element root )
    {
        Element element = super.toDsml( root );

        SearchRequest request = ( SearchRequest ) getDecorated();

        // Dn
        if ( request.getBase() != null )
        {
            element.addAttribute( "dn", request.getBase().getName() );
        }

        // Scope
        SearchScope scope = request.getScope();
        if ( scope != null )
        {
            if ( scope == SearchScope.OBJECT )
            {
                element.addAttribute( "scope", "baseObject" );
            }
            else if ( scope == SearchScope.ONELEVEL )
            {
                element.addAttribute( "scope", "singleLevel" );
            }
            else if ( scope == SearchScope.SUBTREE )
            {
                element.addAttribute( "scope", "wholeSubtree" );
            }
        }

        // DerefAliases
        AliasDerefMode derefAliases = request.getDerefAliases();

        switch ( derefAliases )
        {
            case NEVER_DEREF_ALIASES:
                element.addAttribute( "derefAliases", "neverDerefAliases" );
                break;

            case DEREF_ALWAYS:
                element.addAttribute( "derefAliases", "derefAlways" );
                break;

            case DEREF_FINDING_BASE_OBJ:
                element.addAttribute( "derefAliases", "derefFindingBaseObj" );
                break;

            case DEREF_IN_SEARCHING:
                element.addAttribute( "derefAliases", "derefInSearching" );
                break;

            default:
                throw new IllegalStateException( "Unexpected deref alias mode " + derefAliases );
        }

        // SizeLimit
        if ( request.getSizeLimit() != 0L )
        {
            element.addAttribute( "sizeLimit", "" + request.getSizeLimit() );
        }

        // TimeLimit
        if ( request.getTimeLimit() != 0 )
        {
            element.addAttribute( "timeLimit", "" + request.getTimeLimit() );
        }

        // TypesOnly
        if ( request.getTypesOnly() )
        {
            element.addAttribute( "typesOnly", "true" );
        }

        // Filter
        Element filterElement = element.addElement( "filter" );
        toDsml( filterElement, request.getFilter() );

        // Attributes
        List<String> attributes = request.getAttributes();

        if ( attributes.size() > 0 )
        {
            Element attributesElement = element.addElement( "attributes" );

            for ( String entryAttribute : attributes )
            {
                attributesElement.addElement( "attribute" ).addAttribute( "name", entryAttribute );
            }
        }

        return element;
    }


    /**
     * Recursively converts the filter of the Search Request into a DSML representation and adds 
     * it to the XML Element corresponding to the Search Request
     *
     * @param element
     *      the parent Element
     * @param filter
     *      the filter to convert
     */
    private void toDsml( Element element, ExprNode filter )
    {
        // AND FILTER
        if ( filter instanceof AndNode )
        {
            Element newElement = element.addElement( "and" );

            List<ExprNode> filterList = ( (AndNode) filter ).getChildren();

            for ( int i = 0; i < filterList.size(); i++ )
            {
                toDsml( newElement, filterList.get( i ) );
            }
        }

        // OR FILTER
        else if ( filter instanceof OrNode )
        {
            Element newElement = element.addElement( "or" );

            List<ExprNode> filterList = ( ( OrNode ) filter ).getChildren();

            for ( int i = 0; i < filterList.size(); i++ )
            {
                toDsml( newElement, filterList.get( i ) );
            }
        }

        // NOT FILTER
        else if ( filter instanceof NotNode )
        {
            Element newElement = element.addElement( "not" );

            toDsml( newElement, ( ( NotNode ) filter ).getFirstChild() );
        }

        // SUBSTRING FILTER
        else if ( filter instanceof SubstringNode)
        {
            Element newElement = element.addElement( "substrings" );

            SubstringNode substringFilter = ( SubstringNode ) filter;

            newElement.addAttribute( "name", substringFilter.getAttribute() );

            String initial = substringFilter.getInitial();

            if ( ( initial != null ) && ( !"".equals( initial ) ) )
            {
                newElement.addElement( "initial" ).setText( initial );
            }

            List<String> anyList = substringFilter.getAny();

            for ( int i = 0; i < anyList.size(); i++ )
            {
                newElement.addElement( "any" ).setText( anyList.get( i ) );
            }

            String finalString = substringFilter.getFinal();

            if ( ( finalString != null ) && ( !"".equals( finalString ) ) )
            {
                newElement.addElement( "final" ).setText( finalString );
            }
        }

        // APPROXMATCH, EQUALITYMATCH, GREATEROREQUALS & LESSOREQUAL FILTERS
        else if ( filter instanceof AttributeValueAssertionFilter )
        {
            AttributeValueAssertionFilter avaFilter = ( AttributeValueAssertionFilter ) filter;

            Element newElement = null;
            int filterType = avaFilter.getFilterType();
            if ( filterType == LdapConstants.APPROX_MATCH_FILTER )
            {
                newElement = element.addElement( "approxMatch" );
            }
            else if ( filterType == LdapConstants.EQUALITY_MATCH_FILTER )
            {
                newElement = element.addElement( "equalityMatch" );
            }
            else if ( filterType == LdapConstants.GREATER_OR_EQUAL_FILTER )
            {
                newElement = element.addElement( "greaterOrEqual" );
            }
            else if ( filterType == LdapConstants.LESS_OR_EQUAL_FILTER )
            {
                newElement = element.addElement( "lessOrEqual" );
            }

            AttributeValueAssertion assertion = avaFilter.getAssertion();
            if ( assertion != null )
            {
                newElement.addAttribute( "name", assertion.getAttributeDesc() );

                Value<?> value = assertion.getAssertionValue();
                if ( value != null )
                {
                    if ( ParserUtils.needsBase64Encoding( value ) )
                    {
                        Namespace xsdNamespace = new Namespace( "xsd", ParserUtils.XML_SCHEMA_URI );
                        Namespace xsiNamespace = new Namespace( "xsi", ParserUtils.XML_SCHEMA_INSTANCE_URI );
                        element.getDocument().getRootElement().add( xsdNamespace );
                        element.getDocument().getRootElement().add( xsiNamespace );

                        Element valueElement = newElement.addElement( "value" ).addText(
                            ParserUtils.base64Encode( value ) );
                        valueElement
                            .addAttribute( new QName( "type", xsiNamespace ), "xsd:" + ParserUtils.BASE64BINARY );
                    }
                    else
                    {
                        newElement.addElement( "value" ).setText( value.getString() );
                    }
                }
            }
        }

        // PRESENT FILTER
        else if ( filter instanceof PresentFilter )
        {
            Element newElement = element.addElement( "present" );

            newElement.addAttribute( "name", ( ( PresentFilter ) filter ).getAttributeDescription() );
        }

        // EXTENSIBLEMATCH
        else if ( filter instanceof ExtensibleMatchFilter )
        {
            Element newElement = element.addElement( "extensibleMatch" );

            ExtensibleMatchFilter extensibleMatchFilter = ( ExtensibleMatchFilter ) filter;

            Value<?> value = extensibleMatchFilter.getMatchValue();
            if ( value != null )
            {
                if ( ParserUtils.needsBase64Encoding( value ) )
                {
                    Namespace xsdNamespace = new Namespace( "xsd", ParserUtils.XML_SCHEMA_URI );
                    Namespace xsiNamespace = new Namespace( "xsi", ParserUtils.XML_SCHEMA_INSTANCE_URI );
                    element.getDocument().getRootElement().add( xsdNamespace );
                    element.getDocument().getRootElement().add( xsiNamespace );

                    Element valueElement = newElement.addElement( "value" ).addText( ParserUtils.base64Encode( value ) );
                    valueElement.addAttribute( new QName( "type", xsiNamespace ), "xsd:" + ParserUtils.BASE64BINARY );
                }
                else
                {
                    newElement.addElement( "value" ).setText( value.getString() );
                }
            }

            if ( extensibleMatchFilter.isDnAttributes() )
            {
                newElement.addAttribute( "dnAttributes", "true" );
            }

            String matchingRule = extensibleMatchFilter.getMatchingRule();
            if ( ( matchingRule != null ) && ( "".equals( matchingRule ) ) )
            {
                newElement.addAttribute( "matchingRule", matchingRule );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public MessageTypeEnum[] getResponseTypes()
    {
        return getDecorated().getResponseTypes();
    }


    /**
     * {@inheritDoc}
     */
    public Dn getBase()
    {
        return getDecorated().getBase();
    }


    /**
     * {@inheritDoc}
     */
    public void setBase( Dn baseDn )
    {
        getDecorated().setBase( baseDn );
    }


    /**
     * {@inheritDoc}
     */
    public SearchScope getScope()
    {
        return getDecorated().getScope();
    }


    /**
     * {@inheritDoc}
     */
    public void setScope( SearchScope scope )
    {
        getDecorated().setScope( scope );
    }


    /**
     * {@inheritDoc}
     */
    public AliasDerefMode getDerefAliases()
    {
        return getDecorated().getDerefAliases();
    }


    /**
     * {@inheritDoc}
     */
    public void setDerefAliases( AliasDerefMode aliasDerefAliases )
    {
        getDecorated().setDerefAliases( aliasDerefAliases );
    }


    /**
     * {@inheritDoc}
     */
    public long getSizeLimit()
    {
        return getDecorated().getSizeLimit();
    }


    /**
     * {@inheritDoc}
     */
    public void setSizeLimit( long entriesMax )
    {
        getDecorated().setSizeLimit( entriesMax );
    }


    /**
     * {@inheritDoc}
     */
    public int getTimeLimit()
    {
        return getDecorated().getTimeLimit();
    }


    /**
     * {@inheritDoc}
     */
    public void setTimeLimit( int secondsMax )
    {
        getDecorated().setTimeLimit( secondsMax );
    }


    /**
     * {@inheritDoc}
     */
    public boolean getTypesOnly()
    {
        return getDecorated().getTypesOnly();
    }


    /**
     * {@inheritDoc}
     */
    public void setTypesOnly( boolean typesOnly )
    {
        getDecorated().setTypesOnly( typesOnly );
    }


    /**
     * {@inheritDoc}
     */
    public ExprNode getFilter()
    {
        return getDecorated().getFilter();
    }


    /**
     * {@inheritDoc}
     */
    public void setFilter( ExprNode filter )
    {
        getDecorated().setFilter( filter );
    }


    /**
     * {@inheritDoc}
     */
    public void setFilter( String filter ) throws LdapException
    {
        getDecorated().setFilter( filter );
    }


    /**
     * {@inheritDoc}
     */
    public List<String> getAttributes()
    {
        return getDecorated().getAttributes();
    }


    /**
     * {@inheritDoc}
     */
    public void addAttributes( String... attributes )
    {
        getDecorated().addAttributes( attributes );
    }


    /**
     * {@inheritDoc}
     */
    public void removeAttribute( String attribute )
    {
        getDecorated().removeAttribute( attribute );
    }
}
