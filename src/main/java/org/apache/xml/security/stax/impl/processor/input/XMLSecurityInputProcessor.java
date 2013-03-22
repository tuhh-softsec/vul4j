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

import java.util.ArrayDeque;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.AbstractInputProcessor;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/**
 * Processor for XML Security.
 *
 * @author $Author: coheigea $
 * @version $Revision: 1354898 $ $Date: 2012-06-28 11:19:02 +0100 (Thu, 28 Jun 2012) $
 */
public class XMLSecurityInputProcessor extends AbstractInputProcessor {

    protected static final transient Logger logger = LoggerFactory.getLogger(XMLSecurityInputProcessor.class);

    private final ArrayDeque<XMLSecEvent> xmlSecEventList = new ArrayDeque<XMLSecEvent>();
    private int eventCount = 0;
    private int startIndexForProcessor = 0;

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

        //buffer all events until the end of the required actions
        final InputProcessorChain subInputProcessorChain = inputProcessorChain.createSubChain(this);
        final InternalBufferProcessor internalBufferProcessor
                = new InternalBufferProcessor(getSecurityProperties());
        subInputProcessorChain.addProcessor(internalBufferProcessor);

        boolean signatureElementFound = false;

        XMLSecEvent xmlSecEvent;
        do {
            subInputProcessorChain.reset();
            xmlSecEvent = subInputProcessorChain.processHeaderEvent();
            eventCount++;

            switch (xmlSecEvent.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    final XMLSecStartElement xmlSecStartElement = xmlSecEvent.asStartElement();

                    if (xmlSecStartElement.getName().equals(XMLSecurityConstants.TAG_dsig_Signature)) {
                        signatureElementFound = true;
                        startIndexForProcessor = eventCount - 1;
                    } else if (xmlSecStartElement.getName().equals(XMLSecurityConstants.TAG_xenc_EncryptedData)) {
                        XMLDecryptInputProcessor inputProcessor = new XMLDecryptInputProcessor(getSecurityProperties());
                        subInputProcessorChain.addProcessor(inputProcessor);

                        subInputProcessorChain.removeProcessor(internalBufferProcessor);
                        InternalReplayProcessor internalReplayProcessor = new InternalReplayProcessor(getSecurityProperties());
                        internalReplayProcessor.setPhase(XMLSecurityConstants.Phase.PROCESSING);
                        internalReplayProcessor.getAfterProcessors().clear();
                        internalReplayProcessor.getBeforeProcessors().clear();
                        internalReplayProcessor.addAfterProcessor(XMLDecryptInputProcessor.class.getName());
                        subInputProcessorChain.addProcessor(internalReplayProcessor);

                        AbstractInputProcessor abstractInputProcessor = new AbstractInputProcessor(getSecurityProperties()) {
                            @Override
                            public XMLSecEvent processNextHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
                                return null;
                            }

                            @Override
                            public XMLSecEvent processNextEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
                                inputProcessorChain.removeProcessor(this);
                                return xmlSecStartElement;
                            }
                        };
                        abstractInputProcessor.setPhase(XMLSecurityConstants.Phase.PREPROCESSING);
                        abstractInputProcessor.addBeforeProcessor(XMLSecurityInputProcessor.class.getName());
                        abstractInputProcessor.addAfterProcessor(XMLEventReaderInputProcessor.class.getName());
                        subInputProcessorChain.addProcessor(abstractInputProcessor);

                        //remove this processor from chain now. the next events will go directly to the other processors
                        subInputProcessorChain.removeProcessor(this);
                        //since we cloned the inputProcessor list we have to add the processors from
                        //the subChain to the main chain.
                        inputProcessorChain.getProcessors().clear();
                        inputProcessorChain.getProcessors().addAll(subInputProcessorChain.getProcessors());

                        //remove the last event which will be emitted in the temporary processor above:
                        xmlSecEventList.pollFirst();
                        //return first event now;
                        return xmlSecEventList.pollLast();
                    } 
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    XMLSecEndElement xmlSecEndElement = xmlSecEvent.asEndElement();
                    // Handle the signature
                    if (signatureElementFound
                            && xmlSecEndElement.getName().equals(XMLSecurityConstants.TAG_dsig_Signature)) {
                            XMLSignatureInputHandler inputHandler = new XMLSignatureInputHandler();
                            inputHandler.handle(subInputProcessorChain, getSecurityProperties(), 
                                                xmlSecEventList, startIndexForProcessor);

                        subInputProcessorChain.removeProcessor(internalBufferProcessor);
                        subInputProcessorChain.addProcessor(
                                new InternalReplayProcessor(getSecurityProperties()));

                        //remove this processor from chain now. the next events will go directly to the other processors
                        subInputProcessorChain.removeProcessor(this);
                        //since we cloned the inputProcessor list we have to add the processors from
                        //the subChain to the main chain.
                        inputProcessorChain.getProcessors().clear();
                        inputProcessorChain.getProcessors().addAll(subInputProcessorChain.getProcessors());

                        //return first event now;
                        return xmlSecEventList.pollLast();
                    }
                    break;
            }

        } while (!xmlSecEvent.isEndDocument());
        //if we reach this state we didn't find a signature nor a encryptedData Element
        throw new XMLSecurityException("stax.unsecuredMessage");
    }

    /**
     * Temporary Processor to buffer all events until the end of the required actions
     */
    public class InternalBufferProcessor extends AbstractInputProcessor {

        InternalBufferProcessor(XMLSecurityProperties securityProperties) {
            super(securityProperties);
            setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
            addBeforeProcessor(XMLSecurityInputProcessor.class.getName());
        }

        @Override
        public XMLSecEvent processNextHeaderEvent(InputProcessorChain inputProcessorChain)
                throws XMLStreamException, XMLSecurityException {
            XMLSecEvent xmlSecEvent = inputProcessorChain.processHeaderEvent();
            xmlSecEventList.push(xmlSecEvent);
            return xmlSecEvent;
        }

        @Override
        public XMLSecEvent processNextEvent(InputProcessorChain inputProcessorChain)
                throws XMLStreamException, XMLSecurityException {
            //should never be called because we remove this processor before
            return null;
        }
    }

    /**
     * Temporary processor to replay the buffered events
     */
    public class InternalReplayProcessor extends AbstractInputProcessor {

        public InternalReplayProcessor(XMLSecurityProperties securityProperties) {
            super(securityProperties);
            setPhase(XMLSecurityConstants.Phase.PREPROCESSING);
            addBeforeProcessor(XMLSecurityInputProcessor.class.getName());
            addAfterProcessor(XMLEventReaderInputProcessor.class.getName());
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
