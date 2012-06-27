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

import org.custommonkey.xmlunit.XMLAssert;
import org.swssf.xmlsec.impl.XMLSecurityEventWriter;
import org.testng.annotations.Test;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.StringWriter;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecurityEventWriterTest {

    @Test
    public void testConformness() throws Exception {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        StringWriter secStringWriter = new StringWriter();
        XMLStreamWriter secXmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(secStringWriter);
        XMLSecurityEventWriter xmlSecurityEventWriter = new XMLSecurityEventWriter(secXmlStreamWriter);

        StringWriter stdStringWriter = new StringWriter();
        XMLEventWriter stdXmlEventWriter = xmlOutputFactory.createXMLEventWriter(stdStringWriter);

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(this.getClass().getClassLoader().getResourceAsStream("testdata/plain-soap-1.1.xml"));

        while (xmlEventReader.hasNext()) {
            XMLEvent xmlEvent = xmlEventReader.nextEvent();
            xmlSecurityEventWriter.add(xmlEvent);
            stdXmlEventWriter.add(xmlEvent);
        }

        xmlSecurityEventWriter.close();
        stdXmlEventWriter.close();
        XMLAssert.assertXMLEqual(stdStringWriter.toString(), secStringWriter.toString());
    }
}
