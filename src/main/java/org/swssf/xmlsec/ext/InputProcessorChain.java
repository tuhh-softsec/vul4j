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
package org.swssf.xmlsec.ext;

import org.swssf.xmlsec.ext.stax.XMLSecEvent;

import javax.xml.stream.XMLStreamException;
import java.util.List;

/**
 * The InputProcessorChain manages the InputProcessors and controls the XMLEvent flow
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public interface InputProcessorChain extends ProcessorChain {

    /**
     * Adds an InputProcessor to the chain. The place where it
     * will be applied can be controlled through the Phase,
     * getBeforeProcessors and getAfterProcessors. @see Interface InputProcessor
     *
     * @param inputProcessor The InputProcessor which should be placed in the chain
     */
    public void addProcessor(InputProcessor inputProcessor);

    /**
     * Removes the specified InputProcessor from this chain.
     *
     * @param inputProcessor to remove
     */
    public void removeProcessor(InputProcessor inputProcessor);

    /**
     * Returns a list with the active processors.
     *
     * @return List<InputProcessor>
     */
    public List<InputProcessor> getProcessors();

    /**
     * The actual processed document's security context
     *
     * @return The SecurityContext
     */
    public SecurityContext getSecurityContext();

    /**
     * The actual processed document's document context
     *
     * @return The DocumentContext
     */
    public DocumentContext getDocumentContext();

    /**
     * Create a new SubChain. The XMLEvents will be only be processed from the given InputProcessor to the end.
     * All earlier InputProcessors don't get these events. In other words the chain will be splitted in two parts.
     *
     * @param inputProcessor The InputProcessor position the XMLEvents should be processed over this SubChain.
     * @return A new InputProcessorChain
     * @throws XMLStreamException   thrown when a streaming error occurs
     * @throws XMLSecurityException thrown when a Security failure occurs
     */
    public InputProcessorChain createSubChain(InputProcessor inputProcessor) throws XMLStreamException, XMLSecurityException;

    /**
     * Requests the next security header XMLEvent from the next processor in the chain.
     *
     * @return The next XMLEvent from the previous processor
     * @throws XMLStreamException   thrown when a streaming error occurs
     * @throws XMLSecurityException thrown when a Security failure occurs
     */
    public XMLSecEvent processHeaderEvent() throws XMLStreamException, XMLSecurityException;

    /**
     * Requests the next XMLEvent from the next processor in the chain.
     *
     * @return The next XMLEvent from the previous processor
     * @throws XMLStreamException   thrown when a streaming error occurs
     * @throws XMLSecurityException thrown when a Security failure occurs
     */
    public XMLSecEvent processEvent() throws XMLStreamException, XMLSecurityException;
}
