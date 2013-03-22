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
package org.apache.xml.security.stax.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.*;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a OutputProcessorChain
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class OutputProcessorChainImpl implements OutputProcessorChain {

    protected static final transient Logger log = LoggerFactory.getLogger(OutputProcessorChainImpl.class);
    protected static final transient boolean isDebugEnabled = log.isDebugEnabled();

    private List<OutputProcessor> outputProcessors;
    private int startPos = 0;
    private int curPos = 0;
    private XMLSecStartElement parentXmlSecStartElement;

    private final SecurityContext securityContext;
    private final DocumentContextImpl documentContext;

    public OutputProcessorChainImpl(SecurityContext securityContext) {
        this(securityContext, 0);
    }

    public OutputProcessorChainImpl(SecurityContext securityContext, int startPos) {
        this(securityContext, new DocumentContextImpl(), startPos, new ArrayList<OutputProcessor>(20));
    }

    public OutputProcessorChainImpl(SecurityContext securityContext, DocumentContextImpl documentContext) {
        this(securityContext, documentContext, 0, new ArrayList<OutputProcessor>(20));
    }

    protected OutputProcessorChainImpl(SecurityContext securityContext, DocumentContextImpl documentContextImpl,
                                       int startPos, List<OutputProcessor> outputProcessors) {
        this.securityContext = securityContext;
        this.curPos = this.startPos = startPos;
        documentContext = documentContextImpl;
        this.outputProcessors = outputProcessors;
    }

    @Override
    public void reset() {
        this.curPos = startPos;
    }

    @Override
    public SecurityContext getSecurityContext() {
        return this.securityContext;
    }

    @Override
    public DocumentContext getDocumentContext() {
        return this.documentContext;
    }

    @Override
    public void addProcessor(OutputProcessor newOutputProcessor) {
        int startPhaseIdx = 0;
        int endPhaseIdx = outputProcessors.size();
        int idxToInsert = endPhaseIdx;
        XMLSecurityConstants.Phase targetPhase = newOutputProcessor.getPhase();

        for (int i = outputProcessors.size() - 1; i >= 0; i--) {
            OutputProcessor outputProcessor = outputProcessors.get(i);
            if (outputProcessor.getPhase().ordinal() < targetPhase.ordinal()) {
                startPhaseIdx = i + 1;
                break;
            }
        }
        for (int i = startPhaseIdx; i < outputProcessors.size(); i++) {
            OutputProcessor outputProcessor = outputProcessors.get(i);
            if (outputProcessor.getPhase().ordinal() > targetPhase.ordinal()) {
                endPhaseIdx = i;
                break;
            }
        }

        //just look for the correct phase and append as last
        if (newOutputProcessor.getBeforeProcessors().isEmpty()
                && newOutputProcessor.getAfterProcessors().isEmpty()) {
            outputProcessors.add(endPhaseIdx, newOutputProcessor);
        } else if (newOutputProcessor.getBeforeProcessors().isEmpty()) {
            idxToInsert = endPhaseIdx;

            for (int i = endPhaseIdx - 1; i >= startPhaseIdx; i--) {
                OutputProcessor outputProcessor = outputProcessors.get(i);
                if (newOutputProcessor.getAfterProcessors().contains(outputProcessor)
                        || newOutputProcessor.getAfterProcessors().contains(outputProcessor.getClass().getName())) {
                    idxToInsert = i + 1;
                    break;
                }
            }
            outputProcessors.add(idxToInsert, newOutputProcessor);
        } else if (newOutputProcessor.getAfterProcessors().isEmpty()) {
            idxToInsert = startPhaseIdx;

            for (int i = startPhaseIdx; i < endPhaseIdx; i++) {
                OutputProcessor outputProcessor = outputProcessors.get(i);
                if (newOutputProcessor.getBeforeProcessors().contains(outputProcessor)
                        || newOutputProcessor.getBeforeProcessors().contains(outputProcessor.getClass().getName())) {
                    idxToInsert = i;
                    break;
                }
            }
            outputProcessors.add(idxToInsert, newOutputProcessor);
        } else {
            boolean found = false;
            idxToInsert = endPhaseIdx;

            for (int i = startPhaseIdx; i < endPhaseIdx; i++) {
                OutputProcessor outputProcessor = outputProcessors.get(i);
                if (newOutputProcessor.getBeforeProcessors().contains(outputProcessor)
                        || newOutputProcessor.getBeforeProcessors().contains(outputProcessor.getClass().getName())) {
                    idxToInsert = i;
                    found = true;
                    break;
                }
            }
            if (found) {
                outputProcessors.add(idxToInsert, newOutputProcessor);
            } else {
                for (int i = endPhaseIdx - 1; i >= startPhaseIdx; i--) {
                    OutputProcessor outputProcessor = outputProcessors.get(i);
                    if (newOutputProcessor.getAfterProcessors().contains(outputProcessor)
                            || newOutputProcessor.getAfterProcessors().contains(outputProcessor.getClass().getName())) {
                        idxToInsert = i + 1;
                        break;
                    }
                }
                outputProcessors.add(idxToInsert, newOutputProcessor);
            }
        }
        if (idxToInsert < this.curPos) {
            this.curPos++;
        }
        if (isDebugEnabled) {
            log.debug("Added " + newOutputProcessor.getClass().getName() + " to output chain: ");
            for (int i = 0; i < outputProcessors.size(); i++) {
                OutputProcessor outputProcessor = outputProcessors.get(i);
                log.debug("Name: " + outputProcessor.getClass().getName() + " phase: " + outputProcessor.getPhase());
            }
        }
    }

    @Override
    public void removeProcessor(OutputProcessor outputProcessor) {
        if (isDebugEnabled) {
            log.debug("Removing processor " + outputProcessor.getClass().getName() + " from output chain");
        }
        if (this.outputProcessors.indexOf(outputProcessor) <= this.curPos) {
            this.curPos--;
        }
        this.outputProcessors.remove(outputProcessor);
    }

    @Override
    public List<OutputProcessor> getProcessors() {
        return this.outputProcessors;
    }

    private void setParentXmlSecStartElement(XMLSecStartElement xmlSecStartElement) {
        this.parentXmlSecStartElement = xmlSecStartElement;
    }

    @Override
    public void processEvent(XMLSecEvent xmlSecEvent) throws XMLStreamException, XMLSecurityException {
        boolean reparent = false;
        if (this.curPos == this.startPos) {
            switch (xmlSecEvent.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    if (xmlSecEvent == parentXmlSecStartElement) {
                        parentXmlSecStartElement = null;
                    }
                    xmlSecEvent.setParentXMLSecStartElement(parentXmlSecStartElement);
                    parentXmlSecStartElement = xmlSecEvent.asStartElement();
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    xmlSecEvent.setParentXMLSecStartElement(parentXmlSecStartElement);
                    reparent = true;
                    break;
                default:
                    xmlSecEvent.setParentXMLSecStartElement(parentXmlSecStartElement);
                    break;
            }
        }
        outputProcessors.get(this.curPos++).processNextEvent(xmlSecEvent, this);
        if (reparent && parentXmlSecStartElement != null) {
            parentXmlSecStartElement = parentXmlSecStartElement.getParentXMLSecStartElement();
        }
    }

    @Override
    public void doFinal() throws XMLStreamException, XMLSecurityException {
        outputProcessors.get(this.curPos++).doFinal(this);
    }

    @Override
    public OutputProcessorChain createSubChain(OutputProcessor outputProcessor) throws XMLStreamException, XMLSecurityException {
        return createSubChain(outputProcessor, null);
    }

    @Override
    public OutputProcessorChain createSubChain(OutputProcessor outputProcessor, XMLSecStartElement parentXMLSecStartElement) throws XMLStreamException, XMLSecurityException {
        //we don't clone the processor-list to get updates in the sublist too!
        OutputProcessorChainImpl outputProcessorChain;
        try {
            outputProcessorChain = new OutputProcessorChainImpl(securityContext, documentContext.clone(),
                    outputProcessors.indexOf(outputProcessor) + 1, this.outputProcessors);
        } catch (CloneNotSupportedException e) {
            throw new XMLSecurityException(e);
        }
        if (parentXMLSecStartElement != null) {
            outputProcessorChain.setParentXmlSecStartElement(parentXMLSecStartElement);
        } else {
            outputProcessorChain.setParentXmlSecStartElement(this.parentXmlSecStartElement);
        }
        return outputProcessorChain;
    }
}
