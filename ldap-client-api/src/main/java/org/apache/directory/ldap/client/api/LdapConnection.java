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
import java.util.List;

import org.apache.directory.shared.asn1.util.OID;
import org.apache.directory.shared.ldap.cursor.SearchCursor;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.entry.Modification;
import org.apache.directory.shared.ldap.entry.ModificationOperation;
import org.apache.directory.shared.ldap.entry.Value;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.filter.SearchScope;
import org.apache.directory.shared.ldap.message.AbandonRequest;
import org.apache.directory.shared.ldap.message.AddRequest;
import org.apache.directory.shared.ldap.message.AddResponse;
import org.apache.directory.shared.ldap.message.BindRequest;
import org.apache.directory.shared.ldap.message.BindResponse;
import org.apache.directory.shared.ldap.message.CompareRequest;
import org.apache.directory.shared.ldap.message.CompareResponse;
import org.apache.directory.shared.ldap.message.DeleteRequest;
import org.apache.directory.shared.ldap.message.DeleteResponse;
import org.apache.directory.shared.ldap.message.ExtendedRequest;
import org.apache.directory.shared.ldap.message.ExtendedResponse;
import org.apache.directory.shared.ldap.message.ModifyDnRequest;
import org.apache.directory.shared.ldap.message.ModifyDnResponse;
import org.apache.directory.shared.ldap.message.ModifyRequest;
import org.apache.directory.shared.ldap.message.ModifyResponse;
import org.apache.directory.shared.ldap.message.SearchRequest;
import org.apache.directory.shared.ldap.message.control.Control;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.name.RDN;
import org.apache.directory.shared.ldap.schema.SchemaManager;


