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
package org.apache.xml.security.stax.impl;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.*;
import java.util.*;

/**
 * Custom XMLStreamWriter to map XMLStreamWriter method calls into XMLEvent's
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecurityStreamWriter implements XMLStreamWriter {

    private final OutputProcessorChain outputProcessorChain;
    private final Deque<QName> startElementStack = new ArrayDeque<QName>();
    private QName openStartElement = null;
    private final List<XMLSecAttribute> currentAttributes = new ArrayList<XMLSecAttribute>();
    private final Deque<Map<String, XMLSecNamespace>> nsStack;
    private NamespaceContext namespaceContext;
    private final NamespaceContext defaultNamespaceContext;
    private boolean haveToWriteEndElement = false;
    private boolean endDocumentWritten = false;

    public XMLSecurityStreamWriter(OutputProcessorChain outputProcessorChain) {
        this.outputProcessorChain = outputProcessorChain;
        nsStack = new ArrayDeque<Map<String, XMLSecNamespace>>();
        nsStack.push(Collections.<String, XMLSecNamespace>emptyMap());

        defaultNamespaceContext = new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                Iterator<Map<String, XMLSecNamespace>> stackIterator = nsStack.iterator();
                while (stackIterator.hasNext()) {
                    Map<String, XMLSecNamespace> next = stackIterator.next();
                    Namespace ns = next.get(prefix);
                    if (ns != null) {
                        return ns.getNamespaceURI();
                    }
                }
                if (namespaceContext != null) {
                    return namespaceContext.getNamespaceURI(prefix);
                }
                return null;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                Iterator<Map<String, XMLSecNamespace>> stackIterator = nsStack.iterator();
                while (stackIterator.hasNext()) {
                    Map<String, XMLSecNamespace> next = stackIterator.next();
                    Iterator<Map.Entry<String, XMLSecNamespace>> mapIterator = next.entrySet().iterator();
                    while (mapIterator.hasNext()) {
                        Map.Entry<String, XMLSecNamespace> entry = mapIterator.next();
                        if (namespaceURI.equals(entry.getValue().getNamespaceURI())) {
                            return entry.getKey();
                        }
                    }
                }
                if (namespaceContext != null) {
                    return namespaceContext.getPrefix(namespaceURI);
                }
                return null;
            }

            @Override
            public Iterator<?> getPrefixes(String namespaceURI) {
                List<String> prefixList = new ArrayList<String>(1);
                if (namespaceContext != null) {
                    @SuppressWarnings("unchecked")
                    Iterator<String> iterator = namespaceContext.getPrefixes(namespaceURI);
                    while (iterator.hasNext()) {
                        String next = iterator.next();
                        prefixList.add(next);
                    }
                }
                Iterator<Map<String, XMLSecNamespace>> stackIterator = nsStack.descendingIterator();
                while (stackIterator.hasNext()) {
                    Map<String, XMLSecNamespace> next = stackIterator.next();
                    Iterator<Map.Entry<String, XMLSecNamespace>> mapIterator = next.entrySet().iterator();
                    while (mapIterator.hasNext()) {
                        Map.Entry<String, XMLSecNamespace> entry = mapIterator.next();
                        if (prefixList.contains(entry.getKey())) {
                            prefixList.remove(entry.getKey());
                        }
                        if (namespaceURI.equals(entry.getValue().getNamespaceURI())) {
                            prefixList.add(entry.getKey());
                        }
                    }
                }
                return Collections.unmodifiableList(prefixList).iterator();
            }
        };
    }

    private void putNamespaceOntoStack(String prefix, XMLSecNamespace namespace) {
        Map<String, XMLSecNamespace> namespaceMap = nsStack.peek();
        if (Collections.<String, XMLSecNamespace>emptyMap() == namespaceMap) {
            nsStack.pop();
            namespaceMap = new HashMap<String, XMLSecNamespace>();
            nsStack.push(namespaceMap);
        }
        namespaceMap.put(prefix, namespace);
    }

    private void chainProcessEvent(XMLSecEvent xmlSecEvent) throws XMLStreamException {
        try {
            outputProcessorChain.reset();
            outputProcessorChain.processEvent(xmlSecEvent);
        } catch (XMLSecurityException e) {
            throw new XMLStreamException(e);
        } catch (XMLStreamException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("Trying to declare prefix xmlns (illegal as per NS 1.1 #4)")) {
                throw new XMLStreamException("If you hit this exception this most probably means" +
                        "you are using the javax.xml.transform.stax.StAXResult. Don't use " +
                        "it. It is buggy as hell.", e);
            }
            //NB1: net.java.dev.stax-utils also doesn work: [Fatal Error] :4:425: Attribute "xmlns" was already specified for element ...
            //NB2: The spring version also doesn't work...
            //it seems it is not trivial to write a StAXResult because I couldn't find a implementation which passes the testcases...hmm
            throw e;
        }
    }

    private void outputOpenStartElement() throws XMLStreamException {
        if (openStartElement != null) {
            chainProcessEvent(XMLSecEventFactory.createXmlSecStartElement(openStartElement, currentAttributes, nsStack.peek().values()));
            currentAttributes.clear();
            openStartElement = null;
        }
        if (haveToWriteEndElement) {
            haveToWriteEndElement = false;
            writeEndElement();
        }
        nsStack.push(Collections.<String, XMLSecNamespace>emptyMap());
    }

    @Override
    public void writeStartElement(String localName) throws XMLStreamException {
        outputOpenStartElement();
        QName qName = new QName(localName);
        startElementStack.push(qName);
        openStartElement = qName;
    }

    @Override
    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        outputOpenStartElement();
        String prefix = getNamespaceContext().getPrefix(namespaceURI);
        QName qName;
        if (prefix == null) {
            qName = new QName(namespaceURI, localName);
        } else {
            qName = new QName(namespaceURI, localName, prefix);
        }
        startElementStack.push(qName);
        openStartElement = qName;
    }

    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        outputOpenStartElement();
        QName qName = new QName(namespaceURI, localName, prefix);
        startElementStack.push(qName);
        openStartElement = qName;
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        writeStartElement(namespaceURI, localName);
        haveToWriteEndElement = true;
    }

    @Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        writeStartElement(prefix, localName, namespaceURI);
        haveToWriteEndElement = true;
    }

    @Override
    public void writeEmptyElement(String localName) throws XMLStreamException {
        writeStartElement(localName);
        haveToWriteEndElement = true;
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        outputOpenStartElement();
        QName element = startElementStack.pop();
        nsStack.pop();
        chainProcessEvent(XMLSecEventFactory.createXmlSecEndElement(element));

    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        if (!endDocumentWritten) {
            outputOpenStartElement();
            Iterator<QName> startElements = startElementStack.iterator();
            while (startElements.hasNext()) {
                nsStack.pop();
                chainProcessEvent(XMLSecEventFactory.createXmlSecEndElement(startElements.next()));
            }
            chainProcessEvent(XMLSecEventFactory.createXMLSecEndDocument());
            endDocumentWritten = true;
        }
    }

    @Override
    public void close() throws XMLStreamException {
        try {
            writeEndDocument();
            outputProcessorChain.reset();
            outputProcessorChain.doFinal();
        } catch (XMLSecurityException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void flush() throws XMLStreamException {
    }

    @Override
    public void writeAttribute(String localName, String value) throws XMLStreamException {
        currentAttributes.add(XMLSecEventFactory.createXMLSecAttribute(new QName(localName), value));
    }

    @Override
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        currentAttributes.add(XMLSecEventFactory.createXMLSecAttribute(new QName(namespaceURI, localName, prefix), value));
    }

    @Override
    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        currentAttributes.add(XMLSecEventFactory.createXMLSecAttribute(new QName(namespaceURI, localName, getNamespaceContext().getPrefix(namespaceURI)), value));
    }

    @Override
    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        putNamespaceOntoStack(prefix, XMLSecEventFactory.createXMLSecNamespace(prefix, namespaceURI));
    }

    @Override
    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        //workaround for sun's stax parser:
        if (this.openStartElement != null && this.openStartElement.getPrefix().equals("")) {
            this.openStartElement = new QName(namespaceURI, openStartElement.getLocalPart(), "");
        }
        putNamespaceOntoStack("", XMLSecEventFactory.createXMLSecNamespace(null, namespaceURI));

    }

    @Override
    public void writeComment(String data) throws XMLStreamException {
        outputOpenStartElement();
        chainProcessEvent(XMLSecEventFactory.createXMLSecComment(data));
    }

    @Override
    public void writeProcessingInstruction(String target) throws XMLStreamException {
        outputOpenStartElement();
        chainProcessEvent(XMLSecEventFactory.createXMLSecProcessingInstruction(target, ""));
    }

    @Override
    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        outputOpenStartElement();
        chainProcessEvent(XMLSecEventFactory.createXMLSecProcessingInstruction(target, data));
    }

    @Override
    public void writeCData(String data) throws XMLStreamException {
        outputOpenStartElement();
        chainProcessEvent(XMLSecEventFactory.createXMLSecCData(data));
    }

    @Override
    public void writeDTD(String dtd) throws XMLStreamException {
        outputOpenStartElement();
        chainProcessEvent(XMLSecEventFactory.createXMLSecDTD(dtd));
    }

    @Override
    public void writeEntityRef(final String name) throws XMLStreamException {
        outputOpenStartElement();
        chainProcessEvent(
                XMLSecEventFactory.createXMLSecEntityReference(
                        name,
                        XMLSecEventFactory.createXmlSecEntityDeclaration(name)
                )
        );
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        chainProcessEvent(XMLSecEventFactory.createXmlSecStartDocument(null, null, null, null));
    }

    @Override
    public void writeStartDocument(String version) throws XMLStreamException {
        chainProcessEvent(XMLSecEventFactory.createXmlSecStartDocument(null, null, null, version));
    }

    @Override
    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        chainProcessEvent(XMLSecEventFactory.createXmlSecStartDocument(null, encoding, null, version));
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException {
        outputOpenStartElement();
        chainProcessEvent(XMLSecEventFactory.createXmlSecCharacters(text));
    }

    @Override
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        outputOpenStartElement();
        chainProcessEvent(XMLSecEventFactory.createXmlSecCharacters(text, start, len));
    }

    @Override
    public String getPrefix(String uri) throws XMLStreamException {
        return defaultNamespaceContext.getPrefix(uri);
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        putNamespaceOntoStack(prefix, XMLSecEventFactory.createXMLSecNamespace(prefix, uri));
        if (openStartElement != null && openStartElement.getNamespaceURI().equals(uri)) {
            openStartElement = new QName(openStartElement.getNamespaceURI(), openStartElement.getLocalPart(), prefix);
        }
    }

    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        putNamespaceOntoStack("", XMLSecEventFactory.createXMLSecNamespace("", uri));
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        if (context == null) {
            throw new NullPointerException("context must not be null");
        }
        this.namespaceContext = context;
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return defaultNamespaceContext;
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        throw new IllegalArgumentException("Properties not supported");
    }
}
