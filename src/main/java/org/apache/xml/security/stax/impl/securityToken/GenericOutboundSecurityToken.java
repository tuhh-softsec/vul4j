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

import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Element;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.securityToken.OutboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants.KeyIdentifier;

/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class GenericOutboundSecurityToken extends AbstractSecurityToken implements OutboundSecurityToken {

    private SecurityTokenConstants.TokenType tokenType;
    private Object processor;
    private final List<OutboundSecurityToken> wrappedTokens = new ArrayList<OutboundSecurityToken>();
    private OutboundSecurityToken keyWrappingToken;
    private Element customTokenReference;

    public GenericOutboundSecurityToken(String id, SecurityTokenConstants.TokenType tokenType, Key key, X509Certificate[] x509Certificates) {
        this(id, tokenType, key);
        setX509Certificates(x509Certificates);
    }

    public GenericOutboundSecurityToken(String id, SecurityTokenConstants.TokenType tokenType, Key key) {
        this(id, tokenType);
        setSecretKey("", key);
        if (key instanceof PublicKey) {
            setPublicKey((PublicKey)key);
        }
    }

    public GenericOutboundSecurityToken(String id, SecurityTokenConstants.TokenType tokenType) {
        super(id);
        this.tokenType = tokenType;
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

    public void setTokenType(SecurityTokenConstants.TokenType tokenType) {
        this.tokenType = tokenType;
    }
    
    @Override
    public SecurityTokenConstants.TokenType getTokenType() {
        return tokenType;
    }

    @Override
    public KeyIdentifier getKeyIdentifier() {
        // TODO Auto-generated method stub
        return null;
    }

    public Element getCustomTokenReference() {
        return customTokenReference;
    }

    public void setCustomTokenReference(Element customTokenReference) {
        this.customTokenReference = customTokenReference;
    }

}
