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
package org.apache.directory.shared.ldap.codec.actions.abandonRequest;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.IntegerDecoder;
import org.apache.directory.shared.asn1.ber.tlv.IntegerDecoderException;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.api.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.AbandonRequestDecorator;
import org.apache.directory.shared.ldap.model.message.AbandonRequestImpl;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to initialize the AbandonRequest
 * <pre>
 * LdapMessage ::= ... AbandonRequest ...
 * AbandonRequest ::= [APPLICATION 16] MessageID
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InitAbandonRequest extends GrammarAction<LdapMessageContainer<AbandonRequestDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( InitAbandonRequest.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /**
     * Instantiates a new action.
     */
    public InitAbandonRequest()
    {
        super( "Init Abandon Request" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<AbandonRequestDecorator> container ) throws DecoderException
    {
        // Create the AbandonRequest LdapMessage instance and store it in the container
        AbandonRequestDecorator abandonRequest = new AbandonRequestDecorator(
            container.getLdapCodecService(), new AbandonRequestImpl( container.getMessageId() ) );
        container.setMessage( abandonRequest );

        // The current TLV should be a integer
        // We get it and store it in MessageId
        TLV tlv = container.getCurrentTLV();

        Value value = tlv.getValue();

        if ( ( value == null ) || ( value.getData() == null ) )
        {
            String msg = I18n.err( I18n.ERR_04075 );
            LOG.error( msg );

            // This will generate a PROTOCOL_ERROR
            throw new DecoderException( msg );
        }

        try
        {
            int abandonnedMessageId = IntegerDecoder.parse( value, 0, Integer.MAX_VALUE );

            abandonRequest.setAbandoned( abandonnedMessageId );

            if ( IS_DEBUG )
            {
                LOG
                    .debug( "AbandonMessage Id has been decoded : {}", Integer
                        .valueOf( abandonnedMessageId ) );
            }

            container.setGrammarEndAllowed( true );

            return;
        }
        catch ( IntegerDecoderException ide )
        {
            LOG.error( I18n
                .err( I18n.ERR_04076, Strings.dumpBytes(value.getData()), ide.getMessage() ) );

            // This will generate a PROTOCOL_ERROR
            throw new DecoderException( ide.getMessage() );
        }
    }
}
