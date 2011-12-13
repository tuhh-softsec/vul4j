package org.esigate.esi;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;
import org.esigate.MockDriver;
import org.esigate.ResourceContext;
import org.esigate.test.MockHttpRequest;

public class RemoveElementTest extends TestCase {
	private MockDriver provider;
	private MockHttpRequest request;

	@Override
	protected void setUp() throws Exception {
		provider = new MockDriver("mock");
		request = new MockHttpRequest();
	}

	public void testRemove() throws IOException, HttpErrorPage {
		String page = "begin <esi:remove>some text to be removed</esi:remove> end";
		EsiRenderer tested = new EsiRenderer();
		StringWriter out = new StringWriter();
		tested.render(new ResourceContext(provider, null, null, request, null), page, out);
		assertEquals("begin  end", out.toString());
	}

}
