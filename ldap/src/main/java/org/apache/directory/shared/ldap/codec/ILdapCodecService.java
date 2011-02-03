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
package org.apache.directory.shared.ldap.codec;


import java.util.Iterator;

import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.mina.filter.codec.ProtocolCodecFactory;


/**
 * The service interface for the LDAP codec.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface ILdapCodecService
{
    /**
     * Returns an Iterator over the OID Strings of registered controls.
     * 
     * @return The registered control OID Strings
     */
    Iterator<String> registeredControls();
    
    
    /**
     * Returns an Iterator over the OID Strings of registered extended 
     * requests.
     *
     * @return The registered extended request OID Strings
     */
    Iterator<String> registeredExtendedRequests();
    
    
    /**
     * Returns an Iterator over the OID Strings of registered extended 
     * responses.
     *
     * @return The registered extended response OID Strings
     */
    Iterator<String> extendedResponseOids();
    
    
    /**
     * Registers an {@link IControlFactory} with this service.
     * 
     * @param factory The control factory
     */
    void registerControl( IControlFactory<?,?> factory );
    
    
    /**
     * Registers an {@link IExtendedOpFactory} for generating extended request 
     * response pairs.
     * 
     * @param factory The extended operation factory
     */
    void registerExtendedOp( IExtendedOpFactory<?,?> factory );
    
    
    /**
     * Creates a new Control instance
     * 
     * @param <E>
     * @param clazz
     * @return
     */
    <E> E newControl( Class<? extends Control> clazz );
    
    
    <E> E newControl( String oid );
    

    ICodecControl<? extends Control> decorate( Control control );
    
    
    <E> E newCodecControl( Class<? extends ICodecControl<? extends Control>> clazz );
    
    
    /**
     * Creates a new LDAP {@link ProtocolCodecFactory}.
     *
     * @param client if true a factory designed for clients is returned, 
     * otherwise one for servers is returned.
     * @return the client or server specific {@link ProtocolCodecFactory}
     */
    ProtocolCodecFactory newProtocolCodecFactory( boolean client );
    
    
    javax.naming.ldap.Control toJndiControl( Control control ) throws EncoderException;
    
    Control fromJndiControl( javax.naming.ldap.Control control ) throws DecoderException;
}
