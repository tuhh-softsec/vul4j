/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.xml.security.stax.securityToken;

import org.apache.xml.security.exceptions.XMLSecurityException;

/**
 * A SecurityTokenProvider is a object which provides a Token for cryptographic operations
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public interface SecurityTokenProvider<T> {

    /**
     * Returns the represented SecurityToken of this object
     *
     * @return The SecurityToken
     * @throws XMLSecurityException if the token couldn't be loaded
     */
    T getSecurityToken() throws XMLSecurityException;

    String getId();
}
