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
package org.apache.xml.security.stax.impl.transformer.canonicalizer;

import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

import java.util.*;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public abstract class Canonicalizer20010315_Excl extends CanonicalizerBase {

    public Canonicalizer20010315_Excl(boolean includeComments) {
        super(includeComments);
    }

    @Override
    protected List<XMLSecNamespace> getCurrentUtilizedNamespaces(final XMLSecStartElement xmlSecStartElement,
                                                                      final C14NStack<XMLSecEvent> outputStack) {
        List<XMLSecNamespace> utilizedNamespaces = Collections.emptyList();

        XMLSecNamespace elementNamespace = xmlSecStartElement.getElementNamespace();
        final XMLSecNamespace found = (XMLSecNamespace) outputStack.containsOnStack(elementNamespace);
        //found means the prefix matched. so check the ns further
        if (found == null || found.getNamespaceURI() == null || !found.getNamespaceURI().equals(elementNamespace.getNamespaceURI())) {
            utilizedNamespaces = new ArrayList<XMLSecNamespace>(2);
            utilizedNamespaces.add(elementNamespace);
            outputStack.peek().add(elementNamespace);
        }

        List<XMLSecAttribute> comparableAttributes = xmlSecStartElement.getOnElementDeclaredAttributes();
        for (int i = 0; i < comparableAttributes.size(); i++) {
            XMLSecAttribute comparableAttribute = comparableAttributes.get(i);
            XMLSecNamespace attributeNamespace = comparableAttribute.getAttributeNamespace();
            if ("xml".equals(attributeNamespace.getPrefix())) {
                continue;
            }
            if (attributeNamespace.getNamespaceURI() == null || attributeNamespace.getNamespaceURI().isEmpty()) {
                continue;
            }
            final XMLSecNamespace resultNamespace = (XMLSecNamespace) outputStack.containsOnStack(attributeNamespace);
            //resultNamespace means the prefix matched. so check the ns further
            if (resultNamespace == null || resultNamespace.getNamespaceURI() == null
                    || !resultNamespace.getNamespaceURI().equals(attributeNamespace.getNamespaceURI())) {

                if (utilizedNamespaces == (Object)Collections.emptyList()) {
                    utilizedNamespaces = new ArrayList<XMLSecNamespace>(2);
                }
                utilizedNamespaces.add(attributeNamespace);
                outputStack.peek().add(attributeNamespace);
            }
        }

        return utilizedNamespaces;
    }

    @Override
    protected List<XMLSecNamespace> getInitialUtilizedNamespaces(final XMLSecStartElement xmlSecStartElement,
                                                                      final C14NStack<XMLSecEvent> outputStack) {
        return getCurrentUtilizedNamespaces(xmlSecStartElement, outputStack);
    }

    @Override
    protected List<XMLSecAttribute> getInitialUtilizedAttributes(final XMLSecStartElement xmlSecStartElement,
                                                                      final C14NStack<XMLSecEvent> outputStack) {
        List<XMLSecAttribute> utilizedAttributes = Collections.emptyList();
        @SuppressWarnings("unchecked")
        List<XMLSecAttribute> comparableAttributes = xmlSecStartElement.getOnElementDeclaredAttributes();
        for (int i = 0; i < comparableAttributes.size(); i++) {
            XMLSecAttribute comparableAttribute = comparableAttributes.get(i);
            if (utilizedAttributes == (Object)Collections.emptyList()) {
                utilizedAttributes = new ArrayList<XMLSecAttribute>(2);
            }
            utilizedAttributes.add(comparableAttribute);
        }
        return utilizedAttributes;
    }
}
