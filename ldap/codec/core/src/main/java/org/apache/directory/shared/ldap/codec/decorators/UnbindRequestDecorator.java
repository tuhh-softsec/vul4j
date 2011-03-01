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
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.codec.api.LdapConstants;
import org.apache.directory.shared.ldap.model.message.UnbindRequest;


/**
 * A decorator for the LdapResultResponse message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class UnbindRequestDecorator extends RequestDecorator<UnbindRequest> implements UnbindRequest
{
    /**
     * Makes Request a MessageDecorator.
     *
     * @param decoratedMessage the decorated message
     */
    public UnbindRequestDecorator( LdapCodecService codec, UnbindRequest decoratedMessage )
    {
        super( codec, decoratedMessage );
    }


    //-------------------------------------------------------------------------
    // The Decorator methods
    //-------------------------------------------------------------------------

    
    /**
     * Compute the UnBindRequest length 
     * 
     * UnBindRequest : 
     * 0x42 00
     */
    public int computeLength()
    {
        return 2; // Always 2
    }
    
    
    /**
     * Encode the Unbind protocolOp part
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        try
        {
            // The tag
            buffer.put( LdapConstants.UNBIND_REQUEST_TAG );

            // The length is always null.
            buffer.put( ( byte ) 0 );
        }
        catch ( BufferOverflowException boe )
        {
            String msg = I18n.err( I18n.ERR_04005 );
            throw new EncoderException( msg );
        }
        
        return buffer;
    }
}
