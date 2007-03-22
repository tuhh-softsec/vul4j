/*
 * Copyright  1999-2007 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.xml.security.test.transforms.implementations;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.XPathAPI;

public class TransformXSLTTest extends TestCase {

    private static final String BASEDIR = System.getProperty("basedir");
    private static final String SEP = System.getProperty("file.separator");
    private static final String SOURCE_PATH = 
	"data/com/phaos/phaos-xmldsig-three/";
    private static final String SIGNATURE_FILE = 
	"signature-rsa-detached-xslt-transform.xml";
    private static final String STYLESHEET_FILE = "document-stylesheet.xml";

    /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(
                    TransformXSLTTest.class.getName());

    public static Test suite() {
        return new TestSuite(TransformXSLTTest.class);
    }

    public TransformXSLTTest(String name) {
        super(name);
    }

    public static void main(String[] args) {

        String[] testCaseName = { "-noloading",
                                TransformXSLTTest.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    private static Document getDocument(File file) 
	throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        dfactory.setNamespaceAware(true);
        DocumentBuilder db = dfactory.newDocumentBuilder();
        Document doc = db.parse(new FileInputStream(file));
        return doc;
    }

    /**
     * Make sure Transform.performTransform does not throw NullPointerException.
     * See bug 41927 for more info.
     */
    public static void test1() throws Exception {
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

        Element nscontext = XMLUtils.createDSctx
	    (doc1, "dsig", Constants.SignatureSpecNS);
        Node transformEl = XPathAPI.selectSingleNode
	    (doc1, "//dsig:Transform[1]", nscontext);
	Transform transform = Transform.getInstance
	    (doc1, Transforms.TRANSFORM_XSLT, transformEl.getChildNodes());

	XMLSignatureInput output = 
	    transform.performTransform(new XMLSignatureInput(doc2));
    }

    static {
        org.apache.xml.security.Init.init();
    }
}
