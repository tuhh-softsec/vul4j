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
package org.apache.xml.security.test.stax;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.Init;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.custommonkey.xmlunit.XMLAssert;
import org.apache.xml.security.stax.ext.*;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.impl.DocumentContextImpl;
import org.apache.xml.security.stax.impl.InputProcessorChainImpl;
import org.apache.xml.security.stax.impl.InboundSecurityContextImpl;
import org.apache.xml.security.stax.impl.XMLSecurityStreamReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecurityStreamReaderTest extends org.junit.Assert {

    @Before
    public void setUp() throws Exception {
        Init.init(this.getClass().getClassLoader().getResource("security-config.xml").toURI(),
                this.getClass());
    }

    @Test
    public void testPassThroughDocumentEvents() throws Exception {
        XMLSecurityProperties securityProperties = new XMLSecurityProperties();
        securityProperties.setSkipDocumentEvents(false);
        InboundSecurityContextImpl securityContext = new InboundSecurityContextImpl();
        InputProcessorChainImpl inputProcessorChain = new InputProcessorChainImpl(securityContext);
        inputProcessorChain.addProcessor(new EventReaderProcessor());
        XMLSecurityStreamReader xmlSecurityStreamReader = new XMLSecurityStreamReader(inputProcessorChain, securityProperties);
        int event = xmlSecurityStreamReader.next();
        Assert.assertEquals(XMLStreamConstants.START_DOCUMENT, event);
    }

    @Test
    public void testSkipThroughDocumentEvents() throws Exception {
        XMLSecurityProperties securityProperties = new XMLSecurityProperties();
        securityProperties.setSkipDocumentEvents(true);
        InboundSecurityContextImpl securityContext = new InboundSecurityContextImpl();
        InputProcessorChainImpl inputProcessorChain = new InputProcessorChainImpl(securityContext);
        inputProcessorChain.addProcessor(new EventReaderProcessor());
        XMLSecurityStreamReader xmlSecurityStreamReader = new XMLSecurityStreamReader(inputProcessorChain, securityProperties);
        int event = xmlSecurityStreamReader.next();
        Assert.assertEquals(XMLStreamConstants.START_ELEMENT, event);
    }

    @Test
    public void testIdentityTransformSource() throws Exception {
        XMLSecurityProperties securityProperties = new XMLSecurityProperties();
        InboundSecurityContextImpl securityContext = new InboundSecurityContextImpl();
        InputProcessorChainImpl inputProcessorChain = new InputProcessorChainImpl(securityContext);
        inputProcessorChain.addProcessor(new EventReaderProcessor());
        XMLSecurityStreamReader xmlSecurityStreamReader = new XMLSecurityStreamReader(inputProcessorChain, securityProperties);
        //use the sun internal TransformerFactory since the current xalan version don't know how to handle StaxSources:
        TransformerFactory transformerFactory = TransformerFactory.newInstance("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", this.getClass().getClassLoader());
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new StAXSource(xmlSecurityStreamReader), new StreamResult(baos));
        XMLAssert.assertXMLEqual(readTestFile(), baos.toString("UTF-8"));
    }

    @Test
    public void testCorrectness() throws Exception {
        XMLSecurityProperties securityProperties = new XMLSecurityProperties();
        InboundSecurityContextImpl securityContext = new InboundSecurityContextImpl();
        DocumentContextImpl documentContext = new DocumentContextImpl();
        documentContext.setEncoding("UTF-8");
        InputProcessorChainImpl inputProcessorChain = new InputProcessorChainImpl(securityContext, documentContext);
        inputProcessorChain.addProcessor(new EventReaderProcessor());
        XMLSecurityStreamReader xmlSecurityStreamReader = new XMLSecurityStreamReader(inputProcessorChain, securityProperties);

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);
        xmlInputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
        XMLStreamReader stdXmlStreamReader = 
            xmlInputFactory.createXMLStreamReader(this.getClass().getClassLoader().getResourceAsStream(
                "org/apache/xml/security/c14n/inExcl/plain-soap-1.1.xml"));

        //hmm why does a streamreader return a DOCUMENT_EVENT before we did call next() ??
        int stdXMLEventType = stdXmlStreamReader.getEventType();
        int secXMLEventType = xmlSecurityStreamReader.getEventType();
        do {
            switch (stdXMLEventType) {
                case XMLStreamConstants.START_ELEMENT:
                    Assert.assertTrue(xmlSecurityStreamReader.isStartElement());
                    Assert.assertFalse(xmlSecurityStreamReader.isEndElement());
                    Assert.assertEquals(stdXmlStreamReader.getLocalName(), xmlSecurityStreamReader.getLocalName());
                    Assert.assertEquals(stdXmlStreamReader.getName(), xmlSecurityStreamReader.getName());
                    Assert.assertEquals(stdXmlStreamReader.getNamespaceURI(), xmlSecurityStreamReader.getNamespaceURI());
                    if (stdXmlStreamReader.getPrefix() == null) {
                        Assert.assertEquals("", xmlSecurityStreamReader.getPrefix());
                    } else {
                        Assert.assertEquals(stdXmlStreamReader.getPrefix(), xmlSecurityStreamReader.getPrefix());
                    }
                    Assert.assertEquals(stdXmlStreamReader.hasName(), xmlSecurityStreamReader.hasName());
                    Assert.assertEquals(stdXmlStreamReader.hasText(), xmlSecurityStreamReader.hasText());
                    Assert.assertEquals(stdXmlStreamReader.getAttributeCount(), xmlSecurityStreamReader.getAttributeCount());
                    Assert.assertEquals(stdXmlStreamReader.getNamespaceCount(), xmlSecurityStreamReader.getNamespaceCount());
                    for (int i = 0; i < stdXmlStreamReader.getAttributeCount(); i++) {
                        Assert.assertEquals(stdXmlStreamReader.getAttributeLocalName(i), xmlSecurityStreamReader.getAttributeLocalName(i));
                        Assert.assertEquals(stdXmlStreamReader.getAttributeName(i), xmlSecurityStreamReader.getAttributeName(i));
                        if (stdXmlStreamReader.getAttributeNamespace(i) == null) {
                            Assert.assertEquals("", xmlSecurityStreamReader.getAttributeNamespace(i));
                        } else {
                            Assert.assertEquals(stdXmlStreamReader.getAttributeNamespace(i), xmlSecurityStreamReader.getAttributeNamespace(i));
                        }
                        if (stdXmlStreamReader.getAttributePrefix(i) == null) {
                            Assert.assertEquals("", xmlSecurityStreamReader.getAttributePrefix(i));
                        } else {
                            Assert.assertEquals(stdXmlStreamReader.getAttributePrefix(i), xmlSecurityStreamReader.getAttributePrefix(i));
                        }
                        Assert.assertEquals(stdXmlStreamReader.getAttributeType(i), xmlSecurityStreamReader.getAttributeType(i));
                        Assert.assertEquals(stdXmlStreamReader.getAttributeValue(i), xmlSecurityStreamReader.getAttributeValue(i));
                    }
                    for (int i = 0; i < stdXmlStreamReader.getNamespaceCount(); i++) {
                        if (stdXmlStreamReader.getNamespacePrefix(i) == null) {
                            Assert.assertEquals("", xmlSecurityStreamReader.getNamespacePrefix(i));
                        } else {
                            Assert.assertEquals(stdXmlStreamReader.getNamespacePrefix(i), xmlSecurityStreamReader.getNamespacePrefix(i));
                        }
                        Assert.assertEquals(stdXmlStreamReader.getNamespaceURI(i), xmlSecurityStreamReader.getNamespaceURI(i));
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    Assert.assertFalse(xmlSecurityStreamReader.isStartElement());
                    Assert.assertTrue(xmlSecurityStreamReader.isEndElement());
                    Assert.assertEquals(stdXmlStreamReader.getLocalName(), xmlSecurityStreamReader.getLocalName());
                    Assert.assertEquals(stdXmlStreamReader.getName(), xmlSecurityStreamReader.getName());
                    Assert.assertEquals(stdXmlStreamReader.getNamespaceURI(), xmlSecurityStreamReader.getNamespaceURI());
                    if (stdXmlStreamReader.getPrefix() == null) {
                        Assert.assertEquals("", xmlSecurityStreamReader.getPrefix());
                    } else {
                        Assert.assertEquals(stdXmlStreamReader.getPrefix(), xmlSecurityStreamReader.getPrefix());
                    }
                    Assert.assertEquals(stdXmlStreamReader.hasName(), xmlSecurityStreamReader.hasName());
                    Assert.assertEquals(stdXmlStreamReader.hasText(), xmlSecurityStreamReader.hasText());
                    break;
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                    Assert.assertEquals(stdXmlStreamReader.isCharacters(), xmlSecurityStreamReader.isCharacters());
                    Assert.assertEquals(stdXmlStreamReader.getPITarget(), xmlSecurityStreamReader.getPITarget());
                    Assert.assertEquals(stdXmlStreamReader.getPIData(), xmlSecurityStreamReader.getPIData());
                    break;
                case XMLStreamConstants.CHARACTERS:
                    Assert.assertEquals(stdXmlStreamReader.isCharacters(), xmlSecurityStreamReader.isCharacters());
                    Assert.assertEquals(stdXmlStreamReader.isWhiteSpace(), xmlSecurityStreamReader.isWhiteSpace());
                    Assert.assertEquals(stdXmlStreamReader.getText(), xmlSecurityStreamReader.getText());
                    Assert.assertEquals(
                            new String(stdXmlStreamReader.getTextCharacters(), stdXmlStreamReader.getTextStart(), stdXmlStreamReader.getTextLength()),
                            new String(xmlSecurityStreamReader.getTextCharacters(), xmlSecurityStreamReader.getTextStart(), xmlSecurityStreamReader.getTextLength()));
                    Assert.assertEquals(stdXmlStreamReader.getTextLength(), xmlSecurityStreamReader.getTextLength());
                    break;
                case XMLStreamConstants.COMMENT:
                    Assert.assertEquals(stdXmlStreamReader.isCharacters(), xmlSecurityStreamReader.isCharacters());
                    Assert.assertEquals(stdXmlStreamReader.isWhiteSpace(), xmlSecurityStreamReader.isWhiteSpace());
                    Assert.assertEquals(stdXmlStreamReader.getText(), xmlSecurityStreamReader.getText());
                    Assert.assertEquals(
                            new String(stdXmlStreamReader.getTextCharacters(), stdXmlStreamReader.getTextStart(), stdXmlStreamReader.getTextLength()),
                            new String(xmlSecurityStreamReader.getTextCharacters(), xmlSecurityStreamReader.getTextStart(), xmlSecurityStreamReader.getTextLength()));
                    Assert.assertEquals(stdXmlStreamReader.getTextLength(), xmlSecurityStreamReader.getTextLength());
                    break;
                case XMLStreamConstants.SPACE:
                    Assert.assertEquals(stdXmlStreamReader.isWhiteSpace(), xmlSecurityStreamReader.isWhiteSpace());
                    Assert.assertEquals(stdXmlStreamReader.getText(), xmlSecurityStreamReader.getText());
                    Assert.assertEquals(
                            new String(stdXmlStreamReader.getTextCharacters(), stdXmlStreamReader.getTextStart(), stdXmlStreamReader.getTextLength()),
                            new String(xmlSecurityStreamReader.getTextCharacters(), xmlSecurityStreamReader.getTextStart(), xmlSecurityStreamReader.getTextLength()));
                    Assert.assertEquals(stdXmlStreamReader.getTextLength(), xmlSecurityStreamReader.getTextLength());
                    break;
                case XMLStreamConstants.START_DOCUMENT:
                    Assert.assertEquals(stdXmlStreamReader.getCharacterEncodingScheme(), xmlSecurityStreamReader.getCharacterEncodingScheme());
                    Assert.assertEquals(stdXmlStreamReader.getEncoding(), xmlSecurityStreamReader.getEncoding());
                    //Assert.assertEquals(stdXmlStreamReader.getVersion(), xmlSecurityStreamReader.getVersion());
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    break;
                case XMLStreamConstants.ENTITY_REFERENCE:
                    Assert.assertEquals(stdXmlStreamReader.isCharacters(), xmlSecurityStreamReader.isCharacters());
                    Assert.assertEquals(stdXmlStreamReader.getText(), xmlSecurityStreamReader.getText());
                    break;
                case XMLStreamConstants.ATTRIBUTE:
                    break;
                case XMLStreamConstants.DTD:
                    Assert.assertEquals(stdXmlStreamReader.isCharacters(), xmlSecurityStreamReader.isCharacters());
                    break;
                case XMLStreamConstants.CDATA:
                    Assert.assertEquals(stdXmlStreamReader.isCharacters(), xmlSecurityStreamReader.isCharacters());
                    break;
                case XMLStreamConstants.NAMESPACE:
                    break;
                case XMLStreamConstants.NOTATION_DECLARATION:
                    break;
                case XMLStreamConstants.ENTITY_DECLARATION:
                    Assert.assertEquals(stdXmlStreamReader.isCharacters(), xmlSecurityStreamReader.isCharacters());
                    break;
            }
            //hmm2 an eventreader returns a CHARACTER EVENT for an ignorable whitespace whereby a streamReader returns it as SPACE
            if (stdXMLEventType == XMLStreamConstants.SPACE && secXMLEventType == XMLStreamConstants.CHARACTERS) {
                secXMLEventType = XMLStreamConstants.SPACE;
            }
            Assert.assertEquals(stdXMLEventType, secXMLEventType);
            if (stdXmlStreamReader.hasNext()) {
                Assert.assertTrue(xmlSecurityStreamReader.hasNext());
                stdXMLEventType = stdXmlStreamReader.next();
                secXMLEventType = xmlSecurityStreamReader.next();
            } else {
                Assert.assertFalse(xmlSecurityStreamReader.hasNext());
                break;
            }
        } while (true);
    }

    private String readTestFile() throws Exception {
        InputStream inputStream = 
            this.getClass().getClassLoader().getResourceAsStream(
                "org/apache/xml/security/c14n/inExcl/plain-soap-1.1.xml");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        char[] buf = new char[1024];
        int read;
        StringBuilder stringBuilder = new StringBuilder();
        while ((read = bufferedReader.read(buf)) != -1) {
            stringBuilder.append(buf, 0, read);
        }
        return stringBuilder.toString();
    }

    class EventReaderProcessor implements InputProcessor {

        private XMLStreamReader xmlStreamReader;

        EventReaderProcessor() throws Exception {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);
            xmlInputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
            xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(this.getClass().getClassLoader().getResourceAsStream(
                    "org/apache/xml/security/c14n/inExcl/plain-soap-1.1.xml"));
        }

        @Override
        public void addBeforeProcessor(Object processor) {
        }

        @Override
        public Set<Object> getBeforeProcessors() {
            return new HashSet<Object>();
        }

        @Override
        public void addAfterProcessor(Object processor) {
        }

        @Override
        public Set<Object> getAfterProcessors() {
            return new HashSet<Object>();
        }

        @Override
        public XMLSecurityConstants.Phase getPhase() {
            return XMLSecurityConstants.Phase.PROCESSING;
        }

        @Override
        public XMLSecEvent processNextHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
            return null;
        }

        @Override
        public XMLSecEvent processNextEvent(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
            inputProcessorChain.reset();
            XMLSecEvent xmlSecEvent = XMLSecEventFactory.allocate(xmlStreamReader, null);
            if (xmlStreamReader.hasNext()) {
                xmlStreamReader.next();
            }
            return xmlSecEvent;
        }

        @Override
        public void doFinal(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
        }
    }
}
