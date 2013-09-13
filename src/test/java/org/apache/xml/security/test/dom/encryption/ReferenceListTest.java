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
package org.apache.xml.security.test.dom.encryption;

import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;

import org.apache.xml.security.encryption.Reference;
import org.apache.xml.security.encryption.ReferenceList;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ReferenceListTest extends org.junit.Assert {

    static org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(ReferenceListTest.class);

    private DocumentBuilder db;

    public ReferenceListTest() throws Exception {
        org.apache.xml.security.Init.init();
        db = XMLUtils.createDocumentBuilder(false);
    }

    @org.junit.Test
    public void testReferenceList() throws Exception {
        Document doc = db.newDocument();

        XMLCipher dataCipher = XMLCipher.getInstance();
        dataCipher.init(XMLCipher.DECRYPT_MODE, null);

        ReferenceList dataRefList = dataCipher.createReferenceList(ReferenceList.DATA_REFERENCE);
        Reference dataRef1 = dataRefList.newDataReference("#id1");
        dataRefList.add(dataRef1);
        Reference dataRef2 = dataRefList.newDataReference("#id2");
        dataRefList.add(dataRef2);
        
        assertEquals("#id1", dataRef1.getURI());

        ReferenceList keyRefList = dataCipher.createReferenceList(ReferenceList.KEY_REFERENCE);
        Reference keyRef10 = keyRefList.newKeyReference("#id10");
        keyRefList.add(keyRef10);
        Reference keyRef20 = keyRefList.newKeyReference("#id20");
        keyRefList.add(keyRef20);
        
        assertEquals("#id10", keyRef10.getURI());

        // See SANTUARIO-302
        // https://issues.apache.org/jira/browse/SANTUARIO-302
        Element dataRefListElement = dataCipher.martial(doc, dataRefList);
        assertEquals(EncryptionConstants.EncryptionSpecNS, dataRefListElement.getNamespaceURI());
        assertEquals(EncryptionConstants._TAG_REFERENCELIST, dataRefListElement.getLocalName());
        Element dataRef1Element = (Element)dataRefListElement.getFirstChild();
        assertEquals(EncryptionConstants.EncryptionSpecNS, dataRef1Element.getNamespaceURI());
        assertEquals(EncryptionConstants._TAG_DATAREFERENCE, dataRef1Element.getLocalName());
        assertEquals("#id1", dataRef1Element.getAttribute("URI"));
        Element dataRef2Element = (Element)dataRef1Element.getNextSibling();
        assertEquals(EncryptionConstants.EncryptionSpecNS, dataRef2Element.getNamespaceURI());
        assertEquals(EncryptionConstants._TAG_DATAREFERENCE, dataRef2Element.getLocalName());
        assertEquals("#id2", dataRef2Element.getAttribute("URI"));
        assertNull(dataRef2Element.getNextSibling());
        
        Element keyRefListElement = dataCipher.martial(doc, keyRefList);
        assertEquals(EncryptionConstants.EncryptionSpecNS, keyRefListElement.getNamespaceURI());
        assertEquals(EncryptionConstants._TAG_REFERENCELIST, keyRefListElement.getLocalName());
        Element keyRef10Element = (Element)keyRefListElement.getFirstChild();
        assertEquals(EncryptionConstants.EncryptionSpecNS, keyRef10Element.getNamespaceURI());
        assertEquals(EncryptionConstants._TAG_KEYREFERENCE, keyRef10Element.getLocalName());
        assertEquals("#id10", keyRef10Element.getAttribute("URI"));
        Element keyRef20Element = (Element)keyRef10Element.getNextSibling();
        assertEquals(EncryptionConstants.EncryptionSpecNS, keyRef20Element.getNamespaceURI());
        assertEquals(EncryptionConstants._TAG_KEYREFERENCE, keyRef20Element.getLocalName());
        assertEquals("#id20", keyRef20Element.getAttribute("URI"));
        assertNull(keyRef20Element.getNextSibling());

        // See SANTUARIO-304
        // https://issues.apache.org/jira/browse/SANTUARIO-304
        Iterator<Reference> iter = dataRefList.getReferences();
        Reference ref = iter.next();
        assertEquals(EncryptionConstants._TAG_DATAREFERENCE, ref.getType());
        assertEquals("#id1", ref.getURI());
        ref = iter.next();
        assertEquals(EncryptionConstants._TAG_DATAREFERENCE, ref.getType());
        assertEquals("#id2", ref.getURI());
        assertTrue(!iter.hasNext());
        
        iter = keyRefList.getReferences();
        ref = iter.next();
        assertEquals(EncryptionConstants._TAG_KEYREFERENCE, ref.getType());
        assertEquals("#id10", ref.getURI());
        ref = iter.next();
        assertEquals(EncryptionConstants._TAG_KEYREFERENCE, ref.getType());
        assertEquals("#id20", ref.getURI());
        assertTrue(!iter.hasNext());
    }
}
