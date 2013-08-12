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

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.ResourceResolver;
import org.apache.xml.security.stax.ext.ResourceResolverLookup;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

import java.io.InputStream;
import java.net.URI;

/**
 * Resolver for local filesystem resources. Use the standard java security-manager to
 * restrict filesystem accesses.
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class ResolverFilesystem implements ResourceResolver, ResourceResolverLookup {

    private String uri;
    private String baseURI;

    public ResolverFilesystem() {
    }

    public ResolverFilesystem(String uri, String baseURI) {
        this.uri = uri;
        this.baseURI = baseURI;
    }

    @Override
    public ResourceResolverLookup canResolve(String uri, String baseURI) {
        if (uri == null) {
            return null;
        }
        if (uri.startsWith("file:") || baseURI != null && baseURI.startsWith("file:")) {
            return this;
        }
        return null;
    }

    @Override
    public ResourceResolver newInstance(String uri, String baseURI) {
        return new ResolverFilesystem(uri, baseURI);
    }

    @Override
    public boolean isSameDocumentReference() {
        return false;
    }

    @Override
    public boolean matches(XMLSecStartElement xmlSecStartElement) {
        return false;
    }

    @Override
    public InputStream getInputStreamFromExternalReference() throws XMLSecurityException {
        try {
            URI tmp;
            if (baseURI == null || "".equals(baseURI)) {
                tmp = new URI(uri);
            } else {
                tmp = new URI(baseURI).resolve(uri);
            }

            if (tmp.getFragment() != null) {
                tmp = new URI(tmp.getScheme(), tmp.getSchemeSpecificPart(), null);
            }
            return tmp.toURL().openStream();
        } catch (Exception e) {
            throw new XMLSecurityException(e);
        }
    }
}
