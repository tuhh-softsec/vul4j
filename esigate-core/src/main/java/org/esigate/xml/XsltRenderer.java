package org.esigate.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import nu.validator.htmlparser.common.DoctypeExpectation;
import nu.validator.htmlparser.common.XmlViolationPolicy;
import nu.validator.htmlparser.sax.HtmlParser;

import org.apache.commons.io.IOUtils;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.Renderer;
import org.esigate.ResourceContext;
import org.esigate.api.HttpRequest;
import org.xml.sax.InputSource;

/**
 * Applies an XSLT transformation to the retrieved data.
 * 
 * If no namespace is specified in the document to transform, it is asumed as:<br />
 * xmlns="http://www.w3.org/1999/xhtml"
 * 
 * @author Stanislav Bernatskyi
 * @author Francois-Xavier Bonnet
 */
public class XsltRenderer implements Renderer {
	private final static TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
	private Transformer transformer;

	/**
	 * @param template
	 *            The path to the xsl template, relative to the context root
	 * @param driver
	 *            driver
	 * @throws IOException
	 *             If an error occurs while writing to the output
	 * @throws HttpErrorPage 
	 */
	public XsltRenderer(String template, Driver driver, ResourceContext resourceContext) throws IOException, HttpErrorPage {	
		try {
			InputStream templateStream = resourceContext.getOriginalRequest().getSession(true).getResourceTemplate(template);
			if(templateStream == null)
				throw new ProcessingFailedException("Template " + template + " not found");
			transformer = createTransformer(templateStream);
		} catch (Exception e) {
			transformer = createTransformer(IOUtils.toInputStream(driver.getResourceAsString(template, resourceContext)));
		}
	}
	
	/**
	 * @param template
	 *            The path to the xsl template, relative to the context root
	 * @param request
	 *            HttpRequest
	 * @throws IOException
	 *             If an error occurs while writing to the output
	 */
	public XsltRenderer(String template, HttpRequest request) throws IOException {
		InputStream templateStream = request.getSession(true).getResourceTemplate(template);
		if(templateStream == null)
			throw new ProcessingFailedException("Template " + template + " not found");
		transformer = createTransformer(templateStream);
	}

	/**
	 * @param xsl
	 *            The xsl template to apply as a String
	 * @throws IOException
	 *             If an error occurs while writing to the output
	 */
	public XsltRenderer(String xsl) throws IOException {
		InputStream templateStream = IOUtils.toInputStream(xsl);
		transformer = createTransformer(templateStream);
	}
	
	private static Transformer createTransformer(InputStream templateStream) throws IOException {
		try {
			return TRANSFORMER_FACTORY.newTransformer(new StreamSource(templateStream));
		} catch (TransformerConfigurationException e) {
			throw new ProcessingFailedException("Failed to create XSLT template", e);
		} finally {
			templateStream.close();
		}
	}

	/** {@inheritDoc} */
	public void render(ResourceContext requestContext, String src, Writer out) throws IOException {
		try {
			HtmlParser htmlParser = new HtmlParser(XmlViolationPolicy.ALLOW);
			htmlParser.setDoctypeExpectation(DoctypeExpectation.NO_DOCTYPE_ERRORS);
			Source source = new SAXSource(htmlParser, new InputSource(new StringReader(src)));
			transformer.transform(source, new StreamResult(out));
		} catch (TransformerException e) {
			throw new ProcessingFailedException("Failed to transform source", e);
		}
	}
}
