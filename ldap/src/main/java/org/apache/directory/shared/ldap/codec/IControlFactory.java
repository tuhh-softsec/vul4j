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


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.model.message.Control;


/**
 * Implementors of new codec control extensions must implement a factory using
 * this factory interface, Factory implementations for specific controls are
 * then registered with the codec and used by the codec to encode and decode
 * those controls.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface IControlFactory<C extends Control, D extends ICodecControl<C>>
{
    /**
     * @return The OID of the Control this factory creates.
     */
    String getOid();


    /**
     *
     * @return
     */
    D newCodecControl();
    
    D decorate( C modelControl );
    
    C newControl();
    
    javax.naming.ldap.Control toJndiControl( C modelControl ) throws EncoderException;
    
    C fromJndiControl( javax.naming.ldap.Control jndiControl ) throws DecoderException;
}
