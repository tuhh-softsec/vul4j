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
package org.apache.xml.security.stax.impl.processor.input;

import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.crypto.SecretKey;
import javax.xml.namespace.QName;

import org.apache.xml.security.binding.xmldsig.KeyInfoType;
import org.apache.xml.security.binding.xmldsig.SignatureType;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.SecurityContext;
import org.apache.xml.security.stax.ext.SecurityToken;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

/**
 * An input handler for XML Signature.
 */
public class XMLSignatureInputHandler extends AbstractSignatureInputHandler {

    @Override
    protected SignatureVerifier newSignatureVerifier(final InputProcessorChain inputProcessorChain,
                                                     final XMLSecurityProperties securityProperties,
                                                     final SignatureType signatureType) throws XMLSecurityException {

        if (signatureType.getSignedInfo() == null) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY);
        }
        if (signatureType.getSignedInfo().getSignatureMethod() == null) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY);
        }
        if (signatureType.getSignedInfo().getCanonicalizationMethod() == null) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY);
        }
        if (signatureType.getSignatureValue() == null) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY);
        }
        /*
        if (signatureType.getKeyInfo() == null) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY);
        }
        */
        return new XMLSignatureVerifier(signatureType, inputProcessorChain.getSecurityContext(), securityProperties);
    }

    @Override
    protected void addSignatureReferenceInputProcessorToChain(InputProcessorChain inputProcessorChain,
                                                              XMLSecurityProperties securityProperties,
                                                              SignatureType signatureType, SecurityToken securityToken) throws XMLSecurityException {
        //add processors to verify references
        inputProcessorChain.addProcessor(new XMLSignatureReferenceVerifyInputProcessor(signatureType, securityToken, securityProperties));
    }
    
    public class XMLSignatureVerifier extends SignatureVerifier {
        
        public XMLSignatureVerifier(SignatureType signatureType, SecurityContext securityContext,
                                    XMLSecurityProperties securityProperties) throws XMLSecurityException {
            super(signatureType, securityContext, securityProperties);
        }
        
        protected SecurityToken retrieveSecurityToken(KeyInfoType keyInfoType,
                                                      XMLSecurityProperties securityProperties,
                                                      SecurityContext securityContext) throws XMLSecurityException {
            return new SignatureSecurityToken(securityProperties.getSignatureVerificationKey());
        }
    }
    
    private static class SignatureSecurityToken implements SecurityToken {
        private Key key;
        
        public SignatureSecurityToken(Key key) {
            this.key = key;
        }

        public String getId() {
            return null;
        }


        public Object getProcessor() {
            return null;
        }

        public boolean isAsymmetric() {
            if (key instanceof PublicKey) {
                return true;
            }
            return false;
        }

        public Key getSecretKey(
            String algorithmURI, XMLSecurityConstants.KeyUsage keyUsage
        ) throws XMLSecurityException {
            if (key instanceof SecretKey) {
                return key;
            }
            return null;
        }

        public PublicKey getPublicKey(
            String algorithmURI, XMLSecurityConstants.KeyUsage keyUsage
        ) throws XMLSecurityException {
            if (key instanceof PublicKey) {
                return (PublicKey)key;
            }
            return null;
        }

        public X509Certificate[] getX509Certificates() throws XMLSecurityException {
            return null;
        }

        public void verify() throws XMLSecurityException {
        }

        public SecurityToken getKeyWrappingToken() {
            return null;
        }

        public XMLSecurityConstants.TokenType getTokenType() {
            return null;
        }

        @Override
        public List<QName> getElementPath() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public XMLSecEvent getXMLSecEvent() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<SecurityToken> getWrappedTokens()
                throws XMLSecurityException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void addWrappedToken(SecurityToken securityToken) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void addTokenUsage(TokenUsage tokenUsage)
                throws XMLSecurityException {
            // TODO Auto-generated method stub
            
        }

        @Override
        public List<TokenUsage> getTokenUsages() {
            // TODO Auto-generated method stub
            return null;
        }
    };

}
