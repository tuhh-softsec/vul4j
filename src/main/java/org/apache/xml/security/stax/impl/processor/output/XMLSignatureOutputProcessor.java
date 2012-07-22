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

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.SignaturePartDef;
import org.apache.xml.security.stax.impl.util.IDGenerator;

/**
 * An OutputProcessor for XML Signature.
 */
public class XMLSignatureOutputProcessor extends AbstractSignatureOutputProcessor {
    
    private static final transient Log logger = LogFactory.getLog(XMLSignatureOutputProcessor.class);

    public XMLSignatureOutputProcessor() throws XMLSecurityException {
        super();
    }
    
    @Override
    public void init(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        super.init(outputProcessorChain);
        XMLSignatureEndingOutputProcessor signatureEndingOutputProcessor = new XMLSignatureEndingOutputProcessor(this);
        signatureEndingOutputProcessor.setXMLSecurityProperties(getSecurityProperties());
        signatureEndingOutputProcessor.setAction(getAction());
        signatureEndingOutputProcessor.init(outputProcessorChain);
    }

    @Override
    public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        if (xmlSecEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
            XMLSecStartElement xmlSecStartElement = xmlSecEvent.asStartElement();

            //avoid double signature when child elements matches too
            if (getActiveInternalSignatureOutputProcessor() == null) {
                SecurePart securePart = securePartMatches(xmlSecStartElement, outputProcessorChain, XMLSecurityConstants.SIGNATURE_PARTS);
                if (securePart != null) {
                    logger.debug("Matched securePart for signature");

                    InternalSignatureOutputProcessor internalSignatureOutputProcessor = null;
                    try {
                        SignaturePartDef signaturePartDef = new SignaturePartDef();
                        signaturePartDef.setTransforms(securePart.getTransforms());
                        String digestMethod = securePart.getDigestMethod();
                        if (digestMethod == null) {
                            digestMethod = getSecurityProperties().getSignatureDigestAlgorithm();
                        }
                        signaturePartDef.setDigestAlgo(digestMethod);

                        if (securePart.getIdToSign() == null) {
                            signaturePartDef.setGenerateXPointer(securePart.isGenerateXPointer());
                            signaturePartDef.setSigRefId(IDGenerator.generateID(null));

                            Attribute attribute = xmlSecStartElement.getAttributeByName(XMLSecurityConstants.ATT_NULL_Id);
                            if (attribute != null) {
                                signaturePartDef.setSigRefId(attribute.getValue());
                            } else {
                                List<XMLSecAttribute> attributeList = new ArrayList<XMLSecAttribute>(1);
                                attributeList.add(createAttribute(XMLSecurityConstants.ATT_NULL_Id, signaturePartDef.getSigRefId()));
                                xmlSecEvent = addAttributes(xmlSecStartElement, attributeList);
                            }
                            String signatureAppendId = 
                                    outputProcessorChain.getSecurityContext().get(
                                            XMLSecurityConstants.PROP_APPEND_SIGNATURE_ON_THIS_ID);
                            if (signatureAppendId == null || "".equals(signatureAppendId)) {
                                outputProcessorChain.getSecurityContext().put(
                                    XMLSecurityConstants.PROP_APPEND_SIGNATURE_ON_THIS_ID, 
                                    signaturePartDef.getSigRefId()
                                );
                            }
                        } else {
                            signaturePartDef.setSigRefId(securePart.getIdToSign());
                            String signatureAppendId =
                                    outputProcessorChain.getSecurityContext().get(
                                            XMLSecurityConstants.PROP_APPEND_SIGNATURE_ON_THIS_ID);
                            if (signatureAppendId == null || "".equals(signatureAppendId)) {
                                outputProcessorChain.getSecurityContext().put(
                                    XMLSecurityConstants.PROP_APPEND_SIGNATURE_ON_THIS_ID, 
                                    securePart.getIdToSign()
                                );
                            }
                        }

                        getSignaturePartDefList().add(signaturePartDef);
                        internalSignatureOutputProcessor = new InternalSignatureOutputProcessor(signaturePartDef, xmlSecStartElement.getName());
                        internalSignatureOutputProcessor.setXMLSecurityProperties(getSecurityProperties());
                        internalSignatureOutputProcessor.setAction(getAction());
                        internalSignatureOutputProcessor.addAfterProcessor(XMLSignatureOutputProcessor.class.getName());
                        internalSignatureOutputProcessor.addBeforeProcessor(XMLSignatureEndingOutputProcessor.class.getName());
                        internalSignatureOutputProcessor.init(outputProcessorChain);

                    } catch (NoSuchAlgorithmException e) {
                        throw new XMLSecurityException(
                                XMLSecurityException.ErrorCode.UNSUPPORTED_ALGORITHM, "unsupportedKeyTransp",
                                e, "No such algorithm: " + getSecurityProperties().getSignatureAlgorithm()
                        );
                    } catch (NoSuchProviderException e) {
                        throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILURE, "noSecProvider", e);
                    }

                    setActiveInternalSignatureOutputProcessor(internalSignatureOutputProcessor);
                }
            }
        }
        outputProcessorChain.processEvent(xmlSecEvent);
    }

}
