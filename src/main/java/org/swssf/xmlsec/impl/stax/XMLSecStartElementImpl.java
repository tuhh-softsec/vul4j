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
import org.swssf.xmlsec.ext.stax.XMLSecStartElement;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import java.util.*;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecStartElementImpl extends XMLSecEventBaseImpl implements XMLSecStartElement {

    private final QName elementName;
    private XMLSecNamespace elementNamespace;
    private List<XMLSecAttribute> attributes = Collections.emptyList();
    private List<XMLSecNamespace> namespaces = Collections.emptyList();

    public XMLSecStartElementImpl(QName elementName, List<XMLSecAttribute> attributes, List<XMLSecNamespace> namespaces, XMLSecStartElement parentXmlSecStartElement) {
        this.elementName = elementName;
        setParentXMLSecStartElement(parentXmlSecStartElement);
        if (attributes != null) {
            this.attributes = attributes;
        }
        if (namespaces != null) {
            this.namespaces = namespaces;
        }
    }

    public XMLSecStartElementImpl(QName elementName, Collection<XMLSecAttribute> attributes, Collection<XMLSecNamespace> namespaces) {
        this.elementName = elementName;
        if (attributes != null && !attributes.isEmpty()) {
            this.attributes = new ArrayList<XMLSecAttribute>(attributes);
        }
        if (namespaces != null && !namespaces.isEmpty()) {
            this.namespaces = new ArrayList<XMLSecNamespace>(namespaces);
        }
    }

    @Override
    public QName getName() {
        return elementName;
    }

    @Override
    public XMLSecNamespace getElementNamespace() {
        if (this.elementNamespace == null) {
            this.elementNamespace = new XMLSecNamespaceImpl(this.elementName.getPrefix(), this.elementName.getNamespaceURI());
        }
        return this.elementNamespace;
    }

    @Override
    public Iterator<XMLSecAttribute> getAttributes() {
        return attributes.iterator();
    }

    @Override
    public void getAttributesFromCurrentScope(List<XMLSecAttribute> comparableAttributeList) {
        comparableAttributeList.addAll(attributes);
        if (parentXMLSecStartELement != null) {
            parentXMLSecStartELement.getAttributesFromCurrentScope(comparableAttributeList);
        }
    }

    @Override
    public List<XMLSecAttribute> getOnElementDeclaredAttributes() {
        if (this.attributes == Collections.<XMLSecAttribute>emptyList()) {
            this.attributes = new ArrayList<XMLSecAttribute>();
        }
        return this.attributes;
    }

    public int getDocumentLevel() {
        return super.getDocumentLevel() + 1;
    }

    public void getElementPath(List<QName> list) {
        super.getElementPath(list);
        list.add(this.getName());
    }

    public XMLSecStartElement getStartElementAtLevel(int level) {
        int thisLevel = getDocumentLevel();
        if (thisLevel < level) {
            return null;
        } else if (thisLevel == level) {
            return this;
        } else {
            return parentXMLSecStartELement.getStartElementAtLevel(level);
        }
    }

    @Override
    public Iterator<XMLSecNamespace> getNamespaces() {
        return namespaces.iterator();
    }

    @Override
    public void getNamespacesFromCurrentScope(List<XMLSecNamespace> comparableNamespaceList) {
        comparableNamespaceList.addAll(namespaces);
        if (parentXMLSecStartELement != null) {
            parentXMLSecStartELement.getNamespacesFromCurrentScope(comparableNamespaceList);
        }
    }

    @Override
    public List<XMLSecNamespace> getOnElementDeclaredNamespaces() {
        if (this.namespaces == Collections.<XMLSecNamespace>emptyList()) {
            this.namespaces = new ArrayList<XMLSecNamespace>();
        }
        return this.namespaces;
    }

    @Override
    public XMLSecAttribute getAttributeByName(QName name) {
        for (int i = 0; i < attributes.size(); i++) {
            XMLSecAttribute comparableAttribute = attributes.get(i);
            if (name.equals(comparableAttribute.getName())) {
                return comparableAttribute;
            }
        }
        return null;
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        //todo implement me. Needed by XMLSecurityStreamReader!
        return null;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        for (int i = 0; i < namespaces.size(); i++) {
            XMLSecNamespace comparableNamespace = namespaces.get(i);
            if (prefix.equals(comparableNamespace.getPrefix())) {
                return comparableNamespace.getNamespaceURI();
            }
        }
        if (parentXMLSecStartELement != null) {
            return parentXMLSecStartELement.getNamespaceURI(prefix);
        }
        return null;
    }

    @Override
    public int getEventType() {
        return XMLStreamConstants.START_ELEMENT;
    }

    @Override
    public boolean isStartElement() {
        return true;
    }

    @Override
    public XMLSecStartElement asStartElement() {
        return this;
    }
}
