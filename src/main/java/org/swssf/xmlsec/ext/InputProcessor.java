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
import java.util.Set;

/**
 * This is the Interface which every InputProcessor must implement.
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public interface InputProcessor {

    /**
     * Add this processor before the given processor
     *
     * @param processor
     */
    void addBeforeProcessor(Object processor);

    /**
     * This InputProcessor will be added before the processors in this set
     *
     * @return The set with the named InputProcessors
     */
    Set<Object> getBeforeProcessors();

    /**
     * Add this processor after the given processor
     *
     * @param processor
     */
    void addAfterProcessor(Object processor);

    /**
     * This InputProcessor will be added after the processors in this set
     *
     * @return The set with the named InputProcessors
     */
    Set<Object> getAfterProcessors();

    /**
     * The Phase in which this InputProcessor should be applied
     *
     * @return The Phase
     */
    XMLSecurityConstants.Phase getPhase();

    /**
     * Will be called from the framework when the next security-header XMLEvent is requested
     *
     * @param inputProcessorChain
     * @return The next XMLSecEvent
     * @throws XMLStreamException   thrown when a streaming error occurs
     * @throws XMLSecurityException thrown when a Security failure occurs
     */
    XMLSecEvent processNextHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException;

    /**
     * Will be called from the framework when the next XMLEvent is requested
     *
     * @param inputProcessorChain
     * @return The next XMLSecEvent
     * @throws XMLStreamException   thrown when a streaming error occurs
     * @throws XMLSecurityException thrown when a Security failure occurs
     */
    XMLSecEvent processNextEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException;

    /**
     * Will be called when the whole document is processed.
     *
     * @param inputProcessorChain
     * @throws XMLStreamException   thrown when a streaming error occurs
     * @throws XMLSecurityException thrown when a Security failure occurs
     */
    void doFinal(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException;
}
