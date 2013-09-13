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
package org.apache.xml.security.test.dom.signature;

import java.io.File;
import java.io.FileInputStream;
import java.security.PublicKey;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.apache.xml.security.Init;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.KeyValue;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;

public class KeyValueTest extends org.junit.Assert {

    private static final String BASEDIR = System.getProperty("basedir");
    private static final String SEP = System.getProperty("file.separator");

    private DocumentBuilder db;
    
    static {
        Init.init();
    }

    public KeyValueTest() throws Exception {
        db = XMLUtils.createDocumentBuilder(false);
    }

    @org.junit.Test
    public void testDSAPublicKey() throws Exception {
        File f = null;
        String filename = 
            "src/test/resources/ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/signature-enveloping-dsa.xml";
        if (BASEDIR != null && !"".equals(BASEDIR)) {
            f = new File(BASEDIR + SEP + filename);
        } else {
            f = new File(filename);
        }
        Document doc = db.parse(new FileInputStream(f));
        NodeList nl = doc.getElementsByTagNameNS(Constants.SignatureSpecNS, "Signature");
        XMLSignature sig = new XMLSignature
            ((Element) nl.item(0), f.toURI().toURL().toString());
        KeyInfo ki = sig.getKeyInfo();
        KeyValue kv = ki.itemKeyValue(0);
        PublicKey pk = kv.getPublicKey();
        assertNotNull(pk);
    }
    
}
