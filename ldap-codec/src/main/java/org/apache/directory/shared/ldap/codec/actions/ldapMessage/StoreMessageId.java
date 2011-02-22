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
package org.apache.directory.shared.ldap.codec.actions.ldapMessage;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.IntegerDecoder;
import org.apache.directory.shared.asn1.ber.tlv.IntegerDecoderException;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.api.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.api.MessageDecorator;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store the LdapMessage MessageID.
 * <pre>
 * LDAPMessage --> ... MessageId ...
 *
 * Checks that MessageId is in [0 .. 2147483647] and store the value in
 * the LdapMessage Object
 *
 * (2147483647 = Integer.MAX_VALUE)
 * The next state will be MESSAGE_ID_STATE
 *
 * The message ID will be temporarily stored in the container, because we can't store it
 * into an object.
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoreMessageId extends GrammarAction<LdapMessageContainer<MessageDecorator<? extends Message>>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( StoreMessageId.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();


    /**
     * Instantiates a new action.
     */
    public StoreMessageId()
    {
        super( "Store MessageID" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<MessageDecorator<? extends Message>> container ) throws DecoderException
    {
        // The current TLV should be a integer
        // We get it and store it in MessageId
        TLV tlv = container.getCurrentTLV();

        // The Length should not be null
        if ( tlv.getLength() == 0 )
        {
            LOG.error( I18n.err( I18n.ERR_04068 ) );

            // This will generate a PROTOCOL_ERROR
            throw new DecoderException( I18n.err( I18n.ERR_04069 ) );
        }

        Value value = tlv.getValue();

        try
        {
            int messageId = IntegerDecoder.parse( value, 0, Integer.MAX_VALUE );

            container.setMessageId( messageId );

            if ( IS_DEBUG )
            {
                LOG.debug( "Ldap Message Id has been decoded : " + messageId );
            }
        }
        catch ( IntegerDecoderException ide )
        {
            LOG.error( I18n.err( I18n.ERR_04070, Strings.dumpBytes(value.getData()), ide
                .getLocalizedMessage() ) );

            // This will generate a PROTOCOL_ERROR
            throw new DecoderException( ide.getMessage() );
        }
    }
}
