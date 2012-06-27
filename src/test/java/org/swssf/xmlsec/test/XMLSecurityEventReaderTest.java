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
package org.swssf.xmlsec.test;

import org.junit.Assert;
import org.junit.Test;

import org.swssf.xmlsec.ext.stax.XMLSecEvent;
import org.swssf.xmlsec.ext.stax.XMLSecEventFactory;
import org.swssf.xmlsec.impl.XMLSecurityEventReader;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecurityEventReaderTest extends org.junit.Assert {

    @Test
    public void testConformness() throws Exception {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLStreamReader xmlStreamReader = 
            xmlInputFactory.createXMLStreamReader(this.getClass().getClassLoader().getResourceAsStream(
                "org/apache/xml/security/c14n/inExcl/plain-soap-1.1.xml"));

        Deque<XMLSecEvent> xmlSecEventDeque = new ArrayDeque<XMLSecEvent>();
        do {
            xmlSecEventDeque.push(XMLSecEventFactory.allocate(xmlStreamReader, null));
            xmlStreamReader.next();
        }
        while (xmlStreamReader.hasNext());
        xmlSecEventDeque.push((XMLSecEventFactory.allocate(xmlStreamReader, null)));//EndDocumentEvent

        XMLSecurityEventReader xmlSecurityEventReader = new XMLSecurityEventReader(xmlSecEventDeque, 0);

        XMLEventReader xmlEventReader = 
            xmlInputFactory.createXMLEventReader(this.getClass().getClassLoader().getResourceAsStream(
                "org/apache/xml/security/c14n/inExcl/plain-soap-1.1.xml"));
        while (xmlEventReader.hasNext()) {
            Assert.assertEquals(xmlEventReader.hasNext(), xmlSecurityEventReader.hasNext());
            XMLEvent stdXmlEvent = xmlEventReader.nextEvent();
            XMLEvent secXmlEvent = xmlSecurityEventReader.nextEvent();
            Assert.assertEquals(stdXmlEvent.getEventType(), secXmlEvent.getEventType());

            XMLEvent stdPeekedXMLEvent = xmlEventReader.peek();
            XMLEvent secPeekedXMLEvent = xmlSecurityEventReader.peek();
            if (stdPeekedXMLEvent == null) {
                Assert.assertNull(secPeekedXMLEvent);
            } else {
                Assert.assertEquals(stdPeekedXMLEvent.getEventType(), secPeekedXMLEvent.getEventType());
            }
        }

        Assert.assertFalse(xmlEventReader.hasNext());
        Assert.assertFalse(xmlSecurityEventReader.hasNext());
    }

    @Test
    public void testIndex() throws Exception {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLStreamReader xmlStreamReader = 
            xmlInputFactory.createXMLStreamReader(this.getClass().getClassLoader().getResourceAsStream(
                "org/apache/xml/security/c14n/inExcl/plain-soap-1.1.xml"));

        Deque<XMLSecEvent> xmlSecEventDeque = new ArrayDeque<XMLSecEvent>();
        do {
            xmlSecEventDeque.push(XMLSecEventFactory.allocate(xmlStreamReader, null));
            xmlStreamReader.next();
        }
        while (xmlStreamReader.hasNext());
        xmlSecEventDeque.push((XMLSecEventFactory.allocate(xmlStreamReader, null)));//EndDocumentEvent

        int skip = 100;

        XMLSecurityEventReader xmlSecurityEventReader = new XMLSecurityEventReader(xmlSecEventDeque, skip);

        XMLEventReader xmlEventReader = 
            xmlInputFactory.createXMLEventReader(this.getClass().getClassLoader().getResourceAsStream(
                "org/apache/xml/security/c14n/inExcl/plain-soap-1.1.xml"));
        int currentIndex = 0;
        while (xmlEventReader.hasNext()) {
            XMLEvent stdXmlEvent = xmlEventReader.nextEvent();

            if (currentIndex++ < skip) {
                continue;
            }

            XMLEvent secXmlEvent = xmlSecurityEventReader.nextEvent();
            Assert.assertEquals(stdXmlEvent.getEventType(), secXmlEvent.getEventType());

            XMLEvent stdPeekedXMLEvent = xmlEventReader.peek();
            XMLEvent secPeekedXMLEvent = xmlSecurityEventReader.peek();
            if (stdPeekedXMLEvent == null) {
                Assert.assertNull(secPeekedXMLEvent);
            } else {
                Assert.assertEquals(stdPeekedXMLEvent.getEventType(), secPeekedXMLEvent.getEventType());
            }
        }

        Assert.assertFalse(xmlEventReader.hasNext());
        Assert.assertFalse(xmlSecurityEventReader.hasNext());
    }
}
