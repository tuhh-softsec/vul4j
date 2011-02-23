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
package org.apache.directory.shared.ldap.codec.api;


import java.util.Iterator;

import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.Asn1Container;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.ExtendedRequest;
import org.apache.directory.shared.ldap.model.message.ExtendedResponse;
import org.apache.mina.filter.codec.ProtocolCodecFactory;


/**
 * The service interface for the LDAP codec.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface LdapCodecService
{
    
    // ------------------------------------------------------------------------
    // Control Methods
    // ------------------------------------------------------------------------

    
    /**
     * Returns an Iterator over the OID Strings of registered controls.
     * 
     * @return The registered control OID Strings
     */
    Iterator<String> registeredControls();
    
    
    /**
     * Checks if a control has been registered.
     * 
     * @return The OID of the control to check for registration
     */
    boolean isControlRegistered( String oid );
    
    
    /**
     * Registers an {@link ControlFactory} with this service.
     * 
     * @param factory The control factory
     */
    ControlFactory<?,?> registerControl( ControlFactory<?,?> factory );
    
    
    /**
     * Unregisters an {@link ControlFactory} with this service.
     * 
     * @param oid The oid of the control the factory is associated with.
     */
    ControlFactory<?,?> unregisterControl( String oid );
    
    
    /**
     * Creates a new codec control decorator of the specified type.
     *
     * @param oid The OID of the new control to create.
     * @return The newly created codec control.
     */
    CodecControl<? extends Control> newControl( String oid );
    

    /**
     * Creates a new codec control decorator for the provided control.
     *
     * @param control The control the codec control is generated for.
     * @return The newly created codec control.
     */
    CodecControl<? extends Control> newControl( Control control );
    
    
    /**
     * Creates a JNDI control from the ldap model's control.
     *
     * @param modelControl The model's control.
     * @return The JNDI control.
     * @throws EncoderException if there are problems encoding the modelControl.
     */
    javax.naming.ldap.Control toJndiControl( Control modelControl ) throws EncoderException;
    
    
    /**
     * Creates a model control from the JNDI control.
     *
     * @param jndiControl The JNDI control.
     * @return The model control.
     * @throws DecoderException if there are problems decoding the value of the JNDI control.
     */
    Control fromJndiControl( javax.naming.ldap.Control jndiControl ) throws DecoderException;

    
    // ------------------------------------------------------------------------
    // Extended Request Methods
    // ------------------------------------------------------------------------

    
    /**
     * Returns an Iterator over the OID Strings of registered extended 
     * requests.
     *
     * @return The registered extended request OID Strings
     */
    Iterator<String> registeredExtendedRequests();
    
    
    /**
     * Registers an {@link ExtendedRequestFactory} for generating extended request 
     * response pairs.
     * 
     * @param factory The extended request factory
     * @return The displaced factory if one existed for the oid
     */
    ExtendedRequestFactory<?,?> registerExtendedRequest( ExtendedRequestFactory<?,?> factory );
    
    
    /**
     * Unregisters an {@link ExtendedRequestFactory} for generating extended 
     * request response pairs.
     * 
     * @param oid The extended request oid
     * @return The displaced factory if one existed for the oid
     */
    ExtendedRequestFactory<?,?> unregisterExtendedRequest( String oid );
    
    
    // ------------------------------------------------------------------------
    // Extended Response Methods
    // ------------------------------------------------------------------------

    
    /**
     * Returns an Iterator over the OID Strings of registered unsolicited 
     * extended responses.
     *
     * @return The registered unsolicited extended response OID Strings
     */
    Iterator<String> registeredUnsolicitedResponses();
    
    
    /**
     * Registers an {@link UnsolicitedResponseFactory} for generating extended
     * responses sent by servers without an extended request.
     * 
     * @param factory The unsolicited response creating factory
     * @return The displaced factory if one existed for the oid
     */
    UnsolicitedResponseFactory<?> registerUnsolicitedResponse( UnsolicitedResponseFactory<?> factory );

    
    /**
     * Unregisters an {@link UnsolicitedResponseFactory} for generating 
     * extended responses sent by servers without an extended request.
     * 
     * @param oid The unsolicited response oid
     */
    UnsolicitedResponseFactory<?> unregisterUnsolicitedResponse( String oid );

    
    /**
     * Creates a model ExtendedResponse from the JNDI ExtendedResponse.
     *
     * @param jndiResponse The JNDI ExtendedResponse 
     * @return The model ExtendedResponse
     * @throws DecoderException if the response value cannot be decoded.
     */
    ExtendedResponse fromJndi( javax.naming.ldap.ExtendedResponse jndiResponse ) throws DecoderException;
    
    
    /**
     * Creates a JNDI {@link javax.naming.ldap.ExtendedResponse} from the model 
     * {@link ExtendedResponse}.
     * 
     * @param modelResponse
     * @return
     * @throws EncoderException
     */
    javax.naming.ldap.ExtendedResponse toJndi( ExtendedResponse modelResponse ) throws EncoderException;

    
    /**
     * Creates a model ExtendedResponse from the JNDI ExtendedResponse.
     *
     * @param jndiResponse The JNDI ExtendedResponse 
     * @return The model ExtendedResponse
     * @throws DecoderException if the response value cannot be decoded.
     */
    ExtendedRequest<?> fromJndi( javax.naming.ldap.ExtendedRequest jndiRequest ) throws DecoderException;
    
    
    /**
     * Creates a JNDI {@link javax.naming.ldap.ExtendedResponse} from the model 
     * {@link ExtendedResponse}.
     * 
     * @param modelResponse
     * @return
     * @throws EncoderException
     */
    javax.naming.ldap.ExtendedRequest toJndi( ExtendedRequest<?> modelRequest ) throws EncoderException;
    
    
    // ------------------------------------------------------------------------
    // Other Methods
    // ------------------------------------------------------------------------

    
    /**
     * Creates a new LDAP {@link ProtocolCodecFactory}.
     *
     * @param client if true a factory designed for clients is returned, 
     * otherwise one for servers is returned.
     * @return the client or server specific {@link ProtocolCodecFactory}
     */
    ProtocolCodecFactory newProtocolCodecFactory( boolean client );

    
    /**
     * Creates a new MessageContainer.
     *
     * @TODO akarasulu - Wondering why is this not an LdapMessageContainer?
     * @return The newly created LDAP MessageContainer instance.
     */
    Asn1Container newMessageContainer();


    <E extends ExtendedResponse> E newExtendedResponse( ExtendedRequest<E> req, byte[] serializedResponse ) throws DecoderException;


    /**
     * Creates a new ExtendedRequest instance.
     * 
     * @param oid the extended request's object identifier
     * @param value the encoded value of the extended request
     * @return The new extended request
     */
    ExtendedRequest<?> newExtendedRequest( String oid, byte[] value );
}
