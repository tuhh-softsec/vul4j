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


import java.util.List;

import org.apache.directory.shared.ldap.model.filter.LdapURL;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.ldap.model.message.MessageTypeEnum;
import org.apache.directory.shared.ldap.model.message.SearchResultReference;
import org.apache.directory.shared.ldap.message.SearchResultReferenceImpl;
import org.dom4j.Element;


/**
 * DSML Decorator for SearchResultReference
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultReferenceDsml extends AbstractResponseDsml
{
    /**
     * Creates a new instance of SearchResultReferenceDsml.
     */
    public SearchResultReferenceDsml()
    {
        super( new SearchResultReferenceImpl() );
    }


    /**
     * Creates a new instance of SearchResultReferenceDsml.
     *
     * @param ldapMessage
     *      the message to decorate
     */
    public SearchResultReferenceDsml( Message ldapMessage )
    {
        super( ldapMessage );
    }


    /**
     * {@inheritDoc}
     */
    public MessageTypeEnum getType()
    {
        return instance.getType();
    }


    /**
     * {@inheritDoc}
     */
    public Element toDsml( Element root )
    {
        Element element = root.addElement( "searchResultReference" );
        SearchResultReference searchResultReference = ( SearchResultReference ) instance;

        // Adding References
        List<String> refsList = ( List<String> ) searchResultReference.getReferral().getLdapUrls();

        for ( int i = 0; i < refsList.size(); i++ )
        {
            element.addElement( "ref" ).addText( refsList.get( i ).toString() );
        }

        return element;
    }


    /**
     * Add a new reference to the list.
     * 
     * @param searchResultReference The search result reference
     */
    public void addSearchResultReference( LdapURL searchResultReference )
    {
        ( ( SearchResultReference ) instance ).getReferral().addLdapUrl( searchResultReference.toString() );
    }


    /**
     * Get the list of references
     * 
     * @return An ArrayList of SearchResultReferences
     */
    public List<String> getSearchResultReferences()
    {
        return ( List<String> ) ( ( SearchResultReference ) instance ).getReferral().getLdapUrls();
    }
}
