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
import org.apache.directory.shared.ldap.model.message.controls.Cascade;


/**
 * The Cascade control decorator.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CascadeDecorator extends ControlDecorator<Cascade> implements CascadeCodecControl
{
    /**
     * Default constructor
     */
    public CascadeDecorator()
    {
        super( Cascade.INSTANCE );
    }

    
    /**
     * Returns the default control length.
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
