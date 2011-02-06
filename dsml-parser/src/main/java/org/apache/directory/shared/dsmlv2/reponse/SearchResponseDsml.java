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

import org.apache.directory.shared.dsmlv2.DsmlDecorator;
import org.apache.directory.shared.ldap.codec.LdapCodecService;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.ldap.model.message.Response;
import org.apache.directory.shared.ldap.model.message.SearchResultDone;
import org.apache.directory.shared.ldap.model.message.SearchResultEntry;
import org.apache.directory.shared.ldap.model.message.SearchResultReference;
import org.dom4j.Element;


/**
 * This class represents the Search Response Dsml Container. 
 * It is used to store Search Responses (Search Result Entry, 
 * Search Result Reference and SearchResultDone).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResponseDsml extends AbstractResponseDsml<Response>
{
    /** The responses */
    private List<DsmlDecorator<? extends Response>> responses = 
        new ArrayList<DsmlDecorator<? extends Response>>();


    /**
     * Creates a new getDecoratedMessage() of SearchResponseDsml.
     */
    public SearchResponseDsml( LdapCodecService codec )
    {
        super( codec, null );
    }


    /**
     * Creates a new getDecoratedMessage() of SearchResponseDsml.
     *
     * @param response the LDAP response message to decorate
     */
    public SearchResponseDsml( LdapCodecService codec, Message response )
    {
        super( codec, ( Response ) response );
    }


    /**
     * Adds a response.
     *
     * @param response
     *      the response to add
     * @return
     *      true (as per the general contract of the Collection.add method).
     */
    public boolean addResponse( DsmlDecorator<? extends Response> response )
    {
        if ( response instanceof SearchResultEntry )
        {
            ( ( SearchResponse ) getDecorated() ).addSearchResultEntry( 
                ( SearchResultEntryDsml ) response );            
        }
        else if ( response instanceof SearchResultReference )
        {
            ( ( SearchResponse ) getDecorated() ).addSearchResultReference( 
                ( SearchResultReferenceDsml ) response );            
        }
        else if ( response instanceof SearchResultDone )
        {
            ( ( SearchResponse ) getDecorated() ).setSearchResultDone( 
                ( SearchResultDoneDsml ) response );            
        }
        else
        {
            throw new IllegalArgumentException( "Unidentified search resp type" );
        }
        
        return responses.add( response );
    }


    /**
     * Removes a response.
     *
     * @param response
     *      the response to remove
     * @return
     *      true if this list contained the specified element.
     */
    public boolean removeResponse( DsmlDecorator<Response> response )
    {
        return responses.remove( response );
    }


    /**
     * {@inheritDoc}
     */
    public Element toDsml( Element root )
    {
        Element element = root.addElement( "searchResponse" );

        // RequestID
        if ( getDecorated() != null )
        {
            int requestID = getDecorated().getMessageId();
            if ( requestID > 0 )
            {
                element.addAttribute( "requestID", "" + requestID );
            }
        }

        for ( DsmlDecorator<? extends Response> response : responses )
        {
            response.toDsml( element );
        }

        return element;
    }
}
