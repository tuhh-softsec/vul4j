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

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Deque;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.JAXBElement;

import org.apache.xml.security.binding.xmldsig.KeyInfoType;
import org.apache.xml.security.binding.xmlenc.EncryptedKeyType;
import org.apache.xml.security.stax.config.JCEAlgorithmMapper;
import org.apache.xml.security.stax.ext.AbstractInputSecurityHeaderHandler;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.SecurityContext;
import org.apache.xml.security.stax.ext.SecurityToken;
import org.apache.xml.security.stax.ext.SecurityTokenProvider;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.securityToken.AbstractInboundSecurityToken;
import org.apache.xml.security.stax.impl.securityToken.SecurityTokenFactory;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.securityEvent.EncryptedKeyTokenSecurityEvent;
import org.apache.xml.security.stax.securityEvent.TokenSecurityEvent;
import org.xmlsecurity.ns.configuration.AlgorithmType;

/**
 * An input handler for the EncryptedKey XML Structure
 *
 * @author $Author: coheigea $
 * @version $Revision: 1360243 $ $Date: 2012-07-11 16:53:55 +0100 (Wed, 11 Jul 2012) $
 */
public class XMLEncryptedKeyInputHandler extends AbstractInputSecurityHeaderHandler {

    @Override
    public void handle(final InputProcessorChain inputProcessorChain, final XMLSecurityProperties securityProperties,
                       final Deque<XMLSecEvent> eventQueue, final Integer index) throws XMLSecurityException {
        @SuppressWarnings("unchecked")
        final EncryptedKeyType encryptedKeyType =
                ((JAXBElement<EncryptedKeyType>) parseStructure(eventQueue, index, securityProperties)).getValue();

        final XMLSecEvent responsibleXMLSecStartXMLEvent = getResponsibleStartXMLEvent(eventQueue, index);
        
        handle(inputProcessorChain, encryptedKeyType, responsibleXMLSecStartXMLEvent, securityProperties);
    }
    
