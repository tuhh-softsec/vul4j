/*
 * Copyright  2008-2010 The Apache Software Foundation.
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

import java.io.File;
import java.io.FileInputStream;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.utils.Constants;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Certificate parsing test.
 *
 * @author Sean Mullan
 */
public class XMLX509CertificateTest extends TestCase {

    private static final String BASEDIR =
        System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
    private static final String SEP = System.getProperty("file.separator");

    public XMLX509CertificateTest() {
	super("XMLX509CertificateTest");
    }

    public XMLX509CertificateTest(String name) {
	super(name);
    }

    public static Test suite() {
	return new TestSuite(XMLX509CertificateTest.class);
    }

    public void testGetX509Certificate() throws Exception {

        File f = null;
        f = new File(BASEDIR + SEP + "data" + SEP + "ie" + SEP + "baltimore" +
	    SEP + "merlin-examples" + SEP + "merlin-xmldsig-twenty-three" +
	    SEP + "signature-x509-crt.xml");

	FileInputStream fis = new FileInputStream(f);
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	dbf.setNamespaceAware(true);
	Document doc = dbf.newDocumentBuilder().parse(fis);
	NodeList nl = doc.getElementsByTagNameNS
		(Constants.SignatureSpecNS, "X509Certificate");
	XMLX509Certificate xmlCert = 
	    new XMLX509Certificate((Element) nl.item(0), "");
	xmlCert.getX509Certificate();
	// System.out.println(cert);
    }
}
