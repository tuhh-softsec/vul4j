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
package org.apache.xml.security.test.encryption;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.encryption.Reference;
import org.apache.xml.security.encryption.ReferenceList;
import org.apache.xml.security.encryption.XMLCipher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * See SANTUARIO-302:
 * https://issues.apache.org/jira/browse/SANTUARIO-302
 */
public class RefListMarshalTest extends org.junit.Assert {

    /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(RefListMarshalTest.class.getName());

    private DocumentBuilder db;

    public RefListMarshalTest() throws Exception {
        org.apache.xml.security.Init.init();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        db = dbf.newDocumentBuilder();
    }

    @org.junit.Test
    public void testMarshalReferenceList() throws Exception {
        Document doc = db.newDocument();

        XMLCipher dataCipher = XMLCipher.getInstance();
        dataCipher.init(XMLCipher.DECRYPT_MODE, null);

        ReferenceList refList = dataCipher.createReferenceList(ReferenceList.DATA_REFERENCE);
        Reference dataRef = refList.newDataReference("http://host");
        refList.add(dataRef);
        
        Element refListElement = dataCipher.martial(doc, refList);
        doc.appendChild(refListElement);
        assertNotNull(refListElement);
    }
    
}
