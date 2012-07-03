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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.stax.ext.AbstractInputProcessor;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/**
 * Processor for XML Signature.
 *
 * @author $Author: coheigea $
 * @version $Revision: 1354898 $ $Date: 2012-06-28 11:19:02 +0100 (Thu, 28 Jun 2012) $
 */
public class XMLSignatureInputProcessor extends AbstractInputProcessor {

    protected static final transient Log logger = LogFactory.getLog(XMLSignatureInputProcessor.class);

    private final ArrayDeque<XMLSecEvent> xmlSecEventList = new ArrayDeque<XMLSecEvent>();
    private int eventCount = 0;
    private int startIndexForProcessor = 0;

    public XMLSignatureInputProcessor(XMLSecurityProperties securityProperties) {
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

        boolean elementFound = false;

        XMLSecEvent xmlSecEvent;
        do {
            subInputProcessorChain.reset();
            xmlSecEvent = subInputProcessorChain.processHeaderEvent();
            eventCount++;

            switch (xmlSecEvent.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    XMLSecStartElement xmlSecStartElement = xmlSecEvent.asStartElement();

                    if (xmlSecStartElement.getName().equals(XMLSecurityConstants.TAG_dsig_Signature)) {
                        elementFound = true;
                        startIndexForProcessor = eventCount - 1;
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    XMLSecEndElement xmlSecEndElement = xmlSecEvent.asEndElement();
                    if (elementFound
                            && xmlSecEndElement.getName().equals(XMLSecurityConstants.TAG_dsig_Signature)) {
                        // Handle the signature
                        XMLSignatureInputHandler inputHandler = new XMLSignatureInputHandler();
                        inputHandler.handle(inputProcessorChain, getSecurityProperties(), 
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
        //if we reach this state we didn't find a signature
        throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILURE, "missingSignature");
    }

    /**
     * Temporary Processor to buffer all events until the end of the required actions
     */
    public class InternalBufferProcessor extends AbstractInputProcessor {

        InternalBufferProcessor(XMLSecurityProperties securityProperties) {
            super(securityProperties);
            setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
            addBeforeProcessor(XMLSignatureInputProcessor.class.getName());
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
            addBeforeProcessor(XMLSignatureInputProcessor.class.getName());
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
