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
package org.apache.directory.shared.dsmlv2.reponse;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.ldap.model.message.AbstractResponse;
import org.apache.directory.shared.ldap.model.message.MessageTypeEnum;


/**
 * This class represents the DSML Search Response
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResponse extends AbstractResponse
{
    /** The List of contained Search Result Entries */
    private List<SearchResultEntryDsml> searchResultEntryList = new ArrayList<SearchResultEntryDsml>();

    /** The List of contained Search Result References */
    private List<SearchResultReferenceDsml> searchResultReferenceList = new ArrayList<SearchResultReferenceDsml>();

    /** The Search Result Done object */
    private SearchResultDoneDsml searchResultDone;


    /**
     * Creates a new instance of SearchResponse.
     *
     * @param messageId the response eliciting this Request
     * @param type the message type of the response
     */
    public SearchResponse( int messageId, MessageTypeEnum type )
    {
        super( messageId, type );
    }


    /**
     * Adds a Search Result Entry
     *
     * @param searchResultEntry
     *      the Search Result Entry to add
     * @return
     *      true (as per the general contract of the Collection.add method)
     */
    public boolean addSearchResultEntry( SearchResultEntryDsml searchResultEntry )
    {
        return searchResultEntryList.add( searchResultEntry );
    }


    /**
     * Gets the Current Search Result Entry
     * 
     * @return
     *      the current Searche Result Entry
     */
    public SearchResultEntryDsml getCurrentSearchResultEntry()
    {
        if ( searchResultEntryList.size() > 0 )
        {
            return searchResultEntryList.get( searchResultEntryList.size() - 1 );
        }
        else
        {
            return null;
        }
    }


    /**
     * Gets the Search Result Entry List
     *
     * @return
     *      the Search Result Entry List
     */
    public List<SearchResultEntryDsml> getSearchResultEntryList()
    {
        return searchResultEntryList;
    }


    /**
     * Adds a Search Result Reference
     *
     * @param searchResultReference
     *      the Search Result Reference to add
     * @return
     *      true (as per the general contract of the Collection.add method)
     */
    public boolean addSearchResultReference( SearchResultReferenceDsml searchResultReference )
    {
        return searchResultReferenceList.add( searchResultReference );
    }


    /**
     * Gets the current Search Result Reference
     *
     * @return
     *      the current Search Result Reference
     */
    public SearchResultReferenceDsml getCurrentSearchResultReference()
    {
        if ( searchResultReferenceList.size() > 0 )
        {
            return searchResultReferenceList.get( searchResultReferenceList.size() - 1 );
        }
        else
        {
            return null;
        }
    }


    /**
     * Gets the Search Result Reference List
     *
     * @return
     *      the Search Result Reference List
     */
    public List<SearchResultReferenceDsml> getSearchResultReferenceList()
    {
        return searchResultReferenceList;
    }


    /**
     * Gets the Search Result Entry
     * 
     * @return
     *      the Search Result Entry
     */
    public SearchResultDoneDsml getSearchResultDone()
    {
        return searchResultDone;
    }


    /**
     * Sets the Search Result Entry
     *
     * @param searchResultDone
     *      the Search Result Entry to set
     */
    public void setSearchResultDone( SearchResultDoneDsml searchResultDone )
    {
        this.searchResultDone = searchResultDone;
    }


    @Override
    public MessageTypeEnum getType()
    {
        return null;
    }
}
