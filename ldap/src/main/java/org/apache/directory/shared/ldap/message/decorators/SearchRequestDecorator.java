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


import org.apache.directory.shared.ldap.codec.search.Filter;
import org.apache.directory.shared.ldap.message.SearchRequestImpl;
import org.apache.directory.shared.ldap.model.message.SearchRequest;


/**
 * Doc me!
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchRequestDecorator extends MessageDecorator
{
    /** The searchRequest length */
    private int searchRequestLength;

    /** The attributeDescriptionList length */
    private int attributeDescriptionListLength;


    /**
     * Makes a SearchRequest encodable.
     *
     * @param decoratedMessage the decorated SearchRequest
     */
    public SearchRequestDecorator( SearchRequest decoratedMessage )
    {
        super( decoratedMessage );
    }


    public SearchRequest getSearchRequest()
    {
        return ( SearchRequest ) getMessage();
    }


    /**
     * Stores the encoded length for the SearchRequest
     * @param searchRequestLength The encoded length
     */
    public void setSearchRequestLength( int searchRequestLength )
    {
        this.searchRequestLength = searchRequestLength;
    }


    /**
     * @return The encoded SearchRequest's length
     */
    public int getSearchRequestLength()
    {
        return searchRequestLength;
    }


    /**
     * Stores the encoded length for the list of attributes
     * @param attributeDescriptionListLength The encoded length of the attributes
     */
    public void setAttributeDescriptionListLength( int attributeDescriptionListLength )
    {
        this.attributeDescriptionListLength = attributeDescriptionListLength;
    }


    /**
     * @return The encoded SearchRequest's attributes length
     */
    public int getAttributeDescriptionListLength()
    {
        return attributeDescriptionListLength;
    }


    public Filter getCurrentFilter()
    {
        // TODO - very very bad move for now to make sure it works but need to remove Impl ref and use interface
        return ( ( SearchRequestImpl ) getSearchRequest() ).getCurrentFilter();
    }
}
