package org.apache.xml.security.test.c14n.implementations;

import java.io.FileInputStream;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.ObjectContainer;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.Constants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;

public class Bug45961Test extends TestCase {

	private static final String OBJECT_ID = "Object";
	private static final String MOCK_CANONICALIZATION_METHOD = MockCanonicalizationMethod.MOCK_CANONICALIZATION_METHOD;
	private static final char[] PASSWORD = "changeit".toCharArray();
	private static final String ALIAS = "mullan";
	private DocumentBuilder _builder;

	@Override
	protected void setUp() throws Exception {
		Init.init();
		Canonicalizer.register(MOCK_CANONICALIZATION_METHOD,
				MockCanonicalizationMethod.class.getName());
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		_builder = factory.newDocumentBuilder();
	}

	public void testBug() throws Exception {
		Document document = getSignedDocument();
		try {
			Element element = (Element) document.getFirstChild();
			assertEquals(Constants.getSignatureSpecNSprefix() + ":"
					+ Constants._TAG_SIGNATURE, element.getNodeName());
			new XMLSignature(element, null);
		} catch (XMLSignatureException e) {
			fail(e.getMessage());
		}
	}

	private Document getSignedDocument() throws Exception {
		KeyStore ks = KeyStore.getInstance("JKS");
		FileInputStream fis = new FileInputStream(getAbsolutePath("data/test.jks"));
		ks.load(fis, PASSWORD);
		fis.close();
		PrivateKey privateKey = (PrivateKey) ks.getKey(ALIAS, PASSWORD);
		X509Certificate signingCert = (X509Certificate) ks
				.getCertificate(ALIAS);

		Document document = _builder.newDocument();

		XMLSignature signature = new XMLSignature(document, null,
				XMLSignature.ALGO_ID_SIGNATURE_DSA,
				MOCK_CANONICALIZATION_METHOD);

		document.appendChild(signature.getElement());

		Element root = document.createElementNS("", "RootElement");
		root.appendChild(document.createTextNode("Some simple test\n"));
		ObjectContainer object = new ObjectContainer(document);
		object.appendChild(root);
		object.setId(OBJECT_ID);

		signature.addDocument("#" + OBJECT_ID);

		signature.addKeyInfo(signingCert);
		signature.sign(privateKey);
		return document;
	}

   private String getAbsolutePath(String path)
   {
          String basedir = System.getProperty("basedir");
          if(basedir != null && !"".equals(basedir)) {
                path = basedir + "/" + path;
          }
          return path;
   }
}
