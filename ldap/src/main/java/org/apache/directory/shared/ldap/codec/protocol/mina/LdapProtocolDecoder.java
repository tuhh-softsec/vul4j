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

import org.apache.directory.shared.asn1.ber.Asn1Container;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.ber.tlv.TLVStateEnum;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.util.Strings;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A LDAP message decoder. It is based on shared-ldap decoder.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapProtocolDecoder implements ProtocolDecoder
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( LdapProtocolDecoder.class );

    /** A speedup for logger */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();


    /**
     * Decode a Ldap request and write it to the remote server.
     * 
     * @param session The session containing the LdapMessageContainer
     * @param buffer The ByteBuffer containing the incoming bytes to decode
     * to a LDAP message
     * @param out The callback we have to invoke when the message has been decoded
     * @throws Exception if the read data violated protocol specification
     */
    public void decode( IoSession session, IoBuffer buffer, ProtocolDecoderOutput out ) throws Exception
    {
        // Allocate a LdapMessage Container
        Asn1Decoder ldapDecoder = new Asn1Decoder();
        Asn1Container ldapMessageContainer = ( LdapMessageContainer<?> ) session.getAttribute( "LDAP-Container" );
        ByteBuffer buf = buffer.buf();
        int currentPos = 0;

        while ( buf.hasRemaining() )
        {
            try
            {
                ldapDecoder.decode( buf, ldapMessageContainer );

                if ( IS_DEBUG )
                {
                    LOG.debug( "Decoding the PDU : " );
                    int pos = buf.position();

                    byte[] b = new byte[pos - currentPos];

                    System.arraycopy( buf.array(), currentPos, b, 0, pos - currentPos );
                    currentPos = pos;
                    LOG.debug( "Received buffer : " + Strings.dumpBytes(b) );
                }

                if ( ldapMessageContainer.getState() == TLVStateEnum.PDU_DECODED )
                {
                    // get back the decoded message
                    Message message = ( ( LdapMessageContainer<?> ) ldapMessageContainer ).getMessage();

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "Decoded LdapMessage : " + message );
                        buf.mark();
                    }

                    // Clean the container for the next decoding
                    ( ( LdapMessageContainer<?> ) ldapMessageContainer ).clean();

                    // Send back the message
                    out.write( message );
                }
            }
            catch ( DecoderException de )
            {
                buf.clear();
                ( ( LdapMessageContainer<?> ) ldapMessageContainer ).clean();
                throw de;
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void finishDecode( IoSession session, ProtocolDecoderOutput out ) throws Exception
    {
    }


    /**
     * {@inheritDoc}
     */
    public void dispose( IoSession session ) throws Exception
    {
    }
}
