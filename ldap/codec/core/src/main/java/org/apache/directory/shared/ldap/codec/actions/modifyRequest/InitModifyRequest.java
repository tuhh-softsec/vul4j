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
import org.apache.directory.shared.ldap.codec.api.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.ModifyRequestDecorator;
import org.apache.directory.shared.ldap.model.message.ModifyRequest;
import org.apache.directory.shared.ldap.model.message.ModifyRequestImpl;


/**
 * The action used to initialize the ModifyRequest message
 * <pre>
 * LdapMessage ::= ... ModifyRequest ...
 * ModifyRequest ::= [APPLICATION 6] SEQUENCE { ...
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InitModifyRequest extends GrammarAction<LdapMessageContainer<ModifyRequestDecorator>>
{
    /**
     * Instantiates a new action.
     */
    public InitModifyRequest()
    {
        super( "Init ModifyRequest" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<ModifyRequestDecorator> container )
    {
        // Now, we can allocate the ModifyRequest Object
        ModifyRequest modifyRequest = new ModifyRequestImpl( container.getMessageId() );
        ModifyRequestDecorator modifyRequestDecorator = new ModifyRequestDecorator(
            container.getLdapCodecService(), modifyRequest );
        container.setMessage( modifyRequestDecorator );
    }
}
