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
package org.apache.directory.shared.ldap.codec.controls.search.pagedSearch;


import org.apache.directory.shared.asn1.ber.AbstractContainer;
import org.apache.directory.shared.ldap.codec.ILdapCodecService;
import org.apache.directory.shared.ldap.model.message.controls.PagedResults;


/**
 * A container for the Paged Search Control.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PagedResultsContainer extends AbstractContainer
{
    /** PagedSearchControl */
    private PagedResultsDecorator control;
    
    private ILdapCodecService codec;


    /**
     * Creates a new PagedSearchControl container object. We will store one grammar,
     * it's enough ...
     * @param codec The encoder decoder for this container
     */
    public PagedResultsContainer( ILdapCodecService codec )
    {
        super();
        this.codec = codec;
        stateStack = new int[1];
        grammar = PagedResultsGrammar.getInstance();
        setTransition( PagedResultsStates.START_STATE );
    }


    /**
     * Creates a new PagedSearchControl container object to contain a PagedResults
     * Control, which is optionally decorated if is not a decorator already. If it
     * is a decorator then it is used as the decorator for this container.
     *
     * @param codec The encoder decoder for this container
     * @param control A PagedResults Control to optionally be wrapped.
     */
    public PagedResultsContainer( ILdapCodecService codec, PagedResults control )
    {
        this( codec );
        decorate( control );
    }


    /**
     * @return Returns the paged search control.
     */
    public PagedResultsDecorator getDecorator()
    {

        return control;
    }


    public void decorate( PagedResults control )
    {
        if ( control instanceof PagedResultsDecorator )
        {
            this.control = ( PagedResultsDecorator ) control;
        }
        else
        {
            this.control = new PagedResultsDecorator( codec, control );
        }
    }


    /**
     * Set a PagedSearchControl Object into the container. It will be completed by
     * the ldapDecoder.
     * 
     * @param control the PagedSearchControl to set.
     */
    public void setPagedSearchControl( PagedResultsDecorator control )
    {
        this.control = control;
    }

    /**
     * Clean the container
     */
    public void clean()
    {
        super.clean();
        control = null;
    }
}
