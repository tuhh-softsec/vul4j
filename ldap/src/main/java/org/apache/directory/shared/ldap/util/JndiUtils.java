/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.shared.ldap.util;


import java.util.Hashtable;
import java.util.Map;

import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.ContextNotEmptyException;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.NoPermissionException;
import javax.naming.OperationNotSupportedException;
import javax.naming.PartialResultException;
import javax.naming.ReferralException;
import javax.naming.ServiceUnavailableException;
import javax.naming.TimeLimitExceededException;
import javax.naming.directory.AttributeInUseException;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.directory.InvalidSearchFilterException;
import javax.naming.directory.NoSuchAttributeException;
import javax.naming.directory.SchemaViolationException;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.ExtendedResponse;
import javax.naming.ldap.LdapName;

import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.codec.ILdapCodecService;
import org.apache.directory.shared.ldap.model.exception.LdapAffectMultipleDsaException;
import org.apache.directory.shared.ldap.model.exception.LdapAliasDereferencingException;
import org.apache.directory.shared.ldap.model.exception.LdapAliasException;
import org.apache.directory.shared.ldap.model.exception.LdapAttributeInUseException;
import org.apache.directory.shared.ldap.model.exception.LdapAuthenticationException;
import org.apache.directory.shared.ldap.model.exception.LdapAuthenticationNotSupportedException;
import org.apache.directory.shared.ldap.model.exception.LdapContextNotEmptyException;
import org.apache.directory.shared.ldap.model.exception.LdapEntryAlreadyExistsException;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidAttributeTypeException;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidSearchFilterException;
import org.apache.directory.shared.ldap.model.exception.LdapLoopDetectedException;
import org.apache.directory.shared.ldap.model.exception.LdapNoPermissionException;
import org.apache.directory.shared.ldap.model.exception.LdapNoSuchAttributeException;
import org.apache.directory.shared.ldap.model.exception.LdapNoSuchObjectException;
import org.apache.directory.shared.ldap.model.exception.LdapOperationErrorException;
import org.apache.directory.shared.ldap.model.exception.LdapOtherException;
import org.apache.directory.shared.ldap.model.exception.LdapPartialResultException;
import org.apache.directory.shared.ldap.model.exception.LdapProtocolErrorException;
import org.apache.directory.shared.ldap.model.exception.LdapReferralException;
import org.apache.directory.shared.ldap.model.exception.LdapSchemaViolationException;
import org.apache.directory.shared.ldap.model.exception.LdapServiceUnavailableException;
import org.apache.directory.shared.ldap.model.exception.LdapTimeLimitExceededException;
import org.apache.directory.shared.ldap.model.exception.LdapUnwillingToPerformException;
import org.apache.directory.shared.ldap.model.exception.MessageException;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.ExtendedResponseImpl;
import org.apache.directory.shared.ldap.model.message.LdapResult;
import org.apache.directory.shared.ldap.model.message.MessageTypeEnum;
import org.apache.directory.shared.ldap.model.message.ResultResponse;
import org.apache.directory.shared.ldap.model.name.Dn;


