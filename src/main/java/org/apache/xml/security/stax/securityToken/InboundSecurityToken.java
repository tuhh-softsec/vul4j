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

import java.security.Key;
import java.security.PublicKey;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

/**
 * This class represents the different token types which can occur in WS-Security
 * <p/>
 * Sometimes it isn't known (@see EncryptedKeyInputProcessor) which kind of Token(Asymmetric, Symmetric)
 * we have at creation time. So we use a generic interface for both types.
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public interface InboundSecurityToken extends SecurityToken {

    /**
     * Returns the secret key
     *
     * @param algorithmURI for the requested key
     * @param algorithmUsage
     * @return The requested key for the specified algorithmURI, or null if no matching key is found
     * @throws XMLSecurityException if the key can't be loaded
     */
    Key getSecretKey(String algorithmURI, XMLSecurityConstants.AlgorithmUsage algorithmUsage, String correlationID) throws XMLSecurityException;

    /**
     * Returns the public key if one exist for this token type
     *
     * @param algorithmURI
     * @param algorithmUsage
     * @return The Public-Key for asymmetric algorithms
     * @throws XMLSecurityException if the key can't be loaded
     */
    PublicKey getPublicKey(String algorithmURI, XMLSecurityConstants.AlgorithmUsage algorithmUsage, String correlationID) throws XMLSecurityException;

    void addWrappedToken(InboundSecurityToken securityToken);
    
    /**
     * Verifies the key if applicable
     *
     * @throws XMLSecurityException if the key couldn't be verified or the key isn't valid
     */
    void verify() throws XMLSecurityException;
    
    /**
     * Returns the absolute path to the XMLElement
     *
     * @return A list containing full qualified element names
     */
    List<QName> getElementPath();

    /**
     * Returns the first XMLEvent for this token
     *
     * @return the first XMLEvent for this token
     */
    XMLSecEvent getXMLSecEvent();

    /**
     * Returns if the token is included in the message or not
     * @return true if the token is included false otherwise
     */
    boolean isIncludedInMessage();

}
