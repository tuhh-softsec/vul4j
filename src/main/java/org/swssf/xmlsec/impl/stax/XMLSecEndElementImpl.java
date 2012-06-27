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
package org.swssf.xmlsec.impl.stax;

import org.swssf.xmlsec.ext.stax.XMLSecEndElement;
import org.swssf.xmlsec.ext.stax.XMLSecStartElement;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecEndElementImpl extends XMLSecEventBaseImpl implements XMLSecEndElement {

    private final QName elementName;

    public XMLSecEndElementImpl(QName elementName, XMLSecStartElement parentXmlSecStartElement) {
        this.elementName = elementName;
        setParentXMLSecStartElement(parentXmlSecStartElement);
    }

    @Override
    public QName getName() {
        return elementName;
    }

    @Override
    public Iterator getNamespaces() {
        return Collections.emptyList().iterator();
    }

    @Override
    public int getEventType() {
        return XMLStreamConstants.END_ELEMENT;
    }

    @Override
    public boolean isEndElement() {
        return true;
    }

    @Override
    public XMLSecEndElement asEndElement() {
        return this;
    }
}
