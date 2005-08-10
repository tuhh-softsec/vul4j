/*
 * Copyright  1999-2005 The Apache Software Foundation.
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
package org.apache.xml.security.test.signature;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.xml.security.signature.ObjectContainer;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.XPathContainer;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests that create signatures.
 *
 * @author Sean Mullan
 */
public class CreateSignatureTest extends TestCase {

    /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog
	    (CreateSignatureTest.class.getName());

    private DocumentBuilder db;
    private static final String BASEDIR = System.getProperty("basedir");
    private static final String SEP = System.getProperty("file.separator");

    public static Test suite() {
        return new TestSuite(CreateSignatureTest.class);
    }

    public CreateSignatureTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] testCaseName = { "-noloading",
                                  CreateSignatureTest.class.getName() };

        junit.textui.TestRunner.main(testCaseName);
    }

    public void setUp() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        db = dbf.newDocumentBuilder();
        org.apache.xml.security.Init.init();
    }

    /**
     * Test for bug 36044 - Canonicalizing an empty node-set throws an 
     * ArrayIndexOutOfBoundsException.
     */
    public void testEmptyNodeSet() throws Exception {

        Document doc = db.newDocument();
        Element envelope = doc.createElementNS("http://www.usps.gov/",
                                               "Envelope");
        envelope.appendChild(doc.createTextNode("\n"));
        doc.appendChild(envelope);

        XMLSignature sig = 
	    new XMLSignature(doc, null, XMLSignature.ALGO_ID_SIGNATURE_DSA);

        ObjectContainer object1 = new ObjectContainer(doc);
        object1.setId("object-1");
        object1.setMimeType("text/plain");
        sig.appendObject(object1);

        ObjectContainer object2 = new ObjectContainer(doc);

        object2.setId("object-2");
        object2.setMimeType("text/plain");
        object2.setEncoding("http://www.w3.org/2000/09/xmldsig#base64");
        object2.appendChild(doc.createTextNode("SSBhbSB0aGUgdGV4dC4="));
        sig.appendObject(object2);

        Transforms transforms = new Transforms(doc);
        XPathContainer xpathC = new XPathContainer(doc);

        xpathC.setXPath("self::text()");
        transforms.addTransform(Transforms.TRANSFORM_XPATH,
                                xpathC.getElementPlusReturns());
        sig.addDocument("#object-1", transforms,
                        Constants.ALGO_ID_DIGEST_SHA1, null,
                        "http://www.w3.org/2000/09/xmldsig#Object");

        KeyStore ks = KeyStore.getInstance("JKS");
	FileInputStream fis = null;
        if (BASEDIR != null && !"".equals(BASEDIR)) {
            fis = new FileInputStream(BASEDIR + SEP + 
		  "data/org/apache/xml/security/samples/input/keystore.jks");
	} else {
            fis = new FileInputStream(
		  "data/org/apache/xml/security/samples/input/keystore.jks");
	}
        ks.load(fis, "xmlsecurity".toCharArray());
        PrivateKey privateKey = (PrivateKey) ks.getKey("test",
                                 "xmlsecurity".toCharArray());

	sig.sign(privateKey);
    }
}
