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

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.AbstractInputProcessor;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.util.ArrayDeque;

/**
 * Processor for XML Security.
 *
 * @author $Author: coheigea $
 * @version $Revision: 1354898 $ $Date: 2012-06-28 11:19:02 +0100 (Thu, 28 Jun 2012) $
 */
public class XMLSecurityInputProcessor extends AbstractInputProcessor {

    private int startIndexForProcessor = 0;
    private InternalBufferProcessor internalBufferProcessor;
    private boolean signatureElementFound = false;
    private boolean encryptedDataElementFound = false;

    public XMLSecurityInputProcessor(XMLSecurityProperties securityProperties) {
        super(securityProperties);
        setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
    }

    @Override
    public XMLSecEvent processNextHeaderEvent(InputProcessorChain inputProcessorChain)
            throws XMLStreamException, XMLSecurityException {
        return null;
    }

    @Override
    public XMLSecEvent processNextEvent(InputProcessorChain inputProcessorChain)
            throws XMLStreamException, XMLSecurityException {

        //add the buffer processor (for signature) when this processor is called for the first time
        if (internalBufferProcessor == null) {
            internalBufferProcessor = new InternalBufferProcessor(getSecurityProperties());
            inputProcessorChain.addProcessor(internalBufferProcessor);
        }

        XMLSecEvent xmlSecEvent = inputProcessorChain.processEvent();
        switch (xmlSecEvent.getEventType()) {
            case XMLStreamConstants.START_ELEMENT:
                final XMLSecStartElement xmlSecStartElement = xmlSecEvent.asStartElement();

                if (xmlSecStartElement.getName().equals(XMLSecurityConstants.TAG_dsig_Signature)) {
                    signatureElementFound = true;
                    startIndexForProcessor = internalBufferProcessor.getXmlSecEventList().size() - 1;
                } else if (xmlSecStartElement.getName().equals(XMLSecurityConstants.TAG_xenc_EncryptedData)) {
                    encryptedDataElementFound = true;

                    XMLDecryptInputProcessor decryptInputProcessor = new XMLDecryptInputProcessor(getSecurityProperties());
                    decryptInputProcessor.setPhase(XMLSecurityConstants.Phase.PREPROCESSING);
                    decryptInputProcessor.addAfterProcessor(XMLEventReaderInputProcessor.class.getName());
                    decryptInputProcessor.addBeforeProcessor(XMLSecurityInputProcessor.class.getName());
                    decryptInputProcessor.addBeforeProcessor(XMLSecurityInputProcessor.InternalBufferProcessor.class.getName());
                    inputProcessorChain.addProcessor(decryptInputProcessor);

                    final ArrayDeque<XMLSecEvent> xmlSecEventList = internalBufferProcessor.getXmlSecEventList();
                    //remove the last event (EncryptedData)
                    xmlSecEventList.pollFirst();

                    // temporary processor to return the EncryptedData element for the DecryptionProcessor
                    AbstractInputProcessor abstractInputProcessor = new AbstractInputProcessor(getSecurityProperties()) {
                        @Override
                        public XMLSecEvent processNextHeaderEvent(InputProcessorChain inputProcessorChain)
                                throws XMLStreamException, XMLSecurityException {
                            return processNextEvent(inputProcessorChain);
                        }

                        @Override
                        public XMLSecEvent processNextEvent(InputProcessorChain inputProcessorChain)
                                throws XMLStreamException, XMLSecurityException {
                            inputProcessorChain.removeProcessor(this);
                            return xmlSecStartElement;
                        }
                    };
                    abstractInputProcessor.setPhase(XMLSecurityConstants.Phase.PREPROCESSING);
                    abstractInputProcessor.addBeforeProcessor(decryptInputProcessor);
                    inputProcessorChain.addProcessor(abstractInputProcessor);

                    //fetch the next event from the original chain
                    inputProcessorChain.reset();
                    xmlSecEvent = inputProcessorChain.processEvent();

                    //check if the decrypted element is a Signature element
                    if (xmlSecEvent.isStartElement() &&
                            xmlSecEvent.asStartElement().getName().equals(XMLSecurityConstants.TAG_dsig_Signature)) {
                        signatureElementFound = true;
                        startIndexForProcessor = internalBufferProcessor.getXmlSecEventList().size() - 1;
                    }
                }
                break;
            case XMLStreamConstants.END_ELEMENT:
                XMLSecEndElement xmlSecEndElement = xmlSecEvent.asEndElement();
                // Handle the signature
                if (signatureElementFound
                        && xmlSecEndElement.getName().equals(XMLSecurityConstants.TAG_dsig_Signature)) {
                    XMLSignatureInputHandler inputHandler = new XMLSignatureInputHandler();

                    final ArrayDeque<XMLSecEvent> xmlSecEventList = internalBufferProcessor.getXmlSecEventList();
                    inputHandler.handle(inputProcessorChain, getSecurityProperties(),
                            xmlSecEventList, startIndexForProcessor);

                    inputProcessorChain.removeProcessor(internalBufferProcessor);

                    //add the replay processor to the chain...
                    InternalReplayProcessor internalReplayProcessor =
                            new InternalReplayProcessor(getSecurityProperties(), xmlSecEventList);
                    internalReplayProcessor.addBeforeProcessor(XMLSignatureReferenceVerifyInputProcessor.class.getName());
                    inputProcessorChain.addProcessor(internalReplayProcessor);

                    //...and let the SignatureVerificationProcessor process the buffered events (enveloped signature).
                    InputProcessorChain subInputProcessorChain = inputProcessorChain.createSubChain(this);
                    while (!xmlSecEventList.isEmpty()) {
                        subInputProcessorChain.reset();
                        subInputProcessorChain.processEvent();
                    }
                }
                break;
        }

        return xmlSecEvent;
    }

