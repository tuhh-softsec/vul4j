package org.esigate.esi;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;
import org.esigate.MockDriver;
import org.esigate.ResourceContext;
import org.esigate.test.MockHttpServletRequest;

public class InlineElementTest extends TestCase {
	private MockDriver provider;
	private MockHttpServletRequest request;

	@Override
	protected void setUp() throws Exception {
		provider = new MockDriver("mock");
		provider.addResource("/test", "test");
		provider.addResource("http://www.foo.com/test", "test");
		request = new MockHttpServletRequest();
	}

	public void testInlineElement() throws IOException, HttpErrorPage {
		String page = "begin <esi:inline name=\"someUri\" fetchable=\"yes\">inside inline</esi:inline>end";
		EsiRenderer tested = new EsiRenderer();
		StringWriter out = new StringWriter();
		tested.render(new ResourceContext(provider, null, null, request, null), page, out);
		assertEquals("begin end", out.toString());
		InlineCache actual = InlineCache.getFragment("someUri");
		assertNotNull(actual);
		assertEquals(true, actual.isFetchable());
		assertEquals(false, actual.isExpired());
		assertEquals("inside inline", actual.getFragment());
	}

}
