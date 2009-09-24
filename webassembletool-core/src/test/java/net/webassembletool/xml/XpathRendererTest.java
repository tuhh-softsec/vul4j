package net.webassembletool.xml;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;
import net.webassembletool.HttpErrorPage;
import net.webassembletool.xml.XpathRenderer;

public class XpathRendererTest extends TestCase {

	/**
	 * Tests xpath expression evaluation using a mock XpathExpression just to
	 * check that the expression is used with the right arguments
	 * 
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	public void testXpath() throws IOException, HttpErrorPage {
		String src = "<html><body>The body</body></html>";
		StringWriter out = new StringWriter();
		XpathRenderer tested = new XpathRenderer("//BODY");
		tested.render(src, out);
		assertEquals("<BODY>The body</BODY>", out.toString().replaceAll("[\\t\\r\\n]",
				""));
	}
}
