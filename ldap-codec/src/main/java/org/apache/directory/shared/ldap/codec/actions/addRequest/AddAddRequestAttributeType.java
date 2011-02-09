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
package org.apache.directory.shared.ldap.codec.actions.addRequest;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.api.ResponseCarryingException;
import org.apache.directory.shared.ldap.codec.decorators.AddRequestDecorator;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.message.AddResponseImpl;
import org.apache.directory.shared.ldap.model.message.ResultCodeEnum;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to initialize the SearchResultEntry response
 * <pre>
 * AttributeList ::= SEQUENCE OF SEQUENCE {
 *     type    AttributeDescription,
 *     ...
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AddAddRequestAttributeType extends GrammarAction<LdapMessageContainer<AddRequestDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( AddAddRequestAttributeType.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /**
     * Instantiates a new action.
     */
    public AddAddRequestAttributeType()
    {
        super( "Store attribute type" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<AddRequestDecorator> container ) throws DecoderException
    {
        AddRequestDecorator addRequest = container.getMessage();

        TLV tlv = container.getCurrentTLV();

        // Store the type. It can't be null.
        if ( tlv.getLength() == 0 )
        {
            String msg = I18n.err( I18n.ERR_04086 );
            LOG.error( msg );

            AddResponseImpl response = new AddResponseImpl( addRequest.getMessageId() );

            throw new ResponseCarryingException( msg, response, ResultCodeEnum.INVALID_ATTRIBUTE_SYNTAX,
                addRequest.getEntry().getDn(), null );
        }

        String type = Strings.utf8ToString( tlv.getValue().getData() );

        try
        {
            addRequest.addAttributeType( type );
        }
        catch ( LdapException ne )
        {
            String msg = I18n.err( I18n.ERR_04087 );
            LOG.error( msg );

            AddResponseImpl response = new AddResponseImpl( addRequest.getMessageId() );
            throw new ResponseCarryingException( msg, response, ResultCodeEnum.INVALID_ATTRIBUTE_SYNTAX,
                addRequest.getEntry().getDn(), ne );
        }

        if ( IS_DEBUG )
        {
            LOG.debug( "Adding type {}", type );
        }
    }
}
