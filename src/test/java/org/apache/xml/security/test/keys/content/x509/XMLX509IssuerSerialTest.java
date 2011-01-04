/*
 * Copyright  2008 The Apache Software Foundation.
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
package org.apache.xml.security.test.keys.content.x509;

import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.apache.xml.security.keys.content.x509.XMLX509IssuerSerial;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * XMLX509IssuerSerial tests.
 *
 * @author Sean Mullan
 */
public class XMLX509IssuerSerialTest extends TestCase {

    private Document doc;

    public XMLX509IssuerSerialTest() {
	super("XMLX509IssuerSerialTest");
    }

    public XMLX509IssuerSerialTest(String name) {
	super(name);
    }

    public static Test suite() {
	return new TestSuite(XMLX509IssuerSerialTest.class);
    }

    public void setUp() throws Exception {
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	dbf.setNamespaceAware(true);
	doc = dbf.newDocumentBuilder().newDocument();
    }

    public void testGetIssuerName() throws Exception {

	// Make sure hex encoded value is not escaped (see ...)
	String issuer = "9.99.999=#abc123";
	XMLX509IssuerSerial is = 
	    new XMLX509IssuerSerial(doc, issuer, 0);
	assertEquals(issuer, is.getIssuerName());
	// System.out.println(is.getIssuerName());
	issuer = "CN=#abc123";
	is = new XMLX509IssuerSerial(doc, issuer, 0);
	assertEquals("CN=\\#abc123", is.getIssuerName());
	// System.out.println(is.getIssuerName());
    }
}
