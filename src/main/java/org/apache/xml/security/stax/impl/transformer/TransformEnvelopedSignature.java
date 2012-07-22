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
package org.apache.xml.security.stax.impl.transformer;

import org.apache.xml.security.stax.ext.Transformer;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class TransformEnvelopedSignature implements Transformer {

    private static final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
    private XMLEventWriter xmlEventWriter;
    private Transformer transformer;
    private int curLevel = 0;
    private int sigElementLevel = -1;

    @Override
    public void setOutputStream(OutputStream outputStream) throws XMLSecurityException {
        try {
            xmlEventWriter = xmlOutputFactory.createXMLEventWriter(outputStream);
        } catch (XMLStreamException e) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILURE, e);
        }
    }

    @Override
    public void setList(List list) throws XMLSecurityException {
    }

    @Override
    public void setTransformer(Transformer transformer) throws XMLSecurityException {
        this.transformer = transformer;
    }

    @Override
    public void transform(XMLSecEvent xmlSecEvent) throws XMLStreamException {
        switch (xmlSecEvent.getEventType()) {
            case XMLStreamConstants.START_ELEMENT:
                curLevel++;
                XMLSecStartElement xmlSecStartElement = xmlSecEvent.asStartElement();
                if (XMLSecurityConstants.TAG_dsig_Signature.equals(xmlSecStartElement.getName())) {
                    sigElementLevel = curLevel;
                    return;
                }
                break;
            case XMLStreamConstants.END_ELEMENT:
                XMLSecEndElement xmlSecEndElement = xmlSecEvent.asEndElement();
                if (sigElementLevel == curLevel && XMLSecurityConstants.TAG_dsig_Signature.equals(xmlSecEndElement.getName())) {
                    sigElementLevel = -1;
                    return;
                }
                curLevel--;
        }
        if (sigElementLevel == -1) {
            if (xmlEventWriter != null) {
                xmlEventWriter.add(xmlSecEvent);
            } else if (transformer != null) {
                transformer.transform(xmlSecEvent);
            }
        }
    }

    @Override
    public void doFinal() throws XMLStreamException {
        if (xmlEventWriter != null) {
            xmlEventWriter.close();
        }
        if (transformer != null) {
            transformer.doFinal();
        }
    }
}
