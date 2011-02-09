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
package org.apache.directory.shared.ldap.codec.actions.searchResultEntry;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.SearchResultEntryDecorator;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to initialize the SearchResultEntry response
 * <pre>
 * LdapMessage ::= ... SearchResultEntry ...
 * SearchResultEntry ::= [APPLICATION 4] SEQUENCE { ...
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoreSearchResultEntryObjectName extends GrammarAction<LdapMessageContainer<SearchResultEntryDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( StoreSearchResultEntryObjectName.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /**
     * Instantiates a new action.
     */
    public StoreSearchResultEntryObjectName()
    {
        super( "Init SearchResultEntry" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<SearchResultEntryDecorator> container ) throws DecoderException
    {
        SearchResultEntryDecorator searchResultEntry = container.getMessage();

        TLV tlv = container.getCurrentTLV();

        Dn objectName = Dn.EMPTY_DN;

        // Store the value.
        if ( tlv.getLength() == 0 )
        {
            searchResultEntry.setObjectName( objectName );
        }
        else
        {
            byte[] dnBytes = tlv.getValue().getData();
            String dnStr = Strings.utf8ToString(dnBytes);

            try
            {
                objectName = new Dn( dnStr );
            }
            catch ( LdapInvalidDnException ine )
            {
                // This is for the client side. We will never decode LdapResult on the server
                String msg = "The Dn " + Strings.dumpBytes(dnBytes) + "is invalid : "
                    + ine.getMessage();
                LOG.error( "{} : {}", msg, ine.getMessage() );
                throw new DecoderException( msg, ine );
            }

            searchResultEntry.setObjectName( objectName );
        }

        if ( IS_DEBUG )
        {
            LOG.debug( "Search Result Entry Dn found : {}", searchResultEntry.getObjectName() );
        }
    }
}
