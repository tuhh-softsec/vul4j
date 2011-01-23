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
package org.apache.directory.ldap.client.api;


import java.io.IOException;

import org.apache.directory.ldap.client.api.future.AddFuture;
import org.apache.directory.ldap.client.api.future.BindFuture;
import org.apache.directory.ldap.client.api.future.CompareFuture;
import org.apache.directory.ldap.client.api.future.DeleteFuture;
import org.apache.directory.ldap.client.api.future.ExtendedFuture;
import org.apache.directory.ldap.client.api.future.ModifyDnFuture;
import org.apache.directory.ldap.client.api.future.ModifyFuture;
import org.apache.directory.ldap.client.api.future.SearchFuture;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.filter.SearchScope;
import org.apache.directory.shared.ldap.model.message.AddRequest;
import org.apache.directory.shared.ldap.model.message.*;
import org.apache.directory.shared.ldap.model.message.DeleteRequest;
import org.apache.directory.shared.ldap.model.message.BindRequest;
import org.apache.directory.shared.ldap.model.message.CompareRequest;
import org.apache.directory.shared.ldap.model.message.ModifyDnRequest;
import org.apache.directory.shared.ldap.name.Dn;


/**
 * Root interface for all asynchronous LDAP connections.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface LdapAsyncConnection extends LdapConnection
{

    /**
     * Add an entry to the server asynchronously. This is a non blocking add : 
     * the user has to get for the response from the returned Future.
     * 
     * @param entry The entry to add
     * @return the add operation's future
     * @throws LdapException if some error occurred
     */
    AddFuture addAsync( Entry entry ) throws LdapException;


    /**
     * Add an entry present in the AddRequest to the server.
     * 
     * @param addRequest the request object containing an entry and controls(if any)
     * @return the add operation's future
     * @throws LdapException if some error occurred
     */
    AddFuture addAsync( AddRequest addRequest ) throws LdapException;


    /**
     * Anonymous asynchronous Bind on a server. 
     *
     * @return the bind operation's future
     * @throws LdapException if some error occurred
     * @throws IOException if some IO error occurred
     */
    BindFuture bindAsync() throws LdapException, IOException;


    /**
     * Simple asynchronous Bind on a server.
     *
     * @param name The name we use to authenticate the user, it must be a valid Dn
     * @param credentials The password, it can't be null 
     * @return the bind operation's future
     * @throws LdapException if some error occurred
     * @throws IOException if some IO error occurred
     */
    BindFuture bindAsync( String name, String credentials ) throws LdapException, IOException;


    /**
     * Simple asynchronous Bind on a server.
     *
     * @param name The name we use to authenticate the user, it must be a valid Dn
     * @param credentials The password, it can't be null
     * @return the bind operation's future
     * @throws LdapException if some error occurred
     * @throws IOException if some IO error occurred
     */
    BindFuture bindAsync( Dn name, String credentials ) throws LdapException, IOException;


    /**
     * Do an asynchronous bind, based on a BindRequest.
     *
     * @param bindRequest The BindRequest to send
     * @return the bind operation's future
     * @throws LdapException if some error occurred
     * @throws IOException if some IO error occurred
     */
    BindFuture bindAsync( BindRequest bindRequest ) throws LdapException, IOException;


    /**
     * Do an asynchronous search, on the base object, using the given filter. The
     * SearchRequest parameters default to :
     * <pre>
     * Scope : ONE
     * DerefAlias : ALWAYS
     * SizeLimit : none
     * TimeLimit : none
     * TypesOnly : false
     * Attributes : all the user's attributes.
     * This method is blocking.
     * </pre>
     * 
     * @param baseDn The base for the search, it must be a valid Dn, and can't be emtpy
     * @param filter The filter to use for this search, it can't be empty
     * @param scope The search scope : OBJECT, ONELEVEL or SUBTREE 
     * @param attributes The attributes for this search 
     * @return the search operation's future
     * @throws org.apache.directory.shared.ldap.model.exception.LdapException if some error occurred
     */
    SearchFuture searchAsync( String baseDn, String filter, SearchScope scope, String... attributes )
        throws LdapException;


    /**
     * Do an asynchronous search, on the base object, using the given filter. The
     * SearchRequest parameters default to :
     * <pre>
     * Scope : ONE
     * DerefAlias : ALWAYS
     * SizeLimit : none
     * TimeLimit : none
     * TypesOnly : false
     * Attributes : all the user's attributes.
     * This method is blocking.
     * </pre>
     * 
     * @param baseDn The base for the search, it must be a valid Dn, and can't be empty
     * @param filter The filter to use for this search, it can't be empty
     * @param scope The search scope : OBJECT, ONELEVEL or SUBTREE
     * @param attributes The attributes for this search 
     * @return the search operation's future
     * @throws LdapException if some error occurred
     */
    SearchFuture searchAsync( Dn baseDn, String filter, SearchScope scope, String... attributes )
        throws LdapException;


    /**
     * Do a search, on the base object, using the given filter. The
     * SearchRequest parameters default to :
     * <pre>
     * Scope : ONE
     * DerefAlias : ALWAYS
     * SizeLimit : none
     * TimeLimit : none
     * TypesOnly : false
     * Attributes : all the user's attributes.
     * This method is blocking.
     * </pre>
     * 
     * @param searchRequest The search request to send to the server
     * @return the search operation's future
     * @throws LdapException if some error occurred
     */
    SearchFuture searchAsync( SearchRequest searchRequest ) throws LdapException;


    /**
     * Performs an asynchronous modify operation based on the modifications present in 
     * the ModifyRequest.
     *
     * @param modRequest the request for modify operation
     * @return the modify operation's future
     * @throws LdapException in case of modify operation failure or timeout happens
     */
    ModifyFuture modifyAsync( ModifyRequest modRequest ) throws LdapException;


    /**
     * Performs the modifyDn operation based on the given ModifyDnRequest.
     *
     * @param modDnRequest the request
     * @return modifyDn operation's future
     * @throws LdapException if some error occurred
     */
    ModifyDnFuture modifyDnAsync( ModifyDnRequest modDnRequest ) throws LdapException;


    /**
     * Performs an asynchronous delete operation based on the delete request object.
     *  
     * @param delRequest the delete operation's request
     * @return delete operation's future
     * @throws LdapException If the Dn is not valid or if the deletion failed
     */
    DeleteFuture deleteAsync( DeleteRequest delRequest ) throws LdapException;


    /**
     * Asynchronously compares an entry's attribute's value with that of the given value
     *   
     * @param compareRequest the CompareRequest which contains the target Dn, attribute name and value
     * @return compare operation's future
     * @throws LdapException if some error occurred
     */
    CompareFuture compareAsync( CompareRequest compareRequest ) throws LdapException;


    /**
     * Asynchronously requests the server to perform an extended operation based on the given request.
     *
     * @param extendedRequest the object containing the details of the extended operation to be performed
     * @return extended operation's Future
     * @throws LdapException if some error occurred
     */
    ExtendedFuture extendedAsync( ExtendedRequest extendedRequest ) throws LdapException;


    /**
     * Configuration of LdapNetworkConnection
     * 
     * @return the configuration of the LDAP connection
     */
    LdapConnectionConfig getConfig();
}