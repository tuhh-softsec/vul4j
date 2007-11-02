package org.apache.xml.security.test.signature;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PublicKey;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.Init;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import junit.framework.TestCase;

/**
 * Test case contributed by Matthias Germann for testing that bug 43239 is
 * fixed: "No installed provider supports this key" when checking a RSA 
 * signature against a DSA key before RSA key.
 */
public class InvalidKeyTest extends TestCase {

    static {
	Init.init();
    }

    private static final String BASEDIR = System.getProperty("basedir");
    private static final String SEP = System.getProperty("file.separator");

    public void test() throws Exception {
	FileInputStream input = new FileInputStream(BASEDIR + SEP + 
	    "data/org/apache/xml/security/samples/input/truststore.jks");
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(input, "testpw".toCharArray());
        
	try {
            validate(trustStore.getCertificate("bedag-test").getPublicKey());
	} catch (Exception e) {
	    e.printStackTrace();
	}
        validate(trustStore.getCertificate("a70-garaio-frontend-u").getPublicKey());
    }
    
    public void validate(PublicKey pk) throws Exception {
        FileInputStream is = new FileInputStream(BASEDIR + SEP +
	    "data/org/apache/xml/security/samples/input/test-assertion.xml");
            
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document e = dbf.newDocumentBuilder().parse(is);
            
        NodeList nl = e.getFirstChild().getChildNodes();
        Element n = (Element)nl.item(nl.getLength()-1);
            
        XMLSignature si = new XMLSignature((Element)n,"");
        si.checkSignatureValue(pk);

       	System.out.println("VALIDATION OK" );
    }
}
