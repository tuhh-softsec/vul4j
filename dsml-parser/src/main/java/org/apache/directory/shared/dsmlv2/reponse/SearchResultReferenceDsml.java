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


import java.util.Collection;

import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.model.filter.LdapURL;
import org.apache.directory.shared.ldap.model.message.MessageTypeEnum;
import org.apache.directory.shared.ldap.model.message.Referral;
import org.apache.directory.shared.ldap.model.message.SearchResultReference;
import org.apache.directory.shared.ldap.model.message.SearchResultReferenceImpl;
import org.dom4j.Element;


/**
 * DSML Decorator for SearchResultReference
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultReferenceDsml 
    extends AbstractResponseDsml<SearchResultReference>
    implements SearchResultReference
{
    /**
     * Creates a new getDecoratedMessage() of SearchResultReferenceDsml.
     */
    public SearchResultReferenceDsml( LdapCodecService codec )
    {
        super( codec, new SearchResultReferenceImpl() );
    }


    /**
     * Creates a new getDecoratedMessage() of SearchResultReferenceDsml.
     *
     * @param ldapMessage
     *      the message to decorate
     */
    public SearchResultReferenceDsml( LdapCodecService codec, SearchResultReference ldapMessage )
    {
        super( codec, ldapMessage );
    }


    /**
     * {@inheritDoc}
     */
    public MessageTypeEnum getType()
    {
        return getDecorated().getType();
    }


    /**
     * {@inheritDoc}
     */
    public Element toDsml( Element root )
    {
        Element element = root.addElement( "searchResultReference" );

        // Adding References
        for ( String url : getDecorated().getReferral().getLdapUrls() )
        {
            element.addElement( "ref" ).addText( url );
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
        getDecorated().getReferral().addLdapUrl( searchResultReference.toString() );
    }


    /**
     * Get the list of references
     * 
     * @return An ArrayList of SearchResultReferences
     */
    public Collection<String> getSearchResultReferences()
    {
        return getDecorated().getReferral().getLdapUrls();
    }


    /**
     * {@inheritDoc}
     */
    public Referral getReferral()
    {
        return getDecorated().getReferral();
    }


    /**
     * {@inheritDoc}
     */
    public void setReferral( Referral referral )
    {
        getDecorated().setReferral( referral );
    }
}
