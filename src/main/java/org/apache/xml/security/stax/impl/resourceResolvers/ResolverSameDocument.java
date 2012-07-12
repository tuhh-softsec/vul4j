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
package org.apache.xml.security.stax.impl.resourceResolvers;

import org.apache.xml.security.stax.ext.*;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

import javax.xml.stream.events.Attribute;
import java.io.InputStream;

/**
 * Resolver for references in the same document.
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class ResolverSameDocument implements ResourceResolver, ResourceResolverLookup {

    private String id;

    public ResolverSameDocument() {
    }

    public ResolverSameDocument(String uri) {
        this.id = XMLSecurityUtils.dropReferenceMarker(uri);
    }

    public String getId() {
        return id;
    }

    @Override
    public ResourceResolverLookup canResolve(String uri) {
        if (uri != null && uri.charAt(0) == '#') {
            if (uri.startsWith("#xpointer")) {
                return null;
            }
            return this;
        }
        return null;
    }

    @Override
    public ResourceResolver newInstance(String uri) {
        return new ResolverSameDocument(uri);
    }

    @Override
    public boolean isSameDocumentReference() {
        return true;
    }

    @Override
    public boolean matches(XMLSecStartElement xmlSecStartElement) {
        Attribute attribute = xmlSecStartElement.getAttributeByName(XMLSecurityConstants.ATT_NULL_Id);
        if (attribute != null && attribute.getValue().equals(id)) {
            return true;
        }
        return false;
    }

    @Override
    public InputStream getInputStreamFromExternalReference() throws XMLSecurityException {
        return null;
    }
}
