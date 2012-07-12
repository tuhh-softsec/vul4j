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

import org.apache.xml.security.stax.ext.ResourceResolver;
import org.apache.xml.security.stax.ext.ResourceResolverLookup;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

import javax.xml.stream.events.Attribute;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolver for xpointer references in the same document.
 * Supported xpointers are '#xpointer(/)' and '#xpointer(id('ID'))'
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class ResolverXPointer implements ResourceResolver, ResourceResolverLookup {

    private Pattern pattern = Pattern.compile("^#xpointer\\((/)|(id\\([\"\']([^\"\']*)[\"\']\\))\\)");
    private String id;
    private boolean rootNodeOccured = false;

    public ResolverXPointer() {
    }

    public ResolverXPointer(String uri) {
        Matcher matcher = pattern.matcher(uri);
        if (matcher.find() && matcher.groupCount() == 3) {
            String slash = matcher.group(1);
            if (slash != null) {
                this.id = null;
                return;
            }
            String id = matcher.group(3);
            if (id != null) {
                this.id = id;
                return;
            }
        }
    }

    public String getId() {
        return id;
    }

    public boolean isRootNodeOccured() {
        return rootNodeOccured;
    }

    public void setRootNodeOccured(boolean rootNodeOccured) {
        this.rootNodeOccured = rootNodeOccured;
    }

    @Override
    public ResourceResolverLookup canResolve(String uri) {
        if (uri != null && pattern.matcher(uri).find()) {
            return this;
        }
        return null;
    }

    @Override
    public ResourceResolver newInstance(String uri) {
        return new ResolverXPointer(uri);
    }

    @Override
    public boolean isSameDocumentReference() {
        return true;
    }

    @Override
    public boolean matches(XMLSecStartElement xmlSecStartElement) {
        //when id is null we have #xpointer(/) and then we just return true for the first start-element
        if (id == null) {
            if (!rootNodeOccured) {
                rootNodeOccured = true;
                return true;
            }
            return false;
        }
        //case #xpointer(id('ID')):
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
