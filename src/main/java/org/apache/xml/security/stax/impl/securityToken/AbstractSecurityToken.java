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
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.securityToken.SecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants.TokenUsage;

/**
 * @author $Author: coheigea $
 * @version $Revision: 1359731 $ $Date: 2012-07-10 16:39:40 +0100 (Tue, 10 Jul 2012) $
 */
public abstract class AbstractSecurityToken implements SecurityToken {

    private final String id;
    private PublicKey publicKey;
    private X509Certificate[] x509Certificates;
    private boolean asymmetric = false;
    private String sha1Identifier;
    
    protected final Map<String, Key> keyTable = new Hashtable<String, Key>();
    protected final List<SecurityTokenConstants.TokenUsage> tokenUsages = new ArrayList<SecurityTokenConstants.TokenUsage>();
    
    public AbstractSecurityToken(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("No id specified");
        }


        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }
    
    protected void setAsymmetric(boolean asymmetric) {
        this.asymmetric = asymmetric;
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

    public void setX509Certificates(X509Certificate[] x509Certificates) {
        this.x509Certificates = x509Certificates;
    }

    @Override
    public X509Certificate[] getX509Certificates() throws XMLSecurityException {
        return x509Certificates;
    }
    
    @Override
    public void addTokenUsage(TokenUsage tokenUsage) throws XMLSecurityException {
        tokenUsages.add(tokenUsage);
    }

    @Override
    public List<SecurityTokenConstants.TokenUsage> getTokenUsages() {
        return tokenUsages;
    }

    public String getSha1Identifier() {
        return sha1Identifier;
    }

    public void setSha1Identifier(String sha1Identifier) {
        this.sha1Identifier = sha1Identifier;
    }
}
