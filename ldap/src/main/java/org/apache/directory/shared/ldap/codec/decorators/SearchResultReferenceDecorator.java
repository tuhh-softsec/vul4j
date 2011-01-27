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


import org.apache.directory.shared.ldap.model.message.SearchResultReference;


/**
 * A decorator for the SearchResultReference message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultReferenceDecorator extends MessageDecorator
{
    /** The length of the referral */
    private int referralLength;

    /** The search result reference length */
    private int searchResultReferenceLength;


    /**
     * Makes a SearchResultReference encodable.
     *
     * @param decoratedMessage the decorated SearchResultReference
     */
    public SearchResultReferenceDecorator( SearchResultReference decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated SearchResultReference
     */
    public SearchResultReference getSearchResultReference()
    {
        return ( SearchResultReference ) getDecoratedMessage();
    }


    /**
     * @return The encoded Referral's length
     */
    public int getReferralLength()
    {
        return referralLength;
    }


    /**
     * Stores the encoded length for the Referrals
     * @param referralLength The encoded length
     */
    public void setReferralLength( int referralLength )
    {
        this.referralLength = referralLength;
    }


    /**
     * @return The encoded SearchResultReference's length
     */
    public int getSearchResultReferenceLength()
    {
        return searchResultReferenceLength;
    }


    /**
     * Stores the encoded length for the SearchResultReference's
     * @param searchResultReferenceLength The encoded length
     */
    public void setSearchResultReferenceLength( int searchResultReferenceLength )
    {
        this.searchResultReferenceLength = searchResultReferenceLength;
    }
}