/**
 * An utility class to convert back and forth JNDI classes to ADS classes.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class JndiUtils
{
    /**
     * Private constructor.
     */
    private JndiUtils()
    {
    }


    // @TODO not really needed and can be moved out
    public static javax.naming.ldap.Control toJndiControl( ILdapCodecService codec, Control control ) 
        throws EncoderException
    {
        return codec.toJndiControl( control );
    }


    // @TODO not really needed and can be moved out
    public static javax.naming.ldap.Control[] toJndiControls( ILdapCodecService codec, Control... controls ) 
         throws EncoderException
    {
        if ( controls != null )
        {
            javax.naming.ldap.Control[] jndiControls = new javax.naming.ldap.Control[controls.length];
            int i = 0;

            for ( Control control : controls )
            {
                jndiControls[i++] = toJndiControl( codec, control );
            }

            return jndiControls;
        }
        else
        {
            return null;
        }
    }


    // @TODO not really needed and can be moved out
    public static Control fromJndiControl( ILdapCodecService codec, javax.naming.ldap.Control jndiControl ) 
        throws DecoderException
    {
        return codec.fromJndiControl( jndiControl );
    }


    // @TODO not really needed and can be moved out
    public static Control[] fromJndiControls( ILdapCodecService codec, javax.naming.ldap.Control... jndiControls )
        throws DecoderException
    {
        if ( jndiControls != null )
        {
            Control[] controls = new Control[jndiControls.length];
            int i = 0;

            for ( javax.naming.ldap.Control jndiControl : jndiControls )
            {
                controls[i++] = fromJndiControl( codec, jndiControl );
            }

            return controls;
        }
        else
        {
            return null;
        }
    }


    /**
     * TODO toJndiExtendedResponse. This is NOT correct ATM
     *
     * @param request
     * @return
     */
    public static ExtendedResponse toJndiExtendedResponse(
        final org.apache.directory.shared.ldap.model.message.ExtendedResponse response )
    {
        class JndiExtendedResponse implements ExtendedResponse
        {
            private static final long serialVersionUID = 1L;

            
            public byte[] getEncodedValue()
            {
                return response.getEncodedValue();
            }


            public String getID()
            {
                return response.getResponseName();
            }
        }

        return new JndiExtendedResponse();
    }


    public static ExtendedRequest toJndiExtendedRequest(
        final org.apache.directory.shared.ldap.model.message.ExtendedRequest request )
    {
        class JndiExtendedRequest implements ExtendedRequest
        {
            private static final long serialVersionUID = 1L;
            private ExtendedResponse response;


            public ExtendedResponse createExtendedResponse( String id, byte[] berValue, int offset, int length )
                throws NamingException
            {
                org.apache.directory.shared.ldap.model.message.ExtendedResponse response = new ExtendedResponseImpl( request
                    .getMessageId(), request.getRequestName() );
                response.setResponseName( id );
                response.setResponseValue( berValue );

                this.response = JndiUtils.toJndiExtendedResponse( response );

                return this.response;
            }


            public byte[] getEncodedValue()
            {
                return request.getRequestValue();
            }


            public String getID()
            {
                return request.getRequestName();
            }

        }

        return new JndiExtendedRequest();
    }


    /**
     * TODO toJndiExtendedResponse. This is NOT correct ATM
     *
     * @param request
     * @return
     */
    public static org.apache.directory.shared.ldap.model.message.ExtendedResponse fromJndiExtendedResponse(
        final ExtendedResponse response )
    {
        class ServerExtendedResponse implements org.apache.directory.shared.ldap.model.message.ExtendedResponse
        {
            private static final long serialVersionUID = 1L;

            public String getResponseName()
            {
                return response.getID();
            }


            public byte[] getResponseValue()
            {
                return response.getEncodedValue();
            }


            public void setResponseName( String oid )
            {
            }


            public void setResponseValue( byte[] responseValue )
            {
            }


            public LdapResult getLdapResult()
            {
                return null;
            }


            public void addAllControls( Control[] controls ) throws MessageException
            {
            }


            public void addControl( Control control ) throws MessageException
            {
            }


            public Object get( Object key )
            {
                return null;
            }


            public Control getControl( String oid )
            {
                return null;
            }


            public Map<String, Control> getControls()
            {
                return null;
            }


            @SuppressWarnings("unused")
            public int getControlsLength()
            {
                return 0;
            }


            @SuppressWarnings("unused")
            public Control getCurrentControl()
            {
                return null;
            }


            public int getMessageId()
            {
                return 0;
            }


            @SuppressWarnings("unused")
            public int getMessageLength()
            {
                return 0;
            }


            public MessageTypeEnum getType()
            {
                return null;
            }


            public boolean hasControl( String oid )
            {
                return false;
            }


            public Object put( Object key, Object value )
            {
                return null;
            }


            public void removeControl( Control control ) throws MessageException
            {
            }


            @SuppressWarnings("unused")
            public void setControlsLength( int controlsLength )
            {
            }


            public void setMessageId( int messageId )
            {
            }


            @SuppressWarnings("unused")
            public void setMessageLength( int messageLength )
            {
            }


            public byte[] getEncodedValue()
            {
                // TODO Auto-generated method stub
                return null;
            }


            public String getID()
            {
                // TODO Auto-generated method stub
                return null;
            }
        }

        return new ServerExtendedResponse();
    }


    public static org.apache.directory.shared.ldap.model.message.ExtendedRequest fromJndiExtendedRequest(
        final ExtendedRequest request )
    {
        class ServerExtendedRequest implements org.apache.directory.shared.ldap.model.message.ExtendedRequest
        {
            public String getRequestName()
            {
                return request.getID();
            }


            public byte[] getRequestValue()
            {
                return request.getEncodedValue();
            }


            public void setRequestName( String oid )
            {
            }


            public void setRequestValue( byte[] requestValue )
            {
            }


            public MessageTypeEnum getResponseType()
            {
                return null;
            }


            public ResultResponse getResultResponse()
            {
                return null;
            }


            public boolean hasResponse()
            {
                return false;
            }


            public void addAllControls( Control[] controls ) throws MessageException
            {
            }


            public void addControl( Control control ) throws MessageException
            {
            }


            public Object get( Object key )
            {
                return null;
            }


            public Control getControl( String oid )
            {
                return null;
            }


            public Map<String, Control> getControls()
            {
                return null;
            }


            @SuppressWarnings("unused")
            public int getControlsLength()
            {
                return 0;
            }


            @SuppressWarnings("unused")
            public Control getCurrentControl()
            {
                return null;
            }


            public int getMessageId()
            {
                return 0;
            }


            @SuppressWarnings("unused")
            public int getMessageLength()
            {
                return 0;
            }


            public MessageTypeEnum getType()
            {
                return null;
            }


            public boolean hasControl( String oid )
            {
                return false;
            }


            public Object put( Object key, Object value )
            {
                return null;
            }


            public void removeControl( Control control ) throws MessageException
            {
            }


            @SuppressWarnings("unused")
            public void setControlsLength( int controlsLength )
            {
            }


            public void setMessageId( int messageId )
            {
            }


            @SuppressWarnings("unused")
            public void setMessageLength( int messageLength )
            {
            }

        }

        return new ServerExtendedRequest();
    }


    public static void wrap( Throwable t ) throws NamingException
    {
        if ( t instanceof NamingException )
        {
            throw ( NamingException ) t;
        }

        NamingException ne = null;

        if ( t instanceof LdapAffectMultipleDsaException)
        {
            ne = new NamingException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapAliasDereferencingException )
        {
            ne = new NamingException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapAliasException )
        {
            ne = new NamingException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapAttributeInUseException )
        {
            ne = new AttributeInUseException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapAuthenticationException )
        {
            ne = new AuthenticationException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapAuthenticationNotSupportedException )
        {
            ne = new AuthenticationNotSupportedException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapContextNotEmptyException )
        {
            ne = new ContextNotEmptyException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapEntryAlreadyExistsException )
        {
            ne = new NameAlreadyBoundException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapInvalidAttributeTypeException )
        {
            ne = new InvalidAttributeIdentifierException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapInvalidAttributeValueException )
        {
            ne = new InvalidAttributeValueException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapInvalidDnException )
        {
            ne = new InvalidNameException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapInvalidSearchFilterException )
        {
            ne = new InvalidSearchFilterException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapLoopDetectedException )
        {
            ne = new NamingException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapNoPermissionException )
        {
            ne = new NoPermissionException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapNoSuchAttributeException)
        {
            ne = new NoSuchAttributeException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapNoSuchObjectException )
        {
            ne = new NameNotFoundException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapOperationErrorException )
        {
            ne = new NamingException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapOtherException )
        {
            ne = new NamingException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapProtocolErrorException )
        {
            ne = new CommunicationException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapReferralException )
        {
            ne = new WrappedReferralException( ( LdapReferralException ) t );
        }
        else if ( t instanceof LdapPartialResultException )
        {
            ne = new WrappedPartialResultException( ( LdapPartialResultException ) t );
        }
        else if ( t instanceof LdapSchemaViolationException )
        {
            ne = new SchemaViolationException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapServiceUnavailableException)
        {
            ne = new ServiceUnavailableException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapTimeLimitExceededException )
        {
            ne = new TimeLimitExceededException( t.getLocalizedMessage() );
        }
        else if ( t instanceof LdapUnwillingToPerformException )
        {
            ne = new OperationNotSupportedException( t.getLocalizedMessage() );
        }
        else
        {
            ne = new NamingException( t.getLocalizedMessage() );
        }

        ne.setRootCause( t );

        throw ne;
    }


    /**
     * Convert a Dn to a {@link javax.naming.Name}
     *
     * @param name The Dn to convert
     * @return A Name
     */
    public static Name toName( Dn dn )
    {
        try
        {
            Name name = new LdapName( dn.toString() );

            return name;
        }
        catch ( InvalidNameException ine )
        {
            // TODO : check if we must throw an exception.
            // Logically, the Dn must be valid.
            return null;
        }
    }


    /**
     * Convert a {@link javax.naming.Name} to a Dn
     *
     * @param name The Name to convert
     * @return A Dn
     */
    public static Dn fromName( Name name )
    {
        try
        {
            Dn dn = new Dn( name.toString() );

            return dn;
        }
        catch ( LdapInvalidDnException lide )
        {
            // TODO : check if we must throw an exception.
            // Logically, the Name must be valid.
            return null;
        }
    }
}

