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
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.message.AddResponseImpl;
import org.apache.directory.shared.ldap.model.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store the AddReqyuest entry name
 * <pre>
 * AddRequest ::= [APPLICATION 8] SEQUENCE {
 *     entry           LDAPDN,
 *     ...
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoreAddRequestEntryName extends GrammarAction<LdapMessageContainer<AddRequestDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( StoreAddRequestEntryName.class );

    /**
     * Instantiates a new action.
     */
    public StoreAddRequestEntryName()
    {
        super( "Store Add request entry Name" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<AddRequestDecorator> container ) throws DecoderException
    {
        AddRequestDecorator addRequest = container.getMessage();

        TLV tlv = container.getCurrentTLV();

        // Store the entry. It can't be null
        if ( tlv.getLength() == 0 )
        {
            String msg = I18n.err( I18n.ERR_04085 );
            LOG.error( msg );

            AddResponseImpl response = new AddResponseImpl( addRequest.getMessageId() );

            // I guess that trying to add an entry which Dn is empty is a naming violation...
            // Not 100% sure though ...
            throw new ResponseCarryingException( msg, response, ResultCodeEnum.NAMING_VIOLATION,
                Dn.EMPTY_DN, null );
        }
        else
        {
            Dn entryDn = null;
            byte[] dnBytes = tlv.getValue().getData();
            String dnStr = Strings.utf8ToString(dnBytes);

            try
            {
                entryDn = new Dn( dnStr );
            }
            catch ( LdapInvalidDnException ine )
            {
                String msg = "Invalid Dn given : " + dnStr + " (" + Strings.dumpBytes(dnBytes)
                    + ") is invalid";
                LOG.error( "{} : {}", msg, ine.getMessage() );

                AddResponseImpl response = new AddResponseImpl( addRequest.getMessageId() );
                throw new ResponseCarryingException( msg, response, ResultCodeEnum.INVALID_DN_SYNTAX,
                    Dn.EMPTY_DN, ine );
            }

            addRequest.setEntryDn( entryDn );
        }

        LOG.debug( "Adding an entry with Dn : {}", addRequest.getEntry() );
    }
}
