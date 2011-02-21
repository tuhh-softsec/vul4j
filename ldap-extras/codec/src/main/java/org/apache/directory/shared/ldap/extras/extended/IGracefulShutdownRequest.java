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
package org.apache.directory.shared.ldap.extras.extended;


import org.apache.directory.shared.ldap.model.message.ExtendedRequest;


/**
 * An extended operation requesting the server to shutdown it's LDAP service
 * port while allowing established clients to complete or abandon operations
 * already in progress. More information about this extended request is
 * available here: <a href="http://docs.safehaus.org:8080/x/GR">LDAP Extensions
 * for Graceful Shutdown</a>.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface IGracefulShutdownRequest extends ExtendedRequest<IGracefulShutdownResponse>
{

    /** The OID for the graceful shutdown extended operation request. */
    String EXTENSION_OID = "1.3.6.1.4.1.18060.0.1.3";
    
    /** Undetermined value used for offline time */
    int UNDETERMINED = 0;
    
    /** The shutdown is immediate */
    int NOW = 0;


    /**
     * Gets the delay before disconnection, in seconds.
     *
     * @return the delay before disconnection
     */
    int getDelay();


    /**
     * Sets the delay befor disconnection, in seconds.
     *
     * @param delay the new delay before disconnection
     */
    void setDelay( int delay );


    /**
     * Gets the offline time after disconnection, in minutes.
     *
     * @return the offline time after disconnection
     */
    int getTimeOffline();


    /**
     * Sets the time offline after disconnection, in minutes.
     *
     * @param timeOffline the new time offline after disconnection
     */
    void setTimeOffline( int timeOffline );

}