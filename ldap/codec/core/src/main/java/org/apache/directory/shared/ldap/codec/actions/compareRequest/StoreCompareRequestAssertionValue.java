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
package org.apache.directory.shared.ldap.codec.actions.compareRequest;


import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.ldap.codec.api.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.CompareRequestDecorator;
import org.apache.directory.shared.ldap.model.message.CompareRequest;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store the AssertionValue in a Compare Request
 * <pre>
 * CompareRequest ::= [APPLICATION 14] SEQUENCE {
 *     ...
 *     ava AttributeValueAssertion }
 *
 * AttributeValueAssertion ::= SEQUENCE {
 *     ...
 *     assertionValue AssertionValue }
 *
 * AssertionValue OCTET STRING
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoreCompareRequestAssertionValue extends GrammarAction<LdapMessageContainer<CompareRequestDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( StoreCompareRequestAssertionValue.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /**
     * Instantiates a new action.
     */
    public StoreCompareRequestAssertionValue()
    {
        super( "Store CompareRequest assertion value" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<CompareRequestDecorator> container )
    {
        // Get the CompareRequest Object
        CompareRequest compareRequest = container.getMessage();

        // Get the Value and store it in the CompareRequest
        TLV tlv = container.getCurrentTLV();

        // We have to handle the special case of a 0 length value
        if ( tlv.getLength() == 0 )
        {
            compareRequest.setAssertionValue( "" );
        }
        else
        {
            if ( container.isBinary( compareRequest.getAttributeId() ) )
            {
                compareRequest.setAssertionValue( tlv.getValue().getData() );

                if ( IS_DEBUG )
                {
                    LOG.debug( "Comparing attribute value {}", Strings.dumpBytes(compareRequest
                            .getAssertionValue().getBytes()) );
                }
            }
            else
            {
                compareRequest.setAssertionValue( Strings.utf8ToString(tlv.getValue().getData()) );

                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug( "Comparing attribute value {}", compareRequest.getAssertionValue() );
                }
            }
        }

        // We can have an END transition
        container.setGrammarEndAllowed( true );
    }
}
