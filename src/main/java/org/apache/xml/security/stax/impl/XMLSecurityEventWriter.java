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

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.util.Iterator;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecurityEventWriter implements XMLEventWriter {

    private final XMLStreamWriter xmlStreamWriter;

    public XMLSecurityEventWriter(XMLStreamWriter xmlStreamWriter) {
        this.xmlStreamWriter = xmlStreamWriter;
    }

    public void add(XMLEvent event) throws XMLStreamException {
        switch (event.getEventType()) {
            case XMLStreamConstants.START_ELEMENT:
                StartElement startElement = event.asStartElement();
                QName n = startElement.getName();
                this.xmlStreamWriter.writeStartElement(n.getPrefix(), n.getLocalPart(), n.getNamespaceURI());

                @SuppressWarnings("unchecked")
                Iterator<Namespace> namespaceIterator = startElement.getNamespaces();
                while (namespaceIterator.hasNext()) {
                    add(namespaceIterator.next());
                }

                @SuppressWarnings("unchecked")
                Iterator<Attribute> attributeIterator = startElement.getAttributes();
                while (attributeIterator.hasNext()) {
                    add(attributeIterator.next());
                }
                break;

            case XMLStreamConstants.END_ELEMENT:
                this.xmlStreamWriter.writeEndElement();
                break;

            case XMLStreamConstants.PROCESSING_INSTRUCTION:
                ProcessingInstruction pi = (ProcessingInstruction) event;
                this.xmlStreamWriter.writeProcessingInstruction(pi.getTarget(), pi.getData());
                break;

            case XMLStreamConstants.CHARACTERS:
                Characters characters = event.asCharacters();
                if (characters.isCData()) {
                    this.xmlStreamWriter.writeCData(characters.getData());
                } else {
                    this.xmlStreamWriter.writeCharacters(characters.getData());
                }
                break;

            case XMLStreamConstants.COMMENT:
                this.xmlStreamWriter.writeComment(((Comment) event).getText());
                break;

            case XMLStreamConstants.START_DOCUMENT:
                StartDocument startDocument = (StartDocument) event;
                if (!startDocument.encodingSet()) {
                    this.xmlStreamWriter.writeStartDocument(startDocument.getVersion());
                } else {
                    this.xmlStreamWriter.writeStartDocument(startDocument.getCharacterEncodingScheme(), startDocument.getVersion());
                }
                break;

            case XMLStreamConstants.END_DOCUMENT:
                this.xmlStreamWriter.writeEndDocument();
                break;

            case XMLStreamConstants.ENTITY_REFERENCE:
                this.xmlStreamWriter.writeEntityRef(((EntityReference) event).getName());
                break;

            case XMLStreamConstants.ATTRIBUTE:
                Attribute attribute = (Attribute) event;
                QName name = attribute.getName();
                this.xmlStreamWriter.writeAttribute(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), attribute.getValue());
                break;

            case XMLStreamConstants.DTD:
                this.xmlStreamWriter.writeDTD(((DTD) event).getDocumentTypeDeclaration());
                break;

            case XMLStreamConstants.CDATA:
                this.xmlStreamWriter.writeCData(event.asCharacters().getData());
                break;

            case XMLStreamConstants.NAMESPACE:
                Namespace ns = (Namespace) event;
                this.xmlStreamWriter.writeNamespace(ns.getPrefix(), ns.getNamespaceURI());
                break;

            case XMLStreamConstants.SPACE:
            case XMLStreamConstants.NOTATION_DECLARATION:
            case XMLStreamConstants.ENTITY_DECLARATION:
            default:
                throw new XMLStreamException("Illegal event");
        }
    }

    public void add(XMLEventReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            add(reader.nextEvent());
        }
    }

    public void close() throws XMLStreamException {
        this.xmlStreamWriter.close();
    }

    public void flush() throws XMLStreamException {
        this.xmlStreamWriter.flush();
    }

    public NamespaceContext getNamespaceContext() {
        return this.xmlStreamWriter.getNamespaceContext();
    }

    public String getPrefix(String uri) throws XMLStreamException {
        return this.xmlStreamWriter.getPrefix(uri);
    }

    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this.xmlStreamWriter.setDefaultNamespace(uri);
    }

    public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
        this.xmlStreamWriter.setNamespaceContext(namespaceContext);
    }

    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        this.xmlStreamWriter.setPrefix(prefix, uri);
    }
}
