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
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.filter.SearchScope;
import org.apache.directory.shared.ldap.message.AddRequest;
import org.apache.directory.shared.ldap.message.BindRequest;
import org.apache.directory.shared.ldap.message.CompareRequest;
import org.apache.directory.shared.ldap.message.DeleteRequest;
import org.apache.directory.shared.ldap.message.ExtendedRequest;
import org.apache.directory.shared.ldap.message.ModifyDnRequest;
import org.apache.directory.shared.ldap.message.ModifyRequest;
import org.apache.directory.shared.ldap.message.SearchRequest;
import org.apache.directory.shared.ldap.name.DN;


public interface LdapAsyncConnection extends LdapConnection
{

    /**
     * Add an entry to the server asynchronously. This is a non blocking add : 
     * the user has to get for the response from the returned Future.
     * 
     * @param entry The entry to add
     * @return the add operation's Future 
     */
    AddFuture addAsync( Entry entry ) throws LdapException;


    /**
     * Add an entry present in the AddRequest to the server.
     * 
     * @param addRequest the request object containing an entry and controls(if any)
     * @return the add operation's response
     * @throws LdapException
     */
    AddFuture addAsync( AddRequest addRequest ) throws LdapException;


    /**
     * Anonymous asynchronous Bind on a server. 
     *
     * @return The BindFuture
     */
    BindFuture bindAsync() throws LdapException, IOException;


    /**
     * Simple asynchronous Bind on a server.
     *
     * @param name The name we use to authenticate the user. It must be a 
     * valid DN
     * @param credentials The password. It can't be null 
     * @return The BindResponse LdapResponse 
     */
    BindFuture bindAsync( String name, String credentials ) throws LdapException, IOException;


    /**
     * Simple asynchronous Bind on a server.
     *
     * @param name The name we use to authenticate the user. It must be a 
     * valid DN
     * @param credentials The password. It can't be null 
     * @return The BindResponse LdapResponse 
     */
    BindFuture bindAsync( DN name, String credentials ) throws LdapException, IOException;


    /**
     * Do an asynchronous bind, based on a BindRequest.
     *
     * @param bindRequest The BindRequest to send
     * @return BindFuture A future
     */
    BindFuture bindAsync( BindRequest bindRequest ) throws LdapException, IOException;


    /**
     * Do an asynchronous search, on the base object, using the given filter. The
     * SearchRequest parameters default to :
     * Scope : ONE
     * DerefAlias : ALWAYS
     * SizeLimit : none
     * TimeLimit : none
     * TypesOnly : false
     * Attributes : all the user's attributes.
     * This method is blocking.
     * 
     * @param baseDn The base for the search. It must be a valid
     * DN, and can't be emtpy
     * @param filter The filter to use for this search. It can't be empty
     * @param scope The search scope : OBJECT, ONELEVEL or SUBTREE 
     * @param attributes The attributes for this search 
     * @return A cursor on the result. 
     */
    SearchFuture searchAsync( String baseDn, String filter, SearchScope scope, String... attributes )
        throws LdapException;


    /**
     * Do an asynchronous search, on the base object, using the given filter. The
     * SearchRequest parameters default to :
     * Scope : ONE
     * DerefAlias : ALWAYS
     * SizeLimit : none
     * TimeLimit : none
     * TypesOnly : false
     * Attributes : all the user's attributes.
     * This method is blocking.
     * 
     * @param baseDn The base for the search. It must be a valid
     * DN, and can't be emtpy
     * @param filter The filter to use for this search. It can't be empty
     * @param scope The search scope : OBJECT, ONELEVEL or SUBTREE
     * @param attributes The attributes for this search 
     * @return A cursor on the result. 
     */
    SearchFuture searchAsync( DN baseDn, String filter, SearchScope scope, String... attributes )
        throws LdapException;


    /**
     * Do a search, on the base object, using the given filter. The
     * SearchRequest parameters default to :
     * Scope : ONE
     * DerefAlias : ALWAYS
     * SizeLimit : none
     * TimeLimit : none
     * TypesOnly : false
     * Attributes : all the user's attributes.
     * This method is blocking.
     * 
     * @param searchRequest The search request to send to the server
     * @return A Future 
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
     * 
     * performs the modifyDn operation based on the given ModifyDnRequest.
     *
     * @param modDnRequest the request
     * @return modifyDn operations response, null if non-null listener is provided
     * @throws LdapException
     */
    ModifyDnFuture modifyDnAsync( ModifyDnRequest modDnRequest ) throws LdapException;


    /**
     * Performs an asynchronous delete operation based on the delete request object.
     *  
     * @param delRequest the delete operation's request
     * @return delete operation's response, null if a non-null listener value is provided
     * @throws LdapException If the DN is not valid or if the deletion failed
     */
    DeleteFuture deleteAsync( DeleteRequest delRequest ) throws LdapException;


    /**
     * Asynchronously compares an entry's attribute's value with that of the given value
     *   
     * @param compareRequest the CompareRequest which contains the target DN, attribute name and value
     * @return compare operation's future
     * @throws LdapException
     */
    CompareFuture compareAsync( CompareRequest compareRequest ) throws LdapException;


    /**
     * Asynchronously requests the server to perform an extended operation based on the given request.
     *
     * @param extendedRequest the object containing the details of the extended operation to be performed
     * @return extended operation's Future
     * @throws LdapException
     */
    ExtendedFuture extendedAsync( ExtendedRequest extendedRequest ) throws LdapException;


    /**
     * configuration of LdapNetworkConnection
     * 
     * @return the configuration of the ldap connection
     */
    LdapConnectionConfig getConfig();
}