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
package org.apache.directory.shared.ldap.codec.actions.controls;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.BooleanDecoder;
import org.apache.directory.shared.asn1.ber.tlv.BooleanDecoderException;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.MessageDecorator;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to set the control criticality flag
 * <pre>
 * Control ::= SEQUENCE {
 *     ...
 *     criticality BOOLEAN DEFAULT FALSE,
 *     ...
 * </pre>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoreControlCriticality extends GrammarAction<LdapMessageContainer<MessageDecorator<? extends Message>>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( StoreControlCriticality.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();


    /**
     * Instantiates a new StoreControlCriticality action.
     */
    public StoreControlCriticality()
    {
        super( "Store the control criticality" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<MessageDecorator<? extends Message>> container ) throws DecoderException
    {
        TLV tlv = container.getCurrentTLV();

        // Get the current control
        Control control = null;

        MessageDecorator<? extends Message> message = container.getMessage();
        control = message.getCurrentControl();

        // Store the criticality
        // We get the value. If it's a 0, it's a FALSE. If it's
        // a FF, it's a TRUE. Any other value should be an error,
        // but we could relax this constraint. So if we have
        // something
        // which is not 0, it will be interpreted as TRUE, but we
        // will generate a warning.
        Value value = tlv.getValue();

        try
        {
            control.setCritical( BooleanDecoder.parse( value ) );
        }
        catch ( BooleanDecoderException bde )
        {
            LOG.error( I18n
                .err( I18n.ERR_04100, Strings.dumpBytes(value.getData()), bde.getMessage() ) );

            // This will generate a PROTOCOL_ERROR
            throw new DecoderException( bde.getMessage() );
        }

        // We can have an END transition
        container.setGrammarEndAllowed( true );

        if ( IS_DEBUG )
        {
            LOG.debug( "Control criticality : " + control.isCritical() );
        }
    }
}
