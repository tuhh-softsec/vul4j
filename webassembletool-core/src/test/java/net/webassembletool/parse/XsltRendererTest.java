package net.webassembletool.parse;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import junit.framework.TestCase;
import net.webassembletool.RenderingException;
import net.webassembletool.RetrieveException;
import net.webassembletool.output.MockStringOutput;
import net.webassembletool.output.StringOutput;

import org.xml.sax.SAXException;

public class XsltRendererTest extends TestCase {

	/**
	 * Tests renderer's behavior in case of a Gateway Timeout HTTP error while
	 * retrieving the document. Should throw a RetrieveException with the error
	 * code and message
	 * 
	 * @throws IOException
	 * @throws RenderingException
	 */
	public void testGatewayTimeout() throws IOException, RenderingException {
		XsltRenderer tested = new XsltRenderer(null, null, null);
		StringWriter out = new StringWriter();
		StringOutput src = new MockStringOutput(
				"<list><item>item 1</item><item>item 2</item></list>");
		src.setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT, "expected");
		try {
			tested.render(src, out, null);
			fail("should throw RetrieveException");
		} catch (RetrieveException e) {
			assertEquals(HttpServletResponse.SC_GATEWAY_TIMEOUT, e
					.getStatusCode());
			assertEquals("expected", e.getStatusMessage());
			assertEquals(src.toString(), e.getErrorPageContent());
		}
	}

	/**
	 * Tests xpath expression evaluation using a mock XpathExpression just to
	 * check that the expression is used with the right arguments
	 * 
	 * @throws IOException
	 * @throws RenderingException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	public void testXpath() throws IOException, RenderingException,
			SAXException, ParserConfigurationException,
			XPathExpressionException {
		StringOutput src = new MockStringOutput(
				"<html><body>The body</body></html>");
		src.setStatusCode(HttpServletResponse.SC_OK);
		StringWriter out = new StringWriter();
		XsltRenderer tested = new XsltRenderer("//BODY", null, null);
		tested.render(src, out, null);
		assertEquals("<BODY>The body</BODY>", out.toString().replaceAll("[\\t\\r\\n]",
				""));
	}
}
