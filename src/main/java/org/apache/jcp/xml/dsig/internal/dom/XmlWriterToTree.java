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
package org.apache.jcp.xml.dsig.internal.dom;

import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Manifestation of XmlWriter interface designed to write to a tree.
 */
public class XmlWriterToTree implements XmlWriter {

    public XmlWriterToTree(List<XmlWriter.ToMarshal<? extends XMLStructure>> marshallers, Node parent) {
        m_marshallers = marshallers;
        m_factory = parent instanceof Document ? (Document)parent : parent.getOwnerDocument();
        m_currentNode = parent;
    }
    
    /**
     * Reset to a new parent so that the writer can be re-used.
     * @param newParent
     */
    public void resetToNewParent(Node newParent) {
        m_currentNode = newParent;
        m_createdElement = null;
    }
    
    /**
     * Get the root element created with this writer.
     * @return the root element created with this writer.
     */
    public Element getCreatedElement() {
        return m_createdElement;
    }
    
    /**
     * In cases where the serialization is supposed to precede a specific
     * element, we add an extra parameter to capture that. Only affects the
     * first element insertion (obviously?).
     * 
     * @param marshallers
     * @param parent
     * @param nextSibling The first element created will be created *before* this element.
     */
    public XmlWriterToTree(List<XmlWriter.ToMarshal<? extends XMLStructure>> marshallers, Node parent, Node nextSibling) {
        this(marshallers, parent);
        m_nextSibling = nextSibling;
    }
    
    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI) {
        Element newElem = m_factory.createElementNS(namespaceURI, DOMUtils.getQNameString(prefix, localName));
        if (m_nextSibling != null) {
            newElem = (Element)m_nextSibling.getParentNode().insertBefore(newElem, m_nextSibling);
        }
        else {
            newElem = (Element)m_currentNode.appendChild(newElem);
        }
        m_nextSibling = null;
        m_currentNode = newElem;
        
        if (m_createdElement == null) {
            m_createdElement = newElem;
        }
    }

    @Override
    public void writeEndElement() {
        m_currentNode = m_currentNode.getParentNode();
    }

    
    @Override
    public void writeTextElement(String prefix, String localName, String namespaceURI, String value) {
        writeStartElement(prefix, localName, namespaceURI);
        writeCharacters(value);
        writeEndElement();
    }

    @Override
    public void writeNamespace(String prefix, String namespaceURI) {
        if ("".equals(prefix) || prefix == null) {
            writeAttribute(null, XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns", namespaceURI);
        }
        else {
            writeAttribute("xmlns", XMLConstants.XMLNS_ATTRIBUTE_NS_URI, prefix, namespaceURI);
        }
    }

    @Override
    public void writeCharacters(String text) {
        Text textNode = m_factory.createTextNode(text);
        m_currentNode.appendChild(textNode);
    }
    

    @Override
    public void writeComment(String text) {
        Comment commentNode = m_factory.createComment(text);
        m_currentNode.appendChild(commentNode);
    }

    @Override
    public Attr writeAttribute(String prefix, String namespaceURI, String localName, String value) {

        Attr result = null;
        if (value != null) {
            result = m_factory.createAttributeNS(namespaceURI, DOMUtils.getQNameString(prefix, localName));
            result.setTextContent(value);
            if (! (m_currentNode instanceof Element)) {
                throw new IllegalStateException(
                        "Attempting to add an attribute to something other than an element node. Node is "
                                + m_currentNode.toString());
            }
            ( (Element)m_currentNode).setAttributeNodeNS(result);
        }
        return result;
    }

    @Override
    public void writeIdAttribute(String prefix, String namespaceURI, String localName, String value) {
        if (value == null) {
            return;
        }
        Attr newAttr = writeAttribute(prefix, namespaceURI, localName, value);
        ( (Element)m_currentNode).setIdAttributeNode(newAttr, true);
    }


    @Override
    public String getCurrentLocalName() {
        return m_currentNode.getLocalName();
    }

    @Override
    public XMLStructure getCurrentNodeAsStructure() {
        return new DOMStructure(m_currentNode);
    }

    @Override
    public void marshalStructure(XMLStructure toMarshal, String dsPrefix, XMLCryptoContext context) throws MarshalException {
        
        // look for the first isInstance match, and marshal to that.
        for (int idx = 0 ; idx < m_marshallers.size() ; idx++) {
            @SuppressWarnings("unchecked")
            XmlWriter.ToMarshal<XMLStructure> marshaller = (ToMarshal<XMLStructure>) m_marshallers.get(idx);
            if (marshaller.clazzToMatch.isInstance(toMarshal)) {
                marshaller.marshalObject(this, toMarshal, dsPrefix, context);
                return;
            }
        }
        throw new IllegalArgumentException("Unable to marshal unexpected object of class " + toMarshal.getClass().toString());
    }

    private Document m_factory;
    
    private Element m_createdElement;
    
    private Node m_nextSibling;
    
    private Node m_currentNode;

    private List<XmlWriter.ToMarshal<? extends XMLStructure>> m_marshallers;
    
}
