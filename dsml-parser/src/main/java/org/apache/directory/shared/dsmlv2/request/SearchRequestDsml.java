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


import java.util.List;

import org.apache.directory.shared.dsmlv2.ParserUtils;
import org.apache.directory.shared.ldap.codec.AttributeValueAssertion;
import org.apache.directory.shared.ldap.codec.LdapCodecService;
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.codec.search.AttributeValueAssertionFilter;
import org.apache.directory.shared.ldap.codec.search.ExtensibleMatchFilter;
import org.apache.directory.shared.ldap.codec.search.PresentFilter;
import org.apache.directory.shared.ldap.model.entry.Value;
import org.apache.directory.shared.ldap.model.filter.AndNode;
import org.apache.directory.shared.ldap.model.filter.ExprNode;
import org.apache.directory.shared.ldap.model.filter.NotNode;
import org.apache.directory.shared.ldap.model.filter.OrNode;
import org.apache.directory.shared.ldap.model.filter.SearchScope;
import org.apache.directory.shared.ldap.model.filter.SubstringNode;
import org.apache.directory.shared.ldap.model.message.AliasDerefMode;
import org.apache.directory.shared.ldap.model.message.MessageTypeEnum;
import org.apache.directory.shared.ldap.model.message.SearchRequest;
import org.apache.directory.shared.ldap.model.message.SearchRequestImpl;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;


/**
 * DSML Decorator for SearchRequest
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchRequestDsml extends AbstractRequestDsml<SearchRequest>
{
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
}
