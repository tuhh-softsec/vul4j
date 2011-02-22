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
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.api.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.api.ResponseCarryingException;
import org.apache.directory.shared.ldap.codec.decorators.BindRequestDecorator;
import org.apache.directory.shared.ldap.model.message.BindRequest;
import org.apache.directory.shared.ldap.model.message.BindResponseImpl;
import org.apache.directory.shared.ldap.model.message.ResultCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store the BindRequest version MessageID.
 * <pre>
 * BindRequest ::= [APPLICATION 0] SEQUENCE {
 *     ....
 *     authentication          AuthenticationChoice }
 *
 * AuthenticationChoice ::= CHOICE {
 *     ...
 *     sasl                  [3] SaslCredentials }
 *     ...
 *
 * We have to create an Authentication Object to store the credentials.
 * </pre>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InitSaslBind extends GrammarAction<LdapMessageContainer<BindRequestDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( InitSaslBind.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();


    /**
     * Instantiates a new action.
     */
    public InitSaslBind()
    {
        super( "Initialize Bind SASL Authentication" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<BindRequestDecorator> container ) throws DecoderException
    {
        BindRequest bindRequestMessage = container.getMessage();
        TLV tlv = container.getCurrentTLV();

        // We will check that the sasl is not null
        if ( tlv.getLength() == 0 )
        {
            String msg = I18n.err( I18n.ERR_04079 );
            LOG.error( msg );

            BindResponseImpl response = new BindResponseImpl( bindRequestMessage.getMessageId() );

            throw new ResponseCarryingException( msg, response, ResultCodeEnum.INVALID_CREDENTIALS,
                bindRequestMessage.getName(), null );
        }

        bindRequestMessage.setSimple( false );

        if ( IS_DEBUG )
        {
            LOG.debug( "The SaslCredential has been created" );
        }
    }
}
