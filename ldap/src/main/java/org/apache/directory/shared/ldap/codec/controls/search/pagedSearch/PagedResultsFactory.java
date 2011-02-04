/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.shared.ldap.codec.controls.search.pagedSearch;


import org.apache.directory.shared.ldap.codec.ControlFactory;
import org.apache.directory.shared.ldap.codec.LdapCodecService;
import org.apache.directory.shared.ldap.model.message.controls.PagedResults;


/**
 * A {@link ControlFactory} for {@link EntryChange} controls.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PagedResultsFactory implements ControlFactory<PagedResults, PagedResultsDecorator>
{
    /** The LDAP codec service */
    private LdapCodecService codec;

    
    /**
     * Creates a new instance of PagedResultsFactory.
     *
     * @param codec The LDAP codec.
     */
    public PagedResultsFactory( LdapCodecService codec )
    {
        this.codec = codec;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public String getOid()
    {
        return PagedResults.OID;
    }

    
    /**
     * {@inheritDoc}
     */
    public PagedResultsDecorator newCodecControl()
    {
        return new PagedResultsDecorator( codec );
    }
    

    /**
     * {@inheritDoc}
     */
    public PagedResultsDecorator newCodecControl( PagedResults control )
    {
        return new PagedResultsDecorator( codec, control );
    }
}