    @Override
    public void doFinal(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
        if (!signatureElementFound && !encryptedDataElementFound) {
            throw new XMLSecurityException("stax.unsecuredMessage");
        }
        super.doFinal(inputProcessorChain);
    }

    /**
     * Temporary Processor to buffer all events until the end of the required actions
     */
    public class InternalBufferProcessor extends AbstractInputProcessor {

        private final ArrayDeque<XMLSecEvent> xmlSecEventList = new ArrayDeque<XMLSecEvent>();

        InternalBufferProcessor(XMLSecurityProperties securityProperties) {
            super(securityProperties);
            setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
            addBeforeProcessor(XMLSecurityInputProcessor.class.getName());
        }

        public ArrayDeque<XMLSecEvent> getXmlSecEventList() {
            return xmlSecEventList;
        }

        @Override
        public XMLSecEvent processNextHeaderEvent(InputProcessorChain inputProcessorChain)
                throws XMLStreamException, XMLSecurityException {
            return null;
        }

        @Override
        public XMLSecEvent processNextEvent(InputProcessorChain inputProcessorChain)
                throws XMLStreamException, XMLSecurityException {
            XMLSecEvent xmlSecEvent = inputProcessorChain.processEvent();
            xmlSecEventList.push(xmlSecEvent);
            return xmlSecEvent;
        }
    }

    /**
     * Temporary processor to replay the buffered events
     */
    public class InternalReplayProcessor extends AbstractInputProcessor {

        private final ArrayDeque<XMLSecEvent> xmlSecEventList;

        public InternalReplayProcessor(XMLSecurityProperties securityProperties, ArrayDeque<XMLSecEvent> xmlSecEventList) {
            super(securityProperties);
            this.xmlSecEventList = xmlSecEventList;
        }

        @Override
        public XMLSecEvent processNextHeaderEvent(InputProcessorChain inputProcessorChain)
                throws XMLStreamException, XMLSecurityException {
            return null;
        }

        @Override
        public XMLSecEvent processNextEvent(InputProcessorChain inputProcessorChain)
                throws XMLStreamException, XMLSecurityException {

            if (!xmlSecEventList.isEmpty()) {
                return xmlSecEventList.pollLast();
            } else {
                inputProcessorChain.removeProcessor(this);
                return inputProcessorChain.processEvent();
            }
        }
    }
}
