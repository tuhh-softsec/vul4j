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
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

import javax.xml.namespace.QName;
import java.util.*;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class Canonicalizer11 extends CanonicalizerBase {
    public Canonicalizer11(boolean includeComments) {
        super(includeComments);
    }

    @Override
    protected List<XMLSecAttribute> getInitialUtilizedAttributes(final XMLSecStartElement xmlSecStartElement,
                                                                      final C14NStack<XMLSecEvent> outputStack) {

        List<XMLSecAttribute> utilizedAttributes = Collections.emptyList();

        List<XMLSecAttribute> visibleAttributes = new ArrayList<XMLSecAttribute>();
        xmlSecStartElement.getAttributesFromCurrentScope(visibleAttributes);
        for (int i = 0; i < visibleAttributes.size(); i++) {
            XMLSecAttribute comparableAttribute = visibleAttributes.get(i);
            final QName comparableAttributeName = comparableAttribute.getName();
            //xml:id attributes must be handled like other attributes: emit but dont inherit
            if (!XML.equals(comparableAttributeName.getPrefix())) {
                continue;
            }
            if ("id".equals(comparableAttributeName.getLocalPart())
                    || "base".equals(comparableAttributeName.getLocalPart())) {
                continue;
            }
            if (outputStack.containsOnStack(comparableAttribute) != null) {
                continue;
            }
            if (utilizedAttributes == (Object)Collections.emptyList()) {
                utilizedAttributes = new ArrayList<XMLSecAttribute>(2);
            }
            utilizedAttributes.add(comparableAttribute);
            outputStack.peek().add(comparableAttribute);
        }

        List<XMLSecAttribute> elementAttributes = xmlSecStartElement.getOnElementDeclaredAttributes();
        for (int i = 0; i < elementAttributes.size(); i++) {
            XMLSecAttribute comparableAttribute = elementAttributes.get(i);
            //attributes with xml prefix are already processed in the for loop above
            //xml:id attributes must be handled like other attributes: emit but dont inherit
            final QName attributeName = comparableAttribute.getName();
            if (XML.equals(attributeName.getPrefix())) {
                continue;
            }
            if (utilizedAttributes == (Object)Collections.emptyList()) {
                utilizedAttributes = new ArrayList<XMLSecAttribute>(2);
            }
            utilizedAttributes.add(comparableAttribute);
        }

        return utilizedAttributes;
    }
}
