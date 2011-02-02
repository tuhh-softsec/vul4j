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
package org.apache.directory.shared.ldap.codec.controls.replication.syncStateValue;


import java.nio.ByteBuffer;

import javax.naming.ldap.BasicControl;
import javax.naming.ldap.Control;

import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.codec.api.IControlFactory;
import org.apache.directory.shared.ldap.codec.api.ILdapCodecService;


/**
 * A {@link IControlFactory} which creates {@link ISyncStateValue} controls.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SyncStateValueFactory implements IControlFactory<ISyncStateValue, SyncStateValueDecorator>
{
    private ILdapCodecService codec;
    

    /**
     * Creates a new instance of SyncStateValueFactory.
     *
     */
    public SyncStateValueFactory( ILdapCodecService codec )
    {
        this.codec = codec;
    }
    

    /**
     * 
     * {@inheritDoc}
     */
    public String getOid()
    {
        return ISyncStateValue.OID;
    }

    
    /**
     * 
     * {@inheritDoc}
     */
    public SyncStateValueDecorator newCodecControl()
    {
        return new SyncStateValueDecorator( codec );
    }
    

    public SyncStateValueDecorator decorate( ISyncStateValue control )
    {
        SyncStateValueDecorator decorator = null;
        
        // protect against double decoration
        if ( control instanceof SyncStateValueDecorator )
        {
            decorator = ( SyncStateValueDecorator ) control;
        }
        else
        {
            decorator = new SyncStateValueDecorator( codec, control );
        }
        
        return decorator;
    }

    
    public ISyncStateValue newControl()
    {
        return new SyncStateValue();
    }
    

    public Control toJndiControl( ISyncStateValue control ) throws EncoderException
    {
        SyncStateValueDecorator decorator = decorate( control );
        ByteBuffer bb = ByteBuffer.allocate( decorator.computeLength() );
        decorator.encode( bb );
        bb.flip();
        return new BasicControl( control.getOid(), control.isCritical(), decorator.getValue() );
    }

    
    public ISyncStateValue fromJndiControl( Control jndi ) throws DecoderException
    {
        SyncStateValueDecorator decorator = newCodecControl();
        decorator.setCritical( jndi.isCritical() );
        decorator.setValue( jndi.getEncodedValue() );
        byte[] controlBytes = new byte[ decorator.computeLength() ];
        decorator.decode( controlBytes );
        return decorator.getDecorated();
    }
}
