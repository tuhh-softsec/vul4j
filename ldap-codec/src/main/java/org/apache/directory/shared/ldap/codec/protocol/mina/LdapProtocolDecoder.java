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
package org.apache.directory.shared.ldap.codec.protocol.mina;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.ldap.codec.LdapDecoder;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.codec.api.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.api.MessageDecorator;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;


/**
 * A LDAP message decoder. It is based on shared-ldap decoder.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapProtocolDecoder implements ProtocolDecoder
{
    /** The stateful decoder */
    private LdapDecoder decoder;
    
    
    /**
     * Creates a new instance of LdapProtocolEncoder.
     *
     * @param codec The LDAP codec service associated with this encoder.
     */
    public LdapProtocolDecoder( LdapCodecService codec )
    {
        this.decoder = new LdapDecoder();
    }


    /**
     * {@inheritDoc}
     */
    public void decode( IoSession session, IoBuffer in, ProtocolDecoderOutput out ) throws Exception
    {
        LdapMessageContainer<MessageDecorator<? extends Message>> messageContainer =
            (org.apache.directory.shared.ldap.codec.api.LdapMessageContainer<MessageDecorator<? extends Message>> ) session.getAttribute( "messageContainer" );

        if ( session.containsAttribute( "maxPDUSize" ) )
        {
            int maxPDUSize = ( Integer ) session.getAttribute( "maxPDUSize" );

            messageContainer.setMaxPDUSize( maxPDUSize );
        }

        List<Message> decodedMessages = new ArrayList<Message>();
        ByteBuffer buf = in.buf();

        decoder.decode( buf, messageContainer, decodedMessages );
        
        for ( Message message : decodedMessages )
        {
            out.write( message );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void finishDecode( IoSession session, ProtocolDecoderOutput out ) throws Exception
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void dispose( IoSession session ) throws Exception
    {
        // Nothing to do
    }
}
