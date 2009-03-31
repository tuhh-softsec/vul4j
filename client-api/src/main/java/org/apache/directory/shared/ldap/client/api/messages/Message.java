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
package org.apache.directory.shared.ldap.client.api.messages;


import java.util.Map;

import javax.naming.ldap.Control;

import org.apache.directory.shared.ldap.message.MessageException;


/**
 * Root interface for all LDAP message type interfaces.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface Message
{
    /**
     * Get the client message timeout. When the timeout is reached, the 
     * request is canceled. 
     *
     * @return The timeout
     */
    long getTimeout();
    
    
    /**
     * Set a request client timeout. When this timeout is reached, the request 
     * will be canceled. If <= 0, then we wait for the response forever.  
     *
     * @param timeout The new timeout, expressed in milliseconds
     */
    void setTimeout( long timeout );
    
    
    /**
     * Gets the controls associated with this message mapped by OID.
     * 
     * @return Map of OID strings to Control object instances.
     * @see MutableControl
     */
    Map<String, Control> getControls();

    
    /**
     * Checks whether or not this message has the specified control.
     *
     * @param oid the OID of the control
     * @return true if this message has the control, false if it does not
     */
    boolean hasControl( String oid );
    

    /**
     * Adds a control to this Message.
     * 
     * @param control
     *            the control to add.
     * @throws MessageException
     *             if controls cannot be added to this Message or the control is
     *             not known etc.
     */
    void add( Control control ) throws MessageException;


    /**
     * Adds an array of controls to this Message.
     * 
     * @param controls the controls to add.
     * @throws MessageException if controls cannot be added to this Message or they are not known etc.
     */
    void addAll( Control[] controls ) throws MessageException;


    /**
     * Deletes a control removing it from this Message.
     * 
     * @param control the control to remove.
     * @throws MessageException
     *             if controls cannot be added to this Message or the control is
     *             not known etc.
     */
    void remove( Control control ) throws MessageException;
}
