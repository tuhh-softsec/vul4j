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
package org.swssf.xmlsec.impl;

import org.swssf.xmlsec.ext.InputProcessorChain;
import org.swssf.xmlsec.ext.XMLSecurityException;
import org.swssf.xmlsec.ext.XMLSecurityProperties;
import org.swssf.xmlsec.ext.stax.XMLSecEvent;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.util.Iterator;

/**
 * A custom implementation of a XMLStreamReader to get back from the XMLEventReader world
 * to XMLStreamReader
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecurityStreamReader implements XMLStreamReader {

    private final InputProcessorChain inputProcessorChain;
    private XMLSecEvent currentXMLSecEvent;
    private boolean skipDocumentEvents = false;

    private static final String ERR_STATE_NOT_ELEM = "Current state not START_ELEMENT or END_ELEMENT";
    private static final String ERR_STATE_NOT_STELEM = "Current state not START_ELEMENT";
    private static final String ERR_STATE_NOT_PI = "Current state not PROCESSING_INSTRUCTION";

    public XMLSecurityStreamReader(InputProcessorChain inputProcessorChain, XMLSecurityProperties securityProperties) {
        this.inputProcessorChain = inputProcessorChain;
        this.skipDocumentEvents = securityProperties.isSkipDocumentEvents();
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        if (XMLInputFactory.IS_NAMESPACE_AWARE.equals(name)) {
            return true;
        }
        return null;
    }

    public int next() throws XMLStreamException {
        int eventType;
        try {
            inputProcessorChain.reset();
            currentXMLSecEvent = inputProcessorChain.processEvent();
            eventType = currentXMLSecEvent.getEventType();
            if (eventType == START_DOCUMENT && this.skipDocumentEvents) {
                currentXMLSecEvent = inputProcessorChain.processEvent();
                eventType = currentXMLSecEvent.getEventType();
            }
        } catch (XMLSecurityException e) {
            throw new XMLStreamException(e);
        }
        /*todo why was this needed? Because of the Sun Stax impl?
         if (currentEvent.isCharacters() && currentEvent.asCharacters().isIgnorableWhiteSpace()) {
            return XMLStreamConstants.SPACE;
        }*/
        return eventType;
    }

    private XMLSecEvent getCurrentEvent() {
        return currentXMLSecEvent;
    }

    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != type) {
            throw new XMLStreamException("Event type mismatch");
        }

        if (localName != null) {
            if (xmlSecEvent.getEventType() != START_ELEMENT && xmlSecEvent.getEventType() != END_ELEMENT
                    && xmlSecEvent.getEventType() != ENTITY_REFERENCE) {
                throw new XMLStreamException("Expected non-null local name, but current token not a START_ELEMENT, END_ELEMENT or ENTITY_REFERENCE (was " + xmlSecEvent.getEventType() + ")");
            }
            String n = getLocalName();
            if (!n.equals(localName)) {
                throw new XMLStreamException("Expected local name '" + localName + "'; current local name '" + n + "'.");
            }
        }
        if (namespaceURI != null) {
            if (xmlSecEvent.getEventType() != START_ELEMENT && xmlSecEvent.getEventType() != END_ELEMENT) {
                throw new XMLStreamException("Expected non-null NS URI, but current token not a START_ELEMENT or END_ELEMENT (was " + xmlSecEvent.getEventType() + ")");
            }
            String uri = getNamespaceURI();
            // No namespace?
            if (namespaceURI.length() == 0) {
                if (uri != null && uri.length() > 0) {
                    throw new XMLStreamException("Expected empty namespace, instead have '" + uri + "'.");
                }
            } else {
                if (!namespaceURI.equals(uri)) {
                    throw new XMLStreamException("Expected namespace '" + namespaceURI + "'; have '"
                            + uri + "'.");
                }
            }
        }
    }

    final private static int MASK_GET_ELEMENT_TEXT =
            (1 << CHARACTERS) | (1 << CDATA) | (1 << SPACE)
                    | (1 << ENTITY_REFERENCE);

    public String getElementText() throws XMLStreamException {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != START_ELEMENT) {
            throw new XMLStreamException("Not positioned on a start element");
        }
        StringBuilder stringBuilder = new StringBuilder();

        /**
         * Need to loop to get rid of PIs, comments
         */
        loop:
        while (true) {
            int type = next();
            switch (type) {
                case END_ELEMENT:
                    break loop;
                case COMMENT:
                case PROCESSING_INSTRUCTION:
                    continue loop;
                case ENTITY_REFERENCE:
                case SPACE:
                case CDATA:
                case CHARACTERS:
                    stringBuilder.append(getText());
                default:
                    throw new XMLStreamException("Expected a text token, got " + type + ".");
            }
        }
        return stringBuilder.toString();
    }

    public int nextTag() throws XMLStreamException {
        while (true) {
            int next = next();

            switch (next) {
                case SPACE:
                case COMMENT:
                case PROCESSING_INSTRUCTION:
                    continue;
                case CDATA:
                case CHARACTERS:
                    if (isWhiteSpace()) {
                        continue;
                    }
                    throw new XMLStreamException("Received non-all-whitespace CHARACTERS or CDATA event in nextTag().");
                case START_ELEMENT:
                case END_ELEMENT:
                    return next;
            }
            throw new XMLStreamException("Received event " + next
                    + ", instead of START_ELEMENT or END_ELEMENT.");
        }
    }

    public boolean hasNext() throws XMLStreamException {
        return currentXMLSecEvent == null || currentXMLSecEvent.getEventType() != END_DOCUMENT;
    }

    public void close() throws XMLStreamException {
        try {
            inputProcessorChain.reset();
            inputProcessorChain.doFinal();
        } catch (XMLSecurityException e) {
            throw new XMLStreamException(e);
        }
    }

    public String getNamespaceURI(String prefix) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case START_ELEMENT:
                return xmlSecEvent.asStartElement().getNamespaceURI(prefix);
            case END_ELEMENT:
                //todo somehow...
                return null;
            default:
                throw new IllegalStateException(ERR_STATE_NOT_ELEM);
        }
    }

    public boolean isStartElement() {
        return getCurrentEvent().isStartElement();
    }

    public boolean isEndElement() {
        return getCurrentEvent().isEndElement();
    }

    public boolean isCharacters() {
        return getCurrentEvent().isCharacters();
    }

    public boolean isWhiteSpace() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        return xmlSecEvent.isCharacters() && xmlSecEvent.asCharacters().isWhiteSpace();
    }

    public String getAttributeValue(String namespaceURI, String localName) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != START_ELEMENT) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        Attribute attribute = xmlSecEvent.asStartElement().getAttributeByName(new QName(namespaceURI, localName));
        if (attribute != null) {
            return attribute.getValue();
        }
        return null;
    }

    public int getAttributeCount() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != START_ELEMENT) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getOnElementDeclaredAttributes().size();
    }

    public QName getAttributeName(int index) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != START_ELEMENT) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getOnElementDeclaredAttributes().get(index).getName();
    }

    public String getAttributeNamespace(int index) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != START_ELEMENT) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getOnElementDeclaredAttributes().get(index).getAttributeNamespace().getNamespaceURI();
    }

    public String getAttributeLocalName(int index) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != START_ELEMENT) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getOnElementDeclaredAttributes().get(index).getName().getLocalPart();
    }

    public String getAttributePrefix(int index) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != START_ELEMENT) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getOnElementDeclaredAttributes().get(index).getName().getPrefix();
    }

    public String getAttributeType(int index) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != START_ELEMENT) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getOnElementDeclaredAttributes().get(index).getDTDType();
    }

    public String getAttributeValue(int index) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != START_ELEMENT) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getOnElementDeclaredAttributes().get(index).getValue();
    }

    public boolean isAttributeSpecified(int index) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != START_ELEMENT) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getOnElementDeclaredAttributes().get(index).isSpecified();
    }

    @SuppressWarnings("unchecked")
    public int getNamespaceCount() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case START_ELEMENT:
                return xmlSecEvent.asStartElement().getOnElementDeclaredNamespaces().size();
            case END_ELEMENT:
                int count = 0;
                Iterator<Namespace> namespaceIterator = xmlSecEvent.asEndElement().getNamespaces();
                while (namespaceIterator.hasNext()) {
                    namespaceIterator.next();
                    count++;
                }
                return count;
            default:
                throw new IllegalStateException(ERR_STATE_NOT_ELEM);
        }
    }

    @SuppressWarnings("unchecked")
    public String getNamespacePrefix(int index) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case START_ELEMENT:
                return xmlSecEvent.asStartElement().getOnElementDeclaredNamespaces().get(index).getPrefix();
            case END_ELEMENT:
                int count = 0;
                Iterator<Namespace> namespaceIterator = xmlSecEvent.asEndElement().getNamespaces();
                while (namespaceIterator.hasNext()) {
                    Namespace namespace = namespaceIterator.next();
                    if (count == index) {
                        return namespace.getPrefix();
                    }
                    count++;
                }
                throw new ArrayIndexOutOfBoundsException(index);
            default:
                throw new IllegalStateException(ERR_STATE_NOT_ELEM);
        }
    }

    public String getNamespaceURI(int index) {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != START_ELEMENT) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getOnElementDeclaredNamespaces().get(index).getNamespaceURI();
    }

    public NamespaceContext getNamespaceContext() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != START_ELEMENT) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getNamespaceContext();
    }

    public int getEventType() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent == null) {
            try {
                return next();
            } catch (XMLStreamException e) {
                throw new IllegalStateException(e);
            }
        }
        if (xmlSecEvent.isCharacters() && xmlSecEvent.asCharacters().isIgnorableWhiteSpace()) {
            return XMLStreamConstants.SPACE;
        }
        return xmlSecEvent.getEventType();
    }

    public String getText() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();

        switch (xmlSecEvent.getEventType()) {
            case ENTITY_REFERENCE:
                return ((EntityReference) xmlSecEvent).getDeclaration().getReplacementText();
            case DTD:
                return ((javax.xml.stream.events.DTD) xmlSecEvent).getDocumentTypeDeclaration();
            case COMMENT:
                return ((Comment) xmlSecEvent).getText();
            case CDATA:
            case SPACE:
            case CHARACTERS:
                return xmlSecEvent.asCharacters().getData();
            default:
                throw new IllegalStateException("Current state not TEXT");
        }
    }

    public char[] getTextCharacters() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case ENTITY_REFERENCE:
                return ((EntityReference) xmlSecEvent).getDeclaration().getReplacementText().toCharArray();
            case DTD:
                return ((javax.xml.stream.events.DTD) xmlSecEvent).getDocumentTypeDeclaration().toCharArray();
            case COMMENT:
                return ((Comment) xmlSecEvent).getText().toCharArray();
            case CDATA:
            case SPACE:
            case CHARACTERS:
                return xmlSecEvent.asCharacters().getData().toCharArray();
            default:
                throw new IllegalStateException("Current state not TEXT");
        }
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case ENTITY_REFERENCE:
                ((EntityReference) xmlSecEvent).getDeclaration().getReplacementText().getChars(sourceStart, sourceStart + length, target, targetStart);
                return sourceStart + length;
            case DTD:
                ((javax.xml.stream.events.DTD) xmlSecEvent).getDocumentTypeDeclaration().getChars(sourceStart, sourceStart + length, target, targetStart);
                return sourceStart + length;
            case COMMENT:
                ((Comment) xmlSecEvent).getText().getChars(sourceStart, sourceStart + length, target, targetStart);
                return sourceStart + length;
            case CDATA:
            case SPACE:
            case CHARACTERS:
                xmlSecEvent.asCharacters().getData().getChars(sourceStart, sourceStart + length, target, targetStart);
                return sourceStart + length;
            default:
                throw new IllegalStateException("Current state not TEXT");
        }
    }

    public int getTextStart() {
        return 0;
    }

    public int getTextLength() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case ENTITY_REFERENCE:
                return ((EntityReference) xmlSecEvent).getDeclaration().getReplacementText().length();
            case DTD:
                return ((javax.xml.stream.events.DTD) xmlSecEvent).getDocumentTypeDeclaration().length();
            case COMMENT:
                return ((Comment) xmlSecEvent).getText().length();
            case CDATA:
            case SPACE:
            case CHARACTERS:
                return xmlSecEvent.asCharacters().getData().length();
            default:
                throw new IllegalStateException("Current state not TEXT");
        }
    }

    public String getEncoding() {
        return inputProcessorChain.getDocumentContext().getEncoding();
    }

    final private static int MASK_GET_TEXT =
            (1 << CHARACTERS) | (1 << CDATA) | (1 << SPACE)
                    | (1 << COMMENT) | (1 << DTD) | (1 << ENTITY_REFERENCE);

    public boolean hasText() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        return (((1 << xmlSecEvent.getEventType()) & MASK_GET_TEXT) != 0);
    }

    public Location getLocation() {
        return new Location() {
            public int getLineNumber() {
                return -1;
            }

            public int getColumnNumber() {
                return -1;
            }

            public int getCharacterOffset() {
                return -1;
            }

            public String getPublicId() {
                return null;
            }

            public String getSystemId() {
                return null;
            }
        };
    }

    public QName getName() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case START_ELEMENT:
                return xmlSecEvent.asStartElement().getName();
            case END_ELEMENT:
                return xmlSecEvent.asEndElement().getName();
            default:
                throw new IllegalStateException(ERR_STATE_NOT_ELEM);
        }
    }

    public String getLocalName() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case START_ELEMENT:
                return xmlSecEvent.asStartElement().getName().getLocalPart();
            case END_ELEMENT:
                return xmlSecEvent.asEndElement().getName().getLocalPart();
            default:
                throw new IllegalStateException(ERR_STATE_NOT_ELEM);
        }
    }

    public boolean hasName() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        return xmlSecEvent.getEventType() == START_ELEMENT || xmlSecEvent.getEventType() == END_ELEMENT;
    }

    public String getNamespaceURI() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case START_ELEMENT:
                return xmlSecEvent.asStartElement().getName().getNamespaceURI();
            case END_ELEMENT:
                return xmlSecEvent.asEndElement().getName().getNamespaceURI();
            default:
                throw new IllegalStateException(ERR_STATE_NOT_ELEM);
        }
    }

    public String getPrefix() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case START_ELEMENT:
                return xmlSecEvent.asStartElement().getName().getPrefix();
            case END_ELEMENT:
                return xmlSecEvent.asEndElement().getName().getPrefix();
            default:
                throw new IllegalStateException(ERR_STATE_NOT_ELEM);
        }
    }

    public String getVersion() {
        return null;
    }

    public boolean isStandalone() {
        return false;
    }

    public boolean standaloneSet() {
        return false;
    }

    public String getCharacterEncodingScheme() {
        return null;
    }

    public String getPITarget() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != PROCESSING_INSTRUCTION) {
            throw new IllegalStateException(ERR_STATE_NOT_PI);
        }
        return ((ProcessingInstruction) xmlSecEvent).getTarget();
    }

    public String getPIData() {
        XMLSecEvent xmlSecEvent = getCurrentEvent();
        if (xmlSecEvent.getEventType() != PROCESSING_INSTRUCTION) {
            throw new IllegalStateException(ERR_STATE_NOT_PI);
        }
        return ((ProcessingInstruction) xmlSecEvent).getData();
    }
}