// a ReferralException around the LdapReferralException to be used in tests
class WrappedReferralException extends ReferralException
{
    private static final long serialVersionUID = 1L;

    private LdapReferralException lre;


    public WrappedReferralException( LdapReferralException lre )
    {
        this.lre = lre;
    }


    @Override
    public boolean skipReferral()
    {
        return lre.skipReferral();
    }


    @Override
    public void retryReferral()
    {
        lre.retryReferral();
    }


    @Override
    public Object getReferralInfo()
    {
        return lre.getReferralInfo();
    }


    @Override
    public Context getReferralContext( Hashtable<?, ?> env ) throws NamingException
    {
        return lre.getReferralContext( env );
    }


    @Override
    public Context getReferralContext() throws NamingException
    {
        return lre.getReferralContext();
    }


    @Override
    public Name getRemainingName()
    {
        return JndiUtils.toName( lre.getRemainingDn() );
    }


    @Override
    public Object getResolvedObj()
    {
        return lre.getResolvedObject();
    }


    @Override
    public Name getResolvedName()
    {
        return JndiUtils.toName( lre.getResolvedDn() );
    }
}

// a PartialResultException around the LdapPartialResultException to be used in tests
class WrappedPartialResultException extends PartialResultException
{
    private static final long serialVersionUID = 1L;

    private LdapPartialResultException lpre;


    public WrappedPartialResultException( LdapPartialResultException lpre )
    {
        this.lpre = lpre;
    }


    @Override
    public Name getRemainingName()
    {
        return JndiUtils.toName( lpre.getRemainingDn() );
    }


    @Override
    public Object getResolvedObj()
    {
        return lpre.getResolvedObject();
    }


    @Override
    public Name getResolvedName()
    {
        return JndiUtils.toName( lpre.getResolvedDn() );
    }
}