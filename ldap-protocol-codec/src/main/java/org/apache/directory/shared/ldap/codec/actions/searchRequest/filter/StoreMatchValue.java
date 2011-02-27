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
package org.apache.directory.shared.ldap.codec.actions.searchRequest.filter;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.ldap.codec.api.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.SearchRequestDecorator;
import org.apache.directory.shared.ldap.codec.search.ExtensibleMatchFilter;
import org.apache.directory.shared.ldap.model.entry.BinaryValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store a match value
 * <pre>
 * Filter ::= CHOICE {
 *     ...
 *     extensibleMatch  [9] MatchingRuleAssertion }
 *
 * MatchingRuleAssertion ::= SEQUENCE {
 *     ...
 *     matchValue [3] AssertionValue,
 *     ...
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoreMatchValue extends GrammarAction<LdapMessageContainer<SearchRequestDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( StoreMatchValue.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();


    /**
     * Instantiates a new store match value action.
     */
    public StoreMatchValue()
    {
        super( "Store match Value" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<SearchRequestDecorator> container ) throws DecoderException
    {
        SearchRequestDecorator decorator = container.getMessage();

        TLV tlv = container.getCurrentTLV();

        // Store the value.
        ExtensibleMatchFilter extensibleMatchFilter = ( ExtensibleMatchFilter ) decorator.getTerminalFilter();

        byte[] value = tlv.getValue().getData();
        extensibleMatchFilter.setMatchValue( new BinaryValue( value ) );

        // unstack the filters if needed
        decorator.unstackFilters( container );

        if ( IS_DEBUG )
        {
            LOG.debug( "Stored a match value : {}", value );
        }
    }
}
