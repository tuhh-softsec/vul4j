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


import java.util.LinkedList;
import java.util.List;

import org.apache.directory.shared.ldap.model.entry.DefaultEntryAttribute;
import org.apache.directory.shared.ldap.model.entry.DefaultModification;
import org.apache.directory.shared.ldap.model.entry.EntryAttribute;
import org.apache.directory.shared.ldap.model.entry.Modification;
import org.apache.directory.shared.ldap.model.entry.ModificationOperation;
import org.apache.directory.shared.ldap.model.message.ModifyRequest;


/**
 * A decorator for the ModifyRequest message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModifyRequestDecorator extends MessageDecorator
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
    public ModifyRequestDecorator( ModifyRequest decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated ModifyRequest
     */
    public ModifyRequest getModifyRequest()
    {
        return ( ModifyRequest ) getMessage();
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
        ((ModifyRequest)getMessage()).addModification( modification );
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
}
