package net.webassembletool.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

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
public class XsltRenderer implements Renderer {
	private final Transformer transformer;

	public XsltRenderer(String template, ServletContext ctx) throws IOException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		InputStream templateStream = ctx.getResourceAsStream(template);
		try {
			transformer = tFactory.newTransformer(new StreamSource(
					templateStream));
		} catch (TransformerConfigurationException e) {
			throw new ProcessingFailedException(
					"failed to create XSLT template", e);
		} finally {
			templateStream.close();
		}
	}

	/** {@inheritDoc} */
	public void render(String src, Writer out) throws IOException,
			HttpErrorPage {
		try {
			DOMParser domParser = new DOMParser();
			domParser.parse(new InputSource(new StringReader(src)));
			Document document = domParser.getDocument();
			DOMResult result = new DOMResult();
			transformer.transform(new DOMSource(document), result);
			Node transformed = result.getNode();
			Properties props = OutputPropertiesFactory
					.getDefaultMethodProperties(Method.HTML);
			Serializer ser = SerializerFactory.getSerializer(props);
			ser.setWriter(out);
			DOMSerializer dSer = ser.asDOMSerializer();
			dSer.serialize(transformed);
		} catch (SAXException e) {
			throw new ProcessingFailedException("unable to parse source", e);
		} catch (TransformerException e) {
			throw new ProcessingFailedException("failed to transform source", e);
		}
	}
}
