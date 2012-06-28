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

import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

import javax.xml.namespace.QName;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * This class represents the different token types which can occur in WS-Security
 * <p/>
 * Sometimes it isn't known (@see EncryptedKeyInputProcessor) which kind of Token(Asymmetric, Symmetric)
 * we have at creation time. So we use a generic interface for both types.
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public interface SecurityToken {

    public enum TokenUsage {
        Signature,
        MainSignature,
        Encryption,
        MainEncryption,
        SupportingTokens,
        SignedSupportingTokens,
        EndorsingSupportingTokens,
        SignedEndorsingSupportingTokens,
        SignedEncryptedSupportingTokens,
        EncryptedSupportingTokens,
        EndorsingEncryptedSupportingTokens,
        SignedEndorsingEncryptedSupportingTokens,
    }

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
     * Returns the token type
     *
     * @return true if asymmetric token, false if symmetric token
     */
    boolean isAsymmetric();

    /**
     * Returns the secret key
     *
     * @param algorithmURI for the requested key
     * @param keyUsage
     * @return The requested key for the specified algorithmURI, or null if no matching key is found
     * @throws XMLSecurityException if the key can't be loaded
     */
    Key getSecretKey(String algorithmURI, XMLSecurityConstants.KeyUsage keyUsage) throws XMLSecurityException;

    /**
     * Returns the public key if one exist for this token type
     *
     * @param algorithmURI
     * @param keyUsage
     * @return The Public-Key for asymmetric algorithms
     * @throws XMLSecurityException if the key can't be loaded
     */
    PublicKey getPublicKey(String algorithmURI, XMLSecurityConstants.KeyUsage keyUsage) throws XMLSecurityException;

    /**
     * Returns the certificate chain if one exists for this token type
     *
     * @return The certificate chain
     * @throws XMLSecurityException if the certificates can't be retrieved
     */
    X509Certificate[] getX509Certificates() throws XMLSecurityException;

    /**
     * Verifies the key if applicable
     *
     * @throws XMLSecurityException if the key couldn't be verified or the key isn't valid
     */
    void verify() throws XMLSecurityException;

    /**
     * Returns the key wrapping token
     *
     * @return The wrapping SecurityToken
     */
    SecurityToken getKeyWrappingToken() throws XMLSecurityException;

    List<SecurityToken> getWrappedTokens() throws XMLSecurityException;

    void addWrappedToken(SecurityToken securityToken);

    /**
     * Returns the KeyIdentifierType
     *
     * @return the KeyIdentifierType
     */
    XMLSecurityConstants.TokenType getTokenType();

    void addTokenUsage(TokenUsage tokenUsage) throws XMLSecurityException;

    List<TokenUsage> getTokenUsages();
}
