package org.esigate.esi;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.esigate.ResourceContext;
import org.esigate.test.MockHttpRequest;

public class EsiRendererTest extends TestCase {
	private MockHttpRequest request;
	private ResourceContext ctx;
	private EsiRenderer tested;

	@Override
	protected void setUp() throws Exception {
		request = new MockHttpRequest();
		ctx = new ResourceContext(null, null, null, request, null);
		tested = new EsiRenderer();
	}

	public void testFragmentTagsShouldBeRemoved() throws Exception {
		String page = "begin <esi:fragment name=\"test\">content</esi:fragment> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("begin content end", out.toString());
	}
}
