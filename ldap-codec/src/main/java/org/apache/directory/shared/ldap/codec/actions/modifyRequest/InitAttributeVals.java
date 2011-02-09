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


import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.ModifyRequestDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to initialize the set of ModificationRequest AVAs
 * <pre>
 * ModifyRequest ::= [APPLICATION 6] SEQUENCE {
 *     ...
 *     modification SEQUENCE OF SEQUENCE {
 *             ...
 *         modification   AttributeTypeAndValues }
 *
 * AttributeTypeAndValues ::= SEQUENCE {
 *     ...
 *     vals SET OF AttributeValue }
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InitAttributeVals extends GrammarAction<LdapMessageContainer<ModifyRequestDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( InitAttributeVals.class );

    /**
     * Instantiates a new action.
     */
    public InitAttributeVals()
    {
        super( "Init Attribute vals" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<ModifyRequestDecorator> container )
    {
        TLV tlv = container.getCurrentTLV();

        // If the length is null, we store an empty value
        if ( tlv.getLength() == 0 )
        {
            LOG.debug( "No vals for this attribute" );
        }

        // We can have an END transition
        container.setGrammarEndAllowed( true );

        LOG.debug( "Some vals are to be decoded" );
    }
}
