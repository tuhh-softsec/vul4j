package org.esigate.xml;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nu.validator.htmlparser.common.DoctypeExpectation;
import nu.validator.htmlparser.common.XmlViolationPolicy;
import nu.validator.htmlparser.dom.HtmlDocumentBuilder;

import org.esigate.Renderer;
import org.esigate.ResourceContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML renderer.
 * <p>
 * Applies optional XPath evaluation and XSLT transformation to the retrieved
 * data.
 * 
 * @author Stanislav Bernatskyi
 */
public class XpathRenderer implements Renderer {
	private final static HtmlNamespaceContext HTML_NAMESPACE_CONTEXT = new HtmlNamespaceContext();
	private final static XPathFactory X_PATH_FACTORY = XPathFactory.newInstance();
	private final static TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
	private final XPathExpression expr;
	private final String outputMethod;

	public XpathRenderer(String xpath) {
		this(xpath,  "xml");
	}

	public XpathRenderer(String xpath, String outputMethod) {
		try {
			XPath xpathObj = X_PATH_FACTORY.newXPath();
			xpathObj.setNamespaceContext(HTML_NAMESPACE_CONTEXT);
			this.expr = xpathObj.compile(xpath);
			this.outputMethod = outputMethod;
		} catch (XPathExpressionException e) {
			throw new ProcessingFailedException("failed to compile XPath expression", e);
		}
	}

	/** {@inheritDoc} */
	public void render(ResourceContext requestContext, String src, Writer out) throws IOException {
		try {
			HtmlDocumentBuilder htmlDocumentBuilder = new HtmlDocumentBuilder(XmlViolationPolicy.ALLOW);
			htmlDocumentBuilder.setDoctypeExpectation(DoctypeExpectation.NO_DOCTYPE_ERRORS);
			Document document = htmlDocumentBuilder.parse(new InputSource(new StringReader(src)));
			Node xpathed = (Node) expr.evaluate(document, XPathConstants.NODE);
			Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, outputMethod);
			Source source = new DOMSource(xpathed);
			transformer.transform(source, new StreamResult(out));
		} catch (XPathExpressionException e) {
			throw new ProcessingFailedException("Failed to evaluate XPath expression", e);
		} catch (TransformerException e) {
			throw new ProcessingFailedException("Unable to parse source", e);
		} catch (SAXException e) {
			throw new ProcessingFailedException("Unable to parse source", e);
		}
	}
}
