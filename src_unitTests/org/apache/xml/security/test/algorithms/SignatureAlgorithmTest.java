package org.apache.xml.security.test.algorithms;

import java.security.KeyPairGenerator;
import java.security.PrivateKey;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Document;

public class SignatureAlgorithmTest extends TestCase {
	public void testSameKeySeveralAlgorithSigning() throws Exception {
		Document doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		SignatureAlgorithm signatureAlgorithm = new SignatureAlgorithm(doc,XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
		PrivateKey pk=KeyPairGenerator.getInstance("RSA").genKeyPair().getPrivate();
		signatureAlgorithm.initSign(pk);
		signatureAlgorithm.update((byte)2);
		signatureAlgorithm.sign();
		SignatureAlgorithm otherSignatureAlgorithm = new SignatureAlgorithm(doc,XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256);
		otherSignatureAlgorithm.initSign(pk);
		otherSignatureAlgorithm.update((byte)2);
		otherSignatureAlgorithm.sign();
	}
	public static Test suite() {
		return new TestSuite(SignatureAlgorithmTest.class);
	}
}
