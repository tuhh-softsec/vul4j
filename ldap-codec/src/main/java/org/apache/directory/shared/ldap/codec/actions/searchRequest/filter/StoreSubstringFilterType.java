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
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.api.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.SearchRequestDecorator;
import org.apache.directory.shared.ldap.codec.search.SubstringFilter;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store the Substring Filter type
 * <pre>
 * Filter ::= CHOICE {
 *     ...
 *     substrings  [4] SubstringFilter,
 *     ...
 *
 * SubstringFilter ::= SEQUENCE {
 *     type   AttributeDescription,
 *     ...
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoreSubstringFilterType extends GrammarAction<LdapMessageContainer<SearchRequestDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( StoreSubstringFilterType.class );

    /**
     * Instantiates a new action.
     */
    public StoreSubstringFilterType()
    {
        super( "Store Substring filter type" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<SearchRequestDecorator> container ) throws DecoderException
    {
        SearchRequestDecorator searchRequestDecorator = container.getMessage();

        TLV tlv = container.getCurrentTLV();

        // Store the value.
        SubstringFilter substringFilter = ( SubstringFilter ) searchRequestDecorator.getTerminalFilter();

        if ( tlv.getLength() == 0 )
        {
            String msg = I18n.err( I18n.ERR_04106 );
            LOG.error( msg );
            throw new DecoderException( msg );
        }
        else
        {
            String type = Strings.utf8ToString( tlv.getValue().getData() );
            substringFilter.setType( type );

            // We now have to get back to the nearest filter which
            // is not terminal.
            searchRequestDecorator.setTerminalFilter( substringFilter );
        }
    }
}
