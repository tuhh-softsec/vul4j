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

import javax.security.auth.callback.CallbackHandler;

import org.apache.xml.security.stax.ext.SecurityContext;
import org.apache.xml.security.stax.ext.SecurityToken;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;

/**
 * @author $Author: coheigea $
 * @version $Revision: 1354898 $ $Date: 2012-06-28 11:19:02 +0100 (Thu, 28 Jun 2012) $
 */
public class X509SecurityToken extends AbstractSecurityToken {
    private X509Certificate[] x509Certificates;
    private Key key;
    private final XMLSecurityConstants.TokenType tokenType;

    protected X509SecurityToken(XMLSecurityConstants.TokenType tokenType, SecurityContext securityContext,
                                CallbackHandler callbackHandler, String id,
                                XMLSecurityConstants.KeyIdentifierType keyIdentifierType) {
        super(securityContext, callbackHandler, id, keyIdentifierType);
        this.tokenType = tokenType;
    }

    @Override
    public boolean isAsymmetric() {
        return true;
    }

    @Override
    public Key getKey(String algorithmURI, XMLSecurityConstants.KeyUsage keyUsage) throws XMLSecurityException {
        return key;
    }
    
    public void setKey(Key key) {
        this.key = key;
    }

    @Override
    public PublicKey getPubKey(String algorithmURI, XMLSecurityConstants.KeyUsage keyUsage) throws XMLSecurityException {
        X509Certificate[] x509Certificates = getX509Certificates();
        if (x509Certificates == null || x509Certificates.length == 0) {
            if (getKey(algorithmURI, keyUsage) instanceof PublicKey) {
                return (PublicKey)getKey(algorithmURI, keyUsage);
            }
            return null;
        }
        return x509Certificates[0].getPublicKey();
    }

    @Override
    public X509Certificate[] getX509Certificates() throws XMLSecurityException {
        return this.x509Certificates;
    }
    
    public void setX509Certificates(X509Certificate[] x509Certificates) {
        this.x509Certificates = x509Certificates;
    }

    @Override
    public SecurityToken getKeyWrappingToken() {
        return null;
    }

    @Override
    public XMLSecurityConstants.TokenType getTokenType() {
        return tokenType;
    }
}
