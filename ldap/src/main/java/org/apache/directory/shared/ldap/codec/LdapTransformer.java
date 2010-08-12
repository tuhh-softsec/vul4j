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
package org.apache.directory.shared.ldap.codec;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.bind.BindRequestCodec;
import org.apache.directory.shared.ldap.codec.bind.SaslCredentials;
import org.apache.directory.shared.ldap.codec.bind.SimpleAuthentication;
import org.apache.directory.shared.ldap.codec.extended.ExtendedRequestCodec;
import org.apache.directory.shared.ldap.codec.modify.ModifyRequestCodec;
import org.apache.directory.shared.ldap.codec.modifyDn.ModifyDNRequestCodec;
import org.apache.directory.shared.ldap.codec.search.AndFilter;
import org.apache.directory.shared.ldap.codec.search.AttributeValueAssertionFilter;
import org.apache.directory.shared.ldap.codec.search.ConnectorFilter;
import org.apache.directory.shared.ldap.codec.search.ExtensibleMatchFilter;
import org.apache.directory.shared.ldap.codec.search.Filter;
import org.apache.directory.shared.ldap.codec.search.NotFilter;
import org.apache.directory.shared.ldap.codec.search.OrFilter;
import org.apache.directory.shared.ldap.codec.search.PresentFilter;
import org.apache.directory.shared.ldap.codec.search.SearchRequestCodec;
import org.apache.directory.shared.ldap.codec.search.SearchResultEntryCodec;
import org.apache.directory.shared.ldap.codec.search.SearchResultReferenceCodec;
import org.apache.directory.shared.ldap.codec.search.SubstringFilter;
import org.apache.directory.shared.ldap.codec.util.LdapURLEncodingException;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.apache.directory.shared.ldap.entry.Modification;
import org.apache.directory.shared.ldap.entry.Value;
import org.apache.directory.shared.ldap.filter.AndNode;
import org.apache.directory.shared.ldap.filter.ApproximateNode;
import org.apache.directory.shared.ldap.filter.BranchNode;
import org.apache.directory.shared.ldap.filter.EqualityNode;
import org.apache.directory.shared.ldap.filter.ExprNode;
import org.apache.directory.shared.ldap.filter.ExtensibleNode;
import org.apache.directory.shared.ldap.filter.GreaterEqNode;
import org.apache.directory.shared.ldap.filter.LeafNode;
import org.apache.directory.shared.ldap.filter.LessEqNode;
import org.apache.directory.shared.ldap.filter.NotNode;
import org.apache.directory.shared.ldap.filter.OrNode;
import org.apache.directory.shared.ldap.filter.PresenceNode;
import org.apache.directory.shared.ldap.filter.SimpleNode;
import org.apache.directory.shared.ldap.filter.SubstringNode;
import org.apache.directory.shared.ldap.message.AliasDerefMode;
import org.apache.directory.shared.ldap.message.BindRequestImpl;
import org.apache.directory.shared.ldap.message.ExtendedRequestImpl;
import org.apache.directory.shared.ldap.message.LdapResultImpl;
import org.apache.directory.shared.ldap.message.ModifyDnRequestImpl;
import org.apache.directory.shared.ldap.message.ModifyRequestImpl;
import org.apache.directory.shared.ldap.message.ReferralImpl;
import org.apache.directory.shared.ldap.message.SearchRequestImpl;
import org.apache.directory.shared.ldap.message.SearchResultEntryImpl;
import org.apache.directory.shared.ldap.message.SearchResultReferenceImpl;
import org.apache.directory.shared.ldap.message.control.Control;
import org.apache.directory.shared.ldap.message.extended.GracefulShutdownRequest;
import org.apache.directory.shared.ldap.message.internal.InternalMessage;
import org.apache.directory.shared.ldap.message.internal.InternalReferral;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.util.LdapURL;
import org.apache.directory.shared.ldap.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A Codec to Internal Message transformer.
 * 
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory Project</a>
 */
public class LdapTransformer
{
    /** The logger */
    private static Logger LOG = LoggerFactory.getLogger( LdapTransformer.class );

    /** A speedup for logger */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();


