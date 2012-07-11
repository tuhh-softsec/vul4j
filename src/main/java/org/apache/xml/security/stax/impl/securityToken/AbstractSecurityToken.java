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

import org.apache.xml.security.stax.ext.SecurityContext;
import org.apache.xml.security.stax.ext.SecurityToken;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.securityEvent.AlgorithmSuiteSecurityEvent;

import javax.crypto.SecretKey;
import javax.security.auth.callback.CallbackHandler;
import javax.xml.namespace.QName;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAKey;
import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author $Author: coheigea $
 * @version $Revision: 1359731 $ $Date: 2012-07-10 16:39:40 +0100 (Tue, 10 Jul 2012) $
 */
public abstract class AbstractSecurityToken implements SecurityToken {

    //todo Probably we should introduce a dynamic proxy
    //for this class which then could test for invocation count and could also be
    //used for SecurityEvents and such.
    //prevent recursive key references:
    private int invocationCount = 0;

    private SecurityContext securityContext;
    private CallbackHandler callbackHandler;
    private final String id;
    private Object processor;
    private List<QName> elementPath;
    private XMLSecEvent xmlSecEvent;
    private XMLSecurityConstants.KeyIdentifierType keyIdentifierType;
    private final List<SecurityToken> wrappedTokens = new ArrayList<SecurityToken>();
    private final List<TokenUsage> tokenUsages = new ArrayList<TokenUsage>();

    public AbstractSecurityToken(String id) {
        this.id = id;
    }

    public AbstractSecurityToken(SecurityContext securityContext, CallbackHandler callbackHandler,
                                 String id, XMLSecurityConstants.KeyIdentifierType keyIdentifierType) {
        this.securityContext = securityContext;
        this.callbackHandler = callbackHandler;
        this.id = id;
        this.keyIdentifierType = keyIdentifierType;
    }

    private void incrementAndTestInvocationCount() throws XMLSecurityException {
        invocationCount++;
        if (invocationCount >= 10) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY_TOKEN);
        }
    }

    private void decrementInvocationCount() {
        invocationCount--;
    }

    public XMLSecurityConstants.KeyIdentifierType getKeyIdentifierType() {
        return keyIdentifierType;
    }

    public String getId() {
        return this.id;
    }

    public Object getProcessor() {
        return processor;
    }

    public void setProcessor(Object processor) {
        this.processor = processor;
    }

    @Override
    public List<QName> getElementPath() {
        return elementPath;
    }

    public void setElementPath(List<QName> elementPath) {
        this.elementPath = Collections.unmodifiableList(elementPath);
    }

    @Override
    public XMLSecEvent getXMLSecEvent() {
        return xmlSecEvent;
    }

    @Override
    public void setXMLSecEvent(XMLSecEvent xmlSecEvent) {
        this.xmlSecEvent = xmlSecEvent;
    }

    public CallbackHandler getCallbackHandler() {
        return callbackHandler;
    }

    protected abstract Key getKey(String algorithmURI, XMLSecurityConstants.KeyUsage keyUsage) throws XMLSecurityException;

    @Override
    public Key getSecretKey(String algorithmURI, XMLSecurityConstants.KeyUsage keyUsage) throws XMLSecurityException {
        incrementAndTestInvocationCount();
        Key key = getKey(algorithmURI, keyUsage);
        if (key != null && this.securityContext != null) {
            AlgorithmSuiteSecurityEvent algorithmSuiteSecurityEvent = new AlgorithmSuiteSecurityEvent();
            algorithmSuiteSecurityEvent.setAlgorithmURI(algorithmURI);
            algorithmSuiteSecurityEvent.setKeyUsage(keyUsage);
            if (key instanceof RSAKey) {
                algorithmSuiteSecurityEvent.setKeyLength(((RSAKey) key).getModulus().bitLength());
            } else if (key instanceof SecretKey) {
                algorithmSuiteSecurityEvent.setKeyLength(key.getEncoded().length * 8);
            } else {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.UNSUPPORTED_ALGORITHM, "invalidKeySize");
            }
            this.securityContext.registerSecurityEvent(algorithmSuiteSecurityEvent);
        }
        decrementInvocationCount();
        return key;
    }

    protected abstract PublicKey getPubKey(String algorithmURI, XMLSecurityConstants.KeyUsage keyUsage) throws XMLSecurityException;

    @Override
    public PublicKey getPublicKey(String algorithmURI, XMLSecurityConstants.KeyUsage keyUsage) throws XMLSecurityException {
        incrementAndTestInvocationCount();
        PublicKey publicKey = getPubKey(algorithmURI, keyUsage);
        if (publicKey != null) {
            AlgorithmSuiteSecurityEvent algorithmSuiteSecurityEvent = new AlgorithmSuiteSecurityEvent();
            algorithmSuiteSecurityEvent.setAlgorithmURI(algorithmURI);
            algorithmSuiteSecurityEvent.setKeyUsage(keyUsage);
            if (publicKey instanceof RSAKey) {
                algorithmSuiteSecurityEvent.setKeyLength(((RSAKey) publicKey).getModulus().bitLength());
            } else if (publicKey instanceof DSAKey) {
                algorithmSuiteSecurityEvent.setKeyLength(((DSAKey) publicKey).getParams().getP().bitLength());
            } else if (publicKey instanceof ECKey) {
                algorithmSuiteSecurityEvent.setKeyLength(((ECKey) publicKey).getParams().getOrder().bitLength());
            } else {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.UNSUPPORTED_ALGORITHM);
            }
            securityContext.registerSecurityEvent(algorithmSuiteSecurityEvent);
        }
        decrementInvocationCount();
        return publicKey;
    }

    public X509Certificate[] getX509Certificates() throws XMLSecurityException {
        return null;
    }

    public void verify() throws XMLSecurityException {
    }

    @Override
    public List<SecurityToken> getWrappedTokens() {
        return Collections.unmodifiableList(wrappedTokens);
    }

    @Override
    public void addWrappedToken(SecurityToken securityToken) {
        wrappedTokens.add(securityToken);
    }

    @Override
    public void addTokenUsage(TokenUsage tokenUsage) throws XMLSecurityException {
        incrementAndTestInvocationCount();
        if (!this.tokenUsages.contains(tokenUsage)) {
            this.tokenUsages.add(tokenUsage);
        }
        if (getKeyWrappingToken() != null) {
            getKeyWrappingToken().addTokenUsage(tokenUsage);
        }
        decrementInvocationCount();
    }

    @Override
    public List<TokenUsage> getTokenUsages() {
        return tokenUsages;
    }
}
