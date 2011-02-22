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
package org.apache.directory.shared.ldap.codec.actions.bindResponse;


import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.ldap.codec.api.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.BindResponseDecorator;
import org.apache.directory.shared.ldap.model.message.BindResponseImpl;


/**
 * The action used to initialize the BindResponse
 * <pre>
 * LdapMessage ::= ... BindResponse ...
 * BindResponse ::= [APPLICATION 1] SEQUENCE { ...
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InitBindResponse extends GrammarAction<LdapMessageContainer<BindResponseDecorator>>
{
    /**
     * Instantiates a new action.
     */
    public InitBindResponse()
    {
        super( "Init BindResponse" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<BindResponseDecorator> container )
    {
        // Now, we can allocate the BindResponse Object
        BindResponseDecorator bindResponse = new BindResponseDecorator(
            container.getLdapCodecService(), new BindResponseImpl( container.getMessageId() ) );
        container.setMessage( bindResponse );
    }
}