    /**
     * Transform an ExtendedRequest message from a CodecMessage to a
     * InternalMessage
     * 
     * @param extendedRequest The message to transform
     * @param messageId The message Id
     * @return A Internal ExtendedRequestImpl
     */
    private static InternalMessage transformExtendedRequest( ExtendedRequestCodec extendedRequest, int messageId )
    {
        ExtendedRequestImpl internalMessage;

        if ( extendedRequest.getRequestName().equals( GracefulShutdownRequest.EXTENSION_OID ) )
        {
            internalMessage = new GracefulShutdownRequest( messageId );
        }
        else
        {
            internalMessage = new ExtendedRequestImpl( messageId );
        }

        // Codec : OID requestName -> Internal : String oid
        internalMessage.setID( extendedRequest.getRequestName() );

        // Codec : OctetString requestValue -> Internal : byte [] payload
        internalMessage.setEncodedValue( extendedRequest.getRequestValue() );

        return internalMessage;
    }


    /**
     * Transform a ModifyDNRequest message from a CodecMessage to a
     * InternalMessage
     * 
     * @param modifyDNRequest The message to transform
     * @param messageId The message Id
     * @return A Internal ModifyDNRequestImpl
     */
    private static InternalMessage transformModifyDNRequest( ModifyDNRequestCodec modifyDNRequest, int messageId )
    {
        ModifyDnRequestImpl internalMessage = new ModifyDnRequestImpl( messageId );

        // Codec : DN entry -> Internal : DN m_name
        internalMessage.setName( modifyDNRequest.getEntry() );

        // Codec : RelativeDN newRDN -> Internal : DN m_newRdn
        internalMessage.setNewRdn( modifyDNRequest.getNewRDN() );

        // Codec : boolean deleteOldRDN -> Internal : boolean m_deleteOldRdn
        internalMessage.setDeleteOldRdn( modifyDNRequest.isDeleteOldRDN() );

        // Codec : DN newSuperior -> Internal : DN m_newSuperior
        internalMessage.setNewSuperior( modifyDNRequest.getNewSuperior() );

        return internalMessage;
    }


    /**
     * Transform a ModifyRequest message from a CodecMessage to a InternalMessage
     * 
     * @param modifyRequest The message to transform
     * @param messageId The message Id
     * @return A Internal ModifyRequestImpl
     */
    private static InternalMessage transformModifyRequest( ModifyRequestCodec modifyRequest, int messageId )
    {
        ModifyRequestImpl internalMessage = new ModifyRequestImpl( messageId );

        // Codec : DN object -> Internal : String name
        internalMessage.setName( modifyRequest.getObject() );

        // Codec : ArrayList modifications -> Internal : ArrayList mods
        if ( modifyRequest.getModifications() != null )
        {
            // Loop through the modifications
            for ( Modification modification : modifyRequest.getModifications() )
            {
                internalMessage.addModification( modification );
            }
        }

        return internalMessage;
    }


