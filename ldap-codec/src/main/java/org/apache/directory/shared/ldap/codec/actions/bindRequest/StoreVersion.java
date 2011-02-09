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
package org.apache.directory.shared.ldap.codec.actions.bindRequest;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.IntegerDecoder;
import org.apache.directory.shared.asn1.ber.tlv.IntegerDecoderException;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.BindRequestDecorator;
import org.apache.directory.shared.ldap.model.message.BindRequest;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store the BindRequest version.
 * <pre>
 * BindRequest ::= [APPLICATION 0] SEQUENCE {
 *     version                 INTEGER (1 ..  127),
 *     ....
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoreVersion extends GrammarAction<LdapMessageContainer<BindRequestDecorator>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( StoreVersion.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();


    /**
     * Instantiates a new action.
     */
    public StoreVersion()
    {
        super( "Store BindRequest Version" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<BindRequestDecorator> container ) throws DecoderException
    {
        BindRequest bindRequestMessage = container.getMessage();

        // The current TLV should be a integer between 1 and 127
        // We get it and store it in Version
        TLV tlv = container.getCurrentTLV();

        Value value = tlv.getValue();

        try
        {
            int version = IntegerDecoder.parse( value, 1, 127 );

            if ( IS_DEBUG )
            {
                LOG.debug( "Ldap version ", Integer.valueOf( version ) );
            }

            bindRequestMessage.setVersion3( version == 3 );
        }
        catch ( IntegerDecoderException ide )
        {
            LOG.error( I18n
                .err( I18n.ERR_04078, Strings.dumpBytes(value.getData()), ide.getMessage() ) );

            // This will generate a PROTOCOL_ERROR
            throw new DecoderException( ide.getMessage() );
        }
    }
}
