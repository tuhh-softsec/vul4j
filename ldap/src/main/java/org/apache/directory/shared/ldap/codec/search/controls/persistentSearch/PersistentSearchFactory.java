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
package org.apache.directory.shared.ldap.codec.search.controls.persistentSearch;


import java.nio.ByteBuffer;

import javax.naming.ldap.BasicControl;
import javax.naming.ldap.Control;

import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.codec.IControlFactory;
import org.apache.directory.shared.ldap.codec.ILdapCodecService;
import org.apache.directory.shared.ldap.model.message.controls.PersistentSearch;
import org.apache.directory.shared.ldap.model.message.controls.PersistentSearchImpl;


/**
 * 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PersistentSearchFactory implements IControlFactory<PersistentSearch, PersistentSearchDecorator>
{
    private ILdapCodecService codec;
    
    
    public PersistentSearchFactory( ILdapCodecService codec )
    {
        this.codec = codec;
    }
    
    public String getOid()
    {
        return PersistentSearch.OID;
    }

    
    public PersistentSearchDecorator newCodecControl()
    {
        return new PersistentSearchDecorator( codec );
    }

    
    public PersistentSearchDecorator decorate( PersistentSearch control )
    {
        if ( control instanceof PersistentSearchDecorator )
        {
            return ( PersistentSearchDecorator ) control;
        }
        else 
        {
            return new PersistentSearchDecorator( codec, control );
        }
    }

    
    public PersistentSearch newControl()
    {
        return new PersistentSearchImpl();
    }
    
    
    public Control toJndiControl( PersistentSearch control ) throws EncoderException
    {
        PersistentSearchDecorator decorator = decorate( control );
        ByteBuffer buf = ByteBuffer.allocate( decorator.computeLength() );
        decorator.encode( buf );
        buf.flip();
        BasicControl jndi = new BasicControl( control.getOid(), control.isCritical(), buf.array() );
        return jndi;
    }
    
    
    public PersistentSearch fromJndiControl( Control jndi ) throws DecoderException
    {
        PersistentSearchDecorator decorator = newCodecControl();
        decorator.setCritical( jndi.isCritical() );
        decorator.setValue( jndi.getEncodedValue() );
        byte[] controlBytes = new byte[decorator.computeLength()];
        decorator.decode( controlBytes );
        return decorator.getDecorated();
    }
}
