package org.apache.xml.security.test.signature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import junit.framework.TestCase;

import org.apache.xml.security.Init;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.RFC2253Parser;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class X509DataTest extends TestCase {

    private static final String BASEDIR = System.getProperty("basedir");

	KeyStore ks=null;
	KeyStore getKeyStore() throws Exception {
		if (ks!=null)
			return ks;
		String keystoreType = "JKS";
	    String keystoreFile = "data/org/apache/xml/security/samples/input/keystore.jks";
	    String keystorePass = "xmlsecurity";	    
	    ks = KeyStore.getInstance(keystoreType);
	    FileInputStream fis = null;
            if (BASEDIR != null && !"".equals(BASEDIR)) {
                fis = new FileInputStream(BASEDIR + "/" + keystoreFile);
	    } else {
                fis = new FileInputStream(keystoreFile);
	    }
	    //load the keystore
	    ks.load(fis, keystorePass.toCharArray());
	    return ks;
	}
	X509Certificate getCertificate() throws Exception {
		String certificateAlias = "test";	    
		X509Certificate cert =
            (X509Certificate) getKeyStore().getCertificate(certificateAlias);
		return cert;
	}
	PrivateKey getPrivateKey() throws Exception {
		String privateKeyAlias = "test";
	    String privateKeyPass = "xmlsecurity";	    
		PrivateKey privateKey = (PrivateKey) getKeyStore().getKey(privateKeyAlias,
                privateKeyPass.toCharArray());
		return privateKey;
	}
	public XMLSignature getSignature(byte[] s) throws Exception {
		javax.xml.parsers.DocumentBuilderFactory dbf =
	         javax.xml.parsers.DocumentBuilderFactory.newInstance();

	      //XML Signature needs to be namespace aware
	      dbf.setNamespaceAware(true);

	      javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc=db.parse(new ByteArrayInputStream(s));
		Element el=(Element)doc.getFirstChild();
		return new XMLSignature(el,"");
	}
	public void testAddX509SubjectName() throws Exception {
		Init.init();
		javax.xml.parsers.DocumentBuilderFactory dbf =
	         javax.xml.parsers.DocumentBuilderFactory.newInstance();

	      //XML Signature needs to be namespace aware
	      dbf.setNamespaceAware(true);

	      javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
	      org.w3c.dom.Document doc = db.newDocument();
		 	XMLSignature sig = new XMLSignature(doc, "",
                 XMLSignature.ALGO_ID_SIGNATURE_DSA);
		 	doc.appendChild(sig.getElement());
		 	sig.addDocument("");
	         //Add in the KeyInfo for the certificate that we used the private key of	         
		 	X509Certificate cert =getCertificate();
	         sig.addKeyInfo(cert);
	         sig.addKeyInfo(cert.getPublicKey());
	         // Add these three lines
	         org.apache.xml.security.keys.KeyInfo ki = sig.getKeyInfo();
	         ki.itemX509Data(0).addSubjectName(cert.getSubjectDN().toString());
	         ki.itemX509Data(0).addIssuerSerial(cert.getIssuerDN().toString(),
	        		 cert.getSerialNumber());	         
	         sig.sign(getPrivateKey());
	         ByteArrayOutputStream os = new ByteArrayOutputStream();
	         XMLUtils.outputDOM(doc, os);
	         XMLSignature newSig=getSignature(os.toByteArray());
	         assertNotNull(newSig.getKeyInfo().itemX509Data(0));
	         assertEquals(RFC2253Parser.normalize(cert.getSubjectDN().toString()),
	        		 	newSig.getKeyInfo().itemX509Data(0).itemSubjectName(0).getSubjectName());
	         assertEquals(RFC2253Parser.normalize(cert.getIssuerDN().toString()),
	        		 	newSig.getKeyInfo().itemX509Data(0).itemIssuerSerial(0).getIssuerName());
	
	}
	
}
