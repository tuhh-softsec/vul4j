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

import java.util.List;

import javax.crypto.Cipher;
import javax.xml.namespace.QName;

import org.apache.xml.security.binding.xmlenc.EncryptedDataType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.*;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.securityEvent.*;


/**
 * Processor for decryption of EncryptedData XML structures
 *
 * @author $Author: giger $
 * @version $Revision: 1228829 $ $Date: 2012-01-08 11:44:13 +0000 (Sun, 08 Jan 2012) $
 */
public class XMLDecryptInputProcessor extends AbstractDecryptInputProcessor {
    
    public XMLDecryptInputProcessor(XMLSecurityProperties securityProperties) throws XMLSecurityException {
        super(securityProperties);
    }
    
    @Override
    protected AbstractDecryptedEventReaderInputProcessor newDecryptedEventReaderInputProcessor(
            boolean encryptedHeader, XMLSecStartElement xmlSecStartElement, EncryptedDataType currentEncryptedDataType,
            InboundSecurityToken inboundSecurityToken, InboundSecurityContext inboundSecurityContext) throws XMLSecurityException {
        return new DecryptedEventReaderInputProcessor(getSecurityProperties(),
                SecurePart.Modifier.getModifier(currentEncryptedDataType.getType()),
                 encryptedHeader, xmlSecStartElement, currentEncryptedDataType, this, inboundSecurityToken);
    }

    @Override
    protected void handleSecurityToken(
            InboundSecurityToken inboundSecurityToken, InboundSecurityContext inboundSecurityContext, EncryptedDataType encryptedDataType
    ) throws XMLSecurityException {
        inboundSecurityToken.addTokenUsage(SecurityTokenConstants.TokenUsage_Encryption);

        TokenSecurityEvent<?> tokenSecurityEvent = XMLSecurityUtils.createTokenSecurityEvent(inboundSecurityToken, encryptedDataType.getId());
        inboundSecurityContext.registerSecurityEvent(tokenSecurityEvent);
    }

    @Override
    protected void handleEncryptedContent(InputProcessorChain inputProcessorChain,
                                          XMLSecStartElement parentXMLSecStartElement,
                                          InboundSecurityToken inboundSecurityToken,
                                          EncryptedDataType encryptedDataType)
            throws XMLSecurityException {

        final DocumentContext documentContext = inputProcessorChain.getDocumentContext();
        List<QName> elementPath = parentXMLSecStartElement.getElementPath();

        ContentEncryptedElementSecurityEvent contentEncryptedElementSecurityEvent =
                new ContentEncryptedElementSecurityEvent(inboundSecurityToken, true, documentContext.getProtectionOrder());
        contentEncryptedElementSecurityEvent.setElementPath(elementPath);
        contentEncryptedElementSecurityEvent.setXmlSecEvent(parentXMLSecStartElement);
        contentEncryptedElementSecurityEvent.setSecurityToken(inboundSecurityToken);
        contentEncryptedElementSecurityEvent.setCorrelationID(encryptedDataType.getId());
        inputProcessorChain.getSecurityContext().registerSecurityEvent(contentEncryptedElementSecurityEvent);
    }

    @Override
    protected void handleCipherReference(InputProcessorChain inputProcessorChain, EncryptedDataType encryptedDataType,
                                         Cipher cipher, InboundSecurityToken inboundSecurityToken) throws XMLSecurityException {
        throw new XMLSecurityException("errorMessages.NotYetImplementedException");
    }

    /**
     * The DecryptedEventReaderInputProcessor reads the decrypted stream with a StAX reader and
     * forwards the generated XMLEvents
     */
    public class DecryptedEventReaderInputProcessor extends AbstractDecryptedEventReaderInputProcessor {

        public DecryptedEventReaderInputProcessor(
                XMLSecurityProperties securityProperties, SecurePart.Modifier encryptionModifier,
                boolean encryptedHeader, XMLSecStartElement xmlSecStartElement,
                EncryptedDataType encryptedDataType,
                XMLDecryptInputProcessor decryptInputProcessor,
                InboundSecurityToken inboundSecurityToken
        ) {
            super(
                    securityProperties, encryptionModifier, encryptedHeader, xmlSecStartElement,
                    encryptedDataType, decryptInputProcessor, inboundSecurityToken);
        }

        @Override
        protected void handleEncryptedElement(InputProcessorChain inputProcessorChain,
                                              XMLSecStartElement xmlSecStartElement,
                                              InboundSecurityToken inboundSecurityToken,
                                              EncryptedDataType encryptedDataType) throws XMLSecurityException {
            //fire a SecurityEvent:
            final DocumentContext documentContext = inputProcessorChain.getDocumentContext();
            List<QName> elementPath = xmlSecStartElement.getElementPath();
            
            EncryptedElementSecurityEvent encryptedElementSecurityEvent =
                    new EncryptedElementSecurityEvent(inboundSecurityToken, true, documentContext.getProtectionOrder());
            encryptedElementSecurityEvent.setElementPath(elementPath);
            encryptedElementSecurityEvent.setXmlSecEvent(xmlSecStartElement);
            encryptedElementSecurityEvent.setSecurityToken(inboundSecurityToken);
            encryptedElementSecurityEvent.setCorrelationID(encryptedDataType.getId());
            inputProcessorChain.getSecurityContext().registerSecurityEvent(encryptedElementSecurityEvent);
        }

    }
}
