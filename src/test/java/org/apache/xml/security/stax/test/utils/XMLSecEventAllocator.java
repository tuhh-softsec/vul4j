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
package org.apache.xml.security.stax.test.utils;

import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.stream.util.XMLEventConsumer;

/**
 * <p/>
 * An extended XMLEventAllocator to collect namespaces and C14N relevant attributes
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecEventAllocator implements XMLEventAllocator {

    private XMLEventAllocator xmlEventAllocator;
    private XMLSecStartElement parentXmlSecStartElement;

    public XMLSecEventAllocator() throws Exception {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        if (xmlInputFactory.getClass().getName().equals("com.sun.xml.internal.stream.XMLInputFactoryImpl")) {
            xmlEventAllocator = (XMLEventAllocator) Class.forName("com.sun.xml.internal.stream.events.XMLEventAllocatorImpl").newInstance();
        } else if (xmlInputFactory.getClass().getName().equals("com.ctc.wstx.stax.WstxInputFactory")) {
            xmlEventAllocator = (XMLEventAllocator) Class.forName("com.ctc.wstx.evt.DefaultEventAllocator").getMethod("getDefaultInstance").invoke(null);
        } else {
            throw new Exception("Unknown XMLEventAllocator");
        }
    }

    public XMLEventAllocator newInstance() {
        try {
            return new XMLSecEventAllocator();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public XMLEvent allocate(XMLStreamReader xmlStreamReader) throws XMLStreamException {
        XMLSecEvent xmlSecEvent = XMLSecEventFactory.allocate(xmlStreamReader, parentXmlSecStartElement);
        switch (xmlSecEvent.getEventType()) {
            case XMLStreamConstants.START_ELEMENT:
                parentXmlSecStartElement = (XMLSecStartElement) xmlSecEvent;
                break;
            case XMLStreamConstants.END_ELEMENT:
                if (parentXmlSecStartElement != null) {
                    parentXmlSecStartElement = parentXmlSecStartElement.getParentXMLSecStartElement();
                }
                break;
        }
        return xmlSecEvent;
    }

    public void allocate(XMLStreamReader reader, XMLEventConsumer consumer) throws XMLStreamException {
        xmlEventAllocator.allocate(reader, consumer);
    }
}
