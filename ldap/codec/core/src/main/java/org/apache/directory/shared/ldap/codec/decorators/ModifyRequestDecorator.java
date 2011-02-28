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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.codec.api.LdapConstants;
import org.apache.directory.shared.ldap.model.entry.DefaultEntryAttribute;
import org.apache.directory.shared.ldap.model.entry.DefaultModification;
import org.apache.directory.shared.ldap.model.entry.EntryAttribute;
import org.apache.directory.shared.ldap.model.entry.Modification;
import org.apache.directory.shared.ldap.model.entry.ModificationOperation;
import org.apache.directory.shared.ldap.model.message.ModifyRequest;
import org.apache.directory.shared.ldap.model.message.ModifyResponse;
import org.apache.directory.shared.ldap.model.name.Dn;


/**
 * A decorator for the ModifyRequest message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModifyRequestDecorator extends SingleReplyRequestDecorator<ModifyRequest,ModifyResponse>
    implements ModifyRequest
{
    /** The modify request length */
    private int modifyRequestLength;

    /** The changes length */
    private int changesLength;

    /** The list of all change lengths */
    private List<Integer> changeLength = new LinkedList<Integer>();

    /** The list of all the modification lengths */
    private List<Integer> modificationLength = new LinkedList<Integer>();

    /** The list of all the value lengths */
    private List<Integer> valuesLength = new LinkedList<Integer>();

    /** The current attribute being decoded */
    private EntryAttribute currentAttribute;

    /** A local storage for the operation */
    private ModificationOperation currentOperation;



    /**
     * Makes a ModifyRequest encodable.
     *
     * @param decoratedMessage the decorated ModifyRequest
     */
    public ModifyRequestDecorator( LdapCodecService codec, ModifyRequest decoratedMessage )
    {
        super( codec, decoratedMessage );
    }


    /**
     * @param modifyRequestLength The encoded ModifyRequest's length
     */
    public void setModifyRequestLength( int modifyRequestLength )
    {
        this.modifyRequestLength = modifyRequestLength;
    }


    /**
     * @return The encoded length
     */
    public int getModifyRequestLength()
    {
        return modifyRequestLength;
    }


    /**
     * @param changesLength The encoded Changes length
     */
    public void setChangesLength( int changesLength )
    {
        this.changesLength = changesLength;
    }


    /**
     * @return The encoded length
     */
    public int getChangesLength()
    {
        return changesLength;
    }


    /**
     * @return The list of encoded Change length
     */
    public void setChangeLength( List<Integer> changeLength )
    {
        this.changeLength = changeLength;
    }


    /**
     * @return The list of encoded Change length
     */
    public List<Integer> getChangeLength()
    {
        return changeLength;
    }


    /**
     * @param modificationLength The list of encoded Modification length
     */
    public void setModificationLength( List<Integer> modificationLength )
    {
        this.modificationLength = modificationLength;
    }


    /**
     * @return The list of encoded Modification length
     */
    public List<Integer> getModificationLength()
    {
        return modificationLength;
    }


    /**
     * @param valuesLength The list of encoded Values length
     */
    public void setValuesLength( List<Integer> valuesLength )
    {
        this.valuesLength = valuesLength;
    }


    /**
     * @return The list of encoded Values length
     */
    public List<Integer> getValuesLength()
    {
        return valuesLength;
    }
    
    
    /**
     * Store the current operation
     * 
     * @param currentOperation The currentOperation to set.
     */
    public void setCurrentOperation( int currentOperation )
    {
        this.currentOperation = ModificationOperation.getOperation( currentOperation );
    }


    /**
     * Add a new attributeTypeAndValue
     * 
     * @param type The attribute's name
     */
    public void addAttributeTypeAndValues( String type )
    {
        currentAttribute = new DefaultEntryAttribute( type );

        Modification modification = new DefaultModification( currentOperation, currentAttribute );
        getDecorated().addModification( modification );
    }


    /**
     * Return the current attribute's type
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
    public void addAttributeValue( byte[] value )
    {
        currentAttribute.add( value );
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


    //-------------------------------------------------------------------------
    // The ModifyRequest methods
    //-------------------------------------------------------------------------
    
    
    /**
     * {@inheritDoc}
     */
    public Dn getName()
    {
        return getDecorated().getName();
    }


    /**
     * {@inheritDoc}
     */
    public void setName( Dn name )
    {
        getDecorated().setName( name );
    }


    /**
     * {@inheritDoc}
     */
    public Collection<Modification> getModifications()
    {
        return getDecorated().getModifications();
    }


    /**
     * {@inheritDoc}
     */
    public void addModification( Modification mod )
    {
        getDecorated().addModification( mod );
    }


    /**
     * {@inheritDoc}
     */
    public void removeModification( Modification mod )
    {
        getDecorated().removeModification( mod );
    }


    /**
     * {@inheritDoc}
     */
    public void remove( String attributeName, String... attributeValue )
    {
        getDecorated().remove( attributeName, attributeValue );
    }


    /**
     * {@inheritDoc}
     */
    public void remove( String attributeName, byte[]... attributeValue )
    {
        getDecorated().remove( attributeName, attributeValue );
    }


    /**
     * {@inheritDoc}
     */
    public void remove( EntryAttribute attr )
    {
        getDecorated().remove( attr );
    }


    /**
     * {@inheritDoc}
     */
    public void addModification( EntryAttribute attr, ModificationOperation modOp )
    {
        getDecorated().addModification( attr, modOp );
    }


    /**
     * {@inheritDoc}
     */
    public void add( String attributeName, String... attributeValue )
    {
        getDecorated().add( attributeName, attributeValue );
    }


    /**
     * {@inheritDoc}
     */
    public void add( String attributeName, byte[]... attributeValue )
    {
        getDecorated().add( attributeName, attributeValue );
    }


    /**
     * {@inheritDoc}
     */
    public void add( EntryAttribute attr )
    {
        getDecorated().add( attr );
    }


    /**
     * {@inheritDoc}
     */
    public void replace( String attributeName )
    {
        getDecorated().replace( attributeName );
    }


    /**
     * {@inheritDoc}
     */
    public void replace( String attributeName, String... attributeValue )
    {
        getDecorated().replace( attributeName, attributeValue );
    }


    /**
     * {@inheritDoc}
     */
    public void replace( String attributeName, byte[]... attributeValue )
    {
        getDecorated().replace( attributeName, attributeValue );
    }


    /**
     * {@inheritDoc}
     */
    public void replace( EntryAttribute attr )
    {
        getDecorated().replace( attr );
    }


    //-------------------------------------------------------------------------
    // The Decorator methods
    //-------------------------------------------------------------------------
    
    
    /**
     * Compute the ModifyRequest length 
     * 
     * ModifyRequest :
     * 
     * 0x66 L1
     *  |
     *  +--> 0x04 L2 object
     *  +--> 0x30 L3 modifications
     *        |
     *        +--> 0x30 L4-1 modification sequence
     *        |     |
     *        |     +--> 0x0A 0x01 (0..2) operation
     *        |     +--> 0x30 L5-1 modification
     *        |           |
     *        |           +--> 0x04 L6-1 type
     *        |           +--> 0x31 L7-1 vals
     *        |                 |
     *        |                 +--> 0x04 L8-1-1 attributeValue
     *        |                 +--> 0x04 L8-1-2 attributeValue
     *        |                 +--> ...
     *        |                 +--> 0x04 L8-1-i attributeValue
     *        |                 +--> ...
     *        |                 +--> 0x04 L8-1-n attributeValue
     *        |
     *        +--> 0x30 L4-2 modification sequence
     *        .     |
     *        .     +--> 0x0A 0x01 (0..2) operation
     *        .     +--> 0x30 L5-2 modification
     *                    |
     *                    +--> 0x04 L6-2 type
     *                    +--> 0x31 L7-2 vals
     *                          |
     *                          +--> 0x04 L8-2-1 attributeValue
     *                          +--> 0x04 L8-2-2 attributeValue
     *                          +--> ...
     *                          +--> 0x04 L8-2-i attributeValue
     *                          +--> ...
     *                          +--> 0x04 L8-2-n attributeValue
     */
    public int computeLength()
    {
        // Initialized with name
        int modifyRequestLength = 1 + TLV.getNbBytes( Dn.getNbBytes( getName() ) )
            + Dn.getNbBytes( getName() );

        // All the changes length
        int changesLength = 0;

        Collection<Modification> modifications = getModifications();

        if ( ( modifications != null ) && ( modifications.size() != 0 ) )
        {
            List<Integer> changeLength = new LinkedList<Integer>();
            List<Integer> modificationLength = new LinkedList<Integer>();
            List<Integer> valuesLength = new LinkedList<Integer>();

            for ( Modification modification : modifications )
            {
                // Modification sequence length initialized with the operation
                int localModificationSequenceLength = 1 + 1 + 1;
                int localValuesLength = 0;

                // Modification length initialized with the type
                int typeLength = modification.getAttribute().getId().length();
                int localModificationLength = 1 + TLV.getNbBytes( typeLength ) + typeLength;

                // Get all the values
                if ( modification.getAttribute().size() != 0 )
                {
                    for ( org.apache.directory.shared.ldap.model.entry.Value<?> value : modification.getAttribute() )
                    {
                        localValuesLength += 1 + TLV.getNbBytes( value.getBytes().length ) + value.getBytes().length;
                    }
                }

                localModificationLength += 1 + TLV.getNbBytes( localValuesLength ) + localValuesLength;

                // Compute the modificationSequenceLength
                localModificationSequenceLength += 1 + TLV.getNbBytes( localModificationLength )
                    + localModificationLength;

                // Add the tag and the length
                changesLength += 1 + TLV.getNbBytes( localModificationSequenceLength )
                    + localModificationSequenceLength;

                // Store the arrays of values
                valuesLength.add( localValuesLength );
                modificationLength.add( localModificationLength );
                changeLength.add( localModificationSequenceLength );
            }

            // Add the modifications length to the modificationRequestLength
            modifyRequestLength += 1 + TLV.getNbBytes( changesLength ) + changesLength;
            setChangeLength( changeLength );
            setModificationLength( modificationLength );
            setValuesLength( valuesLength );
        }

        setChangesLength( changesLength );
        setModifyRequestLength( modifyRequestLength );

        return 1 + TLV.getNbBytes( modifyRequestLength ) + modifyRequestLength;
    }


    /**
     * Encode the ModifyRequest message to a PDU. 
     * 
     * ModifyRequest : 
     * <pre>
     * 0x66 LL
     *   0x04 LL object
     *   0x30 LL modifiations
     *     0x30 LL modification sequence
     *       0x0A 0x01 operation
     *       0x30 LL modification
     *         0x04 LL type
     *         0x31 LL vals
     *           0x04 LL attributeValue
     *           ... 
     *           0x04 LL attributeValue
     *     ... 
     *     0x30 LL modification sequence
     *       0x0A 0x01 operation
     *       0x30 LL modification
     *         0x04 LL type
     *         0x31 LL vals
     *           0x04 LL attributeValue
     *           ... 
     *           0x04 LL attributeValue
     * </pre>
     * 
     * @param buffer The buffer where to put the PDU
     * @return The PDU.
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        try
        {
            // The AddRequest Tag
            buffer.put( LdapConstants.MODIFY_REQUEST_TAG );
            buffer.put( TLV.getBytes( getModifyRequestLength() ) );

            // The entry
            Value.encode( buffer, Dn.getBytes( getName() ) );

            // The modifications sequence
            buffer.put( UniversalTag.SEQUENCE.getValue() );
            buffer.put( TLV.getBytes( getChangesLength() ) );

            // The modifications list
            Collection<Modification> modifications = getModifications();

            if ( ( modifications != null ) && ( modifications.size() != 0 ) )
            {
                int modificationNumber = 0;

                // Compute the modifications length
                for ( Modification modification : modifications )
                {
                    // The modification sequence
                    buffer.put( UniversalTag.SEQUENCE.getValue() );
                    int localModificationSequenceLength = getChangeLength().get( modificationNumber );
                    buffer.put( TLV.getBytes( localModificationSequenceLength ) );

                    // The operation. The value has to be changed, it's not
                    // the same value in DirContext and in RFC 2251.
                    buffer.put( UniversalTag.ENUMERATED.getValue() );
                    buffer.put( ( byte ) 1 );
                    buffer.put( ( byte ) modification.getOperation().getValue() );

                    // The modification
                    buffer.put( UniversalTag.SEQUENCE.getValue() );
                    int localModificationLength = getModificationLength().get( modificationNumber );
                    buffer.put( TLV.getBytes( localModificationLength ) );

                    // The modification type
                    Value.encode( buffer, modification.getAttribute().getId() );

                    // The values
                    buffer.put( UniversalTag.SET.getValue() );
                    int localValuesLength = getValuesLength().get( modificationNumber );
                    buffer.put( TLV.getBytes( localValuesLength ) );

                    if ( modification.getAttribute().size() != 0 )
                    {
                        for ( org.apache.directory.shared.ldap.model.entry.Value<?> value : modification.getAttribute() )
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

                    // Go to the next modification number;
                    modificationNumber++;
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
