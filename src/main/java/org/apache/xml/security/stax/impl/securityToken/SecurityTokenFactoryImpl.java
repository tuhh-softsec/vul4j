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

import javax.security.auth.callback.CallbackHandler;

import org.apache.xml.security.binding.xmldsig.DSAKeyValueType;
import org.apache.xml.security.binding.xmldsig.KeyInfoType;
import org.apache.xml.security.binding.xmldsig.KeyValueType;
import org.apache.xml.security.binding.xmldsig.RSAKeyValueType;
import org.apache.xml.security.binding.xmldsig.X509DataType;
import org.apache.xml.security.binding.xmldsig11.ECKeyValueType;
import org.apache.xml.security.stax.crypto.Crypto;
import org.apache.xml.security.stax.ext.SecurityContext;
import org.apache.xml.security.stax.ext.SecurityToken;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityConstants.KeyIdentifierType;
import org.apache.xml.security.stax.ext.XMLSecurityConstants.KeyUsage;
import org.apache.xml.security.stax.ext.XMLSecurityConstants.TokenType;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;

/**
 * Factory to create SecurityToken Objects from keys in XML
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class SecurityTokenFactoryImpl extends SecurityTokenFactory {

    protected SecurityTokenFactoryImpl() {
    }

    @Override
    public SecurityToken getSecurityToken(KeyInfoType keyInfoType, Crypto crypto,
                                          final CallbackHandler callbackHandler,
                                          XMLSecurityProperties securityProperties,
                                          SecurityContext securityContext) throws XMLSecurityException {
        if (keyInfoType != null) {
            final KeyValueType keyValueType
                    = XMLSecurityUtils.getQNameType(keyInfoType.getContent(), XMLSecurityConstants.TAG_dsig_KeyValue);
            if (keyValueType != null) {
                return getSecurityToken(keyValueType, callbackHandler, securityContext);
            }
            // TODO revisit
            final X509DataType x509DataType = 
                XMLSecurityUtils.getQNameType(keyInfoType.getContent(), XMLSecurityConstants.TAG_dsig_X509Data);
            if (x509DataType != null) {
                X509SecurityToken token = 
                        new X509SecurityToken(XMLSecurityConstants.X509V3Token, securityContext,
                                callbackHandler, "", XMLSecurityConstants.XMLKeyIdentifierType.X509_ISSUER_SERIAL);
                token.setKey(securityProperties.getSignatureVerificationKey());
                return token;
            }
        }
        
        // TODO revisit
        SecretKeySecurityToken token = 
                new SecretKeySecurityToken(securityContext, callbackHandler, "", 
                        XMLSecurityConstants.XMLKeyIdentifierType.KEY_VALUE);
        token.setKey(securityProperties.getSignatureVerificationKey());
        return token;
        
        // throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY, "noKeyinfo");
    }
    
    private static SecurityToken getSecurityToken(KeyValueType keyValueType,
            final CallbackHandler callbackHandler, SecurityContext securityContext)
        throws XMLSecurityException {

        final RSAKeyValueType rsaKeyValueType = 
                XMLSecurityUtils.getQNameType(keyValueType.getContent(), XMLSecurityConstants.TAG_dsig_RSAKeyValue);
        if (rsaKeyValueType != null) {
            return new RsaKeyValueSecurityToken(rsaKeyValueType, securityContext,
                    callbackHandler, XMLSecurityConstants.XMLKeyIdentifierType.KEY_VALUE);
        }
        final DSAKeyValueType dsaKeyValueType = 
                XMLSecurityUtils.getQNameType(keyValueType.getContent(), XMLSecurityConstants.TAG_dsig_DSAKeyValue);
        if (dsaKeyValueType != null) {
            return new DsaKeyValueSecurityToken(dsaKeyValueType, securityContext,
                    callbackHandler, XMLSecurityConstants.XMLKeyIdentifierType.KEY_VALUE);
        }
        final ECKeyValueType ecKeyValueType = 
                XMLSecurityUtils.getQNameType(keyValueType.getContent(), XMLSecurityConstants.TAG_dsig11_ECKeyValue);
        if (ecKeyValueType != null) {
            return new ECKeyValueSecurityToken(ecKeyValueType, securityContext,
                    callbackHandler, XMLSecurityConstants.XMLKeyIdentifierType.KEY_VALUE);
        }
        throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY, "unsupportedKeyInfo");
    }
    
    private static class SecretKeySecurityToken extends AbstractSecurityToken {

        private Key key;
        
        public SecretKeySecurityToken(SecurityContext securityContext,
                CallbackHandler callbackHandler, String id,
                KeyIdentifierType keyIdentifierType) {
            super(securityContext, callbackHandler, id, keyIdentifierType);
        }

        @Override
        public boolean isAsymmetric() {
            return false;
        }

        @Override
        public SecurityToken getKeyWrappingToken() throws XMLSecurityException {
            return null;
        }

        @Override
        public TokenType getTokenType() {
            return null;
        }

        @Override
        protected Key getKey(String algorithmURI, KeyUsage keyUsage)
                throws XMLSecurityException {
            return key;
        }
        
        public void setKey(Key key) {
            this.key = key;
        }

        @Override
        protected PublicKey getPubKey(String algorithmURI, KeyUsage keyUsage)
                throws XMLSecurityException {
            return null;
        }
        
    }
}
