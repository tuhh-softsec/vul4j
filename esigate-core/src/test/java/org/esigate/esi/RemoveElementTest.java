package org.esigate.esi;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;
import org.esigate.MockDriver;
import org.esigate.test.MockHttpServletRequest;
import org.esigate.test.MockHttpServletResponse;

public class RemoveElementTest extends TestCase {
	private MockDriver provider;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	@Override
	protected void setUp() throws Exception {
		provider = new MockDriver("mock");
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	public void testRemove() throws IOException, HttpErrorPage {
		String page = "begin <esi:remove>some text to be removed</esi:remove> end";
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin  end", out.toString());
	}

}
