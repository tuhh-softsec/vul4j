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
package org.apache.directory.shared.ldap.codec.osgi;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.Asn1Container;
import org.apache.directory.shared.ldap.codec.BasicControlDecorator;
import org.apache.directory.shared.ldap.codec.api.ExtendedRequestDecorator;
import org.apache.directory.shared.ldap.codec.api.ExtendedResponseDecorator;
import org.apache.directory.shared.ldap.codec.api.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.api.CodecControl;
import org.apache.directory.shared.ldap.codec.api.ControlFactory;
import org.apache.directory.shared.ldap.codec.api.ExtendedRequestFactory;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.codec.api.MessageDecorator;
import org.apache.directory.shared.ldap.codec.api.UnsolicitedResponseFactory;
import org.apache.directory.shared.ldap.codec.controls.cascade.CascadeFactory;
import org.apache.directory.shared.ldap.codec.controls.manageDsaIT.ManageDsaITFactory;
import org.apache.directory.shared.ldap.codec.controls.search.entryChange.EntryChangeFactory;
import org.apache.directory.shared.ldap.codec.controls.search.pagedSearch.PagedResultsFactory;
import org.apache.directory.shared.ldap.codec.controls.search.persistentSearch.PersistentSearchFactory;
import org.apache.directory.shared.ldap.codec.controls.search.subentries.SubentriesFactory;
import org.apache.directory.shared.ldap.codec.protocol.mina.LdapProtocolCodecFactory;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.ExtendedRequest;
import org.apache.directory.shared.ldap.model.message.ExtendedRequestImpl;
import org.apache.directory.shared.ldap.model.message.ExtendedResponse;
import org.apache.directory.shared.ldap.model.message.ExtendedResponseImpl;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.ldap.model.message.controls.OpaqueControl;
import org.apache.directory.shared.util.Strings;
import org.apache.directory.shared.util.exception.NotImplementedException;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.naming.NamingException;


