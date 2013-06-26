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
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

import javax.xml.namespace.QName;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

/**
 * External view to a SecurityToken
 */
public interface SecurityToken {
    /**
     * Returns the token id aka wsu:Id
     *
     * @return The id
     */
    String getId();

    /**
     * Returns the token type
     *
     * @return true if asymmetric token, false if symmetric token
     */
    boolean isAsymmetric() throws XMLSecurityException;

    /**
     * Returns the secret key's if already initialized, null otherwise
     * @return Algorithm-URI key map
     * @throws XMLSecurityException
     */
    Map<String, Key> getSecretKey() throws XMLSecurityException;

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
    SecurityToken getKeyWrappingToken() throws XMLSecurityException;

    List<? extends SecurityToken> getWrappedTokens() throws XMLSecurityException;

    /**
     * Returns the KeyIdentifier
     *
     * @return the KeyIdentifier
     */
    SecurityTokenConstants.KeyIdentifier getKeyIdentifier();

    SecurityTokenConstants.TokenType getTokenType();

    List<SecurityTokenConstants.TokenUsage> getTokenUsages();

    /**
     * Returns the absolute path to the XMLElement
     *
     * @return A list containing full qualified element names
     */
    List<QName> getElementPath();

    /**
     * Returns the first XMLEvent for this token
     *
     * @return
     */
    XMLSecEvent getXMLSecEvent();

    /**
     * Returns if the token is included in the message or not
     * @return true if the token is included false otherwise
     */
    boolean isIncludedInMessage();
    
    /**
     * Returns a SHA-1 Identifier that refers to this token
     * @return a SHA-1 Identifier that refers to this token
     */
    String getSha1Identifier();
}