/**
 * The root interface for all the LDAP connection implementations.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface LdapConnection
{
    /**
     * Check if we are connected
     *
     * @return <code>true</code> if we are connected.
     */
    boolean isConnected();


    /**
     * Check if we are authenticated
     *
     * @return <code>true</code> if we are connected.
     */
    boolean isAuthenticated();


    /**
     * Connect to the remote LDAP server.
     *
     * @return <code>true</code> if the connection is established, false otherwise
     * @throws LdapException if some error occurred
     * @throws IOException if an I/O exception occurred
     */
    boolean connect() throws LdapException, IOException;


    /**
     * Disconnect from the remote LDAP server
     *
     * @return <code>true</code> if the connection is closed, false otherwise
     * @throws IOException if some I/O error occurs
     */
    boolean close() throws IOException;


    //------------------------ The LDAP operations ------------------------//
    // Add operations                                                      //
    //---------------------------------------------------------------------//
    /**
     * Add an entry to the server. This is a blocking add : the user has
     * to wait for the response until the AddResponse is returned.
     *
     * @param entry The entry to add
     * @return the add operation's response
     * @throws LdapException if some error occurred
     */
    AddResponse add( Entry entry ) throws LdapException;


    /**
     * Add an entry present in the AddRequest to the server.
     *
     * @param addRequest the request object containing an entry and controls(if any)
     * @return the add operation's response
     * @throws LdapException if some error occurred
     */
    AddResponse add( AddRequest addRequest ) throws LdapException;


    /**
     * Abandons a request submitted to the server for performing a particular operation
     *
     * The abandonRequest is always non-blocking, because no response is expected
     *
     * @param messageId the ID of the request message sent to the server
     */
    void abandon( int messageId );


    /**
     * An abandon request essentially with the request message ID of the operation to be canceled
     * and/or potentially some controls and timeout (the controls and timeout are not mandatory).
     *
     * The abandonRequest is always non-blocking, because no response is expected
     *
     * @param abandonRequest the abandon operation's request
     */
    void abandon( AbandonRequest abandonRequest );


    /**
     * Anonymous Bind on a server.
     *
     * @return The BindResponse LdapResponse
     * @throws LdapException if some error occurred
     * @throws IOException if an I/O exception occurred
     */
    BindResponse bind() throws LdapException, IOException;


    /**
     * Simple Bind on a server.
     *
     * @param name The name we use to authenticate the user. It must be a
     * valid DN
     * @param credentials The password. It can't be null
     * @return The BindResponse LdapResponse
     * @throws LdapException if some error occurred
     * @throws IOException if an I/O exception occurred
     */
    BindResponse bind( String name, String credentials ) throws LdapException, IOException;


    /**
     * Simple Bind on a server.
     *
     * @param name The name we use to authenticate the user. It must be a
     * valid DN
     * @param credentials The password. It can't be null
     * @return The BindResponse LdapResponse
     * @throws LdapException if some error occurred
     * @throws IOException if an I/O exception occurred
     */
    BindResponse bind( DN name, String credentials ) throws LdapException, IOException;


    /**
     * Bind to the server using a BindRequest object.
     *
     * @param bindRequest The BindRequest POJO containing all the needed
     * parameters
     * @return A LdapResponse containing the result
     * @throws LdapException if some error occurred
     * @throws IOException if an I/O exception occurred
     */
    BindResponse bind( BindRequest bindRequest ) throws LdapException, IOException;


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
     * @param baseDn The base for the search. It must be a valid
     * DN, and can't be emtpy
     * @param filter The filter to use for this search. It can't be empty
     * @param scope The search scope : OBJECT, ONELEVEL or SUBTREE
     * @param attributes The attributes to use for this search
     * @return A search cursor on the result.
     * @throws LdapException if some error occurred
     */
    SearchCursor search( DN baseDn, String filter, SearchScope scope, String... attributes )
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
     * @param baseDn The base for the search. It must be a valid
     * DN, and can't be emtpy
     * @param filter The filter to use for this search. It can't be empty
     * @param scope The search scope : OBJECT, ONELEVEL or SUBTREE
     * @param attributes The attributes to use for this search
     * @return A search cursor on the result.
     * @throws LdapException if some error occurred
     */
    SearchCursor search( String baseDn, String filter, SearchScope scope, String... attributes )
        throws LdapException;


    /**
     * Performs search in a synchronous mode.
     *
     * @param searchRequest The search configuration
     * @return a search cursor on the result.
     * @throws LdapException if some error occurred
     */
    SearchCursor search( SearchRequest searchRequest ) throws LdapException;


    //------------------------ The LDAP operations ------------------------//
    // Unbind operations                                                   //
    //---------------------------------------------------------------------//
    /**
     * UnBind from a server. This is a request which expect no response.
     * @throws LdapException if some error occurred
     */
    void unBind() throws LdapException;


    /**
     * Set the timeOut for the responses. We wont wait longer than this
     * value.
     *
     * @param timeOut The timeout, in milliseconds
     */
    void setTimeOut( long timeOut );


    /**
     * Applies all the modifications to the entry specified by its DN.
     *
     * @param dn The entry's DN
     * @param modifications The list of modifications to be applied
     * @return the modify operation's response
     * @throws LdapException in case of modify operation failure or timeout happens
     */
    ModifyResponse modify( DN dn, Modification... modifications ) throws LdapException;


    /**
     * Applies all the modifications to the entry specified by its DN.
     *
     * @param dn The entry's DN
     * @param modifications The list of modifications to be applied
     * @return the modify operation's response
     * @throws LdapException in case of modify operation failure or timeout happens
     */
    ModifyResponse modify( String dn, Modification... modifications ) throws LdapException;


    /**
     * Modifies all the attributes present in the entry by applying the same operation.
     *
     * @param entry the entry with the attributes to be modified
     * @param modOp the operation to be applied on all the attributes of the above entry
     * @return the modify operation's response
     * @throws LdapException in case of modify operation failure or timeout happens
     */
    ModifyResponse modify( Entry entry, ModificationOperation modOp ) throws LdapException;


    /**
     * Performs an modify operation based on the modifications present in
     * the ModifyRequest.
     *
     * @param modRequest the request for modify operation
     * @return the modify operation's response
     * @throws LdapException in case of modify operation failure or timeout happens
     */
    ModifyResponse modify( ModifyRequest modRequest ) throws LdapException;


    /**
     * Renames the given entryDn with new RDN and deletes the old RDN.
     *
     * @param entryDn the target DN
     * @param newRdn new RDN for the target DN
     * @return modifyDn operation's response
     * @throws LdapException if some error occurred
     * @see #rename(String, String, boolean)
     */
    ModifyDnResponse rename( String entryDn, String newRdn ) throws LdapException;


    /**
     * Renames the given entryDn with new RDN and deletes the old RDN.
     *
     * @param entryDn the target DN
     * @param newRdn new RDN for the target DN
     * @return modifyDn operation's response
     * @throws LdapException if some error occurred
     * @see #rename(DN, RDN, boolean)
     */
    ModifyDnResponse rename( DN entryDn, RDN newRdn ) throws LdapException;


    /**
     * Renames the given entryDn with new RDN and deletes the old RDN if
     * deleteOldRdn is set to true.
     *
     * @param entryDn the target DN
     * @param newRdn new RDN for the target DN
     * @param deleteOldRdn flag to indicate whether to delete the old RDN
     * @return modifyDn operation's response
     * @throws LdapException if some error occurred
     * @see #rename(DN, RDN, boolean)
     */
    ModifyDnResponse rename( String entryDn, String newRdn, boolean deleteOldRdn ) throws LdapException;


    /**
     * Renames the given entryDn with new RDN and deletes the old RDN if
     * deleteOldRdn is set to true.
     *
     * @param entryDn the target DN
     * @param newRdn new RDN for the target DN
     * @param deleteOldRdn flag to indicate whether to delete the old RDN
     * @return modifyDn operation's response
     * @throws LdapException if some error occurred
     */
    ModifyDnResponse rename( DN entryDn, RDN newRdn, boolean deleteOldRdn ) throws LdapException;


    /**
     * Moves the given entry DN under the new superior DN.
     *
     * @param entryDn the DN of the target entry
     * @param newSuperiorDn DN of the new parent/superior
     * @return modifyDn operation's response
     * @throws LdapException if some error occurred
     * @see #move(DN, DN)
     */
    ModifyDnResponse move( String entryDn, String newSuperiorDn ) throws LdapException;


    /**
     * Moves the given entry DN under the new superior DN.
     *
     * @param entryDn the DN of the target entry
     * @param newSuperiorDn DN of the new parent/superior
     * @return modifyDn operation's response
     * @throws LdapException if some error occurred
     */
    ModifyDnResponse move( DN entryDn, DN newSuperiorDn ) throws LdapException;


    /**
     * Moves and renames the given entryDn. The old RDN will be deleted.
     *
     * @param entryDn The original entry DN
     * @param newDn The new Entry DN
     * @return modifyDn operations response
     * @throws LdapException if some error occurred
     * @see #moveAndRename(DN, DN, boolean)
     */
    ModifyDnResponse moveAndRename( DN entryDn, DN newDn ) throws LdapException;


    /**
     * Moves and renames the given entryDn.The old RDN will be deleted
     *
     * @param entryDn The original entry DN
     * @param newDn The new Entry DN
     * @return modifyDn operations response
     * @throws LdapException if some error occurred
     * @see #moveAndRename(DN, DN, boolean)
     */
    ModifyDnResponse moveAndRename( String entryDn, String newDn ) throws LdapException;


    /**
     * Moves and renames the given entryDn. The old RDN will be deleted if requested.
     *
     * @param entryDn The original entry DN
     * @param newDn The new Entry DN
     * @param deleteOldRdn Tells if the old RDN must be removed
     * @return modifyDn operations response
     * @throws LdapException if some error occurred
     */
    ModifyDnResponse moveAndRename( DN entryDn, DN newDn, boolean deleteOldRdn ) throws LdapException;


    /**
     * Moves and renames the given entryDn. The old RDN will be deleted if requested.
     *
     * @param entryDn The original entry DN
     * @param newDn The new Entry DN
     * @param deleteOldRdn Tells if the old RDN must be removed
     * @return modifyDn operation's response
     * @throws LdapException if some error occurred
     */
    ModifyDnResponse moveAndRename( String entryDn, String newDn, boolean deleteOldRdn )
        throws LdapException;


    /**
     * Performs the modifyDn operation based on the given ModifyDnRequest.
     *
     * @param modDnRequest the request
     * @return modifyDn operation's response
     * @throws LdapException if some error occurred
     */
    ModifyDnResponse modifyDn( ModifyDnRequest modDnRequest ) throws LdapException;


    /**
     * Deletes the entry with the given DN.
     *
     * @param dn the target entry's DN as a String
     * @return the delete operation's response
     * @throws LdapException If the DN is not valid or if the deletion failed
     */
    DeleteResponse delete( String dn ) throws LdapException;


    /**
     * Deletes the entry with the given DN.
     *
     * @param dn the target entry's DN
     * @return the delete operation's response
     * @throws LdapException If the DN is not valid or if the deletion failed
     */
    DeleteResponse delete( DN dn ) throws LdapException;


    /**
     * Performs a delete operation based on the delete request object.
     *
     * @param deleteRequest the delete operation's request
     * @return delete operation's response, null if a non-null listener value is provided
     * @throws LdapException If the DN is not valid or if the deletion failed
     */
    DeleteResponse delete( DeleteRequest deleteRequest ) throws LdapException;


    /**
     * Compares whether a given attribute's value matches that of the
     * existing value of the attribute present in the entry with the given DN.
     *
     * @param dn the target entry's String DN
     * @param attributeName the attribute's name
     * @param value a String value with which the target entry's attribute value to be compared with
     * @return compare operation's response
     * @throws LdapException if some error occurred
     */
    CompareResponse compare( String dn, String attributeName, String value ) throws LdapException;


    /**
     * Compares whether a given attribute's value matches that of the
     * existing value of the attribute present in the entry with the given DN.
     *
     * @param dn the target entry's String DN
     * @param attributeName the attribute's name
     * @param value a byte[] value with which the target entry's attribute value to be compared with
     * @return compare operation's response
     * @throws LdapException if some error occurred
     */
    CompareResponse compare( String dn, String attributeName, byte[] value ) throws LdapException;


    /**
     * Compares whether a given attribute's value matches that of the
     * existing value of the attribute present in the entry with the given DN.
     *
     * @param dn the target entry's String DN
     * @param attributeName the attribute's name
     * @param value a Value<?> value with which the target entry's attribute value to be compared with
     * @return compare operation's response
     * @throws LdapException if some error occurred
     */
    CompareResponse compare( String dn, String attributeName, Value<?> value ) throws LdapException;


    /**
     * Compares whether a given attribute's value matches that of the
     * existing value of the attribute present in the entry with the given DN.
     *
     * @param dn the target entry's DN
     * @param attributeName the attribute's name
     * @param value a String value with which the target entry's attribute value to be compared with
     * @return compare operation's response
     * @throws LdapException if some error occurred
     */
    CompareResponse compare( DN dn, String attributeName, String value ) throws LdapException;


    /**
     * Compares whether a given attribute's value matches that of the
     * existing value of the attribute present in the entry with the given DN.
     *
     * @param dn the target entry's DN
     * @param attributeName the attribute's name
     * @param value a byte[] value with which the target entry's attribute value to be compared with
     * @return compare operation's response
     * @throws LdapException if some error occurred
     */
    CompareResponse compare( DN dn, String attributeName, byte[] value ) throws LdapException;


    /**
     * Compares whether a given attribute's value matches that of the
     * existing value of the attribute present in the entry with the given DN.
     *
     * @param dn the target entry's DN
     * @param attributeName the attribute's name
     * @param value a Value<?> value with which the target entry's attribute value to be compared with
     * @return compare operation's response
     * @throws LdapException if some error occurred
     */
    CompareResponse compare( DN dn, String attributeName, Value<?> value ) throws LdapException;


    /**
     * Compares an entry's attribute's value with that of the given value.
     *
     * @param compareRequest the CompareRequest which contains the target DN, attribute name and value
     * @return compare operation's response
     * @throws LdapException if some error occurred
     */
    CompareResponse compare( CompareRequest compareRequest ) throws LdapException;


    /**
     * Sends a extended operation request to the server with the given OID and no value.
     *
     * @param oid the object identifier of the extended operation
     * @return extended operation's response
     * @throws LdapException if some error occurred
     * @see #extended(org.apache.directory.shared.asn1.util.OID, byte[])
     */
    ExtendedResponse extended( String oid ) throws LdapException;


    /**
     * Sends a extended operation request to the server with the given OID and value.
     *
     * @param oid the object identifier of the extended operation
     * @param value value to be used by the extended operation, can be a null value
     * @return extended operation's response
     * @throws LdapException if some error occurred
     * @see #extended(org.apache.directory.shared.asn1.util.OID, byte[])
     */
    ExtendedResponse extended( String oid, byte[] value ) throws LdapException;


    /**
     * Sends a extended operation request to the server with the given OID and no value.
     *
     * @param oid the object identifier of the extended operation
     * @return extended operation's response
     * @throws LdapException if some error occurred
     * @see #extended(org.apache.directory.shared.asn1.util.OID, byte[])
     */
    ExtendedResponse extended( OID oid ) throws LdapException;


    /**
     * Sends a extended operation request to the server with the given OID and value.
     *
     * @param oid the object identifier of the extended operation
     * @param value value to be used by the extended operation, can be a null value
     * @return extended operation's response
     * @throws LdapException if some error occurred
     */
    ExtendedResponse extended( OID oid, byte[] value ) throws LdapException;


    /**
     * Performs an extended operation based on the Extended request object.
     *
     * @param extendedRequest the extended operation's request
     * @return Extended operation's response
     * @throws LdapException If the DN is not valid or if the extended operation failed
     */
    ExtendedResponse extended( ExtendedRequest extendedRequest ) throws LdapException;


    /**
     * Tells if an Entry exists in the server.
     * 
     * @param dn The DN for the entry we want to check the existence
     * @return <code>true</code> if the entry exists, <code>false</code> otherwise. 
     * Note that if the entry exists but if the user does not have the permission to
     * read it, <code>false</code> will also be returned 
     * @throws LdapException if some error occurred
     */
    boolean exists( String dn ) throws LdapException;


    /**
     * Tells if an Entry exists in the server.
     * 
     * @param dn The DN for the entry we want to check the existence
     * @return <code>true</code> if the entry exists, <code>false</code> otherwise. 
     * Note that if the entry exists but if the user does not have the permission to
     * read it, <code>false</code> will also be returned 
     * @throws LdapException if some error occurred
     */
    boolean exists( DN dn ) throws LdapException;


    /**
     * Searches for an entry having the given DN.
     *
     * @param dn the DN of the entry to be fetched
     * @return the Entry with the given DN or null if no entry exists with that DN
     * @throws LdapException in case of any problems while searching for the DN or if the returned response contains a referral
     * @see #lookup(DN, String...)
     */
    Entry lookup( DN dn ) throws LdapException;


    /**
     * Searches for an entry having the given DN.
     *
     * @param dn the DN of the entry to be fetched
     * @return the Entry with the given DN or null if no entry exists with that DN
     * @throws LdapException in case of any problems while searching for the DN or if the returned response contains a referral
     * @see #lookup(String, String...)
     */
    Entry lookup( String dn ) throws LdapException;


    /**
     * Searches for an entry having the given DN.
     *
     * @param dn the DN of the entry to be fetched
     * @param attributes the attributes to be returned along with entry
     * @return the Entry with the given DN or null if no entry exists with that DN
     * @throws LdapException in case of any problems while searching for the DN or if the returned response contains a referral
     */
    Entry lookup( DN dn, String... attributes ) throws LdapException;


    /**
     * Searches for an entry having the given DN.
     *
     * @param dn the DN of the entry to be fetched
     * @param controls the controls to use
     * @param attributes the attributes to be returned along with entry
     * @return the Entry with the given DN or null if no entry exists with that DN
     * @throws LdapException in case of any problems while searching for the DN or if the returned response contains a referral
     */
    Entry lookup( DN dn, Control[] controls, String... attributes ) throws LdapException;



    /**
     * Searches for an entry having the given DN.
     *
     * @param dn the DN of the entry to be fetched
     * @param attributes the attributes to be returned along with entry
     * @return the Entry with the given DN or null if no entry exists with that DN
     * @throws LdapException in case of any problems while searching for the DN or if the returned response contains a referral
     * @see #lookup(DN, String...)
     */
    Entry lookup( String dn, String... attributes ) throws LdapException;


    /**
     * Searches for an entry having the given DN.
     *
     * @param dn the DN of the entry to be fetched
     * @param controls the controls to use
     * @param attributes the attributes to be returned along with entry
     * @return the Entry with the given DN or null if no entry exists with that DN
     * @throws LdapException in case of any problems while searching for the DN or if the returned response contains a referral
     * @see #lookup(DN, String...)
     */
    Entry lookup( String dn, Control[] controls, String... attributes ) throws LdapException;


    /**
     * Checks if a control with the given OID is supported.
     *
     * @param controlOID the OID of the control
     * @return true if the control is supported, false otherwise
     * @throws LdapException if some error occurred
     */
    boolean isControlSupported( String controlOID ) throws LdapException;


    /**
     * Get the Controls supported by server.
     *
     * @return a list of control OIDs supported by server
     * @throws LdapException if some error occurred
     */
    List<String> getSupportedControls() throws LdapException;


    /**
     * Loads all the default schemas that are bundled with the API.<br><br>
     * <b>Note:</b> This method enables <b>all</b> schemas prior to loading
     * @throws LdapException in case of problems while loading the schema
     */
    void loadSchema() throws LdapException;


    /**
     * @return The SchemaManager associated with this LdapConection if any
     */
    SchemaManager getSchemaManager();


    /**
     * Checks if there is a ResponseFuture associated with the given message ID.
     *
     * @param messageId ID of the request
     * @return true if there is a non-null future exists, false otherwise
     */
    boolean doesFutureExistFor( int messageId );

}