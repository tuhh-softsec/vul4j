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
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class OldApiTest extends TestCase {
	
	public static class OldTransform extends TransformSpi {
		static Transform compare;
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
		OldTransform.compare=a;
		a.performTransform(null);
	};
	
	public static class OldResourceResolverSpi extends ResourceResolverSpi {
		Attr uriCompare;
		String baseCompare;
		public boolean engineCanResolve(Attr uri, String BaseURI) {
			if (uri.getValue().indexOf("!!!test=")!=0) {
				return false;
			}
			uriCompare=uri;
			baseCompare=BaseURI;
			return true;
		}

		public XMLSignatureInput engineResolve(Attr uri, String BaseURI) throws ResourceResolverException {
			assertEquals(uriCompare, uri);
			assertEquals(baseCompare,BaseURI);
			return null;
		}
		
	};
	public void testOldResourceResolverSpi() throws Exception {
		Document doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();		
		Attr uri=doc.createAttribute("id");
		uri.setNodeValue("!!!test=1");
		((Element)doc.createElement("test")).setAttributeNode(uri);
		Attr uri1=doc.createAttribute("id");
		uri1.setNodeValue("!!!test=2");
		doc.createElement("test1").setAttributeNode(uri1);
		ResourceResolver.registerAtStart(OldResourceResolverSpi.class.getName());
		ResourceResolver resolver=ResourceResolver.getInstance(uri, "test");
		ResourceResolver resolver1=ResourceResolver.getInstance(uri1, "test1");
		ResourceResolver resolver2=ResourceResolver.getInstance(uri1, "test2");
		
		resolver2.resolve(uri1, "test2");		
		resolver.resolve(uri, "test");
		resolver1.resolve(uri1, "test1");
		
		
	};
}
