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
package org.apache.directory.shared.ldap.model.message;

import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.name.Rdn;


/**
 * Modify Dn request protocol message used to rename or move an existing entry
 * in the directory. Here's what <a
 * href="http://www.faqs.org/rfcs/rfc2251.html">RFC 2251</a> has to say about
 * it:
 * 
 * <pre>
 *  4.9. Modify Dn Operation
 * 
 *   The Modify Dn Operation allows a client to change the leftmost (least
 *   significant) component of the name of an entry in the directory, or
 *   to move a subtree of entries to a new location in the directory.  The
 *   Modify Dn Request is defined as follows:
 * 
 *        ModifyDNRequest ::= [APPLICATION 12] SEQUENCE {
 *                entry           LDAPDN,
 *                newrdn          RelativeLDAPDN,
 *                deleteoldrdn    BOOLEAN,
 *                newSuperior     [0] LDAPDN OPTIONAL }
 * 
 *   Parameters of the Modify Dn Request are:
 * 
 *   - entry: the Distinguished Name of the entry to be changed.  This
 *     entry may or may not have subordinate entries.
 * 
 *   - newrdn: the Rdn that will form the leftmost component of the new
 *     name of the entry.
 * 
 *   - deleteoldrdn: a boolean parameter that controls whether the old Rdn
 *     attribute values are to be retained as attributes of the entry, or
 *     deleted from the entry.
 * 
 *   - newSuperior: if present, this is the Distinguished Name of the entry
 *     which becomes the immediate superior of the existing entry.
 * </pre>
 * 
 * Note that this operation can move an entry and change its Rdn at the same
 * time in fact it might have no choice to comply with name forms.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface ModifyDnRequest extends SingleReplyRequest, AbandonableRequest
{
    /** Modify Dn request message type enumeration value */
    MessageTypeEnum TYPE = MessageTypeEnum.MODIFYDN_REQUEST;

    /** Modify Dn response message type enumeration value */
    MessageTypeEnum RESP_TYPE = ModifyDnResponse.TYPE;


    /**
     * Gets the entry's distinguished name representing the <b>entry</b> PDU
     * field.
     * 
     * @return the distinguished name of the entry.
     */
    Dn getName();


    /**
     * Sets the entry's distinguished name representing the <b>entry</b> PDU
     * field.
     * 
     * @param name
     *            the distinguished name of the entry.
     */
    void setName( Dn name );


    /**
     * Gets the new relative distinguished name for the entry which represents
     * the PDU's <b>newrdn</b> field.
     * 
     * @return the relative dn with one component
     */
    Rdn getNewRdn();


    /**
     * Sets the new relative distinguished name for the entry which represents
     * the PDU's <b>newrdn</b> field.
     * 
     * @param newRdn
     *            the relative dn with one component
     */
    void setNewRdn( Rdn newRdn );


    /**
     * Gets the flag which determines if the old Rdn attribute is to be removed
     * from the entry when the new Rdn is used in its stead. This property
     * corresponds to the <b>deleteoldrdn</b>.
     * 
     * @return true if the old rdn is to be deleted, false if it is not
     */
    boolean getDeleteOldRdn();


    /**
     * Sets the flag which determines if the old Rdn attribute is to be removed
     * from the entry when the new Rdn is used in its stead. This property
     * corresponds to the <b>deleteoldrdn</b>.
     * 
     * @param deleteOldRdn
     *            true if the old rdn is to be deleted, false if it is not
     */
    void setDeleteOldRdn( boolean deleteOldRdn );


    /**
     * Gets the optional distinguished name of the new superior entry where the
     * candidate entry is to be moved. This property corresponds to the PDU's
     * <b>newSuperior</b> field. May be null representing a simple Rdn change
     * rather than a move operation.
     * 
     * @return the dn of the superior entry the candidate entry is moved under.
     */
    Dn getNewSuperior();


    /**
     * Sets the optional distinguished name of the new superior entry where the
     * candidate entry is to be moved. This property corresponds to the PDU's
     * <b>newSuperior</b> field. May be null representing a simple Rdn change
     * rather than a move operation. Setting this property to a non-null value
     * toggles the move flag obtained via the <code>isMove</code> method.
     * 
     * @param newSuperior
     *            the dn of the superior entry the candidate entry for Dn
     *            modification is moved under.
     */
    void setNewSuperior( Dn newSuperior );


    /**
     * Gets whether or not this request is a Dn change resulting in a move
     * operation. Setting the newSuperior property to a non-null name, toggles
     * this flag.
     * 
     * @return true if the newSuperior property is <b>NOT</b> null, false
     *         otherwise.
     */
    boolean isMove();
}
