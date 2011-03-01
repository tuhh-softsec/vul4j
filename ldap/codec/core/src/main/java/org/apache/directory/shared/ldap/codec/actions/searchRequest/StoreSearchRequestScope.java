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
package org.apache.directory.shared.ldap.codec.actions.searchRequest;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.IntegerDecoder;
import org.apache.directory.shared.asn1.ber.tlv.IntegerDecoderException;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.api.LdapConstants;
import org.apache.directory.shared.ldap.codec.api.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.SearchRequestDecorator;
import org.apache.directory.shared.ldap.model.filter.SearchScope;
import org.apache.directory.shared.ldap.model.message.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store the SearchRequest scope
 * <pre>
 * SearchRequest ::= [APPLICATION 3] SEQUENCE {
 *     ...
 *     scope ENUMERATED {
 *         baseObject   (0),
 *         singleLevel  (1),
 *         wholeSubtree (2) },
 *     ...
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoreSearchRequestScope extends GrammarAction<LdapMessageContainer<SearchRequestDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( StoreSearchRequestScope.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /**
     * Instantiates a new action.
     */
    public StoreSearchRequestScope()
    {
        super( "Store SearchRequest scope" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<SearchRequestDecorator> container ) throws DecoderException
    {
        SearchRequest searchRequest = container.getMessage().getDecorated();

        TLV tlv = container.getCurrentTLV();

        // We have to check that this is a correct scope
        Value value = tlv.getValue();
        int scope = 0;

        try
        {
            scope = IntegerDecoder.parse( value, LdapConstants.SCOPE_BASE_OBJECT,
                LdapConstants.SCOPE_WHOLE_SUBTREE );
        }
        catch ( IntegerDecoderException ide )
        {
            String msg = I18n.err( I18n.ERR_04101, value.toString() );
            LOG.error( msg );
            throw new DecoderException( msg );
        }

        searchRequest.setScope( SearchScope.getSearchScope(scope) );

        if ( IS_DEBUG )
        {
            switch ( scope )
            {
                case LdapConstants.SCOPE_BASE_OBJECT:
                    LOG.debug( "Searching within BASE_OBJECT scope " );
                    break;

                case LdapConstants.SCOPE_SINGLE_LEVEL:
                    LOG.debug( "Searching within SINGLE_LEVEL scope " );
                    break;

                case LdapConstants.SCOPE_WHOLE_SUBTREE:
                    LOG.debug( "Searching within WHOLE_SUBTREE scope " );
                    break;
            }
        }
    }
}
