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
package org.apache.directory.shared.ldap.message.decorators;


import org.apache.directory.shared.ldap.model.message.SearchResultDone;


/**
 * Doc me!
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultDoneDecorator extends MessageDecorator
{
    /** The encoded searchResultDone length */
    private int searchResultDoneLength;



    /**
     * Makes a SearchResultDone encodable.
     *
     * @param decoratedMessage the decorated SearchResultDone
     */
    public SearchResultDoneDecorator( SearchResultDone decoratedMessage )
    {
        super( decoratedMessage );
    }


    public SearchResultDone getSearchResultDone()
    {
        return ( SearchResultDone ) getMessage();
    }


    /**
     * Stores the encoded length for the SearchResultDone
     * @param searchResultDoneLength The encoded length
     */
    public void setSearchResultDoneLength( int searchResultDoneLength )
    {
        this.searchResultDoneLength = searchResultDoneLength;
    }


    /**
     * @return The encoded SearchResultDone's length
     */
    public int getSearchResultDoneLength()
    {
        return searchResultDoneLength;
    }
}
