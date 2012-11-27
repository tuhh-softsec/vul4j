package org.esigate.renderers;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;

/**
 * Tests on ResourceFixupRenderer
 * 
 * @author Nicolas Richeton
 * 
 */
public class ResourceFixupRendererTest extends TestCase {

	public void testRenderBlock1() throws IOException, HttpErrorPage {
		String base = "http://myapp/context";
		String page = "templates/template1.html";
		final String input = "  <img src=\"images/logo.png\"/> <a href=\"/context/page/page1.htm\">link</a> <img src=\"http://www.google.com/logo.com\"/>";
		final String expectedOutputRelative = "  <img src=\"/context/templates/images/logo.png\"/> <a href=\"/context/page/page1.htm\">link</a> <img src=\"http://www.google.com/logo.com\"/>";
		final String expectedOutputAbsolute = "  <img src=\"http://myapp/context/templates/images/logo.png\"/> <a href=\"http://myapp/context/page/page1.htm\">link</a> <img src=\"http://www.google.com/logo.com\"/>";

		Writer out = new StringWriter();
		ResourceFixupRenderer tested = new ResourceFixupRenderer(base, base, page, ResourceFixupRenderer.ABSOLUTE);
		tested.render(null, input, out);
		assertEquals(expectedOutputAbsolute, out.toString());

		out = new StringWriter();
		tested = new ResourceFixupRenderer(base, base, page, ResourceFixupRenderer.RELATIVE);
		tested.render(null, input, out);
		assertEquals(expectedOutputRelative, out.toString());
	}
	
