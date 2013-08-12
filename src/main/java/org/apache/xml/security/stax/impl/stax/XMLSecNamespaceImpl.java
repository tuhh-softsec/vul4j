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
package org.apache.xml.security.stax.impl.stax;

import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Class to let XML-Namespaces be comparable how it is requested by C14N
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecNamespaceImpl extends XMLSecEventBaseImpl implements XMLSecNamespace {

    private static final Map<String, Map<String, XMLSecNamespace>> xmlSecNamespaceMap =
            new WeakHashMap<String, Map<String, XMLSecNamespace>>();

    private String prefix;
    private final String uri;
    private QName qName;

    private XMLSecNamespaceImpl(String prefix, String uri) {
        this.prefix = prefix;
        this.uri = uri;
    }

    public static XMLSecNamespace getInstance(String prefix, String uri) {
        if (prefix == null) {
            prefix = "";
        }
        //sun's stax parser returns null for the default namespace
        if (uri == null) {
            uri = "";
        }
        Map<String, XMLSecNamespace> nsMap = xmlSecNamespaceMap.get(prefix);
        if (nsMap != null) {
            XMLSecNamespace xmlSecNamespace = nsMap.get(uri);
            if (xmlSecNamespace != null) {
                return xmlSecNamespace;
            } else {
                xmlSecNamespace = new XMLSecNamespaceImpl(prefix, uri);
                nsMap.put(uri, xmlSecNamespace);
                return xmlSecNamespace;
            }
        } else {
            nsMap = new WeakHashMap<String, XMLSecNamespace>();
            XMLSecNamespace xmlSecNamespace = new XMLSecNamespaceImpl(prefix, uri);
            nsMap.put(uri, xmlSecNamespace);
            xmlSecNamespaceMap.put(prefix, nsMap);
            return xmlSecNamespace;
        }
    }

    @Override
    public int compareTo(XMLSecNamespace o) {
        //An element's namespace nodes are sorted lexicographically by local name
        //(the default namespace node, if one exists, has no local name and is therefore lexicographically least).
        return this.prefix.compareTo(o.getPrefix());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof XMLSecNamespace)) {
            return false;
        }
        XMLSecNamespace comparableNamespace = (XMLSecNamespace) obj;

        if (comparableNamespace.hashCode() != this.hashCode()) {
            return false;
        }
        //just test for prefix to get the last prefix definition on the stack and let overwrite it
        return comparableNamespace.getPrefix().equals(this.prefix);
    }

    @Override
    public int hashCode() {
        //we don't have to cache the hashCode. The string class takes already care of it.
        return this.prefix.hashCode();
    }

    @Override
    public QName getName() {
        if (this.qName == null) {
            this.qName = new QName(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, this.prefix);
        }
        return this.qName;
    }

    @Override
    public String getValue() {
        return this.uri;
    }

    @Override
    public String getDTDType() {
        return "CDATA";
    }

    @Override
    public boolean isSpecified() {
        return true;
    }

    @Override
    public String getNamespaceURI() {
        return uri;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public boolean isDefaultNamespaceDeclaration() {
        return prefix.length() == 0;
    }

    @Override
    public int getEventType() {
        return XMLStreamConstants.NAMESPACE;
    }

    @Override
    public boolean isNamespace() {
        return true;
    }

    @Override
    public String toString() {
        if (this.prefix == null || this.prefix.isEmpty()) {
            return "xmlns=\"" + this.uri + "\"";
        }
        return "xmlns:" + this.prefix + "=\"" + this.uri + "\"";
    }
}
