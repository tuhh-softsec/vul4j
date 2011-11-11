package org.esigate.esi;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;
import org.esigate.MockDriver;
import org.esigate.test.MockHttpServletRequest;
import org.esigate.test.MockHttpServletResponse;

public class TryElementTest extends TestCase {

	private MockDriver provider;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	@Override
	protected void setUp() {
		provider = new MockDriver("mock");
		provider.addResource("/test", "test");
		provider.addResource("http://www.foo.com/test", "test");
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	public void testTry() throws IOException, HttpErrorPage {
		String page = "begin <esi:try>"
				+ "<esi:attempt><esi:include src=\"http://www.foo.com/test\" /></esi:attempt>"
				+ "</esi:try> end";
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin test end", out.toString());
	}

	public void testAttempt() throws IOException, HttpErrorPage {
		String page = "begin <esi:try>"
				+ "<esi:attempt><esi:include src=\"http://www.foo.com/test\" /></esi:attempt>"
				+ "<esi:except>inside except</esi:except>"
				+ "</esi:try> end";
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin test end", out.toString());
	}

	public void testExcept() throws IOException, HttpErrorPage {
		String page = "begin <esi:try>"
				+ "<esi:attempt><esi:include src=\"http://www.foo2.com/test\" /></esi:attempt>"
				+ "<esi:except>inside except</esi:except>"
				+ "</esi:try> end";
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin inside except end", out.toString());
	}

}
