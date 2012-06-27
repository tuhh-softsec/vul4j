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

import org.swssf.xmlsec.ext.stax.XMLSecAttribute;
import org.swssf.xmlsec.ext.stax.XMLSecNamespace;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;

/**
 * Class to let XML-Attributes be comparable how it is requested by C14N
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecAttributeImpl extends XMLSecEventBaseImpl implements XMLSecAttribute {

    private final QName name;
    private final String value;
    private XMLSecNamespace attributeNamespace;

    public XMLSecAttributeImpl(QName name, String value) {
        this.name = name;
        this.value = value;
    }

    public int compareTo(XMLSecAttribute o) {
        //An element's attribute nodes are sorted lexicographically with namespace URI as the primary
        //key and local name as the secondary key (an empty namespace URI is lexicographically least).
        int namespacePartCompare = this.name.getNamespaceURI().compareTo(o.getName().getNamespaceURI());
        if (namespacePartCompare != 0) {
            return namespacePartCompare;
        } else {
            return this.name.getLocalPart().compareTo(o.getName().getLocalPart());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof XMLSecAttribute)) {
            return false;
        }
        XMLSecAttribute comparableAttribute = (XMLSecAttribute) obj;
        if (comparableAttribute.hashCode() != this.hashCode()) {
            return false;
        }
        return comparableAttribute.getName().getLocalPart().equals(this.name.getLocalPart());
    }

    @Override
    public int hashCode() {
        //we don't have to cache the hashCode. The string class takes already care of it.
        return this.name.getLocalPart().hashCode();
    }

    @Override
    public XMLSecNamespace getAttributeNamespace() {
        if (this.attributeNamespace == null) {
            this.attributeNamespace = new XMLSecNamespaceImpl(this.name.getPrefix(), this.name.getNamespaceURI());
        }
        return this.attributeNamespace;
    }

    public QName getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getDTDType() {
        return "CDATA";
    }

    public boolean isSpecified() {
        return true;
    }

    @Override
    public int getEventType() {
        return XMLStreamConstants.ATTRIBUTE;
    }

    @Override
    public boolean isAttribute() {
        return true;
    }
}