    public void handle(final InputProcessorChain inputProcessorChain, 
            final EncryptedKeyType encryptedKeyType, 
            final XMLSecEvent responsibleXMLSecStartXMLEvent,
            final XMLSecurityProperties securityProperties) throws XMLSecurityException {

        if (encryptedKeyType.getEncryptionMethod() == null) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.UNSUPPORTED_ALGORITHM, "noEncAlgo");
        }

        checkBSPCompliance(inputProcessorChain, encryptedKeyType);

        if (encryptedKeyType.getId() == null) {
            encryptedKeyType.setId(IDGenerator.generateID(null));
        }

        final SecurityContext securityContext = (SecurityContext) inputProcessorChain.getSecurityContext();

        final SecurityTokenProvider securityTokenProvider = new SecurityTokenProvider() {

            private AbstractInboundSecurityToken securityToken = null;

            @SuppressWarnings("unchecked")
            public SecurityToken getSecurityToken() throws XMLSecurityException {

                if (this.securityToken != null) {
                    return this.securityToken;
                }

                this.securityToken = new AbstractInboundSecurityToken(
                        securityContext, null, encryptedKeyType.getId(), null) {

                    private byte[] decryptedKey = null;

                    @Override
                    public Key getKey(String algorithmURI, XMLSecurityConstants.KeyUsage keyUsage, String correlationID)
                            throws XMLSecurityException {
                        Key key = getSecretKey().get(algorithmURI);
                        if (key != null) {
                            return key;
                        }

                        String algoFamily = JCEAlgorithmMapper.getJCERequiredKeyFromURI(algorithmURI);
                        key = new SecretKeySpec(getSecret(this, correlationID), algoFamily);
                        setSecretKey(algorithmURI, key);
                        return key;
                    }

                    @Override
                    public SecurityToken getKeyWrappingToken() throws XMLSecurityException {
                        return getWrappingSecurityToken(this);
                    }

                    @Override
                    public XMLSecurityConstants.TokenType getTokenType() {
                        return XMLSecurityConstants.EncryptedKeyToken;
                    }

                    private SecurityToken wrappingSecurityToken = null;

                    private SecurityToken getWrappingSecurityToken(SecurityToken wrappedSecurityToken)
                            throws XMLSecurityException {
                        if (wrappingSecurityToken != null) {
                            return this.wrappingSecurityToken;
                        }
                        KeyInfoType keyInfoType = encryptedKeyType.getKeyInfo();
                        this.wrappingSecurityToken = SecurityTokenFactory.getInstance().getSecurityToken(
                                keyInfoType,
                                SecurityToken.KeyInfoUsage.DECRYPTION,
                                securityProperties,
                                securityContext
                        );
                        this.wrappingSecurityToken.addWrappedToken(wrappedSecurityToken);
                        return this.wrappingSecurityToken;
                    }

                    private byte[] getSecret(SecurityToken wrappedSecurityToken, String correlationID) throws XMLSecurityException {

                        if (this.decryptedKey != null) {
                            return this.decryptedKey;
                        }

                        String algorithmURI = encryptedKeyType.getEncryptionMethod().getAlgorithm();
                        if (algorithmURI == null) {
                            throw new XMLSecurityException(XMLSecurityException.ErrorCode.UNSUPPORTED_ALGORITHM, "noEncAlgo");
                        }
                        AlgorithmType asyncEncAlgo = JCEAlgorithmMapper.getAlgorithmMapping(algorithmURI);
                        if (asyncEncAlgo == null) {
                            throw new XMLSecurityException(XMLSecurityException.ErrorCode.UNSUPPORTED_ALGORITHM, "noEncAlgo");
                        }

                        final SecurityToken wrappingSecurityToken = getWrappingSecurityToken(wrappedSecurityToken);
                        try {
                            XMLSecurityConstants.KeyUsage keyUsage;
                            if (wrappingSecurityToken.isAsymmetric()) {
                                keyUsage = XMLSecurityConstants.Asym_Key_Wrap;
                            } else {
                                keyUsage = XMLSecurityConstants.Sym_Key_Wrap;
                            }

                            Cipher cipher;
                            if (asyncEncAlgo.getJCEProvider() == null) {
                                cipher = Cipher.getInstance(asyncEncAlgo.getJCEName());
                            } else {
                                cipher = Cipher.getInstance(asyncEncAlgo.getJCEName(), asyncEncAlgo.getJCEProvider());
                            }
                            cipher.init(Cipher.UNWRAP_MODE, wrappingSecurityToken.getSecretKey(algorithmURI, keyUsage, correlationID));
                            if (encryptedKeyType.getCipherData() == null
                                    || encryptedKeyType.getCipherData().getCipherValue() == null) {
                                throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY, "noCipher");
                            }
                            Key key = cipher.unwrap(encryptedKeyType.getCipherData().getCipherValue(),
                                    asyncEncAlgo.getJCEName(),
                                    Cipher.SECRET_KEY);
                            return this.decryptedKey = key.getEncoded();

                        } catch (NoSuchPaddingException e) {
                            throw new XMLSecurityException(
                                    XMLSecurityException.ErrorCode.UNSUPPORTED_ALGORITHM, "unsupportedKeyTransp",
                                    e, "No such padding: " + algorithmURI
                            );
                        } catch (NoSuchAlgorithmException e) {
                            throw new XMLSecurityException(
                                    XMLSecurityException.ErrorCode.UNSUPPORTED_ALGORITHM, "unsupportedKeyTransp",
                                    e, "No such algorithm: " + algorithmURI
                            );
                        } catch (InvalidKeyException e) {
                            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_CHECK, e);
                        } catch (NoSuchProviderException e) {
                            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILURE, "noSecProvider", e);
                        }
                    }
                };
                this.securityToken.setElementPath(responsibleXMLSecStartXMLEvent.getElementPath());
                this.securityToken.setXMLSecEvent(responsibleXMLSecStartXMLEvent);
                return this.securityToken;
            }

            @Override
            public String getId() {
                return encryptedKeyType.getId();
            }
        };

        //register the key token for decryption:
        securityContext.registerSecurityTokenProvider(encryptedKeyType.getId(), securityTokenProvider);

        //fire a tokenSecurityEvent
        TokenSecurityEvent tokenSecurityEvent = new EncryptedKeyTokenSecurityEvent();
        tokenSecurityEvent.setSecurityToken((SecurityToken)securityTokenProvider.getSecurityToken());
        tokenSecurityEvent.setCorrelationID(encryptedKeyType.getId());
        securityContext.registerSecurityEvent(tokenSecurityEvent);

        //if this EncryptedKey structure contains a reference list, delegate it to a subclass
        if (encryptedKeyType.getReferenceList() != null) {
            handleReferenceList(inputProcessorChain, encryptedKeyType, securityProperties);
        }
    }

    protected void checkBSPCompliance(InputProcessorChain inputProcessorChain, EncryptedKeyType encryptedKeyType)
            throws XMLSecurityException {
        // do nothing
    }
    
    protected void handleReferenceList(final InputProcessorChain inputProcessorChain, 
            final EncryptedKeyType encryptedKeyType,
            final XMLSecurityProperties securityProperties) throws XMLSecurityException {
        // do nothing
    }

    /*
    <xenc:EncryptedKey xmlns:xenc="http://www.w3.org/2001/04/xmlenc#" Id="EncKeyId-1483925398">
        <xenc:EncryptionMethod Algorithm="http://www.w3.org/2001/04/xmlenc#rsa-1_5" />
        <ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
            <wsse:SecurityTokenReference xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
                <wsse:KeyIdentifier EncodingType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary"
                    ValueType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509SubjectKeyIdentifier">pHoiKNGY2YsLBKxwIV+jURt858M=</wsse:KeyIdentifier>
                </wsse:SecurityTokenReference>
        </ds:KeyInfo>
        <xenc:CipherData>
            <xenc:CipherValue>Khsa9SN3ALNXOgGDKOqihvfwGsXb9QN/q4Fpi9uuThgz+3D4oRSMkrGSPCqwG13vddvHywGAA/XNbWNT+5Xivz3lURCDCc2H/92YlXXo/crQNJnPlLrLZ81bGOzbNo7lnYQBLp/77K7b1bhldZAeV9ZfEW7DjbOMZ+k1dnDCu3A=</xenc:CipherValue>
        </xenc:CipherData>
        <xenc:ReferenceList>
            <xenc:DataReference URI="#EncDataId-1612925417" />
        </xenc:ReferenceList>
    </xenc:EncryptedKey>
     */
}
