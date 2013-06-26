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
import org.apache.xml.security.stax.securityToken.OutboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;

import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class GenericOutboundSecurityToken implements OutboundSecurityToken {

    private String id;
    private SecurityTokenConstants.TokenType tokenType;
    private Object processor;
    private final List<OutboundSecurityToken> wrappedTokens = new ArrayList<OutboundSecurityToken>();
    private OutboundSecurityToken keyWrappingToken;
    private final Map<String, Key> keyTable = new Hashtable<String, Key>();
    private PublicKey publicKey;
    private X509Certificate[] x509Certificates;
    private String sha1Identifier;

    public GenericOutboundSecurityToken(String id, SecurityTokenConstants.TokenType tokenType, Key key, X509Certificate[] x509Certificates) {
        this(id, tokenType, key);
        this.x509Certificates = x509Certificates;
    }

    public GenericOutboundSecurityToken(String id, SecurityTokenConstants.TokenType tokenType, Key key) {
        this(id, tokenType);
        setSecretKey("", key);
    }

    public GenericOutboundSecurityToken(String id, SecurityTokenConstants.TokenType tokenType) {
        this.id = id;
        this.tokenType = tokenType;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Object getProcessor() {
        return processor;
    }

    public void setProcessor(Object processor) {
        this.processor = processor;
    }

    @Override
    public Key getSecretKey(String algorithmURI) throws XMLSecurityException {
        if (algorithmURI == null) {
            return null;
        }
        Key key = keyTable.get(algorithmURI);
        //workaround for user set keys which aren't declared in the xml
        if (key == null) {
            key = keyTable.get("");
        }
        return key;
    }

    public void setSecretKey(String algorithmURI, Key key) {
        if (algorithmURI == null) {
            throw new IllegalArgumentException("algorithmURI must not be null");
        }
        if (key != null) {
            this.keyTable.put(algorithmURI, key);
        }
    }

    @Override
    public PublicKey getPublicKey() throws XMLSecurityException {
        if (this.publicKey != null) {
            return this.publicKey;
        }
        if (this.x509Certificates != null && this.x509Certificates.length > 0) {
            return this.publicKey = this.x509Certificates[0].getPublicKey();
        }
        return null;
    }

    @Override
    public X509Certificate[] getX509Certificates() throws XMLSecurityException {
        return this.x509Certificates;
    }

    @Override
    public OutboundSecurityToken getKeyWrappingToken() throws XMLSecurityException {
        return keyWrappingToken;
    }

    public void setKeyWrappingToken(OutboundSecurityToken keyWrappingToken) {
        this.keyWrappingToken = keyWrappingToken;
    }

    @Override
    public List<OutboundSecurityToken> getWrappedTokens() throws XMLSecurityException {
        return Collections.unmodifiableList(wrappedTokens);
    }

    @Override
    public void addWrappedToken(OutboundSecurityToken securityToken) {
        wrappedTokens.add(securityToken);
    }

    @Override
    public SecurityTokenConstants.TokenType getTokenType() {
        return tokenType;
    }
    
    public String getSha1Identifier() {
        return sha1Identifier;
    }

    public void setSha1Identifier(String sha1Identifier) {
        this.sha1Identifier = sha1Identifier;
    }
}
