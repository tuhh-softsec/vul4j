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

import org.apache.xml.security.utils.RFC2253Parser;
import org.apache.xml.security.stax.ext.stax.*;
import org.apache.xml.security.stax.impl.EncryptionPartDef;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import java.security.cert.X509Certificate;
import java.util.*;

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

    public XMLSecurityConstants.Phase getPhase() {
        return phase;
    }

    public void setPhase(XMLSecurityConstants.Phase phase) {
        this.phase = phase;
    }

    public void addBeforeProcessor(Object processor) {
        if (this.beforeProcessors == null) {
            this.beforeProcessors = new HashSet<Object>();
        }
        this.beforeProcessors.add(processor);
    }

    public Set<Object> getBeforeProcessors() {
        if (this.beforeProcessors == null) {
            return Collections.emptySet();
        }
        return this.beforeProcessors;
    }

    public void addAfterProcessor(Object processor) {
        if (this.afterProcessors == null) {
            this.afterProcessors = new HashSet<Object>();
        }
        this.afterProcessors.add(processor);
    }

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

    public void processNextEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain)
            throws XMLStreamException, XMLSecurityException {
        processEvent(xmlSecEvent, outputProcessorChain);
    }

    public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        outputProcessorChain.doFinal();
    }

    public XMLSecStartElement addAttributes(XMLSecStartElement xmlSecStartElement,
                                            List<XMLSecAttribute> attributeList) throws XMLStreamException {
        xmlSecStartElement.getOnElementDeclaredAttributes().addAll(attributeList);

        List<XMLSecNamespace> declaredNamespaces = xmlSecStartElement.getOnElementDeclaredNamespaces();
        for (int i = 0; i < attributeList.size(); i++) {
            XMLSecAttribute xmlSecAttribute = attributeList.get(i);
            final QName attributeName = xmlSecAttribute.getName();
            if (attributeName.getNamespaceURI() != null && !declaredNamespaces.contains(xmlSecAttribute.getAttributeNamespace())) {
                declaredNamespaces.add(xmlSecAttribute.getAttributeNamespace());
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

    public void createStartElementAndOutputAsEvent(
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

    public XMLSecCharacters createCharacters(String characters) {
        return XMLSecEventFactory.createXmlSecCharacters(characters);
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

    protected void createX509IssuerSerialStructure(OutputProcessorChain outputProcessorChain, X509Certificate[] x509Certificates)
            throws XMLStreamException, XMLSecurityException {
        createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509Data, true, null);
        createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509IssuerSerial, false, null);
        createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509IssuerName, false, null);
        //todo can the call to the rfcparser replaced by x509Certificates[0].getIssuerX500Principal().getName()??
        createCharactersAndOutputAsEvent(outputProcessorChain, RFC2253Parser.normalize(x509Certificates[0].getIssuerDN().getName(), true));
        createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509IssuerName);
        createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509SerialNumber, false, null);
        createCharactersAndOutputAsEvent(outputProcessorChain, x509Certificates[0].getSerialNumber().toString());
        createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509SerialNumber);
        createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509IssuerSerial);
        createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509Data);
    }

    protected void createReferenceListStructure(OutputProcessorChain outputProcessorChain)
            throws XMLStreamException, XMLSecurityException {
        List<EncryptionPartDef> encryptionPartDefs =
                outputProcessorChain.getSecurityContext().getAsList(EncryptionPartDef.class);
        if (encryptionPartDefs == null) {
            return;
        }
        List<XMLSecAttribute> attributes;
        createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_ReferenceList, true, null);
        //output the references to the encrypted data:
        Iterator<EncryptionPartDef> encryptionPartDefIterator = encryptionPartDefs.iterator();
        while (encryptionPartDefIterator.hasNext()) {
            EncryptionPartDef encryptionPartDef = encryptionPartDefIterator.next();

            attributes = new ArrayList<XMLSecAttribute>(1);
            attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_URI, "#" + encryptionPartDef.getEncRefId()));
            createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_DataReference, false, attributes);
            final String compressionAlgorithm = getSecurityProperties().getEncryptionCompressionAlgorithm();
            if (compressionAlgorithm != null) {
                createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_Transforms, true, null);
                attributes = new ArrayList<XMLSecAttribute>(1);
                attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, compressionAlgorithm));
                createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_Transform, false, attributes);
                createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_Transform);
                createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_Transforms);
            }
            createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_DataReference);
        }
        createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_ReferenceList);
    }

    protected SecurePart securePartMatches(XMLSecStartElement xmlSecStartElement,
                                           OutputProcessorChain outputProcessorChain, String dynamicParts) {
        Map<Object, SecurePart> dynamicSecureParts = outputProcessorChain.getSecurityContext().getAsMap(dynamicParts);
        return securePartMatches(xmlSecStartElement, dynamicSecureParts);
    }

    protected SecurePart securePartMatches(XMLSecStartElement xmlSecStartElement, Map<Object, SecurePart> secureParts) {
        SecurePart securePart = secureParts.get(xmlSecStartElement.getName());
        if (securePart == null) {
            Attribute attribute = xmlSecStartElement.getAttributeByName(XMLSecurityConstants.ATT_NULL_Id);
            if (attribute != null) {
                securePart = secureParts.get(attribute.getValue());
            }
        }
        return securePart;
    }
}
