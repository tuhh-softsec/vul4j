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
package org.apache.directory.shared.ldap.codec.actions.intermediateResponse;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.util.OID;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.IntermediateResponseDecorator;
import org.apache.directory.shared.ldap.model.message.IntermediateResponse;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store a IntermediateResponse Name
 * <pre>
 * IntermediateResponse ::= [APPLICATION 25] SEQUENCE {
 *     responseName [0] LDAPOID OPTIONAL,
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoreIntermediateResponseName extends GrammarAction<LdapMessageContainer<IntermediateResponseDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( StoreIntermediateResponseName.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();


    /**
     * Instantiates a new response name action.
     */
    public StoreIntermediateResponseName()
    {
        super( "Store response name" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<IntermediateResponseDecorator> container ) throws DecoderException
    {
        // We can get the IntermediateResponse Object
        IntermediateResponse intermediateResponse = container.getMessage();

        // Get the Value and store it in the IntermediateResponse
        TLV tlv = container.getCurrentTLV();

        // We have to handle the special case of a 0 length matched
        // OID.
        if ( tlv.getLength() == 0 )
        {
            String msg = I18n.err( I18n.ERR_04095 );
            LOG.error( msg );
            // This will generate a PROTOCOL_ERROR
            throw new DecoderException( msg );
        }
        else
        {
            byte[] responseNameBytes = tlv.getValue().getData();

            String oidStr = Strings.utf8ToString(responseNameBytes);

            if ( OID.isOID( oidStr ) )
            {
                OID.isOID( oidStr );
                intermediateResponse.setResponseName( oidStr );
            }
            else
            {
                String msg = "The Intermediate Response name is not a valid OID : "
                    + Strings.utf8ToString(responseNameBytes) + " ("
                    + Strings.dumpBytes(responseNameBytes) + ") is invalid";
                LOG.error( "{} : {}", msg, oidStr );

                // Rethrow the exception, we will get a PROTOCOL_ERROR
                throw new DecoderException( msg );
            }
        }

        // We can have an END transition
        container.setGrammarEndAllowed( true );

        if ( IS_DEBUG )
        {
            LOG.debug( "OID read : {}", intermediateResponse.getResponseName() );
        }
    }
}
