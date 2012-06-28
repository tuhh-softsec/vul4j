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

import org.apache.xml.security.stax.ext.stax.XMLSecEntityReference;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.EntityDeclaration;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecEntityReferenceImpl extends XMLSecEventBaseImpl implements XMLSecEntityReference {

    private final String name;
    private final EntityDeclaration entityDeclaration;

    public XMLSecEntityReferenceImpl(String name, EntityDeclaration entityDeclaration, XMLSecStartElement parentXmlSecStartElement) {
        this.name = name;
        this.entityDeclaration = entityDeclaration;
        setParentXMLSecStartElement(parentXmlSecStartElement);
    }

    @Override
    public EntityDeclaration getDeclaration() {
        return entityDeclaration;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getEventType() {
        return XMLStreamConstants.ENTITY_REFERENCE;
    }

    @Override
    public boolean isEntityReference() {
        return true;
    }
}
