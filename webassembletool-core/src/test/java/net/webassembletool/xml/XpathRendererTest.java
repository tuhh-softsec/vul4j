package net.webassembletool.xml;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;
import net.webassembletool.HttpErrorPage;

public class XpathRendererTest extends TestCase {

	/**
	 * Tests xpath expression evaluation
	 * 
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	public void testXpath() throws IOException, HttpErrorPage {
		String src = "<html><body>The body<br></body></html>";
		StringWriter out = new StringWriter();
		XpathRenderer tested = new XpathRenderer("//html:body");
		tested.render(src, out);
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
		String src = "<html><body>The body<br></body></html>";
		StringWriter out = new StringWriter();
		XpathRenderer tested = new XpathRenderer("//html:body", "html");
		tested.render(src, out);
		assertEquals(
				"<body xmlns=\"http://www.w3.org/1999/xhtml\">The body<br></body>",
				out.toString());
	}
}
