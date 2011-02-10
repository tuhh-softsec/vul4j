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
package org.apache.directory.shared.ldap.codec.actions.modifyDnResponse;


import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.ModifyDnResponseDecorator;
import org.apache.directory.shared.ldap.model.message.ModifyDnResponseImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to initialize the ModifyDnResponse message
 * <pre>
 * ModifyDNResponse ::= [APPLICATION 13] SEQUENCE {
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InitModifyDnResponse extends GrammarAction<LdapMessageContainer<ModifyDnResponseDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( InitModifyDnResponse.class );

    /**
     * Instantiates a new action.
     */
    public InitModifyDnResponse()
    {
        super( "Init ModifyDnResponse" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<ModifyDnResponseDecorator> container )
    {
        // Now, we can allocate the ModifyDnResponse Object
        ModifyDnResponseDecorator modifyDnResponse = new ModifyDnResponseDecorator(
            container.getLdapCodecService(), new ModifyDnResponseImpl( container.getMessageId() ) );
        container.setMessage( modifyDnResponse );

        LOG.debug( "Modify Dn response " );
    }
}
