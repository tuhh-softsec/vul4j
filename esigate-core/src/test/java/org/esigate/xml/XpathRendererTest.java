package org.esigate.xml;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;

public class XpathRendererTest extends TestCase {

	/**
	 * Tests xpath expression evaluation
	 * 
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	public void testXpath() throws IOException, HttpErrorPage {
		String src = "<html><title>The header</title><body>The body<br></body></html>";
		StringWriter out = new StringWriter();
		XpathRenderer tested = new XpathRenderer("/html:html/html:body");
		tested.render(null, src, out);
		assertEquals(
				"<body xmlns=\"http://www.w3.org/1999/xhtml\">The body<br/></body>",
				out.toString());
	}

	/**
	 * Tests xpath expression evaluation with html output method
	 * 
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	public void testXpathOutputHtml() throws IOException, HttpErrorPage {
		String src = "<html><head><title>The header</title></head><body>The body<br></body></html>";
		StringWriter out = new StringWriter();
		XpathRenderer tested = new XpathRenderer("//html:body", "html");
		tested.render(null, src, out);
		assertEquals(
				"<body xmlns=\"http://www.w3.org/1999/xhtml\">The body<br></body>",
				out.toString());
	}
}
