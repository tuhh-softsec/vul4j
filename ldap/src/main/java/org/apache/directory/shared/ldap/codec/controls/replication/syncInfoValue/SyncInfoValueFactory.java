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
package org.apache.directory.shared.ldap.codec.controls.replication.syncInfoValue;


import java.nio.ByteBuffer;

import javax.naming.ldap.BasicControl;
import javax.naming.ldap.Control;

import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.codec.IControlFactory;
import org.apache.directory.shared.ldap.codec.ILdapCodecService;
import org.apache.directory.shared.ldap.model.message.controls.SyncInfoValue;
import org.apache.directory.shared.ldap.model.message.controls.SyncInfoValueImpl;


/**
 * A {@link IControlFactory} which creates {@link SyncInfoValue} controls.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SyncInfoValueFactory implements IControlFactory<SyncInfoValue, SyncInfoValueDecorator>
{
    
    private ILdapCodecService codec;
    

    /**
     * Creates a new instance of SyncInfoValueFactory.
     *
     */
    public SyncInfoValueFactory( ILdapCodecService codec )
    {
        this.codec = codec;
    }
    

    /**
     * 
     * {@inheritDoc}
     */
    public String getOid()
    {
        return SyncInfoValue.OID;
    }

    
    /**
     * 
     * {@inheritDoc}
     */
    public SyncInfoValueDecorator newCodecControl()
    {
        return new SyncInfoValueDecorator( codec );
    }
    

    public SyncInfoValueDecorator decorate( SyncInfoValue control )
    {
        SyncInfoValueDecorator decorator = null;
        
        // protect against double decoration
        if ( control instanceof SyncInfoValueDecorator )
        {
            decorator = ( SyncInfoValueDecorator ) control;
        }
        else
        {
            decorator = new SyncInfoValueDecorator( codec, control );
        }
        
        return decorator;
    }

    
    public SyncInfoValue newControl()
    {
        return new SyncInfoValueImpl();
    }
    

    public Control toJndiControl( SyncInfoValue control ) throws EncoderException
    {
        SyncInfoValueDecorator decorator = decorate( control );
        ByteBuffer bb = ByteBuffer.allocate( decorator.computeLength() );
        decorator.encode( bb );
        bb.flip();
        return new BasicControl( control.getOid(), control.isCritical(), decorator.getValue() );
    }

    
    public SyncInfoValue fromJndiControl( Control jndi ) throws DecoderException
    {
        SyncInfoValueDecorator decorator = newCodecControl();
        decorator.setCritical( jndi.isCritical() );
        decorator.setValue( jndi.getEncodedValue() );
        byte[] controlBytes = new byte[ decorator.computeLength() ];
        decorator.decode( controlBytes );
        return decorator.getDecorated();
    }

}