/**
 * The default {@link LdapCodecService} implementation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DefaultLdapCodecService implements LdapCodecService
{
    /** A logger */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultLdapCodecService.class );

    /** The map of registered {@link org.apache.directory.shared.ldap.codec.api.ControlFactory}'s */
    private Map<String,ControlFactory<?,?>> controlFactories = new HashMap<String, ControlFactory<?,?>>();

    /** The map of registered {@link org.apache.directory.shared.ldap.codec.api.ExtendedRequestFactory}'s by request OID */
    private Map<String,ExtendedRequestFactory<?,?>> extReqFactories = new HashMap<String, ExtendedRequestFactory<?,?>>();

    /** The map of registered {@link UnsolicitedResponseFactory}'s by request OID */
    private Map<String,UnsolicitedResponseFactory<?>> unsolicitedFactories = new HashMap<String, UnsolicitedResponseFactory<?>>();


    /**
     * Creates a new instance of DefaultLdapCodecService.
     */
    DefaultLdapCodecService()
    {
        loadStockControls();
    }


    /**
     * Loads the Controls implement out of the box in the codec.
     */
    private void loadStockControls()
    {
        ControlFactory<?, ?> factory = new CascadeFactory( this );
        controlFactories.put( factory.getOid(), factory );
        LOG.info( "Registered pre-bundled control factory: {}", factory.getOid() );

        factory = new EntryChangeFactory( this );
        controlFactories.put( factory.getOid(), factory );
        LOG.info( "Registered pre-bundled control factory: {}", factory.getOid() );

        factory = new ManageDsaITFactory( this );
        controlFactories.put( factory.getOid(), factory );
        LOG.info( "Registered pre-bundled control factory: {}", factory.getOid() );

        factory = new PagedResultsFactory( this );
        controlFactories.put( factory.getOid(), factory );
        LOG.info( "Registered pre-bundled control factory: {}", factory.getOid() );

        factory = new PersistentSearchFactory( this );
        controlFactories.put( factory.getOid(), factory );
        LOG.info( "Registered pre-bundled control factory: {}", factory.getOid() );

        factory = new SubentriesFactory( this );
        controlFactories.put( factory.getOid(), factory );
        LOG.info( "Registered pre-bundled control factory: {}", factory.getOid() );
    }


    //-------------------------------------------------------------------------
    // LdapCodecService implementation methods
    //-------------------------------------------------------------------------


    /**
     * {@inheritDoc}
     */
    public ControlFactory<?, ?> registerControl( ControlFactory<?,?> factory )
    {
        return controlFactories.put(factory.getOid(), factory);
    }


    /**
     * {@inheritDoc}
     */
    public ControlFactory<?, ?>  unregisterControl( String oid )
    {
        return controlFactories.remove( oid );
    }


    /**
     * {@inheritDoc}
     */
    public Iterator<String> registeredControls()
    {
        return Collections.unmodifiableSet(controlFactories.keySet()).iterator();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isControlRegistered( String oid )
    {
        return controlFactories.containsKey(oid);
    }


    /**
     * {@inheritDoc}
     */
    public Iterator<String> registeredExtendedRequests()
    {
        return Collections.unmodifiableSet( extReqFactories.keySet() ).iterator();
    }


    /**
     * {@inheritDoc}
     */
    public ExtendedRequestFactory<?, ?> registerExtendedRequest( ExtendedRequestFactory<?,?> factory )
    {
        return extReqFactories.put( factory.getOid(), factory );
    }


    /**
     * {@inheritDoc}
     *
     * @TODO - finish this up and add factory registration capabilities,
     * of course there is one default mechanism for now.
     */
    public ProtocolCodecFactory newProtocolCodecFactory( boolean client )
    {
        if ( client )
        {
            return new LdapProtocolCodecFactory( this );
        }
        else
        {
            throw new NotImplementedException(
                "Filters may be different here, and we're probably going to " +
                "want to have a protocol codec factory registration mechanism" +
                "since this way we can swap in and out MINA/Grizzly" );
        }
    }


    /**
     * {@inheritDoc}
     */
    public CodecControl<? extends Control> newControl( String oid )
    {
        ControlFactory<?,?> factory = controlFactories.get( oid );

        if ( factory == null )
        {
            return new BasicControlDecorator<Control>( this, new OpaqueControl( oid ) );
        }

        return factory.newCodecControl();
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public CodecControl<? extends Control> newControl( Control control )
    {
        if ( control == null )
        {
            throw new NullPointerException( "Control argument was null." );
        }

        // protect agains being multiply decorated
        if ( control instanceof CodecControl )
        {
            return (CodecControl<?> )control;
        }
        
        @SuppressWarnings("rawtypes")
        ControlFactory factory = controlFactories.get( control.getOid() );
        
        if ( factory == null )
        {
            return new BasicControlDecorator<Control>( this, control ); 
        }
        
        return factory.newCodecControl( control );
    }


    /**
     * {@inheritDoc}
     */
    public javax.naming.ldap.Control toJndiControl( Control control ) throws EncoderException
    {
        CodecControl<? extends Control> decorator = newControl(control);
        ByteBuffer bb = ByteBuffer.allocate( decorator.computeLength() );
        decorator.encode( bb );
        bb.flip();
        javax.naming.ldap.BasicControl jndiControl = 
            new javax.naming.ldap.BasicControl( control.getOid(), control.isCritical(), bb.array() );
        return jndiControl;
    }


    /**
     * {@inheritDoc}
     */
    public Control fromJndiControl( javax.naming.ldap.Control control ) throws DecoderException
    {
        @SuppressWarnings("rawtypes")
        ControlFactory factory = controlFactories.get( control.getID() );
        
        if ( factory == null )
        {
            OpaqueControl ourControl = new OpaqueControl( control.getID() );
            ourControl.setCritical( control.isCritical() );
            BasicControlDecorator<Control> decorator = 
                new BasicControlDecorator<Control>( this, ourControl );
            decorator.setValue( control.getEncodedValue() );
            return decorator;
        }
        
        @SuppressWarnings("unchecked")
        CodecControl<? extends Control> ourControl = factory.newCodecControl();
        ourControl.setCritical( control.isCritical() );
        ourControl.setValue( control.getEncodedValue() );
        ourControl.decode( control.getEncodedValue() );
        return ourControl;
    }


    /**
     * {@inheritDoc}
     */
    public Asn1Container newMessageContainer()
    {
        return new LdapMessageContainer<MessageDecorator<? extends Message>>( this );
    }


    /**
     * {@inheritDoc}
     */
    public ExtendedRequestFactory<?, ?> unregisterExtendedRequest( String oid )
    {
        return extReqFactories.remove( oid );
    }


    /**
     * {@inheritDoc}
     */
    public Iterator<String> registeredUnsolicitedResponses()
    {
        return Collections.unmodifiableSet( unsolicitedFactories.keySet() ).iterator();
    }


    /**
     * {@inheritDoc}
     */
    public UnsolicitedResponseFactory<?> registerUnsolicitedResponse( UnsolicitedResponseFactory<?> factory )
    {
        return unsolicitedFactories.put( factory.getOid(), factory );
    }


    /**
     * {@inheritDoc}
     */
    public UnsolicitedResponseFactory<?> unregisterUnsolicitedResponse( String oid )
    {
        return unsolicitedFactories.remove( oid );
    }


    /**
     * {@inheritDoc}
     */
    public javax.naming.ldap.ExtendedResponse toJndi( final ExtendedResponse modelResponse ) throws EncoderException
    {
        throw new NotImplementedException( "Figure out how to transform" );
//        final byte[] encodedValue = new byte[ modelResponse.getEncodedValue().length ];
//        System.arraycopy( modelResponse.getEncodedValue(), 0, encodedValue, 0, modelResponse.getEncodedValue().length );
//        
//        return new javax.naming.ldap.ExtendedResponse()
//        {
//            private static final long serialVersionUID = 2955142105375495493L;
//
//            public String getID()
//            {
//                return modelResponse.getID();
//            }
//
//            public byte[] getEncodedValue()
//            {
//                return encodedValue;
//            }
//        };
    }
    

    /**
     * {@inheritDoc}
     */
    public ExtendedResponse fromJndi( javax.naming.ldap.ExtendedResponse jndiResponse ) throws DecoderException
    {   
        throw new NotImplementedException( "Figure out how to transform" );
//        ExtendedResponse modelResponse;
//        ExtendedRequestFactory<?,?> extendedRequestFactory = extReqFactories.get( jndiResponse.getID() );
//        UnsolicitedResponseFactory<?> unsolicitedResponseFactory = unsolicitedFactories.get( jndiResponse.getID() );
//        
//        if ( unsolicitedResponseFactory != null )
//        {
//            modelResponse = unsolicitedResponseFactory.newResponse( jndiResponse.getEncodedValue() );
//        }
//        else if ( extendedRequestFactory != null )
//        {
//            modelResponse = extendedRequestFactory.newResponse( jndiResponse.getEncodedValue() );
//        }
//        else
//        {
//            modelResponse = new ExtendedResponseImpl( jndiResponse.getID() );
//            modelResponse.setResponseValue( jndiResponse.getEncodedValue() );
//        }
//        
//        return modelResponse;
    }


    /**
     * {@inheritDoc}
     */
    public ExtendedRequest<?> fromJndi( javax.naming.ldap.ExtendedRequest jndiRequest ) throws DecoderException
    {
        ExtendedRequestDecorator<?,?> decorator =
            ( ExtendedRequestDecorator<?, ?> ) newExtendedRequest( jndiRequest.getID(), jndiRequest.getEncodedValue() );
        return decorator;
    }


    /**
     * {@inheritDoc}
     */
    public javax.naming.ldap.ExtendedRequest toJndi( final ExtendedRequest<?> modelRequest ) throws EncoderException
    {
        final String oid = modelRequest.getRequestName();
        final byte[] value;
        
        if ( modelRequest instanceof ExtendedRequestDecorator )
        {
            ExtendedRequestDecorator<?, ?> decorator = ( ExtendedRequestDecorator<?, ?> ) modelRequest;
            value = decorator.getRequestValue();
        }
        else
        {
            // have to ask the factory to decorate for us - can't do it ourselves
            ExtendedRequestFactory<?,?> extendedRequestFactory = extReqFactories.get( modelRequest.getRequestName() );
            ExtendedRequestDecorator<?, ?> decorator = extendedRequestFactory.decorate( modelRequest );
            value = decorator.getRequestValue();
        }
        
        
        javax.naming.ldap.ExtendedRequest jndiRequest = new javax.naming.ldap.ExtendedRequest()
        {
            private static final long serialVersionUID = -4160980385909987475L;

            public String getID()
            {
                return oid;
            }

            public byte[] getEncodedValue()
            {
                return value;
            }

            public javax.naming.ldap.ExtendedResponse createExtendedResponse( String id, byte[] berValue, int offset,
                int length ) throws NamingException
            {
                ExtendedRequestFactory<?,?> factory = extReqFactories.get( modelRequest.getRequestName() );
                
                try
                {
                    final ExtendedResponseDecorator<?> resp = ( ExtendedResponseDecorator<?> ) factory.newResponse( berValue );
                    javax.naming.ldap.ExtendedResponse jndiResponse = new javax.naming.ldap.ExtendedResponse()
                    {
                        private static final long serialVersionUID = -7686354122066100703L;

                        public String getID()
                        {
                            return oid;
                        }

                        public byte[] getEncodedValue()
                        {
                            return resp.getResponseValue();
                        }
                    };
                    
                    return jndiResponse;
                }
                catch ( DecoderException e )
                {
                    NamingException ne = new NamingException( "Unable to decode encoded response value: " + 
                        Strings.dumpBytes( berValue ) );
                    ne.setRootCause( e );
                    throw ne;
                }
            }
        };

        return jndiRequest;
    }


    /**
     * {@inheritDoc}
     */
    /**
     * {@inheritDoc}
     * @throws DecoderException 
     */
    @SuppressWarnings("unchecked")
    public <E extends ExtendedResponse> E newExtendedResponse( ExtendedRequest<E> req, byte[] serializedResponse ) throws DecoderException
    {
        ExtendedResponseDecorator<ExtendedResponse> resp;
        
        ExtendedRequestFactory<?,?> extendedRequestFactory = extReqFactories.get( req.getRequestName() );
        if ( extendedRequestFactory != null )
        {
            resp = ( ExtendedResponseDecorator<ExtendedResponse> ) extendedRequestFactory.newResponse( serializedResponse );
        }
        else
        {
            resp = new ExtendedResponseDecorator<ExtendedResponse>( this, 
                new ExtendedResponseImpl( req.getRequestName() ) );
            resp.setResponseValue( serializedResponse );
            resp.setResponseName( req.getRequestName() );
        }
        
        return ( E ) resp;
    }


    /**
     * {@inheritDoc}
     */
    public ExtendedRequest<?> newExtendedRequest( String oid, byte[] value )
    {
        ExtendedRequest<?> req = null;
        
        ExtendedRequestFactory<?,?> extendedRequestFactory = extReqFactories.get( oid );
        if ( extendedRequestFactory != null )
        {
            req = extendedRequestFactory.newRequest( value );
        }
        else
        {
            ExtendedRequestDecorator<ExtendedRequest<ExtendedResponse>, ExtendedResponse> decorator = 
                new ExtendedRequestDecorator<ExtendedRequest<ExtendedResponse>, ExtendedResponse>( this, 
                    new ExtendedRequestImpl() );
            decorator.setRequestName( oid );
            decorator.setRequestValue( value );
            req = decorator;
        }
        
        return req;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public ExtendedRequestDecorator<?, ?> decorate( ExtendedRequest<?> decoratedMessage )
    {
        ExtendedRequestDecorator<?,?> req = null;
        
        ExtendedRequestFactory<?,?> extendedRequestFactory = extReqFactories.get( decoratedMessage.getRequestName() );
        if ( extendedRequestFactory != null )
        {
            req = extendedRequestFactory.decorate( decoratedMessage );
        }
        else
        {
            req = new ExtendedRequestDecorator<ExtendedRequest<ExtendedResponse>, ExtendedResponse>( this, 
                    ( ExtendedRequest<ExtendedResponse> ) decoratedMessage );
        }
        
        return req;
    }


    /**
     * {@inheritDoc}
     */
    public ExtendedResponseDecorator<?> decorate( ExtendedResponse decoratedMessage )
    {
        ExtendedResponseDecorator<?> resp = null;
        
        UnsolicitedResponseFactory<?> unsolicitedResponseFactory = unsolicitedFactories.get( decoratedMessage.getResponseName() );
        ExtendedRequestFactory<?,?> extendedRequestFactory = extReqFactories.get( decoratedMessage.getResponseName() );
        if ( extendedRequestFactory != null )
        {
            resp = extendedRequestFactory.decorate( decoratedMessage );
        }
        else if ( unsolicitedResponseFactory != null )
        {
            resp = unsolicitedResponseFactory.decorate( decoratedMessage );
        }
        else
        {
            resp = new ExtendedResponseDecorator<ExtendedResponse>( this, decoratedMessage );
        }
        
        return resp;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isExtendedOperationRegistered( String oid )
    {
        return extReqFactories.containsKey( oid ) || unsolicitedFactories.containsKey( oid );
    }
}
