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
package org.apache.directory.shared.ldap.codec.actions.addRequest;


import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.ldap.codec.api.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.AddRequestDecorator;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store a Value to an AddRequest
 * <pre>
 * AttributeList ::= SEQUENCE OF SEQUENCE {
 *     ...
 *     vals SET OF AttributeValue }
 *
 * AttributeValue OCTET STRING
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AddAttributeValue extends GrammarAction<LdapMessageContainer<AddRequestDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( AddAttributeValue.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();


    /**
     * Instantiates a new value action.
     */
    public AddAttributeValue()
    {
        super( "Store a value" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<AddRequestDecorator> container )
    {
        AddRequestDecorator addRequest = container.getMessage();

        TLV tlv = container.getCurrentTLV();

        // Store the value. It can't be null
        Object value = null;

        if ( tlv.getLength() == 0 )
        {
            addRequest.addAttributeValue( "" );
        }
        else
        {
            if ( container.isBinary( addRequest.getCurrentAttributeType() ) )
            {
                value = tlv.getValue().getData();

                if ( IS_DEBUG )
                {
                    LOG.debug( "Adding value {}", Strings.dumpBytes((byte[]) value) );
                }

                addRequest.addAttributeValue( ( byte[] ) value );
            }
            else
            {
                value = Strings.utf8ToString(tlv.getValue().getData());

                if ( IS_DEBUG )
                {
                    LOG.debug( "Adding value {}" + value );
                }

                addRequest.addAttributeValue( ( String ) value );
            }

        }

        // We can have an END transition
        container.setGrammarEndAllowed( true );
    }
}
