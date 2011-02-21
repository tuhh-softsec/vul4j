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
package org.apache.directory.shared.ldap.codec.actions.extendedRequest;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.util.OID;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.api.ExtendedRequestDecorator;
import org.apache.directory.shared.ldap.model.message.ExtendedRequest;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store the Extended Request name
 * <pre>
 * ExtendedRequest ::= [APPLICATION 23] SEQUENCE {
 *     requestName [0] LDAPOID,
 *     ...
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoreExtendedRequestName extends GrammarAction<LdapMessageContainer<ExtendedRequestDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( StoreExtendedRequestName.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /**
     * Instantiates a new action.
     */
    public StoreExtendedRequestName()
    {
        super( "Store ExtendedRequest Name" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<ExtendedRequestDecorator> container ) throws DecoderException
    {
        // We can allocate the ExtendedRequest Object
        ExtendedRequest extendedRequest = container.getMessage();

        // Get the Value and store it in the ExtendedRequest
        TLV tlv = container.getCurrentTLV();

        // We have to handle the special case of a 0 length matched
        // OID
        if ( tlv.getLength() == 0 )
        {
            String msg = I18n.err( I18n.ERR_04095 );
            LOG.error( msg );
            // This will generate a PROTOCOL_ERROR
            throw new DecoderException( msg );
        }
        else
        {
            byte[] requestNameBytes = tlv.getValue().getData();

            try
            {
                String requestName = Strings.utf8ToString(requestNameBytes);

                if ( !OID.isOID( requestName ) )
                {

                    String msg = "The Request name is not a valid OID : "
                        + Strings.utf8ToString(requestNameBytes) + " ("
                        + Strings.dumpBytes(requestNameBytes) + ") is invalid";
                    LOG.error( msg );

                    // throw an exception, we will get a PROTOCOL_ERROR
                    throw new DecoderException( msg );
                }

                extendedRequest.setRequestName( requestName );
            }
            catch ( DecoderException de )
            {
                String msg = "The Request name is not a valid OID : "
                    + Strings.utf8ToString(requestNameBytes) + " ("
                    + Strings.dumpBytes(requestNameBytes) + ") is invalid";
                LOG.error( "{} : {}", msg, de.getMessage() );

                // Rethrow the exception, we will get a PROTOCOL_ERROR
                throw de;
            }
        }

        // We can have an END transition
        container.setGrammarEndAllowed( true );

        if ( IS_DEBUG )
        {
            LOG.debug( "OID read : {}", extendedRequest.getRequestName() );
        }
    }
}
