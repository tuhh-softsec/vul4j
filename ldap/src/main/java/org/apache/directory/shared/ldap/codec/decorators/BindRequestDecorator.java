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
package org.apache.directory.shared.ldap.codec.decorators;


import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.model.message.BindRequest;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.util.Strings;


/**
 * A decorator for the BindRequest message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BindRequestDecorator extends SingleReplyRequestDecorator implements BindRequest
{
    /** The bind request length */
    private int bindRequestLength;

    /** The SASL Mechanism length */
    private int saslMechanismLength;

    /** The SASL credentials length */
    private int saslCredentialsLength;


    /**
     * Makes a BindRequest a MessageDecorator.
     *
     * @param decoratedMessage the decorated BindRequests.
     */
    public BindRequestDecorator( BindRequest decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated BindRequest
     */
    public BindRequest getBindRequest()
    {
        return ( BindRequest ) getDecoratedMessage();
    }


    /**
     * Stores the encoded length for the BindRequest
     * @param bindRequestLength The encoded length
     */
    public void setBindRequestLength( int bindRequestLength )
    {
        this.bindRequestLength = bindRequestLength;
    }


    /**
     * @return The encoded BindRequest's length
     */
    public int getBindRequestLength()
    {
        return bindRequestLength;
    }


    /**
     * Stores the encoded length for the SaslCredentials
     * @param saslCredentialsLength The encoded length
     */
    public void setSaslCredentialsLength( int saslCredentialsLength )
    {
        this.saslCredentialsLength = saslCredentialsLength;
    }


    /**
     * @return The encoded SaslCredentials's length
     */
    public int getSaslCredentialsLength()
    {
        return saslCredentialsLength;
    }


    /**
     * Stores the encoded length for the Mechanism
     * @param saslMechanismLength The encoded length
     */
    public void setSaslMechanismLength( int saslMechanismLength )
    {
        this.saslMechanismLength = saslMechanismLength;
    }


    /**
     * @return The encoded SaslMechanism's length
     */
    public int getSaslMechanismLength()
    {
        return saslMechanismLength;
    }
    
    
    //-------------------------------------------------------------------------
    // The BindRequest methods
    //-------------------------------------------------------------------------


    /**
     * {@inheritDoc}
     */
    public boolean isSimple()
    {
        return getBindRequest().isSimple();
    }


    /**
     * {@inheritDoc}
     */
    public boolean getSimple()
    {
        return getBindRequest().getSimple();
    }


    /**
     * {@inheritDoc}
     */
    public void setSimple( boolean isSimple )
    {
        getBindRequest().setSimple( isSimple );
    }


    /**
     * {@inheritDoc}
     */
    public byte[] getCredentials()
    {
        return getBindRequest().getCredentials();
    }


    /**
     * {@inheritDoc}
     */
    public void setCredentials( String credentials )
    {
        getBindRequest().setCredentials( credentials );
    }


    /**
     * {@inheritDoc}
     */
    public void setCredentials( byte[] credentials )
    {
        getBindRequest().setCredentials( credentials );
    }


    /**
     * {@inheritDoc}
     */
    public Dn getName()
    {
        return getBindRequest().getName();
    }


    /**
     * {@inheritDoc}
     */
    public void setName( Dn name )
    {
        getBindRequest().setName( name );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isVersion3()
    {
        return getBindRequest().isVersion3();
    }


    /**
     * {@inheritDoc}
     */
    public boolean getVersion3()
    {
        return getBindRequest().getVersion3();
    }


    /**
     * {@inheritDoc}
     */
    public void setVersion3( boolean isVersion3 )
    {
        getBindRequest().setVersion3( isVersion3 );
    }


    /**
     * {@inheritDoc}
     */
    public String getSaslMechanism()
    {
        return getBindRequest().getSaslMechanism();
    }


    /**
     * {@inheritDoc}
     */
    public void setSaslMechanism( String saslMechanism )
    {
        getBindRequest().setSaslMechanism( saslMechanism );
    }


    //-------------------------------------------------------------------------
    // The Decorator methods
    //-------------------------------------------------------------------------
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
    public int computeLength()
    {
        int bindRequestLength = 1 + 1 + 1; // Initialized with version

        // The name
        bindRequestLength += 1 + TLV.getNbBytes( Dn.getNbBytes( getName() ) )
            + Dn.getNbBytes( getName() );

        byte[] credentials = getCredentials();

        // The authentication
        if ( isSimple() )
        {
            // Compute a SimpleBind operation
            if ( credentials != null )
            {
                bindRequestLength += 1 + TLV.getNbBytes( credentials.length ) + credentials.length;
            }
            else
            {
                bindRequestLength += 1 + 1;
            }
        }
        else
        {
            byte[] mechanismBytes = Strings.getBytesUtf8( getSaslMechanism() );
            int saslMechanismLength = 1 + TLV.getNbBytes( mechanismBytes.length ) + mechanismBytes.length;
            int saslCredentialsLength = 0;

            if ( credentials != null )
            {
                saslCredentialsLength = 1 + TLV.getNbBytes( credentials.length ) + credentials.length;
            }

            int saslLength = 1 + TLV.getNbBytes( saslMechanismLength + saslCredentialsLength ) + saslMechanismLength
                + saslCredentialsLength;

            bindRequestLength += saslLength;

            // Store the mechanism and credentials lengths
            setSaslMechanismLength( saslMechanismLength );
            setSaslCredentialsLength( saslCredentialsLength );
        }

        setBindRequestLength( bindRequestLength );

        // Return the result.
        return 1 + TLV.getNbBytes( bindRequestLength ) + bindRequestLength;
    }


    /**
     * Encode the BindRequest message to a PDU. 
     * 
     * BindRequest : 
     * <pre>
     * 0x60 LL 
     *   0x02 LL version         0x80 LL simple 
     *   0x04 LL name           /   
     *   authentication.encode() 
     *                          \ 0x83 LL mechanism [0x04 LL credential]
     * </pre>
     * 
     * @param buffer The buffer where to put the PDU
     * @return The PDU.
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        try
        {
            // The BindRequest Tag
            buffer.put( LdapConstants.BIND_REQUEST_TAG );
            buffer.put( TLV.getBytes( getBindRequestLength() ) );

        }
        catch ( BufferOverflowException boe )
        {
            throw new EncoderException( I18n.err( I18n.ERR_04005 ) );
        }

        // The version (LDAP V3 only)
        Value.encode( buffer, 3 );

        // The name
        Value.encode( buffer, Dn.getBytes( getName() ) );

        byte[] credentials = getCredentials();

        // The authentication
        if ( isSimple() )
        {
            // Simple authentication
            try
            {
                // The simpleAuthentication Tag
                buffer.put( ( byte ) LdapConstants.BIND_REQUEST_SIMPLE_TAG );

                if ( credentials != null )
                {
                    buffer.put( TLV.getBytes( credentials.length ) );

                    if ( credentials.length != 0 )
                    {
                        buffer.put( credentials );
                    }
                }
                else
                {
                    buffer.put( ( byte ) 0 );
                }
            }
            catch ( BufferOverflowException boe )
            {
                String msg = I18n.err( I18n.ERR_04005 );
                throw new EncoderException( msg );
            }
        }
        else
        {
            // SASL Bind
            try
            {
                // The saslAuthentication Tag
                buffer.put( ( byte ) LdapConstants.BIND_REQUEST_SASL_TAG );

                byte[] mechanismBytes = Strings.getBytesUtf8( getSaslMechanism() );

                buffer.put( TLV
                    .getBytes( getSaslMechanismLength() + getSaslCredentialsLength() ) );

                Value.encode( buffer, mechanismBytes );

                if ( credentials != null )
                {
                    Value.encode( buffer, credentials );
                }
            }
            catch ( BufferOverflowException boe )
            {
                String msg = I18n.err( I18n.ERR_04005 );
                throw new EncoderException( msg );
            }
        }
        
        return buffer;
    }
}
