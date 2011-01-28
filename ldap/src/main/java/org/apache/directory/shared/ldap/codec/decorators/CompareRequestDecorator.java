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
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.model.entry.BinaryValue;
import org.apache.directory.shared.ldap.model.entry.Value;
import org.apache.directory.shared.ldap.model.message.CompareRequest;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.util.Strings;


/**
 * A decorator for the CompareRequest message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CompareRequestDecorator extends SingleReplyRequestDecorator implements CompareRequest
{
    /** The bytes of the attribute id used in the comparison */
    private byte[] attrIdBytes;

    /** The bytes of the attribute value used in the comparison */
    private byte[] attrValBytes;

    /** The compare request length */
    private int compareRequestLength;

    /** The attribute value assertion length */
    private int avaLength;


    /**
     * Makes a CompareRequest a MessageDecorator.
     *
     * @param decoratedMessage the decorated CompareRequest
     */
    public CompareRequestDecorator( CompareRequest decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated CompareRequest
     */
    public CompareRequest getCompareRequest()
    {
        return ( CompareRequest ) getDecoratedMessage();
    }


    /**
     * Stores the encoded length for the CompareRequest
     * @param compareRequestLength The encoded length
     */
    public void setCompareRequestLength( int compareRequestLength )
    {
        this.compareRequestLength = compareRequestLength;
    }


    /**
     * @return The encoded CompareRequest length
     */
    public int getCompareRequestLength()
    {
        return compareRequestLength;
    }


    /**
     * Stores the encoded length for the ava
     * @param avaLength The encoded length
     */
    public void setAvaLength( int avaLength )
    {
        this.avaLength = avaLength;
    }


    /**
     * @return The encoded ava length
     */
    public int getAvaLength()
    {
        return avaLength;
    }


    /**
     * Gets the attribute id bytes use in making the comparison.
     *
     * @return the attribute id bytes used in comparison.
     */
    public byte[] getAttrIdBytes()
    {
        return attrIdBytes;
    }


    /**
     * Sets the attribute id bytes used in the comparison.
     *
     * @param attrIdBytes the attribute id bytes used in comparison.
     */
    public void setAttrIdBytes( byte[] attrIdBytes )
    {
        this.attrIdBytes = attrIdBytes;
    }


    /**
     * Gets the attribute value bytes use in making the comparison.
     *
     * @return the attribute value bytes used in comparison.
     */
    public byte[] getAttrValBytes()
    {
        return attrValBytes;
    }


    /**
     * Sets the attribute value bytes used in the comparison.
     *
     * @param attrValBytes the attribute value bytes used in comparison.
     */
    public void setAttrValBytes( byte[] attrValBytes )
    {
        this.attrValBytes = attrValBytes;
    }


    //-------------------------------------------------------------------------
    // The CompareRequest methods
    //-------------------------------------------------------------------------

    
    /**
     * {@inheritDoc}
     */
    public Dn getName()
    {
        return getCompareRequest().getName();
    }


    /**
     * {@inheritDoc}
     */
    public void setName( Dn name )
    {
        getCompareRequest().setName( name );
    }


    /**
     * {@inheritDoc}
     */
    public Value<?> getAssertionValue()
    {
        return getCompareRequest().getAssertionValue();
    }


    /**
     * {@inheritDoc}
     */
    public void setAssertionValue( String value )
    {
        getCompareRequest().setAssertionValue( value );
    }


    /**
     * {@inheritDoc}
     */
    public void setAssertionValue( byte[] value )
    {
        getCompareRequest().setAssertionValue( value );
    }


    /**
     * {@inheritDoc}
     */
    public String getAttributeId()
    {
        return getCompareRequest().getAttributeId();
    }


    /**
     * {@inheritDoc}
     */
    public void setAttributeId( String attrId )
    {
        getCompareRequest().setAttributeId( attrId );
    }

    
    //-------------------------------------------------------------------------
    // The Decorator methods
    //-------------------------------------------------------------------------
    /**
     * Compute the CompareRequest length 
     * 
     * CompareRequest : 
     * 0x6E L1 
     *   | 
     *   +--> 0x04 L2 entry 
     *   +--> 0x30 L3 (ava) 
     *         | 
     *         +--> 0x04 L4 attributeDesc 
     *         +--> 0x04 L5 assertionValue 
     *         
     * L3 = Length(0x04) + Length(L4) + L4 + Length(0x04) +
     *      Length(L5) + L5 
     * Length(CompareRequest) = Length(0x6E) + Length(L1) + L1 +
     *      Length(0x04) + Length(L2) + L2 + Length(0x30) + Length(L3) + L3
     * 
     * @return The CompareRequest PDU's length
     */
    public int computeLength()
    {
        // The entry Dn
        Dn entry = getName();
        int compareRequestLength = 1 + TLV.getNbBytes( Dn.getNbBytes( entry ) ) + Dn.getNbBytes( entry );

        // The attribute value assertion
        byte[] attributeIdBytes = Strings.getBytesUtf8( getAttributeId() );
        int avaLength = 1 + TLV.getNbBytes( attributeIdBytes.length ) + attributeIdBytes.length;
        setAttrIdBytes( attributeIdBytes );

        org.apache.directory.shared.ldap.model.entry.Value<?> assertionValue = getAssertionValue();

        if ( assertionValue instanceof BinaryValue )
        {
            byte[] value = getAssertionValue().getBytes();
            avaLength += 1 + TLV.getNbBytes( value.length ) + value.length;
            setAttrValBytes( value );
        }
        else
        {
            byte[] value = Strings.getBytesUtf8( getAssertionValue().getString() );
            avaLength += 1 + TLV.getNbBytes( value.length ) + value.length;
            setAttrValBytes( value );
        }

        setAvaLength( avaLength );
        compareRequestLength += 1 + TLV.getNbBytes( avaLength ) + avaLength;
        setCompareRequestLength( compareRequestLength );

        return 1 + TLV.getNbBytes( compareRequestLength ) + compareRequestLength;
    }


    /**
     * Encode the CompareRequest message to a PDU. 
     * 
     * CompareRequest : 
     *   0x6E LL 
     *     0x04 LL entry 
     *     0x30 LL attributeValueAssertion 
     *       0x04 LL attributeDesc 
     *       0x04 LL assertionValue
     * 
     * @param buffer The buffer where to put the PDU
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        try
        {
            // The CompareRequest Tag
            buffer.put( LdapConstants.COMPARE_REQUEST_TAG );
            buffer.put( TLV.getBytes( getCompareRequestLength() ) );

            // The entry
            org.apache.directory.shared.asn1.ber.tlv.Value.encode( buffer, Dn.getBytes( getName() ) );

            // The attributeValueAssertion sequence Tag
            buffer.put( UniversalTag.SEQUENCE.getValue() );
            buffer.put( TLV.getBytes( getAvaLength() ) );
        }
        catch ( BufferOverflowException boe )
        {
            throw new EncoderException( I18n.err( I18n.ERR_04005 ) );
        }

        // The attributeDesc
        org.apache.directory.shared.asn1.ber.tlv.Value.encode( buffer, getAttrIdBytes() );

        // The assertionValue
        org.apache.directory.shared.asn1.ber.tlv.Value.encode( buffer, ( byte[] ) getAttrValBytes() );
        
        return buffer;
    }
 }
