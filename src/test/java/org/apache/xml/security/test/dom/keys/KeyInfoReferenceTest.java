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
package org.apache.xml.security.test.dom.keys;

import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;

import org.apache.xml.security.keys.content.KeyInfoReference;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class KeyInfoReferenceTest extends Assert {

    private static final String BASEDIR = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
    private static final String SEP = System.getProperty("file.separator");

    private DocumentBuilder documentBuilder;

    private final String idControl = "abc123";
    private final String uriControl = "http://www.example.org/keyinfo.xml";

    public KeyInfoReferenceTest() throws Exception {
        documentBuilder = XMLUtils.createDocumentBuilder(false);
    }

    @org.junit.Test
    public void testSchema() throws Exception {
        KeyInfoReference keyInfoReference = new KeyInfoReference(documentBuilder.newDocument(), uriControl);
        Element element = keyInfoReference.getElement();

        assertEquals("http://www.w3.org/2009/xmldsig11#", element.getNamespaceURI());
        assertEquals("KeyInfoReference", element.getLocalName());
    }

    @org.junit.Test
    public void testURIFromElement() throws Exception {
        Document doc = loadXML("KeyInfoReference.xml");
        NodeList nl = doc.getElementsByTagNameNS(Constants.SignatureSpec11NS, Constants._TAG_KEYINFOREFERENCE);
        Element element = (Element) nl.item(0);

        KeyInfoReference keyInfoReference = new KeyInfoReference(element, "");
        assertEquals(uriControl, keyInfoReference.getURI());
        assertEquals(idControl, keyInfoReference.getId());
    }

    @org.junit.Test
    public void testURIOnConstruction() throws Exception {
        KeyInfoReference keyInfoReference = new KeyInfoReference(documentBuilder.newDocument(), uriControl);
        assertEquals(uriControl, keyInfoReference.getURI());
    }

    @org.junit.Test
    public void testId() throws Exception {
        KeyInfoReference keyInfoReference = new KeyInfoReference(documentBuilder.newDocument(), uriControl);
        assertEquals("", keyInfoReference.getId());
        assertNull(keyInfoReference.getElement().getAttributeNodeNS(null, Constants._ATT_ID));

        keyInfoReference.setId(idControl);
        assertEquals(idControl, keyInfoReference.getId());
        assertTrue(keyInfoReference.getElement().getAttributeNodeNS(null, Constants._ATT_ID).isId());

        keyInfoReference.setId(null);
        assertEquals("", keyInfoReference.getId());
        assertNull(keyInfoReference.getElement().getAttributeNodeNS(null, Constants._ATT_ID));
    }

    // Utility methods

    private String getControlFilePath(String fileName) {
        return BASEDIR + SEP + "src" + SEP + "test" + SEP + "resources" + 
            SEP + "org" + SEP + "apache" + SEP + "xml" + SEP + "security" + 
            SEP + "keys" + SEP + "content" +
            SEP + fileName;
    }

    private Document loadXML(String fileName) throws Exception {
        return documentBuilder.parse(new FileInputStream(getControlFilePath(fileName)));
    }

}
