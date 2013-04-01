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
package org.apache.xml.security.stax.impl;

import org.apache.xml.security.stax.ext.OutboundSecurityContext;
import org.apache.xml.security.stax.securityToken.SecurityTokenProvider;
import org.apache.xml.security.stax.securityToken.OutboundSecurityToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Concrete security context implementation
 *
 * @author $Author: giger $
 * @version $Revision: 1416649 $ $Date: 2012-12-03 21:04:06 +0100 (Mon, 03 Dec 2012) $
 */
public class OutboundSecurityContextImpl extends AbstractSecurityContextImpl implements OutboundSecurityContext {

    private final Map<String, SecurityTokenProvider<OutboundSecurityToken>> securityTokenProviders =
            new HashMap<String, SecurityTokenProvider<OutboundSecurityToken>>();

    @Override
    public void registerSecurityTokenProvider(String id, SecurityTokenProvider<OutboundSecurityToken> securityTokenProvider) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        securityTokenProviders.put(id, securityTokenProvider);
    }

    @Override
    public SecurityTokenProvider<OutboundSecurityToken> getSecurityTokenProvider(String id) {
        return securityTokenProviders.get(id);
    }

    @Override
    public List<SecurityTokenProvider<OutboundSecurityToken>> getRegisteredSecurityTokenProviders() {
        return new ArrayList<SecurityTokenProvider<OutboundSecurityToken>>(securityTokenProviders.values());
    }
}
