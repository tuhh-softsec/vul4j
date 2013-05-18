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

import javax.xml.stream.XMLStreamException;
import java.util.*;

/**
 * An abstract OutputProcessor class for reusabilty
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public abstract class AbstractBufferingOutputProcessor extends AbstractOutputProcessor {

    private final ArrayDeque<XMLSecEvent> xmlSecEventBuffer = new ArrayDeque<XMLSecEvent>(100);

    protected AbstractBufferingOutputProcessor() throws XMLSecurityException {
        super();
    }

    protected Deque<XMLSecEvent> getXmlSecEventBuffer() {
        return xmlSecEventBuffer;
    }

    @Override
    public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain)
            throws XMLStreamException, XMLSecurityException {
        xmlSecEventBuffer.offer(xmlSecEvent);
    }

    @Override
    public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        OutputProcessorChain subOutputProcessorChain = outputProcessorChain.createSubChain(this);
        flushBufferAndCallbackAfterHeader(subOutputProcessorChain, getXmlSecEventBuffer());
        //call final on the rest of the chain
        subOutputProcessorChain.doFinal();
        //this processor is now finished and we can remove it now
        subOutputProcessorChain.removeProcessor(this);
    }

    protected abstract void processHeaderEvent(OutputProcessorChain outputProcessorChain)
            throws XMLStreamException, XMLSecurityException;

    protected void flushBufferAndCallbackAfterHeader(OutputProcessorChain outputProcessorChain,
                                                     Deque<XMLSecEvent> xmlSecEventDeque)
            throws XMLStreamException, XMLSecurityException {

        this.processHeaderEvent(outputProcessorChain);
        
        //loop through the rest of the document
        while (!xmlSecEventDeque.isEmpty()) {
            XMLSecEvent xmlSecEvent = xmlSecEventDeque.pop();
            outputProcessorChain.reset();
            outputProcessorChain.processEvent(xmlSecEvent);
        }
        outputProcessorChain.reset();
    }
}
