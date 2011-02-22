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
import org.apache.directory.shared.ldap.codec.api.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.IntermediateResponseDecorator;
import org.apache.directory.shared.ldap.model.message.IntermediateResponseImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to initialize the IntermediateResponse message
 * <pre>
 * LdapMessage ::= ... IntermediateResponse ...
 * IntermediateResponse ::= [APPLICATION 25] SEQUENCE {
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InitIntermediateResponse extends GrammarAction<LdapMessageContainer<IntermediateResponseDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( InitIntermediateResponse.class );

    /**
     * Instantiates a new action.
     */
    public InitIntermediateResponse()
    {
        super( "Init Intermediate Response" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<IntermediateResponseDecorator> container ) throws DecoderException
    {
        // Now, we can allocate the IntermediateResponse Object
        IntermediateResponseDecorator intermediateResponse =
            new IntermediateResponseDecorator( container.getLdapCodecService(),
                new IntermediateResponseImpl( container.getMessageId() ) );
        container.setMessage( intermediateResponse );

        LOG.debug( "Intermediate Response" );
    }
}
