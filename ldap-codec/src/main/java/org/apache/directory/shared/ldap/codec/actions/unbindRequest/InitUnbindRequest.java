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
package org.apache.directory.shared.ldap.codec.actions.unbindRequest;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.UnbindRequestDecorator;
import org.apache.directory.shared.ldap.model.message.UnbindRequestImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store the LdapMessage MessageID.
 * <pre>
 * LdapMessage ::= ... UnBindRequest ...
 * unbindRequest ::= [APPLICATION 2] NULL
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InitUnbindRequest extends GrammarAction<LdapMessageContainer<UnbindRequestDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( InitUnbindRequest.class );

    /**
     * Instantiates a new action.
     */
    public InitUnbindRequest()
    {
        super( "Unbind Request initialization" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<UnbindRequestDecorator> container ) throws DecoderException
    {
        // Create the UnbindRequest LdapMessage instance and store it in the container
        UnbindRequestDecorator unbindRequest = new UnbindRequestDecorator(
            container.getLdapCodecService(), new UnbindRequestImpl( container.getMessageId() ) );
        container.setMessage( unbindRequest );

        TLV tlv = container.getCurrentTLV();
        int expectedLength = tlv.getLength();

        // The Length should be null
        if ( expectedLength != 0 )
        {
            LOG.error( I18n.err( I18n.ERR_04071, Integer.valueOf( expectedLength ) ) );

            // This will generate a PROTOCOL_ERROR
            throw new DecoderException( I18n.err( I18n.ERR_04072 ) );
        }

        // We can quit now
        container.setGrammarEndAllowed( true );
    }
}
