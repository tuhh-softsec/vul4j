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

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.processor.input.XMLEventReaderInputProcessor;
import org.apache.xml.security.stax.impl.util.UnsynchronizedByteArrayInputStream;
import org.apache.xml.security.stax.impl.util.UnsynchronizedByteArrayOutputStream;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.io.*;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class TransformBase64Decode extends TransformIdentity {

    private ChildOutputMethod childOutputMethod;

    @Override
    public void setOutputStream(OutputStream outputStream) throws XMLSecurityException {
        super.setOutputStream(new Base64OutputStream(
                new FilterOutputStream(outputStream) {
                    @Override
                    public void close() throws IOException {
                        //do not close the parent output stream!
                        super.flush();
                    }
                },
                false)
        );
    }

    @Override
    public XMLSecurityConstants.TransformMethod getPreferredTransformMethod(XMLSecurityConstants.TransformMethod forInput) {
        switch (forInput) {
            case XMLSecEvent:
                return XMLSecurityConstants.TransformMethod.InputStream;
            case InputStream:
                return XMLSecurityConstants.TransformMethod.InputStream;
            default:
                throw new IllegalArgumentException("Unsupported class " + forInput.name());
        }
    }

    @Override
    public void transform(XMLSecEvent xmlSecEvent) throws XMLStreamException {
        int eventType = xmlSecEvent.getEventType();
        switch (eventType) {
            case XMLStreamConstants.CHARACTERS:
                if (getOutputStream() != null) {
                    //we have an output stream
                    //encoding shouldn't matter here, because the data is Base64 encoded and is therefore in the ASCII range.
                    try {
                        getOutputStream().write(xmlSecEvent.asCharacters().getData().getBytes());
                    } catch (IOException e) {
                        throw new XMLStreamException(e);
                    }
                } else {
                    //we have a child transformer
                    if (childOutputMethod == null) {

                        final XMLSecurityConstants.TransformMethod preferredChildTransformMethod =
                                getTransformer().getPreferredTransformMethod(XMLSecurityConstants.TransformMethod.XMLSecEvent);

                        switch (preferredChildTransformMethod) {
                            case XMLSecEvent: {
                                childOutputMethod = new ChildOutputMethod() {

                                    private UnsynchronizedByteArrayOutputStream byteArrayOutputStream;
                                    private Base64OutputStream base64OutputStream;

                                    @Override
                                    public void transform(Object object) throws XMLStreamException {
                                        if (base64OutputStream == null) {
                                            byteArrayOutputStream = new UnsynchronizedByteArrayOutputStream();
                                            base64OutputStream = new Base64OutputStream(byteArrayOutputStream, false);
                                        }
                                        try {
                                            base64OutputStream.write(((byte[]) object));
                                        } catch (IOException e) {
                                            throw new XMLStreamException(e);
                                        }
                                    }

                                    @Override
                                    public void doFinal() throws XMLStreamException {
                                        try {
                                            base64OutputStream.close();
                                        } catch (IOException e) {
                                            throw new XMLStreamException(e);
                                        }
                                        XMLEventReaderInputProcessor xmlEventReaderInputProcessor
                                                = new XMLEventReaderInputProcessor(
                                                null,
                                                getXmlInputFactory().createXMLStreamReader(new UnsynchronizedByteArrayInputStream(byteArrayOutputStream.toByteArray()))
                                        );

                                        try {
                                            XMLSecEvent xmlSecEvent;
                                            do {
                                                xmlSecEvent = xmlEventReaderInputProcessor.processNextEvent(null);
                                                getTransformer().transform(xmlSecEvent);
                                            } while (xmlSecEvent.getEventType() != XMLStreamConstants.END_DOCUMENT);
                                        } catch (XMLSecurityException e) {
                                            throw new XMLStreamException(e);
                                        }
                                        getTransformer().doFinal();
                                    }
                                };
                                break;
                            }
                            case InputStream: {
                                childOutputMethod = new ChildOutputMethod() {

                                    private UnsynchronizedByteArrayOutputStream byteArrayOutputStream;
                                    private Base64OutputStream base64OutputStream;

                                    @Override
                                    public void transform(Object object) throws XMLStreamException {
                                        if (base64OutputStream == null) {
                                            byteArrayOutputStream = new UnsynchronizedByteArrayOutputStream();
                                            base64OutputStream = new Base64OutputStream(byteArrayOutputStream, false);
                                        }
                                        try {
                                            base64OutputStream.write(((byte[]) object));
                                        } catch (IOException e) {
                                            throw new XMLStreamException(e);
                                        }
                                    }

                                    @Override
                                    public void doFinal() throws XMLStreamException {
                                        try {
                                            base64OutputStream.close();
                                        } catch (IOException e) {
                                            throw new XMLStreamException(e);
                                        }
                                        getTransformer().transform(new UnsynchronizedByteArrayInputStream(byteArrayOutputStream.toByteArray()));
                                        getTransformer().doFinal();
                                    }
                                };
                                break;
                            }
                        }
                    }
                    childOutputMethod.transform(xmlSecEvent.asCharacters().getData().getBytes());
                }
                break;
        }
    }

    @Override
    public void transform(InputStream inputStream) throws XMLStreamException {
        if (getOutputStream() != null) {
            super.transform(inputStream);
        } else {
            super.transform(new Base64InputStream(inputStream, false));
        }
    }

    @Override
    public void doFinal() throws XMLStreamException {
        if (getOutputStream() != null) {
            try {
                getOutputStream().close();
            } catch (IOException e) {
                throw new XMLStreamException(e);
            }
        }
        if (childOutputMethod != null) {
            childOutputMethod.doFinal();
        } else if (getTransformer() != null) {
            getTransformer().doFinal();
        }
    }
}
