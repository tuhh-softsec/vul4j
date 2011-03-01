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
package org.apache.directory.shared.ldap.codec.actions.modifyDnRequest;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.api.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.api.ResponseCarryingException;
import org.apache.directory.shared.ldap.codec.decorators.ModifyDnRequestDecorator;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.message.ModifyDnRequest;
import org.apache.directory.shared.ldap.model.message.ModifyDnResponseImpl;
import org.apache.directory.shared.ldap.model.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store the ModifyDnRequest entry name
 * <pre>
 * ModifyDNRequest ::= [APPLICATION 12] SEQUENCE { ...
 *     entry LDAPDN,
 *     ...
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoreModifyDnRequestEntryName extends GrammarAction<LdapMessageContainer<ModifyDnRequestDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( StoreModifyDnRequestEntryName.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /**
     * Instantiates a new action.
     */
    public StoreModifyDnRequestEntryName()
    {
        super( "Store ModifyDN request entry Name" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<ModifyDnRequestDecorator> container ) throws DecoderException
    {
        ModifyDnRequest modifyDnRequest = container.getMessage();

        // Get the Value and store it in the modifyDNRequest
        TLV tlv = container.getCurrentTLV();

        // We have to handle the special case of a 0 length matched
        // Dn
        Dn entry = null;

        if ( tlv.getLength() == 0 )
        {
            // This will generate a PROTOCOL_ERROR
            throw new DecoderException( I18n.err( I18n.ERR_04089 ) );
        }
        else
        {
            byte[] dnBytes = tlv.getValue().getData();
            String dnStr = Strings.utf8ToString(dnBytes);

            try
            {
                entry = new Dn( dnStr );
            }
            catch ( LdapInvalidDnException ine )
            {
                String msg = "Invalid Dn given : " + dnStr + " (" + Strings.dumpBytes(dnBytes)
                    + ") is invalid";
                LOG.error( "{} : {}", msg, ine.getMessage() );

                ModifyDnResponseImpl response = new ModifyDnResponseImpl( modifyDnRequest.getMessageId() );
                throw new ResponseCarryingException( msg, response, ResultCodeEnum.INVALID_DN_SYNTAX,
                    Dn.EMPTY_DN, ine );
            }

            modifyDnRequest.setName( entry );
        }

        if ( IS_DEBUG )
        {
            LOG.debug( "Modifying Dn {}", entry );
        }
    }
}
