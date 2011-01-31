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


import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.Asn1Object;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.model.message.controls.BasicControl;


/**
 * A decorator for an opaque control where we know nothing about encoded values.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BasicControlDecorator implements ICodecControl<BasicControl>, IDecorator<BasicControl>
{
    private byte[] value;
    
    private BasicControl control;
    private ILdapCodecService codec;
    
    
    public BasicControlDecorator( ILdapCodecService codec, BasicControl control )
    {
        this.codec = codec;
        this.control = control;
    }
    
    
    public String getOid()
    {
        return control.getOid();
    }

    
    public boolean isCritical()
    {
        return control.isCritical();
    }

    
    public void setCritical( boolean isCritical )
    {
        control.setCritical( isCritical );
    }

    
    public BasicControl getDecorated()
    {
        return control;
    }

    
    public int computeLength()
    {
        return 0;
    }

    
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        return null;
    }

    
    public ILdapCodecService getCodecService()
    {
        return codec;
    }

    
    /**
     * {@inheritDoc}
     */
    public Asn1Object decode( byte[] controlBytes ) throws DecoderException
    {
        return null;
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean hasValue()
    {
        return value != null;
    }
    

    /**
     * {@inheritDoc}
     */
    public byte[] getValue()
    {
        return value;
    }
    

    /**
     * {@inheritDoc}
     */
    public void setValue( byte[] value )
    {
        this.value = value;
    }
}
