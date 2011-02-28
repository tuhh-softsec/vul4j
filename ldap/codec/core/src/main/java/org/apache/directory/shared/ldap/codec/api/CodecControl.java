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
package org.apache.directory.shared.ldap.codec.api;


import org.apache.directory.shared.asn1.Asn1Object;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.ldap.model.message.Control;


/**
 * The codec uses this interface to add additional information to LDAP Model
 * Control objects during encoding and decoding.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface CodecControl<E extends Control> extends Control, Decorator<E>
{
    /**
     * Decodes raw ASN.1 encoded bytes into an Asn1Object for the control.
     * 
     * @param controlBytes the encoded control bytes
     * @return the decoded Asn1Object for the control
     * @throws DecoderException if anything goes wrong
     */
    Asn1Object decode( byte[] controlBytes ) throws DecoderException;


    /**
     * Checks to see if a value is set for this {@link CodecControl}.
     *
     * @return true, if this control has a value, false otherwise
     */
    boolean hasValue();


    /**
     * Gets the binary ASN.1 BER encoded representation of the control.
     * 
     * @return The control's encoded value
     */
    byte[] getValue();


    /**
     * Set the Control's encoded control value.
     * 
     * @param value The encoded control value to store.
     */
    void setValue( byte[] value );
}
