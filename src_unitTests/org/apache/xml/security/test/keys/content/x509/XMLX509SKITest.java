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
package org.apache.xml.security.test.keys.content.x509;

import java.io.File;
import java.io.FileInputStream;
import java.security.cert.*;
import java.util.Collection;
import java.util.Collections;
import org.apache.xml.security.keys.content.x509.XMLX509SKI;
import org.apache.xml.security.utils.Base64;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test bugfix 41892: XML Security 1.4.0 does not build with IBM's JDK
 */
public class XMLX509SKITest extends TestCase {

    private static final String BASEDIR = System.getProperty("basedir");
    private static final String SEP = System.getProperty("file.separator");
    private CertificateFactory cf;

    public XMLX509SKITest() {
	super("XMLX509SKITest");
    }

    public XMLX509SKITest(String name) {
	super(name);
    }

    public static Test suite() {
	return new TestSuite(XMLX509SKITest.class);
    }

    public void setUp() throws Exception {
	cf = CertificateFactory.getInstance("X.509");
    }

    public void testGetSKIBytesFromCert() throws Exception {

        File f = null;
        if (BASEDIR != null && !"".equals(BASEDIR)) {
            f = new File(BASEDIR + SEP +
		"data/ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/certs/lugh.crt");
	} else {
            f = new File(
		"data/ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/certs/lugh.crt");
	}

	FileInputStream fis = new FileInputStream(f);
	X509Certificate cert = (X509Certificate) cf.generateCertificate(fis);

	// Get subject key identifier from certificate
	byte[] skid = XMLX509SKI.getSKIBytesFromCert(cert);

	// Use X509CertSelector to match on certificate using the skid,
	// thereby testing that the returned skid was correct
	X509CertSelector xcs = new X509CertSelector();
	// DER-encode skid - required by X509CertSelector
	byte[] encodedSkid = new byte[skid.length+2];
	encodedSkid[0] = 0x04; // OCTET STRING tag value
	encodedSkid[1] = (byte) skid.length; // length
	System.arraycopy(skid, 0, encodedSkid, 2, skid.length);
	xcs.setSubjectKeyIdentifier(encodedSkid);

	CertStore cs = CertStore.getInstance(
	    "Collection", 
	    new CollectionCertStoreParameters(Collections.singleton(cert)));
	
	Collection certs = cs.getCertificates(xcs);
	assertTrue(!certs.isEmpty());
    }
}
