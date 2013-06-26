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

import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public interface OutboundSecurityToken {

    /**
     * Returns the token id aka wsu:Id
     *
     * @return The id
     */
    String getId();

    /**
     * Returns the responsible processor for this token
     *
     * @return
     */
    Object getProcessor();

    /**
     * Returns the secret key
     *
     * @return The key
     * @throws XMLSecurityException if the key can't be loaded
     */
    Key getSecretKey(String algorithmURI) throws XMLSecurityException;

    /**
     * Returns the public key if one exists and already initialized, null otherwise
     * @return
     * @throws org.apache.xml.security.exceptions.XMLSecurityException
     */
    PublicKey getPublicKey() throws XMLSecurityException;

    /**
     * Returns the certificate chain if one exists for this token type
     *
     * @return The certificate chain
     * @throws org.apache.xml.security.exceptions.XMLSecurityException if the certificates can't be retrieved
     */
    X509Certificate[] getX509Certificates() throws XMLSecurityException;

    /**
     * Returns the key wrapping token
     *
     * @return The wrapping SecurityToken
     */
    OutboundSecurityToken getKeyWrappingToken() throws XMLSecurityException;

    List<OutboundSecurityToken> getWrappedTokens() throws XMLSecurityException;

    void addWrappedToken(OutboundSecurityToken securityToken);

    SecurityTokenConstants.TokenType getTokenType();
    
    /**
     * Returns a SHA-1 Identifier that refers to this token
     * @return a SHA-1 Identifier that refers to this token
     */
    String getSha1Identifier();
}
