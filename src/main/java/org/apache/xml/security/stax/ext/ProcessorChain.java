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

import javax.xml.stream.XMLStreamException;

/**
 * Basic interface for Output- and Input-Processor chains
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public interface ProcessorChain {

    /**
     * resets the chain so that the next event will go again to the first processor in the chain.
     */
    void reset();

    /**
     * Will finally be called when the whole document is processed
     * Important note: Every processor in the chain has to call doFinal() in its own doFinal() method.
     * InputProcessors should call it before doing other stuff to keep the processing order. Remember the
     * input-chain is in principle processed in the reverse order since we "leech" the events through the chain.
     * So that means that we should do the same for the doFinal method, otherwise we may run into troubles.
     *
     * @throws XMLStreamException   thrown when a streaming error occurs
     * @throws XMLSecurityException thrown when a Security failure occurs
     */
    void doFinal() throws XMLStreamException, XMLSecurityException;
}
