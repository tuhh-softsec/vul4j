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
package org.apache.directory.shared.ldap.codec.actions.controls;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.util.OID;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.MessageDecorator;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used add a new control. We store its OID.
 * <pre>
 * Control ::= SEQUENCE {
 *     controlType             LDAPOID,
 *     ...
 * </pre>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AddControl extends GrammarAction<LdapMessageContainer<MessageDecorator<? extends Message>>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( AddControl.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();


    /**
     * Instantiates a new AddControl action.
     */
    public AddControl()
    {
        super( "Add a new control" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<MessageDecorator<? extends Message>> container ) throws DecoderException
    {
        TLV tlv = container.getCurrentTLV();

        // Store the type
        // We have to handle the special case of a 0 length OID
        if ( tlv.getLength() == 0 )
        {
            String msg = I18n.err( I18n.ERR_04097 );
            LOG.error( msg );

            // This will generate a PROTOCOL_ERROR
            throw new DecoderException( msg );
        }

        byte[] value = tlv.getValue().getData();
        String oidValue = Strings.asciiBytesToString(value);

        // The OID is encoded as a String, not an Object Id
        if ( !OID.isOID(oidValue) )
        {
            LOG.error( I18n.err( I18n.ERR_04098, Strings.dumpBytes(value) ) );

            // This will generate a PROTOCOL_ERROR
            throw new DecoderException( I18n.err( I18n.ERR_04099, oidValue ) );
        }

        Message message = container.getMessage();

        Control control = container.getLdapCodecService().newControl( oidValue );

        message.addControl( control );

        // We can have an END transition
        container.setGrammarEndAllowed( true );

        if ( IS_DEBUG )
        {
            LOG.debug( "Control OID : " + oidValue );
        }
    }
}
