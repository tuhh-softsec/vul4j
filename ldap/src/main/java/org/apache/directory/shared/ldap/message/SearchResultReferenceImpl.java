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


import org.apache.directory.shared.ldap.message.control.Control;
import org.apache.directory.shared.ldap.message.internal.InternalAbstractResponse;
import org.apache.directory.shared.ldap.message.internal.InternalReferral;
import org.apache.directory.shared.ldap.message.internal.InternalSearchResultReference;


/**
 * SearchResponseReference implementation
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultReferenceImpl extends InternalAbstractResponse implements InternalSearchResultReference
{
    static final long serialVersionUID = 7423807019951309810L;

    /** Referral holding the reference urls */
    private InternalReferral referral;

    /** The length of the referral */
    private int referralLength;

    /** The search result reference length */
    private int searchResultReferenceLength;


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Creates a Lockable SearchResponseReference as a reply to an SearchRequest
     * to indicate the end of a search operation.
     * 
     * @param id
     *            the session unique message id
     */
    public SearchResultReferenceImpl( final int id )
    {
        super( id, TYPE );
    }


    // ------------------------------------------------------------------------
    // SearchResponseReference Interface Method Implementations
    // ------------------------------------------------------------------------

    /**
     * Gets the sequence of LdapUrls as a Referral instance.
     * 
     * @return the sequence of LdapUrls
     */
    public InternalReferral getReferral()
    {
        return this.referral;
    }


    /**
     * Sets the sequence of LdapUrls as a Referral instance.
     * 
     * @param referral the sequence of LdapUrls
     */
    public void setReferral( InternalReferral referral )
    {
        this.referral = referral;
    }


    /**
     * @return The encoded Referral's length
     */
    /* No qualifier */int getReferralLength()
    {
        return referralLength;
    }


    /**
     * Stores the encoded length for the Referrals
     * @param searchReferralLength The encoded length
     */
    /* No qualifier */void setReferralLength( int referralLength )
    {
        this.referralLength = referralLength;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int hash = 37;
        if ( this.referral != null )
        {
            hash = hash * 17 + this.referral.hashCode();
        }
        hash = hash * 17 + super.hashCode();

        return hash;
    }


    /**
     * Checks to see if an object is equal to this SearchResponseReference stub.
     * 
     * @param obj
     *            the object to compare to this response stub
     * @return true if the objects are equivalent false otherwise
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

        InternalSearchResultReference resp = ( InternalSearchResultReference ) obj;

        if ( this.referral != null && resp.getReferral() == null )
        {
            return false;
        }

        if ( this.referral == null && resp.getReferral() != null )
        {
            return false;
        }

        if ( this.referral != null && resp.getReferral() != null && !this.referral.equals( resp.getReferral() ) )
        {
            return false;
        }

        return true;
    }


    /**
     * @return The encoded SearchResultReference's length
     */
    /* No qualifier */int getSearchResultReferenceLength()
    {
        return searchResultReferenceLength;
    }


    /**
     * Stores the encoded length for the SearchResultReference's
     * @param searchResultReferenceLength The encoded length
     */
    /* No qualifier */void setSearchResultReferenceLength( int searchResultReferenceLength )
    {
        this.searchResultReferenceLength = searchResultReferenceLength;
    }


    /**
     * Returns the Search Result Reference string
     * 
     * @return The Search Result Reference string
     */
    public String toString()
    {

        StringBuilder sb = new StringBuilder();

        sb.append( "    Search Result Reference\n" );

        if ( ( referral == null ) || ( referral.getLdapUrls() == null ) || ( referral.getLdapUrls().size() == 0 ) )
        {
            sb.append( "        No Reference\n" );
        }
        else
        {
            sb.append( "        References\n" );

            for ( String url : referral.getLdapUrls() )
            {
                sb.append( "            '" ).append( url ).append( "'\n" );
            }
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
