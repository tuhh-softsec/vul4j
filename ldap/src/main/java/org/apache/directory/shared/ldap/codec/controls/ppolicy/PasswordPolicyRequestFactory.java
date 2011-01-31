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
package org.apache.directory.shared.ldap.codec.controls.ppolicy;


import java.nio.ByteBuffer;

import javax.naming.ldap.BasicControl;
import javax.naming.ldap.Control;

import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.codec.IControlFactory;
import org.apache.directory.shared.ldap.codec.ILdapCodecService;


/**
 * A {@link IControlFactory} implementation producing {@link IPasswordPolicyRequest} 
 * controls.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PasswordPolicyRequestFactory 
    implements IControlFactory<IPasswordPolicyRequest, PasswordPolicyRequestDecorator>
{
    /** The LDAP codec service */
    private ILdapCodecService codec;
    

    /**
     * Creates a new instance of PasswordPolicyRequestFactory.
     *
     * @param codec The LDAP codec service
     */
    public PasswordPolicyRequestFactory( ILdapCodecService codec )
    {
        this.codec = codec;
    }
    

    /**
     * 
     * {@inheritDoc}
     */
    public String getOid()
    {
        return IPasswordPolicyRequest.OID;
    }

    
    /**
     * 
     * {@inheritDoc}
     */
    public PasswordPolicyRequestDecorator newCodecControl()
    {
        return new PasswordPolicyRequestDecorator( codec, new PasswordPolicyRequest() );
    }

    
    /**
     * 
     * {@inheritDoc}
     */
    public PasswordPolicyRequestDecorator decorate( IPasswordPolicyRequest modelControl )
    {
        return new PasswordPolicyRequestDecorator( codec, modelControl );
    }
    

    /**
     * 
     * {@inheritDoc}
     */
    public IPasswordPolicyRequest newControl()
    {
        return new PasswordPolicyRequest();
    }
    
    
    /**
     * 
     * {@inheritDoc}
     */
    public Control toJndiControl( IPasswordPolicyRequest modelControl ) throws EncoderException
    {
        PasswordPolicyRequestDecorator decorator = decorate( modelControl );
        ByteBuffer bb = ByteBuffer.allocate( decorator.computeLength() );
        decorator.encode( bb );
        bb.flip();
        return new BasicControl( modelControl.getOid(), modelControl.isCritical(), decorator.getValue() );
    }

    
    /**
     * 
     * {@inheritDoc}
     */
    public IPasswordPolicyRequest fromJndiControl( Control jndiControl ) throws DecoderException
    {
        PasswordPolicyRequestDecorator decorator = newCodecControl();
        decorator.setCritical( jndiControl.isCritical() );
        decorator.setValue( jndiControl.getEncodedValue() );
        byte[] controlBytes = new byte[ decorator.computeLength() ];
        decorator.decode( controlBytes );
        return decorator.getDecorated();
    }
}
