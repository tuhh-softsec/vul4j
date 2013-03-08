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
package org.apache.xml.security.stax.ext;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public interface Transformer {

    void setOutputStream(OutputStream outputStream) throws XMLSecurityException;

    void setTransformer(Transformer transformer) throws XMLSecurityException;

    void setList(List<?> list) throws XMLSecurityException;

    XMLSecurityConstants.TransformMethod getPreferredTransformMethod(XMLSecurityConstants.TransformMethod forInput);

    void transform(XMLSecEvent xmlSecEvent) throws XMLStreamException;

    void transform(InputStream inputStream) throws XMLStreamException;

    void doFinal() throws XMLStreamException;
}
