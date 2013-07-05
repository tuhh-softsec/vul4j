/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
	 * @see "https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=120"
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
		ResourceFixupRenderer tested = new ResourceFixupRenderer(base, visibleBase, page,
				ResourceFixupRenderer.ABSOLUTE);
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
		ResourceFixupRenderer tested = new ResourceFixupRenderer(base, visibleBase, page,
				ResourceFixupRenderer.ABSOLUTE);
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
		ResourceFixupRenderer tested = new ResourceFixupRenderer(base, visibleBase, page,
				ResourceFixupRenderer.ABSOLUTE);
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
		ResourceFixupRenderer tested = new ResourceFixupRenderer(base, visibleBase, page,
				ResourceFixupRenderer.ABSOLUTE);
		tested.render(null, input, out);
		assertEquals(expectedOutputAbsolute, out.toString());
	}

	/**
	 * Ensures links like &lt;a href="?test=true">link&lt;a/> are correctly
	 * fixed, with both RELATIVE and ABSOLUTE settings.
	 * 
	 * @throws IOException
	 */
	public void testSimpleUrlWithParamsOnly() throws IOException {
		String base = "http://myapp/";
		String page = "/context/status";
		final String input = "<a href=\"?p=services\">test</a>";
		final String expectedOutputRelative = "<a href=\"/context/status?p=services\">test</a>";
		final String expectedOutputAbsolute = "<a href=\"http://myapp/context/status?p=services\">test</a>";

		// Relative test
		Writer out = new StringWriter();
		ResourceFixupRenderer tested = new ResourceFixupRenderer(base, base, page, ResourceFixupRenderer.RELATIVE);
		tested.render(null, input, out);
		assertEquals(expectedOutputRelative, out.toString());

		// Absolute test
		out = new StringWriter();
		tested = new ResourceFixupRenderer(base, base, page, ResourceFixupRenderer.ABSOLUTE);
		tested.render(null, input, out);
		assertEquals(expectedOutputAbsolute, out.toString());

	}

	/**
	 * Test for 0000186: ResourceFixup : StringIndexOutOfBoundsException: String
	 * index out of range: -1
	 * 
	 * @see "https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=186"
	 * 
	 * @throws IOException
	 */
	public void testBug186() throws IOException {
		String base = "http://localhost:8084/applicationPath/";
		String visible = "http://localhost:8084/";
		String page = "/";
		final String input = "<script src=\"/applicationPath/controller\"></script>";
		final String expectedOutputRelative = "<script src=\"/controller\"></script>";
		final String expectedOutputAbsolute = "<script src=\"http://localhost:8084/controller\"></script>";

		// Relative test
		Writer out = new StringWriter();
		ResourceFixupRenderer tested = new ResourceFixupRenderer(base, visible, page, ResourceFixupRenderer.RELATIVE);
		tested.render(null, input, out);
		assertEquals(expectedOutputRelative, out.toString());

		// Absolute test
		out = new StringWriter();
		tested = new ResourceFixupRenderer(base, visible, page, ResourceFixupRenderer.ABSOLUTE);
		tested.render(null, input, out);
		assertEquals(expectedOutputAbsolute, out.toString());

	}

	/**
	 * Test for 0000238: ResourceFixUp does not support the protocol-relative
	 * urls.
	 * <p>
	 * protocol-relative urls should be considered as absolute urls.
	 * 
	 * @see "http://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=238"
	 * 
	 * 
	 * 
	 * @throws IOException
	 */
	public void testBug238() throws IOException {
		String base = "http://localhost:8084/applicationPath/";
		String visible = "http://localhost:8084/";
		String page = "/";
		final String input = "<script src=\"//domain.com/applicationPath/controller\"></script>";
		final String expectedOutputRelative = "<script src=\"//domain.com/applicationPath/controller\"></script>";
		final String expectedOutputAbsolute = "<script src=\"//domain.com/applicationPath/controller\"></script>";

		// Relative test
		Writer out = new StringWriter();
		ResourceFixupRenderer tested = new ResourceFixupRenderer(base, visible, page, ResourceFixupRenderer.RELATIVE);
		tested.render(null, input, out);
		assertEquals(expectedOutputRelative, out.toString());

		// Absolute test
		out = new StringWriter();
		tested = new ResourceFixupRenderer(base, visible, page, ResourceFixupRenderer.ABSOLUTE);
		tested.render(null, input, out);
		assertEquals(expectedOutputAbsolute, out.toString());
	}

}
