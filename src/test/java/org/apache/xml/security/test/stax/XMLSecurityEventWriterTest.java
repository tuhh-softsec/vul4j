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

import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.impl.stax.XMLSecEndElementImpl;
import org.apache.xml.security.stax.impl.stax.XMLSecNamespaceImpl;
import org.apache.xml.security.stax.impl.stax.XMLSecStartElementImpl;
import org.junit.Assert;
import org.junit.Test;

import org.custommonkey.xmlunit.XMLAssert;
import org.apache.xml.security.stax.impl.XMLSecurityEventWriter;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.StringWriter;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecurityEventWriterTest extends org.junit.Assert {

    @Test
    public void testConformness() throws Exception {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        StringWriter secStringWriter = new StringWriter();
        XMLStreamWriter secXmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(secStringWriter);
        XMLSecurityEventWriter xmlSecurityEventWriter = new XMLSecurityEventWriter(secXmlStreamWriter);

        StringWriter stdStringWriter = new StringWriter();
        XMLEventWriter stdXmlEventWriter = xmlOutputFactory.createXMLEventWriter(stdStringWriter);

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLStreamReader xmlStreamReader =
            xmlInputFactory.createXMLStreamReader(this.getClass().getClassLoader().getResourceAsStream(
                    "org/apache/xml/security/c14n/inExcl/plain-soap-1.1.xml"));

        while (xmlStreamReader.hasNext()) {
            XMLEvent xmlEvent = XMLSecEventFactory.allocate(xmlStreamReader, null);
            xmlSecurityEventWriter.add(xmlEvent);
            stdXmlEventWriter.add(xmlEvent);
            xmlStreamReader.next();
        }

        xmlSecurityEventWriter.close();
        stdXmlEventWriter.close();
        XMLAssert.assertXMLEqual(stdStringWriter.toString(), secStringWriter.toString());
    }

    //@see WSS-437
    @Test
    public void testNamespaces() throws Exception {
        StringWriter stringWriter = new StringWriter();
        XMLStreamWriter xmlStreamWriter = XMLSecurityConstants.xmlOutputFactory.createXMLStreamWriter(stringWriter);
        XMLEventWriter xmlEventWriter = new XMLSecurityEventWriter(xmlStreamWriter);
        xmlEventWriter.add(new XMLSecStartElementImpl(new QName("http://ns1", "a", "ns1"), null, null));
        xmlEventWriter.add(XMLSecNamespaceImpl.getInstance("ns1", "http://ns1"));
        xmlEventWriter.add(new XMLSecStartElementImpl(new QName("http://ns2", "b", ""), null, null));
        xmlEventWriter.add(XMLSecNamespaceImpl.getInstance("", "http://ns2"));
        xmlEventWriter.add(new XMLSecEndElementImpl(new QName("http://ns2", "b", ""), null));
        xmlEventWriter.add(new XMLSecStartElementImpl(new QName("http://ns3", "c", ""), null, null));
        xmlEventWriter.close();

        Assert.assertEquals(
                "<ns1:a xmlns:ns1=\"http://ns1\">" +
                        "<b xmlns=\"http://ns2\"/>" +
                        "<c xmlns=\"http://ns3\">" +
                        "</c>" +
                        "</ns1:a>",
                stringWriter.toString());
    }
}
