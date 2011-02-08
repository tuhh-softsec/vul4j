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
package org.apache.directory.shared.ldap.codec.actions.bindRequest;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.BindRequestDecorator;
import org.apache.directory.shared.ldap.model.message.BindRequest;
import org.apache.directory.shared.util.StringConstants;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store the BindRequest version MessageID.
 * <pre>
 * SaslCredentials ::= SEQUENCE {
 *     mechanism   LDAPSTRING,
 *     ...
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoreSaslCredentials extends GrammarAction<LdapMessageContainer<BindRequestDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( StoreSaslCredentials.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();


    /**
     * Instantiates a new action.
     */
    public StoreSaslCredentials()
    {
        super( "Store SASL mechanism" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<BindRequestDecorator> container ) throws DecoderException
    {
        BindRequest bindRequestMessage = container.getMessage();

        // Get the Value and store it in the BindRequest
        TLV tlv = container.getCurrentTLV();

        // We have to handle the special case of a 0 length
        // credentials
        if ( tlv.getLength() == 0 )
        {
            bindRequestMessage.setCredentials( StringConstants.EMPTY_BYTES );
        }
        else
        {
            bindRequestMessage.setCredentials( tlv.getValue().getData() );
        }

        // We can have an END transition
        container.setGrammarEndAllowed( true );

        if ( IS_DEBUG )
        {
            LOG.debug( "The credentials are : {}", Strings.dumpBytes(bindRequestMessage
                    .getCredentials()) );
        }
    }
}
