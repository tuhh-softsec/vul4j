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


import java.util.List;

import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.entry.EntryAttribute;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.message.SearchResultEntry;
import org.apache.directory.shared.ldap.model.name.Dn;


/**
 * A decorator for the SearchResultEntry message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultEntryDecorator extends MessageDecorator implements SearchResultEntry
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
    private EntryAttribute currentEntry;


    /**
     * Makes a SearchResultEntry encodable.
     *
     * @param decoratedMessage the decorated SearchResultEntry
     */
    public SearchResultEntryDecorator( SearchResultEntry decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated SearchResultEntry
     */
    public SearchResultEntry getSearchResultEntry()
    {
        return ( SearchResultEntry ) getDecoratedMessage();
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
    
    
    public EntryAttribute getCurrentEntry()
    {
        return currentEntry;
    }


    //-------------------------------------------------------------------------
    // The IntermediateResponse methods
    //-------------------------------------------------------------------------
    
    
    /**
     * {@inheritDoc}
     */
    public Dn getObjectName()
    {
        return getSearchResultEntry().getObjectName();
    }


    /**
     * {@inheritDoc}
     */
    public void setObjectName( Dn objectName )
    {
        getSearchResultEntry().setObjectName( objectName );
    }


    /**
     * {@inheritDoc}
     */
    public Entry getEntry()
    {
        return getSearchResultEntry().getEntry();
    }


    /**
     * {@inheritDoc}
     */
    public void setEntry( Entry entry )
    {
        getSearchResultEntry().setEntry( entry );
    }


    /**
     * {@inheritDoc}
     */
    public void addAttribute( String type ) throws LdapException
    {
        getSearchResultEntry().addAttribute( type );
    }


    public void addAttributeValue( Object value )
    {
        getSearchResultEntry().addAttributeValue( value );
    }
}
