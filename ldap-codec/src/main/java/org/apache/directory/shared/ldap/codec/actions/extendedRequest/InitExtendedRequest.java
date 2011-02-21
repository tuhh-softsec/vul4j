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
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.api.ExtendedRequestDecorator;
import org.apache.directory.shared.ldap.model.message.ExtendedRequestImpl;
import org.apache.directory.shared.ldap.model.message.ExtendedResponseImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to initialize the ExtendedRequest message
 * <pre>
 * LdapMessage ::= ... ExtendedRequest ...
 * ExtendedRequest ::= [APPLICATION 23] SEQUENCE {
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InitExtendedRequest extends GrammarAction<LdapMessageContainer<ExtendedRequestDecorator<?,?>>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( InitExtendedRequest.class );

    /**
     * Instantiates a new action.
     */
    public InitExtendedRequest()
    {
        super( "Init ExtendedRequest" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<ExtendedRequestDecorator<?,?>> container ) throws DecoderException
    {
        // Now, we can allocate the ExtendedRequest Object
        ExtendedRequestDecorator<?,?> extendedRequest = 
            new ExtendedRequestDecorator<ExtendedRequestImpl,ExtendedResponseImpl>(
                container.getLdapCodecService(), 
                new ExtendedRequestImpl( container.getMessageId() ) );
        container.setMessage( extendedRequest );

        LOG.debug( "Extended request" );
    }
}
