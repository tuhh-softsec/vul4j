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
package org.apache.xml.security.utils;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Purpose of this class is to enable the XML Parser to keep track of ID
 * attributes. This is done by 'registering' attributes of type ID at the
 * IdResolver.
 */
public class IdResolver {

    /** {@link org.apache.commons.logging} logging facility */
    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog(IdResolver.class);

    private static Map<Document, Map<String, WeakReference<Element>>> docMap = 
        new WeakHashMap<Document, Map<String, WeakReference<Element>>>();
    
    /**
     * Constructor IdResolver
     *
     */
    private IdResolver() {
        // we don't allow instantiation
    }

    /**
     * Method registerElementById
     *
     * @param element the element to register
     * @param idValue the value of the ID attribute
     */
    public static void registerElementById(Element element, String idValue) {
        Document doc = element.getOwnerDocument();
        synchronized (docMap) {
            Map<String, WeakReference<Element>> elementMap = docMap.get(doc);
            if (elementMap == null) {
                elementMap = new WeakHashMap<String, WeakReference<Element>>();
                docMap.put(doc, elementMap);
                elementMap.put(idValue, new WeakReference<Element>(element));
            } else {
                WeakReference<Element> ref = elementMap.get(idValue);
                if (ref != null) {
                    if (!ref.get().equals(element)) {
                        throw new IllegalArgumentException("ID is already registered");
                    }
                } else {
                    elementMap.put(idValue, new WeakReference<Element>(element));
                }
            }
        }
    }

    /**
     * Force a removal of a registered document. Any element id associated
     * with this document will be removed from the weak reference map.
     * 
     * @param doc the DOM document that is to be removed from the map.
     */
    public static void unregisterDocument(Document doc) {
        synchronized (docMap) {
            docMap.remove(doc);
        }
    }

    /**
     * Method registerElementById
     *
     * @param element the element to register
     * @param id the ID attribute
     */
    public static void registerElementById(Element element, Attr id) {
        IdResolver.registerElementById(element, id.getNodeValue());
    }

    /**
     * Method getElementById
     *
     * @param doc the document
     * @param id the value of the ID 
     * @return the element obtained by the id, or null if it is not found.
     */
    public static Element getElementById(Document doc, String id) {

        Element result = IdResolver.getElementByIdType(doc, id);

        if (result != null) {
            if (log.isDebugEnabled()) {
                log.debug(
                    "I could find an Element using the simple getElementByIdType method: "
                    + result.getTagName()
                );
            }
            return result;
        }

        result = IdResolver.getElementByIdUsingDOM(doc, id);
        if (result != null) {
            if (log.isDebugEnabled()) {
                log.debug(
                    "I could find an Element using the simple getElementByIdUsingDOM method: "
                    + result.getTagName()
                );
            }
            return result;
        }

        return null;
    }


    /**
     * Method getElementByIdUsingDOM
     *
     * @param doc the document
     * @param id the value of the ID
     * @return the element obtained by the id, or null if it is not found.
     */
    private static Element getElementByIdUsingDOM(Document doc, String id) {
        if (log.isDebugEnabled()) {
            log.debug("getElementByIdUsingDOM() Search for ID " + id);
        }
        return doc.getElementById(id);
    }

    /**
     * Method getElementByIdType
     *
     * @param doc the document
     * @param id the value of the ID
     * @return the element obtained by the id, or null if it is not found.
     */
    private static Element getElementByIdType(Document doc, String id) {
        if (log.isDebugEnabled()) {
            log.debug("getElementByIdType() Search for ID " + id);
        }
        synchronized (docMap) {
            Map<String, WeakReference<Element>> elementMap = docMap.get(doc);
            if (elementMap != null) {
                WeakReference<Element> weakReference = elementMap.get(id);
                if (weakReference != null) {
                    return weakReference.get();
                }
            }
        }
        return null;
    }

}
