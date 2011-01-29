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

import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.mina.filter.codec.ProtocolCodecFactory;


/**
 * The LdapCodec interface.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface ILdapCodecService
{
    Iterator<String> controlOids();
    
    
    Iterator<String> extendedRequestOids();
    
    
    Iterator<String> extendedResponseOids();
    
    
    void registerControl( IControlFactory<?,?> factory );
    
    
    void registerExtendedOp( IExtendedOpFactory<?,?> factory );
    
    
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
}
