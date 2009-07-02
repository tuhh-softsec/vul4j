package net.webassembletool.parse;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.webassembletool.RenderingException;
import net.webassembletool.RetrieveException;
import net.webassembletool.output.StringOutput;

import org.apache.xml.serializer.DOMSerializer;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.cyberneko.html.parsers.DOMFragmentParser;
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
public class XsltRenderer implements Renderer {
	private final XPathExpression expr;
	private final Transformer transformer;

	public XsltRenderer(String xpath, String template, ServletContext ctx)
			throws ProcessingFailedException, IOException {
		if (xpath != null) {
			XPath xpathObj = XPathFactory.newInstance().newXPath();
			try {
				expr = xpathObj.compile(xpath);
			} catch (XPathExpressionException e) {
				throw new ProcessingFailedException(
						"failed to compile XPath expression", e);
			}
		} else {
			expr = null;
		}
		TransformerFactory tFactory = TransformerFactory.newInstance();
		if (template != null) {
			InputStream templateStream = ctx.getResourceAsStream(template);
			try {
				try {
					transformer = tFactory.newTransformer(new StreamSource(
							templateStream));
				} catch (TransformerConfigurationException e) {
					throw new ProcessingFailedException(
							"failed to create XSLT template", e);
				}
			} finally {
				templateStream.close();
			}
		} else {
			transformer = null;
		}

	}

	/** {@inheritDoc} */
	public void render(StringOutput src, Writer out, Map<String, String> unused)
			throws IOException, RenderingException {
		if (src.getStatusCode() != HttpServletResponse.SC_OK) {
			throw new RetrieveException(src.getStatusCode(), src
					.getStatusMessage(), src.toString());
		}

		try {
			Document document = createSourceDocument(src);
			Node xpathed;
			if (expr != null) {
				xpathed = (Node) expr.evaluate(document, XPathConstants.NODE);
			} else {
				xpathed = document;
			}

			Node transformed;
			if (transformer != null) {
				DOMResult result = new DOMResult();
				transformer.transform(new DOMSource(xpathed), result);
				transformed = result.getNode();
			} else {
				transformed = xpathed;
			}

			Properties props = OutputPropertiesFactory
					.getDefaultMethodProperties(Method.HTML);
			Serializer ser = SerializerFactory.getSerializer(props);
			ser.setWriter(out);
			DOMSerializer dSer = ser.asDOMSerializer();
			dSer.serialize(transformed);
		} catch (SAXException e) {
			throw new ProcessingFailedException("unable to parse source", e);
		} catch (ParserConfigurationException e) {
			throw new ProcessingFailedException("unable to create DOM builder",
					e);
		} catch (XPathExpressionException e) {
			throw new ProcessingFailedException(
					"failed to evaluate XPath expression", e);
		} catch (TransformerException e) {
			throw new ProcessingFailedException("failed to transform source", e);
		}
	}

	private Document createSourceDocument(StringOutput src)
			throws SAXException, IOException, ParserConfigurationException {
		DOMParser domParser = new DOMParser();
		domParser.parse(new InputSource(new StringReader(src.toString())));
		return domParser.getDocument();
	}

}
