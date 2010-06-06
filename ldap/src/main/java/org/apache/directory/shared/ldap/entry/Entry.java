/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.directory.shared.ldap.entry;


import java.io.Externalizable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.directory.shared.ldap.exception.LdapException;

import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.schema.AttributeType;


/**
 * <p>
 * This interface represent a LDAP entry. An LDAP entry contains :
 * <li> A distinguished name (DN)</li>
 * <li> A list of attributes</li>
 * </p>
 * <p>
 * The available methods on this object are described in this interface.
 * </p>
 * <p>
 * This interface is used by the serverEntry and clientEntry interfaces.
 *</p>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface Entry extends Cloneable, Iterable<EntryAttribute>, Externalizable
{
    /**
     * Remove all the attributes for this entry. The DN is not reset
     */
    void clear();


    /**
     * Clone the current entry
     */
    Entry clone();


    /**
     * Get this entry's DN.
     *
     * @return The entry's DN
     */
    DN getDn();


    /**
     * Tells if an entry as a specific ObjectClass value
     * 
     * @param objectClass The ObjectClassw we want to check
     * @return <code>true</code> if the ObjectClass value is present 
     * in the ObjectClass attribute
     */
    boolean hasObjectClass( String objectClass );


    /**
     * Tells if an entry has a specific ObjectClass Attribute
     * 
     * @param objectClass The ObjectClass we want to check
     * @return <code>true</code> if the ObjectClass value is present 
     * in the ObjectClass attribute
     */
    boolean hasObjectClass( EntryAttribute objectClass );

    
    /**
     * <p>
     * Returns the attribute with the specified alias. The return value
     * is <code>null</code> if no match is found.  
     * </p>
     * <p>An Attribute with an id different from the supplied alias may 
     * be returned: for example a call with 'cn' may in some implementations 
     * return an Attribute whose getId() field returns 'commonName'.
     * </p>
     *
     * @param alias an aliased name of the attribute identifier
     * @return the attribute associated with the alias
     */
    EntryAttribute get( String alias );

    
    /**
     * <p>
     * Returns the attribute with the specified AttributeType. The return value
     * is <code>null</code> if no match is found.  
     * </p>
     *
     * @param attributeType The attributeType we are looking for.
     * @return the attribute associated with the AttributeType.
     */
    /**
     * Returns the attribute associated with an AttributeType
     * 
     * @param attributeType the AttributeType we are looking for
     * @return the associated attribute
     */
    EntryAttribute get( AttributeType attributeType );


    /**
     * Gets all the attributes type
     *
     * @return The combined set of all the attributes.
     */
    Set<AttributeType> getAttributeTypes();
    
    
    /**
     * <p>
     * Put some new ClientAttribute using the User Provided ID. 
     * No value is inserted. 
     * </p>
     * <p>
     * If an existing Attribute is found, it will be replaced by an
     * empty attribute, and returned to the caller.
     * </p>
     * 
     * @param upIds The user provided IDs of the AttributeTypes to add.
     * @return A list of replaced Attributes.
     */
    List<EntryAttribute> set( String... upIds );


    /**
     * <p>
     * Put some new attributes using the attributeTypes. 
     * No value is inserted. 
     * </p>
     * <p>
     * If an existing Attribute is found, it will be replaced by an
     * empty attribute, and returned to the caller.
     * </p>
     * 
     * @param attributeTypes The AttributeTypes to add.
     * @return A list of replaced Attributes, of <code>null</code> if no attribute are removed.
     */
    List<EntryAttribute> set( AttributeType... attributeTypes );


    /**
     * Set this entry's DN.
     *
     * @param dn The DN associated with this entry
     */
    void setDn( DN dn );


    /**
     * Fail fast check performed to determine entry consistency according to schema
     * characteristics.
     *
     * @return true if the entry, it's attributes and their values are consistent
     * with the schema
     */
    boolean isValid();


    /**
     * Check performed to determine entry consistency according to the schema
     * requirements of a particular objectClass.  The entry must be of that objectClass
     * to return true: meaning if the entry's objectClass attribute does not contain
     * the objectClass argument, then false should be returned.
     *
     * @param objectClass the objectClass to use while checking for validity
     * @return true if the entry, it's attributes and their values are consistent
     * with the objectClass
     */
    boolean isValid( String objectClass );

    
    /**
     * Check performed to determine entry consistency according to the schema
     * requirements of a particular objectClass.  The entry must be of that objectClass
     * to return true: meaning if the entry's objectClass attribute does not contain
     * the objectClass argument, then false should be returned.
     *
     * @param objectClass the objectClass to use while checking for validity
     * @return true if the entry, it's attributes and their values are consistent
     * with the objectClass
     */
    boolean isValid( EntryAttribute objectClass );


    /**
     * Returns an enumeration containing the zero or more attributes in the
     * collection. The behavior of the enumeration is not specified if the
     * attribute collection is changed.
     *
     * @return an enumeration of all contained attributes
     */
    Iterator<EntryAttribute> iterator();


    /**
     * Add some Attributes to the current Entry.
     *
     * @param attributes The attributes to add
     * @throws LdapException If we can't add any of the attributes
     */
    void add( EntryAttribute... attributes ) throws LdapException;


    /**
     * <p>
     * Add an attribute (represented by its AttributeType and some binary values) into an 
     * entry.
     * </p>
     * <p> 
     * If we already have an attribute with the same values, the duplicated values 
     * are not added (duplicated values are not allowed)
     * </p>
     * <p>
     * If the value cannot be added, or if the AttributeType is null or invalid, 
     * a LdapException is thrown.
     * </p>
     *
     * @param attributeType The attribute Type.
     * @param values The list of binary values to inject. It can be empty.
     * @throws LdapException If the attribute does not exist
     */
    void add( AttributeType attributeType, byte[]... values ) throws LdapException;

    
    /**
     * <p>
     * Add an attribute (represented by its AttributeType and some String values) into an 
     * entry.
     * </p>
     * <p> 
     * If we already have an attribute with the same values, the duplicated values 
     * are not added (duplicated values are not allowed)
     * </p>
     * <p> 
     * If the value cannot be added, or if the AttributeType is null or invalid, 
     * a LdapException is thrown.
     * </p>
     * 
     * @param attributeType The attribute Type
     * @param values The list of binary values to inject. It can be empty
     * @throws LdapException If the attribute does not exist
     */
    void add( AttributeType attributeType, String... values ) throws LdapException;

    
    /**
     * <p>
     * Add an attribute (represented by its AttributeType and some values) into an 
     * entry.
     * </p>
     * <p> 
     * If we already have an attribute with the same values, the duplicated values 
     * are not added (duplicated values are not allowed)
     * </p>
     * <p>
     * If the value cannot be added, or if the AttributeType is null or invalid, 
     * a LdapException is thrown.
     * </p>
     *
     * @param attributeType The attribute Type
     * @param values The list of binary values to inject. It can be empty
     * @throws LdapException If the attribute does not exist
     */
    void add( AttributeType attributeType, Value<?>... values ) throws LdapException;

    
    /**
     * <p>
     * Add an attribute (represented by its AttributeType and some binary values) into an 
     * entry. Set the User Provider ID at the same time
     * </p>
     * <p> 
     * If we already have an attribute with the same values, the duplicated values 
     * are not added (duplicated values are not allowed)
     * </p>
     * <p>
     * If the value cannot be added, or if the AttributeType is null or invalid, 
     * a LdapException is thrown.
     * </p>
     *
     * @param upId The user provided ID for the added AttributeType
     * @param attributeType The attribute Type.
     * @param values The list of binary values to add. It can be empty.
     * @throws LdapException If the attribute does not exist
     */
    void add( String upId, AttributeType attributeType, byte[]... values ) throws LdapException;

    
    /**
     * <p>
     * Add an attribute (represented by its AttributeType and some String values) into an 
     * entry. Set the User Provider ID at the same time
     * </p>
     * <p> 
     * If we already have an attribute with the same values, the duplicated values 
     * are not added (duplicated values are not allowed)
     * </p>
     * <p>
     * If the value cannot be added, or if the AttributeType is null or invalid, 
     * a LdapException is thrown.
     * </p>
     *
     * @param upId The user provided ID for the added AttributeType
     * @param attributeType The attribute Type.
     * @param values The list of binary values to add. It can be empty.
     * @throws LdapException If the attribute does not exist
     */
    void add( String upId, AttributeType attributeType, String... values ) throws LdapException;

    
    /**
     * <p>
     * Add an attribute (represented by its AttributeType and some values) into an 
     * entry. Set the User Provider ID at the same time
     * </p>
     * <p> 
     * If we already have an attribute with the same values, nothing is done 
     * (duplicated values are not allowed)
     * </p>
     * <p>
     * If the value cannot be added, or if the AttributeType is null or invalid, 
     * a LdapException is thrown.
     * </p>
     *
     * @param upId The user provided ID for the added AttributeType
     * @param attributeType The attribute Type.
     * @param values The list of values to add. It can be empty.
     * @throws LdapException If the attribute does not exist
     */
    void add( String upId, AttributeType attributeType, Value<?>... values ) throws LdapException;


    /**
     * Add some String values to the current Entry.
     *
     * @param upId The user provided ID of the attribute we want to add 
     * some values to
     * @param values The list of String values to add
     * @throws LdapException If we can't add any of the values
     */
    void add( String upId, String... values ) throws LdapException;


    /**
     * Add some binary values to the current Entry.
     *
     * @param upId The user provided ID of the attribute we want to add 
     * some values to
     * @param values The list of binary values to add
     * @throws LdapException If we can't add any of the values
     */
    void add( String upId, byte[]... values ) throws LdapException;


    /**
     * Add some Values to the current Entry.
     *
     * @param upId The user provided ID of the attribute we want to add 
     * some values to
     * @param values The list of Values to add
     * @throws LdapException If we can't add any of the values
     */
    void add( String upId, Value<?>... values ) throws LdapException;


    /**
     * <p>
     * Places attributes in the attribute collection. 
     * </p>
     * <p>If there is already an attribute with the same ID as any of the 
     * new attributes, the old ones are removed from the collection and 
     * are returned by this method. If there was no attribute with the 
     * same ID the return value is <code>null</code>.
     *</p>
     *
     * @param attributes the attributes to be put
     * @return the old attributes with the same OID, if exist; otherwise
     *         <code>null</code>
     * @exception LdapException if the operation fails
     */
    List<EntryAttribute> put( EntryAttribute... attributes ) throws LdapException;


    /**
     * <p>
     * Places a new attribute with the supplied AttributeType and binary values 
     * into the attribute collection. 
     * </p>
     * <p>
     * If there is already an attribute with the same AttributeType, the old
     * one is removed from the collection and is returned by this method. 
     * </p>
     * <p>
     * This method provides a mechanism to put an attribute with a
     * <code>null</code> value: the value may be <code>null</code>.
     *
     * @param attributeType the type of the new attribute to be put
     * @param values the binary values of the new attribute to be put
     * @return the old attribute with the same identifier, if exists; otherwise
     * <code>null</code>
     * @throws LdapException if there are failures
     */
    EntryAttribute put( AttributeType attributeType, byte[]... values ) throws LdapException;


    /**
     * <p>
     * Places a new attribute with the supplied AttributeType and String values 
     * into the attribute collection. 
     * </p>
     * <p>
     * If there is already an attribute with the same AttributeType, the old
     * one is removed from the collection and is returned by this method. 
     * </p>
     * <p>
     * This method provides a mechanism to put an attribute with a
     * <code>null</code> value: the value may be <code>null</code>.
     *
     * @param attributeType the type of the new attribute to be put
     * @param values the String values of the new attribute to be put
     * @return the old attribute with the same identifier, if exists; otherwise
     * <code>null</code>
     * @throws LdapException if there are failures
     */
    EntryAttribute put( AttributeType attributeType, String... values ) throws LdapException;


    /**
     * <p>
     * Places a new attribute with the supplied AttributeType and some values 
     * into the attribute collection. 
     * </p>
     * <p>
     * If there is already an attribute with the same AttributeType, the old
     * one is removed from the collection and is returned by this method. 
     * </p>
     * <p>
     * This method provides a mechanism to put an attribute with a
     * <code>null</code> value: the value may be <code>null</code>.
     *
     * @param attributeType the type of the new attribute to be put
     * @param values the values of the new attribute to be put
     * @return the old attribute with the same identifier, if exists; otherwise
     * <code>null</code>
     * @throws LdapException if there are failures
     */
    EntryAttribute put( AttributeType attributeType, Value<?>... values ) throws LdapException;


    /**
     * <p>
     * Places a new attribute with the supplied AttributeType and some binary values 
     * into the attribute collection. 
     * </p>
     * <p>
     * The given User provided ID will be used for this new AttributeEntry.
     * </p>
     * <p>
     * If there is already an attribute with the same AttributeType, the old
     * one is removed from the collection and is returned by this method. 
     * </p>
     * <p>
     * This method provides a mechanism to put an attribute with a
     * <code>null</code> value: the value may be <code>null</code>.
     *
     * @param upId The User Provided ID to be stored into the AttributeEntry
     * @param values the binary values of the new attribute to be put
     * @return the old attribute with the same identifier, if exists; otherwise
     * <code>null</code>
     * @throws LdapException if there are failures.
     */
    EntryAttribute put( String upId, AttributeType attributeType, byte[]... values ) throws LdapException;


    /**
     * <p>
     * Places a new attribute with the supplied AttributeType and some String values 
     * into the attribute collection. 
     * </p>
     * <p>
     * The given User provided ID will be used for this new AttributeEntry.
     * </p>
     * <p>
     * If there is already an attribute with the same AttributeType, the old
     * one is removed from the collection and is returned by this method. 
     * </p>
     * <p>
     * This method provides a mechanism to put an attribute with a
     * <code>null</code> value: the value may be <code>null</code>.
     *
     * @param upId The User Provided ID to be stored into the AttributeEntry
     * @param attributeType the type of the new attribute to be put
     * @param values the String values of the new attribute to be put
     * @return the old attribute with the same identifier, if exists; otherwise
     * <code>null</code>
     * @throws LdapException if there are failures.
     */
    EntryAttribute put( String upId, AttributeType attributeType, String... values ) throws LdapException;


    /**
     * <p>
     * Places a new attribute with the supplied AttributeType and some values 
     * into the attribute collection. 
     * </p>
     * <p>
     * The given User provided ID will be used for this new AttributeEntry.
     * </p>
     * <p>
     * If there is already an attribute with the same AttributeType, the old
     * one is removed from the collection and is returned by this method. 
     * </p>
     * <p>
     * This method provides a mechanism to put an attribute with a
     * <code>null</code> value: the value may be <code>null</code>.
     *
     * @param upId The User Provided ID to be stored into the AttributeEntry
     * @param attributeType the type of the new attribute to be put
     * @param values the values of the new attribute to be put
     * @return the old attribute with the same identifier, if exists; otherwise
     * <code>null</code>
     * @throws LdapException if there are failures.
     */
    EntryAttribute put( String upId, AttributeType attributeType, Value<?>... values ) throws LdapException;


    /**
     * <p>
     * Put an attribute (represented by its ID and some binary values) into an entry. 
     * </p>
     * <p> 
     * If the attribute already exists, the previous attribute will be 
     * replaced and returned.
     * </p>
     *
     * @param upId The attribute ID
     * @param values The list of binary values to put. It can be empty.
     * @return The replaced attribute
     */
    EntryAttribute put( String upId, byte[]... values );


    /**
     * <p>
     * Put an attribute (represented by its ID and some String values) into an entry. 
     * </p>
     * <p> 
     * If the attribute already exists, the previous attribute will be 
     * replaced and returned.
     * </p>
     *
     * @param upId The attribute ID
     * @param values The list of String values to put. It can be empty.
     * @return The replaced attribute
     */
    EntryAttribute put( String upId, String... values );


    /**
     * <p>
     * Put an attribute (represented by its ID and some values) into an entry. 
     * </p>
     * <p> 
     * If the attribute already exists, the previous attribute will be 
     * replaced and returned.
     * </p>
     *
     * @param upId The attribute ID
     * @param values The list of values to put. It can be empty.
     * @return The replaced attribute
     */
    EntryAttribute put( String upId, Value<?>... values );


    /**
     * <p>
     * Removes the specified binary values from an attribute.
     * </p>
     * <p>
     * If at least one value is removed, this method returns <code>true</code>.
     * </p>
     * <p>
     * If there is no more value after having removed the values, the attribute
     * will be removed too.
     * </p>
     * <p>
     * If the attribute does not exist, nothing is done and the method returns 
     * <code>false</code>
     * </p> 
     *
     * @param attributeType The attribute type  
     * @param values the values to be removed
     * @return <code>true</code> if at least a value is removed, <code>false</code>
     * if not all the values have been removed or if the attribute does not exist. 
     */
    boolean remove( AttributeType attributeType, byte[]... values ) throws LdapException;

    
    /**
     * <p>
     * Removes the specified String values from an attribute.
     * </p>
     * <p>
     * If at least one value is removed, this method returns <code>true</code>.
     * </p>
     * <p>
     * If there is no more value after having removed the values, the attribute
     * will be removed too.
     * </p>
     * <p>
     * If the attribute does not exist, nothing is done and the method returns 
     * <code>false</code>
     * </p> 
     *
     * @param attributeType The attribute type  
     * @param values the values to be removed
     * @return <code>true</code> if at least a value is removed, <code>false</code>
     * if not all the values have been removed or if the attribute does not exist. 
     */
    boolean remove( AttributeType attributeType, String... values ) throws LdapException;

    
    /**
     * <p>
     * Removes the specified values from an attribute.
     * </p>
     * <p>
     * If at least one value is removed, this method returns <code>true</code>.
     * </p>
     * <p>
     * If there is no more value after having removed the values, the attribute
     * will be removed too.
     * </p>
     * <p>
     * If the attribute does not exist, nothing is done and the method returns 
     * <code>false</code>
     * </p> 
     *
     * @param attributeType The attribute type  
     * @param values the values to be removed
     * @return <code>true</code> if at least a value is removed, <code>false</code>
     * if not all the values have been removed or if the attribute does not exist. 
     */
    boolean remove( AttributeType attributeType, Value<?>... values ) throws LdapException;

    
    /**
     * Removes the specified attributes. The removed attributes are
     * returned by this method. If there were no attribute the return value
     * is <code>null</code>.
     *
     * @param attributes the attributes to be removed
     * @return the removed attribute, if exists; otherwise <code>null</code>
     */
    List<EntryAttribute> remove( EntryAttribute... attributes ) throws LdapException;
    

    /**
     * <p>
     * Removes the attribute with the specified AttributeTypes. 
     * </p>
     * <p>
     * The removed attribute are returned by this method. 
     * </p>
     * <p>
     * If there is no attribute with the specified AttributeTypes,
     * the return value is <code>null</code>.
     * </p>
     *
     * @param attributes the AttributeTypes to be removed
     * @return the removed attributes, if any, as a list; otherwise <code>null</code>
     */
    List<EntryAttribute> removeAttributes( AttributeType... attributes );


    /**
     * <p>
     * Removes the specified binary values from an attribute.
     * </p>
     * <p>
     * If at least one value is removed, this method returns <code>true</code>.
     * </p>
     * <p>
     * If there is no more value after having removed the values, the attribute
     * will be removed too.
     * </p>
     * <p>
     * If the attribute does not exist, nothing is done and the method returns 
     * <code>false</code>
     * </p> 
     *
     * @param upId The attribute ID  
     * @param values the attributes to be removed
     * @return <code>true</code> if at least a value is removed, <code>false</code>
     * if not all the values have been removed or if the attribute does not exist. 
     */
    boolean remove( String upId, byte[]... values ) throws LdapException;


    /**
     * <p>
     * Removes the specified String values from an attribute.
     * </p>
     * <p>
     * If at least one value is removed, this method returns <code>true</code>.
     * </p>
     * <p>
     * If there is no more value after havong removed the values, the attribute
     * will be removed too.
     * </p>
     * <p>
     * If the attribute does not exist, nothing is done and the method returns 
     * <code>false</code>
     * </p> 
     *
     * @param upId The attribute ID  
     * @param values the attributes to be removed
     * @return <code>true</code> if at least a value is removed, <code>false</code>
     * if no values have been removed or if the attribute does not exist. 
     */
    boolean remove( String upId, String... values ) throws LdapException;


    /**
     * <p>
     * Removes the specified values from an attribute.
     * </p>
     * <p>
     * If at least one value is removed, this method returns <code>true</code>.
     * </p>
     * <p>
     * If there is no more value after having removed the values, the attribute
     * will be removed too.
     * </p>
     * <p>
     * If the attribute does not exist, nothing is done and the method returns 
     * <code>false</code>
     * </p> 
     *
     * @param upId The attribute ID  
     * @param values the attributes to be removed
     * @return <code>true</code> if at least a value is removed, <code>false</code>
     * if not all the values have been removed or if the attribute does not exist. 
     */
    boolean remove( String upId, Value<?>... values ) throws LdapException;


    /**
      * <p>
      * Removes the attribute with the specified alias. 
      * </p>
      * <p>
      * The removed attribute are returned by this method. 
      * </p>
      * <p>
      * If there is no attribute with the specified alias,
      * the return value is <code>null</code>.
      * </p>
      *
      * @param attributes an aliased name of the attribute to be removed
      * @return the removed attributes, if any, as a list; otherwise <code>null</code>
      */
    List<EntryAttribute> removeAttributes( String... attributes );


    // -----------------------------------------------------------------------
    // Container (contains/get/put/remove) Methods
    // -----------------------------------------------------------------------
    /**
     * Checks that the current Entry contain an AttributeType given its ID
     * 
     * @param upId the AttributeType ID
     * @throws LdapException If the attribute does not exists
     */
    public boolean contains( String upId ) throws LdapException;
    
    
    /**
     * Checks if an entry contains an attribute with some given binary values.
     *
     * @param attributeType The Attribute we are looking for.
     * @param values The searched values.
     * @return <code>true</code> if all the values are found within the attribute,
     * <code>false</code> otherwise, or if the attributes does not exist.
     * @throws LdapException If the attribute does not exists
     */
    boolean contains( AttributeType attributeType, byte[]... values );


    /**
     * Checks if an entry contains an attribute with some given String values.
     *
     * @param attributeType The Attribute we are looking for.
     * @param values The searched values.
     * @return <code>true</code> if all the values are found within the attribute,
     * <code>false</code> otherwise, or if the attributes does not exist.
     * @throws LdapException If the attribute does not exists
     */
    boolean contains( AttributeType attributeType, String... values );


    /**
     * Checks if an entry contains an attribute with some given binary values.
     *
     * @param attributeType The Attribute we are looking for.
     * @param values The searched values.
     * @return <code>true</code> if all the values are found within the attribute,
     * <code>false</code> otherwise, or if the attributes does not exist.
     * @throws LdapException If the attribute does not exists
     */
    boolean contains( AttributeType attributeType, Value<?>... values );


    /**
     * Checks if an entry contains a specific AttributeType.
     *
     * @param attributeType The AttributeType to look for.
     * @return <code>true</code> if the attribute is found within the entry.
     */
    boolean containsAttribute( AttributeType attributeType );

    
    /**
     * <p>
     * Checks if an entry contains a list of attributes.
     * </p>
     * <p>
     * If the list is null or empty, this method will return <code>true</code>
     * if the entry has no attribute, <code>false</code> otherwise.
     * </p>
     *
     * @param attributes The Attributes to look for
     * @return <code>true</code> if all the attributes are found within 
     * the entry, <code>false</code> if at least one of them is not present.
     * @throws LdapException If the attribute does not exist
     */
    boolean contains( EntryAttribute... attributes ) throws LdapException;


    /**
     * Checks if an entry contains an attribute with some binary values.
     *
     * @param upId The Attribute we are looking for.
     * @param values The searched values.
     * @return <code>true</code> if all the values are found within the attribute,
     * false if at least one value is not present or if the ID is not valid. 
     */
    boolean contains( String upId, byte[]... values );


    /**
     * Checks if an entry contains an attribute with some String values.
     *
     * @param upId The Attribute we are looking for.
     * @param values The searched values.
     * @return <code>true</code> if all the values are found within the attribute,
     * false if at least one value is not present or if the ID is not valid. 
     */
    boolean contains( String upId, String... values );


    /**
     * Checks if an entry contains an attribute with some values.
     *
     * @param upId The Attribute we are looking for.
     * @param values The searched values.
     * @return <code>true</code> if all the values are found within the attribute,
     * false if at least one value is not present or if the ID is not valid. 
     */
    boolean contains( String upId, Value<?>... values );


    /**
     * Checks if an entry contains some specific attributes.
     *
     * @param attributes The Attributes to look for.
     * @return <code>true</code> if the attributes are all found within the entry.
     */
    boolean containsAttribute( String... attributes );

    
    /**
     * Returns the number of attributes.
     *
     * @return the number of attributes
     */
    int size();
}
