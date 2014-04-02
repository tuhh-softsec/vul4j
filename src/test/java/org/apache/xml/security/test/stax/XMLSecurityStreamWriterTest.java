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
import org.apache.xml.security.stax.impl.OutboundSecurityContextImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.custommonkey.xmlunit.XMLAssert;
import org.apache.xml.security.stax.ext.*;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.OutputProcessorChainImpl;
import org.apache.xml.security.stax.impl.XMLSecurityStreamWriter;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecurityStreamWriterTest extends org.junit.Assert {

    @Before
    public void setUp() throws Exception {
        Init.init(this.getClass().getClassLoader().getResource("security-config.xml").toURI(),
                this.getClass());
    }

    @Test
    public void testIdentityTransformResult() throws Exception {
        StringWriter securityStringWriter = new StringWriter();
        OutboundSecurityContextImpl securityContext = new OutboundSecurityContextImpl();
        OutputProcessorChainImpl outputProcessorChain = new OutputProcessorChainImpl(securityContext);
        outputProcessorChain.addProcessor(new EventWriterProcessor(securityStringWriter));
        XMLSecurityStreamWriter xmlSecurityStreamWriter = new XMLSecurityStreamWriter(outputProcessorChain);

        StringWriter stdStringWriter = new StringWriter();
        XMLStreamWriter stdXmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(stdStringWriter);

        NamespaceContext namespaceContext = new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                if ("t3".equals(prefix)) {
                    return "test3ns";
                }
                return null;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                if ("test2ns".equals(namespaceURI)) {
                    return "t2";
                } else if ("test3ns".equals(namespaceURI)) {
                    return "t3";
                }
                return null;
            }

            @Override
            public Iterator<?> getPrefixes(String namespaceURI) {
                List<String> ns = new ArrayList<String>();
                ns.add(getPrefix(namespaceURI));
                return ns.iterator();
            }
        };

        xmlSecurityStreamWriter.setNamespaceContext(namespaceContext);
        stdXmlStreamWriter.setNamespaceContext(namespaceContext);
        xmlSecurityStreamWriter.writeStartDocument("UTF-8", "1.0");
        stdXmlStreamWriter.writeStartDocument("UTF-8", "1.0");

        xmlSecurityStreamWriter.writeDTD("<!DOCTYPE foobar [\n\t<!ENTITY x0 \"hello\">\n]>");
        stdXmlStreamWriter.writeDTD("<!DOCTYPE foobar [\n\t<!ENTITY x0 \"hello\">\n]>");

        xmlSecurityStreamWriter.writeStartElement("test1");
        stdXmlStreamWriter.writeStartElement("test1");

        xmlSecurityStreamWriter.writeDefaultNamespace("defaultns");
        stdXmlStreamWriter.writeDefaultNamespace("defaultns");

        xmlSecurityStreamWriter.writeNamespace("t2new", "test2ns");
        stdXmlStreamWriter.writeNamespace("t2new", "test2ns");

        xmlSecurityStreamWriter.writeStartElement("test2ns", "test2");
        stdXmlStreamWriter.writeStartElement("test2ns", "test2");

        xmlSecurityStreamWriter.writeNamespace("t2", "test2ns");
        stdXmlStreamWriter.writeNamespace("t2", "test2ns");

        xmlSecurityStreamWriter.writeStartElement("t3", "test3", "test3ns");
        stdXmlStreamWriter.writeStartElement("t3", "test3", "test3ns");

        xmlSecurityStreamWriter.writeNamespace("t3", "test3ns");
        stdXmlStreamWriter.writeNamespace("t3", "test3ns");

        xmlSecurityStreamWriter.writeNamespace("t4", "test4ns");
        stdXmlStreamWriter.writeNamespace("t4", "test4ns");

        xmlSecurityStreamWriter.writeStartElement("test4ns", "test4");
        stdXmlStreamWriter.writeStartElement("test4ns", "test4");

        xmlSecurityStreamWriter.writeAttribute("attr1", "attr1val");
        stdXmlStreamWriter.writeAttribute("attr1", "attr1val");

        xmlSecurityStreamWriter.writeAttribute("t2", "test2ns", "attr2", "attr2val");
        stdXmlStreamWriter.writeAttribute("t2", "test2ns", "attr2", "attr2val");

        xmlSecurityStreamWriter.writeAttribute("test3ns", "attr3", "attr3val");
        stdXmlStreamWriter.writeAttribute("test3ns", "attr3", "attr3val");

        xmlSecurityStreamWriter.writeEmptyElement("test1");
        stdXmlStreamWriter.writeEmptyElement("test1");

        xmlSecurityStreamWriter.setPrefix("t2new", "test2ns");
        stdXmlStreamWriter.setPrefix("t2new", "test2ns");

        xmlSecurityStreamWriter.writeEmptyElement("test2ns", "test2");
        stdXmlStreamWriter.writeEmptyElement("test2ns", "test2");

        xmlSecurityStreamWriter.writeEmptyElement("t2", "test2ns", "test2");
        stdXmlStreamWriter.writeEmptyElement("t2", "test2ns", "test2");

        xmlSecurityStreamWriter.writeEmptyElement("test2ns", "test2");
        stdXmlStreamWriter.writeEmptyElement("test2ns", "test2");

        xmlSecurityStreamWriter.writeEmptyElement("t3", "test3", "test3ns");
        stdXmlStreamWriter.writeEmptyElement("t3", "test3", "test3ns");

        xmlSecurityStreamWriter.writeCharacters("\n");
        stdXmlStreamWriter.writeCharacters("\n");

        xmlSecurityStreamWriter.writeCData("Hi");
        stdXmlStreamWriter.writeCData("Hi");

        xmlSecurityStreamWriter.writeComment("this is a comment");
        stdXmlStreamWriter.writeComment("this is a comment");

        xmlSecurityStreamWriter.writeCharacters("abcdcba".toCharArray(), 3, 1);
        stdXmlStreamWriter.writeCharacters("abcdcba".toCharArray(), 3, 1);

        xmlSecurityStreamWriter.writeEntityRef("x0");
        stdXmlStreamWriter.writeEntityRef("x0");

        xmlSecurityStreamWriter.writeEndElement();
        stdXmlStreamWriter.writeEndElement();

        xmlSecurityStreamWriter.writeProcessingInstruction("PI");
        stdXmlStreamWriter.writeProcessingInstruction("PI");

        xmlSecurityStreamWriter.writeProcessingInstruction("PI", "there");
        stdXmlStreamWriter.writeProcessingInstruction("PI", "there");

        Assert.assertEquals(xmlSecurityStreamWriter.getPrefix("test4ns"), stdXmlStreamWriter.getPrefix("test4ns"));

        stdXmlStreamWriter.close();
        xmlSecurityStreamWriter.close();

        XMLAssert.assertXMLEqual(stdStringWriter.toString(), securityStringWriter.toString());
    }

    class EventWriterProcessor implements OutputProcessor {

        private XMLEventWriter xmlEventWriter;

        EventWriterProcessor(Writer writer) throws Exception {
            XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
            xmlEventWriter = xmlOutputFactory.createXMLEventWriter(writer);
        }

        @Override
        public void setXMLSecurityProperties(XMLSecurityProperties xmlSecurityProperties) {
        }

        @Override
        public void setAction(XMLSecurityConstants.Action action) {
        }

        @Override
        public void init(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
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
            return XMLSecurityConstants.Phase.POSTPROCESSING;
        }

        @Override
        public void processNextEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
            outputProcessorChain.reset();
            xmlEventWriter.add(xmlSecEvent);
        }

        @Override
        public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        }
    }
}
