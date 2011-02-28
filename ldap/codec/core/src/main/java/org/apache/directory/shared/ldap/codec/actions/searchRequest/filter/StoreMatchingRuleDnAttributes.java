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
import org.apache.directory.shared.asn1.ber.tlv.BooleanDecoder;
import org.apache.directory.shared.asn1.ber.tlv.BooleanDecoderException;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.api.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.SearchRequestDecorator;
import org.apache.directory.shared.ldap.codec.search.ExtensibleMatchFilter;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store a matchingRuleAssertion dnAttributes
 * <pre>
 * Filter ::= CHOICE {
 *     ...
 *     extensibleMatch  [9] MatchingRuleAssertion }
 *
 * MatchingRuleAssertion ::= SEQUENCE {
 *     ...
 *     dnAttributes [4] BOOLEAN DEFAULT FALSE }
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoreMatchingRuleDnAttributes extends GrammarAction<LdapMessageContainer<SearchRequestDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( StoreMatchingRuleDnAttributes.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();


    /**
     * Instantiates a new StoreMatchingRuleDnAttributes.
     */
    public StoreMatchingRuleDnAttributes()
    {
        super( "Store matchingRuleAssertion dnAttributes" );
    }


    public void action( LdapMessageContainer<SearchRequestDecorator> container ) throws DecoderException
    {
        SearchRequestDecorator searchRequest = container.getMessage();

        TLV tlv = container.getCurrentTLV();

        // Store the value.
        ExtensibleMatchFilter extensibleMatchFilter = ( ExtensibleMatchFilter ) searchRequest.getTerminalFilter();

        // We get the value. If it's a 0, it's a FALSE. If it's
        // a FF, it's a TRUE. Any other value should be an error,
        // but we could relax this constraint. So if we have
        // something
        // which is not 0, it will be interpreted as TRUE, but we
        // will generate a warning.
        Value value = tlv.getValue();

        try
        {
            extensibleMatchFilter.setDnAttributes( BooleanDecoder.parse( value ) );
        }
        catch ( BooleanDecoderException bde )
        {
            LOG.error( I18n
                .err( I18n.ERR_04110, Strings.dumpBytes(value.getData()), bde.getMessage() ) );

            throw new DecoderException( bde.getMessage() );
        }

        if ( IS_DEBUG )
        {
            LOG.debug( "Dn Attributes : {}", Boolean.valueOf( extensibleMatchFilter.isDnAttributes() ) );
        }

        // unstack the filters if needed
        searchRequest.unstackFilters( container );
    }
}
