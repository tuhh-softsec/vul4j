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

import org.apache.xml.security.stax.securityToken.SecurityTokenProvider;
import org.apache.xml.security.stax.securityToken.OutboundSecurityToken;

import java.util.List;

/**
 * The document security context
 *
 * @author $Author: giger $
 * @version $Revision: 1416649 $ $Date: 2012-12-03 21:04:06 +0100 (Mon, 03 Dec 2012) $
 */
public interface OutboundSecurityContext extends SecurityContext {

    /**
     * Register a new SecurityTokenProvider.
     *
     * @param id                    A unique id
     * @param securityTokenProvider The actual SecurityTokenProvider to register.
     */
    void registerSecurityTokenProvider(String id, SecurityTokenProvider<OutboundSecurityToken> securityTokenProvider);

    /**
     * Returns a registered SecurityTokenProvider with the given id or null if not found
     *
     * @param id The SecurityTokenProvider's id
     * @return The SecurityTokenProvider
     */
    SecurityTokenProvider<OutboundSecurityToken> getSecurityTokenProvider(String id);

    /**
     * Returns all currently registered SecurityTokenProvider's
     * @return All registered SecurityTokenProvider's
     */
    List<SecurityTokenProvider<OutboundSecurityToken>> getRegisteredSecurityTokenProviders();

}
