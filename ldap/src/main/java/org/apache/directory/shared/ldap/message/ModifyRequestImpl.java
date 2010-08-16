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
package org.apache.directory.shared.ldap.message;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.directory.shared.ldap.codec.MessageTypeEnum;
import org.apache.directory.shared.ldap.entry.DefaultEntryAttribute;
import org.apache.directory.shared.ldap.entry.DefaultModification;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.apache.directory.shared.ldap.entry.Modification;
import org.apache.directory.shared.ldap.entry.ModificationOperation;
import org.apache.directory.shared.ldap.message.internal.InternalModifyRequest;
import org.apache.directory.shared.ldap.message.internal.ModifyResponse;
import org.apache.directory.shared.ldap.message.internal.ResultResponse;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.util.StringTools;


/**
 * Lockable ModifyRequest implementation.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModifyRequestImpl extends AbstractAbandonableRequest implements InternalModifyRequest
{
    static final long serialVersionUID = -505803669028990304L;

    /** Dn of the entry to modify or PDU's <b>object</b> field */
    private DN name;

    /** Sequence of modifications or PDU's <b>modification</b> seqence field */
    private List<Modification> mods = new ArrayList<Modification>();

    /** The associated response */
    private ModifyResponse response;

    /** The current attribute being decoded */
    private EntryAttribute currentAttribute;

    /** A local storage for the operation */
    private ModificationOperation currentOperation;

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


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Creates a ModifyRequest implementing object used to modify the
     * attributes of an entry.
     */
    public ModifyRequestImpl()
    {
        super( -1, TYPE );
    }


    /**
     * Creates a ModifyRequest implementing object used to modify the
     * attributes of an entry.
     * 
     * @param id the sequential message identifier
     */
    public ModifyRequestImpl( final int id )
    {
        super( id, TYPE );
    }


    // ------------------------------------------------------------------------
    // ModifyRequest Interface Method Implementations
    // ------------------------------------------------------------------------
    /**
     * Gets an immutable Collection of modification items representing the
     * atomic changes to perform on the candidate entry to modify.
     * 
     * @return an immutable Collection of Modification instances.
     */
    public Collection<Modification> getModifications()
    {
        return Collections.unmodifiableCollection( mods );
    }


    /**
     * Gets the distinguished name of the entry to be modified by this request.
     * This property represents the PDU's <b>object</b> field.
     * 
     * @return the DN of the modified entry.
     */
    public DN getName()
    {
        return name;
    }


    /**
     * Sets the distinguished name of the entry to be modified by this request.
     * This property represents the PDU's <b>object</b> field.
     * 
     * @param name the DN of the modified entry.
     */
    public void setName( DN name )
    {
        this.name = name;
    }


    /**
     * Adds a Modification to the set of modifications composing this modify
     * request.
     * 
     * @param mod a Modification to add
     */
    public void addModification( Modification mod )
    {
        mods.add( mod );
    }


    private void addModification( ModificationOperation modOp, String attributeName, byte[]... attributeValue )
    {
        EntryAttribute attr = new DefaultEntryAttribute( attributeName, attributeValue );
        addModification( attr, modOp );
    }


    private void addModification( ModificationOperation modOp, String attributeName, String... attributeValue )
    {
        EntryAttribute attr = new DefaultEntryAttribute( attributeName, attributeValue );
        addModification( attr, modOp );
    }


    public void addModification( EntryAttribute attr, ModificationOperation modOp )
    {
        mods.add( new DefaultModification( modOp, attr ) );
    }


    /**
     *
     * marks a given attribute for addition in the target entry with the
     * given values.
     *
     * @param attributeName name of the attribute to be added
     * @param attributeValue values of the attribute
     */
    public void add( String attributeName, String... attributeValue )
    {
        addModification( ModificationOperation.ADD_ATTRIBUTE, attributeName, attributeValue );
    }


    /**
     * @see #add(String, String...)
     */
    public void add( String attributeName, byte[]... attributeValue )
    {
        addModification( ModificationOperation.ADD_ATTRIBUTE, attributeName, attributeValue );
    }


    /**
     *
     * marks a given attribute for addition in the target entry.
     *
     * @param attr the attribute to be added
     */
    public void add( EntryAttribute attr )
    {
        addModification( attr, ModificationOperation.ADD_ATTRIBUTE );
    }


    /**
     * @see #replace(String, String...)
     */
    public void replace( String attributeName )
    {
        addModification( ModificationOperation.REPLACE_ATTRIBUTE, attributeName, StringTools.EMPTY_STRINGS );
    }


    /**
     *
     * marks a given attribute for replacement with the given
     * values in the target entry.
     *
     * @param attributeName name of the attribute to be added
     * @param attributeValue values of the attribute
     */
    public void replace( String attributeName, String... attributeValue )
    {
        addModification( ModificationOperation.REPLACE_ATTRIBUTE, attributeName, attributeValue );
    }


    /**
     * @see #replace(String, String...)
     */
    public void replace( String attributeName, byte[]... attributeValue )
    {
        addModification( ModificationOperation.REPLACE_ATTRIBUTE, attributeName, attributeValue );
    }


    /**
     *
     * marks a given attribute for replacement in the target entry.
     *
     * @param attr the attribute to be added
     */
    public void replace( EntryAttribute attr )
    {
        addModification( attr, ModificationOperation.REPLACE_ATTRIBUTE );
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
        mods.add( modification );
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


    /**
     * Removes a Modification to the set of modifications composing this
     * modify request.
     * 
     * @param mod a Modification to remove.
     */
    public void removeModification( Modification mod )
    {
        mods.remove( mod );
    }


    /**
     * marks a given attribute for removal with the given
     * values from the target entry.
     *
     * @param attributeName name of the attribute to be added
     * @param attributeValue values of the attribute
     */
    public void remove( String attributeName, String... attributeValue )
    {
        addModification( ModificationOperation.REMOVE_ATTRIBUTE, attributeName, attributeValue );
    }


    /**
     * @see #remove(String, String...)
     */
    public void remove( String attributeName, byte[]... attributeValue )
    {
        addModification( ModificationOperation.REMOVE_ATTRIBUTE, attributeName, attributeValue );
    }


    /**
     * marks a given attribute for removal from the target entry.
     *
     * @param attr the attribute to be added
     */
    public void remove( EntryAttribute attr )
    {
        addModification( attr, ModificationOperation.REMOVE_ATTRIBUTE );
    }


    // ------------------------------------------------------------------------
    // SingleReplyRequest Interface Method Implementations
    // ------------------------------------------------------------------------

    /**
     * Gets the protocol response message type for this request which produces
     * at least one response.
     * 
     * @return the message type of the response.
     */
    public MessageTypeEnum getResponseType()
    {
        return RESP_TYPE;
    }


    /**
     * The result containing response for this request.
     * 
     * @return the result containing response for this request
     */
    public ResultResponse getResultResponse()
    {
        if ( response == null )
        {
            response = new ModifyResponseImpl( getMessageId() );
        }

        return response;
    }


    /**
     * @return The encoded ModifyRequest's length
     */
    /* No Qualifier*/void setModifyRequestLength( int modifyRequestLength )
    {
        this.modifyRequestLength = modifyRequestLength;
    }


    /**
     * Stores the encoded length for the ModifyRequest
     * @param modifyRequestLength The encoded length
     */
    /* No Qualifier*/int getModifyRequestLength()
    {
        return modifyRequestLength;
    }


    /**
     * @return The encoded Changes length
     */
    /* No Qualifier*/void setChangesLength( int changesLength )
    {
        this.changesLength = changesLength;
    }


    /**
     * Stores the encoded length for the Changes
     * @param changesLength The encoded length
     */
    /* No Qualifier*/int getChangesLength()
    {
        return changesLength;
    }


    /**
     * @return The list of encoded Change length
     */
    /* No Qualifier*/void setChangeLength( List<Integer> changeLength )
    {
        this.changeLength = changeLength;
    }


    /**
     * Stores the list of encoded change length
     * @param changeLength The list of encoded Change length
     */
    /* No Qualifier*/List<Integer> getChangeLength()
    {
        return changeLength;
    }


    /**
     * @return The list of encoded Modification length
     */
    /* No Qualifier*/void setModificationLength( List<Integer> modificationLength )
    {
        this.modificationLength = modificationLength;
    }


    /**
     * Stores the list of encoded modification length
     * @param modificationLength The list of encoded Modification length
     */
    /* No Qualifier*/List<Integer> getModificationLength()
    {
        return modificationLength;
    }


    /**
     * @return The list of encoded Values length
     */
    /* No Qualifier*/void setValuesLength( List<Integer> valuesLength )
    {
        this.valuesLength = valuesLength;
    }


    /**
     * Stores the list of encoded Values length
     * @param valuesLength The list of encoded Values length
     */
    /* No Qualifier*/List<Integer> getValuesLength()
    {
        return valuesLength;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int hash = 37;
        if ( name != null )
        {
            hash = hash * 17 + name.hashCode();
        }
        hash = hash * 17 + mods.size();
        for ( int i = 0; i < mods.size(); i++ )
        {
            hash = hash * 17 + ( ( DefaultModification ) mods.get( i ) ).hashCode();
        }
        hash = hash * 17 + super.hashCode();

        return hash;
    }


    /**
     * Checks to see if ModifyRequest stub equals another by factoring in checks
     * for the name and modification items of the request.
     * 
     * @param obj
     *            the object to compare this ModifyRequest to
     * @return true if obj equals this ModifyRequest, false otherwise
     */
    public boolean equals( Object obj )
    {
        if ( obj == this )
        {
            return true;
        }

        if ( !super.equals( obj ) )
        {
            return false;
        }

        InternalModifyRequest req = ( InternalModifyRequest ) obj;

        if ( name != null && req.getName() == null )
        {
            return false;
        }

        if ( name == null && req.getName() != null )
        {
            return false;
        }

        if ( name != null && req.getName() != null && !name.equals( req.getName() ) )
        {
            return false;
        }

        if ( req.getModifications().size() != mods.size() )
        {
            return false;
        }

        Iterator<Modification> list = req.getModifications().iterator();

        for ( int i = 0; i < mods.size(); i++ )
        {
            Modification item = list.next();

            if ( item == null )
            {
                if ( mods.get( i ) != null )
                {
                    return false;
                }
            }
            else

            if ( !item.equals( ( DefaultModification ) mods.get( i ) ) )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * Get a String representation of a ModifyRequest
     * 
     * @return A ModifyRequest String
     */
    public String toString()
    {

        StringBuffer sb = new StringBuffer();

        sb.append( "    Modify Request\n" );
        sb.append( "        Object : '" ).append( name ).append( "'\n" );

        if ( mods != null )
        {

            for ( int i = 0; i < mods.size(); i++ )
            {

                DefaultModification modification = ( DefaultModification ) mods.get( i );

                sb.append( "            Modification[" ).append( i ).append( "]\n" );
                sb.append( "                Operation : " );

                switch ( modification.getOperation() )
                {
                    case ADD_ATTRIBUTE:
                        sb.append( " add\n" );
                        break;

                    case REPLACE_ATTRIBUTE:
                        sb.append( " replace\n" );
                        break;

                    case REMOVE_ATTRIBUTE:
                        sb.append( " delete\n" );
                        break;
                }

                sb.append( "                Modification\n" );
                sb.append( modification.getAttribute() );
            }
        }

        // The controls
        sb.append( super.toString() );

        return sb.toString();
    }
}
