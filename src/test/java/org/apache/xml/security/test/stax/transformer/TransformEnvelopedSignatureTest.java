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
package org.apache.xml.security.test.stax.transformer;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.Init;
import org.apache.xml.security.stax.ext.Transformer;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.transformer.TransformEnvelopedSignature;
import org.apache.xml.security.test.stax.utils.XMLSecEventAllocator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class TransformEnvelopedSignatureTest extends org.junit.Assert {

    private XMLInputFactory xmlInputFactory;

    @Before
    public void setUp() throws Exception {
        Init.init(this.getClass().getClassLoader().getResource("security-config.xml").toURI(),
                this.getClass());
        this.xmlInputFactory = XMLInputFactory.newInstance();
        this.xmlInputFactory.setEventAllocator(new XMLSecEventAllocator());
    }

    @Test
    public void testXMLSecEventToXMLSecEventAPI() throws Exception {
        TransformEnvelopedSignature transformEnvelopedSignature = new TransformEnvelopedSignature();

        final List<XMLSecEvent> xmlSecEvents = new ArrayList<XMLSecEvent>();

        Transformer transformer = new Transformer() {
            @Override
            public void setOutputStream(OutputStream outputStream) throws XMLSecurityException {
            }

            @Override
            public void setTransformer(Transformer transformer) throws XMLSecurityException {
            }

            @Override
            public void setProperties(Map<String, Object> properties) throws XMLSecurityException {
            }

            @Override
            public XMLSecurityConstants.TransformMethod getPreferredTransformMethod(XMLSecurityConstants.TransformMethod forInput) {
                return XMLSecurityConstants.TransformMethod.XMLSecEvent;
            }

            @Override
            public void transform(XMLSecEvent xmlSecEvent) throws XMLStreamException {
                xmlSecEvents.add(xmlSecEvent);
            }

            @Override
            public void transform(InputStream inputStream) throws XMLStreamException {
                Assert.fail("unexpected call to transform(InputStream");
            }

            @Override
            public void doFinal() throws XMLStreamException {
            }
        };
        transformEnvelopedSignature.setTransformer(transformer);

        XMLEventReader xmlSecEventReader = xmlInputFactory.createXMLEventReader(
                this.getClass().getClassLoader().getResourceAsStream(
                        "com/phaos/phaos-xmldsig-three/signature-rsa-enveloped.xml")
        );

        while (xmlSecEventReader.hasNext()) {
            XMLSecEvent xmlSecEvent = (XMLSecEvent) xmlSecEventReader.nextEvent();
            transformEnvelopedSignature.transform(xmlSecEvent);
        }

        transformEnvelopedSignature.doFinal();

        Assert.assertEquals(19, xmlSecEvents.size());
    }

    @Test
    public void testXMLSecEventToInputStreamAPI() throws Exception {
        TransformEnvelopedSignature transformEnvelopedSignature = new TransformEnvelopedSignature();

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Transformer transformer = new Transformer() {
            @Override
            public void setOutputStream(OutputStream outputStream) throws XMLSecurityException {
            }

            @Override
            public void setTransformer(Transformer transformer) throws XMLSecurityException {
            }

            @Override
            public void setProperties(Map<String, Object> properties) throws XMLSecurityException {
            }

            @Override
            public XMLSecurityConstants.TransformMethod getPreferredTransformMethod(XMLSecurityConstants.TransformMethod forInput) {
                return XMLSecurityConstants.TransformMethod.InputStream;
            }

            @Override
            public void transform(XMLSecEvent xmlSecEvent) throws XMLStreamException {
                Assert.fail("unexpected call to transform(XMLSecEvent");
            }

            @Override
            public void transform(InputStream inputStream) throws XMLStreamException {
                try {
                    XMLSecurityUtils.copy(inputStream, byteArrayOutputStream);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void doFinal() throws XMLStreamException {
            }
        };
        transformEnvelopedSignature.setTransformer(transformer);

        XMLEventReader xmlSecEventReader = xmlInputFactory.createXMLEventReader(
                this.getClass().getClassLoader().getResourceAsStream(
                        "com/phaos/phaos-xmldsig-three/signature-rsa-enveloped.xml")
        );

        while (xmlSecEventReader.hasNext()) {
            XMLSecEvent xmlSecEvent = (XMLSecEvent) xmlSecEventReader.nextEvent();
            transformEnvelopedSignature.transform(xmlSecEvent);
        }

        transformEnvelopedSignature.doFinal();

        Assert.assertEquals(207, byteArrayOutputStream.size());
    }

    @Test
    public void testXMLSecEventToOutputStreamStreamAPI() throws Exception {
        TransformEnvelopedSignature transformEnvelopedSignature = new TransformEnvelopedSignature();
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        transformEnvelopedSignature.setOutputStream(byteArrayOutputStream);

        XMLEventReader xmlSecEventReader = xmlInputFactory.createXMLEventReader(
                this.getClass().getClassLoader().getResourceAsStream(
                        "com/phaos/phaos-xmldsig-three/signature-rsa-enveloped.xml")
        );

        while (xmlSecEventReader.hasNext()) {
            XMLSecEvent xmlSecEvent = (XMLSecEvent) xmlSecEventReader.nextEvent();
            transformEnvelopedSignature.transform(xmlSecEvent);
        }

        transformEnvelopedSignature.doFinal();

        Assert.assertEquals(207, byteArrayOutputStream.size());
    }
}
