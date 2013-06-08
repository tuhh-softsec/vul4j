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
package org.apache.xml.security.stax.impl.securityToken;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;

/**
 * @author $Author: coheigea $
 * @version $Revision: 1354898 $ $Date: 2012-06-28 11:19:02 +0100 (Thu, 28 Jun 2012) $
 */
public class X509SecurityToken extends AbstractInboundSecurityToken {

    private final SecurityTokenConstants.TokenType tokenType;

    protected X509SecurityToken(
            SecurityTokenConstants.TokenType tokenType, InboundSecurityContext inboundSecurityContext,
            String id, SecurityTokenConstants.KeyIdentifier keyIdentifier, boolean includedInMessage) {
        super(inboundSecurityContext, id, keyIdentifier, includedInMessage);
        this.tokenType = tokenType;
    }

    @Override
    public boolean isAsymmetric() throws XMLSecurityException {
        return true;
    }

    @Override
    public SecurityTokenConstants.TokenType getTokenType() {
        return tokenType;
    }
}
