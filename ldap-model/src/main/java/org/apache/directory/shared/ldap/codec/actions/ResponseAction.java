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
package org.apache.directory.shared.ldap.codec.actions;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.ExtendedResponseDecorator;
import org.apache.directory.shared.ldap.model.message.ExtendedResponse;
import org.apache.directory.shared.util.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store a Response to an ExtendedResponse
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ResponseAction extends GrammarAction<LdapMessageContainer<ExtendedResponseDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( ResponseAction.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();


    /**
     * Instantiates a new response action.
     */
    public ResponseAction()
    {
        super( "Store response" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<ExtendedResponseDecorator> container ) throws DecoderException
    {
        // We can allocate the ExtendedResponse Object
        ExtendedResponse extendedResponse = container.getMessage();

        // Get the Value and store it in the ExtendedResponse
        TLV tlv = container.getCurrentTLV();

        // We have to handle the special case of a 0 length matched
        // OID
        if ( tlv.getLength() == 0 )
        {
            extendedResponse.setResponseValue( StringConstants.EMPTY_BYTES );
        }
        else
        {
            extendedResponse.setResponseValue( tlv.getValue().getData() );
        }

        // We can have an END transition
        container.setGrammarEndAllowed( true );

        if ( IS_DEBUG )
        {
            LOG.debug( "Extended value : {}", extendedResponse.getResponseValue() );
        }
    }
}
