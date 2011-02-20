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
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.ExtendedRequestDecorator;
import org.apache.directory.shared.ldap.model.message.ExtendedRequest;
import org.apache.directory.shared.util.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store the Extended Request value
 * <pre>
 * ExtendedRequest ::= [APPLICATION 23] SEQUENCE {
 *     ...
 *     requestValue  [1] OCTET STRING OPTIONAL }
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoreExtendedRequestValue extends GrammarAction<LdapMessageContainer<ExtendedRequestDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( StoreExtendedRequestValue.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /**
     * Instantiates a new action.
     */
    public StoreExtendedRequestValue()
    {
        super( "Store ExtendedRequest value" );
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
        // value
        if ( tlv.getLength() == 0 )
        {
            extendedRequest.setRequestValue( StringConstants.EMPTY_BYTES );
        }
        else
        {
            extendedRequest.setRequestValue( tlv.getValue().getData() );
        }

        // We can have an END transition
        container.setGrammarEndAllowed( true );

        if ( IS_DEBUG )
        {
            LOG.debug( "Extended value : {}", extendedRequest.getRequestValue() );
        }
    }
}
