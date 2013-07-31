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
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.securityEvent.AlgorithmSuiteSecurityEvent;

import javax.crypto.SecretKey;
import javax.xml.namespace.QName;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAKey;
import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;
import java.util.*;

/**
 * @author $Author: coheigea $
 * @version $Revision: 1359731 $ $Date: 2012-07-10 16:39:40 +0100 (Tue, 10 Jul 2012) $
 */
public abstract class AbstractInboundSecurityToken implements InboundSecurityToken {

    //prevent recursive key references
    private boolean invoked = false;

    private InboundSecurityContext inboundSecurityContext;
    private final String id;
    private List<QName> elementPath;
    private XMLSecEvent xmlSecEvent;
    private SecurityTokenConstants.KeyIdentifier keyIdentifier;
    private final List<InboundSecurityToken> wrappedTokens = new ArrayList<InboundSecurityToken>();
    private InboundSecurityToken keyWrappingToken;
    private final List<SecurityTokenConstants.TokenUsage> tokenUsages = new ArrayList<SecurityTokenConstants.TokenUsage>();
    private final Map<String, Key> keyTable = new Hashtable<String, Key>();
    private PublicKey publicKey;
    private X509Certificate[] x509Certificates;
    private boolean asymmetric = false;
    private boolean includedInMessage = false;
    private String sha1Identifier;

    public AbstractInboundSecurityToken(
            InboundSecurityContext inboundSecurityContext, String id,
            SecurityTokenConstants.KeyIdentifier keyIdentifier, boolean includedInMessage) {

        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("No id specified");
        }

        if (keyIdentifier == null) {
            throw new IllegalArgumentException("No keyIdentifier specified");
        }

        this.inboundSecurityContext = inboundSecurityContext;
        this.id = id;
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
    public String getId() {
        return this.id;
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

    @Override
    public boolean isAsymmetric() throws XMLSecurityException {
        return asymmetric;
    }

    public void setSecretKey(String algorithmURI, Key key) {
        if (algorithmURI == null) {
            throw new IllegalArgumentException("algorithmURI must not be null");
        }
        if (key != null) {
            this.keyTable.put(algorithmURI, key);
        }
        if (key instanceof PrivateKey) {
            this.asymmetric = true;
        }
    }

    @Override
    public Map<String, Key> getSecretKey() throws XMLSecurityException {
        return Collections.unmodifiableMap(keyTable);
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

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
        this.asymmetric = true;
    }

    @Override
    public PublicKey getPublicKey() throws XMLSecurityException {
        if (this.publicKey != null) {
            return this.publicKey;
        }
        X509Certificate[] x509Certificates = getX509Certificates();
        if (x509Certificates != null && x509Certificates.length > 0) {
            this.publicKey = x509Certificates[0].getPublicKey();
        }
        return this.publicKey;
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

    public void setX509Certificates(X509Certificate[] x509Certificates) {
        this.x509Certificates = x509Certificates;
    }

    @Override
    public X509Certificate[] getX509Certificates() throws XMLSecurityException {
        return x509Certificates;
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
    public List<SecurityTokenConstants.TokenUsage> getTokenUsages() {
        return tokenUsages;
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

    public String getSha1Identifier() {
        return sha1Identifier;
    }

    public void setSha1Identifier(String sha1Identifier) {
        this.sha1Identifier = sha1Identifier;
    }
}
