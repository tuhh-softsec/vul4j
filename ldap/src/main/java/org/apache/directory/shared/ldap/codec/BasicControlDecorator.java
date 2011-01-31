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


import org.apache.directory.shared.asn1.Asn1Object;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.ldap.codec.controls.ControlDecorator;
import org.apache.directory.shared.ldap.model.message.controls.BasicControl;
import org.apache.directory.shared.util.Strings;


/**
 * A decorator for an opaque control where we know nothing about encoded values.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BasicControlDecorator extends ControlDecorator<BasicControl>
{
    private ILdapCodecService codec;

    /** The control value */
    private byte[] value;

    public BasicControlDecorator( ILdapCodecService codec, BasicControl control )
    {
        super( codec, control );
    }


    @Override
    public Asn1Object decode( byte[] controlBytes ) throws DecoderException
    {
        setValue( controlBytes );
        
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
        if ( ! Strings.isEmpty( value ) )
        {
            byte[] copy = new byte[value.length];
            System.arraycopy( value, 0, copy, 0, value.length );
        }
        
        this.value = value;
    }
}
