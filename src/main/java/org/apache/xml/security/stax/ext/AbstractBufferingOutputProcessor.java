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

import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
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
    private String appendAfterThisTokenId;
    private static final List<QName> appendAfterOneOfThisAttributes;

    static {
        List<QName> list = new ArrayList<QName>(1);
        list.add(XMLSecurityConstants.ATT_NULL_Id);
        appendAfterOneOfThisAttributes = Collections.unmodifiableList(list);
    }

    protected AbstractBufferingOutputProcessor() throws XMLSecurityException {
        super();
    }

    protected Deque<XMLSecEvent> getXmlSecEventBuffer() {
        return xmlSecEventBuffer;
    }

    protected String getAppendAfterThisTokenId() {
        return appendAfterThisTokenId;
    }

    protected void setAppendAfterThisTokenId(String appendAfterThisTokenId) {
        this.appendAfterThisTokenId = appendAfterThisTokenId;
    }

    protected List<QName> getAppendAfterOneOfThisAttributes() {
        return appendAfterOneOfThisAttributes;
    }

    @Override
    public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain)
            throws XMLStreamException, XMLSecurityException {
        xmlSecEventBuffer.push(xmlSecEvent);
    }

    @Override
    public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        OutputProcessorChain subOutputProcessorChain = outputProcessorChain.createSubChain(this);
        final Iterator<XMLSecEvent> xmlSecEventIterator = getXmlSecEventBuffer().descendingIterator();
        flushBufferAndCallbackAfterTokenID(subOutputProcessorChain, xmlSecEventIterator);
        //call final on the rest of the chain
        subOutputProcessorChain.doFinal();
        //this processor is now finished and we can remove it now
        subOutputProcessorChain.removeProcessor(this);
    }

    protected abstract void processHeaderEvent(OutputProcessorChain outputProcessorChain)
            throws XMLStreamException, XMLSecurityException;

    protected void flushBufferAndCallbackAfterTokenID(OutputProcessorChain outputProcessorChain,
                                                      Iterator<XMLSecEvent> xmlSecEventIterator)
            throws XMLStreamException, XMLSecurityException {

        String appendAfterThisTokenId = getAppendAfterThisTokenId();

        //append current header
        if (appendAfterThisTokenId == null) {
            this.processHeaderEvent(outputProcessorChain);
        } else {
            //we have a dependent token. so we have to append the current header after the token
            QName matchingElementName = null;

            loop:
            while (xmlSecEventIterator.hasNext()) {
                XMLSecEvent xmlSecEvent = xmlSecEventIterator.next();

                outputProcessorChain.reset();
                outputProcessorChain.processEvent(xmlSecEvent);
                switch (xmlSecEvent.getEventType()) {
                    //search for an element with a matching wsu:Id. this is our token
                    case XMLStreamConstants.START_ELEMENT:
                        XMLSecStartElement xmlSecStartElement = xmlSecEvent.asStartElement();
                        List<XMLSecAttribute> xmlSecAttributes = xmlSecStartElement.getOnElementDeclaredAttributes();
                        for (int i = 0; i < xmlSecAttributes.size(); i++) {
                            XMLSecAttribute xmlSecAttribute = xmlSecAttributes.get(i);
                            final QName attributeName = xmlSecAttribute.getName();
                            final String attributeValue = xmlSecAttribute.getValue();
                            if (getAppendAfterOneOfThisAttributes().contains(attributeName)
                                    && appendAfterThisTokenId.equals(attributeValue)) {
                                matchingElementName = xmlSecStartElement.getName();
                                break loop;
                            }
                        }
                        break;
                }
            }

            //we found the token and...
            int level = 0;
            loop:
            while (xmlSecEventIterator.hasNext()) {
                XMLSecEvent xmlSecEvent = xmlSecEventIterator.next();

                outputProcessorChain.reset();
                outputProcessorChain.processEvent(xmlSecEvent);
                //...loop until we reach the token end element
                switch (xmlSecEvent.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        level++;
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        XMLSecEndElement xmlSecEndElement = xmlSecEvent.asEndElement();
                        if (level == 0 && xmlSecEndElement.getName().equals(matchingElementName)) {
                            //output now the current header
                            this.processHeaderEvent(outputProcessorChain);
                            break loop;
                        }
                        level--;
                        break;
                }
            }
        }

        //loop through the rest of the document
        while (xmlSecEventIterator.hasNext()) {
            XMLSecEvent xmlSecEvent = xmlSecEventIterator.next();
            outputProcessorChain.reset();
            outputProcessorChain.processEvent(xmlSecEvent);
        }
        outputProcessorChain.reset();
    }
}
