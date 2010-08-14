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


import java.util.List;

import org.apache.directory.shared.ldap.entry.DefaultEntry;
import org.apache.directory.shared.ldap.entry.DefaultEntryAttribute;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.message.control.Control;
import org.apache.directory.shared.ldap.message.internal.AbstractResponse;
import org.apache.directory.shared.ldap.message.internal.SearchResultEntry;
import org.apache.directory.shared.ldap.name.DN;


/**
 * Lockable SearchResponseEntry implementation
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultEntryImpl extends AbstractResponse implements SearchResultEntry
{
    static final long serialVersionUID = -8357316233060886637L;

    /** Entry returned in response to search */
    private Entry entry = new DefaultEntry();

    /** The current attribute being decoded */
    private EntryAttribute currentAttribute;

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


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------
    /**
     * Creates a SearchResponseEntry as a reply to an SearchRequest to
     * indicate the end of a search operation.
     * 
     * @param id the session unique message id
     */
    public SearchResultEntryImpl( final int id )
    {
        super( id, TYPE );
    }


    // ------------------------------------------------------------------------
    // SearchResponseEntry Interface Method Implementations
    // ------------------------------------------------------------------------

    /**
     * Gets the entry
     * 
     * @return the entry
     */
    public Entry getEntry()
    {
        return entry;
    }


    /**
     * Create a new attribute
     * 
     * @param type The attribute's type
     */
    public void addAttribute( String type ) throws LdapException
    {
        currentAttribute = new DefaultEntryAttribute( type );

        entry.put( currentAttribute );
    }


    /**
     * {@inheritDoc}
     */
    public EntryAttribute getCurrentAttribute()
    {
        return currentAttribute;
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


    /**
     * Sets the entry.
     * 
     * @param entry the entry
     */
    public void setEntry( Entry entry )
    {
        this.entry = entry;
    }


    /**
     * Gets the distinguished name of the entry object returned.
     * 
     * @return the Dn of the entry returned.
     */
    public DN getObjectName()
    {
        return ( entry == null ? null : entry.getDn() );
    }


    /**
     * Gets the distinguished name bytes of the entry object returned.
     * 
     * @return the Dn bytes of the entry returned.
     */
    /*No qualifier*/byte[] getObjectNameBytes()
    {
        return objectNameBytes;
    }


    /**
     * Sets the distinguished name bytes of the entry object returned.
     * 
     * @param objectNameBytes the Dn bytes of the entry returned.
     */
    /*No qualifier*/void setObjectNameBytes( byte[] objectNameBytes )
    {
        this.objectNameBytes = objectNameBytes;
    }


    /**
     * Sets the distinguished name of the entry object returned.
     * 
     * @param objectName the Dn of the entry returned.
     */
    public void setObjectName( DN objectName )
    {
        if ( entry != null )
        {
            entry.setDn( objectName );
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int hash = 37;
        if ( entry != null )
        {
            hash = hash * 17 + entry.hashCode();
        }
        hash = hash * 17 + super.hashCode();

        return hash;
    }


    /**
     * Checks for equality by comparing the objectName, and attributes
     * properties of this Message after delegating to the super.equals() method.
     * 
     * @param obj
     *            the object to test for equality with this message
     * @return true if the obj is equal false otherwise
     */
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }

        if ( !super.equals( obj ) )
        {
            return false;
        }

        if ( !( obj instanceof SearchResultEntry ) )
        {
            return false;
        }

        SearchResultEntry resp = ( SearchResultEntry ) obj;

        return entry.equals( resp.getEntry() );
    }


    /**
     * @return The encoded SearchResultEntry's length
     */
    /*No qualifier*/int getSearchResultEntryLength()
    {
        return searchResultEntryLength;
    }


    /**
     * Stores the encoded length for the SearchResultEntry
     * @param searchResultEntryLength The encoded length
     */
    /*No qualifier*/void setSearchResultEntryLength( int searchResultEntryLength )
    {
        this.searchResultEntryLength = searchResultEntryLength;
    }


    /**
     * @return The encoded PartialAttributeList's length
     */
    /*No qualifier*/int getAttributesLength()
    {
        return attributesLength;
    }


    /**
     * Stores the encoded length for the Attributes
     * @param attributeLength The list of encoded lengths
     */
    /*No qualifier*/void setAttributesLength( int attributesLength )
    {
        this.attributesLength = attributesLength;
    }


    /**
     * @return The encoded PartialAttributeList's length
     */
    /*No qualifier*/List<Integer> getAttributeLength()
    {
        return attributeLength;
    }


    /**
     * @return The list of encoded Attributes' length
     */
    /*No qualifier*/void setAttributeLength( List<Integer> attributeLength )
    {
        this.attributeLength = attributeLength;
    }


    /**
     * @return The list of encoded values' length
     */
    /*No qualifier*/List<Integer> getValsLength()
    {
        return valsLength;
    }


    /**
     * Stores the list of encoded length for the values 
     * @param valsLength The list of encoded lengths
     */
    /*No qualifier*/void setValsLength( List<Integer> valsLength )
    {
        this.valsLength = valsLength;
    }


    /**
     * Return a string representation of a SearchResultEntry request
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "    Search Result Entry\n" );

        if ( entry != null )
        {
            sb.append( entry );
        }
        else
        {
            sb.append( "            No entry\n" );
        }

        if ( ( controls != null ) && ( controls.size() != 0 ) )
        {
            for ( Control control : controls.values() )
            {
                sb.append( control );
            }
        }

        return sb.toString();
    }

}
