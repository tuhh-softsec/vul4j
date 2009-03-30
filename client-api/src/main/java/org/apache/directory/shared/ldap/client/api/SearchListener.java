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
package org.apache.directory.shared.ldap.client.api;

import org.apache.directory.shared.ldap.entry.Entry;

/**
 * A listener used for asynchronous search handling. When wanting to handle
 * searches as a non-blocking operation, simply associate a SearchListener
 * to the search operation : for each entry found, the listener will be
 * called back, and so will it when the search will be done.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface SearchListener
{
    /**
     * A callback method for each entry returned by a search operation.
     *
     * @param entry The found entry
     */
    void resultReturned( Entry entry );
    
    /**
     * A callback method called when the search is done.
     */
    void searchDone();
}
