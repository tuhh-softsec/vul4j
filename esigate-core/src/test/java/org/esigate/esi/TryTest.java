package org.esigate.esi;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.esigate.HttpErrorPage;
import org.esigate.MockDriver;

public class TryTest extends TestCase {

	private MockDriver provider;

	@Override
	protected void setUp() throws Exception {
		provider = new MockDriver("mock");
		provider.addResource("/test", "test");
		provider.addResource("http://www.foo.com/test", "test");
	}

	public void testTry() throws IOException, HttpErrorPage {
		String page = "begin <esi:try>"
				+ "<esi:attempt><esi:include src=\"http://www.foo.com/test\" /></esi:attempt>"
				+ "</esi:try> end";

		HttpServletRequest request = EasyMock
				.createNiceMock(HttpServletRequest.class);
		EasyMock.expect(request.getHeader("Host")).andReturn(
				"http://www.foo.com");
		EasyMock.replay(request);

		EsiRenderer tested = new EsiRenderer(request, null, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin test end", out.toString());
	}

	public void testAttempt() throws IOException, HttpErrorPage {
		String page = "begin <esi:try>"
				+ "<esi:attempt><esi:include src=\"http://www.foo.com/test\" /></esi:attempt>"
				+ "<esi:except>inside except</esi:except>" + "</esi:try> end";

		HttpServletRequest request = EasyMock
				.createNiceMock(HttpServletRequest.class);
		EasyMock.expect(request.getHeader("Host")).andReturn(
				"http://www.foo.com");
		EasyMock.replay(request);

		EsiRenderer tested = new EsiRenderer(request, null, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin test end", out.toString());
	}

	public void testExcept() throws IOException, HttpErrorPage {
		String page = "begin <esi:try>"
				+ "<esi:attempt><esi:include src=\"http://www.foo2.com/test\" /></esi:attempt>"
				+ "<esi:except>inside except</esi:except>" + "</esi:try> end";

		HttpServletRequest request = EasyMock
				.createNiceMock(HttpServletRequest.class);
		EasyMock.expect(request.getHeader("Host")).andReturn(
				"http://www.foo.com");
		EasyMock.replay(request);

		EsiRenderer tested = new EsiRenderer(request, null, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin inside except end", out.toString());
	}

}
