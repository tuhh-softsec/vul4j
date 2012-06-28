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
package org.apache.xml.security.stax.ext;

import java.util.List;
import java.util.Map;

/**
 * The document security context
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public interface SecurityContext {

    <T> void put(String key, T value);

    <T> T get(String key);

    <T> T remove(String key);

    <T extends List> void putList(Object key, T value);

    <T> void putAsList(Object key, T value);

    <T> List<T> getAsList(Object key);

    public <T, U> void putAsMap(Object key, T mapKey, U mapValue);

    <T, U> Map<T, U> getAsMap(Object key);

    /**
     * Register a new SecurityTokenProvider.
     *
     * @param id                    A unique id
     * @param securityTokenProvider The actual SecurityTokenProvider to register.
     */
    void registerSecurityTokenProvider(String id, SecurityTokenProvider securityTokenProvider);

    /**
     * Returns a registered SecurityTokenProvider with the given id or null if not found
     *
     * @param id The SecurityTokenProvider's id
     * @return The SecurityTokenProvider
     */
    SecurityTokenProvider getSecurityTokenProvider(String id);
}
