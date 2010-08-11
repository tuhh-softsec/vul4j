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


import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Collection;
import java.util.Map;

import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.asn1.codec.EncoderException;
import org.apache.directory.shared.asn1.codec.stateful.EncoderCallback;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.controls.CodecControl;
import org.apache.directory.shared.ldap.message.BindResponseImpl;
import org.apache.directory.shared.ldap.message.control.Control;
import org.apache.directory.shared.ldap.message.internal.InternalBindResponse;
import org.apache.directory.shared.ldap.message.internal.InternalLdapResult;
import org.apache.directory.shared.ldap.message.internal.InternalMessage;
import org.apache.directory.shared.ldap.message.internal.InternalReferral;
import org.apache.directory.shared.ldap.message.spi.Provider;
import org.apache.directory.shared.ldap.message.spi.ProviderEncoder;
import org.apache.directory.shared.ldap.message.spi.ProviderException;
import org.apache.directory.shared.ldap.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * LDAP BER provider's encoder.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapEncoder implements ProviderEncoder
{
    //TM private static long cumul = 0L;
    //TM private static long count = 0L;
    //TM private Object lock = new Object();

    /** The logger */
    private static Logger log = LoggerFactory.getLogger( LdapEncoder.class );

    /** A speedup for logger */
    private static final boolean IS_DEBUG = log.isDebugEnabled();

    /** The associated Provider */
    final Provider provider;

    /** The callback to call when the encoding is done */
    private EncoderCallback encodeCallback;


    /**
     * Creates an instance of a Ldap Encoder implementation.
     * 
     * @param provider The associated Provider
     */
    public LdapEncoder( Provider provider )
    {
        this.provider = provider;
    }


    /**
     * Encodes a LdapMessage, and calls the callback.
     * 
     * @param lock Not used...
     * @param out Not used ...
     * @param obj The LdapMessage to encode
     * @throws ProviderException If anything went wrong
     */
    public void encodeBlocking( Object lock, OutputStream out, Object obj ) throws ProviderException
    {
        try
        {
            if ( IS_DEBUG )
            {
                log.debug( "Encoding this LdapMessage : " + obj );
            }

            ByteBuffer encoded = ( ( LdapMessageCodec ) obj ).encode();

            try
            {
                ( ( ByteBuffer ) encoded ).flip();
                WritableByteChannel channel = Channels.newChannel( out );
                channel.write( ( ByteBuffer ) encoded );
            }
            catch ( IOException e )
            {
                ProviderException pe = new ProviderException( provider, I18n.err( I18n.ERR_04065, "", e
                    .getLocalizedMessage() ) );
                throw pe;
            }

        }
        catch ( EncoderException e )
        {
            String msg = I18n.err( I18n.ERR_04065, obj, e.getLocalizedMessage() );
            log.error( msg );
            ProviderException pe = new ProviderException( provider, msg );
            throw pe;
        }
    }


    /**
     * Encodes a LdapMessage, and return a ByteBuffer containing the resulting
     * PDU
     * 
     * @param obj The LdapMessage to encode
     * @return The ByteBuffer containing the PDU
     * @throws ProviderException If anything went wrong
     */
    public ByteBuffer encodeBlocking( Object obj ) throws ProviderException
    {
        try
        {
            if ( IS_DEBUG )
            {
                log.debug( "Encoding this LdapMessage : " + obj );
            }

            ByteBuffer pdu = ( ( LdapMessageCodec ) obj ).encode();

            if ( IS_DEBUG )
            {
                log.debug( "Encoded PDU : " + StringTools.dumpBytes( pdu.array() ) );
            }

            pdu.flip();
            return pdu;
        }
        catch ( EncoderException e )
        {
            String msg = I18n.err( I18n.ERR_04065, obj, e.getLocalizedMessage() );
            log.error( msg );
            ProviderException pe = new ProviderException( provider, msg );
            throw pe;
        }
    }


    /**
     * Encodes a LdapMessage, and return a byte array containing the resulting
     * PDU
     * 
     * @param obj The LdapMessage to encode
     * @return The byte[] containing the PDU
     * @throws ProviderException If anything went wrong
     */
    public byte[] encodeToArray( Object obj ) throws ProviderException
    {
        try
        {
            if ( IS_DEBUG )
            {
                log.debug( "Encoding this LdapMessage : " + obj );
            }

            byte[] pdu = ( ( LdapMessageCodec ) obj ).encode().array();

            if ( IS_DEBUG )
            {
                log.debug( "Encoded PDU : " + StringTools.dumpBytes( pdu ) );
            }

            return pdu;
        }
        catch ( EncoderException e )
        {
            String msg = I18n.err( I18n.ERR_04065, obj, e.getLocalizedMessage() );
            log.error( msg );
            ProviderException pe = new ProviderException( provider, msg );
            throw pe;
        }
    }


    /**
     * Gets the Provider associated with this SPI implementation object.
     * 
     * @return Provider The provider
     */
    public Provider getProvider()
    {
        return provider;
    }


    /**
     * Encodes a LdapMessage, and calls the callback
     * 
     * @param obj The LdapMessage to encode
     * @throws EncoderException If anything went wrong
     */
    public void encode( Object request ) throws EncoderException
    {
        //TM long t0 = System.nanoTime();
        InternalMessage message = ( InternalMessage ) request;
        ByteBuffer encoded = null;

        if ( message instanceof InternalBindResponse )
        {
            encoded = encodeMessage( message );
        }
        else
        {
            LdapMessageCodec ldapRequest = ( LdapMessageCodec ) LdapTransformer.transform( ( InternalMessage ) request );
            encoded = ldapRequest.encode();
        }

        encoded.flip();

        encodeCallback.encodeOccurred( null, encoded );
        //TM long t1 = System.nanoTime();

        //TM synchronized (lock)
        //TM {
        //TM     cumul += (t1 - t0);
        //TM     count++;
        //TM    
        //TM
        //TM     if ( count % 1000L == 0)
        //TM     {
        //TM         System.out.println( "Encode cost : " + (cumul/count) );
        //TM         cumul = 0L;
        //TM     }
        //TM }
    }


    /**
     * Set the callback called when the encoding is done.
     * 
     * @param cb The callback.
     */
    public void setCallback( EncoderCallback cb )
    {
        encodeCallback = cb;
    }


    /**
     * Generate the PDU which contains the encoded object. 
     * 
     * The generation is done in two phases : 
     * - first, we compute the length of each part and the
     * global PDU length 
     * - second, we produce the PDU. 
     * 
     * <pre>
     * 0x30 L1 
     *   | 
     *   +--> 0x02 L2 MessageId  
     *   +--> ProtocolOp 
     *   +--> Controls 
     *   
     * L2 = Length(MessageId)
     * L1 = Length(0x02) + Length(L2) + L2 + Length(ProtocolOp) + Length(Controls)
     * LdapMessageLength = Length(0x30) + Length(L1) + L1
     * </pre>
     * 
     * @param message The message to encode
     * @return A ByteBuffer that contains the PDU
     * @throws EncoderException If anything goes wrong.
     */
    private ByteBuffer encodeMessage( InternalMessage message ) throws EncoderException
    {
        int length = computeMessageLength( message );

        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( length );

            try
            {
                // The LdapMessage Sequence
                buffer.put( UniversalTag.SEQUENCE_TAG );

                // The length has been calculated by the computeLength method
                buffer.put( TLV.getBytes( message.getMessageLength() ) );
            }
            catch ( BufferOverflowException boe )
            {
                throw new EncoderException( I18n.err( I18n.ERR_04005 ) );
            }

            // The message Id
            Value.encode( buffer, message.getMessageId() );

            // Add the protocolOp part
            encodeProtocolOp( buffer, message );

            // Do the same thing for Controls, if any.
            Map<String, Control> controls = message.getControls();

            if ( ( controls != null ) && ( controls.size() > 0 ) )
            {
                // Encode the controls
                buffer.put( ( byte ) LdapConstants.CONTROLS_TAG );
                buffer.put( TLV.getBytes( message.getControlsLength() ) );

                // Encode each control
                for ( Control control : controls.values() )
                {
                    ( ( CodecControl ) control ).encode( buffer );
                }
            }

            return buffer;
        }
        catch ( EncoderException ee )
        {
            MessageEncoderException exception = new MessageEncoderException( message.getMessageId(), ee.getMessage() );

            throw exception;
        }
    }


    /**
     * Compute the LdapMessage length LdapMessage : 
     * 0x30 L1 
     *   | 
     *   +--> 0x02 0x0(1-4) [0..2^31-1] (MessageId) 
     *   +--> protocolOp 
     *   [+--> Controls] 
     *   
     * MessageId length = Length(0x02) + length(MessageId) + MessageId.length 
     * L1 = length(ProtocolOp) 
     * LdapMessage length = Length(0x30) + Length(L1) + MessageId length + L1
     */
    private int computeMessageLength( InternalMessage message )
    {
        // The length of the MessageId. It's the sum of
        // - the tag (0x02), 1 byte
        // - the length of the Id length, 1 byte
        // - the Id length, 1 to 4 bytes
        int ldapMessageLength = 1 + 1 + Value.getNbBytes( message.getMessageId() );

        // Get the protocolOp length
        ldapMessageLength += computeProtocolOpLength( message );
        message.setMessageLength( ldapMessageLength );

        Map<String, Control> controls = message.getControls();

        // Do the same thing for Controls, if any.
        if ( controls.size() > 0 )
        {
            // Controls :
            // 0xA0 L3
            //   |
            //   +--> 0x30 L4
            //   +--> 0x30 L5
            //   +--> ...
            //   +--> 0x30 Li
            //   +--> ...
            //   +--> 0x30 Ln
            //
            // L3 = Length(0x30) + Length(L5) + L5
            // + Length(0x30) + Length(L6) + L6
            // + ...
            // + Length(0x30) + Length(Li) + Li
            // + ...
            // + Length(0x30) + Length(Ln) + Ln
            //
            // LdapMessageLength = LdapMessageLength + Length(0x90)
            // + Length(L3) + L3
            int controlsSequenceLength = 0;

            // We may have more than one control. ControlsLength is L4.
            for ( Control control : controls.values() )
            {
                controlsSequenceLength += ( ( CodecControl ) control ).computeLength();
            }

            // Computes the controls length
            // 1 + Length.getNbBytes( controlsSequenceLength ) + controlsSequenceLength;
            message.setControlsLength( controlsSequenceLength );

            // Now, add the tag and the length of the controls length
            ldapMessageLength += 1 + TLV.getNbBytes( controlsSequenceLength ) + controlsSequenceLength;
        }

        // finally, calculate the global message size :
        // length(Tag) + Length(length) + length

        return 1 + ldapMessageLength + TLV.getNbBytes( ldapMessageLength );
    }


    /**
     * Compute the LdapResult length 
     * 
     * LdapResult : 
     * 0x0A 01 resultCode (0..80)
     *   0x04 L1 matchedDN (L1 = Length(matchedDN)) 
     *   0x04 L2 errorMessage (L2 = Length(errorMessage)) 
     *   [0x83 L3] referrals 
     *     | 
     *     +--> 0x04 L4 referral 
     *     +--> 0x04 L5 referral 
     *     +--> ... 
     *     +--> 0x04 Li referral 
     *     +--> ... 
     *     +--> 0x04 Ln referral 
     *     
     * L1 = Length(matchedDN) 
     * L2 = Length(errorMessage) 
     * L3 = n*Length(0x04) + sum(Length(L4) .. Length(Ln)) + sum(L4..Ln) 
     * L4..n = Length(0x04) + Length(Li) + Li 
     * Length(LdapResult) = Length(0x0x0A) +
     *      Length(0x01) + 1 + Length(0x04) + Length(L1) + L1 + Length(0x04) +
     *      Length(L2) + L2 + Length(0x83) + Length(L3) + L3
     */
    private int computeLdapResultLength( InternalLdapResult ldapResult )
    {
        int ldapResultLength = 0;

        // The result code : always 3 bytes
        ldapResultLength = 1 + 1 + 1;

        // The matchedDN length
        if ( ldapResult.getMatchedDn() == null )
        {
            ldapResultLength += 1 + 1;
        }
        else
        {
            byte[] matchedDNBytes = StringTools.getBytesUtf8( StringTools
                .trimLeft( ldapResult.getMatchedDn().getName() ) );
            ldapResultLength += 1 + TLV.getNbBytes( matchedDNBytes.length ) + matchedDNBytes.length;
        }

        // The errorMessage length
        byte[] errorMessageBytes = StringTools.getBytesUtf8( ldapResult.getErrorMessage() );
        ldapResultLength += 1 + TLV.getNbBytes( errorMessageBytes.length ) + errorMessageBytes.length;
        ldapResult.setErrorMessageBytes( errorMessageBytes );

        InternalReferral referral = ldapResult.getReferral();

        if ( referral != null )
        {
            Collection<String> ldapUrls = referral.getLdapUrls();

            if ( ( ldapUrls != null ) && ( ldapUrls.size() != 0 ) )
            {
                int referralsLength = 0;

                // Each referral
                for ( String ldapUrl : ldapUrls )
                {
                    byte[] ldapUrlBytes = StringTools.getBytesUtf8( ldapUrl );
                    referralsLength += 1 + TLV.getNbBytes( ldapUrlBytes.length ) + ldapUrlBytes.length;
                    referral.addLdapUrlBytes( ldapUrlBytes );
                }

                // The referrals
                ldapResultLength += 1 + TLV.getNbBytes( referralsLength ) + referralsLength;
            }
        }

        return ldapResultLength;
    }


    /**
     * Encode the LdapResult message to a PDU.
     * 
     * @param buffer The buffer where to put the PDU
     * @return The PDU.
     */
    private ByteBuffer encodeLdapResult( ByteBuffer buffer, InternalLdapResult ldapResult ) throws EncoderException
    {
        if ( buffer == null )
        {
            throw new EncoderException( I18n.err( I18n.ERR_04023 ) );
        }

        try
        {
            // The result code
            buffer.put( UniversalTag.ENUMERATED_TAG );
            buffer.put( ( byte ) 1 );
            buffer.put( ( byte ) ldapResult.getResultCode().getValue() );
        }
        catch ( BufferOverflowException boe )
        {
            throw new EncoderException( I18n.err( I18n.ERR_04005 ) );
        }

        // The matchedDN
        Value.encode( buffer, ldapResult.getMatchedDnBytes() );

        // The error message
        Value.encode( buffer, ldapResult.getErrorMessageBytes() );

        // The referrals, if any
        InternalReferral referral = ldapResult.getReferral();

        if ( referral != null )
        {
            Collection<String> ldapUrls = referral.getLdapUrls();

            if ( ( ldapUrls != null ) && ( ldapUrls.size() != 0 ) )
            {
                // Encode the referrals sequence
                // The referrals length MUST have been computed before !
                buffer.put( ( byte ) LdapConstants.LDAP_RESULT_REFERRAL_SEQUENCE_TAG );
                //buffer.put( TLV.getBytes( referralsLength ) );

                // Each referral
                for ( String ldapUrl : ldapUrls )
                {
                    // Encode the current referral
                    //Value.encode( buffer, referral.getBytesReference() );
                }
            }
        }

        return buffer;
    }


    /**
     * Compute the BindResponse length 
     * 
     * BindResponse : 
     * <pre>
     * 0x61 L1 
     *   | 
     *   +--> LdapResult
     *   +--> [serverSaslCreds] 
     *   
     * L1 = Length(LdapResult) [ + Length(serverSaslCreds) ] 
     * Length(BindResponse) = Length(0x61) + Length(L1) + L1
     * </pre>
     */
    private int computeBindResponseLength( BindResponseImpl bindResponse )
    {
        int ldapResultLength = computeLdapResultLength( bindResponse.getLdapResult() );

        int bindResponseLength = ldapResultLength;

        byte[] serverSaslCreds = bindResponse.getServerSaslCreds();

        if ( serverSaslCreds != null )
        {
            bindResponseLength += 1 + TLV.getNbBytes( serverSaslCreds.length ) + serverSaslCreds.length;
        }

        bindResponse.setBindResponseLength( bindResponseLength );

        return 1 + TLV.getNbBytes( bindResponseLength ) + bindResponseLength;
    }


    /**
     * Encode the BindResponse message to a PDU.
     * 
     * BindResponse :
     * <pre>
     * LdapResult.encode 
     * [0x87 LL serverSaslCreds]
     * </pre>
     * 
     * @param buffer The buffer where to put the PDU
     * @return The PDU.
     */
    private void encodeBindResponse( ByteBuffer bb, BindResponseImpl bindResponse ) throws EncoderException
    {
        try
        {
            // The BindResponse Tag
            bb.put( LdapConstants.BIND_RESPONSE_TAG );
            bb.put( TLV.getBytes( bindResponse.getBindResponseLength() ) );

            // The LdapResult
            encodeLdapResult( bb, bindResponse.getLdapResult() );

            // The serverSaslCredential, if any
            byte[] serverSaslCreds = bindResponse.getServerSaslCreds();

            if ( serverSaslCreds != null )
            {
                bb.put( ( byte ) LdapConstants.SERVER_SASL_CREDENTIAL_TAG );

                bb.put( TLV.getBytes( serverSaslCreds.length ) );

                if ( serverSaslCreds.length != 0 )
                {
                    bb.put( serverSaslCreds );
                }
            }
        }
        catch ( BufferOverflowException boe )
        {
            throw new EncoderException( I18n.err( I18n.ERR_04005 ) );
        }
    }


    /**
     * Compute the BindRequest length 
     * 
     * BindRequest : 
     * <pre>
     * 0x60 L1 
     *   | 
     *   +--> 0x02 0x01 (1..127) version 
     *   +--> 0x04 L2 name 
     *   +--> authentication 
     *   
     * L2 = Length(name)
     * L3/4 = Length(authentication) 
     * Length(BindRequest) = Length(0x60) + Length(L1) + L1 + Length(0x02) + 1 + 1 + 
     *      Length(0x04) + Length(L2) + L2 + Length(authentication)
     * </pre>
     */
    private int computeProtocolOpLength( InternalMessage message )
    {
        switch ( message.getType() )
        {
            case BIND_RESPONSE:
                return computeBindResponseLength( ( BindResponseImpl ) message );

            default:
                return 0;
        }
    }


    private void encodeProtocolOp( ByteBuffer bb, InternalMessage message ) throws EncoderException
    {
        switch ( message.getType() )
        {
            case BIND_RESPONSE:
                encodeBindResponse( bb, ( BindResponseImpl ) message );
        }
    }
}
