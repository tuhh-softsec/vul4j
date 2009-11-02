package net.webassembletool.xml;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;
import net.webassembletool.HttpErrorPage;

public class XsltRendererTest extends TestCase {

	/**
	 * Tests xpath expression evaluation
	 * 
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	public void testXslt() throws IOException, HttpErrorPage {
		String src = "<html><body>The body<br></body></html>";
		String template = "<?xml version=\"1.0\"?>";
		template += "<xsl:stylesheet version=\"1.0\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:html=\"http://www.w3.org/1999/xhtml\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">";
		template += "<xsl:output method=\"xml\" omit-xml-declaration=\"yes\"/> indent=\"no\"";
		template += "<xsl:template match=\"//html:body\">";
		template += "<xsl:copy-of select=\".\"/>";
		template += "</xsl:template>";
		template += "</xsl:stylesheet>";
		StringWriter out = new StringWriter();
		XsltRenderer tested = new XsltRenderer(template);
		tested.render(src, out);
		assertEquals("<body xmlns=\"http://www.w3.org/1999/xhtml\">The body<br/></body>", out.toString());
	}
}
