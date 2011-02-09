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
package org.apache.directory.shared.ldap.codec.actions.delResponse;


import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.DeleteResponseDecorator;
import org.apache.directory.shared.ldap.model.message.DeleteResponseImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to initialize the SearchResultDone response
 * <pre>
 * LdapMessage ::= ... DelResponse ...
 * DelResponse ::= [APPLICATION 11] SEQUENCE { ...
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InitDelResponse extends GrammarAction<LdapMessageContainer<DeleteResponseDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( InitDelResponse.class );

    /**
     * Instantiates a new action.
     */
    public InitDelResponse()
    {
        super( "Init DelResponse" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<DeleteResponseDecorator> container )
    {
        // Now, we can allocate the DelResponse Object
        DeleteResponseDecorator delResponse = new DeleteResponseDecorator(
            container.getLdapCodecService(), new DeleteResponseImpl( container.getMessageId() ) );
        container.setMessage( delResponse );

        LOG.debug( "Del response " );
    }
}
