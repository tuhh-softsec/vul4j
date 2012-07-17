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

import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.xml.security.stax.ext.Transformer;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class TransformBase64Decode implements Transformer {

    private OutputStream outputStream;

    @Override
    public void setOutputStream(OutputStream outputStream) throws XMLSecurityException {
        this.outputStream = new Base64OutputStream(
                new FilterOutputStream(outputStream) {
                    @Override
                    public void close() throws IOException {
                        //do not close the parent output stream!
                        super.flush();
                    }
                },
                false);
    }

    @Override
    public void setList(List list) throws XMLSecurityException {
    }

    @Override
    public void setTransformer(Transformer transformer) throws XMLSecurityException {
        throw new UnsupportedOperationException("Transformer not supported");
    }

    @Override
    public void transform(XMLSecEvent xmlSecEvent) throws XMLStreamException {
        int eventType = xmlSecEvent.getEventType();
        switch (eventType) {
            case XMLStreamConstants.CHARACTERS:
                try {
                    //encoding shouldn't matter here, because the data is Base64 encoded and is therefore in the ASCII range.
                    outputStream.write(xmlSecEvent.asCharacters().getData().getBytes());
                } catch (IOException e) {
                    throw new XMLStreamException(e);
                }
                break;
            default:
                return;
        }
    }

    @Override
    public void doFinal() throws XMLStreamException {
        try {
            outputStream.close();
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}
