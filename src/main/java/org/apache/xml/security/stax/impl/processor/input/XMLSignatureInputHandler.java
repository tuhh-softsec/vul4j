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

import org.apache.xml.security.binding.xmldsig.KeyInfoType;
import org.apache.xml.security.binding.xmldsig.SignatureType;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.SecurityContext;
import org.apache.xml.security.stax.ext.SecurityToken;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.impl.securityToken.SecurityTokenFactory;
import org.apache.xml.security.stax.securityEvent.AlgorithmSuiteSecurityEvent;
import org.apache.xml.security.stax.securityEvent.KeyValueTokenSecurityEvent;
import org.apache.xml.security.stax.securityEvent.SignatureValueSecurityEvent;
import org.apache.xml.security.stax.securityEvent.TokenSecurityEvent;
import org.apache.xml.security.stax.securityEvent.X509TokenSecurityEvent;

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
        final SecurityContext securityContext = inputProcessorChain.getSecurityContext();
        final SignatureVerifier signatureVerifier = 
                new XMLSignatureVerifier(signatureType, securityContext, securityProperties) {
            @Override
            protected void handleSecurityToken(SecurityToken securityToken) throws XMLSecurityException {
                //we have to emit a TokenSecurityEvent here too since it could be an embedded token
                securityToken.addTokenUsage(SecurityToken.TokenUsage.Signature);
                XMLSecurityConstants.TokenType tokenType = securityToken.getTokenType();
                TokenSecurityEvent tokenSecurityEvent = null;
                if (tokenType == XMLSecurityConstants.X509V1Token
                        || tokenType == XMLSecurityConstants.X509V3Token
                        || tokenType == XMLSecurityConstants.X509Pkcs7Token
                        || tokenType == XMLSecurityConstants.X509PkiPathV1Token) {
                    tokenSecurityEvent = new X509TokenSecurityEvent();
                } else if (tokenType == XMLSecurityConstants.KeyValueToken) {
                    tokenSecurityEvent = new KeyValueTokenSecurityEvent();
                }/* else {
                    throw new XMLSecurityException(XMLSecurityException.ErrorCode.UNSUPPORTED_SECURITY_TOKEN);
                }*/
                if (tokenSecurityEvent != null) {
                    securityContext.registerSecurityEvent(tokenSecurityEvent);
                }
                
                SignatureValueSecurityEvent signatureValueSecurityEvent = new SignatureValueSecurityEvent();
                signatureValueSecurityEvent.setSignatureValue(signatureType.getSignatureValue().getValue());
                securityContext.registerSecurityEvent(signatureValueSecurityEvent);

                AlgorithmSuiteSecurityEvent algorithmSuiteSecurityEvent = new AlgorithmSuiteSecurityEvent();
                algorithmSuiteSecurityEvent.setAlgorithmURI(signatureType.getSignedInfo().getCanonicalizationMethod().getAlgorithm());
                algorithmSuiteSecurityEvent.setKeyUsage(XMLSecurityConstants.C14n);
                securityContext.registerSecurityEvent(algorithmSuiteSecurityEvent);
                
                super.handleSecurityToken(securityToken);
            }
        };
        
        return signatureVerifier;
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
            return SecurityTokenFactory.getInstance().getSecurityToken(keyInfoType, 
                    null,
                    securityProperties.getCallbackHandler(),
                    securityProperties,
                    securityContext);
        }
    }

}
