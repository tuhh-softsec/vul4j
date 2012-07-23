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
import org.apache.xml.security.binding.xmlenc.EncryptedDataType;
import org.apache.xml.security.binding.xmlenc.ReferenceList;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.SecurityContext;
import org.apache.xml.security.stax.ext.SecurityToken;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;


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
    
    public XMLDecryptInputProcessor(
            KeyInfoType keyInfoType, ReferenceList referenceList, 
            XMLSecurityProperties securityProperties
    ) throws XMLSecurityException {
        super(keyInfoType, referenceList, securityProperties);
    }
    
    @Override
    protected AbstractDecryptedEventReaderInputProcessor newDecryptedEventReaderInputProcessor(
            boolean encryptedHeader, XMLSecStartElement xmlSecStartElement, EncryptedDataType currentEncryptedDataType,
            SecurityToken securityToken, SecurityContext securityContext) throws XMLSecurityException {
        return new DecryptedEventReaderInputProcessor(getSecurityProperties(),
                SecurePart.Modifier.getModifier(currentEncryptedDataType.getType()),
                 encryptedHeader, xmlSecStartElement, this, securityToken);
    }

    @Override
    protected void handleSecurityToken(
            SecurityToken securityToken, SecurityContext securityContext, EncryptedDataType encryptedDataType) throws XMLSecurityException {
        /*
         *  securityToken.addTokenUsage(SecurityToken.TokenUsage.Encryption);
        TokenSecurityEvent tokenSecurityEvent = WSSUtils.createTokenSecurityEvent(securityToken);
        securityContext.registerSecurityEvent(tokenSecurityEvent);
         */
    }
    
    @Override
    protected void handleEncryptedContent(InputProcessorChain inputProcessorChain,
                                                   XMLSecStartElement parentXMLSecStartElement,
                                                   SecurityToken securityToken) throws XMLSecurityException {
        // TODO
        /*
         * final DocumentContext documentContext = inputProcessorChain.getDocumentContext();
        List<QName> elementPath = parentStartXMLEvent.getElementPath();
        if (elementPath.size() == 2 && WSSUtils.isInSOAPBody(elementPath)) {
            //soap:body content encryption counts as EncryptedPart
            EncryptedPartSecurityEvent encryptedPartSecurityEvent =
                    new EncryptedPartSecurityEvent(securityToken, true, documentContext.getProtectionOrder());
            encryptedPartSecurityEvent.setElementPath(elementPath);
            encryptedPartSecurityEvent.setXmlSecEvent(parentStartXMLEvent);
            ((WSSecurityContext) inputProcessorChain.getSecurityContext()).registerSecurityEvent(encryptedPartSecurityEvent);
        } else {
            ContentEncryptedElementSecurityEvent contentEncryptedElementSecurityEvent =
                    new ContentEncryptedElementSecurityEvent(securityToken, true, documentContext.getProtectionOrder());
            contentEncryptedElementSecurityEvent.setElementPath(elementPath);
            contentEncryptedElementSecurityEvent.setXmlSecEvent(parentStartXMLEvent);
            ((WSSecurityContext) inputProcessorChain.getSecurityContext()).registerSecurityEvent(contentEncryptedElementSecurityEvent);
        }
         */
    }

    /**
     * The DecryptedEventReaderInputProcessor reads the decrypted stream with a StAX reader and
     * forwards the generated XMLEvents
     */
    public class DecryptedEventReaderInputProcessor extends AbstractDecryptedEventReaderInputProcessor {

        public DecryptedEventReaderInputProcessor(
                XMLSecurityProperties securityProperties, SecurePart.Modifier encryptionModifier,
                boolean encryptedHeader, XMLSecStartElement xmlSecStartElement,
                XMLDecryptInputProcessor decryptInputProcessor,
                SecurityToken securityToken
        ) {
            super(securityProperties, encryptionModifier, encryptedHeader, xmlSecStartElement, decryptInputProcessor, securityToken);
        }

        @Override
        protected void handleEncryptedElement(InputProcessorChain inputProcessorChain, XMLSecStartElement xmlSecStartElement,
                                              SecurityToken securityToken) throws XMLSecurityException {
            //fire a SecurityEvent:
            /* TODO
             *  EncryptedElementSecurityEvent encryptedElementSecurityEvent =
                        new EncryptedElementSecurityEvent(securityToken, true, documentContext.getProtectionOrder());
                encryptedElementSecurityEvent.setElementPath(elementPath);
                encryptedElementSecurityEvent.setXmlSecEvent(xmlSecStartElement);
                ((WSSecurityContext) inputProcessorChain.getSecurityContext()).registerSecurityEvent(encryptedElementSecurityEvent);
             */
        }

    }
}
