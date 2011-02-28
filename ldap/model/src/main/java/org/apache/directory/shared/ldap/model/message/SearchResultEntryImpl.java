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
package org.apache.directory.shared.ldap.model.message;


import org.apache.directory.shared.ldap.model.entry.DefaultEntry;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.name.Dn;


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


    /**
     * Creates a SearchResponseEntry as a reply to an SearchRequest to
     * indicate the end of a search operation.
     */
    public SearchResultEntryImpl()
    {
        super( -1, TYPE );
    }


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
    public Dn getObjectName()
    {
        return ( entry == null ? null : entry.getDn() );
    }


    /**
     * Sets the distinguished name of the entry object returned.
     * 
     * @param objectName the Dn of the entry returned.
     */
    public void setObjectName( Dn objectName )
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

        if ( !( obj instanceof SearchResultEntry) )
        {
            return false;
        }

        SearchResultEntry resp = ( SearchResultEntry ) obj;

        return entry.equals( resp.getEntry() );
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

        return super.toString( sb.toString() );
    }
}
