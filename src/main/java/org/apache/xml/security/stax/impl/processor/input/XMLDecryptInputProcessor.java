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

import javax.xml.namespace.QName;

import org.apache.xml.security.binding.xmldsig.KeyInfoType;
import org.apache.xml.security.binding.xmlenc.EncryptedDataType;
import org.apache.xml.security.binding.xmlenc.ReferenceList;
import org.apache.xml.security.stax.ext.DocumentContext;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.SecurityContext;
import org.apache.xml.security.stax.ext.SecurityToken;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.securityEvent.ContentEncryptedElementSecurityEvent;
import org.apache.xml.security.stax.securityEvent.DefaultTokenSecurityEvent;
import org.apache.xml.security.stax.securityEvent.EncryptedElementSecurityEvent;
import org.apache.xml.security.stax.securityEvent.KeyNameTokenSecurityEvent;
import org.apache.xml.security.stax.securityEvent.KeyValueTokenSecurityEvent;
import org.apache.xml.security.stax.securityEvent.TokenSecurityEvent;
import org.apache.xml.security.stax.securityEvent.X509TokenSecurityEvent;


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
            SecurityToken securityToken, SecurityContext securityContext, EncryptedDataType encryptedDataType
    ) throws XMLSecurityException {
        securityToken.addTokenUsage(SecurityToken.TokenUsage.Encryption);
        XMLSecurityConstants.TokenType tokenType = securityToken.getTokenType();
        
        TokenSecurityEvent tokenSecurityEvent = null;
        if (tokenType == XMLSecurityConstants.X509V1Token
                || tokenType == XMLSecurityConstants.X509V3Token
                || tokenType == XMLSecurityConstants.X509Pkcs7Token
                || tokenType == XMLSecurityConstants.X509PkiPathV1Token) {
            tokenSecurityEvent = new X509TokenSecurityEvent();
        } else if (tokenType == XMLSecurityConstants.KeyValueToken) {
            tokenSecurityEvent = new KeyValueTokenSecurityEvent();
        } else if (tokenType == XMLSecurityConstants.KeyNameToken) {
            tokenSecurityEvent = new KeyNameTokenSecurityEvent();
        } else if (tokenType == XMLSecurityConstants.DefaultToken) {
            tokenSecurityEvent = new DefaultTokenSecurityEvent();
        } else {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.UNSUPPORTED_SECURITY_TOKEN);
        }
        
        tokenSecurityEvent.setSecurityToken(securityToken);
        securityContext.registerSecurityEvent(tokenSecurityEvent);
    }
    
    @Override
    protected void handleEncryptedContent(InputProcessorChain inputProcessorChain,
                                                   XMLSecStartElement parentXMLSecStartElement,
                                                   SecurityToken securityToken) throws XMLSecurityException {
        final DocumentContext documentContext = inputProcessorChain.getDocumentContext();
        List<QName> elementPath = parentXMLSecStartElement.getElementPath();
        
        ContentEncryptedElementSecurityEvent contentEncryptedElementSecurityEvent =
                new ContentEncryptedElementSecurityEvent(securityToken, true, documentContext.getProtectionOrder());
        contentEncryptedElementSecurityEvent.setElementPath(elementPath);
        contentEncryptedElementSecurityEvent.setXmlSecEvent(parentXMLSecStartElement);
        
        contentEncryptedElementSecurityEvent.setSecurityToken(securityToken);
        inputProcessorChain.getSecurityContext().registerSecurityEvent(contentEncryptedElementSecurityEvent);
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
            final DocumentContext documentContext = inputProcessorChain.getDocumentContext();
            List<QName> elementPath = xmlSecStartElement.getElementPath();
            
            EncryptedElementSecurityEvent encryptedElementSecurityEvent =
                    new EncryptedElementSecurityEvent(securityToken, true, documentContext.getProtectionOrder());
            encryptedElementSecurityEvent.setElementPath(elementPath);
            encryptedElementSecurityEvent.setXmlSecEvent(xmlSecStartElement);
            
            encryptedElementSecurityEvent.setSecurityToken(securityToken);
            inputProcessorChain.getSecurityContext().registerSecurityEvent(encryptedElementSecurityEvent);
        }

    }
}
