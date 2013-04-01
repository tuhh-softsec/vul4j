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
package org.apache.xml.security.stax.ext;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

import javax.xml.stream.XMLStreamException;
import java.util.List;

/**
 * The OutputProcessorChain manages the OutputProcessors and controls the XMLEvent flow
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public interface OutputProcessorChain extends ProcessorChain {

    /**
     * Adds an OutputProcessor to the chain. The place where it
     * will be applied can be controlled through the Phase,
     * getBeforeProcessors and getAfterProcessors. @see Interface OutputProcessor
     *
     * @param outputProcessor The OutputProcessor which should be placed in the chain
     */
    void addProcessor(OutputProcessor outputProcessor);

    /**
     * Removes the specified OutputProcessor from this chain.
     *
     * @param outputProcessor to remove
     */
    void removeProcessor(OutputProcessor outputProcessor);

    /**
     * Returns a list with the active processors.
     *
     * @return List<InputProcessor>
     */
    List<OutputProcessor> getProcessors();

    /**
     * The actual processed document's security context
     *
     * @return The InboundSecurityContext
     */
    OutboundSecurityContext getSecurityContext();

    /**
     * The actual processed document's document context
     *
     * @return The DocumentContext
     */
    DocumentContext getDocumentContext();

    /**
     * Create a new SubChain. The XMLEvents will be only be processed from the given OutputProcessor to the end.
     * All earlier OutputProcessors don't get these events. In other words the chain will be splitted in two parts.
     *
     * @param outputProcessor The OutputProcessor position the XMLEvents should be processed over this SubChain.
     * @return A new OutputProcessorChain
     * @throws XMLStreamException   thrown when a streaming error occurs
     * @throws XMLSecurityException thrown when a Security failure occurs
     */
    OutputProcessorChain createSubChain(OutputProcessor outputProcessor) throws XMLStreamException, XMLSecurityException;

    OutputProcessorChain createSubChain(OutputProcessor outputProcessor, XMLSecStartElement parentXMLSecStartElement) throws XMLStreamException, XMLSecurityException;

    /**
     * Forwards the XMLEvent to the next processor in the chain.
     *
     * @param xmlSecEvent The XMLEvent which should be forwarded to the next processor
     * @throws XMLStreamException   thrown when a streaming error occurs
     * @throws XMLSecurityException thrown when a Security failure occurs
     */
    void processEvent(XMLSecEvent xmlSecEvent) throws XMLStreamException, XMLSecurityException;
}