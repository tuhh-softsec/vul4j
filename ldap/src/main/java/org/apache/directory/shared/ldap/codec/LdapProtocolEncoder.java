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


import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;

import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.asn1.codec.EncoderException;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.controls.CodecControl;
import org.apache.directory.shared.ldap.message.BindResponseImpl;
import org.apache.directory.shared.ldap.message.DeleteResponseImpl;
import org.apache.directory.shared.ldap.message.control.Control;
import org.apache.directory.shared.ldap.message.internal.InternalBindResponse;
import org.apache.directory.shared.ldap.message.internal.InternalDeleteResponse;
import org.apache.directory.shared.ldap.message.internal.InternalLdapResult;
import org.apache.directory.shared.ldap.message.internal.InternalMessage;
import org.apache.directory.shared.ldap.message.internal.InternalReferral;
import org.apache.directory.shared.ldap.util.StringTools;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;


/**
 * LDAP BER encoder.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapProtocolEncoder extends ProtocolEncoderAdapter
{
    /**
     * Encode a Ldap request and write it to the remote server.
     * 
     * @param session The session containing the LdapMessageContainer
     * @param message The LDAP message we have to encode to a Byte stream
     * @param out The callback we have to invoke when the message has been encoded 
     */
    public void encode( IoSession session, Object message, ProtocolEncoderOutput out ) throws Exception
    {
        ByteBuffer buffer = encodeMessage( ( InternalMessage ) message );

        IoBuffer ioBuffer = IoBuffer.wrap( buffer );

        out.write( ioBuffer );
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
    public ByteBuffer encodeMessage( InternalMessage message ) throws EncoderException
    {
        int length = computeMessageLength( message );
        ByteBuffer buffer = ByteBuffer.allocate( length );

        if ( ( message instanceof InternalBindResponse ) || ( message instanceof InternalDeleteResponse ) )
        {
            try
            {
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
            }
            catch ( EncoderException ee )
            {
                MessageEncoderException exception = new MessageEncoderException( message.getMessageId(), ee
                    .getMessage() );

                throw exception;
            }
        }
        else
        {
            LdapMessageCodec ldapRequest = ( LdapMessageCodec ) LdapTransformer.transform( message );
            buffer = ldapRequest.encode();
        }

        buffer.flip();

        return buffer;
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

        // Store the messageLength
        message.setMessageLength( ldapMessageLength );

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
            ldapResult.setMatchedDnBytes( matchedDNBytes );
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

                ldapResult.setReferralsLength( referralsLength );

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
            Collection<byte[]> ldapUrlsBytes = referral.getLdapUrlsBytes();

            if ( ( ldapUrlsBytes != null ) && ( ldapUrlsBytes.size() != 0 ) )
            {
                // Encode the referrals sequence
                // The referrals length MUST have been computed before !
                buffer.put( ( byte ) LdapConstants.LDAP_RESULT_REFERRAL_SEQUENCE_TAG );
                buffer.put( TLV.getBytes( ldapResult.getReferralsLength() ) );

                // Each referral
                for ( byte[] ldapUrlBytes : ldapUrlsBytes )
                {
                    // Encode the current referral
                    Value.encode( buffer, ldapUrlBytes );
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
     * Compute the DelResponse length 
     * 
     * DelResponse :
     * 
     * 0x6B L1
     *  |
     *  +--> LdapResult
     * 
     * L1 = Length(LdapResult)
     * 
     * Length(DelResponse) = Length(0x6B) + Length(L1) + L1
     */
    private int computeDeleteResponseLength( DeleteResponseImpl deleteResponse )
    {
        int deleteResponseLength = computeLdapResultLength( deleteResponse.getLdapResult() );

        deleteResponse.setDeleteResponseLength( deleteResponseLength );

        return 1 + TLV.getNbBytes( deleteResponseLength ) + deleteResponseLength;
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
     * Encode the DelResponse message to a PDU.
     * 
     * @param buffer The buffer where to put the PDU
     */
    private void encodeDeleteResponse( ByteBuffer buffer, DeleteResponseImpl deleteResponse ) throws EncoderException
    {
        try
        {
            // The BindResponse Tag
            buffer.put( LdapConstants.DEL_RESPONSE_TAG );
            buffer.put( TLV.getBytes( deleteResponse.getDeleteResponseLength() ) );

            // The LdapResult
            encodeLdapResult( buffer, deleteResponse.getLdapResult() );
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

            case DEL_RESPONSE:
                return computeDeleteResponseLength( ( DeleteResponseImpl ) message );

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
                break;

            case DEL_RESPONSE:
                encodeDeleteResponse( bb, ( DeleteResponseImpl ) message );
                break;
        }
    }
}
