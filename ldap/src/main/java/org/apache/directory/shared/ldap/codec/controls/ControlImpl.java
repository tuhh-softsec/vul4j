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


import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.asn1.codec.EncoderException;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A genericcodec Control.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ControlImpl extends AbstractControl 
{
    /** The logger */
    static final Logger LOG = LoggerFactory.getLogger( ControlImpl.class );

    /** Speedup for logs */
    static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /**
     * Default constructor.
     */
    public ControlImpl( String oid )
    {
        super( oid );
        
        decoder = null;
    }

    
    /**
     * Set the encoded control value
     * 
     * @param encodedValue The encoded control value to store
     */
    public void setValue( byte[] value )
    {
        if ( value != null )
        {
            if ( IS_DEBUG )
            {
                LOG.debug( "Control payoad for {} : {}", getOid(), StringTools.dumpBytes( value ) );
            }
            
            this.value = new byte[ value.length ];
            System.arraycopy( value, 0, this.value, 0, value.length );
        } 
        else 
        {
            this.value = null;
        }
    }


    /**
     * Get the raw control encoded bytes
     * 
     * @return the encoded bytes for the control
     */
    public byte[] getValue()
    {
        if ( value == null )
        {
            return StringTools.EMPTY_BYTES;
        }

        final byte[] copy = new byte[ value.length ];
        System.arraycopy( value, 0, copy, 0, value.length );
        return copy;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int computeLength()
    {
        if ( value != null )
        {
            return super.computeLength( value.length );
        }
        else
        {
            return super.computeLength( 0 );
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        if ( buffer == null )
        {
            throw new EncoderException( I18n.err( I18n.ERR_04023 ) );
        }

        // Encode the Control envelop
        super.encode( buffer );
        
        // If we have a value, encode it
        if ( value != null )
        {
            Value.encode( buffer, value );
        }

        return buffer;
    }
}
