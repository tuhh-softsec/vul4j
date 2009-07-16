package net.webassembletool.parse;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;
import net.webassembletool.HttpErrorPage;
import net.webassembletool.output.MockStringOutput;
import net.webassembletool.output.StringOutput;

public class XsltRendererTest extends TestCase {

	/**
	 * Tests xpath expression evaluation using a mock XpathExpression just to
	 * check that the expression is used with the right arguments
	 * 
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	public void testXpath() throws IOException, HttpErrorPage {
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
