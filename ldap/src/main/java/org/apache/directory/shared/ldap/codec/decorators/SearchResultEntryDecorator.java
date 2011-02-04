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
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.asn1.util.Asn1StringUtils;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.LdapCodecService;
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.model.entry.DefaultEntryAttribute;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.entry.EntryAttribute;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.message.SearchResultEntry;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.util.Strings;


/**
 * A decorator for the SearchResultEntry message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultEntryDecorator extends MessageDecorator<SearchResultEntry> implements SearchResultEntry
{
    /** A temporary storage for the byte[] representing the objectName */
    private byte[] objectNameBytes;

    /** The search result entry length */
    private int searchResultEntryLength;

    /** The partial attributes length */
    private int attributesLength;

    /** The list of all attributes length */
    private List<Integer> attributeLength;

    /** The list of all vals length */
    private List<Integer> valsLength;
    
    /** The current attribute being processed */
    private EntryAttribute currentAttribute;


    /**
     * Makes a SearchResultEntry encodable.
     *
     * @param decoratedMessage the decorated SearchResultEntry
     */
    public SearchResultEntryDecorator( LdapCodecService codec, SearchResultEntry decoratedMessage )
    {
        super( codec, decoratedMessage );
    }


    /**
     * Gets the distinguished name bytes of the entry object returned.
     *
     * @return the Dn bytes of the entry returned.
     */
    public byte[] getObjectNameBytes()
    {
        return objectNameBytes;
    }


    /**
     * Sets the distinguished name bytes of the entry object returned.
     *
     * @param objectNameBytes the Dn bytes of the entry returned.
     */
    public void setObjectNameBytes( byte[] objectNameBytes )
    {
        this.objectNameBytes = objectNameBytes;
    }


    /**
     * @return The encoded SearchResultEntry's length
     */
    public int getSearchResultEntryLength()
    {
        return searchResultEntryLength;
    }


    /**
     * Stores the encoded length for the SearchResultEntry
     * @param searchResultEntryLength The encoded length
     */
    public void setSearchResultEntryLength( int searchResultEntryLength )
    {
        this.searchResultEntryLength = searchResultEntryLength;
    }


    /**
     * @return The encoded PartialAttributeList's length
     */
    public int getAttributesLength()
    {
        return attributesLength;
    }


    /**
     * Stores the encoded length for the Attributes
     * @param attributesLength The list of encoded lengths
     */
    public void setAttributesLength( int attributesLength )
    {
        this.attributesLength = attributesLength;
    }


    /**
     * @return The encoded PartialAttributeList's length
     */
    public List<Integer> getAttributeLength()
    {
        return attributeLength;
    }


    /**
     * @return The list of encoded Attributes' length
     */
    public void setAttributeLength( List<Integer> attributeLength )
    {
        this.attributeLength = attributeLength;
    }


    /**
     * @return The list of encoded values' length
     */
    public List<Integer> getValsLength()
    {
        return valsLength;
    }


    /**
     * Stores the list of encoded length for the values
     * @param valsLength The list of encoded lengths
     */
    public void setValsLength( List<Integer> valsLength )
    {
        this.valsLength = valsLength;
    }
    
    
    public EntryAttribute getCurrentAttribute()
    {
        return currentAttribute;
    }


    /**
     * Create a new attribute
     * 
     * @param type The attribute's type
     */
    public void addAttribute( String type ) throws LdapException
    {
        currentAttribute = new DefaultEntryAttribute( type );

        getDecorated().getEntry().put( currentAttribute );
    }


    /**
     * Add a new value to the current attribute
     * 
     * @param value The added value
     */
    public void addAttributeValue( Object value )
    {
        if ( value instanceof String )
        {
            currentAttribute.add( ( String ) value );
        }
        else
        {
            currentAttribute.add( ( byte[] ) value );
        }
    }


    //-------------------------------------------------------------------------
    // The IntermediateResponse methods
    //-------------------------------------------------------------------------
    
    
    /**
     * {@inheritDoc}
     */
    public Dn getObjectName()
    {
        return getDecorated().getObjectName();
    }


    /**
     * {@inheritDoc}
     */
    public void setObjectName( Dn objectName )
    {
        getDecorated().setObjectName( objectName );
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


    //-------------------------------------------------------------------------
    // The Decorator methods
    //-------------------------------------------------------------------------
    
    
    /**
     * Compute the SearchResultEntry length
     * 
     * SearchResultEntry :
     * <pre>
     * 0x64 L1
     *  |
     *  +--> 0x04 L2 objectName
     *  +--> 0x30 L3 (attributes)
     *        |
     *        +--> 0x30 L4-1 (partial attributes list)
     *        |     |
     *        |     +--> 0x04 L5-1 type
     *        |     +--> 0x31 L6-1 (values)
     *        |           |
     *        |           +--> 0x04 L7-1-1 value
     *        |           +--> ...
     *        |           +--> 0x04 L7-1-n value
     *        |
     *        +--> 0x30 L4-2 (partial attributes list)
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
     *        +--> 0x30 L4-m (partial attributes list)
     *              |
     *              +--> 0x04 L5-m type
     *              +--> 0x31 L6-m (values)
     *                    |
     *                    +--> 0x04 L7-m-1 value
     *                    +--> ...
     *                    +--> 0x04 L7-m-n value
     * </pre>
     */
    public int computeLength()
    {
        Dn dn = getObjectName();

        byte[] dnBytes = Strings.getBytesUtf8(dn.getName());

        // The entry
        int searchResultEntryLength = 1 + TLV.getNbBytes( dnBytes.length ) + dnBytes.length;
        setObjectNameBytes( dnBytes );

        // The attributes sequence
        int attributesLength = 0;

        Entry entry = getEntry();

        if ( ( entry != null ) && ( entry.size() != 0 ) )
        {
            List<Integer> attributeLength = new LinkedList<Integer>();
            List<Integer> valsLength = new LinkedList<Integer>();

            // Store those lists in the object
            setAttributeLength( attributeLength );
            setValsLength( valsLength );

            // Compute the attributes length
            for ( EntryAttribute attribute : entry )
            {
                int localAttributeLength = 0;
                int localValuesLength = 0;

                // Get the type length
                int idLength = attribute.getId().getBytes().length;
                localAttributeLength = 1 + TLV.getNbBytes( idLength ) + idLength;

                if ( attribute.size() != 0 )
                {
                    // The values
                    if ( attribute.size() > 0 )
                    {
                        localValuesLength = 0;

                        for ( org.apache.directory.shared.ldap.model.entry.Value<?> value : attribute )
                        {
                            byte[] binaryValue = value.getBytes();
                            localValuesLength += 1 + TLV.getNbBytes( binaryValue.length ) + binaryValue.length;
                        }

                        localAttributeLength += 1 + TLV.getNbBytes( localValuesLength ) + localValuesLength;
                    }
                    else
                    {
                        // We have to deal with the special wase where
                        // we don't have a value.
                        // It will be encoded as an empty OCTETSTRING,
                        // so it will be two byte slong (0x04 0x00)
                        localAttributeLength += 1 + 1;
                    }
                }
                else
                {
                    // We have no values. We will just have an empty SET OF :
                    // 0x31 0x00
                    localAttributeLength += 1 + 1;
                }

                // add the attribute length to the attributes length
                attributesLength += 1 + TLV.getNbBytes( localAttributeLength ) + localAttributeLength;

                // Store the lengths of the encoded attributes and values
                attributeLength.add( localAttributeLength );
                valsLength.add( localValuesLength );
            }

            // Store the lengths of the entry
            setAttributesLength( attributesLength );
        }

        searchResultEntryLength += 1 + TLV.getNbBytes( attributesLength ) + attributesLength;

        // Store the length of the response 
        setSearchResultEntryLength( searchResultEntryLength );

        // Return the result.
        return 1 + TLV.getNbBytes( searchResultEntryLength ) + searchResultEntryLength;
    }


    /**
     * Encode the SearchResultEntry message to a PDU.
     * 
     * SearchResultEntry :
     * <pre>
     * 0x64 LL
     *   0x04 LL objectName
     *   0x30 LL attributes
     *     0x30 LL partialAttributeList
     *       0x04 LL type
     *       0x31 LL vals
     *         0x04 LL attributeValue
     *         ... 
     *         0x04 LL attributeValue
     *     ... 
     *     0x30 LL partialAttributeList
     *       0x04 LL type
     *       0x31 LL vals
     *         0x04 LL attributeValue
     *         ... 
     *         0x04 LL attributeValue 
     * </pre>
     * @param buffer The buffer where to put the PDU
     * @param searchResultEntryDecorator the SearchResultEntry decorator
     * @return The PDU.
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        try
        {
            // The SearchResultEntry Tag
            buffer.put( LdapConstants.SEARCH_RESULT_ENTRY_TAG );
            buffer.put( TLV.getBytes( getSearchResultEntryLength() ) );

            // The objectName
            Value.encode( buffer, getObjectNameBytes() );

            // The attributes sequence
            buffer.put( UniversalTag.SEQUENCE.getValue() );
            buffer.put( TLV.getBytes( getAttributesLength() ) );

            // The partial attribute list
            Entry entry = getEntry();

            if ( ( entry != null ) && ( entry.size() != 0 ) )
            {
                int attributeNumber = 0;

                // Compute the attributes length
                for ( EntryAttribute attribute : entry )
                {
                    // The partial attribute list sequence
                    buffer.put( UniversalTag.SEQUENCE.getValue() );
                    int localAttributeLength = getAttributeLength().get( attributeNumber );
                    buffer.put( TLV.getBytes( localAttributeLength ) );

                    // The attribute type
                    Value.encode( buffer, Asn1StringUtils.asciiStringToByte( attribute.getUpId() ) );

                    // The values
                    buffer.put( UniversalTag.SET.getValue() );
                    int localValuesLength = getValsLength().get( attributeNumber );
                    buffer.put( TLV.getBytes( localValuesLength ) );

                    if ( attribute.size() > 0 )
                    {
                        for ( org.apache.directory.shared.ldap.model.entry.Value<?> value : attribute )
                        {
                            if ( !value.isBinary() )
                            {
                                Value.encode( buffer, value.getString() );
                            }
                            else
                            {
                                Value.encode( buffer, value.getBytes() );
                            }
                        }
                    }

                    // Go to the next attribute number;
                    attributeNumber++;
                }
            }
        }
        catch ( BufferOverflowException boe )
        {
            throw new EncoderException( I18n.err( I18n.ERR_04005 ) );
        }
        
        return buffer;
    }
}
