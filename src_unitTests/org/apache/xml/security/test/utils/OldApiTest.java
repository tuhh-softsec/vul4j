package org.apache.xml.security.test.utils;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class OldApiTest extends TestCase {
	static Transform compare;
	public static class OldTransform extends TransformSpi {
		protected XMLSignatureInput enginePerformTransform(
			      XMLSignatureInput input)
			         throws IOException,
			                CanonicalizationException, InvalidCanonicalizerException,
			                TransformationException, ParserConfigurationException,
			                SAXException {
			assertEquals(compare,_transformObject);
				 return null ;
	     }

		protected String engineGetURI() {
			// TODO Auto-generated method stub
			return null;
		};
	};
	public void testOldTransformSpiApi() throws Exception {
		Init.init();
		Document doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Transform.register("old", OldTransform.class.getName());

		Transform a=new Transform(doc,"old",null);
		compare=a;
		a.performTransform(null);
	};
}
