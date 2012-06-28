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

import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

import javax.xml.stream.XMLStreamException;
import java.util.Set;

/**
 * This is the Interface which every OutputProcessor must implement.
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public interface OutputProcessor {

    /**
     * setter for the XMLSecurityProperties after instantiation of the processor
     *
     * @param xmlSecurityProperties
     */
    void setXMLSecurityProperties(XMLSecurityProperties xmlSecurityProperties);

    /**
     * setter for the Action after instantiation of the processor
     *
     * @param action
     */
    void setAction(XMLSecurityConstants.Action action);

    /**
     * Method will be called after setting the properties
     */
    void init(OutputProcessorChain outputProcessorChain) throws XMLSecurityException;

    /**
     * Add this processor before the given processor
     *
     * @param processor
     */
    void addBeforeProcessor(Object processor);

    /**
     * This OutputProcessor will be added before the processors in this set
     *
     * @return The set with the named OutputProcessor
     */
    Set<Object> getBeforeProcessors();

    /**
     * Add this processor after the given processor
     *
     * @param processor
     */
    void addAfterProcessor(Object processor);

    /**
     * This OutputProcessor will be added after the processors in this set
     *
     * @return The set with the named OutputProcessor
     */
    Set<Object> getAfterProcessors();

    /**
     * The Phase in which this OutputProcessor should be applied
     *
     * @return The Phase
     */
    XMLSecurityConstants.Phase getPhase();

    /**
     * Will be called from the framework for every XMLEvent
     *
     * @param xmlSecEvent          The next XMLEvent to process
     * @param outputProcessorChain
     * @throws XMLStreamException   thrown when a streaming error occurs
     * @throws XMLSecurityException thrown when a Security failure occurs
     */
    void processNextEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException;

    /**
     * Will be called when the whole document is processed.
     *
     * @param outputProcessorChain
     * @throws XMLStreamException   thrown when a streaming error occurs
     * @throws XMLSecurityException thrown when a Security failure occurs
     */
    void doFinal(OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException;
}
