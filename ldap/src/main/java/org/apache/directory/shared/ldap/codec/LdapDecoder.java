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
package org.apache.directory.shared.ldap.codec;


import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.ProviderException;

import org.apache.directory.shared.asn1.DecoderCallback;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.ber.tlv.TLVStateEnum;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.model.exception.ResponseCarryingMessageException;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.util.Strings;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The LdapDecoder decodes ASN.1 BER encoded PDUs.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapDecoder implements ProtocolDecoder
{
    /** The logger */
    private static Logger LOG = LoggerFactory.getLogger( LdapDecoder.class );

    /** A speedup for logger */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /** The message container for this instance */
    private LdapMessageContainer ldapMessageContainer;

    /** The callback to call when the decoding is done */
    private DecoderCallback decoderCallback;

    /** The ASN 1 decoder instance */
    private Asn1Decoder asn1Decoder;


    /**
     * Creates an instance of a Ldap Decoder implementation.
     */
    public LdapDecoder()
    {
        asn1Decoder = new Asn1Decoder();
    }


    /**
     * Feeds the bytes within the input stream to the digester to generate the
     * resultant decoded Message.
     * 
     * @param in The InputStream containing the PDU to be decoded
     * @throws ProviderException If the decoding went wrong
     */
    private void digest( InputStream in ) throws DecoderException
    {
        byte[] buf;

        try
        {
            int amount;

            while ( in.available() > 0 )
            {
                buf = new byte[in.available()];

                if ( ( amount = in.read( buf ) ) == -1 )
                {
                    break;
                }

                asn1Decoder.decode( ByteBuffer.wrap( buf, 0, amount ), ldapMessageContainer );
            }
        }
        catch ( Exception e )
        {
            String message = I18n.err( I18n.ERR_04060, e.getLocalizedMessage() );
            LOG.error( message );
            throw new DecoderException( message, e );
        }
    }


    /**
     * Decodes a PDU from an input stream into a Snickers compiler generated
     * stub envelope.
     * 
     * @param lock Lock object used to exclusively read from the input stream
     * @param in The input stream to read and decode PDU bytes from
     * @return return decoded stub
     */
    public Object decode( Object lock, InputStream in ) throws DecoderException
    {
        if ( lock == null )
        {
            digest( in );

            if ( ldapMessageContainer.getState() == TLVStateEnum.PDU_DECODED )
            {
                if ( IS_DEBUG )
                {
                    LOG.debug( "Decoded LdapMessage : " + ldapMessageContainer.getMessage() );
                }

                return ldapMessageContainer.getMessage();
            }
            else
            {
                LOG.error( I18n.err( I18n.ERR_04062 ) );
                throw new DecoderException( I18n.err( I18n.ERR_04063 ) );
            }
        }
        else
        {
            try
            {
                // Synchronize on the input lock object to prevent concurrent
                // reads
                synchronized ( lock )
                {
                    digest( in );

                    // Notify/awaken threads waiting to read from input stream
                    lock.notifyAll();
                }
            }
            catch ( Exception e )
            {
                String message = I18n.err( I18n.ERR_04060, e.getLocalizedMessage() );
                LOG.error( message );
                throw new DecoderException( message, e );
            }

            if ( ldapMessageContainer.getState() == TLVStateEnum.PDU_DECODED )
            {
                if ( IS_DEBUG )
                {
                    LOG.debug( "Decoded LdapMessage : " + ldapMessageContainer.getMessage() );
                }

                return ldapMessageContainer.getMessage();
            }
            else
            {
                LOG.error( I18n.err( I18n.ERR_04064 ) );
                throw new DecoderException( I18n.err( I18n.ERR_04063 ) );
            }
        }
    }


    /**
     * Set the callback to call when the PDU has been decoded
     * 
     * @param cb The callback
     */
    public void setCallback( DecoderCallback cb )
    {
        decoderCallback = cb;
    }


    /**
     * {@inheritDoc}
     */
    public DecoderCallback getCallback()
    {
        return decoderCallback;
    }


    public void decode( IoSession session, IoBuffer in, ProtocolDecoderOutput out ) throws Exception
    {
        ByteBuffer buf = in.buf();
        LdapMessageContainer messageContainer = ( LdapMessageContainer ) session
            .getAttribute( "messageContainer" );

        if ( session.containsAttribute( "maxPDUSize" ) )
        {
            int maxPDUSize = ( Integer ) session.getAttribute( "maxPDUSize" );

            messageContainer.setMaxPDUSize( maxPDUSize );
        }

        buf.mark();

        while ( buf.hasRemaining() )
        {
            try
            {
                asn1Decoder.decode( buf, messageContainer );

                if ( IS_DEBUG )
                {
                    LOG.debug( "Decoding the PDU : " );

                    int size = buf.position();
                    buf.reset();
                    int position = buf.position();
                    int pduLength = size - position;

                    byte[] array = new byte[pduLength];

                    System.arraycopy(buf.array(), position, array, 0, pduLength);

                    buf.position( size );

                    if ( array.length == 0 )
                    {
                        LOG.debug( "NULL buffer, what the HELL ???" );
                    }
                    else
                    {
                        LOG.debug( Strings.dumpBytes(array) );
                    }
                }

                buf.mark();

                if ( messageContainer.getState() == TLVStateEnum.PDU_DECODED )
                {
                    if ( IS_DEBUG )
                    {
                        LOG.debug( "Decoded LdapMessage : " + messageContainer.getMessage() );
                    }
                    
                    Message message = messageContainer.getMessage();
                    
                    out.write( message );

                    messageContainer.clean();
                }
            }
            catch ( DecoderException de )
            {
                buf.clear();
                messageContainer.clean();

                if ( de instanceof ResponseCarryingException )
                {
                    // Transform the DecoderException message to a MessageException
                    ResponseCarryingMessageException rcme = new ResponseCarryingMessageException( de.getMessage() );
                    rcme.setResponse( ( ( ResponseCarryingException ) de ).getResponse() );

                    throw rcme;
                }
                else
                {
                    // TODO : This is certainly not the way we should handle such an exception !
                    throw new ResponseCarryingException( de.getMessage() );
                }
            }
        }
    }


    public void dispose( IoSession session ) throws Exception
    {
    }


    public void finishDecode( IoSession session, ProtocolDecoderOutput out ) throws Exception
    {
    }


    /**
     * @param ldapMessageContainer the ldapMessageContainer to set
     */
    public void setLdapMessageContainer( LdapMessageContainer ldapMessageContainer )
    {
        this.ldapMessageContainer = ldapMessageContainer;
    }
}
