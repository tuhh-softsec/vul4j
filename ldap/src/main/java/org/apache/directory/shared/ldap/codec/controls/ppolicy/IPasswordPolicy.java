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
package org.apache.directory.shared.ldap.codec.controls.ppolicy;


import org.apache.directory.shared.ldap.model.message.Control;


/**
 * The password policy {@link Control} interface.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface IPasswordPolicy extends Control
{
    /** the password policy request control */
    public static final String OID = "1.3.6.1.4.1.42.2.27.8.5.1";


    /**
     * Checks whether this Control is the password policy request or the response
     * by carrying with it an IPasswordPolicyResponse object. If it is a request, 
     * then no response component will be associated with the control: getResponse()
     * will return null.
     *
     * @return true if this Control carries a response, false if it is a request
     */
    boolean hasResponse();
    
    
    /**
     * Sets the response. If null hasResponse() will return null and this will be
     * handled as a password policy request control rather than a response control.
     *
     * @param response a valid response object, or null to make this a request
     */
    void setResponse( IPasswordPolicyResponse response );
    
    
    /**
     * If true sets the response to a default newly initialized response object. 
     * If this was previously a request, it automatically becomes a response. If it 
     * was not a request with an already existing response object then that response
     * is replace with a new one and the old is returned. If false then any response
     * object set will be cleared to null. 
     * 
     * Effectively this will cause hasResponse() to return whatever you plug into it.
     *
     * @param hasResponse true to create default response, false to clear it
     * @return the old response object, if one did not exist null is returned
     */
    IPasswordPolicyResponse setResponse( boolean hasResponse );
    
    
    /**
     * Get's the response component of this control if this control carries a 
     * response. If {@link #hasResponse()} returns true, this will return a non-null
     * policy response object. 
     *
     * @return a non-null policy response or null, if {@link #hasResponse()} 
     * returns false
     */
    IPasswordPolicyResponse getResponse();
}
