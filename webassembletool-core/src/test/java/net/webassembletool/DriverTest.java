package net.webassembletool;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

import junit.framework.TestCase;

public class DriverTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		MockDriver provider = new MockDriver("mock");
		provider
				.addResource("/testBlock",
						"abc some<!--$beginblock$A$-->some text goes here<!--$endblock$A$--> cdf hello");
		provider
				.addResource(
						"/testTemplateFullPage",
						"some <!--$beginparam$key$-->some hidden text goes here<!--$endparam$key$--> printed");
		provider
				.addResource(
						"/testTemplate",
						"abc some<!--$begintemplate$A$-->some text goes here<!--$endtemplate$A$--> cdf hello");
	}

	public void testRenderBlock() throws IOException, HttpErrorPage {
		Writer out = new StringWriter();
		DriverFactory.getInstance("mock").renderBlock("/testBlock", "A", out,
				null, new HashMap<String, String>(), null, false, false);
		assertEquals("some text goes here", out.toString());
	}

	public void testRenderTemplateFullPage() throws IOException, HttpErrorPage {
		StringWriter out = new StringWriter();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "'value'");
		params.put("some other key", "'another value'");
		DriverFactory.getInstance("mock").renderTemplate(
				"/testTemplateFullPage", null, out, null, params, null, null,
				false);
		assertFalse(out.toString().contains("key"));
		assertTrue(out.toString().contains("'value'"));
		assertFalse(out.toString().contains("some other key"));
		assertEquals("some 'value' printed", out.toString());
	}

	public void testRenderTemplate() throws IOException, HttpErrorPage {
		StringWriter out = new StringWriter();
		DriverFactory.getInstance("mock").renderTemplate("/testTemplate", "A",
				out, null, null, null, null, false);
		assertEquals("some text goes here", out.toString());
	}
}