	/**
	 * Ensure CDATA does not match replacement rules.
	 * 
	 * @see https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=120
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	public void testComments() throws IOException, HttpErrorPage {
		String base = "http://myapp/context";
		String page = "templates/template1.html";
		final String input = "<![CDATA[   var src=\"test\" ]]>";
		final String expectedOutputRelative = "<![CDATA[   var src=\"test\" ]]>";
		final String expectedOutputAbsolute = "<![CDATA[   var src=\"test\" ]]>";

		Writer out = new StringWriter();
		ResourceFixupRenderer tested = new ResourceFixupRenderer(base, base, page, ResourceFixupRenderer.ABSOLUTE);
		tested.render(null, input, out);
		assertEquals(expectedOutputAbsolute, out.toString());

		out = new StringWriter();
		tested = new ResourceFixupRenderer(base, base, page, ResourceFixupRenderer.RELATIVE);
		tested.render(null, input, out);
		assertEquals(expectedOutputRelative, out.toString());
	}
	

	public void testUrlReplaceContext() throws IOException, HttpErrorPage {
		String base = "http://myapp/context/";
		String newBase = "http://myapp/newcontext/";
		String page = "templates/template1.html";
		final String input = "  <img src=\"images/logo.png\"/> <a href=\"/context/page/page1.htm\">link</a> <img src=\"http://www.google.com/logo.com\"/>";
		final String expectedOutputRelative = "  <img src=\"/newcontext/templates/images/logo.png\"/> <a href=\"/newcontext/page/page1.htm\">link</a> <img src=\"http://www.google.com/logo.com\"/>";
		final String expectedOutputAbsolute = "  <img src=\"http://myapp/newcontext/templates/images/logo.png\"/> <a href=\"http://myapp/newcontext/page/page1.htm\">link</a> <img src=\"http://www.google.com/logo.com\"/>";

		Writer out = new StringWriter();
		ResourceFixupRenderer tested = new ResourceFixupRenderer(base, newBase, page, ResourceFixupRenderer.ABSOLUTE);
		tested.render(null, input, out);
		assertEquals(expectedOutputAbsolute, out.toString());

		out = new StringWriter();
		tested = new ResourceFixupRenderer(base, newBase, page, ResourceFixupRenderer.RELATIVE);
		tested.render(null, input, out);
		assertEquals(expectedOutputRelative, out.toString());
	}

	public void testUrlSanitizing() throws IOException, HttpErrorPage {
		String base = "http://myapp/context/";
		String page = "templates/template1.html";
		final String input = "  <img src=\"images/logo.png\"/> <a href=\"/context/page/page1.htm\">link</a> <img src=\"http://www.google.com/logo.com\"/>";
		final String expectedOutputRelative = "  <img src=\"/context/templates/images/logo.png\"/> <a href=\"/context/page/page1.htm\">link</a> <img src=\"http://www.google.com/logo.com\"/>";
		final String expectedOutputAbsolute = "  <img src=\"http://myapp/context/templates/images/logo.png\"/> <a href=\"http://myapp/context/page/page1.htm\">link</a> <img src=\"http://www.google.com/logo.com\"/>";

		Writer out = new StringWriter();
		ResourceFixupRenderer tested = new ResourceFixupRenderer(base, base, page, ResourceFixupRenderer.ABSOLUTE);
		tested.render(null, input, out);
		assertEquals(expectedOutputAbsolute, out.toString());

		out = new StringWriter();
		tested = new ResourceFixupRenderer(base, base, page, ResourceFixupRenderer.RELATIVE);
		tested.render(null, input, out);
		assertEquals(expectedOutputRelative, out.toString());
	}

	public void testUrlSanitizing2() throws IOException, HttpErrorPage {
		String base = "http://myapp/context/";
		String visibleBase = "http://app2/";
		String page = "/page/";
		final String input = "  <a href=\"../styles/style.css\"/> <img src=\"images/logo.png\"/> <a href=\"/context/page/page1.htm\">link</a> <img src=\"http://www.google.com/logo.com\"/>";
		final String expectedOutputRelative = "  <a href=\"/page/../styles/style.css\"/> <img src=\"/page/images/logo.png\"/> <a href=\"/page/page1.htm\">link</a> <img src=\"http://www.google.com/logo.com\"/>";
		final String expectedOutputAbsolute = "  <a href=\"http://app2/page/../styles/style.css\"/> <img src=\"http://app2/page/images/logo.png\"/> <a href=\"http://app2/page/page1.htm\">link</a> <img src=\"http://www.google.com/logo.com\"/>";

		Writer out = new StringWriter();
		ResourceFixupRenderer tested = new ResourceFixupRenderer(base, visibleBase, page, ResourceFixupRenderer.ABSOLUTE);
		tested.render(null, input, out);
		assertEquals(expectedOutputAbsolute, out.toString());

		out = new StringWriter();
		tested = new ResourceFixupRenderer(base, visibleBase, page, ResourceFixupRenderer.RELATIVE);
		tested.render(null, input, out);
		assertEquals(expectedOutputRelative, out.toString());
	}

	public void testDollarSignReplacement() throws IOException {
		String base = "http://myapp/context/";
		String visibleBase = "http://app2/";
		String page = "/page/";
		final String input = "  <a href=\"../styles/style$.css\"/> <img src=\"images/logo$.png\"/></a> <img src=\"http://www.google.com/logo.com\"/>";
		final String expectedOutputAbsolute = "  <a href=\"http://app2/page/../styles/style$.css\"/> <img src=\"http://app2/page/images/logo$.png\"/></a> <img src=\"http://www.google.com/logo.com\"/>";

		Writer out = new StringWriter();
		ResourceFixupRenderer tested = new ResourceFixupRenderer(base, visibleBase, page, ResourceFixupRenderer.ABSOLUTE);
		tested.render(null, input, out);
		assertEquals(expectedOutputAbsolute, out.toString());
	}

	public void testCaseInsensitiveReplacement() throws IOException {
		String base = "http://myapp/context/";
		String visibleBase = "http://app2/";
		String page = "/page/";
		final String input = "  <a HREF=\"../styles/style.css\"/> <img SrC=\"images/logo.png\"/></a> <img src=\"http://www.google.com/logo.com\"/>";
		final String expectedOutputAbsolute = "  <a HREF=\"http://app2/page/../styles/style.css\"/> <img SrC=\"http://app2/page/images/logo.png\"/></a> <img src=\"http://www.google.com/logo.com\"/>";

		Writer out = new StringWriter();
		ResourceFixupRenderer tested = new ResourceFixupRenderer(base, visibleBase, page, ResourceFixupRenderer.ABSOLUTE);
		tested.render(null, input, out);
		assertEquals(expectedOutputAbsolute, out.toString());
	}

	public void testBackgroundReplacement() throws IOException {
		String base = "http://myapp/context/";
		String visibleBase = "http://app2/";
		String page = "/page/";
		final String input = "  <a background=\"../styles/style.css\"/> <img background=\"images/logo.png\"/></a> <img background=\"http://www.google.com/logo.com\"/>";
		final String expectedOutputAbsolute = "  <a background=\"http://app2/page/../styles/style.css\"/> <img background=\"http://app2/page/images/logo.png\"/></a> <img background=\"http://www.google.com/logo.com\"/>";

		Writer out = new StringWriter();
		ResourceFixupRenderer tested = new ResourceFixupRenderer(base, visibleBase, page, ResourceFixupRenderer.ABSOLUTE);
		tested.render(null, input, out);
		assertEquals(expectedOutputAbsolute, out.toString());
	}
}
