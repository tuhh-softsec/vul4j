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
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * An abstract OutputProcessor class for reusabilty
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public abstract class AbstractBufferingOutputProcessor extends AbstractOutputProcessor {

    private final ArrayDeque<XMLSecEvent> xmlSecEventBuffer = new ArrayDeque<XMLSecEvent>(100);
    private String appendAfterThisTokenId;

    protected AbstractBufferingOutputProcessor() throws XMLSecurityException {
        super();
    }

    public Deque<XMLSecEvent> getXmlSecEventBuffer() {
        return xmlSecEventBuffer;
    }

    public String getAppendAfterThisTokenId() {
        return appendAfterThisTokenId;
    }

    public void setAppendAfterThisTokenId(String appendAfterThisTokenId) {
        this.appendAfterThisTokenId = appendAfterThisTokenId;
    }

    @Override
    public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain)
            throws XMLStreamException, XMLSecurityException {
        xmlSecEventBuffer.push(xmlSecEvent);
    }

    @Override
    public abstract void doFinal(OutputProcessorChain outputProcessorChain)
            throws XMLStreamException, XMLSecurityException;

    public abstract void processHeaderEvent(OutputProcessorChain outputProcessorChain)
            throws XMLStreamException, XMLSecurityException;
}
