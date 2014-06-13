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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecCharacters;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * An abstract OutputProcessor class for reusabilty
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public abstract class AbstractOutputProcessor implements OutputProcessor {

    protected XMLSecurityProperties securityProperties;
    protected XMLSecurityConstants.Action action;

    private XMLSecurityConstants.Phase phase = XMLSecurityConstants.Phase.PROCESSING;
    private Set<Object> beforeProcessors;
    private Set<Object> afterProcessors;

    protected AbstractOutputProcessor() throws XMLSecurityException {
        super();
    }

    @Override
    public void setXMLSecurityProperties(XMLSecurityProperties xmlSecurityProperties) {
        this.securityProperties = xmlSecurityProperties;
    }

    @Override
    public void setAction(XMLSecurityConstants.Action action) {
        this.action = action;
    }

    @Override
    public void init(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        outputProcessorChain.addProcessor(this);
    }

    @Override
    public XMLSecurityConstants.Phase getPhase() {
        return phase;
    }

    public void setPhase(XMLSecurityConstants.Phase phase) {
        this.phase = phase;
    }

    @Override
    public void addBeforeProcessor(Object processor) {
        if (this.beforeProcessors == null) {
            this.beforeProcessors = new HashSet<Object>();
        }
        this.beforeProcessors.add(processor);
    }

    @Override
    public Set<Object> getBeforeProcessors() {
        if (this.beforeProcessors == null) {
            return Collections.emptySet();
        }
        return this.beforeProcessors;
    }

    @Override
    public void addAfterProcessor(Object processor) {
        if (this.afterProcessors == null) {
            this.afterProcessors = new HashSet<Object>();
        }
        this.afterProcessors.add(processor);
    }

    @Override
    public Set<Object> getAfterProcessors() {
        if (this.afterProcessors == null) {
            return Collections.emptySet();
        }
        return this.afterProcessors;
    }

    public XMLSecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    public XMLSecurityConstants.Action getAction() {
        return action;
    }

    public abstract void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain)
            throws XMLStreamException, XMLSecurityException;

    @Override
    public void processNextEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain)
            throws XMLStreamException, XMLSecurityException {
        processEvent(xmlSecEvent, outputProcessorChain);
    }

    @Override
    public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        outputProcessorChain.doFinal();
    }

    public XMLSecStartElement addAttributes(XMLSecStartElement xmlSecStartElement,
                                            List<XMLSecAttribute> attributeList) throws XMLStreamException {

        List<XMLSecNamespace> declaredNamespaces = xmlSecStartElement.getOnElementDeclaredNamespaces();
        for (int i = 0; i < attributeList.size(); i++) {
            XMLSecAttribute xmlSecAttribute = attributeList.get(i);
            xmlSecStartElement.addAttribute(xmlSecAttribute);

            final QName attributeName = xmlSecAttribute.getName();
            if (attributeName.getNamespaceURI() != null && !"".equals(attributeName.getNamespaceURI()) 
                && !declaredNamespaces.contains(xmlSecAttribute.getAttributeNamespace())) {
                xmlSecStartElement.addNamespace(xmlSecAttribute.getAttributeNamespace());
            }
        }
        return xmlSecStartElement;
    }

    public void createStartElementAndOutputAsEvent(
            OutputProcessorChain outputProcessorChain, QName element,
            List<XMLSecNamespace> namespaces, List<XMLSecAttribute> attributes)
            throws XMLStreamException, XMLSecurityException {

        XMLSecStartElement xmlSecStartElement = XMLSecEventFactory.createXmlSecStartElement(element, attributes, namespaces);
        outputAsEvent(outputProcessorChain, xmlSecStartElement);
    }

    public XMLSecStartElement createStartElementAndOutputAsEvent(
            OutputProcessorChain outputProcessorChain, QName element,
            boolean outputLocalNs, List<XMLSecAttribute> attributes) throws XMLStreamException, XMLSecurityException {

        List<XMLSecNamespace> comparableNamespaces = Collections.emptyList();
        if (outputLocalNs) {
            comparableNamespaces = new ArrayList<XMLSecNamespace>(2);
            comparableNamespaces.add(XMLSecEventFactory.createXMLSecNamespace(element.getPrefix(), element.getNamespaceURI()));
        }

        if (attributes != null) {
            for (int i = 0; i < attributes.size(); i++) {
                XMLSecAttribute xmlSecAttribute = attributes.get(i);
                QName attributeName = xmlSecAttribute.getName();
                String attributeNamePrefix = attributeName.getPrefix();

                if (attributeNamePrefix != null && attributeNamePrefix.isEmpty()) {
                    continue;
                }

                if (!comparableNamespaces.contains(xmlSecAttribute.getAttributeNamespace())) {
                    if (comparableNamespaces == Collections.<XMLSecNamespace>emptyList()) {
                        comparableNamespaces = new ArrayList<XMLSecNamespace>(1);
                    }
                    comparableNamespaces.add(xmlSecAttribute.getAttributeNamespace());
                }
            }
        }
        XMLSecStartElement xmlSecStartElement
                = XMLSecEventFactory.createXmlSecStartElement(element, attributes, comparableNamespaces);
        outputAsEvent(outputProcessorChain, xmlSecStartElement);
        return xmlSecStartElement;
    }

    public XMLSecEndElement createEndElement(QName element) {
        return XMLSecEventFactory.createXmlSecEndElement(element);
    }

    public void createEndElementAndOutputAsEvent(OutputProcessorChain outputProcessorChain, QName element)
            throws XMLStreamException, XMLSecurityException {
        outputAsEvent(outputProcessorChain, createEndElement(element));
    }

    public void createCharactersAndOutputAsEvent(OutputProcessorChain outputProcessorChain, String characters)
            throws XMLStreamException, XMLSecurityException {
        outputAsEvent(outputProcessorChain, createCharacters(characters));
    }

    public void createCharactersAndOutputAsEvent(OutputProcessorChain outputProcessorChain, char[] text)
            throws XMLStreamException, XMLSecurityException {
        outputAsEvent(outputProcessorChain, createCharacters(text));
    }

    public XMLSecCharacters createCharacters(String characters) {
        return XMLSecEventFactory.createXmlSecCharacters(characters);
    }

    public XMLSecCharacters createCharacters(char[] text) {
        return XMLSecEventFactory.createXmlSecCharacters(text);
    }

    public XMLSecAttribute createAttribute(QName attribute, String attributeValue) {
        return XMLSecEventFactory.createXMLSecAttribute(attribute, attributeValue);
    }

    public XMLSecNamespace createNamespace(String prefix, String uri) {
        return XMLSecEventFactory.createXMLSecNamespace(prefix, uri);
    }

    protected void outputAsEvent(OutputProcessorChain outputProcessorChain, XMLSecEvent xmlSecEvent)
            throws XMLStreamException, XMLSecurityException {
        outputProcessorChain.reset();
        outputProcessorChain.processEvent(xmlSecEvent);
    }

    protected SecurePart securePartMatches(XMLSecStartElement xmlSecStartElement,
                                           OutputProcessorChain outputProcessorChain, String dynamicParts) {
        Map<Object, SecurePart> dynamicSecureParts = outputProcessorChain.getSecurityContext().getAsMap(dynamicParts);
        return securePartMatches(xmlSecStartElement, dynamicSecureParts);
    }

    protected SecurePart securePartMatches(XMLSecStartElement xmlSecStartElement, Map<Object, SecurePart> secureParts) {
        SecurePart securePart = null;
        if (secureParts != null) {
            securePart = secureParts.get(xmlSecStartElement.getName());
            if (securePart == null) {
                Attribute attribute = xmlSecStartElement.getAttributeByName(XMLSecurityConstants.ATT_NULL_Id);
                if (attribute != null) {
                    securePart = secureParts.get(attribute.getValue());
                }
            }
        }
        return securePart;
    }
    
    protected void outputDOMElement(Element element, OutputProcessorChain outputProcessorChain)
            throws XMLStreamException, XMLSecurityException {

        NamedNodeMap namedNodeMap = element.getAttributes();
        List<XMLSecAttribute> attributes = new ArrayList<XMLSecAttribute>(namedNodeMap.getLength());
        List<XMLSecNamespace> namespaces = new ArrayList<XMLSecNamespace>(namedNodeMap.getLength());
        for (int i = 0; i < namedNodeMap.getLength(); i++) {
            Attr attribute = (Attr) namedNodeMap.item(i);
            if (attribute.getPrefix() == null) {
                attributes.add(
                        createAttribute(
                                new QName(attribute.getNamespaceURI(), attribute.getLocalName()), attribute.getValue()));
            } else if ("xmlns".equals(attribute.getPrefix()) || "xmlns".equals(attribute.getLocalName())) {
                namespaces.add(createNamespace(attribute.getLocalName(), attribute.getValue()));
            } else {
                attributes.add(
                        createAttribute(
                                new QName(attribute.getNamespaceURI(), attribute.getLocalName(), attribute.getPrefix()),
                                attribute.getValue()));
            }
        }

        QName elementName = new QName(element.getNamespaceURI(), element.getLocalName(), element.getPrefix());
        createStartElementAndOutputAsEvent(outputProcessorChain, elementName, namespaces, attributes);
        Node childNode = element.getFirstChild();
        while (childNode != null) {
            switch (childNode.getNodeType()) {
                case Node.ELEMENT_NODE:
                    outputDOMElement((Element) childNode, outputProcessorChain);
                    break;
                case Node.TEXT_NODE:
                    createCharactersAndOutputAsEvent(outputProcessorChain, ((Text) childNode).getData());
                    break;
            }
            childNode = childNode.getNextSibling();
        }
        createEndElementAndOutputAsEvent(outputProcessorChain, elementName);
    }
}
