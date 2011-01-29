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
package org.apache.directory.shared.ldap.codec.controls;


import org.apache.directory.shared.asn1.Asn1Object;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.ldap.model.message.controls.ManageDsaIT;


/**
 * A decorating wrapper for a ManageDsaIT Control.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ManageDsaITDecorator extends ControlDecorator<ManageDsaIT> implements ManageDsaIT
{
    // @TODO We should not bother encoding and decoding marker controls that always
    // encode and decode into the same TLV/byte sequence. Can't the control just
    // supply the canned PDU element?
    //
    // override the decorator component to hard code this control?


    /**
     * Default constructor
     */
    public ManageDsaITDecorator()
    {
        super( ManageDsaIT.INSTANCE );
    }


    /**
     * Returns 0 every time.
     */
    public int computeLength()
    {
        // Call the super class to compute the global control length
        return super.computeLength( 0 );
    }
    

    public Asn1Object decode( byte[] controlBytes ) throws DecoderException  
    {
        return this;
    }
}
