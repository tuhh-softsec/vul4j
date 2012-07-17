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
package org.apache.xml.security.stax.impl.processor.output;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.SecurityTokenProvider;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.EncryptionPartDef;
import org.apache.xml.security.stax.impl.util.IDGenerator;

/**
 * Processor to encrypt XML structures
 *
 * @author $Author: coheigea $
 * @version $Revision: 1236690 $ $Date: 2012-01-27 14:07:10 +0000 (Fri, 27 Jan 2012) $
 */
public class XMLEncryptOutputProcessor extends AbstractEncryptOutputProcessor {

    private static final transient Log logger = LogFactory.getLog(XMLEncryptOutputProcessor.class);
    
    public XMLEncryptOutputProcessor() throws XMLSecurityException {
        super();
    }
    
    @Override
    public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        if (xmlSecEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
            XMLSecStartElement xmlSecStartElement = xmlSecEvent.asStartElement();

            //avoid double encryption when child elements matches too
            if (getActiveInternalEncryptionOutputProcessor() == null) {
                SecurePart securePart = securePartMatches(xmlSecStartElement, outputProcessorChain, XMLSecurityConstants.ENCRYPTION_PARTS);
                if (securePart != null) {
                    logger.debug("Matched encryptionPart for encryption");
                    AbstractInternalEncryptionOutputProcessor internalEncryptionOutputProcessor;
                    try {
                        String tokenId = outputProcessorChain.getSecurityContext().get(XMLSecurityConstants.PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTION);
                        SecurityTokenProvider securityTokenProvider = outputProcessorChain.getSecurityContext().getSecurityTokenProvider(tokenId);
                        EncryptionPartDef encryptionPartDef = new EncryptionPartDef();
                        encryptionPartDef.setModifier(securePart.getModifier());
                        encryptionPartDef.setEncRefId(IDGenerator.generateID(null));
                        encryptionPartDef.setKeyId(securityTokenProvider.getId());
                        encryptionPartDef.setSymmetricKey(securityTokenProvider.getSecurityToken().getSecretKey(getSecurityProperties().getEncryptionSymAlgorithm(), null));
                        outputProcessorChain.getSecurityContext().putAsList(EncryptionPartDef.class, encryptionPartDef);
                        
                        internalEncryptionOutputProcessor =
                                createInternalEncryptionOutputProcessor(
                                        encryptionPartDef, xmlSecStartElement, 
                                        outputProcessorChain.getDocumentContext().getEncoding()
                                );
                        internalEncryptionOutputProcessor.setXMLSecurityProperties(getSecurityProperties());
                        internalEncryptionOutputProcessor.setAction(getAction());
                        internalEncryptionOutputProcessor.init(outputProcessorChain);
                        
                    } catch (IOException e) {
                        throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_ENCRYPTION, e);
                    }

                    setActiveInternalEncryptionOutputProcessor(internalEncryptionOutputProcessor);
                }
            }
        }

        outputProcessorChain.processEvent(xmlSecEvent);
    }
    
    /**
     * Override this method to return a different AbstractInternalEncryptionOutputProcessor instance
     * which will write out the KeyInfo contents in the EncryptedData.
     */
    protected AbstractInternalEncryptionOutputProcessor createInternalEncryptionOutputProcessor(
        EncryptionPartDef encryptionPartDef,
        XMLSecStartElement startElement,
        String encoding
    ) throws XMLStreamException, XMLSecurityException {
        try {
            final AbstractInternalEncryptionOutputProcessor processor = 
                    new AbstractInternalEncryptionOutputProcessor(encryptionPartDef,
                                        startElement,
                                        encoding) {
                /*
                 * Do not write out a KeyInfo Element (for now)
                 */
                @Override
                protected void createKeyInfoStructure(OutputProcessorChain outputProcessorChain)
                        throws XMLStreamException, XMLSecurityException {
                    //createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo, true, null);
                    //createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo);
                }
            };
            return processor;
        } catch (NoSuchAlgorithmException e) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_ENCRYPTION, e);
        } catch (NoSuchPaddingException e) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_ENCRYPTION, e);
        } catch (InvalidKeyException e) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_ENCRYPTION, e);
        } catch (IOException e) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_ENCRYPTION, e);
        }
    }
    

}
