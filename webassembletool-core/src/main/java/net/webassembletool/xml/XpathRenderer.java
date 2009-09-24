package net.webassembletool.xml;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.Properties;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.Renderer;

import org.apache.xml.serializer.DOMSerializer;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.cyberneko.html.parsers.DOMParser;
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
	private final XPathExpression expr;

	public XpathRenderer(String xpath) {
		XPath xpathObj = XPathFactory.newInstance().newXPath();
		try {
			expr = xpathObj.compile(xpath);
		} catch (XPathExpressionException e) {
			throw new ProcessingFailedException(
					"failed to compile XPath expression", e);
		}
	}

	/** {@inheritDoc} */
	public void render(String src, Writer out)
			throws IOException, HttpErrorPage {
		try {
			DOMParser domParser = new DOMParser();
			domParser.parse(new InputSource(new StringReader(src)));
			Document document = domParser.getDocument();
			Node xpathed = (Node) expr.evaluate(document, XPathConstants.NODE);
			Properties props = OutputPropertiesFactory
					.getDefaultMethodProperties(Method.HTML);
			Serializer ser = SerializerFactory.getSerializer(props);
			ser.setWriter(out);
			DOMSerializer dSer = ser.asDOMSerializer();
			dSer.serialize(xpathed);
		} catch (SAXException e) {
			throw new ProcessingFailedException("unable to parse source", e);
		} catch (XPathExpressionException e) {
			throw new ProcessingFailedException(
					"failed to evaluate XPath expression", e);
		}
	}
}
