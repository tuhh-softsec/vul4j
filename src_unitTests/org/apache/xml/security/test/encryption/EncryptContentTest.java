/*
 * Copyright  2007 The Apache Software Foundation.
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
package org.apache.xml.security.test.encryption;

import java.io.ByteArrayInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.xml.security.encryption.XMLCipher;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class EncryptContentTest extends TestCase {

    private static final String DATA =
	"<users>\n" +
  	"  <user>\n" +
    	"    <firstname>Bugs</firstname>\n" +
        "    <lastname>Bunny</lastname>\n" +
        "    <age>34</age>\n" +
        "    <serial>Y10</serial>\n" +
	"  </user>\n" +
	"</users>\n";

    private DocumentBuilder db;
    private Transformer t;
    private SecretKey secretKey;

    public static Test suite() throws Exception {
        return new TestSuite(EncryptContentTest.class);
    }

    public EncryptContentTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {

        org.apache.xml.security.Init.init();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
	db = dbf.newDocumentBuilder();

        byte[] bits192 = "abcdefghijklmnopqrstuvwx".getBytes();
        DESedeKeySpec keySpec = new DESedeKeySpec(bits192);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        secretKey = keyFactory.generateSecret(keySpec);

	TransformerFactory tf = TransformerFactory.newInstance();
	t = tf.newTransformer();
    }

    public void testContentRemoved() throws Exception {

	Document doc = db.parse(new ByteArrayInputStream(DATA.getBytes("UTF8")));
	NodeList dataToEncrypt = doc.getElementsByTagName("user");

	XMLCipher dataCipher = XMLCipher.getInstance(XMLCipher.TRIPLEDES);
	dataCipher.init(XMLCipher.ENCRYPT_MODE, secretKey);

	for (int i = 0; i < dataToEncrypt.getLength(); i++) {
    	    dataCipher.doFinal(doc,(Element) dataToEncrypt.item(i), true);
	}

	// Check that user content has been removed
	Element user = (Element) dataToEncrypt.item(0);
        Node child = user.getFirstChild();
        while (child != null && child.getNodeType() != Node.ELEMENT_NODE) {
            child = child.getNextSibling();
        }
	// child should be EncryptedData, if not throw exception
	Element childElem = (Element) child;
	if (!childElem.getLocalName().equals("EncryptedData")) {
	    t.transform(new DOMSource(doc), new StreamResult(System.out));
	    throw new Exception("Element content not replaced");
	}
	// there shouldn't be any more children elements
        Node sibling = childElem.getNextSibling();
        while (sibling != null && sibling.getNodeType() != Node.ELEMENT_NODE) {
            sibling = sibling.getNextSibling();
        }
	if (sibling != null) {
	    t.transform(new DOMSource(doc), new StreamResult(System.out));
	    throw new Exception("Sibling element content not replaced");
	}

	t.transform(new DOMSource(doc), new StreamResult(System.out));
    }
}
