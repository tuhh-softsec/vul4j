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
import java.security.interfaces.DSAKey;
import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.crypto.SecretKey;
import javax.xml.namespace.QName;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.securityEvent.AlgorithmSuiteSecurityEvent;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;

/**
 * @author $Author: coheigea $
 * @version $Revision: 1359731 $ $Date: 2012-07-10 16:39:40 +0100 (Tue, 10 Jul 2012) $
 */
public abstract class AbstractInboundSecurityToken extends AbstractSecurityToken implements InboundSecurityToken {

    //prevent recursive key references
    private boolean invoked = false;

    private InboundSecurityContext inboundSecurityContext;
    private List<QName> elementPath;
    private XMLSecEvent xmlSecEvent;
    private SecurityTokenConstants.KeyIdentifier keyIdentifier;
    private final List<InboundSecurityToken> wrappedTokens = new ArrayList<InboundSecurityToken>();
    private InboundSecurityToken keyWrappingToken;
    private boolean includedInMessage = false;

    public AbstractInboundSecurityToken(
            InboundSecurityContext inboundSecurityContext, String id,
            SecurityTokenConstants.KeyIdentifier keyIdentifier, boolean includedInMessage) {
        super(id);

        if (keyIdentifier == null) {
            throw new IllegalArgumentException("No keyIdentifier specified");
        }

        this.inboundSecurityContext = inboundSecurityContext;
        this.keyIdentifier = keyIdentifier;
        this.includedInMessage = includedInMessage;
    }

    private void testAndSetInvocation() throws XMLSecurityException {
        if (invoked) {
            throw new XMLSecurityException("stax.recursiveKeyReference");
        }
        invoked = true;
    }

    private void unsetInvocation() {
        invoked = false;
    }

    public SecurityTokenConstants.KeyIdentifier getKeyIdentifier() {
        return keyIdentifier;
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

    public void setXMLSecEvent(XMLSecEvent xmlSecEvent) {
        this.xmlSecEvent = xmlSecEvent;
    }

    protected Key getKey(String algorithmURI, XMLSecurityConstants.AlgorithmUsage algorithmUsage,
                         String correlationID) throws XMLSecurityException {
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
    public final Key getSecretKey(String algorithmURI, XMLSecurityConstants.AlgorithmUsage algorithmUsage,
                                  String correlationID) throws XMLSecurityException {
        if (correlationID == null) {
            throw new IllegalArgumentException("correlationID must not be null");
        }
        testAndSetInvocation();
        Key key = getKey(algorithmURI, algorithmUsage, correlationID);
        if (key != null && this.inboundSecurityContext != null) {
            AlgorithmSuiteSecurityEvent algorithmSuiteSecurityEvent = new AlgorithmSuiteSecurityEvent();
            algorithmSuiteSecurityEvent.setAlgorithmURI(algorithmURI);
            algorithmSuiteSecurityEvent.setAlgorithmUsage(algorithmUsage);
            algorithmSuiteSecurityEvent.setCorrelationID(correlationID);

            if (SecurityTokenConstants.DerivedKeyToken.equals(getTokenType())) {
                algorithmSuiteSecurityEvent.setDerivedKey(true);
            }
            if (key instanceof RSAKey) {
                algorithmSuiteSecurityEvent.setKeyLength(((RSAKey) key).getModulus().bitLength());
            } else if (key instanceof DSAKey) {
                algorithmSuiteSecurityEvent.setKeyLength(((DSAKey) key).getParams().getP().bitLength());
            } else if (key instanceof ECKey) {
                algorithmSuiteSecurityEvent.setKeyLength(((ECKey) key).getParams().getOrder().bitLength());
            } else if (key instanceof SecretKey) {
                algorithmSuiteSecurityEvent.setKeyLength(key.getEncoded().length * 8);
            } else {
                throw new XMLSecurityException("java.security.UnknownKeyType", key.getClass().getName());
            }
            this.inboundSecurityContext.registerSecurityEvent(algorithmSuiteSecurityEvent);
        }
        unsetInvocation();
        return key;
    }

    protected PublicKey getPubKey(String algorithmURI, XMLSecurityConstants.AlgorithmUsage algorithmUsage,
                                  String correlationID) throws XMLSecurityException {
        return getPublicKey();
    }

    @Override
    public final PublicKey getPublicKey(String algorithmURI, XMLSecurityConstants.AlgorithmUsage algorithmUsage,
                                        String correlationID) throws XMLSecurityException {
        if (correlationID == null) {
            throw new IllegalArgumentException("correlationID must not be null");
        }
        testAndSetInvocation();
        PublicKey publicKey = getPubKey(algorithmURI, algorithmUsage, correlationID);
        if (publicKey != null && this.inboundSecurityContext != null) {
            AlgorithmSuiteSecurityEvent algorithmSuiteSecurityEvent = new AlgorithmSuiteSecurityEvent();
            algorithmSuiteSecurityEvent.setAlgorithmURI(algorithmURI);
            algorithmSuiteSecurityEvent.setAlgorithmUsage(algorithmUsage);
            algorithmSuiteSecurityEvent.setCorrelationID(correlationID);
            if (publicKey instanceof RSAKey) {
                algorithmSuiteSecurityEvent.setKeyLength(((RSAKey) publicKey).getModulus().bitLength());
            } else if (publicKey instanceof DSAKey) {
                algorithmSuiteSecurityEvent.setKeyLength(((DSAKey) publicKey).getParams().getP().bitLength());
            } else if (publicKey instanceof ECKey) {
                algorithmSuiteSecurityEvent.setKeyLength(((ECKey) publicKey).getParams().getOrder().bitLength());
            } else {
                throw new XMLSecurityException("java.security.UnknownKeyType", publicKey.getClass().getName());
            }
            inboundSecurityContext.registerSecurityEvent(algorithmSuiteSecurityEvent);
        }
        unsetInvocation();
        return publicKey;
    }

    @Override
    public void verify() throws XMLSecurityException {
    }

    @Override
    public List<InboundSecurityToken> getWrappedTokens() {
        return Collections.unmodifiableList(wrappedTokens);
    }

    @Override
    public void addWrappedToken(InboundSecurityToken inboundSecurityToken) {
        wrappedTokens.add(inboundSecurityToken);
    }

    @Override
    public void addTokenUsage(SecurityTokenConstants.TokenUsage tokenUsage) throws XMLSecurityException {
        testAndSetInvocation();
        if (!this.tokenUsages.contains(tokenUsage)) {
            this.tokenUsages.add(tokenUsage);
        }
        if (getKeyWrappingToken() != null) {
            getKeyWrappingToken().addTokenUsage(tokenUsage);
        }
        unsetInvocation();
    }

    @Override
    public InboundSecurityToken getKeyWrappingToken() throws XMLSecurityException {
        return keyWrappingToken;
    }

    public void setKeyWrappingToken(InboundSecurityToken keyWrappingToken) {
        this.keyWrappingToken = keyWrappingToken;
    }

    @Override
    public boolean isIncludedInMessage() {
        return includedInMessage;
    }
    
}
