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

import org.apache.xml.security.binding.xmldsig.SignatureType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.*;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.stax.securityToken.SecurityTokenFactory;
import org.apache.xml.security.stax.securityEvent.*;

/**
 * An input handler for XML Signature.
 */
public class XMLSignatureInputHandler extends AbstractSignatureInputHandler {

    @Override
    protected SignatureVerifier newSignatureVerifier(final InputProcessorChain inputProcessorChain,
                                                     final XMLSecurityProperties securityProperties,
                                                     final SignatureType signatureType) throws XMLSecurityException {

        final InboundSecurityContext inboundSecurityContext = inputProcessorChain.getSecurityContext();

        AlgorithmSuiteSecurityEvent algorithmSuiteSecurityEvent = new AlgorithmSuiteSecurityEvent();
        algorithmSuiteSecurityEvent.setAlgorithmURI(signatureType.getSignedInfo().getCanonicalizationMethod().getAlgorithm());
        algorithmSuiteSecurityEvent.setAlgorithmUsage(XMLSecurityConstants.SigC14n);
        algorithmSuiteSecurityEvent.setCorrelationID(signatureType.getId());
        inboundSecurityContext.registerSecurityEvent(algorithmSuiteSecurityEvent);

        SignatureValueSecurityEvent signatureValueSecurityEvent = new SignatureValueSecurityEvent();
        signatureValueSecurityEvent.setSignatureValue(signatureType.getSignatureValue().getValue());
        signatureValueSecurityEvent.setCorrelationID(signatureType.getId());
        inboundSecurityContext.registerSecurityEvent(signatureValueSecurityEvent);

        return new XMLSignatureVerifier(signatureType, inboundSecurityContext, securityProperties);
    }

    @Override
    protected void addSignatureReferenceInputProcessorToChain(
            InputProcessorChain inputProcessorChain, XMLSecurityProperties securityProperties,
            SignatureType signatureType, InboundSecurityToken inboundSecurityToken) throws XMLSecurityException {
        //add processors to verify references
        inputProcessorChain.addProcessor(
                new XMLSignatureReferenceVerifyInputProcessor(
                        inputProcessorChain, signatureType, inboundSecurityToken, securityProperties));
    }
    
    public class XMLSignatureVerifier extends SignatureVerifier {
        
        public XMLSignatureVerifier(SignatureType signatureType, InboundSecurityContext inboundSecurityContext,
                                    XMLSecurityProperties securityProperties) throws XMLSecurityException {
            super(signatureType, inboundSecurityContext, securityProperties);
        }

        @Override
        protected InboundSecurityToken retrieveSecurityToken(
                SignatureType signatureType, XMLSecurityProperties securityProperties,
                InboundSecurityContext inboundSecurityContext) throws XMLSecurityException {

            InboundSecurityToken inboundSecurityToken = SecurityTokenFactory.getInstance().getSecurityToken(signatureType.getKeyInfo(),
                    SecurityTokenConstants.KeyUsage_Signature_Verification, securityProperties, inboundSecurityContext);

            inboundSecurityToken.verify();

            inboundSecurityToken.addTokenUsage(SecurityTokenConstants.TokenUsage_Signature);

            TokenSecurityEvent<?> tokenSecurityEvent = XMLSecurityUtils.createTokenSecurityEvent(inboundSecurityToken, signatureType.getId());
            inboundSecurityContext.registerSecurityEvent(tokenSecurityEvent);

            return inboundSecurityToken;
        }
    }
}
