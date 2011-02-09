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
package org.apache.directory.shared.ldap.codec.actions.modifyRequest;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.api.ResponseCarryingException;
import org.apache.directory.shared.ldap.codec.decorators.ModifyRequestDecorator;
import org.apache.directory.shared.ldap.model.message.ModifyRequest;
import org.apache.directory.shared.ldap.model.message.ModifyResponseImpl;
import org.apache.directory.shared.ldap.model.message.ResultCodeEnum;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store the ModificationRequest's attribute type
 * <pre>
 * ModifyRequest ::= [APPLICATION 6] SEQUENCE {
 *     ...
 *     modification SEQUENCE OF SEQUENCE {
 *             ...
 *         modification   AttributeTypeAndValues }
 *
 * AttributeTypeAndValues ::= SEQUENCE {
 *     type AttributeDescription,
 *     ...
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AddModifyRequestAttribute extends GrammarAction<LdapMessageContainer<ModifyRequestDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( AddModifyRequestAttribute.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /**
     * Instantiates a new action.
     */
    public AddModifyRequestAttribute()
    {
        super( "Store Modify request operation type" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<ModifyRequestDecorator> container ) throws DecoderException
    {
        ModifyRequestDecorator modifyRequestDecorator = container.getMessage();
        ModifyRequest modifyRequest = modifyRequestDecorator.getDecorated();

        TLV tlv = container.getCurrentTLV();

        // Store the value. It can't be null
        String type = null;

        if ( tlv.getLength() == 0 )
        {
            String msg = I18n.err( I18n.ERR_04083 );
            LOG.error( msg );

            ModifyResponseImpl response = new ModifyResponseImpl( modifyRequest.getMessageId() );
            throw new ResponseCarryingException( msg, response, ResultCodeEnum.INVALID_ATTRIBUTE_SYNTAX,
                modifyRequest.getName(), null );
        }
        else
        {
            type = Strings.utf8ToString( tlv.getValue().getData() );
            modifyRequestDecorator.addAttributeTypeAndValues( type );
        }

        if ( IS_DEBUG )
        {
            LOG.debug( "Modifying type : {}", type );
        }
    }
}
