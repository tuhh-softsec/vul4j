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
package org.apache.xml.security.test.dom.transforms.implementations;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.test.dom.DSNamespaceContext;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.XMLUtils;

public class TransformXSLTTest extends org.junit.Assert {

    private static final String BASEDIR = 
        System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
    private static final String SEP = System.getProperty("file.separator");
    private static final String SOURCE_PATH = 
        "src/test/resources/com/phaos/phaos-xmldsig-three/";
    private static final String SIGNATURE_FILE = 
        "signature-rsa-detached-xslt-transform.xml";
    private static final String STYLESHEET_FILE = "document-stylesheet.xml";

    static org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(TransformXSLTTest.class);
    
    static {
        org.apache.xml.security.Init.init();
    }

    /**
     * Make sure Transform.performTransform does not throw NullPointerException.
     * See bug 41927 for more info.
     */
    @org.junit.Test
    public void test1() throws Exception {
        File file1  = null;
        File file2  = null;
        if (BASEDIR != null && !"".equals(BASEDIR)) {
            file1 = new File(BASEDIR + SEP + SOURCE_PATH, SIGNATURE_FILE);
            file2 = new File(BASEDIR + SEP + SOURCE_PATH, STYLESHEET_FILE);
        } else {
            file1 = new File(SOURCE_PATH, SIGNATURE_FILE);
            file1 = new File(SOURCE_PATH, STYLESHEET_FILE);
        }
        Document doc1 = getDocument(file1);
        Document doc2 = getDocument(file2);

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());

        String expression = "//ds:Transform[1]";
        Element transformEl = 
            (Element) xpath.evaluate(expression, doc1, XPathConstants.NODE);

        Transform transform = 
            new Transform(doc1, Transforms.TRANSFORM_XSLT, transformEl.getChildNodes());

        transform.performTransform(new XMLSignatureInput(doc2));
    }
    
    private static Document getDocument(File file) 
        throws ParserConfigurationException, SAXException, IOException {
        return XMLUtils.createDocumentBuilder(false).parse(new FileInputStream(file));
    }

}
