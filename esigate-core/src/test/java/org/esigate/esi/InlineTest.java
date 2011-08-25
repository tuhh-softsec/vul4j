package org.esigate.esi;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.esigate.HttpErrorPage;
import org.esigate.MockDriver;

public class InlineTest extends TestCase {
	private MockDriver provider;

	@Override
	protected void setUp() throws Exception {
		provider = new MockDriver("mock");
		provider.addResource("/test", "test");
		provider.addResource("http://www.foo.com/test", "test");
	}

	public void testWhen() throws IOException, HttpErrorPage {
		String page = "begin <esi:inline name=\"someUri\" fetchable=\"yes\">inside inline</esi:inline>end";
		String page2 = "begin <esi:include src=\"someUri\" /> end";

		HttpServletRequest request = EasyMock
				.createNiceMock(HttpServletRequest.class);
		EasyMock.expect(request.getRequestURL()).andReturn(new StringBuffer());
		EasyMock.replay(request);

		EsiRenderer tested = new EsiRenderer(request, null, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin end", out.toString());

		out = new StringWriter();
		tested.render(null, page2, out);
		assertEquals("begin inside inline end", out.toString());
	}

}
