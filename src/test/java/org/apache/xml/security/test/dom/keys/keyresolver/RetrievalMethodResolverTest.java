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
package org.apache.xml.security.test.dom.keys.keyresolver;

import java.io.FileInputStream;
import java.security.Security;

import javax.xml.parsers.DocumentBuilder;

import org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;


/**
 * Some tests on attacks against the RetrievalMethodResolver. 
 */
public class RetrievalMethodResolverTest extends org.junit.Assert {
    
    private static final String BASEDIR = System.getProperty("basedir");
    private static final String SEP = System.getProperty("file.separator");

    public RetrievalMethodResolverTest() {
        org.apache.xml.security.Init.init();
        Security.insertProviderAt(new XMLDSigRI(), 1);
    }

    @org.junit.Test
    public void testReferenceToSameRetrievalMethod() throws Exception {
        FileInputStream fis = null;
        String filename = "src/test/resources/org/apache/xml/security/keyresolver/retrievalmethod1.xml";
        if (BASEDIR != null && !"".equals(BASEDIR)) {
            fis = new FileInputStream(BASEDIR + SEP + filename);
        } else {
            fis = new FileInputStream(filename);
        }
        
        DocumentBuilder db = XMLUtils.createDocumentBuilder(false);
        Document doc = db.parse(fis);
        
        KeyInfo keyInfo = new KeyInfo(doc.getDocumentElement(), null);
        
        // Check neither of these give a StackOverflowError.
        keyInfo.getPublicKey();
        keyInfo.getX509Certificate();
    }
    
    @org.junit.Test
    public void testLoopBetweenRetrievalMethods() throws Exception {
        FileInputStream fis = null;
        String filename = "src/test/resources/org/apache/xml/security/keyresolver/retrievalmethod2.xml";
        if (BASEDIR != null && !"".equals(BASEDIR)) {
            fis = new FileInputStream(BASEDIR + SEP + filename);
        } else {
            fis = new FileInputStream(filename);
        }
        
        DocumentBuilder db = XMLUtils.createDocumentBuilder(false);
        Document doc = db.parse(fis);
        
        KeyInfo keyInfo = new KeyInfo(doc.getDocumentElement(), null);
        
        // Check neither of these give a StackOverflowError.
        keyInfo.getPublicKey();
        keyInfo.getX509Certificate();
    }
    
}
