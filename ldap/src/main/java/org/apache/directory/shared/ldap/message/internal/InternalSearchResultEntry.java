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

package org.apache.directory.shared.ldap.message.internal;


import java.util.List;

import org.apache.directory.shared.ldap.codec.MessageTypeEnum;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.name.DN;


/**
 * Search entry protocol response message used to return non referral entries to
 * the client in response to a search request message.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface InternalSearchResultEntry extends InternalResponse
{
    /** Search entry response message type enumeration value */
    MessageTypeEnum TYPE = MessageTypeEnum.SEARCH_RESULT_ENTRY;


    /**
     * Gets the distinguished name of the entry object returned.
     * 
     * @return the Dn of the entry returned.
     */
    DN getObjectName();


    /**
     * Sets the distinguished name of the entry object returned.
     * 
     * @param objectName the Dn of the entry returned.
     */
    void setObjectName( DN objectName );


    /**
     * Gets the distinguished name bytes of the entry object returned.
     * 
     * @return the Dn bytes of the entry returned.
     */
    byte[] getObjectNameBytes();


    /**
     * Sets the distinguished name bytes of the entry object returned.
     * 
     * @param objectNameBytes the Dn bytes of the entry returned.
     */
    void setObjectNameBytes( byte[] objectNameBytes );


    /**
     * Gets the entry.
     * 
     * @return the entry
     */
    Entry getEntry();


    /**
     * Sets an entry
     * 
     * @param entry the entry
     */
    void setEntry( Entry entry );


    /**
     * Create a new attribute
     * 
     * @param type The attribute's type
     */
    void addAttribute( String type ) throws LdapException;


    /**
     * @return Returns the currentAttribute.
     */
    EntryAttribute getCurrentAttribute();


    /**
     * Add a new value to the current attribute
     * 
     * @param value The added value
     */
    void addAttributeValue( Object value );


    /**
     * @return The encoded SearchResultEntry's length
     */
    int getSearchResultEntryLength();


    /**
     * Stores the encoded length for the SearchResultEntry
     * @param searchResultEntryLength The encoded length
     */
    void setSearchResultEntryLength( int searchResultEntryLength );


    /**
     * @return The encoded PartialAttributeList's length
     */
    int getAttributesLength();


    /**
     * Stores the encoded length for the PartialAttributeList
     * @param attributesLength The encoded length
     */
    void setAttributesLength( int attributesLength );


    /**
     * @return The list of encoded Attributes' length
     */
    List<Integer> getAttributeLength();


    /**
     * Stores the encoded length for the Attributes
     * @param attributeLength The list of encoded lengths
     */
    void setAttributeLength( List<Integer> attributeLength );


    /**
     * @return The list of encoded values' length
     */
    List<Integer> getValsLength();


    /**
     * Stores the list of encoded length for the values 
     * @param valsLength The list of encoded lengths
     */
    void setValsLength( List<Integer> valsLength );
}
