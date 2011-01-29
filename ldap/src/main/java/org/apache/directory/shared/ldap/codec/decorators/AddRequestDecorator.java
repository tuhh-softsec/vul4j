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
import java.util.LinkedList;
import java.util.List;

import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.ILdapCodecService;
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.model.entry.DefaultEntryAttribute;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.entry.EntryAttribute;
import org.apache.directory.shared.ldap.model.entry.Value;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.message.AddRequest;
import org.apache.directory.shared.ldap.model.name.Dn;


/**
 * A decorator for the AddRequest message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class AddRequestDecorator extends SingleReplyRequestDecorator<AddRequest> implements AddRequest
{
    /** The add request length */
    private int addRequestLength;

    /** The Entry length */
    private int entryLength;

    /** The list of all attributes length */
    private List<Integer> attributesLength;

    /** The list of all vals length */
    private List<Integer> valuesLength;

    /** The current attribute being decoded */
    private EntryAttribute currentAttribute;

    
    /**
     * Makes a AddRequest a MessageDecorator.
     *
     * @param decoratedMessage the decorated AddRequest
     */
    public AddRequestDecorator( ILdapCodecService codec, AddRequest decoratedMessage )
    {
        super( codec, decoratedMessage );
    }


    /**
     * Stores the encoded length for the AddRequest
     * @param addRequestLength The encoded length
     */
    public void setAddRequestLength( int addRequestLength )
    {
        this.addRequestLength = addRequestLength;
    }


    /**
     * @return The encoded AddRequest's length
     */
    public int getAddRequestLength()
    {
        return addRequestLength;
    }


    /**
     * Stores the encoded length for the Entry
     * @param entryLength The encoded length
     */
    public void setEntryLength( int entryLength )
    {
        this.entryLength = entryLength;
    }


    /**
     * @return The encoded Entry's length
     */
    public int getEntryLength()
    {
        return entryLength;
    }


    /**
     * Stores the encoded length for the attributes
     * @param attributesLength The encoded length
     */
    public void setAttributesLength( List<Integer> attributesLength )
    {
        this.attributesLength = attributesLength;
    }


    /**
     * @return The encoded values length
     */
    public List<Integer> getAttributesLength()
    {
        return attributesLength;
    }


    /**
     * Stores the encoded length for the values
     * @param valuesLength The encoded length
     */
    public void setValuesLength( List<Integer> valuesLength )
    {
        this.valuesLength = valuesLength;
    }


    /**
     * @return The encoded values length
     */
    public List<Integer> getValuesLength()
    {
        return valuesLength;
    }


    //-------------------------------------------------------------------------
    // The AddRequest methods
    //-------------------------------------------------------------------------

    
    /**
     * {@inheritDoc}
     */
    public Dn getEntryDn()
    {
        return getDecorated().getEntryDn();
    }


    /**
     * {@inheritDoc}
     */
    public void setEntryDn( Dn entry )
    {
        getDecorated().setEntryDn( entry );
    }


    /**
     * {@inheritDoc}
     */
    public Entry getEntry()
    {
        return getDecorated().getEntry();
    }


    /**
     * {@inheritDoc}
     */
    public void setEntry( Entry entry )
    {
        getDecorated().setEntry( entry );
    }

    
    /**
     * Create a new attributeValue
     * 
     * @param type The attribute's name (called 'type' in the grammar)
     */
    public void addAttributeType( String type ) throws LdapException
    {
        // do not create a new attribute if we have seen this attributeType before
        if ( getDecorated().getEntry().get( type ) != null )
        {
            currentAttribute = getDecorated().getEntry().get( type );
            return;
        }

        // fix this to use AttributeImpl(type.getString().toLowerCase())
        currentAttribute = new DefaultEntryAttribute( type );
        getDecorated().getEntry().put( currentAttribute );
    }


    /**
     * @return Returns the currentAttribute type.
     */
    public String getCurrentAttributeType()
    {
        return currentAttribute.getId();
    }


    /**
     * Add a new value to the current attribute
     * 
     * @param value The value to add
     */
    public void addAttributeValue( String value )
    {
        currentAttribute.add( value );
    }


    /**
     * Add a new value to the current attribute
     * 
     * @param value The value to add
     */
    public void addAttributeValue( Value<?> value )
    {
        currentAttribute.add( value );
    }


    /**
     * Add a new value to the current attribute
     * 
     * @param value The value to add
     */
    public void addAttributeValue( byte[] value )
    {
        currentAttribute.add( value );
    }
    
    
    //-------------------------------------------------------------------------
    // The Decorator methods
    //-------------------------------------------------------------------------
    /**
     * Compute the AddRequest length
     * 
     * AddRequest :
     * 
     * 0x68 L1
     *  |
     *  +--> 0x04 L2 entry
     *  +--> 0x30 L3 (attributes)
     *        |
     *        +--> 0x30 L4-1 (attribute)
     *        |     |
     *        |     +--> 0x04 L5-1 type
     *        |     +--> 0x31 L6-1 (values)
     *        |           |
     *        |           +--> 0x04 L7-1-1 value
     *        |           +--> ...
     *        |           +--> 0x04 L7-1-n value
     *        |
     *        +--> 0x30 L4-2 (attribute)
     *        |     |
     *        |     +--> 0x04 L5-2 type
     *        |     +--> 0x31 L6-2 (values)
     *        |           |
     *        |           +--> 0x04 L7-2-1 value
     *        |           +--> ...
     *        |           +--> 0x04 L7-2-n value
     *        |
     *        +--> ...
     *        |
     *        +--> 0x30 L4-m (attribute)
     *              |
     *              +--> 0x04 L5-m type
     *              +--> 0x31 L6-m (values)
     *                    |
     *                    +--> 0x04 L7-m-1 value
     *                    +--> ...
     *                    +--> 0x04 L7-m-n value
     */
    public int computeLength()
    {
        AddRequest addRequest = getDecorated();
        Entry entry = addRequest.getEntry();

        if ( entry == null )
        {
            throw new IllegalArgumentException( I18n.err( I18n.ERR_04481_ENTRY_NULL_VALUE ) );
        }

        // The entry Dn
        int addRequestLength = 1 + TLV.getNbBytes( Dn.getNbBytes(entry.getDn()) ) + Dn.getNbBytes(entry.getDn());

        // The attributes sequence
        int entryLength = 0;

        if ( entry.size() != 0 )
        {
            List<Integer> attributesLength = new LinkedList<Integer>();
            List<Integer> valuesLength = new LinkedList<Integer>();

            // Compute the attributes length
            for ( EntryAttribute attribute : entry )
            {
                int localAttributeLength = 0;
                int localValuesLength = 0;

                // Get the type length
                int idLength = attribute.getId().getBytes().length;
                localAttributeLength = 1 + TLV.getNbBytes( idLength ) + idLength;

                // The values
                if ( attribute.size() != 0 )
                {
                    localValuesLength = 0;

                    for ( org.apache.directory.shared.ldap.model.entry.Value<?> value : attribute )
                    {
                        int valueLength = value.getBytes().length;
                        localValuesLength += 1 + TLV.getNbBytes( valueLength ) + valueLength;
                    }

                    localAttributeLength += 1 + TLV.getNbBytes( localValuesLength ) + localValuesLength;
                }

                // add the attribute length to the attributes length
                entryLength += 1 + TLV.getNbBytes( localAttributeLength ) + localAttributeLength;

                attributesLength.add( localAttributeLength );
                valuesLength.add( localValuesLength );
            }

            setAttributesLength( attributesLength );
            setValuesLength( valuesLength );
            setEntryLength( entryLength );
        }

        addRequestLength += 1 + TLV.getNbBytes( entryLength ) + entryLength;
        setAddRequestLength( addRequestLength );

        // Return the result.
        return 1 + TLV.getNbBytes( addRequestLength ) + addRequestLength;
    }


    /**
     * Encode the AddRequest message to a PDU. 
     * 
     * AddRequest :
     * 
     * 0x68 LL
     *   0x04 LL entry
     *   0x30 LL attributesList
     *     0x30 LL attributeList
     *       0x04 LL attributeDescription
     *       0x31 LL attributeValues
     *         0x04 LL attributeValue
     *         ... 
     *         0x04 LL attributeValue
     *     ... 
     *     0x30 LL attributeList
     *       0x04 LL attributeDescription
     *       0x31 LL attributeValue
     *         0x04 LL attributeValue
     *         ... 
     *         0x04 LL attributeValue 
     * 
     * @param buffer The buffer where to put the PDU
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        try
        {
            // The AddRequest Tag
            buffer.put( LdapConstants.ADD_REQUEST_TAG );
            buffer.put( TLV.getBytes( getAddRequestLength() ) );

            // The entry
            org.apache.directory.shared.asn1.ber.tlv.Value.encode( buffer, Dn.getBytes( getEntryDn() ) );

            // The attributes sequence
            buffer.put( UniversalTag.SEQUENCE.getValue() );
            buffer.put( TLV.getBytes( getEntryLength() ) );

            // The partial attribute list
            Entry entry = getEntry();

            if ( entry.size() != 0 )
            {
                int attributeNumber = 0;

                // Compute the attributes length
                for ( EntryAttribute attribute : entry )
                {
                    // The attributes list sequence
                    buffer.put( UniversalTag.SEQUENCE.getValue() );
                    int localAttributeLength = getAttributesLength().get( attributeNumber );
                    buffer.put( TLV.getBytes( localAttributeLength ) );

                    // The attribute type
                    org.apache.directory.shared.asn1.ber.tlv.Value.encode( buffer, attribute.getId() );

                    // The values
                    buffer.put( UniversalTag.SET.getValue() );
                    int localValuesLength = getValuesLength().get( attributeNumber );
                    buffer.put( TLV.getBytes( localValuesLength ) );

                    if ( attribute.size() != 0 )
                    {
                        for ( org.apache.directory.shared.ldap.model.entry.Value<?> value : attribute )
                        {
                            if ( value.isBinary() )
                            {
                                org.apache.directory.shared.asn1.ber.tlv.Value.encode( buffer, value.getBytes() );
                            }
                            else
                            {
                                org.apache.directory.shared.asn1.ber.tlv.Value.encode( buffer, value.getString() );
                            }
                        }
                    }

                    // Go to the next attribute number;
                    attributeNumber++;
                }
            }
            
            return buffer;
        }
        catch ( BufferOverflowException boe )
        {
            throw new EncoderException( "The PDU buffer size is too small !" );
        }
    }
}
