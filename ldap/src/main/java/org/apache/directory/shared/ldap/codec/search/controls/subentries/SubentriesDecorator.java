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
package org.apache.directory.shared.ldap.codec.search.controls.subentries;


import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.controls.ControlDecorator;
import org.apache.directory.shared.ldap.model.message.controls.SimpleSubentries;
import org.apache.directory.shared.ldap.model.message.controls.Subentries;


/**
 * A Subentries Control implementation which wraps and decorates Subentries
 * Controls to enable them to be encoded and decoded by the codec.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SubentriesDecorator extends ControlDecorator implements Subentries
{
    /**
     * Default constructor
     */
    public SubentriesDecorator()
    {
        this( new SimpleSubentries() );
    }


    /**
     * Creates a Subentries decorating implementation for use with the codec,
     * while decorating the supplied Subentries control.
     *
     * @param control The Subentries Control to wrap with this decorator.
     */
    public SubentriesDecorator( Subentries control )
    {
        super( control, new SubentriesDecoder() );
    }


    public Subentries getSubentries()
    {
        return ( Subentries ) getDecorated();
    }


    /**
     * Compute the SubEntryControl length 0x01 0x01 [0x00|0xFF]
     */
    public int computeLength()
    {
        int subentriesLength =  1 + 1 + 1;
        int valueLength = subentriesLength;

        // Call the super class to compute the global control length
        return super.computeLength( valueLength );
    }


    /**
     * Encodes the Subentries control.
     * 
     * @param buffer The encoded sink
     * @return A ByteBuffer that contains the encoded PDU
     * @throws org.apache.directory.shared.asn1.EncoderException If anything goes wrong.
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        if ( buffer == null )
        {
            throw new EncoderException( I18n.err( I18n.ERR_04023 ) );
        }

        // Encode the Control envelop
        super.encode( buffer );
        
        // Encode the OCTET_STRING tag
        buffer.put( UniversalTag.OCTET_STRING.getValue() );
        buffer.put( TLV.getBytes( valueLength ) );

        // Now encode the Subentries specific part
        Value.encode( buffer, isVisible() );

        return buffer;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public byte[] getValue()
    {
        if ( getDecorated().getValue() == null )
        {
            try
            { 
                computeLength();
                ByteBuffer buffer = ByteBuffer.allocate( valueLength );
                
                // Now encode the Subentries specific part
                Value.encode( buffer, isVisible() );
                
                getDecorated().setValue( buffer.array() );
            }
            catch ( Exception e )
            {
                return null;
            }
        }
        
        return getDecorated().getValue();
    }


    public boolean isVisible()
    {
        return getSubentries().isVisible();
    }


    public void setVisibility( boolean visibility )
    {
        getSubentries().setVisibility( visibility );
    }
}