    /**
     * Transform the Filter part of a SearchRequest to an ExprNode
     * 
     * @param codecFilter The filter to be transformed
     * @return An ExprNode
     */
    private static ExprNode transformFilter( Filter codecFilter )
    {
        if ( codecFilter != null )
        {
            // Transform OR, AND or NOT leaves
            if ( codecFilter instanceof ConnectorFilter )
            {
                BranchNode branch = null;

                if ( codecFilter instanceof AndFilter )
                {
                    branch = new AndNode();
                }
                else if ( codecFilter instanceof OrFilter )
                {
                    branch = new OrNode();
                }
                else if ( codecFilter instanceof NotFilter )
                {
                    branch = new NotNode();
                }

                List<Filter> filtersSet = ( ( ConnectorFilter ) codecFilter ).getFilterSet();

                // Loop on all AND/OR children
                if ( filtersSet != null )
                {
                    for ( Filter filter : filtersSet )
                    {
                        branch.addNode( transformFilter( filter ) );
                    }
                }

                return branch;
            }
            else
            {
                // Transform PRESENT or ATTRIBUTE_VALUE_ASSERTION
                LeafNode branch = null;

                if ( codecFilter instanceof PresentFilter )
                {
                    branch = new PresenceNode( ( ( PresentFilter ) codecFilter ).getAttributeDescription() );
                }
                else if ( codecFilter instanceof AttributeValueAssertionFilter )
                {
                    AttributeValueAssertion ava = ( ( AttributeValueAssertionFilter ) codecFilter ).getAssertion();

                    // Transform =, >=, <=, ~= filters
                    switch ( ( ( AttributeValueAssertionFilter ) codecFilter ).getFilterType() )
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
                else if ( codecFilter instanceof SubstringFilter )
                {
                    // Transform Substring filters
                    SubstringFilter filter = ( SubstringFilter ) codecFilter;
                    String initialString = null;
                    String finalString = null;
                    List<String> anyString = null;

                    if ( filter.getInitialSubstrings() != null )
                    {
                        initialString = filter.getInitialSubstrings();
                    }

                    if ( filter.getFinalSubstrings() != null )
                    {
                        finalString = filter.getFinalSubstrings();
                    }

                    if ( filter.getAnySubstrings() != null )
                    {
                        anyString = new ArrayList<String>();

                        for ( String any : filter.getAnySubstrings() )
                        {
                            anyString.add( any );
                        }
                    }

                    branch = new SubstringNode( anyString, filter.getType(), initialString, finalString );
                }
                else if ( codecFilter instanceof ExtensibleMatchFilter )
                {
                    // Transform Extensible Match Filter
                    ExtensibleMatchFilter filter = ( ExtensibleMatchFilter ) codecFilter;
                    String matchingRule = null;

                    Value<?> value = filter.getMatchValue();

                    if ( filter.getMatchingRule() != null )
                    {
                        matchingRule = filter.getMatchingRule();
                    }

                    branch = new ExtensibleNode( filter.getType(), value, matchingRule, filter.isDnAttributes() );
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
     * Transform an ExprNode filter to a CodecFilter
     * 
     * @param exprNode The filter to be transformed
     * @return A Codec filter
     */
    public static Filter transformFilter( SchemaManager schemaManager, ExprNode exprNode )
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

                List<ExprNode> children = ( ( BranchNode ) exprNode ).getChildren();

                // Loop on all AND/OR children
                if ( children != null )
                {
                    for ( ExprNode child : children )
                    {
                        try
                        {
                            ( ( ConnectorFilter ) filter ).addFilter( transformFilter( schemaManager, child ) );
                        }
                        catch ( DecoderException de )
                        {
                            LOG.error( I18n.err( I18n.ERR_04112, de.getLocalizedMessage() ) );
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
                        assertion.setAttributeDesc( ( ( ApproximateNode<?> ) exprNode ).getAttribute() );
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
     * Transform a SearchRequest message from a CodecMessage to a InternalMessage
     * 
     * @param searchRequest The message to transform
     * @param messageId The message Id
     * @return A Internal SearchRequestImpl
     */
    private static InternalMessage transformSearchRequest( SearchRequestCodec searchRequest, int messageId )
    {
        SearchRequestImpl internalMessage = new SearchRequestImpl( messageId );

        // Codec : DN baseObject -> Internal : String baseDn
        internalMessage.setBase( searchRequest.getBaseObject() );

        // Codec : int scope -> Internal : ScopeEnum scope
        internalMessage.setScope( searchRequest.getScope() );

        // Codec : int derefAliases -> Internal : AliasDerefMode derefAliases
        switch ( searchRequest.getDerefAliases() )
        {
            case LdapConstants.DEREF_ALWAYS:
                internalMessage.setDerefAliases( AliasDerefMode.DEREF_ALWAYS );
                break;

            case LdapConstants.DEREF_FINDING_BASE_OBJ:
                internalMessage.setDerefAliases( AliasDerefMode.DEREF_FINDING_BASE_OBJ );
                break;

            case LdapConstants.DEREF_IN_SEARCHING:
                internalMessage.setDerefAliases( AliasDerefMode.DEREF_IN_SEARCHING );
                break;

            case LdapConstants.NEVER_DEREF_ALIASES:
                internalMessage.setDerefAliases( AliasDerefMode.NEVER_DEREF_ALIASES );
                break;
        }

        // Codec : int sizeLimit -> Internal : int sizeLimit
        internalMessage.setSizeLimit( searchRequest.getSizeLimit() );

        // Codec : int timeLimit -> Internal : int timeLimit
        internalMessage.setTimeLimit( searchRequest.getTimeLimit() );

        // Codec : boolean typesOnly -> Internal : boolean typesOnly
        internalMessage.setTypesOnly( searchRequest.isTypesOnly() );

        // Codec : Filter filter -> Internal : ExprNode filter
        Filter codecFilter = searchRequest.getFilter();

        internalMessage.setFilter( transformFilter( codecFilter ) );

        // Codec : ArrayList attributes -> Internal : ArrayList attributes
        if ( searchRequest.getAttributes() != null )
        {
            List<EntryAttribute> attributes = searchRequest.getAttributes();

            if ( ( attributes != null ) && ( attributes.size() != 0 ) )
            {
                for ( EntryAttribute attribute : attributes )
                {
                    if ( attribute != null )
                    {
                        internalMessage.addAttribute( attribute.getId() );
                    }
                }
            }
        }

        return internalMessage;
    }


    /**
     * Transform the Codec message to a internal message.
     * 
     * @param obj the object to transform
     * @return the object transformed
     */
    public static InternalMessage transform( Object obj )
    {
        if ( obj instanceof InternalMessage )
        {
            return ( InternalMessage ) obj;
        }

        LdapMessageCodec codecMessage = ( LdapMessageCodec ) obj;
        int messageId = codecMessage.getMessageId();

        if ( IS_DEBUG )
        {
            LOG.debug( "Transforming LdapMessage <" + messageId + ", " + codecMessage.getMessageTypeName()
                + "> from Codec to nternal." );
        }

        InternalMessage internalMessage = null;

        MessageTypeEnum messageType = codecMessage.getMessageType();

        switch ( messageType )
        {
            case SEARCH_REQUEST:
                internalMessage = transformSearchRequest( ( SearchRequestCodec ) codecMessage, messageId );
                break;

            case MODIFY_REQUEST:
                internalMessage = transformModifyRequest( ( ModifyRequestCodec ) codecMessage, messageId );
                break;

            case MODIFYDN_REQUEST:
                internalMessage = transformModifyDNRequest( ( ModifyDNRequestCodec ) codecMessage, messageId );
                break;

            case EXTENDED_REQUEST:
                internalMessage = transformExtendedRequest( ( ExtendedRequestCodec ) codecMessage, messageId );
                break;

            case SEARCH_RESULT_ENTRY:
            case SEARCH_RESULT_DONE:
            case SEARCH_RESULT_REFERENCE:
            case MODIFY_RESPONSE:
            case ADD_RESPONSE:
            case DEL_RESPONSE:
            case MODIFYDN_RESPONSE:
            case COMPARE_RESPONSE:
            case EXTENDED_RESPONSE:
            case INTERMEDIATE_RESPONSE:
                // Nothing to do !
                break;

            default:
                throw new IllegalStateException( I18n.err( I18n.ERR_04113 ) );
        }

        // Transform the controls, too
        transformControlsCodecToInternal( codecMessage, internalMessage );

        return internalMessage;
    }


    /**
     * Transform a Ldapresult part of a Internal Response to a Codec LdapResult
     * 
     * @param InternalLdapResult the Internal LdapResult to transform
     * @return A Codec LdapResult
     */
    private static LdapResultCodec transformLdapResult( LdapResultImpl internalLdapResult )
    {
        LdapResultCodec codecLdapResult = new LdapResultCodec();

        // Internal : ResultCodeEnum resultCode -> Codec : int resultCode
        codecLdapResult.setResultCode( internalLdapResult.getResultCode() );

        // Internal : String errorMessage -> Codec : LdapString errorMessage
        String errorMessage = internalLdapResult.getErrorMessage();

        codecLdapResult.setErrorMessage( StringTools.isEmpty( errorMessage ) ? "" : errorMessage );

        // Internal : String matchedDn -> Codec : DN matchedDN
        codecLdapResult.setMatchedDN( internalLdapResult.getMatchedDn() );

        // Internal : Referral referral -> Codec : ArrayList referrals
        ReferralImpl internalReferrals = ( ReferralImpl ) internalLdapResult.getReferral();

        if ( internalReferrals != null )
        {
            codecLdapResult.initReferrals();

            for ( String referral : internalReferrals.getLdapUrls() )
            {
                try
                {
                    LdapURL ldapUrl = new LdapURL( referral.getBytes() );
                    codecLdapResult.addReferral( ldapUrl );
                }
                catch ( LdapURLEncodingException lude )
                {
                    LOG.warn( "The referral " + referral + " is invalid : " + lude.getMessage() );
                    codecLdapResult.addReferral( LdapURL.EMPTY_URL );
                }
            }
        }

        return codecLdapResult;
    }


    /**
     * Transform a Internal BindRequest to a Codec BindRequest
     * 
     * @param internalMessage The incoming Internal BindRequest
     * @return The BindRequestCodec instance
     */
    private static LdapMessageCodec transformBindRequest( InternalMessage internalMessage )
    {
        BindRequestImpl internalBindRequest = ( BindRequestImpl ) internalMessage;

        BindRequestCodec bindRequest = new BindRequestCodec();

        if ( internalBindRequest.isSimple() )
        {
            SimpleAuthentication simple = new SimpleAuthentication();
            simple.setSimple( internalBindRequest.getCredentials() );
            bindRequest.setAuthentication( simple );
        }
        else
        {
            SaslCredentials sasl = new SaslCredentials();
            sasl.setCredentials( internalBindRequest.getCredentials() );
            sasl.setMechanism( internalBindRequest.getSaslMechanism() );
            bindRequest.setAuthentication( sasl );
        }

        bindRequest.setMessageId( internalBindRequest.getMessageId() );
        bindRequest.setName( internalBindRequest.getName() );
        bindRequest.setVersion( internalBindRequest.isVersion3() ? 3 : 2 );

        return bindRequest;
    }


    /**
     * Transform a Internal SearchResponseEntry to a Codec SearchResultEntry
     * 
     * @param internalMessage The incoming Internal SearchResponseEntry
     */
    private static LdapMessageCodec transformSearchResultEntry( InternalMessage internalMessage )
    {
        SearchResultEntryImpl internalSearchResultResponse = ( SearchResultEntryImpl ) internalMessage;
        SearchResultEntryCodec searchResultEntry = new SearchResultEntryCodec();

        // Internal : DN dn -> Codec : DN objectName
        searchResultEntry.setObjectName( internalSearchResultResponse.getObjectName() );

        // Internal : Attributes attributes -> Codec : ArrayList
        // partialAttributeList
        searchResultEntry.setEntry( internalSearchResultResponse.getEntry() );

        return searchResultEntry;
    }


    /**
     * Transform a Internal SearchResponseReference to a Codec
     * SearchResultReference
     * 
     * @param internalMessage The incoming Internal SearchResponseReference
     */
    private static LdapMessageCodec transformSearchResultReference( InternalMessage internalMessage )
    {
        SearchResultReferenceImpl internalSearchResponseReference = ( SearchResultReferenceImpl ) internalMessage;
        SearchResultReferenceCodec searchResultReference = new SearchResultReferenceCodec();

        // Internal : Referral m_referral -> Codec: ArrayList
        // searchResultReferences
        InternalReferral referrals = internalSearchResponseReference.getReferral();

        // Loop on all referals
        if ( referrals != null )
        {
            Collection<String> urls = referrals.getLdapUrls();

            if ( urls != null )
            {
                for ( String url : urls )
                {
                    try
                    {
                        searchResultReference.addSearchResultReference( new LdapURL( url ) );
                    }
                    catch ( LdapURLEncodingException luee )
                    {
                        LOG.warn( "The LdapURL " + url + " is incorrect : " + luee.getMessage() );
                    }
                }
            }
        }

        return searchResultReference;
    }


    /**
     * Transform the internal message to a codec message.
     * 
     * @param msg the message to transform
     * @return the msg transformed
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "NP_NULL_ON_SOME_PATH", justification = "The number of Ldap Message we are dealing with is finite, and we won't ever have to deal with any other unexpected one")
    public static Object transform( InternalMessage msg )
    {
        if ( IS_DEBUG )
        {
            LOG.debug( "Transforming message type " + msg.getType() );
        }

        LdapMessageCodec codecMessage = null;

        switch ( msg.getType() )
        {
            case SEARCH_RESULT_ENTRY:
                codecMessage = transformSearchResultEntry( msg );
                break;

            case SEARCH_RESULT_REFERENCE:
                codecMessage = transformSearchResultReference( msg );
                break;

            case BIND_REQUEST:
                codecMessage = transformBindRequest( msg );
                break;
        }

        codecMessage.setMessageId( msg.getMessageId() );

        // We also have to transform the controls...
        if ( !msg.getControls().isEmpty() )
        {
            transformControlsInternalToCodec( codecMessage, msg );
        }

        if ( IS_DEBUG )
        {
            LOG.debug( "Transformed message : " + codecMessage );
        }

        return codecMessage;
    }


    /**
     * Copy the codec controls into the internal message
     *
     * @param codecMessage the Codec message
     * @param msg the Internal message
     */
    private static void transformControlsCodecToInternal( LdapMessageCodec codecMessage, InternalMessage internalMessage )
    {
        if ( codecMessage.getControls() == null )
        {
            return;
        }

        for ( final Control codecControl : codecMessage.getControls() )
        {
            internalMessage.add( codecControl );
        }
    }


    /**
     * Transforms the controls
     * @param codecMessage The Codec SearchResultReference to produce
     * @param msg The incoming Internal Message
     */
    private static void transformControlsInternalToCodec( LdapMessageCodec codecMessage, InternalMessage internalMessage )
    {
        if ( internalMessage.getControls() == null )
        {
            return;
        }

        for ( Control control : internalMessage.getControls().values() )
        {
            codecMessage.addControl( control );
        }
    }
}
