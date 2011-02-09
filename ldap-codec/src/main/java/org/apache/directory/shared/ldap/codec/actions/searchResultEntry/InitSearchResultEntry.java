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
package org.apache.directory.shared.ldap.codec.actions.searchResultEntry;


import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.SearchResultEntryDecorator;
import org.apache.directory.shared.ldap.model.message.SearchResultEntryImpl;


/**
 * The action used to initialize the SearchResultEntry response
 * <pre>
 * LdapMessage ::= ... SearchResultEntry ...
 * SearchResultEntry ::= [APPLICATION 4] SEQUENCE { ...
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InitSearchResultEntry extends GrammarAction<LdapMessageContainer<SearchResultEntryDecorator>>
{
    /**
     * Instantiates a new action.
     */
    public InitSearchResultEntry()
    {
        super( "Init SearchResultEntry" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<SearchResultEntryDecorator> container )
    {
        // Now, we can allocate the SearchResultEntry Object
        SearchResultEntryDecorator searchResultEntry = new SearchResultEntryDecorator(
            container.getLdapCodecService(), new SearchResultEntryImpl( container.getMessageId() ) );
        container.setMessage( searchResultEntry );
    }
}
